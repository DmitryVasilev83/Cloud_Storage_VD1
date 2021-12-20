package com.geekbrains.client;

import com.geekbrains.core.model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.io.File;
import java.util.List;
import java.util.Objects;

public class ClientController implements Initializable {


    private NettyNet net;

    @FXML
    TableView<FileInfo> filesTable;
    @FXML
    TableView<FileInfo> filesTableServer;
    @FXML
    ComboBox<String> disksBox;
    @FXML
    TextField pathField;
    @FXML
    TextField pathFieldServer;
    @FXML
    TextField login;
    @FXML
    TextField pass;
    @FXML
    TextField clientText;
    @FXML
    ListView<String> serverFiles;
    List<String> filesServer;
    Path fileToDownLoadPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initClientPanel();
        clientText.setText("Войдите на сервер или зарегистрируйтесь");

        net = new NettyNet(this::readMessage);

        serverFiles.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    pathFieldServer.setText(serverFiles.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    void readMessage(AbstractMessage abstractMessage) throws IOException {

        switch (abstractMessage.getType()) {

            case SERVER_FILE_UPLOAD_RESPONSE:
                ServerFileUploadResponse serverFileUploadResponse = (ServerFileUploadResponse) abstractMessage;
                clientText.setText(serverFileUploadResponse.getMsg());
                break;
            case AUTH_OK:
                AuthOK authOK = (AuthOK) abstractMessage;
                if (authOK.getLogin().equals("Неправильный логин или пароль")) {
                    clientText.setText(authOK.getLogin());
                } else {
                    clientText.setText("Добро пожаловать " + authOK.getLogin() );
                    login.clear();
                    pass.clear();
                }
                break;
            case REGISTRATION_OK:
                RegistrationOK registrationOK = (RegistrationOK) abstractMessage;
                if (registrationOK.getLogin().equals("Имя занято")) {
                    clientText.setText(registrationOK.getLogin());
                } else {
                    clientText.setText("Добро пожаловать " + registrationOK.getLogin() );
                    login.clear();
                    pass.clear();
                }
                break;
            case UPDATE_LIST_SERVER_RESPONSE:
                UpdateListServerResponse updateListServerResponse = (UpdateListServerResponse) abstractMessage;
                filesServer = updateListServerResponse.getFiles();
                refreshViewServer(filesServer, serverFiles);
                break;
            case FILE_MESSAGE:

                FileMessage fileMsg = null;
                if (abstractMessage instanceof FileMessage) {
                    fileMsg = (FileMessage) abstractMessage;
                }
                fileToDownLoadPath = Paths.get(pathField.getText());
                Files.write(
                        fileToDownLoadPath.resolve(fileMsg.getFileName()),
                        fileMsg.getBytes()
                );
                updateList(fileToDownLoadPath);

                break;
        }

    }

    private void initClientPanel(){
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);



        filesTable.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn);
        filesTable.getSortOrder().add(fileTypeColumn);

        disksBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disksBox.getItems().add(p.toString());
        }
        disksBox.getSelectionModel().select(0);

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateList(path);
                    }
                }
            }
        });

        updateList(Paths.get("."));
    }

    public void initServerPanel()  { // запрос на обновление списка файлов на сервере
        try {
            net.sendMessage(new UpdateListServerRequest());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initServerPanel1(){

        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Имя");
        filenameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        filenameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1L) {
                            text = "[DIR]";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDateColumn.setPrefWidth(120);

        filesTableServer.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn);
        filesTableServer.getSortOrder().add(fileTypeColumn);


        filesTableServer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(pathFieldServer.getText()).resolve(filesTableServer.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateListServer(path);
                    }
                }
            }
        });

        updateListServer(Paths.get("root"));
    }     //  недоработано

    private void refreshViewServer(List<String> filesServer, ListView<String> view) {
        Platform.runLater(() -> {
            view.getItems().clear();
            view.getItems().addAll(filesServer);
        });
    }

    public void upLoadFile() throws IOException {         //отправляем файл на сервер

        Path filePath = Paths.get(getCurrentPath(), getSelectedFilename());
        net.sendMessage(new FileMessage(filePath));
    }

    public void DownLoadFile(ActionEvent event) {

        net.sendMessage(new ServerFileDownloadRequest(pathFieldServer.getText()));
    }

    public String getSelectedFilename() {
        if (!filesTable.isFocused()) {
            return null;
        }
        return filesTable.getSelectionModel().getSelectedItem().getFilename();
    }     // получение имени файла для отправки на сервер

    public String getCurrentPath() {
        return pathField.getText();
    }  // получение паса файла для отправки на сервер

    // Кнопки и отображения в fx окне

    public void authToServer(){

        try {
            net.sendMessage(new SendAuthToServer(login.getText(), pass.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registration(ActionEvent event) {

        try {
            net.sendMessage(new RegistrationToServer(login.getText(), pass.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void updateList(Path path) {
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            filesTable.getItems().clear();
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTable.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Hе удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void updateListServer(Path path)  {        // недоработано
        try {
            pathFieldServer.setText(path.normalize().toAbsolutePath().toString());
            filesTableServer.getItems().clear();
            filesTableServer.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTableServer.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Hе удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    public void btnPathUpActionServer(ActionEvent actionEvent) {

        Path upperPath = Paths.get(pathFieldServer.getText());
        Path pathRoot = Paths.get("root").normalize().toAbsolutePath();
        if (!upperPath.equals(pathRoot)){
            upperPath = Paths.get(pathFieldServer.getText()).getParent();
            if (upperPath != null) {
                updateListServer(upperPath);
            }
        }

    }

    public void deleteFile(ActionEvent event) {
        net.sendMessage(new ServerFileDeleteRequest(pathFieldServer.getText()));

    }
}
