name := "nlp"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.8.1",
  "org.joda" % "joda-convert" % "1.7",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-validator" % "commons-validator" % "1.4.0",
//  "org.scalanlp" % "chalk" % "1.3.0",
  "org.scalanlp" % "epic-ner-en-conll_2.11" % "2015.2.19",
  "org.ocpsoft.prettytime" % "prettytime-nlp" % "4.0.0.Final"
/*
  "org.scalanlp" %% "breeze" % "0.11.2",
  "org.scalanlp" %% "breeze-natives" % "0.11.2",
  "org.scalanlp" %% "breeze-viz" % "0.11.2",
  "org.scalanlp" % "epic_2.11" % "0.3.1",
  "org.scalanlp" % "epic-parser-en-span_2.11" % "2015.2.19",
  "org.scalanlp" % "epic-pos-en_2.11" % "2015.2.19",
  "org.scalanlp" % "epic-ner-en-conll_2.11" % "2015.2.19"*/
)
