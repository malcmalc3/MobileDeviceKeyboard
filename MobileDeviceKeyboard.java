import java.util.Collections; //For sorting the ArrayList
import java.util.Comparator; //Also for sorting the ArrayList
import java.util.HashMap; //For the HashMaps used in the Trie
import java.util.ArrayList; //For the list of candidates
import java.util.regex.Pattern; //For finding punctuation characters
import java.io.*; //For input/output with the console

public class MobileDeviceKeyboard
{
  /*Interface to make sure that all classes that are candidates
   *are able to return a word and its confidence*/
  interface Candidate
  {
    String getWord();
    Integer getConfidence();
  }

  /*Interface to make sure that all classes that provide autocompleted
   *words will have a way to get the list of words, as well as a way
   *to pass in traning data*/
  interface AutoCompleteProvider
  {
    ArrayList<candidateWord> getWords(String fragment);
    void train(String passage);
  }

  /*Class just contains an autocomplete candidate and its confidence,
   *as well as getter methods for them*/
  class candidateWord implements Candidate
  {
    private String word;
    private Integer confidence;

    public candidateWord(String str, Integer i)
    {
      word = str;
      confidence = i;
    }

    public String getWord() { return word; }

    public Integer getConfidence() {return confidence; }
  }

  /*candidateWord objects will be held in an ArrayList. This class
   *defines the method that will be used to sort the candidateWord
   *objects by confidence in decending order*/
  class sortByConfidence implements Comparator<candidateWord>
  {
    public int compare(candidateWord a, candidateWord b)
    {
      return b.getConfidence() - a.getConfidence();
    }
  }

  /*Class holds a Trie data structure to keep track of the words
   *passed in as training data. Also has an ArrayList that will hold
   *the autocomplete candidateWord objects based on the fragment
   *string passed in by the user*/
  class AutoCompleter implements AutoCompleteProvider
  {
    private ArrayList<candidateWord> autoWords = new ArrayList<>();
    private Trie trie = new Trie();

    public void train(String passage)
    {
      //Separates the words by the space character
      for(String str : passage.split(" "))
      {
        trie.insert(str); //Inserts each word into the Trie
      }
    }

    public ArrayList<candidateWord> getWords(String fragment)
    {
      autoWords.clear(); //Clears to start with an empty list

      TrieNode fragNode = trie.findWord(fragment); //Attempts to find a node in the Trie that matches the fragment

      if(fragNode != null) //If a matching node was found
      {
        finishWord(fragNode); //Method will populate the autoWords list with candidateWord objects
        Collections.sort(autoWords, new sortByConfidence()); //Sorts the autoWords list by confidence in decending order
      }

      return autoWords;
    }

    private void finishWord(TrieNode current)
    {
      //Adds new candidateWord object only if the current node is the end of a word
      if(current.isEndOfWord())
        autoWords.add(new candidateWord(current.getContent(), current.getAppearances()));
      
      //Recursively adds children of this node to the list if they are finished words
      for(TrieNode child : current.getChildren().values())
        finishWord(child);
    }
  }

  class TrieNode
  {
    private HashMap<Character, TrieNode> children = new HashMap<>(); //HashMap holds other TrieNode objects indexed by the next character in the word
    private String content; //Holds the string of the word/prefix up to this node
    private Boolean isWord = false; //True if this node is the end of a word
    private Integer numAppearances = 0; //Keeps track of the number of times this node shows up as the end of a word

    public TrieNode() { content = ""; } //Constructor for root node, no content
    
    public TrieNode(String str) { content = str; } //Constructor for nodes with parent nodes, sets content equal to the word up to this node

    public HashMap<Character, TrieNode> getChildren() { return children; } 

    public void confirmEndOfWord()
    {
      isWord = true;
      numAppearances++;
    }

    public Boolean isEndOfWord() { return isWord; }

    public String getContent() { return content; }

    public Integer getAppearances() { return numAppearances; }
  }

  class Trie
  {
    private TrieNode root = new TrieNode(); //Root holds the nodes for the start of all words in the Trie

    public void insert(String word) //Inserts parameter word into the Trie
    {
      TrieNode current = root; //Start at root of tree

      for(int i=0; i<word.length(); i++) //Goes through word character by character
      {
        String currentPrefix = word.substring(0, i+1);
        Character ch = word.charAt(i);
        
        //Stops loop if current character is punctuation
        if(i == word.length()-1 && Pattern.matches("\\p{Punct}", ch.toString()))
          break;

        //Sets to current to the child node indexed by the character, creates the node if it doesn't already exist
        current = current.getChildren().computeIfAbsent(ch, c->new TrieNode(currentPrefix));
      }
      current.confirmEndOfWord(); //Sets isWord to true and increments numAppearances for the node
    }

    TrieNode findWord(String word)
    {
      TrieNode current = root; //Starts at root of tree

      for(int i=0; i<word.length(); i++) //Goes through word character by character
      {
        current = current.getChildren().get(word.charAt(i)); //Searches through children for a node indexed by the next character
        if(current == null) //If the node does not exist in the trie
          return null;
      }

      return current; //If the node exists in the trie
    }
  }

  public void StartKeyboard()
  {
    AutoCompleter autoCompleter = new AutoCompleter();
    ArrayList<candidateWord> wordList;
    Boolean stopExecution = false;
    String userInput = "";
    Console console = System.console();

    while(!stopExecution)
    {
      userInput = console.readLine("\nOptions \n1 - Pass in training data \n2 - Pass in a fragment \n0 - Quit\n");

      switch(userInput)
      {
        case "0": //Quits program
          stopExecution = true;
          break;
        case "1": //Allows user to insert training data
          userInput = console.readLine("\nEnter training data: ");
          autoCompleter.train(userInput.toLowerCase()); //Inserts all lowercase versions of the words into the Trie
          break;
        case "2": //Prints autocomplete words and their confidence
          userInput = console.readLine("\nEnter a fragment to get auto-complete suggestions: ");
          wordList = autoCompleter.getWords(userInput.toLowerCase());
          System.out.println("\nSuggestions:");
          for(candidateWord candidate : wordList)
            System.out.print(candidate.getWord() + "(" + candidate.getConfidence() + ") ");
          System.out.println();
          break;
        default:
          System.out.println("\nPlease enter an availible option");
          break;
      }
    }
  }
}