package io.github.todoapp.adapter;

import io.github.todoapp.model.Task;
import io.github.todoapp.model.TaskGroup;
import io.github.todoapp.model.TaskGroupRepository;
import io.github.todoapp.model.TaskRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlTaskGroupRepository extends TaskGroupRepository, JpaRepository<TaskGroup, Integer> {
    @Override
    @Query("from TaskGroup g join fetch g.tasks")
    List<TaskGroup> findAll();

    @Override
    boolean existsByDoneIsFalseAndProject_id(Integer projectID);
}
