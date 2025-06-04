package engima;

import java.util.*;
import java.io.*;


public class Vigenere {
    
    public static ArrayList<Integer> findLength(String text) {
        //Given a long list of text
        ArrayList<Integer> vals = new ArrayList<>();
        String temp = text.substring(0, 4);
        for (int i = 0; i < text.length() - 4; i++) {
            if (text.substring(temp.length() + i).contains(temp)) {
                //While loop to go through the next values to see how long the string combo is 
            }
        }


        return vals;
    }

    public static int commonDivisor(ArrayList<Integer> vals) {

        //Given like 8, 10, 22, 4, find the greatest common factor

        //Finding the smallest value by sorting and then getting the first index
        vals.sort((a, b) -> { return -1 * a.compareTo(b); });
        int smallest = vals.get(0);

        //Finding all of the factors of that smallest number
        ArrayList<Integer> factors = new ArrayList<>();
        for (int i = 2; i <= smallest; i++) {
            if (smallest % i == 0) {
                factors.add(i);
            }
        }

        //With those factors, we can loop through every one and see which ones check out, we will pick the largest one
        ArrayList<Integer> validFactors = new ArrayList<>();
        boolean valid;
        for (int factor : factors) { //Looping through all of the factors
            valid = true; //Resetting valid
            for (int i = 1; i < vals.size(); i++) { //Checking all of the distances
                if (vals.get(i) % factor != 0) { //If any distance does NOT match the factor then dismiss the factor
                    valid = false;
                    break;
                }
            }
            if (valid) { //Add the factor is its valid
                validFactors.add(factor);
            }
        }

        validFactors.sort((a, b) -> { return a.compareTo(b); });

        return validFactors.get(0);
    }
    
    public static void main(String[] args) {
        //What do I need to do, break it into steps
        //This only works on large bodies of text
        /*
         * 1) Find repetitions in the text and the spaces between them, print both for now(this could be two steps)
         * 2) Find a common divisor between all of the values that we can estimate it to be(easiest i think) to find key length
         * 3) Go through each letter and estimate how shifted it is using frequency analysis per length of the key length
         * 4) Use freq analysis to find the key word
         * 5) With keyword, decode
         */

        //Getting the text from input from a text file
        try {
            //Getting the file and reading it
            BufferedReader fr = new BufferedReader(new FileReader("Vigenere.txt"));
            
            //Getting all of the characters in the file and putting it in input
            String input = "";
            String temp;
            while ((temp = fr.readLine()) != null) {
                input += temp;
            }
            fr.close();
            input = input.toLowerCase(); //Removing any case issues
            
            //Doing part 1
            ArrayList<Integer> estimatedLength = findLength(input);

            //With the arrays find the common multiple
            int keyLength = commonDivisor(estimatedLength);

        } catch (IOException e) {
            System.out.println("IO Exception");
        } catch (Exception e) {
            System.out.println("Exception");
        }
        

    }
}
