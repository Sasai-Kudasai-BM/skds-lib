package net.skds.lib2.jvmm;

import lombok.Getter;
import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.exception.UnsupportedSystemException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JVMInstallation {

	@Getter
	private final Path path;
	private final Path binPath;
	@Getter
	private final JavaVersion version;
	private final Map<String, String> properties;

	public JVMInstallation(Path path) throws IOException {
		String release = Files.readString(path.resolve("release"));
		this.properties = JVMManager.getPropertyMap(release);
		this.version = JavaVersion.of(this.properties);
		this.path = path;
		this.binPath = path.resolve("bin");
	}


	public String getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public Map<String, String> getProperties() {
		return new HashMap<>(properties);
	}

	public Path getExecutablePath(boolean console) {
		return binPath.resolve(getExecutableName(false));
	}

	public static String getExecutableName(boolean console) {
		switch (SKDSUtils.OS_TYPE) {
			case WINDOWS -> {
				return console ? "java.exe" : "javaw.exe";
			}
			case LINUX -> {
				return "java";
			}
			default -> throw new UnsupportedSystemException(SKDSUtils.OS_TYPE.name());
		}
	}
}
