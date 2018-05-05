import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import org.ejml.simple.SimpleMatrix;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class RidgeLeader extends PlayerImpl implements Regressor  {

    private static final int TRAINING_WINDOW = 20;
    private ArrayList<Record> historicalData;
    private SimpleMatrix betas;
    private int day;
    private int WINDOW_SIZE = 10;
    private int DEGREE = 5;

    private RidgeLeader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "LR Leader");
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
        int startFrom = totalDataSize - TRAINING_WINDOW;
        SimpleMatrix X = StackelbergUtils.getPolynomial(StackelbergUtils.getXGivenWindow(ourPrices.subList(startFrom, totalDataSize), WINDOW_SIZE), DEGREE);
        SimpleMatrix y = StackelbergUtils.getYGivenWindow(theirPrices.subList(startFrom, totalDataSize), WINDOW_SIZE);
        double LAMBDA = 4;
        return X.transpose().mult(X).plus(SimpleMatrix
                .identity(X.numCols()).scale(LAMBDA)).invert().mult(X.transpose().mult(y));
    }

    @Override
    public SimpleMatrix predict() throws RemoteException {
        if (day > 101) {
            updatePrices(day-1);
            betas = fit();
        }
        day++;
        return StackelbergUtils.getLeadersPrice(betas, StackelbergUtils.getPolynomial(StackelbergUtils.getXGivenWindow(ourPrices, WINDOW_SIZE), DEGREE));
    }

    @Override
    public void updatePrices(int day) throws RemoteException {
        Record query = m_platformStub.query(m_type, day);
        ourPrices.add(query.m_leaderPrice);
        theirPrices.add(query.m_followerPrice);
    }

}
