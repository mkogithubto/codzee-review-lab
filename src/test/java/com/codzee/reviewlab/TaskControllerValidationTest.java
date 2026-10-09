
package com.codzee.reviewlab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectBlankTitleWhenCreatingTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "   ",
                                  "description": "Test validation",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankTitleWhenUpdatingTask() throws Exception {
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Test validation",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldCreateTaskWhenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Complete evaluation",
                              "description": "Test valid task creation",
                              "completed": false
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Complete evaluation"));
    }

    @Test
    void shouldRejectTitleLongerThan100Characters() throws Exception {
        String longTitle = "a".repeat(101);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "%s",
                              "description": "Test title length",
                              "completed": false
                            }
                            """.formatted(longTitle)))
                .andExpect(status().isBadRequest());
    }

}
