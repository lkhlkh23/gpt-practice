package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    public Map<Long, List<String>> readTextReviews(final String file) throws FileNotFoundException {
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

    public Map<Long, List<String>> readExcelReviews(final String file, final int start, final int limitOfProducts, final int limitOfReviews, final String split) {
        final Map<Long, List<String>> allReviews = new HashMap<>();
        try (final Workbook workbook = new XSSFWorkbook(new FileInputStream(FILE_PATH + file))) {
            final Sheet sheet = workbook.getSheetAt(0);
            for (int i = start; i < limitOfProducts; i++) {
                final List<String> productReviews = new ArrayList<>();
                for (int j = 1; j <= limitOfReviews; j++) {
                    final Cell cell = sheet.getRow(i).getCell(j);
                    if(cell == null) {
                        break;
                    }
                    productReviews.add(cell.getStringCellValue().split(split)[1]);
                }
                allReviews.put((long) sheet.getRow(i).getCell(0).getNumericCellValue(), productReviews);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allReviews;
    }

}
