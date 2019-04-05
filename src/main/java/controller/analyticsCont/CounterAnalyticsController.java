package controller.analyticsCont;

import com.jfoenix.controls.*;
import entity.CashTransaction;
import entity.CounterRegister;
import entity.Sales;
import interfaces.CartInterface;
import interfaces.CounterRegisterInterface;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.StringConverter;
import model.CartModel;
import model.CounterRegisterModel;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.Icon;
import org.kordamp.ikonli.material.Material;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CounterAnalyticsController implements Initializable, CounterRegisterInterface, CartInterface {

    public JFXDatePicker startDate;
    public JFXTimePicker startTime;
    public JFXDatePicker endDate;
    public JFXTimePicker endTime;
    public Label lblResult;
    public JFXTreeTableView treeTableView;
    public TreeTableColumn<Object, String> colNo;
    public TreeTableColumn<Object, String> colTrans;
    public TreeTableColumn<Object, String> colAmount;
    public TreeTableColumn<Object, String> colComment;
    public TreeTableColumn<Object, String> colDate;
    public TreeTableColumn<Object, String> colTime;
    public TreeTableColumn<Object, String> colCounterId;
    public TreeTableColumn<Object, String> colCashierId;
    public Label lblStart;
    public Label lblAdd;
    public Label lblWithdraw;
    public Label lblTotal;
    public GridPane gridPane;
    public Label lblSales;


    private String startDateTime = null;
    private String endDateTime = null;

    private CounterRegisterModel counterRegisterModel;
    private CartModel cartModel;
    private Boolean isCash = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        counterRegisterModel = new CounterRegisterModel();
        cartModel = new CartModel();

        //setting date display
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
//            System.out.println(startDate.getValue());
        } else {
            //get today date
            st = String.valueOf(LocalDate.now());
//            System.out.println(LocalDate.now());
        }
        String stm = null;
        if (startTime.getValue() != null) {
            stm = String.valueOf(startTime.getValue());
//            System.out.println(startTime.getValue());
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
//            System.out.println(endDate.getValue());
        } else {
            //get today date
            et = String.valueOf(LocalDate.now());
        }
        String etm = null;
        if (endTime.getValue() != null) {
            etm = String.valueOf(endTime.getValue());
//            System.out.println(endTime.getValue());
        } else {
            etm = "23:59";
        }

        endDateTime = et + " " + etm;
        System.out.println(endDateTime);

        lblResult.setText(startDateTime +" to "+ endDateTime);

        //check database
        if (!COUNTERREGISTERLIST.isEmpty()) {
            COUNTERREGISTERLIST.clear();
        }
//        CASHREGISTERLIST.addAll(cashRegisterModel.getCashByDate(startDateTime, endDateTime));
//        ObservableList<CashRegister> cashRegisterList = cashRegisterModel.getCashByDate(startDateTime, endDateTime);

        COUNTERREGISTERLIST.addAll(counterRegisterModel.getCounterByDate(startDateTime, endDateTime));
        //check database
        if (!CARTTRANSACTIONLIST.isEmpty()) {
            CARTTRANSACTIONLIST.clear();
        }

        CARTTRANSACTIONLIST.addAll(cartModel.getCashByDate(startDateTime, endDateTime));

        setCell();
        refreshList();

        BigDecimal startAmount = new BigDecimal(0.00);
        BigDecimal addAmount = new BigDecimal(0.00);
        BigDecimal withdrawAmount = new BigDecimal(0.00);
        BigDecimal totalAmount = new BigDecimal(0.00);
        BigDecimal salesCashOnly = new BigDecimal(0.00);
        if(COUNTERREGISTERLIST != null){
//            System.out.println(CASHREGISTERLIST);
//            System.out.println(CARTTRANSACTIONLIST);
            for(CounterRegister cr: COUNTERREGISTERLIST){
//                System.out.println("from cash " + cp.getCartTransList());
                //get total start
                if(cr.getCashStart() != null){
                    startAmount = startAmount.add(cr.getCashStart());
                }
                if(cr.getTotalCashAdd() != null){
                    addAmount = addAmount.add(cr.getTotalCashAdd());
                }
                if(cr.getTotalCashWithdrawal() != null){
                    withdrawAmount = withdrawAmount.add(cr.getTotalCashWithdrawal());
                }



                totalAmount = startAmount.add(addAmount).subtract(withdrawAmount);

                if(cr.getSalesList() != null){
                    for(Sales sales : cr.getSalesList()){
                        System.out.println(sales.getCashSales());
                        salesCashOnly = salesCashOnly.add(sales.getCashSales());
                    }
                }
                System.out.println(cr.getSalesList());
                totalAmount = totalAmount.add(salesCashOnly);
//                System.out.println(cp.getCashTransactions());
            }

            lblStart.setText(startAmount.toString());
            lblAdd.setText(addAmount.toString());
            lblWithdraw.setText("( "+withdrawAmount.toString()+" )");
            lblSales.setText(salesCashOnly.toString());
            lblTotal.setText(totalAmount.toString());



            //get total start

        }


    }


    private void setCell() {

        colNo.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CounterRegister)) {
                CounterRegister f = (CounterRegister) rowItem.getValue() ;
                return new SimpleStringProperty(String.valueOf(f.getId()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        colTrans.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                return new SimpleStringProperty(emp.getCashType());
            } else if(rowItem != null && (rowItem .getValue() instanceof Sales)){
                return new SimpleStringProperty("Sales cash only");
            }
            else {
                return new SimpleStringProperty("");
            }
        });

        colAmount.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                return new SimpleStringProperty(String.valueOf(emp.getAmount()));
            }
            else if(rowItem != null && (rowItem .getValue() instanceof Sales)){
                Sales sales = (Sales) rowItem.getValue();
                return new SimpleStringProperty(String.valueOf(sales.getCashSales()));
            }
            else {
                return new SimpleStringProperty("");
            }
        });

        colComment.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                return new SimpleStringProperty(emp.getComment());
            } else {
                return new SimpleStringProperty("");
            }
        });

        colDate.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dateUrl = emp.getDate();
                LocalDate dt = LocalDate.parse(dateUrl, dateTimeFormatter);
                return new SimpleStringProperty(dt.toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        colTime.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String dateUrl = emp.getDate();
                LocalTime dt = LocalTime.parse(dateUrl, dateTimeFormatter);
                return new SimpleStringProperty(dt.toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        colCounterId.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                return new SimpleStringProperty(String.valueOf(emp.getCounterRegister().getId()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        colCashierId.setCellValueFactory(cellData -> {
            TreeItem<Object> rowItem = cellData.getValue();
            if (rowItem != null && (rowItem.getValue() instanceof CashTransaction)) {
                CashTransaction emp = (CashTransaction) rowItem.getValue();
                return new SimpleStringProperty(String.valueOf(emp.getUserId()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        treeTableView.getColumns().setAll(colNo, colTrans, colAmount, colComment, colDate, colTime, colCounterId, colCashierId);
    }

    private void refreshList() {
//        final TreeItem<Object> root = new RecursiveTreeItem<>(observableList);

        //normal setup without recursive
        TreeItem<Object> root = new TreeItem<>(null);
        for (CounterRegister cr : COUNTERREGISTERLIST) {
            TreeItem<Object> crItem = new TreeItem<>(cr);
            root.getChildren().add(crItem);
            crItem.setExpanded(true);

            for (CashTransaction ct : cr.getCashTransactions()) {
                TreeItem<Object> employeeItem = new TreeItem<>(ct);
                crItem.getChildren().add(employeeItem);
            }
            for(Sales sales : cr.getSalesList()){
                TreeItem<Object> salesItem = new TreeItem<>(sales);
                crItem.getChildren().add(salesItem);
            }

        }

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

}
