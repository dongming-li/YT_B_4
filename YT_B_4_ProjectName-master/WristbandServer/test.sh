echo "updaing server" >> log.txt
git pull
echo "downloading files" >> log.txt
forever restartall
echo "started server" >> server started
