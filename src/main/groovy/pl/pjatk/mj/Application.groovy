package pl.pjatk.mj

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
    }

    private static void loadConfig() {
        config = new ConfigSlurper().parse(new File("config.groovy").text)
    }

}
