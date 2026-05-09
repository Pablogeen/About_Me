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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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
                        This token will expire in <strong style="color:#e8dfc8;">5 minutes</strong> for security reasons.
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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


    public String buildArticleApprovedEmailHtml(String articleTitle) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Your Article Has Been Approved</title>
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
                        We are delighted to inform you that your article has been
                        <strong style="color:#e8dfc8;">reviewed and approved</strong> by our editorial team.
                        Your work is now live and available to our community.
                      </p>
 
                      <p style="margin:0 0 32px 0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        Thank you for taking the time to contribute to
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#e8dfc8;"> &amp; </span>
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
                        Your insights and effort make our platform richer for everyone who reads it.
                        We truly appreciate your dedication and look forward to more contributions from you.
                      </p>
 
                      <!-- Divider -->
                      <div style="height:1px; background:#2a2a2a; margin:0 0 28px 0;"></div>
 
                      <!-- Article label -->
                      <p style="margin:0 0 8px 0; font-size:11px; letter-spacing:2px; color:#6b6355; text-transform:uppercase;">
                        Approved Article
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
                        Keep up the excellent work. We are proud to feature your voice on our platform.
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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


    public String buildArticleRejectedEmailHtml(String articleTitle) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Regarding Your Article Submission</title>
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
                        Thank you for submitting your article to
                        <span style="color:#c9b87a; font-weight:600;">BEN</span>
                        <span style="color:#e8dfc8;"> &amp; </span>
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
                        We sincerely appreciate the time and effort you invested in crafting your work.
                      </p>
 
                      <p style="margin:0 0 32px 0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        After careful review by our editorial team, we regret to inform you that your submission
                        does <strong style="color:#e8dfc8;">not meet our current contribution requirements</strong>.
                        This decision was not made lightly, and it does not diminish the value of your effort.
                        We encourage you to review our contribution guidelines, refine your work,
                        and consider resubmitting in the future.
                      </p>
 
                      <!-- Divider -->
                      <div style="height:1px; background:#2a2a2a; margin:0 0 28px 0;"></div>
 
                      <!-- Article label -->
                      <p style="margin:0 0 8px 0; font-size:11px; letter-spacing:2px; color:#6b6355; text-transform:uppercase;">
                        Reviewed Article
                      </p>
 
                      <!-- Article title box -->
                      <table cellpadding="0" cellspacing="0" border="0" style="margin:0 0 32px 0; width:100%%;">
                        <tr>
                          <td style="background:#1a1a1a; border-left:3px solid #6b6355; border-radius:2px; padding:14px 20px;">
                            <span style="font-family:Georgia, serif; font-size:16px; color:#a89e8a; font-style:italic;">
                              %s
                            </span>
                          </td>
                        </tr>
                      </table>
 
                      <p style="margin:0; font-size:15px; line-height:1.8; color:#a89e8a;">
                        We remain grateful for your interest in contributing and hope to see your work again
                        in the future. Do not be discouraged — every great writer refines their craft over time.
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
                        <span style="color:#c9b87a; font-weight:600;">CO</span>.
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

    public String buildContactRequestEmail(
            String email, String phoneNumber, String reasonForContact, String message) {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>New Contact Request - BEN & CO</title>
        </head>

        <body style="margin:0; padding:0; background:#161616; font-family:Arial, Helvetica, sans-serif;">

        <table width="100%%" border="0" cellspacing="0" cellpadding="0"
               style="padding:40px 0; background:#161616;">
            <tr>
                <td align="center">

                    <table width="700" border="0" cellspacing="0" cellpadding="0"
                           style="background:#050505; border:1px solid #1d1d1d;">

                        <!-- HEADER -->
                        <tr>
                            <td
                                style="
                                    background:linear-gradient(to right, #5e832f, #314d16);
                                    padding:55px 20px;
                                    text-align:center;
                                "
                            >

                                <h1
                                    style="
                                        margin:0;
                                        color:#eadfc7;
                                        font-size:30px;
                                        letter-spacing:8px;
                                        font-family:Georgia, serif;
                                        font-weight:bold;
                                    "
                                >
                                    BEN & CO
                                </h1>

                            </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                            <td style="padding:60px 60px 45px 60px;">

                                <h2
                                    style="
                                        margin-top:0;
                                        margin-bottom:35px;
                                        color:#f2ead8;
                                        font-size:26px;
                                        font-family:Georgia, serif;
                                    "
                                >
                                    Hello Admin,
                                </h2>

                                <p
                                    style="
                                        color:#a9a39b;
                                        font-size:18px;
                                        line-height:1.9;
                                        margin-bottom:30px;
                                    "
                                >
                                    We have received a new contact request through the
                                    <span style="color:#d9c27c; font-weight:bold;">
                                        BEN & CO
                                    </span>
                                    platform. The user has submitted the following details
                                    for your review.
                                </p>

                                <!-- DIVIDER -->
                                <div style="border-top:1px solid #2a2a2a; margin:45px 0;"></div>

                                <!-- SECTION TITLE -->
                                <p
                                    style="
                                        color:#8f8f8f;
                                        letter-spacing:5px;
                                        font-size:12px;
                                        margin-bottom:20px;
                                    "
                                >
                                    CONTACT REQUEST
                                </p>

                                <!-- EMAIL -->
                                <table width="100%%" border="0" cellspacing="0" cellpadding="0"
                                       style="
                                            background:#111111;
                                            margin-bottom:18px;
                                            border-left:4px solid #5e832f;
                                       ">
                                    <tr>
                                        <td style="padding:22px 25px;">

                                            <p style="margin:0; color:#8f8f8f; font-size:12px;">
                                                USER EMAIL
                                            </p>

                                            <p
                                                style="
                                                    margin:10px 0 0 0;
                                                    color:#ffffff;
                                                    font-size:21px;
                                                    font-style:italic;
                                                "
                                            >
                                                %s
                                            </p>

                                        </td>
                                    </tr>
                                </table>

                                <!-- PHONE -->
                                <table width="100%%" border="0" cellspacing="0" cellpadding="0"
                                       style="
                                            background:#111111;
                                            margin-bottom:18px;
                                            border-left:4px solid #5e832f;
                                       ">
                                    <tr>
                                        <td style="padding:22px 25px;">

                                            <p style="margin:0; color:#8f8f8f; font-size:12px;">
                                                PHONE NUMBER
                                            </p>

                                            <p
                                                style="
                                                    margin:10px 0 0 0;
                                                    color:#ffffff;
                                                    font-size:21px;
                                                    font-style:italic;
                                                "
                                            >
                                                %s
                                            </p>

                                        </td>
                                    </tr>
                                </table>

                                <!-- REASON -->
                                <table width="100%%" border="0" cellspacing="0" cellpadding="0"
                                       style="
                                            background:#111111;
                                            margin-bottom:35px;
                                            border-left:4px solid #5e832f;
                                       ">
                                    <tr>
                                        <td style="padding:22px 25px;">

                                            <p style="margin:0; color:#8f8f8f; font-size:12px;">
                                                REASON FOR CONTACT
                                            </p>

                                            <p
                                                style="
                                                    margin:10px 0 0 0;
                                                    color:#ffffff;
                                                    font-size:21px;
                                                    font-style:italic;
                                                    text-transform:uppercase;
                                                "
                                            >
                                                %s
                                            </p>

                                        </td>
                                    </tr>
                                </table>

                                <!-- MESSAGE TITLE -->
                                <p
                                    style="
                                        color:#8f8f8f;
                                        letter-spacing:5px;
                                        font-size:12px;
                                        margin-bottom:20px;
                                    "
                                >
                                    MESSAGE
                                </p>

                                <!-- MESSAGE BOX -->
                                <table width="100%%" border="0" cellspacing="0" cellpadding="0"
                                       style="
                                            background:#111111;
                                            border-left:4px solid #5e832f;
                                            margin-bottom:40px;
                                       ">
                                    <tr>
                                        <td style="padding:28px 25px;">

                                            <p
                                                style="
                                                    margin:0;
                                                    color:#c2c2c2;
                                                    font-size:18px;
                                                    line-height:1.9;
                                                "
                                            >
                                                %s
                                            </p>

                                        </td>
                                    </tr>
                                </table>

                                <p
                                    style="
                                        color:#a8a8a8;
                                        font-size:18px;
                                        line-height:1.9;
                                        margin-bottom:40px;
                                    "
                                >
                                    Keep up the excellent work. A prompt response to the
                                    user is recommended for better engagement.
                                </p>

                                <!-- FOOTER DIVIDER -->
                                <div style="border-top:1px solid #2a2a2a; margin-bottom:35px;"></div>

                                <!-- FOOTER -->
                                <p
                                    style="
                                        text-align:center;
                                        color:#8e8e8e;
                                        font-size:15px;
                                        margin-bottom:10px;
                                    "
                                >
                                    © 2026
                                    <span style="color:#d8c178; font-weight:bold;">
                                        BEN & CO
                                    </span>.
                                    All rights reserved.
                                </p>

                                <p
                                    style="
                                        text-align:center;
                                        color:#b1b1b1;
                                        font-size:14px;
                                        font-style:italic;
                                        margin:0;
                                    "
                                >
                                    This is an automated email. Please do not reply to this message.
                                </p>

                            </td>
                        </tr>

                    </table>

                </td>
            </tr>
        </table>

        </body>
        </html>
        """.formatted(email, phoneNumber, reasonForContact, message);
    }



}
