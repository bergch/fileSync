package da_bubu.de.filesync.connection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import da_bubu.de.filesync.Main;
import da_bubu.de.filesync.file.LocalDirectory;
import da_bubu.de.filesync.file.LocalEntry;
import da_bubu.de.filesync.file.LocalFile;

public class LocalConnection {

	private static LocalConnection instance;

	public void walk(String path, List<String> fileList, String initialFolder) {

		File root = new File(path);
		File[] files = root.listFiles();

		if (files == null)
			return;

		for (File f : files) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath(), fileList, initialFolder);
				fileList.add(f.getAbsoluteFile().toString()
						.replace(initialFolder, ""));
			} else {
				fileList.add(f.getAbsoluteFile().toString()
						.replace(initialFolder, ""));
			}
		}
	}

	public static LocalConnection getInstance() {
		if (instance == null) {
			instance = new LocalConnection();
		}
		return instance;
	}

	public List<LocalEntry> getDirectory(LocalDirectory parentDir) {
	    Main.log("read local dir:"+parentDir.getFullPath());
		File root = new File(parentDir.getFullPath());
		File[] files = root.listFiles();

		if (files == null) {
			return new ArrayList<LocalEntry>();
		}
		for (File f : files) {
			if (f.isDirectory()) {
				LocalDirectory localDirectory = new LocalDirectory(f);
				List<LocalEntry> dirContent = getDirectory(localDirectory);
				localDirectory.add(dirContent);
				parentDir.add(localDirectory);
			} else {
				LocalFile localFile = new LocalFile(f);
				if(!localFile.getFileName().toLowerCase().equals("thumbs.db") && !localFile.getFileName().toLowerCase().equals("thumbnails.db") && !localFile.getFileName().equals(".DS_Store") ) {
					parentDir.add(localFile);
				}
			}
		}

		return new ArrayList<LocalEntry>();
	}
}
