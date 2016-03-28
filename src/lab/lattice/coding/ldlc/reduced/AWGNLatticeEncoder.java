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
	
	public Signal getEncodedSignal (Signal sourceSignal) {
		
		Signal encodedSignal = new Signal(_length, _gMatrix.times(sourceSignal.toMatrix()));
		
		return encodedSignal;
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
