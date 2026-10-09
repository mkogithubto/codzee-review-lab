
package com.codzee.reviewlab.task;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final ConcurrentHashMap<Long, Task> tasks =
            new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(0);

    public Task createTask(Task task) {
        Long id = idGenerator.incrementAndGet();

        Task newTask = new Task(
                id,
                task.getTitle(),
                task.getDescription(),
                false
        );

        tasks.put(id, newTask);
        return newTask;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(Long id) {
        return tasks.get(id);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existingTask = tasks.get(id);

        if (existingTask == null) {
            return null;
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());

        return existingTask;
    }

    public boolean deleteTask(Long id) {
        return tasks.remove(id) != null;
    }
}
