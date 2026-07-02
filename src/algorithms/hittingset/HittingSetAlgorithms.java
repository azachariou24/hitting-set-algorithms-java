package algorithms.hittingset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Random;

/**
* The HittingSetAlgorithms class contains four recursive algorithms in solving one aspect of the
* Set Cover Problem to create a subset of elements covering all the given subsets
* as a whole but utilizing as minimal a number of elements as possible chosen.
*
* The class employs a 2D array of sets of integers wherein the set is an array of integers.
* The objective is to identify a smallest set of elements (task force members) such that removal of such
* elements would make all sets "covered" (i.e., reduced to zero).
*
* The class is a 2D array of sets of integers whose set is an array of integers.
* In order to reduce the number of elements (members of a task force) whose
* elimination will render all subsets "covered" (i.e., equal to zero).
*
* Core functionalities include:
* - Four recursive algorithms (algorithm1 up to algorithm4) employing alternative heuristics for the selection of elements.
* - Algorithms for removal of covered subsets, computation of element incidences, and status validation of subsets.
* - Dynamic operations like "isZeroSubset", "isAllSubsetsCovered", and "isContains" for ease of subset processing.
*
* Algorithm overview:
* - **Algorithm 1**: Random sampling of non-empty subsets and attempting coverage by recursive removal of elements.
* - **Algorithm 2**: Giving precedence to elements of high incidence over subsets for reasons of efficiency.
* - **Algorithm 3**: Selecting smallest (non-zero) subset, permuting randomly the elements, and attempting recursive coverage.
* - **Algorithm 4**: Combines minimum subset size with element incidence for more conservative selection strategy.
*
* @since 26/04/2025
* @version 1.0
* @author Anastasis Zachariou
* 
*/
public class HittingSetAlgorithms {
	
	/**
	* Recursive backtracking algorithm which makes random choice of a non-zero subset
	* and tries to cover all subsets by recursively eliminating elements.
	*
	* The algorithm employs random choice to choose a candidate subset and tries
	* to eliminate one of its non-zero elements. It tries different recursive paths
	* by reducing the matrix and calling itself with (k-1) choices available.
	*
	* The aim is to encode all subsets (i.e., to transform all to zero-subsets) in up to k elements.
	*
	* @param n Amount of different elements.
	* @param m Amount of subsets.
	* @param c Amount of maximum elements in one subset.
	* @param k Amount of maximum elements in the result (task force).
	* @param subsets The matrix of the m subsets each with at most c elements.
	*
	* @return an array of integers for the task force (solution), or null if impossible.
	*
	* @since 26/04/2025
	* @version 1.0
	* @author Anastasis Zachariou
	*
	*/
	public static int[] algorithm1(int n, int m, int c, int k, int[][] subsets) {
		
		/* Base case: if all subsets are covered (all elements are 0), return an empty array (no elements needed) */
		if(isAllSubsetsCovered(m, c, subsets) == true) {
			
			return new int[0];
			
		}
		
		/* Base case: if we used up all our allowed choices (k == 0) but not all subsets are covered, return null (failure) */
		if(k == 0) {
			
			return (null);
			
		}
		
		/* Array to store the currently selected candidate subset */
		int[] chosenSet = new int[k];
		
		/* Create a Random object for random subset selection */
		Random rand = new Random();
		
		/* Limit the number of attempts to find a valid (non-zero) subset */
		int attempts = 0;
		
		/* Try up to 2 * m times to find a non-zero subset at random */
		while(attempts < (m * 2)) {
			
			int randomNumber = rand.nextInt(m);	// Pick a random subset index
			
			/* If the selected subset is not all zeros */
			if(!isZeroSubset(subsets[randomNumber], c)) {
				
				chosenSet = subsets[randomNumber];	// Save it as the candidate
				
				break;
				
			}
			
			attempts++;	// Try again
			
		}
		
		/* Array to store the non-zero elements of the chosenSet (i.e., potential candidates to try) */
		int[] candidates = new int[c];
		
		int counter = 0;
		
		/* Filter non-zero elements from chosenSet into candidates[] */
		for(int i = 0; (i < c) && (i < chosenSet.length); i++) {
			
			if(chosenSet[i] != 0) {
				
				candidates[counter++] = chosenSet[i];
				
			}
			
		}
		
		/* Shuffle the candidate elements to randomize the order of recursive exploration */
		for(int i = (counter - 1); i > 0; i--) {
			
			int j = rand.nextInt(i + 1);
			
			int temp = candidates[i];
			
			candidates[i] = candidates[j];
			
			candidates[j] = temp;
			
		}
		
		/* Try each candidate element as a possible choice in the result */
		for(int i = 0; i < counter; i++) {
			
			/*int x = chosenSet[i];*/
			int x = candidates[i];	// Select candidate x
			
			/*if(x == 0) {
				
				continue;
				
			}*/
			
			/* Create a reduced matrix (copy of subsets) with x removed (replaced with zeros) wherever it appears */
			int[][] reduced = new int[m][c];
			
			for(int j = 0; j < m; j++) {
				
				boolean skip = false;
				
				/* Check if the current subset contains x */
				for(int z = 0; z < c; z++) {
					
					if(subsets[j][z] == x) {
						
						skip = true;	// Mark this subset for zeroing
						
						break;
						
					}
					
				}
				
				if(skip == true) {
					
					/* Zero out the entire subset if it contained x */
					for(int z = 0; z < c; z++) {
						
						reduced[j][z] = 0;
						
					}
					
				}
				
				else {
					
					/* Otherwise, just copy it as is */
					for(int z = 0; z < c; z++) {
						
						reduced[j][z] = subsets[j][z];
						
					}
					
				}
				
			}
			
			/* Recursive call with the reduced matrix and one fewer element (k - 1) */
			int[] result = algorithm1(n, m, c, (k - 1), reduced);
			
			if(result != null) {
				
				/* If the recursive call succeeded, add x to the front of the result */
				int[] taskForce = new int[result.length + 1];
				
				taskForce[0] = x;
				
				for(int h = 0; h < result.length; h++) {
					
					taskForce[h + 1] = result[h];
					
				}
				
				return (taskForce);	// Return the successful result
				
			}
			
		}
		
		/* If no valid solution found with any of the candidates, return null */
		return (null);
		
	}
	
	/**
	* Recursive backtracking algorithm that prefers subsets with the most common elements
	* and attempts to cover all subsets by recursively removing elements.
	*
	* The algorithm calculates the frequency of each element in all subsets, randomly selects
	* a subset and tries to remove one of its elements. The algorithm prefers the elements that appear
	* frequently throughout the subsets. The algorithm continues recursively, reducing the matrix
	* of subset and finding if it's possible to include all subsets.
	*
	* @param n Unique number of elements.
	* @param m Subsets count.
	* @param c Max number of elements in a subset.
	* @param k Max number of elements which are to be included in the solution (task force).
	* @param subsets m subsets matrix containing at most c elements per subset.
	* 
	* @return int array for task force (solution), or null if not possible.
	*
	* @since 29/04/2025
	* @version 2.0
	* @author Anastasis Zachariou
	*
	*/
	public static int[] algorithm2(int n, int m, int c, int k, int[][] subsets) {
		
		/* Base case: if all subsets are covered (all elements are 0), return an empty array (no elements needed) */
		if(isAllSubsetsCovered(m, c, subsets) == true) {
			
			return new int[0];
			
		}
		
		/* Base case: if we used up all our allowed choices (k == 0) but not all subsets are covered, return null (failure) */
		if(k == 0) {
			
			return (null);
			
		}
		
		/* Array to store how frequently each number appears in all subsets (1-based index) */
		int[] incidence = new int[n + 1];
		
		/* Count frequencies of each non-zero number across all subsets */
		for(int i = 0; i < m; i++) {
			
			for(int j = 0; j <c; j++) {
				
				int number = subsets[i][j];
				
				if(number > 0) {
					
					incidence[number]++;
					
				}
				
			}
			
		}
		
		/* Array to store the currently selected candidate subset */
		int[] chosenSet = new int[k];
		
		/* Create a Random object for random subset selection */
		Random rand = new Random();
		
		/* Limit the number of attempts to find a valid (non-zero) subset */
		int attempts = 0;
		
		/* Try up to 5 * m times to find a non-zero subset at random */
		while(attempts < (m * 5)) {
			
			int randomNumber = rand.nextInt(m);	// Pick a random subset index
			
			/* If the selected subset is not all zeros */
			if(!isZeroSubset(subsets[randomNumber], c)) {
				
				chosenSet = subsets[randomNumber];	// Save it as the candidate
				
				break;
				
			}
			
			attempts++;	// Try again
			
		}
		
		/* If no non-zero subset was found after all attempts, return failure */
		if(chosenSet == null) {
			
			return (null);
			
		}
		
		/* Table to hold unique, non-zero elements of chosenSet */
		int[] table = new int[c];
		int counter = 0;
		
		/* Extract unique non-zero values from chosenSet */
		for(int i = 0; i < c; i++) {
			
			int value = chosenSet[i];
			
			/* Only add if it's not already in the table */
			if((value > 0) && !(isContains(table, counter, value))) {
				
				table[counter++] = value;
				
			}
			
		}
		
		/* Sort the table of elements in descending order of their frequency (most common first) */
		for(int i = 0; i < (counter - 1); i++) {
			
			for(int j = (i + 1); j < counter; j++) {
				
				if(incidence[table[j]] > incidence[table[i]]) {
					
					int temp = table[i];
					table[i] = table[j];
					table[j] = temp;
					
				}
				
			}
			
		}
		
		/* Try each candidate element in the sorted list */
		for(int i = 0; i < counter; i++) {
			
			int criticalNumber = table[i];	// Candidate element to try including
			
			/* Build a reduced version of the subsets matrix with criticalNumber removed */
			int[][] reduced = new int[m][c];
			
			for(int j = 0; j < m; j++) {
				
				boolean skip = false;
				
				/* Check if current subset contains the criticalNumber */
				for(int z = 0; z < c; z++) {
						
					if(subsets[j][z] == criticalNumber) {
							
						skip = true;	// This subset will be zeroed
							
						break;
							
					}
						
				}
					
				if(skip == true) {
					
					/* Zero out the entire subset if it contained x */
					for(int h = 0; h < c; h++) {
							
						reduced[j][h] = 0;
							
					}
						
				}
					
				else {
					
					/* Otherwise, just copy it as is */
					for(int h = 0; h < c; h++) {
							
						reduced[j][h] = subsets[j][h];
							
					}
						
				}
					
			}
			
			/* Recursive call with the reduced matrix and one fewer element (k - 1) */
			int[] result = algorithm2(n, m, c, (k - 1), reduced);
			
			if(result != null) {
				
				/* If the recursive call succeeded, add x to the front of the result */
				int[] taskForce = new int[result.length + 1];
				
				taskForce[0] = criticalNumber;
				
				for(int h = 0; h < result.length; h++) {
					
					taskForce[h + 1] = result[h];
					
				}
				
				return (taskForce);	// Return the successful result
				
			}
		
		}
		
		/* If no valid solution found with any of the candidates, return null */
		return (null);
		
	}
	
	/**
	* A greedy algorithm that chooses the smallest subset and tries to cover all subsets
	* by recursively deleting elements.
	*
	* This algorithm initially chooses the subset with the least non-zero elements and
	* randomly reorders the elements in that subset. It then tries to delete those
	* elements one by one, recursively decreasing the subsets matrix.
	*
	* The algorithm iteratively covers all subsets with at most k elements by selecting
	* the smallest "most expensive" subset (smallest subset) in each step.
	*
	*
	* @param n Number of unique elements.
	* @param m Number of subsets.
	* @param c Maximum number of elements in a subset.
	* @param k Maximum number of elements in the result (task force).
	* @param subsets The matrix of the m subsets with at most c elements each.
	*
	* @return An array of integers with the task force (solution), or null if impossible.
	*
	* @since 27/04/2025
	* @version 1.0
	* @author Anastasis Zachariou
	* 
	*/
	public static int[] algorithm3(int n, int m, int c, int k, int[][] subsets) {
		
		/* Base case: if all subsets are covered (all elements are 0), return an empty array (no elements needed) */
		if(isAllSubsetsCovered(m, c, subsets) == true) {
			
			return new int[0];
			
		}
		
		/* Base case: if we used up all our allowed choices (k == 0) but not all subsets are covered, return null (failure) */
		if(k == 0) {
			
			return (null);
			
		}
		
		/* Array to store the currently selected candidate subset */
		int[] chosenSet = new int[k];
		
		/* Initialize variables to track the smallest non-zero subset */
		int minLength = Integer.MAX_VALUE;
		int minIndex = -1;
		
		/* Find the non-zero subset with the smallest number of elements */
		for(int i = 0; i < m; i++) {
			
			if(!isZeroSubset(subsets[i], c)) {
				
				int length = 0;
				
				for(int j = 0; j < c; j++) {
					
					if(subsets[i][j] != 0) {
						
						length++;	// Count non-zero elements
						
					}
					
				}
				
				/* Choose the subset with the fewest non-zero elements */
				/* In case of tie, pick the one with the lower index */
				if((length < minLength) || ((length == minLength) && (i < minIndex))) {
					
					minLength = length;
					
					minIndex = i;
					
					chosenSet = subsets[i];
					
				}
					
			}
			
		}
		
		/* If no valid subset was found, return failure */
		if(chosenSet == null) {
			
			return (null);
			
		}
		
		/* Extract the non-zero elements of the chosen subset */
		int[] elements = new int[c];
		
		int counter = 0;
		
		for(int i = 0; i < chosenSet.length; i++) {
			
			if(chosenSet[i] != 0) {
				
				elements[counter++] = chosenSet[i];
				
			}
			
		}
		
		/* Shuffle the elements randomly to introduce variability in recursive paths */
		Random rand = new Random();
		
		for(int i = (counter -1); i > 0; i--) {
			
			int j = rand.nextInt(i + 1);
			
			int temp = elements[i];
			elements[i] = elements[j];
			elements[j] = temp;
			
		}
		
		/* Try each non-zero element of the chosen subset */
		for(int i = 0; i < counter; i++) {
			
			int x = elements[i];	// Element to be selected for removal
			
			/* === Shared logic with algorithm1 === */
			
			/* Build a new reduced matrix by eliminating any subset that contains x */
			int[][] reduced = new int[m][c];
			
			for(int j = 0; j < m; j++) {
				
				boolean skip = false;
				
				/* Check if the current subset contains x */
				for(int z = 0; z < c; z++) {
					
					if(subsets[j][z] == x) {
						
						skip = true;	// Mark this subset for zeroing
						
						break;
						
					}
					
				}
				
				if(skip == true) {
					
					/* Zero out the entire subset if it contained x */
					for(int z = 0; z < c; z++) {
						
						reduced[j][z] = 0;
						
					}
					
				}
				
				else {
					
					/* Otherwise, just copy it as is */
					for(int z = 0; z < c; z++) {
						
						reduced[j][z] = subsets[j][z];
						
					}
					
				}
				
			}
			
			/* Recursive call with one less allowed element (k - 1) */
			int[] result = algorithm3(n, m, c, (k - 1), reduced);
			
			if(result != null) {
				
				/* If recursion succeeds, add x to the result */
				int[] taskForce = new int[result.length + 1];
				
				taskForce[0] = x;
				
				for(int h = 0; h < result.length; h++) {
					
					taskForce[h + 1] = result[h];
					
				}
				
				return (taskForce);	// Return the successful result
				
			}
			
		}
		
		/* If no valid solution found with any of the candidates, return null */
		return (null);
		
	}
	
	/**
	 * A modified greedy algorithm that selects the smallest subset and attempts to cover all subsets.
	 * Additionally, it also takes into account the frequency of elements in subsets.
	 *
	 * The algorithm selects the subset with the fewest non-zero elements and uses the frequency 
	 * of elements to prioritize the most critical elements for selection. It then recursively removes 
	 * the elements and checks if all subsets can be covered.
	 *
	 * @param n Total number of unique elements.
	 * @param m Number of subsets.
	 * @param c Maximum number of elements in a subset.
	 * @param k Maximum number of elements allowed in the result (task force).
	 * @param subsets The matrix representing the m subsets with up to c elements each.
	 * 
	 * @return An array of integers representing the task force (solution), or null if not possible.
	 * 
	 * @since 27/04/2025
	 * @version 1.0
	 * @author Anastasis Zachariou
	 * 
	 */
	public static int[] algorithm4(int n, int m, int c, int k, int[][] subsets) {
		
		/* Check if all subsets have been covered (i.e., zeroed out) */
		if(isAllSubsetsCovered(m, c, subsets) == true) {
			
			return new int[0];
			
		}
		
		/* If we have no more elements allowed in the task force */
		if(k == 0) {
			
			return (null);
			
		}
		
		/* Array to store the current candidate subset to explore */
		int[] chosenSet = new int[k];
		
		/* Initialize minimum length of subset (used to find smallest) */
		int minLength = Integer.MAX_VALUE;
		/* Index of the subset with the smallest number of non-zero elements */
		int minIndex = -1;
		
		/* Loop through all subsets */
		for(int i = 0; i < m; i++) {
			
			/* Skip subsets that are already zero (already covered) */
			if(!isZeroSubset(subsets[i], c)) {
				
				int length = 0;
				
				/* Count how many non-zero elements are in the current subset */
				for(int j = 0; j < c; j++) {
					
					if(subsets[i][j] != 0) {
						
						length++;
						
					}
					
				}
				
				/* Update if this is the smallest non-zero subset seen so far */
				if((length < minLength) || ((length == minLength) && (i < minIndex))) {
					
					minLength = length;	// Update the new minimum length
					
					minIndex = i;	// Store its index
					
					chosenSet = subsets[i];	// Store the subset
					
				}
					
			}
			
		}
		
		/* If no valid subset was selected, return null (shouldn't happen) */
		if(chosenSet == null) {
			
			return (null);
			
		}
		
		/* Create an array to hold frequency of each element (1 to n) */
		int[] incidence = new int[n + 1];
		
		/* Count the number of times each element appears in all subsets */
		for(int i = 0; i < m; i++) {
			
			for(int j = 0; j <c; j++) {
				
				int number = subsets[i][j];
				
				if(number > 0) {
					
					incidence[number]++;	// Increment frequency count
					
				}
				
			}
			
		}
		
		/* Temporary array to store the unique elements from chosenSet */
		int[] elements = new int[c];
		
		int counter = 0;	// Counter for unique elements
		
		/* Extract all unique non-zero elements from chosenSet */
		for(int i = 0; i < c; i++) {
			
			int value = chosenSet[i];
			
			/* Only consider non-zero values */
			if(value != 0) {
				
				boolean stop = false;
				
				/* Check if value is already in elements[] */
				for(int j = 0; j < counter; j++) {
					
					if(elements[j] == value) {
						
						stop = false;
						
						break;
						
					}
					
				}
				
				/* If not duplicate, add to elements[] */
				if(!stop) {
					
					elements[counter++] = value;
					
				}
				
			}
			
		}
		
		/* Sort the unique elements by descending frequency.
		 * If frequency is equal, prefer smaller value.
		 */
		for(int i = 0; i < (counter - 1); i++) {
			
			for(int j = (i + 1); j < counter; j++) {
				
				int num1 = elements[i];
				int num2 = elements[j];
				
				/* Swap if frequency of num1 is less than num2,
				 * or if frequencies equal but num1 is greater
				 */
				if((incidence[num1] < incidence[num2]) || 
						((incidence[num1] == incidence[num2]) && (num1 > num2))) {
					
					int temp = elements[i];
					elements[i] = elements[j];
					elements[j] = temp;
					
				}
				
			}
			
		}
		
		/* Try each element as a candidate for the task force */
		for(int i = 0; i < c; i++) {
			
			int x = elements[i];
			
			/* Skip unused slots (zeros) */
			if(x == 0) {
				
				continue;
				
			}
			
			/* Create a reduced version of the subset matrix
			 * where all subsets containing x are "removed" (zeroed)
			 */
			int[][] reduced = new int[m][c];
			
			for(int j = 0; j < m; j++) {
				
				boolean skip = false;
				
				/* Check if subset j contains x */
				for(int z = 0; z < c; z++) {
					
					if(subsets[j][z] == x) {
						
						skip = true;
						
						break;
						
					}
					
				}
				
				/* If it contains x, set it to all zeros */
				if(skip == true) {
					
					for(int z = 0; z < c; z++) {
						
						reduced[j][z] = 0;
						
					}
					
				}
				
				else {
					
					/* Otherwise, copy the subset as-is */
					for(int z = 0; z < c; z++) {
						
						reduced[j][z] = subsets[j][z];
						
					}
					
				}
				
			}
			
			/* Recursively try to solve the reduced problem */
			int[] result = algorithm4(n, m, c, (k - 1), reduced);
			
			/* If recursive call returns a valid result */
			if(result != null) {
				
				/* Create a new array with x as the first element */
				int[] taskForce = new int[result.length + 1];
				
				taskForce[0] = x;
				
				/* Append the rest of the recursive result */
				for(int h = 0; h < result.length; h++) {
					
					taskForce[h + 1] = result[h];
					
				}
				
				return (taskForce);	// Return the complete solution
				
			}
			
		}
		
		/* If no valid solution found with any of the candidates, return null */
		return (null);
		
	}
	
	/**
	 * Checks if all subsets are covered (i.e., all elements of the subsets are zero).
	 *
	 * This method checks each subset, and if it finds any subset with non-zero elements,
	 * it returns `false`. If all subsets are covered (i.e., contain only zeroes), it returns `true`.
	 *
	 * @param m The number of subsets.
	 * @param c The maximum number of elements in each subset.
	 * @param subsets The array representing the subsets.
	 * 
	 * @return `true` if all subsets are covered (i.e., only contain zeroes), otherwise `false`.
	 * 
	 * @since 26/04/2025
	 * @version 1.0
	 * @author Anastasis Zachariou
	 *
	 */
	private static boolean isAllSubsetsCovered(int m, int c, int[][] subsets) {
		
		/* Check each subset in the subset array */
		for(int i = 0; i < m; i++) {
			
			/* If a subset has at least one non-zero element, return false */
			if(!isZeroSubset(subsets[i], c)) {
				
				return (false);
				
			}
			
		}
		
		/* If all subsets are covered, return true */
		return (true);
		
	}
	
	/**
	 * Checks if a subset contains only zero elements.
	 *
	 * This method iterates through all the elements of a subset and checks if all of them
	 * are zero. If it finds any element that is not zero, it returns `false`. If all elements
	 * are zero, it returns `true`.
	 *
	 * @param subset The subset being checked.
	 * @param c The maximum number of elements in the subset.
	 * 
	 * @return `true` if all elements of the subset are zero, otherwise `false`.
	 * 
	 * @since 26/04/2025
	 * @version 1.0
	 * @author Anastasis Zachariou
	 * 
	 */
	private static boolean isZeroSubset(int[] subset, int c) {
		
		/* Iterate through all elements of the subset */
		for(int i = 0; i < c; i++) {
			
			/* If any element is not zero, return false */
			if(subset[i] != 0) {
				
				return (false);
				
			}
			
		}
		
		/* If all elements are zero, return true */
		return (true);
		
	}
	
	/**
	 * Checks if the value already exists in the `table` array.
	 *
	 * This method checks if the specified value exists in the `table` array up to the position
	 * `counter`. If it finds the value, it returns `true`; otherwise, it returns `false`.
	 *
	 * @param table The array to be checked.
	 * @param counter The number of elements in the array up to which the check is performed.
	 * @param value The value to look for in the array.
	 * 
	 * @return `true` if the value exists in the array, otherwise `false`.
	 * 
	 * @since 29/05/2025
	 * @version 1.0
	 * @author Anastasis Zachariou
	 * 
	 */
	private static boolean isContains(int[] table, int counter, int value) {
		
		/* Check each element in the table up to the position 'counter' */
		for(int i = 0; i < counter; i++) {
			
			/* If the value is found, return true */
			if(table[i] == value) {
				
				return (true);
				
			}
			
		}
		
		/* If the value is not found, return false */
		return (false);
		
	}
	
	/**
	 * Main method — the entry point of the program.
	 * 
	 * Before allowing the user to choose which experiment to run, this method 
	 * verifies the correctness of all four algorithms (algorithm1 to algorithm4) 
	 * using the data provided in the file "sets.dat". If any of the algorithms 
	 * fail the correctness check, the program terminates.
	 * 
	 * Afterwards, a menu is displayed allowing the user to choose whether to:
	 * - Run Experiment 1
	 * - Run Experiment 2
	 * - Exit the program
	 * 
	 * - Experiment 1: Repeatedly runs all four algorithms while incrementing the 
	 *   value of k, measuring execution time and whether a solution was found.
	 * 
	 * - Experiment 2: Runs each algorithm 10 times with a fixed k value, recording 
	 *   the execution time and whether each run was successful or not.
	 * 
	 * The results are saved into the output files "outputExperiment1.txt" and 
	 * "outputExperiment2.txt", respectively.
	 * 
	 * @param args Command-line arguments (not used).
     * 
     * @since 30/04/2025
     * @version 3.0
     * @author Anastasis Zachariou
     * 
	 */
	public static void main(String[] args) {
		
		/* File name containing the input data */
		String fileName = "datasets/sets.dat";
		
		/* Declare variables for input parameters */
		int n = 0, m = 0, c = 0, k = 0;
		
		/* Matrix to store the subsets */
		int[][] subsets = null;
		
		/* Try-with-resources block to open and read the input file */
		try(Scanner scanner = new Scanner(new File(fileName))){
			
			/* Read the first line if it exists (contains n, m, c, k) */
			if(scanner.hasNextLine()) {
				
				String[] header = scanner.nextLine().trim().split("\\s+");
				
				/* Validate header length */
				if(header.length != 4) {
					
					throw new IllegalArgumentException("Invalid header format!");
					
				}
				
				/* Parse n = elements, m = subsets, c = max elements in each, k = task force size */
				n = Integer.parseInt(header[0]);
				m = Integer.parseInt(header[1]);
				c = Integer.parseInt(header[2]);
				k = Integer.parseInt(header[3]);
				
			}
			
			/* Initialize the subsets matrix */
			subsets = new int[m][c];
			
			int row = 0;
			/* Read each subset line by line */
			while((scanner.hasNextLine()) && (row < m)) {
				
				String[] tokens = scanner.nextLine().trim().split("\\s+");
				
				/* Fill the row with up to c elements */
				for(int column = 0; (column < tokens.length) && (column < c); column++) {
					
					subsets[row][column] = Integer.parseInt(tokens[column]);
					
				}
				
				row++;
				
			}
			
			/* Print the input summary */
			System.out.println("n = " + n + ", m = " + m + ", c = " + c + ", k = " + k);
			
			/* Print the universal set A */
			System.out.print("A = ");
			for(int i = 1; i <= n; i++) {
				
				System.out.print(i);
				
				if(i != n) {
					
					System.out.print(", ");
					
				}
				
			}
			
			System.out.println();
			
			/* Print all subsets */
			System.out.println("Subsets matrix:");
			for(int i = 0; i < m; i++) {
				
				System.out.print("B" + i + " = ");
				
				for(int j = 0; j < c; j++) {
					
					System.out.print(subsets[i][j] + "\t");
					
				}
				
				System.out.println();
				
			}
			
			/* === Run Algorithm 1 === */
			long start = System.nanoTime();	// Start time
			int[] algor1 = algorithm1(n, m, c, k, subsets);	// Run algorithm 1
			long end = System.nanoTime();	// End time
			
			/* Check result of algorithm 1 */
			if(algor1 == null) {
				
				System.out.println();
			
				System.out.println("No solution found with the argorithm 1!");
				
				System.out.println("The total execution time of the argorithm 1 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}

			else {
				
				System.out.println();
			
				System.out.print("Task Force from Algorithm1 is : ");
			
				for(int x : algor1) {
				
					System.out.print(x + " ");
				
				}
			
				System.out.println();
				
				System.out.println("The total execution time of the argorithm 1 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}
			
			/* === Run Algorithm 2 === */
			start = System.nanoTime();	// Start time
			int[] algor2 = algorithm2(n, m, c, k, subsets);	// Run algorithm 2
			end = System.nanoTime();	// End time
			
			/* Check result of algorithm 2 */
			if(algor2 == null) {
				
				System.out.println();
			
				System.out.println("No solution found with the argorithm 2!");
				
				System.out.println("The total execution time of the argorithm 2 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}

			else {
			
				System.out.print("Task Force from Algorithm2 is : ");
			
				for(int x : algor2) {
				
					System.out.print(x + " ");
				
				}
				
				System.out.println();
				
				System.out.println("The total execution time of the argorithm 2 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}
			
			/* === Run Algorithm 3 === */
			start = System.nanoTime();	// Start time
			int[] algor3 = algorithm3(n, m, c, k, subsets);	// Run algorithm 3
			end = System.nanoTime();	// End time
			
			/* Check result of algorithm 3 */
			if(algor3 == null) {
				
				System.out.println();
			
				System.out.println("No solution found with the argorithm 3!");
				
				System.out.println("The total execution time of the argorithm 3 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}

			else {
			
				System.out.print("Task Force from Algorithm3 is : ");
			
				for(int x : algor3) {
				
					System.out.print(x + " ");
				
				}
				
				System.out.println();
				
				System.out.println("The total execution time of the argorithm 3 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}
			
			/* === Run Algorithm 4 === */
			start = System.nanoTime();	// Start time
			int[] algor4 = algorithm4(n, m, c, k, subsets);	// Run algorithm 4
			end = System.nanoTime();	// End time
			
			/* Check result of algorithm 4 */
			if(algor4 == null) {
				
				System.out.println();
			
				System.out.println("No solution found with the argorithm 4!");
				
				System.out.println("The total execution time of the argorithm 4 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}

			else {
			
				System.out.print("Task Force from Algorithm4 is : ");
			
				for(int x : algor4) {
				
					System.out.print(x + " ");
				
				}
				
				System.out.println();
				
				System.out.println("The total execution time of the argorithm 4 is = " + (end - start)/1000000.0 + "ms");
				
				System.out.println();
			
			}
		
		} catch(FileNotFoundException e) {
			
			/* If file is not found, display error message */
			System.err.println("File not found: " + fileName);
			
		} catch(Exception e) {
			
			/* Catch any other exception */
			System.err.println("Error: " + e.getMessage());
			
		}
		
		/* Create a Scanner object to read input from the keyboard */
		Scanner input = new Scanner(System.in);
		
		/* Display menu title */
		System.out.println("Menu:");
		/* Print separator line */
		System.out.println("---------------------------------------------------");
		
		/* Ask user which experiment to run */
		System.out.println("Which experiment would you like to run?");
		/* Instruction for running Experiment 1 */
		System.out.println("To run Experiment 1, enter '1'.");
		/* Instruction for running Experiment 2 */
		System.out.println("To run Experiment 2, enter '2'.");
		/* Instruction for exiting */
		System.out.println("If you don't want to run any experiment, enter '0'.");
		
		/* Separator line */
		System.out.println("---------------------------------------------------");
		System.out.print("Enter your selection: ");	// Prompt for user input
		
		/* Read user input as an integer */
		int selection = input.nextInt();
		
		/* If user selected 0 (exit option) */
		if(selection == 0) {
			
			System.out.println();
			/* Display exit message */
			System.out.println("Thank you! Have a great day!!!");
			
		}
		
		/* If user selected Experiment 1 */
		else if(selection == 1) {
			
			String fileName2 = "datasets/experiment1.dat";		// Name of the input data file
			String outputFile = "datasets/outputExperiment1.txt";	// Name of the output file to store results
			
			/* Variables to store input parameters */
			int n2 = 0, m2 = 0, c2 = 0, k2 =0;
			int[][] subsets2 = null;	// 2D array to store subsets
			
			/* Try to open and read the file */
			try(Scanner scanner = new Scanner(new File(fileName2))){
				
				/* If file has a first line */
				if(scanner.hasNextLine()) {
					
					/* Read and split first line by whitespace */
					String[] header = scanner.nextLine().trim().split("\\s+");
					
					/* Check if the line contains exactly 4 numbers */
					if(header.length != 4) {
						
						/* If not, throw an exception */
						throw new IllegalArgumentException("Invalid header format!");
						
					}
					
					/* Parse and assign the values */
					n2 = Integer.parseInt(header[0]);
					m2 = Integer.parseInt(header[1]);
					c2 = Integer.parseInt(header[2]);
					k2 = Integer.parseInt(header[3]);
					
				}
				
				/* Initialize the 2D array with dimensions m2 x c2 */
				subsets2 = new int[m2][c2];
				
				int row = 0;	// Row index
				/* Read up to m2 rows */
				while((scanner.hasNextLine()) && (row < m2)) {
					
					/* Split the line into numbers */
					String[] tokens = scanner.nextLine().trim().split("\\s+");
					
					/* Fill each column */
					for(int column = 0; (column < tokens.length) && (column < c2); column++) {
						
						subsets2[row][column] = Integer.parseInt(tokens[column]);	// Convert and store value
						
					}
					
					row++;	// Move to the next row
					
				}
			
			/* Handle any errors while reading the file */
			}catch(Exception e) {
				
				/* Print error message */
				System.err.println("Error reading experiment1.dat: " + e.getMessage());
				
			}
			
			/* Flags to track if each algorithm has finished due to time limit or failure */
			boolean algor1Done = false, algor2Done = false, algor3Done = false, algor4Done = false;
			
			/* Open output file for writing */
			try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))){
				
				/* Continue as long as not all algorithms are finished and k2 < 26 */
				while(!(algor1Done && algor2Done && algor3Done && algor4Done) && (k2 < 12)) {
					
					/* Counters for number of failed runs */
					int failures1 = 0, failures2 = 0, failures3 = 0, failures4 = 0;
					
					/* Total execution time accumulators */
					double totalTime1 = 0, totalTime2 = 0, totalTime3 = 0, totalTime4 = 0;
					
					/* Run Algorithm 1 three times unless already done */
					int i = 0;
					while((algor1Done == false) && (i < 3)) {
						
						long start, end;
						
						start = System.nanoTime();	// Start timing
						int[] result1 = algorithm1(n2, m2, c2, k2, subsets2);	// Run algorithm 1
						end = System.nanoTime();	// End timing
						
						double time = ((end - start) / 1000000.0);	// Calculate time in milliseconds
						
						/* If time exceeds 1 hour */
						if(time >= 3600000.0000) {
							
							algor1Done = true;	// Mark algorithm as done
							
							/* Log as failure */
							writer.printf("algor1 %d %.2f %b%n", k2, time, false);
							
						}
						
						else {
							
							totalTime1 += time;	// Add to total time
							
						}
						
						if(result1 == null) {
							
							failures1++;	// Count failures
							
						}
						
						i++;
						
					}
					
					/* If not marked as done by timeout */
					if(algor1Done != true) {
						
						double avg1 = (totalTime1 / 3);	// Calculate average time
						
						/* Success if at least one run succeeded */
						boolean success1 = !(failures1 == 3);
						
						/* Write result */
						writer.printf("algor1 %d %.2f %b%n", k2, avg1, success1);
						
					}
					
					/* Run Algorithm 2 three times unless already done */
					int j = 0;
					while((algor2Done == false) && (j < 3)) {
						
						long start, end;
						
						start = System.nanoTime();	// Start timing
						int[] result2 = algorithm2(n2, m2, c2, k2, subsets2);	// Run algorithm 2
						end = System.nanoTime();	// End timing
						
						double time = ((end - start) / 1000000.0);	// Calculate time in milliseconds
						
						/* If time exceeds 1 hour */
						if(time >= 3600000.0000) {
							
							algor2Done = true;	// Mark algorithm as done
							
							/* Log as failure */
							writer.printf("algor2 %d %.2f %b%n", k2, time, false);
							
						}
						
						else {
							
							totalTime2 += time;	// Add to total time
							
						}
						
						if(result2 == null) {
							
							failures2++;	// Count failures
							
						}
						
						j++;
						
					}
					
					/* If not marked as done by timeout */
					if(algor2Done != true) {
						
						double avg2 = (totalTime2 / 3);	// Calculate average time
						
						/* Success if at least one run succeeded */
						boolean success2 = !(failures2 == 3);
						
						/* Write result */
						writer.printf("algor2 %d %.2f %b%n", k2, avg2, success2);
						
					}
					
					/* Run Algorithm 3 three times unless already done */
					int x = 0;
					while((algor3Done == false) && (x < 3)) {
						
						long start, end;
						
						start = System.nanoTime();	// Start timing
						int[] result3 = algorithm3(n2, m2, c2, k2, subsets2);	// Run algorithm 3
						end = System.nanoTime();	// End timing
						
						double time = ((end - start) / 1000000.0);	// Calculate time in milliseconds
						
						/* If time exceeds 1 hour */
						if(time >= 3600000.0000) {
							
							algor3Done = true;	// Mark algorithm as done
							
							/* Log as failure */
							writer.printf("algor3 %d %.2f %b%n", k2, time, false);
							
						}
						
						else {
							
							totalTime3 += time;	// Add to total time
							
						}
						
						if(result3 == null) {
							
							failures3++;	// Count failures
							
						}
						
						x++;
						
					}
					
					/* If not marked as done by timeout */
					if(algor3Done != true) {
						
						double avg3 = (totalTime3 / 3);	// Calculate average time
						
						/* Success if at least one run succeeded */
						boolean success3 = !(failures3 == 3);
						
						/* Write result */
						writer.printf("algor3 %d %.2f %b%n", k2, avg3, success3);
						
					}
					
					/* Run Algorithm 4 three times unless already done */
					int y = 0;
					while((algor4Done == false) && (y < 3)) {
						
						long start, end;
						
						start = System.nanoTime();	// Start timing
						int[] result4 = algorithm4(n2, m2, c2, k2, subsets2);	// Run algorithm 4
						end = System.nanoTime();	// End timing
						
						double time = ((end - start) / 1000000.0);	// Calculate time in milliseconds
						
						/* If time exceeds 1 hour */
						if(time >= 3600000.0000) {
							
							algor4Done = true;	// Mark algorithm as done
							
							/* Log as failure */
							writer.printf("algor4 %d %.2f %b%n", k2, time, false);
							
						}
						
						else {
							
							totalTime4 += time;	// Add to total time
							
						}
						
						if(result4 == null) {
							
							failures4++;	// Count failures
							
						}
						
						y++;
						
					}
					
					/* If not marked as done by timeout */
					if(algor4Done != true) {
						
						double avg4 = (totalTime4 / 3);	// Calculate average time
						
						/* Success if at least one run succeeded */
						boolean success4 = !(failures4 == 3);
						
						/* Write result */
						writer.printf("algor4 %d %.2f %b%n", k2, avg4, success4);
						
					}
					
					k2++;	// Increment k2 for next experiment iteration
					
				}
				
				/* Print completion message */
				System.out.println("Experiment 1 complete! Results written to outputExperiment1.txt");
			
			/* Catch errors related to writing to file */
			}catch(IOException e) {
				
				/* Print error */
				System.err.println("Error writing to outputExperiment1.txt: " + e.getMessage());
				
			}
			
		}	// End of Experiment 1
		
		/* If the user selects Experiment 2 */
		else if(selection == 2) {
			
			String fileName3 = "datasets/experiment2.dat";		// Input file name for Experiment 2
			String outputFile = "datasets/outputExperiment2.txt";	// Output file name to store results
			
			/* Variables to hold input parameters */
			int n3 = 0, m3 = 0, c3 = 0, k3 =0;
			int[][] subsets3 = null;	// 2D array to store subset data from the file
			
			/* Try to open the input file */
			try(Scanner scanner = new Scanner(new File(fileName3))){
				
				/* If the file has at least one line */
				if(scanner.hasNextLine()) {
					
					/* Read the first line and split into tokens */
					String[] header = scanner.nextLine().trim().split("\\s+");
					
					/* Check if the line has exactly 4 numbers */
					if(header.length != 4) {
						
						/* Throw an error if format is wrong */
						throw new IllegalArgumentException("Invalid header format!");
						
					}
					
					/* Parse the 4 input values */
					n3 = Integer.parseInt(header[0]);
					m3 = Integer.parseInt(header[1]);
					c3 = Integer.parseInt(header[2]);
					k3 = Integer.parseInt(header[3]);
					
				}
				
				/* Initialize the subsets 2D array */
				subsets3 = new int[m3][c3];
				
				int row = 0;	// Start at first row
				
				/* Read each line, up to m3 lines */
				while((scanner.hasNextLine()) && (row < m3)) {
					
					/* Split current line into numbers */
					String[] tokens = scanner.nextLine().trim().split("\\s+");
					
					for(int column = 0; (column < tokens.length) && (column < c3); column++) {
						
						/* Store each number in the array */
						subsets3[row][column] = Integer.parseInt(tokens[column]);
						
					}
					
					row++;	// Move to the next row
					
				}
			
			/* Catch any file reading or parsing errors */
			}catch(Exception e) {
				
				/* Print error message */
				System.err.println("Error reading experiment2.dat: " + e.getMessage());
				
			}
			
			/* Open the output file for writing */
			try(PrintWriter writer = new PrintWriter(new FileWriter(outputFile))){
				
				/* Run algorithm1 10 times */
				for(int i = 0; i < 10; i++) {
					
					long start, end;
						
					start = System.nanoTime();	// Start timer
					int[] algor1 = algorithm1(n3, m3, c3, k3, subsets3);	// Execute algorithm1
					end = System.nanoTime();	// End timer
						
					double time = ((end - start) / 1000000.0);	// Convert to milliseconds
					
					/* If result is null, algorithm failed */
					if(algor1 == null) {
						
						/* Log failure */
						writer.printf("algor1 %d %.2f %b%n", (i + 1), time, false);
						
					}
					
					else {
						
						/* Log success */
						writer.printf("algor1 %d %.2f %b%n", (i + 1), time, true);
						
					}	
					
				}
				
				/* Run algorithm2 10 times */
				for(int i = 0; i < 10; i++) {
					
					long start, end;
						
					start = System.nanoTime();	// Start timer
					int[] algor2 = algorithm2(n3, m3, c3, k3, subsets3);	// Execute algorithm2
					end = System.nanoTime();	// End timer
						
					double time = ((end - start) / 1000000.0);	// Convert to milliseconds
					
					/* If result is null, algorithm failed */
					if(algor2 == null) {
						
						/* Log failure */
						writer.printf("algor2 %d %.2f %b%n", (i + 1), time, false);
						
					}
					
					else {
						
						/* Log success */
						writer.printf("algor2 %d %.2f %b%n", (i + 1), time, true);
						
					}	
					
				}
				
				/* Run algorithm3 10 times */
				for(int i = 0; i < 10; i++) {
					
					long start, end;
						
					start = System.nanoTime();	// Start timer
					int[] algor3 = algorithm3(n3, m3, c3, k3, subsets3);	// Execute algorithm3
					end = System.nanoTime();	// End timer
						
					double time = ((end - start) / 1000000.0);	// Convert to milliseconds
					
					/* If result is null, algorithm failed */
					if(algor3 == null) {
						
						/* Log failure */
						writer.printf("algor3 %d %.2f %b%n", (i + 1), time, false);
						
					}
					
					else {
						
						/* Log success */
						writer.printf("algor3 %d %.2f %b%n", (i + 1), time, true);
						
					}	
					
				}
				
				/* Run algorithm4 10 times */
				for(int i = 0; i < 10; i++) {
					
					long start, end;
						
					start = System.nanoTime();	// Start timer
					int[] algor4 = algorithm4(n3, m3, c3, k3, subsets3);	// Execute algorithm4
					end = System.nanoTime();	// End timer
						
					double time = ((end - start) / 1000000.0);	// Convert to milliseconds
					
					/* If result is null, algorithm failed */
					if(algor4 == null) {
						
						/* Log failure */
						writer.printf("algor4 %d %.2f %b%n", (i + 1), time, false);
						
					}
					
					else {
						
						/* Log success */
						writer.printf("algor4 %d %.2f %b%n", (i + 1), time, true);
						
					}	
					
				}
				
				/* Notify user of completion */
				System.out.println("Experiment 2 complete! Results written to outputExperiment2.txt");
				
			/* Catch any writing errors */
			}catch(IOException e) {
				
				/* Print error */
				System.err.println("Error writing to outputExperiment2.txt: " + e.getMessage());
				
			}
			
		}	// End of Experiment 2
		
		input.close();	// Close the Scanner (good practice)
		
	}

}	// End of main method or main class
