package com.hl.newsagent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsParser {
	private int uniqueWordCount;
	private String headline;
	private String htmlContent;
	private String Url;
	private Document parsedHtml;
	private LinkedList<String> headlineKeywords = new LinkedList<String>();
	private LinkedList<String> frequentWords = new LinkedList<String>();
	private LinkedList<String> exclusionList = new LinkedList<String>();
	private LinkedList<NewsFragment> contents = new LinkedList<NewsFragment>();
	private Map<String, Integer> wordFreq = new HashMap<String, Integer>();

	public NewsParser(String URL) {
		if (!parseUrl(URL))
			return;
		else
			this.Url = URL;
		headline = parsedHtml.body().select("h1").first().text();
	}

	public void processHeadline() {
		String splittedHeader[] = headline.split("[\\p{P} \\t\\n\\r]");

		for (String s : splittedHeader) {
			if (!exclusionList.contains(s))
				headlineKeywords.add(s);
		}
		System.out.println();
		System.out.println("HEADLINE: " + headline);
		System.out.println();
	}

	public void populateSentencesList() {
		Elements paragraphs = null;
		paragraphs = retrieveHtmlContents(paragraphs);
		if (paragraphs == null) {
			System.out.println("Unable to extract the article");
			return;
		} else {
			BreakIterator iterator = BreakIterator
					.getSentenceInstance(Locale.US);
			int id = 0;
			for (Element e : paragraphs) {
				iterator.setText(e.text());
				int start = iterator.first();
				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
						.next()) {
					NewsFragment n = new NewsFragment(e.text().substring(start,
							end));
					n.setId(id);
					id++;
					contents.add(n);
				}

			}
		}
	}

	private boolean parseUrl(String URL) {
		try {
			parsedHtml = Jsoup.connect(URL).get();
		} catch (IllegalArgumentException e) {
			System.out.println("The URL is invalid: " + e.getMessage());
			return false;
		} catch (MalformedURLException e) {
			System.out.println("The URL is invalid: " + e.getMessage());
			return false;
		} catch (HttpStatusException e) {
			System.out.println("Error with getting the HTML: "
					+ e.getStatusCode());
			return false;
		} catch (IOException e) {
			// e.printStackTrace();\
			return false;
		}
		return true;
	}

	/**
	 * Loops through common HTML layouts in order to retrieve the news article
	 * Returns null if no valid text is found
	 * 
	 * @param paragraphs
	 * @return
	 */
	private Elements retrieveHtmlContents(Elements paragraphs) {
		String[] htmlLayouts = { "div[class*=article] > p",
				"div[class*=story] > p", "div[class*=main] > p", "article > p" };
		int i = 0;
		paragraphs = parsedHtml.body().select(htmlLayouts[i]);
		while (paragraphs.size() == 0 && i < htmlLayouts.length) {
			paragraphs = parsedHtml.body().select(htmlLayouts[i]);
			i++;
		}
		return paragraphs;
	}

	public void calculateRelevance() {
		for (NewsFragment news : contents) {
			for (String headlineKeyword : headlineKeywords) {
				if (news.getSentence().contains(headlineKeyword))
					news.incrementHeadlineWord();
			}
		}

		for (NewsFragment news : contents) {
			for (String word : frequentWords) {
				if (news.getSentence().toLowerCase()
						.contains(word.toLowerCase()))
					news.incrementFrequentWord();
			}
		}

	}

	public void createWordCount() {
		String sentence;
		String[] wordArray;
		for (NewsFragment news : contents) {
			sentence = news.getSentence();
			wordArray = sentence.split("[\\p{P} \\t\\n\\r]");
			for (String s : wordArray) {
				// Converts the string to lowercase
				s = s.toLowerCase();

				// Retrieves the string from the hashmap, if available
				Integer val = wordFreq.get(s);

				if (val != null) {
					// If it's not available, loop through the map until it
					// finds a word that
					// begins with something similar
					// E.g. 'working' begins with 'work', therefore part of the
					// same word
					for (Entry<String, Integer> e : wordFreq.entrySet()) {
						if (e.getKey().startsWith(
								s.substring(0, s.length() / 2))) {
							wordFreq.put(s, new Integer(val + 1));
							uniqueWordCount++;
						}
					}
					wordFreq.put(s, new Integer(val + 1));
					uniqueWordCount++;
				} else {
					// If the word is available, increment the value in the
					// hashmap
					wordFreq.put(s, 1);
				}
			}
		}
	}

	/**
	 * Sorts the most used words
	 * Also writes the frequent words into a CSV file
	 */
	public void processFrequentWords() {
		ValueComparator bvc = new ValueComparator(wordFreq);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

		sorted_map.putAll(wordFreq);

		List<String> keys = new LinkedList<String>(sorted_map.keySet());

		List<String> veryFrequentWords = new LinkedList<String>();

		for (int i = 0; i < keys.size() / 2; i++) {
			veryFrequentWords.add(keys.get(i));
		}
		
		writeToFile(veryFrequentWords);

		for (int i = 0; i < keys.size() / 3; i++) {
			if (!exclusionList.contains(keys.get(i)))
				frequentWords.add(keys.get(i));
		}

	}

	/**
	 * This function creates a new CSV file. The data in this file is populated from the frequent words
	 * in the current bot usage session
	 * @param veryFrequentWords A LinkedList that contains the most frequently used words
	 * @param csvFile The path to the CSV file
	 */
	private void createNewFile(List<String> veryFrequentWords, String csvFile) {
		try {
			FileWriter writer = new FileWriter(csvFile);

			for (String s : veryFrequentWords) {
				writer.append(s);
				writer.append(",");
				writer.append("1");
				writer.append("\n");
			}
			writer.flush();
			writer.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Overwrites the CSV file with the new values. 
	 * Increments the number alongside the word when the word is also used in the article
	 * @param veryFrequentWords A LinkedList that contains the most frequently used words
	 */
	private void writeToFile(List<String> veryFrequentWords) {
		//Variables setup
		String csvFile = "./frequent.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			Map<String, Integer> maps = new HashMap<String, Integer>();

			br = new BufferedReader(new FileReader(csvFile));

			while ((line = br.readLine()) != null) {
				//Delimit the line by comma, as it is a comma separated file
				String[] wordEntry = line.split(cvsSplitBy);
				int wordUsage = Integer.parseInt(wordEntry[1]);

				//If the word in the CSV is also a frequent word in this article, then increment the frequency
				//Otherwise, decrement it.
				if (veryFrequentWords.contains(wordEntry[0]))
					wordUsage++;
				else
					wordUsage--;

				maps.put(wordEntry[0], wordUsage);
			}

			//Writes in the new words and values
			FileWriter writer = new FileWriter(csvFile);
			for (Map.Entry<String, Integer> entry : maps.entrySet()) {
				if (entry.getValue() > 2) {
					exclusionList.add(entry.getKey());
				}
				writer.append(entry.getKey());
				writer.append(",");
				writer.append(String.valueOf(entry.getValue()));
				writer.append("\n");
			}

			//Close off the file IO connection
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			// If no frequent word file exists, create a new one
			createNewFile(veryFrequentWords, csvFile);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * A private class used for sorting a Map by the value Sourced from:
	 * http://stackoverflow
	 * .com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	 * 
	 * @author user157196 from StackOverflow
	 *
	 */
	private class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;

		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	/**
	 * This method takes the five most relevant sentences, sorts it by its
	 * written order, and prints it
	 */
	public void printMostRelevant(boolean showExtra) {
		// Sort the contents list (the sentences) by its rank
		Collections.sort(contents, new CompareByRank());

		LinkedList<NewsFragment> tempCollection = new LinkedList<NewsFragment>();

		// Take the first five relevant sentences
		for (int i = 0; i < 5; i++) {
			tempCollection.add(contents.get(i));
		}

		// Sort the sentences by its Id (i.e. its chronological order in the
		// article)
		Collections.sort(tempCollection, new CompareById());

		for (NewsFragment n : tempCollection) {
			if (showExtra){
				System.out.println(n.getId() + "\t" + n.getRank() + "\t" + n.getSentence());
			} else {
				System.out.println("\u2022 " + n.getSentence());
			}
			
		}
		
		System.out.println(exclusionList);
	}
	

	/**
	 * A comparator class. Used for sorting sentences by their ranks
	 * 
	 * @author Henry
	 *
	 */
	private class CompareByRank implements Comparator<NewsFragment> {
		@Override
		public int compare(NewsFragment o1, NewsFragment o2) {
			return o2.getRank() - o1.getRank();
		}
	}

	/**
	 * A comparator class. Used for sorting sentences by their Id (written order
	 * in the article)
	 * 
	 * @author Henry
	 *
	 */
	private class CompareById implements Comparator<NewsFragment> {
		@Override
		public int compare(NewsFragment o1, NewsFragment o2) {
			return o1.getId() - o2.getId();
		}
	}

	public boolean isUrlValid() {
		return !(parsedHtml == null);
	}

	public void printContents() {
		for (NewsFragment s : contents) {
			System.out.println(s.getSentence());
		}
	}
	
	public void printExcludedWordList(){
		System.out.println();
		System.out.println("The excluded word list is as follows: ");
		for (String s : exclusionList){
			System.out.println("\u2022 " + s);
		}
	}
	
	public void printFrequentWordList(){
		System.out.println();
		System.out.println("The frequent word list is as follows: ");
		for (String s : frequentWords){
			System.out.println("\u2022 " + s);
		}
	}

}
