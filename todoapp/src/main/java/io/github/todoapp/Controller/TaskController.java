package io.github.todoapp.Controller;

import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class TaskController {

    private static final Logger logger =  LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/tasks", params = {"!size", "!page", "!sort"})
    ResponseEntity<List<Task>> readAllTasks(){
        logger.warn("Esposing all tasks");
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/tasks")
    ResponseEntity<List<Task>> readAllTasks(Pageable pageable){
        logger.warn("Custom pageable");
        return ResponseEntity.ok(repository.findAll(pageable).getContent());
    }

    @Transactional
    @PutMapping("/tasks/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(!repository.existsById(id)) {
        return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task -> task.updateFrom(toUpdate));
        return ResponseEntity.noContent().build();

    }

    @Transactional
    @PatchMapping("/tasks/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id){
        if(!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task -> task.setDone(!task.isDone()));
        return ResponseEntity.noContent().build();

    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks/{id}")
    ResponseEntity<Task> getTaskById(@PathVariable int id){
        return repository.findById(id).map(task -> ResponseEntity.ok(task)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/tasks")
    ResponseEntity<Task> createTask(@RequestBody @Valid Task task){
        Task result = repository.save(task);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(task);
    }
}
