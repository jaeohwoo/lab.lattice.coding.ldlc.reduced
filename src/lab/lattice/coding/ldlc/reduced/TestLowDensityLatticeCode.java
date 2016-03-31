package lab.lattice.coding.ldlc.reduced;

import java.io.IOException;

public class TestLowDensityLatticeCode {

	public static void main(String[] argc) throws IOException{
		
		int signalLength = 100; //n
		int magicNumber = 3; //d
		
		int sampleSize = 20000;
		double[] sNRdB = {4};

		AWGNLatticeEncoder awgnEncoder = new AWGNLatticeEncoder(signalLength, magicNumber);
		LatticDecoder latticeDecoder = new LatticDecoder(awgnEncoder.getHMatrix(), signalLength, magicNumber);
//		awgnEncoder.printHMatrix();
//		awgnEncoder.printGMatrix();
		
		System.out.println("det(G) = " + awgnEncoder.getGMatrix().det());
		System.out.println("det(H) = " + awgnEncoder.getHMatrix().det());
		
		RandomSignalGenerator rsGenerator = new RandomSignalGenerator();
		
		for (int i = 0; i < sNRdB.length; i++) {
			double variance = getVariance(sNRdB[i]);
			System.out.println("Variance = " + variance);
			
//			Signal integerSignalVector = rsGenerator.nextZeroIntegerMessageVector(signalLength);
//			Signal integerSignalVector = rsGenerator.nextIntegerMessageVector(signalLength);
			Signal integerSignalVector = rsGenerator.nextSignedOneIntegerMessageVector(signalLength);
		
			Signal encodedSignal = awgnEncoder.getEncodedSignal(integerSignalVector);
			
			double ser = 0;
			double naiveSer = 0;
			double averageSampleNoise = 0;
			for (int j = 0; j < sampleSize; j++) {
				System.out.println("Iteration Count = " + j);

//				System.out.println("Integer Message");
//				integerSignalVector.printMessage();

				Signal noisedSignal = encodedSignal.applyGaussianNoise(variance);
				averageSampleNoise += noisedSignal.getVariance();
//				System.out.println("Signal Sample Noise = " + noisedSignal.getVariance());
//				System.out.println("AWGN Corrupted Signal");
//				noisedSignal.printMessage();
				
				Signal decodedSignal = latticeDecoder.decode(noisedSignal, variance);
//				System.out.println("AWGN Decoded Signal");
//				decodedSignal.printMessage();
				
				Signal naiveGuass = new Signal(signalLength, awgnEncoder.getHMatrix().times(noisedSignal.toMatrix()));
//				System.out.println("Naive Guess");
//				naiveGuass.getRoundedSignal().printMessage();

				double temp = decodedSignal.getRoundedSignal().compareRate(integerSignalVector);
				if (temp > 0) {
					System.out.println("Each Error Rate = " + temp);
				}
				ser = ser + temp;
				naiveSer = naiveSer + naiveGuass.getRoundedSignal().compareRate(integerSignalVector);
//				System.out.println("Each Decoding Bit Error Rate = " + temp);
			}
			
			System.out.println("SER = " + ser / sampleSize);
			System.out.println("Naive SER = " + naiveSer / sampleSize);
			System.out.println("Sample Noise = " + averageSampleNoise / sampleSize);
		}
	}
	
	private static double getVariance(double sNRdB) {
		
		double variance = 1.0 / (2* Math.PI * Math.E * Math.pow(10, sNRdB/10.0));
		return variance;
	}
}
