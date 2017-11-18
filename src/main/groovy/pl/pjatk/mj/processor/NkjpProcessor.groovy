package pl.pjatk.mj.processor

import pl.pjatk.mj.Application

import static org.apache.commons.lang3.StringUtils.substringAfter
import static org.apache.commons.lang3.StringUtils.substringBetween
/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
class NkjpProcessor {

    private XmlSlurper xmlSlurper



    NkjpProcessor() {
        xmlSlurper = new XmlSlurper()
    }

    void process() {
        File direcotry = new File(Application.config.nkjp.path as String)

        if (!direcotry.isDirectory()) {
            throw new IllegalArgumentException("Given path is not directory")
        }

        direcotry.eachDir {
            processDirectory(it)
        }
    }

    private void processDirectory(File directory) {
        def texts = loadText(directory.path)
        def segments = loadSegmentation(directory.path)
        def named = loadNamed(directory.path)
        //def morphosyntaxs = loadMorphosyntax(directory.path)
        println named.size()
    }

    private Map loadText(String path) {
        def root = xmlSlurper.parse(new File(path + File.separator + "text.xml" as String))
        Map texts = [:]

        root.TEI.text.body.div.each {
            it.ab.each {
                texts.put(it."@xml:id", it.text())
            }
        }

        return texts
    }

    private Map loadSegmentation(String path) {
        def root = xmlSlurper.parse(new File(path + File.separator + "ann_segmentation.xml" as String))
        Map segments = [:]
        root.TEI.text.body.p.each {
            it.s.seg.each {
                def (textId, begin, end) = substringBetween(it.@corresp as String,"(", ")").split(",")
                segments.put(it."@xml:id", [
                    textId: textId,
                    begin: begin as Integer,
                    end: end as Integer
                ])
            }
        }
        return segments
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
                def segmentId = "segm" + substringAfter(it.@corresp as String, "#")
                def f = it.seg.fs.f.find { it.@name = "type" }
                def tag = f.symbol.@value
                if (tag != "") {
                    named.put(segmentId, tag)
                }
            }
        }
        return named
    }

}
