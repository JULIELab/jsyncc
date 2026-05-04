package de.julielab.correctannotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.tika.parser.microsoft.ooxml.OOXMLExtractorFactory;

public class writeAnonymAnnotations
{
	static int len = 0;
	static int idAnno = 0;
	static String preAnno = "";
	
	
	static String dataAnonym = "C:/Users/C01638/Desktop/anonym-iterations";
//	static String dataAnonym = "C:/Users/C01638/Desktop/anno-abgleich/anonym-finish";
//	static String dataAnonym = "C:/Users/C01638/Desktop/anno-abgleich/anonymization-not-finish/alles";
//	String dataAnonym = "C:/Users/C01638/Desktop/anno-abgleich/anonymization-not-finish";
//	String dataresults = "C:/Users/C01638/Desktop/anno-abgleich/anonymization-not-finish/test";
	
	static String assignment = "assignments.txt";
	static TreeMap<String, String> assignments = new TreeMap<String, String>(); 
	
	static HashMap<Integer, Path> resultAnno = new HashMap<Integer, Path>(); 
	
	public static void main(String[] args) throws IOException
	{
		getAssignments();
		
		String resultsAnnotation = "annotations.txt";
		List<String> resultList = Files.readAllLines(Paths.get(resultsAnnotation));
		
		System.out.println(resultList.size());
		
		for (int s = 0; s < resultList.size(); s++)
		{
			String[] x = resultList.get(s).split(Pattern.quote(System.getProperty("file.separator")));
			String[] y = x[x.length - 2].split("-");
			
			resultAnno.put(Integer.parseInt(y[0]), Paths.get(resultList.get(s)));
		}
		
		System.out.println("resultAnno.size() " + resultAnno.size());
		
		Stream<Path> taskStatesFiles = Files.walk(Paths.get(dataAnonym))
				.filter(s -> s.toString().endsWith("ann"))
				.sorted();
		
		for (Iterator<Path> iterator = taskStatesFiles.iterator(); iterator.hasNext();)
		{
			Path x = iterator.next();
			System.out.println(x);

			String[] e = x.toString().split(Pattern.quote(System.getProperty("file.separator")));
			String[] y = e[e.length - 1].split("-");
			
			String name = e[e.length - 1].replaceAll("\\.ann", "");
			System.out.println("e[e.length - 1] / name " + name);
			
//			String origTextFile = assignments.get(name)
//					.replaceAll(
//							"annotationen" + Pattern.quote(System.getProperty("file.separator"))
//							+ "fertig" + Pattern.quote(System.getProperty("file.separator"))
//							+ "sent",
//						"original");
//			
//			System.out.println("origTextFile " + origTextFile + " " +  Files.exists(Paths.get(origTextFile)));
			
//			String origText = getOrigText(origTextFile);
			
//			System.out.println("y[0] " + y[0]);
			System.out.println( y[0] + " " + resultAnno.get(Integer.parseInt(y[0])) );
			
			List<String> f = Files.readAllLines(x);
			List<String> replaceElements = new ArrayList<String>();

			List<String> t = Files.readAllLines(Paths.get(x.toString().replaceAll("\\.ann", "\\.txt")));
			
			String text = "";
			String textAnn = "";
			
			List<Integer> beginOffsets = new ArrayList<Integer>();
			HashMap<Integer, String> textBeginOffset = new HashMap<Integer, String>();

			for (int i = 0; i < t.size(); i++)
			{
//				System.out.println(t.get(i));
				text = text + "\n"+ t.get(i);
			}

			text = text.replaceFirst("\n", "");
			
			for (int i = 0; i < f.size(); i++)
			{
				String s[] = f.get(i).split("\t");
//				String o[] = s[s.length - 2].split(" ");
				
				if (s[s.length - 2].contains(";"))
				{
					String o[] = s[s.length - 2].split("( )|(;)");
					
					for (int j = 1; j < o.length; j=j+2)
					{
//						System.out.println("o[j] " + j + " " + o[j] + " " + o[j+1] + " " + text.substring(Integer.parseInt(o[j]), Integer.parseInt(o[j+1])));
						replaceElements.add(text.substring(Integer.parseInt(o[j]), Integer.parseInt(o[j+1])));
						beginOffsets.add(Integer.parseInt(o[j]));
						textBeginOffset.put(Integer.parseInt(o[j]), text.substring(Integer.parseInt(o[j]), Integer.parseInt(o[j+1])));
					}
				}
				else
				{
					String o[] = s[s.length - 2].split(" ");
					
//					System.out.println(o[1] + " " + o[2]);
//					System.out.println(text.substring(Integer.parseInt(o[1]), Integer.parseInt(o[2])));
//					System.out.println("s[s.length - 1] " + s[s.length - 1]);
					
					replaceElements.add(s[s.length - 1]);
					beginOffsets.add(Integer.parseInt(o[1]));
					
					textBeginOffset.put(Integer.parseInt(o[1]), s[s.length - 1]);
				}
				
//				System.out.println();
			}
			
			System.out.println("textBeginOffset.size " + textBeginOffset.size());
			
			for (int i = 0; i < text.length(); i++)
			{
				if(beginOffsets.contains(i))
				{
					String offset = textBeginOffset.get(i);
					
					if ( (textBeginOffset.get(i).contains("(")) || (textBeginOffset.get(i).contains(")")) || (textBeginOffset.get(i).contains("?")) ) 
					{
//						System.out.println("(" + "Fehler");
						offset = offset.replaceAll("\\(", "\\\\(");
						offset = offset.replaceAll("\\)", "\\\\)");
						offset = offset.replaceAll("\\?", "\\\\?");
//						textAnn = textAnn + offset.replaceAll(offset, "");
						textAnn = textAnn + offset.replaceAll(offset, "XXXXX");
						
//						origText = origText.replaceFirst(offset, "XXXXX");
					}
					else
					{
//						textAnn = textAnn + textBeginOffset.get(i);
//						textAnn = textAnn + textBeginOffset.get(i).replaceAll(textBeginOffset.get(i), "");

						textAnn = textAnn + textBeginOffset.get(i).replaceAll(textBeginOffset.get(i), "XXXXX");
						
//						origText = origText.replaceAll(offset, "XXXXX");

					}
					i = i + textBeginOffset.get(i).length() - 1;
				}
				else
				{
					textAnn = textAnn + text.substring(i, i + 1);
				}
				
			}
			
			String anonymFile = x.toString().replaceAll("\\.ann", "\\_anonym.txt");
//			String anonymOrigFile = x.toString().replaceAll("\\.ann", "\\_orig_anonym.txt");

			Files.write(Paths.get(anonymFile), textAnn.getBytes("UTF-8"));
//			Files.write(Paths.get(anonymOrigFile), origText.getBytes("UTF-8"));
			
			
			System.out.println(Files.exists(Paths.get(anonymFile)));
			
//			UnionLists.unionContents(x.toString().replaceAll("\\.ann", "\\_anonym.txt"), resultAnno.get(Integer.parseInt(y[0])).toString());
			System.out.println();
			
		}
		
//		UnionLists.writeCSV(dataAnonym + "/labelsAnonym.csv");
	}
	
	public static void getAssignments() throws IOException
	{
		List<String> ass = Files.readAllLines(Paths.get(assignment));
		
		for (int i = 0; i < ass.size(); i++)
		{
			String[] t = ass.get(i).split("\t");

//			System.out.println(t[0] + "\t" + t[1]);
			assignments.put(t[0], t[1]);
		}
	}
	
	public static String getOrigText(String file) throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get(file));
		
		String out = "";
		
		for (int i = 0; i < lines.size(); i++)
		{
			out = out + "\n" + lines.get(i);
		}
		
		out = out.replaceFirst("\n", "");
		
		return out;
	}
	
}
