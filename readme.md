# Amidst API
A command line tool to help you extract biome data from Minecraft. 
This tool uses [amidst](https://github.com/toolbox4minecraft/amidst/releases) to communicate with official Minecraft jar file and extract information.

## Usage
You need Java Runtime Environment to run this. We encourage you to use Java 8.

Usage:
``` java -jar amidst_api.jar dot_folder version seed radius```

E.g. ```java -jar AmidstAPI.jar "C:\\Users\\huqin\\AppData\\Roaming\\.minecraft" "1.13.2" -6271427238188031176 1```

* ```dot_folder``` is where your ```.minecraft``` folder is located, e.g. ```C:\Users\huqin\AppData\Roaming\.minecraft```. Please remember to escape '\\'.

* ```version``` is the Minecraft version that you want to use to generate the biome, e.g. ```1.13.2```

* ```seed``` is the seed that you use for generating the world, e.g. ```-6271427238188031176```

* ```radius``` The area generated is the **square (not circle!)** with the coordinates ```((radius*1024, radius*1024), (-radius*1024, -radius*1024))```. ```10``` is enough for most of the situations.

Data are exported to ```out``` folder. It usually takes 1 hour when the radius is ```10``` (i7-7820HQ, SSD).

## Development
Download amidst jar file from the its [releases](https://github.com/toolbox4minecraft/amidst/releases), and add it as a library. 

## License
MIT License