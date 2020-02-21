package de.julielab.jsyncc.readbooks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TopicDefinition {

	public static void giveAllTopicDefinitions(List<TextDocument> listDocuments)
	{
		System.out.println("Topic per Text");

		HashSet<String> topDefinitions = new HashSet<>();
		HashSet<String> topDefinitionsMain = new HashSet<>();

		for (int i = 0; i < listDocuments.size(); i++)
		{
			List<String> topics = listDocuments.get(i).getTopic();
			System.out.print(listDocuments.get(i).getIdLong() + "\t");

			topDefinitionsMain.add(listDocuments.get(i).getTopic().get(0));

			for (int j = 0; j < topics.size(); j++)
			{
				System.out.print(topics.get(j) + "\t");
				topDefinitions.add(topics.get(j));
			}

			System.out.println();
		}

		System.out.println();
		System.out.println("Topics single " + topDefinitions.size());

		for (Iterator<String> iterator = topDefinitions.iterator(); iterator.hasNext();)
		{
			System.out.println(iterator.next());
		}

		System.out.println();
		System.out.println("Main Topics " + topDefinitionsMain.size());

		for (Iterator<String> iterator = topDefinitionsMain.iterator(); iterator.hasNext();)
		{
			System.out.println(iterator.next());
		}
	}
}
