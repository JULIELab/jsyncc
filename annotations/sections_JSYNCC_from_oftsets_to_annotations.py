import glob
import pandas as pd
import os

'''
This script produces annotation files on JSynCC's annotations of sections.

Input:
Section files from JSynCC's pipeline and the offset files from 
annotations/JSynCC-2-annotations-sections.zip

Unzip the file an run `python annotations/sections_JSYNCC_from_oftsets_to_annotations.py`

Output in 'annotations/sections_annotations'
'''



path_text_files = 'output/sentences'
section_annotations = 'annotations/sections'
files_with_annotations = 'annotations/sections_annotations'


if not os.path.exists(files_with_annotations):
    os.makedirs(files_with_annotations)


for annotation_file in glob.glob(section_annotations + '**/*.tsv'):

    print(annotation_file)
    print(os.path.basename(annotation_file))

    text_file = path_text_files + os.sep + os.path.basename(annotation_file.replace('_sections_offsets.tsv', '.txt'))
    text = open(text_file, 'r').read().split('\n')

    content = pd.read_csv(annotation_file, delimiter='\t', header=None).transpose().to_dict()

    annotations = {}

    print(content)

    for ann in content:
        annotations[content[ann][1]] = text[content[ann][0]]

    out_file = files_with_annotations + os.sep + os.path.basename(annotation_file.replace('_sections_offsets.tsv', '.txt')).replace('.txt', '.csv')

    df_annotations = pd.DataFrame.from_dict(annotations, orient='index')
    df_annotations.to_csv(
        out_file,
        sep='\t',
        header=False,
        quoting=None
    )
    print(out_file)