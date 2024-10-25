/**
 * Creates a graph-based model for assessing local coherence.
 * Represents an implementation of the Guinaudeau and Strube graph experiment, (http://anthology.aclweb.org//P/P13/P13-1010.pdf).
 * This implementation makes use of the groundwork done by EntityGridFramework class, and projects the data onto a graph representation.
 *
 * @author Karin Sim
 */

package entityGraph;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import entityGrid.EntityGridFramework;
import utils.BipartiteGraph;
import utils.CorpusReader;
import utils.FileOutputUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class EntityGraph {

    private final EntityGridFramework entityGrid;

    // constructor
    public EntityGraph(EntityGridFramework entityGridFramework) {
        this.entityGrid = entityGridFramework;
    }

    public static void main(String[] args) {
        String filename = args[0];
        String outputfile = args[1];
        String rawtext = args[2];
        int projection = Integer.parseInt(args[4]);
        String tagger = args[5];

        Map<String, String> docs = new CorpusReader().readXML(filename);
        StringBuffer stringbuffer = new StringBuffer();

        // Initialize the StanfordCoreNLP pipeline with properties
        Properties properties = new Properties();
        properties.put("-parseInside", "HEADLINE|P");
        properties.put("annotators", "tokenize, ssplit, pos, lemma, parse");
        properties.put("parse.originalDependencies", true);
        properties.put("ssplit.eolonly", "false"); // Ensures sentence splitting is based on punctuation
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        if (Boolean.valueOf(rawtext) == Boolean.TRUE) {
            EntityGraph graph = new EntityGraph(new EntityGridFramework(pipeline));
            for (int fileidx = 0; fileidx < docs.size(); fileidx++) {
                BipartiteGraph bipartitegraph = graph.identifyEntitiesAndConstructGraph(docs.get(fileidx));
                bipartitegraph.setDocId(outputfile + fileidx + "_debug_");
                streamCoherenceScore(projection, fileidx, stringbuffer,
                        bipartitegraph.getLocalCoherence(projection), bipartitegraph);
            }
        }
        System.out.println(stringbuffer);
        FileOutputUtils.streamToFile(outputfile, stringbuffer);
    }

    private static void streamCoherenceScore(int projection, int fileidx,
                                             StringBuffer stringbuffer, double coherence,
                                             BipartiteGraph bipartitegraph) {


        stringbuffer.append(fileidx);
        stringbuffer.append("\t");
        stringbuffer.append(coherence + ", ");
        stringbuffer.append("\n");
    }

    /**
     * Calls on the encapsulated entity grid to identify the entities and their occurances, and then
     * constructs the graph with them.
     *
     * @param docAsString
     * @return a  BipartiteGraph
     */
    public BipartiteGraph identifyEntitiesAndConstructGraph(String docAsString) {
        Map<String, ArrayList<Map<Integer, String>>> entities = entityGrid.identifyEntitiesFromSentences(docAsString);
        return constructGraph(entities);
    }

    /**
     * Constructs a graph from the list of noun occurances over all sentences.
     * The graph can be projected in various ways:
     *
     * @param entities
     * @return
     */
    private BipartiteGraph constructGraph(Map<String, ArrayList<Map<Integer, String>>> entities) {

        BipartiteGraph graph = new BipartiteGraph(entities);

        return graph;
    }
}

/**
 * The various options are set on the command line, to ensure the correct parser is set.
 * In the following format and order:
 * inputfile, outputfile, containsMultipleDocs, language, type of projection
 * "C:\\SMT\\datasets\\corpus-PE\\corpus\\PEofMT_Half.fr"
 * "C:\\SMT\\datasets\\corpus -PE\\corpus\\output_graph\\PEofMT_half.fr.graph.weighted"
 * "true" "French" "1"
 *
 * @param input      file that contains the input text
 * @param output     file to log coherence scores per document
 * @param language   language of the input text, either "English", "French", or "German"
 * @param projection type of projection, can be one of:
 *                   <li> UNWEIGHTED_PROJECTION = 0; // Weights are binary, and equal 1 when two sentences share at least one entity
 *                   <li> WEIGHTED_PROJECTION = 1; // Edges are weighted by the number of entities shared by two sentences
 *                   <li> SYNTACTIC_PROJECTION = 2; // Edges are weighted by syntax, with subject having more weight than object,
 *                   which in turn is higher than other grammatical positions
 */
