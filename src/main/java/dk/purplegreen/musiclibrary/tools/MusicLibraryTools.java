package dk.purplegreen.musiclibrary.tools;

public class MusicLibraryTools {

	public static void main(String[] args) {
		if (args.length == 0) {
			showHelp();
		} else {
			switch (args[0]) {
			case "derbyimport":
				DerbyImport.main(args);
				break;
			case "derbyexport":
				DerbyExport.main(args);
				break;
			case "mysqlimport":
				MySQLImport.main(args);
				break;	
			case "msyqlexport":
				MySQLExport.main(args);
				break;
			case "hsqlimport":
				HSQLImport.main(args);
				break;	
			case "hsqlexport":
				HSQLExport.main(args);
				break;
			case "audiotagimport":
				AudioTagImport.main(args);
				break;
			case "albumreport":
				AlbumReport.main(args);
				break;
			case "audiotagactivemq":
				AudioTagActiveMQ.main(args);
				break;
			case "help":
			default:
				showHelp();
			}
		}
	}

	private static void showHelp() {
		System.out.println("Usage: derbyimport|derbyexport|mysqlexport|audiotagimport|audiotagactivemq|albumreport");
		System.out.println("\n\tderbyimport: Import from Derby to XML");
		System.out.println("\tderbyexport: Export from XML to Derby");
		System.out.println("\tmysqlimport: Import from MySQL to XML");
		System.out.println("\tmysqlexport: Export from XML to MySQL");
		System.out.println("\thsqlimport: Import from HSQL to XML");
		System.out.println("\thsqlexport: Export from XML to HSQL");
		System.out.println("\taudiotagimport: Import from audio tagged MP3 to XML");
		System.out.println("\taudiotagactivemq: Send audio tagged MP3 to ActiveMQ");
		System.out.println("\talbumreport: Output summary of XML album file(s)");
	}

}
