package br.gov.mec.aghu.compras.contaspagar.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.PagamentosRealizadosPeriodoPdfVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class PagamentosRealizadosPeriodoController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -3905943195220654004L;

	//Logger
	private static final Log LOG = LogFactory.getLog(PagamentosRealizadosPeriodoController.class);
		
	//Constante da pagina responsavel pela visualização do relatório
	private static final String PAGE_RELATORIO_PAGAMENTOS_REALIZADOS_PERIODO_PDF = "relatorioPagamentosRealizadosPeriodoPdf";
	//Constante da página responsável pela pesquisa
	private static final String PAGE_RELATORIO_PAGAMENTOS_REALIZADOS_PERIODO = "relatorioPagamentosRealizadosPeriodo";
	//Constante referente a busca do nome do hospital
	private static final String NOME_HOSPITAL = "P_HOSPITAL_RAZAO_SOCIAL";	
		
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	@EJB
	private IComprasFacade comprasFacade;
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private FsoVerbaGestao fsoVerbaGestao;
	Date periodoInicial; 
	Date periodoFinal;
	
	//Coleção
	private List<PagamentosRealizadosPeriodoPdfVO> colecao = new ArrayList<PagamentosRealizadosPeriodoPdfVO>();
		
	/**
	 * Método que retorna o modelo do relatório
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/contaspagar/relatorioPagamentoRealizadoPeriodo.jasper";
	}
	
	@PostConstruct
	public void inicio() {
		begin(conversation);		
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws ApplicationBusinessException, JRException, SystemException, IOException, DocumentException {
	
		DocumentoJasper documento;
		
		try {
			documento = gerarDocumento(true);
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	 	return "relatorio";
	}

	/**
	 * Método responsável pela impressão.
	 */
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Metódo por gerar as dependências a impressão
	 * @return
	 */
	public String imprimirRelatorio() {
		try {
			pesquisar();
			directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}
	
	/**
	 * Método responsável pela visualização do relatório
	 * @return página de referência do pdf
	 * @throws BaseException
	 */
	public String visualizarRelatorio() throws BaseException {			
		String retorno = PAGE_RELATORIO_PAGAMENTOS_REALIZADOS_PERIODO_PDF;
		try {
			pesquisar();
		} catch (ApplicationBusinessException e) {
			retorno = PAGE_RELATORIO_PAGAMENTOS_REALIZADOS_PERIODO;
			apresentarMsgNegocio(Severity.ERROR, e.getMessage(), e.getParameters());
		}	
		return retorno;
	}
	
	/**
	 * Método que retorna a coleção de objetos
	 * @throws BaseException
	 */
	public void pesquisar() throws BaseException {
		Integer numeroVerbaGestao = null;
		if(this.fsoVerbaGestao != null){
			numeroVerbaGestao = this.fsoVerbaGestao.getSeq();
		}
		this.colecao = comprasFacade.pesquisarPagamentosRealizadosPeriodoPDF(periodoInicial, periodoFinal, numeroVerbaGestao);
	}
	
	public List<FsoVerbaGestao> pesquisarFsoVerbaGestao(final String pesquisa) {
		return this.returnSGWithCount(this.cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(pesquisa),pesquisarFsoVerbaGestaoCount(pesquisa));
	}
	
	public Long pesquisarFsoVerbaGestaoCount(final String pesquisa) {
		Long size = (long) this.pesquisarFsoVerbaGestao(pesquisa).size();
		return size;
	}
	
	/**
	 * Método responsável por limpar a tela de pesquisa
	 */
	public String limpar() {
		this.setPeriodoFinal(null);
		this.setPeriodoInicial(null);
		this.setFsoVerbaGestao(null);
		this.colecao = null;
		
		return PAGE_RELATORIO_PAGAMENTOS_REALIZADOS_PERIODO;
	}
	
	/**
	 * Método responsável por retornar a página de pesquisa
	 * @return página de pesquisa
	 */
	public String voltar() {
		return PAGE_RELATORIO_PAGAMENTOS_REALIZADOS_PERIODO;
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	@Override
	 public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {
		DocumentoJasper documento;
		
		try {
			documento = gerarDocumento(true);
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	 }
	
	/**
	 * Método responsável por fornecer os dados do header do relatório
	 */
	@Override
	 public Map<String, Object> recuperarParametros() {
		  Map<String, Object> params = new HashMap<String, Object>();
		  
		  String hospital = comprasFacade.pesquisarHospital(NOME_HOSPITAL).getVlrTexto();
		  params.put("hospitalLocal", hospital);		  
		  params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));  
		  params.put("dataInicial", DateUtil.obterDataFormatada(periodoInicial, "dd/MM/yyyy"));
		  params.put("dataFinal", DateUtil.obterDataFormatada(periodoFinal, "dd/MM/yyyy"));
		  params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/contaspagar/");
		  
		  return params;
	 }

	@Override
	public Collection<PagamentosRealizadosPeriodoPdfVO> recuperarColecao()
			throws ApplicationBusinessException {
		return this.getColecao();
	}
	
	public List<PagamentosRealizadosPeriodoPdfVO> getColecao() {
		return colecao;
	}

	public Date getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(Date periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Date getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(Date periodoFinal) {
		this.periodoFinal = periodoFinal;
	}
	
	public FsoVerbaGestao getFsoVerbaGestao() {
		return fsoVerbaGestao;
	}

	public void setFsoVerbaGestao(FsoVerbaGestao fsoVerbaGestao) {
		this.fsoVerbaGestao = fsoVerbaGestao;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}
}
