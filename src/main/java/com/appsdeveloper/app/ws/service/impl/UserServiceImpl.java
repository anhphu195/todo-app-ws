package com.appsdeveloper.app.ws.service.impl;

import com.appsdeveloper.app.ws.io.entity.PasswordResetTokenEntity;
import com.appsdeveloper.app.ws.io.entity.TaskEntity;
import com.appsdeveloper.app.ws.io.repositories.PasswordResetTokenRepository;
import com.appsdeveloper.app.ws.io.repositories.UserRepository;
import com.appsdeveloper.app.ws.io.entity.UserEntity;
import com.appsdeveloper.app.ws.service.HtmlMail;
import com.appsdeveloper.app.ws.service.UserService;
import com.appsdeveloper.app.ws.shared.Utils;
import com.appsdeveloper.app.ws.shared.dto.AddressDto;
import com.appsdeveloper.app.ws.shared.dto.UserDto;
import com.appsdeveloper.app.ws.ui.model.response.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    HtmlMail htmlMail;

    @Override
    public UserDto createUser(UserDto user) {

        if (userRepository.findUserByEmail(user.getEmail()) != null) throw new RuntimeException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        //UserEntity userEntity = new UserEntity();
        //BeanUtils.copyProperties(user, userEntity);

        for (int i=0; i<user.getAddresses().size() ; i++){
            AddressDto addressDto = user.getAddresses().get(i);
            addressDto.setUserDetails(user);
            addressDto.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i,addressDto);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user,UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncrytedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(Utils.generateEmailVerificationToken(publicUserId));
        //default = false;
        //userEntity.setEmailVerificationStatus(false)
        userEntity.setTasks(new ArrayList<>());

        UserEntity storedUserDetails = userRepository.save(userEntity);

        //UserDto returnValue = new UserDto();
        UserDto returnValue = modelMapper.map(storedUserDetails,UserDto.class);



        return returnValue;
    }

    @Override
    public UserDto getUser(String email){
        UserEntity userEntity = userRepository.findUserByEmail(email);
        if(userEntity == null) throw new UsernameNotFoundException(email);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UsernameNotFoundException("User with ID : " + userId+ " Not Found");
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDto updateUser(String userId,UserDto userDto) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UsernameNotFoundException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(updatedUserDetails,returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(userEntity == null) throw new UsernameNotFoundException(ErrorMessages.COULD_NOT_DELETE_RECORD.getErrorMessage());
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        if (page > 0) page -= 1;
        Pageable pageableRest = PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRest);
        List<UserEntity> users = usersPage.getContent();
        for (UserEntity userEntity : users){
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity,userDto);
            returnValue.add(userDto);
        }
        return returnValue;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;
        UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
        if (userEntity != null){
            boolean hastokenExpired = Utils.hasTokenExpired(token);
            if (!hastokenExpired){
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        boolean returnValue = false;
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            return returnValue;
        }
        String token = new Utils().generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByUserDetails(userEntity);
        if (passwordResetTokenEntity == null){
            passwordResetTokenEntity = new PasswordResetTokenEntity();
        }
        //PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();

        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);
        String htmlMsg = "<h1>A request to reset your password</h1>"
                + "<p>Hi "+userEntity.getLastName()+"</p><br/>"
                +"<p>the password for your candidate profile has just recently been changed.</p><br/>"
                +"<p>If you changed the password yourself, no further action on your part is required.</p><br/>"
                +"<p>In case you are not aware of any password changes, your profile may have been compromised. To regain access, you can always reset the password.</p><br/>"
                +"<a href='http://localhost:8080/verification-service/password-reset.html?token=" + token + "'> Reset Password </a><br/>"
                +"<p>Thank you</p>";

        htmlMail.sendMail(email,"Password-Reset!", htmlMsg);
        returnValue = true;
        return returnValue;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;
        if (Utils.hasTokenExpired(token)){
            return returnValue;
        }

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if(passwordResetTokenEntity == null){
            return returnValue;
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncrytedPassword(encodedPassword);
        UserEntity savedUser = userRepository.save(userEntity);

        if(savedUser != null && savedUser.getEncrytedPassword().equalsIgnoreCase(encodedPassword)){
            returnValue = true;
        }
        passwordResetTokenRepository.delete(passwordResetTokenEntity);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncrytedPassword(), userEntity.getEmailVerificationStatus(), true,true,true, new ArrayList<>());

        //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
