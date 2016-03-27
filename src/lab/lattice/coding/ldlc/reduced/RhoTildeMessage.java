package lab.lattice.coding.ldlc.reduced;

//convolution of messages
public class RhoTildeMessage extends IntermediateMessage {
	
	public RhoTildeMessage(int fromIndex, int toIndex, double hValue) {
		super(fromIndex, toIndex, hValue);
		MeanVarianceWeightTriple triple = new MeanVarianceWeightTriple(0, 0, 1);
		_tripleList.add(triple);
	}
}
