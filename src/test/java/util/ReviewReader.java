package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewReader {

    private static final String FILE_PATH = "/Users/a1101152/Desktop/gpt/";
    private static final ReviewReader INSTANCE = new ReviewReader();

    private ReviewReader() {
    }

    public static ReviewReader getInstance() {
        return INSTANCE;
    }

    public Map<Long, List<String>> readReviews(final String file) throws FileNotFoundException {
        final Map<Long, List<String>> reviews = new HashMap<>();
        final BufferedReader br = new BufferedReader(new FileReader(FILE_PATH + file));
        br.lines()
          .forEach(s -> {
              final String[] line = s.split("\t");
              if(reviews.containsKey(Long.parseLong(line[0]))) {
                  reviews.get(Long.parseLong(line[0])).add(line[2]);
              } else {
                  reviews.put(Long.parseLong(line[0]), new ArrayList<>(List.of(line[2])));
              }
          });

        return reviews;
    }

}
