
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;

public class EventManager {
    private ArrayList<Event> events;

    public EventManager() {
        events = new ArrayList<>();
    }

    public void addEvent(String title, int day, int month, int reminderTimeInMinutes) {
        Calendar eventDate = Calendar.getInstance();
        eventDate.set(Calendar.DAY_OF_MONTH, day);
        eventDate.set(Calendar.MONTH, month);
        events.add(new Event(title, eventDate, reminderTimeInMinutes));
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public ArrayList<Event> getEventsOnDate(Calendar date) {
        ArrayList<Event> eventsOnDate = new ArrayList<>();
        for (Event event : events) {
            if (event.getDate().get(Calendar.YEAR) == date.get(Calendar.YEAR)
                    && event.getDate().get(Calendar.MONTH) == date.get(Calendar.MONTH)
                    && event.getDate().get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
                eventsOnDate.add(event);
            }
        }
        return eventsOnDate;
    }

    // Check for reminders for upcoming events
    public void checkForReminders() {
        Calendar now = Calendar.getInstance();
        for (Event event : events) {
            Calendar reminderTime = (Calendar) event.getDate().clone();
            reminderTime.add(Calendar.MINUTE, -event.getReminderTimeInMinutes());

            if (now.after(reminderTime) && now.before(event.getDate())) {
                JOptionPane.showMessageDialog(null, "Reminder: " + event.getTitle() + " is coming up!");
            }
        }
    }


    public ArrayList<Event> findEventsByTitle(String title) {
        ArrayList<Event> foundEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getTitle().toLowerCase().contains(title.toLowerCase())) {
                foundEvents.add(event);
            }
        }
        return foundEvents;
    }


    public void removeEvent(Event event) {
        events.remove(event);
    }

    public Event findEventByTitle(String title) {
        for (Event event : events) {
            if (event.getTitle().equalsIgnoreCase(title)) {
                return event;
            }
        }
        return null;
    }

    public void addDefaultEvents() {
        addEvent("Makar Sankranti / Pongal / Lohri", 14, Calendar.JANUARY,3600);
        addEvent("Republic Day", 26, Calendar.JANUARY,3600);
        addEvent("Vasant Panchami", 26, Calendar.JANUARY,3600);
        addEvent("Maha Shivaratri", 16, Calendar.FEBRUARY,3600);
        addEvent("Holi", 7, Calendar.MARCH,3600);
        addEvent("Ram Navami", 2, Calendar.APRIL,3600);
        addEvent("Baisakhi", 14, Calendar.APRIL,3600);
        addEvent("Mahavir Jayanti", 13, Calendar.APRIL,3600);
        addEvent("Good Friday", 19, Calendar.APRIL,3600);
        addEvent("Buddha Purnima", 23, Calendar.APRIL,3600);
        addEvent("Rath Yatra", 14, Calendar.JUNE,3600);
        addEvent("Guru Purnima", 5, Calendar.JULY,3600);
        addEvent("Raksha Bandhan", 22, Calendar.AUGUST,3600);
        addEvent("Janmashtami", 28, Calendar.AUGUST,3600);
        addEvent("Independence Day", 15, Calendar.AUGUST,3600);
        addEvent("Ganesh Chaturthi", 31, Calendar.AUGUST,3600);
        addEvent("Navratri / Durga Puja", 26, Calendar.SEPTEMBER,3600);
        addEvent("Dussehra", 11, Calendar.OCTOBER,3600);
        addEvent("Eid Milad-un-Nabi", 29, Calendar.OCTOBER,3600);
        addEvent("Diwali", 14, Calendar.NOVEMBER,1);
        addEvent("Gurpurab", 12, Calendar.NOVEMBER,2);
        addEvent("Christmas", 25, Calendar.DECEMBER,10);
        // Add other events here as needed...
    }
}
