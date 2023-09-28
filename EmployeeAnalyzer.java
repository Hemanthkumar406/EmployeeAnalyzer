package BlueJay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class EmployeeRecord {
    private String name;
    private String position;
    private Date startTime;
    private Date endTime;

    public EmployeeRecord(String name, String position, Date startTime, Date endTime) {
        this.name = name;
        this.position = position;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}

public class EmployeeAnalyzer {
    public static void main(String[] args) {
        List<EmployeeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("C:/Users/hemanth kumar/Documents/YashAlp/employee_records.txt"))) {
            String line;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                String position = parts[1];
                Date startTime = dateFormat.parse(parts[2]);
                Date endTime = dateFormat.parse(parts[3]);

                EmployeeRecord record = new EmployeeRecord(name, position, startTime, endTime);
                records.add(record);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        findEmployeesWithConsecutiveDays(records, 7);
        findEmployeesWithShortBreak(records, 10, 1);
        findEmployeesWithLongShifts(records, 14);
    }

    private static void findEmployeesWithConsecutiveDays(List<EmployeeRecord> records, int consecutiveDays) {
        // Sort records by name and date
        records.sort(Comparator.comparing(EmployeeRecord::getName).thenComparing(EmployeeRecord::getStartTime));

        Map<String, Integer> consecutiveDaysMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentEmployee = null;
        Date currentDate = null;

        for (EmployeeRecord record : records) {
            String employeeName = record.getName();
            Date shiftDate = record.getStartTime();
            String formattedDate = dateFormat.format(shiftDate);

            if (!employeeName.equals(currentEmployee) || currentDate == null) {
                currentEmployee = employeeName;
                consecutiveDaysMap.put(employeeName, 1);
                currentDate = shiftDate;
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(currentDate);
                cal.add(Calendar.DAY_OF_MONTH, 1);

                if (dateFormat.format(cal.getTime()).equals(formattedDate)) {
                    consecutiveDaysMap.put(employeeName, consecutiveDaysMap.get(employeeName) + 1);
                } else {
                    consecutiveDaysMap.put(employeeName, 1);
                }

                currentDate = shiftDate;
            }

            if (consecutiveDaysMap.get(employeeName) >= consecutiveDays) {
                System.out.println("Employee: " + employeeName + ", Position: " + record.getPosition() +
                        " has worked for " + consecutiveDays + " consecutive days.");
            }
        }
    }

    private static void findEmployeesWithShortBreak(List<EmployeeRecord> records, int maxHours, int minHours) {
        records.sort(Comparator.comparing(EmployeeRecord::getName).thenComparing(EmployeeRecord::getStartTime));

        Map<String, Date> lastEndTimeMap = new HashMap<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        for (EmployeeRecord record : records) {
            String employeeName = record.getName();
            Date startTime = record.getStartTime();

            if (lastEndTimeMap.containsKey(employeeName)) {
                Date lastEndTime = lastEndTimeMap.get(employeeName);
                long hoursBetween = (startTime.getTime() - lastEndTime.getTime()) / (60 * 60 * 1000);

                if (hoursBetween < maxHours && hoursBetween > minHours) {
                    System.out.println("Employee: " + employeeName + ", Position: " + record.getPosition() +
                            " has less than " + maxHours + " hours between shifts.");
                }
            }

            lastEndTimeMap.put(employeeName, record.getEndTime());
        }
    }

    private static void findEmployeesWithLongShifts(List<EmployeeRecord> records, int maxHours) {
        for (EmployeeRecord record : records) {
            long hoursWorked = (record.getEndTime().getTime() - record.getStartTime().getTime()) / (60 * 60 * 1000);

            if (hoursWorked > maxHours) {
                System.out.println("Employee: " + record.getName() + ", Position: " + record.getPosition() +
                        " has worked for more than " + maxHours + " hours in a single shift.");
            }
        }
    }
}
