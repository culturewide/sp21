package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** THE BLOBS directory */
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
   /** the place where current branches are */
    public static final File HEADS_DIR = join(GITLET_DIR, "heads");
    public static final File STAGES_DIR = join(GITLET_DIR, "stages");
    public static final File ADDSTAGES_DIR = join(STAGES_DIR, "addstages");
    public static final File REMOVESTAGES_DIR = join(STAGES_DIR, "removestages");
    public static File head;
    /** initialize the .gitlet directory.*/
    public static void initialize() {
        if(!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            if(!BLOBS_DIR.exists()) {
                BLOBS_DIR.mkdir();
            }
            if(!COMMITS_DIR.exists()) {
                COMMITS_DIR.mkdir();
            }
            if(!BRANCHES_DIR.exists()) {
                BRANCHES_DIR.mkdir();
            }
            if(!HEADS_DIR.exists()) {
                HEADS_DIR.mkdir();
            }
            if(!STAGES_DIR.exists()) {
                STAGES_DIR.mkdir();
                ADDSTAGES_DIR.mkdir();
                REMOVESTAGES_DIR.mkdir();
            }
            /** 将commit 初始化，写入commit_File*/

            Commit commit = new Commit("initial commit"," 00:00:00 UTC, Thursday, 1 January 1970");
            File commit_path = join(COMMITS_DIR,commit.getCommitID());
            writeObject(commit_path, commit);
            /** create the master branch
             * 先创建branch，head是指向活跃的branch
             * */
            File newBranches_master = new File(BRANCHES_DIR, "master");
            writeContents(newBranches_master,commit.getCommitID());
            head = new File(HEADS_DIR, "head");
//            writeObject(head,newBranches_master.getPath());
            writeContents(head,"master");
        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }


    }
    /** the method to add it to the addstage
     * 1.如果将要add的file与current commit一致，则不add他，若已经再add区域，移除
     * 如果add的文件 在removefile，把他拿出来
     *
     *
     * */
    public static void add(String name){
        boolean sameInthecommits = false;
        if(!GITLET_DIR.exists()){
            System.out.println("please init first");
            return;
        }
        List<String> list = Utils.plainFilenamesIn(CWD);
        File file = join(CWD, name);
        if(!file.exists()) {

            System.out.println("File does not exist.");
            return;
        }
        String content =  readContentsAsString(file);
        //计算该文档的ID（包括内容和title）
        String id = sha1(content,name); //get sha1 id
        //按照文件内容，文件id，文件名字 来创建新bolb，他的bolbid自己创建
        BOLB newBolb = new BOLB(content,id,name);
        if(!getCurrentCommit().getBolbs().isEmpty()){
            for(BOLB b : getCurrentCommit().getBolbs())  {
                if(newBolb.equals(b)) {
                    //todo 删文档
                    sameInthecommits = true;
                    File dltFile = join(ADDSTAGES_DIR,name);
                    dltFile.delete();

                }
            }
        }
        if(!sameInthecommits){
            addHelper(name,newBolb);
        }
        checkSameInRemove(name);
        return;

    }
    public static void checkSameInRemove(String name){
       List <String> checkFile = plainFilenamesIn(REMOVESTAGES_DIR);
       for(String f : checkFile) {
           if(name.equals(f)) {
               File dltFile = join(REMOVESTAGES_DIR,name);
               dltFile.delete();
           }
       }
    }
    /**  创建一个暂时的blob来存贮需要add的内容*/
    private static void addHelper(String name, BOLB newBolb) {
            File file = join(CWD, name);
            saveContentToAdd(newBolb);

    }
    private static void saveContentToAdd(BOLB bolb){
        File file = join(ADDSTAGES_DIR, bolb.name);
        writeContents(file, bolb.fileContent);
    }
    /** save the bolb in the BOLB dir.
     * debug 不能以name来命名，只能根据唯一的id来命名
     * */
    private static void saveBolb(BOLB bolb){
        File file = join(BLOBS_DIR, bolb.BOLBid);
        writeContents(file, bolb.fileContent);
    }

    /** the method to commit */
    /** 首先要new一个commit，把他的父母亲对BLOB的引用都拿过来*/
    public static void AddCommit(String message){
        if(!GITLET_DIR.exists()){
            System.out.println("please init first");
            return;
        }
        if(isEmptyCommit()){
            System.out.println("No changes added to the commit.");
            return;
        }
        List<String> parentid = getFirstParentID();
        String date = new Date().toString();
        Commit commit = new Commit(message,date,parentid);
        commit.update();
        changeHeadCommit(commit);
    }
    private static void changeHeadCommit(Commit commit){
        File headPath = join(HEADS_DIR,"head");
        String curBranchPath = readContentsAsString(headPath);
        File curBranch = join(BRANCHES_DIR,curBranchPath);
        writeContents(curBranch,commit.getCommitID());
    }
    /** 判断add stage是否为空*/
    public static boolean isEmptyCommit(){
        List<String> st = plainFilenamesIn(ADDSTAGES_DIR);
        List<String> st1 = plainFilenamesIn(REMOVESTAGES_DIR);
        if(st.isEmpty()) {
            if(st1.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    /** 获取父母的ID，应该先从头指针入手，再通过头指针代表着id，反序列化出需要的commit
     * 然后再通过getCommitID来获取答案 这样可以保护private性质
     * @return
     */
    public  static List<String> getFirstParentID(){
        Commit currentCommit = getCurrentCommit();
        List<String> parentid = new ArrayList<String>();
        parentid.add(currentCommit.getCommitID());
        return parentid;
    }
    public static Commit getThisFirstParent( Commit commit){
        if(commit.getParentID().isEmpty()) return null;
        String FirstParentId = commit.getParentID().get(0);
        File FirstParentPath = join(COMMITS_DIR,FirstParentId);
        Commit ParentCommit = readObject(FirstParentPath, Commit.class);
        return  ParentCommit;

    }

    /** head为静态变量，当程序运行完成后，静态变量清空，保持默认值
     * 为了调用正确的head地址，应该从保存的file中读取
     *
     * @return
     */
    public static Commit getCurrentCommit(){
        //heads contains the necessary content of current commit;
        //heads是静态的
        File headFile = join(HEADS_DIR,"head");
        String branchFilePathString = readContentsAsString(headFile);
//        File branchFile = new File(branchFilePathString);
        /** 将当前branch的路径表示出来*/
        File branchFile = join(BRANCHES_DIR,branchFilePathString);
        /**获取branch的内容，也就是commitID*/
        String commitIDString = readContentsAsString(branchFile);
        /**从commit文件夹中读取对应的commit类*/
        Commit currentCommit = readObject(join(COMMITS_DIR,commitIDString),Commit.class);
        return currentCommit;
    }
//    public static void main(String[] args) {
//
//    }
    //todo 删除掉add区的内容（这是commit part）
    /** 1.如果在addStage内，吧他删除掉
     * 2.如果正在被commit追踪，吧他放在 removal file里， 并把它从工作目录中移除（如果用户还没做的话
     * ）*/
    public static void remove(String name){
        if(!GITLET_DIR.exists()){
            System.out.println("please init first");
            return;
        }
        /** 情况1*/
        List<String> tempForAddFile = plainFilenamesIn(ADDSTAGES_DIR);
        Commit currentCommit = getCurrentCommit();
        if(! tempForAddFile.isEmpty()){
            for(String s : tempForAddFile) {
                if(s.equals(name)) {
                    File file = join(ADDSTAGES_DIR, s);
                    file.delete();
                }
            }  /** 情况2*/
        }else if(currentCommit.getBolbFileName().contains(name)) {
            BOLB targetBolb = findBolbByName(currentCommit,name);
            String content = targetBolb.fileContent;
            stageforDel(name,content);
            checkWorkDir(name);
        }else{
            System.out.println("No reason to remove the file.");
        }

    }

    /** 只知道名字，不知道对应的Blbo 用此方法*/
    public static BOLB  findBolbByName(Commit commit, String name){
        List<BOLB> list = commit.getBolbs();
        for(BOLB b : list) {
            if(b.name.equals(name)) {
                return b;
            }
        }
        return null;
    }
    /** 判断工作目录中有无需要删除的文件
     *  如果有，就删除*/
    public static void stageforDel(String name,String content){
        File file = join(REMOVESTAGES_DIR, name);
        writeContents(file,content);
    }
    public static void checkWorkDir(String name){
        File file = join(CWD, name);
        if(file.exists()) {
            file.delete();
        }
    }
    /**   the method
     * 从头指针开始，一直打印信息到起始点
     * 内容包括：：commit uniqueID
     *            date
     *            message
     *   修改日志 : 不可以println一个\n，这样会产生两个空行*/
    public static void log(){
        if(!GITLET_DIR.exists()){
            System.out.println("please init first");
            return;
        }
        Commit curtCommit = getCurrentCommit();
        while(curtCommit != null) {
            System.out.println("===");
            System.out.println("commit " +curtCommit.getCommitID());
            System.out.println("Date: " +curtCommit.getDate());
            System.out.println(curtCommit.getMessage());
            curtCommit = getThisFirstParent(curtCommit);
            System.out.println();


        }

    }
    /**打印出和信息相对应的commit的ID。如果有多个，分行打印
     * 方法：吧目前的commit全部取出来遍历，分别比较他们的message，如果相等就打印*/
    public static void find(String commitMessage){
       List<String>commitList =  plainFilenamesIn(COMMITS_DIR);
       boolean found = false;
       for(String s : commitList) {
           File commitPath = join(COMMITS_DIR,s);
           Commit commit = readObject(commitPath, Commit.class);
           if(commit.getMessage().equals(commitMessage)) {
               System.out.println(commit.getCommitID());
               found = true;
           }
       }
       if(!found){
           System.out.println("Found no commit with that message.");
       }
    }
    /** 显示所有的branch，并用*标记活跃branch
     * 显示所有正在AddStage ，delStage区域的文件
     * */
    public static void status(){
        printAllBranch();
        printAddStage();
        printRemoveStage();
        printElse();
        //todo Modifications Not Staged For Commit
    }
    public static void printElse(){
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
    public static void printAddStage(){
        List<String> addedFile = plainFilenamesIn(ADDSTAGES_DIR);
        System.out.println("=== Staged Files ===");
        for(String s : addedFile) {
            System.out.println(s);
        }
        System.out.println();
    }
    public static void printRemoveStage(){
        List<String> removedFile = plainFilenamesIn(REMOVESTAGES_DIR);
        System.out.println("=== Removed Files ===");
        for(String s : removedFile) {
            System.out.println(s);
        }
        System.out.println();
    }
    public static void printAllBranch(){
        List <String> branchName = plainFilenamesIn(BRANCHES_DIR);
        File headPath = join(HEADS_DIR,"head");
        String curBranch = readContentsAsString(headPath);
        System.out.println("=== Branches ===");
        for(String s : branchName) {
            if(s.equals(curBranch)) {
                System.out.println("*"+s);
            }else{
                System.out.println(s);
            }

        }
        System.out.println();
    }
    /**吧current branch调整到给定branch，如果两者不一致
     * 吧给定branch的头commit中的所有BOLB都取出来覆盖CWD中的文件
     * 吧head调整到这个branch
     * 改变追踪后，不被追踪的file全部删除*/
    public static void checkoutBranch(String branchName){
        if(checkIfBranchSame(branchName)){
            return;
        }else if(!checkIfNoneExist(branchName)){
            return;
        }else{
            /**首先获取该branch的所有BLOB
             * 2.获取Bolb的文件，在CWD覆写
             * 3.找到那些之前被跟踪但是现在不被跟踪的文件，全从CWD删除*/
            File givenBranchPath = join(BRANCHES_DIR, branchName);
            Commit newcommit = getCommitByID(readContentsAsString(givenBranchPath));
            List<String> FileTracked = new ArrayList<>();
            for(BOLB b : newcommit.getBolbs()) {
                recoverFilePlus(b);
                FileTracked.add(b.name);
            }
            deleteFilenotTrackedNow(FileTracked);
            clearStage();
            changeHead(branchName);
            //todo 完善branch
        }
    }
    /**让head指针指向给定branch*/
    public static void changeHead(String branchName){
        File headpath = join(HEADS_DIR, "head");
        writeContents(headpath, branchName);
    }
    public static void clearStage(){
        List<String> list = plainFilenamesIn(ADDSTAGES_DIR);
        for(String s : list) {
            File path = join(ADDSTAGES_DIR, s);
            path.delete();
        }
        List<String> removedFile = plainFilenamesIn(REMOVESTAGES_DIR);
        for(String s : removedFile) {
            File path = join(REMOVESTAGES_DIR, s);
            path.delete();
        }
    }
    /**删除掉之前被追踪，现在不被追踪的文档*/
    public static void deleteFilenotTrackedNow(List<String> FileTracked){
        Commit commit = getCurrentCommit();
        for(BOLB b : commit.getBolbs()) {
            if(!FileTracked.contains(b.name)  ) {
                File file = join(CWD, b.name);
                if(file.exists()) {
                    file.delete();
                }
            }
        }
    }
    //todo 做一些修改
    public static void recoverFilePlus(BOLB b){
        File toRecoverFile = join(CWD,b.name);
        if(toRecoverFile.exists()) {
            if(checkUntracked(b.name)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            }
        }

    }
    /** a method to find if the file is tracked in head commit*/
    public static boolean checkUntracked(String name){
        Commit curCommit = getCurrentCommit();
        for(BOLB b : curCommit.getBolbs()) {
            if(b.name.equals(name)) {
                return false;
            }
        }
        return true;
    }
    public static Commit getCommitByID(String commitID){
        File commitPath = join(COMMITS_DIR, commitID);
        return readObject(commitPath, Commit.class);
    }
    public static boolean checkIfNoneExist(String branchName){
        File branchPath = join(BRANCHES_DIR, branchName);
        return branchPath.exists();
    }
    public static boolean checkIfBranchSame(String branchName){
        File head = join(HEADS_DIR,"head");
        String curBranch = readContentsAsString(head);
        if(curBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return true;
        }
        return false;
    }
    /** 将head commit 中追踪的本文件直接恢复到CWD中，如CWD中已存在，就把他覆盖
     * 如果head commit中不包含本文件，打印错误信息*/
    public static void checkoutFile(String fileName){
        Commit currentCommit = getCurrentCommit();
        /**如果不包含这个文件，直接打印错误信息*/
        if(!currentCommit.getBolbFileName().contains(fileName)){
            System.out.println("File does not exist in that commit.");
            return;
        }
        /**获取当前commit的所有blob，遍历，如果和文件名匹配，就吧他取出来恢复*/
        for(BOLB b : currentCommit.getBolbs()) {
            if(b.name.equals(fileName)) {
                recoverFile(b);
                return;
            }
        }
    }
    public static void recoverFile(BOLB b){
        File toRecoverFile = join(CWD,b.name);
        writeContents(toRecoverFile,b.fileContent);
    }
    /**寻找fileID，filename匹配的commit，不局限于head，如果找到了，就把他取出来恢复*/
    public static void checkoutFileWithID(String commitID,String fileName){
        List<String> CommitList = plainFilenamesIn(COMMITS_DIR);
        for(String s : CommitList) {
            if(s.equals(commitID)) {
                File file = join(COMMITS_DIR, s);
                Commit commit = readObject(file, Commit.class);
                if(commit.getBolbFileName().contains(fileName)) {
                    for(BOLB b : commit.getBolbs()) {
                        if(b.name.equals(fileName)) {
                            recoverFile(b);
                        }
                    }
                }else{
                    System.out.println("File does not exist in that commit.");
                }
            }else{
                System.out.println("No commit with that id exists.");
            }
        }
    }
    /**在branch文件夹中创建一个新的branch，并将他指向目前的commitID*/
    public static  void branch(String branchName){
        File newBranch = join(BRANCHES_DIR, branchName);
        /**判断是否已经存在*/
        if(newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
        }
        File headPath = join(HEADS_DIR,"head");
        String curBranch = readContentsAsString(headPath);
        File curBranchPath = join(BRANCHES_DIR,curBranch);
        writeContents(newBranch,readContentsAsString(curBranchPath));


    }
    /**移除目标branch的指针*/
    public static void removeBranch(String branchName){
        File toDelteBranch = join(BRANCHES_DIR, branchName);
        if(!toDelteBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if(getCurrentBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
        }
        writeContents(toDelteBranch," ");
    }
    public static String getCurrentBranch(){
        File headPath = join(HEADS_DIR,"head");
        return readContentsAsString(headPath);
    }
    public static void reset(String commitID){
        File commitFile = join(COMMITS_DIR, commitID);
        if(!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
        }
        Commit commit = getCommitByID(commitID);
        for(String s : commit.getBolbFileName()){
            checkoutFileWithID(commitID,s);
        }
        List<String> workFile = plainFilenamesIn(CWD);
        for(String s : workFile) {
            if(!commit.getBolbFileName().contains(s)) {
                File file = join(CWD,s);
                file.delete();
            }
        }
        changeHeadCommit(commit);
    }
}   /* TODO: fill in the rest of this class. */
