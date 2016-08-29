package br.gov.mec.aghu.compras.action;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoCadastradaImpressaoVO;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoImpressaoVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.action.RotinaDiariaMateriaisController.EnumTargetRotinaDiariaMateriais;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;
public class ImprimirSolicitacaoPregaoEletronicoBBController extends ActionReport {

	private static final String NOME_ARQUIVO = "NOME_ARQUIVO";

	private static final String NOME_INSTITUICAO = "NOME_INSTITUICAO";

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ImprimirSolicitacaoPregaoEletronicoBBController.class);

	private static final long serialVersionUID = -5590739332664571751L;
	
	private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	
	private static final String IMPRIMIR_HISTORICO_RESUMIDO_LICITACAO = "imprimirHistoricoResumidoLicitacao";
	
	private static final String IMPRIMIR_HISTORICO_DETALHADO_LICITACAO = "imprimirHistoricoDetalhadoLicitacao";
	
	private static final String IMPRIMIR_HISTORICO_CADASTRADA_LICITACAO = "imprimirHistoricoLicitacaoCadastrada";

	private DominioNomeRelatorio relatorio;
	
	private String titlePdfView;
	
	private Map<String, Object> parametrosEspecificos;
	
	private String nomeArquivoRelatorio;

	private String nomeArquivo;

	private Integer numeroPac;
	
	private List<?> dadosRelatorio = null;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private String cameFrom;
	
	public String iniciar(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException, DocumentException, IOException, JRException {
		
		setNomeArquivo(nomeArquivoRetorno);
		setNumeroPac(numPac);
		return print(numPac, nomeArquivoRetorno);
	}
	
	public String iniciarLicitacaoCadastrada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException, DocumentException, IOException, JRException {
		
		setNomeArquivo(nomeArquivoRetorno);
		setNumeroPac(numPac);
		return printLicitacaoCadastrada(numPac, nomeArquivoRetorno);
	}
	
	/**
	 * 
	 * @author aghu
	 *
	 */
	private enum RotinaImprimirSolicitacaoPregaoEletronicoBBControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
	
	/**
	 * 
	 * @author aghu
	 *
	 */
	public enum EnumTargetImprimirSolicitacaoPregaoEletronicoBB {		
		TITLE_RELATORIO_SOLICITACAO_PREGAO_ELETRONICO_BB_DETALHADA, TITLE_RELATORIO_SOLICITACAO_PREGAO_ELETRONICO_BB_RESUMIDA, TITLE_RELATORIO_SOLICITACAO_PREGAO_ELETRONICO_BB_CADASTRADA, PROBLEMA_ARQUIVO_DIRETORIO;
	}
	

	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return getNomeArquivoRelatorio();
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		try{
			String nomeHospital = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put(NOME_INSTITUICAO, nomeHospital);			
			params.put(NOME_ARQUIVO, nomeArquivo);			
					
			if (getParametrosEspecificos() != null) {
				params.putAll(getParametrosEspecificos());
			}
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return params;
	}	

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 * @throws ParseException 
	 */
	public String print(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException, DocumentException, IOException, JRException {
		
		String retorno = null; 
		this.nomeArquivo = nomeArquivoRetorno;
		try {
			List<ScoLicitacaoImpressaoVO> dados = solicitacaoPregaoEletronicoBB(numPac, nomeArquivoRetorno);
			dadosRelatorio = dados;			
			
			if(dadosRelatorio == null || dadosRelatorio.isEmpty()) {
				exibirMensagemPesquisaSemDados();			
			} else {			
				DocumentoJasper documento = gerarDocumento();
				
				retorno = "compras-licitacoesPregaoEletronicoBBPdf";
				
				this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.FALSE)));			
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	public String printLicitacaoCadastrada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException, DocumentException, IOException, JRException {
		
		String retorno = null; 
		try {
			List<ScoLicitacaoCadastradaImpressaoVO> dados = solicitacaoPregaoEletronicoBBLicitacaoCadastrada(numPac, nomeArquivoRetorno);
			dadosRelatorio = dados;			
			
			if(dadosRelatorio == null || dadosRelatorio.isEmpty()) {
				exibirMensagemPesquisaSemDados();			
			} else {			
				DocumentoJasper documento = gerarDocumento();
				
				retorno = "compras-licitacoesPregaoEletronicoBBPdf";
				
				this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.FALSE)));			
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;	
	}

	/**
	 * Renderiza o PDF
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 * @throws BaseException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * 
	 */
	/**
	 * Realiza a impressão direta do relatório
	 */
	public void impressaoDireta(){	
		try {
			if(cameFrom.equals(IMPRIMIR_HISTORICO_CADASTRADA_LICITACAO)){
				if(printLicitacaoCadastrada(numeroPac, nomeArquivo) != null) {
					DocumentoJasper documento = gerarDocumento();
					this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
					apresentarMsgNegocio(Severity.INFO, EnumTargetRotinaDiariaMateriais.MENSAGEM_SUCESSO_IMPRESSAO.toString());
				}
			}else{
				if(print(numeroPac, nomeArquivo) != null) {
					DocumentoJasper documento = gerarDocumento();
					this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
					apresentarMsgNegocio(Severity.INFO, EnumTargetRotinaDiariaMateriais.MENSAGEM_SUCESSO_IMPRESSAO.toString());
				}				
			}							
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	
	/**
	 * Adiciona msg 
	 */
	private void exibirMensagemPesquisaSemDados(){		
		apresentarMsgNegocio(RotinaImprimirSolicitacaoPregaoEletronicoBBControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
	}
	
	
	/**
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ParseException
	 */
	private List<ScoLicitacaoImpressaoVO> solicitacaoPregaoEletronicoBB(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException{
		List<ScoLicitacaoImpressaoVO> dados = new ArrayList<ScoLicitacaoImpressaoVO>();	
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		
			dados = comprasFacade.imprimirHistoricoDetalhadoLicitacaoHomologada(numPac, nomeArquivoRetorno);		
			titlePdfView = recuperarTituloRelatorio();
			setNomeArquivoRelatorio("");
			
			if (cameFrom.equals(IMPRIMIR_HISTORICO_RESUMIDO_LICITACAO)){
				setNomeArquivoRelatorio("br/gov/mec/aghu/compras/compras/imprimirHistoricoResumidoLicitacao.jasper");
			}else if (cameFrom.equals(IMPRIMIR_HISTORICO_DETALHADO_LICITACAO)) {
				setNomeArquivoRelatorio("br/gov/mec/aghu/compras/compras/imprimirHistoricoDetalhadoLicitacao.jasper");
			}
			
			params.put(SUBREPORT_DIR, "br/gov/mec/aghu/compras/compras/");
			setParametrosEspecificos(params);

			return dados;
	}
	
	private List<ScoLicitacaoCadastradaImpressaoVO> solicitacaoPregaoEletronicoBBLicitacaoCadastrada(Integer numPac, String nomeArquivoRetorno) throws ApplicationBusinessException{
		List<ScoLicitacaoCadastradaImpressaoVO> dados = new ArrayList<ScoLicitacaoCadastradaImpressaoVO>();	
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		
			dados = comprasFacade.imprimirLicitacaoCadastrada(numPac, nomeArquivoRetorno);		
			titlePdfView = recuperarTituloRelatorio();
			setNomeArquivoRelatorio("");			
			setNomeArquivoRelatorio("br/gov/mec/aghu/compras/compras/imprimirLicitacaoCadastrada.jasper");
			
			
			params.put(SUBREPORT_DIR, "br/gov/mec/aghu/compras/compras/");
			setParametrosEspecificos(params);

			return dados;
	}
	
	
	private String recuperarTituloRelatorio() {
		String titulo = "";
		if (cameFrom.equals(IMPRIMIR_HISTORICO_RESUMIDO_LICITACAO)){
			titulo = WebUtil.initLocalizedMessage(EnumTargetImprimirSolicitacaoPregaoEletronicoBB.TITLE_RELATORIO_SOLICITACAO_PREGAO_ELETRONICO_BB_RESUMIDA.toString(),
					null, null);			
		}else if (cameFrom.equals(IMPRIMIR_HISTORICO_DETALHADO_LICITACAO)){
			titulo = WebUtil.initLocalizedMessage(EnumTargetImprimirSolicitacaoPregaoEletronicoBB.TITLE_RELATORIO_SOLICITACAO_PREGAO_ELETRONICO_BB_DETALHADA.toString(),
					null, null);
		}else if(cameFrom.equals(IMPRIMIR_HISTORICO_CADASTRADA_LICITACAO)){
			titulo = WebUtil.initLocalizedMessage(EnumTargetImprimirSolicitacaoPregaoEletronicoBB.TITLE_RELATORIO_SOLICITACAO_PREGAO_ELETRONICO_BB_CADASTRADA.toString(),
					null, null);
		}
		return titulo;
	}
	

	public DominioNomeRelatorio getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(DominioNomeRelatorio relatorio) {
		this.relatorio = relatorio;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public Map<String, Object> getParametrosEspecificos() {
		return parametrosEspecificos;
	}

	public void setParametrosEspecificos(Map<String, Object> parametrosEspecificos) {
		this.parametrosEspecificos = parametrosEspecificos;
	}

	public String getNomeArquivoRelatorio() {
		return nomeArquivoRelatorio;
	}

	public void setNomeArquivoRelatorio(String nomeArquivoRelatorio) {
		this.nomeArquivoRelatorio = nomeArquivoRelatorio;
	}

	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<?> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}
}
