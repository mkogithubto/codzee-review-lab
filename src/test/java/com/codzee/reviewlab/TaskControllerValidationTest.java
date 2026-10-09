
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
}
