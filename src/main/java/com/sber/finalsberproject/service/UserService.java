package com.sber.finalsberproject.service;

import com.sber.finalsberproject.dto.AddUserDTO;
import com.sber.finalsberproject.dto.RoleDTO;
import com.sber.finalsberproject.dto.UserDTO;
import com.sber.finalsberproject.mapper.UserMapper;
import com.sber.finalsberproject.model.User;
import com.sber.finalsberproject.repository.UserRepository;
import com.sber.finalsberproject.utils.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class UserService extends GenericService<User, UserDTO> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       JavaMailSender javaMailSender) {
        super(userRepository, userMapper);
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.javaMailSender = javaMailSender;

    }
    public UserDTO update(final UserDTO updatedObject, MultipartFile file) {
        String fileName = FileHelper.createFile(file);
        updatedObject.setOnlineCopyPath(fileName);
        return mapper.toDTO(repository.save(mapper.toEntity(updatedObject)));
    }

    public void addCompany(final AddUserDTO addUserDTO){
        UserDTO user = getOne(addUserDTO.getUserId());
        user.setCompanyId(addUserDTO.getCompanyId());
        update(user);
    }

    public void dismissUser(final Long id){
        UserDTO user = getOne(id);
        user.setCompanyId(null);
        update(user);
    }

    public UserDTO create(final UserDTO newObject, MultipartFile file) {
        String fileName = FileHelper.createFile(file);
        newObject.setOnlineCopyPath(fileName);
        return create(newObject);
    }
    @Override
    public UserDTO create(UserDTO newObject){
        if (Objects.isNull(newObject.getRole())) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(1L);
            newObject.setRole(roleDTO);
            newObject.setCreatedBy("REGISTRATION FORM");
        }
        newObject.setPassword(bCryptPasswordEncoder.encode(newObject.getPassword()));
        newObject.setCreatedWhen(LocalDateTime.now());
        newObject.setActivatedBefore(LocalDate.now());
        return mapper.toDTO(repository.save(mapper.toEntity(newObject)));
    }

    public List<UserDTO> listAllByRole(final Long roleId) {
        List<User> users = ((UserRepository)repository).listAllByRoleAndCompany(roleId);
        return mapper.toDTOs(users);
    }
    public List<UserDTO> listAllByCompany(final Long companyId) {
        List<User> users = ((UserRepository)repository).findAllByCompanyId(companyId);
        return mapper.toDTOs(users);
    }
    public List<UserDTO> listAllByTaskId(final Long taskId) {
        List<User> users = ((UserRepository)repository).listUserByTaskId(taskId);
        return mapper.toDTOs(users);
    }

    public void createAdministrator(UserDTO newObject){
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(2L);
        newObject.setRole(roleDTO);
        newObject.setCreatedBy("ADMINISTRATOR CREATION FORM");
        create(newObject);
    }
    public UserDTO getUserByLogin(final String login) {
        return mapper.toDTO(((UserRepository) repository).findUserByLogin(login));
    }

    public UserDTO getUserByEmail(final String email) {
        return mapper.toDTO(((UserRepository) repository).findUserByEmail(email));
    }

    public boolean checkPassword(String password, UserDetails foundUser) {
        return bCryptPasswordEncoder.matches(password, foundUser.getPassword());
    }

    public Page<UserDTO> findUsers(UserDTO userDTO,
                                   Pageable pageable) {
        Page<User> users = ((UserRepository) repository).searchUsers(userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getLogin(),
                pageable);
        List<UserDTO> result = mapper.toDTOs(users.getContent());
        return new PageImpl<>(result, pageable, users.getTotalElements());
    }
}
