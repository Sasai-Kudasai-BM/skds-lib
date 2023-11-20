package net.skds.lib.utils;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtils {

	private static final HttpClient.Builder builder = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(2));

	public static Map<String, String> queryToMap(String query) {
		if (query == null || query.isEmpty()) {
			return Map.of();
		}
		Map<String, String> map = new HashMap<>();
		String[] arr = query.split("&");
		for (int i = 0; i < arr.length; i++) {
			String val = arr[i];
			int pos = val.indexOf('=');
			if (pos != -1 && pos < val.length() - 1) {
				map.put(val.substring(0, pos), val.substring(pos + 1));
			}
		}
		return map;
	}


	public static DownloadProcess downloadFromNet(String url) {
		try {
			HttpClient client = builder.build();
			HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
			System.out.println(request.headers().map());
			var response = client.send(request, ri -> HttpResponse.BodySubscribers.ofInputStream());
			List<String> cl = response.headers().map().get("content-length");
			if (cl == null || cl.isEmpty()) {
				throw new RuntimeException("content-length not provided");
			}
			int len = Integer.parseInt(cl.get(0));
			return new DownloadProcess(len, response.body());

		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to download " + url);
	}

	public static class DownloadProcess {

		private final InputStream inputStream;
		@Getter
		private final byte[] content;
		@Getter
		private volatile int progress = 0;

		protected DownloadProcess(int size, InputStream is) {
			this.content = new byte[size];
			this.inputStream = is;
		}

		public void readAll() throws IOException {
			do {
				this.progress += inputStream.read(content, progress, content.length - progress);
			} while (!isReady());
			inputStream.close();
		}

		public boolean checkSHA1(String sha1) {
			try {
				byte[] sha = SKDSUtils.HEX_FORMAT_LC.parseHex(sha1);
				MessageDigest md = MessageDigest.getInstance("SHA1");
				return Arrays.equals(md.digest(content), sha);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		public boolean checkSHA1(byte[] sha1) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA1");
				return Arrays.equals(md.digest(content), sha1);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		public int getSize() {
			return content.length;
		}

		public boolean isReady() {
			return progress == content.length;
		}
	}
}
