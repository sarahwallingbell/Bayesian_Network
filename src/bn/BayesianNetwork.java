package bn;

import tui.Query;
import tui.Reader;
import util.BitVector;
import util.WeightedSet;
import java.util.Iterator;
import java.util.Set;


/**
* Represents a generic Bayesian Network of boolean random variables
*
* @author Sarah Walling-Bell
* @version March 29, 2019
*
*/
public class BayesianNetwork {

	private Node[] bn; //the bayesian network

	/**
	* Constructs a new Bayesian network with the given nodes.
	*
	* @param nodes The nodes in the Bayesian network
	*
	* @pre The nodes are listed in topological order
	*/
	public BayesianNetwork(Node[] nodes) {
		bn = nodes;
		for(int i = 0; i < bn.length; i++){
		}
	}


	/**
	* Returns the nodes in the Bayesian network
	* @return The nodes in the Bayesian network
	*/
	public Node[] getNodes() {
		return bn;
	}


	/**
	* Approximates the query using direct sampling
	*
	* @param q
	* 			The query
	*
	* @param numSamples
	* 			The number of samples for direct sampling
	* @return
	* 			A probability distribution over the query variables
	*/
	public WeightedSet directSample(Query q, int numSamples) {

		//weighted_set ← {}
		int querySize =  q.queryVariables.size();
		WeightedSet ws = new WeightedSet(querySize); //a map that stores (sample, count) pairs

		// for s=1...numSamples
		for(int j = 0; j < numSamples; j++){

			// sample ← {}
			BitVector sample = new BitVector(querySize);
			int sampleSet = 0;

			for(int i = 0; i < bn.length; i++){ //for node Xi in bn (bn[i])

				//value = randomly sample from p(Xi | parents(Xi))
				boolean value = bn[i].sampleAndSet();

				if(q.queryVariables.contains(bn[i].getName())){
					sample.set(sampleSet, value);
					sampleSet++;
				}
			}

			//Sample now contains the sampled values for all of the query variables
			ws.increment(sample, 1);
		}

		ws.normalizeWeights();
		return ws;
	}


	/**
	* Approximates the query using rejection sampling
	*
	* @param q
	* 			The query
	*
	* @param numSamples
	* 			The number of samples for rejection sampling
	* @return
	* 			A probability distribution over the query variables
	*/

	public WeightedSet rejectionSampling(Query q, int numSamples) {

		//weighted_set ← {}
		int querySize =  q.queryVariables.size();
		WeightedSet ws = new WeightedSet(querySize); //a map that stores (sample, count) pairs

		// for s=1...numSamples
		for(int j = 0; j < numSamples; j++){

			// sample ← {}
			BitVector sample = new BitVector(querySize);
			int sampleSet = 0;
			boolean increment = true;

			for(int i = 0; i < bn.length; i++){ //for node Xi in bn (bn[i])
				increment = true;

				//value = randomly sample from p(Xi | parents(Xi))
				boolean value = bn[i].sampleAndSet();

				if(q.evidenceVariables.contains(bn[i].getName())){
					if(q.evidenceValues.get(bn[i].getName()) != value){
						increment = false;
						break; // Abandon the sample and start over
					}
				}

				//if (Xi == query variable) --> sample.append(value)
				if(q.queryVariables.contains(bn[i].getName())){
					sample.set(sampleSet, value);
					sampleSet++;
				}
			}

			//Sample now contains the sampled values for all of the query variables
			//The sampled values are guaranteed to be consistent with the evidence
			if(increment == true){
				ws.increment(sample, 1);
			}
		}

		ws.normalizeWeights();
		return ws;
	}


	/**
	* Approximates the query using likelihood weighting
	*
	* @param q
	* 			The query
	*
	* @param numSamples
	* 			The number of samples for likelihood weighting
	* @return
	* 			A probability distribution over the query variables
	*/

	public WeightedSet likelihoodWeighting(Query q, int numSamples) {

		int querySize =  q.queryVariables.size();

		//weighted_set ← {}
		WeightedSet ws = new WeightedSet(querySize); //a map that stores (sample, count) pairs

		// for s=1...numSamples
		for(int j = 0; j < numSamples; j++){

			//weight ← 1
			double weight = 1;
			// sample ← {}
			BitVector sample = new BitVector(querySize);
			int sampleSet = 0;

			for(int i = 0; i < bn.length; i++){ //for node Xi in bn (bn[i])

				if(q.evidenceVariables.contains(bn[i].getName())){
					//clamp the node's variable: set the node value to whatever the evidence is.
					//if cloudy then true, otherwise false. evidence variable hashset value for the variable.
					bn[i].setValue(q.evidenceValues.get(bn[i].getName()));


					//How likely is the evidence given what we've sampled so far?
					weight = weight * bn[i].getProbability();
				}
				else{
					//value = randomly sample from p(Xi | parents(Xi))
					boolean value = bn[i].sampleAndSet();

					//if (Xi == query variable) --> sample.append(value)
					if(q.queryVariables.contains(bn[i].getName())){
						sample.set(sampleSet, value);
						sampleSet++;
					}
				}
			}

			//Sample now contains the sampled values for all of the query variables
			ws.increment(sample, weight);
		}

		ws.normalizeWeights();
		return ws;
	}
}
