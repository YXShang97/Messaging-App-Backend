package com.yuxin.messaging.integration;

import com.yuxin.messaging.dao.UserDAO;
import com.yuxin.messaging.dao.UserValidationCodeDAO;
import com.yuxin.messaging.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserAPIIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UserValidationCodeDAO userValidationCodeDAO;

    @BeforeEach
    public void deleteAllData() {
        this.userValidationCodeDAO.deleteAll();
        this.userDAO.deleteAll();
    }

    @Test
    public void testRegister_happyCase() throws Exception {
        var requestBody = "{\n" +
                "    \"username\": \"111\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"111@gmail.com\",\n" +
                "    \"password\": \"111\",\n" +
                "    \"repeatPassword\": \"111\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address111\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                                    .content(requestBody)
                                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        List<User> users = this.userDAO.selectByUserName("111");
        assertEquals(1, users.size());

        var insertedUser = users.get(0);
        assertEquals("hasa", insertedUser.getNickname());

        String validationCode = this.userValidationCodeDAO.selectByUserId(insertedUser.getId());
        assertNotNull(validationCode);
        assertEquals(6, validationCode.length());
    }

    @Test
    public void testRegister_passwordNotMatched_returnsBadRequest() throws Exception {
        var requestBody = "{\n" +
                "    \"username\": \"222\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"222@gmail.com\",\n" +
                "    \"password\": \"222\",\n" +
                "    \"repeatPassword\": \"111222\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address222\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("Password are not matched."));
    }
    @Test
    public void testRegister_userNameIsNull_returnsBadRequest() throws Exception {
        var requestBody = "{\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"222@gmail.com\",\n" +
                "    \"password\": \"222\",\n" +
                "    \"repeatPassword\": \"222\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address222\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1002))
                .andExpect(jsonPath("$.message").value("Username is empty."));
    }

    @Test
    public void testRegister_userNameIsEmpty_returnsBadRequest() throws Exception {
        var requestBody = "{\n" +
                "    \"username\": \"\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"222@gmail.com\",\n" +
                "    \"password\": \"222\",\n" +
                "    \"repeatPassword\": \"222\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address222\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1002))
                .andExpect(jsonPath("$.message").value("Username is empty."));
    }

    @Test
    public void testRegister_emailAlreadyExists_returnsBadRequest() throws Exception {
        var requestBody1 = "{\n" +
                "    \"username\": \"222\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"222@gmail.com\",\n" +
                "    \"password\": \"222\",\n" +
                "    \"repeatPassword\": \"222\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address222\"\n" +
                "}";
        var requestBody2 = "{\n" +
                "    \"username\": \"333\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"222@gmail.com\",\n" +
                "    \"password\": \"333\",\n" +
                "    \"repeatPassword\": \"333\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address333\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                        .content(requestBody1)
                        .contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(post("/users/register")
                .content(requestBody2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1003))
                .andExpect(jsonPath("$.message").value("Email already exists."));
    }

    @Test
    public void testRegister_userNameAlreadyExists_returnsBadRequest() throws Exception {
        var requestBody1 = "{\n" +
                "    \"username\": \"222\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"222@gmail.com\",\n" +
                "    \"password\": \"222\",\n" +
                "    \"repeatPassword\": \"222\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address222\"\n" +
                "}";
        var requestBody2 = "{\n" +
                "    \"username\": \"222\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"333@gmail.com\",\n" +
                "    \"password\": \"333\",\n" +
                "    \"repeatPassword\": \"333\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address333\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                .content(requestBody1)
                .contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(post("/users/register")
                        .content(requestBody2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1004))
                .andExpect(jsonPath("$.message").value("Username already exists."));
    }

    @Test
    public void testActivate_usingUserName_happyCase() throws Exception {
        var registerRequestBody = "{\n" +
                "    \"username\": \"111\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"111@gmail.com\",\n" +
                "    \"password\": \"111\",\n" +
                "    \"repeatPassword\": \"111\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address111\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                        .content(registerRequestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        List<User> users = this.userDAO.selectByUserName("111");
        var insertedUser = users.get(0);
        String validationCode = this.userValidationCodeDAO.selectByUserId(insertedUser.getId());

        var activateRequestBody = "{\n" +
                "    \"identification\": \"111\",\n" +
                "    \"validationCode\":" + validationCode + "\n" +
                "}";
        this.mockMvc.perform(post("/users/activate")
                        .content(activateRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(header().string("Content-Type", "application/json"))
                        .andExpect(jsonPath("$.code").value(1000))
                        .andExpect(jsonPath("$.message").value("SUCCESS"));

        String validationCodeAfterActivation = this.userValidationCodeDAO.selectByUserId(insertedUser.getId());
        assertNull(validationCodeAfterActivation);
    }

    @Test
    public void testActivate_usingEmail_happyCase() throws Exception {
        var registerRequestBody = "{\n" +
                "    \"username\": \"111\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"111@gmail.com\",\n" +
                "    \"password\": \"111\",\n" +
                "    \"repeatPassword\": \"111\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address111\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                .content(registerRequestBody)
                .contentType(MediaType.APPLICATION_JSON));

        List<User> users = this.userDAO.selectByUserName("111");
        var insertedUser = users.get(0);
        String validationCode = this.userValidationCodeDAO.selectByUserId(insertedUser.getId());

        var activateRequestBody = "{\n" +
                "    \"identification\": \"111@gmail.com\",\n" +
                "    \"validationCode\":" + validationCode + "\n" +
                "}";
        this.mockMvc.perform(post("/users/activate")
                        .content(activateRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        String validationCodeAfterActivation = this.userValidationCodeDAO.selectByUserId(insertedUser.getId());
        assertNull(validationCodeAfterActivation);
    }

    @Test
    public void testActivate_userNotExists_returnsBadRequest() throws Exception {
        var activateRequestBody = "{\n" +
                "    \"identification\": \"111\",\n" +
                "    \"validationCode\": \"111111\"\n" +
                "}";
        this.mockMvc.perform(post("/users/activate")
                        .content(activateRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1005))
                .andExpect(jsonPath("$.message").value("User doesn't exist."));
    }

    @Test
    public void testActivate_validationCodeNotMatch_returnsBadRequest() throws Exception {
        var registerRequestBody = "{\n" +
                "    \"username\": \"111\",\n" +
                "    \"nickname\": \"hasa\",\n" +
                "    \"email\": \"111@gmail.com\",\n" +
                "    \"password\": \"111\",\n" +
                "    \"repeatPassword\": \"111\",\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"address\": \"address111\"\n" +
                "}";
        this.mockMvc.perform(post("/users/register")
                .content(registerRequestBody)
                .contentType(MediaType.APPLICATION_JSON));

        var activateRequestBody = "{\n" +
                "    \"identification\": \"111\",\n" +
                "    \"validationCode\": \"111111\"\n" +
                "}";
        this.mockMvc.perform(post("/users/activate")
                        .content(activateRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.code").value(1006))
                .andExpect(jsonPath("$.message").value("Validation code not match."));
    }
}
