pipeline {
    agent any

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
                sh 'nohup java -jar build/libs/dashboardTemplate-0.0.1-SNAPSHOT.jar > /home/ubuntu/app.log 2>&1 &'
            }
        }
    }
}
