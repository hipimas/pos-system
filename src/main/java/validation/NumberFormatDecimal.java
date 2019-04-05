package validation;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.math.BigDecimal;

public class NumberFormatDecimal extends ValidatorBase {


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
            if (!text.isEmpty())
                new BigDecimal(text);
//                Integer.parseInt(text);
        } catch (Exception e) {
            hasErrors.set(true);
        }
    }

}
