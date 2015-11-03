package fileupload;

/*
 * import org.apache.commons.fileupload.servlet.ServletFileUpload; import
 * org.apache.commons.fileupload.FileItemFactory; import
 * org.apache.commons.fileupload.FileUploadException; import
 * org.apache.commons.fileupload.FileItem; import
 * org.apache.commons.fileupload.disk.DiskFileItemFactory;
 */
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.io.output.*;

/*
 * import javax.servlet.http.HttpServlet; import
 * javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import
 * javax.servlet.ServletException; import java.io.IOException; import
 * java.io.File; import java.util.List; import java.util.Iterator;
 */

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FileUploadServlet extends HttpServlet {

    private static final long serialVersionUID = -3208409086358916855L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String dbid = "";
        String dbn = "";
        String pid = "";
        String centerid = "";
        String modality = "";
        String modid = "";
        
        String comment = "";
        String date = "";
        
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        //System.out.println("isMultipart: " + isMultipart);

        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);

            try {
                List items = upload.parseRequest(request);
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem) iterator.next();

                    //System.out.println("item name: " + item.getFieldName());
                    //System.out.println("item string: " + item.getString());
                    String itemName = item.getFieldName();
                    if (itemName.equals("dbid")) {
                        dbid = item.getString();
                    } else if (itemName.equals("dbn")) {
                        dbn = item.getString();
                    } else if (itemName.equals("pid")) {
                        pid = item.getString();
                    } else if (itemName.equals("centerid")) {
                        centerid = item.getString();
                    } else if (itemName.equals("modality")) {
                        modality = item.getString();
                    } else if (itemName.equals("comment")) {
                        comment = item.getString();
                    } else if (itemName.equals("metabolomics_date")) {
                        date = item.getString();
                    } else if (itemName.equals("modid")) {
                        modid = item.getString();
                    }

                    if (!item.isFormField() && !(item.getString().trim().equals(""))) {
                        String fileName = item.getName();

                        //String root = getServletContext().getRealPath("/");
                        String root = getServletContext().getInitParameter("file_upload");
                        File path = new File(root + "/uploads");
                        if (!path.exists()) {
                            boolean status = path.mkdirs();
                        }

                        fileName = "metabolomics_" + centerid + "_" + pid + "_" + modid + ".pdf";
                        File uploadedFile = new File(path + "/" + fileName);
                        System.out.println("Uploaded file: " + uploadedFile.getAbsolutePath());
                        item.write(uploadedFile);
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String forwardPath = "/jsp/modality/create/create_confirm.jsp";
            forwardPath += "?dbid=" + dbid;
            forwardPath += "&dbn=" + dbn;
            forwardPath += "&pid=" + pid;
            forwardPath += "&centerid=" + centerid;
            forwardPath += "&modality=" + modality;
            forwardPath += "&modid=" + modid;
            
            //Explicitly catch the attributes (rather than parameters) at the other side
            request.setAttribute("comment",comment);
            request.setAttribute("metabolomics_date",date);
            
            System.out.println("ForwardPath: " + forwardPath);

            getServletContext().getRequestDispatcher(forwardPath).forward(request, response);
        }
    }    
}