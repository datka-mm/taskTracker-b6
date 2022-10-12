package kg.peaksoft.taskTrackerb6.entities;

import kg.peaksoft.taskTrackerb6.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.MERGE;

@Entity
@Table(name = "user_workspace_roles")
@Getter
@Setter
@NoArgsConstructor
public class UserWorkSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_workspace_roles_gen")
    @SequenceGenerator(name = "user_workspace_roles_gen", sequenceName = "user_workspace_roles_seq",allocationSize = 1)
    private Long id;

    @OneToOne(cascade = {REFRESH, DETACH, PERSIST, MERGE})
    private User user;

    @ManyToOne(cascade = {REFRESH, DETACH, PERSIST, MERGE})
    private Workspace workspace;

    private Role role;
}
