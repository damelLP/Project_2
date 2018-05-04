import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Arrays;
import java.util.List;

import static org.nd4j.linalg.api.shape.Shape.shapeToString;

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

    @Test
    public void getPolynomials() {
        INDArray twoDArray = Nd4j.create(new float[]{1,2,3,4,5,6,7,8}, new int[]{2,2,2});
        INDArray poly_twoDArray = StackelbergUtils.getPolynomials(twoDArray, 2);
        String twoD_transpose_shape = "Rank: 3,Offset: 0\n" +
                " Order: f Shape: [2,2,4],  stride: [1,2,4]";

        Assert.assertTrue(twoD_transpose_shape.equals(shapeToString(poly_twoDArray)));
    }
}