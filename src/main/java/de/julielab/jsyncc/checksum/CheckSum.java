package de.julielab.jsyncc.checksum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "checkSum")
public class CheckSum {

	public String id = "";
	public String idLong = "";
	public String checkSumText = "";

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

	public String getCheckSumText() {
		return checkSumText;
	}

	public void setCheckSumText(String checkText) {
		this.checkSumText = checkText;
	}

	@Override
	public String toString() {
		return "CheckSum [id=" + id + ", checkText=" + checkSumText + "]";
	}

}
