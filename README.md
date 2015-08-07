selfies
=======

Try to find selfies of people, with other things in background, on twitter. Original purpose was to [find tourists taking selfies](http://blog.houseofmoran.com/post/126043044893/looking-for-bobby-but-found-paris-instead).

compiling
=========

You must have sbt installed. Assuming so:

    sbt compile

You may see some errors like the following below; they can be ignored as they don't seem to break anything

    [warn] circular dependency found: org.apache.xmlgraphics#batik-svg-dom;1.7->org.apache.xmlgraphics#batik-anim;1.7->...
    [warn] circular dependency found: org.apache.xmlgraphics#batik-anim;1.7->org.apache.xmlgraphics#batik-svg-dom;1.7->...

running
=======

You'll need a set of credentials for talking to twitter, see oauth.properties-template for instructions.

Once you have an "oauth.properties" file, you can run the crawler:

    sbt "run-main com.houseofmoran.selfies.FacesCrawlerApp oauth.properties"
