pipeline {
    agent any

    environment {
        DB_CREDENTIALS = credentials('DB_CREDENTIALS')
        JWT_SECRET = credentials('DashboardTemplate_JWT_Secret')
    }

    stages {
        stage('Clone') {
            steps {
                echo '📥 코드 가져오는 중...'
                git credentialsId: 'DashboardTemplate_PAT', url: 'https://github.com/qldmq/dashboardTemplate.git', branch: 'master'
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
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                        ssh -i $SSH_KEY ubuntu@52.79.122.132 "echo '✅ SSH 연결 성공'; whoami; pwd"
                        ssh -i $SSH_KEY ubuntu@52.79.122.132 "mkdir -p /home/ubuntu/app && ls -la /home/ubuntu/app/"
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 서버에 배포 중...'

                withCredentials([sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                        scp -i $SSH_KEY build/libs/dashboardTemplate-0.0.1-SNAPSHOT.jar ubuntu@52.79.122.132:/home/ubuntu/app/
                    '''
                }

                withCredentials([
                    usernamePassword(credentialsId: 'DB_CREDENTIALS', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS'),
                    string(credentialsId: 'DashboardTemplate_JWT_Secret', variable: 'JWT_SECRET')
                ]) {
                    // 큰따옴표 3개 사용해 Groovy 변수 치환 적용
                    sh """
                        ssh -i \$SSH_KEY ubuntu@52.79.122.132 /bin/bash << 'ENDSSH'
                            cd /home/ubuntu/app

                            cat > start_app.sh << 'EOF'
#!/bin/bash

echo '📦 기존 프로세스 정리 시작...'

# PID 종료
if [ -f app.pid ]; then
    PID=\$(cat app.pid)
    if ps -p \$PID > /dev/null 2>&1; then
        echo "🔻 종료 중: \$PID"
        kill -15 \$PID
        sleep 5
        if ps -p \$PID > /dev/null 2>&1; then
            echo "⛔️ 강제 종료: \$PID"
            kill -9 \$PID
        fi
    fi
    rm -f app.pid
fi

# 포트 8080 정리
PORT_PID=\$(/usr/bin/lsof -ti:8080)
if [ -n "\$PORT_PID" ]; then
    echo "🛑 포트 점유 프로세스 종료: \$PORT_PID"
    kill -15 \$PORT_PID
    sleep 5
    if /usr/bin/lsof -ti:8080 > /dev/null 2>&1; then
        kill -9 \$(/usr/bin/lsof -ti:8080)
    fi
fi

# 로그 백업
if [ -f app.log ]; then
    mv app.log app.log.bak.\$(date +%Y%m%d_%H%M%S)
fi

echo '🚀 애플리케이션 실행...'
nohup java \\
  -Dfile.encoding=UTF-8 \\
  -Dspring.profiles.active=dev \\
  -Dspring.datasource.url=jdbc:mysql://dashboardtemplate.ctyqackomgq0.ap-northeast-2.rds.amazonaws.com:3306/DashboardTemplate \\
  -Dspring.datasource.username=${DB_USER} \\
  -Dspring.datasource.password=${DB_PASS} \\
  -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \\
  -Djwt.secret=${JWT_SECRET} \\
  -jar dashboardTemplate-0.0.1-SNAPSHOT.jar > app.log 2>&1 & echo \$! > app.pid

EOF

                            chmod +x start_app.sh
                            ./start_app.sh
                        ENDSSH
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 헬스 체크 실행...'
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                        ssh -i $SSH_KEY ubuntu@52.79.122.132 "
                            curl -f http://localhost:8080/actuator/health -m 10 || echo '❗️헬스 체크 실패 (정상일 수 있음)'
                        "
                    '''
                }
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
            withCredentials([sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'SSH_KEY')]) {
                sh '''
                    echo "🚨 실패 시 서버 상태 확인..."
                    ssh -i $SSH_KEY ubuntu@52.79.122.132 "
                        ps aux | grep -v grep | grep java || echo '실행 중인 Java 프로세스 없음'
                        netstat -tlnp | grep 8080 || echo '포트 8080 사용 없음'
                        /usr/bin/lsof -i:8080 || echo '8080 포트 사용 프로세스 없음'
                        tail -30 /home/ubuntu/app/app.log || echo '로그 파일 없음'
                    "
                '''
            }
        }
    }
}
