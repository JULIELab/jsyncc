package de.julielab.jsyncc.annotation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AnnotationsToken")
public class AnnotationToken
{
	String idTokAnn = "";
	String start = "";
	String end = "";

	public String getIdTokAnn() {
		return idTokAnn;
	}
	public void setIdTokAnn(String idTokAnn) {
		this.idTokAnn = idTokAnn;
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
