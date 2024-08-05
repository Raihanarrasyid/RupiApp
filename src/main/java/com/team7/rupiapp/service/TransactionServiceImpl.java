package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.destination.DestinationAddDto;
import com.team7.rupiapp.dto.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.destination.DestinationDto;
import com.team7.rupiapp.dto.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.qris.QrisDto;
import com.team7.rupiapp.dto.qris.QrisResponseDto;
import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;
import com.team7.rupiapp.enums.MutationType;
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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final QrisRepository qrisRepository;
    private final MutationRepository mutationRepository;
    private final DestinationRepository destinationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public TransactionServiceImpl(UserRepository userRepository, QrisRepository qrisRepository,
            MutationRepository mutationRepository, DestinationRepository destinationRepository,
            PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.qrisRepository = qrisRepository;
        this.mutationRepository = mutationRepository;
        this.destinationRepository = destinationRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
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
        senderMutation.setFullName(sender.getFullName());
        senderMutation.setTransactionType(TransactionType.DEBIT);
        mutationRepository.save(senderMutation);

        Mutation receiverMutation = modelMapper.map(requestDto, Mutation.class);
        receiverMutation.setUser(receiver);
        receiverMutation.setCreatedAt(LocalDateTime.now());
        receiverMutation.setMutationType(requestDto.getType());
        receiverMutation.setAccountNumber(sender.getAccountNumber());
        receiverMutation.setFullName(receiver.getFullName());
        receiverMutation.setTransactionType(TransactionType.CREDIT);
        mutationRepository.save(receiverMutation);

        TransferResponseDto responseDto = new TransferResponseDto();

        TransferResponseDto.ReceiverDetail destinationDetail = new TransferResponseDto.ReceiverDetail();
        destinationDetail.setName(destination.getName());
        destinationDetail.setAccountNumber(destination.getAccountNumber());
        responseDto.setDestinationDetail(destinationDetail);

        TransferResponseDto.MutationDetail mutationDetail = new TransferResponseDto.MutationDetail();
        mutationDetail.setAmount(requestDto.getAmount());
        mutationDetail.setCreatedAt(senderMutation.getCreatedAt());
        responseDto.setMutationDetail(mutationDetail);

        TransferResponseDto.SenderDetail userDetail = new TransferResponseDto.SenderDetail();
        userDetail.setName(sender.getUsername());
        userDetail.setAccountNumber(sender.getAccountNumber());
        responseDto.setUserDetail(userDetail);

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
            String tag = qris.substring(index, index + 2);
            index += 2;

            int length = Integer.parseInt(qris.substring(index, index + 2));
            index += 2;

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
        if (qrisMap.containsKey("54") && qrisMap.containsKey("59") && qrisMap.containsKey("62")) {
            qrisResponse.setType("dynamic");
        } else if (qrisMap.containsKey("52")) {
            qrisResponse.setType("static");
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
    public void createTransactionQris(Principal principal, QrisDto qrisDto) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not registered"));

        Map<String, String> qrisMap = parseQRIS(qrisDto.getQris());
        if (qrisMap.containsKey("62")) {
            Qris qris = qrisRepository.findByTransactionId(qrisMap.get("62"));

            if (qris != null) {
                throw new BadRequestException("Transaction already exists");
            } else {
                Qris newQris = new Qris();
                newQris.setTransactionId(qrisMap.get("62"));
                qrisRepository.save(newQris);

                if (user.getBalance() < Double.parseDouble(qrisMap.get("54"))) {
                    throw new BadRequestException("Insufficient balance");
                }

                user.setBalance(user.getBalance() - Double.parseDouble(qrisMap.get("54")));
                userRepository.save(user);

                Mutation mutation = new Mutation();
                mutation.setUser(user);
                mutation.setAmount(Double.parseDouble(qrisMap.get("54")));
                mutation.setCreatedAt(LocalDateTime.now());
                mutation.setTransactionType(TransactionType.DEBIT);
                mutation.setMutationType(MutationType.QRIS);
                mutation.setTransactionPurpose(TransactionPurpose.PURCHASE);
                mutationRepository.save(mutation);
            }
        } else if (qrisMap.containsKey("52")) {
            if (qrisDto.getAmount() == null) {
                throw new BadRequestException("Amount is required");
            }

            if (user.getBalance() < Double.parseDouble(qrisDto.getAmount())) {
                throw new BadRequestException("Insufficient balance");
            }

            user.setBalance(user.getBalance() - Double.parseDouble(qrisDto.getAmount()));
            userRepository.save(user);

            Mutation mutation = new Mutation();
            mutation.setUser(user);
            mutation.setAmount(Double.parseDouble(qrisDto.getAmount()));
            mutation.setCreatedAt(LocalDateTime.now());
            mutation.setTransactionType(TransactionType.DEBIT);
            mutation.setMutationType(MutationType.QRIS);
            mutation.setTransactionPurpose(TransactionPurpose.PURCHASE);
            mutationRepository.save(mutation);
        } else {
            throw new BadRequestException("Invalid QRIS");
        }
    }

}