import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StackelbergUtilsTest {

    @Test
    public void getXGivenWindow() {
    }

    @Test
    public void getYGivenWindow() {
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
}