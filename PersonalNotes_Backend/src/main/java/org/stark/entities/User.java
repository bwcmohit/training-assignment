package org.stark.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.stark.enums.Roles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "notesList")
@EqualsAndHashCode(callSuper = true, exclude = "notesList")
public class User extends  BaseEntity{

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

   @Enumerated(EnumType.STRING)
    private Roles role;

    @Column(nullable = true)
    private String profileImageFilename;


    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Notes> notesList = new ArrayList<>();



}
