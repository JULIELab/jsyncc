package de.julielab.jsyncc.readbooks;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "document")
public class TextDocument
{
	public String id = "";
	public String idLong = "";
	
	public String text = "";
	public String type = "";
	public String heading = "";
	public ArrayList<String> topic = new ArrayList<String>();
	public String source = "";
	public ArrayList<String> inRelationOf = new ArrayList<String>();

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getIdLong() {
		return idLong;
	}
	public void setIdLong(String idLong) {
		this.idLong = idLong;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}

	public ArrayList<String> getTopic() {
		return topic;
	}
	public void setTopic(ArrayList<String> topic) {
		this.topic = topic;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public ArrayList<String> getInRelationOf() {
		return inRelationOf;
	}
	public void setInRelationOf(ArrayList<String> inRelationOf) {
		this.inRelationOf = inRelationOf;
	}
}