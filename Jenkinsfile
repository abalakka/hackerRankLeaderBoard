node{

    dir("/home/jigar/secrets/"){
    fileOperations([fileCopyOperation(
      excludes: '',
      flattenFiles: false,
      includes: 'cookies.txt,secrets.py',
      targetLocation: "${WORKSPACE}"
    )])
    }


    sh './cookie_add.sh'
    sh './mvnw clean package'
    sh 'python3 setup_cron.py'

}