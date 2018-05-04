import org.apache.commons.lang3.ArrayUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

class StackelbergUtils {
    static SimpleMatrix getXGivenWindow(List<Float> our_prices, int window_size) {
        int numRows = (our_prices.size() - window_size);
        int numCols = window_size + 1; // holds the intercept
        SimpleMatrix X = new SimpleMatrix(numRows, numCols);
        for (int row = 0; row < numRows; row++) {
            // set the initial intercept val
            X.set(row, 0, 1);
            for (int col = 1; col < numCols; col++) {
                X.set(row, col, our_prices.get(row + col - 1));
            }
        }
        return X;
    }

    static SimpleMatrix getPolynomial(SimpleMatrix X, int degree) {
        SimpleMatrix newX = new SimpleMatrix(X.numRows(), ((X.numCols()-1) * degree) +1 );
        SimpleMatrix x1 = X.extractVector(false, 1);
        newX.insertIntoThis(0, 0, X);
        for (int col = 1; col < degree; col++) {
            newX.insertIntoThis(0, col, x1.elementPower(col));
        }
        return newX;
    }

    static SimpleMatrix getYGivenWindow(List<Float> their_prices, int window_size) {
        SimpleMatrix y = new SimpleMatrix(their_prices.size() - window_size, 1);
        for (int row = 0; row < their_prices.size() - window_size; row++) {
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

    static SimpleMatrix getLeadersPrice(SimpleMatrix betas) {

        SimpleMatrix scaled = betas.scale(0.3);
        SimpleMatrix threes = new SimpleMatrix(betas.numRows(), betas.numCols());
        threes.set(3);
        return scaled.plus(threes).scale(.5);

    }
}
