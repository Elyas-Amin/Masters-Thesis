import edu.stanford.nlp.util.CoreMap;
import entityGrid.EntityGridFramework;
import utils.BipartiteGraph;
import utils.CorpusReader;
import utils.FileOutputUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates Entity Grid and optionally also Entity Graph.
 * Outputs grids and scores to file.
 *
 * @author Karin Sim
 */
public class EntityExperiments {

    /**
     * Creates Entity Grid and Entity Graph.
     * Outputs grids and scores to file.
     *
     * @param args: <li>directory(of input file)</li>
     *              <li>language (English/French/German) </li>
     *              <li>isXML (true or false; either <doc> separated or # id=fileid</li>
     *              <li>gridAndGraph (true if both, false if just grid)</li>
     *              <li>projection (NB optional- only required if above is true)</li>
     */
    public static void main(String[] args) {


        String directory = args[0];
        String language = args[1];
        boolean isXML = Boolean.parseBoolean(args[2]);
        boolean gridAndGraph = Boolean.parseBoolean(args[3]);


        EntityExperiments experiments = new EntityExperiments();
        System.out.println("dir = " + directory);
        File[] files = new File(directory).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                System.out.println("file = " + directory + File.separator + file.getName());

                if (gridAndGraph) {
                    int projection = Integer.parseInt(args[4]);
                    experiments.getGridAndGraph(directory, file.getName(), language, projection, isXML);
                } else {
                    experiments.getGrid(directory, file.getName(), language, isXML);
                }
            }
        }
    }

    private static void streamCoherenceScore(int projection, String fileidx,
                                             StringBuffer stringbuffer, double coherence,
                                             BipartiteGraph bipartitegraph) {

        stringbuffer.append(fileidx);
        stringbuffer.append("\t");
        stringbuffer.append(coherence);
        stringbuffer.append("\n");
    }

    private void getGrid(String path, String filename, String language, boolean isXML) {
        Map<String, String> docs;
        if (isXML) {
            Map<String, List<String>> docList = new CorpusReader().readXMLwithDocIds(path + File.separator + filename);
            docs = fitMap(new HashMap<String, String>(), docList);
        } else {
            docs = new CorpusReader().readDataAsDocs(path + File.separator + filename);
        }
        EntityGridFramework framework = new EntityGridFramework();
        for (String docid : docs.keySet()) {

            List<CoreMap> sentences = framework.getAnnotatedDocument(docs.get(docid));
            Map<String, ArrayList<Map<Integer, String>>> entities = framework.identifyEntities(sentences);

            FileOutputUtils.writeGridToFile(FileOutputUtils.getDirectory(path, "output", "grid"),
                    FileOutputUtils.getFilenameWithoutExtensions(filename) + "_grids",
                    framework.constructGrid(entities, sentences.size()), true, docid,
                    FileOutputUtils.isCompressed(filename));
        }
    }

    private void getGridAndGraph(String path, String filename, String language, int projection, boolean isXML) {

        //Map<String, List<String>> docs;
        Map<String, String> docs;
        if (isXML) {
            Map<String, List<String>> docList = new CorpusReader().readXMLwithDocIds(path + File.separator + filename);
            docs = fitMap(new HashMap<String, String>(), docList);
        } else {
            docs = new CorpusReader().readDataAsDocs(path + File.separator + filename);
        }

        StringBuffer stringbuffer = new StringBuffer();

        EntityGridFramework framework = new EntityGridFramework();

        String graphdirectory = path + File.separator +
                "output" +
                File.separator +
                "graph" +
                File.separator +
                filename;

        //for(int fileidx = 0; fileidx< docs.size(); fileidx++){
        int fileidx = 0;
        for (String docid : docs.keySet()) {

            List<CoreMap> sentences = framework.getAnnotatedDocument(docs.get(docid));
            Map<String, ArrayList<Map<Integer, String>>> entities = framework.identifyEntities(sentences);

            BipartiteGraph bipartitegraph = new BipartiteGraph(entities);
            ////bipartitegraph.setDocId(filename+fileidx+"_debug_");
            //docs.get(fileidx),
            //streamCoherenceScore(projection, docid, stringbuffer,
            streamCoherenceScore(projection, String.valueOf(fileidx), stringbuffer,
                    bipartitegraph.getLocalCoherence(projection), bipartitegraph);
            System.out.println("entities" + entities.size());

            FileOutputUtils.writeGridToFile(FileOutputUtils.getDirectory(path, "output", "grid"),
                    //FileOutputUtils.getFilenameWithoutExtensions(filename),
                    filename,
                    framework.constructGrid(entities, sentences.size()), true, docid,
                    FileOutputUtils.isCompressed(filename));
            fileidx++;
        }

        FileOutputUtils.streamToFile(graphdirectory, stringbuffer);
    }

    private Map<String, String> fitMap(Map<String, String> docs,
                                       Map<String, List<String>> docList) {
        //TODO:backwards compatiblility: sort it
        for (String docid : docList.keySet()) {
            docs = new HashMap<String, String>();
            docs.put(docid, String.join(";", docList.get(docid)));
        }
        return docs;
    }

    private String getPath(String filename, String outputdirectory, int fileidx) {
        String path = outputdirectory +
                File.separator +
                "output" +
                File.separator +
                "grid" +
                File.separator +
                filename + "_grid_" + fileidx;
        return path;
    }

    private StringBuffer getPath(String filename, String outputdirectory) {
        StringBuffer path = new StringBuffer();
        path.append(outputdirectory);
        path.append(File.separator);
        path.append("output");
        path.append(File.separator);
        path.append("grid");
        path.append(File.separator);
        //path.append(getFilenameWithoutExtensions(filename)+"_grids");
        return path;
    }
}