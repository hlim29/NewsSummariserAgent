package com.hl.newsagent;
import java.util.*;

public class Main {
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("NewsAgent by Henry L , UTS, 2014. Version v0.9");
		NewsParser news = new NewsParser(readURL());
		if (news.isUrlValid()){
			news.createWordCount();
			news.sortFrequentWords();
			
			news.calculateRelevance();
			
			news.printMostRelevant();
			
			//System.out.print(news.)
			//news.printContents();
		}
		else {
			System.out.println("The URL is invalid, please try again");
			sc.nextLine();
			main(args);
		}
			
	}

	private static String readURL() {
		System.out.print("Please input a URL for me to parse: ");
		return sc.nextLine();
	}

}
