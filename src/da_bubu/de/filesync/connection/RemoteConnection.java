package da_bubu.de.filesync.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import da_bubu.de.filesync.Main;
import da_bubu.de.filesync.file.LocalDirectory;
import da_bubu.de.filesync.file.LocalEntry;
import da_bubu.de.filesync.file.LocalFile;
import da_bubu.de.filesync.file.RemoteDirectory;
import da_bubu.de.filesync.file.RemoteEntry;
import da_bubu.de.filesync.file.RemoteFile;

public class RemoteConnection {

    private static Session session = null;

    private static Channel channel = null;

    private static ChannelSftp channelSftp = null;

    private static RemoteConnection instance = null;

    String SFTPHOST = "da-bubu.de";

    int SFTPPORT = 22;

    String SFTPUSER = "bubu";

    private RemoteConnection() throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(Main.privateKey.getAbsolutePath() );
        Main.log("get connection to: " + SFTPUSER + "@" + SFTPHOST + ":" + SFTPPORT);
        try {
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static RemoteConnection getInstance() {
        if (instance == null) {
            try {
                instance = new RemoteConnection();
            } catch (JSchException e) {
                throw new IllegalArgumentException("private key not found", e);
            }
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public List<RemoteEntry> getDirectory(RemoteDirectory parent) throws SftpException {
        Main.log("get remote infos for:" + parent.getFullPath());
        Vector<ChannelSftp.LsEntry> ls = channelSftp.ls(parent.getFullPath());

        for (ChannelSftp.LsEntry lsEntry : ls) {
            if (!RemoteEntry.isParentOrSelfDir(lsEntry)) {
                if (RemoteEntry.isDir(lsEntry)) {
                    RemoteDirectory dir = new RemoteDirectory(lsEntry, parent);
                    List<RemoteEntry> dirContent = getDirectory(dir);
                    dir.add(dirContent);
                    parent.add(dir);
                } else {
                    RemoteFile file = new RemoteFile(lsEntry, parent);
                    parent.add(file);
                }
            }

        }
        return new ArrayList<RemoteEntry>();

    }

    public void upload(List<LocalEntry> entriesToBeUploaded) throws FileNotFoundException, SftpException {

        for (LocalEntry localEntry : entriesToBeUploaded) {
            
            if (localEntry.isDirectory()) {
                channelSftp.mkdir(localEntry.getRemoteFileName());
                upload(((LocalDirectory) localEntry).getContent());
            } else {
                Main.log("upload: start:" + localEntry.getFullPath());
                channelSftp.put(new FileInputStream(((LocalFile) localEntry).getFile()), localEntry.getRemoteFileName()
                        + ".filepart");
                channelSftp.rename(localEntry.getRemoteFileName() + ".filepart", localEntry.getRemoteFileName());
                Main.log("upload: finished:" + localEntry.getFullPath());
            }
        }

    }

    public void shutdown() {
        channelSftp.disconnect();
        session.disconnect();
        channelSftp = null;
        session = null;
        instance = null;
    }
}
