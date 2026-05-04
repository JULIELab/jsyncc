package de.julielab.jsyncc.readbooks.clindoc;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class Statistics1000PA {
	public static void main(String[] args) throws Exception {
		String inputDir = "C:/Users/C01638/Arztbrief/";

		Stream<Path> caseList = Files.walk(Paths.get(inputDir.toString())).filter(Files::isRegularFile)
				.filter(s -> s.toString().endsWith("doc"));

		Map<String, Integer> countTypes = new TreeMap<>();

		for (Iterator<Path> iterator = caseList.iterator(); iterator.hasNext();) {
			String element = iterator.next().toString();

			String[] ID = element.split(Pattern.quote(System.getProperty("file.separator")));

			String filename = ID[ID.length - 1];

			// 2011-12-31-VER.doc

			filename = filename.replaceAll(".doc", "");
			String[] f = filename.split("-");

			String type = f[3];

			if (!(countTypes.containsKey(type))) {
				countTypes.put(type, 1);
			} else {
				int temp = countTypes.get(type);
				countTypes.remove(type);
				countTypes.put(type, temp + 1);
			}
		}

		// for (Map.Entry<String, Integer> entry : countTypes.entrySet()) {
		// System.out.println(entry.getKey() + "\t" + entry.getValue());
		// }

		// Zusammenschluss aller FAll.csv

		String listAll = "";

		Stream<Path> caseListINFO = Files.walk(Paths.get(inputDir)).filter(Files::isRegularFile)
				.filter(s -> s.toString().contains("FALL"));

		for (Iterator<Path> iter = caseListINFO.iterator(); iter.hasNext();) {
			String element = iter.next().toString();

			Reader inCSV = new FileReader(element);
			Iterable<CSVRecord> inRecords = CSVFormat.DEFAULT.withDelimiter(';').parse(inCSV);

			for (CSVRecord rec : inRecords) {

				if (listAll.equals("")) {
					for (int i = 0; i < rec.size(); i++) {
						listAll = listAll + "\t" + rec.get(i);
					}
					listAll = listAll + "\n";
				}

				if (rec.get(0).equals("IK") == false) {
					for (int i = 0; i < rec.size(); i++) {
						listAll = listAll + "\t" + rec.get(i);
					}
					listAll = listAll + "\n";
				}
			}
		}

		Files.write(Paths.get("target/listAll.txt"), listAll.getBytes());
	}
}
