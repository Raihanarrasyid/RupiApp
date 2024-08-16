package com.team7.rupiapp.service;

import java.time.LocalDateTime;

import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.team7.rupiapp.dto.demo.DemoQrisCPMDto;
import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.TransactionPurpose;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.Qris;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.QrisRepository;

@Service
public class DemoServiceImpl implements DemoService {
    private final ModelMapper modelMapper;
    private final QrisRepository qrisRepository;
    private final MutationRepository mutationRepository;
    private final UserRepository userRepository;

    public DemoServiceImpl(ModelMapper modelMapper, QrisRepository qrisRepository,
                           MutationRepository mutationRepository, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.qrisRepository = qrisRepository;
        this.mutationRepository = mutationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void demoQrisCPM(DemoQrisCPMDto demoQrisCPMDto) {
        Qris qris = qrisRepository.findByPayload(demoQrisCPMDto.getQris());

        if (qris == null) {
            throw new BadRequestException("Qris not valid");
        }

        if (qris.isUsed()) {
            throw new BadRequestException("Qris is already used");
        }

        if (qris.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Qris is expired");
        }
        
        if (demoQrisCPMDto.getAmount() > qris.getUser().getBalance()) {
            throw new BadRequestException("Insufficient balance");
        }

        User user = qris.getUser();

        user.setBalance(user.getBalance() - demoQrisCPMDto.getAmount());
        userRepository.save(user);

        Mutation mutation = modelMapper.map(demoQrisCPMDto, Mutation.class);
        mutation.setUser(qris.getUser());
        mutation.setCreatedAt(LocalDateTime.now());
        mutation.setTransactionType(TransactionType.DEBIT);
        mutation.setMutationType(MutationType.QRIS);
        mutation.setTransactionPurpose(TransactionPurpose.PURCHASE);
        mutation.setFullName(demoQrisCPMDto.getMerchant());
        mutationRepository.save(mutation);

        qris.setUsed(true);

        qrisRepository.save(qris);
    }
}
