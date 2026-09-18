import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;

/** @author Golibe Nwobi
 * <p>
 * This is a class that takes in a string and creates both <b>full</b> and <b>partial</b> anagrams
 * of that string without duplicates
 * </p>
 *
 */

public class Word {
    /*
    Task 1.2 - Discussion of getFullAnagrams:

    The full length anagram algorithm uses a "prefix" + "remaining" recursive strategy to generate
    permutations. The prefix grows one character at a time while the remaining shrinks as the characters are removed.
    A full permutation is complete and then added to the list when the remaining is empty. Input characters are sorted
    first and, during recursion, if they are found to be repeating, skipping happens to avoid duplicates.

    Each recursive call chooses one character from remaining, adds it to prefix, builds a new "remaining" string
    without that character, recurses until no characters are left.
    The number of permutations of n characters = n! and each permutation = string of length "n".
    Duplicate handling reduces the number of branches, but it does not change the worst case complexity, therefore,
    the runtime complexity of the algorithm = O(n * n!).
    There is not any difference in complexity between "only full length" and "any length" anagram calculating
    algorithms because both are factorial but the latter does the factorial work for every subset so it performs
    relatively more computation. They are asymptotically the same class but the latter method is practically larger.
     */
    //constants to represent large numbers
    public static final double MILLION = 1000000.0;
    public static final int HUNDRED = 100;

    //Instance variables
    private String original;
    private int n;
    private List<String> fullAnagrams;
    private List<String> partialAnagrams;
    private List<char[]> results; //generated subsets are stored here
    private char[] charArr;
    private TreeNode root;
    private int testNo = 1;
    private int totalSearches = 0;
    private boolean numberingAllowed = true;

    /**
     * The constructor creates a Word object and generates all <b>full</b> and <b>partial</b>
     * anagrams of the input word
     * @param word the input word to be analysed
     *
     */
    public Word(String word) {
        this.original = word;
        this.n = word.length();
        this.fullAnagrams = new ArrayList<>();
        this.partialAnagrams = new ArrayList<>();
        this.results = new ArrayList<>();

        //The word is normalised, put in a char array and sorted
        String actualWord = word.toLowerCase();
        this.charArr = actualWord.toCharArray();
        Arrays.sort(this.charArr);
        createFull("", new String(this.charArr), fullAnagrams);
        createPartial();
    }

    //creates full anagrams
    private void createFull(String prefix, String remaining, List<String> resultList) {
        //if remaining is empty, add prefix to list
        if (remaining.length() == 0) {
            resultList.add(prefix);
        } else {
            //try each character as the next prefix position
            for (int i = 0; i < remaining.length(); i++) {
                String newPrefix = prefix + remaining.charAt(i);
                String remainingAfter = remaining.substring(i + 1);
                String remainingBefore = remaining.substring(0, i);
                //build new remaining without the character at i
                String newRemaining = remainingBefore + remainingAfter;
                //check for duplicates and skip if found
                if (i > 0 && (remaining.charAt(i) == remaining.charAt(i - 1))) {
                    continue;
                }
                //Recurse with new prefix and remainder
                createFull(newPrefix, newRemaining, resultList);
            }
        }
    }

    //generates subsets based on the formal parameters
    private void subsetGen(char[] sourceArr, int targetSize, int currPosition, char[] partialSubset, int charsPlaced) {
        //return if subset of target size is reached
        if (charsPlaced == targetSize) {
            char[] newSubset = Arrays.copyOf(partialSubset, targetSize);
            results.add(newSubset);
            return;
        }

        //return if the end of the array has been reached
        if (currPosition == sourceArr.length) {
            return;
        }

        for (int i = currPosition; i < sourceArr.length; i++) {
            //skip any duplicates at current recursion depth to prevent identical subsets
            if (i > currPosition && sourceArr[i] == sourceArr[i - 1]) {
                continue;
            }
            partialSubset[charsPlaced] = sourceArr[i];
            subsetGen(sourceArr, targetSize, i + 1, partialSubset, charsPlaced + 1);
        }
    }

    //creates partial anagrams
    private void createPartial() {
        for (int i = 1; i <= charArr.length; i++) {
            //set size based on subset length
            char[] partialSubset = new char[i];
            results.clear();
            subsetGen(charArr, i, 0, partialSubset, 0);
            for (int j = 0; j < results.size(); j++) {
                char[] subset = results.get(j);
                String subsetStr = String.valueOf(subset);
                //Generate all unique permutations of that subset
                createFull("", subsetStr, partialAnagrams);
            }

        }
    }

    /**
     * This method returns all the <b>full anagrams</b> of the original word
     * @return a list of full anagrams
     */
    public List<String> getFullAnagrams() {
        return fullAnagrams;
    }

    /**
     * This method returns all the <b>partial anagrams</b> of the original word
     * @return a list of partial anagrams
     */
    public List<String> getPartialAnagrams() {
        return partialAnagrams;
    }

    /**
     * This private class represents a node in the binary search tree and
     * it stores a non-english word, its english translation and references
     * to its lef and right child nodes
     */
    private class TreeNode {
        String word;
        TreeNode left;
        TreeNode right;
        String translatedWord;

        /**
         * This private method creates a new tree node that stores a non-english
         * word and its english translation
         * @param word the non-english word
         * @param translatedWord the english translation of the non-english word
         */
        private TreeNode(String word, String translatedWord) {
            this.word = word;
            this.left = null;
            this.right = null;
            this.translatedWord = translatedWord;
        }
    }

    /**
     * This public method inserts a new non-english word and its english translation
     * into the binary search tree. Rather than returning a value,
     * it mutates or changes the tree directly (hence void)
     * @param newWord the new non-english word to insert
     * @param transNewWord the english translation of the word
     *
     */
    public void insertWord(String newWord, String transNewWord) {
        //traverse to find the right point for insertion
        TreeNode parent = null;
        TreeNode currentNode = this.root;
        while (currentNode != null) {
            parent = currentNode;
            if (newWord.compareTo(currentNode.word) < 0) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }
        //create new node null space has been found
        TreeNode newNode = new TreeNode(newWord, transNewWord);
        //attach new node to its parent node or set as root if tree is empty
        if (parent == null) {
            this.root = newNode;
        } else {
            if (newWord.compareTo(parent.word) < 0) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }
        }
    }

    private long totalLookupTimeNs = 0;
    /**
     * This private method searches the binary search tree for a provided
     * non-english word and sums up the time taken per search
     * @param word the non-english word that is searched for
     * @return the tree node that contains the found word and its translation
     * or null if the word could not be found
     */
    private TreeNode searchForWord(String word) {
        long startingTime = System.nanoTime();
        totalSearches++;
        TreeNode outcome = null;
        TreeNode currentNode = this.root;
        while (currentNode != null) {
            //non-english target word is compared to the current node's word to determine search direction
            int comparison = word.compareTo(currentNode.word);
            if (comparison == 0) {
                outcome = currentNode;
                break;
            } else if (comparison < 0) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }
        long endingTime = System.nanoTime();
        totalLookupTimeNs += (endingTime - startingTime);
        return outcome;
    }

    /**
     * This public method parses (splits up) a line from the dictionary file
     * @param line a line from the dictionary file
     * @return a string array that contains the non-english word and its translation
     * @throws IllegalArgumentException only if the file contains more or less than 2 fields
     */
    private String[] parseLine(String line) throws IllegalArgumentException {
        line = line.toLowerCase();
        //split the line into two parts based on the tab character
        String[] lineParts = line.split("\t");
        if (lineParts.length != 2) {
            throw new IllegalArgumentException("Sorry, invalid line format");
        }
        String newWord = lineParts[0];
        String transNewWord = lineParts[1];
        return new String[]{newWord, transNewWord};
    }

    /**
     * This is a public method that takes a file name (as the path) and inserts all
     * non-english words + their translations from that same file into the Binary Search Tree
     * @param fileName the name of the dictionary file (path)
     * @throws IOException only if the file cannot be read or opened
     */
    public void createDictionary(String fileName) throws IOException {
        /*
        Task 2.2 - Justification:

        The data structure I chose to store the dictionary words is a Binary Search Tree.
        Because of the BST property, searching is efficient as words that are smaller than
        the current node (lexicographically) go to the left while larger ones go to the right.
        Furthermore, each comparison helps eliminate half of the remaining search space.
        The average case and worst case are O(log n) and O(n) (if the tree becomes unbalanced) respectively.
        On this large dataset, it is very good as logarithmic searches scale exceptionally well and
        it is way more efficient than linear search, especially for repeated search lookups.
         */
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        //read each line from the dictionary, parse it and insert into the binary search tree
        while ((line = br.readLine()) != null) {
            String[] store = parseLine(line);
            String newWord = store[0];
            String transNewWord =  store[1];
            insertWord(newWord, transNewWord);
        }
        br.close();
    }

    private List<Long> timeTakenList = new ArrayList<>();

    /**
     * This is a public method that checks whether any words in the Binary Search Tree
     * are anagrams (full or partial) of a specific input string.
     * @param s the specific input string whose anagrams to be searched for
     */
    public void searchAnagrams(String s) {
        long startingTime = System.nanoTime();
        Word inputStr = new Word(s);
        //initialize arraylist of partial anagrams
        List<String> inputStrStore  = new ArrayList<>(inputStr.getPartialAnagrams());
        if (inputStrStore.isEmpty()) {
            long endingTime = System.nanoTime();
            //calculate the duration of the search
            long timeTakenNs = endingTime - startingTime;
            timeTakenList.add(timeTakenNs);
            //convert from nanoseconds to milliseconds
            double timeTakenMs = timeTakenNs / MILLION;
            resultSeparator();
            System.out.println("Sorry, no anagrams can be made from this input");
            System.out.println("Time taken for search: " + timeTakenMs + " milliseconds");
            resultSeparator();
            return;
        }

        if(numberingAllowed) {
            System.out.println(testNo + ". Specific Input String: " + s);
        } else {
            System.out.println("Specific Input String: " + s);
        }

        //flag starts at false
        boolean anagramFound = false;
        for (String word : inputStrStore) {
            TreeNode searchResult = searchForWord(word);
            if (searchResult != null) {
                //the flag only flips if at least one anagram is found
                anagramFound = true;
                System.out.println("Anagram match found: " +
                        word + "         English translation: " + searchResult.translatedWord);
            }
        }
        //no anagram is found at the end of search
        if (!anagramFound) {
            System.out.println("No anagrams found");
        }
        long endingTime = System.nanoTime();
        long timeTakenNs = endingTime - startingTime;
        timeTakenList.add(timeTakenNs);
        double timeTakenMs = timeTakenNs / MILLION;
        System.out.println("Time taken for search: " + timeTakenMs + " milliseconds");
        resultSeparator();
    }

    /**
     * This is a public method that calculates and returns the average time it
     * takes for a full searchAnagrams() search to run
     * @return the average time per call of searchAnagrams() in milliseconds
     */
    public double avgTimeSearchAnagrams() {
        long totalTime = 0;
        for (int i = 0; i < timeTakenList.size(); i++) {
            totalTime += timeTakenList.get(i);
        }
        //calculate the average time per call of searchAnagrams():
        long avgTime = totalTime / timeTakenList.size();
        double avgTimeMs = avgTime / MILLION;
        return avgTimeMs;
    }

    /**
     * This is public method that calculates and returns the average time to
     * search for a word in the binary search tree
     * @return the average time per search in milliseconds
     */
    public double avgTimeSearchBST() {
        //calculate the average time per individual BST search
        if (totalSearches == 0) {
            return 0.0;
        }
        long avgTime = totalLookupTimeNs / totalSearches;
        double avgTimeMs = avgTime / MILLION;
        return avgTimeMs;
    }

    /**
     * This is a private method that prints a separator line in the output
     * terminal in order to enhance the readability of the results.
     */
    private void resultSeparator() {
        System.out.println("-".repeat(HUNDRED));
    }



    /**
     * This is a public method that provides and performs a variety
     * of manual tests for the searchAnagram() method.
     * It tests for:
     * <ul>
     * <li>Empty input string</li>
     * <li>Non‑alphabetic characters (digits, punctuation, mixed symbols)</li>
     * <li>Whitespace inside the input</li>
     * <li>Input with one letter</li>
     * <li>Short alphabetic inputs with no anagrams</li>
     * <li>Input with repeated letters</li>
     * <li>Words with repeated‑pattern structures (e.g., "banana", "tartar")</li>
     * <li>Inputs that produce no dictionary matches</li>
     * <li>Uppercase input (tests lowercase conversion)</li>
     * <li>Longest allowed input length (6 characters)</li>
     * </ul>
     */
    public void manualEdgeCaseTests() {
        System.out.println("The manual edge-case tests passed through searchAnagrams(): ");
        searchAnagrams("");
        searchAnagrams("67");
        searchAnagrams("b_a.l");
        searchAnagrams("f ra t");
        searchAnagrams("d");
        searchAnagrams("la");
        searchAnagrams("eee");
        searchAnagrams("banana");
        searchAnagrams("moot");
        searchAnagrams("tartar");
        searchAnagrams("xxxx");
        searchAnagrams("zrcvq");
        searchAnagrams("FRUIT");
        searchAnagrams("abcdef");
        resultSeparator();
    }

    public static void main(String[] args) throws IOException {
        Word test = new Word("MOOT");
        test.createDictionary("Galician.tsv");
        List<String> wordsList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("Danish.tsv"));
        String line;
        //read each line then extract and add non-english word to the list
        while ((line = br.readLine()) != null) {
            String[] store = test.parseLine(line);
            String testWord = store[0];
            // only allows words shorter than 7 chars to avoid making very large
            //anagram sets during the 100 test calls.
            //The brief says the testing inputs will be 3-6 letters long
            if (testWord.length() < 7) {
                wordsList.add(testWord);
            }
        }
        br.close();

        //Initialize variables for tracking statistics
        int minIndex = 0;
        int maxIndex = wordsList.size() - 1;
        int minLength = HUNDRED;
        int maxLength = 1;

        test.numberingAllowed = false;
        test.manualEdgeCaseTests();
        test.numberingAllowed = true;

        test.timeTakenList.clear();
        test.testNo = 1;

        System.out.println("The 100 calls of searchAnagrams()" +
                "(with different strings of variable lengths): ");
        System.out.println();
        for (int i = 0; i < HUNDRED; i++) {
            //generate a random index to select a random word from the list
            int randomIndex = minIndex + (int)(Math.random() * ((maxIndex - minIndex) + 1));
            String randomWord = wordsList.get(randomIndex);
            test.searchAnagrams(randomWord);
            test.testNo++;
            if (randomWord.length() < minLength) {
                minLength = randomWord.length();
            } else if (randomWord.length() > maxLength) {
                maxLength = randomWord.length();
            }
        }

        System.out.println("Number of searches performed: " + test.totalSearches);
        System.out.println("Tests performed with input strings between length: " +
                minLength + " and " + maxLength + " inclusive");
        System.out.println("Average time per individual BST search(): " +
                test.avgTimeSearchBST() + " milliseconds" );
        System.out.println("Average time per call of searchAnagrams(): " +
                test.avgTimeSearchAnagrams() + " milliseconds");
        /*
        Task 2.4 Runtime Report:

        Measurement of the average time per individual search in my binary search tree
        was done using System.nanoTime() inside searchWord(). Across 20 runs, the lookup times stayed
        extremely small, ranging from 1.72x10^-4 to 1.99x10^-4 milliseconds giving it a
        mean of 1.855x10^-4 milliseconds. The little fluctuations in the average time are expected
        due to: JVM warm-up, CPU scheduling etc. The process of searching the binary search tree,
        in my implementation, involves comparing the target word with the current node and moving left or
        right based on lexicographic order. This structure gives a time complexity of O(log n) as
        each comparison disregards half of the remaining search space. In the worst case, if the tree
        becomes very unbalanced, the search degrades to O(n). The lookup times measured are very small and
        show little fluctuations (expected). The results align with the theoretical logarithmic
        behaviour of BST search.
         */
    }
}


