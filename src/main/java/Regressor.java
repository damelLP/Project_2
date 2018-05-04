import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;

public interface Regressor {
    List<Float> ourPrices = new ArrayList<>();
    List<Float> theirPrices = new ArrayList<>();

    SimpleMatrix fit(SimpleMatrix X, SimpleMatrix y);
    SimpleMatrix predict();

    default float generateLeaderPrice() {
        SimpleMatrix newX = predict();
        float new_price = (float) newX.get(newX.numRows() - 1);
        ourPrices.add(new_price);
        return new_price;
    }
}
