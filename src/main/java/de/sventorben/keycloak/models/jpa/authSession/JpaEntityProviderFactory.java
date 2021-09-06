package de.sventorben.keycloak.models.jpa.authSession;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;

import java.util.Map;

public final class JpaEntityProviderFactory implements org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory, ServerInfoAwareProviderFactory {

    static final String PROVIDER_ID = "session-model-jpa-entity";

    @Override
    public org.keycloak.connections.jpa.entityprovider.JpaEntityProvider create(KeycloakSession session) {
        return new JpaEntityProvider();
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        String version = getClass().getPackage().getImplementationVersion();
        return Map.of("Version", version);
    }
}
