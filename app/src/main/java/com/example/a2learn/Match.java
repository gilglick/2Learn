package com.example.a2learn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
    private Map<String,Boolean> optionalMatches;

    public Match() {
        this.optionalMatches = new HashMap<>();

    }

    public Map<String,Boolean> getOptionalMatches() {
        return optionalMatches;
    }

    public void setOptionalMatches(Map<String,Boolean> optionalMatches) {
        this.optionalMatches = optionalMatches;
    }
}
