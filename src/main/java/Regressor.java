import org.ejml.simple.SimpleMatrix;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface Regressor {
    List<Float> ourPrices = new ArrayList<>();
    List<Float> theirPrices = new ArrayList<>();

    SimpleMatrix fit();
    SimpleMatrix predict() throws RemoteException;

    default float generateLeaderPrice() throws RemoteException {
        SimpleMatrix newX = predict();
//        return (float) newX.get(newX.numRows() - 1);
        return (float) newX.get(0);
    }

     void updatePrices(int day) throws RemoteException;
}
