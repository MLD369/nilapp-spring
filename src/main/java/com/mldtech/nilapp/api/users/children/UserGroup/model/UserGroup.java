package com.mldtech.nilapp.api.users.children.UserGroup.model;

//import com.mldtech.nilapp.api.group.model.Group;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mldtech.nilapp.api.group.model.Group;
import com.mldtech.nilapp.api.users.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "user_groups",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_groups_id") // TODO make singular
    private Long userGroupId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
