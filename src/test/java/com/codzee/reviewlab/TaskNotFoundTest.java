
package com.codzee.reviewlab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskNotFoundTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn404WhenUpdatingNonexistentTask() throws Exception {
        mockMvc.perform(put("/api/tasks/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated task",
                                  "description": "Task does not exist",
                                  "completed": true
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/999999"))
                .andExpect(status().isNotFound());
    }
}
