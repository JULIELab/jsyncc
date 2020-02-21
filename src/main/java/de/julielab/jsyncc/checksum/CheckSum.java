package de.julielab.jsyncc.checksum;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.digest.DigestUtils;

import de.julielab.jsyncc.readbooks.TextDocument;

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

	public static List<CheckSum> createCheckSums(List<TextDocument> listDocuments) {
		List<CheckSum> listCheckSum = new ArrayList<>();

		for (int i = 0; i < listDocuments.size(); i++) {
			String text = listDocuments.get(i).getText();

			CheckSum checkSum = new CheckSum();
			checkSum.setCheckSumText(DigestUtils.md5Hex(text));
			checkSum.setId(listDocuments.get(i).getId());
			checkSum.setIdLong(listDocuments.get(i).getIdLong());

			listCheckSum.add(checkSum);
		}

		return listCheckSum;
	}
	
}
