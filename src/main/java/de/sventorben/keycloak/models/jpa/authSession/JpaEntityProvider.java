package de.sventorben.keycloak.models.jpa.authSession;

import java.util.List;

public final class JpaEntityProvider implements org.keycloak.connections.jpa.entityprovider.JpaEntityProvider {

    @Override
    public List<Class<?>> getEntities() {
        return List.of(
                JpaRootAuthenticationSessionEntity.class,
                JpaAuthenticationSessionEntity.class,
                JpaNoteEntity.class
        );
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/changelog-master.xml";
    }

    @Override
    public String getFactoryId() {
        return JpaEntityProviderFactory.PROVIDER_ID;
    }

    @Override
    public void close() {
    }
}
