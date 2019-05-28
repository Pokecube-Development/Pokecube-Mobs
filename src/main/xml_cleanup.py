'''
This will auto-format all XML files, recursively, in the current directory.
This ensures all of the XML files are standarized in their formats.
'''

import os
from bs4 import BeautifulSoup

def scan_directory(directory):
    processes = []
    number = 1
    print('Scanning '+directory)
    for filename in os.listdir(directory):
        truefile = os.path.join(directory, filename)
        if os.path.isdir(truefile):
            scan_directory(truefile)
            continue
        if filename.endswith('.xml'):
            xml_file = os.path.join(directory, filename)
            print(xml_file)
            bs = BeautifulSoup(open(xml_file), 'xml')
            file = open(xml_file,'w')
            file.write(bs.prettify())
            file.close()

scan_directory('.')