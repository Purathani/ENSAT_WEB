---ENSAT WEB MODULE---

System Requirements
=============================================
Java JDK 1.8 or later
Netbeans 8.0.2
Apache Tomcat Server 8.0.15.0
MySql and JDBC connection
External Libraries: zxing-2.1.jar, poi-3.9.jar, core.jar, junit.jar, xmlbeans-2.3.0.jar, commons.io-2.4.jar, google-collect-1.0.jar and other system libraries as needed

Instructions to compile and run the project
===============================================

Import Sql files using MySQL workbench
Open ENSAT project in Netbeans IDE
Change context parameters values in WEB-INF -> web.xml
Start Tomcat Server
Deploy and Run the project
Use testuser to login the ENSAT Registry web application
In the home page, click 'FreezerInventory' link
Then go to required Freezer number, Shelf Number, Rack Number and Box Number
The list of biomaterial samples will be displayed in the page
Select destination center from dropdown list to transfer multiple biomaterial samples as requrested 
Click 'Transfer Aliquots' button in the bottom of the list
It will be redirected to confirmation list page 
Then go back and see the list of materials to be transferred in separate list in the page
Click 'Generate QR Code' button to generate QR image (QR code path : web/images/qr_code/qr_group_id.png")
Click 'Export Excel' button to generate Excel manifest file with embeded QR code (Excel manifest path: ENSAT\web\exported_files\Manifest\Ensat_manifest_group_id.xlsx")

===============================================================================================================
Note: For security reason I've only provided the files which I've modified or created for my project purpose. 
      The whole ENSAT Registry project has not been copied here.
