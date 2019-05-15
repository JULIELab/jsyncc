package de.julielab.jsyncc.checksum;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "listCheckSums")
public class CollecedCheckSums
{
	@XmlElement(name = "checkSum", type = CheckSum.class)
	private static List<CheckSum> listCheckSums = new ArrayList<CheckSum>();

	public CollecedCheckSums(){}

	public CollecedCheckSums(List<CheckSum> listCheckSums) {
		this.listCheckSums = listCheckSums;
	}

	public List<CheckSum> getListCheckSums() {
		return listCheckSums;
	}
	
	public static void setListDocuments(List<CheckSum> listCheckSums) {
		CollecedCheckSums.listCheckSums = listCheckSums;
	}
}
