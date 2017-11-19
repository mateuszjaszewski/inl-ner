package pl.pjatk.mj

import groovy.json.JsonBuilder
import groovy.json.StringEscapeUtils
import pl.pjatk.mj.processor.NkjpProcessor

/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
class Application {

    static ConfigObject config

    static void main(def args) {
        loadConfig()

        processNkjp()
    }

    private static void processNkjp() {
        NkjpProcessor processor = new NkjpProcessor()
        processor.process()

        def file = new File(config.data.training.file as String)
        file.getParentFile().mkdirs()
        file.createNewFile()
        file.write(StringEscapeUtils.unescapeJavaScript(new JsonBuilder(processor.getTrainingData()).toPrettyString()))
    }

    private static void loadConfig() {
        config = new ConfigSlurper().parse(new File("config.groovy").text)
    }

}
