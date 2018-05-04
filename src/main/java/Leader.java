import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import org.ejml.simple.SimpleMatrix;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


// Data related Issues:
// 1. Data is not correlated, hence it is not a time series problem

public class Leader extends PlayerImpl {

    // Arraylist with Record as values to store the first 100 values
    private ArrayList<Record> historicalData;
    private int WINDOW_SIZE = 1;
    private SimpleMatrix betas;
    private List<Float> our_prices;
    private List<Float> their_prices;

    // Calling constructor of super to register
    private Leader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "LR Leader");
        our_prices = new ArrayList<>();
        their_prices = new ArrayList<>();
    }

    // Get data for the last 100 days
    public void startSimulation(final int p_steps) throws RemoteException {
        historicalData = new ArrayList<>();
        for (int day = 1; day <= 100; day++) {
            Record new_record = m_platformStub.query(PlayerType.FOLLOWER, day);
            historicalData.add(new_record);
            their_prices.add(new_record.m_followerPrice);
            our_prices.add(new_record.m_leaderPrice);
        }
        SimpleMatrix X = StackelbergUtils.getXGivenWindow(our_prices, WINDOW_SIZE);
        SimpleMatrix y = StackelbergUtils.getYGivenWindow(their_prices, WINDOW_SIZE);

        // Moore-penrose
        betas = X.transpose().mult(X).invert().mult(X.transpose().mult(y));

    }

    // Formula to calculate the daily profit
    private double calculateDailyProfit(float leaders_price, float followers_price) {
        return (leaders_price - 1.0) * (2.0 - leaders_price + (0.3 * followers_price));
    }

    // When simulation proceeds to a new day
    // Could ask the price and cost of the day before the current day
    // Computing an optimal price
    public void proceedNewDay(int p_date) throws RemoteException {
        m_platformStub.publishPrice(m_type, generateLeaderPrice());
    }

    private float generateLeaderPrice() {
        SimpleMatrix X = StackelbergUtils.getXGivenWindow(our_prices, WINDOW_SIZE);
        double follower_price = betas.dot(X.extractVector(true, our_prices.size() - 1));

        return 1;
    }

    private double derivative(double lp, double fp) {
        return (3 - (2 * lp) + (0.3 * fp));
    }

    public static void main(final String[] p_args) throws RemoteException, NotBoundException {
        new Leader();
    }
}
