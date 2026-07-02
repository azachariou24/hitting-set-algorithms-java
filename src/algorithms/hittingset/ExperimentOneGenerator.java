package algorithms.hittingset;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The ExperimentOneGenerator class generates random subsets from a universal set of integers
 * where each element can appear up to a specified maximum number of times across all subsets.
 * It writes the generated data to a file named "datasets/experiment1.dat".
 * 
 * @since 28/04/2025
 * @version 1.0
 * @author Anastasis Zachariou
 * 
 */
public class ExperimentOneGenerator {
	
	/**
     * Main method generates subsets with constrained appearances of elements
     * and writes the structured data to an output file.
     * 
     * @param args Command-line arguments (not used).
     * 
     * @since 28/04/2025
     * @version 1.0
     * @author Anastasis Zachariou
     * 
     */
	public static void main(String[] args) {
		
		/* ==================== PARAMETER INITIALIZATION ==================== */
		int n = 300;	// Total number of elements in the universal set A
        int m = 100;	// Number of subsets to be generated
        int c = 15;		// Maximum subset size
        int minC = 8;	// Minimum subset size
        int k = 1;		//  Max size of the hitting set (included in output format)
        int maxAppearances = 4;	// Max number of times each element can appear in all subsets
        
        /* ==================== DATA STRUCTURES SETUP ==================== */
        
        Random rand = new Random();	// Random number generator
        
        List<Set<Integer>> subsets = new ArrayList<>();	// List to hold all subsets
        /* Map to track usage of each element */
        Map<Integer, Integer> usageCount = new HashMap<>();
        
        /* Initialize the usage count of each element to 0 */
        for (int i = 1; i <= n; i++) {
        	
            usageCount.put(i, 0);
            
        }
        
        /* Create a list containing each element up to 'maxAppearances' times */
        List<Integer> availableElements = new ArrayList<>();
        
        for (int i = 1; i <= n; i++) {
        	
            for (int j = 0; j < maxAppearances; j++) {
            	
                availableElements.add(i);
                
            }
            
        }

        /* Shuffle the available elements to randomize selection order */
        Collections.shuffle(availableElements, rand);
        
        /* ==================== SUBSET GENERATION ==================== */
        for (int i = 0; i < m; i++) {
        	
        	/* Use a LinkedHashSet to maintain insertion order */
            Set<Integer> subset = new LinkedHashSet<>();
            
            /* Random size between minC and c */
            int subsetSize = rand.nextInt(c - minC + 1) + minC;
            int attempts = 0;	// Limit attempts to avoid infinite loops
            
            /* Fill the subset with unique elements while respecting maxAppearances */
            while (subset.size() < subsetSize && attempts < 10000) {
            	
            	/* If no elements are left to use, break out of the loop */
                if (availableElements.isEmpty()) {
                	
                	break;
                	
                }
                
                /* Randomly pick an element from the available elements list */
                int element = availableElements.get(rand.nextInt(availableElements.size()));
                
                /* Add element to the subset only if it's within allowed appearance limit */
                if (usageCount.get(element) < maxAppearances) {
                	
                    subset.add(element);
                    
                }
                
                attempts++;	// Increment attempt counter to prevent infinite loops
                
            }
            
            /* Update usage count and remove overused elements from the available pool */
            for (int num : subset) {
            	
                usageCount.put(num, usageCount.get(num) + 1);
                
                /* If an element reached its max usage, remove all its occurrences */
                if (usageCount.get(num) >= maxAppearances) {
                	
                    availableElements.removeIf(x -> x == num);
                    
                }
                
            }
            
            /* Add the completed subset to the list of subsets*/
            subsets.add(subset);
            
        }
        
        /* ==================== FILE OUTPUT ==================== */
        try (FileWriter writer = new FileWriter("datasets/experiment1.dat")) {
        	
        	/* Write the first line with metadata: n m c k */
            writer.write(n + " " + m + " " + c + " " + k + "\n");
            
            /* Write each subset to the file, one line per subset */
            for (Set<Integer> subset : subsets) {
            	
                for (int num : subset) {
                	
                    writer.write(num + " ");	// Write element followed by a space
                    
                }
                
                writer.write("\n");	// Newline after each subset
                
            }
            
            /* Notify user of success */
            System.out.println("The file 'datasets/experiment1.dat' was created successfully!");
            
        } catch (IOException e) {
        	
        	/* Handle file writing exceptions */
            System.out.println("Error while writing to the file: " + e.getMessage());
            
        }
        
    }	// End of main

}	// End of ExperimentOneGenerator
