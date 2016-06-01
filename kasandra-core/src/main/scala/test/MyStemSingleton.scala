package test

import java.io.File

import ru.stachek66.nlp.mystem.holding.{Factory, Request}

// C:\Users\Jaitl\mystem
object MyStemSingleton {
  val mystemAnalyzer = new Factory("-igd --eng-gr --format json --weight")
    .newMyStem("3.0", Option(new File("C:\\Users\\Jaitl\\mystem\\mystem.exe"))).get

}

object AppExampleScala extends App {

  MyStemSingleton
    .mystemAnalyzer
    .analyze(Request("Есть большие пассажиры мандариновой травы"))
}