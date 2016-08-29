package br.gov.mec.aghu.impressao;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

import javax.ejb.Local;

import net.sf.jasperreports.engine.JasperPrint;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Interface para abstração do sistema de impressão.<br />
 * A abstração ocorre tanto na identificação da impressora a ser usada quanto a
 * forma que o documento será enviado a impressora.<br />
 * 
 * @author cvagheti
 * 
 */
@Local
public interface ISistemaImpressaoCUPS extends Serializable {
	
	

	/**
	 * Imprime o documento fornecido para impressora associada a unidade
	 * funcional e tipo de documento.
	 * 
	 * @param documento
	 *            documento gerado pelo jasper, será exportado conforme a
	 *            necessidade de impressão.
	 * @param unidade
	 *            unidade funcional para identificação da impressora
	 * @param tipo
	 *            tipo de documento para identificação da impressora
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(JasperPrint documento, AghUnidadesFuncionais unidade,
			TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException;

	/**
	 * Imprime o documento fornecido para impressora associada a unidade
	 * funcional e tipo de documento.
	 * 
	 * @param documento
	 *            documento texto plano
	 * @param unidade
	 *            unidade funcional para identificação da impressora
	 * @param tipo
	 *            tipo de documento para identificação da impressora
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(String documento, AghUnidadesFuncionais unidade,
			TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException;


	/**
	 * Imprime o documento fornecido para impressora associada a unidade
	 * funcional e que, ao mesmo tempo, esteja instalada ao remoteHost, e tipo
	 * de documento.
	 * 
	 * @param documento
	 *            documento gerado pelo jasper, será exportado conforme a
	 *            necessidade de impressão.
	 * @param unidade
	 *            unidade funcional para identificação da impressora
	 * @param remoteHost
	 *            InetAddress do cliente
	 * @param tipo
	 *            tipo de documento para identificação da impressora
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(JasperPrint documento, AghUnidadesFuncionais unidade, 
			InetAddress remoteHost, TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException;

	/**
	 * Imprime o documento fornecido para impressora associada a unidade
	 * funcional e que, ao mesmo tempo, esteja instalada ao remoteHost, e tipo
	 * de documento.
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
	void imprimir(String documento, AghUnidadesFuncionais unidade, InetAddress remoteHost,
			TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException;
	
	/**
	 * Imprime o documento fornecido para impressora instalada no remoteHost.
	 * 
	 * @param documento
	 *            documento gerado pelo jasper, será exportado conforme a
	 *            necessidade de impressão.
	 * @param remoteHost
	 *            InetAddress do cliente
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(JasperPrint documento, InetAddress remoteHost)
			throws SistemaImpressaoException;
	
	/**
	 * Imprime o documento fornecido para impressora instalada no remoteHost.
	 * 
	 * @param documento
	 *            documento gerado pelo jasper, será exportado conforme a
	 *            necessidade de impressão.
	 * @param remoteHost
	 *            Nome do Host do cliente
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(JasperPrint documento, String remoteHost)
			throws SistemaImpressaoException;
	
	void imprimir(JasperPrint documento, String remoteHost, OpcoesImpressao opcoes)
			throws SistemaImpressaoException;

	/**
	 * Imprime o documento fornecido para impressora instalada no remoteHost.
	 * 
	 * @param documento
	 *            documento texto plano
	 * @param remoteHost
	 *            InetAddress do cliente
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(String documento, InetAddress remoteHost)
			throws SistemaImpressaoException;

	void imprimir(String documento, String impressora)
			throws SistemaImpressaoException;

	/**
	 * Imprime o documento fornecido para impressora instalada no remoteHost.
	 * 
	 * @deprecated usar @see
	 *             {@link ISistemaImpressaoCUPS#imprimir(String, String)} buscar
	 *             impressora desejada e fornecer no método
	 * 
	 * @param documento
	 *            documento texto plano
	 * @param remoteHost
	 *            Host do cliente
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimirPorEnderecoComputador(String documento, String remoteHost)
			throws SistemaImpressaoException;

	/**
	 * Imprime o documento fornecido através de um servidor de impressão CUPS
	 * para a fila associada ao remoteHost fornecido.<br />
	 * A associação é feita através da classe ImpComputadorImpressora.<br />
	 * O documento JasperPrint é exportado para pdf, portanto deve haver uma
	 * fila com o driver adequado cadastrada para o remoteHost.
	 * 
	 * 
	 * Indicado para impressão remota
	 * 
	 * 
	 * @see obterImpressora(Integer, DominioTipoCups)
	 * 
	 */
	void imprimir(byte[] documento, InetAddress remoteHost) throws SistemaImpressaoException;


	

	/**
	 * Imprime o documento fornecido como ByteArrayOutputStream para a impressora instalada no remoteHost.
	 * 
	 * @param documento
	 * 				ByteArrayOutputStream do arquivo ou conteúdo a ser impresso.
	 * @param remoteHost
	 * 				InetAddress do cliente
	 * @param opcoes
	 * 				opções de impressão @see {@link OpcoesImpressao} ou null para default
	 * @throws SistemaImpressaoException
	 * 				quando algum erro impedir a impressão
	 */	
	void imprimir(ByteArrayOutputStream documento, InetAddress remoteHost,
			OpcoesImpressao opcoes) throws SistemaImpressaoException;
	
	/**
	 * @deprecated mantido para compatibilidade
	 * @param documento
	 * @param remoteHost
	 * @param nomeArquivo
	 * @throws SistemaImpressaoException
	 */
	void imprimir(ByteArrayOutputStream documento, InetAddress remoteHost,
			String  nomeArquivo) throws SistemaImpressaoException;
	
	void imprimir(ImpImpressora impressora, JasperPrint documento) throws SistemaImpressaoException, ApplicationBusinessException; 
	
	/**
	 * Imprime o documento fornecido para impressora associada a unidade
	 * funcional e tipo de documento.
	 * 
	 * @param documento
	 *            documento texto plano
	 * @param unidade
	 *            unidade funcional para identificação da impressora
	 * @param tipo
	 *            tipo de documento para identificação da impressora
	 * @param gerarEtiqueta
	 *            validação para gerar etiqueta
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	void imprimir(String documento, AghUnidadesFuncionais unidade,
			TipoDocumentoImpressao tipo, boolean gerarEtiqueta)
			throws SistemaImpressaoException;
	
}
