pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'develop', description: 'Repository Branch')
        string(name: 'VERSION', defaultValue: '1.0.0', description: 'JAR Version')
    }

    environment {
        MAVEN_HOME = tool 'Maven 3.9.6'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Cloning branch: ${params.BRANCH_NAME}"
                checkout([$class: 'GitSCM',
                          branches: [[name: "*/${params.BRANCH_NAME}"]],
                          userRemoteConfigs: [[url: 'https://github.com/erickperez091/inventory-invoices.git']]])
            }
        }

        stage('Build') {
            steps {
                configFileProvider([configFile(fileId: 'nexus-settings', variable: 'MAVEN_SETTINGS')]) {
                    echo "Compiling version: ${params.VERSION}"
                    sh "${MAVEN_HOME}/bin/mvn clean package -s $MAVEN_SETTINGS"
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: 'nexus:8081',
                    groupId: 'com.example',
                    version: "${params.VERSION}",
                    repository: 'maven-test-releases',
                    credentialsId: 'nexus-creds',
                    artifacts: [
                        [
                            artifactId: 'invoices',
                            classifier: '',
                            file: "target/invoices-${params.VERSION}.jar",
                            type: 'jar'
                        ],
                        [
                            artifactId: 'invoices',
                            classifier: '',
                            file: 'pom.xml',
                            type: 'pom'
                        ]
                    ]
                )
            }
        }
    }
}