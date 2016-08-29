package br.gov.mec.aghu.exames.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioPacientesInternadosExamesRealizarVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;


public class EmitirRelacaoPacientesInternadosExamesRealizarController extends ActionController{


	private static final Log LOG = LogFactory.getLog(EmitirRelacaoPacientesInternadosExamesRealizarController.class);

	private static final long serialVersionUID = -2314379049773019739L;

	private static final String VISUALIZAR_RELATORIO_PACIENTES_INTERNADOS_EXAMES_REALIZAR = "exames-visualizarRelatorioPacientesInternadosExamesRealizarPdf";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private RelatorioPacientesInternadosExamesRealizarPreparoController relatorioPacientesInternadosExamesRealizarPreparoController; 
	
	@Inject
	private RelatorioPacientesInternadosExamesRealizarJejumNpoController relatorioPacientesInternadosExamesRealizarJejumNpoController; 
	
	@Inject
	private RelatorioPacientesInternadosExamesRealizarTodosController relatorioPacientesInternadosExamesRealizarTodosController; 
	
	@Inject
	private RelatorioPacientesInternadosExamesRealizarUnidadeEmergenciaController relatorioPacientesInternadosExamesRealizarUnidadeEmergenciaController;
	
	@Inject
	private VisualizarRelatorioPacientesInternadosExamesRealizarPdfController visualizarRelatorioPacientesInternadosExamesRealizarPdfController;
	
	
	// Lista de relatorios impressos atraves do servidor de impressao: CUPS
	private List<String> listaRelatoriosImpressos;
	
	// Referencias do formulario
	private AghUnidadesFuncionais unidadeFuncional;
	private DominioSimNao imprimeRecomendacoesExame = DominioSimNao.N; // Seta o valor padrao (N) para impressao de recomendacoes de exame
	private Boolean jejumNpo;
	private Boolean preparo;
	private Boolean dietaDiferenciada;
	private Boolean unidadeEmergencia;
	private Boolean todos;
	private Boolean impressao;
	
	// Dados que serao gerados/impressos em PDF
	private RelatorioPacientesInternadosExamesRealizarVO relatorioPacientesInternadosExamesRealizarVO;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversation.setTimeout(600000);
	}
	
	public void iniciar() {
	 

		// Limpa lista de relatorios impressos
		this.listaRelatoriosImpressos = null;
	
	}

	/**
	 * Visualiza impressao na interface de abas
	 */
	public String visualizarImpressao(){
		
		impressao = Boolean.FALSE;
		
		// Verifica se no minimo uma das "check box" de tipo de relatorio foi marcada
		if(!this.verificarMinimoUmaCheckBoxTipoRelatorioMarcada()){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_MINIMO_TIPO_RELATORIO");
			return null;
		}	
		
		// Gera todos relatorios de pacientes com exames a realizar
		this.gerarRelatorios();
		
		// Para nenhum relatorio gerado: Exibe uma mensagem e evita o acesso a tela visualizacao de relatorios gerados 
		if(!this.jejumNpo && !this.preparo && !this.dietaDiferenciada && !this.unidadeEmergencia && !this.todos){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_NENHUM_RESULTADO_RELATORIO");
			return null;
		}
		
		// Seta referencias responsaveis pela ativacao das abas da interface de visualizacao de relatorios gerados
		visualizarRelatorioPacientesInternadosExamesRealizarPdfController.setAtivarAbaJejumNpo(this.jejumNpo);
		visualizarRelatorioPacientesInternadosExamesRealizarPdfController.setAtivarAbaPreparo(this.preparo);
		visualizarRelatorioPacientesInternadosExamesRealizarPdfController.setAtivarAbaDietaDiferenciada(this.dietaDiferenciada);
		visualizarRelatorioPacientesInternadosExamesRealizarPdfController.setAtivarAbaUnidadeEmergencia(this.unidadeEmergencia);
		visualizarRelatorioPacientesInternadosExamesRealizarPdfController.setAtivarAbaTodos(this.todos);

		// Retorna para tela de visualizacao de relatorios gerados
		return VISUALIZAR_RELATORIO_PACIENTES_INTERNADOS_EXAMES_REALIZAR;
	}

	/**
	 * Realiza a impressao direta de todos relatorios selecionados
	 */
	public void imprimir(){
		
		impressao = Boolean.TRUE;
		
		// Verifica se no minimo uma das "check box" de tipo de relatorio foi marcada
		if(!this.verificarMinimoUmaCheckBoxTipoRelatorioMarcada()){
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_MINIMO_TIPO_RELATORIO");
			return;
		}
		
		// Gera todos relatorios de pacientes com exames a realizar
		this.gerarRelatorios();

		// Gera uma nova lista de relatorios impressos. Esta lista sera exibida ao usuario!
		this.listaRelatoriosImpressos = new ArrayList<String>();
	
		// Contabiliza erros de impressao
		boolean isImpressaoConcluida = false; 
		
		// Imprime diretamente cada um dos relatorios atraves do servidor de impressao: CUPS
		try {
			if(this.jejumNpo){
				relatorioPacientesInternadosExamesRealizarJejumNpoController.directPrint();
				this.listaRelatoriosImpressos.add("Pacientes com Jejum/NPO");
			}
			
			if(this.preparo){
				relatorioPacientesInternadosExamesRealizarPreparoController.directPrint();
				this.listaRelatoriosImpressos.add("Pacientes com Preparo");
			}
			
			if(this.dietaDiferenciada){
				relatorioPacientesInternadosExamesRealizarJejumNpoController.directPrint();
				this.listaRelatoriosImpressos.add("Pacientes com Dieta Diferenciada");
			}
			
			if(this.unidadeEmergencia){
				relatorioPacientesInternadosExamesRealizarUnidadeEmergenciaController.directPrint();
				this.listaRelatoriosImpressos.add("Pacientes de Unidade de Emergência");
			}
			
			if(this.todos){
				relatorioPacientesInternadosExamesRealizarTodosController.directPrint();
				this.listaRelatoriosImpressos.add("Todos os Pacientes");
			}
			
			isImpressaoConcluida = true;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
			
		} 
		
		// Verifica a existencia de erros na impressao
		if (isImpressaoConcluida) {
			// Testa a ocorrencia de resultados na pesquisa de relatorios
			if(this.listaRelatoriosImpressos.isEmpty()){
				this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_NENHUM_RESULTADO_RELATORIO");
			} else{
				this.apresentarMsgNegocio(Severity.INFO,"TITLE_RELATORIOS_IMPRESSOS",this.listaRelatoriosImpressos);
			}
		}
	}
	
	/**
	 * Gera todos relatorios de pacientes com exames a realizar
	 */
	public void gerarRelatorios(){
		try {
			// Realiza todas as consultas necessarias
			this.relatorioPacientesInternadosExamesRealizarVO = this.examesFacade.pesquisarRelatorioPacientesInternadosExamesRealizar(unidadeFuncional, imprimeRecomendacoesExame, jejumNpo, preparo, dietaDiferenciada, unidadeEmergencia, todos);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		// Seta as colecoes de cada tipo de relatorio na sua respectiva controller 
		relatorioPacientesInternadosExamesRealizarJejumNpoController.setImpressao(impressao);
		relatorioPacientesInternadosExamesRealizarJejumNpoController.setColecaoJejum(relatorioPacientesInternadosExamesRealizarVO.getJejumNpo());
		relatorioPacientesInternadosExamesRealizarPreparoController.setImpressao(impressao);
		relatorioPacientesInternadosExamesRealizarPreparoController.setColecao(relatorioPacientesInternadosExamesRealizarVO.getPreparo());
		relatorioPacientesInternadosExamesRealizarJejumNpoController.setColecaoDieta(relatorioPacientesInternadosExamesRealizarVO.getDietaDiferenciada());
		relatorioPacientesInternadosExamesRealizarUnidadeEmergenciaController.setImpressao(impressao);
		relatorioPacientesInternadosExamesRealizarUnidadeEmergenciaController.setColecao(relatorioPacientesInternadosExamesRealizarVO.getUnidadeEmergencia());
		relatorioPacientesInternadosExamesRealizarTodosController.setImpressao(impressao);
		relatorioPacientesInternadosExamesRealizarTodosController.setColecao(relatorioPacientesInternadosExamesRealizarVO.getTodos());
	}

	/**
	 * Verifica se no minimo uma das "check box" de tipo de relatorio foi marcada
	 */
	private Boolean verificarMinimoUmaCheckBoxTipoRelatorioMarcada(){
		Boolean checkBoxValues[] = {jejumNpo,preparo,dietaDiferenciada,unidadeEmergencia,todos};
		for (Boolean valor : checkBoxValues) {
			if(valor != null & valor){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de unidade funcional
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorUnidEmergencia(param, true);
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public RelatorioPacientesInternadosExamesRealizarVO getRelatorioPacientesInternadosExamesRealizarVO() {
		return relatorioPacientesInternadosExamesRealizarVO;
	}

	public void setRelatorioPacientesInternadosExamesRealizarVO(
			RelatorioPacientesInternadosExamesRealizarVO relatorioPacientesInternadosExamesRealizarVO) {
		this.relatorioPacientesInternadosExamesRealizarVO = relatorioPacientesInternadosExamesRealizarVO;
	}

	public DominioSimNao getImprimeRecomendacoesExame() {
		return imprimeRecomendacoesExame;
	}

	public void setImprimeRecomendacoesExame(
			DominioSimNao imprimeRecomendacoesExame) {
		this.imprimeRecomendacoesExame = imprimeRecomendacoesExame;
	}

	public Boolean getJejumNpo() {
		return jejumNpo;
	}

	public void setJejumNpo(Boolean jejumNpo) {
		this.jejumNpo = jejumNpo;
	}

	public Boolean getPreparo() {
		return preparo;
	}

	public void setPreparo(Boolean preparo) {
		this.preparo = preparo;
	}

	public Boolean getDietaDiferenciada() {
		return dietaDiferenciada;
	}

	public void setDietaDiferenciada(Boolean dietaDiferenciada) {
		this.dietaDiferenciada = dietaDiferenciada;
	}

	public Boolean getUnidadeEmergencia() {
		return unidadeEmergencia;
	}

	public void setUnidadeEmergencia(Boolean unidadeEmergencia) {
		this.unidadeEmergencia = unidadeEmergencia;
	}

	public Boolean getTodos() {
		return todos;
	}

	public void setTodos(Boolean todos) {
		this.todos = todos;
	}

	public List<String> getListaRelatoriosImpressos() {
		return listaRelatoriosImpressos;
	}

	public void setListaRelatoriosImpressos(
			List<String> listaRelatoriosImpressos) {
		this.listaRelatoriosImpressos = listaRelatoriosImpressos;
	}

	public Boolean getImpressao() {
		return impressao;
	}

	public void setImpressao(Boolean impressao) {
		this.impressao = impressao;
	}
}