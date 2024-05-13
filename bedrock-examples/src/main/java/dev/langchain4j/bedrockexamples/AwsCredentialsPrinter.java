package dev.langchain4j.bedrockexamples;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;

import java.nio.file.Paths;

public class AwsCredentialsPrinter {

    public static void main(String[] args) {
        // Path to the credentials file
        String credentialsFilePath = "src/main/resources/aws/credentials";

        // Create a ProfileFile pointing to the credentials file
        ProfileFile profileFile = ProfileFile.builder()
                .content(Paths.get(credentialsFilePath))
                .type(ProfileFile.Type.CREDENTIALS)
                .build();

        // Create a ProfileCredentialsProvider using the ProfileFile
        AwsCredentialsProvider credentialsProvider = ProfileCredentialsProvider.builder()
                .profileFile(profileFile)
                .profileName("default") // Specify the profile name if not default
                .build();

        // Retrieve the credentials
        AwsCredentials credentials = credentialsProvider.resolveCredentials();

        // Print the credentials
        System.out.println("Access Key ID: " + credentials.accessKeyId());
        System.out.println("Secret Access Key: " + credentials.secretAccessKey());
    }
}
