pipeline {
    agent any

    environment {
            DB_CREDENTIALS = credentials('DB_CREDENTIALS')
            DB_URL = "jdbc:mysql://127.0.0.1:3307/DashboardTemplate"
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
                sh 'pkill -f "java -jar" || true'
                sh """
                    nohup java -Dspring.profiles.active=dev \
                        -Dspring.datasource.url=$DB_URL \
                        -Dspring.datasource.username=$DB_CREDENTIALS_USR \
                        -Dspring.datasource.password=$DB_CREDENTIALS_PSW \
                        -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \
                        -jar build/libs/dashboardTemplate-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
                """
            }
        }
    }
}
