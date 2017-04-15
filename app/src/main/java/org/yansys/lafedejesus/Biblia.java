package org.yansys.lafedejesus;

public class Biblia {
	
	private int book = 0;
	private int chapter = 0;
	private int verse = 0;
	private String scripture = "";
	
	public Biblia() {
		super();
		this.book = 0;
		this.chapter = 0;
		this.verse = 0;
		this.scripture = "";
	}
	
	public Biblia(int book, int chapter, int verse, String scripture) {
		super();
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		this.scripture = scripture;
	}
	
	public int getBook() {
		return book;
	}
	public void setBook(int book) {
		this.book = book;
	}
	public int getChapter() {
		return chapter;
	}
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}
	public int getVerse() {
		return verse;
	}
	public void setVerse(int verse) {
		this.verse = verse;
	}
	public String getScripture() {
		return scripture;
	}
	public void setScripture(String scripture) {
		this.scripture = scripture;
	}

	
}
