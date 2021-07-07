package io.qbeat;

import io.qbeat.file.readers.FileReader;
import io.qbeat.file.writers.FileWriter;
import io.qbeat.models.PersonType;
import io.qbeat.models.Payslip;
import io.qbeat.models.PayslipHistory;
import io.qbeat.utils.DateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PayslipHistoryDAO {
    private final FileReader fileReader;
    private final FileWriter fileWriter;
    private final String filename;

    private static final Logger logger = LogManager.getLogger(PayslipHistoryDAO.class);

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
            logger.debug("Payslip successfully inserted: " + payslip);
        }
        catch (Exception e) {
            logger.error("Failed to insert payslip: " + payslip, e);
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
            logger.info("Payslip successfully updated: " + newPayslip);
        }
        catch (Exception e) {
            logger.error("Failed to update old payslip entries: " + oldPayslipEntries + "\n with new payslip: " + newPayslip, e);
        }
    }

    /**
     * @param payslip The payslip to insert or update if already exists
     */
    public void insertOnDuplicateUpdate(Payslip payslip) {
        List<PayslipHistory> employeeCurrentMonthEntries = findByEmployeeIdAndInCurrentMonth(payslip.getEmployee().getId());

        // TODO: aloizou 07/07/21 What about the December and 13th Salary?
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
