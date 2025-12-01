package com.todaii.english.shared.constants;

public class MailTemplate {
	public static final String VERIFICATION_EMAIL_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Verify Email</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #10b981; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Todaii English</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <h2 style="color: #333333; margin-top: 0; font-size: 20px;">Verify Your Email Address</h2>
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Hello,</p>
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Thank you for starting your journey with Todaii English. To complete your registration, please use the verification code below:</p>

			                            <div style="background-color: #f0fdf4; border: 2px dashed #10b981; border-radius: 6px; padding: 20px; margin: 30px 0; text-align: center;">
			                                <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #10b981; font-family: monospace;">{verificationCode}</span>
			                            </div>

			                            <p style="color: #666666; font-size: 14px; line-height: 1.6;">This code helps us ensure your account is secure. It will expire in <strong>15 minutes</strong>.</p>
			                            <p style="color: #999999; font-size: 14px; margin-top: 30px;">If you didn't sign up for Todaii English, you can safely ignore this email.</p>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
			    </table>
			</body>
			</html>
			""";

	public static final String PASSWORD_RESET_SUCCESS_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Password Reset Successful</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #10b981; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Security Update</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px; text-align: center;">
			                            <div style="background-color: #10b981; color: white; width: 60px; height: 60px; line-height: 60px; border-radius: 50%; display: inline-block; font-size: 30px; margin-bottom: 20px;">✓</div>
			                            <h2 style="color: #333333; margin: 0 0 20px 0; font-size: 20px;">Password Reset Successfully</h2>
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Hello,</p>
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Your password has been successfully updated. You can now log in with your new credentials.</p>

			                            <div style="text-align: left; background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 25px 0;">
			                                <p style="margin: 0 0 10px 0; font-weight: bold; color: #444;">Security Tips:</p>
			                                <ul style="color: #666666; font-size: 14px; padding-left: 20px; margin: 0;">
			                                    <li style="margin-bottom: 5px;">Never share your password with anyone.</li>
			                                    <li style="margin-bottom: 5px;">Enable two-factor authentication if available.</li>
			                                </ul>
			                            </div>

			                            <p style="color: #ef4444; font-size: 14px; margin-top: 20px;">If you did not make this change, please contact our support team immediately.</p>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
			    </table>
			</body>
			</html>
			""";

	public static final String PASSWORD_RESET_REQUEST_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Reset Password</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #10b981; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Reset Password</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">Hello,</p>
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">We received a request to reset your password for your Todaii English account. If you didn't ask for this, you can ignore this email.</p>

			                            <div style="text-align: center; margin: 35px 0;">
			                                <a href="{resetURL}" style="background-color: #10b981; color: white; padding: 14px 28px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">Reset My Password</a>
			                            </div>

			                            <p style="color: #999999; font-size: 14px; text-align: center;">This link is valid for <strong>1 hour</strong>.</p>
			                            <hr style="border: none; border-top: 1px solid #eeeeee; margin: 30px 0;">
			                            <p style="color: #999999; font-size: 12px;">Button not working? Copy and paste this link into your browser:<br><a href="{resetURL}" style="color: #10b981;">{resetURL}</a></p>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
			    </table>
			</body>
			</html>
			""";

	public static final String WELCOME_EMAIL_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Welcome to Todaii</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #10b981; padding: 40px 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0 0 10px 0; font-size: 28px;">Welcome Aboard! 🎉</h1>
			                            <p style="color: #e6fffa; margin: 0; font-size: 16px;">Let's master English together</p>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #333333; font-size: 18px; font-weight: bold; margin-bottom: 20px;">Hello {name},</p>
			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">We are thrilled to have you join the Todaii English community! Your account has been successfully created with the email:</p>

			                            <div style="background-color: #f3f4f6; padding: 15px; border-radius: 6px; text-align: center; margin: 20px 0;">
			                                <span style="color: #374151; font-weight: 600; font-size: 16px;">{email}</span>
			                            </div>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">You now have full access to our learning resources. Ready to start your first lesson?</p>

			                            <div style="text-align: center; margin-top: 30px;">
			                                <a href="#" style="background-color: #333333; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">Go to Dashboard</a>
			                            </div>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
			    </table>
			</body>
			</html>
			""";

	public static final String ACCOUNT_BANNED_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Account Suspended</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #ef4444; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Account Suspended</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #333333; font-size: 18px; font-weight: bold; margin-bottom: 20px;">Hello {name},</p>

			                            <div style="background-color: #fef2f2; border-left: 4px solid #ef4444; padding: 15px; margin-bottom: 25px;">
			                                <p style="color: #991b1b; margin: 0; font-size: 16px;">Your account has been suspended due to a violation of our Terms of Service.</p>
			                            </div>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
			                                While suspended, you will not be able to access your courses, history, or login to the platform.
			                            </p>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin-top: 20px;">
			                                If you believe this is a mistake, please contact our support team immediately for a review of your case.
			                            </p>

			                            <div style="text-align: center; margin: 35px 0;">
			                                <a href="mailto:{contactMail}" style="background-color: #333333; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">Contact Support</a>
			                            </div>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
			    </table>
			</body>
			</html>
			""";

	public static final String ACCOUNT_UNBANNED_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Account Restored</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #10b981; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Account Restored</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #333333; font-size: 18px; font-weight: bold; margin-bottom: 20px;">Hello {name},</p>

			                            <div style="background-color: #ecfdf5; border-left: 4px solid #10b981; padding: 15px; margin-bottom: 25px;">
			                                <p style="color: #065f46; margin: 0; font-size: 16px;">Good news! Your account has been successfully reactivated.</p>
			                            </div>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
			                                We have reviewed your account status and lifted the suspension. You now have full access to all Todaii English features, course history, and learning materials again.
			                            </p>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin-top: 20px;">
			                                We apologize for any inconvenience this may have caused. Welcome back!
			                            </p>

			                            <div style="text-align: center; margin: 35px 0;">
			                                <a href="/client/login" style="background-color: #333333; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">Log In Now</a>
			                            </div>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
			                        </td>
			                    </tr>
			                </table>
			            </td>
			        </tr>
			    </table>
			</body>
			</html>
			""";

	public static final String ACCOUNT_DELETED_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Account Deleted</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #4b5563; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Goodbye for now</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #333333; font-size: 18px; font-weight: bold; margin-bottom: 20px;">Hello {name},</p>

			                            <div style="background-color: #f3f4f6; border-left: 4px solid #4b5563; padding: 15px; margin-bottom: 25px;">
			                                <p style="color: #1f2937; margin: 0; font-size: 16px;">Your account has been successfully deleted as requested.</p>
			                            </div>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
			                                We confirm that your personal data, learning history, and vocabulary lists have been permanently removed from our servers.
			                            </p>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6; margin-top: 20px;">
			                                We are sorry to see you go, but we hope to see you again in the future. You are always welcome to create a new account whenever you're ready to continue your English journey.
			                            </p>

			                            <div style="text-align: center; margin: 35px 0;">
			                                <a href="{homepageURL}" style="background-color: #ffffff; color: #4b5563; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; border: 2px solid #4b5563;">Visit Todaii English</a>
			                            </div>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
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
	public static final String ACCOUNT_CREATED_BY_ADMIN_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Account Created</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #10b981; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Account Created</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #333333; font-size: 18px; font-weight: bold; margin-bottom: 20px;">Hello {name},</p>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
			                                An account has been created for you by the Todaii English administration team. You can now access the system using the credentials below:
			                            </p>

			                            <div style="background-color: #f3f4f6; border: 1px solid #e5e7eb; border-radius: 6px; padding: 20px; margin: 25px 0;">
			                                <table width="100%" cellpadding="0" cellspacing="0">
			                                    <tr>
			                                        <td style="padding-bottom: 10px; color: #666666; font-size: 14px;">Email / Username:</td>
			                                    </tr>
			                                    <tr>
			                                        <td style="padding-bottom: 20px; color: #333333; font-weight: bold; font-size: 16px;">{email}</td>
			                                    </tr>
			                                    <tr>
			                                        <td style="padding-bottom: 10px; color: #666666; font-size: 14px;">Temporary Password:</td>
			                                    </tr>
			                                    <tr>
			                                        <td style="color: #333333; font-weight: bold; font-size: 18px; font-family: monospace; letter-spacing: 1px;">{password}</td>
			                                    </tr>
			                                </table>
			                            </div>

			                            <p style="color: #666666; font-size: 14px; line-height: 1.6;">
			                                <strong>Security Note:</strong> Please verify your information and <a href="{resetPasswordURL}" style="color: #10b981; text-decoration: none;">change your password</a> immediately after your first login.
			                            </p>

			                            <div style="text-align: center; margin: 35px 0;">
			                                <a href="{loginURL}" style="background-color: #333333; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">Login Now</a>
			                            </div>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
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
	public static final String ACCOUNT_UPDATED_BY_ADMIN_TEMPLATE = """
			<!DOCTYPE html>
			<html>
			<head>
			    <meta charset="UTF-8">
			    <meta name="viewport" content="width=device-width, initial-scale=1.0">
			    <title>Account Updated</title>
			</head>
			<body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f7; color: #333333;">
			    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f7; padding: 40px 0;">
			        <tr>
			            <td align="center">
			                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow: hidden; max-width: 100%;">
			                    <tr>
			                        <td style="background-color: #2563eb; padding: 30px; text-align: center;">
			                            <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">Account Updated</h1>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="padding: 40px 30px;">
			                            <p style="color: #333333; font-size: 18px; font-weight: bold; margin-bottom: 20px;">Hello {name},</p>

			                            <div style="background-color: #eff6ff; border-left: 4px solid #2563eb; padding: 15px; margin-bottom: 25px;">
			                                <p style="color: #1e40af; margin: 0; font-size: 16px;">This is a notification that your account details have been updated by an administrator.</p>
			                            </div>

			                            <p style="color: #666666; font-size: 16px; line-height: 1.6;">
			                                The changes have been applied successfully. You can review your profile information by visiting your account settings.
			                            </p>

			                             <p style="color: #666666; font-size: 16px; line-height: 1.6;">
			                                If you have any questions regarding these changes, please contact the support team.
			                            </p>

			                            <div style="text-align: center; margin: 35px 0;">
			                                <a href="{profileURL}" style="background-color: #333333; color: white; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold;">View My Profile</a>
			                            </div>

			                            <hr style="border: none; border-top: 1px solid #eeeeee; margin: 30px 0;">
			                            <p style="color: #999999; font-size: 14px; margin: 0;">Note: If you did not request any support or changes, please reply to this email immediately.</p>
			                        </td>
			                    </tr>
			                    <tr>
			                        <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #eeeeee;">
			                            <p style="color: #999999; font-size: 12px; margin: 0;">&copy; 2025 Todaii English. All rights reserved.</p>
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