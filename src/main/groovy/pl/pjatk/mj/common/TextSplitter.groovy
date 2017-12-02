package pl.pjatk.mj.common

/**
 * Created by Mateusz Jaszewski on 27.11.2017.
 */
class TextSplitter {

    private final String REGEX = ~/\s+|(?=\p{Punct})|(?<=\p{Punct})/

    List splitText(String text) {
        List segments = []

        Integer lastIndex = 0
        text.split(REGEX).each {
            if (it && lastIndex < text.size()) {
                lastIndex = text.indexOf(it, lastIndex)
                segments.add([
                        begin: lastIndex,
                        length: it.length(),
                        value: it
                ])
            }
        }

        return segments
    }
}
