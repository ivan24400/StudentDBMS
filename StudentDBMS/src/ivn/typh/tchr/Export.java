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

import org.apache.poi.POIXMLProperties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ivn.typh.main.Engine;
import ivn.typh.main.Loading;
import ivn.typh.main.Notification;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Export {

	private static int row_index = 0;
	private static int col_index = 0;
	private static XSSFWorkbook book;
	private static File path;
	private static String institute;

	static void export() {

		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Select Destination - Typh™");
		filechooser.setInitialDirectory(new File(System.getProperty("user.home")));
		filechooser.setInitialFileName(Components.tsid.getText() + "_" + Components.tsname.getText());
		filechooser.getExtensionFilters().add(new ExtensionFilter("Excel Workbook", "*.xlsx"));
		path = filechooser.showSaveDialog(Components.stage);

		Loading load = new Loading(Components.stage);

		Task<Boolean> task = createWriteTask();
		load.startTask(task);
		(new Thread(task)).start();
		task.setOnSucceeded(arg -> {
			load.stopTask();
			if (task.getValue())
				Notification.message(Components.stage, "File Exported  to :- \n "+path.getAbsolutePath());
			else
				Notification.message(Components.stage, AlertType.ERROR, "Error - Typh™",
						"Error while writing data to file !\nClose other programs using it");

		});

	}

	private static Task<Boolean> createWriteTask() {
		Task<Boolean> task = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				book = new XSSFWorkbook();
				institute = Engine.db.getCollection("Users").find(eq("user", "admin")).first()
						.getString("instituteName");
				POIXMLProperties property = book.getProperties();
				POIXMLProperties.CoreProperties cproperty = property.getCoreProperties();
				cproperty.setCreator(Components.pname.getText() + " - " + institute);
				cproperty.setTitle(Components.tsname.getText());
				cproperty.setCategory("Academic");

				XSSFSheet sheet = book.createSheet();
				XSSFFont cell_font = book.createFont();
				book.setSheetName(0, Components.tsname.getText() + "\'s Data");

				cell_font.setBold(true);
	
				CellStyle cell_style = book.createCellStyle();
				CellStyle inst_style = book.createCellStyle();
				inst_style.setFont(cell_font);
				inst_style.setAlignment(HorizontalAlignment.CENTER);

				Row row = sheet.createRow(row_index++);
				Cell cell = row.createCell(col_index);
				cell.setCellValue(institute);
				cell.setCellStyle(inst_style);
				sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
				row_index++;
				
				// Name & Batch
				
				cell_style.setFont(cell_font);
				row = sheet.createRow(row_index++);
				cell = row.createCell(col_index);
				cell.setCellValue("Name");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsname.getText());
				cell = row.createCell(col_index+=2);
				cell.setCellValue("Batch");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsbatch.getSelectionModel().getSelectedItem());

				sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 1)); // Name
				sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3)); // vName
				sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5)); // Batch
				sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 7)); // vBatch

				// ID & Class

				col_index=0;
				row = sheet.createRow(row_index++);
				cell = row.createCell(col_index);
				cell.setCellValue("ID");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsid.getText());
				cell = row.createCell(col_index+=2);
				cell.setCellValue("Class");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsclass.getSelectionModel().getSelectedItem());

				sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1)); // ID
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3)); // vID
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 5)); // Class
				sheet.addMergedRegion(new CellRangeAddress(3, 3, 6, 7)); // vClass

				// Roll No & Department

				col_index=0;
				row = sheet.createRow(row_index++);
				cell = row.createCell(col_index);
				cell.setCellValue("Roll No");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsrno.getSelectionModel().getSelectedItem());
				cell = row.createCell(col_index+=2);
				cell.setCellValue("Department");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsdprt.getSelectionModel().getSelectedItem());

				sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 1)); // Roll
				sheet.addMergedRegion(new CellRangeAddress(4, 4, 2, 3)); // vRoll
				sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 5)); // Department
				sheet.addMergedRegion(new CellRangeAddress(4, 4, 6, 7)); // vDepartment

				// Phone No & Parent Phone No

				col_index=0;
				row = sheet.createRow(row_index++);

				cell = row.createCell(col_index);
				cell.setCellValue("Student Contact");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsphone.getText());
				cell = row.createCell(col_index+=2);
				cell.setCellValue("Parent Contact");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tpphone.getText());

				sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 1)); // sphone
				sheet.addMergedRegion(new CellRangeAddress(5, 5, 2, 3)); // vsphone
				sheet.addMergedRegion(new CellRangeAddress(5, 5, 4, 5)); // parent
				sheet.addMergedRegion(new CellRangeAddress(5, 5, 6, 7)); // vparent

				// Email & Address

				col_index=0;
				row = sheet.createRow(row_index++);

				cell = row.createCell(col_index);
				cell.setCellValue("Email");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsmail.getText());
				cell = row.createCell(col_index+=2);
				cell.setCellValue("Address");
				cell.setCellStyle(cell_style);
				cell = row.createCell(col_index+=2);
				cell.setCellValue(Components.tsaddr.getText());

				sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 1)); // email
				sheet.addMergedRegion(new CellRangeAddress(6, 6, 2, 3)); // vemail
				sheet.addMergedRegion(new CellRangeAddress(6, 6, 4, 5)); // address
				sheet.addMergedRegion(new CellRangeAddress(6, 6, 6, 7)); // vaddress

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
					data = Engine.db.getCollection("Students").find(eq("sid", Components.tsid.getText())).first()
							.toJson();
				} catch (JSONException e) {	}

				for (int i = 1; i <= sem_count; i++) {
					List<Map<String, String>> sem_tl = new ArrayList<>();
					jsona = new JSONObject(data).getJSONArray(getYear(i));
					
					Iterator<?> it = jsona.iterator();
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

				int total_counter = 13;
				for (int i = 1; i <= sem_count; i++) {

					// Semester title

					col_index = 0;
					XSSFFont sem_font = book.createFont();
					CellStyle sem_style = book.createCellStyle();
					sem_font.setFontHeight(20);
					sem_style.setAlignment(HorizontalAlignment.LEFT);
					sem_style.setFont(sem_font);
					row = sheet.createRow(row_index++);
					cell = row.createCell(col_index++);
					cell.setCellValue("Semester");
					cell.setCellStyle(sem_style);
					cell = row.createCell(++col_index); 
					cell.setCellValue(i);
					cell.setCellStyle(sem_style);
					sheet.addMergedRegion(
							new CellRangeAddress(row_index - 1, row_index - 1, col_index - 2, col_index - 1));

					// 			Table data
					
					col_index = 0;
					CellStyle cells = book.createCellStyle();
					cells.setVerticalAlignment(VerticalAlignment.CENTER);
					cells.setAlignment(HorizontalAlignment.CENTER);
					cells.setFont(cell_font);
					
					row = sheet.createRow(row_index++);
					cell = row.createCell(col_index++);
					cell.setCellValue("Subject");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index, col_index - 1, col_index));

					cell = row.createCell(++col_index);
					col_index++;
					cell.setCellValue("Theory");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

					cell = row.createCell(++col_index);
					col_index++;
					cell.setCellValue("Oral");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

					cell = row.createCell(++col_index);
					col_index++;
					cell.setCellValue("Practical");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

					cell = row.createCell(++col_index);
					col_index++;
					cell.setCellValue("TermWork");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

					cell = row.createCell(++col_index);
					col_index++;
					cell.setCellValue("TotalMarks");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

					cell = row.createCell(++col_index);
					col_index++;
					cell.setCellValue("Attendance");
					cell.setCellStyle(cells);
					sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, col_index - 1, col_index));

					// Second Header of table

					col_index = 2;
					row = sheet.createRow(row_index++);
					for (int j = 0; j < 6; j++) {
						cell = row.createCell(col_index++);
						cell.setCellValue("Scored");
						cell = row.createCell(col_index++);
						cell.setCellValue("Total");
					}

					List<Map<String, String>> sem_t = semData.get(i);
					for (int k = 0; k < sem_t.size(); k++) {
						col_index = 0;
						row = sheet.createRow(row_index++);
						cell = row.createCell(col_index++);
						cell.setCellValue(sem_t.get(k).get("name"));
						col_index++;
						sheet.addMergedRegion(new CellRangeAddress(row_index - 1, row_index - 1, 0, 1));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("thScored")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("thTotal")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("orScored")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("orTotal")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("prScored")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("prTotal")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("twScored")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("twTotal")));

						cell = row.createCell(col_index++);
						cell.setCellFormula("SUM(C" + total_counter + ",E" + total_counter + ",G" + total_counter + ",I"
								+ total_counter + ")");
						cell = row.createCell(col_index++);
						cell.setCellFormula("SUM(D" + total_counter + ",F" + total_counter + ",H" + total_counter + ",J"
								+ total_counter + ")");

						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("attended")));
						cell = row.createCell(col_index++);
						cell.setCellValue(new Integer(sem_t.get(k).get("attendedTotal")));

						total_counter++;
					}

					row = sheet.createRow(row_index++);
					cell = row.createCell(10);
					cell.setCellStyle(cell_style);
					cell.setCellFormula("SUM(K" + (total_counter - sem_t.size()) + ":K" + (total_counter - 1) + ")");
					cell = row.createCell(11);
					cell.setCellStyle(cell_style);
					cell.setCellFormula("SUM(L" + (total_counter - sem_t.size()) + ":L" + (total_counter - 1) + ")");
					cell = row.createCell(12);
					cell.setCellStyle(cell_style);
					cell.setCellFormula("SUM(M" + (total_counter - sem_t.size()) + ":M" + (total_counter - 1) + ")");
					cell = row.createCell(13);
					cell.setCellStyle(cell_style);
					cell.setCellFormula("SUM(N" + (total_counter - sem_t.size()) + ":N" + (total_counter - 1) + ")");

					total_counter += 4;
				}

				try {
					FileOutputStream file = new FileOutputStream(path);
					book.write(file);
					file.close();
				} catch (IOException e) {
					return false;
				}

				return true;
			}
		};
		return task;

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
