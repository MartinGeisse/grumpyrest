mvn javadoc:aggregate
cd ../grumpystuff.github.io/javadoc
rm -rf grumpyrest
mkdir grumpyrest
cd grumpyrest
cp -R ../../../grumpyrest/target/site/apidocs/* .
git add -A
git commit -m sync
git push
