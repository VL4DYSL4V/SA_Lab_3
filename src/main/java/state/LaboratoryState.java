package state;

import framework.state.AbstractApplicationState;
import framework.state.StateHelper;
import framework.utils.MatrixUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

@Getter
@Setter
public class LaboratoryState extends AbstractApplicationState {

    private double a1 = 1.0;

    private double a2 = 3.0;

    private double b = 1.0;

    private double T = 0.02;

    private int q = 10;

    @Override
    protected void initVariableNameToSettersMap() {
        variableNameToSetter.put("a1", StateHelper.getDoubleSetter("a1", this::setA1));
        variableNameToSetter.put("a2", StateHelper.getDoubleSetter("a2", this::setA2));
        variableNameToSetter.put("b", StateHelper.getDoubleSetter("b", this::setB));
        variableNameToSetter.put("T", StateHelper.getDoubleSetter("T", this::setT));
        variableNameToSetter.put("q", StateHelper.getIntegerSetter("q", this::setQ));
    }

    @Override
    protected void initVariableNameToGettersMap() {
        variableNameToGetter.put("T", this::getT);
        variableNameToGetter.put("q", this::getQ);
        variableNameToGetter.put("A", () -> MatrixUtils.getFrobeniusMatrix(new double[]{1, a1, a2}));
        variableNameToGetter.put("B", () -> new Array2DRowRealMatrix(new double[]{0, 0, b}));
    }
}
