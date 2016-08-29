package br.gov.mec.aghu.exames.agendamento.action;

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
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioFichaTrabalhoPatologiaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 1585906080710567133L;

	private static final String RELATORIO_FICHA_TRABALHO_PATOLOGIA = "relatorioFichaTrabalhoPatologia";
	private static final String RELATORIO_FICHA_TRABALHO_PATOLOGIA_PDF = "relatorioFichaTrabalhoPatologiaPdf";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private Integer soeSeq;
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	
	private boolean sair = false;
	
	private List<RelatorioFichaTrabalhoPatologiaVO> colecao = new ArrayList<RelatorioFichaTrabalhoPatologiaVO>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(!sair) {
			try {
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(servidorLogado.getId());
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
		return RELATORIO_FICHA_TRABALHO_PATOLOGIA;
	}
	
	public String print()throws  BaseException, JRException, SystemException, IOException, DocumentException {
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_FICHA_TRABALHO_PATOLOGIA_PDF;
	}
	
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

	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subReport = "br/gov/mec/aghu/exames/report/relatorioFichaTrabPat_Exames.jasper";
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("subRelatorio", Thread.currentThread().getContextClassLoader().getResourceAsStream(subReport));
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("nomeHospital", hospital);
		params.put("unidadeFuncional", unidadeExecutora.getDescricao());
		params.put("nomeRelatorio", "AELR_FICHA_TRAB_PAT");

		return params;
	}

	@Override
	public Collection<RelatorioFichaTrabalhoPatologiaVO> recuperarColecao() throws ApplicationBusinessException {
		colecao = new ArrayList<RelatorioFichaTrabalhoPatologiaVO>();
		colecao.add(this.examesFacade.obterSolicitacaoAtendimento(soeSeq, unidadeExecutora.getSeq()));

		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioFichaTrabPat.jasper";
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
}