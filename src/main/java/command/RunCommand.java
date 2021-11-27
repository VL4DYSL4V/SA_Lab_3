package command;

import command.dto.MatricesDto;
import dto.FileSystemLaboratoryDataDao;
import dto.LaboratoryDataDao;
import framework.command.AbstractRunnableCommand;
import framework.utils.ValidationUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RunCommand extends AbstractRunnableCommand {

    public static final String NAME = "run";

    private final LaboratoryDataDao dao;

    public RunCommand() {
        super(NAME);
        this.dao = new FileSystemLaboratoryDataDao();
    }

    @Override
    public void execute(String[] strings) {
        MatricesDto dto = MatricesDto.fromState(applicationState);
        RealVector L0 = getL0(dto);
        List<RealVector> listOfUk = getListOfUk(dto, L0);
        List<RealVector> listOfX = getListOfX(dto, listOfUk);
        dao.clear();
        ValidationUtils.requireEquals(listOfUk.size(), listOfX.size(), "List sizes must be equal");
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
        for (RealVector Uk : listOfUk) {
            RealVector newX = dto.getF().operate(previousX).add(dto.getG().operate(Uk));
            out.add(previousX);
            previousX = newX;
        }
        return out;
    }
}
