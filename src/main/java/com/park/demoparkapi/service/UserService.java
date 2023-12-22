package com.park.demoparkapi.service;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.exception.EntityNotFoundException;
import com.park.demoparkapi.exception.UsernameUniqueViolationException;
import com.park.demoparkapi.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;

    @Transactional /* Spring gerencia ciclo de vida da transação */
    public User save(User user) {
        try {
            return userRepo.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("User {%s} already registered.", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%s not found.", id))
        );
    }

    @Transactional()
    public User updatePassword(Long id, String password, String newPassword, String confirmNewPassword) {

        if (!newPassword.equals(confirmNewPassword)) throw new RuntimeException("Incorrect new password.");

        User user = getById(id);

        if(!user.getPassword().equals(password)) throw new RuntimeException("Your password not confer.");

        user.setPassword(password);
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepo.findAll();
    }
}
