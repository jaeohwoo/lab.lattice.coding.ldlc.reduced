package lab.lattice.coding.ldlc.reduced;

import java.io.IOException;

import Jama.Matrix;

public class AWGNLatticeEncoder {

	private int _length;
	private int _degree;
	private Matrix _hMatrix;
	private Matrix _gMatrix;

	public AWGNLatticeEncoder (int length, int degree) throws IOException {
		
		_length = length;
		_degree = degree;
		GenerateParityCheckMatrix g = new GenerateParityCheckMatrix(_length, _degree);
		_hMatrix = g.getParityCheckMatrixSFS();
		_gMatrix = _hMatrix.inverse();
	}
	
	public Signal getAWGNSignal (Signal integerSignalVector, double variance) {
		
		RandomSignalGenerator rsGenerator = new RandomSignalGenerator();
		Signal gNoiseVector = rsGenerator.nextGaussianNoiseVector(_length, variance);
		Signal corruptedSignal = new Signal(_length, _gMatrix.times(integerSignalVector.toMatrix()).plus(gNoiseVector.toMatrix()));
		
		return corruptedSignal;
	}
	
	public Matrix getHMatrix() {
		return _hMatrix;
	}
	
	public Matrix getGMatrix() {
		return _gMatrix;
	}
	
	public void printHMatrix() {
		System.out.println("H Matrix");
		MatrixPrinter.printMatrix(_hMatrix);
	}
	
	public void printGMatrix() {
		System.out.println("G Matrix");
		MatrixPrinter.printMatrix(_gMatrix);
	}

}
