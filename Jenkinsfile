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

        stage('Pre-Deploy Check') {
            steps {
                echo '🔍 배포 전 서버 상태 확인...'

                sh '''
                    echo "SSH 연결 테스트..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "echo 'SSH 연결 성공'; whoami; pwd"
                '''

                sh '''
                    echo "현재 Java 프로세스 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "ps aux | grep java || echo 'Java 프로세스 없음'"
                '''

                sh '''
                    echo "앱 디렉토리 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "ls -la /home/ubuntu/app/ || mkdir -p /home/ubuntu/app"
                '''
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 서버에 배포 중...'

                sh "scp -i /var/jenkins_home/.ssh/dashboardTemplate.pem build/libs/dashboardTemplate-0.0.1-SNAPSHOT.jar ubuntu@52.79.122.132:/home/ubuntu/app/"

                sh '''
                    echo "기존 프로세스 종료 중..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                        PID=\\$(pgrep -f dashboardTemplate) || true
                        if [ ! -z \"\\$PID\" ]; then
                            echo \"프로세스 \\$PID 종료 중...\"
                            kill -15 \\$PID
                            sleep 3
                            kill -9 \\$PID 2>/dev/null || true
                            echo \"프로세스 종료 완료\"
                        else
                            echo \"종료할 프로세스 없음\"
                        fi
                    "
                '''

                sleep(time: 5, unit: 'SECONDS')

                sh '''
                    echo "Java 버전 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "java -version"
                '''

                // 여기서 JWT_SECRET 크레덴셜도 함께 사용
                withCredentials([
                    usernamePassword(credentialsId: 'DB_CREDENTIALS', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS'),
                    string(credentialsId: 'JWT_SECRET', variable: 'JWT_SECRET')
                ]) {
                    sh """
                        ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 '
                            cd /home/ubuntu/app &&
                            nohup java \\
                              -Dspring.profiles.active=dev \\
                              -Dspring.datasource.url=jdbc:mysql://dashboardtemplate.ctyqackomgq0.ap-northeast-2.rds.amazonaws.com:3306/DashboardTemplate \\
                              -Dspring.datasource.username=${DB_USER} \\
                              -Dspring.datasource.password=${DB_PASS} \\
                              -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \\
                              -Djwt.secret=${JWT_SECRET} \\
                              -jar dashboardTemplate-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
                        '
                    """
                }

                sh '''
                    echo "애플리케이션 시작 확인 중..."
                    sleep 10
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                        echo '프로세스 상태:'
                        ps aux | grep dashboardTemplate || echo '프로세스를 찾을 수 없습니다'
                        echo '로그 파일 확인:'
                        tail -20 /home/ubuntu/app/app.log || echo '로그 파일을 찾을 수 없습니다'
                    "
                '''
            }
        }
    }
}