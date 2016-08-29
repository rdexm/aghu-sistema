package br.gov.mec.aghu.impressao;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.print.PrintException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.core.report.PdfUtil;
import br.gov.mec.aghu.core.utils.CupsUtil;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.impressao.expectioncode.SistemaImpressaoExceptionCode;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.model.cups.ImpServidorCups;

/**
 * Implementa o sistema de impressão baseado no servidor de impressão CUPS.
 * 
 * @author cvagheti
 * 
 */
@Stateless
public class SistemaImpressaoCUPS implements ISistemaImpressaoCUPS {

	private static final long serialVersionUID = 4011804679725766468L;

	private static final Log LOG = LogFactory.getLog(SistemaImpressaoCUPS.class);

	@Inject
	private MessagesUtils messagesUtils;
	
	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	public SistemaImpressaoCUPS() {
		LOG.debug(String.format("criado sistema de impressão com implementação %s", this.getClass()));
	}

	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila e servidor associados a unidade funcional e tipo de documento.<br />
	 * A associação é feita através das classes AghImpressoraPadraoUnids e ImpImpressora.<br />
	 * O documento JasperPrint é exportado para pdf, portanto deve haver uma fila com o driver adequado cadastrada para a unidade e tipo fornecido.
	 * 
	 * @see IAghuFacade#obterImpressora(Short, TipoDocumentoImpressao)
	 */
	@Override
	public void imprimir(JasperPrint documento, AghUnidadesFuncionais unidade, TipoDocumentoImpressao tipo) throws SistemaImpressaoException {

		ImpImpressora impressora = this.getImpressora(unidade, tipo);

		try {
			// gerar pdf - exportar
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(documento, outputStream);
			byte[] bytes = outputStream.toByteArray();

			// servidor e fila
			String servidor = getServidorImpressao(impressora);
			String fila = getFilaImpressao(impressora);

			// executar lpr
			CupsUtil.envia(servidor, fila, bytes);
		} catch (PrintException e) {
			throwConnectionException(e, getServidorImpressao(impressora), getFilaImpressao(impressora));
		} catch (JRException e) {			
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.ERRO_IMPRESSAO, e.getMessage());
		}
	}

	
	public String getFilaImpressao(ImpImpressora impressora){
		String fila = impressora.getFilaImpressora();
		return fila;
	}
	
	public String getServidorImpressao(ImpImpressora impressora){
		String servidor = impressora.getImpServidorCups().getIpServidor();
		return servidor;
	}

	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila e servidor associados a unidade funcional e tipo de documento.<br />
	 * A associação é feita através das classes AghImpressoraPadraoUnids e ImpImpressora.<br />
	 * Como o documento é texto plano, deve haver uma fila com o driver adequado(Local Raw Printer) cadastrada para a unidade e tipo fornecido.
	 * 
	 * @see IAghuFacade#obterImpressora(Short, TipoDocumentoImpressao)
	 * 
	 */
	@Override
	public void imprimir(String documento, AghUnidadesFuncionais unidade, TipoDocumentoImpressao tipo) throws SistemaImpressaoException {

		ImpImpressora impressora = this.getImpressora(unidade, tipo);

		String servidor = impressora.getImpServidorCups().getIpServidor();
		String fila = impressora.getFilaImpressora();

		// executar lpr
		try {
			CupsUtil.envia(servidor, fila, documento);
		} catch (PrintException e) {
			throwException(e);
		}

	}
	
	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila e servidor associados a unidade funcional e tipo de documento.<br />
	 * A associação é feita através das classes AghImpressoraPadraoUnids e ImpImpressora.<br />
	 * Como o documento é texto plano, deve haver uma fila com o driver adequado(Local Raw Printer) cadastrada para a unidade e tipo fornecido.
	 * 
	 * @see IAghuFacade#obterImpressora(Short, TipoDocumentoImpressao)
	 * 
	 */
	@Override
	public void imprimir(String documento, AghUnidadesFuncionais unidade, TipoDocumentoImpressao tipo, boolean gerarEtiqueta) throws SistemaImpressaoException {

		ImpImpressora impressora = this.getImpressora(unidade, tipo, gerarEtiqueta);
		
		if(impressora != null) {
			String servidor = impressora.getImpServidorCups().getIpServidor();
			String fila = impressora.getFilaImpressora();
	
			// executar lpr
			try {
				CupsUtil.envia(servidor, fila, documento);
			} catch (PrintException e) {
				throwException(e);
			}
		}
	}
	
	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila associada ao remoteHost fornecido.<br />
	 * A associação é feita através da classe ImpComputadorImpressora.<br />
	 * O documento JasperPrint é exportado para pdf, portanto deve haver uma fila com o driver adequado cadastrada para o remoteHost.
	 * 
	 * @see obterImpressora(Integer, DominioTipoCups)
	 * 
	 */
	@Override
	public void imprimir(JasperPrint documento, InetAddress remoteHost) throws SistemaImpressaoException {
		this.imprimir(documento, remoteHost.getHostAddress(), null);
	}
	
	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila associada ao Nome Hosdo remoteHost fornecido.<br />
	 * A associação é feita através da classe ImpComputadorImpressora.<br />
	 * Padronizado para HCPA onde o IP pode ser dinÃ¢mico.
	 * 
	 */
	@Override
	public void imprimir(JasperPrint documento, String remoteHost) throws SistemaImpressaoException {
		this.imprimir(documento, remoteHost, null);
	}

	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila associada ao remoteHost fornecido.<br />
	 * A associação é feita através da classe ImpComputadorImpressora.<br />
	 * Como o documento é texto plano, deve haver uma fila com o driver adequado(Local Raw Printer) cadastrada para o remoteHost.
	 * 
	 * @see obterImpressora(Integer, DominioTipoCups)
	 * 
	 */
	@Override
	public void imprimir(String documento, InetAddress remoteHost) throws SistemaImpressaoException {

		ImpImpressora impressora = this.getImpressora(remoteHost.getHostAddress(), DominioTipoCups.RAW);

		String servidor = impressora.getImpServidorCups().getIpServidor();
		String fila = impressora.getFilaImpressora();

		// executar lpr
		try {
			CupsUtil.envia(servidor, fila, documento);
		} catch (PrintException e) {
			throwException(e);
		}
	}

	/**
	 * InstÃ¢ncia e lança exceção na LOG.
	 * 
	 * @param e
	 * @throws SistemaImpressaoException
	 */
	private void throwException(PrintException e) throws SistemaImpressaoException {
		LOG.error(e.getMessage(), e);
		throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.ERRO_IMPRESSAO, e.getMessage());
	}
	
	
	public void throwConnectionException(PrintException e, String servidor, String fila) throws SistemaImpressaoException{
		LOG.error(e.getMessage(), e);
		throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.MENSAGEM_FALHA_ENVIAR_CUPS, servidor, fila);
	}

	protected String getResourceBundleValue(String key, Object... params) {
		return messagesUtils.getResourceBundleValue(key, params);
	}
	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS para a fila associada ao remoteHost fornecido.<br />
	 * A associação é feita através da classe ImpComputadorImpressora.<br />
	 */
	@Override
	public void imprimirPorEnderecoComputador(String documento, String remoteHost) throws SistemaImpressaoException {

		ImpImpressora impressora = this.getImpressora(remoteHost, DominioTipoCups.RAW);

		String servidor = impressora.getImpServidorCups().getIpServidor();
		String fila = impressora.getFilaImpressora();

		// executar lpr
		try {
			CupsUtil.envia(servidor, fila, documento);
		} catch (PrintException e) {
			throwException(e);
		}
	}

	/**
	 * Retorna a impressora associada a unidade funcional e tipo de documento.
	 * 
	 * @param unidade
	 * @param tipo
	 * @return
	 * @throws SistemaImpressaoException
	 */
	protected ImpImpressora getImpressora(AghUnidadesFuncionais unidade, TipoDocumentoImpressao tipo) throws SistemaImpressaoException {
		unidade = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unidade.getSeq());
		//
		AghImpressoraPadraoUnids impressora = this.getAghuFacade().obterImpressora(unidade.getSeq(), tipo);
		if (impressora == null) {
			LOG.warn(String.format("Não existe AghImpressoraPadraoUnids associada a %s e tipo %s", unidade, tipo));
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.IMPRESSORA_PARA_UNIDADE_TIPO_NAO_ENCONTRADA, unidade.getDescricao(), tipo.getDescricao());
		}

		//
		ImpImpressora impImpressora = impressora.getImpImpressora();
		if (impImpressora == null) {
			LOG.warn(String.format("Não existe ImpImpressora associada a %s e tipo %s", unidade, tipo));
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.IMPRESSORA_PARA_UNIDADE_TIPO_NAO_ENCONTRADA, unidade.getDescricao(), tipo.getDescricao());
		}
		
		// verifica redirecionamento
		if (impImpressora.getImpRedireciona() != null) {
			LOG.warn(String.format("Impressora %s redireciona para %s", impressora, impImpressora.getImpRedireciona()));
			impImpressora = impImpressora.getImpRedireciona();
		}

		return impImpressora;
	}
	
	/**
	 * Retorna a impressora associada a unidade funcional e tipo de documento.
	 * 
	 * @param unidade
	 * @param tipo
	 * @param gerarEtiqueta
	 * @return 
	 * @throws SistemaImpressaoException
	 */
	protected ImpImpressora getImpressora(AghUnidadesFuncionais unidade, TipoDocumentoImpressao tipo, boolean gerarEtiqueta) throws SistemaImpressaoException {
		unidade = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unidade.getSeq());
		//
		AghImpressoraPadraoUnids impressora = this.getAghuFacade().obterImpressora(unidade.getSeq(), tipo);
		if (impressora == null) {
			if(gerarEtiqueta){
				ImpImpressora imprimirEtiqueta = this.getImpressora(unidade, tipo);
				return imprimirEtiqueta;
			} 
			return null;
		}
		ImpImpressora impImpressora = this.getImpressora(unidade, tipo);
		
		return impImpressora;
		
	}

	/**
	 * Retorna a impressora associada ao ip do cliente para impressão do tipo fornecido.
	 * 
	 * @param ipComputador
	 * @return
	 * @throws SistemaImpressaoException
	 */
	protected ImpImpressora getImpressora(String ipComputador, DominioTipoCups tipo) throws SistemaImpressaoException {
		// busca computador pelo remoteHost
		ImpComputador computador = this.getCadastrosBasicosCupsFacade().obterComputador(ipComputador);
		if (computador == null) {
			LOG.warn(String.format("Não existe computador cadastrado com o IP %s", ipComputador));
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.IMPRESSORA_PARA_CLIENTE_TIPO_NAO_ENCONTRADA, tipo.getDescricao());
		}

		// buscar impressora associada ao remoteHost x tipo
		ImpComputadorImpressora computadorImpressora = this.getCadastrosBasicosCupsFacade().obterImpressora(computador.getSeq(), tipo);

		if (computadorImpressora == null) {
			LOG.warn(String.format("Não existe impressora associada ao computador %s", computador));
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.IMPRESSORA_PARA_CLIENTE_TIPO_NAO_ENCONTRADA, tipo.getDescricao());
		}

		ImpImpressora impressora = computadorImpressora.getImpImpressora();

		// verifica redirecionamento
		if (impressora.getImpRedireciona() != null) {
			LOG.warn(String.format("Impressora %s redireciona para %s", impressora, impressora.getImpRedireciona()));
			impressora = impressora.getImpRedireciona();
		}

		return impressora;
	}

	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS
	 * para a fila associada ao remoteHost fornecido.<br />
	 * A associação é feita através da classe ImpComputadorImpressora.<br />
	 * O documento JasperPrint é exportado para pdf, portanto deve haver uma
	 * fila com o driver adequado cadastrada para o remoteHost.
	 * 
	 * Indicado para impressão remota
	 * 
	 * @see obterImpressora(Integer, DominioTipoCups)
	 * 
	 */
	@Override
	public void imprimir(byte[] documento, InetAddress remoteHost) throws SistemaImpressaoException {
		LOG.debug(String.format("imprimir documento para remote host %s", remoteHost));
		
		ImpImpressora impressora = getImpressora(remoteHost.getHostAddress(), DominioTipoCups.PDF);
		try {
			// servidor e fila
			String servidor = impressora.getImpServidorCups().getIpServidor();
			String fila = impressora.getFilaImpressora();
			
			// executar lpr
			CupsUtil.envia(servidor, fila, documento);
			
		} catch (PrintException e) {
			throwException(e);
		}
	}
	
	
	/**
	 * Imprime o documento fornecido para impressora associada a unidade funcional e que, ao mesmo tempo, esteja instalada ao remoteHost, e tipo de documento.
	 * funcional e que, ao mesmo tempo, esteja instalada ao remoteHost, e tipo
	 * de documento.
	 * 
	 * @param documento
	 *            documento gerado pelo jasper, serÃ¡ exportado conforme a necessidade de impressão.
	 * @param unidade
	 *            unidade funcional para identificação da impressora
	 * @param remoteHost
	 *            InetAddress do cliente
	 * @param tipo
	 *            tipo de documento para identificação da impressora
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	@Override
	public void imprimir(JasperPrint documento, AghUnidadesFuncionais unidade, InetAddress remoteHost, TipoDocumentoImpressao tipo) throws SistemaImpressaoException {
		ImpImpressora impressora = this.getImpressora(unidade, remoteHost.getHostAddress(), tipo);

		try {
			// gerar pdf - exportar
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(documento, outputStream);
			byte[] bytes = outputStream.toByteArray();

			// servidor e fila
			String servidor = impressora.getImpServidorCups().getIpServidor();
			String fila = impressora.getFilaImpressora();

			// executar lpr
			CupsUtil.envia(servidor, fila, bytes);
		} catch (PrintException e) {
			throwException(e);
		} catch (JRException e) {
			LOG.error("erro no export para pdf", e);
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.ERRO_IMPRESSAO, e.getMessage());
		}
	}

	/**
	 * Imprime o documento fornecido para impressora associada a unidade funcional e que, ao mesmo tempo, esteja instalada ao remoteHost, e tipo de documento.
	 * 
	 * @param documento
	 *            documento texto plano
	 * @param unidade
	 *            unidade funcional para identificação da impressora
	 * @param remoteHost
	 *            InetAddress do cliente
	 * @param tipo
	 *            tipo de documento para identificação da impressora
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	@Override
	public void imprimir(String documento, AghUnidadesFuncionais unidade, InetAddress remoteHost, TipoDocumentoImpressao tipo) throws SistemaImpressaoException {
		ImpImpressora impressora = this.getImpressora(unidade, remoteHost.getHostAddress(), tipo);

		String servidor = impressora.getImpServidorCups().getIpServidor();
		String fila = impressora.getFilaImpressora();

		// executar lpr
		try {
			CupsUtil.envia(servidor, fila, documento);
		} catch (PrintException e) {
			throwException(e);
		}
	}

	/**
	 * Retorna a impressora associada ao ip do cliente para impressão do tipo fornecido.
	 * 
	 * @param ipComputador
	 * @return
	 * @throws SistemaImpressaoException
	 */
	protected ImpImpressora getImpressora(AghUnidadesFuncionais unidade, String ipComputador, TipoDocumentoImpressao tipo) throws SistemaImpressaoException {
		unidade = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unidade.getSeq());

		AghImpressoraPadraoUnids impressora = this.getAghuFacade().obterImpressora(unidade.getSeq(), ipComputador, tipo);
		if (impressora == null) {
			LOG.warn(String.format("Não existe AghImpressoraPadraoUnids associada a %s e tipo %s", unidade, tipo));
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.IMPRESSORA_PARA_UNIDADE_TIPO_NAO_ENCONTRADA, unidade.getDescricao(), tipo.getDescricao());
		}

		ImpImpressora impImpressora = impressora.getImpImpressora();
		if (impImpressora == null) {
			LOG.warn(String.format("Não existe ImpImpressora associada a %s e tipo %s", unidade, tipo));
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.IMPRESSORA_PARA_UNIDADE_TIPO_NAO_ENCONTRADA, unidade.getDescricao(), tipo.getDescricao());
		}

		// verifica redirecionamento
		if (impImpressora.getImpRedireciona() != null) {
			LOG.warn(String.format("Impressora %s redireciona para %s", impressora, impImpressora.getImpRedireciona()));
			impImpressora = impImpressora.getImpRedireciona();
		}

		return impImpressora;
	}

	public ICadastrosBasicosCupsFacade getCadastrosBasicosCupsFacade() {
		return cadastrosBasicosCupsFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}


	
	@Override
	public void imprimir(ByteArrayOutputStream documento, InetAddress remoteHost, OpcoesImpressao opcoes) throws SistemaImpressaoException {

		if (PdfUtil.isImpressaoProtegida(documento)) {
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.DOCUMENTO_IMPRESSAO_PROTEGIDA);
		}

		ImpImpressora impressora = this.getImpressora(remoteHost.getHostAddress(), DominioTipoCups.PDF);

		String servidor = impressora.getImpServidorCups().getIpServidor();
		String fila = impressora.getFilaImpressora();

		try {
			if (opcoes == null) {
				CupsUtil.envia(servidor, fila, null, documento.toByteArray(), null);
				
			} else {
				CupsUtil.envia(servidor, fila, opcoes.toArray(), documento.toByteArray(), null);
			}
		} catch (PrintException e) {
			throwException(e);
		}
	}

	/**
	 * @deprecated mantido para compatibilidade, não usa o parametro nomeArquivo, remover
	 */
	@Override
	public void imprimir(ByteArrayOutputStream documento, InetAddress remoteHost, String nomeArquivo) throws SistemaImpressaoException {
		this.imprimir(documento, remoteHost, (OpcoesImpressao) null);
	}

	@Override
	public void imprimir(JasperPrint documento, String remoteHost, OpcoesImpressao opcoes) throws SistemaImpressaoException {
		LOG.debug(String.format("imprimir documento %s para remote host %s", documento.getName(), remoteHost));

		ImpImpressora impressora = getImpressora(remoteHost, DominioTipoCups.PDF);
		try {
			// gerar pdf - exportar
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(documento, outputStream);
			byte[] bytes = outputStream.toByteArray();

			// servidor e fila
			String servidor = impressora.getImpServidorCups().getIpServidor();
			String fila = impressora.getFilaImpressora();

			// executar lpr
			if (opcoes == null) {
				CupsUtil.envia(servidor, fila, null, bytes, null);
			} else {
				CupsUtil.envia(servidor, fila, opcoes.toArray(), bytes, null);
			}

		} catch (PrintException e) {
			throwException(e);
		} catch (JRException e) {
			LOG.error("erro no export para pdf", e);
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.ERRO_IMPRESSAO, e.getMessage());
		}
	}

	@Override
	public void imprimir(String documento, String impressora)
			throws SistemaImpressaoException {
		String servidor = getServidor();
		String fila = getFila(impressora);

		// executar lpr
		try {
			CupsUtil.envia(servidor, fila, documento);
			LOG.warn("Impressão sem definição de Servidor CUPS sendo enviada para o primeiro Servidor CUPS encontrado na base.");
		} catch (PrintException e) {
			throwException(e);
		}		
	}
	
	protected String getServidor() throws SistemaImpressaoException {
		// verificar onde configurar o nome do servidor
		List<ImpServidorCups> list = this.getCadastrosBasicosCupsFacade()
				.pesquisarServidorCups(null);
		if (list.isEmpty()) {
			throw new SistemaImpressaoException(
					SistemaImpressaoExceptionCode.SERVIDOR_NAO_ENCONTRADO);
		}

		return list.get(0).getIpServidor();
	}
	
	/**
	 * Retorna o nome da fila de impressão no servidor CUPS.<br />
	 * O nome da fila é o mesmo nome da impressora no servidor de impressão
	 * windows.<br />
	 * Este método extrai o nome da impressora da string que contém o nome do
	 * servidor ou estação. Exemplo:<br />
	 * //hcps2/gsis_2b48 -> gsis_2b48
	 * 
	 * @param printer
	 * @return
	 * @throws SistemaImpressaoException 
	 */
	protected String getFila(String printer) throws SistemaImpressaoException {
		if (printer == null || printer.isEmpty()) {
			throw new SistemaImpressaoException(
					SistemaImpressaoExceptionCode.IMPRESSORA_NAO_ENCONTRADA);
		}
		int ini = printer.lastIndexOf('\\');
		return printer.substring(ini + 1);
	}

	
	@Override
	public void imprimir(ImpImpressora impressora, JasperPrint documento) throws SistemaImpressaoException, ApplicationBusinessException {

		
		try {
			// gerar pdf - exportar
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			JasperExportManager.exportReportToPdfStream(documento, outputStream);
			byte[] bytes = outputStream.toByteArray();

			// servidor e fila
			impressora.setImpServidorCups(cadastrosBasicosCupsFacade.obterServidorCups(impressora.getImpServidorCups().getId()));
			
			String servidor = impressora.getImpServidorCups().getIpServidor();
			String fila = impressora.getFilaImpressora();

			// executar lpr
			
			CupsUtil.envia(servidor, fila, null, bytes, null);
			

		} catch (PrintException e) {
			throwException(e);
		} catch (JRException e) {
			LOG.error("erro no export para pdf", e);
			throw new SistemaImpressaoException(SistemaImpressaoExceptionCode.ERRO_IMPRESSAO, e.getMessage());
		}

	}
}