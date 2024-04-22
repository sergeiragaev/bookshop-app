package com.example.BookShopApp.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AuthUserControllerTests {
    private final MockMvc mockMvc;
    @Autowired
    public AuthUserControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }
    @Test
    void handleUserRegistrationTest() throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setPhone("+7913123123");
        registrationForm.setName("Tester");
        registrationForm.setEmail("tester@test.org");
        registrationForm.setPass("123456");

        mockMvc.perform(post("/reg").flashAttr("registrationForm", registrationForm))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("regOk"));

        //Trying to register the same user again
        mockMvc.perform(post("/reg").flashAttr("registrationForm", registrationForm))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("regOk"))
                .andExpect(redirectedUrl("/signup"));
    }

    @Test
    public void logoutTest() throws Exception {
        mockMvc.perform(get("/logout"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }

    @Test
    public void loginUsingEmailTest() throws Exception {
        mockMvc.perform(formLogin("/signin").user("user@gmail.com").password("123456"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        //Trying to send bad credentials
        mockMvc.perform(formLogin("/signin").user("user@gmail.com").password("123456"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }
    @Test
    public void loginUsingPhoneTest() throws Exception {
        mockMvc.perform(formLogin("/signin").user( "+7 (123) 333-33-33").password("123456"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        //Trying to send bad credentials
        mockMvc.perform(formLogin("/signin").user( "+7(123)333-33-33").password("123456"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }

}