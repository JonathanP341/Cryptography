package engima;

import java.util.*;

import javax.management.AttributeList;

import java.io.*;


public class Vigenere {
    
    /**
     * findLength
     * 
     * Finding the length of the patterns and how far apart they are from each other
     * 
     * @param text
     * @return Arraylist of distances
     */
    public static ArrayList<Integer> findLength(String text) {
        //Given a long list of text
        ArrayList<Integer> vals = new ArrayList<>();
        String temp = text.substring(0, 4);
        int distance;

        for (int i = 0; i < text.length() - 4; i++) {
            if (text.substring(temp.length() + i).contains(temp)) {
                //While loop to go through the next values to see how long the string combo is
                //For no assume the distance between the pattern is 4 
                distance = findDistance(temp, text.substring(temp.length() + i));
                if (distance > 1) {
                    vals.add(distance);
                    //System.out.println(temp + ": " + distance);
                }
            }
            //Need to shift the temp string
            temp = text.substring(i + 1, i+5);
        }
        return vals;
    }

    /**
     * findDistance
     * 
     * Given a pattern and a substring find the distance between the first and next instance of the pattern
     * 
     * @param pattern
     * @param substring
     * @return the distance between instances
     */
    public static int findDistance(String pattern, String substring) {
        String tester = substring.substring(0, 4);
        int i = 0;
        try {
            while (!pattern.equals(tester)) {
                i++;
                tester = substring.substring(i, i+pattern.length());
            }
            return i + pattern.length();
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * commonDivisor
     * 
     * Find the common divisor given an arraylist of values
     * 
     * @param vals - Array list of lengths/values
     * @return int - Largest common divisor
     */
    public static int commonDivisor(ArrayList<Integer> vals) {

        //Given like 8, 10, 22, 4, find the greatest common factor

        //Finding the smallest value by sorting and then getting the first index
        vals.sort((a, b) -> { return a.compareTo(b); });
        int smallest = vals.get(0);

        //Finding all of the factors of that smallest number
        ArrayList<Integer> factors = new ArrayList<>();
        for (int i = 1; i <= smallest; i++) {
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

        validFactors.sort((a, b) -> { return -1 * a.compareTo(b); });

        return validFactors.get(0);
    }
    
    /**
     * letterOfKeyWord
     * 
     * Finding the frequency of the letters and storing them in a hashmap
     * 
     * @param text
     * @param num - The position of the estimated length we are at, ie 1st position, 2nd position etc
     * @param lengthPattern
     * @return
     */
    public static String letterOfKeyword(String text, int num, int lengthPattern) {
        HashMap<Character, Integer> freq = new HashMap<>();
        int bar = 0;
        for (int i = num; i < text.length(); i += lengthPattern) {
            bar++;
            if (!freq.containsKey(text.charAt(i))) {
                freq.put(text.charAt(i), 1);
            } else {
                freq.put(text.charAt(i), freq.get(text.charAt(i)) + 1);
            }
        }

        /*
        Now that we have all of the values in a hashmap we need to try to match the peaks through 
        The patterns we are trying to match for are:
            - E at 12% almost by itself
            - R S T back to back at 6-9% each 
        These are the most reliable because 2 characters at ~7% could be H,I or N, O
        */
        ArrayList<Character> possibleLetters = new ArrayList<>();

        //Finding the 3 letters that show up the most and adjusting them to find where a should be
        ArrayList<Character> charArr = possibleEVals(freq);
        charArr.forEach( (c) -> {possibleLetters.add(c);});
        
        //Finding the shift that matches up with the peaks of r, s and t
        System.out.println("------" + num + "------");
        for (char c : freq.keySet()) { 
            System.out.println(c + ": " + freq.get(c));
        }
        possibleLetters.add(threePeak(freq, (int)Math.ceil(bar * 0.05)));


        return "";
    }

    public static char threePeak(HashMap<Character, Integer> freq, int bar) {
        //In this we are looking for the three peaks of R, S and T
        //We are looping through the hashmap, which we know is in alphabetical order to find a peak of 3 in a row
        //To do this we need 3 in a row to pass a bar which is >= 5% or bar which we calculate at 

        ArrayList<Character> charArr = new ArrayList<>(); //ArrayList to hold the values we find

        //Looping through all characters to find groups of 3 at a time with the correct values
        ArrayList<Character> temp = new ArrayList(freq.keySet());
        for (int i = 0; i < temp.size(); i++) {
            //For each section we will check if that value and the next two are side by side in the alphabet and if they are all above the bar
            //We use mod to wrap back around so that we can find if it goes z, a, b for example
            if (freq.get(temp.get(i)) >= bar && freq.get(temp.get((i + 1) % temp.size())) >= bar && freq.get(temp.get((i + 2) % temp.size())) >= bar) {
                //Checking if they are all letters next to each other, one after the other
                if (((int)temp.get(i) - 70) % 26 == (int)temp.get((i+1) % temp.size()) - 97 && ((int)temp.get(i+1) - 70) % 26 == (int)temp.get((i+2) % temp.size()) - 97) {
                    charArr.add(temp.get(i));
                }
            }
        }


        return '-';
    }


    /**
     * guessLetter
     * 
     * Finding the most used letters(Top 3) and retuning them to their shifted position
     * 
     * @param freq
     * @return ArrayList<Character> all of the top 3 most used characters, the value of the cipher will probably be one of these
     */
    public static ArrayList<Character> possibleEVals(HashMap<Character, Integer> freq) {
        //Finding the three peaks by sorting the hashmap
        ArrayList<Map.Entry<Character, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort(Map.Entry.<Character, Integer>comparingByValue().reversed());
        
        //Creating an array list to store the biggest values
        ArrayList<Character> charArr = new ArrayList<>();
        charArr.add(entries.get(0).getKey());
        charArr.add(entries.get(1).getKey());
        charArr.add(entries.get(2).getKey());

        //Getting the position of a in the new shifted alphabet, which is our letter - 101 % 26 and then shifted back to a lowercase character
        for (int i = 0; i < charArr.size(); i++) {
            charArr.set(i, shiftLetterByE(charArr.get(i)));
        }
        return charArr;
    }

    public static char shiftLetterByE(char c) {
        return (char)(((((int)c - 101) + 26) % 26) + 97); //Shifting the value to that we know what value a would be at
    }

    public static void main(String[] args) {
        //What do I need to do, break it into steps
        //This only works on large bodies of text
        /*
         * 1) Find repetitions in the text and the spaces between them, print both for now(this could be two steps) - Done
         * 2) Find a common divisor between all of the values that we can estimate it to be(easiest i think) to find key length - Done
         * 3) Go through each letter and estimate how shifted it is using frequency analysis per length of the key length - WIP
         * 4) Use freq analysis to find the key word
         * 5) With keyword, decode
         */

        //Getting the text from input from a text file
        try {
            //Getting the file and reading it
            BufferedReader fr = new BufferedReader(new FileReader("enigma-project\\src\\main\\java\\engima\\Vigenere.txt"));
            
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

            //With the key length, now we have to look at all of the values and measure the frequency of everything
            //To do that we wil go letter by letter
            String keyword = ""; 
            for (int i = 0; i < keyLength; i++) {
                keyword += letterOfKeyword(input, i, keyLength);
            }
            System.out.println(keyword);

        } catch (IOException e) {
            System.out.println("IO Exception " + e);
        } catch (Exception e) {
            System.out.println("Exception" + e);
        }

    }
}
