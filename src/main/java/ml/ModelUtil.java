package ml;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ModelUtil {
    public static int predict(MultiLayerNetwork model, NativeImageLoader loader, BufferedImage square) throws IOException {
        INDArray image;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(square, "bmp", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        image = loader.asRowVector(is);

        INDArray input = Nd4j.create(1, square.getHeight() * square.getWidth() * 3).addRowVector(image);
        input.putRow(0, image);
        int[] predict = model.predict(input);
        return predict[0];
    }

}
