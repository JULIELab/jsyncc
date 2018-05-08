package de.julielab.jsyncc.annotation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class AnnotatedCorpus {
	@XmlElement(name = "doc", type = TextAnnoation.class)
	private static List<TextAnnoation> listDocuments = new ArrayList<TextAnnoation>();

	public AnnotatedCorpus() {
	}

	public AnnotatedCorpus(List<TextAnnoation> listDocuments) {
		AnnotatedCorpus.listDocuments = listDocuments;
	}

	public List<TextAnnoation> getListDocuments() {
		return listDocuments;
	}

	public static void setListDocuments(List<TextAnnoation> listDocuments) {
		AnnotatedCorpus.listDocuments = listDocuments;
	}
}
