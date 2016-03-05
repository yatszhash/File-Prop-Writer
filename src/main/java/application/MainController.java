package application;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import data.FileInfo;
import data.FileInfoWritingOption;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

public class MainController
{
	@FXML
	Button selectFolderButton;
	@FXML
	TextField folderPathField;

	@FXML
	Button selectSaveFileButton;

	//TODO checkBoxのコレクション化
	Map<String, CheckBox>  infoTypeChecks;
	@FXML
	CheckBox fileTypeCheck;
	@FXML
	CheckBox abstractPathCheck;
	@FXML
	CheckBox fileVersionCheck;
	@FXML
	CheckBox createdDateCheck;
	@FXML
	CheckBox updatedDateCheck;
	@FXML
	CheckBox authorCheck;

	@FXML
	TreeTableView<FileInfo> fileInfoTable;
	TreeItem<FileInfo> root;

	@FXML
	private TableColumn<FileInfo, String> name;
	@FXML
	private TableColumn<FileInfo, String> type;
	@FXML
	private TableColumn<FileInfo, String> abstractPath;

	private File selectedFolder;
	private FileInfo rootFolderInfo;
	private FileInfoWritingOption writingOption;

	@FXML
	protected void initialize()
	{
		infoTypeChecks = new HashMap<String, CheckBox>();
		writingOption = new FileInfoWritingOption();
		infoTypeChecks.put("type", fileTypeCheck);
		infoTypeChecks.put("abstractPath", abstractPathCheck);
		selectSaveFileButton.setVisible(false);
	}

	@FXML
	protected void selectFolder(ActionEvent event)
	{
		//TODO iteratorと順番が違うので再考が必要
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(selectedFolder);
		chooser.setTitle("フォルダを選択");

		selectedFolder = chooser.showDialog(null);

		if (selectedFolder != null)
		{
			String filePath = selectedFolder.getPath().toString();
			folderPathField.setText(filePath);
			rootFolderInfo = new FileInfo(filePath);

			root = new TreeItem<>(rootFolderInfo);
			fileInfoTable.setRoot(root);
			root.setExpanded(true);

			setToTree(root);
			linkColumns();

			selectSaveFileButton.setVisible(true);
		}

		if (fileTypeCheck.isSelected())
		{}
	}

	//TODO bind使う
	private void linkColumns() {
		TreeTableColumn<FileInfo, String> typeCol = new TreeTableColumn<>("type");
		TreeTableColumn<FileInfo, String> nameCol = new TreeTableColumn<>("name");

		//TODO プロパティの名前からメソッド化
		typeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FileInfo,String>, ObservableValue<String>>()
		{
			//TODO lambda化
			@Override
			public ObservableValue<String> call(CellDataFeatures<FileInfo, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getType());
			}
		});

		nameCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FileInfo,String>, ObservableValue<String>>()
		{
			@Override
			public ObservableValue<String> call(CellDataFeatures<FileInfo, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
			}
		});

		fileInfoTable.getColumns().setAll(nameCol, typeCol);
	}


	/**
	 * TreeItemをツリーにsetする。
	 **/
	private void setToTree(TreeItem<FileInfo> treeItem)
	{
		List<TreeItem<FileInfo>> children = treeItem.getValue().getChildrenInfos().stream().map(TreeItem<FileInfo>::new).collect(Collectors.toList());
		treeItem.getChildren().addAll(children);
		children.stream().filter(t -> t != null).forEach(this::setToTree); //TODO nullcheckの置き換え（いらない？）
		return;
	}

	/**
	 *
	 */
	@FXML
	protected void selectSaveFile(ActionEvent event)
	{
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("ファイルを保存");
		//TODO Fileの種類をEnumかリスト化
		fileChooser.getExtensionFilters().addAll(
										new ExtensionFilter("text file", "*.txt"),
										new ExtensionFilter("csv file", "*.csv"),
										new ExtensionFilter("tsv file", "*.tsv"),
										new ExtensionFilter("xls file", "*.xls"),
										new ExtensionFilter("xlsx file", "*.xlsx"),
										new ExtensionFilter("user defined file", "*.*"));
		File outputFile = fileChooser.showSaveDialog(null);

		if (outputFile == null) {return;}
		try
		{
			rootFolderInfo.writeProperties(outputFile);
			//TODO 書き込み終了ダイアログ
			System.out.println("書き込み終了");
		} catch (IOException e)
		{
			//TODO 独自RunTime例外に置き換え
			throw new RuntimeException();
		}
		return;
	}

	private class CheckToColumn
	{

	}

}
