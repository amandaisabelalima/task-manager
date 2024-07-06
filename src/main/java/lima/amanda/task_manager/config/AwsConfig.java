package lima.amanda.task_manager.config;

import lima.amanda.task_manager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class AwsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final AwsProperties awsProperties;

    public AwsConfig(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

    @Bean
    public S3Client s3Client() {
        String bucketName = awsProperties.getBucket().getName();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsProperties.getAccessKeyId(), awsProperties.getSecretAccessKey());

        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(awsProperties.getLocalstackUrl()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();

        if (!s3Client.listBuckets().buckets().stream().anyMatch(b -> b.name().equals(bucketName))) {
            s3Client.createBucket(builder -> builder.bucket(bucketName));
            LOGGER.info("M=s3Client, I=Bucket {} criado com sucesso.", bucketName);
        } else {
            LOGGER.info("M=s3Client, I=O bucket {} já existe.", bucketName);
        }

        return s3Client;
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsProperties.getAccessKeyId(), awsProperties.getSecretAccessKey())))
                .endpointOverride(URI.create(awsProperties.getLocalstackUrl())) // URL do LocalStack
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsProperties.getAccessKeyId(), awsProperties.getSecretAccessKey())))
                .endpointOverride(URI.create(awsProperties.getLocalstackUrl())) // URL do LocalStack
                .build();
    }

}
