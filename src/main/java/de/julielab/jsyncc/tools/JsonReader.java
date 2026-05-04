package de.julielab.jsyncc.tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.julielab.jsyncc.readbooks.BookProperties;
import de.julielab.jsyncc.readbooks.PMIDkey;

public class JsonReader
{
	public static void main(String[] args)
	{
		String jsonFile = "Books_properties.json";
		List<BookProperties> listBookProperties = getBookList("Books_properties.json");

		for (int i = 0; i < listBookProperties.size(); i++)
		{
			System.out.println(listBookProperties.get(i).getBookId());
			System.out.println(listBookProperties.get(i).getSourceShort());
			System.out.println(listBookProperties.get(i).getBookPath());
			System.out.println(listBookProperties.get(i).getTitle());
			System.out.println(listBookProperties.get(i).getEditorAuthor());
			System.out.println(listBookProperties.get(i).getYear());
			System.out.println(listBookProperties.get(i).getPublisher());
			System.out.println(listBookProperties.get(i).getEdition());
			System.out.println(listBookProperties.get(i).getDoi());
			System.out.println(listBookProperties.get(i).getJavaClass());
			System.out.println(listBookProperties.get(i).getTextDocuments());
			//System.out.println(listBookProperties.get(i).getTextDocSections());
			//System.out.println(listBookProperties.get(i).getSentences());
			//System.out.println(listBookProperties.get(i).getTokens());
		}

		BookProperties bookPropertiesbyId = getBookPropertyById("01", jsonFile);
		System.out.println(bookPropertiesbyId.getBookPath());
	}

	public static BookProperties getBookPropertyById(String id, String jsonFile)
	{
		JSONParser jsonParserBooks = new JSONParser();
		BookProperties bookPropertiesSelected = new BookProperties();

		try (FileReader reader = new FileReader(jsonFile))
		{
			Object obj = jsonParserBooks.parse(reader);
			JSONArray bookList = (JSONArray) obj;

			for (Object object : bookList)
			{
				BookProperties bookProperties = parseBookObject((JSONObject) object);
				if (bookProperties.getBookId().equals(id))
				{
					bookPropertiesSelected = bookProperties;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return bookPropertiesSelected;
	}

	public static List<BookProperties> getBookList(String jsonFile)
	{
		JSONParser jsonParserBooks = new JSONParser();
		List<BookProperties> listBookProperties = new ArrayList<>();

		try (FileReader reader = new FileReader(jsonFile))
		{
			Object obj = jsonParserBooks.parse(reader);
			JSONArray bookList = (JSONArray) obj;

			for (Object object : bookList)
			{
				BookProperties bookProperties = parseBookObject((JSONObject) object);
				listBookProperties.add(bookProperties);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return listBookProperties;
	}

	@SuppressWarnings("unchecked")
	public static BookProperties parseBookObject(JSONObject book)
	{
		JSONObject bookObject = (JSONObject) book.get("books");
		BookProperties bookProperties = new BookProperties();

		bookProperties.setBookId(			(String)		bookObject.get("id"));
		bookProperties.setSourceShort(		(String)		bookObject.get("source_short"));
		bookProperties.setBook_path(		(String)		bookObject.get("book_path"));
		bookProperties.setTitle(			(String)		bookObject.get("title"));
		bookProperties.setTopics(			(List<String>)	bookObject.get("topics"));

		bookProperties.setEditorAuthor(		(String)		bookObject.get("author_editor"));
		bookProperties.setYear(				(String)		bookObject.get("year"));
		bookProperties.setPublisher(		(String)		bookObject.get("publisher"));
		bookProperties.setEdition(			(String)		bookObject.get("edition"));
		bookProperties.setDoi(				(String)		bookObject.get("doi"));
		bookProperties.setJavaClass(		(String)		bookObject.get("java_class"));
		bookProperties.setDocumentType(		(List<String>)	bookObject.get("text_type"));
		bookProperties.setParser(			(String)		bookObject.get("parser"));
		//bookProperties.setDocuments(		(String)		bookObject.get("documents"));
		//bookProperties.setTextDocSections((String)		bookObject.get("text_doc_sections"));
		//bookProperties.setSentences(		(String)		bookObject.get("sentences"));
		//bookProperties.setTokens(			(String)		bookObject.get("tokens"));

		bookProperties.setJavaClass(		(String)		bookObject.get("java_class"));
		bookProperties.setTableOfContents(	(String)		bookObject.get("table_of_contents"));

		return bookProperties;
	}
	
	@SuppressWarnings("unchecked")
	public static PMIDkey parsePMIDkeyObject(JSONObject pmidkey)
	{
		JSONObject PMIDkeyObject = (JSONObject) pmidkey.get("keys");
		PMIDkey PMIDkey = new PMIDkey();

		PMIDkey.setPMIDid(   (String) PMIDkeyObject.get("pmid"));
		PMIDkey.setJSYNCCid( (String) PMIDkeyObject.get("jsyncc_id"));

		return PMIDkey;
	}

	public static String getPMIDkey(String jsonFile, String pmid)
	{
		JSONParser jsonParserKeys = new JSONParser();
		//List<PMIDkey> listkeys = new ArrayList<>();
		String jsyncc_id = "";

		try (FileReader reader = new FileReader(jsonFile))
		{
			Object obj = jsonParserKeys.parse(reader);
			JSONArray pmidkeyList = (JSONArray) obj;

			for (Object object : pmidkeyList)
			{
				//BookProperties bookProperties = parseBookObject((JSONObject) object);
				PMIDkey pmidkey = parsePMIDkeyObject((JSONObject) object);
				//listkeys.add(pmidkey);
				
				if (pmid.equals(pmidkey.PMIDid))
				{
					jsyncc_id = pmidkey.JSYNCCid;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return jsyncc_id;
	}
}