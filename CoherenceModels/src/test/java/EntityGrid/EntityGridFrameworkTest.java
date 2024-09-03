package EntityGrid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityGridFrameworkTest {


    @BeforeEach
    public void setUp() {
        System.out.println("Setting up the test...");
        System.out.flush(); // Ensure the output is flushed
        // Additional setup code
    }

    @Test
    public void testSomething() {
        System.out.println("Running the test...");
        // Your test code here
    }
}
//
//    public static final String teststring1 = "I love Berlin. Berlin is a very cosmopolitan city in Germany. The city is just buzzing.";
//    public static final String teststring2 = "The atom is a basic unit of matter, it consists of a dense central nucleus surrounded by a cloud of negatively charged electrons.";
//    public static final String teststringA = "I am going to travel to Berlin.";
//    public static final String teststringB = "Berlin is a very cosmopolitan city in Germany.";
//    public static final String teststringC = "The city is just buzzing.";
//    public static final String message = "Entity incorrectly extracted.";
//    public static final String messageXml = "Wrong number of docs extracted from xml.";
//    public static final String xml = "<refset setid=\"newsdev2009\"><doc docid=\"napi.hu/2007/12/12/0\" genre=\"news\"><hl><seg id=\"1\"> Food: Where European inflation slipped up </seg>"+
//            "</hl><p><seg id=\"2\"> The skyward zoom in food prices is the dominant force behind the speed up in eurozone inflation. </seg></p></doc></refset>";
//    public static final String xml2 ="<refset setid=\"newsdev2009\"><doc docid=\"napi.hu/2007\"><hl><seg id=\"1\"> Government crisis coming, says Gallup </seg></hl><p><seg id=\"2\">"+
//            "Fidesz support shot up significantly in early December after a lengthy period of just holding its own. "+
//            "This gives it the strongest support base it has seen since 2002, while Gallup reports support for the socialists at an all-time low of 13 percent. </seg></p></doc>"+
//            "<doc><p><seg id=\"15\"> In comparison, only 55 percent said it would definitely vote in parliamentary elections if they were to be held this Sunday while another 15 percent said it most likely would cast a ballot. "
//            +"Given current determination to vote, the referendum is certain to be valid. </seg></p></doc></refset>";
//
//    public static final List<String> sentences = new ArrayList<>();
//    static {
//        sentences.add(teststringA);
//        sentences.add(teststringB);
//        sentences.add(teststringC);
//    }
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        System.out.println("Im working")
//        // Initialization before each test
//    }
//
//    @Test
//    public void testIdentifyEntities() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        Map<String, ArrayList<Map<Integer, String>>> entities = gridframework.identifyEntitiesFromSentences(teststring1);
//
//        List<Map<Integer, String>> berlinOccurrences = entities.get("Berlin");
//        assertEquals(2, berlinOccurrences.size(), message);
//        Map<Integer, String> occurrence = berlinOccurrences.get(0);
//        assertEquals("O", occurrence.get(0), message);
//        Map<Integer, String> occurrence1 = berlinOccurrences.get(1);
//        assertEquals("S", occurrence1.get(1), message);
//
//        List<Map<Integer, String>> cityOccurrences = entities.get("city");
//        assertEquals(2, cityOccurrences.size(), message);
//        Map<Integer, String> occurrence2 = cityOccurrences.get(0);
//        assertEquals("X", occurrence2.get(1), message);
//        Map<Integer, String> occurrence3 = cityOccurrences.get(1);
//        assertEquals("S", occurrence3.get(2), message);
//    }
//
//    @Test
//    public void testEntityResolver() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        char[][] grid = gridframework.identifyEntitiesAndConstructGrid(teststring1);
//        System.out.println("O and " + grid[0][1]);
//        assertEquals('O', grid[0][1], message); // sentence 1, Berlin
//        assertEquals('S', grid[1][1], message); // sentence 2, Berlin
//        assertEquals('X', grid[1][0], message); // sentence 2, city
//        assertEquals('X', grid[1][2], message); // sentence 2, Germany
//        assertEquals('S', grid[2][0], message); // sentence 3, city
//    }
//
//    @Test
//    public void testEntityResolver2() {
//        EntityGridFramework gridframework2 = getEntityGridFramework();
//        char[][] grid = gridframework2.identifyEntitiesAndConstructGrid(teststring2);
//
//        int Os = 0;
//        int Ss = 0;
//        int Xs = 0;
//        // Count grammatical occurrences
//        for (int i = 0; i < grid[0].length; i++) {
//            switch (grid[0][i]) {
//                case 'O':
//                    Os++;
//                    break;
//                case 'X':
//                    Xs++;
//                    break;
//                case 'S':
//                    Ss++;
//                    break;
//            }
//        }
//        assertEquals(1, Ss, message); // atom
//        assertEquals(5, Xs, message); // unit
//    }
//
//    @Test
//    public void testXmlExtractDocs1() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        Map<String, String> docs = new CorpusReader().readXMLString(xml);
//        int fileidx = 0;
//        for (String docAsString : docs.values()) {
//            char[][] grid = gridframework.identifyEntitiesAndConstructGrid(docAsString);
//            fileidx++;
//        }
//        assertEquals(1, fileidx, messageXml);
//    }
//
//    @Test
//    public void testXmlExtractDocs2() {
//        EntityGridFramework gridframework = getEntityGridFramework();
//        Map<String, String> docs = new CorpusReader().readXMLString(xml2);
//        int fileidx = 0;
//        for (String docAsString : docs.values()) {
//            char[][] grid = gridframework.identifyEntitiesAndConstructGrid(docAsString);
//            fileidx++;
//        }
//        assertEquals(2, fileidx, messageXml);
//    }
//
//    @Test
//    public void testFrenchXmlExtractDocs1() {
//        Map<String, String> docs = new CorpusReader().readMultilingualXMLString(xmlFrench);
//        EntityGridFramework gridframework = new EntityGridFactory().getEntityGridFramework("French");
//        int fileidx = 0;
//        for (String docAsString : docs.values()) {
//            char[][] grid = gridframework.identifyEntitiesAndConstructGrid(docAsString);
//            fileidx++;
//        }
//        assertEquals(1, fileidx, messageXml);
//    }
//
//    @Test
//    public void testFrenchXmlExtractDocs2() {
//        Map<String, String> docs = new CorpusReader().readMultilingualXMLString(xmlFrench2);
//        EntityGridFramework gridframework = new EntityGridFactory().getEntityGridFramework("French");
//        int fileidx = 0;
//        for (String docAsString : docs.values()) {
//            char[][] grid = gridframework.identifyEntitiesAndConstructGrid(docAsString);
//            fileidx++;
//        }
//        assertEquals(2, fileidx, messageXml);
//    }
//
//    protected EntityGridFramework getEntityGridFramework() {
//        return new EntityGridFramework();
//    }
//
//    @Test
//    public void testExtractingEntityTransitions() {
//        // Method for testing entity transitions (implementation needed)
//    }
//}
