Pour tester en local, créer un fichier `src/main/resources/local.conf` avec le
contenu suivant :

```
application {
  jenkins {
    rootUrl = "" // Root URL to access the Jenkins server (e.g. "https://my-jenkins.com")
    username = "" // Jenkins username (e.g. "john.smith@example.com")
    password = "" // Jenkins password or API Token
    jobName = "" // Name of the Jenkins job to monitor
  }
}
```