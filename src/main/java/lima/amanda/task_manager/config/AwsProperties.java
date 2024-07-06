package lima.amanda.task_manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws.s3")
public class AwsProperties {

    private Bucket bucket;
    private String accessKeyId;
    private String secretAccessKey;
    private String localstackUrl;

    @Data
    public static class Bucket {
        private String name;
    }

}
