package com.bensiebert.codelib.todo.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "todo_list")
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoItem> items;
}
