cd ../grumpyrest.wiki
rm -rf doc
cp ../grumpyrest/README.md Home.md
cp -R ../grumpyrest/doc .
git add -A
git commit -m sync
git push
