from crontab import CronTab
import os

#for debugging
# $ grep CRON /var/log/syslog

def setup_cron():
    user_cron  = CronTab(user=True)
    script_path = os.path.dirname(os.path.abspath(__file__))


    for job in user_cron:
        if "script.py" in job.command and "cron.log" in job.command:
            user_cron.remove(job)
            user_cron.write()

    command = f"python3 {os.path.join(script_path, 'script.py')} > {os.path.join(script_path, 'cron.log')} 2>&1"

    job = user_cron.new(command=command)

    job.env["BASE_DIR"] = script_path

    ## every morning 7:20 am uncomment when needed
    job.hour.on(7)
    job.minute.on(20)
    ## for testing comment when done
    # job.hour.on(10)
    # job.minute.on(16)
    # job.minute.every(1)

    user_cron.write()

    print('crontab set')

    return True




if __name__ == "__main__":
    setup_cron()
