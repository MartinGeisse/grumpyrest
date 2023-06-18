./gradlew clean javadoc
cd ../martingeisse.github.io/javadoc
rm -rf grumpyrest
mkdir grumpyrest
cd grumpyrest
cp -R ../../../grumpyrest/grumpyrest/build/docs/javadoc/* .
