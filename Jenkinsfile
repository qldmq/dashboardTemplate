pipeline {
    agent any

    environment {
        DB_CREDENTIALS = credentials('DB_CREDENTIALS')
    }

    stages {
        stage('Clone') {
            steps {
                echo '📥 코드 가져오는 중...'
                git credentialsId: 'DashboardTemplate_Jenkins', url: 'https://github.com/qldmq/dashboardTemplate.git', branch: 'master'
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ Gradle로 빌드 중 (테스트 제외)...'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 서버에 배포 중...'

                // 빌드 파일 서버로 복사
                sh "scp -i /var/jenkins_home/.ssh/dashboardTemplate.pem build/libs/dashboardTemplate-0.0.1-SNAPSHOT.jar ubuntu@52.79.122.132:/home/ubuntu/app/"

                // 서버에 접속해서 기존 프로세스 종료 후 새로 실행
                script {
                    def dbUser = env.DB_CREDENTIALS_USR
                    def dbPassword = env.DB_CREDENTIALS_PSW

                    sh """
                        ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 '
                            pkill -f "dashboardTemplate.*jar" || true
                            sleep 2
                            nohup java -Dspring.profiles.active=dev \\
                                -Dspring.datasource.url=jdbc:mysql://127.0.0.1:3307/DashboardTemplate \\
                                -Dspring.datasource.username=${dbUser} \\
                                -Dspring.datasource.password=${dbPassword} \\
                                -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \\
                                -jar /home/ubuntu/app/dashboardTemplate-0.0.1-SNAPSHOT.jar > /home/ubuntu/app/app.log 2>&1 &
                        '
                    """
                }
            }
        }
    }
}