package de.julielab.jsyncc.readbooks;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class Corpus
{
	@XmlElement(name = "document", type = TextDocument.class)
	private static List<TextDocument> textDocuments = new ArrayList<TextDocument>();

	public Corpus(){}

	public Corpus(List<TextDocument> textDocuments)
	{
		Corpus.textDocuments = textDocuments;
	}

	public List<TextDocument> getListDocuments()
	{
		return textDocuments;
	}

	public static void setListDocuments(List<TextDocument> textDocuments)
	{
		Corpus.textDocuments = textDocuments;
	}
}
