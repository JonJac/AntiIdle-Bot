package ml;

import org.apache.log4j.BasicConfigurator;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FishingClassifier {
    public static final Map<String, Integer> folderMap = Map.of(
            "noHitRed", 0,
            "noHitEmpty", 1,
            "hitRed", 2,
            "hitEmpty", 3);

    public static final Set<Integer> stuffToClick = Set.of(2, 3);

    private static final String RESOURCES_FOLDER_PATH = "fishing";

    private static final int HEIGHT = 30;
    private static final int WIDTH = 216;

    private static int N_SAMPLES_TRAINING = 349;
    private static int N_SAMPLES_TESTING = 349;

    private static final int N_OUTCOMES = folderMap.size();
    private static long t0 = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {
        N_SAMPLES_TESTING = getFilesCount(new File(RESOURCES_FOLDER_PATH));
        N_SAMPLES_TRAINING = N_SAMPLES_TESTING;

        //BasicConfigurator.configure();

        t0 = System.currentTimeMillis();
        System.out.print(RESOURCES_FOLDER_PATH + "/");
        DataSetIterator dataSetIterator = getDataSetIterator(RESOURCES_FOLDER_PATH + "/", N_SAMPLES_TRAINING);

        MultiLayerNetwork model = buildModel(dataSetIterator);

        model.save(new File("fishing.model"));

    }

    public static MultiLayerNetwork getModel() throws IOException {
        BasicConfigurator.configure();

        t0 = System.currentTimeMillis();
        System.out.print(RESOURCES_FOLDER_PATH + "/");
        DataSetIterator dataSetIterator = getDataSetIterator(RESOURCES_FOLDER_PATH + "/", N_SAMPLES_TRAINING);

        return buildModel(dataSetIterator);
    }

    private static MultiLayerNetwork buildModel(DataSetIterator dsi) throws IOException {

        int rngSeed = 123;
        int nEpochs = 2;

        System.out.print("Build Model...");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed)
                .updater(new Nesterovs(0.006, 0.9))
                .l2(1e-4).list()
                .layer(new DenseLayer.Builder()
                        .nIn(HEIGHT * WIDTH * 3).nOut(1000).activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(1000).nOut(N_OUTCOMES).activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        //Print score every 500 interaction
        model.setListeners(new ScoreIterationListener(500));

        System.out.print("Train Model...");
        model.fit(dsi);

        //Evaluation
        DataSetIterator testDsi = getDataSetIterator(RESOURCES_FOLDER_PATH + "/", N_SAMPLES_TESTING);
        System.out.print("Evaluating Model...");
        Evaluation eval = model.evaluate(testDsi);
        System.out.print(eval.stats());

        long t1 = System.currentTimeMillis();
        double t = (double) (t1 - t0) / 1000.0;
        System.out.print("\n\nTotal time: " + t + " seconds");

        return model;
    }

    public static int getFilesCount(File file) {
        File[] files = file.listFiles();
        int count = 0;
        for (File f : files)
            if (f.isDirectory())
                count += getFilesCount(f);
            else
                count++;

        return count;
    }

    private static DataSetIterator getDataSetIterator(String folderPath, int nSamples) throws IOException {
        try {
            File folder = new File(folderPath);
            File[] digitFolders = folder.listFiles();

            NativeImageLoader nativeImageLoader = new NativeImageLoader(HEIGHT, WIDTH); //28x28
            ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(0, 1); //translate image into seq of 0..1 input values

            INDArray input = Nd4j.create(new int[]{nSamples, HEIGHT * WIDTH * 3}); //*3
            INDArray output = Nd4j.create(new int[]{nSamples, N_OUTCOMES});

            int n = 0;
            //scan all 0 to 9 digit subfolders
            for (File digitFolder : digitFolders) {
                int labelDigit = folderMap.get(digitFolder.getName());
                File[] imageFiles = digitFolder.listFiles();

                for (File imgFile : imageFiles) {
                    INDArray img = nativeImageLoader.asRowVector(imgFile);
                    scaler.transform(img);
                    input.putRow(n, img);
                    output.put(n, labelDigit, 1.0);
                    n++;
                }
            }//End of For-loop

            //Joining input and output matrices into a dataset
            DataSet dataSet = new DataSet(input, output);
            //Convert the dataset into a list
            List<DataSet> listDataSet = dataSet.asList();
            //Shuffle content of list randomly
            Collections.shuffle(listDataSet, new Random(System.currentTimeMillis()));
            int batchSize = 10;

            //Build and return a dataset iterator
            DataSetIterator dsi = new ListDataSetIterator<DataSet>(listDataSet, batchSize);
            return dsi;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    } //End of DataIterator Method
}