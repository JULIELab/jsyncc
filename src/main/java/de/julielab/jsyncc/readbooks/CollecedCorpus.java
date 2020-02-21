package de.julielab.jsyncc.readbooks;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class CollecedCorpus
{
	@XmlElement(name = "document", type = TextDocument.class)
	private static List<TextDocument> listDocuments = new ArrayList<TextDocument>();

	public CollecedCorpus(){}

	public CollecedCorpus(List<TextDocument> listDocuments) {
		this.listDocuments = listDocuments;
	}

	public List<TextDocument> getListDocuments() {
		return listDocuments;
	}
	
	public static void setListDocuments(List<TextDocument> listDocuments) {
		CollecedCorpus.listDocuments = listDocuments;
	}
}
