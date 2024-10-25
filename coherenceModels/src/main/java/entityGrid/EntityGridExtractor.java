//package entityGrid;
//
//import edu.stanford.nlp.ling.TaggedWord;
//import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
//import edu.stanford.nlp.trees.GrammaticalRelation;
//import edu.stanford.nlp.trees.GrammaticalStructure;
//import edu.stanford.nlp.trees.GrammaticalStructureFactory;
//import edu.stanford.nlp.trees.PennTreeReader;
//import edu.stanford.nlp.trees.PennTreebankLanguagePack;
//import edu.stanford.nlp.trees.Tree;
//import edu.stanford.nlp.trees.TypedDependency;
//import utils.CorpusReader;
//import utils.FileOutputUtils;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Extracts Entity Grids, similar to EntityGridFramework, but this class takes as input preconstructed
// * ptb trees instead of raw text input.
// *
// * @author Karin Sim
// */
//public class EntityGridExtractor extends EntityGridFramework {
//
//    static List<String> NOUNS = new ArrayList<String>();
//    /**
//     * csubj,
//     * csubjpass, {xsubj}: controlling subject},
//     * subj,
//     * nsubj (nominal subject),
//     * nsubjpass
//     **/
//    static List<GrammaticalRelation> SUBJECT = new ArrayList<GrammaticalRelation>();
//    /**
//     * pobj (object of a preposition)
//     * also dobj ( direct object)
//     * and iobj ( indirect object )
//     **/
//    static List<GrammaticalRelation> OBJECT = new ArrayList<GrammaticalRelation>();
//
//    private static boolean debug = false;
//
//    static {
//        NOUNS.add("NNP");
//        NOUNS.add("NP");
//        NOUNS.add("NNS");
//        NOUNS.add("NN");
//        NOUNS.add("N");
//        NOUNS.add("NE");
//    }
//
//    static {
//        SUBJECT.add(EnglishGrammaticalRelations.NOMINAL_SUBJECT);
//        SUBJECT.add(EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT);
//        SUBJECT.add(EnglishGrammaticalRelations.CLAUSAL_PASSIVE_SUBJECT);
//        SUBJECT.add(EnglishGrammaticalRelations.CLAUSAL_SUBJECT);
//        SUBJECT.add(EnglishGrammaticalRelations.SUBJECT);
//    }
//
//    static {
//        OBJECT.add(EnglishGrammaticalRelations.OBJECT);
//        OBJECT.add(EnglishGrammaticalRelations.DIRECT_OBJECT);
//        OBJECT.add(EnglishGrammaticalRelations.INDIRECT_OBJECT);
//        OBJECT.add(EnglishGrammaticalRelations.PREPOSITIONAL_OBJECT);
//    }
//
//
//    /**
//     * takes any files in given directory, presuming each contains documents comprised of ptb trees.
//     * These will be new line separated, with docid immediately preceding each one, and starting with a '#'
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//        String directory = args[0];
//        if (args.length > 2) {
//            debug = Boolean.parseBoolean(args[2]);
//        }
//
//        EntityGridExtractor gridExtractor = new EntityGridExtractor();
//        System.out.println("Extracting files from directory " + directory);
//        try {
//            gridExtractor.convertPtbsToGrids(directory);
//        } catch (FileNotFoundException e) {
//            System.out.println("No files in directory:" + directory);
//            e.printStackTrace();
//        }
//    }
//
//    private void convertPtbsToGrids(String directory) throws FileNotFoundException {
//
//        PennTreebankLanguagePack tlp = new PennTreebankLanguagePack();
//        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
//
//        File[] files = new File(directory).listFiles();
//        if (files == null) {throw new FileNotFoundException();}
//        for (File file : files) {
//            if (file.isFile()) {
//                System.out.println("file = " + directory + File.separator + file.getName());
//
//                //get all docs from that file:
//                Map<String, List<Tree>> docs = new CorpusReader().readPtbDataAsDocs(directory + File.separator + file.getName());
//
//                for (String docid : docs.keySet()) {
//                    Map<String, ArrayList<Map<Integer, String>>> entities = new HashMap<String, ArrayList<Map<Integer, String>>>();
//                    System.out.println("doc=" + docid);
//
//                    //read in ptb trees for each sub tree in each doc,
//                    List<Tree> treesInDoc = docs.get(docid);
//                    int idx = 0;
//                    for (Tree tree : treesInDoc) {
//
//                        getDependenciesForNouns(tree, entities, idx, gsf);
//                        idx++;
//                    }
//                    //construct grid
//                    FileOutputUtils.writeGridToFile(FileOutputUtils.getDirectory(directory, "output", "grid"),
//                            FileOutputUtils.getFilenameWithoutExtensions(file.getName()) + "_grids",
//                            constructGrid(entities, treesInDoc.size()), true, docid, FileOutputUtils.isCompressed(file.getName()));
//                }
//            }
//        }
//    }
//
//
//    public char[][] convertPtbsStringToGrids(String ptbtree) {
//        PennTreebankLanguagePack tlp = new PennTreebankLanguagePack();
//        tlp.setGenerateOriginalDependencies(true);
//        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
//
//        Map<String, ArrayList<Map<Integer, String>>> entities = new HashMap<String, ArrayList<Map<Integer, String>>>();
//        PennTreeReader treeReader = new PennTreeReader(new StringReader(ptbtree));
//        try {
//            Tree tree = treeReader.readTree();
//            getDependenciesForNouns(tree, entities, 0, gsf);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(1);
//            return null;
//        } finally {
//            try {
//                treeReader.close();
//            } catch (Exception e) { /* log it ?*/ }
//        }
//
//        return constructGrid(entities, 1);
//    }
//
//
//    private void getDependenciesForNouns(Tree tree, Map<String, ArrayList<Map<Integer, String>>> entities, int idx, GrammaticalStructureFactory gsf) {
//
//        //only get dependencies for the nouns..
//        List<TaggedWord> words = tree.taggedYield();
//        for (TaggedWord word : words) {
//            if (NOUNS.contains(word.tag())) {
//
//                //EnglishGrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
//                GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
//
//                Collection<TypedDependency> tdl = gs.typedDependencies();
//
//                for (TypedDependency dependency : tdl) {
//
//                    //find the dependency for the noun in question
//                    //if(word.value().equals(dependency.dep().nodeString())){
//                    if (word.value().equals(dependency.dep().word())) {
//
//                        if (SUBJECT.contains(dependency.reln())) {
//
//                            if (debug) {
//                                System.out.println("tracking " + word.value() + " at " + idx + " as S");
//                            }
//                            trackEntity(word.value(), idx, S, entities);
//                        } else if (OBJECT.contains(dependency.reln())) {
//
//                            if (debug) {
//                                System.out.println("tracking " + word.value() + " at " + idx + " as O");
//                            }
//                            trackEntity(word.value(), idx, O, entities);
//                        } else {
//                            if (debug) {
//                                System.out.println("tracking " + word.value() + " at " + idx + " as X");
//                            }
//                            trackEntity(word.value(), idx, X, entities);
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
//}

package entityGrid;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import utils.CorpusReader;
import utils.FileOutputUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Extracts Entity Grids, similar to EntityGridFramework, but this class takes as input preconstructed
 * ptb trees instead of raw text input.
 *
 * @author Karin Sim
 */
public class EntityGridExtractor extends EntityGridFramework {

    static List<String> NOUNS = new ArrayList<>();
    static List<GrammaticalRelation> SUBJECT = new ArrayList<>();
    static List<GrammaticalRelation> OBJECT = new ArrayList<>();
    private static boolean debug = false;

    static {
        NOUNS.add("NNP");
        NOUNS.add("NP");
        NOUNS.add("NNS");
        NOUNS.add("NN");
        NOUNS.add("N");
        NOUNS.add("NE");
    }

    static {
        SUBJECT.add(EnglishGrammaticalRelations.NOMINAL_SUBJECT);
        SUBJECT.add(EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT);
        SUBJECT.add(EnglishGrammaticalRelations.CLAUSAL_PASSIVE_SUBJECT);
        SUBJECT.add(EnglishGrammaticalRelations.CLAUSAL_SUBJECT);
        SUBJECT.add(EnglishGrammaticalRelations.SUBJECT);
    }

    static {
        OBJECT.add(EnglishGrammaticalRelations.OBJECT);
        OBJECT.add(EnglishGrammaticalRelations.DIRECT_OBJECT);
        OBJECT.add(EnglishGrammaticalRelations.INDIRECT_OBJECT);
        OBJECT.add(EnglishGrammaticalRelations.PREPOSITIONAL_OBJECT);
    }

    // Constructor to pass an existing pipeline
    public EntityGridExtractor(StanfordCoreNLP pipeline) {
        super(pipeline); // Pass the pipeline to the superclass (EntityGridFramework)
    }

    /**
     * Takes any files in the given directory, presuming each contains documents comprised of ptb trees.
     * These will be newline separated, with docid immediately preceding each one, and starting with a '#'.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        String directory = args[0];
        if (args.length > 2) {
            debug = Boolean.parseBoolean(args[2]);
        }

        // Initialize the StanfordCoreNLP pipeline
        Properties properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, pos, lemma, parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);

        EntityGridExtractor gridExtractor = new EntityGridExtractor(pipeline);
        System.out.println("Extracting files from directory " + directory);
        try {
            gridExtractor.convertPtbsToGrids(directory);
        } catch (FileNotFoundException e) {
            System.out.println("No files in directory: " + directory);
            e.printStackTrace();
        }
    }

    private void convertPtbsToGrids(String directory) throws FileNotFoundException {

        PennTreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

        File[] files = new File(directory).listFiles();
        if (files == null) {
            throw new FileNotFoundException();
        }
        for (File file : files) {
            if (file.isFile()) {
                System.out.println("file = " + directory + File.separator + file.getName());

                // Get all docs from that file
                Map<String, List<Tree>> docs = new CorpusReader().readPtbDataAsDocs(directory + File.separator + file.getName());

                for (String docid : docs.keySet()) {
                    Map<String, ArrayList<Map<Integer, String>>> entities = new HashMap<>();
                    System.out.println("doc=" + docid);

                    // Read in ptb trees for each sub-tree in each doc
                    List<Tree> treesInDoc = docs.get(docid);
                    int idx = 0;
                    for (Tree tree : treesInDoc) {
                        getDependenciesForNouns(tree, entities, idx, gsf);
                        idx++;
                    }
                    // Construct grid
                    FileOutputUtils.writeGridToFile(FileOutputUtils.getDirectory(directory, "output", "grid"),
                            FileOutputUtils.getFilenameWithoutExtensions(file.getName()) + "_grids",
                            constructGrid(entities, treesInDoc.size()), true, docid, FileOutputUtils.isCompressed(file.getName()));
                }
            }
        }
    }

    public char[][] convertPtbsStringToGrids(String ptbtree) {
        PennTreebankLanguagePack tlp = new PennTreebankLanguagePack();
        tlp.setGenerateOriginalDependencies(true);
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

        Map<String, ArrayList<Map<Integer, String>>> entities = new HashMap<>();
        PennTreeReader treeReader = new PennTreeReader(new StringReader(ptbtree));
        try {
            Tree tree = treeReader.readTree();
            getDependenciesForNouns(tree, entities, 0, gsf);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        } finally {
            try {
                treeReader.close();
            } catch (Exception e) { /* log it ?*/ }
        }

        return constructGrid(entities, 1);
    }

    private void getDependenciesForNouns(Tree tree, Map<String, ArrayList<Map<Integer, String>>> entities, int idx, GrammaticalStructureFactory gsf) {

        // Only get dependencies for the nouns
        List<TaggedWord> words = tree.taggedYield();
        for (TaggedWord word : words) {
            if (NOUNS.contains(word.tag())) {

                GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);

                Collection<TypedDependency> tdl = gs.typedDependencies();

                for (TypedDependency dependency : tdl) {

                    // Find the dependency for the noun in question
                    if (word.value().equals(dependency.dep().word())) {

                        if (SUBJECT.contains(dependency.reln())) {

                            if (debug) {
                                System.out.println("tracking " + word.value() + " at " + idx + " as S");
                            }
                            trackEntity(word.value(), idx, S, entities);
                        } else if (OBJECT.contains(dependency.reln())) {

                            if (debug) {
                                System.out.println("tracking " + word.value() + " at " + idx + " as O");
                            }
                            trackEntity(word.value(), idx, O, entities);
                        } else {
                            if (debug) {
                                System.out.println("tracking " + word.value() + " at " + idx + " as X");
                            }
                            trackEntity(word.value(), idx, X, entities);
                        }
                        break;
                    }
                }
            }
        }
    }
}
