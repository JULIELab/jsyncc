package de.julielab.jsyncc.readbooks;

import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.exception.ContextedException;

import de.julielab.jsyncc.readbooks.casereports.discussion.CasesOphthalmology;
import de.julielab.jsyncc.readbooks.casedescriptions.discussion.CasesEmergency;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesCulture;
import de.julielab.jsyncc.readbooks.casedescriptions.CasesAnesthetics;


public interface BookExtractor {

	/**
	 * Parses PDF file. Returns plain text string.
	 * 
	 * @param pdfPath
	 * @exception ContextedException
	 * @return
	 */
	public String parseBook(Path pdfPath) throws ContextedException;

	/**
	 * Extracts text documents from a book's plain text.
	 * 
	 * @param plainText
	 * @return
	 */
	public List<TextDocument> extractContent(String plainText);

	/**
	 * Returns true if the extractor instance applies to the supplied plain text,
	 * false otherwise.
	 * 
	 * @param plainText
	 * @return
	 */
	public boolean validateText(String plainText);

	/**
	 * Returns extractor object based on directory name.
	 */
	static BookExtractor getExtractor(String dirName) throws ContextedException
	{
		String dirNumber = dirName.split("-")[0];
		BookExtractor extractor = null;

		switch (dirNumber)
		{
			case "05":
				extractor = new CasesEmergency();
				break;
			case "07":
				extractor = new CasesAnesthetics();
				break;
			case "08":
				extractor = new CasesCulture();
				break;
			case "09":
				extractor = new CasesOphthalmology();
				break;
			default:
				throw new ContextedException("Unknown directory: " + dirName + "!");
		}
		return extractor;
	}

}
