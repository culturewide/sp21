package capers;

import java.io.File;
import java.io.IOException;

import static capers.Dog.DOG_FOLDER;
import static capers.Utils.*;

/** A repository for Capers
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD,".capers"); // TODO Hint: look at the `join`
                                            //      function in Utils
    static final File STORY_FILE = Utils.join(CAPERS_FOLDER,"story");
    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        if (!CAPERS_FOLDER.exists()){
            CAPERS_FOLDER.mkdir();
        }
        if (!DOG_FOLDER.exists()){
            DOG_FOLDER.mkdir();
        }
        if (!STORY_FILE.exists()){
            try {
                STORY_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // TODO
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        String oldStory = "";

        // 1. 检查故事文件是否已存在，如果存在，就先读取它的内容
        if (STORY_FILE.exists()) {
            // 注意：故事文件是普通文本，应该用 readContentsAsString，而不是 readObject
            oldStory = Utils.readContentsAsString(STORY_FILE);
        }

        // 2. 将旧故事和新传入的文本拼接起来
        String newStory = oldStory + text + "\n";

        // 3. 将拼接好的完整故事写回文件（覆盖）
        Utils.writeContents(STORY_FILE, newStory);

        // 4. 打印出完整的故事
        System.out.println(newStory);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
       Dog dog = Dog.fromFile(name);
       dog.haveBirthday();
       dog.saveDog();
        // TODO
    }

}
