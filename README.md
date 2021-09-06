# Keycloak: JPA Session Model

This library allows to store Keycloak's authentication session to Keycloak's database instead of Infinispan cache.

> ⚠️ **WARNING**:
>
> This is a PoC and WIP and in no way production ready.


## How to install?

Download a release (*.jar file) that works with your Keycloak version from the [list of releases](https://github.com/sventorben/keycloak-session-model-jpa/releases).

Drop the file to `standalone/deployments` folder to make use of Keycloak Deployer. For details please refer to the [official documentation](https://www.keycloak.org/docs/latest/server_development/#registering-provider-implementations).

For Docker-based setups follow the [guidelines for adding custom providers](https://github.com/keycloak/keycloak-containers/tree/master/server#user-content-adding-a-custom-provider).

> ℹ️ **Maven/Gradle**:
>
> Packages are being released to GitHub Packages. You find the coordinates [here](https://github.com/sventorben?tab=packages&repo_name=keycloak-session-model-jpa)! It may happen that I remove older packages without prior notice, because the storage is limited on the free tier.


## How to configure?

### via CLI:

Check  [this script](src/main/startup-scripts/authenticationSessionsProvider.cli) for an example.

### via standalone.xml:

```XML
<spi name="authenticationSessions" default-provider="jpa-auth-session">
    <provider name="pa-auth-session" enabled="true">
    </provider>
</spi>
```

For details, please refer to [Manage Subsystem Configuration](https://www.keycloak.org/docs/latest/server_installation/index.html#manage-subsystem-configuration) section in the server installation guide.

## Does it work with Keycloak version X.Y.Z?

If you are using Keycloak version `X` (e.g. `X.y.z`), version `X.b.c` should be compatible.
Keycloak SPIs are quite stable. So, there is a high chance this authenticator will work with other versions, too. Simply give it a try.

Authenticator version `X.b.c` is compiled against Keycloak version `X.y.z`. For example, version `12.3.1` will be compiled against Keycloak version `12.y.z`.

I do not guarantee what version `a.b` or `y.z` will be. Neither do I backport features to older version, nor maintain any older versions of this authenticator. If you need the latest features or bugfixes for an older version, please fork this project or update your Keycloak instance. I recommend doing the latter on regular basis anyways.
