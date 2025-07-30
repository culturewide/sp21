package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import static gitlet.Utils.*;  /** 这个地方还不理解*/
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author culturewide
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** commit的日期 */
    private Date date;
    /** 创建的新的commit的唯一ID*/
    private String uniqueID;
    /** The message of this Commit. */
    private String message;
    /** reference to blbo*/
    private List<BOLB> bolbs;

    private List<String> parentID;
    private List<String> bolbFileName;
    private List<String> bolbID;
    /* TODO: fill in the rest of this class. */
    /** initialize the Commit */

    public Commit(String message, Date date, List<String> parentID){
        this.message = message;
        this.parentID = parentID;
        this.date = date;
        bolbFileName = new ArrayList<String>();
        bolbID = new ArrayList<>();
        bolbs = new ArrayList<>();
        copyFromParent(this);
    }
    /** 单独创建一个commit用于初始化*/
    public Commit(String message,Date date){
        this.message = message;
        this.date = date;
        this.bolbFileName = new ArrayList<>();
        this.bolbID = new ArrayList<>();
        this.parentID = new ArrayList<>();
        this.uniqueID =  sha1(serialize(this));
        this.bolbs = new ArrayList<>();
    }
    /** 将parent的bolb数据搬运过来
     * */
    private void copyFromParent(Commit commit){
        if(this.parentID.size() == 1){
             Commit parent = readObject(join(COMMITS_DIR,this.parentID.get(0)),Commit.class);
             this.bolbFileName.addAll(parent.bolbFileName);
             this.bolbID.addAll(parent.bolbID);
             this.bolbs.addAll(parent.bolbs);
        }
    }

    /**
     * 对新commit的追踪对象进行更新
     * 清空removestage文件夹中的文件
     * */
    public void update() {
        List<String> list = Utils.plainFilenamesIn(Repository.ADDSTAGES_DIR);

        for(String filename : list){
            File file = join(ADDSTAGES_DIR,filename);
            String content = readContentsAsString(file);
            String id = sha1(content,filename);
            BOLB bolb = new BOLB(content,id,filename);
            /**情况1 如果追踪的文件更新了，就去BLOB文件夹寻找最新文件
             * 删除原来的对应bolbID，添加上新的id，更换bolb
             * 情况2 不在追踪的范围内 添加上blob id以及filename，添加bolb
             * 情况3 remove ,先不做
             * 怎么寻找已有的BLOBID呢，只知道文件名，应该是找不到的，因为
             * */
            int indexNum = -1;
            if(this.bolbFileName.contains(bolb.name) && !this.bolbID.contains(bolb.BOLBid)){
                for(int i = 0 ;i<this.bolbFileName.size();i++){
                    if(bolbFileName.get(i).equals(bolb.name)){
                        indexNum = i;
                    }
                }
                if(indexNum != -1){
                    bolbID.set(indexNum,bolb.getBOLBid());
                    bolbs.set(indexNum,bolb);
                }
                /** 这个方法不好，因为他在遍历的过程中改变了队列，会造成奇怪的后果*/
//                for(BOLB b : bolbs){
//                    if(b.name.equals(bolb.name)){
//                        bolbID.remove(b.BOLBid);
//                        bolbID.add(bolb.BOLBid);
//                        bolbs.remove(b);
//                        bolbs.add(bolb);
//                    }
//                }
            }else if(!this.bolbFileName.contains(filename) ){
                this.bolbID.add(bolb.BOLBid);
                this.bolbFileName.add(filename);
                this.bolbs.add(bolb);
            }
            file.delete();
        }
        /**删除部分文件*/
        List<String> deleteFileList =plainFilenamesIn(REMOVESTAGES_DIR);
        for(String deleteFile : deleteFileList){
            for(BOLB bolb : bolbs){
                if(bolb.name.equals(deleteFile)){
                    bolbID.remove(bolb.BOLBid);
                    bolbs.remove(bolb);
                    bolbFileName.remove(deleteFile);
                    File deletPath = join(REMOVESTAGES_DIR,deleteFile);
                    deletPath.delete();
                }
            }
        }
        //更新当前branch
        this.uniqueID  = sha1(serialize(this));
        File headPath = join(HEADS_DIR,"head");
        String branchFilePathString = readContentsAsString(headPath);
//        File branchFile = new File(branchFilePathString);
        File branchFile = join(BRANCHES_DIR,branchFilePathString);
        writeContents(branchFile,this.getCommitID());
        File commitPath = join(COMMITS_DIR,this.uniqueID);
        writeObject(commitPath,this);

    }
    public String getCommitID() {
        return uniqueID;
    }
    public List<BOLB> getBolbs(){
        return bolbs;

    }
    public List<String> getBolbFileName(){
        return bolbFileName;
    }
    public Date getDate(){
        return date;
    }
    public String getMessage(){
        return message;
    }
    public List<String> getParentID(){
        return parentID;
    }

    /**清除stage area 未完成*/
    //todo

}
