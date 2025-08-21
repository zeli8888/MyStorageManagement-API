pipeline{

  agent any
  tools {
  maven 'Maven'
  }
  environment {
    version = '1.0'
    DB_PASSWORD = credentials('DB_PASSWORD')
    STORAGE_MANAGEMENT_SECRETS = credentials('STORAGE_MANAGEMENT_SECRETS')
    STORAGE_MANAGEMENT_POSTGRES_VOLUME = credentials('STORAGE_MANAGEMENT_POSTGRES_VOLUME')
  }

  stages{

    stage('Start Database'){
      steps{
        sh script: 'docker stop storage-management-postgres', returnStatus: true
        sh script: 'docker rm storage-management-postgres', returnStatus: true
        sh 'docker compose -p storage-management -f postgres.yaml up -d --force-recreate'
      }
    }

    stage('Test and Build'){
      steps{
        sh '''
          MAVEN_OPTS="-Xms256m -Xmx512m -Dlog.level=WARN" \
          mvn clean package -Dsurefire.forkCount=1 -Dsurefire.reuseForks=false -T 1
        '''
      }
    }

    stage('Build Docker Image'){
      steps{
        sh "docker build -t storage-management-api:${version} ."
        sh "docker tag storage-management-api:${version} zeli8888/storage-management-api:${version}"
        sh "docker push zeli8888/storage-management-api:${version}"
        sh "docker image prune -f"
      }
    }

    stage('Run Docker Container'){
      steps{
        sh script: 'docker stop storage-management-api', returnStatus: true
        sh script: 'docker rm storage-management-api || true', returnStatus: true
        sh "export version=${version} && docker compose -p storage-management -f storage-management-api.yaml up -d --force-recreate"
      }
    }
  }
}
