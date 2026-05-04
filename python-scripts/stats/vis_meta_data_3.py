import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.colors import hsv_to_rgb
import os
import seaborn as sns


"""
This script visualizes the various occurrences of medical domains in JSynCC.
"""

def get_colors(n):
    return [hsv_to_rgb(((0.95 / n) * (i + 0.02), 1, 1)) for i in range(n)]


output = 'plots'
if not os.path.isdir(output):
    os.mkdir(output)

meta_data_file = "output/stats/meta_data_sent_tok.csv"

meta_data = pd.read_csv(meta_data_file, delimiter="\t")

print(meta_data)
print(meta_data[["Topics", "DocumentType"]])

meta_data_dict = meta_data[["Topics", "DocumentType"]].transpose().to_dict()

print(meta_data_dict)
new_meta_data_dict = {}

for entry in meta_data_dict:
    topic = meta_data_dict[entry]["Topics"].replace("[", "").replace("]", "").replace("]", "").replace('"', "").replace(', ', ",").replace("Polyneuropathie", "Neurologie").replace("Akutes Abdomen", "Gastroenterologie").replace("Augenheilkunde", "Ophthalmologie").replace("Schlafmedzin", "Somnologie").split(',')
    if "Pädiatrie" in topic:
        topic = "Pädiatrie"
    elif "MISC" not in meta_data_dict[entry]["Topics"]:
        topic = topic[0]

    document_type = meta_data_dict[entry]["DocumentType"].replace('Long', '')

    if "MISC" not in meta_data_dict[entry]["Topics"]:
        if document_type not in new_meta_data_dict.keys():
            new_meta_data_dict[document_type] = {}

        if topic not in new_meta_data_dict[document_type].keys():
            new_meta_data_dict[document_type][topic] = 1
        else:
            new_meta_data_dict[document_type][topic] = new_meta_data_dict[document_type][topic] + 1


print(new_meta_data_dict)

jsyncc_counts = pd.DataFrame(new_meta_data_dict)

print('len(jsyncc_counts)', len(jsyncc_counts))

jsyncc_counts.rename(columns={
    'CaseDescription': '(I) CaseDescription',
    'CaseReport': '(II) CaseReport',
    'Discussion': '(III) Discussion',
    'OperativeReport': '(IV) OperativeReport',
    'ReportEmergency': '(V) ReportEmergency',
    'PubMedCaseAbstract': '(VI) PubMedAbstract'
}, inplace=True)

jsyncc_counts['sum'] = jsyncc_counts.sum(axis=1)
jsyncc_counts = jsyncc_counts.sort_values(by=['sum'])

print(jsyncc_counts)
print(jsyncc_counts.transpose().sort_index().transpose())
del jsyncc_counts['sum']


jsyncc_counts = jsyncc_counts.transpose().sort_index().transpose()
#table_out = jsyncc_counts.transpose().sort_index()
#table_out = jsyncc_counts#.sort_index()
table_out = jsyncc_counts#s.sort_index()

#jsyncc_counts.transpse().to_latex('jsyncc_topic_counts.tex', escape=False)
print(table_out)
table_out.round(0).astype(str).to_latex(output + os.sep + 'jsyncc_topic_counts_2.tex')
table_out.to_excel(output + os.sep + 'jsyncc_topic_counts_2.xlsx')


ax = table_out.plot(
    kind='barh',
    stacked=True,
    figsize=(15, 5.5),
    fontsize=14,
    #rot=32,
    #color=sns.color_palette("Spectral"),
    #color=get_colors(n=len(table_out.columns)),
    color=sns.color_palette("Spectral", len(table_out.columns)),
    edgecolor="dimgrey"
)

plt.legend(
    labels=table_out.columns,
    #loc='upper left',
    #bbox_to_anchor=(1, 1),
    fontsize=14
)
plt.xlabel("Anzahl von JSynCC-Dokumenten")

plt.tight_layout()
plt.savefig(output + os.sep + 'jsyncc_topics_per_document_type_2.png', dpi=300)
plt.savefig(output + os.sep + 'jsyncc_topics_per_document_type_2.svg', dpi=300)
plt.close()

#plt.figure(figsize=(20, 7))
#sns.heatmap(table_out, annot=True)#, cmap=get_colors(n=len(jsyncc_counts)))
#plt.tight_layout()
#plt.savefig(output + os.sep + 'topics_2_heatmap.png', dpi=300)
