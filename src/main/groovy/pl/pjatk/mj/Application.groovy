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
        crf.tag("Stefan Żeromski to autor Lalki. Innym przykładem może być Julian Tuwim który tworzył w Toruniu. " +
                "Donald Tusk przyjechał do Warszawy spotkać się z prezesem Jarosławem Kaczyńskim." +
                "Spotkanie odbyło się na Warszawskim Bemowie. IPN to jedna z organizacji. Microsoft to również organizacja. " +
                "Dobrym przykłądem organizacji jest AZS, albo Rada Miasta")
}

    private static void processNkjp() {
        NkjpProcessor processor = new NkjpProcessor()
        processor.process()
    }

    private static void loadConfig() {
        config = new ConfigSlurper().parse(new File("config.groovy").text)
    }

}
