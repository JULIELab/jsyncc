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

import de.julielab.jsyncc.annotation.TextAnnotation;
import de.julielab.jsyncc.checksum.CheckSum;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.JaxBxmlHandler;
import de.julielab.jsyncc.tools.PipelineSentencesTokensFraMed;

public class TextDocumentOutputUtils {
	public static void printDocuments(List<TextDocument> listDocuments)
	{
		for (int i = 0; i < listDocuments.size(); i++) {
			System.out.println(i + " heading: " + listDocuments.get(i).getHeading());
			System.out.println("type: " + listDocuments.get(i).getType());
			System.out.println("topic:" + listDocuments.get(i).getTopic());
			System.out.println("<text>");
			System.out.println(listDocuments.get(i).getText());
			System.out.println("</text>");
			System.out.println("in relation of:" + listDocuments.get(i).getInRelationOf());
			System.out.println("source: " + listDocuments.get(i).getSource());
			System.out.println("id: " + listDocuments.get(i).getId());
			System.out.println("long id:" + listDocuments.get(i).getIdLong());
			System.out.println("\n");
		}
	}

	public static void printDocumentsWithSections(List<TextDocument> listDocuments)
	{
		for (int i = 0; i < listDocuments.size(); i++) {
			System.out.println(i + " heading: " + listDocuments.get(i).getHeading());
			System.out.println("type: " + listDocuments.get(i).getType());
			System.out.println("topic:" + listDocuments.get(i).getTopic());
			System.out.println("<text>");
			System.out.println(listDocuments.get(i).getTextSection());
			System.out.println("</text>");
			System.out.println("in relation of:" + listDocuments.get(i).getInRelationOf());
			System.out.println("source: " + listDocuments.get(i).getSource());
			System.out.println("id: " + listDocuments.get(i).getId());
			System.out.println("long id:" + listDocuments.get(i).getIdLong());
			System.out.println("\n");
		}
	}

	public static void writeXML(List<TextDocument> listDocuments, List<CheckSum> checkSumList, Path outDirXml)
			throws IOException {
		if (!(outDirXml.toFile().exists())) {
			Files.createDirectory(outDirXml);
		}

		try {
			JaxBxmlHandler.marshalCorpus(listDocuments, new File(outDirXml + "/corpus.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		try {
			JaxBxmlHandler.marshalCheckSum(checkSumList, new File(outDirXml + "/checkSums.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void writeTxtFiles(Path outDirTxt, List<TextDocument> listDocuments) throws IOException {
		System.out.println("TextDocumentOutputUtils.writeTxtFiles()");

		String fullText = "";
		String fullTextExperiments = "";

		if (Files.notExists(outDirTxt)) {
			Files.createDirectory(outDirTxt);
		}

		for (int i = 0; i < listDocuments.size(); i++) {
			String text = listDocuments.get(i).getText();
			fullText = fullText + text + "\n";
			fullTextExperiments = fullTextExperiments + text + "\n[END]\n\n\n";

			String item = listDocuments.get(i).getIdLong() + "-" + listDocuments.get(i).getType();

			// System.out.println(item);
			// String fileName = outDirTxt + File.seperator + (i+1) + "-" + item + ".txt";
			String fileName = outDirTxt + File.separator + item + ".txt";
			Files.write(Paths.get(fileName), listDocuments.get(i).getText().getBytes());
			System.out.println((i + 1) + "\t" + fileName);
		}
		Files.write(Paths.get(outDirTxt + File.separator + "jsyncc-text.txt"), fullText.getBytes());
		Files.write(Paths.get(outDirTxt + File.separator + "jsyncc-text-experiments.txt"), fullTextExperiments.getBytes());
	}

	public static void writeTxtSectionFiles(Path outDirTxt, List<TextDocument> listDocuments) throws IOException {
		String fullTextSection = "";

		if (!(outDirTxt.toFile().exists())) {
			Files.createDirectory(outDirTxt);
		}

		for (int i = 0; i < listDocuments.size(); i++) {
			String textSection = listDocuments.get(i).getTextSection();

			if (!(textSection.isEmpty())) {
				fullTextSection = fullTextSection + textSection + "\n";

				String fileName = outDirTxt + File.separator + (i + 1) + "-" + listDocuments.get(i).getIdLong() + "-"
						+ listDocuments.get(i).getType() + "_sections.txt";
				Files.write(Paths.get(fileName), listDocuments.get(i).getTextSection().getBytes());
				System.out.println((i + 1) + "\t" + fileName);
			}
		}
		Files.write(Paths.get(outDirTxt + File.separator + "jsyncc-section-text.txt"), fullTextSection.getBytes());
	}

	public static void writeCheckSums(List<TextDocument> listDocuments, List<CheckSum> checkSumList) {
		for (int i = 0; i < listDocuments.size(); i++) {
			System.out.println(listDocuments.get(i).getText());
			CheckSum checkSum = new CheckSum();
			checkSum.checkSumText = DigestUtils.md5Hex(listDocuments.get(i).getText());
			checkSum.id = Integer.toString(i);
			checkSumList.add(checkSum);
		}
	}

	public static ArrayList<TextAnnotation> writeTxtFilesAndAnnotations(List<TextDocument> listDocuments, String outTxt,
			String outXml) throws IOException, UIMAException {
		String fullText = "";
		String fullSent = "";
		String fullTokens = "";

		ArrayList<TextAnnotation> annotatedCorpus = new ArrayList<>();

		for (int i = 0; i < listDocuments.size(); i++) {
			String text = listDocuments.get(i).getText();

			TextAnnotation annotation = new TextAnnotation();
			annotation =
					PipelineSentencesTokensFraMed.runPipeline(text, listDocuments.get(i).getIdLong(),listDocuments.get(i).getId());

			annotatedCorpus.add(annotation);

			String sent = annotation.getSentences();
			String token = annotation.getTokens();

			fullText = fullText + text + "\n";
			fullSent = fullSent + sent + "\n";
			fullTokens = fullTokens + token + "\n";
		}

		Files.write(Paths.get(outTxt + File.separator + "jsyncc-text.txt"), fullText.getBytes());
		System.out.println(outXml + File.separator + "jsyncc-text.txt created successfully.");

		Files.write(Paths.get(outTxt + File.separator + "jsyncc-sentences.txt"), fullSent.getBytes());
		System.out.println(outXml + File.separator + "jsyncc-sentences.txt created successfully.");

		Files.write(Paths.get(outTxt + File.separator + "jsyncc-tokens.txt"), fullTokens.getBytes());
		System.out.println(outXml + File.separator + "jsyncc-tokens.txt created successfully.");

		String annoFile = "jsyncc-annotations.xml";

		try {
			JaxBxmlHandler.marshalAnnotation(annotatedCorpus, new File(outXml + File.separator + annoFile));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println(outXml + File.separator + annoFile + " created successfully.");

		ProcessBuilder pb = new ProcessBuilder("tar", "cfzv", outXml + File.separator + "jsyncc-annotations.xml.tar",
				"jsyncc-annotations.xml");

		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return annotatedCorpus;
	}

	public static void writeSentencesAndTokens(Path outDirSent, Path outDirTok, List<TextDocument> listDocuments)
			throws IOException, UIMAException {
		if (!(outDirSent.toFile().exists())) {
			Files.createDirectory(outDirSent);
		}

		if (!(outDirTok.toFile().exists())) {
			Files.createDirectory(outDirTok);
		}

		String allSentences = "";
		String allOperativeReports = "";
		String allCaseDescription = "";
		String allCaseReports = "";
		String allReportsEmergency = "";
		String allDiscussion = "";
		String allDPubMedAbstract = "";
		String allCaseLong = "";

		for (int i = 0; i < listDocuments.size(); i++) {
			String text = listDocuments.get(i).getText();

			TextAnnotation annotation = new TextAnnotation();
			annotation = PipelineSentencesTokensFraMed.runPipeline(text, listDocuments.get(i).getIdLong(),
					listDocuments.get(i).getId());

			String item = listDocuments.get(i).getIdLong() + "-" + listDocuments.get(i).getType();

			System.out.println((i + 1) + "\t" + "Sent/Tok" + "\t" + item);

			String fileSent = outDirSent + File.separator + item + "-framed-sent.txt";
			Files.write(Paths.get(fileSent), annotation.getSentences().getBytes());

			String fileTok = outDirTok + File.separator + item + "-framed-token.txt";
			Files.write(Paths.get(fileTok), annotation.getTokens().getBytes());

			allSentences = allSentences + "\n" + annotation.getSentences();

			// special text types
			if (listDocuments.get(i).getType().equals("OperativeReport")) {
				allOperativeReports = allOperativeReports + "\n" + annotation.getSentences();
			} else if (listDocuments.get(i).getType().contains("CaseDescription")) {
				allCaseDescription = allCaseDescription + "\n" + annotation.getSentences();
				if (listDocuments.get(i).getType().contains("CaseDescriptionLong")) {
					allCaseLong = allCaseLong + "\n" + annotation.getSentences();
				}
			} else if (listDocuments.get(i).getType().contains("CaseReport")) {
				allCaseReports = allCaseReports + "\n" + annotation.getSentences();
				if (listDocuments.get(i).getType().contains("CaseReportLong")) {
					allCaseLong = allCaseLong + "\n" + annotation.getSentences();
				}
			} else if (listDocuments.get(i).getType().equals("ReportEmergency")) {
				allReportsEmergency = allReportsEmergency + "\n" + annotation.getSentences();
			} else if (listDocuments.get(i).getType().equals("Discussion")) {
				allDiscussion = allDiscussion + "\n" + annotation.getSentences();
			} else if (listDocuments.get(i).getType().equals("PubMedAbstract")) {
				allDPubMedAbstract = allDPubMedAbstract + "\n" + annotation.getSentences();
			}
			// else if (listDocuments.get(i).getType().equals("CaseReportLong"))
			// {
			// allCaseReportLong = allCaseReportLong + "\n" +
			// annotation.getSentences();
			// }
			else {
				System.out.println("Was vergessen!");
				System.out.println(listDocuments.get(i).getType());
			}

			// fullText = fullText + text + "\n";
			// fullSent = fullSent + sent + "\n";
			// fullTokens = fullTokens + token + "\n";
		}

		allSentences = allSentences.replaceFirst("\n", "");
		allOperativeReports = allOperativeReports.replaceFirst("\n", "");
		allCaseDescription = allCaseDescription.replaceFirst("\n", "");
		allCaseReports = allCaseReports.replaceFirst("\n", "");
		allReportsEmergency = allReportsEmergency.replaceFirst("\n", "");
		allDiscussion = allDiscussion.replaceFirst("\n", "");
		allDPubMedAbstract = allDPubMedAbstract.replaceFirst("\n", "");
		allCaseLong = allCaseLong.replaceFirst("\n", "");

		// Files.write(Paths.get(outTxt + File.seperator + "jsyncc-text.txt"),
		// fullText.getBytes());
		// System.out.println(outXml + File.seperator + "jsyncc-text.txt created
		// successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-sentences.txt"), allSentences.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-sentences.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allOperativeReports.txt"), allOperativeReports.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-sentences.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allCaseDescription.txt"), allCaseDescription.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-allCaseExamples.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allCaseReports.txt"), allCaseReports.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-allCaseReports.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allReportEmergency.txt"), allReportsEmergency.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-allReportEmergency.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allDiscussion.txt"), allDiscussion.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-allDiscussion.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allPubMedAbstract.txt"), allDPubMedAbstract.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-allPubMedAbstract.txt created successfully.");

		Files.write(Paths.get(outDirSent + File.separator + "jsyncc-allCaseReportDescriptionLong.txt"), allCaseLong.getBytes());
		System.out.println(outDirSent + File.separator + "jsyncc-allCaseReportDescriptionLong.txt created successfully.");
	}
}