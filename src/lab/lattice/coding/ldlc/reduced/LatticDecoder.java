package lab.lattice.coding.ldlc.reduced;

import java.util.ArrayList;

import Jama.Matrix;

public class LatticDecoder {

	private Matrix _hMatrix;
	private int _magicNumber;
	private int _signalLength;

	public LatticDecoder(Matrix hMatrix, int signalLength, int magicNumber) {
		_hMatrix = hMatrix;
		_signalLength = signalLength;
		_magicNumber = magicNumber;
	}

	public Signal decode(Signal channelSignal, double channelVariance) {
		
		NodeNeighborMessageManager nodeManager = new NodeNeighborMessageManager(_hMatrix, _magicNumber);

		// initialize
		ArrayList<Node> nodeList = new ArrayList<Node>(); 
		for (int i = 0; i < _signalLength; i++) {
			Node node = new Node(i, channelSignal.get(i), channelVariance, nodeManager);
			nodeList.add(node);
			
			ArrayList<MuMessage> muList = nodeManager.getCheckNodeConnectedMuMessageList(i);
			for (int j = 0; j < muList.size(); j++) {
				MuMessage mu = muList.get(j);
				mu.setMean(channelSignal.get(mu.getVariableNodeIndex()));
				mu.setVariance(channelVariance);
				mu.setWeight(1);
			}
		}
		
		// BP iterations
		for (int n = 0; n < ControlConstants.NUMBER_OF_ITERATIONS; n++) {
	
			// 1-step Forward Belief Propagation
			for (int i = 0; i < _signalLength; i++) {
				Node node = nodeList.get(i);
				node.updateCheckNode();		
			}
			
			// 1-step Backward Belief Propagation
			for (int j = 0; j < _signalLength; j++) {
				Node node = nodeList.get(j);
				node.updateVariableNode();
			}	
		}
		
		// D. Hard Decisions p 661
		double[] intermediateDecodedVector = new double [_signalLength];
		for (int i = 0; i < _signalLength; i++) {
			Node node = nodeList.get(i);
			intermediateDecodedVector[i] = node.getIntermediateDecision();
		}
		Matrix xTilde = new Matrix (intermediateDecodedVector, _signalLength);
		Matrix bHat = _hMatrix.times(xTilde);
		Signal decodedMessage = new Signal(_signalLength, bHat);
		
		return decodedMessage;
	}
}
