package de.julielab.jsnycc.annotation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "corpus")
public class AnnotatedCorpus
{
	@XmlElement(name = "documentAnnotation", type = TextAnnotation.class)
	private static List<TextAnnotation> textDocuments = new ArrayList<TextAnnotation>();

	public AnnotatedCorpus(){}

	public AnnotatedCorpus(List<TextAnnotation> textDocuments) {
		AnnotatedCorpus.textDocuments = textDocuments;
	}

	public List<TextAnnotation> getListDocuments() {
		return textDocuments;
	}

	public static void setListDocuments(List<TextAnnotation> textDocuments) {
		AnnotatedCorpus.textDocuments = textDocuments;
	}
}
