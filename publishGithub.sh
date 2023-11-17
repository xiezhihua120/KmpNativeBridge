./gradlew clean
./gradlew :nativeBridge-annotation:publish
./gradlew :nativeBridge-processor:publish
./gradlew :nativeBridge-script:publish
cd /Users/allin327/maven-repository
git add .
git commit -m "Deploy new library version"
git push origin gh-pages