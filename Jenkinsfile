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
                echo '🏗️ Gradle로 빌드 중...'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 서버에 배포 중...'
                sh 'pkill -f "java -jar" || true'
                sh 'nohup java -jar build/libs/*.jar > app.log 2>&1 &'
            }
        }
    }
}
