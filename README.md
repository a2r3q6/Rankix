![Banner](http://s6.postimg.org/3xisyu8fl/banner.png)
# Rankix
Rankix is a simple console program. It can fetch **IMDB** rating from the web and add those ratings into the movie file name.

### Commands
To fetch ratings for the movies under the directory (*D:/Videos/Films*).
```
rankix -r "D:/Videos/Films"
```

To **remove/undo** the Rankix name format from the file.
```
rankix -u "D:/Videos/Films"
```

BEFORE AND AFTER
================
**Before Rankixing.**
![Before](http://s6.postimg.org/7v62o8v8x/screenshot_1.png)

**After Rankixing**
![After](http://s6.postimg.org/5f49aed69/screenshot_2.png)

**TIME TOOK: 2.456 seconds**

#### NOTE:
The decimal points at the starting of the file specifies the **IMDB** rating.



Demo
====

You must download the rankix.exe file to try this demo.

Suppose you've a directory full of movies and the path of the folder is **D:/Videos/Movies**

Now, if you want to run Rankix under **Movies** directory, then run 
```
rankix -r "D:/Videos/Movies"
```

if you want to remove the Rankixed name and keep the old name, then run
```
rankix -u "D:/Videos/Movies"
```

ADDITIONAL ACCESS
=================
You can also **double click** the rankix.exe file and it'll ask you the movie folder path .So you don't want to worry about the commands.

NOTE
====
As usual, If you want to run **rankix** command any where from the command line, 
You have to put the **rankix.exe** file's path into enviroment variable path. To do this,
Go to **My computer -> properties -> advanced -> environment variables -> Path** and edit path by adding .exe's directory into path.





