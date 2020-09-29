package NetWork.Structure;

import java.io.Serializable;
import java.util.Collection;


public interface MatrixInterface extends Serializable {
    Collection<Index> getNeighbors(final Index index);
    int getValue(final Index index);
}


