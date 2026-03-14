package com.emsist.designhub.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Requirement sync contract skeleton.
 *
 * Source-of-truth rules:
 * - SOT-1: Git docs are authoritative for requirement text
 * - SOT-2: Graph is a materialized projection of Git doc content
 *
 * Content hash scope: doc-authored fields only (requirement text,
 * acceptance criteria, business rules). Excludes graph-computed fields
 * (status, readiness, completenessScore).
 */
@Service
public class RequirementSyncService {

    /**
     * Compute a content hash from doc-authored fields.
     * Hash is deterministic: same inputs always produce the same hash.
     *
     * @param storyId The story identifier (included in hash for namespacing)
     * @param docAuthoredFields Varargs of doc-authored field values
     * @return "sha256:{hex}" format hash string
     */
    public String computeContentHash(String storyId, String... docAuthoredFields) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(storyId.getBytes(StandardCharsets.UTF_8));
            for (String field : docAuthoredFields) {
                if (field != null) {
                    digest.update(field.getBytes(StandardCharsets.UTF_8));
                }
            }
            byte[] hash = digest.digest();
            return "sha256:" + HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /**
     * Compare two content hashes for drift.
     *
     * @return true if hashes differ (drift detected), false if they match
     */
    public boolean hasDrift(String storedHash, String currentHash) {
        return !storedHash.equals(currentHash);
    }
}
