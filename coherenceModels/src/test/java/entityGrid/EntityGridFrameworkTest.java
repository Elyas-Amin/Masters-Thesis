package entityGrid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class EntityGridFrameworkTest {

    //private EntityGridFramework gridframework;
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
    private static final Logger logger = Logger.getLogger(EntityGridFrameworkTest.class.getName());

    static {
        sentences.add(teststringA);
        sentences.add(teststringB);
        sentences.add(teststringC);
    }

    @BeforeEach
    public void setup() {
//        gridFramework = new EntityGridFramework();
        logger.info("test");
    }

    @Test
    public void testIdentifyEntities() {

        logger.info("test");
//        Map<String, ArrayList<Map<Integer, String>>> entities = gridFramework.identifyEntitiesFromSentences(teststring1);
//
//        List<Map<Integer, String>> berlinOccurences = entities.get("Berlin");
//        assertEquals(2, berlinOccurences.size());
//
//        // This will force the message to be part of the test result if it fails
//        assertTrue(berlinOccurences != null, "Berlin occurrences not found!");
//
//        Map<Integer, String> occurance = berlinOccurences.get(0);
//        assertEquals(message, "O", occurance.get(0));
//        System.out.println("First occurrence: " + occurance);  // Should print if visible
//
//        Map<Integer, String> occurance1 = berlinOccurences.get(1);
//        assertEquals(message, "S", occurance1.get(1));
//
//        // Similar for other entities
//        List<Map<Integer, String>> cityOccurences = entities.get("city");
//        assertEquals(2, cityOccurences.size());
    }


//    @Test
//    public void testIdentifyEntities() {
//        Map<String, ArrayList<Map<Integer, String>>> entities = gridFramework.identifyEntitiesFromSentences(teststring1);
//
//        List<Map<Integer, String>> berlinOccurences = entities.get("Berlin");
//        assertEquals(2, berlinOccurences.size());
//        logger.info("count is true");
//        Map<Integer, String> occurance = berlinOccurences.get(0);
//        logger.info(occurance.toString());
//        assertEquals(message, "O", occurance.get(0));
//        Map<Integer, String> occurance1 = berlinOccurences.get(1);
//        assertEquals(message, "S", occurance1.get(1));
//
//        List<Map<Integer, String>> cityOccurences = entities.get("city");
//        assertEquals(2, cityOccurences.size());
//        Map<Integer, String> occurance2 = cityOccurences.get(0);
//        assertEquals(message, "X", occurance2.get(1));
//        Map<Integer, String> occurance3 = cityOccurences.get(1);
//        assertEquals(message, "S", occurance3.get(2));
//    }
}
//
//
//    protected EntityGridFramework getEntityGridFramework() {
//
//        return new EntityGridFramework();
//    }
//
//    public void testEntityResolver() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        char[][] grid = gridframework.identifyEntitiesAndConstructGrid(teststring1);
//        System.out.println("O and " + grid[0][1]);
//        assertEquals(message, 'O', grid[0][1]);//sentence 1, Berlin
//        assertEquals(message, 'S', grid[1][1]);//sentence 2, Berlin
//        assertEquals(message, 'X', grid[1][0]);//sentence 2, city
//        assertEquals(message, 'X', grid[1][2]);//sentence 2, Germany
//        assertEquals(message, 'S', grid[2][0]);//sentence 3, city
//
//    }
//
//
//    /**
//     * Read in source text and invoke coreference resolve to identify entities.
//     * Test that entities are resolved
//     */
//    @SuppressWarnings("CheckStyle")
//    public void testEntityResolver2() {
//
//        EntityGridFramework gridframework2 = getEntityGridFramework();
//        char[][] grid = gridframework2.identifyEntitiesAndConstructGrid(teststring2);
//
//        int os = 0;
//        int ss = 0;
//        int xs = 0;
//        //since order is non-deterministic, need to simply count grammatical occurances:
//        for (int i = 0; i < grid[0].length; i++) {
//            switch (grid[0][i]) {
//                case 'O':
//                    os++;
//                    break;
//                case 'X':
//                    xs++;
//                    break;
//                case 'S':
//                    ss++;
//                    break;
//            }
//        }
//        assertEquals(message, 1, ss);//atom
//        assertEquals(message, 5, xs);//unit
//        //assertEquals(message, 4, os);//matter,nucleaus,cloud,electrons
//        //since 3.5.2 new Stanford dependencies..
//
//    }
//
//    /**
//     * check that a document is correctly extracted from an xml segment
//     */
//    public void testXmlExtractDocs1() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        Map<String, String> docs = new CorpusReader().readXMLString(xml);
//        int fileidx = 0;
//        for (String docAsString : docs.values()) {
//
//            char[][] grid = gridframework.identifyEntitiesAndConstructGrid(docAsString);
//            //FileOutputUtils.writeGridToFile(outputfile+fileidx, grid);
//            fileidx++;
//        }
//        assertEquals(messageXml, 1, fileidx);
//    }
//
//    /**
//     * check that a document is correctly extracted from an xml segment
//     */
//    public void testXmlExtractDocs2() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        Map<String, String> docs = new CorpusReader().readXMLString(xml2);
//        int fileidx = 0;
//        for (String docAsString : docs.values()) {
//
//            char[][] grid = gridframework.identifyEntitiesAndConstructGrid(docAsString);
//            //FileOutputUtils.writeGridToFile(outputfile+fileidx, grid);
//            fileidx++;
//        }
//        assertEquals(messageXml, 2, fileidx);
//    }
//
//    /**
//     * Test that entity transitions are correctly extracted, ie that vertical sequences are
//     * extracted from the grid
//     */
//    public void testExtractingEntityTransitions() {
//        //char grid [][] = gridframework.identifyEntities();
//
//        //for(int col = 0; col< grid[row].length.[col]; col++) {//each column
//        //	for(int row = 0; j< grid[i].length; row++) {//each char representing entity
//
//    }
//
//}