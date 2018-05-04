import org.ejml.simple.SimpleBase;
import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StackelbergUtilsTest {

    @Test
    public void getXGivenWindow() {
        List<Float> testleader = Arrays.asList((float)1.24, (float)1.64, (float)1.78, (float)1.71, (float)1.90,
                                                (float)1.24, (float)1.64, (float)1.78, (float)1.71, (float)1.90,
                                                (float)1.24, (float)1.64, (float)1.78, (float)1.71, (float)1.90,
                                                (float)1.24, (float)1.64, (float)1.78, (float)1.71, (float)1.90);
        SimpleMatrix X = StackelbergUtils.getXGivenWindow(testleader, 5);

        Assert.assertEquals(testleader.size() - 5, X.numRows());
        Assert.assertEquals(6, X.numCols());

    }

    @Test
    public void getYGivenWindow() {
        List<Float> testfollower = Arrays.asList((float)1.24, (float)1.64, (float)1.78,
                                                 (float)1.24, (float)1.64, (float)1.78,
                                                 (float)1.24, (float)1.64, (float)1.78);

        SimpleMatrix y = StackelbergUtils.getYGivenWindow(testfollower, 3);

        Assert.assertEquals(testfollower.size() - 3, y.numRows());
        Assert.assertEquals(1, y.numCols());

    }

    @Test
    public void get2DFeatures() {
        List<Float> testfollower = Arrays.asList((float)1.24, (float)1.64, (float)1.78);
        INDArray array = StackelbergUtils.get2DFeatures(testfollower, 1);

        Assert.assertEquals(2, array.size(0));
        Assert.assertEquals(1, array.size(1));
    }

    @Test
    public void get3DFeatures() {
        List<Float> testleader = Arrays.asList((float)1.24, (float)1.64, (float)1.78, (float)1.71, (float)1.90);
        INDArray array = StackelbergUtils.get3DFeatures(testleader, 2);

        Assert.assertEquals(3, array.size(0));
        Assert.assertEquals(2, array.size(1));
        Assert.assertEquals(1, array.size(2));
    }

    @Test
    public void getLeadersPrice(){
        double fp = 1.86;
        double expected_lp = 1.779;
        SimpleMatrix threes = new SimpleMatrix(10, 3);
        threes.set(3);
        threes.set(2);
//        double actual_lp = StackelbergUtils.getLeadersPrice();
//        assertEquals(expected_lp, actual_lp, 0.01);
    }
}