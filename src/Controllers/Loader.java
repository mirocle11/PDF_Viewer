package Controllers;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;


public class Loader {

    String filename;
    XMLEventReader reader;
    XMLInputFactory factory;
    static ArrayList<String> points = new ArrayList<>();
    String[] dPoints;
    public double width = 0;
    public double height;

    public Loader(String filename) {
        this.filename = filename;
        initialize();
    }

    public void initialize() {
        factory = XMLInputFactory.newInstance();
        factory.setProperty("javax.xml.stream.isValidating", false);
        factory.setProperty("javax.xml.stream.isNamespaceAware", false);
        factory.setProperty("javax.xml.stream.supportDTD", false);
        factory.setProperty("javax.xml.stream.isCoalescing", true);
        try {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bufferedStream = new BufferedInputStream(fis);
            reader = factory.createXMLEventReader(bufferedStream);
        } catch (Exception e) {

        }
    }

    public void getPaths() throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = (StartElement) event;
                if (startElement.getName().toString().equals("path")) {
                    Attribute dAttribute = startElement.getAttributeByName(new QName("d"));
                    dPoints = dAttribute.toString().split("[d=|'|M|L|C|z]");
                    for (String st : dPoints) {
                        if (!points.contains(st)) {
                            points.add(st);
                        }
                    }
                }
            }
        }
    }

    public ArrayList getSnap() {
        try {
            getPaths();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        String[] pntsA;
        double[][] arr;
        ArrayList snapList = new ArrayList<>();
        for (int k = 0; k < points.size(); k++) {
            try {
                if (!points.get(k).equals("") && !points.get(k).equals(null)) {
                    pntsA = points.get(k).split(" ");
                    double a = Double.parseDouble(pntsA[0]);
                    double b = Double.parseDouble(pntsA[1]);
                    arr = new double[1][2];
                    arr[0][0] = (a / .23999)-14;
                    arr[0][1] = height+60 - (b / .23999);
                    snapList.add(arr);
                }
            } catch (Exception e) {

            }
        }
        return snapList;
    }

//    public static void main(String[] args) {
//
//
//        File tempDirectory = new File("temp/"); //temp directory address
//        if (!tempDirectory.exists()) {
//            try {
//                tempDirectory.mkdir();
//            } catch (SecurityException se) {
//                System.out.println("Unable to create temp directory.");
//            }
//        }
//
//        File tempImg = new File(tempDirectory, "images");
//        if (!tempImg.exists()) {
//            try {
//                tempImg.mkdir();
//            } catch (SecurityException se) {
//                System.out.println("Unable to create temp/image directory.");
//            }
//        }
//
//        File tempSvg = new File(tempDirectory, "svg");
//        if (!tempSvg.exists()) {
//            try {
//                tempSvg.mkdir();
//            } catch (SecurityException se) {
//                System.out.println("Unable to create temp/svg directory.");
//            }
//        }
//
//        String inputFile = "C:\\Users\\John Ebarita\\Documents\\JOHN\\miro.pdf"; //dynamic PDF inputt
//
//        File[] svgs = tempSvg.listFiles();
//        File[] imgs = tempImg.listFiles();
//
//            for (File f : svgs) f.delete();
//            for (File f : imgs) f.delete();
//
//        PdfDocument document = new PdfDocument();
//        document.loadFromFile(inputFile);
//
//        BufferedImage image;
//        File file; //recyclable file variable
//
//        for (int i = 0; i < document.getPages().getCount(); i++) {
//            image = document.saveAsImage(i);
//            try {
//                file = new File(tempImg.getPath() + String.format(("/img_%d.png"), i));
//                ImageIO.write(image, "PNG", file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        document.saveToFile(tempSvg.getAbsolutePath() + "/svg.svg", FileFormat.SVG);
//        document.close();
//
//        svgs = tempSvg.listFiles();
//        imgs = tempImg.listFiles();
//
//        Loader load = new Loader(svgs[1].getAbsolutePath());
//
//
//        try {
//            load.getPaths();
//            String[] pntsA;
//            BufferedImage gff = ImageIO.read(imgs[1]);
//
////            BufferedImage bimage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = gff.createGraphics();
//            AffineTransform at = new AffineTransform();
//            at.concatenate(AffineTransform.getScaleInstance(1, -1));
//            at.concatenate(AffineTransform.getTranslateInstance(0, -gff.getHeight()));
//            g2d.transform(at);
//            g2d.setColor(Color.RED);
//            for (int i = 0; i < load.points.size(); i++) {
//                try {
//                    if (!load.points.get(i).equals("") && !load.points.get(i).equals(null)) {
//                        pntsA = load.points.get(i).split(" ");
//                        int a = (int) Double.parseDouble(pntsA[0]);
//                        int b = (int) Double.parseDouble(pntsA[1]);
//                        g2d.drawRect(a, b, 1, 1);
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//            g2d.dispose();
//            ImageIO.write(gff, "png", imgs[1]);
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
