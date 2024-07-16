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

exports.sendMailToUser = functions.https.onCall(async (data, context) => {
  const teamName = data.teamName;
  const userEmail = data.userEmail;
  const isEditMode = data.isEditMode;
  const subject = isEditMode ?
    `Your team ${teamName} has been updated` :
    `Congratulations on creating your new team: ${teamName}`;
  const text = isEditMode ?
    `Dear User,\n
    Your team, ${teamName}, has been successfully updated in the ${APP_NAME}.
    \n\nBest Regards,\nThe Team Management App Team` :
    `Dear User,\n\nCongratulations on creating your new team, ${teamName}, in 
    the ${APP_NAME}! \n\nBest Regards,\nThe Team Management App Team.`;

  const mailOptions = {
    from: `${APP_NAME} <${gmailEmail}>`,
    to: userEmail,
    subject: subject,
    text: text,
  };

  try {
    await mailTransport.sendMail(mailOptions);
    console.log("User email sent:", userEmail);
    return {success: true};
  } catch (error) {
    console.error("There was an error while sending the email:", error);
    throw new functions.https.HttpsError("internal", "Unable to send email");
  }
});

exports.sendDeleteMailToUser = functions.https.onCall(async (data, context) => {
  const teamName = data.teamName;
  const userEmail = data.userEmail;
  const subject = `Your team ${teamName} has been deleted`;
  const text = `Dear User,\n\n
  
  Your team, ${teamName}, has been successfully deleted from the ${APP_NAME}.
  \n\nBest Regards,\nThe Team Management App Team`;

  const mailOptions = {
    from: `${APP_NAME} <${gmailEmail}>`,
    to: userEmail,
    subject: subject,
    text: text,
  };

  try {
    await mailTransport.sendMail(mailOptions);
    console.log("Deletion email sent to user:", userEmail);
    return {success: true};
  } catch (error) {
    console.error("There was an error while sending the deletion email", error);
    throw new functions.https.HttpsError("internal", "Unable to send email");
  }
});
