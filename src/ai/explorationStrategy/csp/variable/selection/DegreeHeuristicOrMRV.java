package ai.explorationStrategy.csp.variable.selection;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

public class DegreeHeuristicOrMRV extends VariableSelectionMode {

    DegreeHeuristic DH = new DegreeHeuristic();
    MinimumRemainValue MRV = new MinimumRemainValue();

    @Override
    public CSPvariable selectVariable(BinaryCSP<?> csp) {

        CSPvariable csPvariable = MRV.selectVariable(csp);

        if(MRV.areDomainsSizeEquals()){
            csPvariable = DH.selectVariable(csp);
        }

        return csPvariable;
    }


}
