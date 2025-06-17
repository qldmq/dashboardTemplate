pipeline {
    agent any

    stages {
        stage('Clone') {
            steps {
                echo '📥 코드 가져오는 중...'
                git credentialsId: 'your-jenkins-credential-id', url: 'https://github.com/qldmq/your-repo.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ Gradle로 빌드 중...'
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
