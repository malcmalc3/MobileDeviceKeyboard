# MobileDeviceKeyboard

## Running the Program
1. Java must be installed to run the program
2. Open the terminal on your machine and navigate to the folder containing the files
3. Type the following command to run the program
    * `java -jar Main.jar`

## Using the Program

When the program first loads up, you will be presented with a menu that has three options:
- 1 - Pass in training data
- 2 - Pass in a fragment
- 0 - Quit

To choose one of these options, the number corresponding to the action must be entered into the console, followed by pressing the enter key. Entering the 1 key will allow you to type in words that the program will use to train the keyboard for future autocomplete inquiries. Entering the 2 key, the program will prompt you to type in a word to get autocomplete suggestions and their confidence. Entering the 0 key will stop the program from running.

### Training
Choosing to train the keyboard, the program will prompt you to enter in data to enter into the autocomplete dictionary. The intended use is to type a sentence that can be split up into separate words byt the space key.
-Ex: "The third thing that I need to tell you is that this thing does not think thoroughly."

### Getting Autocomplete Candidates
Choosing to pass in a fragment, the program will prompt you to enter in data to retrieve candidates for completing the word. The intended use is tp type the first part of a word that has been used to train the keyboard. After pressing enter, the program will print out its candidates for words that could be used to complete the fragment. These candidates will be sorted by the confidence that the candidate is the actual word the user is looking to complete. The confidence value will be displayed in parentheses after the candidate word.
-Ex: Using the example sentence from above, passing in a fragment of "thi", the program should output "thing(2) third(1) this(1) think(1)"
