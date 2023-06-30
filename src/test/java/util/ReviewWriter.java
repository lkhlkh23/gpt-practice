package util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReviewWriter {

    private static final String FILE_PATH = "/Users/a1101152/Desktop/gpt/";
    private static final ReviewWriter INSTANCE = new ReviewWriter();

    private ReviewWriter() {
    }

    public static ReviewWriter getInstance() {
        return INSTANCE;
    }

    public void saveReviews(final Map<Long, List<String>> reviews, final String split) throws IOException {
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet("reviews");
        final CellStyle wrapCellStyle = workbook.createCellStyle();
        wrapCellStyle.setWrapText(true);
        int index = 0;
        for (final Long productNo : reviews.keySet()) {
            final Row row = sheet.createRow(index++);
            row.createCell(0).setCellValue(productNo);

            final Cell reviewCell = row.createCell(1);
            reviewCell.setCellStyle(wrapCellStyle);
            reviewCell.setCellValue(String.join(split, reviews.get(productNo)));
        }

        workbook.write(new FileOutputStream(FILE_PATH + "summary-" + LocalDateTime.now() + ".xlsx"));
    }

}
