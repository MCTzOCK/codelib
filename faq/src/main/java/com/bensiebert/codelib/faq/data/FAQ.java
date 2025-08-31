package com.bensiebert.codelib.faq.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "faq")
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

}