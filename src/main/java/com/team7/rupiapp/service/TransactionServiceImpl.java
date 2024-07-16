package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.model.Destination;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.DestinationRepository;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final MutationRepository mutationRepository;
    private final DestinationRepository destinationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionServiceImpl(UserRepository userRepository, MutationRepository mutationRepository, DestinationRepository destinationRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.mutationRepository = mutationRepository;
        this.destinationRepository = destinationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public TransferResponseDto createTransaction(TransferRequestDto requestDto) {

        User sender = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Destination destination = destinationRepository.findById(requestDto.getDestinationId())
                .orElseThrow(() -> new DataNotFoundException("Destination not found"));

        User receiver = userRepository.findByAccountNumber(destination.getAccountNumber())
                .orElseThrow(() -> new DataNotFoundException("Receiver not found with account number: " + destination.getAccountNumber()));

        if (sender.getBalance() < requestDto.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance() - requestDto.getAmount());
        receiver.setBalance(receiver.getBalance() + requestDto.getAmount());

        userRepository.save(sender);
        userRepository.save(receiver);

        Mutation senderMutation = new Mutation();
        senderMutation.setUser(sender);
        senderMutation.setAmount(-requestDto.getAmount());
        senderMutation.setDescription(requestDto.getDescription());
        senderMutation.setCreatedAt(LocalDateTime.now());
        senderMutation.setType(requestDto.getType());
        mutationRepository.save(senderMutation);

        Mutation receiverMutation = new Mutation();
        receiverMutation.setUser(receiver);
        receiverMutation.setAmount(requestDto.getAmount());
        receiverMutation.setDescription(requestDto.getDescription());
        receiverMutation.setCreatedAt(LocalDateTime.now());
        receiverMutation.setType(requestDto.getType());
        mutationRepository.save(receiverMutation);

        TransferResponseDto responseDto = modelMapper.map(senderMutation, TransferResponseDto.class);
        responseDto.setUserId(sender.getId());

        return responseDto;
    }
}