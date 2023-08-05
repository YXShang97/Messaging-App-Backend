package com.yuxin.messaging.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import com.yuxin.messaging.dao.UserDAO;
import com.yuxin.messaging.dao.UserValidationCodeDAO;
import com.yuxin.messaging.enums.Gender;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.User;
import com.yuxin.messaging.model.UserValidationCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserValidationCodeDAO userValidationCodeDAO;
    @Autowired
    private EmailService emailService;

    private static final Duration TOKEN_EXPIRY = Duration.ofDays(14);

    // QPS, Error, Latency
    public void register(String username,
                         String nickname,
                         String email,
                         String password,
                         String repeatPassword,
                         String address,
                         Gender gender) throws MessagingServiceException {
        // validation
        // passwords are matched
        if (!password.equals(repeatPassword)) {
            throw new MessagingServiceException(Status.PASSWORD_NOT_MATCH);
        }
        if (username == null || username.isEmpty()) {
            throw new MessagingServiceException(Status.EMPTY_USERNAME);
        }

        // check whether email already exists
        List<User> selectedUsers = this.userDAO.selectByEmail(email);
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            System.out.println(selectedUsers);
            throw new MessagingServiceException(Status.EMAIL_EXISTS_ALREADY);
        }

        // check whether username already exists
        List<User> selectedUsersByName = this.userDAO.selectByUserName(username);
        if (selectedUsersByName != null && !selectedUsersByName.isEmpty()) {
            System.out.println(selectedUsersByName);
            throw new MessagingServiceException(Status.USERNAME_EXISTS_ALREADY);
        }

        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setAddress(address);
        user.setGender(gender);
        user.setIsValid(false);
        user.setRegisterTime(new Date());

        // secure password with MD5 hashing and salting
        user.setPassword(saltedMD5(password));

        // insert into user table
        try {
            this.userDAO.insert(user);
        } catch (Exception e) {
            throw new MessagingServiceException(Status.UNKNOWN_EXCEPTION);
        }

        // generate validation code
        generateValidationCode(user);


        // send validation code to user via email
        // emailService.sendEmail(user.getEmail(), "Registration Validation", String.format("Validation code is: %s", validationCode));
    }

    public void activate(String identification, String validationCode) throws MessagingServiceException {
        User selectedUser = findUserByIdentification(identification);
        // select userValidationCode by selectedUser.id
        String code = this.userValidationCodeDAO.selectByUserId(selectedUser.getId());
        // compare -> N: throw exception
        if (!code.equals(validationCode)) {
            throw new MessagingServiceException(Status.VALIDATION_CODE_NOT_MATCH);
        }
        //         -> Y: 1. update selectedUser (set valid = 1)
        this.userDAO.updateValid(selectedUser.getId());
        //               2. delete userValidationCode
        this.userValidationCodeDAO.deleteByUserId(selectedUser.getId());
    }

    public String login(String identification, String password) throws MessagingServiceException {
        var selectedUser = findUserByIdentification(identification);
        if (!selectedUser.getPassword().equals(saltedMD5(password))) {
            throw new MessagingServiceException(Status.USERNAME_AND_PASSWORD_NOT_MATCH);
        }

        // TODO: check whether user is valid
        if (!selectedUser.getIsValid()) {
            throw new MessagingServiceException(Status.INVALID_USER);
        }

        String loginToken = RandomStringUtils.randomAlphanumeric(128);
        this.userDAO.login(loginToken, new Date(), selectedUser.getId());

        return loginToken;
    }

    public void logout(int userId) throws MessagingServiceException {
        this.userDAO.login(null, null, userId);
    }

    public void forgetPassword(String email) throws MessagingServiceException {
        User selectedUser = selectUserByEmail(email);

        // generate validation code
        generateValidationCode(selectedUser);

        // send validation code to user via email
        // emailService.sendEmail(selectedUser.getEmail(), "Forget Password Validation", String.format("Validation code is: %s", validationCode));
    }

    public void resetPassword(String email, String validationCode, String password, String repeatPassword) throws MessagingServiceException {
        User selectedUser = selectUserByEmail(email);
        // select userValidationCode by selectedUser.id
        String code = this.userValidationCodeDAO.selectByUserId(selectedUser.getId());
        // compare -> N: throw exception
        if (!code.equals(validationCode)) {
            throw new MessagingServiceException(Status.VALIDATION_CODE_NOT_MATCH);
        }
        //         -> validation
        //            passwords are matched
        if (!password.equals(repeatPassword)) {
            throw new MessagingServiceException(Status.PASSWORD_NOT_MATCH);
        }
        //         -> Y: update password
        this.userDAO.updatePassword(selectedUser.getId(), saltedMD5(password));
        this.userValidationCodeDAO.deleteByUserId(selectedUser.getId());
    }

    public static String saltedMD5(String input) throws MessagingServiceException {
        String md5 = null;

        if(input == null) return null;

        String salt = "Random$SaltValue#WithSpecialCharacters12@$@4&#%^$*";
        input += salt;

        try {
            //Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());
            //Converts message digest value in base 16 (hex)
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new MessagingServiceException(Status.UNKNOWN_EXCEPTION);
        }
        return md5;
    }

    private User findUserByIdentification(String identification) throws MessagingServiceException {
        List<User> selectedUsers = this.userDAO.selectByEmail(identification);
        if (selectedUsers.isEmpty()) {
            selectedUsers = this.userDAO.selectByUserName(identification);
            if (selectedUsers.isEmpty()) {
                throw new MessagingServiceException(Status.USER_NOT_EXISTS);
            }
        }
        if (selectedUsers.size() > 1) {
            throw new MessagingServiceException(Status.UNKNOWN_EXCEPTION);
        }
        return selectedUsers.get(0);
    }

    public User authenticate(String loginToken) throws MessagingServiceException {
        var selectedUser = this.userDAO.selectUserByLoginToken(loginToken);
        if (selectedUser == null) {
            throw new MessagingServiceException(Status.EXPIRED_LOGIN_TOKEN);
        }
        if (new Date().getTime() - selectedUser.getLastLoginTime().getTime() >= TOKEN_EXPIRY.toMillis()) {
            throw new MessagingServiceException(Status.EXPIRED_LOGIN_TOKEN);
        }

        return selectedUser;
    }

    private void generateValidationCode(User selectedUser) throws MessagingServiceException {
        var validationCode = RandomStringUtils.randomNumeric(6);
        UserValidationCode userValidationCode = new UserValidationCode();
        userValidationCode.setUserId(selectedUser.getId());
        userValidationCode.setValidationCode(validationCode);
        try {
            this.userValidationCodeDAO.insert(userValidationCode);
        } catch (Exception e) {
            throw new MessagingServiceException(Status.UNKNOWN_EXCEPTION);
        }
    }

    private User selectUserByEmail(String email) throws MessagingServiceException {
        List<User> selectedUsers = this.userDAO.selectByEmail(email);
        if (selectedUsers.isEmpty()) {
            throw new MessagingServiceException(Status.USER_NOT_EXISTS);
        }
        if (selectedUsers.size() > 1) {
            throw new MessagingServiceException(Status.UNKNOWN_EXCEPTION);
        }

        return selectedUsers.get(0);
    }

    public User selectById(int userId) throws MessagingServiceException {
        User user = this.userDAO.selectById(userId);
        if (user == null) {
            throw new MessagingServiceException(Status.USER_NOT_EXISTS);
        }
        return user;
    }
}
