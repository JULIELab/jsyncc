package de.julielab.jsyncc.annotation;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "doc")
public class TextAnnoation {
	public String id = "";
	public String longId = "";

	@XmlElement(name = "sent", type = StandOffSentence.class)
	public ArrayList<StandOffSentence> sentencesAnnotation = new ArrayList<StandOffSentence>();

	@XmlElement(name = "tok", type = StandOffToken.class)
	public ArrayList<StandOffToken> tokenAnnotation = new ArrayList<StandOffToken>();

	@XmlElement(name = "pos", type = StandOffPos.class)
	public ArrayList<StandOffPos> posAnnotation = new ArrayList<StandOffPos>();

	public static String sentences = "";
	public static String tokens = "";

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
		TextAnnoation.sentences = sentences;
	}
	public String getSentences() {
		return sentences;
	}

	public void setTokens(String tokens) {
		TextAnnoation.tokens = tokens;
	}
	public String getTokens() {
		return tokens;
	}
}
