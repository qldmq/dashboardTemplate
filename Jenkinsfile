pipeline {
    agent any

    environment {
        DB_CREDENTIALS = credentials('DB_CREDENTIALS')
        JWT_SECRET = credentials('DashboardTemplate_JWT_Secret')
        DB_URL = credentials('DB_URL')
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
                    string(credentialsId: 'DashboardTemplate_JWT_Secret', variable: 'JWT_SECRET'),
                    string(credentialsId: 'DB_URL', variable: 'DB_URL'),
                    sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'SSH_KEY')
                ]) {
                    sh '''
                        # 환경변수를 파일로 생성하여 전송
                        cat > env_vars.sh << EOF
export DB_URL="${DB_URL}"
export DB_USER="${DB_USER}"
export DB_PASS="${DB_PASS}"
export JWT_SECRET="${JWT_SECRET}"
EOF

                        # 환경변수 파일과 시작 스크립트를 서버로 전송
                        scp -i $SSH_KEY env_vars.sh ubuntu@52.79.122.132:/home/ubuntu/app/

                        ssh -i $SSH_KEY ubuntu@52.79.122.132 << 'ENDSSH'
cd /home/ubuntu/app

# 환경변수 로드
source ./env_vars.sh

cat > start_app.sh << 'EOF'
#!/bin/bash

echo '📦 기존 프로세스 정리 시작...'

# PID 종료
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    if ps -p $PID > /dev/null 2>&1; then
        echo "🔻 종료 중: $PID"
        kill -15 $PID
        sleep 5
        if ps -p $PID > /dev/null 2>&1; then
            echo "⛔️ 강제 종료: $PID"
            kill -9 $PID
        fi
    fi
    rm -f app.pid
fi

# 포트 8080 정리
PORT_PID=$(/usr/bin/lsof -ti:8080 2>/dev/null)
if [ -n "$PORT_PID" ]; then
    echo "🛑 포트 점유 프로세스 종료: $PORT_PID"
    kill -15 $PORT_PID
    sleep 5
    if /usr/bin/lsof -ti:8080 > /dev/null 2>&1; then
        kill -9 $(/usr/bin/lsof -ti:8080)
    fi
fi

# 로그 백업
if [ -f app.log ]; then
    mv app.log app.log.bak.$(date +%Y%m%d_%H%M%S)
fi

# 환경변수 로드
source ./env_vars.sh

echo '🚀 애플리케이션 실행...'
echo "DB_URL: ${DB_URL:0:30}..."
echo "DB_USER: $DB_USER"
echo "JWT_SECRET: ${JWT_SECRET:0:10}..."

nohup java \
  -Dfile.encoding=UTF-8 \
  -Dspring.profiles.active=dev \
  -Dspring.datasource.url="$DB_URL" \
  -Dspring.datasource.username="$DB_USER" \
  -Dspring.datasource.password="$DB_PASS" \
  -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \
  -Dspring.jpa.database-platform=org.hibernate.dialect.MySQLDialect \
  -Dhibernate.dialect=org.hibernate.dialect.MySQLDialect \
  -Dspring.jpa.hibernate.ddl-auto=update \
  -Dspring.jpa.show-sql=false \
  -Djwt.secret="$JWT_SECRET" \
  -jar dashboardTemplate-0.0.1-SNAPSHOT.jar > app.log 2>&1 & echo $! > app.pid

echo "애플리케이션 시작됨. PID: $(cat app.pid)"
EOF

chmod +x start_app.sh
./start_app.sh

# 환경변수 파일 삭제 (보안)
rm -f env_vars.sh
ENDSSH

                        # 로컬 환경변수 파일 삭제
                        rm -f env_vars.sh
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '🏥 헬스 체크 실행...'
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                        ssh -i $SSH_KEY ubuntu@52.79.122.132 "
                            echo '앱 시작 대기 중...'
                            sleep 15

                            echo '프로세스 확인:'
                            ps aux | grep java | grep -v grep || echo '자바 프로세스 없음'

                            echo '포트 8080 확인:'
                            netstat -tlnp | grep :8080 || echo '포트 8080 리슨 중 아님'

                            echo '최근 로그:'
                            tail -20 /home/ubuntu/app/app.log

                            echo '헬스 체크 시도:'
                            curl -f http://localhost:8080/actuator/health -m 10 || echo '❗️헬스 체크 실패 (앱이 아직 시작 중일 수 있음)'
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
                    echo "실패 시 로그 확인:"
                    ssh -i $SSH_KEY ubuntu@52.79.122.132 "
                        echo '=== 최근 30줄 로그 ==='
                        tail -30 /home/ubuntu/app/app.log
                        echo '=== 프로세스 상태 ==='
                        ps aux | grep java | grep -v grep || echo '자바 프로세스 없음'
                    "
                '''
            }
        }
    }
}