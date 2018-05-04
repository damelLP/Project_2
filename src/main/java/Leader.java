import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import org.ejml.simple.SimpleMatrix;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class Leader extends PlayerImpl implements Regressor {

    private ArrayList<Record> historicalData;
    private int WINDOW_SIZE = 10;
    private SimpleMatrix betas;
    private int day;

    public Leader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "LR Leader");
    }

    public void startSimulation(final int p_steps) throws RemoteException {
        historicalData = new ArrayList<>();
        for (day = 1; day <= 100; day++) {
            Record new_record = m_platformStub.query(PlayerType.FOLLOWER, day);
            historicalData.add(new_record);
            theirPrices.add(new_record.m_followerPrice);
            ourPrices.add(new_record.m_leaderPrice);
        }
        // Moore-penrose
        betas = fit();

    }

    public void updatePrices(int day) throws RemoteException {
        Record query = m_platformStub.query(m_type, day);
        ourPrices.add(query.m_leaderPrice);
        theirPrices.add(query.m_followerPrice);
    }

    private double calculateDailyProfit(float leaders_price, float followers_price) {
        return (leaders_price - 1.0) * (2.0 - leaders_price + (0.3 * followers_price));
    }

    public void proceedNewDay(int p_date) throws RemoteException {
//        m_platformStub.publishPrice(m_type, generateLeaderPrice());
        m_platformStub.publishPrice(m_type, 2);

    }

    @Override
    public SimpleMatrix fit() {
        SimpleMatrix X = StackelbergUtils.getXGivenWindow(ourPrices, WINDOW_SIZE);
        SimpleMatrix y = StackelbergUtils.getYGivenWindow(theirPrices, WINDOW_SIZE);
        return X.transpose().mult(X).invert().mult(X.transpose().mult(y));
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

    public static void main(final String[] p_args) throws RemoteException, NotBoundException {
        new Leader();
    }
}
