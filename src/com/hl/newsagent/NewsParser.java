package com.hl.newsagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsParser {
	private URL Url;
	private String headline;
	private String htmlContent;
	private Document parsedHtml;
	private LinkedList<String> content;
	private LinkedList<String> headlineKeywords = new LinkedList<String>();
	private LinkedList<NewsFragment> contents = new LinkedList<NewsFragment>();

	public NewsParser(String URL) {
		try {
			parsedHtml = Jsoup.connect(URL).get();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("HEADLINE: " + splitHeader(parsedHtml.body().select("h1").first().text()));
		System.out.println();
		
		
		Elements paragraphs = parsedHtml.body().select("div[class*=article] > p");
		//paragraphs = parsedHtml.body().select("div[class*=story] > p");
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		int id = 0;
		for (Element e : paragraphs){

			NewsFragment n = new NewsFragment(e.text());
			n.setId(id);
			id++;
			contents.add(n);
			
			/*String sentence = e.text();
			
			iterator.setText(sentence);
			int start = iterator.first();
			for (int end = iterator.next();
			    end != BreakIterator.DONE;
			    start = end, end = iterator.next()) {

				NewsFragment n = new NewsFragment(sentence.substring(start,end));
				n.setId(id);
				id++;
				contents.add(n);
			}*/
			
			
			//.out.println(e.text());
		}
		
		/*
		for (NewsFragment fragment : contents){
			System.out.println(fragment.getSentence());
		}*/

	}
	
	
	
	private String splitHeader(String header){
		String splittedHeader[] = header.split("[\\p{P} \\t\\n\\r]");
		
		
		
		String buffer = new String();
		for (String s : splittedHeader){
			//buffer += s;
			//buffer += "|";
			headlineKeywords.add(s);
		}
		return header;
		
	}
	
	public void calculateRelevance(){
		for (NewsFragment news : contents){
			for (String headlineKeyword : headlineKeywords){
				if (news.getSentence().contains(headlineKeyword))
					news.incrementHeadlineWord();
			}
		}
	}
	
	public void printMostRelevant(){
		Collections.sort(contents, new CompareByRank());
		
		LinkedList<NewsFragment> tempCollection = new LinkedList<NewsFragment>();
		
		for (int i = 0; i < 4; i++){
			tempCollection.add(contents.get(i));
		}
		
		Collections.sort(tempCollection, new CompareById());
		
		for (NewsFragment n : tempCollection){
			System.out.println(n.getSentence());
		}
	}
	
	private class CompareByRank implements Comparator<NewsFragment>{
		@Override
		public int compare(NewsFragment o1, NewsFragment o2) {
			return o2.getRank() - o1.getRank();
		}
	}
	
	private class CompareById implements Comparator<NewsFragment>{
		@Override
		public int compare(NewsFragment o1, NewsFragment o2) {
			return o1.getId() - o2.getId();
		}
	}

	public String getHtmlContents() {
		String temp = new String();
		for (String s : content){
			temp += s;
			temp += "\n\n";
		}
		return temp;
	}

	public boolean isUrlValid() {
		return !(parsedHtml == null);
	}

	public void debug() {
		//System.out.println("The URL is " + Url.toString());
	}

}
