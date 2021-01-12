#!/usr/bin/awk -f

BEGIN {
   for (key in ENVIRON) {
      if (key ~ /\./ && key !~ /-/) {
         value = ENVIRON[key];
	 updatedKey = key;
         gsub(/\./, "_", updatedKey);
         print("export " updatedKey "=" value);
      }
   }
}
