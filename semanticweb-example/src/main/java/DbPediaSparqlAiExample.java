import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class DbPediaSparqlAiExample {

    private static final Logger logger = Logger.getLogger(DbPediaSparqlAiExample.class.getName());

    // Create the SPARQL query to get information about Napoleon
    private static String createSparqlQuery() {
        // Complete the PREFIX part of the query
        String queryString = ""
            + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
            + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "SELECT ?property ?hasValue ?isValueOf\n"
            + "WHERE {\n"
            + "  { dbr:Napoleon ?property ?hasValue }\n"
            + "  UNION\n"
            + "  { ?isValueOf ?property dbr:Napoleon }\n"
            + "} LIMIT 100";
        return queryString;
    }

    // Execute the SPARQL query
    private static String executeSparqlQuery(String queryString) {
        Query query = QueryFactory.create(queryString);
        String stringResult="";
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
            	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // Output the result
                ResultSetFormatter.outputAsJSON(outputStream, results);
				try {
					stringResult = outputStream.toString(StandardCharsets.UTF_8.name());
					logger.info("stringResult ="+stringResult);
				} catch (UnsupportedEncodingException e) {
					stringResult = "I can't find any results";
				}
            } else {
                // Log if no results are found
                logger.info("No results found.");
            }
        } catch (Exception e) {
			stringResult = "Error executing query to dbpedia: {" + e.getMessage()+"}";
            logger.severe("Error executing query: " + e.getMessage());
        }
        return stringResult;
    }
    interface SparqlBuilder {
        @SystemMessage("You are a semantic web dbpedia developper for SPARQL query'")
        @UserMessage("writes a sparql query that performs the following request on DbPedia: {{text}}"
        		+ "Follow the rules below:"
        		+ "- Defines prefixes to query header ;"
        		+ "- just return the query and do not comment on the result.")
        String build(@V("text") String text);
    }
    interface Assistant {
        String chat(String userMessage);
    }
    static class QueryExecutor {

		@Tool("Retreive information from Dbpedia")
    	String question(String interrogation) {
            AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                    .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                    .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
                    .temperature(0.2)
                    .logRequestsAndResponses(true)
                    .build();
        	SparqlBuilder queryBuilder = AiServices.create(SparqlBuilder.class, model);
        	String aSparqlQuery = queryBuilder.build(interrogation);
            logger.info("- QUERY - "+aSparqlQuery+ "\n---------");
        	String results = DbPediaSparqlAiExample.executeSparqlQuery(aSparqlQuery);
            return results;
    	}
    }
    public static void main(String[] args) {
//        AzureOpenAiChatModel chatLanguageModel = AzureOpenAiChatModel.builder()
//                .apiKey(System.getenv("AZURE_OPENAI_KEY"))
//                .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
//                .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
//                .temperature(0.2)
//                .logRequestsAndResponses(true)
//                .build();
//        Assistant assistant = AiServices.builder(Assistant.class)
//                .chatLanguageModel(chatLanguageModel)
//                .tools(new QueryExecutor())
//                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .build();
////    	SparqlBuilder queryBuilder = AiServices.create(SparqlBuilder.class, chatLanguageModel);
////    	String aSparqlQuery = queryBuilder.build("list all Napoleon's resources associated to his children");
////    	System.out.println(aSparqlQuery); 
////        executeSparqlQuery(aSparqlQuery);
//        String question = "According to dbpedia, when was Napoleon's born?";
//        String answer = chatLanguageModel.generate(question);
//        System.out.println(answer);
        
        AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
                .apiKey(System.getenv("AZURE_OPENAI_KEY"))
                .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT_NAME"))
                .temperature(0.3)
                .logRequestsAndResponses(true)
                .build();

        String response = model.generate("When was Napoleon's born?");

        System.out.println(response);
    }
}
