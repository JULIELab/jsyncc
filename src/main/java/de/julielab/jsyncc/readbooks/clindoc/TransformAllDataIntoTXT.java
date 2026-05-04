package de.julielab.jsyncc.readbooks.clindoc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TransformAllDataIntoTXT {

	public static void main(String[] args) throws Exception {
		
		String inputDir = "/home/christina/git/GermanClinicalReportsFromBooks/grascco_test-data";
		
		String outputDir = "grascco_test-data_out";

		new File(outputDir).mkdir();
		
		Stream<Path> caseList = Files
				.walk(Paths.get(inputDir.toString()))
				.filter(Files::isRegularFile)
				.filter(s -> s.toString().endsWith("doc"))
				;

		int i = 0;
		
		for (Iterator<Path> iterator = caseList.iterator(); iterator.hasNext();)
		{
			i = i + 1;

			String path_file = iterator.next().toString();

			ReadSingleMSDoc.INPUT_FILE = path_file;

			ReadSingleMSDoc.doc2Text();

			String textOnly = ReadSingleMSDoc.CONTENT_TAB_MARKED;

			textOnly = FindSentencesByFramed.getSentencesNEW(textOnly);
			//System.out.println(textOnly);
			
			String[] textOnlyLines = textOnly.split("\n");
			
			System.out.println(textOnlyLines.length);

			String[] casePAinfo = path_file.split(Pattern.quote(System.getProperty("file.separator")));
			//String minicase = casePAinfo[casePAinfo.length - 3] + "_" + casePAinfo[casePAinfo.length - 2];
			//System.out.println(outputDir + "/" + minicase + "_" + casePAinfo[casePAinfo.length - 1] + ".txt");
			//System.out.println(i + "\t" + minicase + "_" + casePAinfo[casePAinfo.length - 1].replaceAll("doc","txt"));
			//Files.write(
			//	Paths.get(outputDir + "/" + minicase + "_" + casePAinfo[casePAinfo.length - 1].replaceAll("doc","txt")),
			//	textOnly.getBytes()
			//);
			
			Path sent_file = Paths.get(outputDir + "/" + casePAinfo[casePAinfo.length - 1].replaceAll(".doc","") + "-framed-sent.txt");

			Files.write(
					sent_file,
					textOnly.getBytes()
				);
			System.out.println(sent_file);
		}
	}
}
