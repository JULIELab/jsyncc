package de.julielab.jsyncc.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class LanguageTools {

//	public static void main(String[] args) throws IOException, InterruptedException
//	{
//		String x = "Periphere arterielle Verschluss- krankheit (pAVK)";
//		String y = "44-jährige Frau mit Schwere- und Spannungsgefühl der Beine";
//		String z = "i.v.- Nadel."; // i.o.-Zugang
//		String v = "Verschluss- krankheit i.o.- Zugang";
//		String w = "Nadel- Entlasspunktion";
//		String u = "OP-Pro- gramm";
//		String u2 = "OP- Programme";
//		String t = "Uni- Klinik";
//		String s = "Demodex- Milbe";
//		
//		String c = "Charcot-Marie-Tooth- (CMT-) Erkrankung";
//		String c2 = "Schädel-CT- (mit und ohne KM)";
//		
//		String l = "HWS- Beschwerden";
//		String v2 = "Vertex- Fixateur";
//		String a = "Ala- u.";
//		String t2 = "10.00- bis 3- bis";
//		String p = "a.- p.-Projektion";
//		String m = "MR- tomografisch";
//		
////		System.out.println(x);
////		System.out.println(y);
////		System.out.println();
////		System.out.println(removeHyphen(x));
////		System.out.println(removeHyphen(y));
//
//		System.out.println("_" + removeHyphenNew(x) + "_");
//		System.out.println("_" + removeHyphenNew(y) + "_");
//		System.out.println("_" + removeHyphenNew(z) + "_");
//		System.out.println("_" + removeHyphenNew(v) + "_");
//		System.out.println("_" + removeHyphenNew(w) + "_");
//		System.out.println("_" + removeHyphenNew(u) + "_");
//		System.out.println("_" + removeHyphenNew(u2) + "_");
//		System.out.println("_" + removeHyphenNew(t) + "_");
//		
//		System.out.println("_" + removeHyphenNew(s) + "_");
//		System.out.println("_" + removeHyphenNew(c) + "_");
//		System.out.println("_" + removeHyphenNew(c2) + "_");
//		
//		System.out.println("_" + removeHyphenNew(l) + "_");
//		System.out.println("_" + removeHyphenNew(v2) + "_");
//		System.out.println("_" + removeHyphenNew(a) + "_");
//		System.out.println("_" + removeHyphenNew(t2) + "_");
//		System.out.println("_" + removeHyphenNew(p) + "_");
//		System.out.println("_" + removeHyphenNew(m) + "_");
//
//	}
	
	public static String getContentByTika(String resource) {
	
		String content = "";

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(new File(resource)));
			Parser parser = new AutoDetectParser();
			ContentHandler handler = new BodyContentHandler(-1);
			// -1 == no limit
			Metadata metadata = new Metadata();

			parser.parse(is, handler, metadata, new ParseContext());
			content = handler.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static String removeHyphenNew(String element)
	{
		String text = "";
		String[] t = element.split("\n");

		for (int i = 0; i < t.length; i++)
		{
			String[] e = t[i].split(" ");
			String l = "";
			
			for (int j = 0; j < e.length; j++)
			{
				if (
						(!(e[j].equals("-"))) && (e[j].endsWith("-"))
					)
				{
					// (und|oder|sowie|als\\sauch)

					if ((j + 1) < (e.length))
					{
						if (
								!(e[j + 1].matches("(und|oder|sowie|bzw\\.|u\\.|als|wie)"))
								&&
								!(e[j].matches("i\\.v\\.-|i\\.o\\.-"))
								&&
								!( (e[j].matches("Nadel-")) && (e[j+1].startsWith("Ent")) )
								&&
								!( (e[j].matches("Uni-")) && (e[j+1].startsWith("Klinik")) )
								&&
								!( (e[j].matches("OP-")) )
								&&
								!( (e[j].matches("WM-")) )
								&&
								!( (e[j].matches("Charcot-Marie-Tooth-")) && (e[j+1].startsWith("(CMT-)")) )
								&&
								!( (e[j].matches("Demodex-")) && (e[j+1].startsWith("Milbe")) )
								&&
								!(e[j].matches("Schädel-CT-"))
								&&
								!(e[j].endsWith("HWS-"))
								&&
								!(e[j].endsWith("MR-"))
								&&
								!((e[j+1].charAt(0)+"").matches("[A-ZÖÄÜ]"))
								&&
								!(e[j].matches(".*\\d-"))
								&&
								! ( (e[j].equals("a.-")) && (e[j+1].startsWith("p.-")) )
								&&
								! ( (e[j].equals("Meta-")) && (e[j+1].startsWith("zur")) && (e[j+2].startsWith("Diaphyse")) )
								&&
								! ( (e[j].equals("Spiral-")) && (e[j+1].equals("am")) )
							)
						{
							e[j] = e[j].substring(0, e[j].length() - 1) + e[j + 1];
							e[j + 1] = "";
						}
						else if (	// i.v.-Nadel
									(e[j].matches("(i\\.v\\.-|i\\.o\\.-)"))
								||
									( (e[j].matches("Nadel-")) && (e[j+1].startsWith("Ent")) )
								||
									( (e[j].matches("Uni-")) && (e[j+1].startsWith("Klinik")) )
								||
									(e[j].matches("OP-"))
								||
									(e[j].matches("WM-"))
								||
									(e[j].matches("Demodex-"))
								||
									(e[j].endsWith("HWS-"))
								||
									(e[j].endsWith("MR-"))
								||
									((e[j+1].charAt(0)+"").matches("[A-ZÖÄÜ]"))
								||
									( (e[j].equals("a.-")) && (e[j+1].startsWith("p.-")) )
								)
						{
							e[j] = e[j] + e[j + 1];
							e[j + 1] = "";
						}
					}
				}
				
				if (!(e[j].equals(" ")))
				{
						l = l + " " + e[j];
				}
			}

			if (l.startsWith(" "))
			{
				l = l.replaceFirst(" ", "");
			}

			if (l.endsWith(" "))
			{
				l = l.substring(0,l.length()-1);
			}
			text = text + "\n" + l;
		}

		if (text.startsWith("\n"))
		{
			text = text.replaceFirst("\n", "");
		}

		text = text.replaceAll("\\p{Blank}+", " ");

		return text;
	}
}
