package da_bubu.de.filesync;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import com.jcraft.jsch.SftpException;
import da_bubu.de.filesync.connection.LocalConnection;
import da_bubu.de.filesync.connection.RemoteConnection;
import da_bubu.de.filesync.file.LocalDirectory;
import da_bubu.de.filesync.file.LocalEntry;
import da_bubu.de.filesync.file.RemoteDirectory;
import da_bubu.de.filesync.file.RemoteEntry;
import da_bubu.de.filesync.file.RemoteFile;

public class Main {

    public static String localDir = null;

    public static String remoteDir = null;

    public static File privateKey = null;

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        options.addOption(CommandLineArguments.localPath, true, "local path");
        options.addOption(CommandLineArguments.pricateKeyPath, true, "path to private key");
        options.addOption(CommandLineArguments.removePath, true, "remote path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        localDir = cmd.getOptionValue(CommandLineArguments.localPath);
        privateKey = new File(cmd.getOptionValue(CommandLineArguments.pricateKeyPath));
        remoteDir = cmd.getOptionValue(CommandLineArguments.removePath);

        Main main = new Main();

        LocalDirectory local = main.listLocalFiles(localDir);
        RemoteDirectory remote = main.listRemoteFiles(remoteDir);

        List<LocalEntry> entriesToBeUploaded = new ArrayList<LocalEntry>();

        log("start diff remote and local");
        diff(local, remote, entriesToBeUploaded);

        if (entriesToBeUploaded.size() == 0) {
            log("nothing to do");
        } else {
            logDiff(entriesToBeUploaded);

            RemoteConnection.getInstance().upload(entriesToBeUploaded);
        }
        RemoteConnection.getInstance().shutdown();
    }

    private static void logDiff(List<LocalEntry> entriesToBeUploaded) {
        for (LocalEntry localEntry : entriesToBeUploaded) {
            log("to be uploaded:" + localEntry.getFullPath());
            if (localEntry.isDirectory()) {
                logDiff(((LocalDirectory) localEntry).getContent());
            }
        }

    }

    private static void diff(LocalDirectory local, RemoteEntry remote, List<LocalEntry> entriesToBeUploaded) {
        for (LocalEntry entry : local.getContent()) {
            log("diff:" + entry.getFileName());

            if (!isRemote(entry, ((RemoteDirectory) remote).getContent())) {
                log("add to be uploaded:" + entry.getFileName());
                entriesToBeUploaded.add(entry);
            } else {
//                log("local :" + entry.getFullPath() + "(" + ((entry != null) ? entry.isDirectory() : "") + ", "
//                        + ((entry != null) ? entry.getClass() : "") + ")");
//                log("remote:" + remote.getFullPath() + "(" + ((remote != null) ? remote.isDirectory() : "") + ", "
//                        + ((remote != null) ? remote.getClass() : "") + ")");
                if (entry.isDirectory()) {
                    diff((LocalDirectory) entry, (RemoteDirectory) getRemote(entry, ((RemoteDirectory) remote).getContent()), entriesToBeUploaded);

                } else {
//                    log("updateing remote file:" + entry.getFullPath() + ", remote:" + remote.getFullPath());
                    boolean found = false;
                    for (RemoteEntry remoteEntry : ((RemoteDirectory) remote).getContent()) {
                        if (remoteEntry.getFileName().equals(entry.getFileName())){
//                                && entry.getLastModificationDate().compareTo(remoteEntry.getLastModificationDate()) <= 0) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        entriesToBeUploaded.add(entry);
                    }

                }
            }
        }
    }

    private static boolean isRemote(LocalEntry entry, List<RemoteEntry> content) {
        return getRemote(entry, content) != null;
    }

    private static RemoteEntry getRemote(LocalEntry localEntry, List<RemoteEntry> content) {
        for (RemoteEntry remoteEntry : content) {
            if (localEntry.getFileName().equals(remoteEntry.getFileName())) {
                return remoteEntry;
            }
        }
        return null;
    }

    public RemoteDirectory listRemoteFiles(String dir) throws SftpException {
        RemoteDirectory parentDir = new RemoteDirectory(dir);
        RemoteConnection.getInstance().getDirectory(parentDir);

        return parentDir;
    }

    public LocalDirectory listLocalFiles(String dir) throws SftpException {
        LocalDirectory parentDir = new LocalDirectory(dir);
        LocalConnection.getInstance().getDirectory(parentDir);

        return parentDir;
    }

    public static void log(String msg) {
        Calendar cal = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat();
        System.out.println(formatter.format(cal.getTime()) + ":" + msg);
    }
}
