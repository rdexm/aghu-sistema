package br.gov.mec.aghu.core.action;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe utilitária para identificação do host remoto.
 * 
 * @author cvagheti
 *
 */
public class HostRemotoUtil {

	private static final Log LOG = LogFactory.getLog(HostRemotoUtil.class);

	private static final String IP_ADDRESS_FORMAT = "\\d+\\.\\d+\\.\\d+\\.\\d+";

	/**
	 * Retorna a representação do host remoto com a retirada do dominio do nome.
	 * 
	 * @param request
	 * @return
	 * @throws UnknownHostException
	 */
	public static InetAddress getHostRemoto(ServletRequest request)
			throws UnknownHostException {
		LOG.debug("++BEGIN HostRemotoUtil.getHostRemoto()");

		InetAddress result = null;

		String remoteAddr = request.getRemoteAddr();
		LOG.debug("remote address from request " + remoteAddr);

		InetAddress ina = InetAddress.getByName(remoteAddr);

		if (ina.isLoopbackAddress()) {
			// para resolver o nome, o ip continua 127
			LOG.debug("endereço é de loopback, buscando local");
			ina = InetAddress.getLocalHost();
		}

		String name = ina.getCanonicalHostName();
		byte[] address = ina.getAddress();
		LOG.debug("host " + name + "/" + Arrays.toString(address));

		name = extractName(name);

		LOG.debug("name após retirada do dominio " + name);
		
		if (isIPAddress(name)){
			LOG.warn("O nome da estação não foi resolvido para o IP " + name);
		}

		result = InetAddress.getByAddress(name, address);

		LOG.debug("++END HostRemotoUtil.getHostRemoto()");

		return result;
	}

	/**
	 * Extrai apenas o nome do host(sem o dominio), se o parâmetro for um IP não
	 * altera nada.
	 * 
	 * @param name
	 * @return
	 */
	private static String extractName(String name) {
		if (name != null && name.contains(".") && !isIPAddress(name)) {
			// retira dominio do nome
			// por exemplo cgti_samis.hcpa -> cgti_samis
			name = name.substring(0, name.indexOf('.'));
		}
		return name;
	}

	/**
	 * Retorna true se o parâmetro fornecido for um endereço IP.
	 * 
	 * @param remoteHost
	 * @return
	 */
	private static boolean isIPAddress(String remoteHost) {
		return remoteHost == null ? false : remoteHost
				.matches(IP_ADDRESS_FORMAT);
	}

}
