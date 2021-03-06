package SVM;

import java.io.File;

import MLP.Utility;

/*
 * Trains the classifier on one part of the MNIST data set and tests it on the other and vice versa
 * One SVM classifier is trained for each of the unique class pairs
 * and the predictions are generated using a voting system
 * The results are displayed using the metric accuracy score which is the percentage of correct predictions over total predictions
 */
public class Main {
	private static int sizeOfDataset = 2810;
	private static int numberOfClassPairs = 45;
	private static int pairOfClasses = 2;
	private static Classifier[] classifiers = new Classifier[numberOfClassPairs];
	private static int[] votingArray = new int[numberOfClassPairs];
	private static int[] predictions = new int[sizeOfDataset];
	private static double[][] trainData, testData;
	private static int[] trainLabels, testLabels;
	
	public int[] getPredictions() {
		return predictions;
	}

	public static void trainSingleClassifier(int classifierCounter) {
		int epochsLimit = 50000;
		int patienceEpochs = 5000;// iterations interval to check if the cost function value is reducing
		// train the data until the EPOCHS limit or patience EPOCHS
		
		// store the cost function value to test for patience EPOCHS
		double oldCostFunction = classifiers[classifierCounter].computeCostFunction();
		for (int epochs = 0; epochs < epochsLimit; epochs++) {
			// choose to either train using the batched or sochastic gradient descent method
			classifiers[classifierCounter].batchGradientDescent();
			if (epochs % patienceEpochs == 0 && epochs != 0) {
				if ((int) classifiers[classifierCounter].computeCostFunction() >= (int) oldCostFunction) {
					System.out.println("EPOCHS: " + epochs);
					break;
				} else {
					oldCostFunction = classifiers[classifierCounter].computeCostFunction();
				}
			}
		}
	}

	// make predictions using each SVM classifier and store the most frequently
	// occured value as the predicted value for that particular input data
	public static void predict() {
		for (int datasetIndex = 0; datasetIndex < sizeOfDataset; datasetIndex++) {
			for (int classifierIndex = 0; classifierIndex < numberOfClassPairs; classifierIndex++) {
				votingArray[classifierIndex] = classifiers[classifierIndex].predict(testData[datasetIndex]);
			}
			// choose the most frequent answer as the prediction
			predictions[datasetIndex] = Utility.mostFrequent(votingArray);
		}
	}

	// calculate and display accuracy score
	public static void accuracy() {
		int correctPredictions = 0;
		for (int datasetIndex = 0; datasetIndex < sizeOfDataset; datasetIndex++) {
			if (testLabels[datasetIndex] == predictions[datasetIndex]) {
				correctPredictions++;
			}
		}

		// display accuracy percentage and total correct predictions out of total predictions
		System.out.println(correctPredictions+" out of "+sizeOfDataset);
		System.out.println("Accuracy: " + (double) correctPredictions / sizeOfDataset * 100);
	}
	public static void SVM_ApplicationRunner(File file1, File file2) {
		// define data and labels
		trainData = Utility.readFile(file1);
		testData = Utility.readFile(file2);
		if (file1.getName().equals("dataset1")) {
			trainLabels = Utility.getLabels1();
			testLabels = Utility.getLabels2();
		} else if (file1.getName().equals("dataset2")) {
			trainLabels = Utility.getLabels2();
			testLabels = Utility.getLabels1();
		}

		int classifierCounter = 0;
		int secondClassFirstElement = 1;
		int highestValueFirstClass = 9;
		int highestValueSecondClass = 10;
		int[] classes = new int[pairOfClasses];

		// train a classifer for each of the unique class pairs starting from 0/1 and
		// ending at 8/9
		for (int firstClass = 0; firstClass < highestValueFirstClass; firstClass++) {
			for (int secondClass = secondClassFirstElement; secondClass < highestValueSecondClass; secondClass++) {
				// set the two classes
				classes[0] = firstClass;
				classes[1] = secondClass;

				// instantiate and train a new SVM classifier for each unique pair
				classifiers[classifierCounter] = new Classifier(trainData, trainLabels, classes);
				trainSingleClassifier(classifierCounter);
				
				classifierCounter++;
				System.out.println("------------ " + firstClass + "/" + secondClass + " -------------");
				System.out.println("Classifier Counter: " + classifierCounter);// max 44
			}
			secondClassFirstElement++;
		}
		
		predict();

		accuracy();
	}
	public static void main(String[] args) {
		// set train and test files
		final String trainFile = "dataset1";
		final String testFile = "dataset2";
		
		SVM_ApplicationRunner(new File(trainFile),new File(testFile));

	}

}
