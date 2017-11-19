package pl.pjatk.mj.model

/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
enum Tag {
    OTHER, PERSON, ORGANIZATION

    static Tag of(String string) {
        switch (string) {
            case "orgName": return ORGANIZATION
            case "persName": return PERSON
            default: return OTHER
        }
    }

}