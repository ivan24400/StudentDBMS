package ivn.typh.admin;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * This class is Search Box where the user enters name in this box
 * to look a brief information about of the student.
 */

public class Search extends TextField {
	private final SortedSet<String> list;
	private ContextMenu resultList;
	private String result;	
	
	public Search() {
		super();
		
		list = new TreeSet<>();
		resultList = new ContextMenu();
		textProperty().addListener((obs,o,n)-> {
				if (getText().length() == 0) {
					resultList.hide();
				} else {
					List<String> searchResult = new LinkedList<>();
					searchResult.addAll(list.subSet(getText(), getText() + Character.MAX_VALUE));
					if (list.size() > 0) {
						populatePopup(searchResult,10);
						displayPopUp();
					} else {
						resultList.hide();
					}
				}
		});

		focusedProperty().addListener((obs,o,n)-> {
				resultList.hide();
		});

	}

	void displayPopUp() {
		if (!resultList.isShowing()) {
			resultList.show(Search.this, Side.BOTTOM, 0, 0);
		}
	}

	/*
	 * This method gives a popup window if a user selects an entry from the result.
	 */
	private void firePopUp(String nameOfStudent) {
		Components.studGrid.getChildren().forEach(arg->{
			Button tmp = (Button)arg;
			if(tmp.getText().equals(nameOfStudent))
				tmp.fire();
		});
	}


	/*
	 * This method changes the contents of list with provided one.
	 * @param items a List of students
	 */
	public void setItems(List<String> items){
		list.addAll(items);
	}
	
	/*
	 * This method creates the search result list.
	 * @param searchResult the list of students matching the search query
	 */
	void populatePopup(List<String> searchResult,int maxEntries) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		int count = Math.min(searchResult.size(), maxEntries);
		for (int i = 0; i < count; i++) {
			result = searchResult.get(i);
			Label entry = new Label(result);
			entry.setPrefWidth(this.getWidth());
			
			CustomMenuItem item = new CustomMenuItem(entry, true);
			item.setOnAction(action->{
					resultList.hide();
					setText(entry.getText());
					firePopUp(entry.getText());
				});
			menuItems.add(item);
		}
		resultList.getItems().clear();
		resultList.getItems().addAll(menuItems);

	}

}