package de.julielab.jsyncc.readbooks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BookProperties
{
	public String bookId = "";
	public String sourceShort = "";
	public String bookPath = "";
	public String title = "";
	public List<String> topics = new ArrayList<String>();

	public String editorAuthor = "";
	public String year = "";
	public String publisher = "";
	public String edition = "";
	public String doi = "";

	public String javaClass = "";
	public List<String> documentType = new ArrayList<String>();
	public String parser = "";

	public List<TextDocument> textDocuments = new ArrayList<>();
	public String tableOfContents = "";

	public int documents = 0;
	public int sentences = 0;
	public int tokens = 0; 
	public int characters = 0;
	public HashSet<String> tokenTypes = new HashSet<String>();

	public int documentsDisc = 0;
	public int sentencesDisc = 0;
	public int tokensDisc = 0; 
	public int charactersDisc = 0;
	public HashSet<String> tokenTypesDisc = new HashSet<String>();

	public String getBookId()
	{
		return bookId;
	}
	public void setBookId(String bookId)
	{
		this.bookId = bookId;
	}

	public String getSourceShort()
	{
		return sourceShort;
	}
	public void setSourceShort(String sourceShort)
	{
		this.sourceShort = sourceShort;
	}

	public String getBookPath()
	{
		return bookPath;
	}
	public void setBook_path(String bookPath)
	{
		this.bookPath = bookPath;
	}

	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}

	public List<String> getTopics()
	{
		return topics;
	}
	public void setTopics(List<String> topics)
	{
		this.topics = topics;
	}

	public String getEditorAuthor()
	{
		return editorAuthor;
	}
	public void setEditorAuthor(String editorAuthor)
	{
		this.editorAuthor = editorAuthor;
	}

	public String getYear()
	{
		return year;
	}
	public void setYear(String year)
	{
		this.year = year;
	}

	public String getPublisher()
	{
		return publisher;
	}
	public void setPublisher(String publisher)
	{
		this.publisher = publisher;
	}

	public String getEdition()
	{
		return edition;
	}
	public void setEdition(String edition)
	{
		this.edition = edition;
	}

	public String getDoi()
	{
		return doi;
	}
	public void setDoi(String doi)
	{
		this.doi = doi;
	}

	public String getJavaClass()
	{
		return javaClass;
	}
	public void setJavaClass(String javaClass)
	{
		this.javaClass = javaClass;
	}

	public List<String> getDocumentType()
	{
		return documentType;
	}
	public void setDocumentType(List<String> textType)
	{
		this.documentType = textType;
	}

	public String getParser()
	{
		return parser;
	}
	public void setParser(String parser)
	{
		this.parser = parser;
	}

	public int getDocuments()
	{
		return documents;
	}
	public void setDocuments(int documents)
	{
		this.documents = documents;
	}

	public List<TextDocument> getTextDocuments()
	{
		return textDocuments;
	}
	public void setTextDocuments(List<TextDocument> textDocuments)
	{
		this.textDocuments = textDocuments;
	}

	public String getTableOfContents()
	{
		return tableOfContents;
	}
	public void setTableOfContents(String tableOfContents)
	{
		this.tableOfContents = tableOfContents;
	}

	public int getSenteces()
	{
		return sentences;
	}
	public void setSentences(int sentences)
	{
		this.sentences = sentences;
	}

	public int getTokens()
	{
		return tokens;
	}
	public void setTokens(int tokens)
	{
		this.tokens = tokens;
	}

	public int getCharacters()
	{
		return characters;
	}
	public void setCharacters(int characters)
	{
		this.characters = characters;
	}

	public int getDocumentsDisc()
	{
		return documentsDisc;
	}
	public void setDocumentsDisc(int documentsDisc)
	{
		this.documentsDisc = documentsDisc;
	}

	public int getSentecesDisc()
	{
		return sentencesDisc;
	}
	public void setSentencesDisc(int sentencesDisc)
	{
		this.sentencesDisc = sentencesDisc;
	}

	public int getTokensDisc()
	{
		return tokensDisc;
	}
	public void setTokensDisc(int tokensDisc)
	{
		this.tokensDisc = tokensDisc;
	}

	public int getCharactersDisc()
	{
		return charactersDisc;
	}
	public void setCharactersDisc(int charactersDisc)
	{
		this.charactersDisc = charactersDisc;
	}

	public HashSet<String> getTokenTypes()
	{
		return tokenTypes;
	}
	public void setTokenTypes(HashSet<String> tokenTypes)
	{
		this.tokenTypes = tokenTypes;
	}

	public HashSet<String> getTokenTypesDisc()
	{
		return tokenTypesDisc;
	}
	public void setTokenTypesDisc(HashSet<String> tokenTypesDisc)
	{
		this.tokenTypesDisc = tokenTypesDisc;
	}
}