package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.destination.DestinationDto;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<DestinationDto> getDestination(Principal principal) {
        // Fetch user based on principal
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch destinations by user
        List<Destination> destinations = destinationRepository.findByUser(user);

        // Map Destination to DestinationDto directly
        return destinations.stream()
                .map(destination -> {
                    DestinationDto dto = new DestinationDto();
                    dto.setFullname(destination.getName());
                    dto.setAccountNumber(destination.getAccountNumber());
                    dto.setFavorites(destination.isFavorites());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addFavorites(DestinationDto destinationDto, Principal principal) {
        // Fetch user based on principal
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch destination by user and account number
        Destination destination = destinationRepository.findByUserAndAccountNumber(user, destinationDto.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination not found"));

        // Update the destination to set as favorite
        destination.setFavorites(destinationDto.isFavorites());

        // Save the updated destination
        destinationRepository.save(destination);
    }
}