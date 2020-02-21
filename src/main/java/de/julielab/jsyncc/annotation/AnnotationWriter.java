package de.julielab.jsyncc.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBException;

import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.JaxBxmlHandler;

public class AnnotationWriter
{
	static String xmlFileCorpus = "output/xml/corpus_full.xml";
	static File xmlCorpus = new File (xmlFileCorpus);

	static String xmlFileAnnotation = "annotation.xml";
	static File xmlAnnotation = new File(xmlFileAnnotation);

	static String modelSentences = "JarsModelsJCoRe/jsbd-framed.gz";
	static String jarSentences = "jarswithOutput/jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar";

	static String modelTokens = "JarsModelsJCoRe/jtbd-framed.gz";
	static String jarTokens = "jarswithOutput/jtbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar";

	static String modelPOS = "JarsModelsJCoRe/jpos-framed.gz";
	static String jarPOS = "JarsModelsJCoRe/jpos-2.3.0-SNAPSHOT-jar-with-dependencies.jar";

	public static void main(String[] args) throws IOException, InterruptedException
	{
		makeAnnotate();
	}

	public static void makeAnnotate() throws IOException, InterruptedException
	{
		List<TextDocument> ListDocuments = new ArrayList<TextDocument>();

		try
		{
			ListDocuments = JaxBxmlHandler.unmarshalCorpus(xmlCorpus);
			System.out.println(ListDocuments.size());
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
			System.out.println("exeption");
		}

		ArrayList<TextAnnotation> listAnnotation = new ArrayList<TextAnnotation>();

		int countSentences = 0;
		int countTokens = 0;

		int sumSentences = 0;
		int sumTokens = 0;

		HashSet<String> types = new HashSet<String>();

		int i;

		System.out.println("id\tSent\tTokens\tTypes");
		
		for (i = 0; i < ListDocuments.size(); i++)
		{
			String id = ListDocuments.get(i).id;
			String text = ListDocuments.get(i).text;
			String tempFile = "temp.txt";

			Files.write(Paths.get(tempFile), text.getBytes());

			// Sentences
			String sentences = "";

			try
			{
				ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarSentences, tempFile, modelSentences);
				Process process = pb.start();
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String s;
				while ((s = in.readLine()) != null)
				{
					sentences = sentences + s;
				}
				process.waitFor();
			}
			catch (IOException ioe) {}

			// Tokens
			String tokenAnno = "";
			HashSet<String> typesLocal = new HashSet<String>();

			try
			{
				ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarTokens, tempFile, modelTokens);
				Process process = pb.start();
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String t = "";
				boolean readTokens = true;

				while ((t = in.readLine()) != null)
				{
					System.out.print(" _" + t + "_ ");

					if (!(readTokens))
					{
						tokenAnno = t;
					}
					if (t.startsWith("token number"))
					{
						countTokens = Integer.parseInt(t.split(" ")[2]);
						readTokens = false;
					}
					if (readTokens)
					{
						types.add(t);
						typesLocal.add(t);
					}
				}
				process.waitFor();
			}
			catch (IOException ioe) {}

			System.out.println();
			
			// Part-Of-Speech
			ArrayList<String> pos = new ArrayList<String>();

			try
			{
				ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPOS, "p", tempFile, modelPOS, i + ".txt");
				Process process = pb.start();
				process.waitFor();

				List<String> posTXT = Files.readAllLines(Paths.get(i + ".txt"));

				for (Iterator<String> iterator = posTXT.iterator(); iterator.hasNext();)
				{
					String line = iterator.next();
					System.out.println("line " + line);
					
					String[] tempArray = line.split(" ");
					
					for (int j = 0; j < tempArray.length; j++)
					{
						String[] tokenElement = tempArray[j].split("\\|");
						pos.add(tokenElement[1]);
					}
				}
				Files.delete(Paths.get(i + ".txt"));
			}
			catch (IOException ioe) {}
			
			TextAnnotation annotation = new TextAnnotation();
			annotation.setId(id);

			String[] sentencesList = sentences.split(";");
			String[] tokenList = tokenAnno.split(";");

			ArrayList<StandOffSentence> listSentAnno = new ArrayList<StandOffSentence>();
			ArrayList<StandOffToken> listTokAnno = new ArrayList<StandOffToken>();

			for (int j = 0; j < sentencesList.length; j++)
			{
				String[] temp = sentencesList[j].split("-");

				String start = temp[0];
				String end = temp[1];

				StandOffSentence sentAnno = new StandOffSentence();
//				sentAnno.setIdSentAnn(Integer.toString(j));
				sentAnno.setStart(start);
				sentAnno.setEnd(end);
				listSentAnno.add(sentAnno);
			}

			for (int j = 0; j < tokenList.length; j++)
			{
				String[] temp = tokenList[j].split("-");
				String start = temp[0];
				String end = temp[1];

				StandOffToken tokAnno = new StandOffToken();
//				tokAnno.setIdTokAnn(Integer.toString(j));
				tokAnno.setStart(start);
				tokAnno.setEnd(end);
				listTokAnno.add(tokAnno);
			}

			annotation.setSentencesAnnotation(listSentAnno);
			annotation.setTokenAnnotation(listTokAnno);

			listAnnotation.add(annotation);

			countSentences = sentences.split(";").length;

			sumSentences = sumSentences + countSentences;
			sumTokens = sumTokens + countTokens;

			System.out.println("-");
			System.out.println("listSentAnno.size() " + listSentAnno.size());
			System.out.println("listTokAnno.size() " + listTokAnno.size());
			System.out.println("pos.size() " + pos.size());
			System.out.println();
		}

		double avgSent = Math.round(	((double)sumSentences	/ (double)i) * 100 ) / 100.;
		double avgTok = Math.round(		((double)sumTokens		/ (double)i) * 100 ) / 100.;
		double avgTyp = Math.round(		((double)types.size()	/ (double)i) * 100 ) / 100.;
		double avgSentLen = Math.round(	((double)sumTokens		/ (double)sumSentences) * 100 ) / 100.;

		System.out.println();
		System.out.println("sum" + "\t" + sumSentences + "\t"+ sumTokens + "\t" + types.size());
		System.out.println("avg" + "\t" + avgSent + "\t"+ avgTok + "\t" + avgTyp + "\t" + avgSentLen);

		try
		{
			JaxBxmlHandler.marshalAnnotation(listAnnotation, xmlAnnotation);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("");
	}
}
