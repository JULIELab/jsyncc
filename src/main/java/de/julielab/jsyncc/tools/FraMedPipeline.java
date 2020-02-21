package de.julielab.jsyncc.tools;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import de.julielab.jcore.types.Annotation;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;

public class FraMedPipeline {
	static Set<String> types = new HashSet<String>();

	private static int sentencesSize;
	private static int tokensSize;
	private static int typesSize;

	private static Set<String> typesSet = new HashSet<String>();

	private static int sentencesSizeAll;
	private static int tokensSizeAll;
	// private static int typesSizeAll;

	private static String sent = "";
	private static String sentAll = "";

	public static void count(String inputText) throws IOException, UIMAException {
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

		sent = "";
		sentencesSize = countSenTok(jCas, Sentence.type, "Sentences");
		tokensSize = countSenTok(jCas, Token.type, "Tokens"); // set the types
		typesSize = countTypes(jCas);

		// System.out.println("TSI: " + tokensSize);

		jCas.reset();

		sentencesSizeAll = sentencesSizeAll + sentencesSize;
		tokensSizeAll = tokensSizeAll + tokensSize;
		// typesSize = typesSizeAll + typesSize;

		// System.out.println("TSIA: " + tokensSizeAll);

		if (sent.startsWith("\n")) {
			sent = sent.replaceFirst("\n", "");
		}

		if (sent.endsWith("\n")) {
			sent = sent.substring(0, sent.length() - 1);
		}

		if (sentAll.startsWith("\n")) {
			sentAll = sentAll.replaceFirst("\n", "");
		}

		if (sentAll.endsWith("\n")) {
			sentAll = sentAll.substring(0, sent.length() - 1);
		}
	}

	public static void printType(JCas jCas, int type) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> sents = jCas.getAnnotationIndex(type).iterator();

		while (sents.hasNext()) {
			Annotation s = (Annotation) sents.next();
			System.out.println(s.getBegin() + "-" + s.getEnd() + ": " + s.getCoveredText());
		}
	}

	public static String getSentences(String inputText) throws IOException, UIMAException {
		AnalysisEngine sentenceAE = AnalysisEngineFactory
				.createEngine("de.julielab.jcore.ae.jsbd.desc.jcore-jsbd-ae-medical-german");
		JCas jCas = JCasFactory.createJCas("de.julielab.jcore.types.jcore-all-types");
		jCas.setDocumentText(inputText);
		sentenceAE.process(jCas);

		String sent = "";

		FSIterator<org.apache.uima.jcas.tcas.Annotation> sents = jCas.getAnnotationIndex(Sentence.type).iterator();

		while (sents.hasNext()) {
			Annotation s = (Annotation) sents.next();
			sent = sent + "\n" + s.getCoveredText();
		}

		sent = sent.replaceFirst("\n", "");

		return sent;
	}

	public static int countSenTok(JCas jCas, int type, String T) {
		FSIterator<org.apache.uima.jcas.tcas.Annotation> sents = jCas.getAnnotationIndex(type).iterator();

		int i = 0;

		while (sents.hasNext()) {
			Annotation s = (Annotation) sents.next();
			i++;
			// System.out.println(i+ " " + s.getBegin() + "-" + s.getEnd() + ":
			// " + s.getCoveredText());

			if (type == Sentence.type) {
				// sent = sent + "\n" + i + " " + s.getCoveredText();
				// sentAll = sentAll + "\n" + i + " " + s.getCoveredText();

				sent = sent + "\n" + s.getCoveredText();
				sentAll = sentAll + "\n" + s.getCoveredText();
			}
		}
		System.out.println(T + " " + i);
		return i;
	}

	public static int countTypes(JCas jCas) {
		int type = Token.type;

		Set<String> types = new HashSet<String>();
		FSIterator<org.apache.uima.jcas.tcas.Annotation> sents = jCas.getAnnotationIndex(type).iterator();
		while (sents.hasNext()) {
			Annotation s = (Annotation) sents.next();
			types.add(s.getCoveredText());
			typesSet.add(s.getCoveredText());
		}
		// System.out.println(T + " " + types.size());
		return types.size();
	}

	public static Set<String> getTypes() {
		return types;
	}

	public static void setTypes(Set<String> types) {
		FraMedPipeline.types = types;
	}

	public static int getSentencesSize() {
		return sentencesSize;
	}

	public static void setSentencesSize(int sentencesSize) {
		FraMedPipeline.sentencesSize = sentencesSize;
	}

	public static int getTokensSize() {
		return tokensSize;
	}

	public static void setTokensSize(int tokensSize) {
		FraMedPipeline.tokensSize = tokensSize;
	}

	public static int getTypesSize() {
		return typesSize;
	}

	public static void setTypesSize(int typesSize) {
		FraMedPipeline.typesSize = typesSize;
	}

	public static Set<String> getTypesSet() {
		return typesSet;
	}

	public static void setTypesSet(Set<String> typesSet) {
		FraMedPipeline.typesSet = typesSet;
	}

	public static int getSentencesSizeAll() {
		return sentencesSizeAll;
	}

	public static void setSentencesSizeAll(int sentencesSizeAll) {
		FraMedPipeline.sentencesSizeAll = sentencesSizeAll;
	}

	public static int getTokensSizeAll() {
		return tokensSizeAll;
	}

	public static void setTokensSizeAll(int tokensSizeAll) {
		FraMedPipeline.tokensSizeAll = tokensSizeAll;
	}

	public static String getSent() {
		return sent;
	}

	public static void setSent(String sent) {
		FraMedPipeline.sent = sent;
	}

	public static String getSentAll() {
		return sentAll;
	}

	public static void setSentAll(String sentAll) {
		FraMedPipeline.sentAll = sentAll;
	}
}
