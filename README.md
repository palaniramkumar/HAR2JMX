# HAR2JMX

Simple application to convert HAR to JMX

# Credits:

- Credits to all jmeter deverloper and Thanks to benchlab for the awesome library https://sourceforge.net/projects/benchlab/.

- This code uses jmeter & benchlab library to parse and generate the jmx file.

# How to use 

modify the config file as below,

{
  "exclude_host_name": [
   "ayx.com","aaa.com"
  ],
   "exclude_file_type": ["png","jpg","css","js","svg","gif","woff"],
  "jmx_output_name": "test.jmx",
  "jmeter_home": "/home/apache-jmeter-2.12",
  "download_resource": false,
  "har_location":"/home/O365_drive.har"
}

and just simple run the jar file :)
