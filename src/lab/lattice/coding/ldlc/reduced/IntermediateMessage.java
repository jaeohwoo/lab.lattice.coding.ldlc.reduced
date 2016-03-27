package lab.lattice.coding.ldlc.reduced;

public abstract class IntermediateMessage extends GaussianMixtureMessage  {
	
	protected int _checkNodeIndex;
	protected int _variableNodeIndex;
	
	protected double _hValue;
	
	public IntermediateMessage(int fromIndex, int toIndex, double hValue) {
		super(1);
		_checkNodeIndex = fromIndex;
		_variableNodeIndex = toIndex;
		_hValue = hValue;
	}
	
	public void setFromIndex(int index) {
		_checkNodeIndex = index;
	}
	
	public void setToIndex(int index) {
		_variableNodeIndex = index;
	}
	
	public void setHValue(double hValue) {
		_hValue = hValue;
	}
	
	public void setMean(double m) {
		_tripleList.get(0).setMean(m);
	}
	
	public void setVariance(double v) {
		_tripleList.get(0).setVariance(v);
	}
	
	public void setWeight(double w) {
		_tripleList.get(0).setWeight(w);
	}
	
	public double getMean() {
		return getMean(0);
	}
	
	public double getVariance() {
		return getVariance(0);
	}
	
	public double getHValue() {
		return _hValue;
	}
	
	public int getCheckNodeIndex() {
		return _checkNodeIndex;
	}
	
	public int getVariableNodeIndex() {
		return _variableNodeIndex;
	}
}
