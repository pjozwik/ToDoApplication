package io.github.todoapp.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    List<Task>  findAll();
    Page<Task> findAll(Pageable pageable);
    List<Task> findByDone(@Param("state") boolean done);
    Task save(Task entity);
    Optional<Task> findById(Integer id);
    boolean existsById(Integer id);
    boolean existsByDoneIsFalseAndGroup_id(Integer groupID);
}
