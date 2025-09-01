package com.bensiebert.codelib.todo.data;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @With
    private String description;
    @With
    private boolean completed;
    @With
    private String dueDate;
    @With
    private Integer priority;

}
