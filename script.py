import os
import subprocess
import time

import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.base import MIMEBase
from email import encoders

from datetime import date
from glob import glob
from io import StringIO

from secrets import fromaddr,toaddr,password


BASE_DIR = os.environ["BASE_DIR"]


# for debugging
# BASE_DIR = "/home/jigar/projects/hackerRankLeaderBoard"


# for debugging
# result = subprocess.run(['java', '-cp',BASE_DIR,'hello'],stdout=subprocess.PIPE)

exjar = os.path.join(BASE_DIR,'target',"hackerRankLeaderboard-0.0.1-SNAPSHOT.jar")

result = subprocess.run(['java','-jar' ,exjar,"1"],stdout=subprocess.PIPE)

errors = []

if(result.stdout):
	errors.append("Errors in new grads profile tracking..\n\n\n");
	errors.append(result.stdout.decode("utf-8"))

result = subprocess.run(['java','-jar' ,exjar,"2"],stdout=subprocess.PIPE)

if(result.stdout):
	if(errors):
		errors.append("\n\n\n");
		errors.append("#"*100)
		errors.append("\n\n\n");
	errors.append("Errors in employees profile tracking..\n\n\n");
	errors.append(result.stdout.decode("utf-8"))

print("".join(errors))


def prepare_attachment(filename):
	attachment = open(os.path.join(BASE_DIR,filename), "rb")

	# instance of MIMEBase and named as p
	p = MIMEBase('application', 'octet-stream')

	# To change the payload into encoded form
	p.set_payload((attachment).read())

	# encode into base64
	encoders.encode_base64(p)

	p.add_header('Content-Disposition', "attachment; filename= %s" % filename)

	return p


##send email code from gfg
def send_email():

	# instance of MIMEMultipart
	msg = MIMEMultipart()

	# storing the senders email address
	msg['From'] = fromaddr

	# storing the receivers email address
	msg['To'] = ",".join(toaddr) if type(toaddr) is list else toaddr

	# storing the subject
	if(errors == []):
		msg['Subject'] = "leaderboard as of date " +date.today().strftime("%d/%m/%y")

	# string to store the body of the mail
	body = "PFA"
	# body = "not the leaderboard just testing"

	# attach the body with the msg instance
	msg.attach(MIMEText(body, 'plain'))


	for filename in glob(os.path.join(BASE_DIR,"leaderBoard_*.xlsx")):

		p = prepare_attachment(os.path.basename(filename))

		# attach the instance 'p' to instance 'msg'
		msg.attach(p)

	if(errors):

		logfile = "error_logs.txt"

		msg['Subject'] = f"ERROR : Attaching previous leaderboard, check {logfile}"

		with StringIO("".join(errors)) as f:
			# instance of MIMEBase and named as p
			p = MIMEBase('application', 'octet-stream')

			# To change the payload into encoded form
			p.set_payload((f).read())

			# encode into base64
			encoders.encode_base64(p)

			p.add_header('Content-Disposition', f"attachment; filename= {logfile}")

			msg.attach(p)

	# creates SMTP session
	s = smtplib.SMTP("smtp.gmail.com", 587)

	s.ehlo()

	# start TLS for security
	s.starttls()

	# Authentication
	s.login(fromaddr,password)

	# Converts the Multipart msg into a string
	text = msg.as_string()

	# sending the mail
	done = False
	while not done:

		try:
			s.sendmail(fromaddr, toaddr, text)
			done = True
		except smtplib.SMTPDataError as e:
		# smtplib.SMTPDataError: (421, b'4.7.0 Temporary System Problem.  Try again later (10). d10sm816945pfh.8 - gsmtp')
			print("gmail server down, trying to send email again")
			time.sleep(10)


	# terminating the session
	s.quit()



send_email()
print("sending email complete")