package de.julielab.jsyncc.readbooks.clindoc;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.hwpf.converter.WordToTextConverter;

//http://stackoverflow.com/questions/5729806/encode-string-to-utf-8
//https://unicode-table.com/de/#control-character
//https://de.wikipedia.org/wiki/Private_Use_Area
//http://openbook.rheinwerk-verlag.de/javainsel/javainsel_04_002.html#dodtp6bd433f4-397b-45a7-b83d-2ea9fad079d5
//http://www.utf8-chartable.de/unicode-utf8-table.pl?start=57344&unicodeinhtml=hex
//http://openbook.rheinwerk-verlag.de/javainsel/javainsel_04_001.html

/**
 * Private Use Area innerhalb von U+E000 bis U+F8FF
 * https://unicode-table.com/en/blocks/private-use-area/
 * 
 * Basic Latin geht von 0020 bis 007F,
 * 007F ist das DEL-Zeichen, wird nicht gebraucht,
 * brauchbar ist 0020 bis 007E  
 * https://unicode-table.com/en/blocks/basic-latin/
 * 
 * Latin-1 Supplement geht von 0080 bis 00FF 
 * brauchbar ist nur 00A1 bis 00FF
 * https://unicode-table.com/en/blocks/latin-1-supplement/
 * 
 */

public class CharacterChecker
{
	public static final Map <Integer, Integer> CollectionCharactersPrivateUseArea = new TreeMap <Integer, Integer>();
	public static final Map <Integer, Integer> CollectionCharactersNotInLatin1 = new TreeMap <Integer, Integer>();
	
	public static Map<Integer, Integer> getCollectionCharactersPrivateUseArea()
	{
		return CollectionCharactersPrivateUseArea;
	}
	
	public static Map<Integer, Integer> getCollectionCharactersNotInLatin1()
	{
		return CollectionCharactersNotInLatin1;
	}

	public static void main(String[] args) throws Exception
	{
		
		String x = "";
		
		File docFile = new File("resources/doc/test3.doc");
		
		x = WordToTextConverter.getText(docFile);
		
		System.out.println(x);
		CheckCharactersOFPrivateUseArea(x);
		
		File docFile2 = new File("resources/doc/wintest4.doc");
		
		x = WordToTextConverter.getText(docFile2);
		
		System.out.println(x);
		CheckCharactersOFPrivateUseArea(x);		

		System.out.println("Bytestring");
		
		byte[] bytestring = x.getBytes();
		
		for (int i = 0; i < bytestring.length; i++)
		{
			System.out.println(bytestring[i]);
		}
		
	}

	public static int ReadIntegerValueOf1Character(char c)
	{
//		int value = -1;
		String y = c + Character.digit(c, 10) + " ";
		
		y = y.substring(0, y.length() - 1);
		
		return Integer.parseInt(y);
	}
	
	public static void CheckCharactersOFPrivateUseArea(String text)
	{
		boolean containcharactersprivateusezone = false;
		boolean containcharactersnotinlatin1 = false;
		
		for (int i = 0; i < text.length(); i++)
		{
			int charvalue = ReadIntegerValueOf1Character(text.charAt(i));
			
			if ( (57343 <= charvalue) && (charvalue <= 63742) )	// Private Use Area
			{
				System.out.println(charvalue);
				containcharactersprivateusezone = true;
				
				if (CollectionCharactersPrivateUseArea.containsKey(charvalue))
				{
					int oldvalue = CollectionCharactersPrivateUseArea.get(charvalue);
					oldvalue = oldvalue + 1;
					CollectionCharactersPrivateUseArea.put(charvalue, oldvalue);
				}
				else
				{
					CollectionCharactersPrivateUseArea.put(charvalue, 1);	
				}
			}
			else
			{
				if ( // brauchbares aus Latin-1
						((charvalue <= 3) && (125 <= charvalue))
					&&
						((charvalue <= 160) && (254 <= charvalue))
					)
				{
					System.out.println(charvalue);
					containcharactersnotinlatin1 = true;
					
					if (CollectionCharactersNotInLatin1.containsKey(charvalue))
					{
						int oldvalue = CollectionCharactersNotInLatin1.get(charvalue);
						oldvalue = oldvalue + 1;
						CollectionCharactersNotInLatin1.put(charvalue, oldvalue);
					}
					else
					{
						CollectionCharactersNotInLatin1.put(charvalue, 1);	
					}
				}
			}
		}
		
		if (containcharactersprivateusezone)
		{
			System.out.println("Text enthält Zeichen aus der Private Use Area!");
		}
		
		if (containcharactersnotinlatin1)
		{
			System.out.println("Text enthält Zeichen, die nicht in Basic Latin, Latin-1-Supplement oder auch nicht Private Use Area liegen!");
		}
	}

	public static void ReadBadCharacters(File docFile) throws Exception
	{

		String x = WordToTextConverter.getText(docFile);
		
//		System.out.println("Beginn Dokument");
//		System.out.println("===============");
//		System.out.println(x);
//		System.out.println("Ende Dokument");
//		System.out.println("=============");
		
		for (int i = 0; i < x.length(); i++)
		{
			char c = x.charAt(i);
			
//			System.out.println(c);
//			System.out.print("_");
			System.out.print(x.charAt(i) + " ");
//			System.out.print("-");
			System.out.print(c + Character.digit(c, 10) + " ");
			
//			System.out.print(c);
//			int n = Character.digit
//			System.out.print(n + " ");
//			System.out.print("#");
			
		}
	}
	
	public static void ReadAllCharacters(File docFile) throws Exception
	{

		String x = WordToTextConverter.getText(docFile);
		
		System.out.println("Beginn Dokument");
		System.out.println("===============");
		System.out.println(x);
		System.out.println("Ende Dokument");
		System.out.println("=============");
		
		for (int i = 0; i < x.length(); i++)
		{
			char c = x.charAt(i);
			System.out.print(x.charAt(i) + " ");
			System.out.print(c + Character.digit(c, 10) + " ");
		}
	}
}