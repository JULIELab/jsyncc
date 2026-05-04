### JSynCC's Entities -- Annotation Rules

# Clinical Entities

*   **Diagnosis** is defined as the identification or determination of a disease characterizing the patient described in the medical report. A disease is defined here as typically having a defined ICD-10 code. (Note: Annotating specific ICD classification designations is not part of the task).
    *   Excluded are designations from the **R-group** or Chapter XVIII of the ICD-10 code (Symptoms; primarily R00–R49), as well as codes **C79** ("Secondary malignant neoplasm of other and unspecified sites") and **C78** ("Secondary malignant neoplasm of respiratory and digestive organs"), colloquially known as metastasized cancer.
*   **Findings** are defined as medically relevant physical or psychological manifestations, facts, changes, or conditions of the patient described in the report, which were collected by medical professionals (e.g., physicians). This includes data based on laboratory results, TNM, imaging (X-ray, CT, MRI, ultrasound), or physical examinations.
*   **Symptom** is defined as complaints reported by the patient.

The following attributes are defined for these three entities:

*   **[Attribute] Modality:** This should be used when an annotation is not definitively present; it can take the values **unclear (unklar)**, **suspected (Verdacht)**, or **excluded (ausgeschlossen)**. If a statement regarding a diagnosis, finding, or symptom is not excluded (i.e., it is present), no modality is to be annotated.
*   **[Attribute] Temporal Aspect:** This should be used when the mention of a diagnosis is not current; it can take the values **previous (vorhergehend)**, **unclear (unklar)**, or **recurrent (rezidiv)**. If a subject of a description is labeled as "chronic," it is interpreted as current and thus does not need to be explicitly annotated with a temporal attribute.
*   **[Attribute] Complexity:** This is used when at least two units of information are described in a mixed or combined manner. While the tool *Brat* offers the `AddFrag` command to link two annotated text segments that belong together but are separated, the **Complexity** attribute is intended to avoid `AddFrag` by annotating continuous passages as a single annotation span where possible. (Example: "Musculoskeletal system, hands and large joints unremarkable"—where "unremarkable" refers to several anatomical locations).
*   **[Attribute] Family History:** This is defined specifically for the **Diagnosis** entity and should be used for statements regarding the patient's relatives.

*   **`KrebsStreut` (Metastasized Cancer):** In the previous annotation of 3000PAJ, ICD-10 codes **C79** and **C78**—colloquially referred to as "cancer that has spread"—were explicitly excluded. For **JSynCC 2.0**, this category was introduced to estimate the required effort and should be used specifically for ICD-10 codes C79 and C78.
*   **`pregnant`:** A new category, **pregnant**, was introduced in the final annotation to describe information regarding pregnancies. The previous annotation of the 3000PAJ data did not cover the handling of pregnancy descriptions. **JSynCC 2.0** includes data on weeks of pregnancy through excerpts from Lasch and Fillenberg (2017). During the training phase, discussions within the annotation group led to the decision to distinguish these descriptions from the definitions of Diagnosis, Symptom, and Findings, resulting in the definition of this new type.

# PII Entities

*   **Person** includes all names of individuals and is divided into subcategories (if ambiguous, use the main category "Person"):
    **Staff** for medical personnel.
    **Patient** for the described individual being treated.
    **Relative** for family members of the person being treated.
*   **Date** is used for all dates except the date of birth; dates of birth receive a separate annotation as **Birthdate**.
*   **Age** is used to annotate all age specifications of patients as well as their relatives.
*   **Location** includes all geographical information and addresses, including street, house number, zip code (PLZ), city, district, state, country, as well as the room number of a ward.
*   **MedicalUnit** contains all medical facilities, including the names of specific institutions.
*   **ID** defines all identifiable numbers or codes, for example, patient or case numbers, IDs from medical subsystems, and insurance IDs.
*   **Typist** defines abbreviations (or usernames) of transcription staff and secretariats specified in the letterhead of a medical report.
*   **Contact** (URLs, IP addresses, e-mail addresses, fax, and telephone numbers).
*   **Other** includes all passages worthy of protection for which none of the previously mentioned categories apply.

*   **PII Category Extensions:** expanded to include **Body Height** and **Weight**.

*   **Profession:** This category is used to specify the patient's occupation (e.g., baker, mason, gas station operator). Such designations were previously annotated as **Other**.
*   **[Attribute]  `isAnonym`:** This attribute should be used when annotated text segments are already anonymous and no longer allow an identity to be inferred based on their description (e.g., "N.N.", "Ms. M.", "X.Y.").

