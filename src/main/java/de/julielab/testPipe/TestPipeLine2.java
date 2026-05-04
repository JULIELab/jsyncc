package de.julielab.testPipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

public class TestPipeLine2 {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, InterruptedException {

		String test = "Eine 53-jährige Frau stellt sich in Ihrer proktologischen Sprechstunde vor. "
				+ "Sie berichtet, schon seit längerem den Stuhlgang nicht mehr komplett halten zu können. "
				+ "Beim Stuhlgang selbst sei auch immer wieder Blut dabei und es würde sich „etwas vorwölben”, was sie dann anschließend wieder zurückschieben müsse. "
				+ "Anamnestisch berichtet sie auf Ihr Nachfragen über 3 vaginale Geburten, wobei zweimal ein Dammriss genäht werden musste. "
				+ "Bei der proktologischen Untersuchung beobachten Sie beim Pressen der Patientin einen ausgeprägten Prolaps der Schleimhaut mit zirkulärer Fältelung."
				+ "";

		String inputFile = "test.txt";
		Files.write(Paths.get(inputFile), test.getBytes());

		// jarswithOutput/jtbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar

		String jarFileSent = "jarswithOutput/jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar";
		String modelSent = "jarswithOutput/jsbd-framed.gz";

		ProcessBuilder builderSent = new ProcessBuilder("java", "-jar", jarFileSent, inputFile, modelSent);
		String outSent = IOUtils.toString(builderSent.start().getInputStream());
		
		String[] outSentSplit = outSent.split("sentence number:");
		System.out.println(outSentSplit[0]);
		
		String sentFile = "sent.txt";
		Files.write(Paths.get(sentFile), (outSentSplit[0]+"\n").getBytes());
		

		String jarFileTok = "jarswithOutput/jtbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar";
		String modelTok = "jarswithOutput/jtbd-framed.gz";

		ProcessBuilder builderTok = new ProcessBuilder("java", "-jar", jarFileTok, sentFile, modelTok);
		String outTok = IOUtils.toString(builderTok.start().getInputStream());
		
		String[] outTokSplit = outTok.split("token number:");
		String t = outTokSplit[0];
		
		String tokFile = "tok.txt";
		Files.write(Paths.get(tokFile), t.getBytes());
		
		
		String jarFilePOS = "JarsModelsJCoRe/jpos-2.3.0-SNAPSHOT-jar-with-dependencies.jar";
		String modelPOS = "JarsModelsJCoRe/jpos-framed.gz";

		ProcessBuilder builderPOS = new ProcessBuilder("java", "-jar", jarFilePOS, "p", sentFile, modelPOS, "posT.txt");
		builderPOS.start().getInputStream();
		
//		String outPOS = IOUtils.toString(builderPOS.start().getInputStream());
//		System.out.println(outPOS);
		
//		String[] outPOSSplit = outPOS.split("token number:");
//		System.out.println(outPOSSplit[0]);
		
//		String posFile = "pos.txt";
//		Files.write(Paths.get(posFile), (outPOS).getBytes());
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

// JarsModelsJCoRe/jsbd-2.3.0-SNAPSHOT-jar-with-dependencies.jar
// JarsModelsJCoRe/exampledata/TestText.txt jsbd-framed.gz
