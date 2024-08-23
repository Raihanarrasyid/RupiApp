package com.team7.rupiapp.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;

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

    @Getter
    @Setter
    public class TransactionDetails {
        private User user;
        private User receiver;
        private Map<String, String> qrisMap;
        private double amount;
        private String description;
        private Qris qris;
        private QrisDto qrisDto;
        private MutationType mutationType;
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

        Mutation savedSenderMutation = mutationRepository.save(senderMutation);

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
        mutationDetail.setMutationId(savedSenderMutation.getId());
        responseDto.setMutationDetail(mutationDetail);

        TransferResponseDto.SenderDetail senderDetail = new TransferResponseDto.SenderDetail();
        senderDetail.setName(sender.getUsername());
        senderDetail.setAccountNumber(sender.getAccountNumber());
        responseDto.setSenderDetail(senderDetail);

        return responseDto;
    }

    @Override
    public Page<DestinationDto> getDestination(Principal principal, String search, int page, int size) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Destination> destinations;
        if (search != null && !search.isEmpty()) {
            destinations = destinationRepository
                    .findByUserAndNameContainingIgnoreCaseOrAccountNumberContainingIgnoreCase(user,
                            search, search, pageable);
        } else {
            destinations = destinationRepository.findByUser(user, pageable);
        }

        return destinations.map(destination -> {
            DestinationDto dto = new DestinationDto();
            dto.setId(destination.getId());
            dto.setFullname(destination.getName());
            dto.setAccountNumber(destination.getAccountNumber());
            dto.setFavorites(destination.isFavorites());
            return dto;
        });
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

        if (qrisMap.containsKey("62")) {
            String transactionId = qrisMap.get("62");

            if (transactionId.contains("00")) {
                int indexOfLength = transactionId.lastIndexOf("00");

                int length = Integer.parseInt(transactionId.substring(indexOfLength + 2, indexOfLength + 4));
                qrisResponse.setAccountNumber(transactionId.substring(indexOfLength - length, indexOfLength));
            }
        }

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

        HashMap<String, Object> data;
        String merchant = qrisMap.get("59");

        if (qrisMap.get("52").equals("0000")) {
            data = handlePersonToPersonTransaction(user, qrisDto, qrisMap, qris);
        } else if (qrisMap.get("01").equals("11")) {
            data = handleMerchantTransaction(user, qrisDto, qrisMap, qris, true);
        } else if (qrisMap.get("01").equals("12")) {
            data = handleMerchantTransaction(user, qrisDto, qrisMap, qris, false);
        } else {
            throw new BadRequestException("Invalid QRIS");
        }

        return buildQrisTransferResponse(data, merchant, qrisDto.getDescription());
    }

    private HashMap<String, Object> handlePersonToPersonTransaction(User user, QrisDto qrisDto,
            Map<String, String> qrisMap, Qris qris) {
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

        TransactionDetails details = new TransactionDetails();
        details.setUser(user);
        details.setReceiver(receiver);
        details.setQrisMap(qrisMap);
        details.setAmount(amount);
        details.setDescription(qrisDto.getDescription());
        details.setQris(qris);
        details.setQrisDto(qrisDto);
        details.setMutationType(MutationType.QR);

        UUID mutationId = processTransaction(details);

        HashMap<String, Object> data = new HashMap<>();
        data.put("mutationId", mutationId);
        data.put("amount", amount);
        return data;
    }

    private HashMap<String, Object> handleMerchantTransaction(User user, QrisDto qrisDto, Map<String, String> qrisMap,
            Qris qris,
            boolean isStatic) {
        double amount = Double.parseDouble(isStatic ? qrisDto.getAmount() : qrisMap.get("54"));
        UUID mutationId;

        if (qris != null && qris.isUsed()) {
            throw new BadRequestException("Transaction already exists");
        }

        if (isStatic) {
            TransactionDetails details = new TransactionDetails();
            details.setUser(user);
            details.setQrisMap(qrisMap);
            details.setAmount(amount);
            details.setDescription(qrisDto.getDescription());
            details.setQris(qris);
            details.setQrisDto(qrisDto);
            details.setMutationType(MutationType.QRIS);

            mutationId = processTransaction(details);
        } else {
            Qris newQris = new Qris();
            newQris.setType(QrisType.MPM);
            newQris.setTransactionId(qrisMap.get("62"));
            newQris.setPayload(qrisDto.getQris());
            newQris.setUsed(true);
            newQris.setExpiredAt(LocalDateTime.now());
            qrisRepository.save(newQris);

            TransactionDetails newQrisDetails = new TransactionDetails();
            newQrisDetails.setUser(user);
            newQrisDetails.setQrisMap(qrisMap);
            newQrisDetails.setAmount(amount);
            newQrisDetails.setDescription(qrisDto.getDescription());
            newQrisDetails.setQris(newQris);
            newQrisDetails.setQrisDto(qrisDto);
            newQrisDetails.setMutationType(MutationType.QRIS);

            mutationId = processTransaction(newQrisDetails);
        }

        HashMap<String, Object> data = new HashMap<>();
        data.put("mutationId", mutationId);
        data.put("amount", amount);
        return data;
    }

    private UUID processTransaction(TransactionDetails details) {
        User user = details.getUser();
        double amount = details.getAmount();

        if (user.getBalance() < amount) {
            throw new BadRequestException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        UUID mutationId;

        if (details.getReceiver() != null) {
            User receiver = details.getReceiver();
            receiver.setBalance(receiver.getBalance() + amount);
            userRepository.save(receiver);

            mutationId = saveMutation(details, TransactionType.DEBIT).getId();
            TransactionDetails receiverDetails = new TransactionDetails();
            receiverDetails.setUser(receiver);
            receiverDetails.setReceiver(user);
            receiverDetails.setQrisMap(details.getQrisMap());
            receiverDetails.setAmount(amount);
            receiverDetails.setDescription(details.getDescription());
            receiverDetails.setQris(details.getQris());
            receiverDetails.setQrisDto(details.getQrisDto());
            receiverDetails.setMutationType(details.getMutationType());

            saveMutation(receiverDetails, TransactionType.CREDIT);
        } else {
            mutationId = saveMutation(details, TransactionType.DEBIT).getId();
        }

        if (details.getQris() != null) {
            details.getQris().setUsed(true);
            qrisRepository.save(details.getQris());
        }

        return mutationId;
    }

    private Mutation saveMutation(TransactionDetails details, TransactionType transactionType) {
        Mutation mutation = modelMapper.map(details.getQrisDto(), Mutation.class);
        mutation.setUser(details.getUser());
        mutation.setAmount(details.getAmount());
        mutation.setCreatedAt(LocalDateTime.now());
        mutation.setTransactionType(transactionType);
        mutation.setMutationType(details.getMutationType());
        mutation.setTransactionPurpose(TransactionPurpose.OTHER);
        mutation.setDescription(details.getDescription());
        mutation.setFullName(details.getQrisMap().get("59"));
        mutation.setAccountNumber(details.getQrisMap().get("62"));
        mutationRepository.save(mutation);

        return mutation;
    }

    private QrisTransferResponseDto buildQrisTransferResponse(HashMap<String, Object> data, String merchant,
            String description) {
        QrisTransferResponseDto responseDto = new QrisTransferResponseDto();
        responseDto.setMutationId(data.get("mutationId").toString());
        responseDto.setMerchant(merchant);
        responseDto.setAmount(String.valueOf(data.get("amount")));
        responseDto.setDescription(description);
        return responseDto;
    }

    @Override
    public QrisGenerateResponseDto createQris(Principal principal, QrisGenerateMPMDto qrisMPMDto) {
        if (qrisMPMDto != null && qrisMPMDto.getAmount() != null) {
            if (qrisMPMDto.getAmount() <= 0) {
                throw new BadRequestException("Amount must be greater than zero");
            }
        } else {
            qrisMPMDto = new QrisGenerateMPMDto();
            qrisMPMDto.setAmount(null);
        }

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

        String path = "images/RupiApp.png";
        BufferedImage logo = null;

        try {
            logo = ImageIO.read(new File(path));
        } catch (Exception e) {
            throw new BadRequestException("Invalid logo path");
        }

        String qrImage = Base64Util.convertImage(generateService.generateQRCodeImage(qr, 300, 300, logo));

        QrisGenerateResponseDto responseDto = new QrisGenerateResponseDto();
        responseDto.setQris(qrImage);
        responseDto.setExpiredAt(expiredAt);

        return responseDto;
    }
}