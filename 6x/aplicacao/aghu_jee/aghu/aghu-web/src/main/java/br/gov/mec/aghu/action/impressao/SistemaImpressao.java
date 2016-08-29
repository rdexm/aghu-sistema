package br.gov.mec.aghu.action.impressao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.impressao.ISistemaImpressaoCUPS;
import br.gov.mec.aghu.impressao.OpcoesImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.commons.Parametro;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@ApplicationScoped
@Named
public class SistemaImpressao {	
	
	private Boolean flagCups;
	
	@Inject @Parametro("sistema_impressao_cups") 
	private String stFlagCups;
	
	@EJB
	private ISistemaImpressaoCUPS sistemaImpressaoCups;
	
	@Inject
	private SistemaImpressaoClienteManager sistemaImpressaoClienteManager;	
	
	@PostConstruct
	protected void init() {
		flagCups = Boolean.valueOf(stFlagCups);
	}
	
	
	public String download() throws IOException, JRException {
		if (!verificarDownloadRelatorio()){
			return null;
		}
		return sistemaImpressaoClienteManager.download();
	}

	@Deprecated
	public String downloadAll() throws IOException, JRException {
		if (!verificarDownloadRelatorio()){
			return null;
		}
		return sistemaImpressaoClienteManager.download();
	}
	
	
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
	public void imprimir(JasperPrint documento, AghUnidadesFuncionais unidade,
			TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException{
		
			sistemaImpressaoCups.imprimir(documento, unidade, tipo);	
		
	}

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
	public void imprimir(String documento, AghUnidadesFuncionais unidade,
			TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException{	
		
			sistemaImpressaoCups.imprimir(documento, unidade, tipo);		
		
	}


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
	public void imprimir(JasperPrint documento, AghUnidadesFuncionais unidade, 
			InetAddress remoteHost, TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException{	
		
			sistemaImpressaoCups.imprimir(documento, unidade,remoteHost, tipo);		
		
	}

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
	public void imprimir(String documento, AghUnidadesFuncionais unidade, InetAddress remoteHost,
			TipoDocumentoImpressao tipo)
			throws SistemaImpressaoException{		
		
			sistemaImpressaoCups.imprimir(documento, unidade,remoteHost, tipo);
		
		
	}
	
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
	public void imprimir(JasperPrint documento, InetAddress remoteHost)
			throws SistemaImpressaoException{		
		if (flagCups){
			sistemaImpressaoCups.imprimir(documento, remoteHost);
		}else{
			sistemaImpressaoClienteManager.put(documento);
		}
		
	}

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
	public void imprimir(String documento, InetAddress remoteHost)
			throws SistemaImpressaoException{		
		if (flagCups){
			sistemaImpressaoCups.imprimir(documento, remoteHost);
		}else{
			sistemaImpressaoClienteManager.put(documento);
		}
		
	}

	/**
	 * Retorna true se usa impressão no cliente.
	 * 
	 * @return
	 */
	public boolean verificarDownloadRelatorio(){
		return !flagCups && !sistemaImpressaoClienteManager.isEmpty();
	}

	/**
	 * Imprime o documento fornecido como ByteArrayOutputStream para a impressora instalada no remoteHost.
	 * 
	 * @param documento
	 * 				ByteArrayOutputStream do arquivo ou conteúdo a ser impresso.
	 * @param remoteHost
	 * 				InetAddress do cliente
	 * @param nomePdf
	 * 				Nome simples para identificação
	 * @throws SistemaImpressaoException
	 * 				quando algum erro impedir a impressão
	 */	
	public void imprimir(ByteArrayOutputStream documento, InetAddress remoteHost,
			String nomePdf) throws SistemaImpressaoException{
		if (flagCups){
			sistemaImpressaoCups.imprimir(documento, remoteHost, nomePdf);
		}else{
			sistemaImpressaoClienteManager.put(documento);
		}
		
	}
	

	
	/**
	 * Imprime o documento fornecido para impressora instalada no remoteHost.
	 * 
	 * @param documento
	 *            ByteArrayOutputStream do arquivo ou conteúdo a ser impresso.
	 * @param remoteHost
	 *            InetAddress do cliente
	 * @param opcoes 
	 * 			  Opções de impressão
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	public void imprimir(ByteArrayOutputStream documento, InetAddress remoteHost, OpcoesImpressao opcoes) 
			throws SistemaImpressaoException{
		if (flagCups){
			sistemaImpressaoCups.imprimir(documento, remoteHost, opcoes);
		}else{
			sistemaImpressaoClienteManager.put(documento);
		}
		
	}
	
	/**
	 * Imprime o documento na fila CUPS da impressora passada como parâmetro
	 * Caso o compartilhamento seja passado, irá pegar o nome da impressora
	 * e imprimir pelo CUPS nessa fila
	 * 
	 * Ex.: \\BLOCO_12C10\BLOCO_12C10 envia o comando CUPS para a fila BLOCO_12C10
	 * 
	 * Tipicamente esse endereco é obtido na tabela AGH_MICROCOMPUTADORES.IMPRESSORA_ETIQUETAS
	 * 
	 * @param documento
	 * @param impressora
	 * @throws SistemaImpressaoException
	 */
	public void imprimir(String documento, String impressora) throws SistemaImpressaoException {
		sistemaImpressaoCups.imprimir(documento, impressora);
	}
	
	/**
	 * Imprime o documento fornecido para impressora pasada por parametro.
	 * 	
	 * @throws SistemaImpressaoException
	 *             quando algum erro impedir a impressão
	 */
	public void imprimir(ImpImpressora impressora, JasperPrint documento)
			throws SistemaImpressaoException, ApplicationBusinessException {
		
			sistemaImpressaoCups.imprimir(impressora, documento);
		
	}
	
	public Integer getCount(){
		return sistemaImpressaoClienteManager.count();
	}

	public Boolean getFlagCups() {
		return flagCups;
	}

	public void setFlagCups(Boolean flagCups) {
		this.flagCups = flagCups;
	}
	
	public boolean usarCliente(){
		return !this.flagCups;
	}

}
