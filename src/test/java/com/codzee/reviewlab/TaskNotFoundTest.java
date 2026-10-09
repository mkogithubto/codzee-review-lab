package com.codzee.reviewlab;

import com.codzee.reviewlab.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        }
    }

    @Test
    void shouldReturn404WhenUpdatingDeletedTask() throws Exception {
        createdTaskId = createTask();
        Long deletedTaskId = createdTaskId;

        // Verify that updating an existing task works.
        mockMvc.perform(put("/api/tasks/" + deletedTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated task",
                                  "description": "Updated before deletion",
                                  "completed": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated task"))
                .andExpect(jsonPath("$.description")
                        .value("Updated before deletion"))
                .andExpect(jsonPath("$.completed").value(true));

        // Delete the task.
        mockMvc.perform(delete("/api/tasks/" + deletedTaskId))
                .andExpect(status().isNoContent());

        // Prevent cleanup from trying to delete it again.
        createdTaskId = null;

        // Updating the deleted task must return 404.
        mockMvc.perform(put("/api/tasks/" + deletedTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Another update",
                                  "description": "Task was deleted",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingTaskTwice() throws Exception {
        createdTaskId = createTask();
        Long deletedTaskId = createdTaskId;

        // First deletion should succeed.
        mockMvc.perform(delete("/api/tasks/" + deletedTaskId))
                .andExpect(status().isNoContent());

        // Prevent cleanup from attempting a second deletion.
        createdTaskId = null;

        // Second deletion should return 404.
        mockMvc.perform(delete("/api/tasks/" + deletedTaskId))
                .andExpect(status().isNotFound());
    }

    private Long createTask() throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Temporary task",
                                  "description": "Created for not-found testing",
                                  "completed": false
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                .matcher(response);

        if (!matcher.find()) {
            throw new AssertionError(
                    "Task creation response did not contain a valid ID: "
                            + response);
        }

        return Long.parseLong(matcher.group(1));
    }
}
