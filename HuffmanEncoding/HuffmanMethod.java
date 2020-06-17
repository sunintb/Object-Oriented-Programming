/* Sunint Bindra, PS-3, CS10, S20, Professor Li
This is the primary Huffman class with the respective methods. The main method is at the bottom of the program.
 */

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

public class HuffmanMethod {
    private HashMap<Character, Integer> TableFrequency; // Hash map of frequency table of characters
    private PriorityQueue<BinaryTree<CharacterCounter>> ObjPQTree; // Priority queue of trees of characters
    private HashMap<Character, String> HashMapBit; // Hash Map for bit code of characters
    private BufferedReader inputFile; // Input txt file
    private BufferedWriter OutputDecomp; // Decompressed output file
    private BufferedBitReader InputComp; // Compressed input file
    private BufferedBitWriter OutputComp; // Compressed output file

    // Frequency table of txt file characters
    private void generateTableFrequency() throws IOException {
        TableFrequency = new HashMap<Character, Integer>();
        int value;

        while ((value = inputFile.read()) >= 0) { //For input txt file with values
            Character newChar = (char) value;
            if (TableFrequency.containsKey(newChar)) {
                int genFreq = TableFrequency.get(newChar) + 1; //Increase count if already seen character
                TableFrequency.put(newChar, genFreq);
            } else {
                TableFrequency.put(newChar, 1); //Add character if new
            }
        }
    }
    
    // Priority queue of trees with character and frequency
    private void generatePriorityQueue() {
        // Tree comparator
        class TreeComparator implements Comparator<BinaryTree<CharacterCounter>> {
            public int compare(BinaryTree<CharacterCounter> tree1, BinaryTree<CharacterCounter> tree2) {
                // If both trees have same frequency returns 0
                if (tree2.getData().getFrequency().equals(tree1.getData().getFrequency()) ) {
                    return 0;
                }
                // If 2nd tree has a smaller frequency return 1
                if (tree2.getData().getFrequency() < tree1.getData().getFrequency()) {
                    return 1;
                // else return -1 (2nd tree has higher frequency)
                } else {
                    return -1;
                }
            }
        }
        // Get all keys
        Comparator<BinaryTree<CharacterCounter>> freqTreeComp = new TreeComparator();
        ObjPQTree = new PriorityQueue<BinaryTree<CharacterCounter>>(freqTreeComp);
        Set<Character> genCharKeys = TableFrequency.keySet();
        // To priority queue add trees of keys for characters
        for (Character a : genCharKeys) {
            BinaryTree<CharacterCounter> genTree = new BinaryTree<CharacterCounter>(new CharacterCounter(a, TableFrequency.get(a)));
            ObjPQTree.add(genTree);
        }
    }

    // Creates priority queue of trees of corresponding data
    private void generateTree() {
        // Boundary case with no elements
        if (ObjPQTree.size() == 0) {
            // If no elements, just initialize empty arbitrary tree
            // (not read during the compression process)
            BinaryTree<CharacterCounter> node = new BinaryTree<CharacterCounter>(new CharacterCounter('0', 0));
            return;
        }
        // Boundary case with 1 unique element, creates node
        if (ObjPQTree.size() == 1) {
            BinaryTree<CharacterCounter> T1 = ObjPQTree.element();
            BinaryTree<CharacterCounter> node = new BinaryTree<CharacterCounter>(new CharacterCounter('~', T1.getData().getFrequency()), T1, null);
            ObjPQTree.add(node);
            }
        else
            {
            while (ObjPQTree.size() > 1) {
                // Takes sum of trees' frequencies and generates node
                BinaryTree<CharacterCounter> T1 = ObjPQTree.remove();
                BinaryTree<CharacterCounter> T2 = ObjPQTree.remove();
                BinaryTree<CharacterCounter> node = new BinaryTree<CharacterCounter>(new CharacterCounter('~',
                        T1.getData().getFrequency() + T2.getData().getFrequency()), T1, T2);
                // Priority queue will add node
                ObjPQTree.add(node);
            }
        }
    }

    // Recursive method
    private void BitMapRecursion(HashMap<Character, String> HashMapBit, BinaryTree<CharacterCounter> startNode, String bitCode) {
        // Adds leaf data to HashMap, base case
        if (startNode.isLeaf()) {
            HashMapBit.put(startNode.getData().getCharacter(), bitCode); // Add char and code string to map
            return; // Don't check for children unnecessarily
        }
        // Recursion for children of node, adds 1 or 0 if right child or left child, respectively
        if (startNode.hasRight()) {
            BitMapRecursion(HashMapBit, startNode.getRight(), bitCode + "1"); // Add on 1 if go right
        }
        if (startNode.hasLeft()) {
            BitMapRecursion(HashMapBit, startNode.getLeft(), bitCode + "0"); // Add on 0 if go left
        }
        else
            {
            return;
        }

    }

     // HashMap with characters and corresponding identification #, implements recursive method from above
    private void generateBitMap() {
        HashMapBit = new HashMap<Character, String>();
        if (ObjPQTree.peek() != null) {
            BitMapRecursion(HashMapBit, ObjPQTree.peek(), ""); // Call recursive helper to build bit codes for all chars
        }
    }

    // Inputs txt file and outputs compressed file
    public void compress(String inputFilePath, String compressedFilePath) {
        try {
            int newVal;
            inputFile = new BufferedReader(new FileReader(inputFilePath));
            OutputComp = new BufferedBitWriter(compressedFilePath);
            generateTableFrequency();

            generatePriorityQueue();

            generateTree();

            generateBitMap();
            // Methods from class called above

            inputFile = new BufferedReader(new FileReader(inputFilePath)); // Reading in file now that data has been collected and organized from methods
            // For file containing elements
            while ((newVal = inputFile.read()) >= 0) {
                // Character bit data collected from HashMap
                Character newChar = (char) newVal;
                String CharBit = HashMapBit.get(newChar);
                // Compressed file takes character bit code
                int store = CharBit.length();
                int count = -1;
                while (store != 0) {
                    count++;
                    store--;
                    if (CharBit.charAt(count) == '1') {
                        OutputComp.writeBit(true);
                    } else {
                        OutputComp.writeBit(false);
                    }
                }
                }
        }

        // Catching exception errors below
        catch(IOException e) {
        }
        finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                }
                catch(IOException e){
                }
            }
            if (OutputComp != null) {
                try {
                    OutputComp.close();
                }
                catch(IOException e){
                }
            }
        }
    }

    // Inputs compressed file and outputs decompressed file
    public void decompress(String compressedFilePath, String decompressedFilePath)  {
        try {
            // Reads files setting input and output
            BinaryTree<CharacterCounter> presentNode = ObjPQTree.peek();
            InputComp = new BufferedBitReader(compressedFilePath);
            OutputDecomp = new BufferedWriter(new FileWriter(decompressedFilePath));

            // For tree reads bits if present and cycles through them getting frequency and character
            if (ObjPQTree.peek().isLeaf()) {
                for (int i = 0; i < ObjPQTree.peek().getData().getFrequency(); i++) {
                    OutputDecomp.write(ObjPQTree.peek().getData().getCharacter());
                } } // Reads bits, setting currentBit to input bit
                    while (InputComp.hasNext()) {
                        boolean presentBit = InputComp.readBit();
                        // Traverses tree going down right or left children, arriving at leaf and adds
                        // character to decompressed file
                        if (presentBit) {
                            presentNode = presentNode.getRight();
                        }
                        if (!presentBit) {
                            presentNode = presentNode.getLeft();
                        }
                        if (presentNode.isLeaf()) {
                            OutputDecomp.write(presentNode.getData().getCharacter());
                            // Root is set to current node to cycle through tree for the bit
                            presentNode = ObjPQTree.peek();
                        }
                    }
        //Catches exception errors
        } catch (IOException e) {
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (IOException e) {
                }
            }
            if (OutputComp != null) {
                try {
                    OutputDecomp.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
            // Main method, hardcodes txt files, assigns names for compressed and decompressed files
            String inputFilePath = "inputs/cciaooo.txt";
            String decompressedFilePath = inputFilePath.substring(0, inputFilePath.length() - 4) + "_decompressed.txt";
            String compressedFilePath = inputFilePath.substring(0, inputFilePath.length() - 4) + "_compressed.txt";
            // Takes files, implements methods, and compressed and decompresses class
            HuffmanMethod CompressFile = new HuffmanMethod();
            CompressFile.compress(inputFilePath, compressedFilePath);
            CompressFile.decompress(compressedFilePath, decompressedFilePath);
    }
}
