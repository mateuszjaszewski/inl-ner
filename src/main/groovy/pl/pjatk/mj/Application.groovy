package pl.pjatk.mj

import com.github.jcrfsuite.util.CrfSuiteLoader
import groovy.json.JsonSlurper
import pl.pjatk.mj.crf.Crf
import pl.pjatk.mj.common.StatsCalculator
import pl.pjatk.mj.linear2.Linear2ApiClient
import pl.pjatk.mj.processor.NkjpProcessor

import static pl.pjatk.mj.common.Utils.saveJsonToFile
import static pl.pjatk.mj.common.Utils.saveResultsToFile

/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
class Application {

    static ConfigObject config
    static JsonSlurper jsonSlurper
    static Crf crf
    static Linear2ApiClient linear2ApiClient
    static StatsCalculator statsCalculator

    static void main(def args) {
        init()

        if (args.size() != 1) {
            println("Set one of the parameters:")
            println("   process_nkjp - to prepare test, dev and train data")
            println("   train - to train CRF using train data")
            println("   test_dev - to test CRF using dev data")
            println("   test - to test CRF and Linear2 using test data")
            println("   tag - to tag input using trained CRF")
            return
        }

        switch (args[0]) {
            case "process_nkjp": processNkjp()
                break
            case "train": crf.train()
                break
            case "test_dev": testCrf(config.data.dev.file as String)
                break
            case "test":
                testCrf(config.data.test.file as String)
                testLinear2(config.data.test.file as String)
                break
            case "tag": tagInput()
                break
        }

    }

    private static void testCrf(String pathToTestFile) {
        List testData = jsonSlurper.parse(new File(pathToTestFile)) as List
        List texts = testData.collect { it.text }
        List results = crf.tag(texts)
        def stats = statsCalculator.calculateStats(results, testData)

        saveJsonToFile("crf_stats.json", stats)
        saveResultsToFile("crf_results.txt", results)
    }

    private static void testLinear2(String pathToTestFile) {
        List testData = jsonSlurper.parse(new File(pathToTestFile)) as List
        List texts = testData.collect { it.text }
        List results = linear2ApiClient.tag(texts)
        def stats = statsCalculator.calculateStats(results, testData)

        saveJsonToFile("linear2_stats.json", stats)
        saveResultsToFile("linear2_results.txt", results)
    }

    private static void tagInput() {
        Scanner scanner = new Scanner(System.in)
        scanner.useDelimiter("\\u001a")
        String input = scanner.next()
        crf.tag(input).each {
            println("${it.value} \t ${it.tag}")
        }
    }

    private static void processNkjp() {
        NkjpProcessor processor = new NkjpProcessor()
        processor.process()
    }

    private static void init() {
        CrfSuiteLoader.load()
        config = new ConfigSlurper().parse(new File("config.groovy").text)
        jsonSlurper = new JsonSlurper()
        crf = new Crf()
        linear2ApiClient = new Linear2ApiClient()
        statsCalculator = new StatsCalculator()
    }

}
