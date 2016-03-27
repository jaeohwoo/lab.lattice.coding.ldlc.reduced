package lab.lattice.coding.ldlc.reduced;

public class ControlConstants {
	
	public static int RANDOM_SEED = 4;

	// overall BP
	public static int NUMBER_OF_ITERATIONS = 10;
	
	public static int NUMBER_OF_EXTENSIONS = 3;  // odd number, use 3 integers near channel signal (approximation)
	
	public static double MAX_VARIANCE = 1e-10;
	
	// in GaussianMixtureReduction
	public static double THETA = 1e-10;
	public static int MMAX = 2;
}
