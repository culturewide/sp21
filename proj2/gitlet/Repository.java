package gitlet;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;

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
    public static final File HEADS_DIR = join(GITLET_DIR, "heads");
    public static final File STAGES_DIR = join(GITLET_DIR, "stages");
    public static final File ADDSTAGES_DIR = join(STAGES_DIR, "addstages");
    public static final File REMOVESTAGES_DIR = join(STAGES_DIR, "removestages");
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
            }
            File newBranches = new File(BRANCHES_DIR, "master");
            if(!newBranches.exists()) {
                try{
                    newBranches.createNewFile();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
            File Commits = new File(COMMITS_DIR, "initial commit");
            if(!Commits.exists()) {
                try{
                    Commits.createNewFile();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
            // TODO
            Commit commit = new Commit(" 00:00:00 UTC, Thursday, 1 January 1970"," ","initial commit");

            File heads = new File(HEADS_DIR, " head");
            if(!heads.exists()) {
                try{
                    heads.createNewFile();
                } catch (IOException e){
                    throw new RuntimeException(e);
                }
            }


        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }


    }
    public void add(String name){
        
    }
//    public static void main(String[] args) {
//
//    }
    /* TODO: fill in the rest of this class. */
}
