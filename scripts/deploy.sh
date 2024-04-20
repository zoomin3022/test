#!/bin/bash
PROJECT_NAME="test"
DEPLOY_PATH=/home/ubuntu/$PROJECT_NAME
JAR_NAME=$(ls $DEPLOY_PATH/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$DEPLOY_PATH/build/libs/$JAR_NAME
DEPLOY_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy.log"
DEPLOY_ERR_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy_err.log"
APPLICATION_LOG_PATH="/home/ubuntu/$PROJECT_NAME/application.log"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)

sudo echo "===== 배포 시작 : $(date +%c) =====" >> $DEPLOY_LOG_PATH

sudo echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
sudo echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
sudo cp $BUILD_JAR $DEPLOY_PATH

sudo echo "> 현재 동작중인 어플리케이션 pid 체크" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  sudo echo "> 현재 동작중인 어플리케이션 존재 X" >> $DEPLOY_LOG_PATH
else
  sudo echo "> 현재 동작중인 어플리케이션 존재 O" >> $DEPLOY_LOG_PATH
  sudo echo "> 현재 동작중인 어플리케이션 강제 종료 진행" >> $DEPLOY_LOG_PATH
  sudo echo "> kill -9 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  sudo kill -9 $CURRENT_PID
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
sudo echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH
sudo nohup java -jar $DEPLOY_JAR >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &

sleep 3

sudo echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH