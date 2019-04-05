package controller.cashierCont;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import configuration.Pref;
import entity.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CounterRegisterModel;
import model.SalesModel;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class CounterCloseController implements Initializable {
    public VBox vBoxFirst;
    public HBox hBoxSecond;
    public Label lblStart;
    public Label lblAdd;
    public Label lblDraw;
    public Label lblSale;
    public JFXTextField txtAmount;
    public JFXTextArea txtComm;
    public Label lblEnd;
    public Label lblEndSales;
    public Label lblCounterCash;
    public AnchorPane currentPane;

    private CounterRegister counterRegister;
    private CounterRegisterModel cashRegisterModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hBoxSecond.setVisible(false);
        cashRegisterModel = new CounterRegisterModel();
    }

    private void getData() {
        //get current session id
        int counterID = Pref.getCounterId();
        counterRegister = cashRegisterModel.getCounterRegister(counterID);

        System.out.println("close get counter id " + counterID);
        if(counterRegister != null){
            BigDecimal totalSales = new BigDecimal(0.00);
            BigDecimal cashOnlySales = new BigDecimal(0.00);
            BigDecimal cardSales = new BigDecimal(0.00);
            BigDecimal creditSales = new BigDecimal(0.00);
            if(counterRegister.getCartTransList() != null){
                for(CartTransaction p :counterRegister.getCartTransList()){
                    System.out.println("cart list item"  + p);
                    totalSales = totalSales.add(p.getTotalAmount());
                    if(p.getPaymentType().equals("Cash")){
                        cashOnlySales = cashOnlySales.add(p.getTotalAmount());
                    }
                    if(p.getPaymentType().equals("Credit card")){
                        cardSales = cardSales.add(p.getTotalAmount());
                    }
                    if(p.getPaymentType().equals("Credit")){
                        creditSales = creditSales.add(p.getTotalAmount());
                    }
                }
            }

            System.out.println("close amount " + totalSales);
            System.out.println("breakdown amount cash " + cashOnlySales);
            System.out.println("breakdown amount card " + cardSales);
            System.out.println("breakdown amount credit " + creditSales);

            Sales sales = new Sales();
            sales.setCashSales(cashOnlySales);
            sales.setCardSales(cardSales);
            sales.setCreditSales(creditSales);
            sales.setTotalSales(totalSales);
            sales.setUserId(Pref.getUserId());

            sales.setCounterRegister(counterRegister);

            SalesModel salesModel = new SalesModel();
            salesModel.saveSales(sales);

            System.out.println("close at stat " + counterRegister.getCashStart());
            System.out.println("close at add " + counterRegister.getTotalCashAdd());
            System.out.println("close at draw " +counterRegister.getTotalCashWithdrawal());
            System.out.println("close at total " +counterRegister.getCashEnding());

            lblStart.setText(counterRegister.getCashStart().toString());
            lblAdd.setText(counterRegister.getTotalCashAdd() != null ? counterRegister.getTotalCashAdd().toString() : "0");
            lblDraw.setText(counterRegister.getTotalCashWithdrawal() != null ? counterRegister.getTotalCashWithdrawal().toString() : "0");
            lblEnd.setText(counterRegister.getCashEnding().toString());
            lblSale.setText(totalSales.toString());
            lblEndSales.setText((counterRegister.getCashEnding().add(sales.getTotalSales())).toString());
            lblCounterCash.setText((counterRegister.getCashEnding().add(sales.getCashSales())).toString());


            //update database to cash transaction for record
            CashTransaction addCashTransaction = new CashTransaction();
            addCashTransaction.setAmount(counterRegister.getCashEnding().add(sales.getCashSales()));
            addCashTransaction.setCashType("Cash Ending");
            addCashTransaction.setComment("cash end by id " + counterID);
            addCashTransaction.setUserId(Pref.getUserId());

            //update the end cash and date end and close current counter
            cashRegisterModel.closeCounter(counterID, "Penutupan", addCashTransaction);
            CashierController.statusProperty.set("not-active");
            Pref.setStatus("not-active");
            vBoxFirst.setVisible(false);
        }
    }

    public void setPasswordAction(ActionEvent actionEvent) {
        vBoxFirst.setVisible(false);
        hBoxSecond.setVisible(true);
        //check the password and continue

        getData();
    }

    public void confirmClose(ActionEvent actionEvent) {

        Stage stage = (Stage) currentPane.getScene().getWindow();
        stage.close();
    }
}
