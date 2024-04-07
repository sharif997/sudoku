package global.network.sob.ksa.out;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MNISTBackpropagation {

    static List<double[]> trainingData = new ArrayList<>();
    static List<double[]> validationData = new ArrayList<>();
    static List<double[]> testingData = new ArrayList<>();

    public static void main(String[] args) {
        // Load and preprocess MNIST data
        loadMNISTData("path/to/train.csv");
        Collections.shuffle(trainingData);
        Collections.shuffle(validationData);
        Collections.shuffle(testingData);

        // Define parameters
        int inputSize = 28 * 28;
        int hiddenSize = 64; // Change the number of neurons in the hidden layer
        int outputSize = 10; // 10 classes (digits 0-9)
        int batchSize = 32;
        double learningRate = 0.001;
        int epochs = 50;

        // Create ANN
        NeuralNetwork ann = new NeuralNetwork(inputSize, hiddenSize, outputSize);

        // Train ANN
        for (int epoch = 1; epoch <= epochs; epoch++) {
            for (int i = 0; i < trainingData.size(); i += batchSize) {
                List<double[]> batch = trainingData.subList(i, Math.min(i + batchSize, trainingData.size()));
                ann.trainBatch(batch, learningRate);
            }

            double validationAccuracy = calculateAccuracy(ann, validationData);
            System.out.println("Epoch " + epoch + " Validation Accuracy: " + validationAccuracy);
        }

        // Test ANN
        double testAccuracy = calculateAccuracy(ann, testingData);
        System.out.println("Test Accuracy: " + testAccuracy);
    }

    public static void loadMNISTData(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                double[] image = new double[values.length - 1];
                for (int i = 1; i < values.length; i++) {
                    image[i - 1] = Double.parseDouble(values[i]) / 255.0; // Normalize pixel values
                }
                int label = Integer.parseInt(values[0]);
                if (trainingData.size() < 0.7 * 42000) {
                    trainingData.add(image);
                } else if (trainingData.size() < 0.8 * 42000) {
                    validationData.add(image);
                } else {
                    testingData.add(image);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double calculateAccuracy(NeuralNetwork ann, List<double[]> data) {
        int correct = 0;
        for (double[] input : data) {
            double[] output = ann.predict(input);
            int predictedLabel = findMaxIndex(output);
            if (predictedLabel == (int) input[0]) {
                correct++;
            }
        }
        return (double) correct / data.size() * 100;
    }

    public static int findMaxIndex(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
