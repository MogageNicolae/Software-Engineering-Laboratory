package bms.domain;

import javax.persistence.*;

import java.io.Serializable;

@javax.persistence.Entity
@Table (name = "testers")
@AttributeOverride(name = "id", column = @Column(name = "id_tester"))
public class Tester extends Person implements Serializable {
    public Tester(int id, String name, String username, String password) {
        super(id, name, username, password);
    }

    public Tester(String name, String username, String password) {
        super(name, username, password);
    }

    public Tester(String username, String password) {
        super(username, password);
    }

    public Tester() {
    }
}
