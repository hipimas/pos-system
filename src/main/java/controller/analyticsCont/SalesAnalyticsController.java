package controller.analyticsCont;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import entity.CartTransaction;
import interfaces.CartInterface;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.util.StringConverter;
import model.CartModel;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SalesAnalyticsController implements Initializable , CartInterface {
    public JFXDatePicker startDate;
    public JFXTimePicker startTime;
    public JFXDatePicker endDate;
    public JFXTimePicker endTime;
    public Label lblResult;
    public JFXTreeTableView<CartTransaction> treeTableView;
    public TreeTableColumn<CartTransaction, String> colNo;
    public TreeTableColumn<CartTransaction, String> colDate;
    public TreeTableColumn<CartTransaction, String> colTime;
    public TreeTableColumn<CartTransaction, String> colTotalItem;
    public TreeTableColumn<Object, String> colItem;
    public TreeTableColumn<Object, String> colQty;
    public TreeTableColumn<Object, String> colCustomer;
    public TreeTableColumn<CartTransaction, String> colSubTotal;
    public TreeTableColumn<CartTransaction, String> colTax;
    public TreeTableColumn<Object, String> colDisc;
    public TreeTableColumn<CartTransaction, String> colTotal;
    public TreeTableColumn<Object, String> colPaid;
    public TreeTableColumn<CartTransaction, String> colType;
    public TreeTableColumn<CartTransaction, String> colCashId;
    public TreeTableColumn<CartTransaction, String> colCashierId;
    public Label lblSubTotal;
    public Label lblSubTax;
    public Label lblSubDiscount;
    public Label lblTotalAmount;
    public Label lblTotalItem;
    public JFXComboBox optionCombo;
    public JFXComboBox selectCombo;

    private String startDateTime = null;
    private String endDateTime = null;

    private CartModel cartModel;

    private Map<Integer, String> map;
    private int mapId;
    private List<String> listOption;
    private List<String> sortedList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        map = new HashMap<>();
        map.put(1, "Cashier ID");
        map.put(2, "Counter ID");
        map.put(3, "Payment Type");

        cartModel = new CartModel();

        //filter option
        ObservableMap<Integer, String> optionFilter = FXCollections.observableMap(map);
        optionCombo.getItems().setAll(optionFilter.values());

        optionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null){
                for(Map.Entry<Integer, String> entry : map.entrySet()) {
                    if(newVal.equals(entry.getValue())){
                        mapId = entry.getKey();
                        listOption = new ArrayList<>();
                        sortedList = new ArrayList<>();
                        if(CARTTRANSACTIONLIST != null){
                            for(CartTransaction cp: CARTTRANSACTIONLIST){
                                //get total start
                                if(mapId == 1){
                                    listOption.add(String.valueOf(cp.getUserId()));
                                }else if(mapId ==2){
                                    listOption.add(String.valueOf(cp.getCounterRegister().getId()));
                                }else if(mapId == 3){
                                    listOption.add(cp.getPaymentType());
                                }
                            }
                        }

                        Set<String> duplicateSet = new LinkedHashSet<>(listOption);
                        sortedList = new ArrayList<>(duplicateSet);

                        selectCombo.getItems().setAll(sortedList);
                        System.out.println("here key:" + entry.getKey() + " value:" + entry.getValue());
                    }
                }
            }
        });

        selectCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null){
                System.out.println("select option " + newVal);
                treeTableView.setPredicate(e -> {
                    if(mapId == 1){
                        if(e.getValue().getUserId() == Integer.parseInt(newVal.toString())){
                            return true;
                        }
                    }
                    if(mapId == 2){
                        if(e.getValue().getCounterRegister().getId() == Integer.parseInt(newVal.toString())){
                            return true;
                        }
                    }
                    if(mapId == 3){
                        if(e.getValue().getPaymentType().equals(newVal)){
                            return true;
                        }
                    }

                    return false;
                });
            }
        });


        String pattern = "dd/MM/yyyy";
        startDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        //setting date display
        endDate.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

    }

    public void resetAction(ActionEvent actionEvent) {
        startDate.setValue(null);
        endDate.setValue(null);
        startTime.setValue(null);
        endTime.setValue(null);
    }

    public void goAction(ActionEvent actionEvent) {
        //get start date and time
        String st = null;
        if (startDate.getValue() != null) {
            st = String.valueOf(startDate.getValue());
        } else {
            //get today date
            st = String.valueOf(LocalDate.now());
        }
        String stm = null;
        if (startTime.getValue() != null) {
            stm = String.valueOf(startTime.getValue());
        } else {
            //set time from 12.01AM
            stm = "00:01";
        }

        startDateTime = st + " " + stm;
        System.out.println(startDateTime);

        //get end date and time
        String et = null;
        if (endDate.getValue() != null) {
            et = String.valueOf(endDate.getValue());
        } else {
            //get today date
            et = String.valueOf(LocalDate.now());
        }
        String etm = null;
        if (endTime.getValue() != null) {
            etm = String.valueOf(endTime.getValue());
        } else {
            etm = "23:59";
        }

        endDateTime = et + " " + etm;
        System.out.println(endDateTime);

        lblResult.setText(startDateTime +" to "+ endDateTime);

        //check database
        if (!CARTTRANSACTIONLIST.isEmpty()) {
            CARTTRANSACTIONLIST.clear();
        }
        CARTTRANSACTIONLIST.addAll(cartModel.getCashByDate(startDateTime, endDateTime));

        setCell();
        refreshList();

        BigDecimal subAmount = new BigDecimal(0.00);
        BigDecimal subTax = new BigDecimal(0.00);
        BigDecimal subDisc = new BigDecimal(0.00);
        BigDecimal totalAmount = new BigDecimal(0.00);
        int totalItem = 0;
        if(CARTTRANSACTIONLIST != null){
            for(CartTransaction cp: CARTTRANSACTIONLIST){
                //get total start
                subAmount = subAmount.add(cp.getSubTotal());
                subTax = subTax.add(cp.getTaxAmount());
                totalAmount = totalAmount.add(cp.getTotalAmount());
                totalItem = totalItem + cp.getTotalItem();
//                System.out.println(cp.getCashStart());
//                System.out.println(cp.getCashTransactions());
            }

            lblSubTotal.setText(subAmount.toString());
            lblSubTax.setText(subTax.toString());
            lblSubDiscount.setText("");
            lblTotalAmount.setText(totalAmount.toString());
            lblTotalItem.setText(String.valueOf(totalItem));
        }
    }

    private void setCell(){
        colNo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf("#" + cellData.getValue().getValue().getId())));
        colDate.setCellValueFactory(cellData -> {
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateUrl = cellData.getValue().getValue().getDate();
            LocalDate dt = LocalDate.parse(dateUrl, dateTimeFormatter);
            return new SimpleStringProperty(dt.toString());
        });
        colTime.setCellValueFactory(cellData -> {
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateUrl = cellData.getValue().getValue().getDate();
            LocalTime dt = LocalTime.parse(dateUrl, dateTimeFormatter);
            return new SimpleStringProperty(dt.toString());
        });
        colTotalItem.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getTotalItem())));
        colSubTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getSubTotal().toString()));
        colTax.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getTaxAmount().toString()));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getTotalAmount().toString()));
        colCashId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getCounterRegister().getId())));
        colCashierId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getUserId())));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue().getPaymentType())));

        treeTableView.getColumns().setAll(colNo, colDate, colTime, colTotalItem, colSubTotal, colTax, colTotal, colCashId, colCashierId, colType);
    }

    private void refreshList(){
        //normal setup without recursive
//        TreeItem<Object> root = new TreeItem<>(null);
//        for (CartTransaction factory : CARTTRANSACTIONLIST) {
//            TreeItem<Object> factoryItem = new TreeItem<>(factory);
//            root.getChildren().add(factoryItem);
//            factoryItem.setExpanded(true);
//        }
//
//        treeTableView.setRoot(root);
//        treeTableView.setShowRoot(false);


        ObservableList<CartTransaction> products = FXCollections.observableArrayList(CARTTRANSACTIONLIST);
        final TreeItem<CartTransaction> root = new RecursiveTreeItem<>(products, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }
}
