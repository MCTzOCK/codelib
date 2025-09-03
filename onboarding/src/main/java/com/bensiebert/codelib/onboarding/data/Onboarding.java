package com.bensiebert.codelib.onboarding.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "onboarding")
public class Onboarding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String settings_key;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    @ElementCollection
    private List<String> answers;

}