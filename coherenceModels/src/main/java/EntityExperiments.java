import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.json.JSONObject;
import entityGrid.EntityGridFramework;
import utils.BipartiteGraph;
import utils.FileOutputUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EntityExperiments {

    private final StanfordCoreNLP pipeline;

    public EntityExperiments() {
        // Initialize Stanford CoreNLP pipeline with necessary annotators
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse");
        pipeline = new StanfordCoreNLP(props);
    }

    public static void main(String[] args) throws Exception {
        // Programmatically define parameters here instead of relying on command-line args
        String jsonlFilePath = "/Users/Ghamay/Documents/mastersThesis/Data/GCDC/Data/JSONLFiles/Clinton_dev.jsonl";
        boolean gridAndGraph = false;     // Only grid (set to true if both grid and graph are needed)
        int projection = 0;               // Projection value (only needed if gridAndGraph is true)

        EntityExperiments experiments = new EntityExperiments();

        // Read the JSONL file and process each line
        List<String> lines = Files.readAllLines(Paths.get(jsonlFilePath));

        for (String line : lines) {
            JSONObject json = new JSONObject(line);
            String textId = json.getString("text_id");
            String text = json.getString("text");

            // Annotate the text and get the CoreMap sentences
            List<CoreMap> annotatedSentences = experiments.annotateText(text);

            // Proceed with the experiments based on the annotated sentences
//            if (gridAndGraph) {
//                experiments.getGridAndGraph(jsonlFilePath, textId, projection, annotatedSentences);
//            } else {
            experiments.getGrid(jsonlFilePath, textId, annotatedSentences);
//            }
        }
    }

    /**
     * Annotate the given text using Stanford CoreNLP and return a list of CoreMap (annotated sentences).
     */
    private List<CoreMap> annotateText(String text) {
        // Annotate the text using the pipeline
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Extract sentences as CoreMap objects
        return document.get(CoreAnnotations.SentencesAnnotation.class);
    }

    private void getGrid(String path, String textId, List<CoreMap> sentences) {
        EntityGridFramework framework = new EntityGridFramework();
        Map<String, ArrayList<Map<Integer, String>>> entities = framework.identifyEntities(sentences);

        // Get the parent directory of the input file, removing the file name from the path
        File inputFile = new File(path);
        String parentDir = inputFile.getParent();  // This gets the parent directory, not including the file name

        // Now use this parent directory to create output directories
        File outputDirectory = new File(parentDir + "/output/grid");

        // Check if directory exists, if not, create it
        if (!outputDirectory.exists()) {
            boolean dirCreated = outputDirectory.mkdirs();
            if (!dirCreated) {
                System.out.println("Failed to create directory: " + outputDirectory.getAbsolutePath());
                return; // Exit if directory cannot be created
            }
        }

        FileOutputUtils.writeGridToFile(
                outputDirectory.getAbsolutePath(),
                textId + "_grids",
                framework.constructGrid(entities, sentences.size()), true, textId,
                false
        );
    }

//    private void getGridAndGraph(String path, String textId, int projection, List<CoreMap> sentences) {
//        EntityGridFramework framework = new EntityGridFramework();
//        Map<String, ArrayList<Map<Integer, String>>> entities = framework.identifyEntities(sentences);
//
//        BipartiteGraph bipartiteGraph = new BipartiteGraph(entities);
//
//        // Calculate coherence score
//        double coherence = bipartiteGraph.getLocalCoherence(projection);
//        System.out.println("Coherence score for " + textId + ": " + coherence);
//
//        // Write the grid to file
//        FileOutputUtils.writeGridToFile(
//                FileOutputUtils.getDirectory(path, "output", "grid"),
//                textId + "_grids",
//                framework.constructGrid(entities, sentences.size()), true, textId,
//                false
//        );
//
//        // Write the graph to file
//        StringBuffer graphBuffer = new StringBuffer();
//        streamCoherenceScore(projection, textId, graphBuffer, coherence, bipartiteGraph);
//        FileOutputUtils.streamToFile(path + File.separator + "output" + File.separator + "graph" + File.separator + textId + "_graph", graphBuffer);
//    }

//    private static void streamCoherenceScore(int projection, String textId,
//                                             StringBuffer stringBuffer, double coherence,
//                                             BipartiteGraph bipartiteGraph) {
//        stringBuffer.append(textId).append("\t").append(coherence).append("\n");
//    }
}
