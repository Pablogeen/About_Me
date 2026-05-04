package com.ben.my_portfolio.notification.domain;

import org.springframework.stereotype.Component;

@Component
public class EmailBuilder {

    public String buildVerificationEmailHtml(String token) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Verify Your Email</title>
                </head>
                <body style="margin:0; padding:40px 16px; background:#1a1a1a; font-family:Arial, sans-serif;">

                <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                       style="max-width:600px; margin:0 auto; background:#111111; border-radius:4px; overflow:hidden; border:1px solid #2a2a2a;">

                  <!-- HEADER -->
                  <tr>
                    <td style="background:linear-gradient(135deg, #5a7a35 0%%, #3d5c20 60%%, #2e4a15 100%%); padding:32px 40px; text-align:center;">
                      <span style="font-family:Georgia, serif; font-size:26px; font-weight:700; letter-spacing:4px; color:#e8dfc8;">
                        BEN <span style="color:#c9b87a;">&amp;</span> CO
                      </span>
                    </td>
                  </tr>

                  <!-- BODY -->
                  <tr>
                    <td style="padding:44px 40px 32px 40px;">

                      <p style="margin:0 0 20px 0; font-size:20px; font-weight:700; color:#e8dfc8; font-family:Georgia, serif;">
                        Hello,
                      </p>

                      <p style="margin:0 0 32px 0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        Thank you for registering with
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#e8dfc8;"> &amp; </span>
                        <span style="color:#3d5c20; font-weight:600; background:#c9b87a; padding:0 3px; border-radius:2px;">CO</span>.
                        Complete your registration by verifying your email address.
                        Copy and paste the token below to get your account verified:
                      </p>

                      <!-- Token box -->
                      <table cellpadding="0" cellspacing="0" border="0" style="margin:0 0 32px 0;">
                        <tr>
                          <td style="background:linear-gradient(135deg, #5a7a35 0%%, #3d5c20 100%%); border-radius:4px; padding:18px 36px; text-align:center;">
                            <span style="font-family:Arial, sans-serif; font-size:28px; font-weight:700; letter-spacing:10px; color:#e8dfc8; display:block;">
                              %s
                            </span>
                          </td>
                        </tr>
                      </table>

                      <p style="margin:0 0 20px 0; font-size:14px; line-height:1.7; color:#a89e8a;">
                        This token will expire in <strong style="color:#e8dfc8;">15 minutes</strong> for security reasons.
                      </p>

                      <p style="margin:0; font-size:14px; line-height:1.7; color:#a89e8a;">
                        If you didn't create an account with us, you can safely ignore this email.
                      </p>

                    </td>
                  </tr>

                  <!-- DIVIDER -->
                  <tr>
                    <td style="padding:0 40px;">
                      <div style="height:1px; background:#2a2a2a;"></div>
                    </td>
                  </tr>

                  <!-- FOOTER -->
                  <tr>
                    <td style="background:#0e0e0e; padding:24px 40px; text-align:center;">
                      <p style="margin:0 0 6px 0; font-size:12px; color:#6b6355;">
                        &copy; 2026
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#6b6355;"> &amp; </span>
                        <span style="color:#3d5c20; font-weight:600; background:#c9b87a; padding:0 2px; border-radius:2px;">CO</span>.
                        All rights reserved.
                      </p>
                      <p style="margin:0; font-size:11px; color:#4a4035; font-style:italic;">
                        This is an automated email. Please do not reply to this message.
                      </p>
                    </td>
                  </tr>

                </table>

                </body>
                </html>
                """.formatted(token);
    }

    public String buildContributionEmailHtml(String articleTitle) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Thank You for Your Contribution</title>
                </head>
                <body style="margin:0; padding:40px 16px; background:#1a1a1a; font-family:Arial, sans-serif;">

                <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                       style="max-width:600px; margin:0 auto; background:#111111; border-radius:4px; overflow:hidden; border:1px solid #2a2a2a;">

                  <!-- HEADER -->
                  <tr>
                    <td style="background:linear-gradient(135deg, #5a7a35 0%%, #3d5c20 60%%, #2e4a15 100%%); padding:32px 40px; text-align:center;">
                      <span style="font-family:Georgia, serif; font-size:26px; font-weight:700; letter-spacing:4px; color:#e8dfc8;">
                        BEN <span style="color:#c9b87a;">&amp;</span> CO
                      </span>
                    </td>
                  </tr>

                  <!-- BODY -->
                  <tr>
                    <td style="padding:44px 40px 32px 40px;">

                      <p style="margin:0 0 20px 0; font-size:20px; font-weight:700; color:#e8dfc8; font-family:Georgia, serif;">
                        Hello,
                      </p>

                      <p style="margin:0 0 20px 0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        Thank you for your contribution to
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#e8dfc8;"> &amp; </span>
                        <span style="color:#3d5c20; font-weight:600; background:#c9b87a; padding:0 3px; border-radius:2px;">CO</span>.
                        We have successfully received your article and truly appreciate
                        the time and effort you put into crafting it.
                      </p>

                      <p style="margin:0 0 32px 0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        Your submission is currently <strong style="color:#e8dfc8;">under review</strong> by our editorial team.
                        We carefully evaluate every article to ensure it meets our quality standards
                        before it goes live. You can expect a decision within a short period,
                        and we will notify you promptly once a review has been completed.
                      </p>

                      <!-- Divider -->
                      <div style="height:1px; background:#2a2a2a; margin:0 0 28px 0;"></div>

                      <!-- Article label -->
                      <p style="margin:0 0 8px 0; font-size:11px; letter-spacing:2px; color:#6b6355; text-transform:uppercase;">
                        Submitted Article
                      </p>

                      <!-- Article title box -->
                      <table cellpadding="0" cellspacing="0" border="0" style="margin:0 0 32px 0; width:100%%;">
                        <tr>
                          <td style="background:#1a1a1a; border-left:3px solid #5a7a35; border-radius:2px; padding:14px 20px;">
                            <span style="font-family:Georgia, serif; font-size:16px; color:#e8dfc8; font-style:italic;">
                              %s
                            </span>
                          </td>
                        </tr>
                      </table>

                      <p style="margin:0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        We look forward to sharing your work with our community.
                        Thank you once again for being a valued contributor.
                      </p>

                    </td>
                  </tr>

                  <!-- DIVIDER -->
                  <tr>
                    <td style="padding:0 40px;">
                      <div style="height:1px; background:#2a2a2a;"></div>
                    </td>
                  </tr>

                  <!-- FOOTER -->
                  <tr>
                    <td style="background:#0e0e0e; padding:24px 40px; text-align:center;">
                      <p style="margin:0 0 6px 0; font-size:12px; color:#6b6355;">
                        &copy; 2026
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#6b6355;"> &amp; </span>
                        <span style="color:#3d5c20; font-weight:600; background:#c9b87a; padding:0 2px; border-radius:2px;">CO</span>.
                        All rights reserved.
                      </p>
                      <p style="margin:0; font-size:11px; color:#4a4035; font-style:italic;">
                        This is an automated email. Please do not reply to this message.
                      </p>
                    </td>
                  </tr>

                </table>

                </body>
                </html>
                """.formatted(articleTitle);
    }

    public String buildAdminNotificationEmailHtml(String articleTitle, String contributorEmail) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>New Article Submission</title>
                </head>
                <body style="margin:0; padding:40px 16px; background:#1a1a1a; font-family:Arial, sans-serif;">
 
                <table width="100%%" cellpadding="0" cellspacing="0" border="0"
                       style="max-width:600px; margin:0 auto; background:#111111; border-radius:4px; overflow:hidden; border:1px solid #2a2a2a;">
 
                  <!-- HEADER -->
                  <tr>
                    <td style="background:linear-gradient(135deg, #5a7a35 0%%, #3d5c20 60%%, #2e4a15 100%%); padding:32px 40px; text-align:center;">
                      <span style="font-family:Georgia, serif; font-size:26px; font-weight:700; letter-spacing:4px; color:#e8dfc8;">
                        BEN <span style="color:#c9b87a;">&amp;</span> CO
                      </span>
                      <p style="margin:10px 0 0 0; font-size:11px; letter-spacing:3px; color:#a89e8a; text-transform:uppercase;">
                        Admin Notification
                      </p>
                    </td>
                  </tr>
 
                  <!-- BODY -->
                  <tr>
                    <td style="padding:44px 40px 32px 40px;">
 
                      <p style="margin:0 0 20px 0; font-size:20px; font-weight:700; color:#e8dfc8; font-family:Georgia, serif;">
                        Hello, Admin.
                      </p>
 
                      <p style="margin:0 0 32px 0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        A new article has been submitted to
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#e8dfc8;"> &amp; </span>
                        <span style="color:#3d5c20; font-weight:600; background:#c9b87a; padding:0 3px; border-radius:2px;">CO</span>
                        and is currently awaiting your review and approval.
                        Please log in to the admin panel to evaluate the submission at your earliest convenience.
                      </p>
 
                      <!-- Divider -->
                      <div style="height:1px; background:#2a2a2a; margin:0 0 28px 0;"></div>
 
                      <!-- Submission details label -->
                      <p style="margin:0 0 16px 0; font-size:11px; letter-spacing:2px; color:#6b6355; text-transform:uppercase;">
                        Submission Details
                      </p>
 
                      <!-- Article title -->
                      <p style="margin:0 0 8px 0; font-size:11px; letter-spacing:1px; color:#6b6355; text-transform:uppercase;">
                        Article Title
                      </p>
                      <table cellpadding="0" cellspacing="0" border="0" style="margin:0 0 20px 0; width:100%%;">
                        <tr>
                          <td style="background:#1a1a1a; border-left:3px solid #5a7a35; border-radius:2px; padding:14px 20px;">
                            <span style="font-family:Georgia, serif; font-size:16px; color:#e8dfc8; font-style:italic;">
                              %s
                            </span>
                          </td>
                        </tr>
                      </table>
 
                      <!-- Contributor email -->
                      <p style="margin:0 0 8px 0; font-size:11px; letter-spacing:1px; color:#6b6355; text-transform:uppercase;">
                        Submitted By
                      </p>
                      <table cellpadding="0" cellspacing="0" border="0" style="margin:0 0 32px 0; width:100%%;">
                        <tr>
                          <td style="background:#1a1a1a; border-left:3px solid #c9b87a; border-radius:2px; padding:14px 20px;">
                            <span style="font-family:Arial, sans-serif; font-size:14px; color:#a89e8a;">
                              %s
                            </span>
                          </td>
                        </tr>
                      </table>
 
                      <p style="margin:0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        Kindly ensure the article is reviewed promptly so the contributor
                        can be notified of the outcome in a timely manner.
                      </p>
 
                    </td>
                  </tr>
 
                  <!-- DIVIDER -->
                  <tr>
                    <td style="padding:0 40px;">
                      <div style="height:1px; background:#2a2a2a;"></div>
                    </td>
                  </tr>
 
                  <!-- FOOTER -->
                  <tr>
                    <td style="background:#0e0e0e; padding:24px 40px; text-align:center;">
                      <p style="margin:0 0 6px 0; font-size:12px; color:#6b6355;">
                        &copy; 2026
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#6b6355;"> &amp; </span>
                        <span style="color:#3d5c20; font-weight:600; background:#c9b87a; padding:0 2px; border-radius:2px;">CO</span>.
                        All rights reserved.
                      </p>
                      <p style="margin:0; font-size:11px; color:#4a4035; font-style:italic;">
                        This is an automated email. Please do not reply to this message.
                      </p>
                    </td>
                  </tr>
 
                </table>
 
                </body>
                </html>
                """.formatted(articleTitle, contributorEmail);
    }

}
