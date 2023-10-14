package com.lliscano.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users", schema = "public")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", schema = "public", allocationSize = 1)
    private Long id;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "gender", nullable = false, length = 100)
    private String gender;

}
