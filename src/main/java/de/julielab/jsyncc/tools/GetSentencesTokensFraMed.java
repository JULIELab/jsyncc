package de.julielab.jsyncc.tools;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import de.julielab.jcore.types.Annotation;
import de.julielab.jcore.types.POSTag;
//import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import de.julielab.jsyncc.annotation.StandOffPos;
import de.julielab.jsyncc.annotation.StandOffSentence;
import de.julielab.jsyncc.annotation.StandOffToken;
import de.julielab.jsyncc.annotation.TextAnnoation;
import de.julielab.jsyncc.readbooks.BookReader;

public class GetSentencesTokensFraMed {
	static int index = 1;

	public static String sentences = "";
	public static String tokens = "";

	public static void runPipeline(String inputText, String longId) throws IOException, UIMAException {
		AnalysisEngine sentenceAE = AnalysisEngineFactory
				.createEngine("de.julielab.jcore.ae.jsbd.desc.jcore-jsbd-ae-medical-german");
		AnalysisEngine tokenAE = AnalysisEngineFactory
				.createEngine("de.julielab.jcore.ae.jtbd.desc.jcore-jtbd-ae-medical-german");
		AnalysisEngine posAE = AnalysisEngineFactory
				.createEngine("de.julielab.jcore.ae.jpos.desc.jcore-jpos-ae-medical-german");

		JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");

		jCas.setDocumentText(inputText);

		sentenceAE.process(jCas);
		tokenAE.process(jCas);
		posAE.process(jCas);

		sentences = "";
		tokens = "";

		sentences = getTokSent(jCas, Sentence.type);
		tokens = getTokSent(jCas, Token.type);

		ArrayList<StandOffSentence> annoSentences = new ArrayList<StandOffSentence>();
		annoSentences = getSentAnno(jCas, Sentence.type);

		ArrayList<StandOffToken> annoTokens = new ArrayList<StandOffToken>();
		annoTokens = getTokAnno(jCas, Token.type);

		ArrayList<StandOffPos> annoPos = new ArrayList<StandOffPos>();
		annoPos = getPOSAnno(jCas, POSTag.type);

		TextAnnoation tAnno = new TextAnnoation();

		tAnno.sentencesAnnotation = annoSentences;
		tAnno.tokenAnnotation = annoTokens;
		tAnno.posAnnotation = annoPos;
		tAnno.id = Integer.toString(index);
		tAnno.longId = longId;
		index++;

		BookReader.annotatedCorpus.add(tAnno);
	}

	public static ArrayList<StandOffPos> getPOSAnno(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		ArrayList<StandOffPos> posAnno = new ArrayList<StandOffPos>();

		while (elements.hasNext()) {
			POSTag p = (POSTag) elements.next();

			StandOffPos pAnno = new StandOffPos();

			pAnno.start = Integer.toString(p.getBegin());
			pAnno.end = Integer.toString(p.getEnd());
			pAnno.posTag = p.getValue();
			posAnno.add(pAnno);
		}
		return posAnno;
	}

	public static String getTokSent(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		String e = "";
		while (elements.hasNext()) {
			Annotation s = (Annotation) elements.next();
			e = e + "\n" + s.getCoveredText();
		}
		e = e.replaceFirst("\n", "");
		return e;
	}

	public static ArrayList<StandOffToken> getTokAnno(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		ArrayList<StandOffToken> tokAnno = new ArrayList<StandOffToken>();

		while (elements.hasNext()) {
			Annotation s = (Annotation) elements.next();
			StandOffToken tAnno = new StandOffToken();

			tAnno.start = Integer.toString(s.getBegin());
			tAnno.end = Integer.toString(s.getEnd());
			tokAnno.add(tAnno);
		}
		return tokAnno;
	}

	public static ArrayList<StandOffSentence> getSentAnno(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> elements = jCas.getAnnotationIndex(type).iterator();
		ArrayList<StandOffSentence> sentAnno = new ArrayList<StandOffSentence>();

		while (elements.hasNext()) {
			Annotation s = (Annotation) elements.next();
			StandOffSentence sAnno = new StandOffSentence();

			sAnno.start = Integer.toString(s.getBegin());
			sAnno.end = Integer.toString(s.getEnd());
			sentAnno.add(sAnno);
		}
		return sentAnno;
	}
}
