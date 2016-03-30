package lab.lattice.coding.ldlc.reduced;

import Jama.Matrix;

public class Signal {

	private int _length;
	private double[] _message;

	public Signal(int length) {
		
		_length = length;
	}
	
	public Signal(int length, double[] message) {
		
		_length = length;
		_message = message;
	}
	
	public Signal(int length, Matrix message) {
		
		_length = length;
		_message = message.getRowPackedCopy();
	}
	
	public int size() {
		
		return _length;
	}
	
	public double get(int index) {
		return _message[index];
	}
	
	public Matrix toMatrix() {
		
		return new Matrix(_message, _length);
	}
	
	public void printMessage() {
		
//		System.out.println("Message :");
		int size = Math.min(_length, 10);
		for (int i = 0; i < size; i++) {
			System.out.print(_message[i] + "  ");
		}
		System.out.println();
	}
	
	public Signal getRoundedSignal() {
		
		double[] roundedMessage = new double [_length];
		
		for (int i = 0; i < _length; i++) {
			roundedMessage[i] = Math.round(_message[i]);
		}
		
		return new Signal(_length, roundedMessage);
	}
	
	public double compareRate(Signal sig) {
		
		double rate = 0;
		double tolerance = 1e-10;
		if (sig.size() != _length) {
			return -1;
		}
		
		for (int i = 0 ; i < _length; i++) {
			if (Math.abs( sig.get(i) -_message[i]) > tolerance) {
				rate = rate + 1.0;
			}
		}
		rate = rate / _length;
		
		return rate;
	}
	
	public Signal applyGaussianNoise(double variance) {
		
		RandomSignalGenerator rsGenerator = new RandomSignalGenerator();
		Signal gNoiseVector = rsGenerator.nextGaussianNoiseVector(_length, Math.sqrt(variance));
		Signal corruptedSignal = new Signal(_length, this.toMatrix().plus(gNoiseVector.toMatrix()));
		
		return corruptedSignal;
	}
	
	public double getVariance() {
		
		double sum = 0;
		double sqsum = 0;
		for (int i = 0; i < _length; i++) {
			sum += _message[i];
			sqsum += _message[i] * _message[i];
		}
		
		sum /= _length;
		sqsum /= _length;
		
		return (sqsum - (sum*sum));
	}
}
