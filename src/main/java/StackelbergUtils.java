import org.ejml.simple.SimpleMatrix;

import java.util.List;

public class StackelbergUtils {
    public static SimpleMatrix getXGivenWindow(List<Float> our_prices, int window_size) {
        SimpleMatrix X = new SimpleMatrix(our_prices.size() - window_size, window_size + 1);
        for (int row = 0; row < our_prices.size() - window_size; row++) {
            // set the initial intercept val
            X.set(row, 0, 1);
            for (int col = 1; col < window_size + 1; col++) {
                X.set(row, col, our_prices.get(row * window_size + col - 1));
            }
        }
        return X;
    }

    public static SimpleMatrix getYGivenWindow(List<Float> their_prices, int window_size) {
        SimpleMatrix y = new SimpleMatrix(their_prices.size() - window_size, 1);
        for (int row = 0; row < their_prices.size() - window_size; row++) {
            y.set(row, 0, their_prices.get(row + window_size));
        }
        return y;
    }
}
