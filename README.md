# JSynCC: Jena Synthetic Clinical Corpus 

This is the software to create the corpus JSynCC 2.0, the second version of **Jena Synthetic Clinical Corpus** (April 2026).

## Before Usage: Request Content of Corpus

* Subscribe the following text books as PDF files and load them into the given directories:

	* **01: "Operationsberichte Orthopädie und Unfallchirurgie"**,
	    Author / Editor: Siekmann, H., Irlenbusch, L., Klima, S.,
	    Publisher: Springer-Verlag,
	    Edition: 2. Auflage,
	    DOI: 10.1007/978-3-662-48881-2.
	    * &#8594; store sources under source: books/01-Operationsberichte-Orthopaedie-und-Unfallchirurgie/978-3-662-48881-2.pdf
    * **02: "Operationsberichte Orthopädie: mit speziellen unfallchirurgisch-orthopädischen Eingriffen"**,
        Author / Editor: Siekmann, H., Klima, S.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-642-20790-7.
        * &#8594; store sources under source: books/02-Operationsberichte-Orthopaedie/978-3-642-20790-7.pdf
    * **03: "Operationsberichte Unfallchirurgie"**,
        Author / Editor: Siekmann, H., Irlenbusch, L.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-642-20784-6.
        * &#8594; store sources under source: books/03-Operationsberichte-Unfallchirurgie/978-3-642-20784-6.pdf
    * **04: "Operationsberichte für Einsteiger-Chirurgie: Operation vorbereiten—Bericht diktieren"**,
        Author / Editor: Hagen, M.,
        Publisher: Georg-Thieme-Verlag,
        Edition: -,
        DOI: 10.1055/b-002-39790.
        * &#8594; store sources under source: books/04-Operationsberichte-fuer-Einsteiger/pdf
    * **05: "Fallbeispiele Notfallmedizin: Einprägsam-spannend-mit Lerneffekt"**,
        Author / Editor: Wenzel, V.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-47232-3.
        * &#8594; store sources under source: books/05-Fallbeispiele-Notfallmedizin/978-3-662-47232-3.pdf
    * **06: "Fallbuch Chirurgie: 140 Fälle aktiv bearbeiten"**,
        Author / Editor: Eisoldt, S.,
        Publisher: Georg-Thieme-Verlag,
        Edition: 5., unveränderte Auflage,
        DOI: 10.1055/b-004-140674.
        * &#8594; store sources under source: books/06-Fallbuch-Chirurgie
    * **07: "Komplikationen in der Anästhesie"**,
        Author / Editor: Hübler, M., Koch, T.,
        Publisher: Springer-Verlag,
        Edition: 3., überarbeitete Auflage,
        DOI: 10.1007/978-3-662-43440-6.
        * &#8594; store sources under source: books/07-Komplikationen-in-der-Anaesthesie/978-3-662-43440-6.pdf
    * **08: "Basiswissen Medizin des Alterns und des alten Menschen"**,
        Author / Editor: Zeyfang, A., Denkinger, M., Hagg-Grün, U.,
        Publisher: Springer-Verlag,
        Edition: 2., überarbeitete Auflage,
        DOI: 10.1007/978-3-642-28905-7.
        * &#8594; store sources under source: books/08-Basiswissen Medizin-des-Alterns-und-des-alten-Menschen/978-3-642-28905-7.pdf
    * **09: "Praxis der Intensivmedizin"**,
        Author / Editor: Wilhelm, W.,
        Publisher: Springer-Verlag,
        Edition: 2., aktualisierte und erweiterte Auflage,
        DOI: 10.1007/978-3-642-34433-6.
        * &#8594; store sources under source: books/09-Praxis-der-Intensivmedizin/978-3-642-34433-6.pdf
    * **10: "Patienten aus fremden Kulturen im Notarzt- und Rettungsdienst: Fallbeispiele und Praxistipps"**,
        Author / Editor: Machado, C.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-642-34869-3.
        * &#8594; store sources under source: books/10-Patienten-aus-fremden-Kulturen-im-Notarzt-und-Rettungsdienst-Fallbeispiele-und-Praxistipps/978-3-642-34869-3.pdf
    * **11: "Fallbeispiele Augenheilkunde"**,
        Author / Editor: Thiel, M.A., Bernauer, W., Schüpfer, M.Z., Schmid, M.K.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-642-42219-5.
        * &#8594; store sources under source: books/11-Fallbeispiele-Augenheilkunde/978-3-642-42219-5.pdf
    * **12: "Patientenorientierte Beratung in der Pflege Leitfäden und Fallbeispiele"**,
        Author / Editor: von Reibnitz, C., Sonntag, K., Strackbein, D.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-53028-3.
        * &#8594; store sources under source: books/12-Patientenorientierte-Beratung-in-der-Pflege/978-3-662-53028-3.pdf
    * **13: "Polyneuropathie"**,
        Author / Editor: Zifko, U.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-53871-5.
        * &#8594; store sources under source: books/13-Polyneuropathie/978-3-662-53871-5.pdf
    * **14: "Rheumatologie aus der Praxis: Ein Kurzlehrbuch der entzündlichen Gelenkerkrankungen mit Fallbeispielen"**,
        Author / Editor: Puchner, R. (Eds.),
        Publisher: Springer-Verlag,
        Edition: Zweite, überarbeitete und erweiterte Auflage,
        DOI: 10.1007/978-3-7091-1044-7.
        * &#8594; store sources under source: books/14-Rheumatologie-aus-der-Praxis/978-3-7091-1044-7.txt
    * **15: "Pädiatrie"**,
        Author / Editor: Speer, C.P., Gahr, M.,
        Publisher: Springer-Verlag,
        Edition: 4., überarbeitete Auflage,
        DOI: 10.1007/978-3-642-34269-1.
        * &#8594; store sources under source: books/15-Pädiatrie/978-3-642-34269-1.pdf
    * **16: "Fallbuch Innere Medizin"**,
        Author / Editor: Hellmich, B.,
        Publisher: Georg-Thieme-Verlag,
        Edition: 5., vollständig überarbeitete Auflage,
        DOI: 10.1055/b-0037-142744.
        * &#8594; store sources under source: books/16-Fallbuch-Innere-Medizin
    * **17: "Manual Jungenmedizin"**,
        Author / Editor: Stier, B.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-658-17323-4.
        * &#8594; store sources under source: books/17-Manual-Jungenmedizin/978-3-658-17323-4.pdf
    * **18: "Kindernotfälle im Rettungsdienst"**,
        Author / Editor: Flake, F., Steinichen, F.,
        Publisher: Springer-Verlag,
        Edition: 5., aktualisierte Auflage,
        DOI: 10.1007/978-3-662-49305-2.
        * &#8594; store sources under source: books/18-Kindernotfälle-im-Rettungsdienst/978-3-662-49305-2.pdf
    * **19: "Basiswissen Radiologie: Nuklearmedizin und Strahlentherapie"**,
        Author / Editor: Kahl-Scholz, M., Vockelmann, C. (Eds.),
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-54278-1.
        * &#8594; store sources under source: books/19-Basiswissen-Radiologie/978-3-662-54278-1.pdf
    * **20: "Der klinisch-infektiologische Fall"**,
        Author / Editor: Geiss, H.K., Jacobs, E., Mack, D.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-540-69847-0.
        * &#8594; store sources under source: books/20-Der-klinisch-infektiologische-Fall/978-3-540-69847-0.pdf
    * **21: "Basiswissen Humangenetik"**,
        Author / Editor: Schaaf, C., Zschocke, J.,
        Publisher: Springer-Verlag,
        Edition: 2., überarbeitete Auflage,
        DOI: 10.1007/978-3-642-28907-1.
        * &#8594; store sources under source: books/21-Basiswissen-Humangenetik/978-3-642-28907-1.pdf
    * **22: "Sportkardiologie"**,
        Author / Editor: Niebauer, J.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-43711-7
        * &#8594; store sources under source: books/22-Sportkardiologie/978-3-662-43711-7.pdf
    * **23: "Allgemeine und Spezielle Pathologie"**,
        Author / Editor: Riede, U.-N., Werner, M.,
        Publisher: Springer-Verlag,
        Edition: 2., überarbeitete Auflage,
        DOI: 10.1007/978-3-662-48725-9.
        * &#8594; store sources under source: books/23-Allgemeine-und-Spezielle-Pathologie/2017_Book_AllgemeineUndSpeziellePatholog.pdf
    * **24: "Akutes Abdomen im Kindes-und Jugendalter"**,
        Author / Editor: Mayr, J., Fasching, G.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-55995-6.
        * &#8594; store sources under source: books/24-Akutes-Abdomen-im-Kindes-und-Jugendalter/2018_Book_AkutesAbdomenImKindes-UndJugen.pdf
    * **25: "Operationsberichte Allgemein-, Viszeral-, Gefäß- und Thoraxchirurgie"**,
        Author / Editor: Richter, O., Uhlmann, D.,
        Publisher: Springer-Verlag,
        Edition: 2. Auflage,
        DOI: 10.1007/978-3-662-57283-2.
        * &#8594; store sources under source: books/25-Operationsberichte-Allgemein-Viszeral-Gefaeß-und-Thoraxchirurgie/2018_Book_OperationsberichteAllgemein-Vi.pdf
    * **26: "Nichtorganische Schlafstörungen"**,
        Author / Editor: Marx, C.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-50272-3.
        * &#8594; store sources under source: books/26-Nichtorganische-Schlafstörungen/2016_Book_NichtorganischeSchlafstörungen.pdf
    * **27: "Grundlegendes zur Allgemeinmedizin. Basiswissen Allgemeinmedizin"**,
        Author / Editor: Riedl, B., Peter W.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-53480-9.
        * &#8594; store sources under source: books/27-Basiswissen-Allgemein-Medizin/2017_Book_BasiswissenAllgemeinmedizin.pdf
    * **28: "Basiswissen Gynäkologie und Geburtshilfe"**,
        Author / Editor: Lasch, L., Fillenberg S.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-52809-9.
        * &#8594; store sources under source: books/28-Basiswissen-Gynäkologie/2017_Book_BasiswissenGynäkologieUndGebur.pdf
    * **29: "Ambulanzprotokolle Chirurgische Notfälle"**,
        Author / Editor: Siekmann, H.,
        Publisher: Springer-Verlag,
        Edition: -,
        DOI: 10.1007/978-3-662-57651-9.
        * &#8594; store sources under source: books/29-Ambulanzprotokolle/2019_Book_AmbulanzprotokolleChirurgische.pdf
    * **30: "PubMed German Case Abstract"**,
        * Install _Entres API_ from  https://www.ncbi.nlm.nih.gov/books/NBK179288/
        * Open a terminal and run `esearch -db pubmed -query "Case Reports[Publication Type] AND GER[LA]" | efetch -format xml > allGermanPubMedCaseAbstracts_JSynCC.xml`
        * Wait some minutes!
        * JSynCC contains not all German PubMed Case Abstracts, only from this list.
        * &#8594; store sources under source: books/30-PubMed-Abstracts/allGermanPubMedCaseAbstracts_JSynCC.xml

## Installation and Run

* Install Java and Maven and the packes from [`pom.xml`](pom.xml)
* The metadata of the named sources is stored under [`BookProperties.json`](Books_properties.json). This file is adjusted by storage changes of the source files!

* This code works under Linux. The command-line tools `pdftotext` and `pdftohtml` are required. (It was run under Ubuntu.)
* Usage:
    * _'Main' file:_ [BookReader.java](src/main/de/julielab/germanclinicaltext/BookReader.java)
    * Open a terminal and run
      * `mvn clean package`
      * `mvn exec:java -Dexec.mainClass="de.julielab.germanclinicaltext.readbooks.BookReader"`
      * The full run needs around one hour.

## Output of Corpus
Output is stored under directory `output` with some subdirectories:
* `misc`: contains different text outputs:
  * per every subcorpus one file with the extracted data
  * per every subcorpus one file with the extracted data in the mode of detected sentences
  * jsyncc-text.txt: text of full corpus in one file
  * jsyncc-tokens.txt: all tokens of full corpus
* `txt`: a folder with extracted single `txt`-files, it is named by an identifier of the original resource and the JSynCC internal document type. E.g.: `Flake2016PediatricEmergencies-1-CaseDescription` is extracted from "Kindernotfälle im Rettungsdienst" (Flake, F., Steinichen, F.; 2016) and part of the CaseDescription subcorpus. 
* `sentences`: a folder with extracted single `txt`-files, one line contains one sentences, estimated by [JCoRe](https://github.com/JULIELab/jcore-base) pipelines, schema of names is extended with `framed-sent`
* `tokens`: a folder with extracted single `txt`-files, one line contains one tokens, estimated by [JCoRe](https://github.com/JULIELab/jcore-base) pipelines, schema of names is extended with `framed-token`

* [`stats`](/output/stats): contains files with statistics of JSynCC 2.0's subcorpora and single files, statistics of sentences, tokens and types count by JCoRe pipelines.

* `xml`:
  * `corpus.xml`: XML file with corpus data, text and file names
  * `output/xml/jsyncc-annotations.xml`: XML file with annotations of sentences
  * [`md5Hex_checkSums.xml`](output/xml/md5Hex_checkSums.xml): File of md5 check sums for every extraced txt file.

* [`plots`](plots)
  * some plots with corpus statistics created by scripts from [python_scripts/stats/](python_scripts/stats/)

![JSynCC: Occurences of clinical domains (1)](output/plots/jsyncc_topics_per_document_type.png)

![JSynCC: Occurences of clinical domains (2)](output/plots/jsyncc_topics_per_document_type_2.png)

![JSynCC: Occurences of clinical domains (3)](output/plots/topics.png)


## Annotations

* JSynCC 2.0 is annotated
  * Sentences wise definition of Hl7-CDA section header definition
    * [Annotation Rules](annotations/Section_annotation_rules.md)
    * It is derived from: Christina Lohr, Stephanie Luther, Franz Matthies, Luise Modersohn, Danny Ammon, Kutaiba Saleh, Andreas G. Henkel, Michael Kiehntopf, and Udo Hahn: CDA-Compliant Section Annotation of German-Language Discharge Summaries: Guideline Development, Annotation Campaign, Section Classification. In: AMIA Annual Symposium Proceedings 2018, San Francisco, USA, Nov 3-7. [PMCID: PMC6371337]
    * Orig. Annotation Rules https://zenodo.org/records/7707756
    * [Annotation files without textdata](annotations/JSynCC-2-annotations-sections.zip)
    * [Python-Skript to rebuild the annotations from JSynCC's output](annotations/eannotations/sections_JSYNCC_from_oftsets_to_annotations.py)
  * Clinical entities & PII entities 
    * [Annotation Rules](annotations/Entites_annotation_rules.md)
    * It is derived from:
      * Tobias Kolditz, Christina Lohr, Johannes Hellrich, Luise Modersohn, Boris Betz, Michael Kiehntopf, Udo Hahn: Annotating German Clinical Documents for De-Identification (MedInfo 2019 Aug 25-30 Lyon France) [PMID:31437914] [DOI:10.3233/SHTI190212]
      * Christina Lohr, Luise Modersohn, Johannes Hellrich, Tobias Kolditz, Udo Hahn: An Evolutionary Approach to the of Discharge Summaries. In: Studies in Health Technology and Informatics, Vol. 270: Digital Personalized Health and Medicine - Proceedings of MIE 2020 [PMID: 32570340][DOI:10.3233/SHTI200116]
      * Orig. Annotation Rules:
        * https://zenodo.org/records/7707882
        * https://zenodo.org/records/7707917
      * [Annotation files without textdata / offets](annotations/JSynCC-2-annotations-entities.zip)
      * [Python-Skript to rebuild the annotations from JSynCC's output](annotations/entities_JSYNCC_from_offsets_to_annotations.py)


## Some Python scripts

* [python_scripts/stats/](python_scripts/stats/): contains some scripts to visualizing statistics of content of JSynCC


## JSynCC 2.0 : Statistics 

| subcorpus             | Documents | Sentences  | Tokens      | Types      | Characters    |
|:----------------------|:----------|:-----------|:------------|:-----------|:--------------|
| OperativeReport       | 608       | 30.153     | 325.569     | 24.243     | 2.285.295     |
| CaseDescription       | 264       | 11.761     | 185.385     | 24.958     | 1.167.095     |
| CaseReport            | 600       | 6.353      | 105.369     | 16.208     | 692.122       |
| ReportEmergency       | 153       | 6.021      | 50.606      | 8.560      | 351.160       |
| Discussion            | 124       | 1.452      | 34.903      | 8.575      | 241.054       |
| PubMedCaseAbstract    | 363       | 2.850      | 46.852      | 11.382     | 354.399       |
| **(Full) JSynCC 2.0** | **2.112** | **58.590** | **748.684** | **62.651** | **5.091.125** |


## Licence
* **MIT License**: commercial use, modification, distribution, private use, no liability, no warranty.

## About JSynCC 1 (2018)

* Code and documentation: [https://github.com/julielab/jsyncc](https://github.com/julielab/jsyncc)
* Look into the proceedings of [LREC 2018](http://www.lrec-conf.org/proceedings/lrec2018/papers.html):

	Christina Lohr,Sven Buechel and Udo Hahn: [Sharing Copies of Synthetic Clinical Corpora without Physical Distribution — A Case Study to Get Around IPRs and Privacy Constraints Featuring the German JSYNCC Corpus](http://www.lrec-conf.org/proceedings/lrec2018/summaries/701.html)

	```
	@InProceedings{LOHR18.701,
	  author = {Christina Lohr,Sven Buechel and Udo Hahn},
	  title = {Sharing Copies of Synthetic Clinical Corpora without Physical Distribution — A Case Study to Get Around IPRs and Privacy Constraints Featuring the German JSYNCC Corpus},
	  booktitle = {Proceedings of the Eleventh International Conference on Language Resources and Evaluation (LREC 2018)},
	  year = {2018},
	  month = {may},
	  date = {7-12},
	  publisher = {European Language Resources Association (ELRA)},
	  isbn = {979-10-95546-00-9}
	  }
	```

## About Version 1.1 (02/2020)
* During a expansion we revised all classes of the original JSynCC. We have new counts of the corpus:

    * Books: 	10
    * Text Documents:	903
    * Sentences:	29476
    * Tokens:	368389
