package de.julielab.jsyncc.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ContextedException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ExtractionUtils {

	public static String getContentByTika(String pdfPath) throws ContextedException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(new File(pdfPath)));) {
			Parser parser = new AutoDetectParser();
			ContentHandler handler = new BodyContentHandler(-1);
			// -1 == no limit
			Metadata metadata = new Metadata();
			parser.parse(is, handler, metadata, new ParseContext());
			return handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			throw new ContextedException(e);
		}
	}

	public static String getContentByTika(Path pdfPath) throws ContextedException {
		try (InputStream is = new BufferedInputStream(new FileInputStream(new File(pdfPath.toString())));) {
			Parser parser = new AutoDetectParser();
			ContentHandler handler = new BodyContentHandler(-1);
			// -1 == no limit
			Metadata metadata = new Metadata();
			parser.parse(is, handler, metadata, new ParseContext());
			return handler.toString();
		} catch (IOException | SAXException | TikaException e) {
			throw new ContextedException(e);
		}
	}

	public static String getContentByPdftoHTML(Path pdfPath) throws ContextedException {
		String plainText = "";
		List<String> allLines;

		try {
			ProcessBuilder pb = new ProcessBuilder("pdftohtml", pdfPath.toString());
			Process p = pb.start();
			p.waitFor();
		} catch (IOException e1) {
			throw new ContextedException(e1);
		} catch (InterruptedException e2) {
			throw new ContextedException(e2);
		}

		File textFile = new File(pdfPath.toString().replaceAll("\\.pdf", "s.html"));
		try {
			allLines = FileUtils.readLines(textFile, "UTF-8");
		} catch (IOException e) {
			throw new ContextedException(e);
		}

		for (int i = 0; i < allLines.size(); i++) {
			plainText = plainText + allLines.get(i) + "\n";
		}
		return plainText;
	}

	public static String getContentByPdftoHTML(Path pdfPath, String arg) throws ContextedException {
		String plainText = "";
		List<String> allLines;

		try {
			ProcessBuilder pb = new ProcessBuilder("pdftohtml", arg, pdfPath.toString());
			Process p = pb.start();
			p.waitFor();
		} catch (IOException e1) {
			throw new ContextedException(e1);
		} catch (InterruptedException e2) {
			throw new ContextedException(e2);
		}

		File textFile = new File(pdfPath.toString().replaceAll("\\.pdf", "s.html"));
		try {
			allLines = FileUtils.readLines(textFile, "UTF-8");
		} catch (IOException e) {
			throw new ContextedException(e);
		}
		for (int i = 0; i < allLines.size(); i++) {
			plainText = plainText + allLines.get(i) + "\n";
		}

		return plainText;
	}

	public static String getContentByPdftotext(Path pdfPath) throws ContextedException {
		String plainText = "";
		List<String> allLines;

		try {
			ProcessBuilder pb = new ProcessBuilder("pdftotext", pdfPath.toString());
			Process p = pb.start();
			p.waitFor();
		} catch (IOException e1) {
			throw new ContextedException(e1);
		} catch (InterruptedException e2) {
			throw new ContextedException(e2);
		}

		File textFile = new File(pdfPath.toString().replaceAll("pdf", "txt"));
		try {
			allLines = FileUtils.readLines(textFile, "UTF-8");
		} catch (IOException e) {
			throw new ContextedException(e);
		}

		for (int i = 0; i < allLines.size(); i++) {
			plainText = plainText + allLines.get(i) + "\n";
		}
		return plainText;
	}

	public static String getContentByPdftotext(Path pdfPath, String arg) throws ContextedException {
		String plainText = "";
		List<String> allLines;

		try {
			ProcessBuilder pb = new ProcessBuilder("pdftotext", arg, pdfPath.toString());
			Process p = pb.start();
			p.waitFor();
		} catch (IOException e1) {
			throw new ContextedException(e1);
		} catch (InterruptedException e2) {
			throw new ContextedException(e2);
		}

		File textFile = new File(pdfPath.toString().replaceAll("pdf", "txt"));
		try {
			allLines = FileUtils.readLines(textFile, "UTF-8");
		} catch (IOException e) {
			throw new ContextedException(e);
		}
		for (int i = 0; i < allLines.size(); i++) {
			plainText = plainText + allLines.get(i) + "\n";
		}

		return plainText;
	}

	// public static void writeCheckSums()
	// {
	// for (int i = 0; i < ListDocuments.size(); i++)
	// {
	// System.out.println(ListDocuments.get(i).getText());
	//
	// CheckSum checkSum = new CheckSum();
	// checkSum.checkSumText =
	// DigestUtils.md5Hex(ListDocuments.get(i).getText());
	// checkSum.id = Integer.toString(i);
	// listCheckSum.add(checkSum);
	// }
	// }

	// public static void writeXML()
	// {
	// try
	// {
	// JaxBxmlHandler.marshalCorpus(ListDocuments, new
	// File("output/xml/corpus.xml"));
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// catch (JAXBException e)
	// {
	// e.printStackTrace();
	// }
	//
	// try
	// {
	// JaxBxmlHandler.marshalCheckSum(listCheckSum, new
	// File("output/checkSums.xml"));
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// catch (JAXBException e)
	// {
	// e.printStackTrace();
	// }
	// }

}
