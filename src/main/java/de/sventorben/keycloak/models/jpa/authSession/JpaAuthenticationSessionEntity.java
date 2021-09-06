package de.sventorben.keycloak.models.jpa.authSession;

import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.Time;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.sessions.CommonClientSessionModel;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "AUTHENTICATION_SESSIONS")
final class JpaAuthenticationSessionEntity {

    @Id
    @Column(nullable = false)
    private String id;

    @ManyToOne(optional = false)
    private JpaRootAuthenticationSessionEntity root;

    @Column(nullable = false)
    private String tabId;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private int timestamp;

    @OneToMany(mappedBy = "session", cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<JpaNoteEntity> notes;

    @Column
    private String authUserId;

    @Column
    @Convert(converter = StringListAttributeConverter.class)
    private Set<String> requiredActions;

    @Column
    @Convert(converter = StringListAttributeConverter.class)
    private Set<String> clientScopes;

    @Column
    private String redirectUri;

    @Column
    private String action;

    @Column
    private String protocol;

    @ElementCollection
    @MapKeyColumn(name = "authenticator")
    @CollectionTable(name = "AUTHENTICATION_SESSION_EXECUTION_STATUS", joinColumns = @JoinColumn(name = "authSessionId"))
    @Column(name = "executionStatus")
    private Map<String, CommonClientSessionModel.ExecutionStatus> executionStatus;

    String getId() {
        return id;
    }

    String getTabId() {
        return tabId;
    }

    String getClientId() {
        return clientId;
    }

    private JpaAuthenticationSessionEntity() {
        this.id = UUID.randomUUID().toString();
        this.tabId = generateTabId();
        this.timestamp = Time.currentTime();
        this.notes = new ArrayList<>();
        this.requiredActions = new HashSet<>();
        this.clientScopes = new HashSet<>();
        this.executionStatus = new HashMap<>();
    }

    JpaAuthenticationSessionEntity(JpaRootAuthenticationSessionEntity root, String clientId) {
        this();
        this.root = root;
        this.clientId = clientId;
    }

    void setRoot(JpaRootAuthenticationSessionEntity root) {
        this.root = root;
    }

    Map<String, CommonClientSessionModel.ExecutionStatus> getExecutionStatus() {
        return new HashMap<>(executionStatus);
    }

    void setExecutionStatus(String authenticator, CommonClientSessionModel.ExecutionStatus status) {
        if (authenticator != null) {
            if (status != null) {
                this.executionStatus.put(authenticator, status);
            } else {
                this.executionStatus.remove(authenticator);
            }
        }
    }

    void clearExecutionStatus() {
        executionStatus.clear();
    }

    String getAuthUserId() {
        return authUserId;
    }

    void setAuthUserId(String authUserId) {
        this.authUserId = authUserId;
    }

    void removeNote(JpaNoteEntity.NoteType type, String name) {
        JpaNoteEntity toBeRemoved = getNoteEntity(type, name);
        notes.remove(toBeRemoved);
    }

    JpaNoteEntity setNote(JpaNoteEntity.NoteType type, String name, String value) {
        JpaNoteEntity entity = getNoteEntity(type, name);
        if (entity == null) {
            entity = new JpaNoteEntity(this, type, name, value);
            notes.add(entity);
        } else {
            entity.setValue(value);
        }
        return entity;
    }

    List<JpaNoteEntity> getNotes(JpaNoteEntity.NoteType type) {
        return notes.stream().filter(it -> it.getType().equals(type)).collect(Collectors.toList());
    }

    void clearNotes(JpaNoteEntity.NoteType type) {
        this.notes.removeIf(it -> it.getType().equals(type));
    }

    String getNote(JpaNoteEntity.NoteType type, String name) {
        JpaNoteEntity entity = getNoteEntity(type, name);
        if (entity == null) return null;
        return entity.getValue();
    }

    private JpaNoteEntity getNoteEntity(JpaNoteEntity.NoteType type, String name) {
        return notes.stream()
                .filter(it -> it.getType().equals(type))
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaAuthenticationSessionEntity that = (JpaAuthenticationSessionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private String generateTabId() {
        return Base64Url.encode(KeycloakModelUtils.generateSecret(8));
    }

    public Set<String> getRequiredActions() {
        return new HashSet<>(requiredActions);
    }

    public void addRequiredAction(String action) {
        requiredActions.add(action);
    }

    void removeRequiredAction(String action) {
        requiredActions.remove(action);
    }

    Set<String> getClientScopes() {
        return new HashSet<>(clientScopes);
    }

    void setClientScopes(Set<String> clientScopes) {
        this.clientScopes = new HashSet<>(clientScopes);
    }

    String getRedirectUri() {
        return redirectUri;
    }

    void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    String getAction() {
        return action;
    }

    void setAction(String action) {
        this.action = action;
    }

    String getProtocol() {
        return protocol;
    }

    void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
