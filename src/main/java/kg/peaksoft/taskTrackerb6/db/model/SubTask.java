package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@NoArgsConstructor
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_gen")
    @SequenceGenerator(name = "sub_gen", sequenceName = "sub_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    @Column(length = 10000)
    private String description;

    private Boolean isDone = false;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<User> workspacesUsers;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Estimation estimation;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Checklist checklist;

    public SubTask(String description, Boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    public void addMember(User user){
        if (workspacesUsers == null){
            workspacesUsers = new ArrayList<>();
        }
        workspacesUsers.add(user);
    }

}
