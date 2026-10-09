
package com.codzee.reviewlab;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskNotFoundTest {

    @Autowired
    private MockMvc mockMvc;

    private Long createdTaskId;

    @AfterEach
    void cleanUp() throws Exception {
        if (createdTaskId != null) {
            mockMvc.perform(delete("/api/tasks/" + createdTaskId))
                    .andExpect(status().isNoContent());
            createdTaskId = null;
        }
    }

    @Test
    void shouldReturn404WhenUpdatingDeletedTask() throws Exception {
        createdTaskId = createTask();

        mockMvc.perform(delete("/api/tasks/" + createdTaskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(put("/api/tasks/" + createdTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated task",
                                  "description": "Task was deleted",
                                  "completed": true
                                }
                                """))
                .andExpect(status().isNotFound());

        createdTaskId = null;
    }

    @Test
    void shouldReturn404WhenDeletingTaskTwice() throws Exception {
        createdTaskId = createTask();

        mockMvc.perform(delete("/api/tasks/" + createdTaskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/tasks/" + createdTaskId))
                .andExpect(status().isNotFound());

        createdTaskId = null;
    }

    private Long createTask() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Temporary task",
                                  "description": "Created for not-found testing",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                        .matcher(body);

        if (!matcher.find()) {
            throw new AssertionError("Created task ID was missing");
        }

        return Long.parseLong(matcher.group(1));
    }
}
