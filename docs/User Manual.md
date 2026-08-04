# Kingfisher Hotel Management System User Manual

Version: 1.0  
Prepared for: Kingfisher Beach Resort  
System name shown in the app: Kingfisher Hotel Management System, or Kingfisher HMS

## 1. Purpose of This Manual

This manual explains how hotel staff and managers can use the Kingfisher Hotel Management System in day-to-day work.

The system helps the hotel manage:

- Room availability and room prices
- Guest records
- Bookings, check-ins, check-outs, cancellations, and invoices
- Inventory and low-stock alerts
- Employee records
- User accounts and access roles
- Activity history for important actions
- PDF reports from the main record pages

This guide is written for non-technical users. You do not need programming knowledge to use the system.

## 2. Before You Start

Use the system in a modern web browser such as Google Chrome, Microsoft Edge, or Mozilla Firefox.

You will need:

- The system website link, provided by the administrator or developer
- Your email address
- Your password
- Permission to access the system

If you do not have an account, ask the owner or an administrator to create one for you.

## 3. User Roles and Access

The system has three user roles.

### Staff

Staff users can access the main operational areas:

- Dashboard
- Bookings
- Rooms
- Guests
- Inventory

Staff users can manage day-to-day hotel records in these areas, including bookings, rooms, guest records, and inventory.

### Admin

Admin users can access everything Staff users can access, plus:

- Employees
- Activity Log
- Users

Admins can also delete cancelled bookings and create new system user profiles.

### Owner

The Owner has the highest access level. In addition to Admin access, the Owner can:

- Edit existing user profiles
- Change user roles
- Delete user profiles, except their own account and the owner account
- Delete activity log entries

Only one Owner account should exist in the system.

## 4. Signing In

1. Open the Kingfisher HMS website in your browser.
2. The Sign in page will appear.
3. Enter your authorized email address.
4. Enter your password.
5. Select **Sign in**.

If the details are correct, the Dashboard will open.

If the login fails, check that:

- The email address is typed correctly
- The password is typed correctly
- Your account has been created by an administrator or owner

For security, the system automatically signs you out after about 30 minutes of inactivity.

## 5. Signing Out

To safely leave the system:

1. Look at the bottom of the left-side menu.
2. Select **Log out**.

Always log out when using a shared computer.

## 6. Main Screen Layout

After signing in, the system shows a left-side menu and a main work area.

The left-side menu is used to move between pages:

- **Dashboard** - overview of hotel operations
- **Bookings** - reservations, check-ins, check-outs, invoices, and cancellations
- **Rooms** - room list, prices, and room status
- **Guests** - guest details
- **Employees** - staff records, available to Admin and Owner users
- **Inventory** - stock items and low-stock tracking
- **Activity Log** - audit history, available to Admin and Owner users
- **Users** - system user accounts, available to Admin and Owner users

If you do not see a page in the menu, your account does not have permission to use that page.

## 7. Dashboard

The Dashboard gives a quick overview of hotel activity.

It shows:

- **Occupancy Rate** - percentage of rooms currently marked as Occupied
- **Active Bookings** - bookings with Booked or Checked In status
- **Revenue this month** - total booking amount for bookings created during the current month
- **Low Stock Alerts** - number of inventory items at or below their low-stock level
- **Bookings - last 7 days** - chart showing recent booking activity
- **Room status split** - chart showing Available, Occupied, and Maintenance rooms
- **Monthly Revenue Trend** - chart showing revenue over recent months
- **Upcoming checkouts** - bookings checking out within the next 3 days
- **Inventory snapshot** - current quantity of selected inventory items
- **Low stock alerts** - low-stock items shown in chart form
- **Employee status mix** - employee status chart, such as Active or On Leave

Use the Dashboard at the start of the day to quickly check occupancy, upcoming checkouts, stock warnings, and overall activity.

## 8. Bookings

Use **Bookings** to create reservations, check guests in, check guests out, cancel bookings, and export booking reports.

### 8.1 Booking Statuses

A booking can have one of these statuses:

- **Booked** - reservation has been created, but the guest has not checked in
- **Checked In** - guest is currently staying
- **Checked Out** - guest has completed the stay
- **Cancelled** - booking was cancelled

### 8.2 Create a New Booking

1. Select **Bookings** from the left-side menu.
2. Select **+ New Booking**.
3. In the **Guest details** section, either choose an existing guest or enter a new guest.

To choose an existing guest:

1. Type the guest's name, phone number, email, or NIC in **Search existing guest**.
2. Select the matching guest from the list.
3. The guest details will be filled in automatically.

To add a new guest during booking:

1. Leave **Search existing guest** blank.
2. Enter the guest's full name.
3. Enter the guest's email address.
4. Enter the guest's phone number.
5. Enter the guest's NIC.
6. Select the guest's gender.
7. Enter the guest's address.

Then complete the **Booking details** section:

1. Select an available room from **Room (available only)**.
2. Select the **Check-in** date.
3. Select the **Check-out** date.
4. Enter a **Discount amount (LKR)** if needed. Leave it blank or enter 0 if there is no discount.
5. Check the **Total amount (LKR)**. The system calculates it automatically.
6. Select **Create booking**.

The total is calculated from:

Room price per night x number of nights - discount amount

### 8.3 Important Booking Rules

The system checks bookings before saving.

- A guest must be selected or full guest details must be entered.
- Full names must contain letters and spaces only and must be at least 3 characters long.
- Email addresses must be in a valid email format.
- Phone numbers must contain exactly 10 digits.
- NIC and address are required when creating a new guest inside a booking.
- Check-out date must be after check-in date.
- Only currently available rooms can be selected.
- A room cannot be booked for overlapping active stay dates.
- Discount amount cannot be negative.
- The total amount must be greater than 0.

If there is a problem, the system shows a message explaining what must be corrected.

### 8.4 Search and Filter Bookings

On the Bookings page, you can:

- Search by guest name or room number
- Filter by booking status
- Hide one selected status from the list

For example, you can show only **Checked In** bookings, or hide **Cancelled** bookings.

### 8.5 Check In a Guest

1. Go to **Bookings**.
2. Find the booking.
3. If the booking status is **Booked**, select **Check in**.

The **Check in** button is only available on or after the booking's check-in date. Future bookings cannot be checked in early from the system.

When a booking is checked in, the room is marked as **Occupied**.

### 8.6 Check Out a Guest

1. Go to **Bookings**.
2. Find the booking with **Checked In** status.
3. Select **Check out**.

When a booking is checked out, the room is made available again.

### 8.7 Cancel a Booking

1. Go to **Bookings**.
2. Find a booking with **Booked** or **Checked In** status.
3. Select **Cancel**.

When a booking is cancelled, the room is made available again.

### 8.8 Delete a Cancelled Booking

Only Admin and Owner users can delete cancelled bookings.

1. Go to **Bookings**.
2. Find a booking with **Cancelled** status.
3. Select **Delete**.
4. Confirm the deletion.

Use deletion carefully. A deleted booking is removed from the booking list.

### 8.9 View and Export an Invoice

1. Go to **Bookings**.
2. Find the booking.
3. Select **Invoice**.
4. Review the invoice details.
5. Select **Export PDF** to download the invoice.

Invoices include guest details, room details, check-in and check-out dates, number of nights, room rate, base total, discount, and total due.

The **Invoice** button is not shown for cancelled bookings.

### 8.10 Export Booking Reports

On the Bookings page:

- Select **Export PDF** to download a formatted PDF report.

The exported booking report uses the currently visible filtered list. If you search or filter the page first, the export will follow that same list.

The booking report includes guest, room, check-in date, check-out date, status, and total amount.

## 9. Rooms

Use **Rooms** to manage room numbers, room types, prices, and room status.

### 9.1 Room Statuses

Rooms can appear with these statuses:

- **Available** - room is free for booking
- **Booked** - room has an active future or current booking that is not checked in
- **Occupied** - guest has checked in
- **Maintenance** - room is not available because it is under maintenance

The Rooms page calculates the displayed room status using both the room record and active bookings. A room marked as Maintenance stays Maintenance until it is changed.

### 9.2 Add a Room

1. Select **Rooms** from the left-side menu.
2. Select **+ Add Room**.
3. Enter the **Room number**.
4. Select the **Room type**.
5. Enter the **Price / night (LKR)**.
6. Select the room **Status**.
7. Select **Add room**.

Available room types in the system are:

- Standard
- Deluxe
- Suite

### 9.3 Edit a Room

1. Go to **Rooms**.
2. Select the room card.
3. Update the room number, type, price, or status.
4. Select **Save changes**.

### 9.4 Delete a Room

1. Go to **Rooms**.
2. Select **Delete** on the room card.
3. Confirm the deletion.

Before deleting a room, make sure it is no longer needed for hotel operations or reporting.

### 9.5 Search and Filter Rooms

You can:

- Search by room number
- Search by room type
- Filter by room status

Room cards are color-coded to make status easier to see.

### 9.6 Room Validation Rules

- Room number is required.
- Room number must be unique.
- Price per night must be greater than 0.
- Room type must be Standard, Deluxe, or Suite.

## 10. Guests

Use **Guests** to store and manage guest contact information.

### 10.1 Add a Guest

In the current system, new guests are added while creating a booking.

1. Select **Bookings** from the left-side menu.
2. Select **+ New Booking**.
3. In the **Guest details** section, leave **Search existing guest** blank.
4. Enter the guest's full name, email address, phone number, NIC, gender, and address.
5. Complete the booking details.
6. Select **Create booking**.

After the booking is created, the guest is saved and will appear on the **Guests** page.

### 10.2 Edit a Guest

1. Go to **Guests**.
2. Find the guest in the table.
3. Select **Edit**.
4. Update the details.
5. Select **Save changes**.

### 10.3 Delete a Guest

1. Go to **Guests**.
2. Find the guest.
3. Select **Delete**.
4. Confirm the deletion.

Only delete guest records when you are sure they are no longer required.

### 10.4 Search Guests

You can search guests by:

- Name
- Email
- Phone
- NIC

### 10.5 Export Guest Reports

On the Guests page:

- Select **Export PDF** to download a formatted guest report.

The export follows the currently visible search results.

### 10.6 Guest Validation Rules

When using the system forms:

- Full name is required.
- Full name must contain letters and spaces only.
- Full name must be at least 3 characters long.
- Email must be in a valid email format.
- Phone number must contain exactly 10 digits.
- Gender options are Male, Female, and Other.

## 11. Employees

The **Employees** page is available to Admin and Owner users.

Use this page to manage hotel employee records, roles, salaries, hire dates, and employment status.

### 11.1 Add an Employee

1. Select **Employees** from the left-side menu.
2. Select **+ Add Employee**.
3. Enter the employee's full name.
4. Enter the employee's email address.
5. Enter the employee's phone number.
6. Select the job role.
7. Enter the monthly salary in LKR.
8. Select the hire date.
9. Select the employee status.
10. Select **Add employee**.

Available job roles are:

- Reception
- Housekeeping
- Chef
- Safari Guide
- Manager
- Maintenance

Available employee statuses are:

- Active
- On Leave
- Terminated

### 11.2 Edit an Employee

1. Go to **Employees**.
2. Find the employee.
3. Select **Edit**.
4. Update the details.
5. Select **Save changes**.

### 11.3 Delete an Employee

1. Go to **Employees**.
2. Find the employee.
3. Select **Delete**.
4. Confirm the deletion.

### 11.4 Search and Filter Employees

You can:

- Search by name, role, or email
- Filter by job role
- Filter by employee status

### 11.5 Export Employee Reports

On the Employees page:

- Select **Export PDF** to download a formatted employee report

The export follows the currently visible filtered list.

### 11.6 Employee Validation Rules

- Full name is required.
- Full name must contain letters and spaces only.
- Full name must be at least 3 characters long.
- Email must be in a valid email format.
- Phone number must contain exactly 10 digits.
- Monthly salary must be greater than 0.
- Job role must be one of the listed job roles.
- Status must be Active, On Leave, or Terminated.

## 12. Inventory

Use **Inventory** to manage hotel supplies and stock levels.

### 12.1 Add an Inventory Item

1. Select **Inventory** from the left-side menu.
2. Select **+ Add Item**.
3. Enter the item name.
4. Select the category.
5. Enter the unit, such as pcs, kg, reams, or cylinders.
6. Enter the quantity.
7. Enter the low-stock alert level.
8. Enter the unit price in LKR.
9. Select **Add item**.

Available categories are:

- Linen
- Toiletries
- Food & Beverage
- Kitchen
- Maintenance
- Office

### 12.2 Edit an Inventory Item

1. Go to **Inventory**.
2. Find the item.
3. Select **Edit**.
4. Update the details.
5. Select **Save changes**.

### 12.3 Delete an Inventory Item

1. Go to **Inventory**.
2. Find the item.
3. Select **Delete**.
4. Confirm the deletion.

### 12.4 Understand Low Stock

Each inventory item has a quantity and a low-stock alert level.

If the quantity is equal to or below the low-stock alert level, the system marks the item as **Low stock**.

Example:

- Quantity: 8
- Low-stock alert at: 10
- Status shown: Low stock

### 12.5 Search and Filter Inventory

You can:

- Search by item name or category
- Filter by category
- Tick **Low stock only** to show only items that need attention

### 12.6 Export Inventory Reports

On the Inventory page:


- Select **Export PDF** to download a formatted inventory report

The export follows the currently visible filtered list.

The PDF report includes a summary such as number of items, low-stock count, and total stock value.

## 13. Activity Log

The **Activity Log** page is available to Admin and Owner users.

It records important actions, such as:

- Creating a booking
- Checking in a booking
- Checking out a booking
- Cancelling a booking
- Editing or deleting guests
- Creating bookings that may include new guest details
- Adding, editing, or deleting rooms
- Adding, editing, or deleting employees
- Adding, editing, or deleting inventory items
- Creating or updating user profiles

### 13.1 View Activity

1. Select **Activity Log** from the left-side menu.
2. Review the table.

The table shows:

- Timestamp
- User
- Role
- Activity

### 13.2 Search and Filter Activity

You can:

- Search by user, action, role, or details
- Filter activity by user

### 13.3 Export Activity Log

Select **Export PDF** to download a PDF report of the currently visible activity log entries.

### 13.4 Delete Activity Log Entries

Only the Owner can delete activity log entries.

1. Go to **Activity Log**.
2. Find the entry.
3. Select **Delete**.
4. Confirm the deletion.

Activity log entries are useful for accountability. Delete them only when necessary.

## 14. Users

The **Users** page is available to Admin and Owner users.

It is used to view and manage system user profiles and roles.

### 14.1 View Users

1. Select **Users** from the left-side menu.
2. Review the user table.

The table shows:

- Name
- Email
- Role
- Supabase user ID

### 14.2 Search and Filter Users

You can:

- Search by name, email, or role
- Filter by role: owner, admin, or staff

### 14.3 Add a New User

Admin and Owner users can create new users.

1. Go to **Users**.
2. Select **+ Add User**.
3. Enter the user's full name.
4. Enter the user's email address.
5. Enter a starting password.
6. Select the role.
7. Select **Create profile**.

Important:

- Only the Owner can assign the owner role, and only when there is no existing Owner account.
- The system allows only one Owner account.
- New users should protect their password according to the hotel's password policy.

### 14.4 Edit a User

Only the Owner can edit existing user profiles.

1. Go to **Users**.
2. Find the user.
3. Select **Edit**.
4. Update the name, email, or role.
5. Select **Save changes**.

### 14.5 Delete a User

Only the Owner can delete user profiles.

1. Go to **Users**.
2. Find the user.
3. Select **Delete**.
4. Confirm the deletion.

The Owner cannot delete their own account from this page. The owner account also cannot be deleted from this page.

## 15. Reports and Downloads

The system does not use one separate Reports page. Instead, reports are available directly from the pages where the records are managed.

Reports are available from:

- Bookings
- Guests
- Employees
- Inventory
- Activity Log

Common export options:

- **Export PDF** - useful for printing, sharing, and formal records

Exports use the list currently shown on the screen. If you search or filter before exporting, only the matching records are included.

## 16. Recommended Daily Workflow

### Morning

1. Sign in.
2. Open **Dashboard**.
3. Review occupancy, active bookings, upcoming checkouts, and low-stock alerts.
4. Open **Bookings** and check today's arrivals.
5. Open **Inventory** and review low-stock items if there are alerts.

### During Guest Arrival

1. Open **Bookings**.
2. Search for the guest.
3. Confirm the booking details.
4. Select **Check in** if the check-in date has arrived.
5. Confirm the room status changes to Occupied.

### During Guest Departure

1. Open **Bookings**.
2. Search for the guest.
3. Open **Invoice** if needed.
4. Export the invoice PDF if the guest needs a copy.
5. Select **Check out**.
6. Confirm the room becomes available again.

### When Taking a New Reservation

1. Open **Bookings**.
2. Select **+ New Booking**.
3. Search for the guest or enter new guest details.
4. Select an available room.
5. Enter the stay dates.
6. Confirm the total amount.
7. Create the booking.

### End of Day

1. Export booking, inventory, or activity reports if required.
2. Review the Dashboard for any remaining operational issues.
3. Log out.

## 17. Common Problems and What to Do

### I cannot sign in.

Check your email and password. If they are correct and you still cannot sign in, ask an administrator or the owner to check your account.

### A menu item is missing.

Your role may not have permission to access that area. Ask the owner or administrator if you need additional access.

### I cannot check in a booking.

The system only allows check-in on or after the booking's check-in date.

### I cannot choose a room for a new booking.

Only available rooms are shown when creating a booking. The room may already be booked, occupied, or under maintenance.

### The total amount is not correct.

Check the selected room, check-in date, check-out date, and discount amount. The system calculates the total from room price, number of nights, and discount.

### An item is marked Low stock.

The item's quantity is equal to or below its low-stock alert level. Update the quantity after restocking.

### Export buttons are disabled.

Export buttons are disabled when there are no records in the current filtered list. Clear the search or filters and try again.

## 18. Good Practice for Users

- Log out when you finish using the system.
- Do not share your password.
- Keep guest and employee details accurate.
- Use cancellation instead of deletion when you need to keep a booking record.
- Delete records only when you are sure they are no longer needed.
- Review low-stock items regularly.
- Export reports after applying the correct filters.
- Ask the owner before changing user roles.

## 19. Data Handled by the System

The system stores operational hotel information, including:

- Guest names and contact details
- Booking dates, room details, and booking totals
- Room numbers, types, prices, and statuses
- Employee names, contact details, roles, salaries, hire dates, and statuses
- Inventory item quantities, unit prices, and low-stock thresholds
- User names, emails, roles, and account IDs
- Activity records showing user actions

Only authorized users should access the system.

## 20. Quick Reference

| Task | Where to Go | Main Action |
| --- | --- | --- |
| See today's overview | Dashboard | Review cards and charts |
| Create a reservation | Bookings | Select + New Booking |
| Check in a guest | Bookings | Select Check in |
| Check out a guest | Bookings | Select Check out |
| Print or save invoice | Bookings | Select Invoice, then Export PDF |
| Add or edit room | Rooms | Select + Add Room or select a room card |
| Update guest details | Guests | Select Edit |
| Update employee details | Employees | Select Edit |
| Update stock quantity | Inventory | Select Edit |
| See low-stock items | Inventory | Tick Low stock only |
| Export reports | Bookings, Guests, Employees, Inventory, Activity Log | Select Export PDF |
| Review system actions | Activity Log | Search, filter, or export |
| Add a system user | Users | Select + Add User |
| Change a user role | Users | Owner selects Edit |

## 21. Support

If you are unsure what to do, contact the system owner, administrator, or support person responsible for Kingfisher HMS.

When reporting a problem, include:

- What page you were using
- What you were trying to do
- Any message shown on the screen
- The guest, booking, room, employee, or item involved, if relevant
