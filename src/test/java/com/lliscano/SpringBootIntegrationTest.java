package com.lliscano;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lliscano.dto.ResponseDTO;
import com.lliscano.dto.UserDTO;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpringBootIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:14.1-alpine")
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withInitScript("data.sql");
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void before() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void after() {
        postgreSQLContainer.stop();
    }


    @Test
    @Order(value = 1)
    @SneakyThrows
    void testSaveUser() {
        UserDTO fakeUserDTO = Instancio.of(UserDTO.class)
                .ignore(field(UserDTO::getId))
                .create();
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeUserDTO)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/users/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @Order(value = 2)
    @SneakyThrows
    void testUpdateUser() {
        final MvcResult result = mockMvc.perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ResponseDTO<UserDTO> responseDTO = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        new TypeReference<ResponseDTO<UserDTO>>() {});
        UserDTO fakeUserDTO = Instancio.of(UserDTO.class)
                .set(field(UserDTO::getId),responseDTO.getData().getId())
                .create();
        final MvcResult resultPut = mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeUserDTO)))
                .andExpect(status().isOk())
                .andReturn();
        ResponseDTO<UserDTO> responseDTOPut = objectMapper
                .readValue(resultPut.getResponse().getContentAsString(),
                        new TypeReference<ResponseDTO<UserDTO>>() {});
        assertEquals(responseDTOPut.getData().getId(), responseDTO.getData().getId());
    }

    @Test
    @Order(value = 3)
    @SneakyThrows
    void getUserPaginated() {
        List<UserDTO> fakeUsersDTO = Instancio.ofList(UserDTO.class)
                .size(20)
                .ignore(field(UserDTO::getId))
                .create();
        for (UserDTO userDTO : fakeUsersDTO) {
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDTO)))
                    .andExpect(status().isCreated());
        }
        mockMvc.perform(get("/users")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(21))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(3));
    }
    @Test
    @Order(value = 4)
    @SneakyThrows
    void testUserNotFoundexception() {
        mockMvc.perform(get("/users/1000").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User not found by given id: 1000"));
    }

    @Test
    @Order(value = 5)
    @SneakyThrows
    void testDeleteUser() {
        mockMvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @Order(value = 6)
    @SneakyThrows
    void testInvalidRequestBody() {
        UserDTO fakeUserDTO = Instancio.of(UserDTO.class)
                .ignore(field(UserDTO::getId))
                .ignore(field(UserDTO::getFirstname))
                .create();
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fakeUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].firstname").value("Required field"));
    }

    @Test
    @Order(value = 7)
    @SneakyThrows
    void testInvalidRequestParameter() {
        mockMvc.perform(get("/users/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
        mockMvc.perform(get("/users/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
        mockMvc.perform(get("/users")
                        .queryParam("page", "abc")
                        .queryParam("size", "def")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
