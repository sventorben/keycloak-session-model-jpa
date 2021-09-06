package de.sventorben.keycloak.models.jpa.authSession;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "AUTHENTICATION_SESSION_NOTES")
public final class JpaNoteEntity {

    @Id
    @Column(nullable = false)
    private String id;

    @Column
    @Enumerated(EnumType.STRING)
    private NoteType type;

    @Column
    private String name;

    @Column
    private String value;

    @ManyToOne
    private JpaAuthenticationSessionEntity session;

    private JpaNoteEntity() {
        this.id = UUID.randomUUID().toString();
    }

    JpaNoteEntity(JpaAuthenticationSessionEntity session, NoteType type, String name, String value) {
        this();
        this.session = session;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    NoteType getType() {
        return this.type;
    }

    String getName() {
        return this.name;
    }

    String getValue() {
        return this.value;
    }

    void setValue(String value) {
        this.value = value;
    }

    enum NoteType {
        CLIENT,
        AUTH,
        USER
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaNoteEntity that = (JpaNoteEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
