package lab.lattice.coding.ldlc.reduced;

import Jama.Matrix;

public class MatrixPrinter {

	public static void printMatrix(Matrix mat) {
		
		int m = mat.getRowDimension();
		int n = mat.getColumnDimension();
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(mat.get(i,j) + " "); 
			}
			System.out.println();
		}
	}
}
