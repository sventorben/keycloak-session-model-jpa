package de.sventorben.keycloak.models.jpa.authSession;

import org.keycloak.common.util.Time;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class JpaRootAuthenticationSessionAdapter implements RootAuthenticationSessionModel {

    private final KeycloakSession session;
    private final JpaRootAuthenticationSessionEntity entity;

    public JpaRootAuthenticationSessionAdapter(KeycloakSession session, JpaRootAuthenticationSessionEntity entity) {
        this.session = session;
        this.entity = entity;
    }

    @Override
    public String getId() {
        return entity.getId();
    }

    @Override
    public RealmModel getRealm() {
        return session.realms().getRealm(entity.getRealmId());
    }

    @Override
    public int getTimestamp() {
        return entity.getTimestamp();
    }

    @Override
    public void setTimestamp(int timestamp) {
        entity.setTimestamp(timestamp);
    }

    @Override
    public Map<String, AuthenticationSessionModel> getAuthenticationSessions() {
        return entity.getAuthenticationSessions()
                .stream()
                .collect(Collectors.toMap(JpaAuthenticationSessionEntity::getTabId,
                        it -> new JpaAuthenticationSessionAdapter(session, this, it)));
    }

    @Override
    public AuthenticationSessionModel getAuthenticationSession(ClientModel client, String tabId) {
        if (client == null || tabId == null) {
            return null;
        }

        AuthenticationSessionModel authSession = getAuthenticationSessions().get(tabId);
        if (authSession != null && client.equals(authSession.getClient())) {
            session.getContext().setAuthenticationSession(authSession);
            return authSession;
        } else {
            return null;
        }
    }

    @Override
    public AuthenticationSessionModel createAuthenticationSession(ClientModel client) {
        Objects.requireNonNull(client, "The provided client can't be null!");
        JpaAuthenticationSessionEntity authSessionEntity = entity.createAuthenticationSession(client.getId());
        JpaAuthenticationSessionAdapter authSession = new JpaAuthenticationSessionAdapter(session, this, authSessionEntity);
        session.getContext().setAuthenticationSession(authSession);
        return authSession;
    }

    @Override
    public void removeAuthenticationSessionByTabId(String tabId) {
        if (entity.removeAuthenticationSession(tabId) != null) {
            if (entity.getAuthenticationSessions().isEmpty()) {
                session.authenticationSessions().removeRootAuthenticationSession(getRealm(), this);
            } else {
                entity.setTimestamp(Time.currentTime());
            }
        }
    }

    @Override
    public void restartSession(RealmModel realm) {
        entity.clearAuthenticationSessions();
    }

}
