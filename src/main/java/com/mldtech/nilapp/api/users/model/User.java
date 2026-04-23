package com.mldtech.nilapp.api.users.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 50,name = "first_name")
    private String firstName;

    @Column(nullable = false, length = 50,name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(nullable = false, columnDefinition = "text")
    private String password;
//    @Column(name = "total_lifetime_coins")
//    private Long entityId;
    @Column(name = "total_lifetime_coins")
    private Long totalLifetimeCoins;

    private LocalDate birthday;
    @Column(name = "birth_year")
    private String birthYear;

    @Column(insertable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    @Column(insertable = false, updatable = false,name = "updated_at")
    private LocalDateTime updatedAt;

    private String state;
    private String country;
    private String city;
    private String county;

    private String language;
    private String timezone;
    @Column(nullable = false, length = 50,name = "force_password_change")
    private Boolean forcePasswordChange;
    @Column(nullable = false, length = 50,name = "session_id")
    private String sessionId;
}

