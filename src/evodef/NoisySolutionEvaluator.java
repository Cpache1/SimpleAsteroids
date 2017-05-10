package evodef;

/**
 * Created by sml on 10/05/2017.
 */
public interface NoisySolutionEvaluator extends SolutionEvaluator {
    public Boolean isOptimal(int[] solution);
    public double trueFitness(int[] solution);
}