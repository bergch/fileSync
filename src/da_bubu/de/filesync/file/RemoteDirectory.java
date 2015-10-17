package da_bubu.de.filesync.file;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class RemoteDirectory extends RemoteEntry {

	List<RemoteEntry> content = new ArrayList<>();

	public RemoteDirectory(String path) {
		dir = path;
	}

	public RemoteDirectory(LsEntry entry, RemoteDirectory parent) {
		super(entry, parent);
		dir = entry.getFilename();
	}

	public void add(List<RemoteEntry> dirContent) {
		content.addAll(dirContent);

	}

	public void add(RemoteEntry dirContent) {
		content.add(dirContent);

	}

	public List<RemoteEntry> getContent() {
		return content;
	}

    @Override
    public Timestamp getLastModificationDate() {
        return null;
    }

}
