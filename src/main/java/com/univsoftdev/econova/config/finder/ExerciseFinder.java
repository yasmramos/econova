package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Exercise;
import io.ebean.Finder;

public class ExerciseFinder extends Finder<Long, Exercise> {

    public ExerciseFinder() {
        super(Exercise.class);
    }
}
