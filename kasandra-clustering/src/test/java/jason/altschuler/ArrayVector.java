package jason.altschuler;

import java.util.ArrayList;
import java.util.List;

public class ArrayVector implements KMeansVector {

    private List<Double> vector = new ArrayList<>();

    public ArrayVector(List<Double> vector) {
        this.vector = vector;
    }

    @Override
    public List<Double> getVector() {
        return vector;
    }
}
