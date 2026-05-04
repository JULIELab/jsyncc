import pandas as pd
import matplotlib.pyplot as plt
import os
import collections

"""
This script visualizes the various occurrences of medical domains in JSynCC.
"""


output = 'plots'
if not os.path.isdir(output):
    os.mkdir(output)

meta_data_file = "output/stats/meta_data_sent_tok.csv"

meta_data = pd.read_csv(meta_data_file, delimiter="\t")

print(meta_data)

topics = meta_data["Topics"].tolist()

extracted_topics = []

more_topics = set()

for top in topics:

    if (",") in top:
        more_topics.add(top)

    tops = top.replace("[", "").replace("]", "").replace("]", "").replace('"', "").replace(', ', ",").replace("Polyneuropathie", "Neurologie").replace("Akutes Abdomen", "Gastroenterologie").replace("Augenheilkunde", "Ophthalmologie").replace("Schlafmedzin", "Somnologie")
    tops = tops.split(',')

    if "Pädiatrie" in tops:
        extracted_topics.append("Pädiatrie")
    elif "MISC" not in tops:
        extracted_topics.append(tops[0])

print("more_topics", more_topics)


meta_data_dict = meta_data.transpose().to_dict()

topics_cnts = collections.Counter(extracted_topics)

print(sorted(dict(topics_cnts)))

cnts_frame = pd.DataFrame.from_dict(
    dict(topics_cnts),
    orient='index',
    columns=['Anzahl von JSynCC-Dokumenten'],
).sort_values('Anzahl von JSynCC-Dokumenten', ascending=False)

print(cnts_frame)
print(cnts_frame.sum())

ax = cnts_frame.plot(
    kind='bar',
    figsize=(20, 8),
    fontsize=12,
    rot=32,
    color="grey"
)

ax.bar_label(ax.containers[0])

plt.tight_layout()
plt.savefig(output + os.sep + 'topics.png', dpi=300)


