package dev.langchain4j.bedrockexamples;

import java.io.IOException;
import java.nio.file.Paths;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.bedrock.BedrockAnthropicMessageChatModel;
import dev.langchain4j.model.output.Response;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;

public class BedrockAnthropicChatModelExample {
    public static void main(String[] args) throws IOException {
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
        
        // Initialize the Bedrock model with credentials and configured properties
        BedrockAnthropicMessageChatModel bedrockChatModel = BedrockAnthropicMessageChatModel
                .builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.US_EAST_1)
                .temperature(0.50f)
                .maxTokens(300)
                .model(BedrockAnthropicMessageChatModel.Types.AnthropicClaude3HaikuV1.getValue())
                .maxRetries(1)
                .build();

        Response<AiMessage> response = bedrockChatModel.generate(UserMessage.from("hi, how are you doing?"));
        System.out.println(response);
        System.out.println("DONE!");
        System.exit(0);
    }
}
