package lab.lattice.coding.ldlc.reduced;

public class ControlConstants {
	
	public static long RANDOM_SEED = System.currentTimeMillis();

	// overall BP
	public static int NUMBER_OF_ITERATIONS = 50;
	
	public static int NUMBER_OF_EXTENSIONS = 3;  // odd number, use 3 integers near channel signal (approximation)
	
//	public static double MAX_VARIANCE = 1e-3;
	
	// in GaussianMixtureReduction
	public static double THETA = 1e-10;
	public static int MMAX = 2;
}
