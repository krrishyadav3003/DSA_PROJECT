import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import javax.swing.*;

public class CalendarApp {

    private JFrame frame;
    private JLabel monthLabel;
    private JPanel calendarPanel;
    private Calendar calendar;
    private EventManager eventManager;
    private String currentView = "monthly"; // Default view

    public CalendarApp() {
   
        eventManager = new EventManager();
        eventManager.addDefaultEvents();
        startReminderChecker();
        
        calendar = Calendar.getInstance();
        
        frame = new JFrame("Calendar Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setLayout(new BorderLayout());

        monthLabel = new JLabel("", SwingConstants.CENTER);
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
   
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calendar.add(Calendar.MONTH, -1);
                currentView="monthly";
                updateCalendar(); // Update the calendar when navigating back
            }
        });

        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calendar.add(Calendar.MONTH, 1);
                currentView="monthly";
                updateCalendar(); // Update the calendar when navigating forward
            }
        });

   
        JButton addButton = new JButton("Add Event");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEvent(); // Open event creation dialog
            }
        });

        JButton editButton = new JButton("Edit Event");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editEvent(); // Open event edit dialog
            }
        });

        JButton deleteButton = new JButton("Delete Event");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteEvent(); // Open event deletion dialog
            }
        });

        JButton monthlyViewButton = new JButton("Monthly View");
        monthlyViewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentView = "monthly";
                updateCalendar();
            }
        });

        JButton weeklyViewButton = new JButton("Weekly View");
        weeklyViewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentView = "weekly";
                updateCalendar();
            }
        });

        JButton dailyViewButton = new JButton("Daily View");
        dailyViewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentView = "daily";
                updateCalendar();
            }
        });

        JButton searchButton = new JButton("Search Events");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchEvents(); // Open event search dialog
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
   
        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(nextButton, BorderLayout.EAST);
        topPanel.add(monthLabel, BorderLayout.CENTER);
   
        JPanel buttonPanel = new JPanel();

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(monthlyViewButton);
        buttonPanel.add(weeklyViewButton);
        buttonPanel.add(dailyViewButton);
        buttonPanel.add(searchButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        calendarPanel = new JPanel(new GridLayout(0, 7)); // 7 days in a week
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(calendarPanel, BorderLayout.CENTER);

        updateCalendar(); // Initial calendar view update
        frame.setVisible(true);
    }

    private void updateCalendar() {
        calendarPanel.removeAll(); // Clear the previous calendar grid
        switch (currentView) {
            case "monthly":
                updateMonthlyView();
                break;
            case "weekly":
                updateWeeklyView();
                break;
            case "daily":
                updateDailyView();
                break;
        }
    }

    private void updateMonthlyView() {
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the current month
        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK); // Get the first day of the week
        int daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH); // Get total days in the month

        // Get the month and year to update the header
        String monthName = tempCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()); // Full month name
        int year = tempCal.get(Calendar.YEAR); // Get the current year
        monthLabel.setText(monthName + " " + year); // Set the header label with month name and year

        // Add the day names (Sunday, Monday, etc.) in the first row
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel dayNameLabel = new JLabel(dayName, SwingConstants.CENTER);
            calendarPanel.add(dayNameLabel); // Add the day name labels to the top of the calendar
        }

        // Add empty labels for days before the first day of the month
        for (int i = 1; i < firstDayOfWeek; i++) {
            calendarPanel.add(new JLabel("")); // Empty space for the first week
        }

        // Add labels for each day of the month
        for (int day = 1; day <= daysInMonth; day++) {
            Calendar dayCal = (Calendar) tempCal.clone();
            dayCal.set(Calendar.DAY_OF_MONTH, day); // Set the day
            String eventText = "";
            boolean hasEvent = false;
            // Check if there are any events on this day
            for (Event event : eventManager.getEventsOnDate(dayCal)) {
                hasEvent = true;
                eventText = event.getTitle(); // Get the title of the first event for the day
                break; // Only show the first event in the tooltip
            }

            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            if (hasEvent) {
                dayLabel.setForeground(Color.RED); // Highlight days with events in red
                dayLabel.setToolTipText(eventText); // Show event title in tooltip
            }
            calendarPanel.add(dayLabel);
        }

        calendarPanel.revalidate(); // Revalidate the panel to refresh the view
        calendarPanel.repaint(); // Repaint the panel to update the display
    }

    private void updateWeeklyView() {
        calendarPanel.removeAll(); // Clear the previous calendar grid
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Set to the first day of the week (Sunday)

        // Get the week start date and add day labels for 7 days
        for (int i = 0; i < 7; i++) {
            JLabel dayLabel = new JLabel(tempCal.get(Calendar.DAY_OF_MONTH) + " (" + tempCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ")", SwingConstants.CENTER);
            calendarPanel.add(dayLabel);

            // Check for events on the day
            String eventText = "";
            boolean hasEvent = false;
            for (Event event : eventManager.getEventsOnDate(tempCal)) {
                hasEvent = true;
                eventText = event.getTitle();
                break;
            }

            if (hasEvent) {
                dayLabel.setForeground(Color.RED);
                dayLabel.setToolTipText(eventText);
            }

            tempCal.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
        }

        calendarPanel.revalidate(); // Revalidate the panel to refresh the view
        calendarPanel.repaint(); // Repaint the panel to update the display
    }

    private void updateDailyView() {
        calendarPanel.removeAll();
        Calendar tempCal = (Calendar) calendar.clone();
        String eventText = "";
        boolean hasEvent = false;
        for (Event event : eventManager.getEventsOnDate(tempCal)) {
            hasEvent = true;
            eventText = event.getTitle();
            break;
        }

        JLabel dayLabel = new JLabel("Day: " + tempCal.get(Calendar.DAY_OF_MONTH) + " (" + tempCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) + ")", SwingConstants.CENTER);
        calendarPanel.add(dayLabel);

        if (hasEvent) {
            JLabel eventLabel = new JLabel("Event: " + eventText, SwingConstants.CENTER);
            eventLabel.setForeground(Color.RED);
            calendarPanel.add(eventLabel);
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private void addEvent() {
        String title = JOptionPane.showInputDialog(frame, "Enter Event Title:");
        if (title != null && !title.trim().isEmpty()) {
            int day = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter Day:"));
            String reminderTimeString = JOptionPane.showInputDialog(frame, "Enter reminder time (in minutes, e.g., 10 for 10 minutes before event):");
            int reminderTimeInMinutes = Integer.parseInt(reminderTimeString);
            eventManager.addEvent(title, day, calendar.get(Calendar.MONTH), reminderTimeInMinutes);
            updateCalendar();
        }
    }

    // New method to edit events
    private void editEvent() {
        String title = JOptionPane.showInputDialog(frame, "Enter Event Title to Edit:");
        if (title != null && !title.trim().isEmpty()) {
            Event eventToEdit = eventManager.findEventByTitle(title);
            if (eventToEdit != null) {
                // Get current reminder time in minutes
                int currentReminderTime = eventToEdit.getReminderTimeInMinutes();
    
                // Allow the user to change the title
                String newTitle = JOptionPane.showInputDialog(frame, "Enter New Event Title:", eventToEdit.getTitle());
                
                // Allow the user to change the day
                String newDayString = JOptionPane.showInputDialog(frame, "Enter Day:", eventToEdit.getDate().get(Calendar.DAY_OF_MONTH));
                int newDay = Integer.parseInt(newDayString);
    
                // Allow the user to change the reminder time
                String newReminderTimeString = JOptionPane.showInputDialog(frame, "Enter New Reminder Time (in minutes, current: " + currentReminderTime + "):");
                int newReminderTimeInMinutes = Integer.parseInt(newReminderTimeString);
    
                // Update the event's date and reminder time
                eventToEdit.getDate().set(Calendar.DAY_OF_MONTH, newDay);
                eventToEdit.setTitle(newTitle); // Update the event title
                eventToEdit.setReminderTimeInMinutes(newReminderTimeInMinutes); // Update the reminder time
    
                updateCalendar(); // Refresh the calendar view
                JOptionPane.showMessageDialog(frame, "Event edited successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Event not found.");
            }
        }
    }

    private void deleteEvent() {
        String title = JOptionPane.showInputDialog(frame, "Enter Event Title to Delete:");
        if (title != null && !title.trim().isEmpty()) {
            Event eventToDelete = eventManager.findEventByTitle(title);
            if (eventToDelete != null) {
                eventManager.removeEvent(eventToDelete); // Remove event
                updateCalendar(); // Update the calendar view
                JOptionPane.showMessageDialog(frame, "Event deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(frame, "Event not found.");
            }
        }
    }

    private void searchEvents() {
        String searchTerm = JOptionPane.showInputDialog(frame, "Enter Title or Date (MM/DD/YYYY):");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            if (searchTerm.contains("/")) { // Treat as a date
                try {
                    String[] parts = searchTerm.split("/");
                    int month = Integer.parseInt(parts[0]) - 1; // Month is 0-indexed
                    int day = Integer.parseInt(parts[1]);
                    Calendar searchDate = Calendar.getInstance();
                    searchDate.set(Calendar.MONTH, month);
                    searchDate.set(Calendar.DAY_OF_MONTH, day);
                    ArrayList<Event> events = eventManager.getEventsOnDate(searchDate);
                    displaySearchResults(events);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Please use MM/DD.");
                }
            } else { // Treat as a title
                ArrayList<Event> events = eventManager.findEventsByTitle(searchTerm);
                displaySearchResults(events);
            }
        }
    }

    private void displaySearchResults(ArrayList<Event> events) {
        if (events.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No events found.");
        } else {
            StringBuilder results = new StringBuilder("Search Results:\n");
            for (Event event : events) {
                results.append(event.getTitle()).append(" on ").append(event.getDate().get(Calendar.MONTH) + 1)
                        .append("/").append(event.getDate().get(Calendar.DAY_OF_MONTH)).append("\n");
            }
            JOptionPane.showMessageDialog(frame, results.toString());
        }
    }

    private void startReminderChecker() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                eventManager.checkForReminders();
            }
        }, 0, 60000); // Check every minute
    }
    
    public static void main(String[] args) {
        new CalendarApp();
    }
}

class Event {
    private String title;
    private Calendar date;
    
    private int reminderTimeInMinutes;

    public Event(String title, Calendar date, int reminderTimeInMinutes) {
        this.title = title;
        this.date = date;
        this.reminderTimeInMinutes = reminderTimeInMinutes;    
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDate() {
        return date;
    }

    public int getReminderTimeInMinutes() {
        return reminderTimeInMinutes;
    }

    public void setReminderTimeInMinutes(int reminderTimeInMinutes) {
        this.reminderTimeInMinutes = reminderTimeInMinutes;
    }
}