package com.yuxin.messaging.service;

import com.yuxin.messaging.dao.UserDAO;
import com.yuxin.messaging.dao.UserValidationCodeDAO;
import com.yuxin.messaging.enums.Gender;
import com.yuxin.messaging.enums.Status;
import com.yuxin.messaging.exception.MessagingServiceException;
import com.yuxin.messaging.model.User;
import com.yuxin.messaging.model.UserValidationCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserDAO userDAO;
    @Mock
    UserValidationCodeDAO userValidationCodeDAO;
    @Mock
    EmailService emailService;

    @InjectMocks
    private UserService userService = new UserService();

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    ArgumentCaptor<UserValidationCode> userValidationCodeArgumentCaptor;

    @Test
    public void testRegister_happyCase() throws Exception {
        when(this.userDAO.selectByUserName("username")).thenReturn(List.of());
        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of());
        doAnswer(invocationOnMock -> {
            User user = invocationOnMock.getArgument(0);
            user.setId(1);
            return null;
        }).when(this.userDAO).insert(any(User.class));
        doNothing().when(this.userValidationCodeDAO).insert(any(UserValidationCode.class));
        // doNothing().when(this.emailService).sendEmail(eq("email@gmail.com"), eq("Registration Validation"), any(String.class));

        this.userService.register("username", "nickname", "email@gmail.com",
                "xxx", "xxx", "", Gender.FEMALE);

        verify(this.userDAO).selectByUserName("username");
        verify(this.userDAO).selectByEmail("email@gmail.com");

        verify(this.userDAO).insert(userArgumentCaptor.capture());
        var capturedUser = userArgumentCaptor.getValue();
        assertEquals("username", capturedUser.getUsername());
        assertFalse(capturedUser.getIsValid());

        verify(this.userValidationCodeDAO).insert(userValidationCodeArgumentCaptor.capture());
        var capturedUserValidationCode = userValidationCodeArgumentCaptor.getValue();
        assertEquals(1, capturedUserValidationCode.getUserId());
        assertEquals(6, capturedUserValidationCode.getValidationCode().length());

        // verify(this.emailService).sendEmail(eq("email@gmail.com"), eq("Registration Validation"), any(String.class));
    }

    @Test
    public void testRegister_emailAlreadyExists_throwsMessagingServiceException() throws Exception {
        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of(new User()));

        MessagingServiceException messagingServiceException = assertThrows(MessagingServiceException.class,
                () -> this.userService.register("username", "nickname", "email@gmail.com",
                        "xxx", "xxx", "", Gender.FEMALE));

        assertEquals(Status.EMAIL_EXISTS_ALREADY, messagingServiceException.getStatus());
        verify(this.userDAO).selectByEmail("email@gmail.com");
    }

    @Test
    public void testRegister_userNameAlreadyExists_throwsMessagingServiceException() throws Exception {
        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of());
        when(this.userDAO.selectByUserName("username")).thenReturn(List.of(new User()));

        MessagingServiceException messagingServiceException = assertThrows(MessagingServiceException.class,
                () -> this.userService.register("username", "nickname", "email@gmail.com",
                        "xxx", "xxx", "", Gender.FEMALE));

        assertEquals(Status.USERNAME_EXISTS_ALREADY, messagingServiceException.getStatus());
        verify(this.userDAO).selectByEmail("email@gmail.com");
        verify(this.userDAO).selectByUserName("username");
    }

    @Test
    public void testRegister_encounterExceptionWhenInsertUser_throwsMessagingServiceException() throws Exception {
        when(this.userDAO.selectByUserName("username")).thenReturn(List.of());
        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of());
        doThrow(NullPointerException.class).when(this.userDAO).insert(any(User.class));


        MessagingServiceException messagingServiceException = assertThrows(MessagingServiceException.class,
                () -> this.userService.register("username", "nickname", "email@gmail.com",
                        "xxx", "xxx", "", Gender.FEMALE));

        assertEquals(Status.UNKNOWN_EXCEPTION, messagingServiceException.getStatus());
        verify(this.userDAO).selectByUserName("username");
        verify(this.userDAO).selectByEmail("email@gmail.com");
        verify(this.userDAO).insert(any(User.class));
    }

    @Test
    public void testRegister_encounterExceptionWhenInsertUserValidationCode_throwsMessagingServiceException() throws Exception {
        when(this.userDAO.selectByUserName("username")).thenReturn(List.of());
        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of());
        doNothing().when(this.userDAO).insert(any(User.class));
        doThrow(NullPointerException.class).when(this.userValidationCodeDAO).insert(any(UserValidationCode.class));


        MessagingServiceException messagingServiceException = assertThrows(MessagingServiceException.class,
                () -> this.userService.register("username", "nickname", "email@gmail.com",
                        "xxx", "xxx", "", Gender.FEMALE));

        assertEquals(Status.UNKNOWN_EXCEPTION, messagingServiceException.getStatus());
        verify(this.userDAO).selectByUserName("username");
        verify(this.userDAO).selectByEmail("email@gmail.com");
        verify(this.userDAO).insert(any(User.class));
        verify(this.userValidationCodeDAO).insert(any(UserValidationCode.class));
    }

    @Test
    public void testActivate_withEmailAsIdentification_happyCase() throws Exception {
        User user = User.builder()
                .id(111)
                .username("username")
                .nickname("nickname")
                .email("email@gmail.com")
                .password("xxx")
                .address("")
                .gender(Gender.FEMALE)
                .build();

        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of(user));
        when(this.userValidationCodeDAO.selectByUserId(111)).thenReturn("111111");
        doNothing().when(this.userDAO).updateValid(111);
        doNothing().when(this.userValidationCodeDAO).deleteByUserId(111);

        this.userService.activate("email@gmail.com", "111111");

        verify(this.userDAO).selectByEmail("email@gmail.com");
        verify(this.userValidationCodeDAO).selectByUserId(111);
        verify(this.userDAO).updateValid(111);
        verify(this.userValidationCodeDAO).deleteByUserId(111);
    }

    @Test
    public void testActivate_withUserNameAsIdentification_happyCase() throws Exception {
        User user = User.builder()
                .id(111)
                .username("username")
                .nickname("nickname")
                .email("email@gmail.com")
                .password("xxx")
                .address("")
                .gender(Gender.FEMALE)
                .build();

        when(this.userDAO.selectByEmail("username")).thenReturn(List.of());
        when(this.userDAO.selectByUserName("username")).thenReturn(List.of(user));
        when(this.userValidationCodeDAO.selectByUserId(111)).thenReturn("111111");
        doNothing().when(this.userDAO).updateValid(111);
        doNothing().when(this.userValidationCodeDAO).deleteByUserId(111);

        this.userService.activate("username", "111111");

        verify(this.userDAO).selectByEmail("username");
        verify(this.userDAO).selectByUserName("username");
        verify(this.userValidationCodeDAO).selectByUserId(111);
        verify(this.userDAO).updateValid(111);
        verify(this.userValidationCodeDAO).deleteByUserId(111);
    }

    @Test
    public void testActivate_UserNotExists_throwsMessagingServiceException() throws Exception {

        when(this.userDAO.selectByEmail("identification")).thenReturn(List.of());
        when(this.userDAO.selectByUserName("identification")).thenReturn(List.of());

        MessagingServiceException messagingServiceException = assertThrows(MessagingServiceException.class,
                () -> this.userService.activate("identification", "111111"));

        assertEquals(Status.USER_NOT_EXISTS, messagingServiceException.getStatus());
        verify(this.userDAO).selectByEmail("identification");
        verify(this.userDAO).selectByUserName("identification");
    }

    @Test
    public void testActivate_validationCodeNotMatch_throwsMessagingServiceException() throws Exception {
        User user = User.builder()
                .id(111)
                .username("username")
                .nickname("nickname")
                .email("email@gmail.com")
                .password("xxx")
                .address("")
                .gender(Gender.FEMALE)
                .build();

        when(this.userDAO.selectByEmail("email@gmail.com")).thenReturn(List.of(user));
        when(this.userValidationCodeDAO.selectByUserId(111)).thenReturn("111111");

        MessagingServiceException messagingServiceException = assertThrows(MessagingServiceException.class,
                () -> this.userService.activate("email@gmail.com", "222222"));

        assertEquals(Status.VALIDATION_CODE_NOT_MATCH, messagingServiceException.getStatus());
        verify(this.userDAO).selectByEmail("email@gmail.com");
        verify(this.userValidationCodeDAO).selectByUserId(111);
    }


    @Test
    public void testMd5_happyCase() throws Exception {
        String input = "password";
        var md5String = UserService.saltedMD5(input);
        assertEquals("499a5f270313b56b5a3e59b28edc27a0", md5String);
    }

    @Test // testTarget_scenario_expectation()
    public void testMd5_nullInput_returnsNull() throws Exception {
        String input = null;
        assertNull(UserService.saltedMD5(input));
    }
}
