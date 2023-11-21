export JAVA_HOME="/Users/allin327/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/232.9559.62.2321.10773421/Android Studio Preview.app/Contents/jbr/Contents/Home"
./gradlew clean
./gradlew :nativeBridge-annotation:publish
./gradlew :nativeBridge-processor:publish
./gradlew :nativeBridge-script:publish
cd /Users/allin327/maven-repository
git add .
git commit -m "Deploy new library version"
git push origin gh-pages