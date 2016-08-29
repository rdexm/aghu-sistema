package br.gov.mec.aghu.blococirurgico.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacadeBean;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.CadastroPlanejamentoPacienteAgendaController;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.DetalhamentoPortalAgendamentoController;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.PesquisarPacientesCirurgiaVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendaJustificativaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarPacientesCirurgiaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -4997391332261628111L;
	
	private static final String ID_MODAL_JUSTIFICATIVA_EXCLUSAOPACIENTE = "modalJustificativaExclusaoPacienteWG";
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;
	
//	@EJB
//	private IAghuFacade  aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private SecurityController securityController; 
	
	@Inject
	private CadastroPlanejamentoPacienteAgendaController cadastroPlanejamentoPacienteAgendaController;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;
	
	private List<PesquisarPacientesCirurgiaVO> pacientesCirurgia;
	
	private PesquisarPacientesCirurgiaVO selecao;
	
	//Atributos do componente de pesquisa de paciente + pesquisa fonética
	//Fazer conforme manterJustificativaLaudos.xhtml mec:pesquisaPaciente
	private Integer codPac;
	private Integer prontuario;
	private AipPacientes paciente;
	private Integer pacCodigoFonetica;
	
	private Comparator<PesquisarPacientesCirurgiaVO> currentComparator;
	
	private Boolean pesquisou = Boolean.FALSE;
		
	//#22397
	private MbcAgendas agenda;
	private Integer seq;
	private String tituloModal;
	private String justificativa;
	private String titulo;
	private boolean gravouJustificativa;
	
	// parametro para chamar #22328 e #22403
	private Date dataAgenda;
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private Short seqEspecialidade;
	private Short seqUnidFuncionalCirugica;
	private String cameFrom;
	private Long dataAgendaMili;
	private Integer agdSeq;
	private String situacaoAgenda;
	private Short sciUnfSeq;
	private Short sciSeqp;
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";	
	private static final String PLANEJAMENTO_PACIENTE_AGENDA_CRUD = "blococirurgico-planejamentoPacienteAgendaCRUD";
	private static final String PESQUISA_PACIENTE_CIRURGIA = "blococirurgico-pesquisarPacientesCirurgia";
	private static final String DETALHAMENTO_PORTAL_AGENDAMENTO = "blococirurgico-detalhamentoPortalAgendamento";
	
	public void iniciar() {
		if(codPac != null) {
			buscaPacientePorCodigo();
		}else{
			this.setPacientesCirurgia(null);
		}
	}
	
	public void limpar() {
		this.paciente = null;
		this.prontuario = null;
		this.codPac = null;
		this.pacientesCirurgia = null;
		this.pacCodigoFonetica = null;
		this.pesquisou = Boolean.FALSE;
		this.selecao = null;
		pesquisaPacienteController.limparCampos();
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Testa se o paciente é null <br>
	 * Caso venha null, primeiro pesquisa pelo prontuário<br>
	 * Depois pesquisa pelo paciente.
	 * @return
	 */
	public String pesquisarPacientes() {
		if (paciente != null) {
			pesquisarPacientesCirurgia();
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");
		}
		
		return PESQUISA_PACIENTE_CIRURGIA;
	}
	
	public void carregaPaciente(AipPacientes paciente) {
		if (paciente != null) {
			codPac = paciente.getCodigo();
			this.paciente = paciente;
		} else {
			codPac = null;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");
		}
	}
	
	public void buscaPacientePorCodigo() {
		if (codPac != null) {
			carregaPaciente(pacienteFacade.buscaPaciente(codPac));
		} else {
			carregaPaciente(null);
		}
	}

	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}
	
	public void pesquisarPacientesCirurgia(){

		this.codPac = paciente.getCodigo();
		pacientesCirurgia = blocoCirurgicoFacade.pesquisarPacientesCirurgia(paciente);
		
		// mantem a ordem corrente
		if (this.currentComparator != null && this.pacientesCirurgia != null) {
			Collections.sort(this.pacientesCirurgia, this.currentComparator);
		}

		pesquisou = Boolean.TRUE;
	}
	
	public boolean getIncluirPacienteAgendaListaEspera(Boolean indExclusao, String descricaoContexto, Integer agdSeq){
		if(agdSeq != null){
			if(indExclusao){
				return false;
			} else if(DominioSituacaoAgendas.LE.equals(descricaoContexto) && securityController.usuarioTemPermissao("incluirPacienteListaEspera", "persistir")){
				return true;
			} else if(!DominioSituacaoAgendas.LE.equals(descricaoContexto) && (securityController.usuarioTemPermissao("incluirPlanejamentoPacienteAgenda", "visualizar") || 
					securityController.usuarioTemPermissao("incluirPlanejamentoPacienteAgenda", "persistir"))){
				return true;
			}
		}
		
		return false;
	}
	
	//ON2 popula parametros e redireciona para outras telas
	public String redirecionarEdicao(Integer agdSeq, String descricaoContexto){
		MbcAgendas agenda = blocoCirurgicoPortalPlanejamentoFacade.buscarAgenda(agdSeq);
		if(agenda==null){
			return "";
		}
		this.setAgdSeq(agdSeq);
		if(descricaoContexto.equals(DominioSituacaoAgendas.LE.getDescricao())){
			this.popularParametroPaciente(agenda);
			// chamar Estória #22254		
			cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(seqEspecialidade);
			cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(matriculaEquipe);
			cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(vinCodigoEquipe);
			cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(unfSeqEquipe);
			cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(indFuncaoProfEquipe);
			cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(seqUnidFuncionalCirugica);
			cadastroPlanejamentoPacienteAgendaController.setAgdSeq(agdSeq);
			cadastroPlanejamentoPacienteAgendaController.setCameFrom(PESQUISA_PACIENTE_CIRURGIA);			
			return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
		}
		
		//popula os atributos para sem usados como parametro para a estoria 22328
		popularVisualizarPlanejamentoParametros(agenda);
		// chamar Estória #22328
		cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(seqEspecialidade);
		cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(matriculaEquipe);
		cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(vinCodigoEquipe);
		cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(unfSeqEquipe);
		cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(indFuncaoProfEquipe);
		cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(seqUnidFuncionalCirugica);
		cadastroPlanejamentoPacienteAgendaController.setCameFrom(PESQUISA_PACIENTE_CIRURGIA);
		cadastroPlanejamentoPacienteAgendaController.setDataAgenda(dataAgendaMili);
		cadastroPlanejamentoPacienteAgendaController.setAgdSeq(agdSeq);
		cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam(situacaoAgenda);
		
		return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
	}
	
	//#22403 - ON1
	public Boolean exibirBotaoIrPara(Integer agdSeq, String codigoContexto) {
		if(agdSeq != null){
			MbcAgendas agenda = blocoCirurgicoPortalPlanejamentoFacade.buscarAgenda(agdSeq);
			if(agenda != null && agenda.getDtAgenda() != null && agenda.getSalaCirurgica() != null && 
					agenda.getSalaCirurgica().getId() != null && agenda.getSalaCirurgica().getUnidadeFuncional().getSeq()!=null){
				if (!codigoContexto.equals(String.valueOf(DominioSituacaoAgendas.CA.getCodigo()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String redirecionarDetalhamento(Integer agdSeq) {
		MbcAgendas agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agdSeq, new Enum[] {MbcAgendas.Fields.SALA_CIRURGICA}, null);
		setAgdSeq(agdSeq);
		setDataAgendaMili(agenda.getDtAgenda().getTime());
		if(agenda.getSalaCirurgica()!=null){
			setSciUnfSeq(agenda.getSalaCirurgica().getId().getUnfSeq());
			setSciSeqp(agenda.getSalaCirurgica().getId().getSeqp());
		}
		setSeqEspecialidade(agenda.getEspecialidade().getSeq());
		setMatriculaEquipe(agenda.getProfAtuaUnidCirgs().getId().getSerMatricula());
		setVinCodigoEquipe(agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo());
		setIndFuncaoProfEquipe(agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf().toString());
		setUnfSeqEquipe(agenda.getProfAtuaUnidCirgs().getId().getUnfSeq());
		if(agenda.getUnidadeFuncional()!=null){
			this.setSeqUnidFuncionalCirugica(agenda.getUnidadeFuncional().getSeq());
		}

		detalhamentoPortalAgendamentoController.setUnfSeqSala(sciUnfSeq);
		detalhamentoPortalAgendamentoController.setSalaSeqp(sciSeqp);
		detalhamentoPortalAgendamentoController.setDataAgendaMili(dataAgendaMili);
		detalhamentoPortalAgendamentoController.setSeqAgd2Select(agdSeq);
		detalhamentoPortalAgendamentoController.setSeqUnidFuncionalCirugica(seqUnidFuncionalCirugica);
		detalhamentoPortalAgendamentoController.setSeqEspecialidade(seqEspecialidade);
		detalhamentoPortalAgendamentoController.setMatriculaEquipe(matriculaEquipe);
		detalhamentoPortalAgendamentoController.setVinCodigoEquipe(vinCodigoEquipe);
		detalhamentoPortalAgendamentoController.setUnfSeqEquipe(unfSeqEquipe);
		detalhamentoPortalAgendamentoController.setIndFuncaoProfEquipe(indFuncaoProfEquipe);
		detalhamentoPortalAgendamentoController.setCameFrom(PESQUISA_PACIENTE_CIRURGIA);
		detalhamentoPortalAgendamentoController.iniciar();
		return DETALHAMENTO_PORTAL_AGENDAMENTO;
	}
	
	private void popularParametroPaciente(MbcAgendas agenda){
		
		if(agenda.getProfAtuaUnidCirgs()!=null && agenda.getProfAtuaUnidCirgs().getId()!=null){
			this.setMatriculaEquipe(agenda.getProfAtuaUnidCirgs().getId().getSerMatricula());
			this.setVinCodigoEquipe(agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo());
			this.setUnfSeqEquipe(agenda.getProfAtuaUnidCirgs().getId().getUnfSeq());
			if(agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf()!=null){
				this.setIndFuncaoProfEquipe(agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf().getDescricao());
			}
		}
		if(agenda.getEspecialidade()!=null){
			this.setSeqEspecialidade(agenda.getEspecialidade().getSeq());
		}
	}
	
	private void popularVisualizarPlanejamentoParametros(MbcAgendas agenda){
		//parametros
		if(agenda.getDtAgenda()!=null){
			this.setDataAgenda(agenda.getDtAgenda());
		}
		this.popularParametroPaciente(agenda);
		if(agenda.getUnidadeFuncional()!=null){
			this.setSeqUnidFuncionalCirugica(agenda.getUnidadeFuncional().getSeq());
		}
		this.setCameFrom("pesqPac");
		this.setSituacaoAgenda(agenda.getIndSituacao().toString());
		if(agenda.getDtAgenda()!=null){
			this.setDataAgendaMili(agenda.getDtAgenda().getTime());
		}
		
	}
	
	public String obterTituloExcluirPaciente(Integer seq) {
		StringBuilder tituloPaciente = new StringBuilder();
		
		if(seq != null) {
			agenda = blocoCirurgicoFacadeBean.obterAgendaPorAgdSeq(seq);
			
			if (agenda != null) {							
				tituloPaciente.append(getBundle().getString("TITLE_JUSTIFICATIVA_EXCLUSAO_PACIENTE_PESQUISA_CIRURGIA")).append(' ')
				.append(WordUtils.capitalizeFully(agenda.getPaciente().getNome())).append(" - ")
				.append(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario())).append(" - ")
				.append(CoreUtil.capitalizaTextoFormatoAghu(agenda.getProcedimentoCirurgico().getDescricao()));
				
				this.setTitulo(tituloPaciente.toString());				
			}
		}	
		return tituloPaciente.toString();
	}
	
	public void abrirModalJustificativaExclusaoPaciente(){
		this.justificativa = null;
		this.buscarSeqAgenda(this.agdSeq);
		openDialog(ID_MODAL_JUSTIFICATIVA_EXCLUSAOPACIENTE);
	}

	public String abreviar(String str, int maxWidth) {
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public Integer buscarSeqAgenda(Integer agdSeq){
		this.seq = agdSeq;
		return this.seq;
	}
	
	public void cancelarExclusao(){
		if(this.gravouJustificativa){
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_PACIENTE_PESQUISA");
		}
		this.gravouJustificativa = false;
		this.agenda = new MbcAgendas();
		this.seq = null;
		this.justificativa = null;
		this.tituloModal = null;
	}
		
	public void gravarJustificativaExclusaoPacienteCirurgia() {
		
		if(StringUtils.isBlank(this.justificativa)){
			apresentarMsgNegocio("inputJustificativa",Severity.ERROR, "CAMPO_OBRIGATORIO", super.getBundle().getString("LABEL_JUSTIFICATIVA"));
			return;
		} else if (this.justificativa.length() > 500){
			this.justificativa = this.justificativa.replaceAll("\\r\\n", "\n"); 
		}

		MbcAgendaJustificativa agendaJustificativa = new MbcAgendaJustificativa();
		MbcAgendaJustificativaId agendaJustificativaId = new MbcAgendaJustificativaId();

		agendaJustificativaId.setAgdSeq(agenda.getSeq());
		agendaJustificativa.setId(agendaJustificativaId);
		agendaJustificativa.setMbcAgendas(agenda);
		agendaJustificativa.setTipo(DominioTipoAgendaJustificativa.ELE);
		agendaJustificativa.setJustificativa(getJustificativa());

		pacientesCirurgia = null;
		this.justificativa = null;
		try {
			this.blocoCirurgicoFacadeBean.excluirAgenda(agendaJustificativa);
			pesquisarPacientesCirurgia();
			this.gravouJustificativa = true;
			closeDialog(ID_MODAL_JUSTIFICATIVA_EXCLUSAOPACIENTE);
		} catch (final BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean verificarDominioSituacaoAgendaPaciente(String codigoContexto) {
		if (codigoContexto.equals(String.valueOf(DominioSituacaoAgendas.AG.getCodigo())) ||
			codigoContexto.equals(String.valueOf(DominioSituacaoAgendas.LE.getCodigo()))) {
			return true;
		}
		
		return false;
	}
	
	public List<PesquisarPacientesCirurgiaVO> getPacientesCirurgia() {
		return pacientesCirurgia;
	}

	public void setPacientesCirurgia(
			List<PesquisarPacientesCirurgiaVO> pacientesCirurgia) {
		this.pacientesCirurgia = pacientesCirurgia;
	}

	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
		
		if (this.pacCodigoFonetica != null) {
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			this.codPac = paciente.getCodigo();
			this.prontuario = paciente.getProntuario();
		}
	}

	public boolean verificarSituacaoAgendaCancelada(String codigoContexto) {
		if (codigoContexto.equals(String.valueOf(DominioSituacaoAgendas.CA.getCodigo()))) {
			return true;
		}
	
		return false;
	}
	
	public Date getDataAgenda() {
		return dataAgenda;
	}

	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}

	public Integer getMatriculaEquipe() {
		return matriculaEquipe;
	}

	public void setMatriculaEquipe(Integer matriculaEquipe) {
		this.matriculaEquipe = matriculaEquipe;
	}

	public Short getVinCodigoEquipe() {
		return vinCodigoEquipe;
	}

	public void setVinCodigoEquipe(Short vinCodigoEquipe) {
		this.vinCodigoEquipe = vinCodigoEquipe;
	}

	public Short getUnfSeqEquipe() {
		return unfSeqEquipe;
	}

	public void setUnfSeqEquipe(Short unfSeqEquipe) {
		this.unfSeqEquipe = unfSeqEquipe;
	}

	public String getIndFuncaoProfEquipe() {
		return indFuncaoProfEquipe;
	}

	public void setIndFuncaoProfEquipe(String indFuncaoProfEquipe) {
		this.indFuncaoProfEquipe = indFuncaoProfEquipe;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Short getSeqUnidFuncionalCirugica() {
		return seqUnidFuncionalCirugica;
	}

	public void setSeqUnidFuncionalCirugica(Short seqUnidFuncionalCirugica) {
		this.seqUnidFuncionalCirugica = seqUnidFuncionalCirugica;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Long getDataAgendaMili() {
		return dataAgendaMili;
	}

	public void setDataAgendaMili(Long dataAgendaMili) {
		this.dataAgendaMili = dataAgendaMili;
	}

	public String getSituacaoAgenda() {
		return situacaoAgenda;
	}

	public void setSituacaoAgenda(String situacaoAgenda) {
		this.situacaoAgenda = situacaoAgenda;
	}

	public MbcAgendas getAgenda() {
		return agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public String getTituloModal() {
		return tituloModal;
	}
	
	public void setTituloModal(String tituloModal) {
		this.tituloModal = tituloModal;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public Short getSciUnfSeq() {
		return sciUnfSeq;
	}

	public void setSciUnfSeq(Short sciUnfSeq) {
		this.sciUnfSeq = sciUnfSeq;
	}

	public Short getSciSeqp() {
		return sciSeqp;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}
	
	public boolean isGravouJustificativa() {
		return gravouJustificativa;
	}
	
	public void setGravouJustificativa(boolean gravouJustificativa) {
		this.gravouJustificativa = gravouJustificativa;
	}

	public PesquisarPacientesCirurgiaVO getSelecao() {
		return selecao;
	}

	public void setSelecao(PesquisarPacientesCirurgiaVO selecao) {
		this.selecao = selecao;
	}
}
