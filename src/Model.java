package Sudoku;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

public interface Model extends Serializable {

    void addPropertyChangeListener(PropertyChangeListener l);

    void removePropertyChangeListener(PropertyChangeListener l);
}
