import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.ejml.simple.SimpleMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


// Data related Issues:
// 1. Data is not correlated, hence it is not a time series problem

public class DeepLeader extends PlayerImpl {

    // Arraylist with Record as values to store the first 100 values
    private ArrayList<Record> historicalData;
    private int WINDOW_SIZE = 1;
    private SimpleMatrix betas;
    private List<Float> our_prices;
    private List<Float> their_prices;
    private MultiLayerNetwork model;

    // Calling constructor of super to register
    private DeepLeader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "LR Leader");

        // initialize datastore
        our_prices = new ArrayList<>();
        their_prices = new ArrayList<>();

        // load model
        try {
            URL modelJson = getClass().getResource("lstm_model.json");
            URL modelH5 = getClass().getResource("lstm_model.h5");
            MultiLayerConfiguration config = KerasModelImport
                    .importKerasSequentialConfiguration(modelJson.getFile(), true);
            model = new MultiLayerNetwork(config);
            model.init();
        } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
            e.printStackTrace();
        }
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
        INDArray X = StackelbergUtils.get3DFeatures(our_prices, WINDOW_SIZE);
        INDArray y = StackelbergUtils.get2DFeatures(their_prices, WINDOW_SIZE);
        model.fit(X, y);
    }


    private MultiLayerNetwork getNetwork() {

        return null;
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
//        INDArray X = StackelbergUtils.get3DFeatures(our_prices, WINDOW_SIZE);
//        INDArray follower_price_Matrix = model.output(X.getRow(X.size(0)), false);
//        double follower_price = 1.8;
//        return (float) StackelbergUtils.getLeadersPrice(betas);
        return 1;
    }

    private double derivative(double lp, double fp) {
        return (3 - (2 * lp) + (0.3 * fp));
    }

    public static void main(final String[] p_args) throws RemoteException, NotBoundException {
        new DeepLeader();
    }
}
