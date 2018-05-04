import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import org.ejml.simple.SimpleMatrix;
import java.util.List;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class LogisticRegressor extends PlayerImpl implements Regressor {
    private SimpleMatrix betas;
    private int day;
    private double[] weights;
    private int epochs = 100;

    protected LogisticRegressor(PlayerType p_type, String p_displayName) throws RemoteException, NotBoundException {
        super(p_type, p_displayName);
    }

    public void startSimulation(final int p_steps) throws RemoteException {
        for (day = 1; day <= 100; day++) {
            Record new_record = m_platformStub.query(PlayerType.FOLLOWER, day);
            theirPrices.add(new_record.m_followerPrice);
            ourPrices.add(new_record.m_leaderPrice);
        }
        // Modified moore-penrose
        betas = fit();
    }

    @Override
    public SimpleMatrix predict() throws RemoteException {
        if (day > 101) {
            updatePrices(day - 1);
            betas = fit();
        }
        day++;
        return StackelbergUtils.getLeadersPrice(betas);
    }

    @Override
    public SimpleMatrix fit() {
        int WINDOW_SIZE = 5;
        int DEGREE = 3;
        SimpleMatrix X = StackelbergUtils.getPolynomial(StackelbergUtils.getXGivenWindow(ourPrices, WINDOW_SIZE), DEGREE);
        SimpleMatrix y = StackelbergUtils.getYGivenWindow(theirPrices, WINDOW_SIZE);
        double LAMBDA = 2;
        return X.transpose().mult(X).plus(SimpleMatrix
                .identity(X.numCols()).scale(LAMBDA)).invert().mult(X.transpose().mult(y));
    }

    @Override
    public void updatePrices(int day) throws RemoteException {
        Record query = m_platformStub.query(m_type, day);
        ourPrices.add(query.m_leaderPrice);
        theirPrices.add(query.m_followerPrice);
    }

    @Override
    public void proceedNewDay(int i) throws RemoteException {
        train();
        classify(ourPrices);
    }

    private void train(List<Float> ourPrices) {
        for(int n = 0; n < epochs; n++) {
        }
    }

    private double classify(List<Float> ourPrices) {
        double logit = .0;
        for (int i=0; i < weights.length;i++)  {
            logit += weights[i] * ourPrices.get(i);
        }
        return sigmoid(logit);
    }

    private static double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }
}
