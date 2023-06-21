cd ..
cd grumpyrest-wiki
cp ../grumpyrest/README.md Home.md
rm -rf doc
cp -R ../grumpyrest/doc .
git status
