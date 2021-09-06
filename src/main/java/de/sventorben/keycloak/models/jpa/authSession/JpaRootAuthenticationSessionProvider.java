package de.sventorben.keycloak.models.jpa.authSession;

import org.jboss.logging.Logger;
import org.keycloak.cluster.ClusterProvider;
import org.keycloak.common.util.Time;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.RealmInfoUtil;
import org.keycloak.sessions.AuthenticationSessionCompoundId;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.sessions.RootAuthenticationSessionModel;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static de.sventorben.keycloak.models.jpa.authSession.JpaRootAuthenticationSessionEntity.QUERY_DELETE_BY_REALM_ID;
import static de.sventorben.keycloak.models.jpa.authSession.JpaRootAuthenticationSessionEntity.QUERY_REMOVE_EXPIRED;

final class JpaRootAuthenticationSessionProvider implements AuthenticationSessionProvider {

    private static final Logger LOG = Logger.getLogger(JpaRootAuthenticationSessionProvider.class);
    private static final String AUTHENTICATION_SESSION_EVENTS = "AUTHENTICATION_SESSION_EVENTS";

    private final KeycloakSession keycloakSession;

    public JpaRootAuthenticationSessionProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Override
    public RootAuthenticationSessionModel createRootAuthenticationSession(RealmModel realm) {
        return createRootAuthenticationSession(realm, UUID.randomUUID().toString());
    }

    @Override
    public RootAuthenticationSessionModel createRootAuthenticationSession(RealmModel realm, String id) {
        LOG.debugf("Creating new RootAuthenticationSession for realm %s with id %s.", realm.getId(), id);
        JpaRootAuthenticationSessionEntity entity = new JpaRootAuthenticationSessionEntity(id, realm.getId());
        em().persist(entity);
        return wrap(entity);
    }

    @Override
    public RootAuthenticationSessionModel getRootAuthenticationSession(RealmModel realm, String authenticationSessionId) {
        Objects.requireNonNull(realm, "The provided realm can't be null!");
        JpaRootAuthenticationSessionEntity entity = getEntity(authenticationSessionId);
        if (entity == null || !entity.getRealmId().equals(realm.getId())) {
            LOG.debugf("RootAuthenticationSession for realm %s with id %s not found.", realm.getId(), authenticationSessionId);
            return null;
        }
        return wrap(entity);
    }

    @Override
    public void removeRootAuthenticationSession(RealmModel realm, RootAuthenticationSessionModel authenticationSession) {
        JpaRootAuthenticationSessionEntity entity = getEntity(authenticationSession.getId());
        if (entity != null && entity.getRealmId().equals(realm.getId())) {
            LOG.debugf("Removing RootAuthenticationSession for realm %s with id %s.", realm.getId(), authenticationSession.getId());
            em().remove(entity);
        }
    }

    @Override
    public void removeAllExpired() {
        keycloakSession.realms().getRealmsStream().forEach(this::removeExpired);
    }

    @Override
    public void removeExpired(RealmModel realm) {
        Objects.requireNonNull(realm, "The provided realm can't be null!");

        LOG.debugf("Removing expired RootAuthenticationSessions for realm %s.", realm.getId());

        int expired = Time.currentTime() - RealmInfoUtil.getDettachedClientSessionLifespan(realm);
        em().createNamedQuery(QUERY_REMOVE_EXPIRED)
                .setParameter("realmId", realm.getId())
                .setParameter("expired", expired)
                .executeUpdate();
    }

    @Override
    public void onRealmRemoved(RealmModel realm) {
        Objects.requireNonNull(realm, "The provided realm can't be null!");

        em().createNamedQuery(QUERY_DELETE_BY_REALM_ID)
                .setParameter("realmId", realm.getId())
                .executeUpdate();
    }

    @Override
    public void onClientRemoved(RealmModel realm, ClientModel client) {
        // Don't care for now. Handle at runtime or during expiration
    }

    @Override
    public void updateNonlocalSessionAuthNotes(AuthenticationSessionCompoundId compoundId, Map<String, String> authNotesFragment) {
        if (compoundId == null) {
            return;
        }
        Objects.requireNonNull(authNotesFragment, "The provided authentication's notes map can't be null!");

        ClusterProvider cluster = keycloakSession.getProvider(ClusterProvider.class);
        cluster.notify(
                AUTHENTICATION_SESSION_EVENTS,
                new JpaAuthenticationSessionAuthNoteUpdateEvent(compoundId.getRootSessionId(), compoundId.getTabId(),
                        compoundId.getClientUUID(), authNotesFragment),
                true,
                ClusterProvider.DCNotify.ALL_BUT_LOCAL_DC
        );
    }

    @Override
    public void close() {
    }

    private JpaRootAuthenticationSessionEntity getEntity(String authenticationSessionId) {
        if (authenticationSessionId == null) {
            return null;
        }
        return em().find(JpaRootAuthenticationSessionEntity.class, authenticationSessionId);
    }

    private EntityManager em() {
        return keycloakSession.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    private RootAuthenticationSessionModel wrap(JpaRootAuthenticationSessionEntity entity) {
        return new JpaRootAuthenticationSessionAdapter(keycloakSession, entity);
    }
}
