package command.dto;

import framework.state.ApplicationState;
import framework.utils.MatrixUtils;
import lombok.Getter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Map;

@Getter
public class MatricesDto {

    private final RealMatrix F;

    private final RealMatrix FwithNegativePower;

    private final RealMatrix G;

    private MatricesDto(RealMatrix f, RealMatrix fwithNegativePower, RealMatrix g) {
        F = f;
        FwithNegativePower = fwithNegativePower;
        G = g;
    }

    public static MatricesDto fromState(ApplicationState applicationState) {
        RealMatrix A = (RealMatrix) applicationState.getVariable("A");
        int q = (int) applicationState.getVariable("q");
        double T = (double) applicationState.getVariable("T");
        RealMatrix B = (RealMatrix) applicationState.getVariable("B");

        Map<Integer, RealMatrix> powerToMatrixInThatPower = MatrixUtils.getPowerToMatrixInThatPower(A, q);
        RealMatrix F = computeMatrixF(A, T, q, powerToMatrixInThatPower);
        RealMatrix FwithNegativePower = computeMatrixFwithNegativePower(A, T, q, powerToMatrixInThatPower);
        RealMatrix G = computeMatrixG(A, B, T, q, powerToMatrixInThatPower);
        return new MatricesDto(F, FwithNegativePower, G);
    }

    private static RealMatrix computeMatrixF(RealMatrix A, double T, int q,
                                             Map<Integer, RealMatrix> powerToMatrixInThatPower) {
        RealMatrix F = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q; i++) {
            RealMatrix matrixToAdd = powerToMatrixInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i));
            F = F.add(matrixToAdd);
        }
        return F;
    }

    private static RealMatrix computeMatrixFwithNegativePower(RealMatrix A, double T, int q,
                                                              Map<Integer, RealMatrix> powerToMatrixInThatPower) {
        RealMatrix F = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q; i++) {
            RealMatrix matrixToAdd = powerToMatrixInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i))
                    .scalarMultiply(Math.pow(-1, i));
            F = F.add(matrixToAdd);
        }
        return F;
    }

    private static RealMatrix computeMatrixG(RealMatrix A, RealMatrix B, double T, int q,
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
