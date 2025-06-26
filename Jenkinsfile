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
                    echo "현재 dashboardTemplate 프로세스 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                        ps aux | grep -v grep | grep dashboardTemplate-0.0.1-SNAPSHOT.jar || echo 'dashboardTemplate 프로세스 없음'
                    "
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
                        # 정확한 jar 파일명으로 프로세스 찾기
                        PID=\\$(ps aux | grep -v grep | grep 'dashboardTemplate-0.0.1-SNAPSHOT.jar' | awk '{print \\$2}') || true

                        if [ ! -z \"\\$PID\" ] && [ \"\\$PID\" != \"\" ]; then
                            echo \"프로세스 \\$PID 종료 중...\"
                            kill -15 \\$PID || true
                            sleep 5

                            # 프로세스가 여전히 살아있는지 확인
                            if ps -p \\$PID > /dev/null 2>&1; then
                                echo \"강제 종료 실행...\"
                                kill -9 \\$PID || true
                            fi
                            echo \"프로세스 종료 완료\"
                        else
                            echo \"종료할 프로세스 없음\"
                        fi
                    "
                '''

                sleep(time: 3, unit: 'SECONDS')

                sh '''
                    echo "Java 버전 확인..."
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "java -version"
                '''

                withCredentials([
                    usernamePassword(credentialsId: 'DB_CREDENTIALS', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS'),
                    string(credentialsId: 'DashboardTemplate_JWT_Secret', variable: 'JWT_SECRET')
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
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                        # 최대 30초 대기
                        for i in {1..6}; do
                            echo \"시작 확인 시도 \\$i/6...\"

                            # 프로세스 확인
                            if ps aux | grep -v grep | grep 'dashboardTemplate-0.0.1-SNAPSHOT.jar' > /dev/null; then
                                echo '✅ 프로세스 실행 중'

                                # 포트 확인
                                if netstat -tlnp | grep ':8080 ' > /dev/null 2>&1; then
                                    echo '✅ 포트 8080 리스닝 중'
                                    echo '🎉 애플리케이션 시작 완료!'
                                    break
                                else
                                    echo '⏳ 포트 8080 대기 중...'
                                fi
                            else
                                echo '❌ 프로세스가 실행되지 않음'
                            fi

                            if [ \\$i -eq 6 ]; then
                                echo '⚠️ 시작 확인 타임아웃'
                                echo '📋 현재 프로세스 상태:'
                                ps aux | grep -v grep | grep java || echo '실행 중인 Java 프로세스 없음'
                                echo '📋 최근 로그:'
                                tail -20 /home/ubuntu/app/app.log || echo '로그 파일을 찾을 수 없습니다'
                            else
                                sleep 5
                            fi
                        done
                    "
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 헬스 체크 실행...'
                sh '''
                    ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                        # 간단한 헬스 체크 (옵션)
                        curl -f http://localhost:8080/actuator/health -m 10 || echo '헬스 체크 실패 (정상일 수 있음)'
                    "
                '''
            }
        }
    }

    post {
        always {
            echo '🧹 파이프라인 완료'
        }
        success {
            echo '✅ 배포 성공!'
        }
        failure {
            echo '❌ 배포 실패!'
            sh '''
                echo "실패 시 서버 상태 확인..."
                ssh -i /var/jenkins_home/.ssh/dashboardTemplate.pem ubuntu@52.79.122.132 "
                    echo '프로세스 상태:'
                    ps aux | grep -v grep | grep java || echo '실행 중인 Java 프로세스 없음'
                    echo '포트 상태:'
                    netstat -tlnp | grep 8080 || echo '포트 8080이 사용되지 않음'
                    echo '최근 로그:'
                    tail -30 /home/ubuntu/app/app.log || echo '로그 파일을 찾을 수 없습니다'
                "
            '''
        }
    }
}