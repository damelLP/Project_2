import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import org.ejml.simple.SimpleMatrix;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;


public class RidgeLeader extends PlayerImpl implements Regressor {

    private int trainingWindow;
    private SimpleMatrix betas;
    private int day;
    private int multivariateWindow;
    private int degree;
    private double lambda;

    RidgeLeader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "LR Leader");
    }

    public void startSimulation(final int p_steps) throws RemoteException {
        for (day = 1; day <= 100; day++) {
            Record new_record = m_platformStub.query(PlayerType.FOLLOWER, day);
            theirPrices.add(new_record.m_followerPrice);
            ourPrices.add(new_record.m_leaderPrice);
        }

        // Grid search on the model parameters
        int[] trainingWindow = {10, 20, 30};
        int[] multiWindows = {1, 5};
        int[] polynomials = {1, 3, 5, 7};
        float[] lambdas = {0, 2, 4, 6, 8};

        double minError = Integer.MAX_VALUE;
        double curError;
        for (int trainWindow : trainingWindow) {
            for (int vars : multiWindows) {
                for (float lam : lambdas) {
                    for (int polynomial : polynomials) {
                        curError = getMSE(ourPrices, theirPrices, trainWindow, vars, lam, polynomial);
                        if (curError < minError) {
                            minError = curError;
                            this.trainingWindow = trainWindow;
                            this.multivariateWindow = vars;
                            this.lambda = lam;
                            this.degree = polynomial;
                        }
                    }
                }
            }
        }
        betas = fit();

    }

    double getMSE(List<Float> ourPrices, List<Float> theirPrices, int trainWindow, int vars, float lam, int polynomial) {
        List<Float> trainX = ourPrices.subList(0, trainWindow);
        List<Float> trainY = theirPrices.subList(0, trainWindow);
        List<Float> testX = ourPrices.subList(trainWindow, trainWindow + trainWindow);
        List<Float> testY = theirPrices.subList(trainWindow, trainWindow + trainWindow);

        SimpleMatrix X = StackelbergUtils.getXGivenPolynomialAndWindow(trainX, vars, polynomial);
        SimpleMatrix y = StackelbergUtils.getYGivenWindow(trainY, vars);
        SimpleMatrix testBetas = fitData(X, y, lam);

        X = StackelbergUtils.getXGivenPolynomialAndWindow(testX, vars, polynomial);
        y = StackelbergUtils.getYGivenWindow(testY, vars);

        SimpleMatrix y_predict = testBetas.transpose().mult(X.transpose()).transpose();

        return y.minus(y_predict).elementPower(2).elementSum() / y.numRows();
    }


    private double calculateDailyProfit(float leaders_price, float followers_price) {
        return (leaders_price - 1.0) * (2.0 - leaders_price + (0.3 * followers_price));
    }

    public void proceedNewDay(int p_date) throws RemoteException {
        m_platformStub.publishPrice(m_type, generateLeaderPrice());
    }

    public static void main(final String[] p_args) throws RemoteException, NotBoundException {
        new RidgeLeader();
    }

    @Override
    public SimpleMatrix fit() {
        int totalDataSize = theirPrices.size();
        int startFrom = totalDataSize - trainingWindow;
        SimpleMatrix X = StackelbergUtils.getXGivenPolynomialAndWindow(ourPrices.subList(startFrom, totalDataSize), multivariateWindow, degree);
        SimpleMatrix y = StackelbergUtils.getYGivenWindow(theirPrices.subList(startFrom, totalDataSize), multivariateWindow);
        return fitData(X, y, lambda);
    }

    private SimpleMatrix fitData(SimpleMatrix x, SimpleMatrix y, double LAMBDA) {
        return x.transpose().mult(x).plus(SimpleMatrix
                .identity(x.numCols()).scale(LAMBDA)).pseudoInverse().mult(x.transpose().mult(y));
    }

    @Override
    public SimpleMatrix predict() throws RemoteException {
        if (day > 101) {
            updatePrices(day - 1);
            betas = fit();
        }
        day++;
        SimpleMatrix X = StackelbergUtils.getXGivenPolynomialAndWindow(ourPrices, multivariateWindow, degree);
        return StackelbergUtils.getLeadersPrice(betas, X);
    }


    @Override
    public void updatePrices(int day) throws RemoteException {
        Record query = m_platformStub.query(m_type, day);
        ourPrices.add(query.m_leaderPrice);
        theirPrices.add(query.m_followerPrice);
    }

}
