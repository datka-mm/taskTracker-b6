package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
@NoArgsConstructor
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workspace_gen")
    @SequenceGenerator(name = "workspace_gen", sequenceName = "workspace_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String name;

    private boolean isFavorite = false;

    @OneToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<UserWorkSpace> members;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<Card> allIssues;

    @ManyToOne(cascade = {ALL})
    private User lead;

    @OneToOne(cascade = {ALL}, mappedBy = "workspace")
    private UserWorkSpace userWorkSpace;

    @OneToMany(cascade = {ALL}, mappedBy = "workspace")
    private List<Board> boards;

    public Workspace(String name, boolean isFavorite, User lead) {
        this.name = name;
        this.isFavorite = isFavorite;
        this.lead = lead;
    }

}
