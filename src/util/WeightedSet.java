package util;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;

/**
* This is a general purpose class that maps configurations (of n boolean random variables) to a numerical value.
* The meaning of the numerical value depends upon the usage. The numerical value could be:
*
* - A tally (e.g. for direct sampling or rejection sampling)
* - A weight (e.g. for likelihood weighting)
* - A probability (e.g. for a CPT)
*
* Thus, if n=2 and the numerical values were probabilities, the weighted set would contain the following
* configurations:
*
* 				{TT, TF, FT, FF}
*
* and each assignment would be associated with a probability.
*
*
* @author Sarah Walling-Bell
* @version March 29, 2019
*
*/
public class WeightedSet {
	private HashMap<BitVector, Double> mapping;
	private int size;

	//create a hashmap that maps TT or TF etc (Bit Vector), to a double. This will be
	//populated by the input bn file at runtime.

	/**
	* Creates a new weighted set that contains all possible configurations of n boolean random variables
	*
	* @param n
	* 			The number of boolean random variables
	* @pre
	* 			The weighted set contains all possible configurations
	*/
	public WeightedSet(int n) {
		mapping = new HashMap<BitVector, Double>();
		size = n;
		Iterator<BitVector> ai = new AssignmentIterator(n);
		while(ai.hasNext()){
			BitVector config = ai.next();
			mapping.put(config, 0.0);
		}
	}

	/**
	* Adds the event with corresponding weight to the set
	* @param event
	* 				An event
	* @param weight
	* 				A numerical weight
	*/
	public void addEvent(BitVector event, double weight) {
		mapping.put(event, weight);
	}

	/**
	* Increments the weight of the event by the specified amount
	*
	* @param event
	* 				An event
	* @param amount
	* 				An amount by which to increase the weight
	*/
	public void increment(BitVector event, double amount) {
		double currWeight = mapping.get(event);
		double newWeight = currWeight + amount;
		mapping.replace(event, newWeight);
	}

	/**
	* Returns the weight of the particular event
	*
	* @param event
	* 				An event
	* @return The weight of the specified event
	*/
	public double getWeight(BitVector event) {
		return mapping.get(event);
	}

	/**
	* Returns the set of all events
	* @return The set of all events
	*/
	public Set<BitVector> getEvents() {
		return mapping.keySet();
	}

	/**
	* Normalizes the weights so that they sum to 1
	*/
	public void normalizeWeights() {

		//get the sum of all of the weights in the CPT and save as the normalization constant
		double normConstant = 0.0;
		for(BitVector b : mapping.keySet()){
			normConstant += mapping.get(b);
		}

		double oldWeight, newWeight;
		for(BitVector b : mapping.keySet()){
			oldWeight = getWeight(b);
			newWeight = oldWeight/normConstant;
			mapping.replace(b, newWeight);
		}
	}

	/**
	* Print weighted set contents
	*/
	public void printCPT(){
		for(BitVector b : mapping.keySet()){
			double weight = getWeight(b);
		}
	}

	/**
	* Return size of weighted set
	*/
	public int getSize(){
		return mapping.size();
	}
}
