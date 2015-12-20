![Banner](http://s6.postimg.org/3xisyu8fl/banner.png)


An IMDB Bot that can fetch movie details and ratings of the available movies on your Computer.

### Features

1. Instant movie ratings.
2. Can be sorted according to the rating.
3. The result can be shared with anybody.
4. Detailed Movie view.

The tools is available in both WEB and Console versions.

# WEB Version 
**released on 11-6-2015**

**Steps**

1. Goto the movie folder and hold **shift** then click **right mouse button**
2. Select **open command window here**
3. type **tree /f /a | clip** (linux users just type **tree**)
4. Open [Rankix Website](http://shifar-shifz.rhcloud.com/Rankix)
5. Press **Control + v** to paste the TREE Structure
6. Click the **Rankix!** button
7. That's all :)

**Web Version - Demo**

[![IMAGE](http://s6.postimg.org/4xkv84i0h/screenshot_39.png)](https://www.youtube.com/watch?v=5NXnCliu7Hk)


-----------------------------------------------------------------------------


# Console Version
Rankix is a simple Java console program that can help you to find **IMDB** ratings.

**Console Version - DEMO**

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

PS: double click the `.exe` file to get into the Interactive mode.


TODOs
===========

1. Improve html/cs/js.
2. Implement more cacheing features.
3. Make an Android version.



NOTE
====
```
As usual, If you want to run rankix command any where from the command line, 
You have to put the rankix.exe file's path into enviroment variable path. To do this,
Go to My computer -> properties -> advanced -> environment variables -> Path and edit path by adding .exe's directory into path.
```


	




