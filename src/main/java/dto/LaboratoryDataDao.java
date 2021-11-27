package dto;

import org.apache.commons.math3.linear.RealVector;

public interface LaboratoryDataDao {

    void append(int step, RealVector x, RealVector u);

    void clear();

}
