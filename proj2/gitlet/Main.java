package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return; // 直接退出程序
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.initialize();
                // TODO: handle the `init` command
                break;
            case "add":
                Repository.add(args[1]);
                // TODO: handle the `add [filename]` command
                break;
            case "commit":
                Repository.AddCommit(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "rm":
                Repository.remove(args[1]);
                break;
            case "merge":
                break;
            case "find":
                Repository.find(args[1]);
                break;
            case "status":
                Repository.status();
                break;
            case "checkout":
                if(args.length == 2){
                    String branchName = args[1];
                    Repository.checkoutBranch(branchName);
                }else if(args.length == 3 && args[1].equals("--")){
                    String fileName = args[2];
                    Repository.checkoutFile(fileName);
                }else if(args.length == 4 && args[2].equals("--")){
                    String commitID = args[1];
                    String FileName = args[3];
                    Repository.checkoutFileWithID(commitID,FileName);
                }else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.removeBranch(args[1]);
                break;
            // TODO: FILL THE REST IN

            case "reset":
                Repository.reset(args[1]);
                break;
            case "global-log":
                Repository.globallog();
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
