package lima.amanda.task_manager.service;

import lima.amanda.task_manager.config.AwsProperties;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
public class S3Service {

    private final S3AsyncClient s3AsyncClient;

    private final S3Presigner s3Presigner;

    private final AwsProperties awsProperties;

    public S3Service(S3AsyncClient s3AsyncClient, S3Presigner s3Presigner, AwsProperties awsProperties) {
        this.s3AsyncClient = s3AsyncClient;
        this.s3Presigner = s3Presigner;
        this.awsProperties = awsProperties;
    }

    public Flux<String> uploadFileToS3(Flux<FilePart> filePartFlux) {
        return filePartFlux.flatMap(filePart -> {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    return filePart.content().flatMap(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        try {
                            outputStream.write(bytes);
                        } catch (IOException e) {
                            return Mono.error(e);
                        }
                        return Mono.empty();
                    }).then(Mono.defer(() -> {
                        String uniqueIdentifier = UUID.randomUUID().toString();
                        String uniqueFileName = "uploads/" + uniqueIdentifier + "_" + filePart.filename();

                        byte[] bytes = outputStream.toByteArray();
                        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                .bucket(awsProperties.getBucket().getName())
                                .key(uniqueFileName)
                                .build();

                        AsyncRequestBody requestBody = AsyncRequestBody.fromBytes(bytes);
                        CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(putObjectRequest, requestBody);

                        return Mono.fromFuture(future).thenReturn(uniqueFileName);
                    }));
                });
    }


    public Mono<String> generateUrlToFileDownload(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.getBucket().getName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        return Mono.fromCallable(() -> s3Presigner.presignGetObject(presignRequest))
                .map(presignedUrl -> presignedUrl.url().toString());
    }


























//    public Flux<String> uploadFileToS3(Flux<FilePart> filePartMono) {
//        // ORIGINAL QUE SALVA SÓ O NOME DO ARQUIVO
//        return filePartMono.flatMap(filePart -> {
//            String filename = filePart.filename();
//            String key = "uploads/" + filename; // Define a chave no S3
//
//            // Faz o upload do arquivo para o S3
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket("task-manager-bucket") // Nome do bucket no Localstack
//                    .key(key)
//                    .build();
//
//            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromByteBuffer(byteBuffer));
//
//            return Flux.just(key);
//        });
//    }





    //COLOCAR TRY CATCH
//        return file.flatMap(filePart -> {
//            String filename = filePart.filename();
//            String key = "uploads/" + filename; // Define a chave no S3
//
//            // Faz o upload do arquivo para o S3
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket("task-manager-bucket") // Nome do bucket no Localstack
//                    .key(key)
//                    .build();
//
//            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromByteBuffer(byteBuffer));
//
//            return Mono.just(key);
//        });

}
