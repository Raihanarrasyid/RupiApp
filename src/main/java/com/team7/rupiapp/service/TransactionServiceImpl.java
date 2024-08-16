package com.team7.rupiapp.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.team7.rupiapp.dto.transfer.destination.DestinationAddDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.transfer.qris.QrisDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateCPMDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateMPMDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateResponseDto;
import com.team7.rupiapp.dto.transfer.qris.QrisResponseDto;
import com.team7.rupiapp.dto.transfer.qris.QrisTransferResponseDto;
import com.team7.rupiapp.dto.transfer.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.transfer.TransferResponseDto;
import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.QrisType;
import com.team7.rupiapp.enums.TransactionPurpose;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.exception.UnauthorizedException;
import com.team7.rupiapp.model.Destination;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.Qris;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.DestinationRepository;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.QrisRepository;
import com.team7.rupiapp.repository.UserRepository;
import com.team7.rupiapp.util.Base64Util;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final QrisRepository qrisRepository;
    private final MutationRepository mutationRepository;
    private final DestinationRepository destinationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final GenerateService generateService;

    public TransactionServiceImpl(UserRepository userRepository, QrisRepository qrisRepository,
            MutationRepository mutationRepository, DestinationRepository destinationRepository,
            PasswordEncoder passwordEncoder, ModelMapper modelMapper, GenerateService generateService) {
        this.userRepository = userRepository;
        this.qrisRepository = qrisRepository;
        this.mutationRepository = mutationRepository;
        this.destinationRepository = destinationRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.generateService = generateService;
    }

    @Override
    @Transactional
    public TransferResponseDto createTransaction(TransferRequestDto requestDto, Principal principal) {
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getPin(), sender.getPin())) {
            throw new BadRequestException("Invalid PIN");
        }

        Destination destination = destinationRepository.findById(requestDto.getDestinationId())
                .orElseThrow(() -> new DataNotFoundException("Destination not found"));

        if (!destination.getUser().getId().equals(sender.getId())) {
            throw new BadRequestException("Destination does not belong to the sender");
        }

        User receiver = userRepository.findByAccountNumber(destination.getAccountNumber())
                .orElseThrow(() -> new DataNotFoundException(
                        "Receiver not found with account number: " + destination.getAccountNumber()));

        if (sender.getBalance() < requestDto.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - requestDto.getAmount());
        receiver.setBalance(receiver.getBalance() + requestDto.getAmount());

        userRepository.save(sender);
        userRepository.save(receiver);

        Mutation senderMutation = modelMapper.map(requestDto, Mutation.class);
        senderMutation.setUser(sender);
        senderMutation.setCreatedAt(LocalDateTime.now());
        senderMutation.setMutationType(requestDto.getType());
        senderMutation.setAccountNumber(destination.getAccountNumber());
        senderMutation.setFullName(receiver.getFullName());
        senderMutation.setTransactionType(TransactionType.DEBIT);
        mutationRepository.save(senderMutation);

        Mutation receiverMutation = modelMapper.map(requestDto, Mutation.class);
        receiverMutation.setUser(receiver);
        receiverMutation.setCreatedAt(LocalDateTime.now());
        receiverMutation.setMutationType(requestDto.getType());
        receiverMutation.setAccountNumber(sender.getAccountNumber());
        receiverMutation.setFullName(sender.getFullName());
        receiverMutation.setTransactionType(TransactionType.CREDIT);
        mutationRepository.save(receiverMutation);

        TransferResponseDto responseDto = new TransferResponseDto();
        responseDto.setDescription(requestDto.getDescription());
        responseDto.setTransactionPurpose(requestDto.getTransactionPurpose().toString());

        TransferResponseDto.ReceiverDetail receiverDetail = new TransferResponseDto.ReceiverDetail();
        receiverDetail.setName(destination.getName());
        receiverDetail.setAccountNumber(destination.getAccountNumber());
        responseDto.setReceiverDetail(receiverDetail);

        TransferResponseDto.MutationDetail mutationDetail = new TransferResponseDto.MutationDetail();
        mutationDetail.setAmount(requestDto.getAmount());
        mutationDetail.setCreatedAt(senderMutation.getCreatedAt());
        responseDto.setMutationDetail(mutationDetail);

        TransferResponseDto.SenderDetail senderDetail = new TransferResponseDto.SenderDetail();
        senderDetail.setName(sender.getUsername());
        senderDetail.setAccountNumber(sender.getAccountNumber());
        responseDto.setSenderDetail(senderDetail);

        return responseDto;
    }

    @Override
    public List<DestinationDto> getDestination(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        List<Destination> destinations = destinationRepository.findByUser(user);

        return destinations.stream()
                .map(destination -> {
                    DestinationDto dto = new DestinationDto();
                    dto.setId(destination.getId());
                    dto.setFullname(destination.getName());
                    dto.setAccountNumber(destination.getAccountNumber());
                    dto.setFavorites(destination.isFavorites());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addFavorites(UUID id, DestinationFavoriteDto destinationFavoriteDto) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Destination not found"));
        destination.setFavorites(destinationFavoriteDto.getIsFavorites());
        destinationRepository.save(destination);
        log.info("updated favorite for destination with id: {}", id);
    }

    @Override
    public DestinationAddDto addDestination(DestinationAddDto requestDto, Principal principal) {
        try {
            Long.parseLong(requestDto.getAccountNumber());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Account number must be a number");
        }
        User user = userRepository.findByAccountNumber(requestDto.getAccountNumber())
                .orElseThrow(() -> new DataNotFoundException("Account number not found"));
        User user1 = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Optional<Destination> existingDestination = destinationRepository.findByUserAndAccountNumber(user1,
                requestDto.getAccountNumber());
        requestDto.setFullname(user.getFullName());

        if (user == user1) {
            throw new BadRequestException("can't add your own account number");
        }

        if (existingDestination.isPresent()) {
            log.info("nothing has been added");
            requestDto.setDestinationId(existingDestination.get().getId().toString());
        } else {
            Destination newDestination = new Destination();
            newDestination.setUser(user1);
            newDestination.setAccountNumber(requestDto.getAccountNumber());
            newDestination.setName(user.getFullName());
            newDestination.setFavorites(false);

            destinationRepository.save(newDestination);
            requestDto.setDestinationId(newDestination.getId().toString());
            log.info("destination has been added with userID: {}", user1.getId());
        }
        return requestDto;
    }

    @Override
    public DestinationDetailDto getDestinationDetail(UUID id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Destination not found"));
        DestinationDetailDto destinationDetail = new DestinationDetailDto();
        destinationDetail.setFullname(destination.getName());
        destinationDetail.setAccountNumber(destination.getAccountNumber());

        return destinationDetail;
    }

    private Map<String, String> parseQRIS(String qris) {
        Map<String, String> result = new java.util.HashMap<>();
        int index = 0;

        while (index < qris.length()) {
            if (index + 4 > qris.length()) {
                throw new IllegalArgumentException("QRIS format is not suitable");
            }

            String tag = qris.substring(index, index + 2);
            index += 2;

            int length;
            try {
                length = Integer.parseInt(qris.substring(index, index + 2));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("QRIS format is not suitable", e);
            }
            index += 2;

            if (index + length > qris.length()) {
                throw new IllegalArgumentException("QRIS format is not suitable");
            }

            String value = qris.substring(index, index + length);
            index += length;

            result.put(tag, value);
        }

        return result;
    }

    @Override
    public QrisResponseDto detailQris(String qris) {
        Map<String, String> qrisMap = parseQRIS(qris);
        QrisResponseDto qrisResponse = new QrisResponseDto();
        if (qrisMap.get("01").equals("11")) {
            qrisResponse.setType("static");
        } else if (qrisMap.get("01").equals("12")) {
            qrisResponse.setType("dynamic");
        } else {
            qrisResponse.setType("unknown");
        }

        qrisResponse.setTransactionId(qrisMap.get("62"));
        qrisResponse.setMerchant(qrisMap.get("59"));
        qrisResponse.setAmount(qrisMap.get("54"));

        return qrisResponse;
    }

    @Override
    @Transactional
    public QrisTransferResponseDto createTransactionQris(Principal principal, QrisDto qrisDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (!passwordEncoder.matches(qrisDto.getPin(), user.getPin())) {
            throw new BadRequestException("Invalid PIN");
        }

        Map<String, String> qrisMap = parseQRIS(qrisDto.getQris());
        Qris qris = qrisRepository.findByTransactionId(qrisMap.get("62"));

        double amount = 0.0;
        String transactionId = qrisMap.get("62");
        String merchant = qrisMap.get("59");

        if (qrisMap.get("52").equals("0000")) {
            amount = handlePersonToPersonTransaction(user, qrisDto, qrisMap, qris);
        } else if (qrisMap.get("01").equals("12")) {
            amount = handleMerchantTransaction(user, qrisDto, qrisMap, qris, false);
        } else if (qrisMap.get("01").equals("11")) {
            amount = handleMerchantTransaction(user, qrisDto, qrisMap, qris, true);
        } else {
            throw new BadRequestException("Invalid QRIS");
        }

        return buildQrisTransferResponse(transactionId, merchant, amount, qrisDto.getDescription());
    }

    private double handlePersonToPersonTransaction(User user, QrisDto qrisDto, Map<String, String> qrisMap, Qris qris) {
        String transactionId = qrisMap.get("62");
        int indexOfLength = transactionId.lastIndexOf("00");

        if (indexOfLength == -1 || indexOfLength + 2 > transactionId.length() - 2) {
            throw new BadRequestException("Invalid QRIS");
        }

        int length = Integer.parseInt(transactionId.substring(indexOfLength + 2, indexOfLength + 4));
        String accountNumber = transactionId.substring(indexOfLength - length, indexOfLength);

        if (accountNumber.equals(user.getAccountNumber())) {
            throw new BadRequestException("Can't transfer to your own account");
        }

        User receiver = userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new DataNotFoundException("Receiver not found"));

        if (qris != null && qris.isUsed()) {
            throw new BadRequestException("Transaction already exists");
        }

        double amount = qrisMap.get("01").equals("11") ? Double.parseDouble(qrisDto.getAmount())
                : Double.parseDouble(qrisMap.get("54"));

        processTransaction(user, receiver, amount, qrisDto.getDescription(), qris, qrisDto);

        return amount;
    }

    private double handleMerchantTransaction(User user, QrisDto qrisDto, Map<String, String> qrisMap, Qris qris,
            boolean isStatic) {
        double amount = Double.parseDouble(isStatic ? qrisDto.getAmount() : qrisMap.get("54"));

        if (qris != null && qris.isUsed()) {
            throw new BadRequestException("Transaction already exists");
        }

        if (!isStatic) {
            Qris newQris = new Qris();
            newQris.setType(QrisType.MPM);
            newQris.setTransactionId(qrisMap.get("62"));
            newQris.setPayload(qrisDto.getQris());
            newQris.setUsed(true);
            newQris.setExpiredAt(LocalDateTime.now());
            qrisRepository.save(newQris);

            processTransaction(user, null, amount, qrisDto.getDescription(), newQris, qrisDto);
        }

        return amount;
    }

    private void processTransaction(User user, User receiver, double amount, String description, Qris qris,
            QrisDto qrisDto) {
        if (user.getBalance() < amount) {
            throw new BadRequestException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        if (receiver != null) {
            receiver.setBalance(receiver.getBalance() + amount);
            userRepository.save(receiver);
        }

        saveMutation(user, amount, description, qrisDto, TransactionType.DEBIT);

        if (receiver != null) {
            saveMutation(receiver, amount, description, qrisDto, TransactionType.CREDIT);
        }

        if (qris != null) {
            qris.setUsed(true);
            qrisRepository.save(qris);
        }
    }

    private void saveMutation(User user, double amount, String description, QrisDto qrisDto,
            TransactionType transactionType) {
        Mutation mutation = modelMapper.map(qrisDto, Mutation.class);
        mutation.setUser(user);
        mutation.setAmount(amount);
        mutation.setCreatedAt(LocalDateTime.now());
        mutation.setTransactionType(transactionType);
        mutation.setMutationType(MutationType.QRIS);
        mutation.setTransactionPurpose(TransactionPurpose.OTHER);
        mutation.setDescription(description);
        mutation.setFullName(user.getFullName());
        mutationRepository.save(mutation);
    }

    private QrisTransferResponseDto buildQrisTransferResponse(String transactionId, String merchant, double amount,
            String description) {
        QrisTransferResponseDto responseDto = new QrisTransferResponseDto();
        responseDto.setTransactionId(transactionId);
        responseDto.setMerchant(merchant);
        responseDto.setAmount(String.valueOf(amount));
        responseDto.setDescription(description);
        return responseDto;
    }

    @Override
    public QrisGenerateResponseDto createQris(Principal principal, QrisGenerateMPMDto qrisMPMDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        String qr = generateService.generateQrisMPM(user, UUID.randomUUID().toString().replace("-", ""),
                qrisMPMDto.getAmount());
        LocalDateTime expiredAt = LocalDateTime.now().plusHours(24);

        Map<String, String> qrisMap = parseQRIS(qr);

        Qris qris = new Qris();
        qris.setUser(user);
        qris.setType(QrisType.MPM);
        qris.setTransactionId(qrisMap.get("62"));
        qris.setPayload(qr);
        qris.setUsed(false);
        qris.setExpiredAt(expiredAt);
        qrisRepository.save(qris);

        String qrImage = Base64Util.convertImage(generateService.generateQRCodeImage(qr, 300, 300));

        QrisGenerateResponseDto responseDto = new QrisGenerateResponseDto();
        responseDto.setQris(qrImage);
        responseDto.setExpiredAt(expiredAt);

        return responseDto;
    }

    @Override
    public QrisGenerateResponseDto createQrisCPM(Principal principal, QrisGenerateCPMDto qrisCPMDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        if (!passwordEncoder.matches(qrisCPMDto.getPin(), user.getPin())) {
            throw new BadRequestException("Invalid PIN");
        }

        String transactionId = UUID.randomUUID().toString();
        String qr = generateService.generateQrisCPM(user, transactionId);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(1);

        Qris qris = new Qris();
        qris.setUser(user);
        qris.setType(QrisType.CPM);
        qris.setTransactionId(transactionId);
        qris.setPayload(qr);
        qris.setUsed(false);
        qris.setExpiredAt(expiredAt);
        qrisRepository.save(qris);

        String qrImage = Base64Util.convertImage(generateService.generateQRCodeImage(qr, 300, 300));

        QrisGenerateResponseDto responseDto = new QrisGenerateResponseDto();
        responseDto.setQris(qrImage);
        responseDto.setExpiredAt(expiredAt);

        return responseDto;
    }

    @Override
    @Transactional
    public Object getTransactionDetails(UUID transactionId, Principal principal) {
        Mutation mutation = mutationRepository.findById(transactionId)
                .orElseThrow(() -> new DataNotFoundException("Transaction not found"));

        User requestingUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (!isUserAuthorized(mutation, requestingUser)) {
            throw new UnauthorizedException("Not authorized to access this transaction");
        }

        if (mutation.getMutationType() == MutationType.QRIS) {
            return buildQrisResponseDto(mutation);
        } else if (mutation.getMutationType() == MutationType.TRANSFER) {
            return buildTransferResponseDto(mutation);
        } else {
            throw new DataNotFoundException("Invalid mutation type");
        }
    }

    private boolean isUserAuthorized(Mutation mutation, User requestingUser) {
        if (mutation.getMutationType() == MutationType.QRIS) {
            return mutation.getUser().getId().equals(requestingUser.getId());
        } else if (mutation.getMutationType() == MutationType.TRANSFER) {
            return mutation.getUser().getId().equals(requestingUser.getId()) ||
                    mutation.getAccountNumber().equals(requestingUser.getAccountNumber());
        }
        return false;
    }

    private QrisTransferResponseDto buildQrisResponseDto(Mutation mutation) {
        QrisTransferResponseDto qrisResponseDto = new QrisTransferResponseDto();
        qrisResponseDto.setTransactionId(mutation.getId().toString());
        qrisResponseDto.setMerchant(mutation.getFullName());
        qrisResponseDto.setAmount(String.valueOf(mutation.getAmount()));
        qrisResponseDto.setDescription(mutation.getDescription());
        return qrisResponseDto;
    }

    private TransferResponseDto buildTransferResponseDto(Mutation mutation) {
        User sender = mutation.getUser();
        TransferResponseDto transferResponseDto = new TransferResponseDto();

        TransferResponseDto.SenderDetail senderDetail = new TransferResponseDto.SenderDetail();
        TransferResponseDto.ReceiverDetail receiverDetail = new TransferResponseDto.ReceiverDetail();

        if (mutation.getTransactionType() == TransactionType.DEBIT) {
            senderDetail.setName(sender.getFullName());
            senderDetail.setAccountNumber(sender.getAccountNumber());
            receiverDetail.setName(mutation.getFullName());
            receiverDetail.setAccountNumber(mutation.getAccountNumber());
        } else if (mutation.getTransactionType() == TransactionType.CREDIT) {
            senderDetail.setName(mutation.getFullName());
            senderDetail.setAccountNumber(mutation.getAccountNumber());
            receiverDetail.setName(sender.getFullName());
            receiverDetail.setAccountNumber(sender.getAccountNumber());
        }

        transferResponseDto.setSenderDetail(senderDetail);
        transferResponseDto.setReceiverDetail(receiverDetail);

        TransferResponseDto.MutationDetail mutationDetail = new TransferResponseDto.MutationDetail();
        mutationDetail.setAmount(mutation.getAmount());
        mutationDetail.setCreatedAt(mutation.getCreatedAt());
        transferResponseDto.setMutationDetail(mutationDetail);

        transferResponseDto.setDescription(mutation.getDescription());
        transferResponseDto.setTransactionPurpose(mutation.getTransactionPurpose().toString());

        return transferResponseDto;
    }
}