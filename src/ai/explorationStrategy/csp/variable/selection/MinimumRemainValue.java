package ai.explorationStrategy.csp.variable.selection;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

public class MinimumRemainValue extends VariableSelectionMode {

    private boolean domainsSizeEquals;

    /**
     *
     * Selectionne la variable qui possede le moins de valeur restantes à attribuer
     * */

    @Override
    public CSPvariable selectVariable(BinaryCSP<?> csp) {

        domainsSizeEquals = true;

        CSPvariable minIvar = null;
        int minDomainSize = Integer.MAX_VALUE, lastDomSize = -1;

        for(CSPvariable variable : csp.getVariables()){

            if(variable.getValue() != null)
                continue;

            int domainSize = csp.getDomain(variable).size();

            //System.out.println(domainSize);

            if(lastDomSize != -1 && domainSize != lastDomSize) {
                domainsSizeEquals = false;
            }

            if( domainSize < minDomainSize) {
                minDomainSize = domainSize;
                minIvar = variable;
            }

            lastDomSize = domainSize;

        }

        return minIvar;
    }

    public boolean areDomainsSizeEquals() {
        return domainsSizeEquals;
    }

}
