# JSynCC

This is the software to create the **Jena Synthetic Clinical Corpus**.

## Installation and Reproduction of Results

* Subscribe the following text books (pdf) and load them into the given directories:

    * Siekmann, H., Irlenbusch, L., and Klima, S. (2016). Operationsberichte Orthopädie und Unfallchirurgie. Springer-Verlag.
	--> src/main/resources/books/01-Operationsberichte-Orthopaedie-und-Unfallchirurgie/

    * Siekmann, H. and Klima, S. (2013). Operationsberichte Orthopädie: mit speziellen unfallchirurgisch-orthopädischen Eingriffen. Springer-Verlag.
	--> src/main/resources/books/02-Operationsberichte-Orthopaedie/

    * Siekmann, H. and Irlenbusch, L. (2012). Operationsberichte Unfallchirurgie. Springer-Verlag.
	--> src/main/resources/books/03-Operationsberichte-Unfallchirurgie/

    * Hagen, Monika. (2005). Operationsberichte für Einsteiger-Chirurgie: Operation vorbereiten—Bericht diktieren. Georg Thieme Verlag.
	--> src/main/resources/books/04-Operationsberichte-fuer-Einsteiger/

    * Wenzel, V. (2015). Fallbeispiele Notfallmedizin: Einprägsam-spannend-mit Lerneffekt.  Springer-Verlag.
	--> src/main/resources/books/05-Fallbeispiele-Notfallmedizin/

    * Eisoldt, S. (2017). Fallbuch Chirurgie: 140 Fälle aktiv bearbeiten. Georg Thieme Verlag, 5. unveränderte edition.
	--> src/main/resources/books/06-Fallbuch-Chirurgie

    * Hübler, M. and Koch, T. (2014). Komplikationen in der Anästhesie.  Springer-Verlag. 3., überarb. u. erw. aufl. edition.
	--> src/main/resources/books/07-Komplikationen-in-der-Anaesthesie/

    * Machado, C. (2013). Patienten aus fremden Kulturen im Notarzt- und Rettungsdienst: Fallbeispiele und Praxistipps. Springer-Verlag.
	--> src/main/resources/books/08-Patienten-aus-fremden-Kulturen-im-Notarzt-und-Rettungsdienst-Fallbeispiele-und-Praxistipps/978-3-642-34869-3.pdf

    * Michael A. Thiel (2013). Fallbeispiele Augenheilkunde. Springer-Verlag.
	--> src/main/resources/books/09-Fallbeispiele-Augenheilkunde/

    * Hellmich, B. (2017). Fallbuch Innere Medizin. Georg Thieme Verlag, 5., vollständig überarbeitete Auflage.
	--> src/main/resources/books/10-Fallbuch-Innere-Medizin

* This code works under Linux. The command-line tools "pdftotext" and "pdftohtml" are required.
* Usage: Start BookReader.java and look into the directory /output (`mvn exec:java -Dexec.mainClass="de.julielab.jsyncc.readbooks.BookReader"` from the command line).

## Licence
* **MIT License**: commercial use, modification, distribution, private use, no liability, no warranty 

## About JSynCC

* For more information look into the proceedings of [LREC 2018](http://www.lrec-conf.org/proceedings/lrec2018/papers.html):

	Christina Lohr ,Sven Buechel and Udo Hahn: [Sharing Copies of Synthetic Clinical Corpora without Physical Distribution — A Case Study to Get Around IPRs and Privacy Constraints Featuring the German JSYNCC Corpus](http://www.lrec-conf.org/proceedings/lrec2018/summaries/701.html)


If you publish with this corpus, please cite:
```
@InProceedings{LOHR18.701,
  author = {Christina Lohr ,Sven Buechel and Udo Hahn},
  title = {Sharing Copies of Synthetic Clinical Corpora without Physical Distribution — A Case Study to Get Around IPRs and Privacy Constraints Featuring the German JSYNCC Corpus},
  booktitle = {Proceedings of the Eleventh International Conference on Language Resources and Evaluation (LREC 2018)},
  year = {2018},
  month = {may},
  date = {7-12},
  publisher = {European Language Resources Association (ELRA)},
  isbn = {979-10-95546-00-9}
  }
```

## Revision / Version 1.1 (02/2020)
* During a expansion we revised all classes of the original JSynCC. We have new counts of the corpus:

    * Books: 	10
    * Text Documents:	903
    * Sentences:	29476
    * Tokens:	368389

    * Source:	Siekmann, H., Irlenbusch, L., Klima, S. (2016). Operationsberichte Orthopädie und Unfallchirurgie.
        * Documents:	211	Sentences:	10990	Tokens:	111760
    * Source:	Siekmann, H., Klima, S. (2013). Operationsberichte Orthopädie':' mit speziellen unfallchirurgisch-orthopädischen Eingriffen.
        * Documents:	71	Sentences:	3675	Tokens:	38099
    * Source:	Siekmann, H., Irlenbusch, L. (2012). Operationsberichte Unfallchirurgie.
        * Documents:	55	Sentences:	2604	Tokens:	24676
    * Source:	Hagen, M. (2005). Operationsberichte für Einsteiger-Chirurgie':' Operation vorbereiten—Bericht diktieren. Georg-Thieme-Verlag.
        * Documents:	62	Sentences:	1759	Tokens:	18460
    * Source:	Eisoldt, S. (2017). Fallbuch Chirurgie':' 140 Fälle aktiv bearbeiten.
        * Documents:	140	Sentences:	700	Tokens:	10325
    * Source:	Hellmich, B. (2017). Fallbuch Innere Medizin.
        * Documents:	150	Sentences:	1056	Tokens:	15638
    * Source:	Wenzel, V. (2015). Fallbeispiele Notfallmedizin':' Einprägsam-spannend-mit Lerneffekt.
        * Documents:	96	Sentences:	2716	Tokens:	63109
    * Source:	Hübler, M., Koch, T. (2014). Komplikationen in der Anästhesie.
        * Documents:	35	Sentences:	4607	Tokens:	63250
    * Source:	Thiel, M.A., Bernauer, W., Schüpfer, M.Z., Schmid, M.K. (2013). Fallbeispiele Augenheilkunde.
        * Documents:	72	Sentences:	953	Tokens:	17171
    * Source:	Machado, C. (2013). Patienten aus fremden Kulturen im Notarzt- und Rettungsdienst':' Fallbeispiele und Praxistipps.
        * Documents:	11	Sentences:	416	Tokens:	5901

