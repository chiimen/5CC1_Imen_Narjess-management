pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'M2_HOME'
    }

    environment {
        DOCKERHUB_USER = 'imenchi'
        IMAGE = "${DOCKERHUB_USER}/student-management"
    }

    triggers {
        pollSCM('H/5 * * * *')
    }

    stages {
        stage('Git') {
            steps {
                checkout scm
                script {
                    env.BRANCHE = (env.GIT_BRANCH ?: 'main').replaceAll('^origin/', '')
                    env.TAG = "${env.BRANCHE}-${env.BUILD_NUMBER}"
                }
                sh 'git log -1 --pretty=format:"%h - %an : %s"'
                echo "Branche : ${env.BRANCHE} — tag : ${env.TAG}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test unitaire') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                        mvn -B sonar:sonar \
                          -Dsonar.projectKey=student-management-${BRANCHE} \
                          -Dsonar.projectName=student-management-${BRANCHE}
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker build') {
            steps {
                sh 'docker build -t "$IMAGE:$TAG" .'
            }
        }

        stage('Docker push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'DH_USER',
                    passwordVariable: 'DH_PASS'
                )]) {
                    sh '''
                        set +x
                        printf '%s' "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push "$IMAGE:$TAG"
                    '''
                    script {
                        if (env.BRANCHE == 'main') {
                            sh '''
                                docker tag "$IMAGE:$TAG" "$IMAGE:latest"
                                docker push "$IMAGE:latest"
                            '''
                        }
                    }
                }
            }
        }

        stage('Déploiement') {
            when {
                expression { env.BRANCHE == 'main' }
            }
            steps {
                sh 'docker compose pull app'
                sh 'docker compose up -d'
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo "Succès : image ${IMAGE}:${TAG} publiée."
        }
        failure {
            echo 'Échec : consulter la Console Output du stage en rouge.'
        }
        always {
            sh 'docker logout || true'
            sh '''
                if [ -n "$TAG" ]; then
                    docker image rm "$IMAGE:$TAG" || true
                fi
            '''
        }
    }
}
