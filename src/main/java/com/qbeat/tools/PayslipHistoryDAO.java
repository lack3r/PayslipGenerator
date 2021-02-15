package com.qbeat.tools;

import com.qbeat.tools.config.FileReader;
import com.qbeat.tools.config.FileWriter;
import com.qbeat.tools.config.PersonType;
import com.qbeat.tools.models.Payslip;
import com.qbeat.tools.models.PayslipHistory;
import com.qbeat.tools.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PayslipHistoryDAO {
    private static final String PROPERTY_DELIMITER = ",";
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final String filename;

    public PayslipHistoryDAO(FileReader fileReader, FileWriter fileWriter, String filename) {
        this.fileReader = fileReader;
        this.fileWriter = fileWriter;
        this.filename = filename;
    }

    /**
     * @return All payslip histories
     */
    public List<PayslipHistory> getAll() {
        return fileReader.read(filename)
                .stream()
                .map(PayslipHistory::fromCSVLine)
                .collect(Collectors.toList());
    }

    /**
     * @param id The id of the employee to retrieve payslip histories
     * @return All payslip histories of the given employee
     */
    public List<PayslipHistory> findByEmployeeId(String id) {
        return getAll()
                .stream()
                .filter(payslipHistory -> payslipHistory.getEmployeeId().equals(id))
                .collect(Collectors.toList());
    }

    /**
     * @param id The id of the employee to retrieve payslip histories
     * @param personType The person type to retrieve payslip histories
     * @return All payslip histories of the given employee and person type
     */
    public List<PayslipHistory> findByEmployeeIdAndPersonType(String id, PersonType personType) {
        return getAll()
                .stream()
                .filter(payslipHistory -> payslipHistory.getEmployeeId().equals(id) && payslipHistory.getPersonType() == personType)
                .collect(Collectors.toList());
    }

    /**
     * @param id The id of the employee to retrieve payslip histories
     * @return All payslip histories in current month of the given employee
     */
    public List<PayslipHistory> findByEmployeeIdAndInCurrentMonth(String id) {
        return getAll()
                .stream()
                .filter(payslipHistory -> payslipHistory.getEmployeeId().equals(id) && DateUtil.isInCurrentMonth(payslipHistory.getDate()))
                .collect(Collectors.toList());
    }

    /**
     * @param payslip The payslip to insert in payslip histories
     */
    public void insert(Payslip payslip) {
        List<String> entriesToInsert = preparePayslipEntries(payslip);

        try {
            fileWriter.write(filename, entriesToInsert, true);
            System.out.println("Payslip successfully inserted: " + payslip);
        }
        catch (Exception e) {
            System.out.println("Failed to insert payslip: " + payslip);
            e.printStackTrace();
        }
    }

    /**
     * @param oldPayslipEntries The payslip histories to update
     * @param newPayslip The new payslip
     */
    public void update(List<PayslipHistory> oldPayslipEntries, Payslip newPayslip) {
        final List<PayslipHistory> entriesWithoutOldEntries = getAll().stream()
                .filter(payslipHistory -> !oldPayslipEntries.contains(payslipHistory))
                .collect(Collectors.toList());

        List<String> entriesToInsert = preparePayslipHistoryEntries(entriesWithoutOldEntries);
        entriesToInsert.addAll(preparePayslipEntries(newPayslip));

        try {
            fileWriter.write(filename, entriesToInsert, false);
            System.out.println("Payslip successfully updated: " + newPayslip);
        }
        catch (Exception e) {
            System.out.println("Failed to update old payslip entries: " + oldPayslipEntries + "\n with new payslip: " + newPayslip);
            e.printStackTrace();
        }
    }

    /**
     * @param payslip The payslip to insert or update if already exists
     */
    public void insertOnDuplicateUpdate(Payslip payslip) {
        List<PayslipHistory> employeeCurrentMonthEntries = findByEmployeeIdAndInCurrentMonth(payslip.getEmployee().getId());

        //TODO WHAT ABOUT DECEMBER AND 13TH SALARY?
        if (!employeeCurrentMonthEntries.isEmpty()) {
            update(employeeCurrentMonthEntries, payslip);
            return;
        }

        insert(payslip);
    }

    /**
     * @param payslips A list of payslips to insert or update if already exists
     */
    public void insertOnDuplicateUpdate(List<Payslip> payslips) {
        for (Payslip payslip : payslips) {
            insertOnDuplicateUpdate(payslip);
        }
    }

    /**
     * @param payslip The payslip to prepare as payslip history entry in csv
     * @return A list of csv payslip history entries
     */
    private List<String> preparePayslipEntries(Payslip payslip) {
        List<String> entriesToInsert = new ArrayList<>();
        entriesToInsert.add(payslip.toPayslipHistoryCSVLine(PersonType.EMPLOYEE));
        entriesToInsert.add(payslip.toPayslipHistoryCSVLine(PersonType.EMPLOYER));

        return entriesToInsert;
    }

    /**
     * @param payslipHistories A list of payslip histories to prepare as payslip history entries in csv
     * @return A list of csv payslip history entries
     */
    private List<String> preparePayslipHistoryEntries(List<PayslipHistory> payslipHistories) {
        return payslipHistories.stream()
                .map(PayslipHistory::toCSVLine)
                .collect(Collectors.toList());
    }
}
