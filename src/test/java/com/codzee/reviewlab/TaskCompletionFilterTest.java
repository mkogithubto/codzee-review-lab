package com.codzee.reviewlab;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskCompletionFilterTest {

    @Autowired
    private MockMvc mockMvc;

    private Long completedTaskId;
    private Long incompleteTaskId;

    @AfterEach
    void cleanUp() throws Exception {
        Throwable cleanupFailure = null;

        try {
            if (completedTaskId != null) {
                mockMvc.perform(delete("/api/tasks/" + completedTaskId))
                        .andExpect(status().isNoContent());
            }
        } catch (Exception | AssertionError exception) {
            cleanupFailure = exception;
        }

        try {
            if (incompleteTaskId != null) {
                mockMvc.perform(delete("/api/tasks/" + incompleteTaskId))
                        .andExpect(status().isNoContent());
            }
        } catch (Exception | AssertionError exception) {
            if (cleanupFailure == null) {
                cleanupFailure = exception;
            } else {
                cleanupFailure.addSuppressed(exception);
            }
        }

        if (cleanupFailure instanceof Exception exception) {
            throw exception;
        }

        if (cleanupFailure instanceof AssertionError assertionError) {
            throw assertionError;
        }
    }

    @Test
    void shouldReturnOnlyCompletedTasks() throws Exception {
        completedTaskId = createTask("Completed task", true);
        incompleteTaskId = createTask("Incomplete task", false);

        mockMvc.perform(get("/api/tasks")
                        .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == "
                        + completedTaskId + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == "
                        + incompleteTaskId + ")]").doesNotExist())
                .andExpect(jsonPath("$[?(@.completed == false)]").isEmpty());
    }

    @Test
    void shouldReturnOnlyIncompleteTasks() throws Exception {
        completedTaskId = createTask("Completed task", true);
        incompleteTaskId = createTask("Incomplete task", false);

        mockMvc.perform(get("/api/tasks")
                        .param("completed", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == "
                        + incompleteTaskId + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == "
                        + completedTaskId + ")]").doesNotExist())
                .andExpect(jsonPath("$[?(@.completed == true)]").isEmpty());
    }

    @Test
    void shouldReturnAllTasksWhenCompletionFilterIsOmitted()
            throws Exception {
        completedTaskId = createTask("Completed task", true);
        incompleteTaskId = createTask("Incomplete task", false);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == "
                        + completedTaskId + ")]").exists())
                .andExpect(jsonPath("$[?(@.id == "
                        + incompleteTaskId + ")]").exists());
    }

    private Long createTask(String title, boolean completed)
            throws Exception {
        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "description": "Created for filter testing",
                                  "completed": %s
                                }
                                """.formatted(title, completed)))
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