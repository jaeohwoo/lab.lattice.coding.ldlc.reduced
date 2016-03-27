package lab.lattice.coding.ldlc.reduced;

// sigle gaussian
public class MuMessage extends IntermediateMessage  {

	public MuMessage(int fromIndex, int toIndex, double hValue) {
		super(fromIndex, toIndex, hValue);
		MeanVarianceWeightTriple triple = new MeanVarianceWeightTriple(0, 0, 1);
		_tripleList.add(triple);
	}
	
	public MuMessage(GaussianMixtureMessage gMessage, int fromIndex, int toIndex, double hValue) {
		super(fromIndex, toIndex, hValue);
		_tripleList = gMessage.getTripleList();
	}
}
