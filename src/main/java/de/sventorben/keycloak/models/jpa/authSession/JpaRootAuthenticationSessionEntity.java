package de.sventorben.keycloak.models.jpa.authSession;

import org.keycloak.common.util.Time;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.sventorben.keycloak.models.jpa.authSession.JpaRootAuthenticationSessionEntity.QUERY_DELETE_BY_REALM_ID;
import static de.sventorben.keycloak.models.jpa.authSession.JpaRootAuthenticationSessionEntity.QUERY_REMOVE_EXPIRED;

@Entity
@Table(name = "ROOT_AUTHENTICATION_SESSIONS")
@NamedQueries({
        @NamedQuery(name = QUERY_REMOVE_EXPIRED, query = "delete from JpaRootAuthenticationSessionEntity e where e.realmId = :realmId and e.timestamp < :expired"),
        @NamedQuery(name = QUERY_DELETE_BY_REALM_ID, query = "delete from JpaRootAuthenticationSessionEntity e where e.realmId = :realmId")
})
final class JpaRootAuthenticationSessionEntity {

    static final String QUERY_REMOVE_EXPIRED = "JpaRootAuthenticationSessionEntity.removeExpired";
    static final String QUERY_DELETE_BY_REALM_ID = "JpaRootAuthenticationSessionEntity.delteByRealmId";

    @Id
    @Column
    private String id;

    @Column
    private String realmId;

    @Column
    private int timestamp;

    @OneToMany(mappedBy = "root", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<JpaAuthenticationSessionEntity> authenticationSessions;

    String getId() {
        return id;
    }

    String getRealmId() {
        return realmId;
    }

    int getTimestamp() {
        return timestamp;
    }

    void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    private JpaRootAuthenticationSessionEntity() {
        authenticationSessions = new ArrayList<>();
    }

    JpaRootAuthenticationSessionEntity(String id, String realmId) {
        this();
        this.id = id;
        this.realmId = realmId;
        this.timestamp = Time.currentTime();
    }

    List<JpaAuthenticationSessionEntity> getAuthenticationSessions() {
        return Collections.unmodifiableList(authenticationSessions);
    }

    JpaAuthenticationSessionEntity createAuthenticationSession(String clientId) {
        JpaAuthenticationSessionEntity authSessionEntity = new JpaAuthenticationSessionEntity(this, clientId);
        this.authenticationSessions.add(authSessionEntity);
        this.timestamp = Time.currentTime();
        return authSessionEntity;
    }

    JpaAuthenticationSessionEntity removeAuthenticationSession(String tabId) {
        JpaAuthenticationSessionEntity jpaAuthenticationSessionEntity = authenticationSessions.stream().filter(
                it -> it.getTabId().equals(tabId)).findFirst().orElse(null);
        if (jpaAuthenticationSessionEntity != null) {
            authenticationSessions.remove(jpaAuthenticationSessionEntity);
        }
        return jpaAuthenticationSessionEntity;
    }

    void clearAuthenticationSessions() {
        authenticationSessions.clear();
        this.timestamp = Time.currentTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaRootAuthenticationSessionEntity that = (JpaRootAuthenticationSessionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
