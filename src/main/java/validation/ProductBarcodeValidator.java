package validation;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;
import model.ProductModel;

public class ProductBarcodeValidator extends ValidatorBase {

    private ProductModel productModel = new ProductModel();

    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        String text = textField.getText();
        try {
            hasErrors.set(false);
            if (!text.isEmpty()){
                boolean check = productModel.checkProductByBarcode(text.toLowerCase().trim());
                if(check){
                    throw new Exception();
                }
            }

        } catch (Exception e) {
            hasErrors.set(true);
        }
    }
}
