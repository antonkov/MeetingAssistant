__author__ = 'antonkov'

import utils
import sys

if __name__ == "__main__":
    print('sound_to_text called')
    utils.sound_to_text(sys.argv[1], sys.argv[2])