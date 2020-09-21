package io.github.todoapp.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;


@Entity             //adnotacja ze jest to encja
@Table(name = "tasks")              //adnotacja ze to tabela
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Task's description must not be empty")
    private String description;
    private LocalDateTime deadline;
    private boolean done;

    @Embedded
    private Audit audit = new Audit();

    @ManyToOne
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;

    Task(){}

    public Task(String description, LocalDateTime deadline){
        this.description = description;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

     void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    TaskGroup getGroup() {
        return group;
    }

    public void updateFrom(Task source){
        done = source.done;
        description = source.description;
        deadline = source.deadline;
        group = source.group;
    }

}
