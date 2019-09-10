import os

BASE_DIR = os.environ["BASE_DIR"]


# for debugging
# BASE_DIR = "/home/jigar/projects/hackerRankLeaderBoard"


server_addr = "http://localhost:8080/leaderboard"

# for debugging
# server_addr = "http://127.0.0.1:8887/Pipfile"

# -O => output file to $filename
filename = "leaderboard.xlsx"

# for debugging
# filename = "zzz.txt"

os.system(f"wget -O {os.path.join(BASE_DIR,filename)} {server_addr}")


##send email code from gfg

def send_email():

	# Python code to illustrate Sending mail with attachments
	# from your Gmail account

	# libraries to be imported
	import smtplib
	from email.mime.multipart import MIMEMultipart
	from email.mime.text import MIMEText
	from email.mime.base import MIMEBase
	from email import encoders
	from datetime import date
	from secrets import fromaddr,toaddr,password

	# instance of MIMEMultipart
	msg = MIMEMultipart()

	# storing the senders email address
	msg['From'] = fromaddr

	# storing the receivers email address
	msg['To'] = toaddr

	# storing the subject
	msg['Subject'] = "leaderboard as of date " +date.today().strftime("%d/%m/%y")

	# string to store the body of the mail
	body = "PFA"

	# attach the body with the msg instance
	msg.attach(MIMEText(body, 'plain'))

	# open the file to be sent

	# filename = filename

	attachment = open(os.path.join(BASE_DIR,filename), "rb")

	# instance of MIMEBase and named as p
	p = MIMEBase('application', 'octet-stream')

	# To change the payload into encoded form
	p.set_payload((attachment).read())

	# encode into base64
	encoders.encode_base64(p)

	p.add_header('Content-Disposition', "attachment; filename= %s" % filename)

	# attach the instance 'p' to instance 'msg'
	msg.attach(p)

	# creates SMTP session
	s = smtplib.SMTP('smtp.gmail.com', 587)

	# start TLS for security
	s.starttls()

	# Authentication
	s.login(fromaddr, password)

	# Converts the Multipart msg into a string
	text = msg.as_string()

	# sending the mail
	s.sendmail(fromaddr, toaddr, text)

	# terminating the session
	s.quit()



send_email()