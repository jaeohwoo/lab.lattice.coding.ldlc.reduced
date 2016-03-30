package lab.lattice.coding.ldlc.reduced;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import Jama.Matrix;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;

public class GenerateParityCheckMatrix {
	
	private int _length;
	private int _degree;
	
	public GenerateParityCheckMatrix(int length, int degree) {
		_length = length;
		_degree = degree;
	}
	
	public Matrix getParityCheckMatrixSFS() throws IOException {
		
		// Initialization
		Random r = new Random(/*ControlConstants.RANDOM_SEED*/);
		int[][] PMatrix = new int[_degree][_length];
		for (int i = 0; i < _degree; i++) {
			int[] perm = getRandomPermutation(_length);
			for (int j = 0; j < _length; j++) {
				PMatrix[i][j]= perm[j];
			}
		}
		
		int c = 0;
		int loopless_columns = 0;
		
		//loop removal
		while(loopless_columns < _length) {
			int changed_permutation = -1;
			
			boolean loop2_exists = false;
			for (int i = 0; (i < _degree) && !loop2_exists; i++) {
				for (int k = i + 1; (k < _degree) && !loop2_exists; k++) {
					if (PMatrix[i][c]==PMatrix[k][c]) {
						changed_permutation = i;
						loop2_exists = true;
					}
				}
			}
			boolean loop4_exists = false;
			int first_row = -1;
			if (!loop2_exists) {
				for (int j = 0; (j < _length) && !loop4_exists; j++) {
					int sameCount = 0;
					if (j == c) {
						continue;
					}
					for (int i = 0; (i < _degree) && !loop4_exists; i++) {
						for (int k = 0; (k < _degree) && !loop4_exists; k++) {
							if (PMatrix[i][c] == PMatrix[k][j]) {
								sameCount++;
							}
							if (sameCount == 1) {
								first_row = i;
							}
							if (sameCount == 2) {
								changed_permutation = first_row;
								loop4_exists = true;
							}
						}
					}
				}
			}
			
			if (changed_permutation >= 0) {
				// remove loop
				int random_column = r.nextInt(_length);
				int temp = PMatrix[changed_permutation][c];
				PMatrix[changed_permutation][c] = PMatrix[changed_permutation][random_column];
				PMatrix[changed_permutation][random_column] = temp;
				loopless_columns = 0;
			}
			else {
				// no loop
				loopless_columns = loopless_columns + 1;
			}
			c++;
			
			if (c >= _length) {
				c = 0;
			}
		}
		
		//final H
		double[] h = new double [_degree];
		h[0] = 1;
		for (int i = 1; i < _degree; i++) {
			h[i] = 1.0/Math.sqrt(_degree);
		}
		
		Matrix HMatrix = new Matrix (_length, _length);
		for (int i = 0; i < _degree; i++) {
			for (int j = 0; j < _length; j++) {
				int randomSign = -1;
				if (r.nextBoolean()) {
					randomSign = 1;
				}
				HMatrix.set(PMatrix[i][j], j, h[i]*randomSign);
			}
		}
		
		HMatrix = HMatrix.times(1.0/Math.pow(Math.abs(HMatrix.det()), 1.0/_length));
		
		return HMatrix;
	}

	public Matrix getParityCheckMatrix10() throws IOException {
		
		File f = new File("H_N10_d3.mat");
		
		MatFileReader reader = new MatFileReader(f);
		
		String str = new String("H");
		MLDouble hArray = (MLDouble) reader.getMLArray(str);
		int m = hArray.getM();
		int n = hArray.getN();
		System.out.println("M :" + m + " N :" +n);
		
		Matrix hMatrix = new Matrix (hArray.getArray());
		return hMatrix;
	}
	
	private int[] getRandomPermutation (int length){
		
		Random r = new Random(/*ControlConstants.RANDOM_SEED*/);

	    // initialize array and fill it with {0,1,2...}
	    int[] array = new int[length];
	    for(int i = 0; i < array.length; i++)
	        array[i] = i;

	    for(int i = 0; i < length; i++){

	        // randomly chosen position in array whose element
	        // will be swapped with the element in position i
	        // note that when i = 0, any position can chosen (0 thru length-1)
	        // when i = 1, only positions 1 through length -1
	                    // NOTE: r is an instance of java.util.Random
	        int ran = i + r.nextInt (length-i);

	        // perform swap
	        int temp = array[i];
	        array[i] = array[ran];
	        array[ran] = temp;
	    }                       
	    return array;
	}
}
