package bms.domain;

public interface Entity<Tid> {
    Tid getId();

    void setId(Tid id);
}
