package com.hl.newsagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

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

	public NewsParser(String URL) {
		try {/*
			this.Url = new URL(URL);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					Url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				htmlContent += inputLine;
			in.close();*/
			parsedHtml = Jsoup.connect(URL).get();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//parsedHtml = Jsoup.parse(htmlContent);
		System.out.println("Header is: " + parsedHtml.body().select("h1").first().text());
		Elements paragraphs = parsedHtml.body().select("div[class*=story] > p");
		//paragraphs = parsedHtml.body().select("div[class*=story] > p");
		for (Element e : paragraphs){
			System.out.println(e.text());
		}
		//System.out.println(paragraphs.first().text());
	}

	public String getHtmlContents() {
		String temp = new String();
		for (String s : content){
			temp += s;
			temp += "\n";
		}
		return temp;
	}

	public boolean isUrlValid() {
		return !(Url == null);
	}

	public void debug() {
		System.out.println("The URL is " + Url.toString());
	}

}
