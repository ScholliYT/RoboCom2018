package pc.ui;

import javax.swing.JDialog;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

import pc.object.DataFieldType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class NewRowDialog extends JDialog{
	
	private static final long serialVersionUID = -4502546001590783751L;
	
	private static NewRowDialog SINGLETONE;
	
	private JTextField tfDatafieldName;
	private JTextField tfValue;
	private JComboBox<String> cbDataType;
	private JButton btnConfirm;
	private JButton btnCancel;
	private JSpinner spinner;
	private SpinnerNumberModel modelInteger, modelLong, modelFloat, modelDouble;
	
	private boolean editMode;
	private int row;
	private JComboBox<String> cbBooleans;
	
	public NewRowDialog(NXTCommunicationFrame parent){
		this.row = -1;
		this.editMode = false;
		setIconImage(Toolkit.getDefaultToolkit().getImage(NewRowDialog.class.getResource("/resources/add_new_icon_16px.png")));
		setTitle("Neues Datenfeld erzeugen");
		setResizable(false);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setType(Type.POPUP);
		setBounds(100, 100, 380, 150);
		getContentPane().setLayout(null);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 86, 374, 2);
		getContentPane().add(separator);
		
		btnConfirm = new JButton("\u00DCbernehmen");
		btnConfirm.setToolTipText("Daten dieses Dialoges in ein neues Datenfeld \u00FCbertragen");
		btnConfirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				DataFieldType type = DataFieldType.getDataFieldTypeFromString((String) cbDataType.getSelectedItem());
				if(editMode){
					if(tfDatafieldName.getText().equals(parent.getModel().getValueAt(row, 0))){
						parent.getModel().setValueAt(tfDatafieldName.getText(), row, 0);
					}else if(parent.isDatafieldNameAvailable(tfDatafieldName.getText())){
						parent.getModel().setValueAt(tfDatafieldName.getText(), row, 0);
					}else{
						return;
					}
					parent.getModel().setValueAt(type, row, 1);
					
					if(type == DataFieldType.STRING){
						parent.getModel().setValueAt(tfValue.getText(), row, 2);
					}else if(type == DataFieldType.BOOLEAN){
						parent.getModel().setValueAt(cbBooleans.getSelectedIndex() == 0 ? true : false, row, 2);
					}else{
						parent.getModel().setValueAt((Number) spinner.getValue(), row, 2);
					}
					
					parent.getTable().updateUI();
					parent.showWarning();
					setVisible(false);
				}else if(parent.isDatafieldNameAvailable(tfDatafieldName.getText())){
					if(type == DataFieldType.STRING){
						parent.addNewRow(tfDatafieldName.getText(), type, tfValue.getText());
					}else if(type == DataFieldType.BOOLEAN){
						parent.addNewRow(tfDatafieldName.getText(), type, Boolean.parseBoolean((String) cbBooleans.getSelectedItem()));
					}else{
						parent.addNewRow(tfDatafieldName.getText(), type, (Number) spinner.getValue());
					}
					parent.showWarning();
					setVisible(false);
				}
			}
		});
		btnConfirm.setBounds(116, 92, 119, 23);
		btnConfirm.setFocusPainted(false);
		getContentPane().add(btnConfirm);
		
		btnCancel = new JButton("Abbrechen");
		btnCancel.setToolTipText("Das Erstellen eines neuen Datenfeldes abbrechen");
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});
		btnCancel.setBounds(245, 92, 119, 23);
		btnCancel.setFocusPainted(false);
		getContentPane().add(btnCancel);
		
		JLabel lblDatenfeldname = new JLabel("Datenfeldname:");
		lblDatenfeldname.setBounds(10, 11, 112, 14);
		getContentPane().add(lblDatenfeldname);
		
		JLabel lblDatentyp = new JLabel("Datentyp:");
		lblDatentyp.setBounds(10, 36, 112, 14);
		getContentPane().add(lblDatentyp);
		
		JLabel lblWert = new JLabel("Wert:");
		lblWert.setBounds(10, 61, 112, 14);
		getContentPane().add(lblWert);
		
		tfDatafieldName = new JTextField();
		tfDatafieldName.setToolTipText("Geben Sie hier einen Datenfeldnamen ein. Dieser darf noch nicht existieren");
		tfDatafieldName.setText("(Datenfeldname)");
		tfDatafieldName.setHorizontalAlignment(SwingConstants.RIGHT);
		tfDatafieldName.setBounds(132, 8, 232, 20);
		getContentPane().add(tfDatafieldName);
		tfDatafieldName.setColumns(10);
		
		cbDataType = new JComboBox<>();
		cbDataType.setToolTipText("Geben Sie hier den Datentyp des neuen Datenfeldes an");
		cbDataType.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				switch(DataFieldType.getDataFieldTypeFromString((String) cbDataType.getSelectedItem())){
				case DOUBLE:
					tfValue.setVisible(false);
					spinner.setModel(modelDouble);
					spinner.setVisible(true);
					cbBooleans.setVisible(false);
					break;
				case FLOAT:
					tfValue.setVisible(false);
					spinner.setModel(modelFloat);
					spinner.setVisible(true);
					cbBooleans.setVisible(false);
					break;
				case INTEGER:
					tfValue.setVisible(false);
					spinner.setModel(modelInteger);
					spinner.setVisible(true);
					cbBooleans.setVisible(false);
					break;
				case LONG:
					tfValue.setVisible(false);
					spinner.setModel(modelLong);
					spinner.setVisible(true);
					cbBooleans.setVisible(false);
					break;
				case STRING:
					spinner.setVisible(false);
					tfValue.setVisible(true);
					cbBooleans.setVisible(false);
					break;
				case BOOLEAN:
					spinner.setVisible(false);
					tfValue.setVisible(false);
					cbBooleans.setVisible(true);
				default:
					break;
				}
			}
		});
		cbDataType.setModel(new DefaultComboBoxModel<>(new String[] {"String", "Integer", "Long", "Double", "Float", "Boolean"}));
		cbDataType.setFocusable(false);
		cbDataType.setBounds(132, 33, 232, 20);
		getContentPane().add(cbDataType);
		
		tfValue = new JTextField();
		tfValue.setToolTipText("Geben Sie hier den Wert f\u00FCr das Datenfeld ein");
		tfValue.setText("(Datenfeldwert)");
		tfValue.setHorizontalAlignment(SwingConstants.RIGHT);
		tfValue.setBounds(132, 58, 232, 20);
		getContentPane().add(tfValue);
		tfValue.setColumns(10);
		
		spinner = new JSpinner();
		spinner.setToolTipText("Geben Sie hier den Wert f\u00FCr das Datenfeld ein");
		spinner.setVisible(false);
		spinner.setBounds(132, 58, 232, 20);
		getContentPane().add(spinner);
		
		cbBooleans = new JComboBox<>();
		cbBooleans.setFocusable(false);
		cbBooleans.setModel(new DefaultComboBoxModel<>(new String[] {"True", "False"}));
		cbBooleans.setBounds(132, 58, 232, 20);
		getContentPane().add(cbBooleans);
		
		this.modelInteger = new SpinnerNumberModel(new Integer(0), null, null, 1);
		this.modelLong = new SpinnerNumberModel(new Long(0L), null, null, 1);
		this.modelDouble = new SpinnerNumberModel(new Double(0.0D), null, null, 0.1);
		this.modelFloat = new SpinnerNumberModel(new Float(0.0F), null, null, 0.1F);
		
	}
	
	public static void showDialog(NXTCommunicationFrame parent, String name, DataFieldType type, Object value, int row) throws NumberFormatException{
		if(SINGLETONE == null){
			SINGLETONE = new NewRowDialog(parent);
		}
		SINGLETONE.setTitle("Datenfeld bearbeiten");
		SINGLETONE.editMode = true;
		SINGLETONE.row = row;
		SINGLETONE.tfDatafieldName.setText(name);
		
		int index = 0;
		boolean isString = (type == DataFieldType.STRING);
		boolean isBoolean = (type == DataFieldType.BOOLEAN);
		SINGLETONE.spinner.setVisible(!isString && !isBoolean);
		SINGLETONE.tfValue.setVisible(isString && !isBoolean);
		SINGLETONE.cbBooleans.setVisible(isBoolean && !isString);
		
		switch(type){
			case STRING:
				SINGLETONE.tfValue.setText((String) value);
				break;
			case INTEGER:
				index = 1;
				SINGLETONE.spinner.setModel(SINGLETONE.modelInteger);
				break;
			case LONG:
				index = 2;
				SINGLETONE.spinner.setModel(SINGLETONE.modelLong);
				break;
			case DOUBLE:
				index = 3;
				SINGLETONE.spinner.setModel(SINGLETONE.modelDouble);
				break;
			case FLOAT:
				index = 4;
				SINGLETONE.spinner.setModel(SINGLETONE.modelFloat);
				break;
			case BOOLEAN:
				index = 5;
				SINGLETONE.cbBooleans.setSelectedIndex(Boolean.parseBoolean(value + "") ? 0 : 1);
			default:
				break;
		}
		
		if(!isString && !isBoolean){
			SINGLETONE.spinner.setValue(value);
		}
		
		SINGLETONE.cbDataType.setSelectedIndex(index);
		SINGLETONE.setLocationRelativeTo(parent);
		SINGLETONE.setVisible(true);
	}
	
	public static void showDialog(NXTCommunicationFrame parent){
		if(SINGLETONE == null){
			SINGLETONE = new NewRowDialog(parent);
		}
		SINGLETONE.setTitle("Neues Datenfeld erzeugen");
		SINGLETONE.editMode = false;
		SINGLETONE.setLocationRelativeTo(parent);
		SINGLETONE.setVisible(true);
	}
}