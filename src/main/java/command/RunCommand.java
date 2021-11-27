package command;

import command.dto.MatricesDto;
import framework.command.AbstractRunnableCommand;
import framework.utils.MatrixUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Map;

public class RunCommand extends AbstractRunnableCommand {

    public static final String NAME = "run";

    public RunCommand() {
        super(NAME);
    }

    @Override
    public void execute(String[] strings) {

    }

    private MatricesDto computeMatrices() {
        RealMatrix A = (RealMatrix) applicationState.getVariable("A");
        int q = (int) applicationState.getVariable("q");
        Map<Integer, RealMatrix> powerToMatrixInThatPower = MatrixUtils.getPowerToMatrixInThatPower(A, q);
        double T = (double) applicationState.getVariable("T");
        RealMatrix F = computeMatrixF(A, T, q, powerToMatrixInThatPower);
        RealMatrix B = (RealMatrix) applicationState.getVariable("B");
        RealMatrix G = computeMatrixG(A, B, T, q, powerToMatrixInThatPower);
        return new MatricesDto(F, G);
    }

    private RealMatrix computeMatrixF(RealMatrix A, double T, int q,
                                      Map<Integer, RealMatrix> powerToMatrixInThatPower) {
        RealMatrix F = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q; i++) {
            RealMatrix matrixToAdd = powerToMatrixInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i));
            F = F.add(matrixToAdd);
        }
        return F;
    }

    private RealMatrix computeMatrixG(RealMatrix A, RealMatrix B, double T, int q,
                                      Map<Integer, RealMatrix> powerToMatrixInThatPower) {
        RealMatrix G = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q - 1; i++) {
            RealMatrix matrixToAdd = powerToMatrixInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i + 1));
            G = G.add(matrixToAdd);
        }
        G = G.scalarMultiply(T);
        G = G.multiply(B);
        return G;
    }
}
