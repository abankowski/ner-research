package pl.abankowski.nlp.date

import java.io._
import java.util.zip.{ GZIPInputStream, GZIPOutputStream }

import epic.corpora.CONLLSequenceReader
import epic.models.NerSelector
import epic.sequences.{ Segmentation, SemiCRF }
import epic.trees.{ AnnotatedLabel, Span }
import nak.data.Example
import org.apache.poi.ss.formula.functions.T
import org.joda.time.DateTime


object Explorer extends App {

  case class Label(l: String) extends Serializable {
    override def toString = l
  }

  def generate(filename: String) = {

    println(s"Generate SemiCRF and store in file $filename")

    def makeSegmentation(ex: Example[IndexedSeq[String], IndexedSeq[IndexedSeq[String]]]): Segmentation[Label,
      String] = {

      val segments = ex.label.foldLeft(List.empty[(Label, Int, Int)]) {
        case (acc, label) => acc match {
          case head :: tail => head match {
            case (Label(`label`), beg, end) => (Label(label), beg, end + 1) :: tail
            case (Label(nextLabel), beg, end) => (Label(label), end, end + 1) :: head :: tail
          }
          case Nil => List((Label(label), 0, 1))
        }
      }.reverse.map {
        case (label, beg, end) => (label, Span(beg, end))
      }.toIndexedSeq

      Segmentation(segments, ex.features.map(_.mkString), ex.id)
    }

    val file = new File("/Users/abankowski/Documents/praca/evojam/dataset-generator-for-nlp/out.train")
    val in = new BufferedInputStream(new FileInputStream(file))

    val standardTrain = CONLLSequenceReader.readTrain(in).toIndexedSeq

    val segmentedTrain = standardTrain.map(makeSegmentation)

    val crf = SemiCRF.buildSimple(segmentedTrain)

    //    epic.models.deserialize[SemiCRF[AnnotatedLabel, String]](path)


    val oos = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(filename))))
    oos.writeObject(crf)
    oos.close()
    // epic.sequences.SemiCRFInference
    crf
  }

  val tests = Seq(
    """Valved Voice in The Box Expo Center on September 13, 2014""",
    """On April 27, 2012""",
    """On November 3, 2012 in Oasis Church""",
    """Asylum, The Memories""")

  def load[T](name: String) = {
    val input = new FileInputStream(name)
    val gzipin = breeze.util.nonstupidObjectInputStream(new BufferedInputStream(new GZIPInputStream(input)))
    try {
      gzipin.readObject().asInstanceOf[T]
    } finally {
      gzipin.close()
    }
  }

  def loadFromFile(name: String) =
    load[SemiCRF[Label, String]](name)

  val defaultFilename = "model.ser.gz"

  def filename(i: String): String =
    Option(i.dropWhile(_ != "=").drop(1))
      .filter(_.nonEmpty)
      .getOrElse(defaultFilename)

  val crf = args.toList
    .find(_.startsWith("--load"))
    .map(filename)
    .map(loadFromFile)
    .getOrElse(generate(args.toList.find(_ == "--generate").map(filename).getOrElse(defaultFilename)))

  def preprocess(in: String) = epic.preprocess.tokenize(in.toLowerCase)

  tests.foreach(test =>
    println(crf.bestSequence(preprocess(test)).render))

  //  println(SegmentationEval.eval(crf, test))
}
