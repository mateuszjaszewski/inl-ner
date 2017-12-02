package pl.pjatk.mj.common

import groovy.json.JsonBuilder
/**
 * Created by Mateusz Jaszewski on 02.12.2017.
 */
class Utils {

    static void saveJsonToFile(String path, def data) {
        File file = createFile(path)
        FileWriter fileWriter = new FileWriter(file)
        fileWriter.write(new JsonBuilder(data).toPrettyString())
        fileWriter.flush()
        fileWriter.close()
    }

    static void saveResultsToFile(String path, List results) {
        File file = createFile(path)
        FileWriter fileWriter = new FileWriter(file)
        results.each {
            it.each {
                fileWriter.write("${it.value} \t ${it.tag} \r\n")
            }
            fileWriter.write("\r\n")
        }
        fileWriter.flush()
        fileWriter.close()
    }

    private static File createFile(String path) {
        File file = new File(path)
        if (file.getParentFile()) {
            file.getParentFile().mkdirs()
        }
        file.createNewFile()
        return file
    }
}
