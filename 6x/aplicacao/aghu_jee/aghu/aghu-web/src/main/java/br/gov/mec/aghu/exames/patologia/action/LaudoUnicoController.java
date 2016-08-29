package br.gov.mec.aghu.exames.patologia.action;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.exames.patologia.vo.AelKitMatPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.pesquisa.action.CancelarExameController;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exames.solicitacao.action.DetalharItemSolicitacaoExameController;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameSismamaVO;
import br.gov.mec.aghu.exames.vo.ListaDesenhosMascarasExamesVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelExtratoExameApId;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.converter.NumeroApConverter;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class LaudoUnicoController extends ActionController  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(LaudoUnicoController.class);
	private static final long serialVersionUID = -4863767461222610277L;
	static final Integer rowSize = 25;
	static final Integer maxSize = 125;
	static final Integer empty = 0;	
	private final String TAB_0="aba0", TAB_1="aba1", TAB_2="aba2", TAB_3="aba3", TAB_4="aba4", TAB_5="aba5", TAB_6="aba6";
	
	public enum LaudoUnicoControllerExceptionCode implements BusinessExceptionCode  {
		ERRO_SELECIONAR_CAMPO_SITUACAO;
	}
	
	@EJB
	private IExamesFacade examesFacade;
	@EJB
	private IAghuFacade aghuFacade;	
	@Inject
	private IExamesPatologiaFacade examesPatologiaFacade;
	@Inject
	private LaudoLaudoUnicoController laudoLaudoUnicoController;
	@Inject
	private CadastroLaudoUnicoController cadastroLaudoUnicoController;
	@Inject
	private LaminaLaudoUnicoController laminaLaudoUnicoController;
	@Inject
	private IndiceBlocoLaudoUnicoController indiceBlocoLaudoUnicoController;
	@Inject
	private NotaAdicionalLaudoUnicoController notaAdicionalLaudoUnicoController;
	@Inject
	private DetalharItemSolicitacaoExameController detalharItemSolicitacaoExameController;
	@Inject
	private ResultadoExameHistopatologicoController resultadoExameHistopatologicoController;
	@Inject
	private CancelarExameController cancelarExameController;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private ISismamaFacade sismamaFacade;
	@EJB
	private IPermissionService permissionService;	
	private ItemSolicitacaoExameSismamaVO itemSolicitacaoExameSismamaVO;
	List<AelKitMatPatologiaVO> kitMatPatologiaVO = null;
	List<AelGrpTxtPadraoMacro> listaMarcro = null;
	List<AelGrpTxtPadraoDiags> listaDiagnostico = null;
	private TelaLaudoUnicoVO telaVo;
	/** Retorno */
	private String voltarPara;
	private AelConfigExLaudoUnico configExame;
	private Integer configExameId;
	private Long numeroAp;
	private Short unidadeExecutora;
	private Integer soeSeq;
	private Short seqp;
	// Atributos relacionados a SISMAMA
	private Boolean habilitaBotaoSismama = Boolean.FALSE;
	private Boolean exibeModalExamesSismama = Boolean.FALSE;
	private List<ItemSolicitacaoExameSismamaVO> listaItemSolicitacaoExameSismamaVO;
	private Boolean habilitaBotaoLiberarTecnica = Boolean.FALSE;
	private Boolean habilitaBotaoConcluirTecnica = Boolean.FALSE;
	private String codigoBarras;
	private Boolean pacienteNaoEncontrado = Boolean.FALSE;
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	private List<ListaDesenhosMascarasExamesVO> listaDesenhosMascarasExamesVO;
	private boolean dadosAlterados = false;
	private boolean exibirModalAlteracaoPendente = false;
	private boolean laudoAntigo;	
	private Integer selectedTab;
	private boolean renderLaudo;
	private boolean renderCadastro;
	private boolean renderIndiceBloco;
	private boolean renderLamina;
	private boolean renderImagem;
	private boolean renderNotaAdicional;
	private boolean renderConclusao;
	private Integer accordionIndexLaudoUnico;
	private final String PERMISSAO_PREENCHER_DIAGNOSTICO = "preencherDiagnosticoLaudoUnico";
	private final String PERMISSAO_PREENCHER_TOPOGRAFIA = "preencherTopografiaLaudoUnico";			
	/**
	 * Método de entrada do Laudo Único.<br>
	 * Prepara a aba de Laudo.<br>
	 * Chamado apenas uma vez na requisicao inicial da pagina.
	 */
	public void inicio() {
		itemSolicitacaoExameSismamaVO = null;
		exibeModalExamesSismama = Boolean.FALSE;
		
		consultarResultadosNotaAdicionalController.setExibeBotoes(false);
		consultarResultadosNotaAdicionalController.setListaDesenhosMascarasExamesVO(null);

		if (configExame == null && configExameId != null) {
			configExame = examesPatologiaFacade.obterConfigExameLaudoUncioPorChavePrimaria(configExameId);
		}
		
		AelAnatomoPatologico laudo = null;
		String sigla = null;
		
		if(voltarPara != null){
			accordionIndexLaudoUnico = 1;			
		}else{
			accordionIndexLaudoUnico = 0;
		}
		
		if(codigoBarras != null && !codigoBarras.isEmpty()) {
			
			accordionIndexLaudoUnico = 1;
			
			sigla = codigoBarras.substring(0,2);
			sigla = sigla.trim();
			String numeroExame = codigoBarras.substring(2,codigoBarras.length());
			numeroExame = numeroExame.trim();

			configExame = examesPatologiaFacade.obterPorSigla(sigla);
			if(configExame != null) {
				try {
					numeroAp = Long.valueOf(numeroExame);
				} catch (final NumberFormatException e) {
					accordionIndexLaudoUnico = 0;
					apresentarMsgNegocio(Severity.ERROR, "MSG_CODIGO_BARRAS_INVALIDO", codigoBarras);
					return;
				}
				if (telaVo == null || !telaVo.getNumeroAp().equals(numeroAp) || !sigla.equals(configExame.getSigla())) {
					laudo = examesPatologiaFacade.obterAelAnatomoPatologicoByNumeroAp(numeroAp, configExame.getSeq());
	
					if (laudo != null) {
						telaVo = new TelaLaudoUnicoVO();
						telaVo.setAelAnatomoPatologico(laudo);
						telaVo.setNumeroAp(numeroAp);
						pacienteNaoEncontrado = Boolean.TRUE;
	
					} else {
						NumeroApConverter converter = new NumeroApConverter();
						Object[] params = new Object[] { sigla, converter.getAsString(numeroAp) };
						pacienteNaoEncontrado = Boolean.FALSE;
						accordionIndexLaudoUnico = 0;
						apresentarMsgNegocio(Severity.ERROR, "MSG_LAUDO_NAO_ENCONTRADO_LAUDO_UNICO", params);
						return;
					}
				}
			} else {
				accordionIndexLaudoUnico = 0;
				apresentarMsgNegocio(Severity.ERROR, "MSG_CODIGO_BARRAS_INVALIDO", codigoBarras);
				pacienteNaoEncontrado = Boolean.FALSE;
				return;
			}
		} else {
			if (telaVo == null && configExame != null && numeroAp != null) {
				accordionIndexLaudoUnico = 1;
				telaVo = new TelaLaudoUnicoVO();

				laudo = examesPatologiaFacade.obterAelAnatomoPatologicoByNumeroAp(numeroAp, configExame.getSeq());

				if(laudo != null) {
					telaVo = new TelaLaudoUnicoVO();
					telaVo.setAelAnatomoPatologico(laudo);
					telaVo.setNumeroAp(numeroAp);
					pacienteNaoEncontrado = Boolean.TRUE;
				} else {
					NumeroApConverter converter = new NumeroApConverter();
					Object[] params = new Object[]{configExame.getSigla(), converter.getAsString(numeroAp)};
					pacienteNaoEncontrado = Boolean.FALSE;
					accordionIndexLaudoUnico = 0;
					this.apresentarMsgNegocio(Severity.ERROR, "MSG_LAUDO_NAO_ENCONTRADO_LAUDO_UNICO", params);
					return;
				}
			} 
		}
		
		inicioParte2(telaVo);
		
		
	}
	
	private void inicioParte2(TelaLaudoUnicoVO telaVo2) {
		if(telaVo != null && telaVo.getAelAnatomoPatologico() != null) {
			try {
				final AelExameAp aelExameAp = examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(telaVo.getAelAnatomoPatologico());
				this.laudoAntigo = isLaudoUnicoAntigo(aelExameAp);
				this.cadastroLaudoUnicoController.setLaudoAntigo(laudoAntigo);
				this.laminaLaudoUnicoController.setLaudoAntigo(laudoAntigo);
				telaVo.setAelExameAp(aelExameAp);
				telaVo.setConfigExame(aelExameAp.getConfigExLaudoUnico());
				if (aelExameAp.getAelAnatomoPatologicoOrigem() != null) {
					telaVo.setAelAnatomoPatologicoOrigem(examesPatologiaFacade.obterAelAnatomoPatologicoPorId(aelExameAp.getAelAnatomoPatologicoOrigem().getSeq()));
				}
				if (DominioSituacaoExamePatologia.RE.equals(telaVo.getAelExameAp().getEtapasLaudo())) {
					examesPatologiaFacade.carregaPatologistas(telaVo);
					examesPatologiaFacade.atualizaInformacoesClinicas(telaVo);
				}
				// Abaixo é feita uma conversão para Integer que não deverá ocasionar perda de precisão, pois o número de AP é zerado por ano.
				habilitaBotaoSismama = sismamaFacade.verificarExamesSismamaPorNumeroAp(telaVo.getAelAnatomoPatologico().getNumeroAp(), telaVo.getConfigExame().getSeq());
			} catch (BaseException e) {
				accordionIndexLaudoUnico = 0;
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(),e);
				return;
			}
			try {
				examesPatologiaFacade.aelpAtualizaTela(telaVo);
				this.populaConfiguracaoCampos(telaVo);
				this.renderAbas(null);
				getLabelFiltro();
				setHabilitaBotaoLiberarTecnica(this.examesFacade.habilitaBotaoTecnica(telaVo, DominioSituacaoExamePatologia.MC));
				setHabilitaBotaoConcluirTecnica(this.examesFacade.habilitaBotaoTecnica(telaVo, DominioSituacaoExamePatologia.TC));
			} 
			catch(BaseException e) {
				limpar();
				accordionIndexLaudoUnico = 0;
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(),e);
				return;
			}
		}
		
	}

	private boolean isLaudoUnicoAntigo(AelExameAp aelExameAp) {
		boolean retorno = false;
		for(AelExameApItemSolic item : aelExameAp.getAelExameApItemSolic()){
			if(item.getItemSolicitacaoExames() != null && item.getItemSolicitacaoExames().getNumeroAp() != null){
				retorno = true;
				break;
			}
		}		return retorno;	}	
	
	/**
	 * Popula os campos de configuração de exames.
	 * 
	 * @param telaVo
	 */
	private void populaConfiguracaoCampos(TelaLaudoUnicoVO telaVo) {
		if (telaVo != null && telaVo.getAelAnatomoPatologico() != null) {
			Integer versaoConf = telaVo.getAelAnatomoPatologico().getLu2VersaoConf();
			Integer lu2Seq = telaVo.getAelAnatomoPatologico().getConfigExame().getSeq();
			Boolean informacoesClinicas = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LIC, versaoConf, lu2Seq);
			Boolean descricaoMaterial = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDM, versaoConf, lu2Seq);
			Boolean macroscopia = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LMA, versaoConf, lu2Seq);
			Boolean combosDiagnostico = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDI, versaoConf, lu2Seq);
			Boolean descricaoDiagnostico = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDE, versaoConf, lu2Seq);
			Boolean topografias = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.CTO, versaoConf, lu2Seq);
			Boolean diagnosticos = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.CDI, versaoConf, lu2Seq);
			Boolean indiceBlocos = examesPatologiaFacade.buscarAtivoPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.IBL, versaoConf, lu2Seq);
			telaVo.setExibeInformacoesClinicas(this.verificaExibicao(informacoesClinicas));
			telaVo.setExibeDescricaoMaterial(this.verificaExibicao(descricaoMaterial));
			telaVo.setExibeMacroscopia(this.verificaExibicao(macroscopia));
			telaVo.setExibeCombosDiagnostico(this.verificaExibicao(combosDiagnostico));
			telaVo.setObrigCombosDiagnostico(this.verificaObrigatoriedade(combosDiagnostico));
			telaVo.setExibeDescricaoDiagnostico(this.verificaExibicao(descricaoDiagnostico));
			telaVo.setExibeTopografias(this.verificaExibicao(topografias));
			telaVo.setExibeDiagnosticos(this.verificaExibicao(diagnosticos));
			telaVo.setExibeIndiceBlocos(this.verificaExibicao(indiceBlocos));
		}
		if (this.telaVo != null){
			telaVo.setPermitirEdicaoCID(permitirEdicaoTopografiaCID(PERMISSAO_PREENCHER_DIAGNOSTICO));
			telaVo.setPermitirEdicaoTopografia(permitirEdicaoTopografiaCID(PERMISSAO_PREENCHER_TOPOGRAFIA));
		}
	}
	
	private boolean permitirEdicaoTopografiaCID(String permissao){
		if(isLaudoAssinado() ||
				!telaVo.isStTopografia() || 
				!this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), permissao, "persistir")){
			return false;
		}
		return true;
	}
	
	public boolean isLaudoAssinado(){
		if (telaVo.getAelExameAp() != null && telaVo.getAelExameAp().getEtapasLaudo() != null){		
			if (DominioSituacaoExamePatologia.LA.equals(telaVo.getAelExameAp().getEtapasLaudo())){
				return true;
			}			
		}					
		return false;
	}
	
	private boolean verificaExibicao(Boolean obrigatoriedade) {
		return obrigatoriedade != null ? true : false;
	}

	private boolean verificaObrigatoriedade(Boolean obrigatoriedade) {
		return obrigatoriedade != null ? obrigatoriedade.booleanValue() : false;
	}
	
	/**
	 * Obtem label do filtro de consulta.
	 * 
	 * @return Label
	 */
	public String getLabelFiltro() {
		StringBuilder sb = new StringBuilder().append(getBundle().getString("LABEL_LAUDO_UNICO"));
		// Exibe dados da consulta no cabeçalho do filtro se consulta foi realizada e filtro não está expandido.
		if (telaVo != null && configExame != null && pacienteNaoEncontrado) {
			sb.append(String.format(" | %s: %s", getBundle().getString("LABEL_EXAME_PATOLOGIA_CIRURGICA"), configExame.getSigla()));
			sb.append(String.format(" | %s: %s", getBundle().getString("LABEL_NUMERO_EXAME_PATOLOGIA_CIRURGICA"), NumberUtil.formatarNumeroAP(numeroAp.toString())));
			if (telaVo.getNomePaciente() != null) {
				sb.append(String.format(" | %s: %s", getBundle().getString("LABEL_NOME_PACIENTE"), telaVo.getNomePaciente()));
			} else {
				sb.append(String.format(" | %s", getBundle().getString("LABEL_PACIENTE_NAO_ENCONTRADO")));
			}
		}
		return sb.toString();
	}
	
	public void pesquisaInicio(){
		if(codigoBarras == null && configExame == null && numeroAp == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_LAUDO_UNICO_TIPO_EXAME");
			apresentarMsgNegocio(Severity.ERROR, "MSG_LAUDO_UNICO_NUMERO_AP");
		}else if(codigoBarras == null && configExame == null && numeroAp != null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_LAUDO_UNICO_TIPO_EXAME");
		}else if(codigoBarras == null && configExame != null && numeroAp == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_LAUDO_UNICO_NUMERO_AP");
		}else{
			limparInicio();
		}
		
	}
	
	public void limparInicio() {
		laudoLaudoUnicoController.limpar();
		cadastroLaudoUnicoController.limpar();
		laminaLaudoUnicoController.limpar();
		indiceBlocoLaudoUnicoController.limpar();
		notaAdicionalLaudoUnicoController.limpar();
		inicio();
	}	
	
	public void limpar(){
		telaVo = null; numeroAp = null;	configExame = null;	codigoBarras = null;	
		laudoLaudoUnicoController.limpar();
		cadastroLaudoUnicoController.limpar();
		laminaLaudoUnicoController.limpar();
		indiceBlocoLaudoUnicoController.limpar();
		notaAdicionalLaudoUnicoController.limpar();
		consultarResultadosNotaAdicionalController.limpar();
		accordionIndexLaudoUnico = 0;
	}
	
	public String detalharItemSolicitacaoExame(){
		final AelItemSolicitacaoExameLaudoUnicoVO vo = examesPatologiaFacade.obterAelItemSolicitacaoExameLaudoUnicoVO(telaVo.getAelExameAp(), true);
		if(vo != null){
			soeSeq = vo.getSoeSeq();
			seqp = vo.getSeqp();
			detalharItemSolicitacaoExameController.setSoeSeq(soeSeq);
			detalharItemSolicitacaoExameController.setSeqp(seqp);
			detalharItemSolicitacaoExameController.setVoltarPara("exames-laudounico");
			return "exames-detalharItemSolicitacaoExame";
		}
		return null;
	}
	
	public void assinarReabrirLaudo() {
		DominioSituacaoExamePatologia situacaoExame = DominioSituacaoExamePatologia.valueOf(telaVo.getAelExameAp().getEtapasLaudo().toString());
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			Boolean assinado = false;
			//ARRUMAR DIAGNÓSTICO
			assinado = this.examesPatologiaFacade.assinarReabrirLaudo(telaVo.getNumeroAp(), telaVo.getAelExameAp().getSeq(), telaVo.getAelExameAp().getConfigExLaudoUnico().getSeq(), telaVo.getAelExameAp().getEtapasLaudo(), this.laudoLaudoUnicoController.getDiagnostico(), this.laudoLaudoUnicoController.getListaMateriaisVO(), nomeMicrocomputador);
			//em caso de sucesso atualiza as permissoes da tela
			laudoLaudoUnicoController.setListaMateriaisVO(examesPatologiaFacade.listaMateriais(telaVo.getAelExameAp().getSeq(), telaVo.getAelExameAp().getEtapasLaudo()));
			inicio();
			if(assinado) {
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_LAUDO_ASSINADO_COM_SUCESSO");
			}
			else {
				this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_LAUDO_REABERTO_COM_SUCESSO");				
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			telaVo.getAelExameAp().setEtapasLaudo(situacaoExame);			
		}
	}
	
	public void getDescartarAlteracoes(){
		this.dadosAlterados = false;
		getOcultarModalAlteracaoPendente();
		this.renderAbas(null);
	}

	public void getOcultarModalAlteracaoPendente(){
		this.exibirModalAlteracaoPendente = false;
	}	
	
	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void onTabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null && event.getTab().getId() != null){
			renderAbas(event);
		}
	}
	
	public void renderAbas(TabChangeEvent event) {
		if (dadosAlterados) {
			exibirModalAlteracaoPendente = true;
			return;
		}
		this.initFlagAbas();
		if (event != null && event.getTab() != null && event.getTab().getId() != null) {
			String abaAtual = event.getTab().getId();
			if (TAB_0.equals(abaAtual)) {
				this.setRenderLaudo(true);
				laudoLaudoUnicoController.inicio(telaVo);
				this.telaVo.setSelectedTab(0);
				this.setSelectedTab(0);
			} else if (TAB_1.equals(abaAtual)) {
				this.setRenderCadastro(true);
				this.telaVo.setSelectedTab(1);
				this.setSelectedTab(1);
				this.cadastroLaudoUnicoController.inicio(telaVo);
			} else if (TAB_2.equals(abaAtual)) { // ESSA ABA NAO EXISTE MAIS (INDICE BLOCOS)
				this.setRenderIndiceBloco(true);
				this.indiceBlocoLaudoUnicoController.inicio(telaVo);
				this.telaVo.setSelectedTab(1);
				this.setSelectedTab(1);
			} else if (TAB_3.equals(abaAtual)) { // ESSA ABA PASSOU A SER CHAMADA DE INDICE BLOCOS
				this.setRenderLamina(true);
				if (telaVo.getAelExameAp() != null) {
					this.laminaLaudoUnicoController.inicio(telaVo.getAelExameAp().getSeq());
					this.telaVo.setSelectedTab(2);
					this.setSelectedTab(2);
				}
			} else if (TAB_4.equals(abaAtual)) {
				this.setRenderImagem(true); // disabled
				this.telaVo.setSelectedTab(3);
				this.setSelectedTab(3);
			} else if (TAB_5.equals(abaAtual)) {
				this.setRenderNotaAdicional(true);
				if (telaVo.getAelExameAp() != null) {
					this.notaAdicionalLaudoUnicoController.inicio(telaVo.getAelExameAp().getSeq());
					this.telaVo.setSelectedTab(4);
					this.setSelectedTab(4);
				}
			} else if (TAB_6.equals(abaAtual) && telaVo.getNumeroAp() != null) {
				this.setRenderConclusao(true);
				try {
					this.getDesenharMascaraLaudo(telaVo.getAelExameAp(), true);
					this.telaVo.setSelectedTab(5);
					this.setSelectedTab(5);
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
					LOG.error(e.getMessage(), e);
				}
			}
		}else{
			if(this.telaVo != null){
				if(this.telaVo.getSelectedTab() != null){					
					if(this.telaVo.getSelectedTab() == 1){
						this.setRenderCadastro(Boolean.TRUE);
						this.cadastroLaudoUnicoController.inicio(telaVo);
						this.setSelectedTab(1);
					} else if(this.telaVo.getSelectedTab() == 5){
						this.setRenderConclusao(true);
						try {
							this.getDesenharMascaraLaudo(telaVo.getAelExameAp(), true);
							this.telaVo.setSelectedTab(5);
							this.setSelectedTab(5);
						} catch (BaseException e) {
							apresentarExcecaoNegocio(e);
							LOG.error(e.getMessage(), e);
						}
					}

				}else{
					this.setRenderLaudo(true);
					laudoLaudoUnicoController.inicio(telaVo);
					this.telaVo.setSelectedTab(0);
					this.setSelectedTab(0);
				}
			}
		}
	}
	
	/**
	 * 
	 *  #21655 - Adaptar Relatorio Laudo Unico
	 * @param exameAP
	 * @throws MECBaseException
	 */
	public void getDesenharMascaraLaudo(AelExameAp exameAP, Boolean executarInicio) throws BaseException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		/**
		 * RN1 e RN2 - #21655
		 */
		Integer lu2Seq = telaVo.getAelAnatomoPatologico().getConfigExame().getSeq();
		if(!exameAP.getEtapasLaudo().equals(DominioSituacaoExamePatologia.LA)){
			this.examesPatologiaFacade.aelpAtualizaAel(lu2Seq, exameAP.getSeq(), nomeMicrocomputador);
		}
		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		final AelItemSolicitacaoExameLaudoUnicoVO itemVo = examesPatologiaFacade.obterAelItemSolicitacaoExameLaudoUnicoVO(exameAP, false);
		if(itemVo != null){
			soeSeq = itemVo.getSoeSeq();
			seqp = itemVo.getSeqp();
			solicitacoes.put(soeSeq, new Vector<Short>());
			solicitacoes.get(soeSeq).add(seqp);
			consultarResultadosNotaAdicionalController.setListaDesenhosMascarasExamesVO(null);
			consultarResultadosNotaAdicionalController.setIsHist(false);
			consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
			consultarResultadosNotaAdicionalController.setExibeBotoes(false);
			if (executarInicio) {
				consultarResultadosNotaAdicionalController.inicio();
			}
		}
	}
	
	/**
	 * Marca todas as abas como nao selecionadas.
	 */
	private void initFlagAbas() {
		this.setRenderLaudo(true);
		this.setRenderCadastro(false);
		this.setRenderIndiceBloco(false);
		this.setRenderImagem(false);
		this.setRenderNotaAdicional(false);
		this.setRenderConclusao(false);
	}
	
	/**
	 * Botão Cancelar
	 * @return
	 */
	public String cancelar() {
		if (StringUtils.isBlank(this.voltarPara)) {
			this.voltarPara = "cancelar";
		}
		return null;
	}
	
	public String cancelarExame(){
		AelUnidExecUsuario usuarioUnidadeExecutora;
		try {
			usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}
		if (usuarioUnidadeExecutora == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVIDOR_LOGADO_SEM_UNIDADE_EXECUTORA", obterLoginUsuarioLogado());
			return null;
		}
		unidadeExecutora = usuarioUnidadeExecutora.getUnfSeq().getSeq();
		final AelItemSolicitacaoExameLaudoUnicoVO vo = examesPatologiaFacade.obterAelItemSolicitacaoExameLaudoUnicoVO(telaVo.getAelExameAp(), false);
		if(vo != null){
			soeSeq = vo.getSoeSeq();
			seqp = vo.getSeqp();		
			cancelarExameController.setSoeSeq(soeSeq);
			cancelarExameController.setUfeUnfSeq(this.unidadeExecutora);
			cancelarExameController.setVoltarPara("exames-laudounico");			
			return "exames-cancelarExamesAreaExecutora";
		}		
		return null;	
	}
	
	/**
	 * Este método é executado pela ação do
	 * botão de confirmar da aba de conclusão
	 * quando o sumário alta for uma antecipação.
	 */
	public String voltarPara() {
		String retorno = this.voltarPara;
		if(retorno.equalsIgnoreCase("listaRealizacaoExamesPatologia")){
			limpar();
			//super.reiniciarPaginator(ListaRealizacaoExamesPatologiaController.class);
			this.telaVo = null;
			return retorno;
		}
		this.limparParametros();
		return retorno;
	}

	public void concluirLaudo() {
		this.initFlagAbas();
		this.setRenderConclusao(true);
		this.setSelectedTab(5);
		try {
			this.getDesenharMascaraLaudo(telaVo.getAelExameAp(), true);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void direcionaMapaLaminas() {
		this.setSelectedTab(3);
	}
	
	public String exibirModalExamesSismama() {
		Long numeroAp = telaVo.getNumeroAp();
		if (numeroAp != null) {
			try {
				listaItemSolicitacaoExameSismamaVO = sismamaFacade.
						pesquisarExameSismama(numeroAp, telaVo.getConfigExame().getSeq());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(),e);
				return null;
			}
			if (!(listaItemSolicitacaoExameSismamaVO.size() > 1)) {
				exibeModalExamesSismama = Boolean.FALSE;
				// Caso seja TRUE redireciona para a tela de questionário do SISMAMA
				if (listaItemSolicitacaoExameSismamaVO.size() == 1) {
					this.itemSolicitacaoExameSismamaVO = listaItemSolicitacaoExameSismamaVO.get(0);
					this.resultadoExameHistopatologicoController.setItemSolicitacaoExameSismamaVO(itemSolicitacaoExameSismamaVO);
					return "exames-resultadoExameHistopatologico";
				}
			} else {
				exibeModalExamesSismama = Boolean.TRUE;
				super.openDialog("modalExamesSismamaWG");
				return null;
			}
		}
		return null;
	}
	
	public String carregarItemSolicExameSismama(Integer itemSolicExameSismamaSoeSeq, 
			Short itemSolicExameSismamaSeqp) {
		if (itemSolicExameSismamaSoeSeq != null && itemSolicExameSismamaSeqp != null) {
			AelItemSolicitacaoExamesId itemSolicitacaoExameId = new AelItemSolicitacaoExamesId(itemSolicExameSismamaSoeSeq, itemSolicExameSismamaSeqp);
			for (ItemSolicitacaoExameSismamaVO itemSolicitacaoExameSismamaVO : listaItemSolicitacaoExameSismamaVO) {
				if (itemSolicitacaoExameSismamaVO.getItemSolicitacaoExame().getId().equals(itemSolicitacaoExameId)
						&& telaVo != null
						&& telaVo.getProntuario() != null
						&& telaVo.getNomePaciente() != null) {
					this.itemSolicitacaoExameSismamaVO = itemSolicitacaoExameSismamaVO;
					resultadoExameHistopatologicoController.setItemSolicitacaoExameSismamaVO(itemSolicitacaoExameSismamaVO);
					return "exames-resultadoExameHistopatologico";
				}
			}
		}
		return null;
	}
	
	public void verificarPreenchimentoExamesSismama() {
		Long numeroAp = telaVo.getNumeroAp();
		if (numeroAp != null) {
			try {
				sismamaFacade.verificarPreenchimentoExamesSismama(numeroAp, telaVo.getConfigExame().getSeq());
			} catch (ApplicationBusinessException e) {
				exibeModalExamesSismama = Boolean.TRUE;
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(),e);
			}	
		}
		exibeModalExamesSismama = Boolean.FALSE;
	}
	
	// LIBERAR TECNICA
	public String executaLiberarTecnica() {
		try {
			buscaSecoesConfiguracaoObrigatorias(DominioSituacaoExamePatologia.MC);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_LAUDO_LIBERADO_TECNICA");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return "laudoUnico";
	}
	
	// CONCLUIR TECNICA
	public String executaConcluirTecnica() {
		try {
			buscaSecoesConfiguracaoObrigatorias(DominioSituacaoExamePatologia.TC);
			this.apresentarMsgNegocio(Severity.INFO,"MSG_LAUDO_TECNICA_CONCLUIDA");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return "laudoUnico";
	}

	private void buscaSecoesConfiguracaoObrigatorias(DominioSituacaoExamePatologia domain) throws BaseException {
		this.examesFacade.buscaSecoesConfiguracaoObrigatorias(telaVo, domain);
		if (null == domain) {
			domain = DominioSituacaoExamePatologia.LA;
			this.telaVo.getAelExameAp().setEtapasLaudo(domain);
		} else {
			AelExameAp aelExameApOld = this.clonarAelExameAp(telaVo.getAelExameAp());
			this.telaVo.getAelExameAp().setEtapasLaudo(domain);
			this.examesPatologiaFacade.persistirAelExameAp(this.telaVo.getAelExameAp(),aelExameApOld);
		}
	}
	
	private AelExameAp clonarAelExameAp(AelExameAp exame) {
		AelExameAp copia = new AelExameAp();
		copia.setAelAnatomoPatologicoOrigem(exame.getAelAnatomoPatologicoOrigem());
		copia.setAelAnatomoPatologicos(exame.getAelAnatomoPatologicos());
		copia.setAelExameApItemSolic(exame.getAelExameApItemSolic());
		copia.setAelExtratoExameApses(exame.getAelExtratoExameApses());
		copia.setAelInformacaoClinicaAP(exame.getAelInformacaoClinicaAP());
		copia.setConfigExLaudoUnico(exame.getConfigExLaudoUnico());
		copia.setDthrImpressao(exame.getDthrImpressao());
		copia.setEtapasLaudo(exame.getEtapasLaudo());
		copia.setIndImpresso(exame.getIndImpresso());
		copia.setIndIndiceBloco(exame.getIndIndiceBloco());
		copia.setIndLaudoVisual(exame.getIndLaudoVisual());
		copia.setIndRevisaoLaudo(exame.getIndRevisaoLaudo());
		copia.setMateriais(exame.getMateriais());
		copia.setNroApOrigem(exame.getNroApOrigem());
		copia.setObservacaoTecnica(exame.getObservacaoTecnica());
		copia.setObservacoes(exame.getObservacoes());
		copia.setSeq(exame.getSeq());
		copia.setServidor(exame.getServidor());
		copia.setServidorRespImpresso(exame.getServidorRespImpresso());
		copia.setServidorRespLaudo(exame.getServidorRespLaudo());
		copia.setSituacao(exame.getSituacao());
		copia.setVersion(exame.getVersion());
		return copia;
	}
	
	private void limparParametros(){
		this.itemSolicitacaoExameSismamaVO= null;
		this.kitMatPatologiaVO = null;
		this.listaMarcro = null;
		this.listaDiagnostico = null;
		this.telaVo= null;
		this.voltarPara= null;
		this.configExame= null;
		this. configExameId= null;
		this.numeroAp= null;
		this.unidadeExecutora= null;
		this.soeSeq= null;
		this.seqp= null;
		this.codigoBarras = null;
		this.habilitaBotaoSismama = Boolean.FALSE;
		this.exibeModalExamesSismama = Boolean.FALSE;
		this.habilitaBotaoLiberarTecnica = Boolean.FALSE;
		this.habilitaBotaoConcluirTecnica = Boolean.FALSE;
		this.pacienteNaoEncontrado = false;
		this.listaItemSolicitacaoExameSismamaVO = null;
		this.listaDesenhosMascarasExamesVO = null;
	}
	
	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(String value) {
		return returnSGWithCount(examesPatologiaFacade.pesquisarAelConfigExLaudoUnico(
					AelConfigExLaudoUnico.Fields.NOME.toString(),value),
				this.pesquisarAelConfigExLaudoUnicoCount(value));
	}
	
	public Long pesquisarAelConfigExLaudoUnicoCount(String value) {
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnicoCount(value);
	}
	public String getVoltarPara() {
		return voltarPara;
	}
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	public TelaLaudoUnicoVO getTelaVo() {
		return telaVo;
	}
	public void setTelaVo(TelaLaudoUnicoVO telaVo) {
		this.telaVo = telaVo;
	}
	public Long getNumeroAp() {
		return numeroAp;
	}
	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Short getUnidadeExecutora() {
		return unidadeExecutora;
	}
	public void setUnidadeExecutora(Short unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}
	public Boolean getHabilitaBotaoSismama() {
		return habilitaBotaoSismama;
	}
	public void setHabilitaBotaoSismama(Boolean habilitaBotaoSismama) {
		this.habilitaBotaoSismama = habilitaBotaoSismama;
	}
	public ItemSolicitacaoExameSismamaVO getItemSolicitacaoExameSismamaVO() {
		return itemSolicitacaoExameSismamaVO;
	}
	public void setItemSolicitacaoExameSismamaVO(ItemSolicitacaoExameSismamaVO itemSolicitacaoExameSismamaVO) {
		this.itemSolicitacaoExameSismamaVO = itemSolicitacaoExameSismamaVO;
	}
	public List<ItemSolicitacaoExameSismamaVO> getListaItemSolicitacaoExameSismamaVO() {
		return listaItemSolicitacaoExameSismamaVO;
	}
	public void setListaItemSolicitacaoExameSismamaVO(List<ItemSolicitacaoExameSismamaVO> listaItemSolicitacaoExameSismamaVO) {
		this.listaItemSolicitacaoExameSismamaVO = listaItemSolicitacaoExameSismamaVO;
	}
	public Boolean getExibeModalExamesSismama() {
		return exibeModalExamesSismama;
	}
	public void setExibeModalExamesSismama(Boolean exibeModalExamesSismama) {
		this.exibeModalExamesSismama = exibeModalExamesSismama;
	}
	public AelConfigExLaudoUnico getConfigExame() {
		return configExame;
	}
	public void setConfigExame(AelConfigExLaudoUnico configExame) {
		this.configExame = configExame;
	}
	public Integer getConfigExameId() {
		return configExameId;
	}
	public void setConfigExameId(Integer configExameId) {
		this.configExameId = configExameId;
	}
	public void liberarLaudo(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção capturada:", e);
		}
		DominioSituacaoExamePatologia situacaoExame = DominioSituacaoExamePatologia.valueOf(telaVo.getAelExameAp().getEtapasLaudo().toString());
		try {
			buscaSecoesConfiguracaoObrigatorias(null);
			//ARRUMAR DIAGNÓSTICO
			this.examesPatologiaFacade.assinarLaudo(telaVo.getNumeroAp(), telaVo.getAelExameAp().getSeq(), telaVo.getAelExameAp().getConfigExLaudoUnico().getSeq(), this.laudoLaudoUnicoController.getDiagnostico(), this.laudoLaudoUnicoController.getListaMateriaisVO(), nomeMicrocomputador);
			//em caso de sucesso atualiza as permissoes da tela
			laudoLaudoUnicoController.setListaMateriaisVO(examesPatologiaFacade.listaMateriais(telaVo.getAelExameAp().getSeq(), telaVo.getAelExameAp().getEtapasLaudo()));
			inicio();
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_LAUDO_ASSINADO_COM_SUCESSO");
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			telaVo.getAelExameAp().setEtapasLaudo(situacaoExame);			
		}
	}

	public void reabrirLaudo(){
		String nomeMicrocomputador = null;
		Short maxSeqLa;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		DominioSituacaoExamePatologia situacaoExame = DominioSituacaoExamePatologia.valueOf(telaVo.getAelExameAp().getEtapasLaudo().toString());
		try {
			AelExtratoExameAp extratoExameAp = new AelExtratoExameAp();
			maxSeqLa = this.examesPatologiaFacade.obterMaxSeqAelExtratoExameAp(telaVo.getAelExameAp().getSeq(), DominioSituacaoExamePatologia.LA);
			if(maxSeqLa > 0) {
				maxSeqLa--;
			}
			AelExtratoExameApId id = new AelExtratoExameApId(telaVo.getAelExameAp().getSeq(), maxSeqLa);
			extratoExameAp = this.examesPatologiaFacade.obterAelExtratoExameApPorChavePrimaria(id);
			examesPatologiaFacade.reabrirLaudo(telaVo.getNumeroAp(), telaVo.getAelExameAp().getSeq(), telaVo.getAelExameAp().getConfigExLaudoUnico().getSeq(), extratoExameAp.getEtapasLaudo(), this.laudoLaudoUnicoController.getDiagnostico(), this.laudoLaudoUnicoController.getListaMateriaisVO(), nomeMicrocomputador);
			telaVo.getAelExameAp().setEtapasLaudo(extratoExameAp.getEtapasLaudo());
			//em caso de sucesso atualiza as permissoes da tela
			laudoLaudoUnicoController.setListaMateriaisVO(examesPatologiaFacade.listaMateriais(telaVo.getAelExameAp().getSeq(), telaVo.getAelExameAp().getEtapasLaudo()));
			inicio();
			this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_LAUDO_REABERTO_COM_SUCESSO");				
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			telaVo.getAelExameAp().setEtapasLaudo(situacaoExame);
		}
	}
	
	public Integer dataTableSize(Object lista){
		if (lista instanceof List){
			List<Object> listaRegistros = (List<Object>) lista;
			
			if (listaRegistros != null && !listaRegistros.isEmpty()){
				return listaRegistros.size() * rowSize;	
				// maxSize define o número de registros que serão mostrados antes do uso de scroll.
				// Comentado o trecho para impedir scroll no grid. 
//				Integer numRow = listaRegistros.size();	
//				if (numRow >= 5){
//					return maxSize;
//				} else {
//					return numRow * rowSize; 
//				}		
			}
		}		
		return empty;		
	}	
	
	public Boolean isHcpa() {
		return aghuFacade.isHCPA();
	}	
	public void setHabilitaBotaoLiberarTecnica(Boolean habilitaBotaoLiberarTecnica) {
		this.habilitaBotaoLiberarTecnica = habilitaBotaoLiberarTecnica;
	}
	public Boolean getHabilitaBotaoLiberarTecnica() {
		return habilitaBotaoLiberarTecnica;
	}
	public void setHabilitaBotaoConcluirTecnica(Boolean habilitaBotaoConcluirTecnica) {
		this.habilitaBotaoConcluirTecnica = habilitaBotaoConcluirTecnica;
	}
	public Boolean getHabilitaBotaoConcluirTecnica() {
		return habilitaBotaoConcluirTecnica;
	}
	public String getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}
	public List<ListaDesenhosMascarasExamesVO> getListaDesenhosMascarasExamesVO() {
		return listaDesenhosMascarasExamesVO;
	}
	public void setListaDesenhosMascarasExamesVO(List<ListaDesenhosMascarasExamesVO> listaDesenhosMascarasExamesVO) {
		this.listaDesenhosMascarasExamesVO = listaDesenhosMascarasExamesVO;
	}
	public boolean isDadosAlterados() {
		return dadosAlterados;
	}
	public void setDadosAlterados(boolean dadosAlterados) {
		this.dadosAlterados = dadosAlterados;
	}
	public boolean isExibirModalAlteracaoPendente() {
		return exibirModalAlteracaoPendente;
	}
	public void setExibirModalAlteracaoPendente(boolean exibirModalAlteracaoPendente) {
		this.exibirModalAlteracaoPendente = exibirModalAlteracaoPendente;
	}
	public Object getLaudoAntigo() {
		return laudoAntigo;
	}
	public void setLaudoAntigo(boolean laudoAntigo) {
		this.laudoAntigo = laudoAntigo;
	}
	public Integer getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}
	public boolean isRenderLaudo() {
		return renderLaudo;
	}
	public void setRenderLaudo(boolean renderLaudo) {
		this.renderLaudo = renderLaudo;
	}
	public boolean isRenderCadastro() {
		return renderCadastro;
	}
	public void setRenderCadastro(boolean renderCadastro) {
		this.renderCadastro = renderCadastro;
	}
	public boolean isRenderIndiceBloco() {
		return renderIndiceBloco;
	}
	public void setRenderIndiceBloco(boolean renderIndiceBloco) {
		this.renderIndiceBloco = renderIndiceBloco;
	}
	public boolean isRenderLamina() {
		return renderLamina;
	}
	public void setRenderLamina(boolean renderLamina) {
		this.renderLamina = renderLamina;
	}
	public boolean isRenderImagem() {
		return renderImagem;
	}
	public void setRenderImagem(boolean renderImagem) {
		this.renderImagem = renderImagem;
	}
	public boolean isRenderNotaAdicional() {
		return renderNotaAdicional;
	}
	public void setRenderNotaAdicional(boolean renderNotaAdicional) {
		this.renderNotaAdicional = renderNotaAdicional;
	}
	public boolean isRenderConclusao() {
		return renderConclusao;
	}
	public void setRenderConclusao(boolean renderConclusao) {
		this.renderConclusao = renderConclusao;
	}
	public Integer getAccordionIndexLaudoUnico() {
		return accordionIndexLaudoUnico;
	}
	public void setAccordionIndexLaudoUnico(Integer accordionIndexLaudoUnico) {
		this.accordionIndexLaudoUnico = accordionIndexLaudoUnico;
	}
}