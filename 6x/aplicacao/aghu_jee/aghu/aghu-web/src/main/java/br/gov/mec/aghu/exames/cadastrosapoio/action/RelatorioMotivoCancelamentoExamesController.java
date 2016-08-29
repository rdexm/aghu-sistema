package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.AelMotivoCancelaExamesVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import net.sf.jasperreports.engine.JRException;

public class RelatorioMotivoCancelamentoExamesController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 2498409362861699085L;
	
	public final static String ARQUIVO_JASPER = "br/gov/mec/aghu/exames/report/relatorioMotivoCancelamentoExames.jasper";

	private static final String MOTIVO_CANCELAMENTO_LIST = "exames-motivoCancelamentoList";
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	private AelMotivoCancelaExames motivoCancelaExamesFiltro;
	
	/*
	 * Dados que serão impressos em PDF.
	 */
	List<AelMotivoCancelaExamesVO> colecao = new LinkedList<AelMotivoCancelaExamesVO>();
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Collection<AelMotivoCancelaExamesVO> recuperarColecao() throws ApplicationBusinessException {
		this.colecao = this.examesFacade.listarMotivoCancelamentoExamesAtivos(this.motivoCancelaExamesFiltro);
		return this.colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		// Instancia mapa de parametros do relatorio
		Map<String, Object> params = new HashMap<String, Object>();
		
		// Popula parametro da data atual
		Date dataAtual = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		// Popula parametro com o nome da instituicao hospitalar
		String hospital = this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		// Seta parametros
		params.put("dataAtual", simpleDateFormat.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", DominioNomeRelatorio.AELR_MOTIVOS_CANC.toString());
		params.put("descricaoRelatorio", DominioNomeRelatorio.AELR_MOTIVOS_CANC.getDescricao());
		params.put("tituloRelatorio", super.getBundle().getObject("TITLE_RELATORIO_MOTIVO_CANCELAMENTO_EXAMES"));
	
		return params;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return ARQUIVO_JASPER;
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf()throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	// TODO como fica isso
//	@Restrict("#{s:hasPermission('relatorioMotivosCancelamentoExames','imprimir')}")
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String voltar(){
		return MOTIVO_CANCELAMENTO_LIST;
	}

	public List<AelMotivoCancelaExamesVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<AelMotivoCancelaExamesVO> colecao) {
		this.colecao = colecao;
	}

	public AelMotivoCancelaExames getMotivoCancelaExamesFiltro() {
		return motivoCancelaExamesFiltro;
	}

	public void setMotivoCancelaExamesFiltro(
			AelMotivoCancelaExames motivoCancelaExamesFiltro) {
		this.motivoCancelaExamesFiltro = motivoCancelaExamesFiltro;
	}
}