/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PDFBean;

//These imports are for the PDF file creation
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfLayer;

//These imports are for the QR barcode creation
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Hashtable;

import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.*;

import com.google.zxing.oned.ITFWriter;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author astell
 */
public class PDFBean {

    public void createPdf(String filename, String[] labelOutputStr/*, String[] imagefilename*/, boolean a4labels) {

        if (!a4labels) {

            int labelNumber = labelOutputStr.length;
            //int imageNumber = imagefilename.length;

            // step 1
            //Document document = new Document(PageSize.POSTCARD.rotate(), 30, 30, 30, 30);
            Document document = new Document(PageSize.POSTCARD.rotate(), 1, 1, 1, 1);
            // step 2
            PdfWriter writer = null;
            try {
                writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
                writer.setCompressionLevel(0);
            } catch (DocumentException de) {
                System.out.println("Document error: " + de.getMessage());
            } catch (IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }
            // step 3
            document.open();
            // step 4
            try{
                PdfContentByte cb = writer.getDirectContent();            
                PdfLayer zoom = new PdfLayer("Zoom 2.80-3.00", writer);
                zoom.setOnPanel(false);
                zoom.setZoom(2.80f, 3.00f);
                cb.beginLayer(zoom);            
                cb.endLayer();
            }catch(Exception e){
                System.out.println("Zoom layer setting error: " + e.getMessage());
            }

            //PdfPTable table = new PdfPTable(2);
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100f);

            for (int i = 0; i < labelNumber; i++) {
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(labelOutputStr[i], FontFactory.getFont(FontFactory.HELVETICA, 30, Font.BOLD, new BaseColor(0, 0, 0))));
                cell.setFixedHeight(200f);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                table.addCell(cell);

                //TESTING - TO REPLACE IMAGE
                //table.addCell(cell);
                //}

                //for (int i = 0; i < imageNumber; i++) {
                /*Image img = null;
                try {
                    img = Image.getInstance(imagefilename[i] + "_itf.png");
                } catch (Exception e) {
                    System.out.println("Bad element error: " + e.getMessage());
                }
                img.setAbsolutePosition((PageSize.POSTCARD.getWidth() - img.getScaledWidth()) / 2,
                        (PageSize.POSTCARD.getHeight() - img.getScaledHeight()) / 2);
                PdfPCell cell2 = new PdfPCell(img);
                cell2.setBorder(Rectangle.NO_BORDER);
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell2);*/
            }

            try {
                document.add(table);
            } catch (DocumentException de) {
                System.out.println("Document error: " + de.getMessage());
            }

            // step 5
            document.close();


        } else {
            int labelNumber = labelOutputStr.length;
            int numberIn = labelNumber;
            int rowSize = 5;
            //labelOutputStr = "test";

            // step 1
            //Document document = new Document(PageSize.POSTCARD.rotate(), 30, 30, 30, 30);
            //Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            Document document = new Document(PageSize.A4, 20, 20, 26, 20);
            // step 2
            PdfWriter writer = null;
            try {
                writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
                writer.setCompressionLevel(0);
            } catch (DocumentException de) {
                System.out.println("Document error: " + de.getMessage());
            } catch (IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }
            // step 3
            document.open();
            // step 4

            PdfPTable table = new PdfPTable(rowSize);
            table.setWidthPercentage(100f);            

            for (int j = 0; j < (numberIn / rowSize); j++) {

                for (int i = 0; i < rowSize; i++) {

                    String phraseIn = labelOutputStr[i + (j * rowSize)];
                    //String imageIn = imagefilename[i + (j * rowSize)] + "_itf.png";
                    Phrase phrase = new Phrase(phraseIn, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new BaseColor(0, 0, 0)));

                    //PdfPTable subtable = new PdfPTable(2);
                    PdfPTable subtable = new PdfPTable(1);                    
                    subtable.setWidthPercentage(20f);
                    //for (int k = 0; k < 4; k++) {
                    PdfPCell cell1 = new PdfPCell(phrase);
                    //cell1.setFixedHeight(50f);
                    cell1.setFixedHeight(61f);
                    cell1.setBorder(Rectangle.NO_BORDER);
                    cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtable.addCell(cell1);

                    /*Image img = null;
                    try {
                    img = Image.getInstance(imageIn);
                    } catch (Exception e) {
                    System.out.println("Bad element error: " + e.getMessage());
                    }
                    img.setAbsolutePosition((PageSize.POSTCARD.getWidth() - img.getScaledWidth()) / 2,
                    (PageSize.POSTCARD.getHeight() - img.getScaledHeight()) / 2);
                    
                    
                    PdfPCell cell2 = new PdfPCell(img);
                    //cell2.setFixedHeight(100f);
                    cell2.setBorder(Rectangle.NO_BORDER);
                    cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtable.addCell(cell2);*/

                    //}

                    PdfPCell subcell = new PdfPCell(subtable);
                    subcell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(subcell);
                }
            }

            if (numberIn % rowSize != 0) {

                for (int i = 0; i < (numberIn % rowSize); i++) {

                    String phraseIn = labelOutputStr[labelNumber - ((numberIn % rowSize)-i)];
                    //String imageIn = imagefilename[labelNumber - 1] + "_itf.png";
                    Phrase phrase = new Phrase(phraseIn, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new BaseColor(0, 0, 0)));

                    PdfPTable subtable = new PdfPTable(1);
                    subtable.setWidthPercentage(20f);
                    //for (int k = 0; k < 4; k++) {
                    PdfPCell cell1 = new PdfPCell(phrase);
                    //cell1.setFixedHeight(50f);
                    cell1.setFixedHeight(61f);
                    cell1.setBorder(Rectangle.NO_BORDER);
                    cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtable.addCell(cell1);

                    /*Image img = null;
                    try {
                    img = Image.getInstance(imageIn);
                    } catch (Exception e) {
                    System.out.println("Bad element error: " + e.getMessage());
                    }
                    img.setAbsolutePosition((PageSize.POSTCARD.getWidth() - img.getScaledWidth()) / 2,
                    (PageSize.POSTCARD.getHeight() - img.getScaledHeight()) / 2);
                    
                    
                    PdfPCell cell2 = new PdfPCell(img);
                    //cell2.setFixedHeight(100f);
                    cell2.setBorder(Rectangle.NO_BORDER);
                    cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtable.addCell(cell2);*/

                    //}
                    PdfPCell subcell = new PdfPCell(subtable);
                    subcell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(subcell);
                }

                for (int i = 0; i < (rowSize - (numberIn % rowSize)); i++) {
                    String phraseIn = "";
                    Phrase phrase = new Phrase(phraseIn, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, new BaseColor(0, 0, 0)));

                    PdfPTable subtable = new PdfPTable(1);
                    subtable.setWidthPercentage(20f);
                    //for (int k = 0; k < 4; k++) {
                    PdfPCell cell1 = new PdfPCell(phrase);
                    //cell1.setFixedHeight(50f);
                    cell1.setFixedHeight(61f);
                    cell1.setBorder(Rectangle.NO_BORDER);
                    cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtable.addCell(cell1);

                    /*cell2 = new PdfPCell(phrase);
                    cell2.setFixedHeight(100f);
                    cell2.setBorder(Rectangle.NO_BORDER);
                    cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    subtable.addCell(cell2);*/

                    //}

                    PdfPCell subcell = new PdfPCell(subtable);
                    subcell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(subcell);
                }
            }


            try {
                document.add(table);
            } catch (DocumentException de) {
                System.out.println("Document error: " + de.getMessage());
            }

            // step 5
            document.close();
        }
    }

    public void createQR(String filename, String labelOutputStr, boolean a4labels) {

        //Need to modify the label to two characters here (assume no greater than six)
        int labelNumLength = labelOutputStr.length();
        if (labelNumLength % 2 != 0) {
            //If character length is odd, add a leading zero
            labelOutputStr = "0" + labelOutputStr;
        }
        labelNumLength = labelOutputStr.length();
        int elemNum = labelNumLength / 2;

        String[] labelOutputArray = new String[elemNum];

        for (int i = 0; i < elemNum; i++) {
            int startElem = i * 2;
            labelOutputArray[i] = "" + labelOutputStr.charAt(startElem) + labelOutputStr.charAt(startElem + 1);
        }

        BufferedImage[] bimg = new BufferedImage[elemNum];
        String fileNew = filename + "_itf.png";
        for (int i = 0; i < elemNum; i++) {
            bimg[i] = new BufferedImage(50, 100, BufferedImage.TYPE_BYTE_GRAY);
        }

        Charset charset = Charset.forName("ISO-8859-1");
        CharsetEncoder encoder = charset.newEncoder();

        ByteBuffer[] bbuf = new ByteBuffer[elemNum];
        byte[] b = null;
        for (int i = 0; i < elemNum; i++) {
            try {
                // Convert a string to UTF-8 bytes in a ByteBuffer
                bbuf[i] = encoder.encode(CharBuffer.wrap(labelOutputArray[i]));
                b = bbuf[i].array();

                //b = bbuf.array();
            } catch (CharacterCodingException e) {
                System.out.println(e.getMessage());
            }

            String[] data = new String[elemNum];
            //Integer data
            try {

                data[i] = new String(b, "ISO-8859-1");
                // get a byte matrix for the data
                BitMatrix[] matrix = new BitMatrix[elemNum];
                int h = 60;
                int w = 60;
                /*if(a4labels){
                h = 60;
                w = 60;
                }*/

                com.google.zxing.Writer writer = new MultiFormatWriter();
                //com.google.zxing.oned.ITFWriter writer = new ITFWriter();
                try {
                    Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
                    hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
                    matrix[i] = writer.encode(data[i], com.google.zxing.BarcodeFormat.ITF, w, h, hints);
                } catch (com.google.zxing.WriterException e) {
                    System.out.println(e.getMessage());
                }

                bimg[i] = MatrixToImageWriter.toBufferedImage(matrix[i]);
                //ImageIO.write(bimg[i], "png", file);
                //MatrixToImageWriter.writeToFile(matrix[i], "PNG", file);
                //System.out.println("printing to " + file.getAbsolutePath());

            } catch (UnsupportedEncodingException e) {
                System.out.println("UnsupportedEncodingException: " + e.getMessage());
            }
        }


        BufferedImage imageOne = bimg[0];

        //Now stitch the first two images together and write the output
        if (elemNum > 1) {
            BufferedImage imageTwo = bimg[1];

            int width1, width2, height1, height2, height;
            try {
                width1 = imageOne.getWidth();// image width 
                height1 = imageOne.getHeight();// image height 
                width2 = imageTwo.getWidth();// image width 
                height2 = imageTwo.getHeight();// image height 
                if (height1 > height2) {
                    height = height1;
                } else {
                    height = height2;
                }

                // Read from the RGB image 
                int[] imageArrayOne = new int[width1 * height1];
                imageArrayOne = imageOne.getRGB(0, 0, width1, height1, imageArrayOne, 0, width1);

                int[] imageArrayTwo = new int[width2 * height2];
                imageArrayTwo = imageTwo.getRGB(0, 0, width2, height2, imageArrayTwo, 0, width2);

                // Generate a new image
                BufferedImage imageNew = new BufferedImage(width1 + width2, height, BufferedImage.TYPE_INT_RGB);

                imageNew.setRGB(0, 0, width1, height1, imageArrayOne, 0, width1);
                // set the left part of the RGB 
                imageNew.setRGB(width1, 0, width2, height, imageArrayTwo, 0, width2);
                // set the right part of the RGB 

                File file = new File(fileNew);
                ImageIO.write(imageNew, "png", file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                File file = new File(fileNew);
                ImageIO.write(imageOne, "png", file);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /*Charset charset = Charset.forName("ISO-8859-1");
        CharsetEncoder encoder = charset.newEncoder();
        byte[] b = null;
        try {
        // Convert a string to UTF-8 bytes in a ByteBuffer
        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(labelOutputStr));
        b = bbuf.array();
        } catch (CharacterCodingException e) {
        System.out.println(e.getMessage());
        }
        
        String data;
        try {
        data = new String(b, "ISO-8859-1");
        // get a byte matrix for the data
        BitMatrix matrix = null;
        int h = 100;            
        int w = 100;            
        //com.google.zxing.Writer writer = new MultiFormatWriter();
        com.google.zxing.oned.ITFWriter writer = new ITFWriter();
        try {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
        hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
        matrix = writer.encode(data,com.google.zxing.BarcodeFormat.ITF, w, h, hints);
        } catch (com.google.zxing.WriterException e) {
        System.out.println(e.getMessage());
        }
        
        // change this path to match yours (this is my mac home folder, you can use: c:\\qr_png.png if you are on windows)            
        File file = new File(filename);
        try {
        MatrixToImageWriter.writeToFile(matrix, "PNG", file);
        //System.out.println("printing to " + file.getAbsolutePath());
        } catch (IOException e) {
        System.out.println(e.getMessage());
        }
        } catch (UnsupportedEncodingException e) {
        System.out.println(e.getMessage());
        }*/




    }
}
