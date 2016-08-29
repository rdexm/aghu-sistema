package br.gov.mec.aghu.exames.action;

import java.io.IOException;
import java.text.Collator;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioViewMonitorPendenciasExames;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesFiltrosPesquisaVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesVO;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;

public class MonitorPendenciasExamesController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -5289790883951729275L;

	// Injeções
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private RelatorioMonitorPendenciasExamesController relatorioMonitorPendenciasExamesController;

	@EJB
	protected IParametroFacade parametroFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Filtros de pesquisa
	private AelUnidExecUsuario unidadeExecutoraUsuario;
	private MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa;

	// Valor da aba atual/selecionada
	private Integer currentTabIndex;
	
	private Integer total = 0;

	// Flags que controlam as abas selecionadas
	private boolean renderAbaAreaExecutora;
	private boolean renderAbaExecutando;
	private boolean renderAbaColetado;
	private boolean renderAbaEmColeta;

	// Flag de pesquisa ativa para o refresh/atualização automática
	private Boolean pesquisaAtiva;

	// Lista dos resultados da aba selecionada
	List<MonitorPendenciasExamesVO> listaResultadosAbaSelecionada;

	private boolean carregouPagina;

	private static final String PAGE_DETALHAR_ITEM_SOLICITACAO_EXAME = "exames-detalharItemSolicitacaoExame";
	
	/**
	 * Chamado no inicio da conversação
	 */
	public void iniciar() {
	 


		if (!this.carregouPagina) {
			this.pesquisaAtiva = false;

			if (this.filtrosPesquisa == null) {
				this.filtrosPesquisa = new MonitorPendenciasExamesFiltrosPesquisaVO();
				this.currentTabIndex = DominioViewMonitorPendenciasExames.AREA_EXECUTORA.getCodigo();
			}

			this.listaResultadosAbaSelecionada = new LinkedList<MonitorPendenciasExamesVO>();

			// Resgata a unidade executora de exames através do usuario logado
			if (this.filtrosPesquisa.getUnidadeFuncionalExames() == null) {
				this.obterUnidadeExecutoraExames();
			}

			// Renderiza abas
			this.renderAbas();

			this.carregouPagina = true;
		}
		total = listaResultadosAbaSelecionada.size();
	
	}
	
	public String redirecionaDetalharItemSolicitacaoExame() {
		return PAGE_DETALHAR_ITEM_SOLICITACAO_EXAME;
	}

	/**
	 * Obtem a unidade executora do exame através do usuário logado
	 */
	public void obterUnidadeExecutoraExames() {
		// Resgata a UNIDADE EXECUTORA DE EXAMES do ÚSUARIO
		try {
			this.unidadeExecutoraUsuario = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			unidadeExecutoraUsuario = null;
		}
		// Resgata a UNIDADE FUNCIONAL da unidade executora
		if(unidadeExecutoraUsuario != null) {
			this.filtrosPesquisa.setUnidadeFuncionalExames(this.unidadeExecutoraUsuario.getUnfSeq());
		}
	}

	/**
	 * 
	 */
	public void atualizarCamposPesquisaUnidadeExecutoraExames() {
		if (this.filtrosPesquisa != null && this.filtrosPesquisa.getQuantidadeMaximaRegistros() == null) {
			this.filtrosPesquisa.setQuantidadeMaximaRegistros(0);
		}
	}

	/**
	 * Pesquisa automática
	 */
	public void pesquisarAutomaticamente() {

		// // A pesquisa não deve estar ativa
		// if (this.pesquisaAtiva) {
		// return;
		// }

		// this.getStatusMessages().clear();
		// this.getStatusMessages().clearGlobalMessages();
		// A pesquisa não deve estar ativa sendo obrigatório o preenchimento da unidade executora
		if (isPesquisaMonitorExameAtiva()) {
			// Pesquisa de acordo com a lista selecionada
			this.listaResultadosAbaSelecionada = new LinkedList<MonitorPendenciasExamesVO>();
			return;
		}
		this.pesquisar();
	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.unidadeExecutoraUsuario, this.filtrosPesquisa.getUnidadeFuncionalExames());
			this.pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public boolean isPesquisaMonitorExameAtiva() {
		return this.pesquisaAtiva && this.filtrosPesquisa.getUnidadeFuncionalExames() == null;
	}

	/**
	 * Pesquisa conforme a aba selecionada Também executado ao: - Selecionar uma nova unidade executora de exames - Botão carregar - REFRESH/Atualização automática do monitor
	 */
	public void pesquisar() {

		// // A pesquisa não deve estar ativa
		// if (this.pesquisaAtiva) {
		// return;
		// }

		// Pesquisa de acordo com a lista selecionada
		this.listaResultadosAbaSelecionada = new LinkedList<MonitorPendenciasExamesVO>();

		// this.getStatusMessages().clear();
		// this.getStatusMessages().clearGlobalMessages();
		// A pesquisa não deve estar ativa sendo obrigatório o preenchimento da unidade executora
		if (isPesquisaMonitorExameAtiva()) {
			return;
		}

		// Seta o domínio da view/aba/tabela selecionada para pesquisa de pendências
		DominioViewMonitorPendenciasExames viewMonitorPendenciasExames = DominioViewMonitorPendenciasExames.fromValue(this.currentTabIndex);
		this.filtrosPesquisa.setViewMonitorPendenciasExames(viewMonitorPendenciasExames);

		try {

			this.pesquisaAtiva = true;

			/*
			 * Chama antecipadamente a validação dos filtros da pesquisa da ON Questão de performance, pois seriam necessárias duas chamadas devido a consulta páginada. Vide:
			 * pesquisa count e pesquisa.
			 */
			this.examesFacade.verificarFiltrosPesquisa(this.filtrosPesquisa);

			// Pesquisa de acordo com a lista selecionada
			this.listaResultadosAbaSelecionada = this.examesFacade.pesquisarMonitorPendenciasExames(this.filtrosPesquisa);

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			pesquisaAtiva = false;
		}

	}

	private String currentSortProperty;
	private Comparator<MonitorPendenciasExamesVO> currentComparator;

	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator PT_BR_COMPARATOR = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
			return ((Comparable) o1).compareTo(o2);
		}
	};

	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {

		Comparator comparator = null;

		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null && this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(PT_BR_COMPARATOR, false));
		}

		Collections.sort(this.listaResultadosAbaSelecionada, comparator);

		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;

	}

	/**
	 * Obtém a data de hoje para as datas de referência
	 */
	public void obterHojeDataReferencia() {

		// Data atual
		Date dataAtual = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);

		// Transforma as horas da para 00:00:00
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		// Somente data às 00:00:00
		Date dataSemHoras = calendar.getTime();

		this.filtrosPesquisa.setDataReferenciaIni(dataSemHoras);
		this.filtrosPesquisa.setDataReferenciaFim(dataAtual);
	}

	/**
	 * Obtém a data de hoje
	 */
	public void obterHoje() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.filtrosPesquisa.setDataDia(cal.getTime());
	}

	/*
	 * Metodos para Suggestion Box
	 */

	/**
	 * Metodo para pesquisa na suggestion box da unidade executora de exames
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutoraExames(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	public void limparUnidadeExecutoraExames() {
		this.listaResultadosAbaSelecionada = new LinkedList<MonitorPendenciasExamesVO>();
		final DominioViewMonitorPendenciasExames viewMonitorPendenciasExames = this.filtrosPesquisa.getViewMonitorPendenciasExames();
		this.filtrosPesquisa = new MonitorPendenciasExamesFiltrosPesquisaVO();
		this.filtrosPesquisa.setViewMonitorPendenciasExames(viewMonitorPendenciasExames);
		this.pesquisaAtiva = false;
	}

	/*
	 * Métodos das Abas
	 */

	/**
	 * Inicializa a as flags das abas
	 */
	private void iniciarFlagAbas() {
		this.renderAbaAreaExecutora = false;
		this.renderAbaExecutando = false;
		this.renderAbaColetado = false;
		this.renderAbaEmColeta = false;
	}

	/**
	 * Identifica a tab selecionada e executa o metodo de render desta tab.<br>
	 * Utiliza a variavel <code>currentTabIndex</code>.
	 */
	public void renderAbas() {

		DominioViewMonitorPendenciasExames abaSelecionada = DominioViewMonitorPendenciasExames.fromValue(this.currentTabIndex);

		this.iniciarFlagAbas();

		switch (abaSelecionada) {
		case AREA_EXECUTORA:
			this.renderAbaAreaExecutora = true;
			this.pesquisar();
			break;
		case EXECUTANDO:
			this.renderAbaExecutando = true;
			this.pesquisar();
			break;
		case COLETADO:
			this.renderAbaColetado = true;
			this.pesquisar();
			break;
		case EM_COLETA:
			this.renderAbaEmColeta = true;
			this.pesquisar();
			break;
		}

	}

	/*
	 * Métodos impressão
	 */

	/**
	 * Visualização de relatório
	 * 
	 * @return
	 * @throws BaseException
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	public String print() throws BaseException, JRException, IOException, DocumentException {
		this.relatorioMonitorPendenciasExamesController.setFiltrosPesquisa(this.filtrosPesquisa);
		return this.relatorioMonitorPendenciasExamesController.print();
	}

	/**
	 * Impressão direta de relatório
	 * 
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() throws BaseException, JRException, SystemException, IOException {
		this.relatorioMonitorPendenciasExamesController.setFiltrosPesquisa(this.filtrosPesquisa);
		this.relatorioMonitorPendenciasExamesController.directPrint();
	}

	/*
	 * Getters and Setters
	 */

	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public AelUnidExecUsuario getUnidadeExecutoraUsuario() {
		return unidadeExecutoraUsuario;
	}

	public void setUnidadeExecutoraUsuario(AelUnidExecUsuario unidadeExecutoraUsuario) {
		this.unidadeExecutoraUsuario = unidadeExecutoraUsuario;
	}

	public MonitorPendenciasExamesFiltrosPesquisaVO getFiltrosPesquisa() {
		return filtrosPesquisa;
	}

	public void setFiltrosPesquisa(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) {
		this.filtrosPesquisa = filtrosPesquisa;
	}

	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}

	public boolean isRenderAbaAreaExecutora() {
		return renderAbaAreaExecutora;
	}

	public void setRenderAbaAreaExecutora(boolean renderAbaAreaExecutora) {
		this.renderAbaAreaExecutora = renderAbaAreaExecutora;
	}

	public boolean isRenderAbaExecutando() {
		return renderAbaExecutando;
	}

	public void setRenderAbaExecutando(boolean renderAbaExecutando) {
		this.renderAbaExecutando = renderAbaExecutando;
	}

	public boolean isRenderAbaColetado() {
		return renderAbaColetado;
	}

	public void setRenderAbaColetado(boolean renderAbaColetado) {
		this.renderAbaColetado = renderAbaColetado;
	}

	public boolean isRenderAbaEmColeta() {
		return renderAbaEmColeta;
	}

	public void setRenderAbaEmColeta(boolean renderAbaEmColeta) {
		this.renderAbaEmColeta = renderAbaEmColeta;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public List<MonitorPendenciasExamesVO> getListaResultadosAbaSelecionada() {
		return listaResultadosAbaSelecionada;
	}

	public void setListaResultadosAbaSelecionada(List<MonitorPendenciasExamesVO> listaResultadosAbaSelecionada) {
		this.listaResultadosAbaSelecionada = listaResultadosAbaSelecionada;
	}

	public boolean isCarregouPagina() {
		return carregouPagina;
	}
	
	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

}