package de.julielab.jsyncc.annotation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AnnotationsSentence")
public class AnnotationSentence
{
	String idSentAnn = "";
	String start = "";
	String end = "";

	public String getIdSentAnn() {
		return idSentAnn;
	}
	public void setIdSentAnn(String idSentAnn) {
		this.idSentAnn = idSentAnn;
	}

	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}

}
