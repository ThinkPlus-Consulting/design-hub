package com.emsist.designhub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FrontmatterParser {

    private static final String DELIMITER = "---";
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public Optional<Frontmatter> parse(String markdown) {
        String yaml = extractYamlBlock(markdown);
        if (yaml == null) return Optional.empty();
        try {
            return Optional.of(yamlMapper.readValue(yaml, Frontmatter.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public String extractBody(String markdown) {
        int firstDelim = markdown.indexOf(DELIMITER);
        if (firstDelim < 0) return markdown;
        int secondDelim = markdown.indexOf(DELIMITER, firstDelim + DELIMITER.length());
        if (secondDelim < 0) return markdown;
        return markdown.substring(secondDelim + DELIMITER.length()).strip();
    }

    private String extractYamlBlock(String markdown) {
        String trimmed = markdown.strip();
        if (!trimmed.startsWith(DELIMITER)) return null;
        int end = trimmed.indexOf(DELIMITER, DELIMITER.length());
        if (end < 0) return null;
        return trimmed.substring(DELIMITER.length(), end).strip();
    }
}
