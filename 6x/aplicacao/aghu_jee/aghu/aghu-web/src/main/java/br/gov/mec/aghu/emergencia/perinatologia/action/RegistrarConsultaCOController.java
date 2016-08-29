package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.dominio.DominioDelee;
import br.gov.mec.aghu.dominio.DominioDinamicaUterina;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.DesbloqueioConsultaCOVO;
import br.gov.mec.aghu.emergencia.vo.FiltroVerificaoInclusaoAnamneseVO;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAnamneseEfsId;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.controller.AutenticacaoController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class RegistrarConsultaCOController extends ActionController {
	private static final long serialVersionUID = 4126313594932214952L;
	private static final String PERMISSAO_INGRESSAR_PACIENTE_SO = "ingressarPacientePerinatologiaSO";
	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "pesquisaGestacoesList";
	private static final String TELA_ORIGEM = "/pages/perinatologia/registrarGestacao.xhtml";
	private static final String NAME_ABA_ORIGEM = "abaConsultaCO";
	private static final Integer ABA_GESTACAO_ATUAL = 0;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IEmergenciaFacade emergenciaFacade;	
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;
	@Inject
	private AutenticacaoController autenticacaoController;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	private static final Log LOG = LogFactory.getLog(RegistrarConsultaCOController.class);
	private McoAnamneseEfs anamneseEfs;
	private McoAnamneseEfs anamneseEfsOriginal;
	private McoPlanoIniciais planoIniciais;
	private Boolean editConsulta;
	private Boolean editConduta;
	private DominioDinamicaUterina dinamicaUterina;
	private DominioDelee planoDelee;
	private Boolean requiredDataAlterar;
	private McoDiagnostico diagnostico;
	private CidVO cidVO;
	private McoConduta conduta;
	private List<McoAnamneseEfs> listConsultas = new ArrayList<McoAnamneseEfs>();
	private List<McoPlanoIniciais> listCondutas = new ArrayList<McoPlanoIniciais>();
	private Integer pacCodigo;
	private Short seqp;
	private Integer numeroConsulta;
	private McoConduta condutaSelecionada;
	private McoAnamneseEfs itemAnamnese;
	private Boolean consultaEmEdicao = Boolean.FALSE;
	private McoAnamneseEfs dadosEdicaoAnamnese;
	private McoPlanoIniciais planoIniciaisExclusao;
	private Boolean condutaEmEdicao = Boolean.FALSE;
	private DominioSimNao indDthrIgnorada;
	private DominioSimNao indAcelTrans;
	private DominioSimNao indMovFetal;
	private Long nrBfc;
	private boolean permNrBfc1 = Boolean.FALSE;
	private boolean permNrBfc2 = Boolean.FALSE;
	private boolean permNrBfc3 = Boolean.FALSE;
	private boolean permNrBfc4 = Boolean.FALSE;
	private boolean permNrBfc5 = Boolean.FALSE;
	private boolean permNrBfc6 = Boolean.FALSE;
	private boolean readDinUterina = Boolean.FALSE;
	private boolean mostraModalGravarConsultaCO;	
	private Integer prontuario; 
	private Short seqUnidadeFuncional;
	private String nomePaciente;
	private String idadeFormatada;	
	private Boolean retornoTelaPrescricao;
	private boolean permDesbloquearConsultaCO = false;
	private DesbloqueioConsultaCOVO desbloqueioConsultaCOVO;
	private Integer matricula;
	private Short vinCodigo;
	private boolean disabled = false;
	private boolean internacaoDisabled = false;
	private ServidorIdVO servidorIdVO;
	private boolean exibeModalTipoDeGravidez = false;
	private boolean exibeModalSolicitarExames = false;
	private Integer atdSeq;
	private Date criadoEm;
	private boolean gerarPendenciaDeAssinaturaDigital = false;	
	private boolean posicaoObrigatorio = false;
	private boolean exibeModalAutenticacao;
	private String dataInternacaoFormatada; 
	private Long trgSeq; //informação oriunda da aba aguardando da lista pacientes emergencia, botão atender
	private boolean alteracaoConsulta;
	private boolean alteracaoConduta;
	private boolean mostrarModalDadosPendentesConsultaCO;
	private Boolean finalizarConsulta;
	private boolean mantemAba;
	private boolean exibeModalAutenticacaoPrescrever;
	
	@PostConstruct
	public void init() {
		String usuarioLogado = super.obterLoginUsuarioLogado();
		this.permDesbloquearConsultaCO = getPermissionService().usuarioTemPermissao(usuarioLogado, "desbloquearConsultaCO", "executar");
		desbloqueioConsultaCOVO = new DesbloqueioConsultaCOVO();
		desbloqueioConsultaCOVO.setNotasAdicionais(false);
		desbloqueioConsultaCOVO.setHabilitaDesbloqueio(true);
		desbloqueioConsultaCOVO.setHabilitaBloqueio(true);
		desbloqueioConsultaCOVO.setHabilitaExclusao(true);
		desbloqueioConsultaCOVO.setHabilitaFinalizarConsulta(true);
		desbloqueioConsultaCOVO.setHabilitaInternar(true);
		desbloqueioConsultaCOVO.setPermiteAlterarAbaGestAtual(true);
		desbloqueioConsultaCOVO.setPermiteAlterarAbaConsCO(true);
	}
	
	private boolean verificaSeModuloEstaAtivo(String nome) {
		boolean ativo = false;
		try{
			ativo = emergenciaFacade.verificarSeModuloEstaAtivo(nome);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return ativo;		
	}
	
	public void inicio() {
		mantemAba = false;
		this.finalizarConsulta = Boolean.FALSE;
		try {
			emergenciaFacade.ajustarDesbloqueioConsultaCO(desbloqueioConsultaCOVO, pacCodigo, seqp, numeroConsulta);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}		
		if (verificarExisteAnamnese()) {
			editConsulta = Boolean.FALSE;
			editConduta = Boolean.FALSE;
		} else {
			editConsulta = Boolean.TRUE;
			editConduta = Boolean.TRUE;
			if(dadosEdicaoAnamnese == null){
				dadosEdicaoAnamnese = new McoAnamneseEfs();
			}
			dadosEdicaoAnamnese.setConsulta(ambulatorioFacade.obterAacConsulta(this.numeroConsulta));
		}
		this.obterConsultas();
		this.inicializarPojos();
		if(!this.listConsultas.isEmpty() && this.listConsultas != null){
			if(retornoTelaPrescricao != null && retornoTelaPrescricao){
				this.setItemAnamnese(this.buscaConsultaSelecionada());
			}else {
				this.setItemAnamnese(this.listConsultas.get(0));
			}
			this.selecionarConsulta();
			this.obterDinamicaUterina(this.getAnamneseEfs());
			this.obterReadDinUterina();
			this.obterMovFetalAcelTransDthrIgnorada();
			this.obterPlanoDelee();
		} else {
			listCondutas = new ArrayList<McoPlanoIniciais>();
			listConsultas = new ArrayList<McoAnamneseEfs>();
		}
		this.setMostraModalGravarConsultaCO(Boolean.FALSE);
		this.validarPosicaoObrigatorio();
		
	}
	private McoAnamneseEfs buscaConsultaSelecionada() {
		if(numeroConsulta != null) {
			for (McoAnamneseEfs consulta : listConsultas) {
				if(consulta.getConsulta().equals(numeroConsulta)){
					return consulta;
				}
			}
		}
		return null;
	}
	private void obterPlanoDelee(){
		if(this.getAnamneseEfs().getPlanoDelee() != null){
			if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.MENOS_QUATRO.getCodigo()){
				this.setPlanoDelee(DominioDelee.MENOS_QUATRO);
			} else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.MENOS_TRES.getCodigo()){
				this.setPlanoDelee(DominioDelee.MENOS_TRES);
			} else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.MENOS_DOIS.getCodigo()){
				this.setPlanoDelee(DominioDelee.MENOS_DOIS);
			} else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.MENOS_UM.getCodigo()){
				this.setPlanoDelee(DominioDelee.MENOS_UM);
			} else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.ZERO.getCodigo()){
				this.setPlanoDelee(DominioDelee.ZERO);
			}  else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.UM.getCodigo()){
				this.setPlanoDelee(DominioDelee.UM);
			}  else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.DOIS.getCodigo()){
				this.setPlanoDelee(DominioDelee.DOIS);
			}  else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.TRES.getCodigo()){
				this.setPlanoDelee(DominioDelee.TRES);
			}  else if(this.getAnamneseEfs().getPlanoDelee() == DominioDelee.QUATRO.getCodigo()){
				this.setPlanoDelee(DominioDelee.QUATRO);
			}
		} else {
			this.setPlanoDelee(null);
		}
	}
	
	private void converterPlanoDelle(){
		if(this.getPlanoDelee() != null){
			if(this.getPlanoDelee().equals(DominioDelee.MENOS_QUATRO)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.MENOS_QUATRO.getCodigo());
			} else if(this.getPlanoDelee().equals(DominioDelee.MENOS_TRES)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.MENOS_TRES.getCodigo());
			} else if(this.getPlanoDelee().equals(DominioDelee.MENOS_DOIS)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.MENOS_DOIS.getCodigo());
			} else if(this.getPlanoDelee().equals(DominioDelee.MENOS_UM)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.MENOS_UM.getCodigo());
			} else if(this.getPlanoDelee().equals(DominioDelee.ZERO)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.ZERO.getCodigo());
			}  else if(this.getPlanoDelee().equals(DominioDelee.UM)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.UM.getCodigo());
			}  else if(this.getPlanoDelee().equals(DominioDelee.DOIS)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.DOIS.getCodigo());
			}  else if(this.getPlanoDelee().equals(DominioDelee.TRES)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.TRES.getCodigo());
			}  else if(this.getPlanoDelee().equals(DominioDelee.QUATRO)){
				this.getAnamneseEfs().setPlanoDelee(DominioDelee.QUATRO.getCodigo());
			}
		} else {
			this.getAnamneseEfs().setPlanoDelee(null);
		}
	}
	private void obterMovFetalAcelTransDthrIgnorada(){
		if(this.anamneseEfs.getIndDthrIgnorada() == null){
			this.setIndDthrIgnorada(null);
		} else if(this.anamneseEfs.getIndDthrIgnorada().booleanValue()){
			this.setIndDthrIgnorada(DominioSimNao.S);
		} else {
			this.setIndDthrIgnorada(DominioSimNao.N);
		}
		if(this.anamneseEfs.getIndAcelTrans() == null){
			this.setIndAcelTrans(null);
		} else if(this.anamneseEfs.getIndAcelTrans().isSim()){
			this.setIndAcelTrans(DominioSimNao.S);
		} else {
			this.setIndAcelTrans(DominioSimNao.N);
		}
		if(this.anamneseEfs.getIndMovFetal() == null){
			this.setIndMovFetal(null);
		} else if(this.anamneseEfs.getIndMovFetal().isSim()){
			this.setIndMovFetal(DominioSimNao.S);
		} else {
			this.setIndMovFetal(DominioSimNao.N);
		}
	}	
	private void obterDinamicaUterina(McoAnamneseEfs anamneseEfs2) {
		if(anamneseEfs2.getDinamicaUterina() != null){
			if(anamneseEfs2.getDinamicaUterina().equalsIgnoreCase(DominioDinamicaUterina.ZERO.getDescricao())){
				this.setDinamicaUterina(DominioDinamicaUterina.ZERO);
			} else if(anamneseEfs2.getDinamicaUterina().equalsIgnoreCase(DominioDinamicaUterina.UM_DECIMO.getDescricao())){
				this.setDinamicaUterina(DominioDinamicaUterina.UM_DECIMO);
			} else if(anamneseEfs2.getDinamicaUterina().equalsIgnoreCase(DominioDinamicaUterina.DOIS_DECIMOS.getDescricao())){
				this.setDinamicaUterina(DominioDinamicaUterina.DOIS_DECIMOS);
			} else if(anamneseEfs2.getDinamicaUterina().equalsIgnoreCase(DominioDinamicaUterina.TRES_DECIMOS.getDescricao())){ 
				this.setDinamicaUterina(DominioDinamicaUterina.TRES_DECIMOS);
			} else if(anamneseEfs2.getDinamicaUterina().equalsIgnoreCase(DominioDinamicaUterina.QUATRO_DECIMOS.getDescricao())){
				this.setDinamicaUterina(DominioDinamicaUterina.QUATRO_DECIMOS);
			} else if(anamneseEfs2.getDinamicaUterina().equalsIgnoreCase(DominioDinamicaUterina.MAIOR_QUE_QUATRO_DECIMOS.getDescricao())){
				this.setDinamicaUterina(DominioDinamicaUterina.MAIOR_QUE_QUATRO_DECIMOS);
			}
		} else {
			this.setDinamicaUterina(null);
			this.getAnamneseEfs().setIntensidadeDinUterina(null);
		}
	}	
	private void converterDinamicaUterina(DominioDinamicaUterina dinamica){
		if(dinamica != null){
			if(dinamica.equals(DominioDinamicaUterina.ZERO)){
				this.getAnamneseEfs().setDinamicaUterina(DominioDinamicaUterina.ZERO.getDescricao());
				
			} else if(dinamica.equals(DominioDinamicaUterina.UM_DECIMO)){
				this.getAnamneseEfs().setDinamicaUterina(DominioDinamicaUterina.UM_DECIMO.getDescricao());
				
			} else if(dinamica.equals(DominioDinamicaUterina.DOIS_DECIMOS)){
				this.getAnamneseEfs().setDinamicaUterina(DominioDinamicaUterina.DOIS_DECIMOS.getDescricao());
				
			} else if(dinamica.equals(DominioDinamicaUterina.TRES_DECIMOS)){ 
				this.getAnamneseEfs().setDinamicaUterina(DominioDinamicaUterina.TRES_DECIMOS.getDescricao());
				
			} else if(dinamica.equals(DominioDinamicaUterina.QUATRO_DECIMOS)){
				this.getAnamneseEfs().setDinamicaUterina(DominioDinamicaUterina.QUATRO_DECIMOS.getDescricao());
				
			} else if(dinamica.equals(DominioDinamicaUterina.MAIOR_QUE_QUATRO_DECIMOS)){
				this.getAnamneseEfs().setDinamicaUterina(DominioDinamicaUterina.MAIOR_QUE_QUATRO_DECIMOS.getDescricao());
			}
		} else {
			this.getAnamneseEfs().setDinamicaUterina(null);
			this.getAnamneseEfs().setIntensidadeDinUterina(null);
		}
	}	
	public void obterReadDinUterina(){
		if(this.dinamicaUterina == null || this.dinamicaUterina.equals(DominioDinamicaUterina.ZERO)){
			this.setReadDinUterina(Boolean.TRUE);
			this.getAnamneseEfs().setIntensidadeDinUterina(null);
		} else {
			this.setReadDinUterina(Boolean.FALSE);
		}
	}
	private void verificaNrBfc(Long nrBfc2) {
		if(nrBfc2 != null){
			switch (nrBfc2.intValue()) {
			case 1:
				this.setPermNrBfc1(Boolean.TRUE);break;			
			case 2:
				this.setPermNrBfc1(Boolean.TRUE); this.setPermNrBfc2(Boolean.TRUE); break;			
			case 3:
				this.setPermNrBfc1(Boolean.TRUE); this.setPermNrBfc2(Boolean.TRUE); this.setPermNrBfc3(Boolean.TRUE); break;
			case 4:
				this.setPermNrBfc1(Boolean.TRUE); this.setPermNrBfc2(Boolean.TRUE); this.setPermNrBfc3(Boolean.TRUE); this.setPermNrBfc4(Boolean.TRUE); break;
			case 5:
				this.setPermNrBfc1(Boolean.TRUE); this.setPermNrBfc2(Boolean.TRUE); this.setPermNrBfc3(Boolean.TRUE); this.setPermNrBfc4(Boolean.TRUE); this.setPermNrBfc5(Boolean.TRUE); break;	
			case 6:
				this.setPermNrBfc1(Boolean.TRUE); this.setPermNrBfc2(Boolean.TRUE); this.setPermNrBfc3(Boolean.TRUE); this.setPermNrBfc4(Boolean.TRUE); this.setPermNrBfc5(Boolean.TRUE); this.setPermNrBfc6(Boolean.TRUE); break;				
			default:
				this.setPermNrBfc1(Boolean.TRUE); break;
			}
		}	
	}
	private void limparNrBfc(){
		this.setPermNrBfc1(Boolean.FALSE); this.setPermNrBfc2(Boolean.FALSE); this.setPermNrBfc3(Boolean.FALSE); this.setPermNrBfc4(Boolean.FALSE); this.setPermNrBfc5(Boolean.FALSE); this.setPermNrBfc6(Boolean.FALSE);			
	}
	private void obterConsultas(){
		listConsultas = emergenciaFacade.obterListaMcoAnamneseEfs(pacCodigo, seqp);
	}
	private void inicializarPojos(){
		this.anamneseEfs = new McoAnamneseEfs();
		this.itemAnamnese = new McoAnamneseEfs();
		this.planoIniciais = new McoPlanoIniciais();
		this.planoIniciaisExclusao = new McoPlanoIniciais();
		this.limparNrBfc();
		if(dadosEdicaoAnamnese == null){
			this.dadosEdicaoAnamnese = new McoAnamneseEfs();
		}	
		this.alterarValoresNulosBooleanos(anamneseEfs);
		try {
			anamneseEfsOriginal = new McoAnamneseEfs();
			PropertyUtils.copyProperties(anamneseEfsOriginal, anamneseEfs);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LOG.error("Erro ao copiar objetos.", e);
		}
	}
	private void alterarValoresNulosBooleanos(McoAnamneseEfs item) {
		if(item.getIndEspCiaticaSaliente() == null){
			item.setIndEspCiaticaSaliente(false);
		}
		if(item.getIndOdorFetido() == null){
			item.setIndOdorFetido(false);
		}
		if(item.getIndPromontorioAcessivel() == null) {
			item.setIndPromontorioAcessivel(false);
		}
		if(item.getIndSubPubicoMenor90() == null) {
			item.setIndSubPubicoMenor90(false);
		}
	}
	private Boolean verificarExisteAnamnese(){
		try {
			return emergenciaFacade.isAnamnese(new FiltroVerificaoInclusaoAnamneseVO(numeroConsulta, pacCodigo, seqp));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return Boolean.FALSE;
	}
	public void cancelarEdicaoConsultas(){
		this.setConsultaEmEdicao(Boolean.FALSE);
		this.setDadosEdicaoAnamnese(new McoAnamneseEfs());
		this.obterConsultas();
	}
	public void editarConsulta(){
		this.setConsultaEmEdicao(Boolean.TRUE);
	}
	public void cancelarEdicaoCondutas(){
		this.setCondutaEmEdicao(Boolean.FALSE);
		this.setPlanoIniciais(new McoPlanoIniciais());
		this.setConduta(null);
		obterConsultasECondutas();
	}
	private void obterConsultasECondutas() {
		this.obterCondutas();
		this.obterConsultas();
	}
	public String adiconarCondutas(){
		if(this.condutaEmEdicao){
			this.emergenciaFacade.atualizaConduta(this.conduta, this.anamneseEfs, this.planoIniciais.getComplemento());
			this.setCondutaEmEdicao(Boolean.FALSE);
			this.setPlanoIniciais(new McoPlanoIniciais());
			this.setConduta(null);
			obterConsultasECondutas();
			apresentarMsgNegocio(Severity.INFO, "MSG_REG_CONS_CO_ALTERACAO_CONDUTA_SUCESSO");
			setAlteracaoConduta(true);
			return "";
		} else {			
			if(!this.listCondutas.isEmpty()){
				for (McoPlanoIniciais planos : this.listCondutas) {
					if(planos.getConduta().equals(this.conduta)){
						obterConsultasECondutas();
						apresentarMsgNegocio(Severity.ERROR, "MSG_REG_CONS_CO_MCO_00103_1");
						return null;
					}
				}
			}
			this.emergenciaFacade.insereConduta(this.conduta, this.anamneseEfs, this.planoIniciais.getComplemento());
			this.setPlanoIniciais(new McoPlanoIniciais());
			this.setConduta(null);
			obterConsultasECondutas();
			apresentarMsgNegocio(Severity.INFO, "MSG_REG_CONS_CO_INCLUSAO_CONDUTA_SUCESSO");
			setAlteracaoConduta(true);
			return "";
		}
	}
	public void editarCondutas(McoPlanoIniciais itemConduta){
		this.setCondutaEmEdicao(Boolean.TRUE);
		this.setConduta(itemConduta.getConduta());
		this.setPlanoIniciais(itemConduta);
		LOG.info("Conduta em Edição.");
	}
	public void ingressarPacienteSO()  {
		if (!this.getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), PERMISSAO_INGRESSAR_PACIENTE_SO, "executar")) {
			this.apresentarMsgNegocio(Severity.INFO, "ERRO_PERMISSAO_INGRESSAR_PACIENTE_SO");
		} else {
			String hostName = null;
			try {
				hostName = super.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				apresentarMsgNegocio(Severity.ERROR, "Não foi possível obter hostName");
			}
			try {
				emergenciaFacade.ingressarConsultaSO(pacCodigo, numeroConsulta, hostName);
				this.apresentarMsgNegocio(Severity.INFO, "MSG_PACIENTE_INGRESSO_SO_SUCESSO");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	public void excluirConduta(){
		try {
			emergenciaFacade.excluirConduta(this.planoIniciaisExclusao, this.anamneseEfs);
			this.setPlanoIniciais(new McoPlanoIniciais());
			this.setConduta(null);
			obterConsultasECondutas();
			this.apresentarMsgNegocio(Severity.INFO, "MSG_REG_CONS_CO_EXCLUSAO_CONDUTA_SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public List<McoDiagnostico> pesquisarDiagnosticoSuggestion(final String strPesquisa){
		return emergenciaFacade.pesquisarDiagnosticoSuggestion((String) strPesquisa);
	}
	public void posSelectionActionDiagnostico(){
		try {
			cidVO =  new CidVO();
			List<Integer> cids = new ArrayList<>();
			cids.add(diagnostico.getAghCid().getSeq() );
			List<CidVO> vo = this.emergenciaFacade.obterCidPorSeq(cids);
			this.setCidVO(!vo.isEmpty() ? vo.get(0) : null);
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(e.getMessage());
		}
	}
	public void posDeleteActioDiagnostico(){
		this.setDiagnostico(null);
		this.setCidVO(null);
	}
	public List<CidVO> pesquisarCIDSuggestion(final String strPesquisa){
		try {
			return emergenciaFacade.pesquisarCIDSuggestion((String) strPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	public List<McoConduta> pesquisarConduta(final String strPesquisa){
		return emergenciaFacade.pesquisarMcoCondutaSuggestion((String) strPesquisa);
	}
	public Long pesquisarCondutaCount(final Object strPesquisa){
		return emergenciaFacade.pesquisarMcoCondutaSuggestionCount((String) strPesquisa);
	}
	private void validarDataConsulta() throws ApplicationBusinessException{
		if(this.dadosEdicaoAnamnese.getDthrConsulta() == null){
			throw new ApplicationBusinessException("MSG_REG_CAMPO_OBRIGATORIO", Severity.ERROR, getBundle().getString("LABEL_REG_CONS_CO_DATA"));
		}
		emergenciaFacade.validarDataConsulta(this.dadosEdicaoAnamnese.getDthrConsulta());
	}
	public String alterarConsulta(){
		try {
			if(this.dadosEdicaoAnamnese.getDthrConsulta() == null){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_REG_CONS_CO_DATA_NULL");
				return null;
			}
			validarDataConsulta();
			if(this.dadosEdicaoAnamnese.getMotivo().isEmpty()){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_REG_CONS_CO_MOTIVO_NULL");
				return null;
			}
			anamneseEfs.setConsulta(this.dadosEdicaoAnamnese.getConsulta());
			anamneseEfs.setDthrConsulta(this.dadosEdicaoAnamnese.getDthrConsulta());
			anamneseEfs.setMotivo(this.dadosEdicaoAnamnese.getMotivo());
			emergenciaFacade.persistirMcoAnamneseEfs(anamneseEfs, null, null, null);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_REG_CONS_CO_ALTERACAO_CONSULTA_SUCESSO");
			this.setConsultaEmEdicao(Boolean.FALSE);
			this.setDadosEdicaoAnamnese(new McoAnamneseEfs());
			this.obterConsultas();
			alteracaoConsulta = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return "";
	}
	public void adicionarConsulta(){
		try {
			validarMotivo();
			validarDataConsulta();
			String nomeMicroComputador = null;
			try {
				nomeMicroComputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e1) {
				LOG.error("Exceção capturada:", e1);
			}			
			this.emergenciaFacade.atualizarAtendimentoGestante(this.getNumeroConsulta(), this.getPacCodigo(), this.getSeqp(), 
					nomeMicroComputador, super.obterLoginUsuarioLogado());	
			emergenciaFacade.persistirMcoAnamneseEfs(this.dadosEdicaoAnamnese, this.getNumeroConsulta(), this.getPacCodigo(), this.getSeqp());
			this.apresentarMsgNegocio(Severity.INFO, "MSG_REG_CONS_CO_INCLUSAO_CONSULTA_SUCESSO");
			this.setDadosEdicaoAnamnese(new McoAnamneseEfs());
			this.obterConsultas();
			//marcar consulta adicionada na datagrid
			McoAnamneseEfsId pk = new McoAnamneseEfsId();
			pk.setConNumero(this.getNumeroConsulta());
			pk.setGsoPacCodigo(this.getPacCodigo());
			pk.setGsoSeqp(this.getSeqp());
			this.setItemAnamnese(this.emergenciaFacade.obterMcoAnamneseEfsPorId(pk));
			selecionarConsulta();
			alteracaoConsulta = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	private void validarMotivo()  throws ApplicationBusinessException {
		if(StringUtils.isBlank(dadosEdicaoAnamnese.getMotivo())){
			throw new ApplicationBusinessException("MSG_REG_CAMPO_OBRIGATORIO", Severity.ERROR, getBundle().getString("LABEL_REG_CONS_CO_MOT_CONST_HDA"));
		}
	}
	public void selecionarConsulta(){
		try{
			McoAnamneseEfsId pk = new McoAnamneseEfsId();
			pk.setConNumero(itemAnamnese.getId().getConNumero());
			pk.setGsoPacCodigo(itemAnamnese.getId().getGsoPacCodigo());
			pk.setGsoSeqp(itemAnamnese.getId().getGsoSeqp());
			this.setAnamneseEfs(this.emergenciaFacade.obterMcoAnamneseEfsPorId(pk));
			this.alterarValoresNulosBooleanos(anamneseEfs);
			try {
				PropertyUtils.copyProperties(anamneseEfsOriginal, anamneseEfs);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				LOG.error("Erro ao copiar objetos.", e);
			}
			this.setNrBfc(this.emergenciaFacade.obtemQuantidadeDefetosDaGestante(itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp()));
			this.verificaNrBfc(getNrBfc());
			if(this.getAnamneseEfs().getDigSeq() != null){
				List<McoDiagnostico> diagnostico = this.pesquisarDiagnosticoSuggestion(this.getAnamneseEfs().getDigSeq().toString());
				this.setDiagnostico(!diagnostico.isEmpty() ? diagnostico.get(0) : null);
			} else {
				this.setDiagnostico(null);
			}
			if(this.getAnamneseEfs().getCidSeq() != null){
				List<Integer> cids = new ArrayList<>();
				cids.add(this.getAnamneseEfs().getCidSeq());
				List<CidVO> vo = this.emergenciaFacade.obterCidPorSeq(cids);
				this.setCidVO(!vo.isEmpty() ? vo.get(0) : null);
			} else {
				this.setCidVO(null);
			}
			this.obterCondutas();
		} catch (ServiceException e) {
			this.apresentarMsgNegocio(e.getMessage());
		}
		try {
			emergenciaFacade.ajustarDesbloqueioConsultaCOSelecionada(desbloqueioConsultaCOVO, itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp(), itemAnamnese.getId().getConNumero()); 
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	private void obterCondutas(){
		this.setListCondutas(emergenciaFacade.listarMcoPlanoIniciaisConduta(this.getAnamneseEfs().getId().getConNumero(), this.getAnamneseEfs().getId().getGsoSeqp(), this.getAnamneseEfs().getId().getGsoPacCodigo())); 
	}
	private void gravarAnamnese() throws ApplicationBusinessException{
		this.verificarDtIgnoradaAcelTRansMovFetal();
		this.getAnamneseEfs().setDigSeq(this.getDiagnostico() != null ? this.getDiagnostico().getSeq() : null);
		this.getAnamneseEfs().setCidSeq(this.getCidVO() != null ? this.getCidVO().getSeq() : null);
		this.emergenciaFacade.gravarConsultaCO(this.anamneseEfs, this.anamneseEfsOriginal);
		this.selecionarConsulta();
	}
	private void verificarDtIgnoradaAcelTRansMovFetal(){
		if(this.getIndDthrIgnorada() == null){
			this.anamneseEfs.setIndDthrIgnorada(null);
		} else if(this.getIndDthrIgnorada().equals(DominioSimNao.S)){
			this.anamneseEfs.setIndDthrIgnorada(Boolean.TRUE);
		} else if(this.getIndDthrIgnorada().equals(DominioSimNao.N)) {
			this.anamneseEfs.setIndDthrIgnorada(Boolean.FALSE);
		}
		if(this.getIndAcelTrans() == null){
			this.anamneseEfs.setIndAcelTrans(null);
		} else if(this.getIndAcelTrans().equals(DominioSimNao.S)){
			this.anamneseEfs.setIndAcelTrans(DominioSimNao.S);
		} else if(this.getIndAcelTrans().equals(DominioSimNao.N)){
			this.anamneseEfs.setIndAcelTrans(DominioSimNao.N);
		}
		if(this.getIndMovFetal() == null){
			this.anamneseEfs.setIndMovFetal(null);
		} else if(this.getIndMovFetal().equals(DominioSimNao.S)){
			this.anamneseEfs.setIndMovFetal(DominioSimNao.S);
		} else if(this.getIndMovFetal().equals(DominioSimNao.N)){
			this.anamneseEfs.setIndMovFetal(DominioSimNao.N);
		}
	}
	public void imprimirPrevia() {
		try {
			this.gravarConsultaCOSemExibirMsgSucesso();
			this.emergenciaFacade.validarDadosGestacional(pacCodigo, seqp);
			final String jsExecute = "parent.tab.addNewTab('redirect_#{id}', 'Imprimir Prévia', '/aghu/pages/paciente/prontuarioonline/relatorioAtendEmergObstetricaPdf.xhtml?" + "pacCodigo=" + pacCodigo + ";" + "previa=" + true + ";" + "seqp=" + seqp + ";" + "numeroConsulta=" + numeroConsulta + ";"
					+ "voltarPara=" + getTelaOrigem() + ";" + "abaOrigem=" + getAbaOrigem() + ";" + "paramCid=#{javax.enterprise.context.conversation.id}', null, 1, true)";
			RequestContext.getCurrentInstance().execute(jsExecute);
		} catch (BaseException e) {
			FacesContext.getCurrentInstance().validationFailed();
			this.apresentarExcecaoNegocio(e);
		}
	}
	public void gravarConsultaCO(){
		if(itemAnamnese.getConsulta() != null){
			try {
				mostrarModalDadosPendentesConsultaCO = false;
				gravarConsultaCOSemExibirMsgSucesso();
				if(!finalizarConsulta){
					this.apresentarMsgNegocio(Severity.INFO, "MSG_REG_CONS_CO_ALTERACAO_DADOS_CONSULTA_SUCESSO");
				}	
				inicio();
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
	}
	private void gravarConsultaCOSemExibirMsgSucesso() throws BaseException, ApplicationBusinessException {
		//this.validarBatimentoFetal();
		this.setMostraModalGravarConsultaCO(Boolean.FALSE);
		this.converterDinamicaUterina(this.dinamicaUterina);
		this.converterPlanoDelle();
		this.emergenciaFacade.verificarDilatacao(anamneseEfs.getDilatacao());
		mantemAba = this.preGravarConsultaCO();
		if(!mantemAba) {
			this.gravarAnamnese();
		}
	}
	private boolean preGravarConsultaCO() {
		if((this.dinamicaUterina != null && !DominioDinamicaUterina.ZERO.equals(this.dinamicaUterina)) && this.getAnamneseEfs().getIntensidadeDinUterina() == null){
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Intensidade");
			return true;
		} 
		if(this.getAnamneseEfs().getEspessuraCervice() != null && this.getAnamneseEfs().getPosicaoCervice() == null){
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Posição");
			return true;
		}
		if(this.getAnamneseEfs().getEspessuraCervice() == null){
			this.getAnamneseEfs().setPosicaoCervice(null);
		}
		return false;
		
	}
	public String gravarModalConf(){
		if(itemAnamnese.getConsulta() != null){
			try {
				gravarConsultaCOSemExibirMsgSucesso();
				if(mantemAba){
					this.setMostraModalGravarConsultaCO(Boolean.FALSE);
					return "";
				}
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
				this.setMostraModalGravarConsultaCO(Boolean.FALSE);
				return "";
			}
		}
		return voltar();
	}
	public String confirmaVoltar(){
		obterDadosTela();
		if(this.emergenciaFacade.verificarAlteracaoTela(this.anamneseEfs, this.anamneseEfsOriginal) || alteracaoConsulta || alteracaoConduta){
			this.setMostraModalGravarConsultaCO(Boolean.TRUE);
			return null;
		}
		else{
			return this.voltar();
		}
	}
	public void obterDadosTela() {
		if(this.anamneseEfs != null){
			this.converterDinamicaUterina(this.dinamicaUterina);
			this.converterPlanoDelle();
			this.verificarDtIgnoradaAcelTRansMovFetal();
			this.getAnamneseEfs().setDigSeq(this.getDiagnostico() != null ? this.getDiagnostico().getSeq() : null);
			this.getAnamneseEfs().setCidSeq(this.getCidVO() != null ? this.getCidVO().getSeq() : null);
		}
	}
	public boolean verificarAlteracaoPendentes(){	
		if(anamneseEfs != null){
			obterDadosTela();
			return this.emergenciaFacade.verificarAlteracaoTela(this.anamneseEfs, this.anamneseEfsOriginal) || alteracaoConsulta || alteracaoConduta;
		}
		return false;
	}
	public void gravaDadosPendentes(){
		mostraModalGravarConsultaCO = false;
		alteracaoConsulta = false;
		alteracaoConduta = false;
		gravarConsultaCO();
		registrarGestacaoController.preparaAbaDestino();
	}
	public void ignoraDadosPendencias(){
		mostrarModalDadosPendentesConsultaCO = false;
		alteracaoConsulta = false;
		alteracaoConduta = false;
		inicio();
		registrarGestacaoController.preparaAbaDestino();
	}
	public String voltar() {
		this.setMostraModalGravarConsultaCO(Boolean.FALSE);
		this.registrarGestacaoController.setAbaSelecionada(ABA_GESTACAO_ATUAL);
		alteracaoConsulta = false;
		alteracaoConduta = false;
		return REDIRECIONA_PESQUISAR_GESTACOES;
	}
	
	public void gravarFinalizarConsulta(){
		try {
			gravarConsultaCOSemExibirMsgSucesso(); 
			emergenciaFacade.verificarIdadeGestacional(itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp());
			setExibeModalAutenticacao(true);
			openDialog("modalAutenticacaoFinalizarConsultaWG");
		}  catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} 
	}
	public void finalizarConsulta() {
		finalizarConsulta = Boolean.TRUE;
		this.gravarConsultaCO();
		try {
			this.matricula = this.servidorIdVO.getMatricula();
			this.vinCodigo = this.servidorIdVO.getSerVinCodigo();
			String hostName = super.getEnderecoIPv4HostRemoto().getHostName();
			emergenciaFacade.finalizarConsulta(itemAnamnese.getId().getConNumero(), itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp(), this.matricula, this.vinCodigo, new Date(), hostName, false);
			this.gerarPendenciaDeAssinaturaDigital = emergenciaFacade.gerarPendenciaDeAssinaturaDigital(this.matricula, this.vinCodigo);
			this.setDisabled(true);
			this.apresentarMsgNegocio(Severity.INFO, "SUCESSO_REGISTRAR_ALTA");
			this.exibeModalAutenticacao = false;
		} catch (BaseException e) {
			FacesContext.getCurrentInstance().validationFailed();
			super.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			FacesContext.getCurrentInstance().validationFailed();
			super.apresentarMsgNegocio(Severity.ERROR, "ERRO_OBTER_HOST_NAME");
		}
	}
	public void realizarInternacao() {	
		if(itemAnamnese.getConsulta() != null){
			try {
				gravarConsultaCOSemExibirMsgSucesso();		
				emergenciaFacade.realizarInternacao(itemAnamnese.getId().getConNumero(), itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp());		
				this.exibeModalTipoDeGravidez = emergenciaFacade.verificarTipoDeGravidez(itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp());			
				if(this.exibeModalTipoDeGravidez){ return; }		
				verificarExameVDRLnaoSolicitado();		
				if(this.exibeModalTipoDeGravidez){
					openDialog("modalTipoGravidezWG");
				} else if(this.exibeModalSolicitarExames){
					openDialog("modalSolicitarExamesWG");
				} else if (this.exibeModalAutenticacao){
					openDialog("modalAutenticacaoAposVerificarCondutaWG");
				}
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		} else {
			FacesContext.getCurrentInstance().validationFailed();
		}
	}
	public void verificarExameVDRLnaoSolicitado(){
		try {
			Integer atdSeq = emergenciaFacade.obterSeqAtendimentoPorConNumero(itemAnamnese.getId().getConNumero());
			this.exibeModalSolicitarExames = !emergenciaFacade.verificarExameVDRLnaoSolicitado(atdSeq);
			if(this.exibeModalSolicitarExames){
				setParametersParaSolicitarExames(atdSeq, itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getConNumero(), itemAnamnese.getId().getGsoSeqp()); return;
			}
			verificaSeExisteCondutaSemComplementoNaoCadastrada();
			closeDialog("modalTipoGravidezWG");
			if(this.exibeModalSolicitarExames){
				openDialog("modalSolicitarExamesWG");
			} else if(this.exibeModalAutenticacao){
				openDialog("modalAutenticacaoAposVerificarCondutaWG");
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	public void exibeModalAutenticacaoExameVDRL(){
		exibeModalAutenticacao = true;
	}
	private void setParametersParaSolicitarExames(Integer atdSeq, Integer pacCodigo, Integer conNumero, Short seqp) {
		this.setAtdSeq(atdSeq);
		this.setPacCodigo(pacCodigo);
		this.setNumeroConsulta(conNumero);
		this.setSeqp(seqp);				
	}
	public void verificaSeExisteCondutaSemComplementoNaoCadastrada(){
		try {
			for (McoPlanoIniciais planoIniciais : listCondutas) {
				emergenciaFacade.verificaSeExisteCondutaSemComplementoNaoCadastrada(planoIniciais.getConduta(), planoIniciais);
			}
			exibeModalAutenticacaoExameVDRL();
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			super.apresentarExcecaoNegocio(e);
		}
	}
	public void verificarPermissaoUsuario(){
		try {
			this.matricula = this.servidorIdVO.getMatricula();
			this.vinCodigo = this.servidorIdVO.getSerVinCodigo();
			emergenciaFacade.verificarPermissaoUsuario(this.matricula, this.vinCodigo);
			setExibeModalAutenticacao(false);
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			super.apresentarExcecaoNegocio(e);
		} catch (ServiceException e) {
			FacesContext.getCurrentInstance().validationFailed();
			this.apresentarMsgNegocio(e.getMessage());
		}
	}
	public void usuarioSemAcesso(){
		this.matricula = this.servidorIdVO.getMatricula();
		this.vinCodigo = this.servidorIdVO.getSerVinCodigo();
		setExibeModalAutenticacao(false);
		if(matricula == null || vinCodigo == null){
			FacesContext.getCurrentInstance().validationFailed();
		}
	}
	public void inserirLogDeImpressao(){
		try {
			this.criadoEm = new Date();
			this.dataInternacaoFormatada = DateUtil.dataToString(criadoEm, "dd/MM/yyyy");
			emergenciaFacade.inserirLogImpressao(itemAnamnese.getId().getConNumero(), 
					itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp(), 
					DominioEventoLogImpressao.MCOR_ADMISSAO_OBS.toString(),
					this.matricula, this.vinCodigo, this.criadoEm);			
			setParametersParaEmitirRelatorioAdmissaoObstetrica(itemAnamnese.getId().getConNumero(), itemAnamnese.getId().getGsoPacCodigo(), itemAnamnese.getId().getGsoSeqp(), this.matricula, this.vinCodigo, this.criadoEm);
			//TODO - Desabilitar campos da aba Intercorrencias - quando a estoria desta aba for desenvolvida.
			this.realizarInternacaoPacienteAutomaticamente();
			this.setInternacaoDisabled(true);
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			super.apresentarExcecaoNegocio(e);
		}
	}
	private void setParametersParaEmitirRelatorioAdmissaoObstetrica(Integer numeroConsulta, Integer pacCodigo, Short seqp, Integer matricula, Short vinCodigo, Date criadoEm) throws ApplicationBusinessException {
		this.numeroConsulta = numeroConsulta;
		this.pacCodigo = pacCodigo;
		this.seqp = seqp;
		this.matricula = matricula;
		this.vinCodigo = vinCodigo; //vinculo
		this.gerarPendenciaDeAssinaturaDigital = emergenciaFacade.gerarPendenciaDeAssinaturaDigital(this.matricula, this.vinCodigo);
	}
	
	public boolean habilitarPrescricao(){		
		return (verificaSeModuloEstaAtivo("prescricaomedica")); 
	}
	
	public boolean validaLoginPrescricao(){
		return (!isDisabled() && this.itemAnamnese != null && this.itemAnamnese.getConsulta() != null && (getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarPrescricaoMedicaPerinatologia", "visualizar") || getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "executarPrescricaoMedicaPerinatologia", "executar")));
	}
		
	public void abrirModalPrescricao() {
		if(!validaLoginPrescricao()) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_PERMISSAO_AUTENTICAR_USUARIO_PRESCRICAO_CONSULTA_CO");
		}else{
			setExibeModalAutenticacaoPrescrever(true);
			autenticacaoController.setMensagemErro("ERRO_PERMISSAO_AUTENTICAR_USUARIO_PRESCRICAO_CONSULTA_CO");
			openDialog("modalAutenticacaoPrescreverAbaConsultaCOWG");
		}
	}	
	
	public void elaborarPrescricaoMedica() {
		try {
			if(validaLoginPrescricao()){			
			emergenciaFacade.elaborarPrescricaMedica(itemAnamnese.getId().getConNumero(), this.seqUnidadeFuncional);
			final String jsExecute = "parent.tab.loadPage(window.name, '/aghu/pages/prescricaomedica/verificaprescricao/verificaPrescricaoMedica.xhtml?" + "codPac=" + itemAnamnese.getId().getGsoPacCodigo() + ";" + "seqp=" + itemAnamnese.getId().getGsoSeqp() + ";" + "prontPac=" + getProntuario() + ";"
					+ "origemEmergencia=true;" + "voltarPara=" + getTelaOrigem() + ";" + "abaOrigem=" + getAbaOrigem() + ";" + "numeroConsulta=" + itemAnamnese.getConsulta().getNumero() + ";" + "nome=" + nomePaciente + ";" + "idade=" + idadeFormatada + ";" + "trgSeq=" + trgSeq + ";"
					+ "param_cid=#{javax.enterprise.context.conversation.id')";
			RequestContext.getCurrentInstance().execute(jsExecute);
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void realizarInternacaoPacienteAutomaticamente(){
		try{ String hostName = super.getEnderecoIPv4HostRemoto().getHostName();
			 emergenciaFacade.realizarInternacaoPacienteAutomaticamente(this.matricula, this.vinCodigo, this.pacCodigo, this.seqp, this.numeroConsulta, hostName, registrarGestacaoController.getTrgSeq());
			 this.apresentarMsgNegocio(Severity.INFO, "INTERNACAO_REALIZADA_SUCESSO");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
			FacesContext.getCurrentInstance().validationFailed();
		} catch (UnknownHostException e) {
			super.apresentarMsgNegocio(Severity.ERROR, "ERRO_OBTER_HOST_NAME");
		}
	}
	
	public void validarPosicaoObrigatorio(){
		if(anamneseEfs != null && anamneseEfs.getEspessuraCervice() != null){
			posicaoObrigatorio = true;
		} else {
			posicaoObrigatorio = false;
		} 
	}
	public void validarBatimentoFetal() throws ApplicationBusinessException{
		if(anamneseEfs != null){
				emergenciaFacade.validarBCF(anamneseEfs, permNrBfc1, 1);emergenciaFacade.validarBCF(anamneseEfs, permNrBfc2, 2);emergenciaFacade.validarBCF(anamneseEfs, permNrBfc3, 3);emergenciaFacade.validarBCF(anamneseEfs, permNrBfc4, 4);emergenciaFacade.validarBCF(anamneseEfs, permNrBfc5, 5);emergenciaFacade.validarBCF(anamneseEfs, permNrBfc6, 6);
		}
	}
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	public void redirecionarTermosConsentimento() {
		try {
			AghParametros parametroConvenioPadrao = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_URL_TERMOS_CONSENTIMENTO_CO);
			RequestContext.getCurrentInstance().execute("window.open('" + parametroConvenioPadrao.getVlrTexto() + "')");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	public McoAnamneseEfs getAnamneseEfs() {	return anamneseEfs;}
	public void setAnamneseEfs(McoAnamneseEfs anamneseEfs) {this.anamneseEfs = anamneseEfs;	}
	public McoAnamneseEfs getAnamneseEfsOriginal() {	return anamneseEfsOriginal;	}
	public void setAnamneseEfsOriginal(McoAnamneseEfs anamneseEfsOriginal) {	this.anamneseEfsOriginal = anamneseEfsOriginal;	}
	public McoPlanoIniciais getPlanoIniciais() {return planoIniciais;}
	public void setPlanoIniciais(McoPlanoIniciais planoIniciais) {this.planoIniciais = planoIniciais;}
	public Boolean getEditConsulta() {return editConsulta;}
	public void setEditConsulta(Boolean editConsulta) {	this.editConsulta = editConsulta;}
	public Boolean getEditConduta() {return editConduta;}
	public void setEditConduta(Boolean editConduta) {this.editConduta = editConduta;}
	public Boolean getRequiredDataAlterar() {return requiredDataAlterar;	}
	public void setRequiredDataAlterar(Boolean requiredDataAlterar) {this.requiredDataAlterar = requiredDataAlterar;}
	public McoDiagnostico getDiagnostico() {return diagnostico;	}
	public void setDiagnostico(McoDiagnostico diagnostico) {this.diagnostico = diagnostico;}
	public CidVO getCidVO() {return cidVO;}
	public void setCidVO(CidVO cidVO) {	this.cidVO = cidVO;}
	public McoConduta getConduta() {return conduta;}
	public void setConduta(McoConduta conduta) {this.conduta = conduta;	}
	public List<McoAnamneseEfs> getListConsultas() {return listConsultas;}
	public void setListConsultas(List<McoAnamneseEfs> listConsultas) {	this.listConsultas = listConsultas;	}
	public List<McoPlanoIniciais> getListCondutas() {	return listCondutas;	}
	public void setListCondutas(List<McoPlanoIniciais> listCondutas) {	this.listCondutas = listCondutas;	}
	public Integer getPacCodigo() {	return pacCodigo;	}
	public void setPacCodigo(Integer pacCodigo) {	this.pacCodigo = pacCodigo;	}
	public Short getSeqp() {	return seqp;	}
	public void setSeqp(Short seqp) {	this.seqp = seqp;	}
	public Integer getNumeroConsulta() {	return numeroConsulta;}
	public void setNumeroConsulta(Integer numeroConsulta) {	this.numeroConsulta = numeroConsulta;	}
	public McoConduta getCondutaSelecionada() {	return condutaSelecionada;	}
	public void setCondutaSelecionada(McoConduta condutaSelecionada) {	this.condutaSelecionada = condutaSelecionada;	}
	public McoAnamneseEfs getItemAnamnese() {	return itemAnamnese;	}	
	public void setItemAnamnese(McoAnamneseEfs itemAnamnese) {	this.itemAnamnese = itemAnamnese;	}
	public Boolean getConsultaEmEdicao() {	return consultaEmEdicao;}
	public void setConsultaEmEdicao(Boolean consultaEmEdicao) {	this.consultaEmEdicao = consultaEmEdicao;	}
	public Boolean getCondutaEmEdicao() {return condutaEmEdicao;}
	public void setCondutaEmEdicao(Boolean condutaEmEdicao) {this.condutaEmEdicao = condutaEmEdicao;}
	public McoPlanoIniciais getPlanoIniciaisExclusao() {return planoIniciaisExclusao;	}
	public void setPlanoIniciaisExclusao(McoPlanoIniciais planoIniciaisExclusao) {	this.planoIniciaisExclusao = planoIniciaisExclusao;	}
	public DominioSimNao getIndDthrIgnorada() {	return indDthrIgnorada;	}
	public void setIndDthrIgnorada(DominioSimNao indDthrIgnorada) {	this.indDthrIgnorada = indDthrIgnorada;}
	public DominioSimNao getIndAcelTrans() {	return indAcelTrans;	}
	public void setIndAcelTrans(DominioSimNao indAcelTrans) {	this.indAcelTrans = indAcelTrans;	}
	public DominioSimNao getIndMovFetal() {	return indMovFetal;	}
	public void setIndMovFetal(DominioSimNao indMovFetal) {	this.indMovFetal = indMovFetal;	}
	public McoAnamneseEfs getDadosEdicaoAnamnese() {	return dadosEdicaoAnamnese;	}
	public void setDadosEdicaoAnamnese(McoAnamneseEfs dadosEdicaoAnamnese) {	this.dadosEdicaoAnamnese = dadosEdicaoAnamnese;	}
	public Long getNrBfc() {return nrBfc;}
	public void setNrBfc(Long nrBfc) {	this.nrBfc = nrBfc;}
	public boolean isPermNrBfc1() {	return permNrBfc1;}
	public void setPermNrBfc1(boolean permNrBfc1) {	this.permNrBfc1 = permNrBfc1;}
	public boolean isPermNrBfc2() {	return permNrBfc2;}
	public void setPermNrBfc2(boolean permNrBfc2) {	this.permNrBfc2 = permNrBfc2;}
	public boolean isPermNrBfc3() {	return permNrBfc3;}
	public void setPermNrBfc3(boolean permNrBfc3) {	this.permNrBfc3 = permNrBfc3;};
	public boolean isPermNrBfc4() {	return permNrBfc4;}
	public void setPermNrBfc4(boolean permNrBfc4) {	this.permNrBfc4 = permNrBfc4;}
	public boolean isPermNrBfc5() {	return permNrBfc5;}
	public void setPermNrBfc5(boolean permNrBfc5) {	this.permNrBfc5 = permNrBfc5;}
	public boolean isPermNrBfc6() {	return permNrBfc6;}
	public void setPermNrBfc6(boolean permNrBfc6) {	this.permNrBfc6 = permNrBfc6;}
	public boolean isMostraModalGravarConsultaCO() {return mostraModalGravarConsultaCO;}
	public void setMostraModalGravarConsultaCO(boolean mostraModalGravarConsultaCO) {this.mostraModalGravarConsultaCO = mostraModalGravarConsultaCO;}
	public DominioDinamicaUterina getDinamicaUterina() {return dinamicaUterina;}
	public void setDinamicaUterina(DominioDinamicaUterina dinamicaUterina) {this.dinamicaUterina = dinamicaUterina;}
	public DominioDelee getPlanoDelee() {return planoDelee;}
	public void setPlanoDelee(DominioDelee planoDelee) { this.planoDelee = planoDelee;	}
	public boolean isReadDinUterina() {	return readDinUterina;	}
	public void setReadDinUterina(boolean readDinUterina) {	this.readDinUterina = readDinUterina;	}
	public Integer getProntuario() { return prontuario;	}
	public void setProntuario(Integer prontuario) {	this.prontuario = prontuario; }
	public Short getSeqUnidadeFuncional() {	return seqUnidadeFuncional;	}
	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {	this.seqUnidadeFuncional = seqUnidadeFuncional;	}
	public String getNomePaciente() {	return nomePaciente;	}
	public void setNomePaciente(String nomePaciente) {	this.nomePaciente = nomePaciente;	}
	public String getIdadeFormatada() {	return idadeFormatada;	}
	public void setIdadeFormatada(String idadeFormatada) {	this.idadeFormatada = idadeFormatada;}
	public String getTelaOrigem() {	return TELA_ORIGEM;}
	public String getAbaOrigem() {	return NAME_ABA_ORIGEM;}
	public Boolean getRetornoTelaPrescricao() {return retornoTelaPrescricao;}
	public void setRetornoTelaPrescricao(Boolean retornoTelaPrescricao) {this.retornoTelaPrescricao = retornoTelaPrescricao;}
	public boolean isPermDesbloquearConsultaCO() {return permDesbloquearConsultaCO;}
	public void setPermDesbloquearConsultaCO(boolean permDesbloquearConsultaCO) {this.permDesbloquearConsultaCO = permDesbloquearConsultaCO;}
	public boolean isHabilitaBloqueio() {return desbloqueioConsultaCOVO.isHabilitaBloqueio();}
	public boolean isHabilitaFinalizarConsulta() {return desbloqueioConsultaCOVO.isHabilitaFinalizarConsulta();}
	public boolean isHabilitaInternar() {return desbloqueioConsultaCOVO.isHabilitaInternar();}
	public boolean isPermiteAlterarAbaGestAtual() {return desbloqueioConsultaCOVO.isPermiteAlterarAbaGestAtual();}
	public boolean isPermiteAlterarAbaConsCO() {return desbloqueioConsultaCOVO.isPermiteAlterarAbaConsCO();}
	public boolean isNotasAdicionais() {return desbloqueioConsultaCOVO.isNotasAdicionais();}
	public boolean isHabilitaDesbloqueio() {return desbloqueioConsultaCOVO.isHabilitaDesbloqueio();}
	public boolean isHabilitaExclusao() {return desbloqueioConsultaCOVO.isHabilitaExclusao();}
	public Integer getMatricula() {return matricula;}
	public void setMatricula(Integer matricula) {this.matricula = matricula;}
	public Short getVinCodigo() {return vinCodigo;}
	public void setVinCodigo(Short vinCodigo) {this.vinCodigo = vinCodigo;}
	public boolean isDisabled() {return disabled;}
	public boolean isInternacaoDisabled() {return internacaoDisabled;}
	public ServidorIdVO getServidorIdVO() {return servidorIdVO;}
	public void setServidorIdVO(ServidorIdVO servidorIdVO) {this.servidorIdVO = servidorIdVO;}
	public boolean isExibeModalTipoDeGravidez() {return exibeModalTipoDeGravidez;}
	public boolean isExibeModalSolicitarExames() {return exibeModalSolicitarExames;	}
	public Integer getAtdSeq() {return atdSeq;}
	public void setAtdSeq(Integer atdSeq) {	this.atdSeq = atdSeq;}
	public Date getCriadoEm() {	return criadoEm;}
	public boolean isGerarPendenciaDeAssinaturaDigital() {return gerarPendenciaDeAssinaturaDigital;	}
	public boolean isPosicaoObrigatorio() {	return posicaoObrigatorio;}
	public void setPosicaoObrigatorio(boolean posicaoObrigatorio) { this.posicaoObrigatorio = posicaoObrigatorio; }
	public boolean isExibeModalAutenticacao() {	return exibeModalAutenticacao; }
	public void setExibeModalAutenticacao(boolean exibeModalAutenticacao) { this.exibeModalAutenticacao = exibeModalAutenticacao;}
	public String getDataInternacaoFormatada() { return dataInternacaoFormatada; }
	public void setDataInternacaoFormatada(String dataInternacaoFormatada) { this.dataInternacaoFormatada = dataInternacaoFormatada;}
	public Long getTrgSeq() {return trgSeq;	}
	public void setTrgSeq(Long trgSeq) {this.trgSeq = trgSeq;}
	public boolean isAlteracaoConsulta() {	return alteracaoConsulta;}
	public void setAlteracaoConsulta(boolean alteracaoConsulta) {	this.alteracaoConsulta = alteracaoConsulta;}
	public boolean isAlteracaoConduta() {	return alteracaoConduta;	}
	public void setExibeModalAutenticacaoPrescrever(boolean exibeModalAutenticacaoPrescrever) {	this.exibeModalAutenticacaoPrescrever = exibeModalAutenticacaoPrescrever;}
	public boolean isExibeModalAutenticacaoPrescrever() {	return exibeModalAutenticacaoPrescrever;  }
	public void setAlteracaoConduta(boolean alteracaoConduta) {	this.alteracaoConduta = alteracaoConduta;}
	public boolean isMostrarModalDadosPendentesConsultaCO() {return mostrarModalDadosPendentesConsultaCO;}
	public void setMostrarModalDadosPendentesConsultaCO(boolean mostrarModalDadosPendentesConsultaCO) {	this.mostrarModalDadosPendentesConsultaCO = mostrarModalDadosPendentesConsultaCO;	}
	public Boolean getFinalizarConsulta() {	return finalizarConsulta;}
	public void setFinalizarConsulta(Boolean finalizarConsulta) {	this.finalizarConsulta = finalizarConsulta;}
	public boolean isMantemAba() {return mantemAba;	}
	public void setMantemAba(boolean mantemAba) {	this.mantemAba = mantemAba;	}
	public void setDisabled(boolean disabled) {this.disabled = disabled;}
	public void setInternacaoDisabled(boolean internacaoDisabled) {this.internacaoDisabled = internacaoDisabled;}
	public DesbloqueioConsultaCOVO getDesbloqueioConsultaCOVO() {return desbloqueioConsultaCOVO;}
	public void setDesbloqueioConsultaCOVO(DesbloqueioConsultaCOVO desbloqueioConsultaCOVO) {this.desbloqueioConsultaCOVO = desbloqueioConsultaCOVO;}
}