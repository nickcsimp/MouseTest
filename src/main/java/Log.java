import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;

public class Log {

    File log;

    public Log() throws InterruptedException, IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
        log = new File(System.getProperty("user.dir")+"/Mouse Log"+LocalDateTime.now() +".txt");
        if(log.createNewFile()) {
            System.out.println("File created: " + log.getName());
        }

    }

    //adds a line with the RR at some point in time
    public void addPoint(String isoChange) throws InterruptedException, IOException{
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        FileWriter myWriter = new FileWriter(log.getName());
        myWriter.write(LocalDateTime.now().format(dtf)+"\t"+"Change of isoflurane: "+isoChange);
        myWriter.close();
    }

}
