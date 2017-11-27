package pl.pjatk.mj

import com.github.jcrfsuite.util.CrfSuiteLoader
import pl.pjatk.mj.crf.Crf
import pl.pjatk.mj.processor.NkjpProcessor
/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
class Application {

    static ConfigObject config

    static void main(def args) {
        CrfSuiteLoader.load()
        loadConfig()

        //FeatureGenerator generator = new FeatureGenerator()
        //println(generator.generateFeatures("ĘbawdN", "ĄŻŹŁ", "."))

        //processNkjp()

        Crf crf = new Crf()
        //crf.train()
        List results = crf.tag("Stefan Żeromski to autor Lalki. Innym przykładem może być Julian Tuwim który tworzył w Toruniu. " +
                "Donald Tusk przyjechał do Warszawy spotkać się z prezesem Jarosławem Kaczyńskim." +
                "Spotkanie odbyło się na Warszawskim Bemowie. IPN to jedna z organizacji. Microsoft to również organizacja. " +
                "Dobrym przykłądem organizacji jest AZS, albo Rada Miasta")


      /*  List results = crf.tag("Do zatrzymania doszło w piątek. Zatrzymanemu odczytano decyzję FSB z 21 listopada br. o konieczności opuszczenia terytorium Federacji Rosyjskiej w ciągu 24 godzin od daty jej otrzymania, pod groźbą przymusowej deportacji. Decyzja FSB, bez wskazania merytorycznego powodu wydalenia oraz możliwości odwołania, oznacza jednocześnie zakaz ponownego wjazdu na terytorium Rosji - podał w komunikacie IPN." +
                "Henryk Głębocki przyjechał do Rosji 14 listopada, by kontynuować prowadzone od 1993 r. badania zbiorów archiwalnych i bibliotecznych w zakresie relacji polsko-rosyjskich w wieku XIX-XX. Jak dodaje IPN, właśnie temu zagadnieniu historyk czemu poświęcił znaczą część swoich prac naukowych.")
*/

        results.each {
            println(it.value + " " + it.tag)
        }

    }

    private static void processNkjp() {
        NkjpProcessor processor = new NkjpProcessor()
        processor.process()
    }

    private static void loadConfig() {
        config = new ConfigSlurper().parse(new File("config.groovy").text)
    }

}
