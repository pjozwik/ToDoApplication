package io.github.todoapp.logic;

import io.github.todoapp.TaskConfigurationProperties;
import io.github.todoapp.model.*;
import io.github.todoapp.model.projections.GroupReadModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    TaskGroupRepository repository;
    ProjectRepository projectRepository;
    private TaskConfigurationProperties config;

    public ProjectService(TaskGroupRepository repository, ProjectRepository projectRepository, TaskConfigurationProperties config) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.config = config;
    }

    public List<Project> readAll(){
        return projectRepository.findAll();
    }

    public Project save(final Project project){
        return projectRepository.save(project);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){
        if(!config.getTemplate().isAllowMultipleTasks() && repository.existsByDoneIsFalseAndProject_id(projectId)){
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        TaskGroup result = projectRepository.findById(projectId)
                    .map(project -> {
                        var targetGroup = new TaskGroup();
                        targetGroup.setDescription(project.getDescription());
                        targetGroup.setTasks(project.getSteps().stream()
                            .map(step -> new Task(
                                    step.getDescription(),
                                    deadline.plusDays(step.getDaysToDeadline()
                                    )
                        )).collect(Collectors.toSet())
                        );
                        targetGroup.setProject(project);
                        return repository.save(targetGroup);
                    }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        return new GroupReadModel(result);
    }

}
