package entityGrid;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import utils.CorpusReader;

public class EntityGridFrameworkTest {
    public static final String teststring1 = "I love Berlin. Berlin is a very cosmopolitan city in Germany. The city is just buzzing.";
    public static final String teststring2 = "The atom is a basic unit of matter, it consists of a dense central nucleus surrounded by a cloud of negatively charged electrons.";
    public static final String teststringA = "I am going to travel to Berlin.";
    public static final String teststringB = "Berlin is a very cosmopolitan city in Germany.";
    public static final String teststringC = "The city is just buzzing.";
    public static final String message = "Entity incorrectly extracted.";
    public static final String messageXml = "Wrong number of docs extracted from xml.";
    public static final String xml = "<refset setid=\"newsdev2009\"><doc docid=\"napi.hu/2007/12/12/0\" genre=\"news\"><hl><seg id=\"1\"> Food: Where European inflation slipped up </seg>" +
            "</hl><p><seg id=\"2\"> The skyward zoom in food prices is the dominant force behind the speed up in eurozone inflation. </seg></p></doc></refset>";
    public static final String xml2 = "<refset setid=\"newsdev2009\"><doc docid=\"napi.hu/2007\"><hl><seg id=\"1\"> Government crisis coming, says Gallup </seg></hl><p><seg id=\"2\">" +
            "Fidesz support shot up significantly in early December after a lengthy period of just holding its own. " +
            "This gives it the strongest support base it has seen since 2002, while Gallup reports support for the socialists at an all-time low of 13 percent. </seg></p></doc>" +
            "<doc><p><seg id=\"15\"> In comparison, only 55 percent said it would definitely vote in parliamentary elections if they were to be held this Sunday while another 15 percent said it most likely would cast a ballot. "
            + "Given current determination to vote, the referendum is certain to be valid. </seg></p></doc></refset>";

    public static final List<String> sentences = new ArrayList<String>();
    private EntityGridFramework gridFramework;
    protected StanfordCoreNLP pipeline;

    static {
        sentences.add(teststringA);
        sentences.add(teststringB);
        sentences.add(teststringC);
    }

    @BeforeEach
    public void setUp() {
        // Initialize the StanfordCoreNLP pipeline with properties
        Properties properties = new Properties();
        properties.put("-parseInside", "HEADLINE|P");
        properties.put("annotators", "tokenize, ssplit, pos, lemma, parse");
        properties.put("parse.originalDependencies", true);
        properties.put("ssplit.eolonly", "false"); // Ensures sentence splitting is based punctuation
        pipeline = new StanfordCoreNLP(properties);
        gridFramework = new EntityGridFramework();
        gridFramework.pipeline = this.pipeline;
    }

    @Test
    public void testGetAnnotatedDocument() {
        // Given
        Annotation document = new Annotation(teststring1); // Create an Annotation object from the input string
        gridFramework.pipeline.annotate(document); // Annotate the document using the initialized pipeline

        // When
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        // Then
        assertThat(sentences).isNotNull();
        assertThat(sentences.size()).isEqualTo(3);
        assertThat(sentences.get(0).get(CoreAnnotations.TextAnnotation.class)).isEqualTo("I love Berlin.");
        assertThat(sentences.get(1).get(CoreAnnotations.TextAnnotation.class)).isEqualTo("Berlin is a very cosmopolitan city in Germany.");
        assertThat(sentences.get(2).get(CoreAnnotations.TextAnnotation.class)).isEqualTo("The city is just buzzing.");
    }

    @Test
    public void testIdentifyEntitiesBerlin() {

        Map<String, ArrayList<Map<Integer, String>>> entities = gridFramework.identifyEntitiesFromSentences(teststring1);

        List<Map<Integer, String>> berlinOccurrences = entities.get("Berlin");
        assertThat(berlinOccurrences).isNotNull();
        System.out.println("berlin occurrences" + berlinOccurrences);
        assertThat(berlinOccurrences.size()).isEqualTo(2);


        Map<Integer, String> occurrence = berlinOccurrences.get(0);
        assertThat(occurrence.get(0)).withFailMessage(message).isEqualTo("O");

        Map<Integer, String> occurrence1 = berlinOccurrences.get(1);
        assertThat(occurrence1.get(1)).withFailMessage(message).isEqualTo("S");
    }

    @Test
    public void testIdentifyEntitiesCity() {
        Map<String, ArrayList<Map<Integer, String>>> entities = gridFramework.identifyEntitiesFromSentences(teststring1);

        System.out.println(entities);
        List<Map<Integer, String>> cityOccurrences = entities.get("city");
        System.out.println("city occurrences" + cityOccurrences.toString());
        assertThat(cityOccurrences.size()).isEqualTo(2);

        Map<Integer, String> occurrence = cityOccurrences.get(0);
        assertThat(occurrence.get(1)).withFailMessage(message).isEqualTo("X");

        Map<Integer, String> occurrence1 = cityOccurrences.get(1);
        assertThat(occurrence1.get(2)).withFailMessage(message).isEqualTo("S");
    }

    @Test
    public void testEntityResolver() {
        char[][] grid = gridFramework.identifyEntitiesAndConstructGrid(teststring1);
        assertThat(grid[0][1]).withFailMessage(message).isEqualTo('O'); //sentence 1, Berlin
        assertThat(grid[1][1]).withFailMessage(message).isEqualTo('S'); //sentence 2, Berlin
        assertThat(grid[1][0]).withFailMessage(message).isEqualTo('X'); //sentence 2, city
        assertThat(grid[1][2]).withFailMessage(message).isEqualTo('X'); //sentence 2, Germany
        assertThat(grid[2][0]).withFailMessage(message).isEqualTo('S'); //sentence 3, city
    }

    @Test
    public void testEntityResolver2() {
        char[][] grid = gridFramework.identifyEntitiesAndConstructGrid(teststring2);
        System.out.println(Arrays.deepToString(grid));

        int os = 0;
        int ss = 0;
        int xs = 0;
        //since order is non-deterministic, need to simply count grammatical occurrences:
        for (int i = 0; i < grid[0].length; i++) {
            switch (grid[0][i]) {
                case 'O':
                    os++;
                    break;
                case 'X':
                    xs++;
                    break;
                case 'S':
                    ss++;
                    break;
            }
        }
        System.out.println("counts: " + ss + " " + xs + " " + os);
        assertThat(ss).withFailMessage(message).isEqualTo(1); // atom
        assertThat(xs).withFailMessage(message).isEqualTo(5); // unit, matter, nucleus, cloud, electrons
    }

    /**
     * check that a document is correctly extracted from an xml segment
     */
    @Test
    public void testXmlExtractDocs1() {
        Map<String, String> docs = new CorpusReader().readXMLString(xml);
        int fileidx = 0;
        for (String docAsString : docs.values()) {

            char[][] grid = gridFramework.identifyEntitiesAndConstructGrid(docAsString);
            //FileOutputUtils.writeGridToFile(outputfile+fileidx, grid);
            fileidx++;
        }
        assertThat(fileidx).withFailMessage(message).isEqualTo(1);
    }

    /**
     * check that a document is correctly extracted from an xml segment
     */
    @Test
    public void testXmlExtractDocs2() {
        Map<String, String> docs = new CorpusReader().readXMLString(xml2);
        int fileidx = 0;
        for (String docAsString : docs.values()) {

            char[][] grid = gridFramework.identifyEntitiesAndConstructGrid(docAsString);
            fileidx++;
        }
        assertThat(fileidx).withFailMessage(message).isEqualTo(2);
    }


    /**
     * Test that entity transitions are correctly extracted, ie that vertical sequences are
     * extracted from the grid
     */
//    @Test
//    public void testExtractingEntityTransitions() {
//        char[][] grid = gridFramework.identifyEntitiesAndConstructGrid(teststring2);
//
//        // Ensure grid is not null before processing
//        if (grid == null || grid.length == 0) {
//            System.out.println("Grid is empty or null");
//            return;
//        }
//
//        print2DArray(grid);
//
//        for (int col = 0; col < grid[0].length; col++) {
//            System.out.println("Processing column " + col);
//
//            // Loop through each row in the column
//            for (int row = 0; row < grid.length - 1; row++) {
//                char currentEntity = grid[row][col];
//                char nextEntity = grid[row + 1][col];
//
//                System.out.println("Entity at row " + row + ", column " + col + " is: " + currentEntity);
//                System.out.println("Entity at row " + (row + 1) + ", column " + col + " is: " + nextEntity);
//
//                // Test that the current entity is different from the next entity (if you expect transitions)
//                assertThat(currentEntity).isNotEqualTo(nextEntity);
//
//                // Test if we expect a subject (S) to transition to an object (O) within the same column
//                if (currentEntity == 'S') {
//                    assertThat(nextEntity).isEqualTo('O');
//                }
//
//                // Test if entity transition stays consistent (no change) for non-subject, non-object (X)
//                if (currentEntity == 'X') {
//                    assertThat(nextEntity).isEqualTo('X');
//                }
//            }
//        }
//    }

    private void print2DArray(char[][] array) {
        for (char[] chars : array) {
            for (char aChar : chars) {
                System.out.print(aChar + " ");
            }
            System.out.println();
        }
    }
}