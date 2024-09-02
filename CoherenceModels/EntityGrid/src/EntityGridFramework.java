package EntityGrid.EntityGrid.src;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
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

public class EntityGridFramework {
    protected static final char EMPTY = '-';
    protected static final char O = 'O';
    protected static final char S = 'S';
    protected static final char X = 'X';

    private static final String NNP = "NNP"; //Proper noun, singular
    private static final String NNS = "NNS"; //Noun, plural
    private static final String NP = "NP";
    private static final String NN = "NN"; //Noun, singular or mass
    private static final String N = "N";
    private static final String NE = "NE";
    private static final String NC = "NC";
    private static final String NPP = "NPP";

    public static final String ENGLISH_PARSER = "edu/standford/nlp/models/parser/nndep/english_SD.gz";
    public static final String ENGLISH_TAGGER = "models/english-left3words-distsim.tagger";

    private static final String ENGLISH = "English";

    protected StanfordCoreNLP pipeline;






}
