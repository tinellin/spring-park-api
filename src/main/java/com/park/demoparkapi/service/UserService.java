package com.park.demoparkapi.service;

import com.park.demoparkapi.entity.User;
import com.park.demoparkapi.exception.EntityNotFoundException;
import com.park.demoparkapi.exception.PasswordInvalidException;
import com.park.demoparkapi.exception.UsernameUniqueViolationException;
import com.park.demoparkapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* Spring gerencia ciclo de vida da transação */
    @Transactional
    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("User {%s} already registered.", user.getUsername()));
        }
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%s not found.", id))
        );
    }

    @Transactional()
    public User updatePassword(Long id, String password, String newPassword, String confirmNewPassword) {

        if (!newPassword.equals(confirmNewPassword)) throw new PasswordInvalidException("Incorrect new password.");

        User user = getById(id);

        if(!passwordEncoder.matches(password, user.getPassword())) throw new PasswordInvalidException("Your password not confer.");

        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("User '%s' not found.", username))
        );
    }
    @Transactional(readOnly = true)
    public User.Role getRoleById(String username) {
        return userRepository.findRoleByUsername(username);
    }
}
