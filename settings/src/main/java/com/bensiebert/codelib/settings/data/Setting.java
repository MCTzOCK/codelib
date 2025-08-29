package com.bensiebert.codelib.settings.data;

import com.bensiebert.codelib.auth.data.User;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @With private User user;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;
}
