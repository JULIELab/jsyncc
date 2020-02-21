package de.julielab.jsyncc.tools;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NormalizationUtils {
	private static final Pattern BULLET = Pattern.compile("^ 5 ", Pattern.MULTILINE);

	private static final Pattern HYPHEN_NEWLINE_LOWER = Pattern.compile("-\\s?\n(?=\\p{javaLowerCase})",
			Pattern.MULTILINE);

	private static final Pattern HYPHEN_NEWLINE_UPPER = Pattern.compile("-\\s?\n(?=[^\\p{javaLowerCase}])",
			Pattern.MULTILINE);

	private NormalizationUtils() {
		// should not be instantiated
	}

	public static String correctBullets(String description) {
		return BULLET.matcher(description).replaceAll(" - ");
	}

	public static String normalizeHyphensAndNewlines(String text) {
		text = HYPHEN_NEWLINE_LOWER.matcher(text).replaceAll("");
		return HYPHEN_NEWLINE_UPPER.matcher(text).replaceAll("-");
	}

	public static String normalizeNewlines(String text) {
		text = Arrays.stream(text.split("\\r?\\n")).map(NormalizationUtils::normalizeLine)
				.collect(Collectors.joining(""));
		text = text.replaceAll("\\p{Z}+", " ");
		text = text.replaceAll("[\r\n]\\p{Z}+[\n\r]", "\n");
		text = text.replaceAll(" *[\n\r]+", "\n");

		if (text.endsWith("\u0020")) {
			return text.substring(0, text.length() - 1);
		} else {
			return text;
		}
	}

	private static String normalizeLine(String line) {
		if (isBulletPoint(line)) {
			return "\n" + line;
		} else if (line.isEmpty()) {
			return "\n";
		}
		return line + " ";
	}

	public static boolean isBulletPoint(String line) {
		return (line.startsWith(" - ") || line.startsWith("    - "));
	}

	public static void printStructuringHyphens(String text) {
		Pattern structuringHyphenPattern = Pattern.compile("\\S*?( +- *\\n)\\S*");
		Matcher structuringHyphenMatcher = structuringHyphenPattern.matcher(text);
		while (structuringHyphenMatcher.find()) {
			System.out.println(structuringHyphenMatcher.group());
		}
	}

	public static void printSubstitutingHyphens(String text) {
		Pattern substitutingHyphenPattern = Pattern.compile("\\S*?(- +\\n)\\S*");
		Matcher substitutingHyphenMatcher = substitutingHyphenPattern.matcher(text);
		while (substitutingHyphenMatcher.find()) {
			System.out.println(substitutingHyphenMatcher.group());
		}
	}

	public static void printConcatenatingHyphens(String text) {
		Pattern concatenatingHyphenPattern = Pattern.compile("\\S*?(-\\n)\\S*");
		Matcher concatenatingHyphenMatcher = concatenatingHyphenPattern.matcher(text);
		while (concatenatingHyphenMatcher.find()) {
			System.out.println(concatenatingHyphenMatcher.group());
		}
	}
}
