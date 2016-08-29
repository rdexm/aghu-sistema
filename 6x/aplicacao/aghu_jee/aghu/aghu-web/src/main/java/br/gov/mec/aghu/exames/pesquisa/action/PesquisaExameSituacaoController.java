package br.gov.mec.aghu.exames.pesquisa.action;

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
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExameSituacaoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import net.sf.jasperreports.engine.JRException;


public class PesquisaExameSituacaoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 1350008746440561272L;

	private static final Log LOG = LogFactory.getLog(PesquisaExameSituacaoController.class);

	private static final String PESQUISA_EXAME_SITUACAO = "exames-pesquisaExameSituacao";

	private static final String RELATORIO_EXAME_SITUACAO_PDF = "exames-relatorioExameSituacaoPdf";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController; 
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	
	private List<PesquisaExameSituacaoVO> colecao = new ArrayList<PesquisaExameSituacaoVO>(0);
	private String exame;
	private AelSitItemSolicitacoes situacao;
	private Date dataReferenciaIni;
	private Date dataReferenciaFim;
	private Date dataProgramada;
	private DominioOrigemAtendimento origemAtendimento;
	private DominioConvenioExameSituacao tipoConvenio;
	private DominoOrigemMapaAmostraItemExame origemMapaTrabalho;
	private VAelExamesSolicitacao nomeExame;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<PesquisaExameSituacaoVO> recuperarColecao() throws ApplicationBusinessException {
		colecao = pesquisaExamesFacade.pesquisaExameSolicitacaoPacAtendRel(unidadeExecutoraController.getUnidadeExecutora(), 
																		   dataReferenciaIni, dataReferenciaFim, dataProgramada, tipoConvenio, 
																		   situacao, nomeExame, origemAtendimento, origemMapaTrabalho);
		return this.colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");


		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		String sit = situacao.getDescricao()!=null?situacao.getDescricao():"";
		
		params.put("exame", exame);
		if (dataReferenciaIni != null) {
			params.put("dataReferenciaIni", sdf_1.format(dataReferenciaIni));
		}
		if (dataReferenciaFim != null) {
			params.put("dataReferenciaFim", sdf_1.format(dataReferenciaFim));
		}
		if (dataProgramada != null) {
			params.put("dataProgramada", sdf_1.format(dataProgramada));
		}
		
		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", "Exames na situação "+ sit+ " da "+ unidadeExecutoraController.getUnidadeExecutora().getDescricao()
				+ " - "+ unidadeExecutoraController.getUnidadeExecutora().getSeq().toString()); 
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeRelatorio", "AELR_PESQ_EXAME_SIT_2");
		params.put("totalRegistros", colecao.size()-1);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioExamesPorSituacao.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public String voltar(){
		return PESQUISA_EXAME_SITUACAO;
	}

	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_EXAME_SITUACAO_PDF;
	}
	
	public List<PesquisaExameSituacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<PesquisaExameSituacaoVO> colecao) {
		this.colecao = colecao;
	}

	public IdentificarUnidadeExecutoraController getUnidadeExecutoraController() {
		return unidadeExecutoraController;
	}

	public void setUnidadeExecutoraController(
			IdentificarUnidadeExecutoraController unidadeExecutoraController) {
		this.unidadeExecutoraController = unidadeExecutoraController;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public Date getDataReferenciaIni() {
		return dataReferenciaIni;
	}

	public void setDataReferenciaIni(Date dataReferenciaIni) {
		this.dataReferenciaIni = dataReferenciaIni;
	}

	public Date getDataReferenciaFim() {
		return dataReferenciaFim;
	}

	public void setDataReferenciaFim(Date dataReferenciaFim) {
		this.dataReferenciaFim = dataReferenciaFim;
	}

	public Date getDataProgramada() {
		return dataProgramada;
	}

	public void setDataProgramada(Date dataProgramada) {
		this.dataProgramada = dataProgramada;
	}

	public DominioOrigemAtendimento getOrigemAtendimento() {
		return origemAtendimento;
	}

	public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}

	public DominioConvenioExameSituacao getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(DominioConvenioExameSituacao tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public DominoOrigemMapaAmostraItemExame getOrigemMapaTrabalho() {
		return origemMapaTrabalho;
	}

	public void setOrigemMapaTrabalho(
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho) {
		this.origemMapaTrabalho = origemMapaTrabalho;
	}

	public VAelExamesSolicitacao getNomeExame() {
		return nomeExame;
	}

	public void setNomeExame(VAelExamesSolicitacao nomeExame) {
		this.nomeExame = nomeExame;
	}
}