## JSynCC's Annotation Rules for CDA-Based section categories

It is derived from Christina Lohr, Stephanie Luther, Franz Matthies, Luise Modersohn, Danny Ammon, Kutaiba Saleh, Andreas G. Henkel, Michael Kiehntopf, and Udo Hahn: CDA-Compliant Section Annotation of German-Language Discharge Summaries: Guideline Development, Annotation Campaign, Section Classification. In: AMIA Annual Symposium Proceedings 2018, San Francisco, USA, Nov 3-7. [PMCID: PMC6371337] 
https://zenodo.org/records/7707756

1.  **Preamble:** Based on the "Salutation" category of the CDA standard, it contains the "general introductory sentences of a document, e.g., a medical report or a diagnostic document."
2.  **Anamnesis (Medical History):** Includes the specific (medical) query or the reason for a referral and is divided as follows:
    *   (a) Patient history
    *   (b) Family history
3.  **Diagnosis:** Comprises:
    *   (a) **Past diagnoses:** Established prior to the described hospital stay.
    *   (b) **Current diagnosis:** Established during the discharge examination.
    *   If a distinction is not possible, the general category "Diagnosis" should be used. This category also includes ICD diagnoses and TNM classifications.
4.  **Findings (Clinical Observations):** Includes findings obtained during the hospital stay from physical examinations, imaging, and laboratory data—however, only those appearing in the continuous text (no laboratory data listed in tabular form in the appendix of the medical report).
5.  **Procedures and Interventions:** Covers a "brief description of all measures carried out during the stay, including surgeries, interventions, and other measures." It contains information on: "specialist-specific interventions, operations, radiation therapy, light therapy, psychiatric therapy." Mentions that can be classified under the CDA standard "Medical aids and appliances" also fall into this category.
6.  **Allergies, Intolerances, Risks, CAVE:** Refers to any indications regarding risk factors, allergies, intolerances, and observed side effects.
7.  **Medication:** Includes all medication details within the medical report and is divided into subcategories based on the timing of the medication:
    *   (a) Medication on admission
    *   (b) Medication during the stay
    *   (c) Medication upon discharge
    *   If a decision regarding the timing is not possible, the main category "Medication" should be used.
8.  **Course of Treatment / Epicrisis:** Based on the CDA section "Epicrisis / Summary of Stay." It is used for describing summary reviews, interpretations of hospital events, and the initiated therapy. If sentences contain descriptions of both findings and therapy, they should also be labeled as Course / Epicrisis.
9.  **Further Recommended Measures:** Contains recommendations for additional measures yet to be carried out, for example, by the follow-up physician.
10. **Concluding Remarks / Closing Text:** Assigned to the free-text passages formulated at the end of the medical report, which are often linked to farewell formulas and the names of the report's author.
11. **Appendix (Attachments):** Includes all other supplements and attachments that are either directly contained in the document or referenced through a link to an external document.


JSynCC's Extension:

*   **Book Text (Buchtext):** This category should be used for text segments containing descriptions that would not typically appear in a standard medical report. For example, the sentence "A 5-year-old girl is presented by her parents in your consultation room" (from Eisoldt (2017)) would likely not be phrased so generally in an actual medical report; it is assumed that such an example would be formulated with a specific name and date of birth. Across three iterations, experiments were conducted to expand this category into **Book Text (Medically Relevant)**—for medically significant descriptions with a structure untypical for a report—and **Book Text (Medically Irrelevant)**. However, since the susceptibility to errors increases with a higher number of categories, a consensus was reached during discussions with the annotation group to use a single category for textbook-typical segments for the final annotation.

*   **Structural Text (Strukturtext):** This should be annotated when a description introduces the structure of a section or a specific segment, for example, "Procedure:".

