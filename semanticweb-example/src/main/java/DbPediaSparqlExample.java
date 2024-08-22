import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import java.util.logging.Logger;

public class DbPediaSparqlExample {

    private static final Logger logger = Logger.getLogger(DbPediaSparqlExample.class.getName());

    // Create the SPARQL query to get information about Napoleon
    private static String createSparqlQuery() {
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
    private static void executeSparqlQuery(String queryString) {
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                // Output the result
                ResultSetFormatter.out(System.out, results, query);
            } else {
                // Log if no results are found
                logger.info("No results found.");
            }
        } catch (Exception e) {
            // Log any exceptions
            logger.severe("Error executing query: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String sparqlQuery = createSparqlQuery();
        executeSparqlQuery(sparqlQuery);
    }
}
