
package com.codzee.reviewlab;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerValidationTest {

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
        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Complete evaluation",
                              "description": "Test valid task creation",
                              "completed": true
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Complete evaluation"))
                .andExpect(jsonPath("$.description")
                        .value("Test valid task creation"))
                .andExpect(jsonPath("$.completed").value(true))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                .matcher(responseBody);

        if (!matcher.find()) {
            throw new AssertionError("Created task ID was missing");
        }

        createdTaskId = Long.parseLong(matcher.group(1));

        mockMvc.perform(get("/api/tasks/" + createdTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTaskId))
                .andExpect(jsonPath("$.title").value("Complete evaluation"))
                .andExpect(jsonPath("$.description")
                        .value("Test valid task creation"))
                .andExpect(jsonPath("$.completed").value(true));
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

    @Test
    void shouldAcceptTitleWithExactly100Characters() throws Exception {
        String title = "a".repeat(100);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "description": "Boundary test",
                                  "completed": true
                                }
                                """.formatted(title)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                .matcher(responseBody);

        if (!matcher.find()) {
            throw new AssertionError("Created task ID was missing");
        }

        createdTaskId = Long.parseLong(matcher.group(1));

        mockMvc.perform(get("/api/tasks/" + createdTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.completed").value(true));
    }


    @Test
    void shouldUpdateTaskWhenRequestIsValid() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Original task",
                              "description": "Original description",
                              "completed": false
                            }
                            """))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();

        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)")
                .matcher(responseBody);

        if (!matcher.find()) {
            throw new AssertionError("Created task ID was missing");
        }

        createdTaskId = Long.parseLong(matcher.group(1));

        mockMvc.perform(put("/api/tasks/" + createdTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Updated task",
                              "description": "Updated description",
                              "completed": true
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdTaskId))
                .andExpect(jsonPath("$.title").value("Updated task"))
                .andExpect(jsonPath("$.description")
                        .value("Updated description"))
                .andExpect(jsonPath("$.completed").value(true));
    }

}
