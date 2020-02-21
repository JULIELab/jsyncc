package de.julielab.jsyncc.annotation;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.julielab.jsyncc.annotation.StandOffPos;
import de.julielab.jsyncc.annotation.StandOffSentence;
import de.julielab.jsyncc.annotation.StandOffToken;
import de.julielab.jsyncc.annotation.TextAnnotation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "documentAnnotation")
public class TextAnnotation
{
	String id = "";
	public String longId = "";
	static String sentences = "";
	static String tokens = "";

	static Integer countSentences = 0;
	static Integer countTokens = 0;

	@XmlElement(name = "sent", type = StandOffSentence.class)
	public ArrayList<StandOffSentence> sentencesAnnotation = new ArrayList<StandOffSentence>();

	@XmlElement(name = "tok", type = StandOffToken.class)
	public ArrayList<StandOffToken> tokenAnnotation = new ArrayList<StandOffToken>();

	@XmlElement(name = "pos", type = StandOffPos.class)
	public ArrayList<StandOffPos> posAnnotation = new ArrayList<StandOffPos>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLongId() {
		return longId;
	}
	public void setLongId(String longId) {
		this.longId = longId;
	}

	public void setSentencesAnnotation(ArrayList<StandOffSentence> sentencesAnnotation) {
		this.sentencesAnnotation = sentencesAnnotation;
	}
	public ArrayList<StandOffSentence> getSentencesAnnotation() {
		return sentencesAnnotation;
	}

	public ArrayList<StandOffToken> getTokenAnnotation() {
		return tokenAnnotation;
	}
	public void setTokenAnnotation(ArrayList<StandOffToken> tokenAnnotation) {
		this.tokenAnnotation = tokenAnnotation;
	}

	public void setPosAnnotation(ArrayList<StandOffPos> posAnnotation) {
		this.posAnnotation = posAnnotation;
	}
	public ArrayList<StandOffPos> getPosAnnotation() {
		return posAnnotation;
	}

	public void setSentences(String sentences) {
		TextAnnotation.sentences = sentences;
	}
	public String getSentences() {
		return sentences;
	}

	public void setTokens(String tokens) {
		TextAnnotation.tokens = tokens;
	}
	public String getTokens() {
		return tokens;
	}

	public Integer getCountSentences() {
		return countSentences;
	}
	public void setCountSentences(Integer countSentences) {
		TextAnnotation.countSentences = countSentences;
	}

	public Integer getCountTokens() {
		return countTokens;
	}
	public void setCountTokens(Integer countTokens) {
		TextAnnotation.countTokens = countTokens;
	}
}
