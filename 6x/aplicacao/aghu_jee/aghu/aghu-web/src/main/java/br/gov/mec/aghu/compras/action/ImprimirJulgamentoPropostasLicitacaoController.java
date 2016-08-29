package br.gov.mec.aghu.compras.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.JulgamentoPropostasLicitacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;


public class ImprimirJulgamentoPropostasLicitacaoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 5057885640864865396L;

	private static final String IMPRIMIR_JULGAMENTO_PROPOSTAS_LICITACAO     = "imprimirJulgamentoPropostasLicitacao";
	private static final String IMPRIMIR_JULGAMENTO_PROPOSTAS_LICITACAO_PDF = "imprimirJulgamentoPropostasLicitacaoPdf";
	
	@EJB
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
		
	@EJB
	private IComprasFacade comprasFacade;

	private Integer nroLicitacao;
	private Integer itemInicial;
	private Integer itemFinal;
	
	private List<JulgamentoPropostasLicitacaoVO> colecao = new ArrayList<JulgamentoPropostasLicitacaoVO>();
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
//	public List<JulgamentoPropostasLicitacaoVO> gerarRelatorio(){
//		return comprasFacade.gerarRelatorioJulgamentoPropostasLicitacao(this.nroLicitacao, this.itemInicial, this.itemFinal);
//	}
	
	public void limpar(){
		nroLicitacao = null;
		itemInicial = null;
		itemFinal = null;
	}

	
	public String print()throws BaseException, JRException, SystemException, IOException, DocumentException {
		colecao = comprasFacade.gerarRelatorioJulgamentoPropostasLicitacao(this.nroLicitacao, this.itemInicial, this.itemFinal);
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return IMPRIMIR_JULGAMENTO_PROPOSTAS_LICITACAO_PDF;
	}
	
	public String voltar() {
		return IMPRIMIR_JULGAMENTO_PROPOSTAS_LICITACAO;
	}
		
	@Override
	public Collection<JulgamentoPropostasLicitacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;		
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeHospital", hospital);
		params.put("tituloRelatorio", "Quadro de Julgamento de Propostas");
		
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/ImprimirJulgamentoPropostaLicitacao.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			colecao = comprasFacade.gerarRelatorioJulgamentoPropostasLicitacao(this.nroLicitacao, this.itemInicial, this.itemFinal);		
			
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public Integer getNroLicitacao() {
		return nroLicitacao;
	}

	public void setNroLicitacao(Integer nroLicitacao) {
		this.nroLicitacao = nroLicitacao;
	}

	public Integer getItemInicial() {
		return itemInicial;
	}

	public void setItemInicial(Integer itemInicial) {
		this.itemInicial = itemInicial;
	}

	public Integer getItemFinal() {
		return itemFinal;
	}

	public void setItemFinal(Integer itemFinal) {
		this.itemFinal = itemFinal;
	}
}