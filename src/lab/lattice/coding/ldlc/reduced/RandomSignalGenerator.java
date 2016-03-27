package lab.lattice.coding.ldlc.reduced;

import java.util.Random;

public class RandomSignalGenerator {
	
	private Random _r;
	private int maxInt = 1000;
	
	public RandomSignalGenerator() {
		_r = new Random(ControlConstants.RANDOM_SEED);
	}
	
	public Signal nextIntegerMessageVector(int length) {
		
		double[] b = new double [length];
		
		for (int i = 0; i < length; i++) {
			int randomSign = -1;
			if (_r.nextBoolean()) {
				randomSign = 1;
			}
			b[i] = _r.nextInt(maxInt) * randomSign;
		}
		
		return new Signal(length, b);
	}
	
	public Signal nextSignedOneIntegerMessageVector(int length) {
		
		double[] b = new double [length];
		
		for (int i = 0; i < length; i++) {
			int randomSign = -1;
			if (_r.nextBoolean()) {
				randomSign = 1;
			}
			b[i] = 1 * randomSign;
		}
		
		return new Signal(length, b);
	}
	
	public Signal nextZeroIntegerMessageVector(int length) {
		
		double[] b = new double [length];
		
		for (int i = 0; i < length; i++) {
			b[i] = 0;
		}
		
		return new Signal(length, b);
	}
	
	public Signal nextGaussianNoiseVector(int length, double variance) {
		
		double[] b = new double [length];
		
		for (int i = 0; i < length; i++) {
			b[i] = _r.nextGaussian() * Math.sqrt(variance);
		}

		return new Signal(length, b);
	}


}
