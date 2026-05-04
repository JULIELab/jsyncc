package de.julielab.jsyncc.readbooks.casereports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.exception.ContextedException;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.TextDocument;
import de.julielab.jsyncc.tools.ExtractionUtils;
import de.julielab.jsyncc.tools.LanguageTools;

public class CasesSurgery
{

	public static List<TextDocument> extractContent(BookProperties bookProperties) throws IOException, ContextedException
	{
		List<TextDocument> textDocuments = new ArrayList<>();

		List<String> tableOfContents			= readTableOfContents(bookProperties.tableOfContents);
		HashMap<Integer,String> tableOfTopics	= readTableOfTopics(bookProperties.tableOfContents);
		HashMap<Integer,String> tableOfAnswers	= readTableOfAnswers(bookProperties.tableOfContents);

		Stream<Path> list = Files.walk(Paths.get(bookProperties.bookPath)).filter(Files::isRegularFile).sorted().filter(f -> f.toString().endsWith(".pdf"));

		int index = 0;

		for (Iterator<Path> iterator = list.iterator(); iterator.hasNext();)
		{
			String element = iterator.next().toString();

			// exclude the file with different tables of content
			if (!(element.endsWith("b-0036-141230.pdf")))
			{
				index++;
				textDocuments.add(handleOneDocument(element, index, tableOfContents, tableOfTopics, tableOfAnswers, bookProperties));
			}
		}

		return textDocuments;
	}

	public static List<String> readTableOfContents(String file) throws ContextedException
	{
		List<String> tableOfContents = new ArrayList<String>();

		String element = ExtractionUtils.getContentByTika(file);
		String[] lines = element.split("\n");
		boolean tabContents = false;
		String temp = "";
		int index = 0;

		for (int i = 0; i < lines.length; i++)
		{
			if (
					(lines[i].equals("Inhaltsverzeichnis"))
				&&
					(lines[i + 2].equals("Chirurgische Infektionen"))
				)
			{
				tabContents = false;

				// last element
				temp = temp.replaceAll(" Fall \\d+ ", "");
				temp = temp.replaceAll("\\.", "");
				temp = temp.replaceAll("\\s\\d+", "");
				temp = LanguageTools.removeHyphenNew(temp);
				tableOfContents.add(temp);
				temp = "";
				index = index + 1;
			}

			if (
					(tabContents)
				&&
					(3 < lines[i].length())
				&&
					(!(lines[i].contains("Inhaltsverzeichnis nach Fällen")))
				&&
					(index <= 140)
				)
			{
				if (lines[i].startsWith("Fall"))
				{
					temp = temp.replaceAll(" Fall \\d+ ", "");
					temp = temp.replaceAll("\\.", "");
					temp = temp.replaceAll("\\s\\d+", "");
					temp = LanguageTools.removeHyphenNew(temp);
					tableOfContents.add(temp);
					temp = "";
					index = index + 1;
				}

				temp = temp + " " + lines[i];
			}

			if (
					(lines[i].equals("Inhaltsverzeichnis"))
				&&
					(lines[i + 1].startsWith("Fall 1 20"))
				)
			{
				tabContents = true;
			}
		}

		return tableOfContents;
	}

	public static HashMap<Integer, String> readTableOfTopics(String file) throws ContextedException
	{
		HashMap<Integer,String> tableOfTopics = new HashMap<Integer,String>();

		String element = ExtractionUtils.getContentByTika(file);
		String[] lines = element.split("\n");
		boolean tabContents = false;
		String temp = "";
		String actTopic = "";
		int index = 0;

		for (int i = 0; i < lines.length; i++)
		{
			if (
					(lines[i].startsWith("Inhaltsverzeichnis nach Themen"))
				&&
					(lines[i + 1].equals("Inhaltsverzeichnis"))
				)
			{
				tabContents = false;
			}

			if (
					(tabContents)
				&&
					(3 < lines[i].length())
				&&
					(index < 140)
				)
			{
				if (lines[i].startsWith("Fall"))
				{
					index = index + 1;

					String actCase = lines[i].replaceAll("\\.", "");
					actCase = actCase.replaceAll("Fall ", "");
					actCase = actCase.replaceAll("\\s\\s+\\d+", "");

					tableOfTopics.put(Integer.parseInt(actCase), actTopic);
				}
				else
				{
					actTopic = LanguageTools.removeHyphenNew(lines[i]).replaceAll("\\n", "");
				}

				temp = temp + " " + lines[i];
			}

			if (
					(lines[i].equals("Inhaltsverzeichnis"))
				&&
					(lines[i + 2].startsWith("Chirurgische Infektionen"))
				)
			{
				tabContents = true;
			}
		}

		return tableOfTopics;
	}

	public static HashMap<Integer, String> readTableOfAnswers(String file) throws ContextedException 
	{
		HashMap<Integer,String> tableOfAnswers = new HashMap<Integer,String>();
		
		String element = ExtractionUtils.getContentByTika(file);
		String[] lines = element.split("\n");
		boolean tabContents = false;
		String temp = "";
		int index = 0;

		for (int i = 0; i < lines.length; i++)
		{
			if (
					(lines[i].startsWith("Anhang"))
				)
			{
				tabContents = false;
				
			}

			if (
					(tabContents)
				&&
					(3 < lines[i].length())
				&&
					(index < 140)
				)
			{
				if (lines[i].startsWith("Fall"))
				{
					index = index + 1;

					String actCase = lines[i];

					if (!(lines[i].contains(".")))
					{
						actCase = actCase + " " + lines[i+2];
					}

					actCase = lines[i].replaceAll("\\.", "");
					actCase = actCase.replaceAll("Fall \\d+ ", "");
					actCase = actCase.replaceAll("\\s\\s+\\d+", "");

					actCase = LanguageTools.removeHyphenNew(actCase).replaceAll("\\n", "");

					// Normalize En dash – \\u2013
					actCase = actCase.replaceAll("\\s\\u2013"," \u2013");
					actCase = actCase.replaceAll("\\u2013\\s","\u2013 ");
					actCase = actCase.replaceAll("\\u2013"," - ");

					tableOfAnswers.put(index, actCase);
				}

				temp = temp + " " + lines[i];
			}

			if (
					(lines[i].equals("Inhaltsverzeichnis"))
				&&
					(lines[i + 1].startsWith("Fall 1 Beckenfraktur"))
				)
			{
				tabContents = true;
			}
		}
		return tableOfAnswers;
	}

	public static TextDocument handleOneDocument(
			String file,
			int index,
			List<String> tableOfContents,
			HashMap<Integer,String> tableOfTopics,
			HashMap<Integer,String> tableOfAnswers,
			BookProperties bookProperties
			) throws ContextedException
	{
		String element = ExtractionUtils.getContentByTika(file);
		String[] lines = element.split("\\n");
		boolean readText = true;
		String actText = "";
		int caseNumber = 0;

		for (int i = 0; i < lines.length; i++)
		{
			if (2 < lines[i].length())
			{
				if (lines[i].matches("\\d+\\.\\d .*"))
				{
					readText = false;
				}

				if (readText)
				{
					if (
							(lines[i].contains("Abb."))
						&&
							(!(lines[i].contains("\u25B6")))
						)
					{
						String[] temp = lines[i].split("Abb\\.");
						actText = actText + " " + temp[0];
						readText = false;
					}
					else
					{
						actText = actText + " " + lines[i];
					}
				}

				if (lines[i].startsWith("Fall"))
				{
					caseNumber = Integer.parseInt(lines[i].replaceAll("Fall ", ""));
				}
			}
		}

		actText = LanguageTools.removeHyphenNew(actText);

		// remove expressions like "(s. ▶Abb. 132.1)" and "(siehe ▶Abb. 46.1)"
		actText = actText.replaceAll("\\s\\((s\\.|siehe)\\s\\u25B6Abb\\.\\s\\d+\\.\\d\\)", "");
		actText = actText.replaceAll("\\s\\(\\u25B6Abb\\.\\s\\d+\\.\\d\\)", "");
		actText = actText.replaceAll("▶", "");
		
		String head = tableOfContents.get(caseNumber);
		actText = actText.substring(head.length());

		if (actText.startsWith(" "))
		{
			actText = actText.replaceFirst(" ", "");
		}

		actText = actText.replaceAll("\\A[A-Za-zÖÄÜßöäü]* ", "");

		TextDocument textDocument = new TextDocument();
		textDocument.setHeading(head);
		textDocument.setText(actText);

		textDocument.topics.add(bookProperties.topics.get(0));
		//textDocument.topics.add(tableOfTopics.get(caseNumber));
		//textDocument.topics.add(tableOfAnswers.get(caseNumber));

		textDocument.setDocumentType(bookProperties.documentType.get(0));
		textDocument.setSource(
			bookProperties.getTitle() + " " +
			bookProperties.getEditorAuthor() + " " +
			bookProperties.getYear() + " " +
			bookProperties.getPublisher() + " " + 
			bookProperties.getDoi()
		);
		textDocument.setSourcShort(bookProperties.sourceShort);
		textDocument.setBookId(bookProperties.bookId);
		textDocument.setIdLong(bookProperties.getSourceShort() + "-" + index);

		return textDocument;
	}
}
