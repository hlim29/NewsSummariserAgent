package com.hl.newsagent;
import java.util.*;

public class Main {
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		System.out.println("NewsAgent by Henry L , UTS, 2014. Version v1.0");
		String input = readURL();
		String[] inputs = input.split(" ");
		
		NewsParser news = new NewsParser(inputs[0]);
		
		boolean showRankAndId = askIdRank();
		boolean showExcluded = askExclusionList();
		boolean showFrequent = askFrequentList();
		
		
		if (news.isUrlValid()){
			
			news.populateSentencesList();
			news.createWordCount();
			news.processFrequentWords();
			news.processHeadline();
			news.calculateRelevance();
			news.printMostRelevant(showRankAndId);
			
			if (showExcluded){
				news.printExcludedWordList();
			}
			if (showFrequent){
				news.printFrequentWordList();
			}
		}
		else {
			System.out.println("The URL is invalid, please try again");
			sc.nextLine();
		}
	}
	
	private static boolean askIdRank(){
		System.out.print("Do you want the IDs and the rank of a sentence to be shown? (y/n) ");
		return sc.nextLine().toLowerCase().equals("y");
	}
	
	private static boolean askExclusionList(){
		System.out.print("Do you want to also see the excluded keywords? (y/n) ");
		return sc.nextLine().toLowerCase().equals("y");
	}
	
	private static boolean askFrequentList(){
		System.out.print("Do you want to also see the frequent words? (y/n) ");
		return sc.nextLine().toLowerCase().equals("y");
	}

	private static String readURL() {
		System.out.print("Please input a URL to summarise: ");
		return sc.nextLine();
	}

}
