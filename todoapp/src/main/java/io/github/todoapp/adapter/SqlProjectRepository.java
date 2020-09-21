package io.github.todoapp.adapter;

import io.github.todoapp.model.Project;
import io.github.todoapp.model.ProjectRepository;
import io.github.todoapp.model.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlProjectRepository extends ProjectRepository, JpaRepository<Project, Integer> {
    @Override
    @Query("from Project p join fetch p.steps")
    List<Project> findAll();


}
