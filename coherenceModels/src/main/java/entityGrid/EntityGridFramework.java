package entityGrid;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;
import utils.CorpusReader;
import utils.FileOutputUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Constructs an entity grid from a given file in English.
 * The entity grid uses the Stanford Parser to identify all nouns in the input text.
 * The Stanford Parser additionally determines the grammatical role played by that entity in each particular occurrence.
 * The various options are set on the commandline to ensure the correct parser is set.
 *
 * @author Karin Sim
 * adapted by: Elyas Amin
 */

public class EntityGridFramework {

    protected static final char O = 'O';
    protected static final char S = 'S';
    protected static final char X = 'X';
    private static final String NNP = "NNP";
    private static final String NNS = "NNS";
    private static final String NP = "NP";
    private static final String NN = "NN";
    private static final String N = "N";
    private static final String NE = "NE";
    private static final String NC = "NC";
    private static final String NPP = "NPP";
    protected static StringBuffer buffer = new StringBuffer();
    protected StanfordCoreNLP pipeline;
    private char[][] grid;

    // Constructor to pass an existing pipeline
    // (allows you to use the same pipeline everytime while consecutively extracting grids)
    public EntityGridFramework(StanfordCoreNLP pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Read in source text and invoke coreference resolver to identify entities.
     */
    public Map<String, ArrayList<Map<Integer, String>>> identifyEntitiesFromSentences(String docAsString) {
        return identifyEntities(getAnnotatedDocument(docAsString));
    }

    /**
     * Read in source text and invoke coreference resolve to identify entities and construct grid.
     */
    public char[][] identifyEntitiesAndConstructGrid(String docAsString) {
        List<CoreMap> sentences = getAnnotatedDocument(docAsString);
        Map<String, ArrayList<Map<Integer, String>>> entities = identifyEntities(sentences);

        return constructGrid(entities, sentences.size());
    }

    public List<CoreMap> getAnnotatedDocument(String docAsString) {
        Annotation document = new Annotation(docAsString);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        return sentences;
    }

    /**
     * @param sentences list of sentences
     * @return entities
     */
    public Map<String, ArrayList<Map<Integer, String>>> identifyEntities(List<CoreMap> sentences) {

        Map<String, ArrayList<Map<Integer, String>>> entities = new HashMap<>();
        int idx = 0;  // Sentence index

        for (CoreMap sentence : sentences) { // loop through each sentence
//            System.out.println("Processing sentence " + idx + ": " + sentence);
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) { // loop through each token in the sentence
                String pos = token.get(PartOfSpeechAnnotation.class); // annotate token
//                System.out.println("POS: " + pos + " for " + token.lemma());
                if (isNoun(pos)) { //check to see if POS is a noun for that token
                    SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
//                    System.out.println(dependencies);
                    char isSubjOrObj = 'X';

                    if (dependencies != null) {
                        for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
//                            System.out.println("Relation: " + edge.getRelation() + " between " + edge.getSource().word() + " and " + edge.getTarget().word());
//                            System.out.print(token.get(CoreAnnotations.ValueAnnotation.class));
//                            System.out.print(" ");
//                            System.out.print(edge.getTarget().get(CoreAnnotations.ValueAnnotation.class));
//                            System.out.print(" ");
//                            System.out.println(token.get(CoreAnnotations.ValueAnnotation.class).equals(edge.getTarget().get(CoreAnnotations.ValueAnnotation.class)));
                            if (token.get(CoreAnnotations.ValueAnnotation.class).equals(edge.getTarget().get(CoreAnnotations.ValueAnnotation.class))) {
                                GrammaticalRelation relation = edge.getRelation();
                                isSubjOrObj = determineGrammaticalRelation(entities, idx, token, relation);
                            }
                        }
                    }
                    // Track the entity with the correct idx for the sentence
                    trackEntity(token.lemma(), idx, isSubjOrObj, entities);
//                    System.out.println("Tracking entity: " + token.lemma() + " at index " + idx + " with role " + isSubjOrObj);
//                    System.out.println("Entity: " + token.lemma() + " at index " + idx + " tracked with role " + isSubjOrObj);
                }
            }

            // Increment idx after each sentence is processed
            idx++;
        }

        return entities;
    }

    private boolean isNoun(String posTag) {
        if (posTag.equalsIgnoreCase(NNP) || posTag.equalsIgnoreCase(NP) || posTag.equalsIgnoreCase(NNS)
                || posTag.equalsIgnoreCase(NN) || posTag.equalsIgnoreCase(N) || posTag.equalsIgnoreCase(NE)
                || posTag.equalsIgnoreCase(NC) || posTag.equalsIgnoreCase(NPP)) {
            return true;
        } else {
            return posTag.equalsIgnoreCase("ADJ");
        }
    }

    private char determineGrammaticalRelation(
            Map<String, ArrayList<Map<Integer, String>>> entities, int idx,
            CoreLabel token, GrammaticalRelation relation) {

        char isSubjOrObj = 'X';
//        System.out.println("In determineGrammaticalRelation:");
//        System.out.println("relation: " + relation);
        // Check for object relations
        if (relation.getShortName().equals(EnglishGrammaticalRelations.PREPOSITIONAL_OBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.OBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.INDIRECT_OBJECT.getShortName())
        ) {
//            System.out.println("is object: ");
            isSubjOrObj = 'O';
            trackEntity(token.lemma(), idx, O, entities);  // Track as object (O)
        }
        // Check for subject relations
        else if (relation.getShortName().equals(EnglishGrammaticalRelations.SUBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.CLAUSAL_SUBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.CLAUSAL_PASSIVE_SUBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.NOMINAL_SUBJECT.getShortName())
                || relation.getShortName().equals(EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT.getShortName())) {
//            System.out.println("is subject: ");
            isSubjOrObj = 'S';
            trackEntity(token.lemma(), idx, S, entities);  // Track as a subject (S)
        }
        // Debugging output to verify the relations found
//        System.out.println("found: " + token.lemma() + " as " + relation.getShortName() + " returning " + String.valueOf(isSubjOrObj));
        return isSubjOrObj;
    }


    /**
     * @param entities        is Map for tracking occurances in format : "word"->list of : sentence_number->grammatical_role
     * @param lemma           is the entity
     * @param idx             is the index of the sentence currently being examined
     * @param grammaticalRole is the grammatical role played by this entity in this particular instance
     */
    protected void trackEntity(String lemma, int idx, char grammaticalRole, Map<String, ArrayList<Map<Integer, String>>> entities) {
        // Get the current list of occurrences for the entity (lemma)
//        System.out.println("lemma: " + lemma);
        ArrayList<Map<Integer, String>> entityOccurrences = entities.get(lemma);
//        System.out.println("entityOccurrences: " + entityOccurrences);

        if (entityOccurrences == null) {
            // If no occurrences found, create a new list for the entity
            entityOccurrences = new ArrayList<>();
            entities.put(lemma, entityOccurrences);
        }

        // Flag to indicate if the entity has been found in the current sentence
        boolean foundInCurrentSentence = false;

        // Iterate through the list of occurrences and check if it is already in this sentence
        for (Map<Integer, String> occurrence : entityOccurrences) {
            if (occurrence.containsKey(idx)) {
                foundInCurrentSentence = true;
                String currentRole = occurrence.get(idx);

                // Only update grammatical role if it's truly necessary:
                // 1. If the current role is 'X', we can upgrade to 'O' or 'S'
                // 2. If the current role is 'O', we do NOT upgrade to 'S' in the same sentence
                if (currentRole.equals(String.valueOf('X')) || (currentRole.equals(String.valueOf('O')) && grammaticalRole != 'S')) {
                    occurrence.put(idx, String.valueOf(grammaticalRole));  // Update role to the higher priority one
//                    System.out.println("Updating role for " + lemma + " to " + grammaticalRole + " in sentence " + idx);
                } else if (currentRole.equals(String.valueOf('O')) && grammaticalRole == 'S') {
//                    System.out.println("Not updating role for " + lemma + " because it is already marked as an object (O)");
                } else {
//                    System.out.println("No update needed for " + lemma + " with role " + currentRole);
                }
                break; // Stop further checks as we found the sentence
            }
        }

        // If the entity was not found in the current sentence, add a new occurrence
        if (!foundInCurrentSentence) {
            Map<Integer, String> newOccurrence = new HashMap<>();
            newOccurrence.put(idx, String.valueOf(grammaticalRole));
            entityOccurrences.add(newOccurrence);
//            System.out.println("Added new occurrence for " + lemma + " at index " + idx + " with role " + grammaticalRole);
        }
    }

    /**
     * Constructs a grid from the list of noun occurances over all sentences.
     * The grid is a 2D array where entities are tracked on the vertical, and each sentence is a horizontal.
     *
     * @param entities
     * @return
     */
    public char[][] constructGrid(Map<String, ArrayList<Map<Integer, String>>> entities, int numberOfSentences) {
        this.grid = new char[numberOfSentences][entities.size()];
        System.out.println("ENTITIES: " + entities);
        int entityIndex = 0;
        for (String entity : entities.keySet()) {
            List<Map<Integer, String>> occurrences = entities.get(entity);
            String lexicalInfo = "\n entity: " + entity + " at " + occurrences;
            //FileOutputUtils.writeDebugToFile(debugFile, lexicalInfo);
            buffer.append(lexicalInfo);

            //only track salient entities: UPDATE: moved this to Python script, as then can derive from existing grids
            //if(occurrences.size() >= SALIENCE){
            for (Map<Integer, String> occurance : occurrences) {
                Integer sentence = occurance.keySet().iterator().next();
                grid[sentence][entityIndex] = occurance.get(sentence).charAt(0);
            }
            entityIndex++;
            //}
        }
        return grid;
    }

}

/* GRAVEYARD

    public EntityGridFramework(String urlForPOStagger, String urlForParseModel) {

        Properties properties = new Properties();
        properties.put("-parseInside", "HEADLINE|P");
        properties.put("annotators", "tokenize, ssplit, pos, lemma, parse");
        // properties.setProperty("tokenize.whitespace", "true");//for annotator tokenize
        //properties.setProperty("ssplit.eolonly", "true");//for annotator ssplit
        //properties.put("parse.model", "edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");
        properties.put("parse.model", urlForParseModel);
        properties.put("pos.model", urlForPOStagger);
        properties.put("ssplit.eolonly", "true");//for annotator ssplit
        this.pipeline = new StanfordCoreNLP(properties);
    }

        public static void main(String[] args) {

        // Initialize the StanfordCoreNLP pipeline with properties
        Properties properties = new Properties();
        properties.put("-parseInside", "HEADLINE|P");
        properties.put("annotators", "tokenize, ssplit, pos, lemma, parse");
        properties.put("parse.originalDependencies", true);
        properties.put("ssplit.eolonly", "false"); // Ensures sentence splitting is based on punctuation
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        // Pass the initialized pipeline to the EntityGridFramework
        EntityGridFramework gridFramework = new EntityGridFramework(pipeline);

        char[][] grid = gridFramework.identifyEntitiesAndConstructGrid(doc);
        FileOutputUtils.writeGridToFile(outputFile, grid);
    }
 */