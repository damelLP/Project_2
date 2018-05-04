import org.apache.commons.lang3.ArrayUtils;
import org.ejml.simple.SimpleMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

import static org.nd4j.linalg.ops.transforms.Transforms.pow;

class StackelbergUtils {
    static SimpleMatrix getXGivenWindow(List<Float> our_prices, int window_size) {
        SimpleMatrix X = new SimpleMatrix(our_prices.size() - window_size, window_size + 1);
        for (int row = 0; row < our_prices.size() - window_size; row += window_size) {
            // set the initial intercept val
            X.set(row, 0, 1);
            for (int col = 1; col < window_size + 1; col++) {
                X.set(row, col, our_prices.get(row * window_size + col - 1));
            }
        }
        return X;
    }

    static SimpleMatrix getYGivenWindow(List<Float> their_prices, int window_size) {
        SimpleMatrix y = new SimpleMatrix(their_prices.size() - window_size, 1);
        for (int row = 0; row < their_prices.size() - window_size; row += window_size) {
            y.set(row, 0, their_prices.get(row + window_size));
        }
        return y;
    }


    static INDArray get2DFeatures(List<Float> their_prices, int window_size) {
        INDArray y = Nd4j.zeros(their_prices.size() - window_size, 1);
        for (int row = 0; row < their_prices.size() - window_size; row += window_size) {
            y.put(row, 0, their_prices.get(row + window_size));
        }
        return y;
    }

    static INDArray getPolynomials(INDArray X, int polynomial) {
        for (int p= 1; p <= polynomial; p++){
            INDArray X_2 = pow(X, polynomial);
            if (p>=2){
                X = Nd4j.concat(0, X, X_2);
            }
        }
        return X.transpose();
    }

    static INDArray get3DFeatures(List<Float> our_prices, int window_size) {
        int n_samples = our_prices.size() - window_size;
        INDArray X = Nd4j.ones(n_samples, window_size, 1);
        float[] priceWindow = new float[our_prices.size()];

        // map to float[]
        for (int i = 0; i < our_prices.size(); i++) {
            priceWindow[i] = our_prices.get(i);
        }

        // values into the matrix
        for (int row = 0; row < n_samples; row += window_size) {
            X.putRow(row, Nd4j.create(ArrayUtils.subarray(priceWindow, row, row + window_size)));
        }
        return X;
    }
}
