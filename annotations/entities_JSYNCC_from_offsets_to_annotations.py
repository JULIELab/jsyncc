import glob
import shutil

import pandas as pd
import os

'''
This script produces annotation files on JSynCC's annotations of entities.

Input:
Section files from JSynCC's pipeline and the offset files from 
annotations/JSynCC-2-annotations-entities.zip

Unzip the file an run `python annotations/entities_JSYNCC_from_offsets_to_annotations.py`

Output in 'annotations/entities_annotations'
'''


path_text_files = 'output/sentences'
offsets = 'annotations/entities'
files_with_annotations = 'annotations/entities_annotations'

if not os.path.exists(files_with_annotations):
    os.makedirs(files_with_annotations)

for offset_file in glob.glob(offsets + '**/*.ann'):

    print(offset_file)
    print(os.path.basename(offset_file))

    if os.path.getsize(offset_file) > 0:

        text_file = path_text_files + os.sep + os.path.basename(offset_file.replace('.ann', '.txt'))
        text = open(text_file, 'r').read()

        content = pd.read_csv(offset_file, delimiter='\t', header=None).transpose().to_dict()

        annotations = {}

        for ann in content:

            cat = content[ann][1].split(' ')[0]
            start = content[ann][1].split(' ')[1]

            if content[ann][0].startswith('T'):
                end = content[ann][1].split(' ')[2]

                if ';' not in end:

                    annotations[ content[ann][0] ] = {
                        0: content[ann][1],
                        1: text[int(start):int(end)]
                    }

                else:
                    print(end)
                    print(content[ann][1])
                    s_end = end.split(';')

                    string_to_handle = content[ann][1].replace(cat + ' ', '').split(';')
                    print(string_to_handle)

                    snip = ''
                    for st in string_to_handle:
                        snip = snip + ' ' + text[int(st.split(' ')[0]):int(st.split(' ')[1])]

                    annotations[ content[ann][0] ] = {
                        0: content[ann][1],
                        1: snip
                    }

            else:
                annotations[content[ann][0]] = {
                    1: content[ann][1]
                }

        df_annotations = pd.DataFrame.from_dict(annotations, orient='index')
        print(df_annotations)
        df_annotations.to_csv(
            files_with_annotations + os.sep + os.path.basename(offset_file.replace('.ann', '.txt')).replace('.txt', '.ann'),
            sep='\t',
            header=False,
            quoting=None
        )

        shutil.copyfile(text_file, files_with_annotations + os.sep + os.path.basename(offset_file.replace('.ann', '.txt')))

    else:
         print(offset_file, 'is empty and does not contains annotations.')