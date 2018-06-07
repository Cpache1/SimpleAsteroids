package agents.coevo;

import evodef.DefaultMutator;

public class CoEvoAgentFactory {

    public double mutationRate = 2;
    public boolean totalRandomMutation = false;
    public boolean useShiftBuffer = true;

    public CoEvoAgent getAgent() {
        //
        // todo Add in the code t make this
        int nResamples = 1;

        DefaultMutator mutator = new DefaultMutator(null);
        // setting to true may give best performance
        mutator.pointProb = mutationRate;
        mutator.totalRandomChaosMutation = totalRandomMutation;

        CRMHC searchAlg = new CRMHC();
        searchAlg.setSamplingRate(nResamples);
        searchAlg.setMutator(mutator);

        CRMHC evoAlg = searchAlg;

        // evoAlg = new SlidingMeanEDA();

        int nEvals = 20;
        int seqLength = 200;
        CoEvoAgent evoAgent = new CoEvoAgent().setEvoAlg(evoAlg, nEvals).setSequenceLength(seqLength);
        evoAgent.setUseShiftBuffer(useShiftBuffer);
        // evoAgent.setVisual();

        return evoAgent;
    }
}
