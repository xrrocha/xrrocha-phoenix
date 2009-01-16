package com.sun.phoenix.example.vacation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.phoenix.Processor;

public class VacationRequestProcessor extends Processor<VacationRequest> {
    private static final long serialVersionUID = 1L;

    private transient Mailer mailer;
    private transient EmployeeLocator employeeLocator;
    private transient TemplateExpander templateExpander;

    private String employeeTemplate;
    private String managerTemplate;
    private String humanResourcesTemplate;
    private String approvalTemplate;
    private String rejectionTemplate;
    private String humanResourcesEmail;
    private String vacationTitleTemplate;
    private String vacationServiceEmail;
    private String vacationTitle;

    private ManagerReply managerReply;

    private static final Logger logger = Logger.getLogger(VacationRequestProcessor.class.getName());

    @Override
    public void process(VacationRequest vacationRequest) throws Exception {
        final Employee employee = employeeLocator.locateEmployee(vacationRequest.getEmployeeId());
        final Employee employeeManager = employeeLocator.locateEmployee(employee.getManagerId());

        logger.info("Employee " + employee.getName() + " requests vacation from " + vacationRequest.getStartDate() + " to " + vacationRequest.getEndDate());

        final Map<String, Object> context = new HashMap<String, Object>();
        context.put("employee", employee);
        context.put("manager", employeeManager);
        context.put("vacationRequest", vacationRequest);
        context.put("humanResourcesEmail", humanResourcesEmail);

        vacationTitle = templateExpander.expand(vacationTitleTemplate, context);

        // TODO Replace by services
        sendMail(templateExpander.expand(employeeTemplate, context), employee.getEmail());
        sendMail(templateExpander.expand(managerTemplate, context), employeeManager.getEmail());
        sendMail(templateExpander.expand(humanResourcesTemplate, context), humanResourcesEmail);

        // TODO Pass only trailing portion of uri; remove dependency on uri prefix
        final String managerReplyUri = "managerReply";
        expect(managerReplyUri, new Processor<ManagerReply>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void process(ManagerReply arg) {
                managerReply = arg;
                context.put("vacationReply", managerReply);
                logger.info("Manager decision: " + (managerReply.isApproved() ? "Approved" : "Rejected"));
            }
        });
        final String hrAcknowledgementUri = "hrAcknowledgement";
        expect(hrAcknowledgementUri);

        logger.info("Waiting for manager decision and HR acknowledgement");
        waitFor(managerReplyUri, hrAcknowledgementUri);

        context.put("managerReply", managerReply);

        if (managerReply.isApproved()) {
            sendMail(templateExpander.expand(approvalTemplate, context), employee.getEmail(), humanResourcesEmail);
        } else {
            sendMail(templateExpander.expand(rejectionTemplate, context), employee.getEmail(), humanResourcesEmail);
        }

        final String employeeAcknowledgementUri = "employeeAcknowledgement";
        expect(employeeAcknowledgementUri);

        logger.info("Waiting for employee acknowledgement");
        waitFor(employeeAcknowledgementUri);

        logger.info("End of process");
    }
    
    @Override
    protected Object query() throws Exception {
        if (managerReply == null) {
            return "Pending";
        } else if (managerReply.isApproved()) {
            return "Approved";
        } else {
            return "Rejected";
        }
    }

    private void sendMail(String body, String... recipients) {
        body = buildParagraph(body, 80);
        if (mailer != null) {
            mailer.sendMail(vacationServiceEmail, vacationTitle, body, recipients);
        } else {
            logger.info("Sending mail to: " + recipients[0]);
            logger.info(body);
        }
    }

    private static String buildParagraph(final String text, final int width) {
        String[] allTokens = text.trim().split("\\s+");
        List<String> lines = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for(String token : allTokens) {
          if((token.length() + builder.length() - 1) > width) {
            lines.add(builder.toString().trim());
            builder = new StringBuilder();
          }
          builder.append(token);
          builder.append(' ');
        }
        lines.add(builder.toString().trim());
        
        builder = new StringBuilder();
        for (String line: lines) {
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
      }

    public Mailer getMailer() {
        return mailer;
    }

    public void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }

    public EmployeeLocator getEmployeeLocator() {
        return employeeLocator;
    }

    public void setEmployeeLocator(EmployeeLocator employeeLocator) {
        this.employeeLocator = employeeLocator;
    }

    public TemplateExpander getTemplateExpander() {
        return templateExpander;
    }

    public void setTemplateExpander(TemplateExpander templateExpander) {
        this.templateExpander = templateExpander;
    }

    public String getEmployeeTemplate() {
        return employeeTemplate;
    }

    public void setEmployeeTemplate(String employeeTemplate) {
        this.employeeTemplate = employeeTemplate;
    }

    public String getManagerTemplate() {
        return managerTemplate;
    }

    public void setManagerTemplate(String managerTemplate) {
        this.managerTemplate = managerTemplate;
    }

    public String getHumanResourcesTemplate() {
        return humanResourcesTemplate;
    }

    public void setHumanResourcesTemplate(String humanResourcesTemplate) {
        this.humanResourcesTemplate = humanResourcesTemplate;
    }

    public String getApprovalTemplate() {
        return approvalTemplate;
    }

    public void setApprovalTemplate(String approvalTemplate) {
        this.approvalTemplate = approvalTemplate;
    }

    public String getRejectionTemplate() {
        return rejectionTemplate;
    }

    public void setRejectionTemplate(String rejectionTemplate) {
        this.rejectionTemplate = rejectionTemplate;
    }

    public String getHumanResourcesEmail() {
        return humanResourcesEmail;
    }

    public void setHumanResourcesEmail(String humanResourcesEmail) {
        this.humanResourcesEmail = humanResourcesEmail;
    }

    public String getVacationTitleTemplate() {
        return vacationTitleTemplate;
    }

    public void setVacationTitleTemplate(String vacationTitleTemplate) {
        this.vacationTitleTemplate = vacationTitleTemplate;
    }

    public String getVacationServiceEmail() {
        return vacationServiceEmail;
    }

    public void setVacationServiceEmail(String vacationServiceEmail) {
        this.vacationServiceEmail = vacationServiceEmail;
    }
}
