package hotel.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class UpdateEmployee extends JFrame implements ActionListener{
    
    JTextField searchField,tfage,tfname,tfaddress,tfphone,tfemail;
    JPasswordField tfpassword,tfcpassword;
    JButton update,search,delete,back;
    JRadioButton rbmale,rbfemale;
    JComboBox cbjob;
    String id = "";
    
    UpdateEmployee(){
        
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);
        
        JLabel heading = new JLabel("EDIT EMPLOYEES");
        heading.setBounds(325, 10, 400, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setForeground(new Color(30, 30, 30));
        heading.setHorizontalAlignment(JLabel.CENTER);
        add(heading);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/tenth.jpg"));
        Image i2 = i1.getImage().getScaledInstance(450, 450, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(420, 90, 450, 370);
        add(image);
        
        
        JLabel lblnic = new JLabel ("Enter NIC");
        lblnic.setBounds(60, 90, 120, 30);
        lblnic.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblnic);
        
        searchField = new JTextField("      Enter NIC to Search");
        searchField.setBounds(200, 90, 150, 30);
        searchField.setForeground(Color.GRAY);
        
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getForeground().equals(Color.GRAY)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Enter NIC to Search");
                }
            }
        });

        
        add(searchField);
        
        
        JLabel lblage = new JLabel ("Age");
        lblage.setBounds(60, 170, 120, 30);
        lblage.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblage);
        
        tfage = new JTextField();
        tfage.setBounds(200, 170, 150, 30);
        add(tfage);
        
        //lbl for Gender
        JLabel lblgender = new JLabel ("Gender");
        lblgender.setBounds(60, 210, 120, 30);
        lblgender.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblgender);
        
        rbmale = new JRadioButton("Male");
        rbmale.setBounds(200, 210, 70, 30);
        rbmale.setFont(new Font("Arial", Font.PLAIN, 13));
        rbmale.setBackground(Color.WHITE);
        add(rbmale);
        
        rbfemale = new JRadioButton("Female");
        rbfemale.setBounds(280, 210, 70, 30);
        rbfemale.setFont(new Font("Arial", Font.PLAIN, 13));
        rbfemale.setBackground(Color.WHITE);
        add(rbfemale);
        
//        ButtonGroup bg = new ButtonGroup();
//        bg.add(rbmale);
//        bg.add(rbfemale);
        
         //Lbl for Job
        JLabel lbljob = new JLabel ("Job");
        lbljob.setBounds(60, 250, 120, 30);
        lbljob.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lbljob);
        
        String str[] = { "Admin", "Receptionist","Cleaning Staff","Chef/Cook","Waiter/Waitress","Security Officer","Maintenance Worker"};
        cbjob = new JComboBox(str);
        cbjob.setBounds(200, 250, 150, 30);
        cbjob.setBackground(Color.WHITE);
        add(cbjob);
        
        //Lbl for name
        JLabel lblname = new JLabel ("Name");
        lblname.setBounds(60, 130, 120, 30);
        lblname.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblname);
        
        tfname = new JTextField();
        tfname.setBounds(200, 130, 150, 30);
        add(tfname);
        
        //Lbl for Address
        JLabel lbladdress = new JLabel ("Address");
        lbladdress.setBounds(60, 290, 120, 30);
        lbladdress.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lbladdress);
        
        tfaddress = new JTextField();
        tfaddress.setBounds(200, 290, 150, 30);
        add(tfaddress);
        
        //Lbl for Phone
        JLabel lblphone = new JLabel ("Phone");
        lblphone.setBounds(60, 330, 120, 30);
        lblphone.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblphone);
        
        tfphone = new JTextField();
        tfphone.setBounds(200, 330, 150, 30);
        add(tfphone);
        
        //Lbl for Email
        JLabel lblemail = new JLabel ("Email");
        lblemail.setBounds(60, 370, 120, 30);
        lblemail.setFont(new Font("Arial", Font.PLAIN, 17));
        add(lblemail);
        
        tfemail = new JTextField();
        tfemail.setBounds(200, 370, 150, 30);
        add(tfemail);
        
        //Lbl for Password
        JLabel pass = new JLabel("Password");
        pass.setBounds(60, 410, 120, 30);
        pass.setFont(new Font("Arial", Font.PLAIN, 17));
        add(pass);
        
        tfpassword = new JPasswordField();
        tfpassword.setBounds(200, 410, 150, 30);
        add(tfpassword);
        
        JLabel cpass = new JLabel("Confirm Pw");
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
        
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbmale);
        genderGroup.add(rbfemale);
        
        
        //Buttons
        search = new JButton("SEARCH");
        search.setBackground(Color.BLUE);
        search.setForeground(Color.WHITE);
        search.setBounds(170, 530, 150, 30);
        search.addActionListener(this);
        add(search);
        
        update = new JButton("UPDATE");
        update.setBackground(new Color(255, 165, 0));
        update.setForeground(Color.WHITE);
        update.setBounds(340, 530, 150, 30);
        update.addActionListener(this);
        add(update);
        
        delete = new JButton("DELETE");
        delete.setBackground(Color.RED);
        delete.setForeground(Color.WHITE);
        delete.setBounds(510, 530, 150, 30);
        delete.addActionListener(this);
        add(delete);
        
        back = new JButton("BACK");
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.setBounds(680, 530, 150, 30);
        back.addActionListener(this);
        add(back);
                
        setBounds(265, 115, 1000, 650);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae){
        
        String searchNIC = null;
        
        if (ae.getSource() == search) {
            searchNIC = searchField.getText().trim();
            
             if (searchNIC.isEmpty() || searchNIC.equalsIgnoreCase("Enter NIC to Search")) {
                    JOptionPane.showMessageDialog(this, "Please enter a NIC to search.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search employee", "Failed");
                    return;
            }
            
            String query = "SELECT * FROM employee WHERE nic = '" + searchNIC + "'";
            
            try {
                Conn c = new Conn();
                ResultSet rs = c.s.executeQuery(query);
                
                if (rs.next()) {
                    id = rs.getString("nic"); 

                    tfname.setText(rs.getString("name"));
                    tfage.setText(rs.getString("age"));
                    tfaddress.setText(rs.getString("address"));
                    tfphone.setText(rs.getString("phone"));
                    tfemail.setText(rs.getString("email"));
                    
                    String password = rs.getString("password");
                    tfpassword.setText(password);
                    tfcpassword.setText(password);
                    
                    String gender = rs.getString("gender");
                    rbmale.setSelected("Male".equals(gender));
                    rbfemale.setSelected("Female".equals(gender));

                    cbjob.setSelectedItem(rs.getString("job"));
                    
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search employee ( NIC: " + searchNIC + ")", "Success");
                } else {
                    JOptionPane.showMessageDialog(null, "Employee not found.");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search employee ( NIC: " + searchNIC + ")", "Not Found");
                }                           
            } catch (Exception e){
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to search employee ( NIC: " + searchNIC + ")", "Failed");
                        e.printStackTrace();
            }
             
        } else if(ae.getSource() == update) {
            
            searchNIC = searchField.getText().trim();
            
            String name = tfname.getText();
            String age = tfage.getText();
            String address = tfaddress.getText();
            String phone = tfphone.getText();
            String email = tfemail.getText();
            String password = new String(tfpassword.getPassword());
            String confirmPassword = new String(tfcpassword.getPassword());
            String gender = null;
            
            if(name.equals("")){
                JOptionPane.showMessageDialog(null, "Name Should Not Be Empty!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee", "Failed");
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
            
            if(address.equals("")){
             JOptionPane.showMessageDialog(null, "Address Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
             return;
            }
            
            if(phone.equals("")){
                JOptionPane.showMessageDialog(null, "Phone Number Should Not Be Empty!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee", "Failed");
                return;
            }
            
            if (!isValidPhone(phone)) {
                JOptionPane.showMessageDialog(null, "Invalid phone number. It must be exactly 10 digits.");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new employee", "Failed");
                return;
            }
            
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Email field cannot be empty. Please enter a valid email address.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee", "Failed");
                return;
            } else if (!email.contains("@") || !email.contains(".com")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid email address with '@' and '.com'. For example: user@example.com", "Invalid Email", JOptionPane.ERROR_MESSAGE);
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee", "Failed");
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
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee: " + name + " (" + searchNIC + ")", "Failed");
                return;
            }

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password and Confirm Password cannot be empty!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee: " + name + " (" + searchNIC + ")", "Failed");
                return;
            }

            if (password.length() != 6 || confirmPassword.length() != 6) {
                JOptionPane.showMessageDialog(null, "Password and Confirm Password must be exactly 6 characters long!");
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee: " + name + " (" + searchNIC + ")", "Failed");
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
            
            try {
                Conn c = new Conn();
                c.s.executeUpdate("UPDATE employee SET "
                                    + "name = '"+name+"', "
                                    + "age = '"+age+"', "
                                    + "gender = '"+gender+"', "
                                    + "job = '"+job+"', "
                                    + "address = '"+address+"', "
                                    + "phone = '"+phone+"', "
                                    + "email = '"+email+"', "
                                    + "password = '"+password+"' "
                                    + "WHERE nic = '"+id+"'");
                
                JOptionPane.showMessageDialog(null, "Employee Updated Successfully");
                
                tfname.setText("");
                tfage.setText("");
                tfaddress.setText("");
                tfphone.setText("");
                tfemail.setText("");
                tfpassword.setText("");
                tfcpassword.setText("");
                rbmale.setSelected(false);
                rbfemale.setSelected(false);
                cbjob.setSelectedIndex(0);
                
                 ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee: " + name + " (" + searchNIC + ")", "Success");
                
            }catch (Exception e){
                
                ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to update employee: " + name + " (" + searchNIC + ")", "Failed");
                
                JOptionPane.showMessageDialog(null, "Employee Updated Failed");
                 e.printStackTrace();
            }
         
        }
        
          else if (ae.getSource() == delete) {
             
            searchNIC = searchField.getText().trim();
            String name = tfname.getText();
            
                if(searchNIC.isEmpty() || searchNIC.equalsIgnoreCase("Enter NIC to Search") || name.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter correct NIC of Employee to delete.");
                    ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to delete employee", "Failed");
                    return;
                }
                
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this employee?", "Delete Employee", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    try {
                        Conn c = new Conn();
                        c.s.executeUpdate("DELETE FROM employee WHERE nic = '" + id + "'");

                        JOptionPane.showMessageDialog(null, "Employee Deleted Successfully");

                            tfname.setText("");
                            tfage.setText("");
                            tfaddress.setText("");
                            tfphone.setText("");
                            tfemail.setText("");
                            tfpassword.setText("");
                            tfcpassword.setText("");
                            rbmale.setSelected(false);
                            rbfemale.setSelected(false);
                            cbjob.setSelectedIndex(0);
                            id = "";
                            
                            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to delete employee: " + name + " (" + searchNIC + ")", "Success");
                            
                    } catch (Exception e) {
                            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempt to delete employee", "Failed");
                            JOptionPane.showMessageDialog(null, "Employee Delete failed");
                            e.printStackTrace();
                    }
                }
         }   

        
        else{
            setVisible(false);
            new AddEmployee();
        }
    }
    
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
     
    
    public static void main(String[] args){
        new UpdateEmployee();
    }
    
}
