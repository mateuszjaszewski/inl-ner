package pl.pjatk.mj.crf

import com.github.jcrfsuite.CrfTagger
import com.github.jcrfsuite.CrfTrainer
import com.github.jcrfsuite.util.Pair
import groovy.json.JsonSlurper
import pl.pjatk.mj.common.TextSplitter

import static pl.pjatk.mj.Application.config
/**
 * Created by Mateusz Jaszewski on 26.11.2017.
 */
class Crf {

    private File tempDirectory
    private TextSplitter textSplitter
    private FeatureGenerator featureGenerator

    Crf() {
        tempDirectory = new File(config.crf.temp.directory as String)
        tempDirectory.mkdirs()
        featureGenerator = new FeatureGenerator()
        textSplitter = new TextSplitter()
    }

    void train() {
        println("Loading training data")

        JsonSlurper jsonSlurper = new JsonSlurper()

        List trainingTexts = jsonSlurper.parse(new File(config.data.training.file as String)) as List
        File trainingFile = prepareCrfSuiteFile(trainingTexts, true)

        println("CRF training...")
        String modelFile = config.crf.model.file as String
        String algorithm = config.crf.algorithm as String
        String graphicalModel = config.crf.graphicalModel as String
        Pair<String ,String>[] parameters = config.crf.properties.collect {
            new Pair<>(it.getKey(), it.getValue())
        }
        CrfTrainer.train(trainingFile.path, modelFile, algorithm, graphicalModel, "UTF-8", parameters)

        trainingFile.delete()
    }

    List tag(String text) {
        List segments = textSplitter.splitText(text)
        File fileToTag = prepareCrfSuiteFile([[segments: segments]], false)

        CrfTagger tagger = new CrfTagger(config.crf.model.file as String)
        List<List<Pair<String, Double>>> results = tagger.tag(fileToTag.path)

        for (def i = 0; i < results[0].size(); i++) {
            segments[i].tag = results[0][i].first
        }

        fileToTag.delete()
        return segments
    }

    List tag(List texts) {
        int count
        return texts.collect() {
            println("CRF - tagging ${++count} of ${texts.size()} texts")
            tag(it as String)
        }
    }

    private File prepareCrfSuiteFile(List texts, boolean attacheTag) {
        File crfTrainingFile = new File(tempDirectory.path + File.separator +  UUID.randomUUID().toString() + ".txt")
        crfTrainingFile.createNewFile()
        PrintWriter printWriter = new PrintWriter(crfTrainingFile)

        texts.each {
            List segments = it.segments
            for (def i = 0; i < segments.size(); i++) {
                String previous = i - 1 > 0 ? segments[i - 1].value : null
                String current = segments[i].value
                String next = i + 1 < segments.size() ? segments[i + 1].value : null

                String features = featureGenerator.generateFeatures(previous, current, next)

                if (attacheTag) {
                    String tag = segments[i].tag
                    printWriter.println("$tag\t$features")
                } else {
                    printWriter.println("$features")
                }
            }
        }
        printWriter.flush()
        printWriter.close()
        return crfTrainingFile
    }

}
