package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.action.AgendaProcedimentosController;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacadeBean;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PlanejamentoPacienteAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasReservaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurno2VO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class DetalhamentoPortalAgendamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 2180692121705212906L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
		
	@Inject
	private SecurityController securityController;
	
	@Inject
	private JustificativaTransferirAgendamentoController justificativaTransferirAgendamentoController;
	
	@Inject
	private RelatorioPortalPlanejamentoCirurgiasController relatorioPortalPlanejamentoCirurgiasController;
	
	@Inject
	private PortalPlanejamentoCirurgiasController portalPlanejamentoCirurgiasController;
	
	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	
	@Inject
	private AgendaProcedimentosController agendaProcedimentosController;

	@Inject
	private AtualizarEscalaPortalAgendamentoController atualizarEscalaPortalAgendamentoController;
	
	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;	

	@Inject
	private CadastroPlanejamentoPacienteAgendaController cadastroPlanejamentoPacienteAgendaController;
		
	private List<MbcSalaCirurgica> salasCirurgicasEquipe;
	
	private PortalPlanejamentoCirurgiasDiaVO detalhamento;
	
	private final String TELA_AGENDAMENTO = "agendamentoPaciente";
	private final String PESQUISAR_AGENDAS = "pesquisaAgendaCirurgia";
	private final String DETALHAMENTO_PORTAL = "blococirurgico-detalhamentoPortalAgendamento";
	private final String ATUALIZAR_ESCALA = "atualizarEscalaPortalPlanejamento";
	private final String AGENDAR_PROCEDIMENTO = "blococirurgico-agendaProcedimentos";
	private final String DETALHAMENTO_BOTAO_ATUALIZAR_ESCALA = "detalhamentoPortalAgendamentoBotaoAtualizarEscala";
	private final static String PLANEJAMENTO_PACIENTE_AGENDA_CRUD = "planejamentoPacienteAgendaCRUD";
	private final static String TROCAR_LOCAL_ESP_EQUIPE = "trocarLocalEspEquipeListaEspera";
	private final static Short SALA_NAO_INFORMADA = Short.valueOf("-1");
	private MbcSalaCirurgica salaNaoInformada;
	
	private String voltarPara;
		
	//Parametros tela anterior
	private Short seqEspecialidade;
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private DominioFuncaoProfissional funcaoProf;
	private Short seqUnidFuncionalCirugica;
	private String cameFrom;
	private Short unfSeqSala;
	private Short salaSeqp;
	private Date dataInicio;
	private AghEspecialidades especialidade;
	private AghUnidadesFuncionais unidadeFuncional;
	private Long dataAgendaMili;
	private Integer agdSeq;
	private Integer seqAgd2Select;
	
	// parametros utilizados quando selecionado a agenda 
	private PlanejamentoPacienteAgendaVO parametrosPlanejamentoPaciente;
	
	private String tituloAtualizarEscala;
	
	private DominioSituacaoAgendas situacaoAgenda;
	
	private MbcProfAtuaUnidCirgs equipe;
	private MbcSalaCirurgica salaCirurgica;
	
	private List<MbcSalaCirurgica> salasCirurgicas;
	
	private Integer seqpVO;
	private Integer seqAgenda;
	private PortalPlanejamentoCirurgiasAgendaVO agendaSelecionado;
	private PortalPlanejamentoCirurgiasReservaVO reservaSelecionada;
	private String equipeSelecionada;
	private Boolean turnoBloqueado = Boolean.FALSE;
	
	private Boolean pesquisaRealizada = Boolean.FALSE;
	private Boolean readOnlyExclusao = Boolean.TRUE;
	private Boolean readOnlyTransferir = Boolean.TRUE;
	private Boolean readOnlyAlterarEsp = Boolean.TRUE;
	private Boolean readOnlyRemarcar = Boolean.TRUE;
	private Boolean readOnlyIncluir = Boolean.TRUE;
	private Boolean readOnlyAnotar = Boolean.TRUE;
	private Boolean readOnlyEditar = Boolean.TRUE;
	private Boolean readOnlyAjustar = Boolean.FALSE;
	private Boolean readOnlyAtualizarEscala = Boolean.FALSE;
	
	private MbcAgendas agenda;
	private String descricaoTurno;
	private Boolean habilitaBtVisualizar;
	private Boolean cancelouCadastroPlanejamento;
	
	public void iniciar() {
		if(cancelouCadastroPlanejamento != null){
			if(!cancelouCadastroPlanejamento) {
				buscarDetalhamento();
			}
			cancelouCadastroPlanejamento = false;
			return;
		}
		popularEspecialidadeEquipe();
		if(this.seqUnidFuncionalCirugica!=null){
			this.setUnidadeFuncional(this.aghuFacade.obterUnidadeFuncional(seqUnidFuncionalCirugica));
			this.listarSalasCirurgicas();
		}
		if(this.unfSeqSala!=null && salaSeqp!=null){
			if(SALA_NAO_INFORMADA.equals(salaSeqp)) {
				salaNaoInformada = new MbcSalaCirurgica(new MbcSalaCirurgicaId(unfSeqSala, SALA_NAO_INFORMADA), "SALA NÃO INFORMADA");
				this.setSalaCirurgica(salaNaoInformada);
			}
			else {
				this.setSalaCirurgica(this.blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(this.salaSeqp, this.unfSeqSala));
			}
		}
		if(dataInicio == null && dataAgendaMili!=null){
			this.setDataInicio(new Date(dataAgendaMili));
		}
		if (this.salaCirurgica!=null) {
			buscarDetalhamento();
			if (seqAgd2Select != null) {
				// Seleciona linha do agendamento passado por parametro pela tela de pesqPac
				if (detalhamento != null && !detalhamento.getListaSalas().isEmpty()) {
					for (PortalPlanejamentoCirurgiasTurno2VO turno : detalhamento.getListaSalas().get(0).getListaTurnos2()) {
						for ( PortalPlanejamentoCirurgiasAgendaVO ag : turno.getListaAgendas()) {
							if (this.seqAgd2Select.equals(ag.getSeqAgenda())) {
								this.agendaSelecionado = ag;
								this.seqAgenda = ag.getSeqAgenda();
								atualizarBotoesAcaoAgenda();
								return;
							}
						}
					}
				}
			}
		}
		habilitaBtVisualizar= Boolean.FALSE;
	}

	private void popularEspecialidadeEquipe() {
		if(this.seqEspecialidade!=null){
			this.setEspecialidade(aghuFacade.obterAghEspecialidadesPorChavePrimaria(seqEspecialidade));
		}
		if(indFuncaoProfEquipe!=null && matriculaEquipe!=null && vinCodigoEquipe!=null && unfSeqEquipe!=null){
			DominioFuncaoProfissional funcaoprofissional = DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe);
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, funcaoprofissional);
			this.setEquipe(blocoCirurgicoFacade.obterMbcProfAtuaUnidCirgsPorChavePrimaria(id));
		}
		if(this.indFuncaoProfEquipe!=null && !this.indFuncaoProfEquipe.isEmpty()){
			this.setFuncaoProf(DominioFuncaoProfissional.getInstance(this.indFuncaoProfEquipe));
		}				
	}

	public Integer obterSize(PortalPlanejamentoCirurgiasTurno2VO tur) {
		if (tur != null) {
			return (tur.getListaReservas().size() + 1)+(tur.getListaAgendas().size() + 1);
		} else {
			return 0;
		}
	}
	
	// para a sala e data da tela busca o detalhamento da Agenda
	public void mudarSalaCirurgica(){
		buscarDetalhamento();
	}
	
	public String getAbreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public String getTooltip(String str, int intervalo) {
	   StringBuilder anotacao = new StringBuilder();
	    int tam = str.length();
	    if(tam < intervalo) {
	       	anotacao.append(str);
	       	return anotacao.toString();
	    } else {
	    	int size = tam/intervalo;
	        int strFinal = intervalo;
	        int inicial=0;
	        for (int i = 0; i < size+1; i++) {
	        	strFinal = inicial+intervalo;
	        	anotacao.append(StringUtils.substring(str, inicial, strFinal) ).append( "<br/>");
	        	inicial = strFinal;
			}
	        anotacao.append(StringUtils.substring(str, strFinal, tam));
	    }
	    return anotacao.toString();
	}
	
	public void selecionarItemReserva(final Integer seqp){
		this.seqpVO = seqp;
		selecionaItemReserva();
	}
	
	public void selecionaItemReserva() {
		for(PortalPlanejamentoCirurgiasTurno2VO turno : detalhamento.getListaSalas().get(0).getListaTurnos2()) {
			for(PortalPlanejamentoCirurgiasReservaVO reserv : turno.getListaReservas()) {
				if(reserv.getSeqpVO().equals(this.seqpVO)) {
					this.seqAgenda = null;
					this.agendaSelecionado = null;
					this.reservaSelecionada = reserv;
					this.equipeSelecionada = reserv.getEquipe();
					this.matriculaEquipe = reserv.getEquipeAgenda().getId().getSerMatricula();
					this.vinCodigoEquipe = reserv.getEquipeAgenda().getId().getSerVinCodigo();
					this.unfSeqEquipe = reserv.getEquipeAgenda().getId().getUnfSeq();
					this.funcaoProf = reserv.getEquipeAgenda().getId().getIndFuncaoProf();
					this.turnoBloqueado = turno.getBloqueado();
					this.descricaoTurno = turno.getDescricaoTurno();
					break;
				}
			}
		}
		atualizarBotoesAcaoReserva();
	}
	
	public void selecionarItemAgenda(final Integer seq){
		this.seqAgenda = seq;
		selecionaItemAgenda();
	}
	
	public void selecionaItemAgenda() {
		
		for(PortalPlanejamentoCirurgiasTurno2VO turno : detalhamento.getListaSalas().get(0).getListaTurnos2()) {
			for(PortalPlanejamentoCirurgiasAgendaVO agd : turno.getListaAgendas()) {
				if(agd.getSeqAgenda().equals(this.seqAgenda)) {
					this.seqpVO = null;
					this.reservaSelecionada = null;
					this.agendaSelecionado = agd;
					this.equipeSelecionada = agd.getEquipe();
					break;
				}
			}
		}
		atualizarBotoesAcaoAgenda();
	}

	private void atualizarBotoesAcaoAgenda() {
		//possue agenda habilita botoes para paciente
		if(salaSeqp == null){
			salaSeqp = salaCirurgica.getId().getSeqp();
		}
		salaCirurgica.getId().getSeqp();
		if (agendaSelecionado != null && agendaSelecionado.getSeqAgenda() != null) {
			agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorAgdSeq(agendaSelecionado.getSeqAgenda());	
			parametrosPlanejamentoPaciente = new PlanejamentoPacienteAgendaVO(agenda);
			desabilitarBotoesAgenda();
			desabilitarBotoesPaciente();
			//#50482
			if(SALA_NAO_INFORMADA.equals(salaSeqp)) {
				readOnlyAtualizarEscala = !securityController.usuarioTemPermissao(DETALHAMENTO_BOTAO_ATUALIZAR_ESCALA, DETALHAMENTO_BOTAO_ATUALIZAR_ESCALA);
			}
			List<MbcCirurgias> cirurgias = this.blocoCirurgicoFacade.pesquisarCirurgiasDeReserva(agenda.getDtAgenda(), agenda.getSeq());
			if (agendaSelecionado.getIndGeradoSistema()) {
				if (getPermissaoAdm()) {
					if (cirurgias.isEmpty()){	
							readOnlyEditar = Boolean.TRUE;
					}
					else{
						readOnlyEditar = Boolean.FALSE;
					}
				}
			} else {
				if (getPermissaoMedico()) {
					if (getPermissaoMedico()){
						readOnlyEditar = Boolean.FALSE;
					}	
				}
				else if (getPermissaoAdm()){
						if (cirurgias.isEmpty()){	
							readOnlyEditar = Boolean.TRUE;
						}
					else{
						readOnlyEditar = Boolean.FALSE;
					}
				}
			}	
		}	
		
		if (agenda != null) {
			if (DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())) {
				readOnlyExclusao = Boolean.FALSE;
				readOnlyTransferir = Boolean.FALSE;
				readOnlyRemarcar = Boolean.FALSE;
				MbcControleEscalaCirurgica controleEscalaCirurgica = blocoCirurgicoFacade.obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(this.salaCirurgica.getId().getUnfSeq(), dataInicio, null);
				if(controleEscalaCirurgica == null){
					readOnlyAlterarEsp = Boolean.FALSE;
				}
			}
		}
		
		habilitaBtVisualizar = Boolean.TRUE;
	}
	
	private void atualizarBotoesAcaoReserva() {
		parametrosPlanejamentoPaciente = popularParametroPlanejamentoPorEquipe();
		//habilita botoes para agenda
		habilitarBotoesAgenda();
		desabilitarBotoesPaciente();
		habilitaBtVisualizar = Boolean.TRUE;
	}
	
	private PlanejamentoPacienteAgendaVO popularParametroPlanejamentoPorEquipe(){
		MbcAgendas agenda = new MbcAgendas();
		if (reservaSelecionada != null){
			//especialidade da tela
			if(reservaSelecionada.getEspSeq() != null) {
				agenda.setEspecialidade(aghuFacade.obterEspecialidadePorChavePrimaria(reservaSelecionada.getEspSeq()));
			}
			//equipe da tela
			agenda.setProfAtuaUnidCirgs(reservaSelecionada.getEquipeAgenda());
		}
		agenda.setDtAgenda(dataInicio);
		agenda.setUnidadeFuncional(this.salaCirurgica.getUnidadeFuncional());
		//popula parametro para as demais estorias
		parametrosPlanejamentoPaciente = new PlanejamentoPacienteAgendaVO(agenda);
		return parametrosPlanejamentoPaciente;
	}
	
	public String colorirTabela(Object obj) {
		if (obj == null) {
			return "";
		} else 	if(obj instanceof PortalPlanejamentoCirurgiasAgendaVO ){
			PortalPlanejamentoCirurgiasAgendaVO item = (PortalPlanejamentoCirurgiasAgendaVO) obj;
			if(item.getOverbooking() != null && item.getOverbooking()) {
				return "agd-overbooking";
			} else if(item.getPlanejado() != null && item.getPlanejado()) {
				return "agd-planejada";
			} else if(item.getRealizada() != null && item.getRealizada()) {
				return "agd-realizada";
			} else if(item.getEscala() != null && item.getEscala()) {
				return "agd-escala";
			} else {
				return "agd-indisponivel";
			}
		}else{
			PortalPlanejamentoCirurgiasReservaVO item = (PortalPlanejamentoCirurgiasReservaVO) obj;
			if(item.getBloqueio() != null && item.getBloqueio()){
				return "agd-bloqueada";
			}
			if(item.getCedencia()) {
				return "agd-cedencia";
			} else {
				return  "agd-reservada";
			}
		}
	}	

	public void buscarDetalhamento(){
		try{
			this.detalhamento = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(unidadeFuncional.getSeq(),dataInicio,especialidade,equipe,salaCirurgica, false,1, true);
			if(detalhamento!=null && detalhamento.getDatasAgendaDate()[0]!=null){
				this.dataInicio = detalhamento.getDatasAgendaDate()[0];
			}
			this.desmarcarItemSelecionado();
			this.desabilitarBotoesPaciente();
			this.desabilitarBotoesAgenda();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String redirectTrocarLocalEspEquipeListaEspera() {
		return TROCAR_LOCAL_ESP_EQUIPE;
	}
	
	//executa ação de incluir paciente selecionado recebe parâmetro indicando se o usuário tem permissão para agendar procedimento eletivo, redicerionando p/ #22460, senão redireciona # 22328 (incluir paciente)
	public String incluirPaciente(){
		try {
			this.parametrosPlanejamentoPaciente.setCameFrom(TELA_AGENDAMENTO);
			this.parametrosPlanejamentoPaciente.setSituacaoAgenda(null);
			if (getPermissaoMedico()){
				Short espSeq = null;
				espSeq = reservaSelecionada.getEspSeq();
				blocoCirurgicoPortalPlanejamentoFacade.validarInclusaoPacienteAgenda(dataInicio, reservaSelecionada.getEquipeAgenda(),
						espSeq, salaCirurgica.getUnidadeFuncional().getSeq(), salaCirurgica.getId().getSeqp(),this.descricaoTurno);
				cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(parametrosPlanejamentoPaciente.getSeqEspecialidade());
				cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(parametrosPlanejamentoPaciente.getMatriculaEquipe());
				cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(parametrosPlanejamentoPaciente.getVinCodigoEquipe());
				cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(parametrosPlanejamentoPaciente.getUnfSeqEquipe());
				cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe());
				cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(parametrosPlanejamentoPaciente.getSeqUnidFuncionalCirugica());
				cadastroPlanejamentoPacienteAgendaController.setCameFrom(DETALHAMENTO_PORTAL);
				cadastroPlanejamentoPacienteAgendaController.setDataAgenda(parametrosPlanejamentoPaciente.getDataAgenda());
				cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam(parametrosPlanejamentoPaciente.getSituacaoAgenda());
				cadastroPlanejamentoPacienteAgendaController.setSalaSeqp(salaCirurgica.getId().getSeqp());
				cadastroPlanejamentoPacienteAgendaController.setAgdSeq(null);
				return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
			} else if(getPermissaoAdm()) {
				agendaProcedimentosController.setVoltarPara(DETALHAMENTO_PORTAL);
				agendaProcedimentosController.setAgendaUnfSeq(seqUnidFuncionalCirugica);
				agendaProcedimentosController.setAgendaEspSeq(parametrosPlanejamentoPaciente.getSeqEspecialidade());
				agendaProcedimentosController.setAgendaData(parametrosPlanejamentoPaciente.getDataAgenda());
				agendaProcedimentosController.setAgendaUnfSeqSala(this.salaCirurgica.getId().getUnfSeq());
				agendaProcedimentosController.setAgendaSalaSeqp(this.salaCirurgica.getId().getSeqp());
				agendaProcedimentosController.setAgendaMatriculaEquipe(parametrosPlanejamentoPaciente.getMatriculaEquipe());
				agendaProcedimentosController.setAgendaVinCodigoEquipe(parametrosPlanejamentoPaciente.getVinCodigoEquipe());
				agendaProcedimentosController.setAgendaSeq(parametrosPlanejamentoPaciente.getAgdSeq());
				agendaProcedimentosController.setIndFuncaoProf(DominioFuncaoProfissional.getInstance(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe()));
				return AGENDAR_PROCEDIMENTO;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	//executa ação de editar paciente selecionado redireciona # 22328
	public String editarPaciente(){
		if(!this.verificaPacienteAdicionado()){ //verificar se o agendamento foi selecionado
			return null;
		}
		this.parametrosPlanejamentoPaciente.setCameFrom(TELA_AGENDAMENTO);
		if (agendaSelecionado.getIndGeradoSistema()){
			return setarParametrosAgendarProcedimentos();
		} else if (getPermissaoAdm() && !agendaSelecionado.getIndGeradoSistema()) {
			return setarParametrosAgendarProcedimentos();
		} else {
			cadastroPlanejamentoPacienteAgendaController.setSeqEspecialidade(parametrosPlanejamentoPaciente.getSeqEspecialidade());
			cadastroPlanejamentoPacienteAgendaController.setMatriculaEquipe(parametrosPlanejamentoPaciente.getMatriculaEquipe());
			cadastroPlanejamentoPacienteAgendaController.setVinCodigoEquipe(parametrosPlanejamentoPaciente.getVinCodigoEquipe());
			cadastroPlanejamentoPacienteAgendaController.setUnfSeqEquipe(parametrosPlanejamentoPaciente.getUnfSeqEquipe());
			cadastroPlanejamentoPacienteAgendaController.setIndFuncaoProfEquipe(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe());
			cadastroPlanejamentoPacienteAgendaController.setSeqUnidFuncionalCirugica(parametrosPlanejamentoPaciente.getSeqUnidFuncionalCirugica());
			cadastroPlanejamentoPacienteAgendaController.setCameFrom(DETALHAMENTO_PORTAL);
			cadastroPlanejamentoPacienteAgendaController.setDataAgenda(parametrosPlanejamentoPaciente.getDataAgenda());
			cadastroPlanejamentoPacienteAgendaController.setSituacaoAgendaParam(parametrosPlanejamentoPaciente.getSituacaoAgenda());
			cadastroPlanejamentoPacienteAgendaController.setSalaSeqp(salaCirurgica.getId().getSeqp());
			cadastroPlanejamentoPacienteAgendaController.setAgdSeq(parametrosPlanejamentoPaciente.getAgdSeq());
			return PLANEJAMENTO_PACIENTE_AGENDA_CRUD;
		}		
	}
	
	private String setarParametrosAgendarProcedimentos() {
		agendaProcedimentosController.setVoltarPara(DETALHAMENTO_PORTAL);
		agendaProcedimentosController.setAgendaUnfSeq(seqUnidFuncionalCirugica);
		agendaProcedimentosController.setAgendaEspSeq(parametrosPlanejamentoPaciente.getSeqEspecialidade());
		agendaProcedimentosController.setAgendaData(parametrosPlanejamentoPaciente.getDataAgenda());
		agendaProcedimentosController.setAgendaUnfSeqSala(this.salaCirurgica.getId().getUnfSeq());
		agendaProcedimentosController.setAgendaSalaSeqp(this.salaCirurgica.getId().getSeqp());
		agendaProcedimentosController.setAgendaMatriculaEquipe(parametrosPlanejamentoPaciente.getMatriculaEquipe());
		agendaProcedimentosController.setAgendaVinCodigoEquipe(parametrosPlanejamentoPaciente.getVinCodigoEquipe());
		agendaProcedimentosController.setAgendaSeq(parametrosPlanejamentoPaciente.getAgdSeq());
		agendaProcedimentosController.setIndFuncaoProf(DominioFuncaoProfissional.getInstance(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe()));
		return AGENDAR_PROCEDIMENTO;
	}
	
	/**
	 * chama ON2 e inicializa a modal para transferir o agendamento
	 * passando como parametro o seg da agenda e o tipo da agenda
	 * 
	 */
	public void habilitarTransferirAgendamento(){
		DominioTipoAgendaJustificativa parametro;
		try {
			if (agenda != null && agenda.getSeq() != null) {
				parametro = blocoCirurgicoPortalPlanejamentoFacade.retornarParametroCirurgiasCanceladas(agenda.getSeq());
				justificativaTransferirAgendamentoController.setComeFrom(DETALHAMENTO_PORTAL);
				justificativaTransferirAgendamentoController.iniciarModal(agenda.getSeq(), parametro.toString());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
		}
	}
	
	public String pesquisarAgendas(){
		if(parametrosPlanejamentoPaciente == null){
			parametrosPlanejamentoPaciente = popularParametroPlanejamentoPorEquipe();
		}
		this.setVoltarPara(DETALHAMENTO_PORTAL);
		this.desmarcarItemSelecionado();
		pesquisaAgendaCirurgiaController.setVoltarPara(DETALHAMENTO_PORTAL);
		pesquisaAgendaCirurgiaController.setSeqUnidadeFuncional(parametrosPlanejamentoPaciente.getSeqUnidFuncionalCirugica());
		pesquisaAgendaCirurgiaController.setSeqEspecialidade(parametrosPlanejamentoPaciente.getSeqEspecialidade());
		pesquisaAgendaCirurgiaController.setIndFuncaoProfEquipe(DominioFuncaoProfissional.getInstance(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe()));
		pesquisaAgendaCirurgiaController.setUnfSeqEquipe(parametrosPlanejamentoPaciente.getUnfSeqEquipe());
		pesquisaAgendaCirurgiaController.setSerMatriculaEquipe(parametrosPlanejamentoPaciente.getMatriculaEquipe());
		pesquisaAgendaCirurgiaController.setSerVinCodigoEquipe(parametrosPlanejamentoPaciente.getVinCodigoEquipe());
		pesquisaAgendaCirurgiaController.inicio();
		return PESQUISAR_AGENDAS;
	}
	
	private boolean verificaPacienteAdicionado(){
		if(this.agendaSelecionado == null || this.agendaSelecionado.getSeqAgenda() == null){
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_DETALHAMENTO_PACIENTE_NAO_ADICIONADO");
			return false;
		}
		return true;
	}
	
	public void retroceder() {
		try{
			Date data =  DateUtil.adicionaDias(dataInicio, -1);
			detalhamento = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(unidadeFuncional.getSeq(),data,especialidade,equipe,salaCirurgica, true,1, true);
			if(detalhamento!=null && detalhamento.getDatasAgendaDate()[0]!=null){
				dataInicio = detalhamento.getDatasAgendaDate()[0];
			}else{
				dataInicio = data;
			}
			this.desmarcarItemSelecionado();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void avancar() {
		try{
			Date data =  DateUtil.adicionaDias(dataInicio, 1);
			detalhamento = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(unidadeFuncional.getSeq(),data,especialidade,equipe,salaCirurgica, false,1, true);
			if(detalhamento!=null && detalhamento.getDatasAgendaDate()[0]!=null){
				dataInicio = detalhamento.getDatasAgendaDate()[0];
			}else{
				dataInicio = data;
			}
			this.desmarcarItemSelecionado();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void desabilitarBotoesPaciente(){
		readOnlyExclusao = Boolean.TRUE;
		readOnlyTransferir = Boolean.TRUE;
		readOnlyRemarcar = Boolean.TRUE;
		readOnlyEditar = Boolean.TRUE;
		readOnlyAlterarEsp = Boolean.TRUE;
	}
	
	private void desabilitarBotoesAgenda(){
		readOnlyAnotar = Boolean.TRUE;
		readOnlyIncluir = Boolean.TRUE;
		readOnlyAjustar = Boolean.TRUE;
		readOnlyAtualizarEscala = Boolean.TRUE;
	}
	
	private void habilitarBotoesAgenda(){
		readOnlyAnotar = Boolean.FALSE;
		readOnlyIncluir = Boolean.FALSE;
		readOnlyAtualizarEscala = Boolean.FALSE;
		if (!blocoCirurgicoPortalPlanejamentoFacade.verificarEscalaDefinitivaFoiExecutada(dataInicio, unidadeFuncional.getSeq())) {
			readOnlyAjustar = Boolean.FALSE;
		}
		readOnlyAtualizarEscala = !securityController.usuarioTemPermissao(DETALHAMENTO_BOTAO_ATUALIZAR_ESCALA, DETALHAMENTO_BOTAO_ATUALIZAR_ESCALA);
	}
	
	public void desmarcarItemSelecionado(){
		seqpVO = null;
		seqAgenda = null;
		agendaSelecionado = null;
		reservaSelecionada = null;
		desabilitarBotoesAgenda();
		desabilitarBotoesPaciente();
		habilitaBtVisualizar= Boolean.FALSE;
	}
	
	public String voltar() {
		final String retorno = this.cameFrom;
		if("portalPlanejamentoCirurgias".equalsIgnoreCase(retorno)) {
			portalPlanejamentoCirurgiasController.pesquisar();
		}
		this.desmarcarItemSelecionado();
		dataInicio = null; //Seta null aqui, para não afetar a tela, pois, o método buscarDetalhamento é chamado em varias fases. #41083
		this.limparParametros();
		return retorno;
	}
	
	private void limparParametros() {
		this.salasCirurgicasEquipe = null;
		this.detalhamento = null;
		this.voltarPara = null;
		this.seqEspecialidade = null;
		this.matriculaEquipe = null;
		this.vinCodigoEquipe = null;
		this.unfSeqEquipe = null;
		this.indFuncaoProfEquipe = null;
		this.funcaoProf = null;
		this.seqUnidFuncionalCirugica = null;
		this.cameFrom = null;
		this.unfSeqSala = null;
		this.salaSeqp = null;
		this.dataInicio = null;
		this.especialidade = null;
		this.unidadeFuncional = null;
		this.dataAgendaMili = null;
		this.agdSeq = null;
		this.seqAgd2Select = null;
		this.parametrosPlanejamentoPaciente = null;
		this.tituloAtualizarEscala = null;
		this.situacaoAgenda = null;
		this.equipe = null;
		this.salaCirurgica = null;
		this.salasCirurgicas = null;
		this.seqpVO = null;
		this.seqAgenda = null;
		this.agendaSelecionado = null;
		this.reservaSelecionada = null;
		this.equipeSelecionada = null;
		this.turnoBloqueado = Boolean.FALSE;
		this.pesquisaRealizada = Boolean.FALSE;
		this.readOnlyExclusao = Boolean.TRUE;
		this.readOnlyTransferir = Boolean.TRUE;
		this.readOnlyAlterarEsp = Boolean.TRUE;
		this.readOnlyRemarcar = Boolean.TRUE;
		this.readOnlyIncluir = Boolean.TRUE;
		this.readOnlyAnotar = Boolean.TRUE;
		this.readOnlyEditar = Boolean.TRUE;
		this.readOnlyAjustar = Boolean.FALSE;
		this.readOnlyAtualizarEscala = Boolean.FALSE;
		this.agenda = null;
		this.descricaoTurno = null;
		this.habilitaBtVisualizar = null;
		this.cancelouCadastroPlanejamento = null;
	}
	
	public void listarSalasCirurgicas() {
		setSalasCirurgicas(this.blocoCirurgicoPortalPlanejamentoFacade.buscarSalasCirurgicasPorUnfSeq(unidadeFuncional.getSeq()));
		if(cameFrom.equals("portalPlanejamentoCirurgias")) {
			setSalasCirurgicas(this.salasCirurgicasEquipe);
		}
	}
	
	public void inicializarModalRelatorioPlanjCirg(){		
		relatorioPortalPlanejamentoCirurgiasController.setMsgModal(null);
		relatorioPortalPlanejamentoCirurgiasController.setApresentaMsgModal(Boolean.FALSE);
		relatorioPortalPlanejamentoCirurgiasController.setarDatasInicioEFim(dataInicio);
	}
	
	public void ajustarHorariosAgendamentos() {
		try {
			Short espSeq = null;
			espSeq = reservaSelecionada.getEspSeq();
			blocoCirurgicoFacadeBean.ajustarHorariosAgendamentoEmEscala(dataInicio, salaCirurgica.getId().getSeqp(), reservaSelecionada.getEquipeAgenda(), 
					espSeq, unidadeFuncional.getSeq(), false);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_AJUSTAR_HORARIOS");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		buscarDetalhamento();
	}
	
	public String getAtualizarEscala() {
		try {
			blocoCirurgicoPortalPlanejamentoFacade.chamarTelaEscala(dataInicio, parametrosPlanejamentoPaciente.getMatriculaEquipe(),
					parametrosPlanejamentoPaciente.getVinCodigoEquipe(), parametrosPlanejamentoPaciente.getUnfSeqEquipe(),
					DominioFuncaoProfissional.getInstance(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe()), parametrosPlanejamentoPaciente.getSeqUnidFuncionalCirugica(),
					parametrosPlanejamentoPaciente.getSeqEspecialidade());

			unidadeFuncional = (this.salaCirurgica != null ? aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(salaCirurgica.getId().getUnfSeq()) : null);
			
			AghEspecialidades esp = null;
			if(parametrosPlanejamentoPaciente.getSeqEspecialidade() != null) {
				esp = aghuFacade.obterAghEspecialidadesPorChavePrimaria(parametrosPlanejamentoPaciente.getSeqEspecialidade());
			}
			DominioFuncaoProfissional funcaoprofissional = DominioFuncaoProfissional.getInstance(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe());
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(parametrosPlanejamentoPaciente.getMatriculaEquipe(),
					parametrosPlanejamentoPaciente.getVinCodigoEquipe(), parametrosPlanejamentoPaciente.getUnfSeqEquipe(), funcaoprofissional);
			MbcProfAtuaUnidCirgs prof = blocoCirurgicoFacade.obterMbcProfAtuaUnidCirgs(id);
			String siglaEsp = esp != null ? " - " + esp.getSigla() : "";
			tituloAtualizarEscala = "<strong>Escala:</strong> " + unidadeFuncional.getDescricao() + " - " + DateUtil.obterDataFormatada(dataInicio, "dd/MM/yyyy")
				+ siglaEsp + " - <strong>Equipe:</strong> " + prof.getRapServidores().getPessoaFisica().getNome();
			atualizarEscalaPortalAgendamentoController.setEspSeq(parametrosPlanejamentoPaciente.getSeqEspecialidade());
			atualizarEscalaPortalAgendamentoController.setMatriculaEquipe(parametrosPlanejamentoPaciente.getMatriculaEquipe());
			atualizarEscalaPortalAgendamentoController.setVinCodigoEquipe(parametrosPlanejamentoPaciente.getVinCodigoEquipe());
			atualizarEscalaPortalAgendamentoController.setUnfSeqEquipe(parametrosPlanejamentoPaciente.getUnfSeqEquipe());
			atualizarEscalaPortalAgendamentoController.setIndFuncaoProfEquipe(parametrosPlanejamentoPaciente.getIndFuncaoProfEquipe());
			atualizarEscalaPortalAgendamentoController.setSeqUnidFuncionalCirugica(parametrosPlanejamentoPaciente.getSeqUnidFuncionalCirugica());
			atualizarEscalaPortalAgendamentoController.setUnfSeq(parametrosPlanejamentoPaciente.getSeqUnidFuncionalCirugica());
			atualizarEscalaPortalAgendamentoController.setDataEscala(parametrosPlanejamentoPaciente.getDataAgenda());
			atualizarEscalaPortalAgendamentoController.setSalaSeqp(this.salaCirurgica.getId().getSeqp());
			atualizarEscalaPortalAgendamentoController.setUnfSeqSala(this.salaCirurgica.getId().getUnfSeq());
			atualizarEscalaPortalAgendamentoController.setSemSala(SALA_NAO_INFORMADA.equals(salaSeqp));
			return ATUALIZAR_ESCALA;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Boolean getPermissaoMedico() {
		return securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaBotaoGravar", "cadastroPlanejamentoPacienteAgendaBotaoGravar");
	}
	
	public Boolean getPermissaoAdm() {
		return securityController.usuarioTemPermissao("agendarProcedimentoEletUrgEmergencia", "executar");
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}
	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}
	public MbcProfAtuaUnidCirgs getEquipe() {
		return equipe;
	}
	public void setEquipe(MbcProfAtuaUnidCirgs equipe) {
		this.equipe = equipe;
	}
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public Boolean getPesquisaRealizada() {
		return pesquisaRealizada;
	}
	public void setPesquisaRealizada(Boolean pesquisaRealizada) {
		this.pesquisaRealizada = pesquisaRealizada;
	}
	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}
	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
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
	public Short getUnfSeqSala() {
		return unfSeqSala;
	}
	public void setUnfSeqSala(Short unfSeqSala) {
		this.unfSeqSala = unfSeqSala;
	}
	public Short getSalaSeqp() {
		return salaSeqp;
	}
	public void setSalaSeqp(Short salaSeqp) {
		this.salaSeqp = salaSeqp;
	}
	public List<MbcSalaCirurgica> getSalasCirurgicas() {
		return salasCirurgicas;
	}
	public void setSalasCirurgicas(List<MbcSalaCirurgica> salasCirurgicas) {
		this.salasCirurgicas = salasCirurgicas;
	}
	public Integer getSeqpVO() {
		return seqpVO;
	}
	public void setSeqpVO(Integer seqpVO) {
		this.seqpVO = seqpVO;
	}
	public DominioSituacaoAgendas getSituacaoAgenda() {
		return situacaoAgenda;
	}
	public void setSituacaoAgenda(DominioSituacaoAgendas situacaoAgenda) {
		this.situacaoAgenda = situacaoAgenda;
	}
	public Long getDataAgendaMili() {
		return dataAgendaMili;
	}
	public void setDataAgendaMili(Long dataAgendaMili) {
		this.dataAgendaMili = dataAgendaMili;
	}
	public DominioFuncaoProfissional getFuncaoProf() {
		return funcaoProf;
	}
	public void setFuncaoProf(DominioFuncaoProfissional funcaoProf) {
		this.funcaoProf = funcaoProf;
	}
	public Integer getAgdSeq() {
		return agdSeq;
	}
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	public PortalPlanejamentoCirurgiasDiaVO getDetalhamento() {
		return detalhamento;
	}
	public void setDetalhamento(PortalPlanejamentoCirurgiasDiaVO detalhamento) {
		this.detalhamento = detalhamento;
	}
	public PlanejamentoPacienteAgendaVO getParametrosPlanejamentoPaciente() {
		return parametrosPlanejamentoPaciente;
	}
	public void setParametrosPlanejamentoPaciente(PlanejamentoPacienteAgendaVO parametrosPlanejamentoPaciente) {
		this.parametrosPlanejamentoPaciente = parametrosPlanejamentoPaciente;
	}
	
	public MbcAgendas getAgenda() {
		return agenda;
	}

	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
	}

	public Boolean getReadOnlyExclusao() {
		return readOnlyExclusao;
	}
	public void setReadOnlyExclusao(Boolean readOnlyExclusao) {
		this.readOnlyExclusao = readOnlyExclusao;
	}
	public Boolean getReadOnlyTransferir() {
		return readOnlyTransferir;
	}
	public void setReadOnlyTransferir(Boolean readOnlyTransferir) {
		this.readOnlyTransferir = readOnlyTransferir;
	}
	public Boolean getReadOnlyRemarcar() {
		return readOnlyRemarcar;
	}
	public void setReadOnlyRemarcar(Boolean readOnlyRemarcar) {
		this.readOnlyRemarcar = readOnlyRemarcar;
	}
	public Boolean getReadOnlyIncluir() {
		return readOnlyIncluir;
	}
	public void setReadOnlyIncluir(Boolean readOnlyIncluir) {
		this.readOnlyIncluir = readOnlyIncluir;
	}
	public Boolean getReadOnlyAnotar() {
		return readOnlyAnotar;
	}
	public void setReadOnlyAnotar(Boolean readOnlyAnotar) {
		this.readOnlyAnotar = readOnlyAnotar;
	}
	public Boolean getReadOnlyEditar() {
		return readOnlyEditar;
	}
	public void setReadOnlyEditar(Boolean readOnlyEditar) {
		this.readOnlyEditar = readOnlyEditar;
	}
	public String getVoltarPara() {
		return voltarPara;
	}
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	public Boolean getHabilitaBtVisualizar() {
		return habilitaBtVisualizar;
	}
	public void setHabilitaBtVisualizar(Boolean habilitaBtVisualizar) {
		this.habilitaBtVisualizar = habilitaBtVisualizar;
	}
	public Integer getSeqAgd2Select() {
		return seqAgd2Select;
	}
	public void setSeqAgd2Select(Integer seqAgd2Select) {
		this.seqAgd2Select = seqAgd2Select;
	}
	public Boolean getReadOnlyAlterarEsp() {
		return readOnlyAlterarEsp;
	}
	public void setReadOnlyAlterarEsp(Boolean readOnlyAlterarEsp) {
		this.readOnlyAlterarEsp = readOnlyAlterarEsp;
	}
	public Integer getSeqAgenda() {
		return seqAgenda;
	}
	public void setSeqAgenda(Integer seqAgenda) {
		this.seqAgenda = seqAgenda;
	}
	public PortalPlanejamentoCirurgiasAgendaVO getAgendaSelecionado() {
		return agendaSelecionado;
	}
	public void setAgendaSelecionado(PortalPlanejamentoCirurgiasAgendaVO agendaSelecionado) {
		this.agendaSelecionado = agendaSelecionado;
	}
	public PortalPlanejamentoCirurgiasReservaVO getReservaSelecionada() {
		return reservaSelecionada;
	}
	public void setReservaSelecionada(
			PortalPlanejamentoCirurgiasReservaVO reservaSelecionada) {
		this.reservaSelecionada = reservaSelecionada;
	}
	public Boolean getCancelouCadastroPlanejamento() {
		return cancelouCadastroPlanejamento;
	}
	public void setCancelouCadastroPlanejamento(Boolean cancelouCadastroPlanejamento) {
		this.cancelouCadastroPlanejamento = cancelouCadastroPlanejamento;
	}
	public void setReadOnlyAjustar(Boolean readOnlyAjustar) {
		this.readOnlyAjustar = readOnlyAjustar;
	}
	public Boolean getReadOnlyAjustar() {
		return readOnlyAjustar;
	}
	public Boolean getReadOnlyAtualizarEscala() {
		return readOnlyAtualizarEscala;
	}
	public void setReadOnlyAtualizarEscala(Boolean readOnlyAtualizarEscala) {
		this.readOnlyAtualizarEscala = readOnlyAtualizarEscala;
	}
	public String getTituloAtualizarEscala() {
		return tituloAtualizarEscala;
	}
	public void setTituloAtualizarEscala(String tituloAtualizarEscala) {
		this.tituloAtualizarEscala = tituloAtualizarEscala;
	}
	public Boolean getTurnoBloqueado() {
		return turnoBloqueado;
	}
	public void setTurnoBloqueado(Boolean turnoBloqueado) {
		this.turnoBloqueado = turnoBloqueado;
	}
	public String getDescricaoTurno() {
		return descricaoTurno;
	}
	public void setDescricaoTurno(String descricaoTurno) {
		this.descricaoTurno = descricaoTurno;
	}
	public void setEquipeSelecionada(String equipeSelecionada) {
		this.equipeSelecionada = equipeSelecionada;
	}
	public String getEquipeSelecionada() {
		return equipeSelecionada;
	}
	public List<MbcSalaCirurgica> getSalasCirurgicasEquipe() {
		return salasCirurgicasEquipe;
	}
	public void setSalasCirurgicasEquipe(
			List<MbcSalaCirurgica> salasCirurgicasEquipe) {
		this.salasCirurgicasEquipe = salasCirurgicasEquipe;
	}
	public MbcSalaCirurgica getSalaNaoInformada() {
		return salaNaoInformada;
	}
	public void setSalaNaoInformada(MbcSalaCirurgica salaNaoInformada) {
		this.salaNaoInformada = salaNaoInformada;
	}
}