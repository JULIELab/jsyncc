package de.julielab.jsyncc.readbooks.clindoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import de.julielab.jcore.types.Annotation;
import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import de.julielab.jsnycc.annotation.StandOffPos;
import de.julielab.jsnycc.annotation.StandOffSentence;
import de.julielab.jsnycc.annotation.StandOffToken;
import de.julielab.jsnycc.annotation.TextAnnotation;

public class FindSentencesByFramed
{
	static String modelSentences = "src/main/resources/FraMedSent/jsbd-framed.gz";
	static String jarSentences = "src/main/resources/FraMedSent/jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar";
	
	static String textfile = "C:/Users/C01638/Desktop/anonym-iterations/iter-01/07-clinical-section-iter-01/segment-labeling_anonym-orig.txt";
	
	public static void main(String[] args) throws IOException, InterruptedException, UIMAException
	{
//		String text = "Diagnose:\nDas ist ein Testsatz. Und das ist noch ein Satz. Das ist ein Testsatz.";
		
		String text = getOrigText(textfile);
		
		//System.out.println(getSentences(text));
		
		System.out.println(getSentencesNEW(text));
		
		//Files.write(Paths.get(textfile.replaceAll("orig", "framed")), getSentences(text).getBytes("UTF-8"));
	}

	public static String getSentencesNEW(String inputText) throws IOException, UIMAException
	{
		AnalysisEngine sentenceAE = AnalysisEngineFactory.createEngine("de.julielab.jcore.ae.jsbd.desc.jcore-jsbd-ae-medical-german");

		JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");
		jCas.setDocumentText(inputText);
		sentenceAE.process(jCas);

		return getTokSent(jCas, Sentence.type);
	}

	public static String getOrigText(String file) throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get(file));
		
		String out = "";
		
		for (int i = 0; i < lines.size(); i++)
		{
			out = out + "\n" + lines.get(i);
		}
		
		out = out.replaceFirst("\n", "");
		
		return out;
	}
	
	public static String getTokSent(JCas jCas, int type)
	{
		FSIterator<org.apache.uima.jcas.tcas.Annotation> textElements = jCas.getAnnotationIndex(type).iterator();
		String text = "";

		while (textElements.hasNext())
		{
			Annotation s = (Annotation) textElements.next();
			text = text + "\n" + s.getCoveredText();
		}
		return text;
	}
}
