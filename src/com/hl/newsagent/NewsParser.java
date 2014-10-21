package com.hl.newsagent;

import java.io.BufferedReader;
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
import java.util.TreeMap;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsParser {
	private int uniqueWordCount;
	private URL Url;
	private String headline;
	private String htmlContent;
	private Document parsedHtml;
	private LinkedList<String> content;
	private LinkedList<String> headlineKeywords = new LinkedList<String>();
	private LinkedList<NewsFragment> contents = new LinkedList<NewsFragment>();
	private Map<String, Integer> wordFreq = new HashMap<String, Integer>();
	private LinkedList<String> words = new LinkedList<String>();

	public NewsParser(String URL) {
		try {
			parsedHtml = Jsoup.connect(URL).get();
		} catch (IllegalArgumentException e){
			System.out.println("The URL is invalid: "
					+ e.getMessage());
			return;
		} catch (MalformedURLException e) {
			System.out.println("The URL is invalid: "
					+ e.getMessage());
			return;
		} catch (HttpStatusException e) {
			System.out.println("Error with getting the HTML: "
					+ e.getStatusCode());
			return;
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		System.out.println("HEADLINE: "
				+ splitHeader(parsedHtml.body().select("h1").first().text()));

		System.out.println();

		Elements paragraphs = null;

		paragraphs = parsedHtml.body().select("div[class*=article] > p");

		if (paragraphs.size() == 0) {
			paragraphs = parsedHtml.body().select("div[class*=story] > p");
		}

		if (paragraphs.size() == 0) {
			paragraphs = parsedHtml.body().select("div[class*=main] > p");
		}

		if (paragraphs.size() == 0) {
			paragraphs = parsedHtml.body().select("article > p");
		}

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		int id = 0;
		for (Element e : paragraphs) {

			NewsFragment n = new NewsFragment(e.text());
			n.setId(id);
			id++;
			contents.add(n);
		}

	}

	private String splitHeader(String header) {
		String splittedHeader[] = header.split("[\\p{P} \\t\\n\\r]");

		String buffer = new String();
		for (String s : splittedHeader) {
			// buffer += s;
			// buffer += "|";
			headlineKeywords.add(s);
		}
		return header;

	}

	public void calculateRelevance() {
		for (NewsFragment news : contents) {
			for (String headlineKeyword : headlineKeywords) {
				if (news.getSentence().contains(headlineKeyword))
					news.incrementHeadlineWord();
			}
		}
		
		
	}

	public void createWordCount() {
		String sentence;
		String[] wordArray;
		for (NewsFragment news : contents) {
			// for (String headlineKeyword : headlineKeywords){
			sentence = news.getSentence();
			wordArray = sentence.split("[\\p{P} \\t\\n\\r]");
			for (String s : wordArray) {
				Integer val = wordFreq.get(s);
				if (val != null) {
					wordFreq.put(s, new Integer(val + 1));
					uniqueWordCount++;
				} else {
					wordFreq.put(s, 1);
					words.add(s);
				}
			}

			// }
		}
	}

	public void printWordCount() {
		ValueComparator bvc = new ValueComparator(wordFreq);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

		sorted_map.putAll(wordFreq);

		List<String> keys = new LinkedList<String>(sorted_map.keySet());

		for (int i = 0; i < keys.size() / 5; i++) {
			if (keys.get(i).length() > 4) {
				System.out.println(keys.get(i));
			} 
		}

	}

	/**
	 * A private class used for sorting a Map by the value
	 * Sourced from: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
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

	public void printMostRelevant() {
		Collections.sort(contents, new CompareByRank());

		LinkedList<NewsFragment> tempCollection = new LinkedList<NewsFragment>();

		for (int i = 0; i < 4; i++) {
			tempCollection.add(contents.get(i));
		}

		Collections.sort(tempCollection, new CompareById());

		for (NewsFragment n : tempCollection) {
			System.out.println(n.getSentence());
		}
	}

	private class CompareByRank implements Comparator<NewsFragment> {
		@Override
		public int compare(NewsFragment o1, NewsFragment o2) {
			return o2.getRank() - o1.getRank();
		}
	}

	private class CompareById implements Comparator<NewsFragment> {
		@Override
		public int compare(NewsFragment o1, NewsFragment o2) {
			return o1.getId() - o2.getId();
		}
	}

	public String getHtmlContents() {
		String temp = new String();
		for (String s : content) {
			temp += s;
			temp += "\n\n";
		}
		return temp;
	}

	public boolean isUrlValid() {
		return !(parsedHtml == null);
	}


}
