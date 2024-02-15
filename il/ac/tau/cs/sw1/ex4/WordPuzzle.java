package il.ac.tau.cs.sw1.ex4;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class WordPuzzle 
{
	public static final char HIDDEN_CHAR = '_';
	
	/*
	 * @pre: template is legal for word
	 */
	public static char[] createPuzzleFromTemplate(String word, boolean[] template) 
	{ // Q - 1
		char[] riddle = new char[word.length()];
		for (int i=0; i<word.length(); i++)
		{
			riddle[i] = template[i] ? HIDDEN_CHAR : word.charAt(i);
		}
		return riddle;
	}

	public static boolean checkLegalTemplate(String word, boolean[] template) 
	{ // Q - 2
		if (word.length() != template.length)				//Legal riddles must have same length word and template
			return false;
		else
		{
			boolean one_hidden = false;
			boolean one_exposed =false;
			for (int i=0; i<template.length; i++)			//Rule 1: At least one instance of each hidden and exposed char
			{
				if (template[i]) one_hidden = true;
				if (!template[i]) one_exposed = true;
				if (one_hidden && one_exposed)
					break;
			}
			if (!one_hidden || !one_exposed)
				return false;
			else
			{
				//Rule 2: Checking for each letter if all of it's instances are the same: hidden or exposed
				boolean[] abc = new boolean[26];			//abcdefghijklmnop. Is there an instance of this character being hidden?
				for (int i=0; i<template.length; i++)		//initializing with the last instance of each letter in the word
				{
					abc[(int)(word.charAt(i))-(int)('a')] = template[i];
				}
				for (int i=0; i<template.length; i++) 		//Checking if all other instances of said letter are the same
				{
					if (template[i] != abc[(int)(word.charAt(i))-(int)('a')])
						return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * @pre: 0 < k < word.lenght(), word.length() <= 10
	 */
	private static int nChooseK(String word, int k)
	{
		int n = word.length();
		int ret = 1;
		for (int i=0; i<k; i++)
			ret = ret * (n-i) / (i+1);
		return ret;
	}
	
	private static boolean[] binToBool(String bin)
	{
		boolean[] ret = new boolean[bin.length()];
		for (int i=0; i<bin.length(); i++)
		{
			if (bin.charAt(i) == '0')
				ret[i] = false;
			if (bin.charAt(i) == '1')
				ret[i] = true;
		}
		return ret;
	}
	
	private static int countChar(String str, char c)
	{
	    int count = 0;
	    for (int i=0; i < str.length(); i++)
	    {    if (str.charAt(i) == c)
	            count++;
	    }
	    return count;
	}
	
	public static boolean[][] getAllLegalTemplates(String word, int k)
	{  // Q - 3
		int n = word.length();
		int nCk = nChooseK(word, k);
		boolean[][] templates = new boolean[nCk][n];
		String bin;
		int idx = 0;
		for (int i=0; i<Math.pow(2, n); i++)
		{
			bin = String.format("%" + String.valueOf(n) + "s", Integer.toBinaryString(i)).replace(' ', '0');
			if(checkLegalTemplate(word, binToBool(bin)) && countChar(bin, '1') == k)
			{
				templates[idx++] = binToBool(bin);
			}
		}
		return (Arrays.copyOfRange(templates, 0, idx));
	}
	
	
	/*
	 * @pre: puzzle is a legal puzzle constructed from word, guess is in [a...z]
	 */
	public static int applyGuess(char guess, String word, char[] puzzle) 
	{ // Q - 4
		int cnt = 0;
		for (int i=0; i<puzzle.length; i++)
		{
			if (word.charAt(i) == guess && puzzle[i] == HIDDEN_CHAR)
			{
				puzzle[i] = guess;
				cnt++;
			}
		}
		return cnt;
	}
	

	/*
	 * @pre: puzzle is a legal puzzle constructed from word
	 * @pre: puzzle contains at least one hidden character. 
	 * @pre: there are at least 2 letters that don't appear in word, and the user didn't guess
	 */
	public static char[] getHint(String word, char[] puzzle, boolean[] already_guessed) 
	{ // Q - 5
		Random  rnd  = new Random();
		rnd.setSeed(25);
		int num;
		char[] hint = new char[2];
		
		//"Wrong char"
		while(true)
		{//Exit loop if: char was not guessed && char is not in the word 
			num = (int)rnd.nextInt(26);
			if((!already_guessed[num] &&  word.indexOf((char)(num + (int)('a'))) == -1))
				break;
		}
		hint[0] = (char)(num + (int)('a'));
		
		//"Correct char"
		while(true)
		{//Exit loop if: char was not guessed && char is in the word && char is hidden
			num = (int)rnd.nextInt(26);
			if((!already_guessed[num] &&  word.indexOf((char)(num + (int)('a'))) != -1 && puzzle[word.indexOf((char)(num + (int)('a')))] == HIDDEN_CHAR))
				break;
		}
		hint[1] = (char)(num + (int)('a'));
		
		/*
		do 
		{
			num = (int)rnd.nextInt(26);
		}//Exit loop if: char was not guessed && char is not in the word 
		while (!(!already_guessed[num] &&  word.indexOf((char)(num + (int)('a'))) == -1));
		hint[0] = (char)(num + (int)('a'));
		
		//"Correct char"
		do 
		{
			num = (int)rnd.nextInt(26);
		}//Exit loop if: char was not guessed && char is in the word && char is hidden
		while (!(!already_guessed[num] &&  word.indexOf((char)(num + (int)('a'))) != -1 && puzzle[word.indexOf((char)(num + (int)('a')))] == HIDDEN_CHAR));
		hint[1] = (char)(num + (int)('a'));
		*/
		
		//Sort the hint[] array
		if (hint[1] < hint[0])
		{
			char tmp = hint[0];
			hint[0] = hint[1];
			hint[1] = tmp;
		}
		return hint;
	}


	public static char[] mainTemplateSettings(String word, Scanner inputScanner) 
	{ // Q - 6
		//a.
		System.out.println("--- stage Settings-");
		//b.
		while (true)
		{
			//i.
			System.out.println("Choose a (1) random or (2) manual template:");
			//ii.
			int settings = inputScanner.nextInt();
			///iii.
			if (settings == 1)
			{
				//1.
				System.out.println("Enter number of hidden characters:");
				//2.
				int k = inputScanner.nextInt();		//k == number of hidden chars
				boolean[][] templates = getAllLegalTemplates(word, k);
				
				//4.
				if (templates.length == 0)		//No possible templates
				{
					System.out.println("Cannot generate puzzle, try again.");
				}
				
				//3.
				else
				{
					Random  rnd  = new Random();
					rnd.setSeed(25);
					int num = (int)rnd.nextInt(templates.length);
					boolean[] template = templates[num];
					return createPuzzleFromTemplate(word, template);
					
				}
			}
			//iv.
			else if (settings == 2)
			{
				//1.
				System.out.println("Enter your puzzle template: ");
				//2.
				String inputTemplate = inputScanner.next();
				//3.
				int n = (inputTemplate.length()+1)/2;	//template length
				boolean[] template = new boolean[n];
				for (int i=0; i<n; i++)
				{
					if (inputTemplate.charAt(i*2) == 'X')
						template[i] = false;
					if (inputTemplate.charAt(i*2) == HIDDEN_CHAR)
						template[i] = true;
				}
				if (checkLegalTemplate(word, template))
				{
					return (createPuzzleFromTemplate(word, template));
				}
				//4.
				else
				{
					System.out.println("Cannot generate puzzle, try again.");
				}
			}
		}
	}
	
	private static boolean isPuzzleSolved(char[] puzzle)
	{
		for (int i=0; i< puzzle.length; i++)
		{
			if (puzzle[i] == HIDDEN_CHAR)
				return false;
		}
		return true;
	}
	
	public static void mainGame(String word, char[] puzzle, Scanner inputScanner)
	{ // Q - 7
		//a.
		System.out.println("--- Game stage ---");
		//b.
		int k = 0;		//How many hidden chars
		for (int i=0; i<puzzle.length; i++)
		{
			if (puzzle[i] == HIDDEN_CHAR)
				k++;
		}
		int tries = k + 3;
		boolean[] already_guessed = new boolean[26];
		//c.
		while (tries > 0)
		{
			System.out.println(Arrays.toString(puzzle));
			System.out.println("Enter your guess:");
			
			//d.
			char input  = inputScanner.next().charAt(0);
			
			//i.
			if (input == 'H')
			{
				char[] hints = getHint(word, puzzle, already_guessed);
				System.out.println("Here's a hint for you: choose either " + hints[0] +" or " + hints[1] + ".");
			}
			
			//ii.
			else if ((int)('a') <= (int)(input) && (int)(input) <= (int)('z'))
			{
				already_guessed[(int)(input) - (int)('a')] = true;
				//1.
				if (applyGuess(input, word, puzzle) != 0)
				{
					//(1).
					if(isPuzzleSolved(puzzle))
					{
						System.out.println("Congratulations! You solved the puzzle!");
						return;
					}
					//(2).
					else
					{
						tries--;
						System.out.println("Correct Guess, " + tries + " guesses left.");
					}
				}
				
				//2.
				else
				{
					tries--;
					System.out.println("Wrong Guess, " + tries + " guesses left.");
				}
				
			}
		}
		System.out.println("Game over!");
		return;
	}
				
				


/*************************************************************/
/********************* Don't change this ********************/
/*************************************************************/

	public static void main(String[] args) throws Exception 
	{/*
		if (args.length < 1){
			throw new Exception("You must specify one argument to this program");
		}
		String wordForPuzzle = args[0].toLowerCase();
		if (wordForPuzzle.length() > 10)
		{
			throw new Exception("The word should not contain more than 10 characters");
		}
		Scanner inputScanner = new Scanner(System.in);
		char[] puzzle = mainTemplateSettings(wordForPuzzle, inputScanner);
		mainGame(wordForPuzzle, puzzle, inputScanner);
		inputScanner.close();*/
		getAllLegalTemplates("abcde", 2);
	}


	public static void printSettingsMessage() 
	{
		System.out.println("--- Settings stage ---");
	}

	public static void printEnterWord() 
	{
		System.out.println("Enter word:");
	}
	
	public static void printSelectNumberOfHiddenChars()
	{
		System.out.println("Enter number of hidden characters:");
	}
	public static void printSelectTemplate() 
	{
		System.out.println("Choose a (1) random or (2) manual template:");
	}
	
	public static void printWrongTemplateParameters() 
	{
		System.out.println("Cannot generate puzzle, try again.");
	}
	
	public static void printEnterPuzzleTemplate() 
	{
		System.out.println("Enter your puzzle template:");
	}


	public static void printPuzzle(char[] puzzle) 
	{
		System.out.println(puzzle);
	}


	public static void printGameStageMessage() 
	{
		System.out.println("--- Game stage ---");
	}

	public static void printEnterYourGuessMessage() 
	{
		System.out.println("Enter your guess:");
	}

	public static void printHint(char[] hist)
	{
		System.out.println(String.format("Here's a hint for you: choose either %s or %s.", hist[0] ,hist[1]));
	}
	public static void printCorrectGuess(int attemptsNum) 
	{
		System.out.println("Correct Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWrongGuess(int attemptsNum) {
		System.out.println("Wrong Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWinMessage() 
	{
		System.out.println("Congratulations! You solved the puzzle!");
	}

	public static void printGameOver() 
	{
		System.out.println("Game over!");
	}

}
