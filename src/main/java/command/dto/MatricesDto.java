package command.dto;

import framework.state.ApplicationState;
import framework.utils.MatrixUtils;
import lombok.Getter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MatricesDto {

    private final RealMatrix F;

    private final RealMatrix FwithNegativePower;

    private final RealMatrix G;

    private final List<RealMatrix> listOfGp;

    private final RealMatrix inverseL;

    private MatricesDto(RealMatrix f, RealMatrix fwithNegativePower, RealMatrix g, List<RealMatrix> listOfGp,
                        RealMatrix inverseL) {
        this.F = f;
        this.FwithNegativePower = fwithNegativePower;
        this.G = g;
        this.listOfGp = listOfGp;
        this.inverseL = inverseL;
    }

    public static MatricesDto fromState(ApplicationState applicationState) {
        RealMatrix A = (RealMatrix) applicationState.getVariable("A");
        int q = (int) applicationState.getVariable("q");
        double T = (double) applicationState.getVariable("T");
        RealMatrix B = (RealMatrix) applicationState.getVariable("B");
        int k0 = (int) applicationState.getVariable("k0");

        Map<Integer, RealMatrix> powerToAInThatPower = MatrixUtils.getPowerToMatrixInThatPower(A, q);
        RealMatrix F = computeMatrixF(A, T, q, powerToAInThatPower);
        RealMatrix FwithNegativePower = computeMatrixFwithNegativePower(A, T, q, powerToAInThatPower);
        RealMatrix G = computeMatrixG(A, B, T, q, powerToAInThatPower);
        List<RealMatrix> listOfGp = computeListOfGp(FwithNegativePower, G, k0);
        RealMatrix inverseL = getInverseL(listOfGp, F, k0);
        return new MatricesDto(F, FwithNegativePower, G, listOfGp, inverseL);
    }

    private static RealMatrix computeMatrixF(RealMatrix A, double T, int q,
                                             Map<Integer, RealMatrix> powerToAInThatPower) {
        RealMatrix F = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q; i++) {
            RealMatrix matrixToAdd = powerToAInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i));
            F = F.add(matrixToAdd);
        }
        return F;
    }

    private static RealMatrix computeMatrixFwithNegativePower(RealMatrix A, double T, int q,
                                                              Map<Integer, RealMatrix> powerToAInThatPower) {
        RealMatrix F = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q; i++) {
            RealMatrix matrixToAdd = powerToAInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i))
                    .scalarMultiply(Math.pow(-1, i));
            F = F.add(matrixToAdd);
        }
        return F;
    }

    private static RealMatrix computeMatrixG(RealMatrix A, RealMatrix B, double T, int q,
                                             Map<Integer, RealMatrix> powerToAInThatPower) {
        RealMatrix G = new Array2DRowRealMatrix(A.getRowDimension(), A.getColumnDimension());
        for (int i = 0; i <= q - 1; i++) {
            RealMatrix matrixToAdd = powerToAInThatPower.get(i)
                    .scalarMultiply(Math.pow(T, i)).scalarMultiply(1.0 / CombinatoricsUtils.factorial(i + 1));
            G = G.add(matrixToAdd);
        }
        G = G.scalarMultiply(T);
        G = G.multiply(B);
        return G;
    }

    private static List<RealMatrix> computeListOfGp(RealMatrix FwithNegativePower, RealMatrix G, int k0) {
        Map<Integer, RealMatrix> powerToMatrixInThatPower = MatrixUtils
                .getPowerToMatrixInThatPower(FwithNegativePower, k0 - 1);
        List<RealMatrix> out = new ArrayList<>();
        for (int p = 0; p < k0; p++) {
            RealMatrix FPowered = powerToMatrixInThatPower.get(p);
            RealMatrix Gp = FPowered.multiply(G);
            out.add(Gp);
        }
        return out;
    }

    private static RealMatrix getSumOfGMultipliedToGTransposed(List<RealMatrix> listOfGp) {
        RealMatrix out = new Array2DRowRealMatrix(3, 3);
        for (RealMatrix Gp : listOfGp) {
            RealMatrix GpTransposed = Gp.transpose();
            out.add(Gp.multiply(GpTransposed));
        }
        return out;
    }

    private static RealMatrix getInverseL(List<RealMatrix> listOfGp, RealMatrix F, int k0) {
        RealMatrix sumOfGMultipliedToGTransposed = getSumOfGMultipliedToGTransposed(listOfGp);
        RealMatrix FPowered = F.power(k0 - 1);
        RealMatrix L = FPowered.multiply(sumOfGMultipliedToGTransposed);
        return org.apache.commons.math3.linear.MatrixUtils.inverse(L);
    }
}
