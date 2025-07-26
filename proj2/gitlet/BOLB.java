package gitlet;
import static gitlet.Utils.*;
import java.io.Serializable;

public class BOLB implements Serializable {
    public String fileContent;
    /** the id of this bolb */
    public String BOLBid;
    /** the name of file*/
    public String name;
    /** the id of the file(include name content)
     *  to distinguish between files
     */
    public String fileID;
    public BOLB(String fileContent, String fileID, String name) {
        this.fileContent = fileContent;
        this.fileID = fileID;
        this.name = name;
        this.BOLBid = getBOLBid();
    }

    public String getBOLBid(){
        return sha1(fileID,name,fileContent);
    }
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BOLB)) return false;
        BOLB bolb = (BOLB) obj;
        return BOLBid .equals(bolb.BOLBid) ;
    }
}
