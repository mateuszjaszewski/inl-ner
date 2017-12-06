package pl.pjatk.mj.linear2

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import pl.pjatk.mj.common.Tag

import static groovyx.net.http.Method.POST
/**
 * Created by Mateusz Jaszewski on 02.12.2017.
 */
class Linear2ApiClient {

    private static final API_URL = "http://ws.clarin-pl.eu"
    private static final PATH = "/nlprest2/base/process"
    private static final LPMN = "any2txt|wcrft2({\"morfeusz2\":true})|liner2({\"model\":\"top9\"})"

    private static Map TAG_MAP = [
            "nam_adj" : Tag.OTHER,
            "nam_eve" : Tag.OTHER,
            "nam_fac" : Tag.OTHER,
            "nam_liv" : Tag.PERSON,
            "nam_loc" : Tag.PLACE,
            "nam_num" : Tag.OTHER,
            "nam_org" : Tag.ORGANIZATION,
            "nam_oth" : Tag.OTHER,
            "nam_pro" : Tag.OTHER
    ]

    private HTTPBuilder http
    private XmlSlurper xmlSlurepr

    Linear2ApiClient() {
        http = new HTTPBuilder(API_URL)
        xmlSlurepr = new XmlSlurper()
        xmlSlurepr.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        xmlSlurepr.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }

    List tag(List texts) {
        int count = 0
        return texts.collect {
            println("Linear2 - tagging ${++count} of ${texts.size()} texts")
            tag(it as String)
        }
    }

    List tag(String text) {
        List segments = []
        http.request(POST) {
            uri.path = PATH
            contentType = ContentType.BINARY
            requestContentType = ContentType.JSON
            body = [text: text, lpmn: LPMN, user: ""]

            response.success = { resp, data ->
                segments = parseResponse(text, data)
            }

            response.failure = { resp ->
                println "Request failed with status ${resp.status}"
            }
        }
        return segments
    }

    private List parseResponse(String text, InputStream data) {
        List segments = []
        def xml = xmlSlurepr.parse(data as InputStream)
        int lastIndex = 0
        xml.chunk.each {
            it.sentence.each {
                it.tok.each {
                    String value = it.orth.text()
                    def ann = it.ann.find { it.text() != "0" }
                    int begin = text.indexOf(value, lastIndex)
                    lastIndex = begin + value.length()
                    segments.add([
                            value: value,
                            begin: begin,
                            length: value.length(),
                            tag: (TAG_MAP[ann.@chan as String] ?: Tag.OTHER).name()
                    ])
                }
            }
        }
        return segments
    }

}
