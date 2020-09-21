package io.github.todoapp.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskGroupRepository {

    List<TaskGroup>  findAll();

    TaskGroup save(TaskGroup entity);

    Optional<TaskGroup> findById(Integer id);

    boolean existsByDoneIsFalseAndProject_id(Integer projID);
}
