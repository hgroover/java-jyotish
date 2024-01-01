Developer notes for Java Jyotish

To run and debug locally, run the following on Ubuntu / Debian linux systems:
sudo apt-get install apache2
sudo apt-get install php5
sudo apache2ctl restart

You should now have a /var/www directory. By default javajyotish will install itself there if you have write permissions.

For debugging, you can run firefox from the command line and view Java console output directly, e.g.
firefox http://localhost/javajyotish

