package io.github.todoapp.logic;

import io.github.todoapp.model.TaskGroup;
import io.github.todoapp.model.TaskGroupRepository;
import io.github.todoapp.model.TaskRepository;
import io.github.todoapp.model.projections.GroupReadModel;
import io.github.todoapp.model.projections.GroupWriteModel;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScope
public class TaskGroupService {

 private TaskGroupRepository repository;
 private TaskRepository taskRepository;

    public TaskGroupService(TaskGroupRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }


    public GroupReadModel createGroup(GroupWriteModel source){
        TaskGroup result = repository.save(source.toGroup());
        return  new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));
        result.setDone(!result.isDone());
        repository.save(result);
    }
}
