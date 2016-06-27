package jason.altschuler;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

public class KMeansTest {
    @Test
    public void testKMeans() {
        // run K-means
        final long startTime = System.currentTimeMillis();

        ArrayVector vector1 = new ArrayVector(ImmutableList.of(1.0, 1.0));
        ArrayVector vector2 = new ArrayVector(ImmutableList.of(1.5, 2.0));
        ArrayVector vector3 = new ArrayVector(ImmutableList.of(3.0, 4.0));
        ArrayVector vector4 = new ArrayVector(ImmutableList.of(5.0, 7.0));
        ArrayVector vector5 = new ArrayVector(ImmutableList.of(3.5, 5.0));
        ArrayVector vector6 = new ArrayVector(ImmutableList.of(4.5, 5.0));
        ArrayVector vector7 = new ArrayVector(ImmutableList.of(3.5, 4.5));
        List<KMeansVector> points = ImmutableList.of(vector1, vector2, vector3, vector4, vector5, vector6, vector7);

        KMeans clustering = new KMeans.Builder(2, points)
                .iterations(50)
                .pp(true)
                .epsilon(.001)
                .useEpsilon(true)
                .build();
        final long endTime = System.currentTimeMillis();

        // print timing information
        final long elapsed = endTime - startTime;
        System.out.println("Clustering took " + (double) elapsed/1000 + " seconds");
        System.out.println();

        // get output
        List<KMeansVector> centroids = clustering.getCentroids();
        double WCSS          = clustering.getWCSS();
    }

}