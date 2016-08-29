package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacadeBean;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RegimeProcedimentoAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.TempoSalaAgendaVO;
import br.gov.mec.aghu.blococirurgico.vo.MensagemParametro;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioPlanejamentoCirurgicoController;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
public class CadastroPlanejamentoPacienteAgendaController extends ActionController {
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	private static final long serialVersionUID = 598453911401613426L;
	private static final Integer TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS = 4;
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	@EJB
	private IBlocoCirurgicoFacadeBean blocoCirurgicoFacadeBean;
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IInternacaoFacade internacaoFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@Inject
	private CadastroPlanejamentoPacienteAgendaAba1Controller aba1Controller;
	@Inject
	private CadastroPlanejamentoPacienteAgendaOrteseProteseController abaOrteseProteseController;
	@Inject
	private CadastroPlanejamentoPacienteAgendaAba2Controller abaOutrosProcedimentos;
	@Inject
	private CadastroPlanejamentoPacienteAgendaDiagnosticoController diagnosticoController;
	@Inject
	private CadastroPlanejamentoPacienteAgendaSolicitacaoEspecialController solicEspecController;
	@Inject
	private RelatorioPlanejamentoCirurgicoController relatorioPlanejamentoCirurgicoController;
	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	@Inject
	private SecurityController securityController;
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	@Inject
	private CadastroPlanejamentoPacienteAgendaOPMESController abaOpmesController;
	private MbcAgendas agenda = new MbcAgendas();	
	private Short seqEspecialidade; //Parametros tela anterior
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private Short seqUnidFuncionalCirugica;
	private String cameFrom;
	private Long dataAgenda; 	
	private Integer agdSeq;
	private String situacaoAgendaParam;
	private Short salaSeqp;
	private Boolean clicouCancelar;
	AghEspecialidades especialidadeEquipe;
	private List<MbcAgendas> listAgendas;
	private Integer codPac; //Atributos do componente de pesquisa de paciente + pesquisa fonética
	private Integer prontuario;
	private AipPacientes paciente;
	private Integer pacCodigoFonetica;
	private AinLeitos leito; //Atributo da suggestion de leito
	private VMbcProcEsp procedimento;
	private MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs;
	private MbcProcPorEquipe mbcProcPorEquipe;
	private MbcAgendaHemoterapia reservaSelecionada;
	private Integer selectedTab;
	private Boolean exibeAbaAnestesia = true; //aplicar no rendered 
	private Boolean exibeAbaOutrosProcedimentos = true; //aplicar no rendered 
	private Boolean exibeAbaDiagnostico = true; //aplicar no rendered 
	private Boolean exibeAbaReservaHemoterapica = true; //aplicar no rendered 
	private Boolean exibeAbaSolicitacoesEspeciais = true; //aplicar no rendered 
	private Boolean exibeAbaOrteseProtese = true; //aplicar no rendered
	private Boolean exibeCheckApCongelacao = true; //aplicar no rendered
	private Boolean exibeAbaDiagnosticos = true;
	private Boolean cameFromPesqPacOuListaEspera = false;
	private Boolean apCongelacao = false;
	private Boolean readOnlyCamposPorON5 = false; //aplicar no readonly de todos os campos da tela
	private Boolean readOnlyLadoCirurgia = false; //aplicar no readonly do campo Lado da cirurgia
	private Boolean readOnlyEspecialidadeEquipe = true; //aplicar no readOnly do campo especialidade
	private Boolean readOnlyBotaoHistoricoAgenda = false;
	private Boolean readOnlyInputQtdeUnidade = false;
	private Boolean habilitarBotaoGravar = true;
	private Boolean renderBotaoVoltar = false;
	private Boolean exibirModalAgendamento = false;
	private Boolean isInclusao;
	private final Integer maxRegitrosEsp = 100; 
	private Set<MbcAgendaHemoterapia> agendaHemoterapias;
	private List<MbcAgendaHemoterapia> agendaHemoterapiasRemovidas;
	private final String TELA_AGENDAMENTO = "blococirurgico-detalhamentoPortalAgendamento";
	private final String TELA_LISTA_ESPERA = "blococirurgico-pesquisaAgendaCirurgia";
	private final String MOCK_PORTAL = "mockPortalPlanejamento";
	private final String ATUALIZAR_ESCALA = "blococirurgico-atualizarEscalaPortalPlanejamento";
	private final String ANESTESIA = "aba1";
	private final String PROCEDIMENTO = "aba2";
	private final String DIAGNOSTICO = "diagnostico";
	private final String RESERVA_HEMO = "reservaHemoterapica";
	private final String SOLIC_ESPECIAL = "solicEsp";
	private final String ORTESE_PROTESE = "orteseProtese";
	private static final String PESQUISA_PACIENTE_CIRURGIA = "blococirurgico-pesquisarPacientesCirurgia";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String DETALHAMENTO_PORTAL = "blococirurgico-detalhamentoPortalAgendamento";
	private static final String ID_MODAL_AGENDAMENTOS = "modalAgendamentosWG";
	private Integer abaAberta;
	private Boolean limpaOpme;
	private DominioRegimeProcedimentoCirurgicoSus regimeAnterior;
	public void inicio() {
		abaAberta = 0;
		limpaOpme = true;
		if(agdSeq != null){ // edicao
			isInclusao = false;
			aghuFacade.limparEntityManager();
			agenda = blocoCirurgicoPortalPlanejamentoFacade.obterAgendaPorSeq(agdSeq);
			paciente = agenda.getPaciente();
			codPac = paciente.getCodigo();
			prontuario = paciente.getProntuario();
			procedimento = getCarregarProcedimentoPrincipal();
			if(PESQUISA_PACIENTE_CIRURGIA.equals(cameFrom) || TELA_LISTA_ESPERA.equals(cameFrom)) {
				cameFromPesqPacOuListaEspera = true;
			}
			carregarLeito();			
			if(agenda.getIndGeradoSistema() || (TELA_AGENDAMENTO.equals(cameFrom) && !DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())) || (PESQUISA_PACIENTE_CIRURGIA.equals(cameFrom))) {
				readOnlyCamposPorON5 = true; //deixa a tela apenas em modo leitura
				verificarExisteSolicEspApCongelacao(agenda.getSeq());
			}
			especialidadeEquipe = agenda.getEspecialidade();			
			this.agendaHemoterapias = blocoCirurgicoPortalPlanejamentoFacade.listarAgendasHemoterapiaPorAgendaSeq(agenda.getSeq());
			this.agendaHemoterapiasRemovidas = null;			
		} else { // inclusao  
			setValoresInclusaoAgenda();
		}
		if(Boolean.TRUE.equals(exibirModalAgendamento) && !DominioSituacaoAgendas.LE.equals(situacaoAgendaParam)) {
			this.openDialog(ID_MODAL_AGENDAMENTOS);
			exibirModalAgendamento = false;}
		if(!DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			exibeAbaReservaHemoterapica = false;
			exibeAbaSolicitacoesEspeciais = false;
			exibeAbaOrteseProtese = false;
			exibeCheckApCongelacao = false;
		}
		if (PESQUISA_PACIENTE_CIRURGIA.equals(cameFrom)) { renderBotaoVoltar = true; }
		abaOutrosProcedimentos.inicio();
		aba1Controller.inicio();
		diagnosticoController.inicio();
		solicEspecController.inicio();
		abaOrteseProteseController.inicio();
		abaOpmesController.inicio(this);
		validarLadoCirurgia();		
		getValidarRegrasPermissao();
		selectedTab = 0;
	}	
	public String getHelpComentario() {
		if(TELA_LISTA_ESPERA.equals(cameFrom)) {
			return getBundle().getString("TITLE_COMENTARIO_ESPECIAL_PLANEJAMENTO_PACIENTE_AGENDA_LE");
		} else {
			return getBundle().getString("TITLE_COMENTARIO_ESPECIAL_PLANEJAMENTO_PACIENTE_AGENDA");}
	}
	public String getHelpMaterialEspecial() {
		if(TELA_LISTA_ESPERA.equals(cameFrom)) {
			return getBundle().getString("TITLE_MATERIAL_ESPECIAL_PLANEJAMENTO_PACIENTE_AGENDA_LE");
		} else {
			return getBundle().getString("TITLE_MATERIAL_ESPECIAL_PLANEJAMENTO_PACIENTE_AGENDA");}
	}
	public void collapseTogglePesquisa() {
		if (abaAberta == null || (abaAberta != null && -1 == abaAberta)) {abaAberta = 0;
		} else {abaAberta = -1;}
	}
	private void setValoresInclusaoAgenda() {
		isInclusao = true;
		if(dataAgenda != null) {
			Date d = new Date(dataAgenda);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			agenda.setDtAgenda(new Date(dataAgenda));}
		if(agenda.getComentario() == null) {
			agenda.setComentario("Telefone para contato:");
		}
		if(!indFuncaoProfEquipe.equals("")){
			MbcProfAtuaUnidCirgsId id = new MbcProfAtuaUnidCirgsId(matriculaEquipe, vinCodigoEquipe, unfSeqEquipe, DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe));
			agenda.setProfAtuaUnidCirgs(blocoCirurgicoFacade.obterMbcProfAtuaUnidCirgsPorChavePrimaria(id));
		}
		if(salaSeqp != null) {
			agenda.setSalaCirurgica(this.blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(salaSeqp, seqUnidFuncionalCirugica)); //utilizar seqUnidFuncionalCirugica pois é o parametro selecionado na primeira tela, o campo unfSeqEquipe é do filtro de Equipe, que nem sempre é selecionado.
		}
		if(situacaoAgendaParam != null && situacaoAgendaParam.length() > 0) {
			agenda.setIndSituacao(DominioSituacaoAgendas.valueOf(situacaoAgendaParam));
		}
		if(seqEspecialidade != null){
			especialidadeEquipe = aghuFacade.obterAghEspecialidadesPorChavePrimaria(seqEspecialidade);
			agenda.setEspecialidade(especialidadeEquipe);
		}
	}
	private void getValidarRegrasPermissao() {
		exibeAbaAnestesia = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaAnestesiaConsultar", "cadastroPlanejamentoPacienteAgendaAbaAnestesiaConsultar");
		exibeAbaOutrosProcedimentos = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaOutrosProcConsultar", "cadastroPlanejamentoPacienteAgendaAbaOutrosProcConsultar");
		exibeAbaDiagnosticos = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaDiagnosticosConsultar", "cadastroPlanejamentoPacienteAgendaAbaDiagnosticosConsultar");
		if(this.getAgdSeq() == null && !this.getIsInclusao()){
			readOnlyBotaoHistoricoAgenda = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaHistoricoConsultar", "cadastroPlanejamentoPacienteAgendaHistoricoConsultar");
		}
		if(exibeAbaReservaHemoterapica){ //se não foi setado para false no método inicio
			exibeAbaReservaHemoterapica = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaReservaHemoConsultar", "cadastroPlanejamentoPacienteAgendaAbaReservaHemoConsultar");
		}
		readOnlyInputQtdeUnidade = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaReservaHemoExecutar", "cadastroPlanejamentoPacienteAgendaAbaReservaHemoExecutar") || securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaReservaHemoAlterar", "cadastroPlanejamentoPacienteAgendaAbaReservaHemoAlterar");
		if(exibeAbaSolicitacoesEspeciais){ //se não foi setado para false no método inicio
			exibeAbaSolicitacoesEspeciais = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaSolicEsp", "cadastroPlanejamentoPacienteAgendaAbaSolicEsp");
		}
		if(exibeAbaOrteseProtese){
			exibeAbaOrteseProtese = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaAbaOrteseProtese", "cadastroPlanejamentoPacienteAgendaAbaOrteseProtese");
		}
		habilitarBotaoGravar = securityController.usuarioTemPermissao("cadastroPlanejamentoPacienteAgendaBotaoGravar", "cadastroPlanejamentoPacienteAgendaBotaoGravar");
	}
	public void carregarLeito(){ //utilizado em onUpdateAction de mec:pesquisaPaciente
		if(paciente != null){
			AinInternacao internacao = internacaoFacade.obrterInternacaoPorPacienteInternado(paciente.getCodigo());
			if(internacao != null){
				leito = internacao.getLeito();
			} else { leito = null;}
			DominioFuncaoProfissional funcaoprofissional = DominioFuncaoProfissional.getInstance(indFuncaoProfEquipe);
			listAgendas = blocoCirurgicoPortalPlanejamentoFacade.pesquisarAgendasPorPacienteEquipe(new DominioSituacaoAgendas[]{DominioSituacaoAgendas.LE,DominioSituacaoAgendas.CA},paciente.getCodigo(),matriculaEquipe,vinCodigoEquipe,unfSeqEquipe,funcaoprofissional,seqEspecialidade,seqUnidFuncionalCirugica);
			if (!cameFromPesqPacOuListaEspera && !readOnlyCamposPorON5 && !listAgendas.isEmpty() && !DominioSituacaoAgendas.LE.equals(situacaoAgendaParam)) { 
				exibirModalAgendamento = true;
				this.openDialog(ID_MODAL_AGENDAMENTOS);
			}
		}
	}
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}
	private static <T> Collection<T> popularListasAbas(Collection<T> listaOrigem) {
		return listaOrigem != null ? new ArrayList<T>(listaOrigem) : new ArrayList<T>();
	}
	public void populaAgenda(List<MbcAgendaAnestesia> anestesia,List<MbcAgendaSolicEspecial> solicitacoesEspeciais){
		if(agenda.getSeq() == null) { //Se inclusão de agenda
			//if (ATUALIZAR_ESCALA.equals(cameFrom)) { //nao adicionado ao metodo inicio devido ao PMD - NPathComplexity
			//	agenda.setIndSituacao(DominioSituacaoAgendas.ES);
		    //}
			agenda.setIndExclusao(false);
			agenda.setIndGeradoSistema(false);
			agenda.setPaciente(paciente);
			agenda.setUnidadeFuncional(this.aghuFacade.obterUnidadeFuncional(seqUnidFuncionalCirugica));
			agenda.setIndPrecaucaoEspecial(false);
			agenda.setEspecialidade(especialidadeEquipe);
		} else {
			agenda.setAgendasOrtProteses(null);
			agenda.setAgendasprocedimentos(null);
			agenda.setAgendasHemoterapias(null);
			agenda.setAgendasDiagnosticos(null);
			agenda.setAgendasAnestesias(null);
			agenda.setAgendasSolicEspeciais(null);
		}
		agenda.setItensProcedHospitalar(abaOpmesController.getProcedimentoSus());
		agenda.setAgendasOrtProteses(this.abaOrteseProteseController.getAgendaOrtProteseList());
		agenda.setAgendasprocedimentos(this.abaOutrosProcedimentos.getAgendaProcedimentoList());
		agenda.setAgendasHemoterapias(this.getAgendaHemoterapias());
		agenda.setAgendasDiagnosticos(new ArrayList<MbcAgendaDiagnostico>(1));
		agenda.getAgendasDiagnosticos().add(this.diagnosticoController.getDiagnostico());
		agenda.setAgendasAnestesias(anestesia);
		agenda.setAgendasSolicEspeciais(solicitacoesEspeciais);
	}
	public String gravar() throws ParseException, IllegalAccessException, InvocationTargetException{
		final List<MbcAgendaOrtProtese> orteseProtese = (List<MbcAgendaOrtProtese>) popularListasAbas(this.abaOrteseProteseController.getAgendaOrtProteseList()); // Resgata e armazena localmente os valores/listas das abas
		final List<MbcAgendaProcedimento> outrosProcedimentos = (List<MbcAgendaProcedimento>) popularListasAbas(this.abaOutrosProcedimentos.getAgendaProcedimentoList());
		final Set<MbcAgendaHemoterapia> reservaHemoterapica = new HashSet<MbcAgendaHemoterapia>(popularListasAbas(this.getAgendaHemoterapias()));
		final MbcAgendaDiagnostico diagnostico = this.diagnosticoController.getDiagnostico();
		final List<MbcAgendaAnestesia> anestesia = (List<MbcAgendaAnestesia>) popularListasAbas(this.aba1Controller.getListaAgendaAnestesias());
		final List<MbcAgendaSolicEspecial> solicitacoesEspeciais = (List<MbcAgendaSolicEspecial>) popularListasAbas(this.solicEspecController.obterAgendasSolicitacaoEspecial());
		if(diagnosticoController.getDiagnostico() == null || diagnosticoController.getDiagnostico().getAghCid() == null){
			apresentarMsgNegocio("cid", Severity.ERROR,	"CAMPO_OBRIGATORIO","CID");
			selectedTab = 2;
			return null;
		}
		try { 	
			validarTempoSala();
			populaAgenda(anestesia,solicitacoesEspeciais);
			if (!setBuildRequisicao()) { 
				return null; 
			}
			MensagemParametro msgParam = this.blocoCirurgicoFacadeBean.gravarAgenda(agenda, isInclusao, diagnosticoController.getDiagnosticoRemocao(),aba1Controller.getListaRemocao(), solicEspecController.getListaAgendasSolicEspecRemover(),this.getAgendaHemoterapiasRemovidas(), abaOutrosProcedimentos.getListaRemocao(),abaOrteseProteseController.getListaRemocao(),this.obterLoginUsuarioLogado(), abaOpmesController.getListaExcluidos(), abaOpmesController.getListaPesquisadaClone(),abaOpmesController.getListaPesquisada(),abaOpmesController.getAlterouOPME());
			if(msgParam != null){
				if("MSG_ERRO_REQ_OBRIG_AGENDA".equals(msgParam.getMensagem())){
					this.apresentarMsgNegocio(Severity.ERROR, msgParam.getMensagem());
					return null;
				} else{
					this.apresentarMsgNegocio(Severity.ERROR, msgParam.getMensagem(), msgParam.getParametros());
					return null;
				}
			}
			posGravar();
			//#54563
			abaOpmesController.iniciaProcessosOPME();
			validaLimparOpme();
		} catch (final BaseException e) {	
			agenda.setSeq(null);
			this.abaOrteseProteseController.setAgendaOrtProteseList(orteseProtese); // Força a recuperação  dos valores/listas das abas armazenados localmente
			this.abaOutrosProcedimentos.setAgendaProcedimentoList(outrosProcedimentos);
			this.agenda.setAgendasHemoterapias(reservaHemoterapica);
			this.agenda.setAgendasDiagnosticos(new ArrayList<MbcAgendaDiagnostico>(1));
			this.agenda.getAgendasDiagnosticos().add(diagnostico);
			this.aba1Controller.setListaAgendaAnestesias(this.aba1Controller.getListaAgendaAnestesias());
			this.agenda.setAgendasSolicEspeciais(this.solicEspecController.obterAgendasSolicitacaoEspecial());
			this.apresentarExcecaoNegocio(e);
			return null;
		}
		if(DETALHAMENTO_PORTAL.equals(cameFrom) || TELA_AGENDAMENTO.equals(cameFrom) || ATUALIZAR_ESCALA.equals(cameFrom)){
			if(isInclusao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AGENDAMENTO_INCLUIDO");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AGENDAMENTO_EDITADO");
			}
			if (DETALHAMENTO_PORTAL.equals(cameFrom)) {
				detalhamentoPortalAgendamentoController.buscarDetalhamento();
			}
			clicouCancelar = false;
			if (DETALHAMENTO_PORTAL.equals(cameFrom)) {
				this.detalhamentoPortalAgendamentoController.buscarDetalhamento();
				this.detalhamentoPortalAgendamentoController.selecionarItemAgenda(agenda.getSeq());
			}
			limparParametros();
			return cameFrom;
		}else if(PESQUISA_PACIENTE_CIRURGIA.equals(cameFrom)){
			limparParametros();
			 return PESQUISA_PACIENTE_CIRURGIA;
		} else if(TELA_LISTA_ESPERA.equals(cameFrom)) {
			if(isInclusao){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PAC_LISTA_ESPERA_INCLUSAO");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PAC_LISTA_ESPERA_ALTERACAO");
			}
			pesquisaAgendaCirurgiaController.pesquisar(); //pesquisaAgendaCirurgiaController.setExibirLista(false);
			limparParametros();
			return TELA_LISTA_ESPERA;
		}
		return null;
	}
	private void posGravar() throws BaseException{
		if(blocoCirurgicoFacade.verificarMaterialEspecial(agenda)){
			this.apresentarMsgNegocio(Severity.INFO, "MBC_01833", agenda.getUnidadeFuncional().getDescricao());
		}
		if(!TELA_LISTA_ESPERA.equals(cameFrom)){
			if(prontuarioOnlineFacade.validarGeracaoPendenciaAssinaturaPlanejamentoPaciente( agenda.getSeq())){ 
				relatorioPlanejamentoCirurgicoController.setSeqMbcAgenda(agenda.getSeq());
				relatorioPlanejamentoCirurgicoController.geraPendenciasCertificacaoDigital();
			}
		}
	}
	private void validaLimparOpme(){
		if(limpaOpme){
			abaOpmesController.limpar();
		}
	}
	private boolean setBuildRequisicao() throws ParseException,ApplicationBusinessException, IllegalAccessException,InvocationTargetException {
		agenda = blocoCirurgicoFacadeBean.atualizarConvenio(agenda);
		abaOpmesController.setGravar(false);
		if (!abaOpmesController.isProcedimentoSusPreenchido()) {
			selectedTab = 5;
			return false;
		}
		if (!abaOpmesController.validaJustificativa()) {
			abaOpmesController.setGravar(true);
			selectedTab = 5;
			return false;
		} else {
			vinculaRequisicao();
			abaOpmesController.setValidaPrazo();
		}
		return true;
	}
	private void vinculaRequisicao() throws ParseException, ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		MbcRequisicaoOpmes requisicaoOpmes = abaOpmesController.concluirOpmes();
		agenda.setRequisicoesOpmes(new ArrayList<MbcRequisicaoOpmes>());
		agenda.getRequisicoesOpmes().add(requisicaoOpmes);
	}
	public void tabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null) {
			if(ANESTESIA.equals(event.getTab().getId())) {
				selectedTab = 0;
			} else if(PROCEDIMENTO.equals(event.getTab().getId())) {
				selectedTab = 1;
			} else if (DIAGNOSTICO.equals(event.getTab().getId())) {
				selectedTab = 2;
			} else if (RESERVA_HEMO.equals(event.getTab().getId())) {
				selectedTab = 3;
			} else if (SOLIC_ESPECIAL.equals(event.getTab().getId())) {
				selectedTab = 4;
			} else if (ORTESE_PROTESE.equals(event.getTab().getId())) {
				selectedTab = 5;
			}
		}	}	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				carregarLeito();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	public Integer obterSizeListAgendas() { return listAgendas != null ? listAgendas.size() : 0; }
	public void mudouValorUnidadeAdicional() {this.apresentarMsgNegocio(Severity.INFO, "MBC_01836");}
	public void modalApenasUmAgendamentoSim(){ // ação do botão SIM da modal para apenas um agendamento.
		MbcAgendas agendamentoUnico = listAgendas.get(0);
		agendamentoUnico.setDtAgenda(agenda.getDtAgenda());
		agendamentoUnico.setIndSituacao(DominioSituacaoAgendas.AG);
		agenda = agendamentoUnico;
		if(agendamentoUnico.getAgendasDiagnosticos() == null || agendamentoUnico.getAgendasDiagnosticos().isEmpty()){
			agenda.setAgendasDiagnosticos(null);
		}
		procedimento = getCarregarProcedimentoPrincipal(); 
		if(dataAgenda != null) {
			Date d = new Date(dataAgenda);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			agenda.setDtAgenda(new Date(dataAgenda));
		}
		if(salaSeqp != null) {
			agenda.setSalaCirurgica(this.blocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(salaSeqp, seqUnidFuncionalCirugica)); //utilizar seqUnidFuncionalCirugica pois é o parametro selecionado na primeira tela, o campo unfSeqEquipe é do filtro de Equipe, que nem sempre é selecionado.
		}
		if(seqEspecialidade != null){
			especialidadeEquipe = aghuFacade.obterAghEspecialidadesPorChavePrimaria(seqEspecialidade);
			agenda.setEspecialidade(especialidadeEquipe);
		}else{
			especialidadeEquipe = agenda.getEspecialidade();
		}
		readOnlyLadoCirurgia = false;
		isInclusao = true;
		if(paciente != null){
			AinInternacao internacao = internacaoFacade.obrterInternacaoPorPacienteInternado(paciente.getCodigo());
			if(internacao != null){
				leito = internacao.getLeito();
			} else {
				leito = null;
			}		}
		if(procedimento != null){
			abaOpmesController.setRenderizaProcedimentoSus();
		}
		//this.agendaHemoterapias = agenda.getAgendasHemoterapias();
		this.agendaHemoterapias = blocoCirurgicoPortalPlanejamentoFacade.listarAgendasHemoterapiaPorAgendaSeq(agenda.getSeq());
		this.agendaHemoterapiasRemovidas = null;
		abaOutrosProcedimentos.inicio();
		aba1Controller.inicio();
		diagnosticoController.inicio();
		solicEspecController.inicio();
		abaOrteseProteseController.inicio();
		validarLadoCirurgia();
	}
	public String confirmarMultiplosAgendamentos(){
		limparParametros();
		detalhamentoPortalAgendamentoController.buscarDetalhamento();
		return DETALHAMENTO_PORTAL;
	}
	private VMbcProcEsp getCarregarProcedimentoPrincipal() {
		MbcEspecialidadeProcCirgs espProcCirgs = this.blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(agenda.getEspProcCirgs().getId(), new Enum[] {MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS, MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE}, null);
		if (espProcCirgs != null) {
			Short espSeq = null;
			if(especialidadeEquipe != null) {
				espSeq = especialidadeEquipe.getSeq();
			}
			List<VMbcProcEsp> procedimentos = blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEsp(espProcCirgs.getMbcProcedimentoCirurgicos().getSeq().toString(), espSeq, maxRegitrosEsp);
			if(procedimentos != null && !procedimentos.isEmpty()) {
				return procedimentos.get(0);
			}			}
		return null;
	}
	public String buscaListaCirurgia(){
		if(listAgendas.get(0).getIndSituacao().equals(DominioSituacaoAgendas.CA)){
			return "Lista de Cancelados";
		}else if(listAgendas.get(0).getIndSituacao().equals(DominioSituacaoAgendas.LE)){
			return "Lista de Espera";}
		return null;
	}
	public List<AinLeitos> pesquisarLeito(String objParam) throws ApplicationBusinessException {
		List<AinLeitos> leitos = new ArrayList<AinLeitos>();
		List<AinLeitos> leitosAux = new ArrayList<AinLeitos>();
		String strPesquisa = (String) objParam;
		if (!StringUtils.isBlank(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
		}
		leitos = this.internacaoFacade.obterLeitosAtivosPorUnf(strPesquisa, null);
		for(AinLeitos leito : leitos){
			if(leito.getInternacao() != null && leito.getInternacao().getIndPacienteInternado()){
				leitosAux.add(leito);
			}		}
		return this.returnSGWithCount(leitosAux,pesquisarLeitoCount(objParam));
	}
	public Integer pesquisarLeitoCount(String objParam) throws ApplicationBusinessException {
		List<AinLeitos> leitos = new ArrayList<AinLeitos>();
		List<AinLeitos> leitosAux = new ArrayList<AinLeitos>();
		String strPesquisa = (String) objParam;
		if (!StringUtils.isBlank(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
		}
		leitos = this.internacaoFacade.obterLeitosAtivosPorUnf(strPesquisa, null);
		for(AinLeitos leito : leitos){
			if(leito.getInternacao() != null && leito.getInternacao().getIndPacienteInternado()){
				leitosAux.add(leito);
			}
		}
		return leitosAux != null ? leitosAux.size() : null;
	}
	public void carregarPaciente(){
		if(leito != null){
			paciente = leito.getInternacao().getPaciente();
			codPac = paciente.getCodigo();
			prontuario = paciente.getProntuario();
		}	}
	public List<VMbcProcEsp> pesquisarProcedimento(String objParam) {
		Short espSeq = null;
		if(especialidadeEquipe != null) {
			espSeq = especialidadeEquipe.getSeq();
		}
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEsp(objParam, espSeq, maxRegitrosEsp), pesquisarProcedimentoCount(objParam));
	}
	public Long pesquisarProcedimentoCount(String objParam) {
		Short espSeq = null;
		if(especialidadeEquipe != null) {
			espSeq = especialidadeEquipe.getSeq();
		}
		return blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEspCount(objParam, espSeq);
	}
	public void executarONS() throws ParseException{ //utilizado em posSelectionAction da suggestion  de Procedimento Principal	
		validarLadoCirurgia();
		validarTempoSala();
		validarRegimeSUS();
		this.executarLimparProcPrincipal();
		if (DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			this.setAgendaHemoterapias(blocoCirurgicoFacade.buscarAgendaHemoterapia(this.getProcedimento().getId().getPciSeq(), this.getProcedimento().getId().getEspSeq()));
			if (this.getAgendaHemoterapias() != null && !this.getAgendaHemoterapias().isEmpty() && isInclusao) {
				this.apresentarMsgNegocio(Severity.INFO, "MBC_01012_1");
			}		
		}
		agenda.setEspProcCirgs(mbcEspecialidadeProcCirgs); //Setada EspProcCirgs na agenda para ser salvo procedimento principal.
		agenda.setProcedimentoCirurgico(mbcEspecialidadeProcCirgs.getMbcProcedimentoCirurgicos()); //Setado procedimento na agenda para contornar cache do hibernate
		getProcedimentoSUS();
	}
	public void executarLimparProcPrincipal() {
		this.abaOpmesController.setAlterouOPME(true);
		this.abaOpmesController.setProcedimentoSus(null);
		if(this.getAgendaHemoterapias() != null && !getAgendaHemoterapias().isEmpty()){
			for (final MbcAgendaHemoterapia adgHemo : this.getAgendaHemoterapias()) {
				if(adgHemo.getId() != null){
					if(this.agendaHemoterapiasRemovidas == null){
						this.agendaHemoterapiasRemovidas = new ArrayList<MbcAgendaHemoterapia>();
					}
					this.agendaHemoterapiasRemovidas.add(adgHemo);
				}
			}
		}
		setAgendaHemoterapias(null);
		agenda.setEspProcCirgs(null);
		agenda.setProcedimentoCirurgico(null);
	}
	public void limparJustificativa() {
		if(Boolean.FALSE.equals(agenda.getIndPrioridade())) { agenda.setJustifPrioridade(null);}
	}
	private void validarLadoCirurgia() { //ON2
		Boolean procedimentoladoCirurgico = null;
		if(procedimento != null) {
			MbcEspecialidadeProcCirgsId id = new MbcEspecialidadeProcCirgsId(procedimento.getId().getPciSeq(),procedimento.getId().getEspSeq());
			mbcEspecialidadeProcCirgs = blocoCirurgicoCadastroApoioFacade.obterEspecialidadeProcedimentoCirurgico(id, new Enum[] {MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS}, null);
			if(mbcEspecialidadeProcCirgs!= null && mbcEspecialidadeProcCirgs.getMbcProcedimentoCirurgicos()!=null){
				procedimentoladoCirurgico = mbcEspecialidadeProcCirgs.getMbcProcedimentoCirurgicos().getLadoCirurgia();
			}
		}
		if(procedimento == null || (procedimento != null && procedimentoladoCirurgico != null && !procedimentoladoCirurgico)){
			agenda.setLadoCirurgia(null);
			readOnlyLadoCirurgia = true;
		}else{
			readOnlyLadoCirurgia = false; }
	}
	public void executarON3() {
		if(agenda.getTempoSala() != null && procedimento != null){
			StringBuffer strTempoMinimo = new StringBuffer();
			strTempoMinimo.append(procedimento.getTempoMinimo());
			while (strTempoMinimo.length() < TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS) {
				strTempoMinimo.insert(0, "0"); // Coloca zeros a esquerda
			}
			String strHora = strTempoMinimo.substring(0, 2);
			String strMinuto = strTempoMinimo.substring(2, TAMANHO_MAXIMO_SHORT_HORAS_MINUTOS);
			Short hora = Short.valueOf(strHora);
			Short minuto = Short.valueOf(strMinuto);
			Short procMinutos = (short) (hora * Short.valueOf("60") + minuto);
			GregorianCalendar gc = new GregorianCalendar();
			gc.set(0, 0, 0, hora, minuto, 0);
			GregorianCalendar gc2 = new GregorianCalendar();
			gc2.setTime(agenda.getTempoSala());
			Integer minutos = gc2.get(Calendar.HOUR_OF_DAY)*60 + gc2.get(Calendar.MINUTE);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			if(minutos < procMinutos){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ALTERACAO_TEMPO_SALA",sdf.format(agenda.getTempoSala()), sdf.format(gc.getTime()), procedimento.getDescricao());
				agenda.setTempoSala(gc.getTime());
			}		}
	}
	public String getTempoProcedimentoFormatado(VMbcProcEsp vMbcProcEsp) throws ParseException {
		StringBuffer tempo = new StringBuffer(vMbcProcEsp.getTempoMinimo().toString());
		while (tempo.length() < 4) {
			tempo.insert(0, "0"); // Coloca zeros a esquerda
		}
		DateFormat formatacaoBanco = new SimpleDateFormat("HHmm");
		DateFormat formatacaoTela = new SimpleDateFormat("HH:mm");
		Date dataFormatadaBanco = formatacaoBanco.parse(tempo.toString());
		return formatacaoTela.format(dataFormatadaBanco);
	}
	private void validarTempoSala() throws ParseException { // ON3
		TempoSalaAgendaVO tempoSala = blocoCirurgicoFacade.validaTempoMinimo(agenda.getTempoSala(), procedimento);
		agenda.setTempoSala(tempoSala.getTempo());
		if (tempoSala != null && tempoSala.getInfo() != null) {
			apresentarMsgNegocio(tempoSala.getInfo(), tempoSala.getMensagem(), tempoSala.getTempoSalaFormatada(), tempoSala.getDataFormatada(), tempoSala.getDescricaoProcedimento());
		}
	}
	public void executarON4() {
		if(procedimento != null){
			if(regimeAnterior != null && agenda.getRegime() != null){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ALTERACAO_REGIME",String.valueOf(regimeAnterior.getDescricao()),String.valueOf(agenda.getRegime().getDescricao()), procedimento.getDescricao());
				regimeAnterior = agenda.getRegime();
			} else if(regimeAnterior == null && agenda.getRegime() != null){
				regimeAnterior = agenda.getRegime();
			}
		}
	}
	private void validarRegimeSUS() { //ON4
		RegimeProcedimentoAgendaVO regimeSus = blocoCirurgicoFacade.populaRegimeSus(agenda.getRegime(), procedimento);
		agenda.setRegime(regimeSus.getRegime());
		regimeAnterior = regimeSus.getRegime();
		if (regimeSus != null && regimeSus.getSeveridade() != null) {
			apresentarMsgNegocio(regimeSus.getSeveridade(), regimeSus.getMensagem(), regimeSus.getDescricaoRegime(), regimeSus.getDescricaoRegimeProcedSus(), regimeSus.getDescricaoProc());}
	}
	public Boolean verificarExisteSolicEspApCongelacao(Integer agdSeq){
		AghParametros parametro;
		try {
			if(parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_AP_CONGELACAO)){
				parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AP_CONGELACAO);
			}else{return false;}
		} catch (ApplicationBusinessException e) {
			return false;
		}
		return blocoCirurgicoPortalPlanejamentoFacade.verificarExisteSolicEspApCongelacao(agdSeq,parametro.getVlrNumerico());
	}
	public List<MbcProcPorEquipe> pesquisarProcedimentosEquipe(){
		return blocoCirurgicoPortalPlanejamentoFacade.pesquisarProcedimentosEquipe(matriculaEquipe,vinCodigoEquipe,unfSeqEquipe);
	}
	public void setarProcedPrincipPorProcedEquipe() throws ParseException{
		Short espSeq = null;
		if(mbcProcPorEquipe != null){
			if(especialidadeEquipe != null) {
				espSeq = especialidadeEquipe.getSeq();}
			List<VMbcProcEsp> procedimentos = blocoCirurgicoPortalPlanejamentoFacade.pesquisarVMbcProcEspPorEsp(mbcProcPorEquipe.getMbcProcedimentoCirurgicos().getSeq().toString(), espSeq, maxRegitrosEsp);
			if(procedimentos.size() > 0){
				setProcedimento(procedimentos.get(0));
				executarONS();
				mbcProcPorEquipe = null;
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MBC_00798_1", especialidadeEquipe.getNomeEspecialidade());
				procedimento = null;}
		}
	}
	public void carregarEspecialidadesParaEquipe() {
		if (getPesquisarEspecialidadeEquipeSalaCount() == 1) {
			readOnlyEspecialidadeEquipe = true; 
			List<AghEspecialidades> especialidades = getPesquisarEspecialidadeParaEquipe(null);
			setEspecialidadeEquipe(especialidades.get(0));
			agenda.setEspecialidade(getEspecialidadeEquipe());} 
	}
	public List<AghEspecialidades> getPesquisarEspecialidadeParaEquipe(String param) {
		return this.returnSGWithCount(this.aghuFacade.obterEspecialidadePorProfissionalAmbInt((String) param, agenda.getProfAtuaUnidCirgs().getId().getSerMatricula(), agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo()), getPesquisarEspecialidadeParaEquipeCount(param));
	}
	public Long getPesquisarEspecialidadeParaEquipeCount(String param) {
		return this.aghuFacade.obterEspecialidadePorProfissionalAmbIntCount((String) param, agenda.getProfAtuaUnidCirgs().getId().getSerMatricula(), agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo());
	}
	public Long getPesquisarEspecialidadeEquipeSalaCount() {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarEspecialidadeEquipeSalaCount(agenda.getSalaCirurgica(), agenda.getProfAtuaUnidCirgs(), new Date(dataAgenda));		
	}
	public String cancelar(){
		final String retorno =  this.cameFrom;
		abaOpmesController.limpar();
		if(agenda.getSeq() != null) {
			agenda = this.blocoCirurgicoFacadeBean.obterAgendaPorAgdSeq(agenda.getSeq());}
		if (retorno != null && !StringUtils.isBlank(retorno)) {
			if (DETALHAMENTO_PORTAL.equals(cameFrom)) {
				this.detalhamentoPortalAgendamentoController.buscarDetalhamento();
				this.detalhamentoPortalAgendamentoController.selecionarItemAgenda(agenda.getSeq());}
			this.limparParametros();
			return retorno;
		} else {
			if(PESQUISA_PACIENTE_CIRURGIA.equals(retorno)){
				this.limparParametros();
				return PESQUISA_PACIENTE_CIRURGIA;}
			if(TELA_AGENDAMENTO.equals(retorno)){
				clicouCancelar = true;
				this.limparParametros();
				return TELA_AGENDAMENTO;}
			if(ATUALIZAR_ESCALA.equals(retorno)){
				this.limparParametros();
				return ATUALIZAR_ESCALA;}
			if(TELA_LISTA_ESPERA.equals(retorno)) {
				this.limparParametros();
				return TELA_LISTA_ESPERA;}
			this.limparParametros();
			return MOCK_PORTAL;
		}
	}
	private void getProcedimentoSUS() {
		abaOpmesController.setRenderizaProcedimentoSus();
		List<FatItensProcedHospitalar> procedimentosSUS = abaOpmesController.pesquisarProcedimentoSus("");
		if(procedimentosSUS != null && procedimentosSUS.size() == 1){
			abaOpmesController.setProcedimentoSus(procedimentosSUS.get(0));
			abaOpmesController.criaLista();
		}
	}
	private void limparParametros(){
		this.agenda = new MbcAgendas();
		this.seqEspecialidade= null;
		this.matriculaEquipe= null;
		this.vinCodigoEquipe= null;
		this.unfSeqEquipe= null;
		this.indFuncaoProfEquipe= null;
		this.seqUnidFuncionalCirugica= null;
		this.dataAgenda= null; 	
		this.agdSeq= null;
		this.situacaoAgendaParam= null;
		this.salaSeqp= null;
		this.clicouCancelar= null;
		this.especialidadeEquipe= null;
		this.listAgendas= null;
		this.codPac = null;
		this.prontuario= null;
		this.paciente= null;
		this.pacCodigoFonetica= null;
		this.leito= null;
		this.procedimento= null;
		this.mbcEspecialidadeProcCirgs= null;
		this.mbcProcPorEquipe= null;
		this.selectedTab= null;
		this.exibeAbaAnestesia = true; 
		this.exibeAbaOutrosProcedimentos = true; 
		this.exibeAbaDiagnostico = true; 
		this.exibeAbaReservaHemoterapica = true;
		this.exibeAbaSolicitacoesEspeciais = true;
		this.exibeAbaOrteseProtese = true;
		this.exibeCheckApCongelacao = true;
		this.exibeAbaDiagnosticos = true;
		this.cameFromPesqPacOuListaEspera = false;
		this.apCongelacao = false;
		this.readOnlyCamposPorON5 = false; 
		this.readOnlyLadoCirurgia = false; 
		this.readOnlyEspecialidadeEquipe = true;
		this.readOnlyBotaoHistoricoAgenda = false;
		this.readOnlyInputQtdeUnidade = false;
		this.habilitarBotaoGravar = true;
		this.renderBotaoVoltar = false;
		this.isInclusao= null;
		this.agendaHemoterapias = null;
		this.agendaHemoterapiasRemovidas = null;
		this.reservaSelecionada = null;
		this.exibirModalAgendamento = false;
		this.diagnosticoController.limparParametros(); // Controller
		this.aba1Controller.limparParametros(); // Aba Anestesia
		this.abaOutrosProcedimentos.limparParametros(); // Aba outros procedimentos
		this.solicEspecController.limparCampos(); // Aba solicitacao especial
		this.abaOrteseProteseController.limparCampos(); // Aba Órtese/Prótese
	}
	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		if (pacCodigoFonetica != null) {
			this.pacCodigoFonetica = pacCodigoFonetica;
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			this.codPac = paciente.getCodigo();
			this.prontuario = paciente.getProntuario();
			carregarLeito();
		}
	}
	public Integer getCodPac() {return codPac;}
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
	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}
	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	public AinLeitos getLeito() {
		return leito;
	}
	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}
	public MbcAgendas getAgenda() {
		return agenda;
	}
	public void setAgenda(MbcAgendas agenda) {
		this.agenda = agenda;
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
	public List<MbcAgendas> getListAgendas() {
		return listAgendas;
	}
	public void setListAgendas(List<MbcAgendas> listAgendas) {
		this.listAgendas = listAgendas;
	}
	public String getCameFrom() {
		return cameFrom;
	}
	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	public Boolean getExibeAbaAnestesia() {
		return exibeAbaAnestesia;
	}
	public void setExibeAbaAnestesia(Boolean exibeAbaAnestesia) {
		this.exibeAbaAnestesia = exibeAbaAnestesia;
	}
	public Boolean getExibeAbaOutrosProcedimentos() {
		return exibeAbaOutrosProcedimentos;
	}
	public void setExibeAbaOutrosProcedimentos(Boolean exibeAbaOutrosProcedimentos) {
		this.exibeAbaOutrosProcedimentos = exibeAbaOutrosProcedimentos;
	}
	public Boolean getExibeAbaDiagnostico() {
		return exibeAbaDiagnostico;
	}
	public void setExibeAbaDiagnostico(Boolean exibeAbaDiagnostico) {
		this.exibeAbaDiagnostico = exibeAbaDiagnostico;
	}
	public Boolean getExibeAbaReservaHemoterapica() {
		return exibeAbaReservaHemoterapica;
	}
	public void setExibeAbaReservaHemoterapica(Boolean exibeAbaReservaHemoterapica) {
		this.exibeAbaReservaHemoterapica = exibeAbaReservaHemoterapica;
	}
	public Boolean getExibeAbaSolicitacoesEspeciais() {
		return exibeAbaSolicitacoesEspeciais;
	}
	public void setExibeAbaSolicitacoesEspeciais(Boolean exibeAbaSolicitacoesEspeciais) {
		this.exibeAbaSolicitacoesEspeciais = exibeAbaSolicitacoesEspeciais;
	}
	public Boolean getExibeAbaOrteseProtese() {return exibeAbaOrteseProtese;}
	public void setExibeAbaOrteseProtese(Boolean exibeAbaOrteseProtese) {this.exibeAbaOrteseProtese = exibeAbaOrteseProtese;}
	public Boolean getExibeCheckApCongelacao() {return exibeCheckApCongelacao;}
	public void setExibeCheckApCongelacao(Boolean exibeCheckApCongelacao) {this.exibeCheckApCongelacao = exibeCheckApCongelacao;}
	public Boolean getReadOnlyCamposPorON5() {return readOnlyCamposPorON5;}
	public void setReadOnlyCamposPorON5(Boolean readOnlyCamposPorON5) {this.readOnlyCamposPorON5 = readOnlyCamposPorON5;}
	public Boolean getReadOnlyLadoCirurgia() {return readOnlyLadoCirurgia;}
	public void setReadOnlyLadoCirurgia(Boolean readOnlyLadoCirurgia) {this.readOnlyLadoCirurgia = readOnlyLadoCirurgia;}
	public MbcProcPorEquipe getMbcProcPorEquipe() {return mbcProcPorEquipe;}
	public void setMbcProcPorEquipe(MbcProcPorEquipe mbcProcPorEquipe) {this.mbcProcPorEquipe = mbcProcPorEquipe;}
	public Long getDataAgenda() {return dataAgenda;}
	public void setDataAgenda(Long dataAgenda) {this.dataAgenda = dataAgenda;}
	public Integer getPacCodigoFonetica() {return pacCodigoFonetica;}
	public Integer getSelectedTab() {return selectedTab;}
	public void setSelectedTab(Integer selectedTab) {this.selectedTab = selectedTab;}
	public VMbcProcEsp getProcedimento() {return procedimento;}
	public void setProcedimento(VMbcProcEsp procedimento) {this.procedimento = procedimento;}
	public void setAgendaHemoterapias(Set<MbcAgendaHemoterapia> agendaHemoterapias) {this.agendaHemoterapias = agendaHemoterapias;}
	public Set<MbcAgendaHemoterapia> getAgendaHemoterapias() {return agendaHemoterapias;}
	public void setAgendaHemoterapiasRemovidas(List<MbcAgendaHemoterapia> agendaHemoterapiasRemovidas) {this.agendaHemoterapiasRemovidas = agendaHemoterapiasRemovidas;}
	public List<MbcAgendaHemoterapia> getAgendaHemoterapiasRemovidas() {return agendaHemoterapiasRemovidas;}
	public Boolean getApCongelacao() {return apCongelacao;}
	public void setApCongelacao(Boolean apCongelacao) {this.apCongelacao = apCongelacao;}
	public Integer getAgdSeq() {return agdSeq;}
	public void setAgdSeq(Integer agdSeq) {this.agdSeq = agdSeq;}
	public MbcEspecialidadeProcCirgs getMbcEspecialidadeProcCirgs() {return mbcEspecialidadeProcCirgs;}
	public void setMbcEspecialidadeProcCirgs(MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs) {this.mbcEspecialidadeProcCirgs = mbcEspecialidadeProcCirgs;}
	public Boolean getIsInclusao() {return isInclusao;}
	public void setIsInclusao(Boolean isInclusao) {this.isInclusao = isInclusao;}
	public String getSituacaoAgendaParam() {return situacaoAgendaParam;}
	public void setSituacaoAgendaParam(String situacaoAgendaParam) {this.situacaoAgendaParam = situacaoAgendaParam;}
	public AghEspecialidades getEspecialidadeEquipe() {return especialidadeEquipe;}
	public void setEspecialidadeEquipe(AghEspecialidades especialidadeEquipe) {this.especialidadeEquipe = especialidadeEquipe;}
	public void setSalaSeqp(Short salaSeqp) {this.salaSeqp = salaSeqp;}
	public Short getSalaSeqp() {return salaSeqp;}
	public Boolean getReadOnlyEspecialidadeEquipe() {return readOnlyEspecialidadeEquipe;}
	public void setReadOnlyEspecialidadeEquipe(Boolean readOnlyEspecialidadeEquipe) {this.readOnlyEspecialidadeEquipe = readOnlyEspecialidadeEquipe;}
	public Boolean getCameFromPesqPacOuListaEspera() {return cameFromPesqPacOuListaEspera;}
	public void setCameFromPesqPacOuListaEspera(Boolean cameFromPesqPacOuListaEspera) {this.cameFromPesqPacOuListaEspera = cameFromPesqPacOuListaEspera;}
	public Boolean getClicouCancelar() {return clicouCancelar;}
	public void setClicouCancelar(Boolean clicouCancelar) {this.clicouCancelar = clicouCancelar;}
	public Boolean getExibeAbaDiagnosticos() {return exibeAbaDiagnosticos;}
	public void setExibeAbaDiagnosticos(Boolean exibeAbaDiagnosticos) {this.exibeAbaDiagnosticos = exibeAbaDiagnosticos;}
	public Boolean getReadOnlyBotaoHistoricoAgenda() {return readOnlyBotaoHistoricoAgenda;}
	public void setReadOnlyBotaoHistoricoAgenda(Boolean readOnlyBotaoHistoricoAgenda) {this.readOnlyBotaoHistoricoAgenda = readOnlyBotaoHistoricoAgenda;}
	public Boolean getReadOnlyInputQtdeUnidade() {return readOnlyInputQtdeUnidade;}
	public void setReadOnlyInputQtdeUnidade(Boolean readOnlyInputQtdeUnidade) {this.readOnlyInputQtdeUnidade = readOnlyInputQtdeUnidade;}
	public Boolean getHabilitarBotaoGravar() {return habilitarBotaoGravar;}
	public void setHabilitarBotaoGravar(Boolean habilitarBotaoGravar) {this.habilitarBotaoGravar = habilitarBotaoGravar;}
	public Boolean getRenderBotaoVoltar() {return renderBotaoVoltar;}
	public void setReservaSelecionada(MbcAgendaHemoterapia reservaSelecionada) {this.reservaSelecionada = reservaSelecionada;}
	public MbcAgendaHemoterapia getReservaSelecionada() {return this.reservaSelecionada;}
	public Integer getAbaAberta() {return abaAberta;}
	public void setAbaAberta(Integer abaAberta) {this.abaAberta = abaAberta;}
	public Boolean getLimpaOpme() {return limpaOpme;}
	public void setLimpaOpme(Boolean limpaOpme) {this.limpaOpme = limpaOpme;}
	public DominioRegimeProcedimentoCirurgicoSus getRegimeAnterior() {
		return regimeAnterior;
	}
	public void setRegimeAnterior(DominioRegimeProcedimentoCirurgicoSus regimeAnterior) {
		this.regimeAnterior = regimeAnterior;
	}
}