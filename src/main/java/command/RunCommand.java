package command;

import command.dto.MatricesDto;
import framework.command.AbstractRunnableCommand;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunCommand extends AbstractRunnableCommand {

    public static final String NAME = "run";

    public RunCommand() {
        super(NAME);
    }

    @Override
    public void execute(String[] strings) {
        MatricesDto dto = MatricesDto.fromState(applicationState);
        RealVector L0 = getL0(dto);
        List<RealVector> listOfUk = getListOfUk(dto, L0);
        List<RealVector> listOfX = getListOfX(dto, listOfUk);

    }

    private RealVector getL0(MatricesDto dto) {
        RealVector x = (RealVector) applicationState.getVariable("x");
        return dto.getInverseL().operate(x);
    }

    private List<RealVector> getListOfUk(MatricesDto dto, RealVector l0) {
        return dto.getListOfGp()
                .stream()
                .map(Gt -> Gt.transpose().operate(l0))
                .collect(Collectors.toList());
    }

    private List<RealVector> getListOfX(MatricesDto dto, List<RealVector> listOfUk) {
        List<RealVector> out = new ArrayList<>();
        RealVector previousX = new ArrayRealVector(3);
        for (RealVector Uk: listOfUk) {
            RealVector newX = dto.getF().operate(previousX).add(dto.getG().operate(Uk));
            out.add(previousX);
            previousX = newX;
        }
        return out;
    }
}
