package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuario.vo.InformacoesPerinataisVO;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;



public class InformacoesPerinataisPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8764119058026431909L;
	
	private static final String PAGE_VISUALIZAR_DOC_CERTIF="pol-visualizarDocumentoCertificado";
	private static final String PAGE_SUMARIO_ALTA_PDF="pol-relatorioSumarioAltaObitoPdf";
	private static final String VISUALIZAR_RELATORIO_SUMARIO_ASSISTENCIA_PARTO = "relatorioSumarioAssistenciaPartoPdf";
	private static final String VOLTAR_INFORMACOES_PERINATAIS = "informacoesPerinataisListPOL";
	private static final String VISUALIZAR_RELATORIO_RECEM_NASCIDO = "relatorioAtendimentoRecemNascidoPdf";
	private static final String VISUALIZAR_RELATORIO_EXAME_FISICO_RN_INF_PERINATAIS = "relatorioExameFisicoRNPdf";
	private static final String INTERNACAO = "INT";
	
	private Boolean acessoAdminPOL;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;

	private Boolean habilitarBotaoParto;
	private Boolean habilitarBotaoNascimento;
	private Boolean habilitarBotaoExameFisico;
	private Boolean habilitarBotaoSumarioAlta;
	private Boolean habilitarBotaoSumarioAltaSemInt;
	private InformacoesPerinataisVO registroSelecionado;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private ConsultarInternacoesPOLController consultarInternacoesPOLController;
	
	@Inject
	private SecurityController securityController;

	@Inject
	private RelatorioAtendimentoRNController relatorioAtendimentoRNController;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;
	
	@Inject @Paginator
	private DynamicDataModel<InformacoesPerinataisVO> dataModel;


	private AipPacientes paciente;	
	private String indImpPrevia;
	private InternacaoVO internacao;
	private List<InternacaoVO> internacoes;
	
	private Integer gsoPacCodigo;
	private Short gsoSeqp;	
	private Integer conNumero;	
	private Integer atdSeq;
	private Byte rnaSeqp;
	
		
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		acessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
		internacoes = new ArrayList<InternacaoVO>();
	}
	
	
	public void inicio() {
		if(registroSelecionado != null){
			return;
		}
		
		setPaciente(pacienteFacade.pesquisarPacientePorProntuario(itemPOL.getProntuario()));
		setHabilitarBotaoParto(false);
		setHabilitarBotaoExameFisico(false);
		setHabilitarBotaoNascimento(false);
		setHabilitarBotaoSumarioAlta(false);
		
			
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {		
		return prontuarioOnlineFacade.pesquisarInformacoesPerinataisCodigoPacienteCount(getPaciente().getCodigo()).longValue();		
	}

	@Override
	public List<InformacoesPerinataisVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,boolean asc) {
		return prontuarioOnlineFacade.pesquisarInformacoesPerinataisCodigoPaciente(getPaciente().getCodigo(), firstResult, maxResult, orderProperty,asc);
	}

	public void selecionarRegistro() {		
		gsoPacCodigo = registroSelecionado.getGsoPacCodigo();
		gsoSeqp = registroSelecionado.getGsoSeqp();
		conNumero = registroSelecionado.getConNumero();
		atdSeq = registroSelecionado.getAtdSeq();
		rnaSeqp = registroSelecionado.getRnaSeqp();
		internacao = null;
		
		setHabilitarBotaoParto(prontuarioOnlineFacade.habilitarBotaoParto(registroSelecionado.getGsoPacCodigo(), registroSelecionado.getGsoSeqp()));
		setHabilitarBotaoNascimento(prontuarioOnlineFacade.habilitarBotaoNascimento(registroSelecionado.getGsoPacCodigo(), registroSelecionado.getGsoSeqp()));
		setHabilitarBotaoExameFisico(prontuarioOnlineFacade.habilitarBotaoExameFisico(null, registroSelecionado.getGsoPacCodigo(), registroSelecionado.getGsoSeqp()));
		setHabilitarBotaoSumarioAlta(prontuarioOnlineFacade.habilitarBotaoSumarioAlta(registroSelecionado));
		
		McoAnamneseEfs anamneseEfs = emergenciaFacade.obterAnamnesePorPaciente(registroSelecionado.getGsoPacCodigo(),
				registroSelecionado.getGsoSeqp());

		if ( anamneseEfs != null && anamneseEfs.getConsulta() != null ){		
			
			conNumero = anamneseEfs.getConsulta().getNumero();
			
			if (getHabilitarBotaoSumarioAlta()){
				try {
					AinInternacao ainInternacao = internacaoFacade.obterAinInternacaoPorAtdSeq(registroSelecionado.getAtdSeq());
					
					if(ainInternacao != null){
						int index = internacoes.indexOf(new InternacaoVO(ainInternacao.getSeq())); 
						if(index >= 0){
							internacao = internacoes.get(index);
						} else {
							internacao = prontuarioOnlineFacade.obterInternacao(ainInternacao.getSeq(), INTERNACAO);
							internacoes.add(internacao);
						}
					}

				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}		
		
		if ( !verificarPermissaoAcesso() ){
			setHabilitarBotaoParto(false);
			setHabilitarBotaoNascimento(false);
			setHabilitarBotaoExameFisico(false);
			setHabilitarBotaoSumarioAlta(false);
		}
	}
	
	private boolean verificarPermissaoAcesso(){
		if (acessoAdminPOL){
			return false;
		}
		return true;
	}
	
	public String getTitleDocAssinado(){
		if(internacao.getObito().equals("S")){
			return getBundle().getString("TITLE_SUMARIO_OBITO");
		}else{
			return getBundle().getString("TITLE_SUMARIO_ALTA");
		}
	}
	
	public String abrirVisualizarDocumentoCertificado(){
		return PAGE_VISUALIZAR_DOC_CERTIF;
	}

	public String abrirSumarioAltaPDF(){
		return PAGE_SUMARIO_ALTA_PDF;
	}
	
	public String abrirRelatorioParto() {		
		return VISUALIZAR_RELATORIO_SUMARIO_ASSISTENCIA_PARTO;
	}
	
	public String abrirRelatorioNascimento() throws ApplicationBusinessException {		
		relatorioAtendimentoRNController.setPacCodigo(getGsoPacCodigo());
		relatorioAtendimentoRNController.setConNumero(getConNumero());
		relatorioAtendimentoRNController.setGsoSeqp(getGsoSeqp());
		relatorioAtendimentoRNController.setAtdSeq(getAtdSeq());
		relatorioAtendimentoRNController.setRnaSeqp(getRnaSeqp());
		relatorioAtendimentoRNController.gerarDados();
		relatorioAtendimentoRNController.setOrigem(VOLTAR_INFORMACOES_PERINATAIS);
		return VISUALIZAR_RELATORIO_RECEM_NASCIDO;
	}
	
	public String abrirRelatorioExameFisico() {
		return VISUALIZAR_RELATORIO_EXAME_FISICO_RN_INF_PERINATAIS;
	}	
		
	public Boolean habilitarBotaoAlta() {
		return habilitarBotaoPadrao() && 
					(internacao.getPossuiDocumentoAssinado() || 
						internacao.getPossuiDocumentoPendente() || 
							internacao.getPossuiRelatorioAltaObito() );
	}
	
	public Boolean habilitarBotaoPadrao() {
		if (internacao != null && internacao.getSeq() != null && !acessoAdminPOL) {
			return Boolean.TRUE;
		}
		return false;
	}
	
	public InformacoesPerinataisVO getRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(
			InformacoesPerinataisVO registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public ConsultarInternacoesPOLController getConsultarInternacoesPOLController() {
		return consultarInternacoesPOLController;
	}

	public void setConsultarInternacoesPOLController(
			ConsultarInternacoesPOLController consultarInternacoesPOLController) {
		this.consultarInternacoesPOLController = consultarInternacoesPOLController;
	}

	public Boolean getHabilitarBotaoParto() {
		return habilitarBotaoParto;
	}

	public void setHabilitarBotaoParto(Boolean habilitarBotaoParto) {
		this.habilitarBotaoParto = habilitarBotaoParto;
	}

	public Boolean getHabilitarBotaoNascimento() {
		return habilitarBotaoNascimento;
	}

	public void setHabilitarBotaoNascimento(Boolean habilitarBotaoNascimento) {
		this.habilitarBotaoNascimento = habilitarBotaoNascimento;
	}

	public Boolean getHabilitarBotaoExameFisico() {
		return habilitarBotaoExameFisico;
	}

	public void setHabilitarBotaoExameFisico(Boolean habilitarBotaoExameFisico) {
		this.habilitarBotaoExameFisico = habilitarBotaoExameFisico;
	}

	public Boolean getHabilitarBotaoSumarioAlta() {
		return habilitarBotaoSumarioAlta;
	}

	public void setHabilitarBotaoSumarioAlta(Boolean habilitarBotaoSumarioAlta) {
		this.habilitarBotaoSumarioAlta = habilitarBotaoSumarioAlta;
	}

	public void setIndImpPrevia(String indImpPrevia) {
		this.indImpPrevia = indImpPrevia;
	}

	public String getIndImpPrevia() {
		return indImpPrevia;
	}	 


	public DynamicDataModel<InformacoesPerinataisVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<InformacoesPerinataisVO> dataModel) {
	 this.dataModel = dataModel;
	}


	public InternacaoVO getInternacao() {
		return internacao;
	}

	public void setInternacao(InternacaoVO internacao) {
		this.internacao = internacao;
	}
	
	public Boolean getHabilitarBotaoSumarioAltaSemInt() {
		return habilitarBotaoSumarioAltaSemInt;
	}

	public void setHabilitarBotaoSumarioAltaSemInt(
			Boolean habilitarBotaoSumarioAltaSemInt) {
		this.habilitarBotaoSumarioAltaSemInt = habilitarBotaoSumarioAltaSemInt;
	}

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}


	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}


	public Short getGsoSeqp() {
		return gsoSeqp;
	}


	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}


	public Integer getConNumero() {
		return conNumero;
	}


	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}


	public Byte getRnaSeqp() {
		return rnaSeqp;
	}


	public void setRnaSeqp(Byte rnaSeqp) {
		this.rnaSeqp = rnaSeqp;
	}
	

	public List<InternacaoVO> getInternacoes() {
		return internacoes;
	}


	public void setInternacoes(List<InternacaoVO> internacoes) {
		this.internacoes = internacoes;
	}
}