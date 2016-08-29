package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.action.ListarCirurgiasController;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.CertificarRelatorioCirurgiasPdtVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoProcDiagTerapController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7430868288084292757L;
		
	private final Integer TAB_0=0, TAB_2=2, TAB_4=4, TAB_6=6, TAB_7=7; 
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;


	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	
	@Inject
	private DescricaoProcDiagTerapEquipeController descricaoProcDiagTerapEquipeController;

	@Inject
	private DescricaoProcDiagTerapIndicacaoController descricaoProcDiagTerapIndicacaoController;
	
	@Inject
	private DescricaoProcDiagTerapTecnicaController descricaoProcDiagTerapTecnicaController;

	@Inject
	DescricaoProcDiagTerapProcedController descricaoProcDiagTerapProcedController;

	@Inject
	private DescricaoProcDiagTerapNotaAdicionalController descricaoProcDiagTerapNotaAdicionalController;

	@Inject
	DescricaoProcDiagTerapResultadoController descricaoProcDiagTerapResultadoController;

	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	@Inject
	private RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
		
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private DescricaoProcDiagTerapSedacaoController descricaoProcDiagTerapSedacaoController;

	
	private CirurgiaVO crgSelecionada;
	
	@Inject
	private ListarCirurgiasController listarCirurgiasController;
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	
	//@Out(required = false, scope = ScopeType.SESSION)
	private DescricaoProcDiagTerapVO descricaoProcDiagTerapVO;
	
	private Integer abaSelecionada;
	
	// Parametros de page.xml
	private Integer ddtSeq;
	private Integer ddtCrgSeq;
	private Short unfSeq;
	private Boolean existeIntegracaoAghWebAGHU;
	
	private Integer atdSeqSelecionado;
	
	private boolean showNotaAdicional;

	private List<PdtTecnica> tecnicas;
	
	private PdtDadoDesc dadoDesc;
	private PdtProc firstPdtProc;
	private List<PdtEquipamento> equipamentos;
	private PdtEquipamento equipamento;
	private PdtTecnica tecnica;
	
	private List<MbcTipoAnestesias> tipoAnestesias;
	private MbcTipoAnestesias tipoAnestesia;
	
	private Boolean tecnicaSedacao = Boolean.FALSE;
	
	private String banco = null;
	private String urlBaseWebForms = null;
	
	private static final String PAGE_LISTAR_CIRURGIAS ="blococirurgico-listaCirurgias";
	private static final String PAGE_DESCRICAO_PROC_DIAG_TERAP = "blococirurgico-descricaoProcDiagTerap";
	private static final String PAGE_RELATORIO_LISTAR_CIRURGIAS = "blococirurgico-relatorio-CIR";
	private static final String PAGE_RELATORIO_LISTAR_CIRURGIAS_PDT = "blococirurgico-relatorio-PDT";
	private final String PAGE_EXAMES_SOLIC_EXAMES ="exames-solicitacaoExameCRUD";
	
	private PdtDescricao descricao;
	
	private boolean showAvalPreSedacao;
	
	private boolean avisouUltrapassaTempoMinimoCirurgia = false;

	private Integer indiceAbaSelecionada;
	
	private boolean focusPrincipal;

	private String mensagemTempoMinimoCirurgia;
	
	public void iniciar() {
		try {
			Integer pacCodigo = crgSelecionada.getPacCodigo();
			AipPacientes paciente = null;
			if (pacCodigo != null) {
				paciente = pacienteFacade.obterPacientePorCodigo(pacCodigo);
			}
			
			// VO utilizado no cabeçalho da Descrição de PDT
			descricaoProcDiagTerapVO = new DescricaoProcDiagTerapVO();
			if (paciente != null) {
				StringBuilder sbDescricaoPaciente = new StringBuilder(150);
				Integer prontuario = paciente.getProntuario();
				if (prontuario != null) {
					sbDescricaoPaciente.append(prontuario.toString());
					sbDescricaoPaciente.append(' ');
				}
				sbDescricaoPaciente.append(paciente.getNome()).append(' ')
				.append(blocoCirurgicoFacade.obterIdadePorDataNascimento(paciente
						.getDtNascimento()));
				
				descricaoProcDiagTerapVO.setDescricaoPaciente(sbDescricaoPaciente.toString());		
			}
			
			descricaoProcDiagTerapVO.setLeito(crgSelecionada.getLeito());
			
			if (unfSeq != null) {
				AghUnidadesFuncionais unidadeFuncional = aghuFacade.obterUnidadeFuncional(unfSeq);	
				descricaoProcDiagTerapVO.setUnidadeFuncional(unidadeFuncional);
			}		
			
			descricao = blocoCirurgicoProcDiagTerapFacade.obterPdtDescricaoEAtendimentoPorSeq(ddtSeq);
			
			if (descricao != null) {
				descricaoProcDiagTerapVO.setEspecialidade(descricao.getEspecialidade());	
			}

			descricaoProcDiagTerapVO.setEspSeqCirurgia(crgSelecionada.getEspSeq());
			
			// Seta o seq da instância de PdtDescricao e o seg da cirurgia obtidos anteriormente na Lista de Cirurgias 
			descricaoProcDiagTerapVO.setDdtSeq(ddtSeq);
			descricaoProcDiagTerapVO.setDdtCrgSeq(ddtCrgSeq);		
			
			List<PdtProc> procs = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(descricaoProcDiagTerapVO.getDdtSeq());
			if(!procs.isEmpty()){
				firstPdtProc = procs.get(0);
				populaTecnicas();
				populaEquipamentos();
			}
			
			dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(descricaoProcDiagTerapVO.getDdtSeq());
			if(dadoDesc != null){
				tecnica = dadoDesc.getPdtTecnica();
				equipamento = dadoDesc.getPdtEquipamento();
				tipoAnestesia = dadoDesc.getMbcTipoAnestesias();
			}
	
			populaTipoAnestesia();
			verificaTipoAnestesias();
		
			showNotaAdicional = blocoCirurgicoProcDiagTerapFacade.habilitarNotasAdicionais(ddtCrgSeq, descricao);
			verificaAvalPreSedacao();
			renderAbas();
			
			this.existeIntegracaoAghWebAGHU = this.verificaSeHaIntegracaoEntreAGHUeAghWEB();
			this.popularParametros();
			this.renderAbas();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e); 
		} 
		
		setFocusPrincipal(true);
	}
	
	public void onTabChange(TabChangeEvent event) {
		String abaSelecionada = event.getTab().getId();
		if(StringUtils.isNotBlank(abaSelecionada)) {
			indiceAbaSelecionada = Integer.valueOf(StringUtils.replace(abaSelecionada, "aba", ""));
			setAbaSelecionada(indiceAbaSelecionada);
		}
	}


	private void popularParametros() {
		try {
			if (aghuFacade.isHCPA()){
				AghParametros aghParametroUrlBaseWebForms = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
				if (aghParametroUrlBaseWebForms != null) {
					urlBaseWebForms = aghParametroUrlBaseWebForms.getVlrTexto();
				}
				AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

    //verifica se a tecnica é do tipo anestesia
	private void verificaTipoAnestesias(){
		for (MbcTipoAnestesias tipo : tipoAnestesias) {
			if(tipo.getSituacao().isAtivo() && tipo.getIndNessAnest()){
				tecnicaSedacao = Boolean.TRUE;
			}
		} 
	}
	
	// *************************************************************************************************
	// Equipamentos e Técnica

	public void populaEquipamentos() {
		equipamentos = blocoCirurgicoProcDiagTerapFacade.pesquisarEquipamentosDiagnosticoTerapeutico(firstPdtProc.getPdtProcDiagTerap().getSeq());
	}

	public boolean isDisableEquipamentos(){
		boolean result = (firstPdtProc == null || equipamentos == null || equipamentos.isEmpty());
		return result;
	}
	
	public void populaTecnicas() {
		tecnicas = blocoCirurgicoProcDiagTerapFacade.listarPdtTecnicaPorDptSeq(firstPdtProc.getPdtProcDiagTerap().getSeq());
	}

	public boolean isDisableTecnicas(){
		//empty descricaoProcDiagTerapProcedController.procs or (not descricaoProcDiagTerapProcedController.equipamentos empty and descricaoProcDiagTerapProcedController.equipamentos.size eq 0)
		boolean result = (firstPdtProc == null || tecnicas == null || tecnicas.isEmpty());
		return result;
	}

	public void atualizarPdtEquipamento(){
		if(equipamento != null){
			dadoDesc.setPdtEquipamento(equipamento);
			dadoDesc.setDeqSeq(equipamento.getSeq());
			
		} else {
			dadoDesc.setPdtEquipamento(null);
			dadoDesc.setDeqSeq(null);
		}
			
		descricaoProcDiagTerapProcedController.atualizarPdtDadoDesc(dadoDesc);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void atualizarPdtTecnica(){
		if(tecnica != null){
			dadoDesc.setPdtTecnica(tecnica);
			dadoDesc.setDteSeq(tecnica.getSeq());
		} else {
			dadoDesc.setPdtTecnica(null);
			dadoDesc.setDteSeq(null);
		}
		descricaoProcDiagTerapProcedController.atualizarPdtDadoDesc(dadoDesc);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void excluirProc(){
		descricaoProcDiagTerapProcedController.excluirProc();
		
		if(descricaoProcDiagTerapProcedController.isAlterouFirstPdtProc()){
			tecnica = null;
			equipamento = null;
			
			tecnicas = null;
			equipamentos = null;
			
			if(!descricaoProcDiagTerapProcedController.getProcs().isEmpty()){
				firstPdtProc = descricaoProcDiagTerapProcedController.getProcs().get(0);
				populaEquipamentos();
				populaTecnicas();
			}
		}
	}
	
	public void posSalvarProcDiagTerap(){
		if(descricaoProcDiagTerapProcedController.isErrorSaveProcDiagTerap()){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PROCEDIMENTO_TERAPEUTICO_FALHA_INSERSAO");
			
		} else{ 
			if(descricaoProcDiagTerapProcedController.isAlterouFirstPdtProc()){
				firstPdtProc = descricaoProcDiagTerapProcedController.getProcs().get(0);
				populaEquipamentos();
				populaTecnicas();
			}
			
		//	String ds = descricaoProcDiagTerapProcedController.getProcs().get(descricaoProcDiagTerapProcedController.getProcs().size()-1).getPdtProcDiagTerap().getDescricao();
		//	this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_PROCEDIMENTO_TERAPEUTICO_INSERSAO_SUCESSO",ds);
		}
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void populaTipoAnestesia(){
		tipoAnestesias = blocoCirurgicoCadastroApoioFacade.obterMbcTipoAnestesiasAtivas();
	
	}
	
	public void posSelectionTipoAnestesia(){
		descricaoProcDiagTerapProcedController.posSelectionTipoAnestesia(tipoAnestesia);
		verificaAvalPreSedacao();
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	private void verificaAvalPreSedacao() {
		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TIPO_ANESTESIA_SEDACAO);
			if (aghParametros != null) {
				String str = aghParametros.getVlrTexto();
				if (str != null && tipoAnestesia != null && str.equalsIgnoreCase(tipoAnestesia.getDescricao())) {
					showAvalPreSedacao = true;
				} else {
					showAvalPreSedacao = false;
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void renderAbas() {
		this.initFlagAbas();
		descricaoProcDiagTerapEquipeController.iniciar(descricaoProcDiagTerapVO);
		descricaoProcDiagTerapIndicacaoController.iniciar(descricaoProcDiagTerapVO);
		if(tecnicaSedacao){
			descricaoProcDiagTerapSedacaoController.iniciar(descricaoProcDiagTerapVO);
		}
		descricaoProcDiagTerapProcedController.iniciar(descricaoProcDiagTerapVO);
		descricaoProcDiagTerapTecnicaController.iniciar(descricaoProcDiagTerapVO, descricao);
		descricaoProcDiagTerapResultadoController.iniciar(descricaoProcDiagTerapVO, descricao);
		descricaoProcDiagTerapNotaAdicionalController.iniciar(descricaoProcDiagTerapVO);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(descricaoProcDiagTerapVO.getDdtCrgSeq());
		relatorioListarCirurgiasPdtDescProcCirurgiaController.setDdtSeq( descricaoProcDiagTerapVO.getDdtSeq());
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	/**
	 * Seleciona a primeira aba caso nenhuma esteja selecionada.
	 */
	private void initFlagAbas() {
		
		if (getAbaSelecionada() == null && showNotaAdicional) {
			setAbaSelecionada(TAB_6);
		}
		if (getAbaSelecionada() == null) {
			setAbaSelecionada(TAB_0);
		}
	}
	
	public void visualizarRascunho(){
		setAbaSelecionada(TAB_7);
		if(descricaoProcDiagTerapTecnicaController.getStrDescricaoTecnica() != null){
	    	descricaoProcDiagTerapTecnicaController.gravarDescricaoTecnica();
	    }
		renderAbas();
	}

	public String cancelarDescricaoTerapeutica(){
		try {

			blocoCirurgicoProcDiagTerapFacade.desfazCarregamentoDescricaoCirurgicaPDT( descricaoProcDiagTerapVO.getDdtCrgSeq(), 
																					   descricaoProcDiagTerapVO.getDdtSeq());
			
			return PAGE_LISTAR_CIRURGIAS;
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} 
		
		return null;
	}
	
	public String liberarLaudoDefinitivo() {
		
		if (descricaoProcDiagTerapSedacaoController.getAvalPreSedacao() == null) {
			descricaoProcDiagTerapSedacaoController.iniciar(descricaoProcDiagTerapVO);
		}
		
		if(isShowAvalPreSedacao() && descricaoProcDiagTerapSedacaoController.validarCamposObrigatorios()){
			setAbaSelecionada(TAB_2);
			renderAbas();
			return null;
		}
		
		if (descricaoProcDiagTerapTecnicaController.getDthrInicioProcedimento() == null ||
				descricaoProcDiagTerapTecnicaController.getDthrFimProcedimento() == null) {
			descricaoProcDiagTerapTecnicaController.iniciar(descricaoProcDiagTerapVO, descricao);
		}
		
		if(descricaoProcDiagTerapTecnicaController.ultrapassaTempoMinimoCirurgia() && !avisouUltrapassaTempoMinimoCirurgia) {
			mensagemTempoMinimoCirurgia = getBundle().getString("MBC_01096");
		}
		
		if(descricaoProcDiagTerapTecnicaController.validarDatasDadoDesc()){
			try {
				CertificarRelatorioCirurgiasPdtVO certificar = this.blocoCirurgicoProcDiagTerapFacade.liberarLaudoDefinitivo(descricaoProcDiagTerapVO.getDdtSeq(), descricaoProcDiagTerapVO.getDdtCrgSeq(), unfSeq, descricao.getDthrExecucao(), DominioTipoDocumento.DEF);
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(certificar.getCrgSeq());
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setCirurgia(this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(certificar.getCrgSeq()));
				if(certificar.getGerarRelatorio()) {
					relatorioListarCirurgiasPdtDescProcCirurgiaController.setPrevia(certificar.getPrevia());
					for(Integer i = 0 ; i < certificar.getNroCopias(); i++) {
						relatorioListarCirurgiasPdtDescProcCirurgiaController.directPrint();
					}
				}
				else {
					if (certificacaoDigitalFacade.verificaProfissionalHabilitado()){
						relatorioListarCirurgiasPdtDescProcCirurgiaController.gerarPendenciaCertificacao();
					}	
				}
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(null);
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setDdtSeq(null);
				
				listarCirurgiasController.setDispararPesquisa(true);
				return PAGE_LISTAR_CIRURGIAS;
			} catch(BaseException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			} catch(Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR,
						"ERRO_GERAR_RELATORIO");
				LOG.error(e.getMessage(), e);
			}			
		}

		return null;
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadeAtivaPorNomeOuSigla(objPesquisa, 100) , 
				aghuFacade.pesquisarEspecialidadeAtivaPorNomeOuSigla(objPesquisa, 100).size());
	}
	
	public void gravarEspecialidade() {
		//PdtDescricao descricao = blocoCirurgicoProcDiagTerapFacade.obterPdtDescricao(ddtSeq, fetchArgsInnerJoin, null);
		if (descricao != null) {
			descricao.setEspecialidade(descricaoProcDiagTerapVO.getEspecialidade());
			
			try {
				blocoCirurgicoProcDiagTerapFacade.atualizarDescricao(descricao);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public String imprimirRascunho(){
		if(this.crgSelecionada != null){
			try {
				relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(this.descricaoProcDiagTerapVO.getDdtCrgSeq());
				
				relatorioListarCirurgiasPdtDescProcCirurgiaController.directPrint();
				
			} catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			}
		}	
		return PAGE_LISTAR_CIRURGIAS;
		
	}
	
	public String confirmarUltrapassaTempoMinimo() {
		super.closeDialog("modalConfirmacaoUltrapassarTempoMinimo");
		setAvisouUltrapassaTempoMinimoCirurgia(true);
		return liberarLaudoDefinitivo();
	}
	
	public Boolean isDescTecnica() {
		return TAB_4.equals(getAbaSelecionada());
	}	
	
	public Boolean isConcluir() {
		return TAB_7.equals(getAbaSelecionada());
	}
	
	public String redirecionarSolicitacaoExame() {
		blocoCirurgicoFacade.executarEventoBotaoPressionadoListaCirurgias(crgSelecionada);
		
		atdSeqSelecionado = crgSelecionada.getAtdSeq();
		
		if (atdSeqSelecionado == null) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MENSAGEM_CIRURGIA_REDIRECIONAR_PACIENTE_NAO_ESTA_EM_ATENDIMENTO");
		} else if (crgSelecionada.getProntuario() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MENSAGEM_CIRURGIA_REDIRECIONAR_PRONTUARIO_NULO", "Solicitação de Exame");
		} else {
			solicitacaoExameController.setAtendimentoSeq(atdSeqSelecionado);
			solicitacaoExameController.setPaginaChamadora(PAGE_DESCRICAO_PROC_DIAG_TERAP);
			
			return PAGE_EXAMES_SOLIC_EXAMES;
		}
		return null;
	}
	
	public String liberarLaudoPreliminar() {
		//PdtDescricao descricao = blocoCirurgicoProcDiagTerapFacade.obterPdtDescricao(ddtSeq, fetchArgsInnerJoin, null);
//		PdtDadoDesc dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(ddtSeq);
		
		// #43714 Cirurgias/ Descrição PDT - Erro ao liberar laudo Definitivo - Mensagem de Erro Já foi validado anteriormente
		//DescricaoProcDiagTerapTecnicaController - validarDatasDadoDesc
		
//		List<PdtProc> listaProc = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(descricaoProcDiagTerapVO.getDdtSeq());
//		MbcProcedimentoCirurgicos proced = null;
//		if (!listaProc.isEmpty()) {
//			proced = listaProc.get(0).getPdtProcDiagTerap().getProcedimentoCirurgico();
//		}
		
//		if (proced != null) {
//			try {
//				blocoCirurgicoProcDiagTerapFacade.validarTempoMinimoCirurgia(dadoDesc.getDthrInicio(), dadoDesc.getDthrFim(), proced.getTempoMinimo());
//			} catch (ApplicationBusinessException e) {
				// Exceção exibida apenas como "warning"
//				apresentarExcecaoNegocio(e);
//				LOG.error(e.getMessage(), e);
//			}
//		}
		
		try {
			blocoCirurgicoProcDiagTerapFacade.liberarLaudoPreliminar(descricao, unfSeq);
	//		this.apresentarMsgNegocio(Severity.INFO, 
	//				"MENSAGEM_DESCRICAO_PROC_DIAG_TERAP_LIBERAR_LAUDO_SUCESSO");
			
			listarCirurgiasController.setDispararPesquisa(true);
			return PAGE_LISTAR_CIRURGIAS;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private boolean verificaSeHaIntegracaoEntreAGHUeAghWEB(){
		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_INTEGRACAO_AGH_ORACLE_WEBFORMS);
			if (aghParametros != null) {
				String str = aghParametros.getVlrTexto();
				if (str != null && str.equalsIgnoreCase("S")) {
					return true;
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return false;
	}
	
	public Boolean validarUrlBaseWebFormsBanco(){
		return StringUtils.isBlank(urlBaseWebForms) || StringUtils.isBlank(banco);
	}
	
	public boolean moduloExamesLaudoNaoEstaAtivoEEhHospitalClinicas(){
		return !cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao()) && existeIntegracaoAghWebAGHU;
	}
	
	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public Integer getDdtCrgSeq() {
		return ddtCrgSeq;
	}

	public void setDdtCrgSeq(Integer ddtCrgSeq) {
		this.ddtCrgSeq = ddtCrgSeq;
	}
	
	public CirurgiaVO getCrgSelecionada() {
		return crgSelecionada;
	}

	public void setCrgSelecionada(CirurgiaVO crgSelecionada) {
		this.crgSelecionada = crgSelecionada;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public DescricaoProcDiagTerapVO getDescricaoProcDiagTerapVO() {
		return descricaoProcDiagTerapVO;
	}

	public void setDescricaoProcDiagTerapVO(
			DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
	}

	public boolean isShowNotaAdicional() {
		return showNotaAdicional;
	}

	public void setShowNotaAdicional(boolean showNotaAdicional) {
		this.showNotaAdicional = showNotaAdicional;
	}

	public Integer getAtdSeqSelecionado() {
		return atdSeqSelecionado;
	}

	public void setAtdSeqSelecionado(Integer atdSeqSelecionado) {
		this.atdSeqSelecionado = atdSeqSelecionado;
	}

	public List<PdtTecnica> getTecnicas() {
		return tecnicas;
	}

	public void setTecnicas(List<PdtTecnica> tecnicas) {
		this.tecnicas = tecnicas;
	}

	public PdtTecnica getTecnica() {
		return tecnica;
	}

	public void setTecnica(PdtTecnica tecnica) {
		this.tecnica = tecnica;
	}

	public PdtDadoDesc getDadoDesc() {
		return dadoDesc;
	}

	public void setDadoDesc(PdtDadoDesc dadoDesc) {
		this.dadoDesc = dadoDesc;
	}

	public PdtProc getFirstPdtProc() {
		return firstPdtProc;
	}

	public void setFirstPdtProc(PdtProc firstPdtProc) {
		this.firstPdtProc = firstPdtProc;
	}

	public List<PdtEquipamento> getEquipamentos() {
		return equipamentos;
	}

	public void setEquipamentos(List<PdtEquipamento> equipamentos) {
		this.equipamentos = equipamentos;
	}

	public PdtEquipamento getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(PdtEquipamento equipamento) {
		this.equipamento = equipamento;
	}

	public List<MbcTipoAnestesias> getTipoAnestesias() {
		return tipoAnestesias;
	}

	public void setTipoAnestesias(List<MbcTipoAnestesias> tipoAnestesias) {
		this.tipoAnestesias = tipoAnestesias;
	}

	public MbcTipoAnestesias getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(MbcTipoAnestesias tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public Boolean getTecnicaSedacao() {
		return tecnicaSedacao;
	}

	public void setTecnicaSedacao(Boolean tecnicaSedacao) {
		this.tecnicaSedacao = tecnicaSedacao;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getUrlBaseWebForms() {
		return urlBaseWebForms;
	}

	public void setUrlBaseWebForms(String urlBaseWebForms) {
		this.urlBaseWebForms = urlBaseWebForms;
	}
	
	public String visualizarDescricaoCirurgicaOuPDT() {
		String retorno = listarCirurgiasController.visualizarDescricaoCirurgicaOuPDT(crgSelecionada);
		if(StringUtils.endsWith(retorno, "PDT")){
			relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(crgSelecionada.getCrgSeq());
			relatorioListarCirurgiasPdtDescProcCirurgiaController.setVoltarPara(PAGE_DESCRICAO_PROC_DIAG_TERAP);
			return PAGE_RELATORIO_LISTAR_CIRURGIAS_PDT;
		} else if (StringUtils.endsWith(retorno, "CIR")){
			relatorioDescricaoCirurgiaController.setCrgSeq(crgSelecionada.getCrgSeq());
			relatorioDescricaoCirurgiaController.setVoltarPara(PAGE_DESCRICAO_PROC_DIAG_TERAP);
			return PAGE_RELATORIO_LISTAR_CIRURGIAS;
		}
		return null;
	}

	public String getVisualizarDescricaoCirurgicaOuPDT() {
		return listarCirurgiasController.visualizarDescricaoCirurgicaOuPDT(crgSelecionada);
	}
	
	public boolean getHabilitaBotoesDescricaoCirurgica(boolean permissaoDescricaoCirurgica, boolean permissaoDescricaoCirurgicaPDT){
		return listarCirurgiasController.habilitaBotoesDescricaoCirurgica(crgSelecionada.isOutraDescricao(), permissaoDescricaoCirurgica, permissaoDescricaoCirurgicaPDT);
	}

	public boolean isShowAvalPreSedacao() {
		return showAvalPreSedacao;
	}

	public void setShowAvalPreSedacao(boolean showAvalPreSedacao) {
		this.showAvalPreSedacao = showAvalPreSedacao;
	}

	public boolean isAvisouUltrapassaTempoMinimoCirurgia() {
		return avisouUltrapassaTempoMinimoCirurgia;
	}

	public void setAvisouUltrapassaTempoMinimoCirurgia(boolean avisouUltrapassaTempoMinimoCirurgia) {
		this.avisouUltrapassaTempoMinimoCirurgia = avisouUltrapassaTempoMinimoCirurgia;
	}

	public Integer getIndiceAbaSelecionada() {
		return indiceAbaSelecionada;
	}

	public void setIndiceAbaSelecionada(Integer indiceAbaSelecionada) {
		this.indiceAbaSelecionada = indiceAbaSelecionada;
	}

	public boolean isFocusPrincipal() {
		return focusPrincipal;
	}

	public void setFocusPrincipal(boolean focusPrincipal) {
		this.focusPrincipal = focusPrincipal;
	}
	
	public void focusSetFalse(){
	     setFocusPrincipal(false);
	}

	public String getMensagemTempoMinimoCirurgia() {
		return mensagemTempoMinimoCirurgia;
	}

	public void setMensagemTempoMinimoCirurgia(String mensagemTempoMinimoCirurgia) {
		this.mensagemTempoMinimoCirurgia = mensagemTempoMinimoCirurgia;
	}
}
