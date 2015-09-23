package Grammar;

import java.io.Serializable;
import java.util.Objects;

public class AtomicCategory implements CCGCategory, Serializable {

    String category;

    public AtomicCategory(String Cat) {
        this.category = Cat;
    }

    public AtomicCategory() {
    }

    public CCGCategory apply(CCGCategory cat) {
        // TODO Auto-generated method stub
        return null;
    }

    public String toString() {
        return category;
    }

    public void setCategory(String cat) {
        category = cat;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.category);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AtomicCategory other = (AtomicCategory) obj;
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        return true;
    }

}
