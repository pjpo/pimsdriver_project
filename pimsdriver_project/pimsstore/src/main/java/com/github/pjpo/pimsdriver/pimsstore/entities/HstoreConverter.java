package com.github.pjpo.pimsdriver.pimsstore.entities;

import java.util.HashMap;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

@Converter
public class HstoreConverter implements AttributeConverter<HashMap<String, String>, String> {

    private static final String K_V_SEPARATOR = "=>";

    @Override
	public String convertToDatabaseColumn(final HashMap<String, String> m) {
        if (m.isEmpty()) {
            return "";
        } else {
        	final StringBuilder sb = new StringBuilder();
        	int n = m.size();
        	for (final String key : m.keySet()) {
        		sb.append("\"").append(key).append("\"").append(K_V_SEPARATOR).
        			append("\"").append(m.get(key)).append("\"");

        		if (n > 1) {
        			sb.append(", ");
        			n--;
        		}
        	}

        	return sb.toString();
        }
	}

	@Override
	public HashMap<String, String> convertToEntityAttribute(final String s) {
        final HashMap<String, String> m = new HashMap<>();
        if (StringUtils.isEmpty(s)) {
            return m;
        } else {
        	final String[] tokens = s.split(", ");
        	for (final String token : tokens) {
        		final String[] kv = token.split(K_V_SEPARATOR);
        		final String k = kv[0].trim().substring(1, kv[0].length() - 1);
        		final String v = kv[1].trim().substring(1, kv[1].length() - 1);
        		m.put(k, v);
        	}
        	return m;
        }
	}

}
