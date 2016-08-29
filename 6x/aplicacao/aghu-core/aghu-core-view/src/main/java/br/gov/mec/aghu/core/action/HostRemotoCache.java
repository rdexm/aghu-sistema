package br.gov.mec.aghu.core.action;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.Parametro;

/**
 * Classe que mantém em cache o endereço de rede do host remoto (IP ou nome do
 * computador, dependendo do parâmetro do components.properties) e o InetAddress
 * do host remoto.
 * 
 * <br>
 * <b>Deve ser utilizado via métodos das controllers</b>
 * 
 * @author lcmoura
 * 
 */
@SessionScoped
public class HostRemotoCache implements Serializable {
	private static final long serialVersionUID = 628213165227040009L;
	
	private static final Log LOG = LogFactory.getLog(HostRemotoCache.class);

	@Inject
	@Parametro("identificar_host_remoto_por_ip")
	private String indentificarHostRemotoPorIp;

	protected boolean isIdentificarHostRemotoPorIp() {
		return Boolean.parseBoolean(indentificarHostRemotoPorIp);
	}

	/**
	 * Retorna endereço de rede do host remoto (IP ou nome do computador,
	 * dependendo do parâmetro do components.properties).
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public String getEnderecoRedeHostRemoto() throws UnknownHostException {
		LOG.debug("HostRemotoCache.getEnderecoRedeHostRemoto()");
		InetAddress hostRemoto = getHostRemoto();

		if (this.isIdentificarHostRemotoPorIp()) {
			return hostRemoto.getHostAddress();
		}

		return hostRemoto.getHostName();
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
		LOG.debug("HostRemotoCache.getEnderecoIPv4HostRemoto(request)");
		return HostRemotoUtil.getHostRemoto(request);
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
		LOG.debug("HostRemotoCache.getEnderecoIPv4HostRemoto()");
		return this.getHostRemoto();
	}

	private InetAddress getHostRemoto() throws UnknownHostException {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) fc
				.getExternalContext().getRequest();
		return HostRemotoUtil.getHostRemoto(request);
	}

}