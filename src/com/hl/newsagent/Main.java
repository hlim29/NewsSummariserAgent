package com.hl.newsagent;
import java.util.*;

public class Main {
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		NewsParser news = new NewsParser(readURL());
		if (news.isUrlValid()){
			news.debug();
			System.out.println(news.getHtmlContents());
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
