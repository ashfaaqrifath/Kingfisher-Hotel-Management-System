
package hotel.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddRooms extends JFrame implements ActionListener {
  
   JButton add, back; 
   JTextField tfroom, tfprice;
   JComboBox typecombo, availablecombo, cleancombo;
   AddRooms(){
        
       getContentPane().setBackground(Color.WHITE);
       setLayout(null);
   
       JLabel heading = new JLabel("ADD NEW ROOMS");
       heading.setFont(new Font("Arial", Font.BOLD, 30));
       heading.setBounds(400, 30, 300, 30);
       add (heading);
       
       
       JLabel lblRoomno = new JLabel("Room Number");
       lblRoomno.setFont(new Font("Arial", Font.PLAIN, 17));
       lblRoomno.setBounds(52, 120, 120, 20);
       add (lblRoomno);
       
       tfroom = new JTextField();
       tfroom.setBounds(200, 120, 180, 30);
       add(tfroom);
       
       //placeholder
       tfroom.setText(getLastRoomNumber());

       
       JLabel lblavailable = new JLabel("Available");
       lblavailable.setFont(new Font("Arial", Font.PLAIN, 17));
       lblavailable.setBounds(52, 190, 120, 20);
       add (lblavailable);
       
       String availableOptions[] = {"Available", "Not yet"};
       availablecombo = new JComboBox(availableOptions);
       availablecombo.setBounds(200, 190, 180, 30);
       availablecombo.setBackground(Color.WHITE);
       add(availablecombo);
       
       
       JLabel lblclean = new JLabel("Cleaning Status");
       lblclean.setFont(new Font("Arial", Font.PLAIN, 17));
       lblclean.setBounds(52, 260, 120, 20);
       add (lblclean);
       
       String cleanOptions[] = {"Cleaned", "Dirty"};
       cleancombo = new JComboBox(cleanOptions);
       cleancombo.setBounds(200, 260, 180, 30);
       cleancombo.setBackground(Color.WHITE);
       add(cleancombo);
       
       
       JLabel lblprice = new JLabel("Price");
       lblprice.setFont(new Font("Arial", Font.PLAIN, 17));
       lblprice.setBounds(52, 330, 120, 20);
       add (lblprice);
       
       tfprice = new JTextField();
       tfprice.setBounds(200, 330, 180, 30);
       add(tfprice);
       
       
       JLabel lbltype = new JLabel("Bed Type");
       lbltype.setFont(new Font("Arial", Font.PLAIN, 17));
       lbltype.setBounds(52, 400, 120, 20);
       add (lbltype);
       
       String typeOptions[] = {"Single Bed", "Double Bed"};
       typecombo = new JComboBox(typeOptions);
       typecombo.setBounds(200, 400, 180, 30);
       typecombo.setBackground(Color.WHITE);
       add(typecombo);
       
       
       add = new JButton("ADD");
       add.setBounds(250, 520, 120, 30);
       add.setBackground(new Color(0, 153, 76));
       add.setForeground(Color.WHITE);
       add.addActionListener(this);
       add(add);
       
       
       back = new JButton("BACK");
       back.setBounds(590, 520, 120, 30);
       back.setBackground(Color.BLACK);
       back.setForeground(Color.WHITE);
       back.addActionListener(this);
       add(back);
       
       ImageIcon il = new ImageIcon(ClassLoader.getSystemResource("icons/twelve.jpg"));
       JLabel image = new JLabel(il);
        image.setBounds(425, 130, 500, 300);
       add(image);
      
//       setBounds(330, 200, 940, 470);
       setBounds(265, 115, 1000, 650);
       setVisible(true);
   }   
   
   
   //function for last room number 
      public String getLastRoomNumber() {
       String lastRoomNumber = "001"; 
       try {
           Conn conn = new Conn();
           String query = "SELECT roomnumber FROM room ORDER BY roomnumber DESC LIMIT 1";
           ResultSet rs = conn.s.executeQuery(query);
           if (rs.next()) {
               int lastNumber = Integer.parseInt(rs.getString("roomnumber"));
               lastRoomNumber = String.format("%03d", lastNumber + 1); 
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
       return lastRoomNumber;
   }
   public void actionPerformed(ActionEvent ae){
       if (ae.getSource()== add){
           String roomnumber = tfroom.getText().trim();
           String availability = (String) availablecombo.getSelectedItem();
           String status = (String) cleancombo.getSelectedItem();
           String price = tfprice.getText();
           String type = (String) typecombo.getSelectedItem();
           
           
           if (!roomnumber.matches("\\d{3}")) {  
            JOptionPane.showMessageDialog(null, "Room number must be exactly 3 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
           if(price.equals("")){
             JOptionPane.showMessageDialog(null, "Price Should Not Be Empty!");
             ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add new room", "Failed");
             return;
         }
           
           try {
            Conn conn = new Conn();
               
            String checkQuery = "SELECT COUNT(*) FROM room WHERE roomnumber = ?";
            PreparedStatement checkStmt = conn.c.prepareStatement(checkQuery);
            checkStmt.setString(1, roomnumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "Room number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            // Insert the new room if it's unique
            String insertQuery = "INSERT INTO room VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.c.prepareStatement(insertQuery);
            insertStmt.setString(1, roomnumber);
            insertStmt.setString(2, availability);
            insertStmt.setString(3, status);
            insertStmt.setString(4, price);
            insertStmt.setString(5, type);

            insertStmt.executeUpdate();         
            JOptionPane.showMessageDialog(null, "New Room Added Successfully");
            ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add room ( Room no: " + roomnumber + ")", "Success");
            
            tfroom.setText(getLastRoomNumber()); // Reset to placeholder or last room number
            availablecombo.setSelectedIndex(0); // Reset to first option ("Available")
            cleancombo.setSelectedIndex(0);     // Reset to first option ("Cleaned")
            tfprice.setText("");                // Clear price field
            typecombo.setSelectedIndex(0); 
   
           }catch(Exception e){
               e.printStackTrace();
               ActivityLogger.log(Session.loggedNIC, Session.loggedName, Session.loggedRole,"Attempted to add room ( Room no: " + roomnumber + ")", "Failed");
           }
           
       }else if (ae.getSource() == back) {
            setVisible(false);
            new SearchRoom();
        }
   }
   
   public static void main(String [] args){
       new AddRooms();
   } 
}
