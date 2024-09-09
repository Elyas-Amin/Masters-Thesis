package entityGrid;

import com.google.common.collect.Sets.SetView;
import java.util.Map;

public class SyntacticGraphProjection extends GraphProjection {

    public SyntacticGraphProjection(BipartiteGraph graph) {
        super(graph);
    }

    protected double calculateEdgeWeight(double sumOfEdgeWeights, SetView<String> intersectingSets, Map<String, Integer> S1, Map<String, Integer> S2, double distance) {
        if (intersectingSets != null) {
            for (String entity : intersectingSets) {

                //sumOfEdgeWeights+= S1.get(entity).doubleValue() * S2.get(entity).doubleValue();
                double sum = S1.get(entity).doubleValue() * S2.get(entity).doubleValue();
                sumOfEdgeWeights += (sum / distance);

                buffer.append("\n $ SYNTACTIC.. incrementing edge weights by " +
                        S1.get(entity).doubleValue() + " * " + S2.get(entity).doubleValue() + " = " +
                        S1.get(entity).doubleValue() * S2.get(entity).doubleValue() + " for " + entity);
            }
        }
        return sumOfEdgeWeights;
    }
}