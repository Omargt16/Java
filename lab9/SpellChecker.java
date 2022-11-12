package lab9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;
import javax.swing.JFileChooser;

public class SpellChecker {
	public static void main(String[] args) throws FileNotFoundException {

		HashSet<String> legalWords = new HashSet<>();
		TreeSet<String> results = new TreeSet<>();

		Scanner filein = new Scanner(
				new File("C:\\Users\\datre\\OneDrive\\Documentos\\Eclipse\\Labs\\src\\lab9\\words.txt"));
		while (filein.hasNext()) {
			String tk = filein.next();
			legalWords.add(tk.toLowerCase());
		}
		//System.out.println(legalWords.size());
		File filein2 = getInputFileNameFromUser();
		Scanner in = new Scanner(filein2);
		while (in.hasNext()) {
			String tk2 = in.next();
			tk2 = tk2.toLowerCase();
			results = corrections(tk2, legalWords);
			System.out.println(tk2 + " : ");
			if (results.size() == 0) {
				System.out.println("(no suggestions)");
			} else {
				for (String suggestion : results) {
					System.out.println(suggestion + ", ");
				}
			}
		}
	}

	static TreeSet corrections(String badWord, HashSet dictionary) {
		TreeSet<String> results = new TreeSet<>();
		StringBuilder sb = new StringBuilder(badWord);
		String word = "";
		char a, b;
		// Delete any one of the letters from the misspelled word.
		word = badWord;
		for (int i = 0; i < word.length(); i++) {
			sb = new StringBuilder(word);
			sb.deleteCharAt(i);
			word = sb.toString();
			if (dictionary.contains(word))
				results.add(word);
			word = badWord;
		}
		// Change any letter in the misspelled word to any other letter.
		word = badWord;
		for (int i = 0; i < word.length(); i++) {
			for (char ch = 'a'; ch <= 'z'; ch++) {
				word = word.substring(0, i) + ch + word.substring(i + 1);
				if (dictionary.contains(word))
					results.add(word);
			}
			word = badWord;
		}
		// Insert any letter at any point in the misspelled word.
		word = badWord;
		for (int i = 0; i < word.length(); i++) {
			for (char ch = 'a'; ch <= 'z'; ch++) {
				word = word.substring(0, i) + ch + word.substring(i);
				if (dictionary.contains(word))
					results.add(word);
				word = badWord;
			}
		}
		// Swap any two neighboring characters in the misspelled word.
		word = badWord;
		for (int i = 0; i < word.length() - 1; i++) {
			for (int j = i + 1; j < word.length(); j++) {
				a = word.charAt(i);
				b = word.charAt(j);
				word = word.substring(0, i) + b + word.substring(i + 1);
				word = word.substring(0, j) + a + word.substring(j + 1);
				if (dictionary.contains(word))
					results.add(word);
				word = badWord;
			}
		}
		// Insert a space at any point in the misspelled word (and check that both of the words that are produced are in the dictionary)
		word = badWord;
		for (int i = 1; i < word.length(); i++) {
			word = word.substring(0, i) + " " + word.substring(i);
			String[] splited = word.split("\\s+");
			if (dictionary.contains(splited[0]))
				results.add(splited[0]);
			if (dictionary.contains(splited[1]))
				results.add(splited[1]);
			word = badWord;
		}
		
		return results;
	}

	static File getInputFileNameFromUser() {
		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select File for Input");
		int option = fileDialog.showOpenDialog(null);
		if (option != JFileChooser.APPROVE_OPTION)
			return null;
		else
			return fileDialog.getSelectedFile();
	}
}
