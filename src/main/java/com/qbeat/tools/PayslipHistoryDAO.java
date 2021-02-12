package com.qbeat.tools;

import com.qbeat.tools.config.FileReader;
import com.qbeat.tools.config.PersonType;
import com.qbeat.tools.models.PayslipHistory;
import com.qbeat.tools.utils.DateUtil;

import java.util.List;
import java.util.stream.Collectors;

public class PayslipHistoryDAO {
    private static final String PROPERTY_DELIMITER = ",";
    private final FileReader fileReader;
    private final String filename;

    public PayslipHistoryDAO(FileReader fileReader, String filename) {
        this.fileReader = fileReader;
        this.filename = filename;
    }

    public List<PayslipHistory> getAll() {
        return fileReader.read(filename)
                .stream()
                .map(PayslipHistory::fromCSVLine)
                .collect(Collectors.toList());
    }

    public List<PayslipHistory> findByEmployeeId(String id) {
        return getAll()
                .stream()
                .filter(payslipHistory -> payslipHistory.getEmployeeId().equals(id))
                .collect(Collectors.toList());
    }

    public List<PayslipHistory> findByEmployeeIdAndPersonType(String id, PersonType personType) {
        return getAll()
                .stream()
                .filter(payslipHistory -> payslipHistory.getEmployeeId().equals(id) && payslipHistory.getPersonType() == personType)
                .collect(Collectors.toList());
    }

    public List<PayslipHistory> findByEmployeeIdAndInCurrentMonth(String id) {
        return getAll()
                .stream()
                .filter(payslipHistory -> payslipHistory.getEmployeeId().equals(id) && DateUtil.isInCurrentMonth(payslipHistory.getDate()))
                .collect(Collectors.toList());
    }

    // TODO
    public void insert() {

    }

    // TODO
    public void update() {

    }

    public void insertOnDuplicateUpdate(Payslip payslip) {
        if (hasPayslipHistoryOfCurrentMonth(payslip.getEmployee().getId())) {
            update();
            return;
        }

        insert();
    }

    public void insertOnDuplicateUpdate(List<Payslip> payslips) {
        for (Payslip payslip : payslips) {
            insertOnDuplicateUpdate(payslip);
        }
    }

    public boolean hasPayslipHistoryOfCurrentMonth(String employeeId) {
        return findByEmployeeIdAndInCurrentMonth(employeeId).size() > 0;
    }
}
