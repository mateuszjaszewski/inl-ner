package pl.pjatk.mj.processor

import pl.pjatk.mj.model.Tag

import static org.apache.commons.lang3.StringUtils.substringAfterLast
import static org.apache.commons.lang3.StringUtils.substringBetween
import static pl.pjatk.mj.Application.config
/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
class NkjpProcessor {

    private XmlSlurper xmlSlurper
    private List texts = []

    List trainingData
    List devData
    List testData

    NkjpProcessor() {
        xmlSlurper = new XmlSlurper()
    }

    void process() {
        File directory = new File(config.nkjp.path as String)

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Given path is not directory")
        }

        def counter = 0
        def dirsCount = directory.listFiles( { return it.isDirectory() } as FileFilter ).size()
        directory.eachDir {
            counter++
            println "Processing ${it.name} - $counter / $dirsCount"
            processDirectory(it)
        }
        println "Loaded ${texts.size()} texts"

        Collections.shuffle(texts)

        int trainingOffset = 0
        int devOffset = texts.size() * config.data.training.percent
        int testOffset = texts.size() * (config.data.training.percent + config.data.dev.percent)

        trainingData = texts[trainingOffset..<devOffset]
        devData = texts[devOffset..<testOffset]
        testData = texts[testOffset..<texts.size()]
    }


    private void processDirectory(File directory) {
        def textMap = loadText(directory.path)
        def namedMap = loadNamed(directory.path)
        loadSegmentation(textMap, namedMap, directory.path)
        texts.addAll(textMap.values())
    }

    private Map loadText(String path) {
        def root = xmlSlurper.parse(new File(path + File.separator + "text.xml" as String))
        Map texts = [:]
        root.TEI.text.body.div.each {
            it.ab.each {
                texts.put(it."@xml:id".text(), [
                        text: it.text(),
                        segments: []
                ])
            }
        }
        return texts
    }

    private void loadSegmentation(Map textMap, Map namedMap, String path) {
        def root = xmlSlurper.parse(new File(path + File.separator + "ann_segmentation.xml" as String))
        root.TEI.text.body.p.each {
            it.s.seg.each {
                def (textId, begin, length) = substringBetween(it.@corresp as String,"(", ")").split(",")
                def segmentKey = substringAfterLast(it."@xml:id" as String, "segm_")
                def textEntry = textMap.get(textId)
                textEntry.segments.add([
                    begin: begin as Integer,
                    length: length as Integer,
                    value: textEntry.text.substring(begin.toInteger(), begin.toInteger() + length.toInteger()),
                    symbol: namedMap.get(segmentKey) ?: Tag.OTHER
                ])
            }
        }
    }

    private Map loadNamed(String path) {
        def file = new File(path + File.separator + "ann_named.xml" as String)
        if (!file.exists()) {
            return [:]
        }
        def root = xmlSlurper.parse(file)
        Map named = [:]
        root.TEI.text.body.p.each {
            it.s.each {
                it.seg.each {
                    def f = it.fs.f.find { it.@name = "type" }
                    def tag = Tag.of(f.symbol.@value as String)
                    it.ptr.each {
                        def segmentKey = substringAfterLast(it.@target as String, "#morph_")
                        if (tag != "" && segmentKey != "") {
                            named.put(segmentKey, tag)
                        }
                    }
                }
            }
        }
        return named
    }

}
