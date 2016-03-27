package lab.lattice.coding.ldlc.reduced;

import java.util.ArrayList;

public class Node {
	
	private int _nodeIndex;
	
	private double _channelSignal;
	private double _channelVariance;
	
	private NodeNeighborMessageManager _manager;
	private GaussianMixtureMessage _alphaD;
	
	public Node(int nodeIndex, double channelSignal, double channelVariance,
			NodeNeighborMessageManager manager) {
		
		_nodeIndex = nodeIndex;
		
		_channelSignal = channelSignal;
		_channelVariance = channelVariance;
		
		_manager = manager;
	}
	
	// p.660 follows B. Check node
	public void updateCheckNode() {
		
		ArrayList<RhoTildeMessage> rhoTildeList = _manager.getCheckNodeConnectedRhoTildeMessageList(_nodeIndex);
		ArrayList<MuMessage> muList = _manager.getCheckNodeConnectedMuMessageList(_nodeIndex);
		
		int checkNodeNeighborSize = rhoTildeList.size();
		
		double meanSum = 0;
		double varianceSum = 0;
		for (int j = 0; j < checkNodeNeighborSize; j++) {
			MuMessage mu = muList.get(j);
			meanSum += mu.getHValue() * mu.getMean();
			varianceSum += mu.getHValue() * mu.getHValue() * mu.getVariance();
		}
		
		for (int j = 0; j < checkNodeNeighborSize; j++) {

			// evaluate newMean and new Variance
			MuMessage messageMu = muList.get(j);
			double mean = meanSum - messageMu.getHValue() * messageMu.getMean();
			double variance = varianceSum - messageMu.getHValue() * messageMu.getHValue() * messageMu.getVariance();
			mean = - mean / messageMu.getHValue();
			variance = variance / (messageMu.getHValue() * messageMu.getHValue());
			
			// update rhoTilde
			RhoTildeMessage rhoTilde = rhoTildeList.get(j);
			rhoTilde.setMean(mean);
			rhoTilde.setVariance(variance);
			rhoTilde.setWeight(1);
		}
	}
	
	// p.660 follows C. Variable Node
	public void updateVariableNode() {
		
		ArrayList<RhoTildeMessage> rhoTildeList = _manager.getVariableNodeConnectedRhoTildeMessageList(_nodeIndex);
		ArrayList<MuMessage> muList = _manager.getVariableNodeConnectedMuMessageList(_nodeIndex);
		
		int rhoTildeSize = rhoTildeList.size();
		int muSize = muList.size();
		
		// periodic extension
		ArrayList<GaussianMixtureMessage> rhoList = new ArrayList<GaussianMixtureMessage> (0);
		for (int i = 0; i < rhoTildeSize; i++) {
			double[] extendedMeans = new double [ControlConstants.NUMBER_OF_EXTENSIONS];
			double[] extendedVariances = new double [ControlConstants.NUMBER_OF_EXTENSIONS];
			double[] extendedWeights = new double [ControlConstants.NUMBER_OF_EXTENSIONS];
			
			RhoTildeMessage message = rhoTildeList.get(i);
			for (int j = 0; j < ControlConstants.NUMBER_OF_EXTENSIONS; j++) {
				extendedMeans[j] = message.getMean() + ((j - (ControlConstants.NUMBER_OF_EXTENSIONS/2)) / message.getHValue());
				extendedVariances[j] = message.getVariance();
				extendedWeights[j] = 1;
			}
			
			GaussianMixtureMessage rhoMessage = new GaussianMixtureMessage (ControlConstants.NUMBER_OF_EXTENSIONS, extendedMeans, extendedVariances, extendedWeights);
			rhoList.add(rhoMessage);
		}
		
		// iterate alphaMessages (index + 1);
		ArrayList<GaussianMixtureMessage> alphaList = new ArrayList<GaussianMixtureMessage> ();
		GaussianMixtureMessage alpha = new GaussianMixtureMessage(_channelSignal, 2 * _channelVariance);
		alphaList.add(alpha);
		for (int i = 0; i < muSize; i++) {
			GaussianMixtureMessage alphaTilde = productOfMixtures(alpha, rhoList.get(i));
			alpha = GaussianMixtureReduction(alphaTilde);
			alphaList.add(alpha);
		}
		_alphaD = alpha;
		
		// iterate betaMessage
		ArrayList<GaussianMixtureMessage> betaList = new ArrayList<GaussianMixtureMessage> ();
		GaussianMixtureMessage beta = new GaussianMixtureMessage(_channelSignal, 2 * _channelVariance);
		betaList.add(beta);
		for (int i = muSize - 2; i >= 0; i--) {
			GaussianMixtureMessage betaTilde = productOfMixtures(beta, rhoList.get(i+1));
			beta = GaussianMixtureReduction(betaTilde);
			betaList.add(0, beta);
		}
		
		// final assignments
		for (int i = 0; i < muSize; i++) {
			GaussianMixtureMessage temp = productOfMixtures(alphaList.get(i), betaList.get(i));
			GaussianMixtureMessage temp2 = this.MomentMatching(temp);
			MuMessage muMessage = muList.get(i);
			muMessage.setMean(temp2.getMean(0));
			muMessage.setVariance(temp2.getVariance(0));
			muMessage.setWeight(1);
		}
	}
	
	// product of mixtures
	private GaussianMixtureMessage productOfMixtures(GaussianMixtureMessage a, GaussianMixtureMessage b) {
		
		int sizeA = a.getSize();
		int sizeB = b.getSize();
		int sizeNew = sizeA * sizeB;
		
		double[] newMeans = new double [sizeNew];
		double[] newVariances = new double [sizeNew];
		double[] newWeights = new double [sizeNew];
		
		for (int i = 0; i < sizeA; i++) {
			for (int j = 0; j < sizeB; j++) {
				int currentIndex = sizeB * i + j;
				newVariances[currentIndex] = Math.max(1.0 / (1.0 / a.getVariance(i) + 1.0 / b.getVariance(j)), ControlConstants.MAX_VARIANCE);
				newMeans[currentIndex] = newVariances[currentIndex] * (a.getMean(i) / a.getVariance(i) + b.getMean(j) / b.getVariance(j));
				newWeights[currentIndex] = (a.getWeight(i)*b.getWeight(j)) / (Math.sqrt(2*Math.PI*(a.getVariance(i) + b.getVariance(j)))) 
						* Math.exp(-0.5*(a.getMean(i) - b.getMean(j))*(a.getMean(i) - b.getMean(j)) / (a.getVariance(i)+b.getVariance(j)));
				
				if (Double.isNaN(newWeights[currentIndex])) {
					System.out.println("ERROR3");
				}
				
				if (Double.isInfinite(newWeights[currentIndex])) {
					System.out.println("ERROR4");
				}
			}
		}
		
		GaussianMixtureMessage message = new GaussianMixtureMessage(sizeNew, newMeans, newVariances, newWeights);
//		message.normalizeWegiths();
		
		return message;
	}
	
	// p 2492
	private GaussianMixtureMessage GaussianMixtureReduction(GaussianMixtureMessage a) {
		
		//initialize
		ArrayList<MeanVarianceWeightTriple> currentSearchList = (ArrayList<MeanVarianceWeightTriple>) (a.getTripleList()).clone();
		int Mc = currentSearchList.size();
		double thetac = minGQL(currentSearchList);
		
		// greedy combining
		while (thetac < ControlConstants.THETA || Mc > ControlConstants.MMAX) {
			
			int[] argMinIndices = argMinGQL(currentSearchList);
			MeanVarianceWeightTriple t1 = currentSearchList.get(argMinIndices[0]);
			MeanVarianceWeightTriple t2 = currentSearchList.get(argMinIndices[1]);
			ArrayList<MeanVarianceWeightTriple> tempList = new ArrayList<MeanVarianceWeightTriple> (2);
			tempList.add(t1);
			tempList.add(t2);
			GaussianMixtureMessage gm = new GaussianMixtureMessage(2, tempList);
			MeanVarianceWeightTriple t3 = (MomentMatching(gm)).getTripleList().get(0);
			currentSearchList.remove(argMinIndices[0]);
			currentSearchList.remove(argMinIndices[1] - 1);
			currentSearchList.add(t3);
			Mc = currentSearchList.size();
			new GaussianMixtureMessage(Mc, currentSearchList); // doing normalization
			thetac = minGQL(currentSearchList);
		}
		
		GaussianMixtureMessage message = new GaussianMixtureMessage(currentSearchList.size(), currentSearchList);
		message.normalizeWegiths();
		
		if (Double.isNaN(message.getWeight(0))) {
			System.out.println("ERROR2");
		}
		
		return message;
	}
	
	private double minGQL(ArrayList<MeanVarianceWeightTriple> searchList) {
		
		int size = searchList.size();
		
		double min = Double.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				double gql = GQL(searchList.get(i).getMean(),searchList.get(i).getVariance(),searchList.get(i).getWeight(),
						searchList.get(j).getMean(),searchList.get(j).getVariance(),searchList.get(j).getWeight());
				if (gql < min) {
					min = gql; 
				}
			}
		}
		return min;
	}
	
	private int[] argMinGQL(ArrayList<MeanVarianceWeightTriple> searchList) {
		
		int[] indices = new int [2];
		int size = searchList.size();
		
		double min = Double.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				double gql = GQL(searchList.get(i).getMean(),searchList.get(i).getVariance(),searchList.get(i).getWeight(),
						searchList.get(j).getMean(),searchList.get(j).getVariance(),searchList.get(j).getWeight());
				if (gql < min) {
					min = gql; 
					indices[0] = i;
					indices[1] = j;
				}
				
//				System.out.println(gql);
			}
		}
		
		if (indices[1] == 0) {
			System.out.println("ERROR1");
		}
		
		return indices;
	}
	
	private double GQL(double mean1, double variance1, double weight1, double mean2, double variance2, double weight2) {
		
		double m = weight1 * mean1 + weight2 * mean2;
		double v = weight1 * (mean1 * mean1 + variance1) + weight2 * (mean2 * mean2 + variance2) - (m*m);
		double gql = 1 / (2 * Math.sqrt(Math.PI * v)) + weight1*weight1/(2*Math.sqrt(Math.PI*variance1)) + weight2*weight2/(2*Math.sqrt(Math.PI*variance2)) 
				- ((2*weight1) / (Math.sqrt(2*Math.PI*(v+variance1))))*Math.exp(-(m-mean1)*(m-mean1)/(2*(v+variance1)))
				- ((2*weight2) / (Math.sqrt(2*Math.PI*(v+variance2))))*Math.exp(-(m-mean2)*(m-mean2)/(2*(v+variance2)))
				+ ((2*weight1*weight2) / (Math.sqrt(2*Math.PI*(variance1 + variance2))))*Math.exp(-(mean1-mean2)*(mean1-mean2)/(2*(variance1+variance2)));
		
		return gql;
	}
	
	private GaussianMixtureMessage MomentMatching(GaussianMixtureMessage a) {
		
		a.normalizeWegiths();
		int size = a.getSize();
		
		double mean = 0;
		for (int i = 0; i < size; i++) {
			mean += a.getWeight(i) * a.getMean(i);
		}
		double variance = 0;
		for (int i = 0; i < size; i++) {
			variance += a.getWeight(i) * ( a.getMean(i) * a.getMean(i) + a.getVariance(i) );
		}
		variance -= mean * mean;
		
		variance = Math.max(variance, ControlConstants.MAX_VARIANCE);
		
		return new GaussianMixtureMessage(mean, variance);
	}
	
	public double getIntermediateDecision() {
		
		GaussianMixtureMessage sqrtY = new GaussianMixtureMessage(_channelSignal, 2 * _channelVariance);
		GaussianMixtureMessage temp = MomentMatching(productOfMixtures(_alphaD, sqrtY));
		return temp.getMean(0);
	}
}
