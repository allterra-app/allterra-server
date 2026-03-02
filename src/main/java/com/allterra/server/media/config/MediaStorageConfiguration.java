package com.allterra.server.media.config;

import com.allterra.server.media.service.LocalMediaStorage;
import com.allterra.server.media.service.MediaStorage;
import com.allterra.server.media.service.S3MediaStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

/**
 * Configures media storage implementation by environment.
 */
@Configuration
@SuppressWarnings("checkstyle:DesignForExtension")
public class MediaStorageConfiguration {
    private static final String STORAGE_TYPE_LOCAL = "local";
    private static final String STORAGE_TYPE_S3 = "s3";

    /**
     * Resolves and creates storage implementation by configured environment.
     *
     * @param storageType storage type (`local` or `s3`)
     * @param localStoragePath local storage path
     * @param s3Bucket s3 bucket name
     * @param s3Region s3 region name
     * @param s3Endpoint optional s3 endpoint override
     * @param s3KeyPrefix object key prefix
     * @param s3PathStyleAccess whether path-style URLs are enabled
     * @return configured media storage implementation
     */
    @Bean
    public MediaStorage mediaStorage(
            @Value("${allterra.media.storage-type:local}") final String storageType,
            @Value("${allterra.media.local-storage-path:./uploads}") final String localStoragePath,
            @Value("${allterra.media.s3.bucket:}") final String s3Bucket,
            @Value("${allterra.media.s3.region:}") final String s3Region,
            @Value("${allterra.media.s3.endpoint:}") final String s3Endpoint,
            @Value("${allterra.media.s3.key-prefix:media}") final String s3KeyPrefix,
            @Value("${allterra.media.s3.path-style-access:false}") final boolean s3PathStyleAccess
    ) {
        final var normalizedType = storageType == null ? STORAGE_TYPE_LOCAL : storageType.trim().toLowerCase();
        if (STORAGE_TYPE_LOCAL.equals(normalizedType)) {
            return new LocalMediaStorage(localStoragePath);
        }

        if (!STORAGE_TYPE_S3.equals(normalizedType)) {
            throw new IllegalStateException("Unsupported media storage type: " + storageType);
        }

        if (s3Bucket == null || s3Bucket.isBlank()) {
            throw new IllegalStateException("allterra.media.s3.bucket is required for S3 media storage");
        }
        if (s3Region == null || s3Region.isBlank()) {
            throw new IllegalStateException("allterra.media.s3.region is required for S3 media storage");
        }

        final var s3ClientBuilder = S3Client.builder().region(Region.of(s3Region.trim()));
        if (s3Endpoint != null && !s3Endpoint.isBlank()) {
            s3ClientBuilder.endpointOverride(URI.create(s3Endpoint.trim()));
        }
        if (s3PathStyleAccess) {
            s3ClientBuilder.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }

        return new S3MediaStorage(s3ClientBuilder.build(), s3Bucket.trim(), s3KeyPrefix);
    }
}
