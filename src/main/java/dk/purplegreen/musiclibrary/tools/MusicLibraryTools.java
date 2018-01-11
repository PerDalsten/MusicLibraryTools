package dk.purplegreen.musiclibrary.tools;

public class MusicLibraryTools {

    public static void main(String[] args) {
        if (!(args.length > 0)) {
            showHelp();
        } else {
            switch (args[0]) {
                case "derbyimport":
                    DerbyImport.main(args);
                    break;
                case "derbyexport":
                    DerbyExport.main(args);
                    break;
                case "msyqlexport":
                    MySQLExport.main(args);
                    break;
                case "audiotagimport":
                    AudioTagImport.main(args);
                    break;
                case "albumreport":
                    AlbumReport.main(args);
                    break;
                case "help":
                default:
                    showHelp();
            }
        }
    }

    private static void showHelp() {
        System.out.println("Usage: derbyimport|derbyexport|mysqlexport|audiotagimport");
        System.out.println("\n\tderbyimport: Import from Derby to XML");
        System.out.println("\tderbyexport: Export from XML to Derby");
        System.out.println("\tmysqlexport: Export from XML to MySQL");
        System.out.println("\taudiotagimport: Import from audio tagged MP3 to XML");
        System.out.println("\talbumreport: Output summary of XML album file(s)");
    }

}