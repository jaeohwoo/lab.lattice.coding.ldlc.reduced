package lab.lattice.coding.ldlc.reduced;

import java.util.ArrayList;

public class GaussianMixtureMessage {

	protected ArrayList<MeanVarianceWeightTriple> _tripleList;
	protected int _size;
	
	public GaussianMixtureMessage(int size) {
		_size = size;
		_tripleList = new ArrayList<MeanVarianceWeightTriple> (size);
	}
	
	public GaussianMixtureMessage(double mean, double variance) {
		_size = 1;
		_tripleList = new ArrayList<MeanVarianceWeightTriple> (1);
		MeanVarianceWeightTriple triple = new MeanVarianceWeightTriple(mean, variance, 1);
		_tripleList.add(triple);
	}
	
	public GaussianMixtureMessage(int size, double[] means, double[] variances, double[] weights) {
		_size = size;	
		_tripleList = new ArrayList<MeanVarianceWeightTriple> (size);
		for (int i = 0; i < _size; i++) {
			MeanVarianceWeightTriple triple = new MeanVarianceWeightTriple(means[i], variances[i], weights[i]);
			_tripleList.add(triple);
		}
		this.normalizeWegiths();
	}
	
	public GaussianMixtureMessage(int size, ArrayList<MeanVarianceWeightTriple> tripleList) {
		
		_size = size;	
		_tripleList = tripleList;
		this.normalizeWegiths();
	}
	
	public int getSize() {
		return _size;
	}
	
	public double getMean(int index) {
		return _tripleList.get(index).getMean();
	}
	
	public double getVariance(int index) {
		return _tripleList.get(index).getVariance();
	}
	
	public double getWeight(int index) {
		return _tripleList.get(index).getWeight();
	}

	private void normalizeWegiths() {
		
		double sum = 0;
		for (int i = 0; i < _size; i++) {
			sum += this.getWeight(i);
		}
		for (int i = 0; i < _size; i++) {
			_tripleList.get(i).setWeight(this.getWeight(i) / sum);
		}
	}
	
	public ArrayList<MeanVarianceWeightTriple> getTripleList() {
		return _tripleList;
	}
}
