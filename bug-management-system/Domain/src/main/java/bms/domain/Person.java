package bms.domain;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class Person implements Entity<Integer>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column (name = "name")
    private String name;
    @Column (name = "username")
    private String username;
    @Column (name = "password")
    private String password;

    public Person(int id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public Person(String name, String username, String password) {
        this.id = 0;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public Person(String username, String password) {
        this.id = 0;
        this.name = "";
        this.username = username;
        this.password = password;
    }

    public Person() {
        this.id = 0;
        this.name = "";
        this.username = "";
        this.password = "";
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(name, person.name) && Objects.equals(username, person.username) && Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
