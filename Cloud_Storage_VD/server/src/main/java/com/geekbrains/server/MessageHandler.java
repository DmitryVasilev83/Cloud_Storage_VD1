package com.geekbrains.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.geekbrains.core.model.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private Path currentDir;
    private Path rootDir;
    boolean authentificationAccess = false; //  маркер успешной аутентификации юзера
    @FXML
    TableView<FileInfo> filesTableServer;
    @FXML
    TextField pathFieldServer;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client connected...");
        rootDir = Paths.get("root");

        if (!Files.exists(rootDir)) {
            Files.createDirectory(rootDir);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Client disconnected...");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                AbstractMessage abstractMessage) throws Exception {
        switch (abstractMessage.getType()) {

            case FILE_MESSAGE:
                if (authentificationAccess){
                    FileMessage fileMsg = (FileMessage) abstractMessage;
                    Files.write(
                            currentDir.resolve(fileMsg.getFileName()),
                            fileMsg.getBytes()
                    );
                    ctx.writeAndFlush(new ServerFileUploadResponse());
                    ctx.writeAndFlush(new UpdateListServerResponse(currentDir));
                }

                break;
            case UPDATE_LIST_SERVER_REQUEST:         // обновление списка файлов сервера
                if (authentificationAccess){
                    ctx.writeAndFlush(new UpdateListServerResponse(currentDir));
                }
                break;
            case SEND_AUTH_TO_SERVER:       // запрос на аутентификацию
                SendAuthToServer sats = (SendAuthToServer) abstractMessage;
                BaseAuthService bas = new BaseAuthService();
                String auth = bas.getAuthByLoginAndPassword(sats.getLogin(), sats.getPass());
                if (!auth.equals("Неправильный логин или пароль")){
                    authentificationAccess = true;
                    currentDir = Paths.get("root", auth);
                    ctx.writeAndFlush(new UpdateListServerResponse(currentDir));
                    if (!Files.exists(currentDir)) {
                        Files.createDirectory(currentDir);
                    }
                }
                ctx.writeAndFlush(new AuthOK(auth));
                break;
            case REGISTRATION_TO_SERVER:
                RegistrationToServer registrationToServer = (RegistrationToServer) abstractMessage;
                BaseAuthService bas1 = new BaseAuthService();
                String reg = bas1.getRegByLoginAndPassword(registrationToServer.getLogin(), registrationToServer.getPass());
                if (!reg.equals("Имя занято")){
                    authentificationAccess = true;
                    currentDir = Paths.get("root", reg);
                    if (!Files.exists(currentDir)) {
                        Files.createDirectory(currentDir);
                    }
                }
                ctx.writeAndFlush(new RegistrationOK(reg));
                break;
            case SERVER_FILE_DOWNLOAD_REQUEST:
                if (authentificationAccess){
                    ServerFileDownloadRequest serverFileDownloadRequest =(ServerFileDownloadRequest) abstractMessage;
                    Path path = Paths.get(String.valueOf(currentDir.resolve(serverFileDownloadRequest.getFileName())));
                    ctx.writeAndFlush(new FileMessage(path));
                }
                break;
            case SERVER_FILE_DELETE_REQUEST:
                if (authentificationAccess){
                    ServerFileDeleteRequest serverFileDeleteRequest =(ServerFileDeleteRequest) abstractMessage;
                    Path path = Paths.get(String.valueOf(currentDir.resolve(serverFileDeleteRequest.getFileName())));
                    Files.delete(path);
                    ctx.writeAndFlush(new UpdateListServerResponse(currentDir));
                }

                break;
        }
    }

    // Недоработанные методы

    public void initServerPanel1(){    //  недоработано

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

        System.out.println("test1");        // test

        filesTableServer.getColumns().addAll(fileTypeColumn, filenameColumn, fileSizeColumn, fileDateColumn);
        filesTableServer.getSortOrder().add(fileTypeColumn);



        filesTableServer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(pathFieldServer.getText()).resolve(filesTableServer.getSelectionModel().getSelectedItem().getFilename());
                    if (Files.isDirectory(path)) {
                        updateListServer1(path);
                    }
                }
            }
        });

        updateListServer1(Paths.get("root"));
    }

    public void updateListServer1(Path path) {    // недоработано



        try {
            pathFieldServer.setText(path.normalize().toAbsolutePath().toString());
            filesTableServer.getItems().clear();
            filesTableServer.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
            filesTableServer.sort();
        }catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Hе удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }





    }
}

