![Banner](http://s6.postimg.org/3xisyu8fl/banner.png)
# Rankix
Rankix is a simple Java console program that can help you to find **IMDB** ratings.

[![ScreenShot](http://s6.postimg.org/l8z2ufe41/screenshot_21.png)](http://www.youtube.com/watch?v=jxwGH4DcWb0)

### Commands

**command format**
```
rankix (-r|-u) ("Your-Movie-Directory"|.) 
```
`-r` stands for `RATE` and used to get **RATING**.

`-u` stands for `UNDO` and used to undo the changes.



To fetch ratings for the movies use **-r** flag. Use the **.** (dot) to use the current directory.

```
rankix -r ("Your-Movie-Directory"|.) 
```

To **undo** the directory use **-u** flag. Use the **.**(dot) to use the current directory.

```
rankix -u ("Your-Movie-Directory"|.) 
```



BEFORE AND AFTER
================

**Before Rankixing.**

![Before](http://s6.postimg.org/7v62o8v8x/screenshot_1.png)



**After Rankixing**

![After](http://s6.postimg.org/5f49aed69/screenshot_2.png)


**TIME TAKEN: 2.456 seconds**



Rankixedname format = **(IMDBRating) # (originalFileName) - Rankix (fileExtension)**


Demo
====

**NOTE:** You should download the latest **[.exe](https://github.com/shifarshifz/Rankix/releases/download/v1.0.0/rankix.exe) or [.jar](https://github.com/shifarshifz/Rankix/releases/download/v1.0.0/rankix.jar)** version of Randix to try the Demo.

Suppose you've a directory full of movies and the path of the folder is **D:/Videos/Movies**

Now, if you want to run Rankix under **Movies** directory, then run 

```
rankix -r "D:/Videos/Movies"
```

if you want to remove the Rankixed name and keep the old name, then run

```
rankix -u "D:/Videos/Movies"
```


DOUBLE CLICK
============
You can also **double click** the rankix.exe file and it'll ask you the movie folder path .So you don't want to worry about the commands.


NOTE
====
```
As usual, If you want to run rankix command any where from the command line, 
You have to put the rankix.exe file's path into enviroment variable path. To do this,
Go to My computer -> properties -> advanced -> environment variables -> Path and edit path by adding .exe's directory into path.
```





	
