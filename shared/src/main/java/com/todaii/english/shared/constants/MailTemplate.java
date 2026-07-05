// package com.todaii.english.shared.constants;
//
// public class MailTemplate {
//  public static final String VERIFICATION_EMAIL_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Verify Email</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #10b981; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Todaii English</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <h2 style="color: #333333; margin-top: 0; font-size: 20px;">Verify
// Your Email Address</h2>
//			                            <p style="color: #666666; font-size: 16px; line-height:
// 1.6;">Hello,</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Thank
// you for starting your journey with Todaii English. To complete your registration, please use the
// verification code below:</p>
//
//			                            <div style="background-color: #f0fdf4; border: 2px dashed #10b981;
// border-radius: 6px; padding: 20px; margin: 30px 0; text-align: center;">
//			                                <span style="font-size: 32px; font-weight: bold;
// letter-spacing: 5px; color: #10b981; font-family: monospace;">{verificationCode}</span>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 14px; line-height: 1.6;">This
// code helps us ensure your account is secure. It will expire in <strong>15 minutes</strong>.</p>
//			                            <p style="color: #999999; font-size: 14px; margin-top: 30px;">If
// you didn't sign up for Todaii English, you can safely ignore this email.</p>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String PASSWORD_RESET_SUCCESS_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Password Reset Successful</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #10b981; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Security Update</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px; text-align: center;">
//			                            <div style="background-color: #10b981; color: white; width: 60px;
// height: 60px; line-height: 60px; border-radius: 50%; display: inline-block; font-size: 30px;
// margin-bottom: 20px;">✓</div>
//			                            <h2 style="color: #333333; margin: 0 0 20px 0; font-size:
// 20px;">Password Reset Successfully</h2>
//			                            <p style="color: #666666; font-size: 16px; line-height:
// 1.6;">Hello,</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Your
// password has been successfully updated. You can now log in with your new credentials.</p>
//
//			                            <div style="text-align: left; background-color: #f8f9fa; padding:
// 15px; border-radius: 5px; margin: 25px 0;">
//			                                <p style="margin: 0 0 10px 0; font-weight: bold; color:
// #444;">Security Tips:</p>
//			                                <ul style="color: #666666; font-size: 14px; padding-left: 20px;
// margin: 0;">
//			                                    <li style="margin-bottom: 5px;">Never share your password
// with anyone.</li>
//			                                    <li style="margin-bottom: 5px;">Enable two-factor
// authentication if available.</li>
//			                                </ul>
//			                            </div>
//
//			                            <p style="color: #ef4444; font-size: 14px; margin-top: 20px;">If
// you did not make this change, please contact our support team immediately.</p>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String PASSWORD_RESET_REQUEST_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Reset Password</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #10b981; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Reset Password</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #666666; font-size: 16px; line-height:
// 1.6;">Hello,</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">We
// received a request to reset your password for your Todaii English account. If you didn't ask for
// this, you can ignore this email.</p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="{resetURL}" style="background-color: #10b981; color:
// white; padding: 14px 28px; text-decoration: none; border-radius: 6px; font-weight: bold;
// font-size: 16px; display: inline-block; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">Reset My
// Password</a>
//			                            </div>
//
//			                            <p style="color: #999999; font-size: 14px; text-align:
// center;">This link is valid for <strong>1 hour</strong>.</p>
//			                            <hr style="border: none; border-top: 1px solid #eeeeee; margin:
// 30px 0;">
//			                            <p style="color: #999999; font-size: 12px;">Button not working?
// Copy and paste this link into your browser:<br><a href="{resetURL}" style="color:
// #10b981;">{resetURL}</a></p>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String WELCOME_EMAIL_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Welcome to Todaii</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #10b981; padding: 40px 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0 0 10px 0; font-size:
// 28px;">Welcome Aboard! 🎉</h1>
//			                            <p style="color: #e6fffa; margin: 0; font-size: 16px;">Let's master
// English together</p>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">We
// are thrilled to have you join the Todaii English community! Your account has been successfully
// created with the email:</p>
//
//			                            <div style="background-color: #f3f4f6; padding: 15px;
// border-radius: 6px; text-align: center; margin: 20px 0;">
//			                                <span style="color: #374151; font-weight: 600; font-size:
// 16px;">{email}</span>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">You
// now have full access to our learning resources. Ready to start your first lesson?</p>
//
//			                            <div style="text-align: center; margin-top: 30px;">
//			                                <a href="#" style="background-color: #333333; color: white;
// padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">Go to
// Dashboard</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String ACCOUNT_BANNED_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Account Suspended</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #ef4444; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Account Suspended</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//
//			                            <div style="background-color: #fef2f2; border-left: 4px solid
// #ef4444; padding: 15px; margin-bottom: 25px;">
//			                                <p style="color: #991b1b; margin: 0; font-size: 16px;">Your
// account has been suspended due to a violation of our Terms of Service.</p>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
//			                                While suspended, you will not be able to access your courses,
// history, or login to the platform.
//			                            </p>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;
// margin-top: 20px;">
//			                                If you believe this is a mistake, please contact our support
// team immediately for a review of your case.
//			                            </p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="mailto:{contactMail}" style="background-color:
// #333333; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px;
// font-weight: bold;">Contact Support</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String ACCOUNT_UNBANNED_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Account Restored</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #10b981; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Account Restored</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//
//			                            <div style="background-color: #ecfdf5; border-left: 4px solid
// #10b981; padding: 15px; margin-bottom: 25px;">
//			                                <p style="color: #065f46; margin: 0; font-size: 16px;">Good
// news! Your account has been successfully reactivated.</p>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
//			                                We have reviewed your account status and lifted the suspension.
// You now have full access to all Todaii English features, course history, and learning materials
// again.
//			                            </p>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;
// margin-top: 20px;">
//			                                We apologize for any inconvenience this may have caused.
// Welcome back!
//			                            </p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="/client/login" style="background-color: #333333;
// color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight:
// bold;">Log In Now</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String ACCOUNT_DELETED_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Account Deleted</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #4b5563; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Goodbye for now</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//
//			                            <div style="background-color: #f3f4f6; border-left: 4px solid
// #4b5563; padding: 15px; margin-bottom: 25px;">
//			                                <p style="color: #1f2937; margin: 0; font-size: 16px;">Your
// account has been successfully deleted as requested.</p>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
//			                                We confirm that your personal data, learning history, and
// vocabulary lists have been permanently removed from our servers.
//			                            </p>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;
// margin-top: 20px;">
//			                                We are sorry to see you go, but we hope to see you again in the
// future. You are always welcome to create a new account whenever you're ready to continue your
// English journey.
//			                            </p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="{homepageURL}" style="background-color: #ffffff;
// color: #4b5563; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;
// border: 2px solid #4b5563;">Visit Todaii English</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  // --- TEMPLATE: ADMIN TẠO TÀI KHOẢN (GỬI KÈM MẬT KHẨU) ---
//  public static final String ACCOUNT_CREATED_BY_ADMIN_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Account Created</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #10b981; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Account Created</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
//			                                An account has been created for you by the Todaii English
// administration team. You can now access the system using the credentials below:
//			                            </p>
//
//			                            <div style="background-color: #f3f4f6; border: 1px solid #e5e7eb;
// border-radius: 6px; padding: 20px; margin: 25px 0;">
//			                                <table width="100%" cellpadding="0" cellspacing="0">
//			                                    <tr>
//			                                        <td style="padding-bottom: 10px; color: #666666;
// font-size: 14px;">Email / Username:</td>
//			                                    </tr>
//			                                    <tr>
//			                                        <td style="padding-bottom: 20px; color: #333333;
// font-weight: bold; font-size: 16px;">{email}</td>
//			                                    </tr>
//			                                    <tr>
//			                                        <td style="padding-bottom: 10px; color: #666666;
// font-size: 14px;">Temporary Password:</td>
//			                                    </tr>
//			                                    <tr>
//			                                        <td style="color: #333333; font-weight: bold;
// font-size: 18px; font-family: monospace; letter-spacing: 1px;">{password}</td>
//			                                    </tr>
//			                                </table>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 14px; line-height: 1.6;">
//			                                <strong>Security Note:</strong> Please verify your information
// and <a href="{resetPasswordURL}" style="color: #10b981; text-decoration: none;">change your
// password</a> immediately after your first login.
//			                            </p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="{loginURL}" style="background-color: #333333; color:
// white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">Login
// Now</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  // --- TEMPLATE: ADMIN CẬP NHẬT THÔNG TIN TÀI KHOẢN ---
//  public static final String ACCOUNT_UPDATED_BY_ADMIN_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Account Updated</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #2563eb; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">Account Updated</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//
//			                            <div style="background-color: #eff6ff; border-left: 4px solid
// #2563eb; padding: 15px; margin-bottom: 25px;">
//			                                <p style="color: #1e40af; margin: 0; font-size: 16px;">This is
// a notification that your account details have been updated by an administrator.</p>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
//			                                The changes have been applied successfully. You can review your
// profile information by visiting your account settings.
//			                            </p>
//
//			                             <p style="color: #666666; font-size: 16px; line-height: 1.6;">
//			                                If you have any questions regarding these changes, please
// contact the support team.
//			                            </p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="{profileURL}" style="background-color: #333333; color:
// white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">View My
// Profile</a>
//			                            </div>
//
//			                            <hr style="border: none; border-top: 1px solid #eeeeee; margin:
// 30px 0;">
//			                            <p style="color: #999999; font-size: 14px; margin: 0;">Note: If you
// did not request any support or changes, please reply to this email immediately.</p>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String STREAK_RISK_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Keep Your Streak Alive!</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #f59e0b; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">🔥 Streak in Danger!</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Your
// active study streak of <strong>{streak} days</strong> is about to end today! Don't let your hard
// work go to waste.</p>
//
//			                            <div style="background-color: #fffbeb; border-left: 4px solid
// #f59e0b; padding: 15px; margin: 25px 0;">
//			                                <p style="color: #b45309; margin: 0; font-size: 16px;
// font-weight: 600;">Just 5 minutes of study today will keep your streak alive!</p>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height:
// 1.6;">Consistency is the key to mastering English. Spend a few minutes reading an article,
// watching a video, or answering practice questions now.</p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="#" style="background-color: #f59e0b; color: white;
// padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display:
// inline-block;">Resume Learning Now</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String EXAM_COUNTDOWN_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Your Exam is Approaching!</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #3b82f6; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">⏰ The Countdown is On!</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Your
// registered TOEIC exam is just <strong>{daysLeft} days</strong> away!</p>
//
//			                            <div style="background-color: #eff6ff; border: 1px solid #bfdbfe;
// padding: 20px; margin: 25px 0; border-radius: 6px; text-align: center;">
//			                                <span style="font-size: 14px; color: #1d4ed8; font-weight: 600;
// display: block; margin-bottom: 5px;">DAYS REMAINING</span>
//			                                <span style="font-size: 48px; font-weight: bold; color:
// #1d4ed8; display: block; line-height: 1;">{daysLeft}</span>
//			                                <span style="font-size: 14px; color: #60a5fa; display: block;
// margin-top: 10px;">Target Score: <strong>TOEIC {targetScore}</strong></span>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Now
// is the perfect time to review your weaknesses and take full practice tests to build your
// confidence and pacing.</p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="#" style="background-color: #3b82f6; color: white;
// padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display:
// inline-block;">Start Final Prep</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
//
//  public static final String STUDY_PLAN_TEMPLATE =
//      """
//			<!DOCTYPE html>
//			<html>
//			<head>
//			    <meta charset="UTF-8">
//			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
//			    <title>Your AI Personalized Study Plan</title>
//			</head>
//			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial,
// sans-serif; background-color: #f4f4f7; color: #333333;">
//			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7;
// padding: 40px 0;">
//			        <tr>
//			            <td align="center">
//			                <table width="600" cellpadding="0" cellspacing="0" style="background-color:
// #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width:
// 100%;">
//			                    <tr>
//			                        <td style="background-color: #8b5cf6; padding: 30px; text-align:
// center;">
//			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight:
// 600;">📋 Your Personalized AI Study Plan</h1>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="padding: 40px 30px;">
//			                            <p style="color: #333333; font-size: 18px; font-weight: bold;
// margin-bottom: 20px;">Hello {name},</p>
//			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Our
// AI coach has analyzed your learning performance and generated a tailored study plan for the next
// 2 days to optimize your prep.</p>
//
//			                            <div style="background-color: #f5f3ff; border: 1px solid #ddd6fe;
// border-radius: 6px; padding: 25px; margin: 25px 0;">
//			                                <h3 style="color: #7c3aed; margin-top: 0; margin-bottom: 15px;
// font-size: 18px;">Your Plan Details:</h3>
//			                                <div style="color: #4b5563; font-size: 15px; line-height: 1.6;
// white-space: pre-wrap;">{planContent}</div>
//			                            </div>
//
//			                            <p style="color: #666666; font-size: 16px; line-height:
// 1.6;">Follow this plan to focus on your weakest areas and maximize your score efficiency!</p>
//
//			                            <div style="text-align: center; margin: 35px 0;">
//			                                <a href="#" style="background-color: #8b5cf6; color: white;
// padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display:
// inline-block;">Open Study Dashboard</a>
//			                            </div>
//			                        </td>
//			                    </tr>
//			                    <tr>
//			                        <td style="background-color: #f9fafb; padding: 20px; text-align:
// center; border-top: 1px solid #eeeeee;">
//			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025
// Todaii English. All rights reserved.</p>
//			                        </td>
//			                    </tr>
//			                </table>
//			            </td>
//			        </tr>
//			    </table>
//			</body>
//			</html>
//			""";
// }

package com.todaii.english.shared.constants;

/**
 * Unified email template system for Todaii English.
 *
 * <p>Design principles: - One consistent header/footer shell across every template. - Single ink
 * color for headings/buttons, single muted accent for eyebrow labels. - No emoji, no gradients, no
 * per-template color banners. - Status/context conveyed via a subtle left-border callout, not
 * saturated color blocks. - All placeholder variables preserved exactly as before.
 */
public class MailTemplate {

  // ---------------------------------------------------------------------
  // Shared design tokens (kept as comments for reference — inlined below
  // since Java text blocks can't share CSS across constants):
  //
  // Page bg:      #f4f4f5
  // Card bg:      #ffffff / border #e5e5e5 / radius 10px
  // Ink (heading, buttons): #18181b
  // Body text:    #52525b
  // Muted text:   #a1a1aa
  // Border:       #e5e5e5
  // Accent (eyebrow label, links, code): #0f6b3f
  // Danger text:  #9f1c1c   Warning text: #92400e   (used only as text, on neutral bg)
  // Font stack:   -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial,
  // sans-serif
  // ---------------------------------------------------------------------

  public static final String VERIFICATION_EMAIL_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Verify your email</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Verify email</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Confirm your email address</h1>
                                              <p style="margin: 0 0 16px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Hi,</p>
                                              <p style="margin: 0 0 24px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Thanks for creating an account with Todaii English. Enter the verification code below to complete your registration.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #e5e5e5; border-radius: 8px; background-color: #fafafa; margin-bottom: 20px;">
                                                  <tr>
                                                      <td style="padding: 22px; text-align: center;">
                                                          <span style="font-family: 'SFMono-Regular', Consolas, monospace; font-size: 28px; font-weight: 600; letter-spacing: 6px; color: #18181b;">{verificationCode}</span>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 4px 0; font-size: 14px; line-height: 1.6; color: #71717a;">This code expires in 15 minutes.</p>
                                              <p style="margin: 0; font-size: 14px; line-height: 1.6; color: #a1a1aa;">If you didn't request this, you can safely ignore this email.</p>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String PASSWORD_RESET_SUCCESS_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Password changed</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Password changed</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Your password was updated</h1>
                                              <p style="margin: 0 0 16px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Hi,</p>
                                              <p style="margin: 0 0 24px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Your password has been changed successfully. You can now sign in using your new credentials.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border-left: 3px solid #18181b; background-color: #fafafa; border-radius: 4px; margin-bottom: 24px;">
                                                  <tr>
                                                      <td style="padding: 16px 18px;">
                                                          <p style="margin: 0 0 6px 0; font-size: 13px; font-weight: 700; color: #18181b;">Security tips</p>
                                                          <p style="margin: 0 0 4px 0; font-size: 14px; line-height: 1.6; color: #52525b;">Never share your password with anyone.</p>
                                                          <p style="margin: 0; font-size: 14px; line-height: 1.6; color: #52525b;">Enable two-factor authentication if available.</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0; font-size: 14px; line-height: 1.6; color: #9f1c1c;">If you didn't make this change, please contact our support team immediately.</p>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String PASSWORD_RESET_REQUEST_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Reset your password</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Reset password</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Reset your password</h1>
                                              <p style="margin: 0 0 16px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Hi,</p>
                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">We received a request to reset the password for your Todaii English account. If you didn't make this request, you can ignore this email.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto 28px auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="{resetURL}" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Reset my password</a>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 24px 0; font-size: 13px; color: #a1a1aa; text-align: center;">This link is valid for 1 hour.</p>
                                              <hr style="border: none; border-top: 1px solid #e5e5e5; margin: 0 0 20px 0;">
                                              <p style="margin: 0; font-size: 13px; line-height: 1.6; color: #a1a1aa;">If the button doesn't work, copy and paste this link into your browser:<br><a href="{resetURL}" style="color: #0f6b3f; word-break: break-all;">{resetURL}</a></p>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String WELCOME_EMAIL_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Welcome to Todaii English</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Welcome</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}, welcome aboard</h1>
                                              <p style="margin: 0 0 20px 0; font-size: 15px; line-height: 1.65; color: #52525b;">We're glad to have you join the Todaii English community. Your account has been created with the email address below.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #e5e5e5; border-radius: 8px; background-color: #fafafa; margin-bottom: 24px;">
                                                  <tr>
                                                      <td style="padding: 16px; text-align: center;">
                                                          <span style="font-size: 15px; font-weight: 600; color: #18181b;">{email}</span>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">You now have full access to our learning resources. Ready for your first lesson?</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="#" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Go to dashboard</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String ACCOUNT_BANNED_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Account suspended</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #9f1c1c; text-transform: uppercase;">Account suspended</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}</h1>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border-left: 3px solid #9f1c1c; background-color: #fafafa; border-radius: 4px; margin-bottom: 20px;">
                                                  <tr>
                                                      <td style="padding: 16px 18px;">
                                                          <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #18181b;">Your account has been suspended due to a violation of our Terms of Service.</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 16px 0; font-size: 15px; line-height: 1.65; color: #52525b;">While suspended, you will not be able to access your courses, history, or sign in to the platform.</p>
                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">If you believe this is a mistake, please contact our support team for a review of your case.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="mailto:{contactMail}" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Contact support</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String ACCOUNT_UNBANNED_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Account restored</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Account restored</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}</h1>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border-left: 3px solid #0f6b3f; background-color: #fafafa; border-radius: 4px; margin-bottom: 20px;">
                                                  <tr>
                                                      <td style="padding: 16px 18px;">
                                                          <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #18181b;">Good news — your account has been reactivated.</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 16px 0; font-size: 15px; line-height: 1.65; color: #52525b;">We've reviewed your account status and lifted the suspension. You now have full access to all Todaii English features, course history, and learning materials again.</p>
                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">We apologize for any inconvenience this may have caused. Welcome back.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="/client/login" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Log in now</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String ACCOUNT_DELETED_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Account deleted</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #71717a; text-transform: uppercase;">Account deleted</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}</h1>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border-left: 3px solid #71717a; background-color: #fafafa; border-radius: 4px; margin-bottom: 20px;">
                                                  <tr>
                                                      <td style="padding: 16px 18px;">
                                                          <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #18181b;">Your account has been deleted, as requested.</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 16px 0; font-size: 15px; line-height: 1.65; color: #52525b;">We confirm that your personal data, learning history, and vocabulary lists have been permanently removed from our servers.</p>
                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">We're sorry to see you go. You're always welcome to create a new account whenever you're ready to continue your English journey.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; border: 1px solid #18181b;">
                                                          <a href="{homepageURL}" style="display: inline-block; padding: 12px 27px; font-size: 14px; font-weight: 600; color: #18181b; text-decoration: none;">Visit Todaii English</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  // --- TEMPLATE: ADMIN TẠO TÀI KHOẢN (GỬI KÈM MẬT KHẨU) ---
  public static final String ACCOUNT_CREATED_BY_ADMIN_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Account created</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Account created</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}</h1>
                                              <p style="margin: 0 0 24px 0; font-size: 15px; line-height: 1.65; color: #52525b;">An account has been created for you by the Todaii English administration team. You can sign in using the credentials below.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #e5e5e5; border-radius: 8px; background-color: #fafafa; margin-bottom: 24px;">
                                                  <tr>
                                                      <td style="padding: 20px;">
                                                          <p style="margin: 0 0 4px 0; font-size: 12px; color: #71717a;">Email / Username</p>
                                                          <p style="margin: 0 0 16px 0; font-size: 15px; font-weight: 600; color: #18181b;">{email}</p>
                                                          <p style="margin: 0 0 4px 0; font-size: 12px; color: #71717a;">Temporary password</p>
                                                          <p style="margin: 0; font-family: 'SFMono-Regular', Consolas, monospace; font-size: 17px; font-weight: 600; letter-spacing: 1px; color: #18181b;">{password}</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 28px 0; font-size: 14px; line-height: 1.6; color: #52525b;">For security, please <a href="{resetPasswordURL}" style="color: #0f6b3f; text-decoration: none; font-weight: 600;">change your password</a> immediately after your first login.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="{loginURL}" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Log in now</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  // --- TEMPLATE: ADMIN CẬP NHẬT THÔNG TIN TÀI KHOẢN ---
  public static final String ACCOUNT_UPDATED_BY_ADMIN_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Account updated</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #71717a; text-transform: uppercase;">Account updated</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}</h1>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border-left: 3px solid #18181b; background-color: #fafafa; border-radius: 4px; margin-bottom: 20px;">
                                                  <tr>
                                                      <td style="padding: 16px 18px;">
                                                          <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #18181b;">Your account details were updated by an administrator.</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">The changes have been applied successfully. You can review your profile information in your account settings.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto 28px auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="{profileURL}" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">View my profile</a>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <hr style="border: none; border-top: 1px solid #e5e5e5; margin: 0 0 20px 0;">
                                              <p style="margin: 0; font-size: 13px; color: #a1a1aa;">If you didn't request these changes, please reply to this email immediately.</p>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String STREAK_RISK_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Your streak is at risk</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #92400e; text-transform: uppercase;">Study reminder</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}, your streak is at risk</h1>
                                              <p style="margin: 0 0 20px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Your active study streak of <strong style="color: #18181b;">{streak} days</strong> is about to end today. Don't let your progress reset.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border-left: 3px solid #92400e; background-color: #fafafa; border-radius: 4px; margin-bottom: 24px;">
                                                  <tr>
                                                      <td style="padding: 16px 18px;">
                                                          <p style="margin: 0; font-size: 15px; line-height: 1.6; color: #18181b;">Just five minutes of study today will keep your streak alive.</p>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Consistency is the key to mastering English. Spend a few minutes reading an article, watching a video, or answering practice questions now.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="#" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Resume learning</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  public static final String EXAM_COUNTDOWN_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Your exam is approaching</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Exam reminder</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}, the countdown is on</h1>
                                              <p style="margin: 0 0 24px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Your registered TOEIC exam is coming up soon.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #e5e5e5; border-radius: 8px; background-color: #fafafa; margin-bottom: 24px;">
                                                  <tr>
                                                      <td style="padding: 24px; text-align: center;">
                                                          <span style="display: block; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #71717a; text-transform: uppercase; margin-bottom: 6px;">Days remaining</span>
                                                          <span style="display: block; font-size: 44px; font-weight: 700; color: #18181b; line-height: 1;">{daysLeft}</span>
                                                          <span style="display: block; font-size: 13px; color: #71717a; margin-top: 10px;">Target score: <strong style="color: #18181b;">TOEIC {targetScore}</strong></span>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Now is the perfect time to review your weak areas and take full practice tests to build pacing and confidence.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="#" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Start final prep</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;

  // NOTE: {planContent} is injected as raw HTML (not escaped, not pre-wrapped).
  // The wrapper below resets common tag margins so arbitrary rich content
  // (headings, lists, paragraphs) inherits the template's typography.
  public static final String STUDY_PLAN_TEMPLATE =
      """
                  <!DOCTYPE html>
                  <html>
                  <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>Your personalized study plan</title>
                  </head>
                  <body style="margin: 0; padding: 0; background-color: #f4f4f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                      <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f5; padding: 40px 0;">
                          <tr>
                              <td align="center">
                                  <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #e5e5e5; border-radius: 10px; max-width: 100%;">
                                      <tr>
                                          <td style="padding: 28px 40px; border-bottom: 1px solid #e5e5e5;">
                                              <span style="font-size: 15px; font-weight: 700; color: #18181b; letter-spacing: -0.01em;">Todaii English</span>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 40px;">
                                              <p style="margin: 0 0 10px 0; font-size: 12px; font-weight: 700; letter-spacing: 0.08em; color: #0f6b3f; text-transform: uppercase;">Study plan</p>
                                              <h1 style="margin: 0 0 16px 0; font-size: 20px; font-weight: 600; color: #18181b;">Hello {name}, here's your plan</h1>
                                              <p style="margin: 0 0 24px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Our AI coach reviewed your learning performance and put together a tailored plan for the next two days.</p>

                                              <table width="100%" cellpadding="0" cellspacing="0" style="border: 1px solid #e5e5e5; border-radius: 8px; background-color: #fafafa; margin-bottom: 28px;">
                                                  <tr>
                                                      <td style="padding: 24px;">
                                                          <p style="margin: 0 0 14px 0; font-size: 13px; font-weight: 700; letter-spacing: 0.04em; color: #18181b; text-transform: uppercase;">Plan details</p>
                                                          <div style="font-size: 15px; line-height: 1.65; color: #52525b;">{planContent}</div>
                                                      </td>
                                                  </tr>
                                              </table>

                                              <p style="margin: 0 0 28px 0; font-size: 15px; line-height: 1.65; color: #52525b;">Follow this plan to focus on your weakest areas and study with maximum efficiency.</p>

                                              <table cellpadding="0" cellspacing="0" style="margin: 0 auto;">
                                                  <tr>
                                                      <td style="border-radius: 6px; background-color: #18181b;">
                                                          <a href="#" style="display: inline-block; padding: 13px 28px; font-size: 14px; font-weight: 600; color: #ffffff; text-decoration: none;">Open study dashboard</a>
                                                      </td>
                                                  </tr>
                                              </table>
                                          </td>
                                      </tr>
                                      <tr>
                                          <td style="padding: 24px 40px; border-top: 1px solid #e5e5e5; text-align: center;">
                                              <p style="margin: 0; font-size: 12px; color: #a1a1aa;">&copy; 2025 Todaii English. All rights reserved.</p>
                                          </td>
                                      </tr>
                                  </table>
                              </td>
                          </tr>
                      </table>
                  </body>
                  </html>
                  """;
}
