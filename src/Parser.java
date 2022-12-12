import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Trida na precteni souboru o mapach a odstraneni komentaru z neho
 */
public class Parser {

    /**
     * Nacte cely vstupni soubor a ulozi ho do Stringu, ze ktereho pak odstrani komentare
     *
     * @param file		cely nazev vstupniho souboru
     * @return				vstupni soubor jako String bez komentaru
     * @throws IOException    chyba ve vstupnim souboru nebo jeho jmene
     */
    public static String fileToString(String file) throws IOException {
        String s = new String(Files.readAllBytes(Paths.get(file)));

        int end;
        int start = 0;
        while((end = s.indexOf("🏜", start)) > 0) {
            start = s.lastIndexOf("🐪", end);
            s = (s.substring(0, start)) + " " + (s.substring(end+2));
        }

        return s;
    }
}
