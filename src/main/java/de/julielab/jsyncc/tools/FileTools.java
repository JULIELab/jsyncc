package de.julielab.jsyncc.tools;

import java.io.File;

public final class FileTools {

	/**
	 * @param dir name
	 * @return the single PDF file in the {@code dir} or {@code null} if folder is empty
	 * @throws {@link IllegalStateException}, {@code dir} is not a directory or contains multiple PDF files
	 */
	public static String getSinglePDFFileName(String dir) {
		File folder = new File(dir);
		if (!folder.isDirectory()) {
			throw new IllegalStateException(dir + " is not a directory");
		}
		File pdfFile = null;
		for (File f : folder.listFiles()) {
			if (f.getName().endsWith(".pdf")) {
				if (pdfFile != null) {
					throw new IllegalStateException("Multiple PDF files in folder: " + pdfFile.getName() + ", " + f.getName());
				}
				pdfFile = f;
			}
		}
		if (pdfFile == null) {
			System.err.println(dir + " contains no PDF files, skipping documents");
		}
		return pdfFile == null ? null : pdfFile.getAbsolutePath(); 
	}
	
}
