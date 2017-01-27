package main.scene;

import database.SqlDeleteData;
import database.SqlRetrieveData;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import entity.Task;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by lerai on 12/7/2016.
 */
public class TaskListController implements Initializable {

    private ResultSet rs;
    private ObservableList<Task> personalList = FXCollections.observableArrayList();

    @FXML
    public ComboBox chooseTask;

    @FXML
    private ComboBox  chooseTask1;

   // private enum state {PERSONAL, SCHOOL,WORK}

   // private String currentState ;

    @FXML
    void handleButtonAction(ActionEvent event) {
        /*currentState=chooseTask.getSelectionModel().getSelectedItem().toString();

        switch (currentState){
            case "Personal":
                System.out.println("Personal");
                break;
            case "School":
                System.out.println("School");
                break;
            case "Work":
                System.out.println("Work");
                break;

        }*/

    }


    @FXML
    private javafx.scene.control.ListView listContent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        chooseTask.getItems().addAll("Personal","School","Work");
        chooseTask1.getItems().addAll("OOPP","IP networking","Law of IT");


        personalList.addListener(new ListChangeListener<Task>() {
            @Override
            public void onChanged(Change<? extends Task> c) {

                ObservableList<String> displayTitle=FXCollections.observableArrayList();
                for(int i=0;i<personalList.size();i++){
                    Task task=personalList.get(i);
                    displayTitle.add(task.getTitle()+"\t"+task.getDueDate()+"\t"+task.getDueTime()+"\t"+task.getLocation()+"\t"+task.getPriority());

                }
                listContent.setItems(displayTitle);


            }
        });
        listContent.setOnMouseClicked(event -> showContectMenu(listContent.getSelectionModel().getSelectedIndex()));
        retrieveData();

    }

    @FXML
    void addTask(ActionEvent event) throws IOException{

        Parent root = FXMLLoader.load(getClass().getResource("AddTask.fxml"));
        Scene scene=new Scene(root);
        Stage stage=new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add Task");
        stage.setScene(scene);
        stage.showAndWait();
        retrieveData();

    }
    private void retrieveData (){
        personalList.clear();
        //System.out.println(personalList.size());
        SqlRetrieveData data= new SqlRetrieveData();
        data.openConnection();

        rs=data.retriveWholeTable("task");
        data.closeConnection();
        try {
            while(rs.next()){
                Task task = new Task();
                task.setTitle(rs.getString("title"));
                task.setLocation(rs.getString("location"));
                task.setPriority(rs.getString("priority"));
                task.setDueDate(rs.getDate("dueDate"));
                task.setDueTime(rs.getTime("dueTime"));

               personalList.add(task);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


       // listContent.setItems(personalList);
    }
    private void showContectMenu(int index){
        System.out.println(listContent.getSelectionModel().getSelectedItems());
        ContextMenu contextMenu=new ContextMenu();
        contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("showing");
            }
        });
        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

            }

        });
        //Label label=new Label("Menu");
        MenuItem delete=new MenuItem("delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Task task=personalList.get(index);
                SqlDeleteData sql=new SqlDeleteData();
                sql.openConnection();
                sql.deleteTableRow("task","title","=","\""+task.getTitle()+"\"");
                sql.closeConnection();
                retrieveData();

            }
        });
        contextMenu.getItems().addAll(delete);
        listContent.setContextMenu(contextMenu);
    }



}
