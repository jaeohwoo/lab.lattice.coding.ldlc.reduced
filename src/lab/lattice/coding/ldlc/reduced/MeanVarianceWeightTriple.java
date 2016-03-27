package lab.lattice.coding.ldlc.reduced;

public class MeanVarianceWeightTriple {

	private double _mean;
	private double _variance;
	private double _weight;
	
	public MeanVarianceWeightTriple(double m, double v, double w) {
		
		_mean = m;
		_variance = v;
		_weight = w;
	}
	
	public double getMean() {
		return _mean;
	}
	
	public double getVariance() {
		return _variance;
	}
	
	public double getWeight() {
		return _weight;
	}
	
	public void setWeight(double w) {
		_weight = w;
	}
	
	public void setMean(double m) {
		_mean = m;
	}
	
	public void setVariance(double v) {
		_variance = v;
	}
}
