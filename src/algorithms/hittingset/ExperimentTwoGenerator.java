package algorithms.hittingset;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The ExperimentTwoGenerator class creates a number of subsets from a universal set,
 * ensuring that each element appears no more than a specified number of times and each subset
 * has a limited number of elements. The results are saved to a file "datasets/experiment2.dat".
 * 
 * @since 29/04/2025
 * @version 1.0
 * @author Anastasis Zachariou
 * 
 */
public class ExperimentTwoGenerator {
	
	/**
     * The main method generates controlled subsets from a fixed-size universal set and writes them to a file.
     * 
     * @param args Command-line arguments (not used).
     * 
     * @since 29/04/2025
     * @version 1.0
     * @author Anastasis Zachariou
     * 
     */
	public static void main(String[] args) {
		
		/* ==================== PARAMETER INITIALIZATION ==================== */
		int n = 70;		// Number of elements in the universal set A
		int m = 50;		// Number of subsets to be created
		int c = 7;		// Maximum size of each subset
		int k = 12;		// Size limit of the hitting set (output only)
		int maxAppearances = 5;	// Max number of times each element can appear across all subsets
		
		Random rand = new Random();	// Random number generator
		
		/* ==================== CREATE MULTIPLE COPIES FOR EACH ELEMENT ==================== */
		
		/* List to hold elements with repeated appearances */
		List<Integer> allAppearances = new ArrayList<>();
		
		for(int i = 1; i <= n; i++) {
			
			for(int j = 0; j <= maxAppearances; j++) {
				
				allAppearances.add(i);	// Add element 'i' (maxAppearances + 1) times
				
			}
			
		}
		
		/* Shuffle the list to randomize placement order */
		Collections.shuffle(allAppearances, rand);
		
		/* ==================== INITIALIZE SUBSETS ==================== */
		
		/* List to store all subsets */
		List<Set<Integer>> subsets = new ArrayList<>();
		
		/* Initialize each subset as an empty LinkedHashSet */
		for(int i = 0; i < m; i++) {
			
			subsets.add(new LinkedHashSet<>());
			
		}
		
		/* ==================== DISTRIBUTE ELEMENTS INTO SUBSETS ==================== */
		
		int index = 0;	// Used to balance distribution across subsets
		for(int element : allAppearances) {
			
			/* Track whether the element was successfully added */
			boolean added = false;
			
			int attempts = 0;	// Prevent infinite attempts
			
			/* Try to place the element into a subset without exceeding limits */
			while(!(added) && (attempts < m)) {
				
				int subsetIndex = ((index + attempts) % m);	// Circular index across subsets
				
				Set<Integer> subset = subsets.get(subsetIndex);
				
				/* Only add the element if it's not already present and the subset has space */
				if((subset.size() < c) && !(subset.contains(element))) {
					
					subset.add(element);	// Add element to subset
					
					added = true;	// Mark as added
					
				}
				
				attempts++;	// Try next subset if not successful
				
			}
			
			/* Log warning if the element couldn't be added to any subset */
			if(!added) {
				
				System.out.println("Failed to place element: " + element);
				
			}
			
			index++;	// Move to next starting subset index
			
		}
		
		/* ==================== WRITE RESULTS TO FILE ==================== */
		try (FileWriter writer = new FileWriter("datasets/experiment2.dat")) {
        	
			
			/* Write metadata line: n m c k */
            writer.write(n + " " + m + " " + c + " " + k + "\n");
            
            /* Write each subset on a separate line */
            for (Set<Integer> subset : subsets) {
            	
                for (int num : subset) {
                	
                    writer.write(num + " ");	// Write each element followed by space
                    
                }
                
                writer.write("\n");	// End of subset line
                
            }
            
            /* Notify user of successful file creation */
            System.out.println("The file 'datasets/experiment2.dat' was created successfully!");
            
        } catch (IOException e) {
        	
        	/* Handle exceptions during file writing */
            System.out.println("Error while writing to the file: " + e.getMessage());
            
        }

	}	// End of main method

}	// End of ExperimentTwoGenerator class
