Tools
=====

AudioTagImport: Import to xml from MP3
PMAXMLImport: Import to xml from MySQL dump
DerbyImport: Import to xml from Derby DB

DerbyExport: Export xml to Derby DB
MySQLExport: Export xml to MySQL DB


MySql reset:

delete from song;
delete from album;
delete from artist;
alter table song auto_increment =1;
alter table artist auto_increment =1;
alter table album auto_increment =1;
