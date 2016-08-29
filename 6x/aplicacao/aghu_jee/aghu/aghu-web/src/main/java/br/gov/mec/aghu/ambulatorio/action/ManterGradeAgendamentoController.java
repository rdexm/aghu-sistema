package br.gov.mec.aghu.ambulatorio.action;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacCaracteristicaGradeId;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.AacGradeProcedHospitalarId;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacGradeSituacaoId;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.prescricaomedica.vo.FormaAgendamentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


@SuppressWarnings({"PMD.AghuTooManyMethods","PMD.ExcessiveClassLength"})
public class ManterGradeAgendamentoController extends ActionController  {

	private static final String SITUACAO_GRADE_AGENDAMENTO_SEM_ABA = "situacaoGradeAgendamentoSemAba";
	private static final String MANTER_GRADE_AGENDAMENTO = "manterGradeAgendamento";
	private static final String CONSULTAR_GRADE_AGENDAMENTO = "consultarGradeAgendamento";
	private static final Log LOG = LogFactory.getLog(ManterGradeAgendamentoController.class);
	private static final long serialVersionUID = -5859654119680277136L;
	

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AacGradeAgendamenConsultas gradeAgendamenConsultas;
	private AacGradeAgendamenConsultas oldGradeAgendamenConsultas;
	private AacHorarioGradeConsulta horarioGradeConsulta;
	//lista
	private List<AacHorarioGradeConsulta> listaHorarioGradeConsulta;
	//private AacHorarioGradeConsulta oldHorarioGradeConsulta;
	private List<AacHorarioGradeConsulta> horarioGradeConsultaList;
	private EspCrmVO espCrmVO;
	private String parametroZona;
	private String titleParametroZona;
	private Short parametroProjetoPesquisa;
	private String parametroSala;
	private String descricaoGrade;

	private FormaAgendamentoVO formaAgendamentoAba1;
	private FormaAgendamentoVO formaAgendamentoAba2;
	
	private List<AacPagador> pagadorList;
	
	private List<AacGradeProcedHospitalar> procedimentos;	
	private AacProcedHospEspecialidades procedimento;
	
	private List<String> caracteristicas;	
	private List<AacCaracteristicaGrade> caracteristicasGrade;
	private String caracteristica;
	private Integer seqCaracteristica;
	
	private List<AacGradeSituacao> situacoes;
	private DominioSituacao situacao;
	private Date dtInicio;
	private DominioSituacao indSituacao;
	
	private Date dataInicial;
	private Date dataFinal;
	
	private Boolean existeConsultas;
	private Boolean gradeSisreg;
	
	private String selectedTab;
	
	private Integer currentTabIndex;
	
	private Boolean gradeNova = false;
	
	//Dias da semana na drid
	private boolean dom;
	private boolean seg;
	private boolean ter;
	private boolean qua;
	private boolean qui;
	private boolean sex;
	private boolean sab;
	
	private boolean acaoEditar;
	
	private AacGradeAgendamenConsultas gradeAgendamenConsultaCopia;
	private boolean gradeCopiada;
	private String mensagemConfirmacaoPendencias;
	private boolean habilitarAbasSecundariaGrade;
	private Integer seqGradeAnteriorCopia;
	private boolean houveCopiaDeGrade;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		if (this.gradeAgendamenConsultas != null && this.gradeAgendamenConsultas.getSeq() != null) {
			this.habilitarAbasSecundariaGrade = true;
			this.seqCaracteristica = this.gradeAgendamenConsultas.getSeq(); 
		}else if (gradeAgendamenConsultaCopia != null && this.gradeAgendamenConsultaCopia.getSeq() != null) {
			this.seqCaracteristica = this.gradeAgendamenConsultaCopia.getSeq(); 
			this.habilitarAbasSecundariaGrade = false;
		}
	}

	
	public void inicio() {
		acaoEditar=false;
		setFalseTodosDias();
		if (gradeAgendamenConsultas==null){
			this.habilitarAbasSecundariaGrade = false;
			oldGradeAgendamenConsultas = null;
			gradeAgendamenConsultas=new AacGradeAgendamenConsultas();
			gradeAgendamenConsultas.setEmiteTicket(true);
			gradeAgendamenConsultas.setEnviaSamis(true);
			gradeAgendamenConsultas.setIndAvisaConsultaTurno(true);
			formaAgendamentoAba1=new FormaAgendamentoVO();
			formaAgendamentoAba2=new FormaAgendamentoVO();
			popularCombosAba1();
			popularCombosAba2(null);
			horarioGradeConsultaList=new ArrayList<AacHorarioGradeConsulta>();
			procedimentos = new ArrayList<AacGradeProcedHospitalar>();
			caracteristicasGrade = new ArrayList<AacCaracteristicaGrade>();		
			situacoes =  new ArrayList<AacGradeSituacao>();
			espCrmVO=null;
			criarAgendamento();
			existeConsultas=false;	
			gradeSisreg = false;
			seqGradeAnteriorCopia = null;
			houveCopiaDeGrade = false;
		}
		this.indSituacao=null;
		this.dtInicio=null;
		buscarParametros();
		dataInicial=null;
		dataFinal=null;
		caracteristicas = this.ambulatorioFacade.listarCaracteristicas();
	
	}

	
	public void popularCombosAba1(){
		formaAgendamentoAba1.set(
			ambulatorioFacade.pesquisaPagadoresComAgendamento(),
			ambulatorioFacade.pesquisaTipoAgendamentoComAgendamentoEPagador(formaAgendamentoAba1.getPagador()),
			ambulatorioFacade.pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(formaAgendamentoAba1.getPagador(), 
				formaAgendamentoAba1.getTipoAgendamento()));
	}
	
	
	public void popularCombosAba2(String field) {
		if (formaAgendamentoAba2==null){
			formaAgendamentoAba2=new FormaAgendamentoVO();
		}
		
		if ("PAGADOR".equals(field)){
			formaAgendamentoAba2.setTipoAgendamento(null);
			formaAgendamentoAba2.setCondicaoAtendimento(null);
		}else if("TIPO".equals(field)){
			if (formaAgendamentoAba2.getPagador()==null){
				formaAgendamentoAba2.setTipoAgendamento(null);
			}			
			formaAgendamentoAba2.setCondicaoAtendimento(null);			
		}
		formaAgendamentoAba2.set(
			ambulatorioFacade.pesquisaPagadoresComAgendamento(),
			ambulatorioFacade.pesquisaTipoAgendamentoComAgendamentoEPagador(formaAgendamentoAba2.getPagador()),
			ambulatorioFacade.pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(formaAgendamentoAba2.getPagador(), 
				formaAgendamentoAba2.getTipoAgendamento()));
	}	
	
	
	public void buscarParametros(){
		try {
			AghParametros p1 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			String zona=p1!=null?p1.getVlrTexto():"Zona";
			AghParametros p2 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA);
			String sala=p2!=null?p2.getVlrTexto():"Sala";
			parametroZona=zona+"/"+sala;			
			String message = WebUtil.initLocalizedMessage("TITLE_MANTER_GRADE_AGENDAMENTO_ZONA", null);
			this.titleParametroZona = MessageFormat.format(message, this.parametroZona);
			
			AghParametros p3 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_VALIDA_PROJETO_PESQUISA);
			parametroProjetoPesquisa = p3!=null?p3.getVlrNumerico().shortValue():null;
			
			this.parametroSala = sala;
		} catch (ApplicationBusinessException e) {
			parametroZona="Zona/Sala";
			parametroProjetoPesquisa=null;		
			String message = WebUtil.initLocalizedMessage("TITLE_MANTER_GRADE_AGENDAMENTO_ZONA", null);
			this.titleParametroZona = MessageFormat.format(message, this.parametroZona);
		}
		
	}
	
	public String voltarLista(){
		if (!houveCopiaDeGrade){
			seqGradeAnteriorCopia = null;			
		}
		gradeCopiada = false;
		gradeNova = true;
		gradeAgendamenConsultas = null;
		return CONSULTAR_GRADE_AGENDAMENTO;
	}	
	
	
	//--[ACTIONS]	
	public void salvar() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		boolean novo = gradeAgendamenConsultas.getSeq()==null;
		gradeNova = false;
		try {
			formaAgendamentoAba1.insereNaGrade(gradeAgendamenConsultas);
			gradeAgendamenConsultas.setIndRefCompleta("N");
			if(gradeAgendamenConsultas.getIndSituacao() == null){
				gradeAgendamenConsultas.setIndSituacao(DominioSituacao.A);
			}
				
			if (espCrmVO!=null) {				
				AghProfEspecialidades prof = aghuFacade.findProfEspecialidadesById(espCrmVO.getMatricula(),espCrmVO.getVinCodigo(), espCrmVO.getEspSeq());
				ambulatorioFacade.validaCRMAmbulatorio(prof, gradeAgendamenConsultas);
			}else{
				gradeAgendamenConsultas.setProfEspecialidade(null);
			}
			if (gradeAgendamenConsultas.getSiglaUnfSala().getId().getSala()!=null){
				gradeAgendamenConsultas.setAacUnidFuncionalSala(ambulatorioFacade.obterUnidadeFuncional(
						gradeAgendamenConsultas.getSiglaUnfSala().getId().getUnfSeq(),
						gradeAgendamenConsultas.getSiglaUnfSala().getId().getSala()));
			}else{
				gradeAgendamenConsultas.setAacUnidFuncionalSala(null);
			}
			
			if (gradeCopiada && gradeAgendamenConsultaCopia != null) {
				
				gradeAgendamenConsultaCopia.setEspecialidade(gradeAgendamenConsultas.getEspecialidade());
				gradeAgendamenConsultaCopia.setProfEspecialidade(gradeAgendamenConsultas.getProfEspecialidade());
				gradeAgendamenConsultaCopia.setEquipe(gradeAgendamenConsultas.getEquipe());
				//gradeAgendamenConsultaCopia.setUslSala(gradeAgendamenConsultas.getAacUnidFuncionalSala().getId().getSala());
				gradeAgendamenConsultaCopia.setIndRefCompleta("N");
				seqGradeAnteriorCopia = gradeAgendamenConsultas.getSeq();
				//grava a copia da grade agendamento
				gradeAgendamenConsultas = ambulatorioFacade.copiarGradeAgendamento(gradeAgendamenConsultaCopia, gradeAgendamenConsultas);
				reiniciarListagens(gradeAgendamenConsultas);
				gradeAgendamenConsultaCopia = null;
				seqCaracteristica = gradeAgendamenConsultas.getSeq();
				houveCopiaDeGrade = true;
			}else {
				ambulatorioFacade.salvarGradeAgendamentoConsulta(gradeAgendamenConsultas,oldGradeAgendamenConsultas);
			}
			//gradeAgendamenConsultas.setVersion(newVersion);
			criarAgendamento();
		
			oldGradeAgendamenConsultas = (AacGradeAgendamenConsultas)BeanUtils.cloneBean(gradeAgendamenConsultas);
			
			if(novo || gradeCopiada) {
				gradeNova = true;
				seqCaracteristica = gradeAgendamenConsultas.getSeq();
			}
			
			
			apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_AGENDAMENTO_SALVO");
			situacoes = this.ambulatorioFacade.listarSituacoesGrade(gradeAgendamenConsultas.getSeq());
			verificarProjetoPesquisa();
			criarAgendamento();
		} catch (BaseException e) {
			if (novo){
				seqCaracteristica=null;
				gradeAgendamenConsultas = null;
				criar();
				this.inicio();
			}
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	
	public void gerarDisponibilidade(){
		try {
			//ambulatorioFacade.refresh(gradeAgendamenConsultas);
			
			try {
				faturamentoFacade.commit(60*60);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e1) {
				LOG.error("Exceção capturada:", e1);
			}
			
			
			Integer count=ambulatorioFacade.gerarDisponibilidade(gradeAgendamenConsultas, dataInicial, dataFinal, nomeMicrocomputador);
			
			gradeAgendamenConsultas = this.ambulatorioFacade.obterGrade(gradeAgendamenConsultas.getSeq());
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_AGENDAMENTO_GERADO", 
					count,sdf.format(dataInicial), sdf.format(dataFinal));
			dataInicial=null;
			dataFinal=null;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			//ambulatorioFacade.evict(gradeAgendamenConsultas);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "Erro ao gerar disponibilidade: " + e.getMessage());			
			LOG.error(e.getMessage(),e);
		}
	}
	
	
	public String criar(){
		gradeAgendamenConsultas=null;
		gradeAgendamenConsultaCopia = null;
		gradeCopiada = true;
		gradeNova = true;
		seqCaracteristica = null;
		inicio();
		return MANTER_GRADE_AGENDAMENTO;
	}
	
	public void copiarGradeAgendamento(){
		gradeNova = true;
		gradeCopiada = true;
		gradeSisreg = false;
		existeConsultas = false;
		this.habilitarAbasSecundariaGrade = false;
		gradeAgendamenConsultas.setAacConsultas(null);
		try {
			gradeAgendamenConsultaCopia = new AacGradeAgendamenConsultas();
			seqCaracteristica= null;
			gradeAgendamenConsultas.setCriadoEm(null);
			gradeAgendamenConsultas.setAlteradoEm(null);
			resetarListasGradeAgendamento();
			inicio();
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			gradeAgendamenConsultaCopia = null;
		}
	
	}
	
	private void resetarListasGradeAgendamento() {
		this.gradeAgendamenConsultaCopia.setHorarioGradeConsulta(new HashSet<AacHorarioGradeConsulta>());
		this.gradeAgendamenConsultaCopia.setGradeProcedimentosHospitalar(new HashSet<AacGradeProcedHospitalar>());
		this.gradeAgendamenConsultaCopia.setCaracteristicaGrade(new HashSet<AacCaracteristicaGrade>());
		this.gradeAgendamenConsultaCopia.setGradeSituacao(new HashSet<AacGradeSituacao>());
	}
	
	
	
	public String editar(Integer entitySeq) throws ApplicationBusinessException {
		houveCopiaDeGrade = false;
		gradeCopiada = true;
		gradeNova = false;
		gradeAgendamenConsultas=ambulatorioFacade.obterGrade(entitySeq);
		setSeqCaracteristica(gradeAgendamenConsultas.getSeq());
		inicio();
		if (gradeAgendamenConsultas.getProfEspecialidade()!=null) {
			obterMedico(gradeAgendamenConsultas.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
		}else{
			espCrmVO=null;
		}
		
		if (formaAgendamentoAba1==null){
			formaAgendamentoAba1=new FormaAgendamentoVO();
			formaAgendamentoAba2=new FormaAgendamentoVO();			
		}
		
		formaAgendamentoAba1.setCondicaoAtendimento(gradeAgendamenConsultas.getCondicaoAtendimento());
		formaAgendamentoAba1.setPagador(gradeAgendamenConsultas.getPagador());
		formaAgendamentoAba1.setTipoAgendamento(gradeAgendamenConsultas.getTipoAgendamento());
			
		popularCombosAba1();
		verificarProjetoPesquisa();
		criarAgendamento();
		existeConsultas = ambulatorioFacade.existeConsultasAgendadas(gradeAgendamenConsultas);
		gradeSisreg = ambulatorioFacade.verificaGradeTipoSisreg(gradeAgendamenConsultas.getSeq());
		
		try {
			oldGradeAgendamenConsultas = (AacGradeAgendamenConsultas)BeanUtils.cloneBean(gradeAgendamenConsultas);
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			oldGradeAgendamenConsultas = null;
		}
		reiniciarListagens(gradeAgendamenConsultas);
		this.gradeAgendamenConsultaCopia = null;
		this.habilitarAbasSecundariaGrade = true;
		return MANTER_GRADE_AGENDAMENTO;
	}
	
	
	private void reiniciarListagens(AacGradeAgendamenConsultas gradeAgendamenConsultas){
		horarioGradeConsultaList = ambulatorioFacade.pesquisarHorariosPorGrade(gradeAgendamenConsultas);		
		procedimentos = this.ambulatorioFacade.listarProcedimentosGrade(gradeAgendamenConsultas.getSeq());
		caracteristicasGrade = this.ambulatorioFacade.listarCaracteristicasGrade(gradeAgendamenConsultas.getSeq());
		situacoes = this.ambulatorioFacade.listarSituacoesGrade(gradeAgendamenConsultas.getSeq());
	}
		

	public String editarSituacao(Integer entitySeq) throws ApplicationBusinessException {
		inicio();
		gradeAgendamenConsultas=ambulatorioFacade.obterGrade(entitySeq);
		if (gradeAgendamenConsultas.getProfEspecialidade()!=null) {
			obterMedico(gradeAgendamenConsultas.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
		}
		
		try {
			oldGradeAgendamenConsultas = (AacGradeAgendamenConsultas)BeanUtils.cloneBean(gradeAgendamenConsultas);
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			oldGradeAgendamenConsultas = null;
		}
		procedimentos = this.ambulatorioFacade.listarProcedimentosGrade(gradeAgendamenConsultas.getSeq());
		caracteristicasGrade = this.ambulatorioFacade.listarCaracteristicasGrade(gradeAgendamenConsultas.getSeq());
		situacoes = this.ambulatorioFacade.listarSituacoesGrade(gradeAgendamenConsultas.getSeq());
		return SITUACAO_GRADE_AGENDAMENTO_SEM_ABA;
	}
	
	
	public void verificarProjetoPesquisa(){
		if (gradeAgendamenConsultas.getProjetoPesquisa()!=null && parametroProjetoPesquisa!=null){
			formaAgendamentoAba1.setPagador(ambulatorioFacade.obterPagador(parametroProjetoPesquisa));
			formaAgendamentoAba1.setIndProjetoPesquisa(true);
			popularCombosAba1();
			
		} else if(formaAgendamentoAba1.getIndProjetoPesquisa()){
			formaAgendamentoAba1.limpaSelecionados();
		}
	}
	
		
	//--[SUGGESTIONS]	
	public List<VAacSiglaUnfSala> obterZonaSala(String objPesquisa) throws BaseException  {		
		List<AghUnidadesFuncionais> undFuncionais = aghuFacade.listarUnidadeFuncionalPorFuncionalSala(objPesquisa);	
		if (undFuncionais.isEmpty()){
			return new ArrayList<VAacSiglaUnfSala>();
		}
		return this.aghuFacade.pesquisarSalasUnidadesFuncionais(undFuncionais, DominioSituacao.A);
	}	
	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidades((String) parametro);
	}	
	
	public void posEspecialidade(){
		if (gradeAgendamenConsultas.getEspecialidade()!=null) {
			gradeAgendamenConsultas.setEmiteBa(gradeAgendamenConsultas.getEspecialidade().getIndEmiteBoletimAtendimento()!=null && gradeAgendamenConsultas.getEspecialidade().getIndEmiteBoletimAtendimento().isSim());
			gradeAgendamenConsultas.setEmiteTicket(gradeAgendamenConsultas.getEspecialidade().getIndEmiteTicket()!=null && gradeAgendamenConsultas.getEspecialidade().getIndEmiteTicket().isSim());
		}	
		
		gradeAgendamenConsultas.setEquipe(null);
		espCrmVO=null;
	}
	
	public List<AghEquipes> obterEquipe(String parametro) {
		if (gradeAgendamenConsultas.getEspecialidade()==null){
			return new ArrayList<AghEquipes>();
		}
		
		List<AghEquipes> resultList = null;
		try {
			resultList = aghuFacade.pesquisarEquipesPorEspecialidadeServidores((String) parametro, gradeAgendamenConsultas.getEspecialidade(), DominioSituacao.A);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		return resultList;
	}	
	
	public void posEquipe(){
		espCrmVO=null;
	}
	
	public List<AacProcedHospEspecialidades> buscarProcedimentos(String parametro) {
		if(gradeAgendamenConsultas.getEspecialidade()!=null){
			return this.returnSGWithCount(this.ambulatorioFacade.listarProcedimentosEspecialidades(parametro, gradeAgendamenConsultas.getEspecialidade()),buscarProcedimentosCount(parametro));	
		} else {
			return null;
		}
	}	
	
	public Long buscarProcedimentosCount(String parametro) {
		Long count = 0l;
		if(gradeAgendamenConsultas.getEspecialidade()!=null){
			count = this.ambulatorioFacade.listarProcedimentosEspecialidadesCount(parametro, gradeAgendamenConsultas.getEspecialidade());	
		} 
		return count;
	}
	

	public List<EspCrmVO> obterMedicos(String parametro) throws ApplicationBusinessException {
		List<Object[]> listaObject = new ArrayList<Object[]>();
		if (gradeAgendamenConsultas.getEspecialidade() != null && gradeAgendamenConsultas.getEquipe() != null){
			listaObject =  aghuFacade.pesquisarEspCrmVOAmbulatorioEquip(parametro, gradeAgendamenConsultas.getEquipe(), gradeAgendamenConsultas.getEspecialidade());
		}
		Set<EspCrmVO> setEspCrmVO = new HashSet<EspCrmVO>();
		for (Object[] objeto : listaObject){
			EspCrmVO espCrmVO = new EspCrmVO();
			espCrmVO.setNomeMedico((String) objeto[0]);
			espCrmVO.setMatricula((Integer) objeto[1]);
			espCrmVO.setVinCodigo((Short) objeto[2]);
			espCrmVO.setEspSeq((Short)objeto[3]);
			setEspCrmVO.add(espCrmVO);
		}
		List<EspCrmVO> listaEspCrmVO = new ArrayList<EspCrmVO>(setEspCrmVO);
		return listaEspCrmVO;
	}	
	
	public void obterMedico(Object parametro) throws ApplicationBusinessException {
		espCrmVO =  solicitacaoInternacaoFacade
						.obterEspCrmVOComAmbulatorio(parametro, gradeAgendamenConsultas.getEspecialidade(), DominioSimNao.S, gradeAgendamenConsultas.getProfEspecialidade()!=null?gradeAgendamenConsultas.getProfEspecialidade().getRapServidor():null);
	}
	
	
	
	public List<AelProjetoPesquisas> obterProjetoPesquisa(String objParam) {
		List<AelProjetoPesquisas> retorno;
		String strPesquisa = (String) objParam;
		retorno = internacaoFacade.pesquisarProjetosPesquisaInternacao(strPesquisa);

		return retorno;
	}	

	
	//--[ABA HORÁRIOS AGENDADOS]
	public void adicionarAgendamento() {	
		if (horarioGradeConsulta.getHoraFim()!=null && !horarioGradeConsulta.getHoraFim().after(horarioGradeConsulta.getHoraInicio())){
			apresentarMsgNegocio(Severity.ERROR,"MSG_MANTER_GRADE_AGENDAMENTO_HORARIO_FIM");
			return;
		}
		horarioGradeConsulta.setFormaAgendamento(ambulatorioFacade
					.findFormaAgendamento(formaAgendamentoAba2
							.getPagador(), formaAgendamentoAba2.getTipoAgendamento(), formaAgendamentoAba2.getCondicaoAtendimento()));
		try{
			ambulatorioFacade.validaHorarioSobreposto(horarioGradeConsulta, gradeAgendamenConsultas);
		} catch (BaseException e) {			
			apresentarMsgNegocio(Severity.WARN,e.getLocalizedMessage(), e.getParameters());			
			LOG.warn(e.getMessage(),e);
		}
		boolean novo = horarioGradeConsulta.getId()==null;
		try {			
			
			ambulatorioFacade.salvarHorarioGradeConsulta(horarioGradeConsulta, gradeAgendamenConsultas);
			horarioGradeConsultaList = ambulatorioFacade.pesquisarHorariosPorGrade(gradeAgendamenConsultas);
			criarAgendamento(horarioGradeConsulta);
			horarioGradeConsulta = new AacHorarioGradeConsulta();
			formaAgendamentoAba2.limpaSelecionados();
			apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_AGENDAMENTO_HORARIO_SALVO");
			acaoEditar=false;
			setFalseTodosDias();
		} catch (BaseException e) {
			if (novo){
				horarioGradeConsulta.setId(null);
			}	
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
	}	
	
	
	//--[ABA HORÁRIOS AGENDADOS(Mehoria #34579)]
			public void adicionaVarios() {
				if (horarioGradeConsulta.getHoraFim()!=null && !horarioGradeConsulta.getHoraFim().after(horarioGradeConsulta.getHoraInicio())){
					apresentarMsgNegocio(Severity.ERROR,"MSG_MANTER_GRADE_AGENDAMENTO_HORARIO_FIM");
					return;
				}
				//busca forma de agendamento a ser setado em todos os dias marcados
				AacFormaAgendamento formaAgendamento = ambulatorioFacade.findFormaAgendamento(formaAgendamentoAba2
						.getPagador(), formaAgendamentoAba2.getTipoAgendamento(), formaAgendamentoAba2.getCondicaoAtendimento());
				
				//seta em uma lista todos os dias que foram selecionados na tela
				setarDiasSemanaSelecionados();
				
				boolean problemaInsererirRegistro=false;
					if(validarCampos()){
					for(AacHorarioGradeConsulta horarioGradeCon:listaHorarioGradeConsulta){
						horarioGradeCon.setFormaAgendamento(formaAgendamento);
						if(!gravarHorarioGrade(horarioGradeCon)){
							problemaInsererirRegistro=true;
						}
					}
					listaHorarioGradeConsulta=null;
					if(!problemaInsererirRegistro){
							apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_AGENDAMENTO_HORARIOS_SALVO");
					}
						setFalseTodosDias();
						horarioGradeConsulta= new AacHorarioGradeConsulta();
					horarioGradeConsultaList = ambulatorioFacade.pesquisarHorariosPorGrade(gradeAgendamenConsultas);
				}
				}
				
			private boolean validarCampos() {
				boolean validos= true;
				if(horarioGradeConsulta.getHoraInicio()==null){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_HORA_INICIO_OBRIGATORIO");					
			}
				if(horarioGradeConsulta.getDuracao()==null){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_DURACAO_OBRIGATORIO");
				}
				if(formaAgendamentoAba2.getPagador()==null){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_PAGADOR_OBRIGATORIO");
				}
				if(formaAgendamentoAba2.getTipoAgendamento()==null){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_TIPO_AGENDAMENTO_OBRIGATORIO");
				}
				if(formaAgendamentoAba2.getCondicaoAtendimento()==null){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_CONDICAO_OBRIGATORIO");
				}
				if(horarioGradeConsulta.getSituacao()==null){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_SITUACAO_OBRIGATORIO");
				}
				if(listaHorarioGradeConsulta==null || listaHorarioGradeConsulta.isEmpty()){
					validos=false;
					apresentarMsgNegocio(Severity.ERROR,"MSG_SELECIONAR_UM_DIA");
				}
				if(validos && !verificaViolacaoRegra()){
					validos=false;
				}
				
		return validos;
	}

			public boolean verificaViolacaoRegra(){
				if (horarioGradeConsulta.getHoraFim() != null && horarioGradeConsulta.getNumHorario() != null) {
					apresentarMsgNegocio(Severity.ERROR,"AAC_00075");
					return false;
				}

				if (horarioGradeConsulta.getHoraFim() == null && horarioGradeConsulta.getNumHorario() == null) {
					apresentarMsgNegocio(Severity.ERROR,"AAC_00076");
					return false;
				}
				return true;
			}

			//retorna falso caso o registro não seja inserido
			private boolean gravarHorarioGrade(
					AacHorarioGradeConsulta horarioGradeCon) {
				try{
					ambulatorioFacade.validaHorarioSobreposto(horarioGradeCon, gradeAgendamenConsultas);
				} catch (BaseException e) {			
					apresentarMsgNegocio(Severity.WARN,e.getLocalizedMessage(), e.getParameters());			
					LOG.warn(e.getMessage(),e);
				}
				boolean novo = horarioGradeCon.getId()==null;
				try {			
					
					ambulatorioFacade.salvarHorarioGradeConsulta(horarioGradeCon, gradeAgendamenConsultas);
					criarAgendamento(horarioGradeCon);
					horarioGradeCon = new AacHorarioGradeConsulta();
					formaAgendamentoAba2.limpaSelecionados();
					return true;
				} catch (BaseException e) {
					if (novo){
						horarioGradeCon.setId(null);
					}	
					apresentarExcecaoNegocio(e);
					LOG.error(e.getMessage(),e);
					return false;
				}
				
			}	
		
	
	
	public void criarAgendamento() {
		criarAgendamento(null);
	}
	
	public void cancelarEdicao() {
		setFalseTodosDias();
		criarAgendamento();
		acaoEditar=false;
		formaAgendamentoAba2.limpaSelecionados();
		
	}
	
	public void criarAgendamento(AacHorarioGradeConsulta old){
		horarioGradeConsulta = new AacHorarioGradeConsulta();
		if (old!=null) {
			horarioGradeConsulta.setHoraInicio((Date)old.getHoraInicio().clone());
			if (old.getHoraFim()!=null){
			horarioGradeConsulta.setHoraFim((Date)old.getHoraFim().clone());
			}
			if (old.getNumHorario()!=null){
				horarioGradeConsulta.setNumHorario(old.getNumHorario().shortValue());
			}	
			horarioGradeConsulta.setDiaSemana(old.getDiaSemana());
			horarioGradeConsulta.setDuracao(old.getDuracao());
			horarioGradeConsulta.setSituacao(old.getSituacao());			
		}else{
			if (formaAgendamentoAba1!=null){
				formaAgendamentoAba2.clonaSelecionados(formaAgendamentoAba1);
			}	
		}
		popularCombosAba2(null);
	}
	
	public void editarAgendamento(AacHorarioGradeConsulta entity) {
		horarioGradeConsulta = entity;
		acaoEditar=true;
		setFalseTodosDias();
		setarDiasSemanaSelecionadosTela(horarioGradeConsulta);
		if (horarioGradeConsulta.getFormaAgendamento()!=null){
			formaAgendamentoAba2.setCondicaoAtendimento(horarioGradeConsulta.getFormaAgendamento().getCondicaoAtendimento());
			formaAgendamentoAba2.setPagador(horarioGradeConsulta.getFormaAgendamento().getPagador());
			formaAgendamentoAba2.setTipoAgendamento(horarioGradeConsulta.getFormaAgendamento().getTipoAgendamento());
		}
		popularCombosAba2(null);
//		try {
//			oldHorarioGradeConsulta = (AacHorarioGradeConsulta)BeanUtils.cloneBean(horarioGradeConsulta);
//		} catch (Exception e) {
//			LOG.error(e.getMessage(),e);
//			oldHorarioGradeConsulta = null;
//		}		
	}
	

	public void removerAgendamento(AacHorarioGradeConsulta horarioGrade) {	
		try {
			acaoEditar=false;
			setFalseTodosDias();
			ambulatorioFacade.excluirHorarioGradeConsulta(horarioGrade);
			horarioGradeConsultaList = ambulatorioFacade.pesquisarHorariosPorGrade(gradeAgendamenConsultas);
			horarioGradeConsulta = new AacHorarioGradeConsulta();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
	}	
	
	public void removerAgendamentoTodos(){
		if(horarioGradeConsultaList!= null && !horarioGradeConsultaList.isEmpty()){
			for(AacHorarioGradeConsulta horario:horarioGradeConsultaList){
				removerAgendamento(horario);
			}
			apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_AGENDAMENTO_HORARIOS_EXCLUIDOS");
			acaoEditar=false;
			horarioGradeConsulta = new AacHorarioGradeConsulta();
			setFalseTodosDias();
			horarioGradeConsultaList = ambulatorioFacade.pesquisarHorariosPorGrade(gradeAgendamenConsultas);
		}
	}
	
	
	//--[ABA PROCEDIMENTOS]
	public void adicionarProcedimento() throws ApplicationBusinessException {
		try {
			if(this.procedimento!=null){
				AacGradeProcedHospitalar gradeProcedHospitalar = new AacGradeProcedHospitalar();
				gradeProcedHospitalar.setGradeAgendamentoConsulta(this.gradeAgendamenConsultas);
				gradeProcedHospitalar.setProcedHospInterno(this.procedimento.getProcedHospInterno());
				AacGradeProcedHospitalarId gradeProcedHospitalarId = new AacGradeProcedHospitalarId();
				gradeProcedHospitalarId.setGrdSeq(this.gradeAgendamenConsultas.getSeq());
				gradeProcedHospitalarId.setPhiSeq(this.procedimento.getProcedHospInterno().getSeq());
				gradeProcedHospitalar.setId(gradeProcedHospitalarId);
				this.ambulatorioFacade.persistirProcedimento(gradeProcedHospitalar);
				this.procedimentos = this.ambulatorioFacade.listarProcedimentosGrade(gradeAgendamenConsultas.getSeq());
				this.procedimento = null;
			}
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
	}
	
	public void adicionarCaracteristica() throws ApplicationBusinessException {
		try{
			if(!StringUtils.isBlank(this.caracteristica)){
				AacCaracteristicaGrade caracteristicaGrade = new AacCaracteristicaGrade();
				AacCaracteristicaGradeId caracteristicaGradeId = new AacCaracteristicaGradeId();
				caracteristicaGradeId.setCaracteristica(this.caracteristica);
				caracteristicaGradeId.setGrdSeq(this.gradeAgendamenConsultas.getSeq());
				caracteristicaGrade.setId(caracteristicaGradeId);
				caracteristicaGrade.setGradeAgendamentoConsulta(this.gradeAgendamenConsultas);
				caracteristicaGrade.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				caracteristicaGrade.setCriadoEm(new Date());
				this.ambulatorioFacade.persistirCaracteristica(caracteristicaGrade);
				this.caracteristicasGrade = this.ambulatorioFacade.listarCaracteristicasGrade(gradeAgendamenConsultas.getSeq());
				this.caracteristica = null;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}	
	}
	

	public void adicionarSituacao() throws ApplicationBusinessException {
		try{
		AacGradeSituacaoId gradeSituacaoId = new AacGradeSituacaoId();
		gradeSituacaoId.setDtInicioSituacao(this.dtInicio);
		gradeSituacaoId.setGrdSeq(this.gradeAgendamenConsultas.getSeq());
		AacGradeSituacao gradeSituacao = new AacGradeSituacao();
		gradeSituacao.setId(gradeSituacaoId);
		gradeSituacao.setCriadoEm(new Date());
		gradeSituacao.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		gradeSituacao.setSituacao(this.getIndSituacao());
		gradeSituacao.setGradeAgendamentoConsulta(gradeAgendamenConsultas);
		if(this.getIndSituacao()!=null && this.getIndSituacao().equals(DominioSituacao.A) && gradeSituacao.getServidor()!=null && gradeSituacao.getServidor().getIndSituacao().equals(DominioSituacaoVinculo.I)){
			apresentarMsgNegocio(Severity.ERROR,"ERRO_PROFISSIONAL_INATIVO");
			gradeSituacaoId = null;
			gradeSituacao = null;
			return;
		}		
		this.ambulatorioFacade.persistirSituacao(gradeSituacao);
		situacoes = this.ambulatorioFacade.listarSituacoesGrade(gradeAgendamenConsultas.getSeq());
		this.dtInicio = null;
		this.indSituacao = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}	
	}
	
	public void removerProcedimento(AacGradeProcedHospitalar procedimento) throws ApplicationBusinessException  {
		try{
			ambulatorioFacade.removerProcedimento(procedimento);
			this.procedimentos = this.ambulatorioFacade.listarProcedimentosGrade(gradeAgendamenConsultas.getSeq());
			this.procedimento = null;
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_PROCEDIMENTO");
		}
		 catch (BaseException e) {
		apresentarExcecaoNegocio(e);
		LOG.error(e.getMessage(),e);
		}	
	}

	
	public void removerCaracteristica(AacCaracteristicaGrade caracteristica) throws ApplicationBusinessException  {
	try{
		ambulatorioFacade.removerCaracteristica(caracteristica);
		ambulatorioFacade.persistirCaracteristicaJn(caracteristica);
		this.caracteristicasGrade = this.ambulatorioFacade.listarCaracteristicasGrade(gradeAgendamenConsultas.getSeq());
		this.caracteristicas = this.ambulatorioFacade.listarCaracteristicas();
		apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_CARACTERISTICA");
	} catch (BaseException e) {
		apresentarExcecaoNegocio(e);
		LOG.error(e.getMessage(), e);
		}
	}

	
	public void removerSituacao(AacGradeSituacao situacao) throws ApplicationBusinessException  {
		try{
			ambulatorioFacade.removerSituacao(situacao);
			situacoes = this.ambulatorioFacade.listarSituacoesGrade(gradeAgendamenConsultas.getSeq());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_SITUACAO");
		}
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void setarDiasSemanaSelecionados(){
		listaHorarioGradeConsulta = new ArrayList<AacHorarioGradeConsulta>();
		if(dom){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.DOMINGO));
		}
		if(seg){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.SEGUNDA));
		}
		if(ter){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.TERCA));
		}
		if(qua){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.QUARTA));
		}
		if(qui){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.QUINTA));
		}
		if(sex){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.SEXTA));
		}
		if(sab){
			listaHorarioGradeConsulta.add(criaHorarioAgendamento(DominioDiaSemana.SABADO));
		}
	}
	
	
	
	public void setarDiasSemanaSelecionadosTela(AacHorarioGradeConsulta horarioGradeConsulta){
		listaHorarioGradeConsulta = new ArrayList<AacHorarioGradeConsulta>();
				
		if(horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.DOMINGO)){
			dom=true;
		}
		if(horarioGradeConsulta.getDiaSemana()!=null && horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.SEGUNDA)){
			seg=true;
		}
		if(horarioGradeConsulta.getDiaSemana()!=null && horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.TERCA)){
			ter=true;
		}
		setarDiasSemanaSelecionadosTelaSegundaPate(horarioGradeConsulta);
	}


	/**
	 * @param horarioGradeConsulta
	 */
	private void setarDiasSemanaSelecionadosTelaSegundaPate(
			AacHorarioGradeConsulta horarioGradeConsulta) {
		if(horarioGradeConsulta.getDiaSemana()!=null && horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.QUARTA)){
			qua=true;
		}
		if(horarioGradeConsulta.getDiaSemana()!=null && horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.QUINTA)){
			qui=true;
		}
		if(horarioGradeConsulta.getDiaSemana()!=null && horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.SEXTA)){
			sex=true;
		}
		if(horarioGradeConsulta.getDiaSemana()!=null && horarioGradeConsulta.getDiaSemana().equals(DominioDiaSemana.SABADO)){
			sab=true;
		}
	}
	
	public void setFalseTodosDias(){
	dom=false;
	seg=false;
	ter=false;
	qua=false;
	qui=false;
	sex=false;
	sab=false;
		
	}
	
	public AacHorarioGradeConsulta criaHorarioAgendamento(DominioDiaSemana dia){
		AacHorarioGradeConsulta horario = new AacHorarioGradeConsulta();
		horario.setHoraInicio(horarioGradeConsulta.getHoraInicio());
		horario.setHoraFim(horarioGradeConsulta.getHoraFim());
		horario.setDuracao(horarioGradeConsulta.getDuracao());
		horario.setSituacao((horarioGradeConsulta.getSituacao()));
		horario.setNumHorario((horarioGradeConsulta.getNumHorario()));
		horario.setDiaSemana(dia);
		horario.setId(null);
		return horario;
	}

	
	//--[GETTERS AND SETTERS]
	public AacGradeAgendamenConsultas getGradeAgendamenConsultas() {
		return gradeAgendamenConsultas;
	}

	public void setGradeAgendamenConsultas(
			AacGradeAgendamenConsultas gradeAgendamenConsultas) {
		this.gradeAgendamenConsultas = gradeAgendamenConsultas;
	}

	public List<AacGradeProcedHospitalar> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(List<AacGradeProcedHospitalar> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public AacProcedHospEspecialidades getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(
			AacProcedHospEspecialidades procedimento) {
		this.procedimento = procedimento;
	}

	public List<AacPagador> getPagadorList() {
		return pagadorList;
	}

	public void setPagadorList(List<AacPagador> pagadorList) {
		this.pagadorList = pagadorList;
	}

	public AacHorarioGradeConsulta getHorarioGradeConsulta() {
		return horarioGradeConsulta;
	}


	public void setHorarioGradeConsulta(AacHorarioGradeConsulta horarioGradeConsulta) {
		this.horarioGradeConsulta = horarioGradeConsulta;
	}

	public List<AacHorarioGradeConsulta> getHorarioGradeConsultaList() {
		return horarioGradeConsultaList;
	}


	public void setHorarioGradeConsultaList(
			List<AacHorarioGradeConsulta> horarioGradeConsultaList) {
		this.horarioGradeConsultaList = horarioGradeConsultaList;
	}


	public String getCaracteristica() {
		return caracteristica;
	}

	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}

	public List<String> getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(List<String> caracteristicas) {
		this.caracteristicas = caracteristicas;
	}


	public List<AacCaracteristicaGrade> getCaracteristicasGrade() {
		return caracteristicasGrade;
	}

	public void setCaracteristicasGrade(List<AacCaracteristicaGrade> caracteristicasGrade) {
		this.caracteristicasGrade = caracteristicasGrade;
	}

	

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}


	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	public List<AacGradeSituacao> getSituacoes() {
		return situacoes;
	}


	public void setSituacoes(List<AacGradeSituacao> situacoes) {
		this.situacoes = situacoes;
	}

	
	public EspCrmVO getEspCrmVO() {
		return espCrmVO;
	}


	public void setEspCrmVO(EspCrmVO espCrmVO) {
		this.espCrmVO = espCrmVO;
	}


	public FormaAgendamentoVO getFormaAgendamentoAba1() {
		return formaAgendamentoAba1;
	}


	public void setFormaAgendamentoAba1(FormaAgendamentoVO formaAgendamentoAba1) {
		this.formaAgendamentoAba1 = formaAgendamentoAba1;
	}


	public FormaAgendamentoVO getFormaAgendamentoAba2() {
		return formaAgendamentoAba2;
	}


	public void setFormaAgendamentoAba2(FormaAgendamentoVO formaAgendamentoAba2) {
		this.formaAgendamentoAba2 = formaAgendamentoAba2;
	}
	
	public String getCriadoEm() throws ApplicationBusinessException {
		if (gradeAgendamenConsultas!=null && gradeAgendamenConsultas.getCriadoEm()!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			RapServidores servidor = registroColaboradorFacade.obterServidor(gradeAgendamenConsultas.getServidor());
			String nomeServidor = registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()).getNome();
			return "Em " +sdf.format(gradeAgendamenConsultas.getCriadoEm()) + " por " + nomeServidor;
		}
		return null;
	}
	
	public String getAlteradoEm() throws ApplicationBusinessException {
		if (gradeAgendamenConsultas!=null && gradeAgendamenConsultas.getCriadoEm()!=null && gradeAgendamenConsultas.getServidorAlterado()!=null){
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
			RapServidores servidor = registroColaboradorFacade.obterServidor(gradeAgendamenConsultas.getServidorAlterado());
			String nomeServidor = registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()).getNome();
			return "Em " +sdf.format(gradeAgendamenConsultas.getAlteradoEm()) + " por " + nomeServidor;
		}
		return null;
	}

	
	public String getDescricaoGrade() {
		StringBuilder sb = new StringBuilder();		
		if (gradeAgendamenConsultas != null && !gradeNova) {
			if (gradeAgendamenConsultas.getProcedimento() != null && gradeAgendamenConsultas.getProcedimento() ){
				sb.append("<b>Grade de Procedimento: </b>");
			} else {
				sb.append("<b>Grade de Consulta: </b>");				
			}
			sb.append(gradeAgendamenConsultas.getSeq() ).append(' ');
			sb.append("<b>" +parametroZona + ": </b>");
			sb.append(gradeAgendamenConsultas.getUnidadeFuncional().getSigla() )
				.append( '/' )
				.append( gradeAgendamenConsultas.getAacUnidFuncionalSala().getId().getSala()).append(' ');
			sb.append("<b>Esp/Agenda: </b>");
			if (gradeAgendamenConsultas.getEspecialidade() != null) {
				sb.append(gradeAgendamenConsultas.getEspecialidade().getNomeReduzido().replaceAll("\\<.*?>","") ).append(' ');
			}
			sb.append("<b>Equipe: </b>");
			if (gradeAgendamenConsultas.getEquipe()!=null){
				sb.append(gradeAgendamenConsultas.getEquipe().getNome().replaceAll("\\<.*?>","") ).append(' ');
			}	
			if (espCrmVO!=null){
				sb.append("<b>Profissional: </b>");
				sb.append(espCrmVO.getNomeMedico().replaceAll("\\<.*?>",""));
			}	
		}
		descricaoGrade = String.valueOf(sb);
		return descricaoGrade;
	}
	
	public void setDescricaoGrade(String descricaoGrade) {
		this.descricaoGrade = descricaoGrade;
	}

	public String getParametroZona() {
		return parametroZona;
	}

	public void setParametroZona(String parametroZona) {
		this.parametroZona = parametroZona;
	}

	public String getParametroProjetoPesquisa() {
		return parametroProjetoPesquisa.toString();
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Boolean getExisteConsultas() {
		return existeConsultas;
	}

	public void setExisteConsultas(Boolean existeConsultas) {
		this.existeConsultas = existeConsultas;
	}

	public String getTitleParametroZona() {
		return titleParametroZona;
	}

	public void setTitleParametroZona(String titleParametroZona) {
		this.titleParametroZona = titleParametroZona;
	}

	public String getParametroSala() {
		return parametroSala;
	}

	public void setParametroSala(String parametroSala) {
		this.parametroSala = parametroSala;
	}

	public Boolean getGradeSisreg() {
		return gradeSisreg;
	}

	public void setGradeSisreg(Boolean gradeSisreg) {
		this.gradeSisreg = gradeSisreg;
	}


	public String getSelectedTab() {
		return selectedTab;
	}


	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}


	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}


	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}
	
	public void setGradeNova(Boolean gradeNova) {
		this.gradeNova = gradeNova;
	}


	public Boolean getGradeNova() {
		return gradeNova;
	}
	
	public boolean isGradeCopiada() {
		return gradeCopiada;
	}

	public void setGradeCopiada(boolean gradeCopiada) {
		this.gradeCopiada = gradeCopiada;
	}

	public AacGradeAgendamenConsultas getGradeAgendamenConsultaCopia() {
		return gradeAgendamenConsultaCopia;
	}

	public void setGradeAgendamenConsultaCopia(AacGradeAgendamenConsultas gradeAgendamenConsultaCopia) {
		this.gradeAgendamenConsultaCopia = gradeAgendamenConsultaCopia;
	}

	public boolean isHabilitarAbasSecundariaGrade() {
		return habilitarAbasSecundariaGrade;
	}

	public void setHabilitarAbasSecundariaGrade(boolean habilitarAbasSecundariaGrade) {
		this.habilitarAbasSecundariaGrade = habilitarAbasSecundariaGrade;
	}

	public Integer getSeqCaracteristica() {
		return seqCaracteristica;
	}

	public void setSeqCaracteristica(Integer seqCaracteristica) {
		this.seqCaracteristica = seqCaracteristica;
	}

	public String getMensagemConfirmacaoPendencias() {
		mensagemConfirmacaoPendencias = this.getBundle().getString("MENSAGEM_PENDENCIAS_ALTERACAO");
		if (gradeAgendamenConsultaCopia != null && seqCaracteristica == null) {
			mensagemConfirmacaoPendencias = this.getBundle().getString("MENSAGEM_VOLTAR_COPIAR_GRADE_NAO_SALVA");
		}
		return mensagemConfirmacaoPendencias;
	}
	
	public void setMensagemConfirmacaoPendencias(String mensagemConfirmacaoPendencias) {
		this.mensagemConfirmacaoPendencias = mensagemConfirmacaoPendencias;
	}


	public List<AacHorarioGradeConsulta> getListaHorarioGradeConsulta() {
		return listaHorarioGradeConsulta;
	}

	public void setListaHorarioGradeConsulta(
			List<AacHorarioGradeConsulta> listaHorarioGradeConsulta) {
		this.listaHorarioGradeConsulta = listaHorarioGradeConsulta;
	}

	public boolean isDom() {
		return dom;
	}

	public void setDom(boolean dom) {
		this.dom = dom;
	}

	public boolean isSeg() {
		return seg;
	}

	public void setSeg(boolean seg) {
		this.seg = seg;
	}

	public boolean isTer() {
		return ter;
	}

	public void setTer(boolean ter) {
		this.ter = ter;
	}

	public boolean isQua() {
		return qua;
	}

	public void setQua(boolean qua) {
		this.qua = qua;
	}

	public boolean isQui() {
		return qui;
	}

	public void setQui(boolean qui) {
		this.qui = qui;
	}

	public boolean isSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public boolean isSab() {
		return sab;
	}

	public void setSab(boolean sab) {
		this.sab = sab;
	}


	public boolean isAcaoEditar() {
		return acaoEditar;
	}


	public void setAcaoEditar(boolean acaoEditar) {
		this.acaoEditar = acaoEditar;
	}
	
	public Integer getSeqGradeAnteriorCopia() {
		return seqGradeAnteriorCopia;
	}


	public void setSeqGradeAnteriorCopia(Integer seqGradeAnteriorCopia) {
		this.seqGradeAnteriorCopia = seqGradeAnteriorCopia;
	}
}