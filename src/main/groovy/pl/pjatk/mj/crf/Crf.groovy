package pl.pjatk.mj.crf

import com.github.jcrfsuite.CrfTagger
import com.github.jcrfsuite.CrfTrainer
import com.github.jcrfsuite.util.Pair
import groovy.json.JsonSlurper

import static pl.pjatk.mj.Application.config
/**
 * Created by Mateusz Jaszewski on 26.11.2017.
 */
class Crf {

    private File tempDirectory
    private FeatureGenerator featureGenerator

    Crf() {
        tempDirectory = new File(config.crf.temp.directory as String)
        tempDirectory.mkdirs()
        featureGenerator = new FeatureGenerator()
    }

    void train() {
        println("Loading training data")

        JsonSlurper jsonSlurper = new JsonSlurper()
        List trainingTexts = jsonSlurper.parse(new File(config.data.training.file as String)) as List

        File crfTrainingFile = new File(tempDirectory.path + File.separator + "training.txt")
        crfTrainingFile.createNewFile()
        PrintWriter printWriter = new PrintWriter(crfTrainingFile)

        println("Generating training file for CRF...")

        trainingTexts.each {
            List segments = it.segments
            for (def i = 0; i < segments.size(); i++) {

                String tag = segments[i].tag

                String previous = i - 1 > 0 ? segments[i].value : null
                String current = segments[i].value
                String next = i + 1 < segments.size() ? segments[i].value : null

                String features = featureGenerator.generateFeatures(previous, current, next)
                printWriter.println("$tag\t$features")
            }
        }
        printWriter.flush()
        printWriter.close()

        println("CRF training...")
        CrfTrainer.train(crfTrainingFile.path, config.crf.model.file as String)
    }

    void tag(String text) {
        File fileToTag = new File(tempDirectory.path + File.separator + "text.txt")
        fileToTag.createNewFile()
        PrintWriter printWriter = new PrintWriter(fileToTag)

        List words = text.split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})")

        for (def i = 0; i < words.size(); i++) {
            String previous = i - 1 > 0 ? words[i] : null
            String current = words[i]
            String next = i + 1 < words.size() ? words[i] : null

            String features = featureGenerator.generateFeatures(previous, current, next)
            printWriter.println(features)
        }

        printWriter.flush()
        printWriter.close()

        CrfTagger tagger = new CrfTagger(config.crf.model.file as String)
        List<List<Pair<String, Double>>> tagProbLists = tagger.tag(fileToTag.path)

        for (def i = 0; i < words.size(); i++) {
            println(words[i] + " " + tagProbLists[0][i].first)
        }


    }

}
