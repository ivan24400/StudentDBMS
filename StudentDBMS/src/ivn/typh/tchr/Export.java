package ivn.typh.tchr;

import static com.mongodb.client.model.Filters.eq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ivn.typh.main.Engine;
import ivn.typh.main.Notification;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Export {

	private static int row_index = 0;
	private static int col_index = 0;
	private static XSSFWorkbook book;
	private static File path;

	static void export() {

		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Select Destination - Typh™");
		filechooser.setInitialDirectory(new File(System.getProperty("user.home")));
		filechooser.setInitialFileName(Components.tsid.getText() + "_" + Components.tsname.getText());
		filechooser.getExtensionFilters().add(new ExtensionFilter("Excel Workbook", "*.xlsx"));
		path = filechooser.showSaveDialog(Components.stage);
		writeData();
	}

	private static void writeData() {
		book = new XSSFWorkbook();
		XSSFSheet sheet = book.createSheet();

		Row row = sheet.createRow(row_index++);
		Cell cell = row.createCell(0);

		// Name & Batch

		cell.setCellValue("Name");
		cell = row.createCell(2);
		cell.setCellValue(Components.tsname.getText());
		cell = row.createCell(4);
		cell.setCellValue("Batch");
		cell = row.createCell(6);
		cell.setCellValue(Components.tsbatch.getSelectionModel().getSelectedItem());

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1)); // Name
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3)); // vName
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5)); // Batch
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 6, 7)); // vBatch

		// ID & Class

		row = sheet.createRow(row_index++);
		cell = row.createCell(0);
		cell.setCellValue("ID");
		cell = row.createCell(2);
		cell.setCellValue(Components.tsid.getText());
		cell = row.createCell(4);
		cell.setCellValue("Class");
		cell = row.createCell(6);
		cell.setCellValue(Components.tsclass.getSelectionModel().getSelectedItem());

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 1)); // ID
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3)); // vID
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 4, 5)); // Class
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 6, 7)); // vClass

		// Roll No & Department

		row = sheet.createRow(row_index++);
		cell = row.createCell(0);
		cell.setCellValue("Roll No");
		cell = row.createCell(2);
		cell.setCellValue(Components.tsrno.getSelectionModel().getSelectedItem());
		cell = row.createCell(4);
		cell.setCellValue("Department");
		cell = row.createCell(6);
		cell.setCellValue(Components.tsdprt.getSelectionModel().getSelectedItem());

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1)); // Roll
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3)); // vRoll
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5)); // Department
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 7)); // vDepartment

		// Phone No & Parent Phone No

		row = sheet.createRow(row_index++);

		cell = row.createCell(0);
		cell.setCellValue("Student Contact");
		cell = row.createCell(2);
		cell.setCellValue(Components.tsphone.getText());
		cell = row.createCell(4);
		cell.setCellValue("Parent Contact");
		cell = row.createCell(6);
		cell.setCellValue(Components.tpphone.getText());

		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1)); // sphone
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3)); // vsphone
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 5)); // parent
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 6, 7)); // vparent

		// Email & Address

		row = sheet.createRow(row_index++);

		cell = row.createCell(0);
		cell.setCellValue("Email");
		cell = row.createCell(2);
		cell.setCellValue(Components.tsmail.getText());
		cell = row.createCell(4);
		cell.setCellValue("Address");
		cell = row.createCell(6);
		cell.setCellValue(Components.tsaddr.getText());

		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1)); // email
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 2, 3)); // vemail
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 5)); // address
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 6, 7)); // vaddress

		// Semester Data

		col_index = 0;
		row_index += 2;
		int sem_count = 0;

		switch (Components.tscsem) {
		case "SEM 1":
			sem_count = 1;
			break;
		case "SEM 2":
			sem_count = 2;
			break;
		case "SEM 3":
			sem_count = 3;
			break;
		case "SEM 4":
			sem_count = 4;
			break;
		case "SEM 5":
			sem_count = 5;
			break;
		case "SEM 6":
			sem_count = 6;
			break;
		case "SEM 7":
			sem_count = 7;
			break;
		case "SEM 8":
			sem_count = 8;
			break;
		}

		Map<Integer, List<Map<String, String>>> semData = new HashMap<Integer, List<Map<String, String>>>();
		JSONArray jsona = null;
		String data = null;
		try {
			data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first().toJson();
		} catch (JSONException e) {
		}

		for (int i = 1; i <= sem_count; i++) {
			List<Map<String,String>> sem_tl = new ArrayList<>();
			jsona = new JSONObject(data).getJSONArray(getYear(i));
			Iterator it = jsona.iterator();
			while (it.hasNext()) {
				JSONObject json = (JSONObject) it.next();
				int sem = json.getInt("sem");
				if (sem == i) {
					Map<String, String> sem_t = new HashMap<>();
					sem_t.put("thScored", Integer.toString(json.getInt("thScored")));
					sem_t.put("thTotal", Integer.toString(json.getInt("thTotal")));
					sem_t.put("orScored", Integer.toString(json.getInt("orScored")));
					sem_t.put("orTotal", Integer.toString(json.getInt("orTotal")));
					sem_t.put("prScored", Integer.toString(json.getInt("prScored")));
					sem_t.put("prTotal", Integer.toString(json.getInt("prTotal")));
					sem_t.put("twScored", Integer.toString(json.getInt("twScored")));
					sem_t.put("twTotal", Integer.toString(json.getInt("attended")));
					sem_t.put("attended", Integer.toString(json.getInt("twScored")));
					sem_t.put("attendedTotal", Integer.toString(json.getInt("attendedTotal")));
					sem_t.put("name", json.getString("name"));
					sem_t.put("back", Boolean.toString(json.getBoolean("back")));
					sem_t.put("sem", Integer.toString(sem));
					
					sem_tl.add(sem_t);
				}
			}
				semData.put(i, sem_tl);
		}

		for (int i = 1; i <= sem_count; i++) {

			// Semester title
			col_index = 0;

			row = sheet.createRow(row_index++);
			cell = row.createCell(col_index++);
			cell.setCellValue("Semester");
			cell = row.createCell(++col_index); // To skip column
			cell.setCellValue(i);
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 2, col_index - 1));

			// Table data
			col_index=0;
			row = sheet.createRow(row_index++);
			cell = row.createCell(col_index++);
			cell.setCellValue("Subject");
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index, col_index - 1, col_index));

			cell = row.createCell(++col_index);
			col_index++;
			cell.setCellValue("Theory");
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

			cell = row.createCell(++col_index);
			col_index++;
			cell.setCellValue("Oral");
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

			cell = row.createCell(++col_index);
			col_index++;
			cell.setCellValue("Practical");
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

			cell = row.createCell(++col_index);
			col_index++;
			cell.setCellValue("TermWork");
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

			cell = row.createCell(++col_index);
			col_index++;
			cell.setCellValue("Attendance");
			sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

			// Second Header of table

			col_index = 2;
			row = sheet.createRow(row_index++);
			for (int j = 0; j < 5; j++) {
				cell = row.createCell(col_index++);
				cell.setCellValue("Scored");
				cell = row.createCell(col_index++);
				cell.setCellValue("Total");
			}
			
			List<Map<String,String>> sem_t = semData.get(i);
			col_index=0;
			for(int k=0;k<sem_t.size();k++){
				col_index=0;
				row = sheet.createRow(row_index++);
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("name"));
				col_index++;
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("thScored"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("thTotal"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("orScored"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("orTotal"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("prScored"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("prTotal"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("twScored"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("twTotal"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("attended"));
				cell = row.createCell(col_index++);
				cell.setCellValue(sem_t.get(k).get("attendedTotal"));
			}
			
			row_index++;
		}

		try {
			FileOutputStream file = new FileOutputStream(path);
			book.write(file);
			Notification.message(Components.stage, "File Exported  !");
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getYear(int sem) {
		String year = null;

		switch (sem) {
		case 1:
		case 2:
			year = "fe";
			break;
		case 3:
		case 4:
			year = "se";
			break;
		case 5:
		case 6:
			year = "te";
			break;
		case 7:
		case 8:
			year = "be";
			break;
		}
		return year;
	}

}
