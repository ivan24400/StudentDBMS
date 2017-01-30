package ivn.typh.admin;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;



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
					LinkedList<String> searchResult = new LinkedList<>();
					searchResult.addAll(list.subSet(getText(), getText() + Character.MAX_VALUE));
					if (list.size() > 0) {
						populatePopup(searchResult);
						if (!resultList.isShowing()) {
							resultList.show(Search.this, Side.BOTTOM, 0, 0);
						}
					} else {
						resultList.hide();
					}
				}
		});

		focusedProperty().addListener((obs,o,n)-> {
				resultList.hide();
		});

	}

	private void loadData() {
//		AdminUI.studGrid.getChildren().forEach(arg->{
//			Students tmp = (Students)arg;
//			if(tmp.getStudentName().equals(result))
//				tmp.initWindow();
//		});
	}

	public SortedSet<String> getEntries() {
		return list;
	}

	public void setItems(List<String> items){
		list.addAll(items);
	}
	private void populatePopup(List<String> searchResult) {
		List<CustomMenuItem> menuItems = new LinkedList<>();
		int maxEntries = 10;
		int count = Math.min(searchResult.size(), maxEntries);
		for (int i = 0; i < count; i++) {
			result = searchResult.get(i);
			Label entryLabel = new Label(result);
			CustomMenuItem item = new CustomMenuItem(entryLabel, true);
			item.setOnAction(action->{
					resultList.hide();
					setText(result);
					loadData();
				});
			menuItems.add(item);
		}
		resultList.getItems().clear();
		resultList.getItems().addAll(menuItems);

	}

}