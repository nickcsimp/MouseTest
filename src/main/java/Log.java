import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Log extends Thread{
    boolean finished;
    PrintWriter log;
    SidePanel sidePanel;
    Integer iso; // isoflurane concentration

    public Log(SidePanel sidePanel) throws InterruptedException, IOException {
        this.sidePanel = sidePanel;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM");
        log = null;
        try {
            log = new PrintWriter(new File("MouseLog.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("File created");
        // give category "headings"
        addPoint("iso", "average", "current");


    }

    public void changeIso(Integer isoChange){
        this.iso = this.iso+isoChange;
    }

    //adds a line with the RR at some point in time
    public void addPoint(String iso, String average, String current) throws InterruptedException, IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(dtf) + ",");
        sb.append(iso + ",");
        sb.append(average + ",");
        sb.append(current + ",");

        log.write(sb.toString());
        log.close();
    }

    public void run(){
        while(!finished) {
            try {
                addPoint(iso.toString(), sidePanel.getCurrent(), sidePanel.getAverage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}