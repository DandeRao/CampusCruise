/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadb;
 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
 
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
/**
 *
 * @author S525037
 */
public class GenerateQR {
    public static void main(String[] args) {
 
ByteArrayOutputStream out = QRCode.from("Sneha Kotu - GDP1 project").to(ImageType.PNG).stream();
 
try {
 
FileOutputStream fout = new FileOutputStream(new File("D:\\Generated_QR.PNG"));
 
fout.write(out.toByteArray());
 
fout.flush();
fout.close();
 
} catch (FileNotFoundException e) {
// Do Logging
e.printStackTrace();
} catch (IOException e) {
// Do Logging
e.printStackTrace();
}
}
}
