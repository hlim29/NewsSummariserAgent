package com.hl.newsagent;

public class NewsFragment {
	private int Id;
	private String sentence;
	private int rank;
	private boolean isRank;
	
	public NewsFragment(String sentence){
		this.sentence = sentence;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}
	
	public void incrementHeadlineWord(){
		rank += 10;
	}
	
	public void incrementFrequentWord(){
		rank += 3;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isRank() {
		return isRank;
	}

	public void setRank(boolean isRank) {
		this.isRank = isRank;
	}

	
	
}
