package com.example.a2learn;

import java.util.ArrayList;
import java.util.List;

public class Match {
    private List<String> optionalMatches;

    public Match() {
        this.optionalMatches = new ArrayList<>();

    }

    public List<String> getOptionalMatches() {
        return optionalMatches;
    }

    public void setOptionalMatches(List<String> optionalMatches) {
        this.optionalMatches = optionalMatches;
    }
}
