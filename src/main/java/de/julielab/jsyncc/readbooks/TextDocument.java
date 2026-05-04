package de.julielab.jsyncc.readbooks;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "document")
public class TextDocument
{
	public String id = "";
	public String idLong = "";
	public String bookId = "";
	public String text = "";

	public String textDocumentType = "";
	public String heading = "";
	public List<String> topics = new ArrayList<String>();
	public String source = "";
	public String sourceShort = "";
	public ArrayList<String> inRelationOf = new ArrayList<String>();

	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}

	public String getIdLong()
	{
		return idLong;
	}
	public void setIdLong(String idLong)
	{
		this.idLong = idLong;
	}

	public String getBookId()
	{
		return bookId;
	}
	public void setBookId(String idLong)
	{
		this.bookId = idLong;
	}

	public String getText()
	{
		return text;
	}
	public void setText(String text)
	{
		this.text = text;
	}

	public String getDocumentType()
	{
		return textDocumentType;
	}
	public void setDocumentType(String type)
	{
		this.textDocumentType = type;
	}

	public String getHeading()
	{
		return heading;
	}
	public void setHeading(String heading)
	{
		this.heading = heading;
	}

	public List<String> getTopic()
	{
		return topics;
	}
	public void setTopic(List<String> topics)
	{
		this.topics = topics;
	}

	public String getSource()
	{
		return source;
	}
	public void setSource(String source)
	{
		this.source = source;
	}

	public String getSourcShort()
	{
		return sourceShort;
	}
	public void setSourcShort(String sourceShort)
	{
		this.sourceShort = sourceShort;
	}
}