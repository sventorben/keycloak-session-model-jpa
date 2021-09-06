package de.sventorben.keycloak.models.jpa.authSession;

import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static de.sventorben.keycloak.models.jpa.authSession.JpaNoteEntity.NoteType.*;

final class JpaAuthenticationSessionAdapter implements AuthenticationSessionModel {

    private final KeycloakSession session;
    private final JpaRootAuthenticationSessionAdapter parent;
    private final JpaAuthenticationSessionEntity entity;

    public JpaAuthenticationSessionAdapter(KeycloakSession session, JpaRootAuthenticationSessionAdapter parent, JpaAuthenticationSessionEntity entity) {
        this.session = session;
        this.parent = parent;
        this.entity = entity;
    }

    @Override
    public String getTabId() {
        return entity.getTabId();
    }

    @Override
    public RootAuthenticationSessionModel getParentSession() {
        return parent;
    }

    @Override
    public Map<String, ExecutionStatus> getExecutionStatus() {
        return entity.getExecutionStatus();
    }

    @Override
    public void setExecutionStatus(String authenticator, ExecutionStatus status) {
        Objects.requireNonNull(authenticator, "The provided authenticator can't be null!");
        Objects.requireNonNull(status, "The provided execution status can't be null!");
        entity.setExecutionStatus(authenticator, status);
    }

    @Override
    public void clearExecutionStatus() {
        entity.clearExecutionStatus();
    }

    @Override
    public UserModel getAuthenticatedUser() {
        return entity.getAuthUserId() == null ? null : session.users().getUserById(getRealm(), entity.getAuthUserId());
    }

    @Override
    public void setAuthenticatedUser(UserModel user) {
        String userId = (user == null) ? null : user.getId();
        entity.setAuthUserId(userId);
    }

    @Override
    public Set<String> getRequiredActions() {
        return entity.getRequiredActions();
    }

    @Override
    public void addRequiredAction(String action) {
        Objects.requireNonNull(action, "The provided action can't be null!");
        entity.addRequiredAction(action);
    }

    @Override
    public void removeRequiredAction(String action) {
        Objects.requireNonNull(action, "The provided action can't be null!");
        entity.removeRequiredAction(action);
    }

    @Override
    public void addRequiredAction(UserModel.RequiredAction action) {
        Objects.requireNonNull(action, "The provided action can't be null!");
        addRequiredAction(action.name());
    }

    @Override
    public void removeRequiredAction(UserModel.RequiredAction action) {
        Objects.requireNonNull(action, "The provided action can't be null!");
        removeRequiredAction(action.name());
    }

    @Override
    public void setUserSessionNote(String name, String value) {
        if (name != null) {
            if (value == null) {
                entity.removeNote(USER, name);
            } else {
                entity.setNote(USER, name, value);
            }
        }
    }

    @Override
    public Map<String, String> getUserSessionNotes() {
        return entity.getNotes(USER).stream().collect(
                Collectors.toMap(JpaNoteEntity::getName, JpaNoteEntity::getValue));
    }

    @Override
    public void clearUserSessionNotes() {
        entity.clearNotes(USER);
    }

    @Override
    public String getAuthNote(String name) {
        return (name != null) ? entity.getNote(AUTH, name) : null;
    }

    @Override
    public void setAuthNote(String name, String value) {
        if (name != null) {
            if (value == null) {
                removeAuthNote(name);
            } else {
                entity.setNote(AUTH, name, value);
            }
        }
    }

    @Override
    public void removeAuthNote(String name) {
        if (name != null && !name.equalsIgnoreCase("active_code")) {
            entity.removeNote(AUTH, name);
        }
    }

    @Override
    public void clearAuthNotes() {
        this.entity.clearNotes(AUTH);
    }

    @Override
    public String getClientNote(String name) {
        return (name != null) ? entity.getNote(CLIENT, name) : null;
    }

    @Override
    public void setClientNote(String name, String value) {
        if (name != null) {
            if (value == null) {
                removeClientNote(name);
            } else {
                entity.setNote(CLIENT, name, value);
            }
        }
    }

    @Override
    public void removeClientNote(String name) {
        if (name != null) {
            entity.removeNote(CLIENT, name);
        }
    }

    @Override
    public Map<String, String> getClientNotes() {
        return entity.getNotes(CLIENT).stream().collect(
                Collectors.toMap(JpaNoteEntity::getName, JpaNoteEntity::getValue));
    }

    @Override
    public void clearClientNotes() {
        entity.clearNotes(CLIENT);
    }

    @Override
    public Set<String> getClientScopes() {
        return entity.getClientScopes();
    }

    @Override
    public void setClientScopes(Set<String> clientScopes) {
        Objects.requireNonNull(clientScopes, "The provided client scopes set can't be null!");
        entity.setClientScopes(clientScopes);
    }

    @Override
    public String getRedirectUri() {
        return entity.getRedirectUri();
    }

    @Override
    public void setRedirectUri(String uri) {
        entity.setRedirectUri(uri);
    }

    @Override
    public RealmModel getRealm() {
        return parent.getRealm();
    }

    @Override
    public ClientModel getClient() {
        return parent.getRealm().getClientById(entity.getClientId());
    }

    @Override
    public String getAction() {
        return entity.getAction();
    }

    @Override
    public void setAction(String action) {
        entity.setAction(action);
    }

    @Override
    public String getProtocol() {
        return entity.getProtocol();
    }

    @Override
    public void setProtocol(String method) {
        entity.setProtocol(method);
    }

    private EntityManager em() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

}
