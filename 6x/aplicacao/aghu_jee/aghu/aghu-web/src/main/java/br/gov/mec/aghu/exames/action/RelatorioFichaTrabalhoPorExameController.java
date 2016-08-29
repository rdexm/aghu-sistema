package br.gov.mec.aghu.exames.action;

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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaClinicaVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioFichaTrabalhoPorExameController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final long serialVersionUID = 1585906080710567133L;

	private static final String RELATORIO_FICHA_TRABALHO_POR_EXAME = "relatorioFichaTrabalhoPorExame";
	private static final String RELATORIO_FICHA_TRABALHO_POR_EXAME_PDF = "relatorioFichaTrabalhoPorExamePdf";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private Integer soeSeq;
	private Short amostra;
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private Boolean receberAmostra = false;
	
	private boolean sair = false;
	
	private List<RelatorioFichaTrabalhoPatologiaClinicaVO> colecao = new ArrayList<RelatorioFichaTrabalhoPatologiaClinicaVO>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		if(!sair) {
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				usuarioUnidadeExecutora=null;
			}
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
		}
	
	}

	public void limparPesquisa() {
		if (this.usuarioUnidadeExecutora != null) {
			// Reseta a unidade executora associada ao usuario
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}
		soeSeq = null;
		amostra = null;
		sair = true;
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar(){
		return RELATORIO_FICHA_TRABALHO_POR_EXAME;
	}
	
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_FICHA_TRABALHO_POR_EXAME_PDF;
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), unidadeExecutora, TipoDocumentoImpressao.FICHA_TRABALHO);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
		
	}

	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("nomeHospital", hospital);
		params.put("unidadeFuncional", unidadeExecutora.getDescricao());
		params.put("nomeRelatorio", "AELR_FICHA_TRAB_LAB");

		return params;
	}

	@Override
	public Collection<RelatorioFichaTrabalhoPatologiaClinicaVO> recuperarColecao() throws ApplicationBusinessException {
		colecao = new ArrayList<RelatorioFichaTrabalhoPatologiaClinicaVO>();
		colecao.addAll(this.examesFacade.obterFichaTrabPorExame(soeSeq, amostra, receberAmostra, unidadeExecutora.getSeq()));
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioFichaTrabLab.jasper";
	}

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String objPesquisa){
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getAmostra() {
		return amostra;
	}

	public void setAmostra(Short amostra) {
		this.amostra = amostra;
	}

	public List<RelatorioFichaTrabalhoPatologiaClinicaVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			List<RelatorioFichaTrabalhoPatologiaClinicaVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getReceberAmostra() {
		return receberAmostra;
	}

	public void setReceberAmostra(Boolean receberAmostra) {
		this.receberAmostra = receberAmostra;
	}
}
