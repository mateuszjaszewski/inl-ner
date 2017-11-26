nkjp {
    directory = "nkjp"
}
data {
    training {
        file = "data/training.json"
        percent = 0.8
    }
    dev {
        file = "data/dev.json"
        percent = 0.1
    }
    test {
        file = "data/test.json"
        percent = 0.1
    }
}
crf {
    model {
        file = "crf/crf.model"
    }
    temp {
        directory = "crf/temp"
    }
}
