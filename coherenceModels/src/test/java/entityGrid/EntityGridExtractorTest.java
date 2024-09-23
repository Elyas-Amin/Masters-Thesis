package entityGrid;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EntityGridExtractorTest {

    public static final String message = "Problem creating grid from ptb";
    public static final String ptb1 = "(ROOT (S (S (NP (DT The) (NNP Pact)) (VP (VBZ is) (ADVP (RB already)) (VP (VBG knocking) (NP (NNP Germany)) (PP (IN into) (NP (NN recession)))))) (, ,) (CC and) (S (NP (NP (NNP Italy) (POS 's)) (NN government)) (VP (VBZ is) (VP (VBG struggling) (S (VP (TO to) (VP (VB revise) (NP (PRP$ its) (NN growth) (NNS forecasts)) (ADVP (RB fast) (RB enough) (S (VP (TO to) (VP (VB keep) (PRT (RP up)) (PP (IN with) (NP (VBG falling) (NN output))))))))))))) (. .)))";
    public static final String ptb2 = "(ROOT (S (SBAR (IN As) (S (NP (NNP Herbert) (NNP Hoover)) (VP (MD could) (VP (VB attest))))) (, ,) (SBAR (WHADVP (WRB when)) (S (NP (PRP we)) (VP (VBP see) (NP (NP (RB only) (DT the) (JJ economic) (NN policy) (NNS problems)) (PP (IN of) (ADVP (NP (DT a) (NN generation)) (RB ago))))))) (, ,) (NP (PRP we)) (VP (VBP risk) (S (VP (VBG missing) (NP (NP (DT the) (NNS hazards)) (SBAR (WHNP (WDT that)) (S (VP (VBP lie) (ADVP (RB directly)) (PP (IN in) (NP (NP (NN front)) (PP (IN of) (NP (PRP$ our) (NNS eyes)))))))))))) (. .)))";
    public static final String ptb3 = "(ROOT (S (PP (IN On) (NP (DT the) (JJ other) (NN hand))) (, ,) (NP (NNP GM)) (VP (VBZ is) (VP (VBG making) (ADJP (JJ sure)) (SBAR (IN that) (S (NP (NP (DT a) (NN decision)) (PP (VBG regarding) (NP (NP (DT the) (NN sale)) (PP (IN of) (NP (NNP Saab)))))) (VP (MD should) (VP (VB be) (VP (VBN made) (PP (IN in) (NP (DT the) (JJ next) (JJ few) (NNS days)))))))))) (. .)))";
    public static final String ptb4 = "(ROOT (S (PP (VBG According) (PP (TO to) (NP (DT an) (NNP EU) (NN study)))) (, ,) (SBAR (IN if) (S (NP (DT all) (NNS cars)) (VP (VBD were) (VP (VBN equipped) (PP (IN with) (NP (DT this) (NN feature))))))) (, ,) (NP (NP (CD 1,100) (JJ fatal) (NNS accidents)) (VP (VBG involving) (NP (NNS pedestrians)))) (VP (MD could) (VP (VB be) (VP (VBN avoided) (NP (DT a) (NN year))))) (. .)))";
    public static final String ptb5 = "(ROOT (S (NP (DT The) (NN reduction)) (VP (MD will) (VP (VB be) (ADJP (ADJP (RB even) (JJR bigger)) (PRN (: -) (S (NP (PRP it)) (VP (VBZ estimates) (SBAR (IN that) (S (NP (NP (CD one)) (PP (IN in) (NP (NP (CD four) (NNS accidents)) (VP (VBG injuring) (NP (NNS people)))))) (VP (MD would) (VP (VB be) (VP (VBN avoided)))))))) (: -)) (SBAR (WHADVP (WRB when)) (S (NP (DT the) (JJ so-called) (JJ smart) (NN emergency)) (VP (VBZ braking) (S (NP (NNS systems)) (VP (VB become) (ADJP (JJ popular)))))))))) (. .)))";
    private EntityGridExtractor extractor;

    @BeforeEach
    public void setUp() {
        extractor = new EntityGridExtractor();
    }

    private EntityGridExtractor getEntityGridExtractor() {
        return extractor;
    }

    @Test
    public void testEntityResolver1() {
        char[][] grid = extractor.convertPtbsStringToGrids(ptb1);

        int os = 0;
        int ss = 0;
        int xs = 0;

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

        assertThat(ss).withFailMessage(message).isEqualTo(2);
        assertThat(xs).withFailMessage(message).isEqualTo(2);
        assertThat(os).withFailMessage(message).isEqualTo(4);
    }

    @Test
    public void testEntityResolver2() {
        Map<String, Integer> results = getGridResults(ptb2);
        assertThat(results.get("S")).withFailMessage(message).isEqualTo(1);
        assertThat(results.get("X")).withFailMessage(message).isEqualTo(3);
        assertThat(results.get("O")).withFailMessage(message).isEqualTo(4);
    }

    @Test
    public void testEntityResolver3() {
        Map<String, Integer> results = getGridResults(ptb3);
        assertThat(results.get("S")).withFailMessage(message).isEqualTo(2);
        assertThat(results.get("X")).withFailMessage(message).isEqualTo(0);
        assertThat(results.get("O")).withFailMessage(message).isEqualTo(4);
    }

    @Test
    public void testEntityResolver4() {
        Map<String, Integer> results = getGridResults(ptb4);
        assertThat(results.get("S")).withFailMessage(message).isEqualTo(2);
        assertThat(results.get("X")).withFailMessage(message).isEqualTo(2);
        assertThat(results.get("O")).withFailMessage(message).isEqualTo(3);
    }

    @Test
    public void testEntityResolver5() {
        Map<String, Integer> results = getGridResults(ptb5);
        assertThat(results.get("S")).withFailMessage(message).isEqualTo(3);
        assertThat(results.get("X")).withFailMessage(message).isEqualTo(0);
        assertThat(results.get("O")).withFailMessage(message).isEqualTo(2);
    }

    private Map<String, Integer> getGridResults(String ptb) {
        char[][] grid = extractor.convertPtbsStringToGrids(ptb);

        int os = 0;
        int ss = 0;
        int xs = 0;

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

        Map<String, Integer> roles = new HashMap<>();
        roles.put("S", ss);
        roles.put("X", xs);
        roles.put("O", os);

        return roles;
    }
}