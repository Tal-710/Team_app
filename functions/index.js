const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

admin.initializeApp();

const gmailEmail = "teammanagement32112@gmail.com";
const gmailPassword = "hajmkgxbijblwasl"; // Use the app password here

const mailTransport = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

const APP_NAME = "Team Management App";

exports.sendMail = functions.https.onCall(async (data, context) => {
  const teamName = data.teamName;
  const mailOptions = {
    from: `${APP_NAME} <${gmailEmail}>`,
    to: "talbrachya10@gmail.com",
    subject: `New Team Created: ${teamName}`,
    text: `A new team named ${teamName} has been created in the ${APP_NAME}.`,
  };

  try {
    await mailTransport.sendMail(mailOptions);
    console.log("New team notification email sent:", "talbrachya10@gmail.com");
    return { success: true };
  } catch (error) {
    console.error("There was an error while sending the email:", error);
    throw new functions.https.HttpsError("internal", "Unable to send email");
  }
});

exports.sendMailToUser = functions.https.onCall(async (data, context) => {
  const teamName = data.teamName;
  const userEmail = data.userEmail;
  const mailOptions = {
    from: `${APP_NAME} <${gmailEmail}>`,
    to: userEmail,
    subject: `Congratulations on creating your new team: ${teamName}`,
    text: `Dear User,\n\nCongratulations on creating your new team, ${teamName}, in the ${APP_NAME}!\n\nBest Regards,\nThe Team Management App Team`,
  };

  try {
    await mailTransport.sendMail(mailOptions);
    console.log("Congratulations email sent to:", userEmail);
    return { success: true };
  } catch (error) {
    console.error("There was an error while sending the email to the user:", error);
    throw new functions.https.HttpsError("internal", "Unable to send email");
  }
});
