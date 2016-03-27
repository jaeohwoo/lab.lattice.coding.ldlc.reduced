package lab.lattice.coding.ldlc.reduced;

import java.io.IOException;

public class TestLowDensityLatticeCode {

	public static void main(String[] argc) throws IOException{
		
		int signalLength = 10; //n
		int magicNumber = 3; //d
		double[] sNRdB = {1,2,3,4,5,6};

		AWGNLatticeEncoder awgnEncoder = new AWGNLatticeEncoder(signalLength, magicNumber);
		LatticDecoder latticeDecoder = new LatticDecoder(awgnEncoder.getHMatrix(), signalLength, magicNumber);
//		awgnEncoder.printHMatrix();
//		awgnEncoder.printGMatrix();
		
		System.out.println("det(G) = " + awgnEncoder.getGMatrix().det());
		System.out.println("det(H) = " + awgnEncoder.getHMatrix().det());
		
		RandomSignalGenerator rsGenerator = new RandomSignalGenerator();
//		Signal integerSignalVector = rsGenerator.nextZeroIntegerMessageVector(signalLength);
//		Signal integerSignalVector = rsGenerator.nextIntegerMessageVector(signalLength);
		Signal integerSignalVector = rsGenerator.nextSignedOneIntegerMessageVector(signalLength);
		System.out.println("Integer Message");
		integerSignalVector.printMessage();
		
		for (int i = 0; i < sNRdB.length; i++) {
			double variance = getVariance(sNRdB[i]);
			System.out.println("Variance = " + variance);
			Signal noisedSignal = awgnEncoder.getAWGNSignal(integerSignalVector, variance);
			System.out.println("AWGN Corrupted Signal");
			noisedSignal.printMessage();
			Signal decodedSignal = latticeDecoder.decode(noisedSignal, variance);
			System.out.println("AWGN Decoded Signal");
			decodedSignal.printMessage();
			Signal naiveGuass = new Signal(signalLength, awgnEncoder.getHMatrix().times(noisedSignal.toMatrix()));
			System.out.println("Naive Guess");
			naiveGuass.printMessage();
		}
	}
	
	private static double getVariance(double sNRdB) {
		
		double variance = 1.0 / (2* Math.PI * Math.E * Math.pow(10, sNRdB/10.0));
		return variance;
	}
}
