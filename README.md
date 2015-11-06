![Banner](http://s6.postimg.org/3xisyu8fl/banner.png)

Rankix is a small tool that can help you to find IMDB ratings for a list of movies with in seconds. Rankix is available in both WEB and CONSOLE.

# Rankix WEB 
**released on 11-6-2015**

**Steps**
1. Goto the movie folder and hold **shift** then click **right mouse button**
2. Select **open command window here**
3. type **tree /f /a | clip**
4. Open [Rankix Website](http://shifar-shifz.rhcloud.com/Rankix)
5. Press **Control + v** to paste the TREE Structure
6. Click the **Rankix!** button
7. That's all :)

**Rankix WEB Demo**

[![IMAGE](http://s6.postimg.org/4xkv84i0h/screenshot_39.png)](https://www.youtube.com/watch?v=5NXnCliu7Hk)


-----------------------------------------------------------------------------


# Rankix CONSOLE
Rankix is a simple Java console program that can help you to find **IMDB** ratings.

**Rankix Console DEMO**

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


	





