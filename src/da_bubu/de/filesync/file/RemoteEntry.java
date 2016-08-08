package da_bubu.de.filesync.file;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public abstract class RemoteEntry implements Entry {

	private ChannelSftp.LsEntry entry;

	private RemoteDirectory parent;

	protected String dir;

	public RemoteEntry() {

	}

	public RemoteEntry(ChannelSftp.LsEntry entry, RemoteDirectory parent) {
		this.entry = entry;
		this.parent = parent;
	}

	public static boolean isParentOrSelfDir(LsEntry lsEntry) {
		return lsEntry.getFilename().equals(".")
				|| lsEntry.getFilename().equals("..");
	}

	@Override
	public String getFileName() {
		return entry.getFilename();
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	@Override
	public String toString() {
		return getFullPath();
	}

	@Override
	public String getFullPath() {
		return follow(parent) + "/" + dir;
	}

	private String follow(RemoteDirectory element) {
		if (element != null) {
			return follow(element.getParent()) + "/" + element.getDir();
		} else {
			return "";
		}
	}

	public RemoteDirectory getParent() {
		return parent;
	}

	@Override
	public boolean isFile() {
		return !isDirectory();
	}

	@Override
	public boolean isDirectory() {
		return getParent() == null ||  entry.getLongname().substring(0, 1).toLowerCase().equals("d");
	}

	public static boolean isDir(LsEntry lsEntry) {
		return lsEntry.getLongname().substring(0, 1).toLowerCase().equals("d");
	}
	
	
    public ChannelSftp.LsEntry getEntry() {
        return entry;
    }
}
