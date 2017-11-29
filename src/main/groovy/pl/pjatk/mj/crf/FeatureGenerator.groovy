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
                endsWithUppercaseFeature(word, index),
                allLettersUppercaseFeature(word, index),
                allLettersLowercaseFeature(word, index),
                isEndOfSentenceFeature(word, index),
                containsMoreThanOneUppercaseLetterFeature(word, index),
                lastLetterFeature(word, index),
                last2LettersFeature(word, index),
                last3LettersFeature(word, index),
                containsDigitFeature(word, index)
        ].findAll {it != null}
    }

    private static String lengthFeature(String word, Integer index) {
        return "length[$index]=${word.length()}"
    }

    private static String startsWithUppercaseFeature(String word, Integer index) {
        return "startsWithUpper[$index]=${word[0..0].matches("[A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń")}"
    }

    private static String endsWithUppercaseFeature(String word, Integer index) {
        return "endsWithUpper[$index]=${word[-1..-1].matches("[A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń")}"
    }

    private static String allLettersUppercaseFeature(String word, Integer index) {
        return "allLettersUpper[$index]=${word.matches("([A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń)+")}"
    }

    private static String containsMoreThanOneUppercaseLetterFeature(String word, Integer index) {
        return "moreThanOneLetterIsUpper[$index]=${word.matches("([A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń)+.*([A-Z]|Ą|Ę|Ć|Ś|Ź|Ż|Ł|Ó|Ń)+")}"
    }

    private static String allLettersLowercaseFeature(String word, Integer index) {
        return "allLettersLowercase[$index]=${word.matches("([a-z]|ą|ę|ć|ś|ź|ż|ł|ó|ń)+")}"
    }

    private static String isEndOfSentenceFeature(String word, Integer index) {
        return "endOfSentence[$index]=${word == "." || word == "?" || word == "!"}"
    }

    private static String lastLetterFeature(String word, Integer index) {
        return "lastLetter[$index]=${word[-1..-1]}"
    }

    private static String last2LettersFeature(String word, Integer index) {
        if (word.size() >= 2) {
            return "last2Letters[$index]=${word[-2..-1]}"
        }
    }

    private static String last3LettersFeature(String word, Integer index) {
        if (word.size() >= 3) {
            return "last3Letters[$index]=${word[-3..-1]}"
        }
    }

    private static String containsDigitFeature(String word, Integer index) {
        return "containsDigit[$index]=${word.matches(".*[0-9].*")}"
    }

}
