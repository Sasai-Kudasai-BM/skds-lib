package net.skds.tests.tcp;

import net.skds.lib2.network.AddressLocation;
import net.skds.lib2.network.intercom.*;
import net.skds.lib2.network.tcp.ClientsideTCPConnectionOptions;
import net.skds.lib2.network.tcp.ServersideTCPConnectionOptions;
import net.skds.lib2.security.AuthorizationData;
import net.skds.lib2.utils.SKDSFiles;
import net.skds.lib2.utils.ThreadUtils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.security.cert.CertificateEncodingException;
import java.util.concurrent.CompletableFuture;

public class IntercomTest {

	private static final byte[] certificates = getCertificate();

	static void main() throws CertificateEncodingException {
		SKDSLogger.replaceOuts();

		InetSocketAddress address = new InetSocketAddress(12214);

		IntercomClient client = getIntercomClient();

		IntercomServer server = getIntercomServer();
		server.start(address);

		ThreadUtils.await(100);
		CompletableFuture.supplyAsync(() -> client.tryConnect(address, 10, 500))
				.thenAccept(r -> {
					System.out.println("Connected: " + r);
					if (r) {
						var connection = client.getConnection();
						connection.requestPublicInfo();
						connection.nextPacket(true).handle(connection);
					}
				});

	}

	private static IntercomServer getIntercomServer() {
		ServersideTCPConnectionOptions serverOptions = new ServersideTCPConnectionOptions();
		IntercomServer.Configuration serverConfiguration = new IntercomServer.Configuration(
				serverOptions,
				SecurityOptions.DEFAULT,
				new IntercomSettings(128, 6, AddressLocation.LOCALHOST),
				"ServerTest"
		);
		return new IntercomServer(serverConfiguration, (sc, s) ->
				CompletableFuture.completedFuture(new ServerIntercomConnection(
						sc, s, s.getConfiguration().intercomSettings(), s.getOptions()
				) {

					@Override
					protected String getPublicInfo() {
						return "aboba";
					}

					@Override
					protected byte[] getCertificates() {
						return certificates;
					}

					@Override
					protected String processAuthorization(AuthorizationData authorizationData) {
						System.out.println(authorizationData);
						return null;
					}

					@Override
					protected void onAuthorizationSuccess(AuthorizationData authorizationData) {
						System.out.println("onAuthorizationSuccess");
					}

					@Override
					public void receivePublicInfo(String info) {
						System.out.println(info);
					}
				})
		);
	}


	private static byte[] getCertificate() {
		try {
			return Files.readAllBytes(SKDSFiles.DESKTOP_PATH.resolve("ssl", "test.pem"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static IntercomClient getIntercomClient() {
		ClientsideTCPConnectionOptions clientOptions = new ClientsideTCPConnectionOptions();
		IntercomClient.Configuration clientConfiguration = new IntercomClient.Configuration(
				clientOptions,
				SecurityOptions.DEFAULT,
				new IntercomSettings(128, 6, AddressLocation.LOCALHOST),
				"ClientTest"
		);
		return new IntercomClient(clientConfiguration, (sc, c) ->
				CompletableFuture.completedFuture(new ClientIntercomConnection(
						sc, c, c.getConfiguration().intercomSettings(), c.getOptions()
				) {

					@Override
					protected String getPublicInfo() {
						return "sex";
					}

					@Override
					protected AuthorizationData getAuthorizationData() {
						return AuthorizationData.TRUSTED;
					}

					@Override
					protected void onAuthorizationSuccess() {
						System.out.println("onAuthorizationSuccess");
						ThreadUtils.runTickable(() -> {
							if (isAlive()) {
								ping();
							}
							return true;
						}, "Client tick", 1000);
					}

					@Override
					protected void onAuthorizationFail(String error) {
						System.out.println(error);
					}

					@Override
					public void receivePublicInfo(String info) {
						System.out.println(info);
						startHandshake(SecurityLevel.WEB_SAFE);
					}
				})
		);
	}

}
