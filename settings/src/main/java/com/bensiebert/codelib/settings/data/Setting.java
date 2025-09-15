package com.bensiebert.codelib.settings.data;

import com.bensiebert.codelib.auth.data.User;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueUserAndKey", columnNames = {"user_id", "key"})})
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Hidden
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @With private User user;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;
}
