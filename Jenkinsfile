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

                // SSH 연결 테스트
                sh '''
                    echo "SSH 연결 테스트..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "echo 'SSH 연결 성공'; whoami; pwd"
                '''

                // 현재 실행 중인 Java 프로세스 확인
                sh '''
                    echo "현재 Java 프로세스 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "ps aux | grep java || echo 'Java 프로세스 없음'"
                '''

                // 디렉토리 및 권한 확인
                sh '''
                    echo "앱 디렉토리 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "ls -la /home/ubuntu/app/ || mkdir -p /home/ubuntu/app"
                '''
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 서버에 배포 중...'

                // 빌드 파일 서버로 복사
                sh "scp -i /var/jenkins_home/.ssh/dashboardTemplate.pem build/libs/dashboardTemplate-0.0.1-SNAPSHOT.jar ubuntu@52.79.122.132:/home/ubuntu/app/"

                // 1. 기존 프로세스 종료
                sh '''
                    echo "기존 프로세스 종료 중..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "pkill -f dashboardTemplate || echo '종료할 프로세스 없음'"
                '''

                // 2. 잠시 대기
                sleep(time: 5, unit: 'SECONDS')

                // 3. Java 실행 가능 여부 확인
                sh '''
                    echo "Java 버전 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "java -version"
                '''

                // 4. 새로운 프로세스 시작
                withCredentials([usernamePassword(credentialsId: 'DB_CREDENTIALS', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS')]) {
                    sh '''
                        echo "애플리케이션 시작 중..."
                        ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                            cd /home/ubuntu/app
                            echo '현재 디렉토리:' \$(pwd)
                            echo '파일 목록:'
                            ls -la
                            echo '애플리케이션 시작...'
                            nohup java -Dspring.profiles.active=dev \\
                                -Dspring.datasource.url=jdbc:mysql://127.0.0.1:3307/DashboardTemplate \\
                                -Dspring.datasource.username=${DB_USER} \\
                                -Dspring.datasource.password=${DB_PASS} \\
                                -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \\
                                -jar dashboardTemplate-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
                            echo '애플리케이션 시작 명령 실행 완료'
                        "
                    '''
                }

                // 5. 시작 확인
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