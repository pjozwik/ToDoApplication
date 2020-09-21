package io.github.todoapp.logic;

import io.github.todoapp.TaskConfigurationProperties;
import io.github.todoapp.model.ProjectRepository;
import io.github.todoapp.model.TaskGroup;
import io.github.todoapp.model.TaskGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.AssertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone group.")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateEsception() {
        //given
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_id(anyInt())).thenReturn(true);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //system under test
        var toTest = new ProjectService(mockGroupRepository, null, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("undone group");


    }

    @Test
    @DisplayName("should throw IllegalStateException when configuration ok amd no projects for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalStateEsception() {
        //given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());


        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //system under test
        var toTest = new ProjectService(null, mockRepository, mockConfig);

        //when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should create a new group project")
    void createGroup_configurationOk_existingProject_createsAndSaveGroup(){

        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //system under test
        var toTest = new ProjectService(inMemoryGroupRepo, mockRepository, mockConfig);

    }


    private TaskConfigurationProperties configurationReturning(boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private TaskGroupRepository inMemoryGroupRepository(){
        return new TaskGroupRepository(){
            private int index = 0;
            private Map<Integer, TaskGroup> map = new HashMap<>();

            @Override
            public List<TaskGroup> findAll() {
                return map.values().stream().collect(Collectors.toList());
            }

            @Override
            public TaskGroup save(TaskGroup entity) {
                if(entity.getId() == 0){
                    try {
                        TaskGroup.class.getDeclaredField("id").set(entity,++index);
                    } catch ( NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                map.put(entity.getId(), entity);
                return entity;
            }

            @Override
            public Optional<TaskGroup> findById(Integer id) {
                return Optional.ofNullable(map.get(id));
            }

            @Override
            public boolean existsByDoneIsFalseAndProject_id(Integer projID) {
                return map.values().stream()
                        .filter(taskGroup -> !taskGroup.isDone())
                        .anyMatch(taskGroup -> taskGroup.getProject() != null && taskGroup.getProject().getId() == projID);

            }
        };
    }
}