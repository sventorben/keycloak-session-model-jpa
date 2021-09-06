package de.sventorben.keycloak.models.jpa.authSession;

import org.keycloak.cluster.ClusterEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class JpaAuthenticationSessionAuthNoteUpdateEvent implements ClusterEvent {

    private String authSessionId;
    private String tabId;
    private String clientUUID;
    private Map<String, String> authNotesFragment;

    JpaAuthenticationSessionAuthNoteUpdateEvent(String authSessionId, String tabId, String clientUUID,
                                                Map<String, String> authNotesFragment) {
        this.authSessionId = authSessionId;
        this.tabId = tabId;
        this.clientUUID = clientUUID;
        this.authNotesFragment = new LinkedHashMap<>(authNotesFragment);
    }

    public String getAuthSessionId() {
        return authSessionId;
    }

    public String getTabId() {
        return tabId;
    }

    public String getClientUUID() {
        return clientUUID;
    }

    public Map<String, String> getAuthNotesFragment() {
        return authNotesFragment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JpaAuthenticationSessionAuthNoteUpdateEvent that = (JpaAuthenticationSessionAuthNoteUpdateEvent) o;
        return Objects.equals(authSessionId, that.authSessionId) && Objects.equals(tabId, that.tabId) && Objects.equals(
                clientUUID, that.clientUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authSessionId, tabId, clientUUID);
    }

    @Override
    public String toString() {
        return String.format(
                "AuthenticationSessionAuthNoteUpdateEvent [ authSessionId=%s, tabId=%s, clientUUID=%s, authNotesFragment=%s ]",
                authSessionId, tabId, clientUUID, authNotesFragment);
    }
}
