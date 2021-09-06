package de.sventorben.keycloak.models.jpa.authSession;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.component.AmphibianProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;
import org.keycloak.sessions.AuthenticationSessionProvider;
import org.keycloak.sessions.AuthenticationSessionProviderFactory;

import java.util.Map;

public final class JpaRootAuthenticationSessionProviderFactory implements AuthenticationSessionProviderFactory, ServerInfoAwareProviderFactory, AmphibianProviderFactory<AuthenticationSessionProvider> {

    private static final Logger LOG = Logger.getLogger(JpaRootAuthenticationSessionProviderFactory.class);

    private static final String PROVIDER_ID = "jpa-auth-session";

    @Override
    public String getHelpText() {
        return "Authentication session provider for storing auth sessions into Keycloak database via JPA";
    }

    @Override
    public AuthenticationSessionProvider create(KeycloakSession keycloakSession) {
        return new JpaRootAuthenticationSessionProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
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
