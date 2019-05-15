package de.julielab.jsyncc.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.uima.UIMAException;

import de.julielab.jsyncc.annotation.TextAnnoation;
import de.julielab.jsyncc.checksum.CheckSum;
import de.julielab.jsyncc.readbooks.TextDocument;

public class TextDocumentOutputUtils
{
	public static void printDocuments(List<TextDocument> documentlist)		//test method
	{
		for (int i = 0; i < documentlist.size(); i++)
		{
			System.out.println(i + " heading: " + documentlist.get(i).getHeading());
			System.out.println("type: " + documentlist.get(i).getType());
			System.out.println("topic:" + documentlist.get(i).getTopic());
			System.out.println("<text>");
			System.out.println(documentlist.get(i).getText());
			System.out.println("</text>");
			System.out.println("in relation of:" + documentlist.get(i).getInRelationOf());
			System.out.println("source: " + documentlist.get(i).getSource());
			System.out.println("id: " + documentlist.get(i).getId());
			System.out.println("long id:" + documentlist.get(i).getIdLong());
			System.out.println("\n");
		}
	}
	
	public static void writeXML(List<TextDocument> documentlist, List<CheckSum> checkSumList, Path outDirXml) throws IOException
	{
		if (!(outDirXml.toFile().exists()))
		{
			Files.createDirectory(outDirXml);
		}

		try
		{
			JaxBxmlHandler.marshalCorpus(documentlist, new File(outDirXml + "/corpus.xml"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		try
		{
			JaxBxmlHandler.marshalCheckSum(checkSumList, new File(outDirXml + "/checkSums.xml"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeTxtFiles(Path outDirTxt, List<TextDocument> documentlist) throws IOException
	{
		String fullText = "";

		if (!(outDirTxt.toFile().exists()))
		{
			Files.createDirectory(outDirTxt);
		}

		for (int i = 0; i < documentlist.size(); i++)
		{
			String text = documentlist.get(i).text;
			fullText = fullText + text + "\n";

			System.out.println(i + "\t" + documentlist.get(i).getIdLong());
			String f = outDirTxt + "/" + (i+1) + "-" + documentlist.get(i).getIdLong() + ".txt";
			Files.write(Paths.get(f), documentlist.get(i).text.getBytes());
		}
		Files.write(Paths.get(outDirTxt + "/" + "jsynncc-text.txt"), fullText.getBytes());
	}

	public static void writeCheckSums(List<TextDocument> documentlist, List<CheckSum> checkSumList)
	{
		for (int i = 0; i < documentlist.size(); i++)
		{
			System.out.println(documentlist.get(i).getText());
			CheckSum checkSum = new CheckSum();
			checkSum.checkSumText = DigestUtils.md5Hex(documentlist.get(i).getText());
			checkSum.id = Integer.toString(i);
			checkSumList.add(checkSum);
		}
	}
	
	public static ArrayList<TextAnnoation> writeTxtFilesAndAnnotations(
			List<TextDocument> documents,
			String outTxt,
			String outXml
			) throws IOException, UIMAException {
		String fullText = "";
		String fullSent = "";
		String fullTokens = "";

		ArrayList<TextAnnoation> annotatedCorpus = new ArrayList<TextAnnoation>();

		for (int i = 0; i < documents.size(); i++) {
			String text = documents.get(i).text;

			TextAnnoation annotation = new TextAnnoation();
			annotation = PipelineSentencesTokensFraMed.runPipeline(text, documents.get(i).getIdLong(), documents.get(i).getId());

			annotatedCorpus.add(annotation);

			String sent = annotation.getSentences();
			String token = annotation.getTokens();

			fullText = fullText + text + "\n";
			fullSent = fullSent + sent + "\n";
			fullTokens = fullTokens + token + "\n";
		}

		Files.write(Paths.get(outTxt + "/" + "jsynncc-text.txt"), fullText.getBytes());
		System.out.println(outXml + "/" + "jsynncc-text.txt created successfully.");

		Files.write(Paths.get(outTxt + "/" + "jsynncc-sentences.txt"), fullSent.getBytes());
		System.out.println(outXml + "/" + "jsynncc-sentences.txt created successfully.");

		Files.write(Paths.get(outTxt + "/" + "jsynncc-tokens.txt"), fullTokens.getBytes());
		System.out.println(outXml + "/" + "jsynncc-tokens.txt created successfully.");

		String annoFile = "jsyncc-annotations.xml";

		try {
			JaxBxmlHandler.marshalAnnotation(annotatedCorpus, new File(outXml + "/" + annoFile));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println(outXml + "/" + annoFile + " created successfully.");

		ProcessBuilder pb = new ProcessBuilder(
			"tar",
			"cfzv",
			outXml + "/" + "jsyncc-annotations.xml.tar",
			"jsyncc-annotations.xml");

		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return annotatedCorpus;
	}
}