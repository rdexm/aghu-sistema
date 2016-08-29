package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioMateriaisRecebidosNoDiaVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioMateriaisRecebidosNoDiaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 7932450874969527042L;

	private static final String RELATORIO_MATERIAIS_RECEBIDOS_NO_DIA = "relatorioMateriaisRecebidosNoDia";
	private static final String RELATORIO_MATERIAIS_RECEBIDOS_NO_DIA_PDF = "relatorioMateriaisRecebidosNoDiaPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB 
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private Date dtInicial;
	private Date dtFinal;
	private AghUnidadesFuncionais unidadeExecutora;

	
	// Dados que serão impressos em PDF.
	private List<RelatorioMateriaisRecebidosNoDiaVO> colecao = new ArrayList<RelatorioMateriaisRecebidosNoDiaVO>();
		
	/**
	 * Nome do arquivo JASPER que será utilizado na geração do PDF.
	 */
	private String relatorio = null;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Impressao direta via CUPS.
	 * @throws DocumentException 
	 */
	public void impressaoDireta() throws BaseException, JRException, SystemException, IOException, ParseException, DocumentException {
		this.print();

		this.relatorio = "/br/gov/mec/aghu/exames/report/relatorioMateriasRecebidosNoDia.jasper";

		try {
			if(!colecao.isEmpty()){
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			}
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String voltar(){
		return RELATORIO_MATERIAIS_RECEBIDOS_NO_DIA;
	}

	/**
	 * Busca os dados para geração do relatório.
	 * @throws ApplicationBusinessException 
	 */
	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
		
		// Recupera coleção.
		try {
			this.colecao = this.examesFacade.pesquisarMateriaisRecebidosNoDia(this.unidadeExecutora.getSeq(), this.dtInicial, this.dtFinal);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);				
		return null;
		}
		this.relatorio = "/br/gov/mec/aghu/exames/report/relatorioMateriasRecebidosNoDia.jasper";	
		DocumentoJasper documento = null;
		documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_MATERIAIS_RECEBIDOS_NO_DIA_PDF;

	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		
		DocumentoJasper documento = null;
	
		this.relatorio = "/br/gov/mec/aghu/exames/report/relatorioMateriasRecebidosNoDia.jasper";
		documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.action.MECRelatorioController#recuperarParametros()
	 */
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeRelatorio", "AELR_MAT_RECEBIDOS");
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

		params.put("unidadeExecutora", this.unidadeExecutora.getDescricao());
		params.put("dataInicial", DateFormatUtil.obterDataFormatada(dtInicial, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("dataFinal", DateFormatUtil.obterDataFormatada(dtFinal, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));

		String hospital = aghuFacade.getRazaoSocial();
		params.put("hospitalLocal", hospital);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return this.relatorio;
	}

	@Override
	public Collection<RelatorioMateriaisRecebidosNoDiaVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutoraExames(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasExecutoraColeta(objPesquisa);
	}
	
	public void limparCampos(){
        unidadeExecutora = null;
        dtInicial = null;
        dtFinal = null;
	}
	
	/*
	 * GET's e SET's.
	 */
	
	public List<RelatorioMateriaisRecebidosNoDiaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioMateriaisRecebidosNoDiaVO> colecao) {
		this.colecao = colecao;
	}
	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}
	
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(final AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}
}