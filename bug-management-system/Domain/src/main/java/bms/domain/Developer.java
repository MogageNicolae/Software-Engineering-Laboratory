package bms.domain;

import javax.persistence.*;

import java.io.Serializable;

@javax.persistence.Entity
@Table (name = "developers")
@AttributeOverride(name = "id", column = @Column(name = "id_developer"))
public class Developer extends Person implements Serializable {
    public Developer(int id, String name, String username, String password) {
        super(id, name, username, password);
    }

    public Developer(String name, String username, String password) {
        super(name, username, password);
    }

    public Developer(String username, String password) {
        super(username, password);
    }

    public Developer() {
    }
}
