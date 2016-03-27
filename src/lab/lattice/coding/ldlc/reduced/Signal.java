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
}
