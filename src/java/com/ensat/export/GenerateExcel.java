package com.ensat.export;

// Import required java libraries
import ConnectBean.ConnectBean;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Extend HttpServlet class
public class GenerateExcel extends HttpServlet {

    private String message;

    public void init() throws ServletException {
        // Do required initialization
        message = "File has been created";
    }

    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        //response.setContentType("application/csv");
        PreparedStatement ps = null;
        Connection conn = null;
        File csv = null;
        try {
            ConnectBean connect = (ConnectBean) request.getSession().getAttribute("connect");
            conn = connect.getConnection();

            String centerid = request.getParameter("centerid");
            String group_id = request.getParameter("group_id");
         
            //SQL String to retrieve transferred materials for given centerid and group_id
            
            String sql = "SELECT F.ensat_id, F.bio_id, B.biomaterial_date, F.material,  F.aliquot_sequence_id, "
                    + "F.freezer_number, F.freezershelf_number, F.rack_number, F.shelf_number, F.box_number, F.position_number, "
                    + "T.center_id, T.destination_center_id, T.transfered_date, T.status FROM "
                    + "ACC_Biomaterial_Freezer_Information as F left join ACC_Biomaterial as B ON "
                    + "B.acc_biomaterial_id = F.acc_biomaterial_id AND B.center_id = F.center_id AND B.ensat_id = F.ensat_id "
                    + "left join acc_biomaterial_aliquots_transfer as T ON "
                    + "F.acc_biomaterial_location_id = T.acc_biomaterial_location_id AND F.ensat_id = T.ensat_id "
                    + "AND F.center_id = T.center_id AND F.acc_biomaterial_id = T.acc_biomaterial_id "
                    + " WHERE F.center_id= ? AND F.material_transferred!= ? AND T.acc_biomaterial_transfer_group_id = ?;";
          
            ps = conn.prepareStatement(sql);
            ps.setString(1, centerid);
            ps.setString(2, "");
            ps.setString(3, group_id);
            ResultSet rs = ps.executeQuery();
            csv = generateExcelFile(rs,group_id);
        } catch (Exception e) {
            //logger.debug("Error (getAliquotsTransferred): " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    //conn.close();
                }
            } catch (Exception e) {
            }
        }
        
        PrintWriter out = response.getWriter();
        //out.print(csv);
        out.println("<h1>" + message + "</h1>");
    }

    public File generateExcelFile(ResultSet rs, String group_id) {
        File csv = null;
        try {
            // Get generated qr code from its location
             String image_name = "qr_" + group_id + ".png";
            // String filePath = "E:/GIT_REPO/ENSAT/web/images/qr_code/"+image_name;
             
             // Get QR code path from servelet config parameter value
             String filePath = getServletContext().getInitParameter("qr_code_path");
             filePath = filePath + image_name;
        
             int size = 125;
             String fileType = "png";
             File myFile = new File(filePath);
            //Blank workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            //Create a blank sheet
            XSSFSheet sheet = workbook.createSheet("Biomaterial Information Data");
            //This data needs to be written (Object[])
            Map<String, Object[]> data = new TreeMap<String, Object[]>();
            
          //FileInputStream obtains input bytes from the image file
            InputStream inputStream = new FileInputStream(filePath);
            //Get the contents of an InputStream as a byte[].
            byte[] bytes = IOUtils.toByteArray(inputStream);
            //Adds a picture to the workbook
            int pictureIdx = workbook.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
            //close the input stream
            inputStream.close();

            //Returns an object that handles instantiating concrete classes
            CreationHelper helper = workbook.getCreationHelper();

            //Creates the top-level drawing patriarch.
            Drawing drawing = sheet.createDrawingPatriarch();

            //Create an anchor that is attached to the worksheet
            ClientAnchor anchor = helper.createClientAnchor();
            //set top-left corner for the image
            anchor.setCol1(1);
            anchor.setRow1(2);
            
              //Creates a picture
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            //Reset the image to the original size
            pict.resize();
 
           // Set excel collumn names as required
            data.put("1", new Object[]{"ENSAT_ID", "BIO_ID", "MATERIAL_DATE", "MATERIAL" ,"ALIQUOT_SEQUENCE_ID", "FREEZER_NO", "F_SHELF_NO", "RACK_NO", "RACK_SHELF_NO", "BOX", "POSITION", "FROM_CENTER", "TO_CENTER", "TRANSFERRED_DATE"});
            int i = 1;
            while (rs.next()) {
                data.put(++i + "", new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),rs.getString(8),rs.getString(9), rs.getString(10), rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)});
            }
            //Iterate over data and write to sheet
            Set<String> keyset = data.keySet();
            int rownum = 10;
            for (String key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
                    if (obj instanceof String) {
                        cell.setCellValue((String) obj);
                    } else if (obj instanceof Integer) {
                        cell.setCellValue((Integer) obj);
                    }
                }
            }

            //Write the workbook in file system
            //String excel_file_path = "E:\\GIT_REPO\\ENSAT\\web\\exported_files\\Manifest\\Ensat_manifest_" + group_id + ".xlsx";
           
            //Set file path by getting value from servelet config parameter
            String excel_file_path = getServletContext().getInitParameter("excel_manifest_path");
            excel_file_path = excel_file_path + "Ensat_manifest_" + group_id + ".xlsx";
            csv = new File(excel_file_path);
           
            // write csv excel file
            FileOutputStream out = new FileOutputStream(csv);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return csv;
    }
}
