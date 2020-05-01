# DatToCSV
 Converts No-Intro .DAT to .CSV
## Overview
Originally started off as a program that would extract HTML tags from a file which I later modified to help with [nds-bootstrap](https://github.com/ahezard/nds-bootstrap)'s compatibility list.  I've since extended it to work with other .DAT files and be more user-friendly.  So far I've only tested it with the Nintendo DS, DSi (Digital & Decrypted), Game Boy, and Game Boy Advance .DATs and have all worked, but should work with other non-Nintendo system.
## Issues
This only works for .DAT from **No-Intro's [DAT-o-MATIC](https://datomatic.no-intro.org/)**, .DATs from **Redump** isn't supported yet but shouldn't be too hard to implement.  You'll need to be on the latest verison of Java in order to run the .jar, or just rebuild the source yourself  
## TODO
* Add support for Redump
* Add option to include serial number/gameID if found
* Add option to remove/identify tags for special cartridge types (i.e. *(DSi Enhanced)*, *(SGB Compatible)*, etc.)
* Tweak GUI
* Add more options as request by users.
