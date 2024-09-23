package utils;

import com.google.common.collect.Sets.SetView;

import java.util.Map;

public class WeightedGraphProjection extends GraphProjection {

    public WeightedGraphProjection(BipartiteGraph graph) {
        super(graph);
    }


    /**
     * sum all the projections leaving each sentence node
     * For weighted projection: count the number of shared entities.
     */
    protected double calculateEdgeWeight(double sumOfEdgeWeights, SetView<String> intersectingSets, Map<String, Integer> s1, Map<String, Integer> s2, double distance) {
        if (intersectingSets != null) {

            //sumOfEdgeWeights+= (double)intersectingSets.size();
            double sum = intersectingSets.size();
            sumOfEdgeWeights += (sum / distance);
            buffer.append("\n $ WEIGHTED.. incrementing edge weights by " + (double) intersectingSets.size() + " for ");
            for (String entity : intersectingSets) {
                buffer.append(entity + " ");
            }
        }
        return sumOfEdgeWeights;
    }

}