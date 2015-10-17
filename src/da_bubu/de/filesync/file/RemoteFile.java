package da_bubu.de.filesync.file;

import java.sql.Timestamp;
import com.jcraft.jsch.ChannelSftp.LsEntry;


public class RemoteFile extends RemoteEntry {
	
    public RemoteFile(LsEntry entry, RemoteDirectory parent) {
        super(entry, parent);
        dir =  entry.getFilename();
    }

    @Override
    public Timestamp getLastModificationDate() {
        int mTime = getEntry().getAttrs().getMTime();
        String mtimeString = getEntry().getAttrs().getMtimeString();
        
        return null;
    }

    
}
