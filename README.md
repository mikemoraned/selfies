selfies
=======

Try to find selfies of people, with other things in background, on twitter. Original purpose was to [find tourists taking selfies](http://blog.houseofmoran.com/post/126043044893/looking-for-bobby-but-found-paris-instead).

compiling for local running
===========================

You must have sbt installed. Assuming so:

    sbt compile

You may see some errors like the following below; they can be ignored as they don't seem to break anything

    [warn] circular dependency found: org.apache.xmlgraphics#batik-svg-dom;1.7->org.apache.xmlgraphics#batik-anim;1.7->...
    [warn] circular dependency found: org.apache.xmlgraphics#batik-anim;1.7->org.apache.xmlgraphics#batik-svg-dom;1.7->...

running locally
===============

You'll need a place to store the data:

    mkdir ../selfies-data/
    
(We store it outside the current dir because it creates a lot of files and when I open this dir, Intellij
spends ages indexing all these files)

You'll need a set of credentials for talking to twitter, see src/main/resources/twitter4j.properties-template for instructions.

Once you have a "twitter4j.properties" file, you can run the crawler:

    sbt "run-main com.houseofmoran.selfies.FacesCrawlerApp"
