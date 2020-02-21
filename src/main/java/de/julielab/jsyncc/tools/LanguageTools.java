package de.julielab.jsyncc.tools;

public class LanguageTools {

	public static String removeHyphenNew(String element) {
		String text = "";
		String[] t = element.split("\n");

		for (int i = 0; i < t.length; i++) {
			
			String[] e = t[i].split(" ");
			String l = "";

			for (int j = 0; j < e.length; j++) {
				
//				System.out.print("e[j] " + e[j] + " " + j + " " + e.length); // TODO Logger
//				if ((j + 1) < (e.length))
//				{
//					System.out.println(" " + e[j+1]);
//				}
//				System.out.println();
				
				if ((!(e[j].equals("-"))) && (e[j].endsWith("-"))) {
					// (und|oder|sowie|als\\sauch)

					if ((j + 1) < (e.length))
					{
						if (		!(e[j + 1].matches("(und|oder|sowie|bzw\\.|u\\.|als|wie|aber|bis|mit)"))
								&& !(e[j].matches("i\\.v\\.-|i\\.o\\.-"))
								&& !((e[j].matches("Nadel-")) && (e[j + 1].startsWith("Ent")))
								&& !((e[j].matches("Uni-")) && (e[j + 1].startsWith("Klinik")))
								&& !((e[j].matches("OP-"))) && !((e[j].matches("WM-")))
								&& !((e[j].matches("Charcot-Marie-Tooth-")) && (e[j + 1].startsWith("(CMT-)")))
								&& !((e[j].matches("Demodex-")) && (e[j + 1].startsWith("Milbe")))
								&& !(e[j].matches("Schädel-CT-"))
								&& !(e[j].endsWith("HWS-"))
								&& !(e[j].endsWith("HNO-"))
								&& !(e[j].endsWith("MR-"))
								&& !((e[j + 1].charAt(0) + "").matches("[A-ZÖÄÜ]"))
								&& !(e[j].matches(".*\\d-"))
								&& !((e[j].equals("a.-")) && (e[j + 1].startsWith("p.-")))
//								&& ((e[j].equals("so-")) && (e[j + 1].equals("wie")))
								&& !((e[j].equals("End-")) && (e[j + 1].startsWith("zu-Seit-")))
								&& !((e[j].equals("Prolene-")) && (e[j + 1].matches("\\d/\\d.*")))
								&& !((e[j].equals("Meta-")) && (e[j + 1].startsWith("zur")) && (e[j + 2].startsWith("Diaphyse")))
								&& !((e[j].equals("Spiral-")) && (e[j + 1].equals("am")))
								&& !((e[j].equals("Speise-")) && (e[j + 1].equals("in")) && (e[j + 2].equals("der")) && (e[j + 3].endsWith("röhre")))
							)
						{
//							System.out.println("+ removeHyphen " + e[j] + " " + e[j + 1] + " -> " + e[j].substring(0, e[j].length() - 1) + e[j + 1]);
							// TODO This line into a Logger!

							e[j] = e[j].substring(0, e[j].length() - 1) + e[j + 1];

							// e[j] = e[j].replaceAll("-", "") + e[j + 1]; // entfernt alle Leerzeichen,
							// soll aber nur letztes Leerzeichen entfernen

							e[j + 1] = "";
						}
						else if
							( // i.v.-Nadel
								(e[j].matches("(i\\.v\\.-|i\\.o\\.-)"))
								|| ((e[j].matches("Nadel-")) && (e[j + 1].startsWith("Ent")))
								|| ((e[j].matches("Uni-")) && (e[j + 1].startsWith("Klinik")))
								|| (e[j].matches("OP-"))
								|| (e[j].matches("WM-"))
								|| (e[j].matches("Demodex-"))
								|| (e[j].endsWith("HWS-"))
								|| (e[j].endsWith("HNO-"))
								|| (e[j].endsWith("MR-"))
								|| ((e[j + 1].charAt(0) + "").matches("[A-ZÖÄÜ]"))
								|| ((e[j].equals("a.-")) && (e[j + 1].startsWith("p.-")))
								|| ((e[j].equals("End-")) && (e[j + 1].startsWith("zu-Seit-")))
								|| ((e[j].equals("Prolene-")) && (e[j + 1].matches("\\d/\\d.*")))
//								|| ((e[j].equals("so-")) && (e[j + 1].startsWith("wie")))
							)
						{
							e[j] = e[j] + e[j + 1];
							e[j + 1] = "";
						}
					}
				}

				if (!(e[j].equals(" "))) {

					// if ( (j < (e.length - 1)) && (e[j + 1].equals("")) )
					// {
					l = l + " " + e[j];
					// }
					// else
					// {
					// l = l + " " + e[j];
					// }
				}
			}

			if (l.startsWith(" ")) {
				l = l.replaceFirst(" ", "");
			}

			if (l.endsWith(" ")) {
				l = l.substring(0, l.length() - 1);
			}

			text = text + "\n" + l;
		}

		if (text.startsWith("\n")) {
			text = text.replaceFirst("\n", "");
		}

		text = text.replaceAll(" so- wie ", " sowie ");
		text = text.replaceAll("\\p{Blank}+", " ");

		return text;
	}
}
