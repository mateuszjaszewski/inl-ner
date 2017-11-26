package pl.pjatk.mj.crf

/**
 * Created by Mateusz Jaszewski on 26.11.2017.
 */
class FeatureGenerator {

    String generateFeatures(String previous, String current, String next) {
        List features = []

        if (previous) {
            features.addAll(featuresForWord(previous, -1))
        }

        features.addAll(featuresForWord(current, 0))

        if (next) {
            features.addAll(featuresForWord(next, 1))
        }

        return features.join("\t")
    }

    private static List featuresForWord(String word, Integer index) {
        return [
                lengthFeature(word, index),
                startsWithUppercaseFeature(word, index),
                allLettersUppercaseFeature(word, index),
                isEndOfSentenceFeature(word, index),
                firstLetterFeature(word, index),
                lastLetterFeature(word, index)
        ]
    }

    private static String lengthFeature(String word, Integer index) {
        return "LEN[$index]=${word.length()}"
    }

    private static String startsWithUppercaseFeature(String word, Integer index) {
        return "SWU[$index]=${word.substring(0, 1).matches("[A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń")}"
    }

    private static String allLettersUppercaseFeature(String word, Integer index) {
        return "ALU[$index]=${word.matches("([A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń)+")}"
    }

    private static String isEndOfSentenceFeature(String word, Integer index) {
        return "EOS[$index]=${word == "." || word == "?" || word == "!"}"
    }

    private static String firstLetterFeature(String word, Integer index) {
        return "FL[$index]=${word[0]}"
    }

    private static String lastLetterFeature(String word, Integer index) {
        return "LL[$index]=${word[word.length() - 1]}"
    }
}
