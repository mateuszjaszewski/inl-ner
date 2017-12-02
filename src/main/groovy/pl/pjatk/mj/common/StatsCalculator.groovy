package pl.pjatk.mj.common
/**
 * Created by Mateusz Jaszewski on 29.11.2017.
 */
class StatsCalculator {

    def calculateStats(List results, List testData) {
        int[][] confusionMatrix = new int[Tag.values().size()][Tag.values().size()]

        for (int i = 0; i < results.size(); i++) {
            for (int j = 0; j < results[i].size(); j++) {
                def resultSegment = results[i][j]
                def testSegment = testData[i].segments.find { it.begin == resultSegment.begin }

                if (!testSegment) {
                    continue
                }

                Tag testTag = Tag.valueOf(testSegment.tag)
                Tag resultTag = Tag.valueOf(resultSegment.tag)
                confusionMatrix[testTag.ordinal()][resultTag.ordinal()]++
            }
        }

        return Tag.values().collect {
            tag -> [
                    tag: tag,
                    precision: precision(confusionMatrix, tag),
                    recall: recall(confusionMatrix, tag),
                    f1: f1(confusionMatrix, tag)
            ]
        }
    }

    private double precision(int[][] confusionMatrix, Tag tag) {
        int i = tag.ordinal()
        int sum = 0
        for (int j = 0; j < Tag.values().size(); j++) {
            sum += confusionMatrix[j][i]
        }
        return confusionMatrix[i][i] / sum
    }

    private double recall(int[][] confusionMatrix, Tag tag) {
        int i = tag.ordinal()
        int sum = 0
        for (int j = 0; j < Tag.values().size(); j++) {
            sum += confusionMatrix[i][j]
        }
        return confusionMatrix[i][i] / sum
    }

    private double f1(int[][] confusionMatrix, Tag tag) {
        double precision = precision(confusionMatrix, tag)
        double recall = recall(confusionMatrix, tag)
        return (2.0 * precision * recall) / (precision + recall)
    }

}
