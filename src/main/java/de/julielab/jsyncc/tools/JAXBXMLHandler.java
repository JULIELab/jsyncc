package de.julielab.jsyncc.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.julielab.jsyncc.annotation.AnnotatedCorpus;
import de.julielab.jsyncc.annotation.TextAnnoation;
import de.julielab.jsyncc.readbooks.CheckSum;
import de.julielab.jsyncc.readbooks.CollecedCheckSums;
import de.julielab.jsyncc.readbooks.CollecedCorpus;
import de.julielab.jsyncc.readbooks.TextDocument;

public class JAXBXMLHandler
{
	
	// export XML - corpus
	public static void marshalCorpus(List<TextDocument> listOfDocuments, File outputFile)
		throws IOException, JAXBException
	{
		JAXBContext context;
		context = JAXBContext.newInstance(CollecedCorpus.class);

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputFile));

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(new CollecedCorpus(listOfDocuments), writer);
		writer.close();
	}
	
	// export XML - checksums
	public static void marshalCheckSum(List<CheckSum> listOfCheckSums, File outputFile)
		throws IOException, JAXBException
	{
		JAXBContext context;
		context = JAXBContext.newInstance(CollecedCheckSums.class);

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputFile));

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(new CollecedCheckSums(listOfCheckSums), writer);
		writer.close();
	}
	
	// import XML - corpus
	public static List<TextDocument> unmarshalCorpus(File importFile)
		throws JAXBException
	{
		CollecedCorpus col = new CollecedCorpus();

		JAXBContext context = JAXBContext.newInstance(CollecedCorpus.class);
		Unmarshaller um = context.createUnmarshaller();
		col = (CollecedCorpus) um.unmarshal(importFile);

		return col.getListDocuments();
	}
	
	// export XML - annotation
	// export XML - corpus
	public static void marshalAnnotation(List<TextAnnoation> listOfAnnotations, File outputFile)
		throws IOException, JAXBException
	{
		JAXBContext context;
		context = JAXBContext.newInstance(AnnotatedCorpus.class);

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(outputFile));

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(new AnnotatedCorpus(listOfAnnotations), writer);
		writer.close();
	}
}