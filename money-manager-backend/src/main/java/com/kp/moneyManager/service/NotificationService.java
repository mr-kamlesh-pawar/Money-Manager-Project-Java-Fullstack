package com.kp.moneyManager.service;

import com.kp.moneyManager.dto.ExpenseDTO;
import com.kp.moneyManager.entity.ProfileEntity;
import com.kp.moneyManager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontEndUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST") //10pm
    //@Scheduled(cron = "0 * * * * *", zone = "IST")
    public void senDailyIncomeExpensesReminder() {
        log.info("Job started: senDailyIncomeExpensesReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            String body = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                      <meta charset="UTF-8">
                      <title>Money Manager Reminder</title>
                      <style>
                        body {
                          font-family: Arial, sans-serif;
                          background: #f4f7fb;
                          margin: 0;
                          padding: 0;
                        }
                        .container {
                          background: #ffffff;
                          max-width: 600px;
                          margin: 30px auto;
                          border-radius: 12px;
                          box-shadow: 0px 4px 12px rgba(0,0,0,0.1);
                          overflow: hidden;
                          width: 100%%;
                        }
                        .header {
                          background: linear-gradient(90deg, #43cea2, #185a9d);
                          color: #fff;
                          padding: 25px;
                          text-align: center;
                        }
                        .header h1 {
                          margin: 0;
                          font-size: 22px;
                          letter-spacing: 1px;
                        }
                        .content {
                          padding: 25px;
                          color: #444;
                          line-height: 1.6;
                          font-size: 15px;
                        }
                        .content h2 {
                          color: #185a9d;
                          font-size: 20px;
                        }
                        .highlight {
                          background: #e8f9f3;
                          padding: 12px;
                          border-left: 4px solid #43cea2;
                          margin: 20px 0;
                          border-radius: 6px;
                        }
                        .btn {
                          display: inline-block;
                          padding: 14px 28px;
                          margin-top: 25px;
                          color: #fff !important;
                          background: linear-gradient(90deg, #43cea2, #185a9d);
                          text-decoration: none;
                          border-radius: 30px;
                          font-weight: bold;
                          font-size: 16px;
                          box-shadow: 0px 4px 10px rgba(0,0,0,0.2);
                          transition: transform 0.3s ease, background 0.3s ease;
                          animation: pulse 1.8s infinite;
                        }
                        .btn:hover {
                          transform: scale(1.08);
                          background: linear-gradient(90deg, #185a9d, #43cea2);
                        }
                        @keyframes pulse {
                          0%% { box-shadow: 0 0 0 0 rgba(67, 206, 162, 0.6); }
                          70%% { box-shadow: 0 0 0 12px rgba(67, 206, 162, 0); }
                          100%% { box-shadow: 0 0 0 0 rgba(67, 206, 162, 0); }
                        }
                        .footer {
                          background: #f1f1f1;
                          text-align: center;
                          padding: 18px;
                          font-size: 13px;
                          color: #666;
                        }
                        .footer a {
                          color: #185a9d;
                          text-decoration: none;
                        }
                      </style>
                    </head>
                    <body>
                      <div class="container">
                        <div class="header">
                          <h1>✨ Daily Reminder ✨</h1>
                        </div>
                        <div class="content">
                          <h2>Hi %s,</h2>
                          <p>We noticed you haven’t added today’s <b>income & expenses</b> yet.<br>
                          Keeping track daily helps you <b>save more</b>, <b>spend wisely</b> and <b>achieve your goals faster</b>. 🚀</p>
                    
                          <div class="highlight">
                            <p>✔ Log your expenses in seconds<br>
                               ✔ Stay ahead with smart insights<br>
                               ✔ Build better financial habits daily</p>
                          </div>
                    
                          <p><b>Click the button below</b> to quickly open the app and update your records now 👇</p>
                    
                          <center>
                            <a class="btn" href="%s" target="_blank">Open App & Add Now</a>
                          </center>
                        </div>
                        <div class="footer">
                          <p>© 2025 Money Manager. All Rights Reserved.</p>
                          <p>Need help? Contact us at <a href="mailto:itskpawar@gmail.com">itskpawar@gmail.com</a></p>
                        </div>
                      </div>
                    </body>
                    </html>
                    """.formatted(profile.getFullName(), frontEndUrl);


            emailService.sendMail(profile.getEmail(), "Daily Reminder : Add your income and expenses", body);

            //  System.out.println("Email Send successfully " + profile.getEmail());
            log.info("Daily email sending job finished");
        }

    }


    //@Scheduled(cron = "0 * * * * *", zone = "IST")
    @Scheduled(cron = "0 0 23 * * *", zone = "IST") // 11pm
    public void sendDailyExpenseSummary() {
        log.info("send daily expense summary job started");

        List<ProfileEntity> profiles = profileRepository.findAll();
        log.info("Total profiles found: {}", profiles.size());

        for (ProfileEntity profile : profiles) {
            log.info("Checking expenses for profile={}, email={}", profile.getId(), profile.getEmail());
            List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForProfileIdOnDate(profile.getId(), LocalDate.now());

            log.info("Expenses found for {}: {}", profile.getEmail(), todaysExpenses.size());

            if (!todaysExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();

                table.append("<!DOCTYPE html>")
                        .append("<html>")
                        .append("<head>")
                        .append("<style>")
                        .append("table { border-collapse: collapse; width: 100%; font-family: Arial, sans-serif; }")
                        .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }")
                        .append("th { background: linear-gradient(90deg, #4CAF50, #45a049); color: white; }")
                        .append("tr:nth-child(even) { background-color: #f9f9f9; }")
                        .append("tr:hover { background-color: #f1f1f1; transform: scale(1.02); transition: all 0.3s ease; }")
                        .append(".header { font-size: 18px; margin-bottom: 15px; color: #333; font-weight: bold; }")
                        .append("</style>")
                        .append("</head>")
                        .append("<body>")
                        .append("<div class='header'>Daily Expense Summary - ").append(LocalDate.now()).append("</div>")
                        .append("<table>")
                        .append("<tr>")
                        .append("<th>#</th>")
                        .append("<th>Expense Name</th>")
                        .append("<th>Category</th>")
                        .append("<th>Amount (₹)</th>")
                        .append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : todaysExpenses) {
                    table.append("<tr>")
                            .append("<td>").append(i++).append("</td>")
                            .append("<td>").append(expense.getName()).append("</td>")
                            .append("<td>").append(expense.getCategoryId() != null ? expense.getCategoryId() : "N/A").append("</td>")
                            .append("<td>").append(expense.getAmount()).append("</td>")
                            .append("</tr>");
                }

                table.append("</table>")
                        .append("</body>")
                        .append("</html>");


                emailService.sendMail(
                        profile.getEmail(),
                        "Daily Expense Summary - " + LocalDate.now(),
                        table.toString()
                );

                System.out.println(table.toString());
                System.out.println("Mail Send to " + profile.getEmail());
            }
        }

        log.info("send daily expense summary job ended");
    }

}
