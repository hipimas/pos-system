package controller.subCont;

import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import entity.Promotion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.PromotionModel;
import org.hibernate.HibernateException;
import validation.NumberFormatDecimal;

import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PromotionController implements Initializable {
    public JFXTreeTableView treeTableView;
    public TreeTableColumn colName;
    public TreeTableColumn colType;
    public TreeTableColumn colItem;
    public TreeTableColumn collAmount;
    public TreeTableColumn colStart;
    public TreeTableColumn colEnd;
    public JFXComboBox<String> comboType;
    public JFXDatePicker dateStart;
    public JFXDatePicker dateEnd;
    public JFXButton btnSave;
    public JFXButton btnReset;
    public VBox mainVBox;
    public VBox extraVBox;

    // Create a Map.
    private Map<Integer ,String> mapType = new LinkedHashMap<>();

    private boolean addExtra = false;
    private int typeOption = 0;

    //for addition item extra
    private String name = null;
    private String item = null;
    private JFXTextField txtName = new JFXTextField();
    private Boolean error = false;
    private String startDate = null;
    private String endDate = null;

    //validation
    private RequiredFieldValidator validatorCombo = new RequiredFieldValidator();
    private RequiredFieldValidator validatorName = new RequiredFieldValidator();
    private RequiredFieldValidator validatorItemAmount = new RequiredFieldValidator();
    private NumberValidator numberValidatorItemAmount = new NumberValidator();
    private RequiredFieldValidator validatorItemPercent = new RequiredFieldValidator();
    private NumberValidator numberValidatorItemPercent = new NumberValidator();
    private RequiredFieldValidator validatorAmount = new RequiredFieldValidator();
    private NumberFormatDecimal numberValidatorAmount = new NumberFormatDecimal();
    private RequiredFieldValidator validatorPercentDiscount = new RequiredFieldValidator();
    private NumberValidator numberValidatorPercentDiscount = new NumberValidator();

    //case1
    private JFXTextField txtAmountItem = new JFXTextField();
    private JFXTextField txtAmountDiscount = new JFXTextField();
    private BigDecimal amountDiscount = null;

    //case2
    private JFXTextField txtPercentItem = new JFXTextField();
    private JFXTextField txtPercentDiscount = new JFXTextField();
    private int percentDiscount = 0;

    //case3
    private JFXTextField txtItemFree = new JFXTextField();

    //case 17
    private JFXTextField txtItemBox = new JFXTextField();
    private JFXTextField txtAmountTotalBox = new JFXTextField();
    private BigDecimal amountTotal = null;
    private RequiredFieldValidator validatorItemBox = new RequiredFieldValidator();
    private RequiredFieldValidator validatorAmountTotalBox = new RequiredFieldValidator();
    private NumberValidator numberValidatorItemBox = new NumberValidator();
    private NumberFormatDecimal numberValidatorAmountTotalBox = new NumberFormatDecimal();


    private PromotionModel promotionModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        promotionModel = new PromotionModel();

        //crete a list of types
        createListType();

        //insert data into comboBox
        ObservableMap<Integer, String> items = FXCollections.observableMap(mapType);
        comboType.getItems().setAll(items.values());

        //listener for comboBox when change the selected and inserted the required field
        comboType.valueProperty().addListener((obs, oldval, newval) -> {
            if(newval != null){
                for(Map.Entry<Integer, String> entry : mapType.entrySet()) {
                    int key = entry.getKey();
                    String value = entry.getValue();
                    if(newval.equals(value)){
                        //change the current field if prev already selected

                        if(addExtra){
                            extraVBox.getChildren().clear();
//                            extraVBox = new VBox();
                            addExtra = false;
                            clearValidation();
                        }

                        addExtraField(key);
////                      System.out.println("selected done id: " + key + " value: " + newval);
                        comboType.setValue(mapType.get(key));
                        typeOption = key;
                        System.out.println("here key " + key);
                    }
                }

            }
        });

        //setting for display date
        String pattern = "dd/MM/yyyy";
        StringConverter<LocalDate> dateStringConverter = new StringConverter<>() {
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
        };

        dateStart.setConverter(dateStringConverter);
        dateEnd.setConverter(dateStringConverter);
    }

    public void saveAction(ActionEvent actionEvent) {
        validatorCombo.setMessage("User Input Required");

        //validation for name
        txtName.validate();
        txtAmountItem.validate();
        txtAmountDiscount.validate();
        txtPercentItem.validate();
        txtPercentDiscount.validate();
        txtItemBox.validate();
        txtAmountTotalBox.validate();

        if(validatorCombo.getHasErrors() || validatorName.getHasErrors()){
            txtName.requestFocus();
            return;
        }
        name = txtName.getText();
        //set value based option
        switch (typeOption){
            case 1:
                if(validatorItemAmount.getHasErrors() || numberValidatorItemAmount.getHasErrors()
                        || validatorAmount.getHasErrors() || numberValidatorAmount.getHasErrors() ){
                    return;
                }
                item = txtAmountItem.getText();
                amountDiscount = new BigDecimal(txtAmountDiscount.getText());
                break;
            case 2:
                if(validatorItemPercent.getHasErrors() || numberValidatorItemPercent.getHasErrors()
                        || validatorPercentDiscount.getHasErrors() || numberValidatorPercentDiscount.getHasErrors() ){
                    return;
                }
                item = txtPercentItem.getText();
                percentDiscount = Integer.parseInt(txtPercentDiscount.getText());
                break;
            case 17:
                if(validatorItemBox.getHasErrors()|| numberValidatorItemBox.getHasErrors()
                        || validatorAmountTotalBox.getHasErrors() || numberValidatorAmountTotalBox.getHasErrors()){
                    return;
                }

                item = txtItemBox.getText();
                amountTotal = new BigDecimal(txtAmountTotalBox.getText());
                break;
        }

        //get date
        if(dateStart.getValue() != null){
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String textdate = dateTimeFormatter.format(dateStart.getValue());

            LocalTime s = LocalTime.now();
            String startUserDateString = textdate+" "+s;

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date startUserDate = null;
            try {
                startUserDate = df.parse(startUserDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDate = df.format(startUserDate);
        }

        if(dateEnd.getValue() != null){
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String textdate = dateTimeFormatter.format(dateEnd.getValue());

            LocalTime s = LocalTime.now();
            String startUserDateString = textdate+" "+s;

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date startUserDate = null;
            try {
                startUserDate = df.parse(startUserDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            endDate = df.format(startUserDate);
        }


        Promotion promotion = new Promotion();
        promotion.setName(name);
        promotion.setPromoType(typeOption);
        promotion.setCondition(item);
        promotion.setDiscountAmount(amountDiscount);
        promotion.setDiscountPercent(percentDiscount);
        promotion.setDiscountTotal(amountTotal);

        if(startDate != null){
            promotion.setStartDate(startDate);
        }
        if(endDate != null){
            promotion.setEndDate(endDate);
        }

        try {
            promotionModel.savPromotion(promotion);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Berjaya");
            alert.setHeaderText(null);
            alert.setContentText("Produk baharu ditambah dalam senarai");
            alert.showAndWait();

        }catch (HibernateException r){
            r.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan !");
            alert.setContentText("Masalah dalam menambah produk baharu!");

            alert.showAndWait();
        }


        System.out.println("type: " + typeOption +" name: " + name + " item: " + item);
        resetAction(actionEvent);
        System.out.println("done adding");
    }

    public void resetAction(ActionEvent actionEvent) {
        txtName.clear();
        txtAmountItem.clear();
        txtPercentItem.clear();
        txtAmountDiscount.clear();
        txtPercentDiscount.clear();
        dateStart.setValue(null);
        dateEnd.setValue(null);
        txtAmountTotalBox.clear();
        txtItemBox.clear();

        name = null;
        item = null;
        amountDiscount = null;
        percentDiscount = 0;

        comboType.getSelectionModel().clearSelection();
        if(addExtra){
            extraVBox = new VBox();
            addExtra = false;
        }
        clearValidation();
    }

    private void clearValidation() {
        //clear the error for validation if exist
        txtName.resetValidation();
        txtAmountItem.resetValidation();
        txtPercentItem.resetValidation();
        txtAmountDiscount.resetValidation();
        txtItemBox.resetValidation();
        txtAmountTotalBox.resetValidation();

//        numberValidatorPercentDiscount = new NumberValidator();
    }

    private void addExtraField(int key){
        //similar field for all extra item
        txtName.setPromptText("Name");
        txtName.setLabelFloat(true);
        //set validation
        validatorName.setMessage("Sila masukkan nama");
        txtName.getValidators().add(validatorName);

        switch (key){
            case 1:
                //how many item -name/howmanyitem/amountdiscount
                txtAmountItem.setPromptText("Item");
                txtAmountItem.setLabelFloat(true);
                validatorItemAmount.setMessage("Jumlah item");
                numberValidatorItemAmount.setMessage("Nombor dalam unit sahaja");
                txtAmountItem.getValidators().addAll(validatorItemAmount, numberValidatorItemAmount);

                txtAmountDiscount.setPromptText("Amaun Diskaun");
                txtAmountDiscount.setLabelFloat(true);
                validatorAmount.setMessage("Amaun diskaun");
                numberValidatorAmount.setMessage("Amaun sahaja");
                txtAmountDiscount.getValidators().addAll(validatorAmount, numberValidatorAmount);
                extraVBox.getChildren().addAll(txtName, txtAmountItem, txtAmountDiscount);

                break;
            case 2:
                //how many item
                txtPercentItem.setPromptText("Item");
                txtPercentItem.setLabelFloat(true);

                validatorItemPercent.setMessage("Jumlah item");
                numberValidatorItemPercent.setMessage("Nombor dalam unit sahaja");
                txtPercentItem.getValidators().addAll(validatorItemPercent, numberValidatorItemPercent);

                txtPercentDiscount.setPromptText("Percent Diskaun");
                txtPercentDiscount.setLabelFloat(true);

                validatorPercentDiscount.setMessage("Jumlah peratusan");
                numberValidatorPercentDiscount.setMessage("Nombor dalam unit sahaja");
                txtPercentDiscount.getValidators().addAll(validatorPercentDiscount, numberValidatorPercentDiscount);
                extraVBox.getChildren().addAll(txtName, txtPercentItem, txtPercentDiscount);

                break;
            case 3:
                txtItemFree.setPromptText("Item");
                txtItemFree.setLabelFloat(true);

                extraVBox.getChildren().addAll(txtName, txtItemFree);
                //show two item and chose which one was free
                //set discount % based on item
                break;
            case 17:
                txtItemBox.setPromptText("Item");
                txtItemBox.setLabelFloat(true);

                txtAmountTotalBox.setPromptText("Jumlah amaun keseluruhan");
                txtAmountTotalBox.setLabelFloat(true);

                validatorItemBox.setMessage("Jumlah item");
                validatorAmountTotalBox.setMessage("Jumlah amaun diskaun");
                numberValidatorItemBox.setMessage("Hanya nombor unit");
                numberValidatorAmountTotalBox.setMessage("Hanya amaun sahaja");

                txtItemBox.getValidators().addAll(validatorItemBox,numberValidatorItemBox);
                txtAmountTotalBox.getValidators().addAll(validatorAmountTotalBox, numberValidatorAmountTotalBox);
                extraVBox.getChildren().addAll(txtName, txtItemBox, txtAmountTotalBox);
                break;

        }

//        mainVBox.getChildren().add(1, extraVBox);

        //to inform extra already add
        addExtra = true;
    }

    private void createListType(){
        mapType.put(1, "ItemByAmount"); //buy 2 get discount
        mapType.put(2, "ItemByPercent"); //buy 2 get percent off discount
        mapType.put(3, "ItemByFree"); //buy 2 get free the current
        mapType.put(4, "NextByAmount"); //buy 1 next item get discount
        mapType.put(5, "NextByPercent"); //buy 1 next item get percent off
        mapType.put(6, "NextByFree"); //buy 1 next get free
        mapType.put(7, "DiscountDate"); //discount at certain date
        mapType.put(8, "DiscountFestival"); //discount festival
        mapType.put(9, "DiscountDaytime"); //discount based on daytime
        mapType.put(10, "DiscountByTotalAmount"); //discount amount based on total
        mapType.put(11, "DiscountByTotalPercent"); //discount percent off based on total
        mapType.put(12, "DiscountByQrCode"); //discount based on QR Code
        mapType.put(13, "DiscountMember"); //discount based on Membership
        mapType.put(14, "DiscountWholesaler"); //discount for wholesalers or pemborong
        mapType.put(15, "DiscountSpecialty"); //discount for special case e.g Gift / Reward
        mapType.put(16, "DiscountPointRedeem"); //discount based on Point Redeem
        mapType.put(17, "DiscountBox"); //discount based on Pax e.g  1box /
        mapType.put(18, "DiscountPack"); //discount based on Pax e.g 1pack /
        mapType.put(19, "DiscountCtn"); //discount based on Pax e.g 1carton
    }

}
