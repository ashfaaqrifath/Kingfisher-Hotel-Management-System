package hotel.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddEmployee extends JFrame implements ActionListener{
    
    JTextField tfname,tfage,tfaddress,tfphone,tfnic,tfemail;
    JPasswordField tfpassword,tfcpassword;
    JRadioButton rbmale,rbfemale;
    JComboBox cbjob;
    JButton addemployee,viewemployee,updateemployee,btnback;
    
    AddEmployee(){
        setLayout(null);
        
        JLabel heading = new JLabel("MANAGE EMPLOYEES");
        heading.setBounds(325, 10, 400, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 30));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);
        
        //Lbl for name
        JLabel lblname = new JLabel ("Name");
        lblname.setBounds(60, 90, 120, 30);
        lblname.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblname);
        
        tfname = new JTextField();
        tfname.setBounds(200, 90, 150, 30);
        add(tfname);
        
        //Lbl for age
        JLabel lblage = new JLabel ("Age");
        lblage.setBounds(60, 130, 120, 30);
        lblage.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblage);
        
        tfage = new JTextField();
        tfage.setBounds(200, 130, 150, 30);
        add(tfage);
        
        //Lbl for gender
        JLabel lblgender = new JLabel ("Gender");
        lblgender.setBounds(60, 170, 120, 30);
        lblgender.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblgender);
        
        rbmale = new JRadioButton("Male");
        rbmale.setBounds(200, 170, 70, 30);
        rbmale.setFont(new Font("Arial", Font.PLAIN, 13));
        rbmale.setBackground(Color.WHITE);
        add(rbmale);
        
        rbfemale = new JRadioButton("Female");
        rbfemale.setBounds(280, 170, 70, 30);
        rbfemale.setFont(new Font("Arial", Font.PLAIN, 13));
        rbfemale.setBackground(Color.WHITE);
        add(rbfemale);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbmale);
        bg.add(rbfemale);
        
        //Lbl for Job
        JLabel lbljob = new JLabel ("Job");
        lbljob.setBounds(60, 210, 120, 30);
        lbljob.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lbljob);

        String str[] = { "Admin", "Receptionist","Cleaning Staff","Chef/Cook","Waiter/Waitress","Security Officer","Maintenance Worker"};
        cbjob = new JComboBox(str);
        cbjob.setBounds(200, 210, 150, 30);
        cbjob.setBackground(Color.WHITE);
        add(cbjob);
        
        //Lbl for Address
        JLabel lbladdress = new JLabel ("Address");
        lbladdress.setBounds(60, 250, 120, 30);
        lbladdress.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lbladdress);
        
        tfaddress = new JTextField();
        tfaddress.setBounds(200, 250, 150, 30);
        add(tfaddress);
        
        //Lbl for Phone
        JLabel lblphone = new JLabel ("Phone");
        lblphone.setBounds(60, 290, 120, 30);
        lblphone.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblphone);
        
        tfphone = new JTextField();
        tfphone.setBounds(200, 290, 150, 30);
        add(tfphone);
        
        //Lbl for NIC
        JLabel lblnic = new JLabel ("NIC");
        lblnic.setBounds(60, 330, 120, 30);
        lblnic.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblnic);
        
        tfnic = new JTextField();
        tfnic.setBounds(200, 330, 150, 30);
        add(tfnic);
        
        //Lbl for Email
        JLabel lblemail = new JLabel ("Email");
        lblemail.setBounds(60, 370, 120, 30);
        lblemail.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblemail);
        
        tfemail = new JTextField();
        tfemail.setBounds(200, 370, 150, 30);
        add(tfemail);
        
        JLabel pass = new JLabel("Password");
        pass.setBounds(60, 410, 120, 30);
        pass.setFont(new Font("Arial", Font.PLAIN, 17));
        add(pass);
        
        tfpassword = new JPasswordField();
        tfpassword.setBounds(200, 410, 150, 30);
        add(tfpassword);
        
        JLabel cpass = new JLabel("Confirm PW");
        cpass.setBounds(60, 450, 120, 30);
        cpass.setFont(new Font("Arial", Font.PLAIN, 17));
        add(cpass);
        
        tfcpassword = new JPasswordField();
        tfcpassword.setBounds(200, 450, 150, 30);
        add(tfcpassword);
        
        JLabel passwordNote = new JLabel("* Password required only for Admin or Receptionist roles");
        passwordNote.setForeground(Color.RED);
        passwordNote.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordNote.setBounds(200, 485, 300, 15);
        add(passwordNote);

        //Jbtn Add Employee
        addemployee = new JButton("ADD");
        addemployee.setBackground(new Color(0, 153, 76));
        addemployee.setForeground(Color.WHITE);
        addemployee.setBounds(170, 530, 150, 30);
        addemployee.addActionListener(this);
        add(addemployee);
        
        //Jbtn View Employee
        viewemployee = new JButton("VIEW");
        viewemployee.setBackground(new Color(0, 128, 128));
        viewemployee.setForeground(Color.WHITE);
        viewemployee.setBounds(340, 530, 150, 30);
        viewemployee.addActionListener(this);
        add(viewemployee);
        
        //Jbtn Update Employee
        updateemployee = new JButton("UPDATE");
        updateemployee.setBackground(new Color(255, 165, 0));
        updateemployee.setForeground(Color.WHITE);
        updateemployee.setBounds(510, 530, 150, 30);
        updateemployee.addActionListener(this);
        add(updateemployee);
        
        btnback = new JButton("BACK");
        btnback.setBackground(Color.BLACK);
        btnback.setForeground(Color.WHITE);
        btnback.setBounds(680, 530, 150, 30);
        btnback.addActionListener(this);
        add(btnback);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/tenth.jpg"));
        Image i2 = i1.getImage().getScaledInstance(450, 450, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(420, 90, 450, 370);
        add(image);
        
        
        
        getContentPane().setBackground(Color.WHITE);
        setBounds(265, 115, 1000, 650);
        //setExtenddState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
     public void actionPerformed(ActionEvent ae){
         
         if(ae.getSource() == addemployee) {
         String name = tfname.getText();
         String age = tfage.getText();
         String address = tfaddress.getText();
         String phone = tfphone.getText();
         String nic = tfnic.getText();
         String email = tfemail.getText();
         String password = new String(tfpassword.getPassword());
         String confirmPassword = new String(tfcpassword.getPassword());
         
         LocalDate today = LocalDate.now();
         String dateJoined = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
         String gender = null;
         
         if(name.equals("")){
             JOptionPane.showMessageDialog(null, "Name Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
             return;
         }
         
         if(age.equals("")){
             JOptionPane.showMessageDialog(null, "Age Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
             return;
         }
         
         if (!rbmale.isSelected() && !rbfemale.isSelected()) {
            JOptionPane.showMessageDialog(null, "Gender should be select!");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
            return;
        }
         
         if(phone.equals("")){
             JOptionPane.showMessageDialog(null, "Phone Number Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
             return;
         }
         
         if(address.equals("")){
             JOptionPane.showMessageDialog(null, "Address Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
             return;
         }
         
         if (!isValidPhone(phone)) {
                JOptionPane.showMessageDialog(null, "Invalid phone number. It must be exactly 10 digits.");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
                return;
            }
         
         if (!isValidNIC(nic)) {
                JOptionPane.showMessageDialog(null, "Invalid NIC. Enter a valid 10-character old NIC (e.g., 871234567V) or 12-digit new NIC (e.g., 200087123456).");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
                return;
            }
         
         if (email.isEmpty()) {
             JOptionPane.showMessageDialog(null, "Email field cannot be empty. Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
              ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
            return;
         } else if (!email.contains("@") || !email.contains(".com")) {
             JOptionPane.showMessageDialog(null, "Please enter a valid email address with '@' and '.com'. For example: user@example.com", "Invalid Email", JOptionPane.ERROR_MESSAGE);
              ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
             return;
         }
         
         if(rbmale.isSelected()){
             gender = "Male";
         }else if(rbfemale.isSelected()){
            gender = "Female";
         }
         
         String job = (String) cbjob.getSelectedItem();
         
          if (job.equals("Admin") || job.equals("Receptionist")) {
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match! Please try again.");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee: " + name + " (" + nic + ")", "Failed");
                return;
            }

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password and Confirm Password cannot be empty!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee: " + name + " (" + nic + ")", "Failed");
                return;
            }

            if (password.length() != 6 || confirmPassword.length() != 6) {
                JOptionPane.showMessageDialog(null, "Password and Confirm Password must be exactly 6 characters long!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee: " + name + " (" + nic + ")", "Failed");
                return;
            }
        } else {
            password = "-";
        }
         
            int salary = 0;

            switch (job) {
                case "Admin":
                    salary = 200000;
                    break;
                case "Receptionist":
                    salary = 60000;
                    break;
                case "Cleaning Staff":
                    salary = 40000;
                    break;
                case "Chef/Cook":
                    salary = 65000;
                    break;
                case "Waiter/Waitress":
                    salary = 45000;
                    break;
                case "Security Officer":
                    salary = 50000;
                    break;
                case "Maintenance Worker":
                    salary = 55000;
                    break;
            }
         
         try{
             Conn conn = new Conn();
             
              String checkQuery = "SELECT * FROM employee WHERE nic = '" + nic + "'";
              ResultSet rs = conn.s.executeQuery(checkQuery);
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Employee with this NIC already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
             String query = "insert into employee values ('"+nic+"','"+name+"','"+age+"','"+gender+"','"+job+"','"+address+"','"+phone+"','"+email+"','"+password+"','"+dateJoined+"', '" +salary +"')";
                
             conn.s.executeUpdate(query);
             
             JOptionPane.showMessageDialog(null, "Employee Added Successfully");
             
             tfname.setText("");
             tfage.setText("");
             tfaddress.setText("");
             tfphone.setText("");
             tfnic.setText("");
             tfemail.setText("");
             tfpassword.setText("");
             tfcpassword.setText("");
             rbmale.setSelected(false);
             rbfemale.setSelected(false);
             cbjob.setSelectedIndex(0);
             
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee: " + name + " (" + nic + ")", "Success");
             
         }catch (Exception e){
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee: " + name + " (" + nic + ")", "Failed");
             JOptionPane.showMessageDialog(null, "Error adding employee!", "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         }
         
         }
         
          if(ae.getSource() == viewemployee) {
              setVisible(false);
              new viewemployee();
        }
          if(ae.getSource() == updateemployee) {
              setVisible(false);
              new UpdateEmployee();
        }
          if(ae.getSource() == btnback) {
              setVisible(false);
        }
         
     }
    
     
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
     
    private boolean isValidNIC(String nic) {
        if (nic.length() == 10) {
            return nic.matches("\\d{9}[vVxX]");
        } else if (nic.length() == 12) {
            return nic.matches("\\d{12}");
        }
        return false;
    }
     
    public static void main(String[] args){
        new AddEmployee();
    }
}
