package sample.packages;

import TraveExDB.TravelPackage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sample.myActionListener;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class PackageForm extends VBox {

    @FXML private TextField txtPackageId;
    @FXML private TextField txtPkgName;
    @FXML private TextArea txtPkgDesc;
    @FXML private DatePicker dpPkgStartDate;
    @FXML private Button btnClearStartDate;
    @FXML private DatePicker dpPkgEndDate;
    @FXML private Button btnClearEndDate;
    @FXML private TextField txtPkgBasePrice;
    @FXML private TextField txtPkgAgencyCommission;

    private SimpleObjectProperty<TravelPackage> travelPackage = new SimpleObjectProperty<>(this, "travelPackage");
    private myActionListener actionListener;

    PackageForm() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("packageForm.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //Set date clear buttons
        btnClearStartDate.setOnAction((event) -> clearDatePicker(dpPkgStartDate));
        btnClearEndDate.setOnAction((event) -> clearDatePicker(dpPkgEndDate));
        
        //Set "onFieldChanged" listeners
        TextInputControl[] textControls = {txtPackageId, txtPkgName, txtPkgDesc, txtPkgBasePrice, txtPkgAgencyCommission};
        for (TextInputControl txt:textControls)
            txt.textProperty().addListener(this::onFieldChanged);
        DatePicker[] datePickers = {dpPkgStartDate, dpPkgEndDate};
        for (DatePicker dp : datePickers)
            dp.valueProperty().addListener(this::onFieldChanged);
    }

    private void clearDatePicker(DatePicker datePicker) {
        datePicker.setValue(null);
    }

    private void onFieldChanged(ObservableValue observable, Object oldValue, Object newValue) {
        if (actionListener != null)
            actionListener.onAction();
    }

    TravelPackage getTravelPackage() {
        String validationResponse = checkFields();
        if ( validationResponse != ""){
            Alert alert = new Alert(Alert.AlertType.WARNING, validationResponse, ButtonType.OK);
            alert.show();
        }

        TravelPackage p = new TravelPackage();
        if (txtPackageId.getText().isEmpty())
            p.setPackageId(null);
        else
            p.setPackageId(Integer.parseInt(txtPackageId.getText()));
        p.setPkgName(txtPkgName.getText());
        p.setPkgDesc(txtPkgDesc.getText() );
        if (p.getPkgDesc()==null) p.setPkgDesc(null);
        p.setPkgStartDate(dateFromLocal(dpPkgStartDate.getValue()));
        p.setPkgEndDate(dateFromLocal(dpPkgEndDate.getValue()));
        p.setPkgBasePrice( Float.parseFloat(txtPkgBasePrice.getText()) );
        p.setPkgAgencyCommission(txtPkgAgencyCommission.getText().equals("") ?
                null :
                Float.parseFloat(txtPkgAgencyCommission.getText()) );

        return p;
    }

    private String checkFields() {
        // Check that required fields are filled
        if (txtPkgName.getText().isEmpty()) return "Package name cannot be empty";
        if (txtPkgBasePrice.getText().isEmpty()) return "Base price field cannot be empty";
        // NOTE: We don't need to worry about the ID since it's not editable

        // Check that description is not too long
        if (txtPkgDesc.getText().length() >= 50) return "Package description cannot be more than 50 characters";

        // Check that End date is not before Start date
        LocalDate startDate = dpPkgStartDate.getValue();
        LocalDate endDate = dpPkgEndDate.getValue();
        if (startDate != null && endDate != null && startDate.isAfter(endDate) )
            return "Package end date must be after start date";

        // Check that price and commission are numbers
        try {
            Double.parseDouble(txtPkgBasePrice.getText());
            if (!txtPkgBasePrice.getText().isEmpty())
                Double.parseDouble(txtPkgBasePrice.getText());

        } catch (NumberFormatException exception) {
            return "Base and Commission must be numbers";
        }

        return "";
    }

    public SimpleObjectProperty<TravelPackage> travelPackageProperty() {
        return travelPackage;
    }

    public void setTravelPackage(TravelPackage travelPackage) {
        this.travelPackage.set(travelPackage);

        txtPackageId.setText( String.valueOf(travelPackage.getPackageId()) );
        txtPkgName.setText( travelPackage.getPkgName() );
        txtPkgDesc.setText( travelPackage.getPkgDesc() );
        dpPkgStartDate.setValue( localFromDate(travelPackage.getPkgStartDate()) );
        dpPkgEndDate.setValue( localFromDate(travelPackage.getPkgEndDate()) );
        txtPkgBasePrice.setText( String.format("%.2f", travelPackage.getPkgBasePrice()) );
        txtPkgAgencyCommission.setText( travelPackage.getPkgAgencyCommission() == null ?
                "" :
                String.format("%.2f", travelPackage.getPkgAgencyCommission()) );
    }

    private Date dateFromLocal(LocalDate localDate) {
        if (localDate == null) return null;

        return Date.valueOf(localDate);
    }

    private LocalDate localFromDate(Date date) {
        if (date == null) return null;

        return date.toLocalDate();
    }

    void clear() {
        txtPackageId.clear();
        txtPkgName.clear();
        txtPkgDesc.clear();
        dpPkgStartDate.setValue(null);
        dpPkgEndDate.setValue(null);
        txtPkgBasePrice.clear();
        txtPkgAgencyCommission.clear();
    }

    public void setActionListener(myActionListener actionListener) {
        this.actionListener = actionListener;
    }
}
