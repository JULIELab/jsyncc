package de.julielab.testPipe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class TestPipeLine {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		String test = "Eine 53-jährige Frau stellt sich in Ihrer proktologischen Sprechstunde vor. "
				+ "Sie berichtet, schon seit längerem den Stuhlgang nicht mehr komplett halten zu können. "
				+ "Beim Stuhlgang selbst sei auch immer wieder Blut dabei und es würde sich „etwas vorwölben”, was sie dann anschließend wieder zurückschieben müsse. "
				+ "Anamnestisch berichtet sie auf Ihr Nachfragen über 3 vaginale Geburten, wobei zweimal ein Dammriss genäht werden musste. "
				+ "Bei der proktologischen Untersuchung beobachten Sie beim Pressen der Patientin einen ausgeprägten Prolaps der Schleimhaut mit zirkulärer Fältelung."
				+ "";

		System.out.println(test);
		
		String inputFile = "test.txt";
		
		Files.write(Paths.get(inputFile), test.getBytes());
		
		String jarFileSent = "JarsModelsJCoRe/jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar";
		String modelSent = "JarsModelsJCoRe/jsbd-framed.gz";
		
		ProcessBuilder builderSent = new ProcessBuilder("java", "-jar", jarFileSent, inputFile, modelSent);
		Process p = builderSent.start();
		Scanner s = new Scanner(p.getInputStream());
		
		String sentIndex = s.next();
		
		String[] senInds = sentIndex.split(";");
		
		String sentences = "";
		
		for (int i = 0; i < senInds.length; i++)
		{
			String[] ind = senInds[i].split("-");
			System.out.println((i+1) + " " + test.substring(Integer.parseInt(ind[0]), Integer.parseInt(ind[1])));
			sentences = sentences + test.substring(Integer.parseInt(ind[0]), Integer.parseInt(ind[1])) + "\n";
		}
		
		Files.write(Paths.get("sent.txt"), sentences.getBytes());
		
		
//		java -jar jarswithOutput/jtbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar sent.txt jarswithOutput/jtbd-framed.gz
		
		
	}

}



// # Sentences
// java -jar jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar
// exampledata/TestText.txt jsbd-framed.gz
//
// # Token
// java -jar jtbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar
// exampledata/TestText.txt jtbd-framed.gz
//
// # Part-Of-Speech
// java -jar jpos-2.3.0-SNAPSHOT-jar-with-dependencies.jar p
// exampledata/TestText.txt jpos-framed.gz exampledata/pos.txt

//JarsModelsJCoRe/jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar JarsModelsJCoRe/exampledata/TestText.txt jsbd-framed.gz

