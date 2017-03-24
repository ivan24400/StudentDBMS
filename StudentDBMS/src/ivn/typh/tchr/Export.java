package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ivn.typh.main.Engine;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Export {

	private static int row_index = 0;
	private static int col_index = 0;
	private static XSSFWorkbook book;

	static void export() {

		book = new XSSFWorkbook();
		XSSFSheet sheet = book.createSheet();

		Row row = sheet.createRow(row_index++);
		Cell cell = row.createCell(col_index++);

		// Name & Batch

		cell.setCellValue("Name");
		cell = row.createCell(col_index++);
		cell.setCellValue(Components.tsname.getText());
		cell = row.createCell(col_index++);
		cell.setCellValue("Batch");
		cell = row.createCell(col_index);
		cell.setCellValue(Components.tsbatch.getSelectionModel().getSelectedItem());

		col_index = 0;
		row = sheet.createRow(row_index++);

		// ID & Class

		cell = row.createCell(col_index++);
		cell.setCellValue("ID");
		cell = row.createCell(col_index++);
		cell.setCellValue(Integer.getInteger(Components.tsid.getText()));
		cell = row.createCell(col_index++);
		cell.setCellValue("Class");
		cell = row.createCell(col_index);
		cell.setCellValue(Components.tsclass.getSelectionModel().getSelectedItem());

		col_index = 0;
		row = sheet.createRow(row_index++);

		// Roll No & Department

		cell = row.createCell(col_index++);
		cell.setCellValue("Roll No");
		cell = row.createCell(col_index++);
		cell.setCellValue(Components.tsrno.getSelectionModel().getSelectedItem());
		cell = row.createCell(col_index++);
		cell.setCellValue("Department");
		cell = row.createCell(col_index);
		cell.setCellValue(Components.tsdprt.getSelectionModel().getSelectedItem());

		col_index = 0;
		row = sheet.createRow(row_index++);

		// Phone No & Parent Phone No

		cell = row.createCell(col_index++);
		cell.setCellValue("Phone No");
		cell = row.createCell(col_index++);
		cell.setCellValue(Integer.getInteger(Components.tsphone.getText()));
		cell = row.createCell(col_index++);
		cell.setCellValue("Parent Phone No");
		cell = row.createCell(col_index);
		cell.setCellValue(Integer.getInteger(Components.tpphone.getText()));

		col_index = 0;
		row = sheet.createRow(row_index++);

		// Email & Address

		cell = row.createCell(col_index++);
		cell.setCellValue("Email");
		cell = row.createCell(col_index++);
		cell.setCellValue(Components.tsmail.getText());
		cell = row.createCell(col_index++);
		cell.setCellValue("Address");
		cell = row.createCell(col_index);
		cell.setCellValue(Components.tsaddr.getText());

		// Semester Data

		col_index = 0;
		row_index += 2;

		int year_count = 0;

		switch (Components.tscyear) {
		case "BE":
			year_count = 4;
			break;
		case "TE":
			year_count = 3;
			break;
		case "SE":
			year_count = 2;
			break;
		case "FE":
			year_count = 1;
			break;
		}

		for (int i = 1; i <= year_count; i++) {

			String year = null;
			switch (i) {
			case 1:
				year = "fe";
				break;
			case 2:
				year = "se";
				break;
			case 3:
				year = "te";
				break;
			case 4:
				year = "be";
				break;
			}
			JSONArray jsona = null;

			try {
				String data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first()
						.toJson();
				jsona = new JSONObject(data).getJSONArray(year);
			} catch (JSONException e) {
			}
			Iterator<?> it = jsona.iterator();
			row = sheet.createRow(row_index++);
			cell = row.createCell(col_index++);
			cell.setCellValue("Semester");
			cell = row.createCell(col_index++);
			cell.setCellValue(sem);
			
			row = sheet.createRow(row_index +9);
			cell = row.createCell(col_index++);
			cell.setCellValue("Semester");
			cell = row.createCell(col_index++);
			cell.setCellValue(sem);
			
			while(it.hasNext()){
				JSONObject json = (JSONObject) it.next();
				String name = json.getString("name");
				int sem = json.getInt("sem");
				int ths = json.getInt("thScored");
				int tht = json.getInt("thTotal");
				int ors = json.getInt("orScored");
				int ort = json.getInt("orTotal");
				int prs = json.getInt("prScored");
				int prt = json.getInt("prTotal");
				int tws = json.getInt("twScored");
				int twt = json.getInt("twTotal");
				boolean back = json.getBoolean("back");
				

				
				
			}
		}

		writeData();
	}

	private static void writeData() {

		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Select Destination - Typh™");
		filechooser.setInitialDirectory(new File(System.getProperty("user.home")));
		filechooser.setInitialFileName(Components.tsid.getText() + "_" + Components.tsname.getText());
		filechooser.getExtensionFilters().add(new ExtensionFilter("Excel Workbook", "*.xlsx"));
		File path = filechooser.showSaveDialog(Components.stage);

		try {
			FileOutputStream file = new FileOutputStream(path);
			book.write(file);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
