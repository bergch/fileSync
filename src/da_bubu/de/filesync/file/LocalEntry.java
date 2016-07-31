package da_bubu.de.filesync.file;

import java.io.File;

import da_bubu.de.filesync.Main;

public abstract class LocalEntry implements Entry {

	private File file;

	public LocalEntry(String dir) {
		file = new File(dir);
	}

	public LocalEntry(File file) {
		this.file = file;
	}

	@Override
	public String getFullPath() {
		return file.getAbsolutePath();
	}

	@Override
	public String toString() {
		return getFullPath();
	}

	@Override
	public boolean isFile() {
		return !file.isDirectory();
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public String getFileName() {
		return file.getName();
	}
	
	public String getRemoteFileName() {
		return Main.remoteDir + file.getAbsolutePath().replace("\\", "/").replace(Main.localDir, "");
	}

	public File getFile() {
		return file;
	}
	
}
