package de.sventorben.keycloak.models.jpa.authSession;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Converter
public final class StringListAttributeConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> strings) {
        if (strings == null || strings.isEmpty()) return null;
        return String.join("|", strings);
    }

    @Override
    public Set<String> convertToEntityAttribute(String joined) {
        if (joined == null || joined.isBlank()) return Collections.emptySet();
        HashSet<String> set = new HashSet<>();
        Collections.addAll(set, joined.split("\\|"));
        return set;
    }

}