
package com.codzee.reviewlab;

import com.codzee.reviewlab.task.Task;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskValidationTest {

    private final Validator validator;

    TaskValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    void shouldRejectBlankTaskTitle() {
        Task task = new Task(1L, "   ", "Description", false);

        assertFalse(validator.validate(task).isEmpty());
    }

    @Test
    void shouldAcceptValidTask() {
        Task task = new Task(
                1L,
                "Complete assignment",
                "Test task validation",
                false
        );

        assertTrue(validator.validate(task).isEmpty());
    }

    @Test
    void shouldRejectTitleLongerThan100Characters() {
        Task task = new Task(1L, "A".repeat(101), "Description", false);

        assertFalse(validator.validate(task).isEmpty());
    }

    @Test
    void shouldRejectDescriptionLongerThan500Characters() {
        Task task = new Task(1L, "Valid title", "A".repeat(501), false);

        assertFalse(validator.validate(task).isEmpty());
    }
}
