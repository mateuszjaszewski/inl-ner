package pl.pjatk.mj.common

/**
 * Created by Mateusz Jaszewski on 18.11.2017.
 */
enum Tag {
    OTHER, PERSON, ORGANIZATION, PLACE

    static Tag of(String string) {
        switch (string) {
            case "orgName": return ORGANIZATION
            case "persName": return PERSON
            case "placeName": return PLACE
            case "geogName": return PLACE
            default: return OTHER
        }
    }

}