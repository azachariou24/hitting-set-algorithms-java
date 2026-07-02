package algorithms.hittingset;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The RandomInstanceGenerator class prompts the user for parameters to generate a collection of random subsets 
 * and writes them to a file named "datasets/sets.dat". Each subset contains unique integers, and the overall 
 * structure can be used for problems involving hitting sets or combinatorial optimization.
 * 
 * @since 28/04/2025
 * @version 1.0
 * @author Anastasis Zachariou
 */
public class RandomInstanceGenerator {
	
	/**
     * The main method interacts with the user to collect input values, generates random subsets, 
     * and writes them to a file following the defined structure.
     * 
     * @param args Command-line arguments (not used).
     * 
     * @since 28/04/2025
     * @version 1.0
     * @author Anastasis Zachariou
     * 
     */
	public static void main(String[] args) {
		
		/* Create a Scanner object to read user input from the console */
		Scanner scanner = new Scanner(System.in);
		
		/* Declare and initialize variables for user input */
		int n = 0, m = 0, c = 0, k = 0;
		
		/* ==================== USER INPUT LOOP ==================== */
		while(true) {
			
			try {
				
				/* Prompt user for the number of elements in the universal set A */
				System.out.print("Enter the number of elements in set A (n > 0): ");
				n = Integer.parseInt(scanner.nextLine());
				
				/* Validate input: n must be > 0 */
				if(n <= 0) {
					
					throw new IllegalArgumentException("n must be greater than 0");
					
				}
				
				/* Prompt user for the number of subsets to generate */
				System.out.print("Enter the number of subsets (m > 0): ");
				m = Integer.parseInt(scanner.nextLine());
				
				/* Validate input: m must be > 0 */
				if(m <= 0) {
					
					throw new IllegalArgumentException("m must be greater than 0");
					
				}
				
				/* Prompt user for the max size of each subset */
				System.out.print("Enter the maximum number of elements per subset (1 <= c <= " + n + "): ");
				c = Integer.parseInt(scanner.nextLine());
				
				/* Validate input: 1 <= c <= n */
				if(c <= 0 || c > n) {
					
					throw new IllegalArgumentException("c must be between 1 and " + n);
					
				}
				
				/* Prompt user for the size limit of a hitting set */
				System.out.print("Enter the maximum number of elements in the hitting set (1 <= k <= " + n + "): ");
				k = Integer.parseInt(scanner.nextLine());
				
				/* Validate input: 1 <= k <= n */
				if(k <= 0 || k > n) {
					
					throw new IllegalArgumentException("k must be between 1 and " + n);
					
				}
				
				
				break;	// If all inputs are valid, exit the loop
				
			} catch(NumberFormatException e) {
				
				/* Handle non-integer input */
				System.out.println("Please enter a valid integer.");
				
			} catch(IllegalArgumentException e) {
				
				/* Handle validation errors */
				System.out.println("Error: " + e.getMessage());
				
			}
				
		}
		
		/* ==================== SUBSET GENERATION ==================== */
		
		/* Create a Random object for generating random numbers */
		Random rand = new Random();
		
		/* Create a list to store all generated subsets */
		List<Set<Integer>> subsets = new ArrayList<>();
		
		/* Loop to generate m subsets */
		for(int i = 0; i < m; i++) {
			
			/* Use LinkedHashSet to preserve insertion order and ensure uniqueness */
			Set<Integer> subset = new LinkedHashSet<>();
			
			/* Randomly determine the size of the current subset (between 1 and c) */
			int subsetSize = rand.nextInt(c) + 1;
			
			/* Populate the subset with unique random elements from 1 to n */
			while(subset.size() < subsetSize) {
				
				int element = rand.nextInt(n) + 1;	// Random integer in range [1, n]
				
				subset.add(element);	// Add element if not already in the set
				
			}
			
			subsets.add(subset);	// Add the generated subset to the list
			
		}
		
		/* ==================== FILE OUTPUT ==================== */
		
		/* Try-with-resources to ensure FileWriter is properly closed */
		try(FileWriter writer = new FileWriter("datasets/sets.dat")){
			
			/* Write the metadata line: n m c k */
			writer.write(n + " " + m + " " + c + " " + k + "\n");
			
			/* Write each subset on a new line */
			for(Set<Integer> subset : subsets) {
				
				for(int num : subset) {
					
					writer.write(num + " ");	// Write each number in the subset
					
				}
				
				writer.write("\n");	// Newline after each subset
				
			}
			
			/* Inform the user of success */
			System.out.println("\nFile 'datasets/sets.dat' was created successfully!");
			
		} catch(IOException e) {
			
			/* Handle file write errors */
			System.out.println("Error while writing to file: " + e.getMessage());
			
		}

	}	// End of main

}	// End of RandomInstanceGenerator
