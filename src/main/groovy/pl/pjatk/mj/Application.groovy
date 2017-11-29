package pl.pjatk.mj

import com.github.jcrfsuite.util.CrfSuiteLoader
import groovy.json.JsonSlurper
import pl.pjatk.mj.crf.Crf
import pl.pjatk.mj.crf.StatsCalculator
import pl.pjatk.mj.processor.NkjpProcessor
/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
class Application {

    static ConfigObject config

    static void main(def args) {
        CrfSuiteLoader.load()
        loadConfig()

        Crf crf = new Crf()
        crf.train()

        testOnDevData()
    }

    private static void testOnDevData() {
        JsonSlurper jsonSlurper = new JsonSlurper()
        List devData = jsonSlurper.parse(new File(config.data.dev.file as String)) as List

        List texts = devData.collect { it.text }

        Crf crf = new Crf()
        List results = crf.tag(texts)

        StatsCalculator statsCalculator = new StatsCalculator()
        def stats = statsCalculator.calculateStats(results, devData)

        println(stats)
    }



    private static void processNkjp() {
        NkjpProcessor processor = new NkjpProcessor()
        processor.process()
    }

    private static void loadConfig() {
        config = new ConfigSlurper().parse(new File("config.groovy").text)
    }

}
