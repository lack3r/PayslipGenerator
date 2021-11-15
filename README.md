# Payslip Generator

### **Objective**

This application generates invoices for all employees of a company. 

**Note**: Deductions used reflect employment deductions as imposed by the Government of Cyprus. However, the application is designed using best practices, and thus it can be easily adapted to other rules and regulations as well.



### **Behavior**

The application is used as an internal tool to generate the payslips of a company.

It is written following SOLID principles, with the purpose to be easier to read, test and maintain.

The application firstly reads the input and configuration files as mentioned in the [Input and Configuration files](#input-and-configuration-files) section. Then it generates the invoices as HTML files in the `output\generatedPayslips` directory. Each payslip is named as: `<<EMPLOYEE_ID>>_YYYYMMDD.html`.



### **Technologies and methodology**

- Language: Java 8
- Dependency Injection: Spring Framework
- Reading JSON files and Object Mapping: Jackson
- Logger: Log4j



### **Input and configuration files**

- app.properties: Contains the files with the rest of the configuration files. Use relative paths.

- DEDUCTION_PERCENTAGES_FILENAME. Default Location: "config_files/deduction_percentages.csv". Contains the percentage of each of the deductions that should be applied on the gross salary.  Format of entries `ENTITY (Employer or Employee),DEDUCTION_NAME,DEDUCTION_PERCENTAGE,UP_TO_AMOUNT_APPLICABLE`

- TAX_CONFIG_FILENAME. Default Location:  "config_files/tax_config.csv". Tax in Cyprus uses tax zones. Each zone has a different percentage of tax. Format of entries `FROM_SALARY,TO_SALARY,TAX_PERCENTAGE`

- COMPANY_INFO_FILENAME. Default Location:"config_files/company_info.csv". Deprecated and will be removed in the next release. It is now replaced by the COMPANY_WITH_EMPLOYEES_FILENAME file.

- PAYSLIP_HISTORY_FILENAME. Default Location:"payslip_history.csv". This file contains the deductions historically for all employees. It is used to calculate the Year to Date (YtD) values on the payslips.

- HTML_TEMPLATE_FILENAME. Default Location: "html/payslip_template.html". It contains the HTML template that is used as a template for the invoices.

- PAYSLIPS_OUTPUT_DIRECTORY. Default Location: "output/generatedPayslips". The directory where the generated payslips will be saved at.

- COMPANY_WITH_EMPLOYEES_FILENAME. Default Location: "config_files/company.json". A JSON file containing information regarding the employer (company), and also contains an array with all the employees of the company.

  

### **Functionality to be implemented**

1. Some of the deductions are applicable for up to a certain amount of gross salary. The current solution does not take these into account.
2. Some Year to Date amounts are not yet calculated.



### **Non-functional tasks to be implemented**

The application is written in a way that can be testable. No tests have been written *yet*, and this is one of the following actions that will be taken.
