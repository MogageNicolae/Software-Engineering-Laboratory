package bms.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@javax.persistence.Entity
@Table(name = "bugs")
public class Bug implements Entity<Integer>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "lastUpdate")
    private LocalDateTime lastUpdate;
    @Column(name = "severity")
    private int severity;
    @Column(name = "testerId")
    private int testerId;
    @Enumerated(EnumType.STRING)
    private StatusBug status;

    public Bug(int id, String name, String description, LocalDateTime lastUpdate, int severity, int testerId, StatusBug status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.severity = severity;
        this.testerId = testerId;
        this.status = status;
    }

    public Bug(String name, String description, LocalDateTime lastUpdate, int severity, int testerId, StatusBug status) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.severity = severity;
        this.testerId = testerId;
        this.status = status;
    }

    public Bug() {
        this.id = 0;
        this.name = "";
        this.description = "";
        this.lastUpdate = LocalDateTime.now();
        this.severity = 0;
        this.testerId = 0;
        this.status = StatusBug.UNSOLVED;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getTesterId() {
        return testerId;
    }

    public void setTesterId(int testerId) {
        this.testerId = testerId;
    }

    public StatusBug getStatus() {
        return status;
    }

    public void setStatus(StatusBug status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Bug{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", severity=" + severity +
                ", status=" + status +
                '}';
    }
}
