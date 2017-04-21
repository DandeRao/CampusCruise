package com.campusTour;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "university")
@ManagedBean
public class University {

    //List to store university
    List<University> universityList = new ArrayList<>();
    private University tempUniversity;
    private String universityCoordinates;
    private String outputMessage;
    private List<Part> universityImage;
    private List<Part> universityAudio;
    private List<Part> universityVideo;

    public String getOutputMessage() {
        return outputMessage;
    }

    public List<Part> getUniversityImage() {
        return universityImage;
    }

    public void setUniversityImage(List<Part> universityImage) {
        this.universityImage = universityImage;
    }

    public List<Part> getUniversityAudio() {
        return universityAudio;
    }

    public void setUniversityAudio(List<Part> universityAudio) {
        this.universityAudio = universityAudio;
    }

    public List<Part> getUniversityVideo() {
        return universityVideo;
    }

    public void setUniversityVideo(List<Part> universityVideo) {
        this.universityVideo = universityVideo;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    public String getUniversityCoordinates() {
        return universityCoordinates;
    }

    public void setUniversityCoordinates(String universityCoordinates) {
        this.universityCoordinates = universityCoordinates;
    }

    /**
     * Creates a new instance of University
     */
    public University() {

    }
    //Getting the current instance using face context
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    //created seesion
    HttpSession session = request.getSession();

    String univName;
    //user to admin university
    private String user;

    /**
     * Method that gets the password
     *
     * @return password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method that sets the password
     *
     * @param password password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
    private String password;

    /**
     * Method that gets the user
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Method that sets the user
     *
     * @param user email of the user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Method that sets the university name
     *
     * @param univName name of the university
     */
    public void setUnivName(String univName) {
        this.univName = univName;
    }

    /**
     * Method that gets the university name
     *
     * @return universityName
     */
    public String getUnivName() {
        return this.univName;
    }

    /**
     * Method that add the university to database
     *
     * @return string
     */
    public String addUniversity() throws IOException, ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");

        Statement st = con.createStatement();
        ResultSet r = st.executeQuery("select count(*) from universities where university_name='" + this.getUnivName() + "'");
        r.next();
        int number = r.getInt(1);
        if (number != 0) {

            outputMessage = "University Already exists";
            return "";

        }

        r = st.executeQuery("select count(*) from user where email='" + this.getUser() + "'");
        r.next();
        number = r.getInt(1);
        if (number == 0) {
            st.executeUpdate("insert into user values('" + this.getUser() + "','" + this.getPassword() + "','user')");
            ResultSet rs = st.executeQuery("select max(university_id) from universities");
            rs.next();
            int count = rs.getInt(1);

            st.executeUpdate("insert into universities values('" + (count + 1) + "','" + this.getUnivName() + "','" + this.getUniversityCoordinates() + "', '" + ";" + "','" + this.getUser() + "')");
            String path = "C:\\CampusTourFiles\\RawFiles";
            String univ = (this.getUnivName()).replace(" ", "");
            path = path + "\\" + univ;
            File folder = new File(path);
            new File("C:\\CampusTourFiles\\ZipFiles\\" + univ).mkdir();

            if (!folder.exists()) {
                folder.mkdir();
                String path0 = path + "\\" + univ;
                folder = new File(path0);
                folder.mkdir();
                String path3 = path0 + "\\" + "Images";
                String path1 = path0 + "\\" + "Audio";
                String path2 = path0 + "\\Video";
                folder = new File(path3);
                folder.mkdir();
                folder = new File(path1);
                folder.mkdir();
                folder = new File(path2);
                folder.mkdir();
                InputStream fp = null;
                byte[] data;
                FileOutputStream fout;
                for (int i = 0; i < universityImage.size(); i++) {
                    fp = universityImage.get(i).getInputStream();
                    data = new byte[fp.available()];
                    fp.read(data);
                    fout = new FileOutputStream(new File(path3 + "\\" + this.univName.replaceAll(" ", "") +i+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + universityImage.get(i).getContentType().split("/")[1]));//+buildingImage.getContentType().split("/")[1]));
                    fout.write(data);
                    fp.close();
                    fout.close();
                }
                for (int i = 0; i < universityAudio.size(); i++) {

                    fp = universityAudio.get(i).getInputStream();
                    data = new byte[fp.available()];
                    fp.read(data);
                    fout = new FileOutputStream(new File(path1 + "\\" + this.univName.replaceAll(" ", "") +i+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + "3gpp"));//+buildingImage.getContentType().split("/")[1]));
                    fout.write(data);
                    fp.close();
                    fout.close();
                }
                for (int i = 0; i < universityVideo.size(); i++) {
                    fp = universityVideo.get(i).getInputStream();
                    data = new byte[fp.available()];
                    fp.read(data);
                    fout = new FileOutputStream(new File(path2 + "\\" + this.univName.replaceAll(" ", "") + i+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + "mp4"));//+buildingImage.getContentType().split("/")[1]));
                    fout.write(data);
                    fp.close();
                    fout.close();

                }

                String OUTPUT_ZIP_FILE = "C:\\CampusTourFiles\\ZipFiles\\" + univ.replace(" ", "") + "\\" + univ.replace(" ", "") + ".zip";
                String SOURCE_FOLDER = "C:\\CampusTourFiles\\RawFiles\\" + univ.replace(" ", "") + "\\" + univ.replace(" ", "");

                AppZip appZip = new AppZip(OUTPUT_ZIP_FILE, SOURCE_FOLDER);

                appZip.generateFileList(
                        new File(SOURCE_FOLDER));
                appZip.zipIt(OUTPUT_ZIP_FILE);

            }
        } else {
            outputMessage = "Email is already assigned to a university please give another email";
            return "";

        }

        return "inserted";
    }

    /**
     * Methods that updates university details
     *
     * @return string
     */
    public String updateUniversity() throws IOException {

        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");
            Statement st = con.createStatement();
            String z = (String) session.getAttribute("editUnivName");
            ResultSet rs = st.executeQuery("select university_id from universities where university_name='" + z + "'");
            rs.next();
            int a = rs.getInt("university_id");

            ResultSet r = st.executeQuery("select count(*) from universities where university_name='" + this.getUnivName() + "' and university_id!='" + a + "'");
            r.next();
            int number = r.getInt(1);
            if (number != 0) {

                outputMessage = "University Already exists";
                return "";

            }
            if (this.univName != session.getAttribute("editUnivName")) {
                new File("C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("editUnivName") + "\\" + session.getAttribute("editUnivName")).renameTo(new File("C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("editUnivName") + "\\" + this.getUnivName()));
                new File("C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("editUnivName")).renameTo(new File("C:\\CampusTourFiles\\RawFiles\\" + this.getUnivName()));
                new File("C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("editUnivName") + "\\" + session.getAttribute("editUnivName") + ".zip").renameTo(new File("C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("editUnivName") + "\\" + this.getUnivName() + ".zip"));
                new File("C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("editUnivName")).renameTo(new File("C:\\CampusTourFiles\\ZipFiles\\" + this.getUnivName()));

            }
            if (!this.getUniversityImage().isEmpty() || !this.getUniversityAudio().isEmpty() || !this.getUniversityVideo().isEmpty()) {
                if (!this.getUniversityImage().isEmpty()) {
                    InputStream fp = null;
                    byte[] data;
                    FileOutputStream fout;
                    for (int i = 0; i < universityImage.size(); i++) {
                        fp = universityImage.get(i).getInputStream();
                        data = new byte[fp.available()];
                        fp.read(data);
                        fout = new FileOutputStream(new File("C:\\CampusTourFiles\\RawFiles" + "\\" + this.univName.replaceAll(" ", "") + "\\" + this.univName.replaceAll(" ", "") + "\\Images\\" + this.univName.replaceAll(" ", "")+i + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + universityImage.get(i).getContentType().split("/")[1]));//+buildingImage.getContentType().split("/")[1]));
                        fout.write(data);
                        fp.close();
                        fout.close();

                    }

                }
                if (!this.getUniversityAudio().isEmpty()) {
                    InputStream fp = null;
                    byte[] data;
                    FileOutputStream fout;
                    for (int i = 0; i < universityAudio.size(); i++) {
                        fp = universityAudio.get(i).getInputStream();
                        data = new byte[fp.available()];
                        fp.read(data);
                        fout = new FileOutputStream(new File("C:\\CampusTourFiles\\RawFiles" + "\\" + this.univName.replaceAll(" ", "") + "\\" + this.univName.replaceAll(" ", "") + "\\Audio\\" + this.univName.replaceAll(" ", "")+i + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + universityAudio.get(i).getContentType().split("/")[1]));//+buildingImage.getContentType().split("/")[1]));
                        fout.write(data);
                        fp.close();
                        fout.close();

                    }

                }
                if (!this.getUniversityVideo().isEmpty()) {
                    InputStream fp = null;
                    byte[] data;
                    FileOutputStream fout;
                    for (int i = 0; i < universityVideo.size(); i++) {
                        fp = universityVideo.get(i).getInputStream();
                        data = new byte[fp.available()];
                        fp.read(data);
                        fout = new FileOutputStream(new File("C:\\CampusTourFiles\\RawFiles" + "\\" + this.univName.replaceAll(" ", "") + "\\" + this.univName.replaceAll(" ", "") + "\\Video\\" + this.univName.replaceAll(" ", "")+i + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + universityVideo.get(i).getContentType().split("/")[1]));//+buildingImage.getContentType().split("/")[1]));
                        fout.write(data);
                        fp.close();
                        fout.close();

                    }

                }
                deleteDir(new File("C:\\CampusTourFiles\\ZipFiles" + "\\" + this.univName.replaceAll(" ", "") + "\\" + this.univName.replaceAll(" ", "") + ".zip"));
                String OUTPUT_ZIP_FILE = "C:\\CampusTourFiles\\ZipFiles\\" + this.univName.replace(" ", "") + "\\" + this.univName.replace(" ", "") + ".zip";
                String SOURCE_FOLDER = "C:\\CampusTourFiles\\RawFiles\\" + this.univName.replace(" ", "") + "\\" + this.univName.replace(" ", "");

                AppZip appZip = new AppZip(OUTPUT_ZIP_FILE, SOURCE_FOLDER);

                appZip.generateFileList(
                        new File(SOURCE_FOLDER));
                appZip.zipIt(OUTPUT_ZIP_FILE);
            }
            st.executeUpdate("update universities set university_name = '" + this.getUnivName() + "',university_coordinates = '" + this.getUniversityCoordinates() + "' where university_id='" + a + "'");

        } catch (ClassNotFoundException | SQLException e) {

        }
        return "updated";
    }

    /**
     * Method to set university name to session
     *
     * @param university object of the university
     * @return string
     */
    public String getUniversityDetails(University university) {
        session.setAttribute("editUnivName", university.univName);
        session.setAttribute("editEmail", university.user);
        tempUniversity = university;
        this.setUnivName(tempUniversity.univName);
        this.setUser(tempUniversity.user);
        this.setUniversityCoordinates(tempUniversity.getUniversityCoordinates());

        System.out.println(tempUniversity);
        return "edit";
    }

    /**
     * This method is used to delete university details from database
     *
     * @param universityName name of the university
     * @return string
     */
    public String deleteUniversityDetails(String universityName) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select email from universities where university_name = '" + universityName + "'");
            rs.next();
            String email = rs.getString("email");
            String path = "C:\\CampusTourFiles\\RawFiles";
            String path0 = "C:\\CampusTourFiles\\ZipFiles";
            String univ = this.getUnivName();

            Boolean a = true;
//            for(int i=0;i<this.getUnivName().length();i++){
//                if(this.getUnivName().charAt(i)==' '){
//                    a=true;
//                } else {
//                    a=false;
//                }
//            }
//            if(a){
//                univ = (this.getUnivName()).replace(" ","");
//            }else{
//                univ = this.getUnivName();
//            }

            path = path + "\\" + univ;
            File folder = new File(path);
            File folder1 = new File(path0);
            if (folder.exists()) {
                deleteDir(folder);
                deleteDir(folder1);
                
            }
            st.executeUpdate("delete from user where email = '" + email + "'");
            st.executeUpdate("delete from universities where email = '" + email + "'");

        } catch (ClassNotFoundException | SQLException e) {

        }
        return "delete";
    }

    /**
     * Returns the list of university
     *
     * @return list of university
     */
    public List<University> universityList() {
        universityList.clear();
        try {

            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select university_name,university_coordinates,email from universities");
                while (rs.next()) {
                    University un = new University();
                    un.setUnivName(rs.getString("university_name"));
                    un.setUniversityCoordinates(rs.getString("university_coordinates"));
                    un.setUser(rs.getString("email"));
                    universityList.add(un);

                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            //con.close();
        } finally {

        }
        return universityList;
    }

    /**
     * Delete the file
     *
     * @param file
     * @return boolean whether file is deleted or not
     */
    public boolean deleteDir(File file) {
        File[] contents = file.listFiles();

        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        return file.delete();
    }

}
