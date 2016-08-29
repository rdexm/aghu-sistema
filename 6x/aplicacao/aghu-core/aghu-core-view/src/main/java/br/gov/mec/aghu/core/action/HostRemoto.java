package br.gov.mec.aghu.core.action;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author lcmoura
 * @deprecated utilizar a classe @see {@link HostRemotoUtil}
 */
public abstract class HostRemoto implements Serializable {
	private static final long serialVersionUID = -3359429072850067935L;

	private static final Log LOG = LogFactory.getLog(HostRemoto.class);
	
	/**
	 * Valida se um endereço de rede está no padrão IPv4. Endereços retornados
	 * como válidos por este método incluem: 0.0.0.0 127.0.0.1 255.255.255.255
	 * 192.168.0.1 192.00.0.1 001.0.0.1
	 * 
	 * @param enderecoIPv4
	 *            Um endereço de rede padrão IPv4
	 * @return Verdadeiro caso o endereço esteja no padrão IPv4, falso caso
	 *         contrário
	 */
	private boolean validarEnderecoIPv4(String enderecoIPv4) {
		Pattern pattern = Pattern
				.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

		Matcher matcher = pattern.matcher(enderecoIPv4);
		return matcher.find() && matcher.group().equals(enderecoIPv4);
	}

	/**
	 * Método que retorna o nome do computador na rede, ou o IP
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public String getEnderecoRedeHostRemoto() throws UnknownHostException {
		String computerName = "";

		// --[BUSCA CONTEXTO PARA ADQUIRIR IP DO CLIENTE]
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();

		// Caso não tenha conseguido obter pela forma canônica ou normal,
		// tenta obter o IP pelo atributo X-Forwarded-For do header HTTP.
		// Se houver um valor retornado neste atributo e se ele for um IPv4
		// válido então este será forçado como o endereço do host. Isto serve
		// para casos onde o AGHU rode com proxy web na frente do servidor
		// de aplicação.
		// Proxy da microsoft (TMG, ISA, etc...) não geram por default esse header
		// http://www.isaserver.org/software/Reporting/X-Forwarded-For-for-TMG-ISA-Server-243076.html
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()
				&& validarEnderecoIPv4(xForwardedFor)) {
			computerName = xForwardedFor;
			LOG.debug("Obteve o seguinte IP pelo atributo X-Forwarded-For do header HTTP: "
					+ computerName);
			return computerName;
		}

		// O computador tem que estar registrado no DNS. Pode demorar um pouco o registro,
		// mas teoricamente nas máquinas windows é registrado no momento do login.
		InetAddress addr = InetAddress.getByName(request.getRemoteAddr());

		// --[VERIFICA SE A CONEXÃO É LOCAL E TRATA O STRING DE RETORNO]
		boolean identificarHostRemotoPorIp = isIdentificarHostRemotoPorIp();
		if (addr.isLoopbackAddress()) {
			LOG.debug("Endereço é de loopback");
			if (identificarHostRemotoPorIp) {
				computerName = InetAddress.getLocalHost().getHostAddress();
			} else {
				computerName = InetAddress.getLocalHost()
						.getCanonicalHostName();
			}
		} else {
			if (identificarHostRemotoPorIp) {
				computerName = addr.getHostAddress();
			} else {
				computerName = addr.getCanonicalHostName();
			}
			
		}
		
		// Testa se endereço retornado não é nulo e nem é um IPv4 válido.
		// Isto é para testar se foi atribuído, por exemplo, um endereço
		// do tipo 192.168.1.2.HCPA que será então limpo retirando-se a
		// última parte (HCPA no exemplo)
		if (computerName != null && computerName.contains(".")
			&& !validarEnderecoIPv4(computerName)) {
			LOG.debug("Obteve o seguinte endereço canônico do host: "  + computerName);
			computerName = computerName.substring(0,
					computerName.indexOf('.'));
		}

		return computerName;
	}

	/**
	 * Retorna o InetAddress do cliente recuperado a partir do requestfornecido.<br />
	 * Não resolve o endereço e nome do localhost para permitir testes em
	 * desenvolvimento.
	 * 
	 * @see InetAddress
	 * @param request
	 * @return endereço de rede no formato IPv4
	 * @throws UnknownHostException
	 */
	public InetAddress getEnderecoIPv4HostRemoto(ServletRequest request)
			throws UnknownHostException {
		// IP obtido caso haja um proxy web
		String remoteAddr = ((HttpServletRequest) request)
				.getHeader("X-Forwarded-For");
		if (remoteAddr == null || remoteAddr.isEmpty()
				|| !validarEnderecoIPv4(remoteAddr)) {
			remoteAddr = request.getRemoteAddr(); // IP
		}

		return InetAddress.getByName(remoteAddr);
	}

	/**
	 * Retorna o InetAddress do cliente recuperado a partir do request do faces
	 * context.<br />
	 * Não resolve o endereço e nome do localhost para permitir testes em
	 * desenvolvimento.
	 * 
	 * @see InetAddress
	 * @return endereço de rede no formato IPv4
	 * @throws UnknownHostException
	 */
	public InetAddress getEnderecoIPv4HostRemoto() throws UnknownHostException {
		FacesContext fc = FacesContext.getCurrentInstance();
		ServletRequest request = (ServletRequest) fc.getExternalContext()
				.getRequest();
		return getEnderecoIPv4HostRemoto(request);

	}

	protected abstract boolean isIdentificarHostRemotoPorIp();
}