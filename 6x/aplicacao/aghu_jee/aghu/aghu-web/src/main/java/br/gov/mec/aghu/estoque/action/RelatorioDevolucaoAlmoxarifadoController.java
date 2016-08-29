package br.gov.mec.aghu.estoque.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.action.RelatorioMateriaisValidadeVencidaController.EnumRelatorioMateriaisValidadeVencidaControllerMessageCode;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioDevolucaoAlmoxarifadoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioDevolucaoAlmoxarifadoController.class);
	
	private static final long serialVersionUID = -4005773789501382636L;

	private static final String RELATORIO_DEVOLUCAO_ALMOXARIFADO     = "relatorioDevolucaoAlmoxarifado";
	private static final String RELATORIO_DEVOLUCAO_ALMOXARIFADO_PDF = "relatorioDevolucaoAlmoxarifadoPdf";
	private static final String IMPRIMIR_DEVOLUCAO_ALMOXARIFADO_SEQ_ = "Imprimir_Devolucao_Almoxarifado_SEQ_";


	public enum EnumTargetRelatorioDevolucaoAlmoxarifado{
		MENSAGEM_SUCESSO_IMPRESSAO;
	}
	
	public enum EnumMessagesRelatorioDevolucaoAlmoxarifado{
		RELATORIO_DEVOLUCAO_ALMOXARIFADO_RODAPE;
	}
	
	private enum RelatorioDevolucaoAlmoxarifadoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
	
	// Variaveis da pesquisa
	private Integer numeroDevolAlmox;
	
	//indica se houve geração do arquivo do relatório
	private String fileName;
	private Boolean gerouArquivo;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	// Parametros do relatório
	private RelatorioDevolucaoAlmoxarifadoVO grupoPrincipal;
	private List<RelatorioDevolucaoAlmoxarifadoVO> dados;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}


	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/estoque/report/relatorioDevolucaoAlmoxarifado.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatóro
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return getDados();
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeInstituicao", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());		
		params.put("nomeRelatorio", DominioNomeRelatorio.RELATORIO_DEVOLUCAO_ALMOXARIFADO.getDescricao());
		params.put("nomeRelatorioRodape", super.getBundle().getString(EnumMessagesRelatorioDevolucaoAlmoxarifado.RELATORIO_DEVOLUCAO_ALMOXARIFADO_RODAPE.toString()));
		
		if(getGrupoPrincipal() != null) {
			params.put("dtGeracao", getGrupoPrincipal().getDtGeracao());
			params.put("seq", getGrupoPrincipal().getSeq());
			params.put("almSeq", getGrupoPrincipal().getAlmSeq());
			params.put("centroCustoSeqDescricao", getGrupoPrincipal().getCentroCustoSeqDescricao());
			params.put("ramNroRamal", getGrupoPrincipal().getRccRamal());
			params.put("observacao", getGrupoPrincipal().getObservacao());
			params.put("valorTotal", getGrupoPrincipal().getValorTotal());
			params.put("nomePessoaServidor", getGrupoPrincipal().getNomePessoaServidor());
		}
		
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório
	 */
	public String print()throws JRException, IOException, DocumentException {
		String mensagem = null;
		try {
			setDados(estoqueFacade.gerarDadosRelatorioDevolucaoAlmoxarifado(getNumeroDevolAlmox()));
			if(getDados() != null && !getDados().isEmpty()){
				setGrupoPrincipal(getDados().get(0));			
			
		try {
			DocumentoJasper documento = gerarDocumento();
			if(documento != null) {
				media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return RELATORIO_DEVOLUCAO_ALMOXARIFADO_PDF;
			} else{
				mensagem = super.getBundle().getString(RelatorioDevolucaoAlmoxarifadoControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
			}		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, 
																SystemException, DocumentException {
		try {
			DocumentoJasper documento = gerarDocumento();
			if(documento != null) {
				return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	/**
	 * Gera o arquivo CSV.
	 */
	/*public void gerarCsv() {
		try {
			setDados(estoqueFacade.gerarDadosRelatorioDevolucaoAlmoxarifado(getNumeroDevolAlmox()));
			if(getDados() != null && !getDados().isEmpty()){
				setGrupoPrincipal(dados.get(0));			
			}
			Map<String, Object> parametros = recuperarParametros();
			setFileName(estoqueFacade.gerarCsvRelatorioDevolucaoAlmoxarifado(getNumeroDevolAlmox(), dados, parametros));
			setGerouArquivo(Boolean.TRUE);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
	}*/
	
	public void gerarArquivo() throws ApplicationBusinessException {
		
		try {
			setDados(estoqueFacade.gerarDadosRelatorioDevolucaoAlmoxarifado(getNumeroDevolAlmox()));
			if(getDados() != null && !getDados().isEmpty()){
				setGrupoPrincipal(dados.get(0));			
			}
			Map<String, Object> parametros = recuperarParametros();
			setFileName(estoqueFacade.gerarCsvRelatorioDevolucaoAlmoxarifado(getNumeroDevolAlmox(), dados, parametros));
			setGerouArquivo(Boolean.TRUE);
			this.dispararDownload();			
			
		} catch(IOException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Inicia o processo de download para o arquivo CSV
	 */
	public void dispararDownload(){ 
		if(StringUtils.isNotEmpty(getFileName())){
			try {
				download(getFileName(), IMPRIMIR_DEVOLUCAO_ALMOXARIFADO_SEQ_ + getNumeroDevolAlmox());
				setGerouArquivo(false);
				setFileName(null);				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}

	/**
	 * Envia o documento gerado para impressora.
	 */
	public void impressaoDireta(){
		try {
			if(print() != null){				
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, EnumRelatorioMateriaisValidadeVencidaControllerMessageCode.MENSAGEM_SUCESSO_IMPRESSAO.toString());
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	/**
	 * Inicilializa atributos com seus valores default.
	 */
	public void limparCampos(){
		setNumeroDevolAlmox(null);
		setDados(null);
		setGrupoPrincipal(null);
		setGerouArquivo(Boolean.FALSE);
	}
	
	public String voltar(){
		return RELATORIO_DEVOLUCAO_ALMOXARIFADO;
	}

	public Integer getNumeroDevolAlmox() {
		return numeroDevolAlmox;
	}
	
	public void setNumeroDevolAlmox(Integer NumeroDevolAlmox) {
		this.numeroDevolAlmox = NumeroDevolAlmox;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public RelatorioDevolucaoAlmoxarifadoVO getGrupoPrincipal() {
		return grupoPrincipal;
	}

	public void setGrupoPrincipal(RelatorioDevolucaoAlmoxarifadoVO grupoPrincipal) {
		this.grupoPrincipal = grupoPrincipal;
	}

	public List<RelatorioDevolucaoAlmoxarifadoVO> getDados() {
		return dados;
	}

	public void setDados(List<RelatorioDevolucaoAlmoxarifadoVO> dados) {
		this.dados = dados;
	}
}