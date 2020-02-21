package de.julielab.jsyncc.count;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;

import de.julielab.jsyncc.annotation.TextAnnotation;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.PipelineSentencesTokensFraMed;
import de.julielab.jcore.types.Annotation;
import de.julielab.jcore.types.Token;

public class CountCorpus
{
	private static Set<String> types = new HashSet<String>();
	private static Set<String> typesSet = new HashSet<String>();

	public static void countTextDocumentElements(List<TextDocument> listDocuments, boolean detail) throws UIMAException, IOException
	{
		if (0 < listDocuments.size())
		{
			int sourceSent = 0;
			int sourceTok = 0;

			String source = "";

			int index = 1;
			int sections = 0;

			int sectionsGlobal = 0;
			int sentGlobal = 0;
			int tokGlobal = 0;

			for (int i = 0; i < (listDocuments.size()-1); i++)
			{
				String text = listDocuments.get(i).getText();

				TextAnnotation annotation = new TextAnnotation();
				annotation = PipelineSentencesTokensFraMed.runPipeline(text, listDocuments.get(i).getIdLong(), listDocuments.get(i).getId());

				int sent = annotation.getCountSentences();
				int tok = annotation.getCountTokens();

				if (detail)
				{
					System.out.println(listDocuments.get(i).getIdLong() + "\tSentences:\t" + sent + "\tTokens:\t" + tok);
				}

				if ( !(listDocuments.get(i).getTextSection().isEmpty()) )
				{
					sections++;
				}

				if (i == 0)
				{
					source = listDocuments.get(i).getSource();
					sourceSent = sent;
					sourceTok = tok;
				}

				else if ( ( i < listDocuments.size() ) && (source.equals(listDocuments.get(i+1).getSource())) )
				{
					sourceSent = sourceSent + sent;
					sourceTok = sourceTok + tok;
				}

						// Last Element of a Book
				else //if ( i < listDocuments.size() )
				{
					sourceSent = sourceSent + sent;
					sourceTok = sourceTok + tok;

					sectionsGlobal = sectionsGlobal + sections;

					String longId = listDocuments.get(i).getIdLong();
					String[] id = longId.split("-");
					String docs = id[id.length - 1];

					System.out.print(index + "\t");
					System.out.print("Source:\t" + listDocuments.get(i).getSource() + "\t");
					System.out.print(listDocuments.get(i).getIdLong() + "\t");
					System.out.print(listDocuments.get(i).getTopic().get(0) + "\t");

					System.out.print("Documents:\t" + docs + "\t");
					System.out.print("Docs Sections:\t" + sections + "\t");
					System.out.print("Sentences:\t" + sourceSent + "\t");
					System.out.print("Tokens:\t" + sourceTok + "\t");
//					System.out.print("Types:\t" + sourceTyp + "\n");
					System.out.println();

					sentGlobal = sentGlobal + sourceSent;
					tokGlobal = tokGlobal + sourceTok;

					sourceSent = 0;
					sourceTok = 0;
//					sourceTyp = 0;
					sections = 0;
					index++;

					source = listDocuments.get(i+1).getSource();
				}
			}

			// Last Element
			int lastElement = listDocuments.size() - 1;
			String longId = listDocuments.get(lastElement).getIdLong();
			String[] id = longId.split("-");

			String idShortName = id[0];
			String docs = id[id.length - 1];

			if ( (listDocuments.get(lastElement).getTextSection().length()) > 0)
			{
				sections++;
			}

			sectionsGlobal = sectionsGlobal + sections;

			String text = listDocuments.get(lastElement).getText();
			TextAnnotation annotation = new TextAnnotation();
			annotation = PipelineSentencesTokensFraMed.runPipeline(text, listDocuments.get(lastElement).getIdLong(), listDocuments.get(lastElement).getId());

			int sent = annotation.getCountSentences();
			int tok = annotation.getCountTokens();

			sourceSent = sourceSent + sent;
			sourceTok = sourceTok + tok;

			if (detail)
			{
				System.out.println(listDocuments.get(lastElement).getIdLong() + "\tSentences:\t" + sent + "\tTokens:\t" + tok);
			}

			System.out.print(index + "\t");
			System.out.print("Source:\t" + listDocuments.get(listDocuments.size() - 1).getSource() + "\t");
			System.out.print(idShortName + "\t");
			System.out.print(listDocuments.get(listDocuments.size() - 1).getTopic().get(0) + "\t");

			System.out.print("Documents:\t" + docs + "\t");
			System.out.print("Docs Sections:\t" + sections + "\t");
			System.out.print("Sentences:\t" + sourceSent + "\t");
			System.out.print("Tokens:\t" + sourceTok + "\t");
//			System.out.print("Types:\t" + sourceTyp + "\n");
			System.out.println();
			System.out.println();

			sentGlobal = sentGlobal + sourceSent;
			tokGlobal = tokGlobal + sourceTok;

			System.out.println("==============================");
			System.out.println("# Books / Sources: \t" + index);
			System.out.println("# Documents:\t" + listDocuments.size());
			System.out.println("# Doc. with Sections:\t" + sectionsGlobal);
			System.out.println("# Sentences:\t" + sentGlobal);
			System.out.println("# Tokens:\t" + tokGlobal);
		}
		else
		{
			System.out.println("No documents!");
		}
	}



	public static void printType(JCas jCas, int type)
	{
		FSIterator<org.apache.uima.jcas.tcas.Annotation> sents = jCas.getAnnotationIndex(type).iterator();

		while (sents.hasNext())
		{
			Annotation s = (Annotation) sents.next();
			System.out.println(s.getBegin() + "-" + s.getEnd() + ": " + s.getCoveredText());
		}
	}

	public static int countTypes(JCas jCas)
	{
		int type = Token.type;

		Set<String> types = new HashSet<String>();

		FSIterator<org.apache.uima.jcas.tcas.Annotation> sents = jCas.getAnnotationIndex(type).iterator();

		while (sents.hasNext())
		{
			Annotation s = (Annotation) sents.next();
//			System.out.println(s.getBegin() + "-" + s.getEnd() + ": " + s.getCoveredText());
			types.add(s.getCoveredText());
			typesSet.add(s.getCoveredText());
		}

//		System.out.println(T + " " + types.size());
		return types.size();
	}
}
