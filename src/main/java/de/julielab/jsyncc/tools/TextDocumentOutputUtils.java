package de.julielab.jsyncc.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBException;
import org.apache.uima.UIMAException;

import de.julielab.jsnycc.annotation.TextAnnotation;
import de.julielab.jsyncc.checksum.CheckSum;
import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;

public class TextDocumentOutputUtils
{
	/*
	public static void printDocuments(List<TextDocument> textDocuments)		//test method
	{
		for (int i = 0; i < textDocuments.size(); i++)
		{
			System.out.println(i + " heading: " + textDocuments.get(i).getHeading());
			System.out.println("type: " + textDocuments.get(i).getDocumentType());
			System.out.println("topic:" + textDocuments.get(i).getTopic());
			System.out.println("<text>");
			System.out.println(textDocuments.get(i).getText());
			System.out.println("</text>");
			System.out.println("source: " + textDocuments.get(i).getSource());
			System.out.println("id: " + textDocuments.get(i).getId());
			System.out.println("long id:" + textDocuments.get(i).getIdLong());
			System.out.println("\n");
		}
	}*/

	public static void exportCorpusAsXML(
			List<BookProperties> listBookProperties,
			Path outDirXml
	) throws IOException
	{
		if (!(outDirXml.toFile().exists()))
		{
			Files.createDirectory(outDirXml);
		}

		List<TextDocument> textDocuments = new ArrayList<>();

		for (int i = 0; i < listBookProperties.size(); i++)
		{
			textDocuments.addAll(listBookProperties.get(i).textDocuments);
		}

		try
		{
			JaxBxmlHandler.marshalCorpus(textDocuments, new File(outDirXml + File.separator + "corpus.xml"));
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

	public static void exportMD5HexCheckSumsAsXML(
			List<BookProperties> listBookProperties,
			Path outDirXml
	) throws IOException
	{
		if (!(outDirXml.toFile().exists()))
		{
			Files.createDirectory(outDirXml);
		}

		List<CheckSum> corpusCheckSums = new ArrayList<>();

		for (int i = 0; i < listBookProperties.size(); i++)
		{
			List<TextDocument> textDocuments = listBookProperties.get(i).textDocuments;

			for (int j = 0; j < textDocuments.size(); j++)
			{
				CheckSum checkSum = new CheckSum();
				checkSum.setCheckSumText(textDocuments.get(j).getText());
				checkSum.setId(textDocuments.get(j).getId());
				checkSum.setIdLong(textDocuments.get(j).getIdLong());
				corpusCheckSums.add(checkSum);
			}
		}
		try
		{
			JaxBxmlHandler.marshalCheckSum(corpusCheckSums, new File(outDirXml + File.separator + "md5Hex_checkSums.xml"));
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

	public static void exportTxtFiles(
			List<BookProperties> listBookProperties,
			Path outDirTxt,
			Path outDirMisc,
			Path outDirStats
			) throws IOException
	{
		String fullText = "";

		if (!(outDirTxt.toFile().exists()))
		{
			Files.createDirectory(outDirTxt);
		}

		if (!(outDirMisc.toFile().exists()))
		{
			Files.createDirectory(outDirMisc);
		}

		String allOperativeReports = "";
		String allCaseDescription = "";
		String allCaseReports = "";

		String allReportsEmergency = "";
		String allDiscussion = "";
		String allPubMedAbstract = "";

		int index = 0;

		for (int i = 0; i < listBookProperties.size(); i++)
		{
			List<TextDocument> textDocuments = listBookProperties.get(i).textDocuments;

			for (int j = 0; j < textDocuments.size(); j++)
			{
				String text = textDocuments.get(j).getText();
				fullText = fullText + text + "\n";
				String item = textDocuments.get(j).getIdLong() + "-" + textDocuments.get(j).getDocumentType();

				String fileName = outDirTxt + File.separator + item + ".txt";

				text = textDocuments.get(j).getText();

				Files.write(Paths.get(fileName), textDocuments.get(j).getText().getBytes());
				System.out.println((index+j+1) + "\t" + fileName);

				// special text types
				if (textDocuments.get(j).getDocumentType().equals("OperativeReport"))
				{
					allOperativeReports = allOperativeReports + "\n" + text;
				}
				else if (textDocuments.get(j).getDocumentType().contains("CaseDescription"))
				{
					allCaseDescription = allCaseDescription + "\n" + text;
				}
				else if (textDocuments.get(j).getDocumentType().contains("CaseReport"))
				{
					allCaseReports = allCaseReports + "\n" + text;
				}
				else if (textDocuments.get(j).getDocumentType().equals("ReportEmergency"))
				{
					allReportsEmergency = allReportsEmergency + "\n" + text;
				}
				else if (textDocuments.get(j).getDocumentType().equals("Discussion"))
				{
					allDiscussion = allDiscussion + "\n" + text;
				}
				else if (textDocuments.get(j).getDocumentType().equals("PubMedAbstract"))
				{
					allPubMedAbstract = allPubMedAbstract + "\n" + text;
				}
			}
			index = index + textDocuments.size();
		}

		allOperativeReports		= allOperativeReports.replaceFirst("\n", "");
		allCaseDescription		= allCaseDescription.replaceFirst("\n", "");
		allCaseReports			= allCaseReports.replaceFirst("\n", "");
		allReportsEmergency		= allReportsEmergency.replaceFirst("\n", "");
		allDiscussion			= allDiscussion.replaceFirst("\n", "");
		allPubMedAbstract		= allPubMedAbstract.replaceFirst("\n", "");

		System.out.println();
		System.out.println("All documents as .txt-files in " + outDirTxt);

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-OperativeReports.txt"), allOperativeReports.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-OperativeReports.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-CaseDescription.txt"), allCaseDescription.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-CaseDescription.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-CaseReports.txt"), allCaseReports.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-CaseReports.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-ReportEmergency.txt"), allReportsEmergency.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-ReportEmergency.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-Discussion.txt"), allDiscussion.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-Discussion.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-PubMedAbstract.txt"), allPubMedAbstract.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-PubMedAbstract.txt");

		System.out.println("\tFull JSynCC in 1 file : "      + outDirMisc + File.separator + "jsyncc-text.txt");
		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-text.txt"), fullText.getBytes());

		//System.out.println("Misc output: "                   + outDirMisc);
	}

	public static void exportSentencesAndTokens(
			List<BookProperties> listBookProperties,
			Path outDirSent,
			Path outDirTok,
			Path outDirType,
			Path outDirMisc,
			Path outDirXml,
			Path outDirStats
			
			) throws IOException, UIMAException
	{
		if (!(outDirSent.toFile().exists()))
		{
			Files.createDirectory(outDirSent);
		}

		if (!(outDirTok.toFile().exists()))
		{
			Files.createDirectory(outDirTok);
		}

		if (!(outDirType.toFile().exists()))
		{
			Files.createDirectory(outDirType);
		}

		if (!(outDirMisc.toFile().exists()))
		{
			Files.createDirectory(outDirMisc);
		}

		int cntSentences = 0;
		int cntTokens = 0;
		int cntCharacters = 0;
		HashSet<String> tokenTypes = new HashSet<String>();
		//ArrayList <Integer> cntSentencesBook = new ArrayList<Integer>();

		String allSentences = "";
		String allTokens = "";

		int cntDocumentsOperativeReport = 0;
		int cntSentencesOperativeReport = 0;
		int cntTokensOperativeReport = 0;
		int cntCharactersOperativeReport = 0;
		HashSet<String> tokenTypesOperativeReport = new HashSet<String>();

		int cntDocumentsCaseDescription = 0;
		int cntSentencesCaseDescription = 0;
		int cntTokensCaseDescription = 0;
		int cntCharactersCaseDescription = 0;
		HashSet<String> tokenTypesCaseDescription = new HashSet<String>();

		int cntDocumentsCaseReport = 0;
		int cntSentencesCaseReport = 0;
		int cntTokensCaseReport = 0;
		int cntCharactersCaseReport = 0;
		HashSet<String> tokenTypesCaseReport = new HashSet<String>();

		int cntDocumentsReportEmergency = 0;
		int cntSentencesReportEmergency = 0;
		int cntTokensReportEmergency = 0;
		int cntCharactersReportEmergency = 0;
		HashSet<String> tokenTypesReportEmergency = new HashSet<String>();

		int cntDocumentsDiscussion = 0;
		int cntSentencesDiscussion = 0;
		int cntTokensDiscussion = 0;
		int cntCharactersDiscussion = 0;
		HashSet<String> tokenTypesDiscussion = new HashSet<String>();

		int cntDocumentsPubMedCaseAbstract = 0;
		int cntSentencesPubMedCaseAbstract = 0;
		int cntTokensPubMedCaseAbstract = 0;
		int cntCharactersPubMedCaseAbstract = 0;
		HashSet<String> tokenTypesPubMedCaseAbstract = new HashSet<String>();

		String allOperativeReports = "";
		String allCaseDescription = "";
		String allCaseReports = "";

		String allReportsEmergency = "";
		String allDiscussion = "";
		String allPubMedAbstract = "";

		ArrayList<TextAnnotation> lingAnnotatedCorpus = new ArrayList<>();
		String meta_data = "";

		String head = "ID\tSourceShort\tBookId\tBookIdNr\tTopics\tDocumentType\tSentences\tTokens\tTypes\tCharacters\n";
		System.out.println(head);
		int index = 0;

		for (int i = 0; i < listBookProperties.size(); i++)
		{
			List<TextDocument> textDocuments = listBookProperties.get(i).textDocuments;

			int cntResDocuments = 0;
			int cntResSentences = 0;
			int cntResTokens = 0;
			int cntResCharacters = 0;
			HashSet<String> cntTokenTypes = new HashSet<String>();

			int cntResDocumentsDisc = 0;
			int cntResSentencesDisc = 0;
			int cntResTokensDisc = 0;
			int cntResCharactersDisc = 0;
			HashSet<String> cntTokenTypesDisc = new HashSet<String>();

			for (int j = 0; j < textDocuments.size(); j++)
			{
				String text = textDocuments.get(j).getText();
				cntCharacters = cntCharacters + text.length();
	
				TextAnnotation annotation = new TextAnnotation();
				annotation = PipelineSentencesTokensFraMed.runPipeline(text, textDocuments.get(j).getIdLong(), textDocuments.get(j).getId());
				lingAnnotatedCorpus.add(annotation);

				if (!(textDocuments.get(j).getDocumentType().equals("Discussion")))
				{
					cntResDocuments  = cntResDocuments + 1;
					cntResSentences  = cntResSentences + annotation.getCountSentences();
					cntResTokens     = cntResTokens + annotation.getCountTokens();
					cntResCharacters = cntResCharacters + text.length();
					cntTokenTypes.addAll(annotation.getTokenTypes());
				}
				else
				{
					cntResDocumentsDisc  = cntResDocumentsDisc + 1;
					cntResSentencesDisc  = cntResSentencesDisc + annotation.getCountSentences();
					cntResTokensDisc     = cntResTokensDisc + annotation.getCountTokens();
					cntResCharactersDisc = cntResCharactersDisc + text.length();
					cntTokenTypesDisc.addAll(annotation.getTokenTypes());
				}

				String item = textDocuments.get(j).getIdLong() + "-" + textDocuments.get(j).getDocumentType();
				String meta_data_line = 
						(index+j+1) + "\t" + textDocuments.get(j).getIdLong() +
						'\t' + textDocuments.get(j).getSourcShort() +
						'\t' + textDocuments.get(j).getBookId() +
						'\t' + textDocuments.get(j).getTopic() +
						'\t' + textDocuments.get(j).getDocumentType() +
						'\t' + annotation.getCountSentences() +
						'\t' + annotation.getCountTokens() + 
						'\t' + annotation.getCountTokenTypes() +
						'\t' + text.length();
				System.out.println(meta_data_line);
				meta_data = meta_data + meta_data_line + '\n';

				String fileSent = outDirSent + File.separator + item + "-framed-sent.txt";
				Files.write(Paths.get(fileSent), annotation.getSentences().getBytes());

				String fileTok = outDirTok + File.separator + item + "-framed-token.txt";
				Files.write(Paths.get(fileTok), annotation.getTokens().getBytes());

				String fileTyp = outDirType + File.separator + item + "-framed-types.txt";
				Files.write(Paths.get(fileTyp), annotation.getTokenTypes().toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\, ", "\n").getBytes()); // TODO neu, hier prüfen

				allSentences = allSentences + "\n" + annotation.getSentences();
				allTokens    =    allTokens + "\n" + annotation.getTokens();

				cntSentences = cntSentences + annotation.getCountSentences();
				cntTokens = cntTokens + annotation.getCountTokens();
				tokenTypes.addAll(annotation.getTokenTypes());

				// special text types
				if (textDocuments.get(j).getDocumentType().equals("OperativeReport"))
				{
					cntDocumentsOperativeReport = cntDocumentsOperativeReport + 1;
					allOperativeReports = allOperativeReports + "\n" + annotation.getSentences();
					cntSentencesOperativeReport = cntSentencesOperativeReport + annotation.getCountSentences();
					cntTokensOperativeReport = cntTokensOperativeReport + annotation.getCountTokens();
					cntCharactersOperativeReport = cntCharactersOperativeReport + text.length();
					tokenTypesOperativeReport.addAll(annotation.getTokenTypes());
				}
				else if (textDocuments.get(j).getDocumentType().contains("CaseDescription"))
				{
					cntDocumentsCaseDescription = cntDocumentsCaseDescription + 1;
					allCaseDescription = allCaseDescription + "\n" + annotation.getSentences();
					cntSentencesCaseDescription = cntSentencesCaseDescription + annotation.getCountSentences();
					cntTokensCaseDescription = cntTokensCaseDescription + annotation.getCountTokens();
					cntCharactersCaseDescription = cntCharactersCaseDescription + text.length();
					tokenTypesCaseDescription.addAll(annotation.getTokenTypes());
				}
				else if (textDocuments.get(j).getDocumentType().contains("CaseReport"))
				{
					cntDocumentsCaseReport = cntDocumentsCaseReport + 1;
					allCaseReports = allCaseReports + "\n" + annotation.getSentences();
					cntSentencesCaseReport = cntSentencesCaseReport + annotation.getCountSentences();
					cntTokensCaseReport = cntTokensCaseReport + annotation.getCountTokens();
					cntCharactersCaseReport = cntCharactersCaseReport + text.length();
					tokenTypesCaseReport.addAll(annotation.getTokenTypes());
				}
				else if (textDocuments.get(j).getDocumentType().equals("ReportEmergency"))
				{
					cntDocumentsReportEmergency = cntDocumentsReportEmergency + 1;
					allReportsEmergency = allReportsEmergency + "\n" + annotation.getSentences();
					cntSentencesReportEmergency = cntSentencesReportEmergency + annotation.getCountSentences();
					cntTokensReportEmergency = cntTokensReportEmergency + annotation.getCountTokens();
					cntCharactersReportEmergency = cntCharactersReportEmergency + text.length();
					tokenTypesReportEmergency.addAll(annotation.getTokenTypes());
				}
				else if (textDocuments.get(j).getDocumentType().equals("Discussion"))
				{
					cntDocumentsDiscussion = cntDocumentsDiscussion + 1;
					allDiscussion = allDiscussion + "\n" + annotation.getSentences();
					cntSentencesDiscussion = cntSentencesDiscussion + annotation.getCountSentences();
					cntTokensDiscussion = cntTokensDiscussion + annotation.getCountTokens();
					cntCharactersDiscussion = cntCharactersDiscussion + text.length();
					tokenTypesDiscussion.addAll(annotation.getTokenTypes());
				}
				else if (textDocuments.get(j).getDocumentType().equals("PubMedAbstract"))
				{
					cntDocumentsPubMedCaseAbstract = cntDocumentsPubMedCaseAbstract + 1;
					allPubMedAbstract = allPubMedAbstract + "\n" + annotation.getSentences();
					cntSentencesPubMedCaseAbstract = cntSentencesPubMedCaseAbstract + annotation.getCountSentences();
					cntTokensPubMedCaseAbstract = cntTokensPubMedCaseAbstract + annotation.getCountTokens();
					cntCharactersPubMedCaseAbstract = cntCharactersPubMedCaseAbstract + text.length();
					tokenTypesPubMedCaseAbstract.addAll(annotation.getTokenTypes());
				}
			}

			listBookProperties.get(i).documents  = cntResDocuments;
			listBookProperties.get(i).sentences  = cntResSentences;
			listBookProperties.get(i).tokens     = cntResTokens;
			listBookProperties.get(i).tokenTypes = cntTokenTypes;
			listBookProperties.get(i).characters = cntResCharacters;

			listBookProperties.get(i).documentsDisc  = cntResDocumentsDisc;
			listBookProperties.get(i).sentencesDisc  = cntResSentencesDisc;
			listBookProperties.get(i).tokensDisc     = cntResTokensDisc;
			listBookProperties.get(i).tokenTypesDisc = cntTokenTypesDisc;
			listBookProperties.get(i).charactersDisc = cntResCharacters;

			index = index + textDocuments.size();
		}

		meta_data = head + meta_data;
		Files.write(Paths.get(outDirStats + File.separator + "meta_data_sent_tok.csv"), meta_data.getBytes());
		System.out.println("======================");

		String cnts_corpus = "index\tbookId\tsourceShort\tdocumentType\ttextDocuments\tsentences\ttokens\tcharacters\ttypes";

		int bookindex = 0;
		
		for (int i = 0; i < listBookProperties.size(); i++)
		{
			String cnt_line = bookindex + '\t' + listBookProperties.get(i).bookId + '\t' +
				listBookProperties.get(i).sourceShort + '\t' +
				listBookProperties.get(i).getDocumentType().get(0) + '\t' +
				listBookProperties.get(i).documents + '\t' +
				listBookProperties.get(i).sentences + '\t' +
				listBookProperties.get(i).tokens + '\t' +
				listBookProperties.get(i).tokenTypes.size() + '\t' +
				listBookProperties.get(i).characters + '\t';

			cnts_corpus = cnts_corpus + '\n' + cnt_line;
			System.out.println(cnt_line);

			if (listBookProperties.get(i).documentsDisc > 0)
			{
				cnt_line = bookindex + '\t' + listBookProperties.get(i).bookId + '\t' +
					listBookProperties.get(i).sourceShort + '\t' +
					"Discussion\t" +
					listBookProperties.get(i).documentsDisc + '\t' +
					listBookProperties.get(i).sentencesDisc + '\t' +
					listBookProperties.get(i).tokensDisc + '\t' + 
					listBookProperties.get(i).charactersDisc + '\t' +
					listBookProperties.get(i).tokenTypesDisc.size() + '\t';
					cnts_corpus = cnts_corpus + '\n' + cnt_line;
					System.out.println(cnt_line);
			}
		}

		System.out.println("======================");
		Files.write(Paths.get(outDirStats + File.separator + "jsyncc-cnts_corpus.csv"), cnts_corpus.getBytes());
		System.out.println(   outDirStats + File.separator + "jsyncc-cnts_corpus.csv");
		System.out.println("======================");

		allSentences			= allSentences.replaceFirst("\n", "");
		allTokens				= allTokens.replaceFirst("\n", "");

		allOperativeReports		= allOperativeReports.replaceFirst("\n", "");
		allCaseDescription		= allCaseDescription.replaceFirst("\n", "");
		allCaseReports			= allCaseReports.replaceFirst("\n", "");
		allReportsEmergency		= allReportsEmergency.replaceFirst("\n", "");
		allDiscussion			= allDiscussion.replaceFirst("\n", "");
		allPubMedAbstract		= allPubMedAbstract.replaceFirst("\n", "");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences.txt"), allSentences.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences.txt created.");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-tokens.txt"), allTokens.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-tokens.txt created.");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences-OperativeReports.txt"), allOperativeReports.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences-OperativeReports.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences-CaseDescription.txt"), allCaseDescription.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences-CaseDescription.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences-CaseReports.txt"), allCaseReports.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences-CaseReports.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences-ReportEmergency.txt"), allReportsEmergency.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences-ReportEmergency.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences-Discussion.txt"), allDiscussion.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences-Discussion.txt");

		Files.write(Paths.get(outDirMisc + File.separator + "jsyncc-sentences-PubMedAbstract.txt"), allPubMedAbstract.getBytes());
		System.out.println(   outDirMisc + File.separator + "jsyncc-sentences-PubMedAbstract.txt");

		String annoFile = "jsyncc-annotations.xml";
		String cntsCorpusCharacteristics = "(sub-)corpus\tsentences\ttokens\ttypes\tcharacters\n";

		System.out.println("---------------------------------");
		System.out.println("All OperativeReport Documents:\t"  + cntDocumentsOperativeReport);
		System.out.println("All OperativeReport Sentences:\t"  + cntSentencesOperativeReport);
		System.out.println("All OperativeReport Tokens:\t"     + cntTokensOperativeReport);
		System.out.println("All OperativeReport Types:\t"      + tokenTypesOperativeReport.size());
		System.out.println("All OperativeReport Characters:\t" + cntCharactersOperativeReport);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "OperativeReport\t" + cntDocumentsOperativeReport + "\n" + cntSentencesOperativeReport + "\t" + cntTokensOperativeReport + "\t" + tokenTypesOperativeReport.size() + "\t" + cntCharactersOperativeReport + "\n";
		System.out.println("---------------------------------");

		System.out.println("All CaseDescription Documents:\t"  + cntDocumentsCaseDescription);
		System.out.println("All CaseDescription Sentences:\t"  + cntSentencesCaseDescription);
		System.out.println("All CaseDescription Tokens:\t"     + cntTokensCaseDescription);
		System.out.println("All CaseDescription Types:\t"      + tokenTypesCaseDescription.size());
		System.out.println("All CaseDescription Characters:\t" + cntCharactersCaseDescription);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "CaseDescription\t" + cntDocumentsCaseDescription + "\n" + cntSentencesCaseDescription + "\t" + cntTokensCaseDescription + "\t" + tokenTypesCaseDescription.size() + "\t" + cntCharactersCaseDescription + "\n";
		System.out.println("---------------------------------");

		System.out.println("All CaseReport Documents:\t"  + cntDocumentsCaseReport);
		System.out.println("All CaseReport Sentences:\t"  + cntSentencesCaseReport);
		System.out.println("All CaseReport Tokens:\t"     + cntTokensCaseReport);
		System.out.println("All CaseReport Types:\t"      + tokenTypesCaseReport.size());
		System.out.println("All CaseReport Characters:\t" + cntCharactersCaseReport);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "CaseReport\t" + cntDocumentsCaseReport + "\n" + cntSentencesCaseReport + "\t" + cntTokensCaseReport + "\t" + tokenTypesCaseReport.size() + "\t" + cntCharactersCaseReport + "\n";
		System.out.println("---------------------------------");

		System.out.println("All ReportEmergency Documents:\t"  + cntDocumentsReportEmergency);
		System.out.println("All ReportEmergency Sentences:\t"  + cntSentencesReportEmergency);
		System.out.println("All ReportEmergency Tokens:\t"     + cntTokensReportEmergency);
		System.out.println("All ReportEmergency Types:\t"      + tokenTypesReportEmergency.size());
		System.out.println("All ReportEmergency Characters:\t" + cntCharactersReportEmergency);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "ReportEmergency\t" + cntDocumentsReportEmergency + "\n" + cntSentencesReportEmergency + "\t" + cntTokensReportEmergency + "\t" + tokenTypesReportEmergency.size() + "\t" + cntCharactersReportEmergency + "\n";
		System.out.println("---------------------------------");

		System.out.println("All Discussion Documents:\t"  + cntDocumentsDiscussion);
		System.out.println("All Discussion Sentences:\t"  + cntSentencesDiscussion);
		System.out.println("All Discussion Tokens:\t"     + cntTokensDiscussion);
		System.out.println("All Discussion Types:\t"      + tokenTypesDiscussion.size());
		System.out.println("All Discussion Characters:\t" + cntCharactersDiscussion);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "Discussion\t" + cntDocumentsDiscussion + "\n" + cntSentencesDiscussion + "\t" + cntTokensDiscussion + "\t" + tokenTypesDiscussion.size() + "\t" + cntCharactersDiscussion + "\n";
		System.out.println("---------------------------------");

		System.out.println("All PubMedCaseAbstract Documents:\t"  + cntDocumentsPubMedCaseAbstract);
		System.out.println("All PubMedCaseAbstract Sentences:\t"  + cntSentencesPubMedCaseAbstract);
		System.out.println("All PubMedCaseAbstract Tokens:\t"     + cntTokensPubMedCaseAbstract);
		System.out.println("All PubMedCaseAbstract Types:\t"      + tokenTypesPubMedCaseAbstract.size());
		System.out.println("All PubMedCaseAbstract Characters:\t" + cntCharactersPubMedCaseAbstract);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "PubMedCaseAbstract\t" + cntDocumentsPubMedCaseAbstract + "\n" + cntSentencesPubMedCaseAbstract + "\t" + cntTokensPubMedCaseAbstract + "\t" + tokenTypesPubMedCaseAbstract.size() + "\t" + cntCharactersPubMedCaseAbstract + "\n";
		System.out.println("---------------------------------");

		System.out.println("All Sentences:\t"  + cntSentences);
		System.out.println("All Tokens:\t"     + cntTokens);
		System.out.println("All Types:\t"      + tokenTypes.size());
		System.out.println("All Characters:\t" + cntCharacters);
		cntsCorpusCharacteristics = cntsCorpusCharacteristics + "(Full) JSynCC\t" +  cntSentences + "\t" + cntTokens + "\t" + tokenTypes.size() + "\t" + cntCharacters + "\n";
		System.out.println("---------------------------------");

		Files.write(Paths.get(outDirStats + File.separator + "cntsCorpusCharacteristics.csv"), cntsCorpusCharacteristics.toString().getBytes());

		try
		{
			JaxBxmlHandler.marshalAnnotation(lingAnnotatedCorpus, new File(outDirXml + File.separator + annoFile));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		ProcessBuilder pb = new ProcessBuilder(
			"tar",
			"cfzv",
			outDirXml + File.separator + annoFile + "jsyncc-syntax_annotations.xml.tar",
			outDirXml + File.separator + annoFile + "jsyncc-syntax_annotations.xml"
		);

		Process p = pb.start();
		try
		{
			p.waitFor();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}