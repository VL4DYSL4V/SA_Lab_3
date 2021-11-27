package command;

import command.dto.MatricesDto;
import framework.command.AbstractRunnableCommand;
import framework.utils.MatrixUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Map;

public class RunCommand extends AbstractRunnableCommand {

    public static final String NAME = "run";

    public RunCommand() {
        super(NAME);
    }

    @Override
    public void execute(String[] strings) {

    }

    private RealMatrix getSumOfGMultipliedToGTransposed(MatricesDto dto) {
        int k0 = (int) applicationState.getVariable("k0");
        Map<Integer, RealMatrix> powerToMatrixInThatPower = MatrixUtils
                .getPowerToMatrixInThatPower(dto.getFwithNegativePower(), k0 - 1);
        RealMatrix out = new Array2DRowRealMatrix(3, 3);
        for (int p = 0; p < k0; p++) {
            RealMatrix FPowered = powerToMatrixInThatPower.get(p);
            RealMatrix Gp = FPowered.multiply(dto.getG());
            RealMatrix GpTransposed = Gp.transpose();
            out.add(Gp.multiply(GpTransposed));
        }
        return out;
    }

    private RealMatrix getInverseL(MatricesDto dto, RealMatrix sumOfGMultipliedToGTransposed) {
        int k0 = (int) applicationState.getVariable("k0");
        RealMatrix FPowered = dto.getF().power(k0 - 1);
        RealMatrix L = FPowered.multiply(sumOfGMultipliedToGTransposed);
        return org.apache.commons.math3.linear.MatrixUtils.inverse(L);
    }
}
