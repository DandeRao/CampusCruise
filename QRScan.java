/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadb;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 *
 * @author S525037
 */

public class QRScan  {
    
    public static void main(String[]args) throws Exception{
        String details = "Campus tour application - Team C" +"Colden HAll";
        
        ByteArrayOutputStream out = QRCode.from(details).to(ImageType.JPG).stream();
        File f = new File("C:\\Users\\S525037\\Documents\\NetBeansProjects\\JavaDB\\MyQRCode.jpg");
        FileOutputStream fos = new FileOutputStream(f);      
        fos.write(out.toByteArray());
        fos.flush();
        
    }
}