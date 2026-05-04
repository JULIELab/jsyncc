package de.julielab.testPipe;

public class TestStringReplacement
{

	public static void main(String[] args)
	{
		String text = "Es wird empfohlen, einen Kardiologen zur weiteren Therapie konsiliarisch hinzuzuziehen (siehe zu dieser Thematik auch Fall 21, 7 Abschn. 21.1.6 und 7 Abschn. 21.1.7 sowie 7 Abb. 21.2).";
		
//		System.out.println(text);
		
		if (text.contains("siehe zu dieser Thematik"))
		{
			String[] t = text.split("siehe zu dieser Thematik");
			text = t[0].substring(0, t[0].length() - 2) + ".";
		}
		
		System.out.println(text);
		
		
		String x = " (. Abb. 1.4)";
		
		System.out.println(x);
		
//		"\\. Abb\\. \\d+\\.\\d+"
		x = x.replaceAll("\\. Abb\\.(\u00A0|\u0020)\\d+\\.\\d+", "");
		
		System.out.println(x);
		
		String y = "10.00-";
		
		System.out.println("y " + y);
		if (y.matches(".+\\d-"))
		{
			System.out.println("y.matches");
		}
		
		String e = "<b>5</b> ";
		System.out.println(e);
		System.out.println(e.matches("<b>\\d</b> +"));
		
		String z = "- 64";
		System.out.println(z);
		System.out.println(z.matches("- \\d+"));
		
	}

}
