package com.hl.newsagent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import org.jsoup.Jsoup;

public class NewsParser {
	private URL Url;
	private String headline;
	private String HtmlContent;
	private LinkedList<String> content;

	public NewsParser(String URL) {
		try {
			this.Url = new URL(URL);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					Url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				HtmlContent += inputLine;
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String printHtmlContents() {
		return HtmlContent;
	}

	public boolean isUrlValid() {
		return !(Url == null);
	}

	public void debug() {
		System.out.println("The URL is " + Url.toString());
	}

}
