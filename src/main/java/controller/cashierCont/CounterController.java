package controller.cashierCont;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import entity.CashTransaction;
import entity.CounterRegister;
import configuration.Pref;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.CounterRegisterModel;
import org.hibernate.HibernateException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class CounterController implements Initializable {
    public Label lblTitle;
    public JFXTextField txt01;
    public JFXTextField txt05;
    public JFXTextField txt10;
    public JFXTextField txt20;
    public JFXTextField txt50;
    public JFXTextField txtRM1;
    public JFXTextField txtRM2;
    public JFXTextField txtRM5;
    public JFXTextField txtRM10;
    public JFXTextField txtRM20;
    public JFXTextField txtRM50;
    public JFXTextField txtRM100;
    public Label lblTotal;
    public Label lbl01;
    public Label lbl05;
    public Label lbl10;
    public Label lbl20;
    public Label lbl50;
    public Label lblRM1;
    public Label lblRM2;
    public Label lblRM5;
    public Label lblRM10;
    public Label lblRM20;
    public Label lblRM50;
    public Label lblRM100;
    public HBox hBoxFirst;
    public HBox hBoxDomination;
    public HBox hBoxAmount;
    public JFXTextField txtAmount;
    public JFXTextArea amountComment;
    public Label lblTotal1;
    public JFXTextArea domComment;
    public JFXButton btnCash;
    public JFXButton btnDomination;
    public AnchorPane currentPane;

    public JFXButton btnSave;
    public JFXButton btnCancel;
    public JFXButton btnBack;

    private BigDecimal amountTotal;
    private String commentAll;

    private String status;
    private String color;

    private boolean isAmount;
    private boolean isDomination;
    private BorderPane borderPane;


    private CounterRegisterModel cashRegisterModel;

    public void initData(String title, String color, BorderPane borderPane) {
        this.status = title;
        this.color = color;
        this.borderPane = borderPane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cashRegisterModel = new CounterRegisterModel();
        hBoxAmount.setVisible(false);
        hBoxDomination.setVisible(false);

        if(status != null){
            lblTitle.setText(status);
            btnCash.setStyle("-fx-background-color: "+color+";");
            btnDomination.setStyle("-fx-background-color: "+color+";");
        }

        amountTotal = new BigDecimal(0.00);

        txt01.textProperty().addListener(new calculateDebit(txt01,lbl01,new BigDecimal(0.01)));
        txt05.textProperty().addListener(new calculateDebit(txt05,lbl05,new BigDecimal(0.05)));
        txt10.textProperty().addListener(new calculateDebit(txt10,lbl10,new BigDecimal(0.10)));
        txt20.textProperty().addListener(new calculateDebit(txt20,lbl20,new BigDecimal(0.20)));
        txt50.textProperty().addListener(new calculateDebit(txt50,lbl50,new BigDecimal(0.50)));
        txtRM1.textProperty().addListener(new calculateDebit(txtRM1,lblRM1,new BigDecimal(1.00)));
        txtRM2.textProperty().addListener(new calculateDebit(txtRM2,lblRM2,new BigDecimal(2.00)));
        txtRM5.textProperty().addListener(new calculateDebit(txtRM5,lblRM5,new BigDecimal(5.00)));
        txtRM10.textProperty().addListener(new calculateDebit(txtRM10,lblRM10,new BigDecimal(10.00)));
        txtRM20.textProperty().addListener(new calculateDebit(txtRM20,lblRM20,new BigDecimal(20.00)));
        txtRM50.textProperty().addListener(new calculateDebit(txtRM50,lblRM50,new BigDecimal(50.00)));
        txtRM100.textProperty().addListener(new calculateDebit(txtRM100,lblRM100,new BigDecimal(100.00)));

        txtAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    txtAmount.setText(oldValue);
                }
                if(txtAmount.getText().isEmpty()){
                    amountTotal = new BigDecimal(0.00);
                }else {
                    amountTotal = new BigDecimal(txtAmount.getText());
                }
//                System.out.println("total " + amountTotal);
            }
        });

        if(btnBack != null){
            btnBack.setVisible(false);

            btnBack.setOnAction(event -> {
                hBoxAmount.setVisible(false);
                hBoxDomination.setVisible(false);
                hBoxFirst.setVisible(true);
                resetAction(event);
                isAmount = false;
                isDomination = false;
                btnBack.setVisible(false);
            });

        }

        if(btnCancel != null){
            btnCancel.setOnAction(this::close);
        }
    }

    private void close(ActionEvent actionEvent){
        resetAction(actionEvent);
        isAmount = false;
        isDomination = false;

        Stage stage = (Stage) borderPane.getScene().getWindow();
        stage.close();
    }

    public void resetAction(ActionEvent actionEvent) {
        txt01.clear();
        txt05.clear();
        txt10.clear();
        txt20.clear();
        txt50.clear();
        txtRM1.clear();
        txtRM2.clear();
        txtRM5.clear();
        txtRM10.clear();
        txtRM20.clear();
        txtRM50.clear();
        txtRM100.clear();
    }

    public void saveAction(ActionEvent actionEvent) {
        switch (status){
            case "Start Cash":
                if(isAmount){
                    commentAll = amountComment.getText();
                }
                if(isDomination){
                    commentAll = domComment.getText();
                }

                CounterRegister counterRegister = new CounterRegister();
                counterRegister.setCashStart(amountTotal);
                counterRegister.setTotalCashAdd(new BigDecimal(0.00));
                counterRegister.setTotalCashWithdrawal(new BigDecimal(0.00));
                counterRegister.setDateStart(LocalDateTime.now().toString());

                CashTransaction cashTransaction = new CashTransaction();
                cashTransaction.setAmount(amountTotal);
                cashTransaction.setCashType(status);
                cashTransaction.setComment(commentAll);
                cashTransaction.setUserId(Pref.getUserId());

                counterRegister.getCashTransactions().add(cashTransaction);
                cashRegisterModel.saveCounter(counterRegister);

                Pref.setStatus("active");
                Pref.setCounterId(cashRegisterModel.getId());
                CashierController.statusProperty.set("active");
                close(actionEvent);

                break;
            case "Add Cash":
                if(isAmount){
                    commentAll = amountComment.getText();
                }
                if(isDomination){
                    commentAll = domComment.getText();
                }

                Preferences userPreferences = Preferences.userRoot();
                String defaultValue = "default string";
                String info = userPreferences.get("id",defaultValue);

                System.out.println("here is pref " + info);

                //check first the start cash was add
                if(Pref.getStatus() != null){

                    if(amountTotal.intValue() != 0){
                        CashTransaction addCashTransaction = new CashTransaction();
                        addCashTransaction.setAmount(amountTotal);
                        addCashTransaction.setCashType(status);
                        addCashTransaction.setComment(commentAll);
                        addCashTransaction.setUserId(Pref.getUserId());

                        //update only since the prev cash already setup
                        cashRegisterModel.updateCounter(Pref.getCounterId(), "Menambah" , addCashTransaction);
                    }
                    close(actionEvent);
                }

                break;
            case "Withdraw Cash":
                if(isAmount){
                    commentAll = amountComment.getText();
                }
                if(isDomination){
                    commentAll = domComment.getText();
                }
                //check first the start cash was add
                if(Pref.getStatus() != null){

                    if(amountTotal.intValue() != 0){
                        CashTransaction addCashTransaction = new CashTransaction();
                        addCashTransaction.setAmount(amountTotal);
                        addCashTransaction.setCashType(status);
                        addCashTransaction.setComment(commentAll);
                        addCashTransaction.setUserId(Pref.getUserId());

                        //update only since the prev cash already setup
                        cashRegisterModel.updateCounter(Pref.getCounterId(), "Mengeluarkan" , addCashTransaction);
                    }
                    close(actionEvent);
                }
                break;
        }
    }

    public void amountAction(ActionEvent actionEvent) {
        hBoxAmount.setVisible(true);
        hBoxDomination.setVisible(false);
        hBoxFirst.setVisible(false);
        isAmount = true;
        isDomination = false;
        btnBack.setVisible(true);
    }

    public void dominationAction(ActionEvent actionEvent) {
        hBoxDomination.setVisible(true);
        hBoxAmount.setVisible(false);
        hBoxFirst.setVisible(false);
        isDomination = true;
        isAmount = false;
        btnBack.setVisible(true);
    }

    class calculateDebit implements ChangeListener<String> {
        JFXTextField text;
        Label label;
        BigDecimal amount;

        public calculateDebit() {
        }

        public calculateDebit(JFXTextField text, Label label, BigDecimal amount) {
            this.text = text;
            this.label = label;
            this.amount = amount;
        }


        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(!oldValue.matches("\\d*")){
                System.out.println("from old");
                oldValue = "";
            }
            if(newValue.matches("\\d*")){
                System.out.println("new " + newValue);
                System.out.println("old " + oldValue);
            }else{
                //contains not numbers
                text.setText(newValue.replaceAll("[^\\d]", ""));
                newValue = "";
                System.out.println("bad number " + newValue);
                System.out.println("bad old " + oldValue);

            }
            if(newValue.isEmpty()){
                newValue = "0";
            }
            if(oldValue.isEmpty()){
                oldValue = "0";
            }

            BigDecimal subAmount = new BigDecimal(newValue).multiply(amount).setScale(2,RoundingMode.FLOOR);
            System.out.println("sub amount " + subAmount);

            BigDecimal oldAmount = new BigDecimal(oldValue).multiply(amount).setScale(2,RoundingMode.FLOOR);
            System.out.println("old amount " + oldAmount);

            label.setText(subAmount.toString());
            getTotal(subAmount, oldAmount);
        }

    }

    private void getTotal(BigDecimal subAmount, BigDecimal old){
        amountTotal = amountTotal.add(subAmount).subtract(old);
        lblTotal.setText(amountTotal.toString());
        lblTotal1.setText(amountTotal.toString());
    }
}
