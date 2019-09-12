from crontab import CronTab
import os

#for debugging
# $ grep CRON /var/log/syslog

def setup_cron():
    user_cron  = CronTab(user=True)
    script_path = os.path.dirname(os.path.abspath(__file__))


    for job in user_cron:
        if script_path in job.command:
            user_cron.remove(job)
            user_cron.write()

    command = f"python3 {os.path.join(script_path, 'script.py')}"

    job = user_cron.new(command=command)

    job.env["BASE_DIR"] = script_path

    ## every morning 8 am uncomment when needed
    job.hour.on(8)
    ## for testing comment when done
    # job.hour.on(1)
    # job.minute.on(9)
    # job.minute.every(1)

    user_cron.write()

    print('crontab set')

    return True




if __name__ == "__main__":
    setup_cron()
