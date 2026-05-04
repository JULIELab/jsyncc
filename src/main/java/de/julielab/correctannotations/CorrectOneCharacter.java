package de.julielab.correctannotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CorrectOneCharacter
{
	// TODO ersetze \u00AD mit nichts und korrigiere das Annotat
	// TODO ersetze \u202F mit Leerzeichen 

	public static void main(String[] args) throws IOException
	{

		String backUpdir = "/home/christina/git/GermanClinicalReportsFromBooks/backUp";
		
		Stream<Path> files = Files.walk(Paths.get(backUpdir))
				.filter(Files::isRegularFile)
//				.filter(s -> s.toString().endsWith(".ann"))
				.filter(s -> s.toString().endsWith(".txt"))
//				.filter(s -> s.toString().contains("sent"))
				;
		
		resolvechar00AD(files);
//		resolvechar202F(files);

	}

	public static void resolvechar00AD(Stream<Path> filesAG) throws IOException
	{
		int char00AD = 0;
		
		for (Iterator<Path> iterator = filesAG.iterator(); iterator.hasNext();)
		{
			Path p = iterator.next(); //\u202F
			
			List<String>lines = Files.readAllLines(p);
			String content = "";
			
			for (int i = 0; i < lines.size(); i++)
			{
				content = content + "\n" + lines.get(i);
			}
			
			content = content.replaceFirst("\n", "");
			
			if (content.contains("\u00AD"))
			{
				System.out.println(p + " contains u 00AD");
				char00AD++;
				content = content.replaceAll("\u00AD", "");
				Files.write(p, content.getBytes());
//				System.out.println(content);
			}
			
			// TODO Korrektur des Annotates fehlt noch
			// TODO prüfe noch, ob Index der Wörter zum Index von Brat passt
			// Brauche dazu die Stelle des falschen Zeichens
		}
		System.out.println("char00AD " + char00AD);
	}
	
	public static void resolvechar202F(Stream<Path> filesAG) throws IOException
	{
		int char202F = 0;
		
		for (Iterator<Path> iterator = filesAG.iterator(); iterator.hasNext();)
		{
			Path p = iterator.next(); //\u202F
			
			List<String>lines = Files.readAllLines(p);
			String content = "";
			
			for (int i = 0; i < lines.size(); i++)
			{
				content = content + "\n" + lines.get(i);
			}
			
			content = content.replaceFirst("\n", "");
			
			if (content.contains("\u202F"))
			{
				System.out.println(p + " contains u 202F");
				char202F++;
				content = content.replaceAll("\u202F", " ");
				
				Files.write(p, content.getBytes());
			}
		}
		System.out.println("char202F " + char202F);
		
	}

}
