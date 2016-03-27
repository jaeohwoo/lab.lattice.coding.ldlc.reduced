package lab.lattice.coding.ldlc.reduced;

import java.util.ArrayList;
import java.util.HashMap;

import Jama.Matrix;

public class NodeNeighborMessageManager {
	
	private static HashMap<String,RhoTildeMessage> _rhoTildeMessageMap;
	private static HashMap<String,MuMessage> _muMessageMap;
	
	// convention i : check node
	// convention j : variable node
	private ArrayList<int[]> _checkNodeNeighborList; //w.r.t. check node, check<-variable
	private ArrayList<int[]> _variableNodeNeighborList; //w.r.t. variable node, check->variable

	public NodeNeighborMessageManager(Matrix hMatrix, int magicNumber) {
		
		int rowSize = hMatrix.getRowDimension();
		int columnSize = hMatrix.getColumnDimension();
		
		_rhoTildeMessageMap = new HashMap<String,RhoTildeMessage>();
		_muMessageMap = new HashMap<String,MuMessage>();
		
		_checkNodeNeighborList = new ArrayList<int[]>();
		for (int i = 0; i < rowSize; i++) {
			int[] checkNodeNeighbors = new int [magicNumber];
			int iter = 0;
			for (int j = 0; j < columnSize; j++) {
				double valueH = hMatrix.get(i, j);
				if (valueH != 0) {
					checkNodeNeighbors[iter] = j;
					iter++;
					
					RhoTildeMessage rhoTildeMessageIJ = new RhoTildeMessage(i, j, valueH);
					MuMessage muMessageIJ = new MuMessage(i, j, valueH);
					
					_rhoTildeMessageMap.put(this.keyGenerator(i,j), rhoTildeMessageIJ);
					_muMessageMap.put(this.keyGenerator(i,j), muMessageIJ);
				}
			}
			_checkNodeNeighborList.add(checkNodeNeighbors);
		}
		
		_variableNodeNeighborList = new ArrayList<int[]>(); 
		for (int j = 0; j < columnSize ; j++) {
			int[] variableNodeNeighbors = new int [magicNumber];
			int iter = 0;
			for (int i = 0; i < rowSize; i++) {
				double valueH = hMatrix.get(i, j);
				if (valueH != 0) {
					variableNodeNeighbors[iter] = i;
					iter++;
				}
			}
			_variableNodeNeighborList.add(variableNodeNeighbors);
		}
	}
	
	// RhoTilde : check <- variable
	public ArrayList<RhoTildeMessage> getCheckNodeConnectedRhoTildeMessageList(int nodeIndexI) {
		
		int size = _checkNodeNeighborList.get(nodeIndexI).length;
		
		ArrayList<RhoTildeMessage> list = new ArrayList<RhoTildeMessage>();
		for (int j = 0; j < size; j++) {
			list.add(_rhoTildeMessageMap.get(
					this.keyGenerator(nodeIndexI, _checkNodeNeighborList.get(nodeIndexI)[j])));
		}
		return list;
	}
	// RhoTilde : check -> variable
	public ArrayList<RhoTildeMessage> getVariableNodeConnectedRhoTildeMessageList(int nodeIndexJ) {
	
		int size = _variableNodeNeighborList.get(nodeIndexJ).length;
		
		ArrayList<RhoTildeMessage> list = new ArrayList<RhoTildeMessage>();
		for (int i = 0; i < size; i++) {
			list.add(_rhoTildeMessageMap.get(
					this.keyGenerator(_variableNodeNeighborList.get(nodeIndexJ)[i],nodeIndexJ)));
		}
		return list;
	}
	
	// Mu : check <- variable
	public ArrayList<MuMessage> getCheckNodeConnectedMuMessageList(int nodeIndexI) {
		
		int size = _checkNodeNeighborList.get(nodeIndexI).length;
		
		ArrayList<MuMessage> list = new ArrayList<MuMessage>();
		for (int j = 0; j < size; j++) {
			list.add(_muMessageMap.get(
					this.keyGenerator(nodeIndexI, _checkNodeNeighborList.get(nodeIndexI)[j])));
		}
		return list;
	}
	
	// Mu : check -> variable
	public ArrayList<MuMessage> getVariableNodeConnectedMuMessageList(int nodeIndexJ) {
		
		int size = _variableNodeNeighborList.get(nodeIndexJ).length;
		
		ArrayList<MuMessage> list = new ArrayList<MuMessage>();
		for (int i = 0; i < size; i++) {
			list.add(_muMessageMap.get(
					this.keyGenerator(_variableNodeNeighborList.get(nodeIndexJ)[i], nodeIndexJ)));
		}
		return list;
	}
	
	public void updateRhoTildeHashMap(String key, RhoTildeMessage message) {
		
		_rhoTildeMessageMap.replace(key, message);
	}
	
	public void updateMuHashMap(String key, MuMessage message) {
		
		_muMessageMap.replace(key, message);
	}
	
	private String keyGenerator(int i, int j) {
		return Integer.toString(i) + "," + Integer.toString(j);
	}
}
