package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MbcEquipeVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.ListaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.ProcedimentoCirurgicoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class ListaPacientesCCIHPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5715851128971620888L;

	private static final String LISTA_PACIENTES = "controleinfeccao-listaPacientes";

	private static final String FATOR_PREDISPONENTE = "controleinfeccao-notificacaoFatorPredisponente";

	private static final String NOTIFICACAO_TOPOGRAFIA = "controleinfeccao-notificacaoTopografia";

	private static final String PROCEDIMENTO_RISCO = "controleinfeccao-notificacaoProcedimentoRisco";
	
	private static final String NOTIFICACAO_PREVENTIVA = "controleinfeccao-notificacaoMedidaPreventiva";
	
	private static final String PATOLOGIA_INFECCAO = "controleinfeccao-pesquisaPatologiasInfeccao";
	
	private static final String DETALHES_PACIENTE = "controleinfeccao-detalharPacienteCCIH";
	
	private static final String LISTA_GMR_PACIENTE = "controleinfeccao-listaGMRPaciente";
	
	private final static ConstanteAghCaractUnidFuncionais[] CARACTERISTICAS_UNIDADE_FUNCIONAL = {ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS};

	private static final String NOTAS_CCIH = "controleinfeccao-cadastroNotasAdicionaisCCIH";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	// Filtro
	private FiltroListaPacienteCCIHVO filtro = new FiltroListaPacienteCCIHVO();
	private Integer prontuario;
	private Integer consulta;
	
	private Boolean sliderAberta = Boolean.TRUE;
		
	private boolean sliderAbertoCirurgias = true;
	private boolean sliderAbertoInternacoes = true;
	private boolean sliderAbertoNotificacoes = true;
	
	private boolean exibeModoLeitura = false; 		//RN1
	private boolean periodoObrigatorio = true;		//RN3
	
	private String voltarPara = LISTA_PACIENTES;
	
	//procedimento selecionado
	private ProcedimentoCirurgicoVO procedimento;
	
	//doenca condicao
	private DoencaInfeccaoVO doencaCondicao;
	
	//doenca condicao
	private TopografiaProcedimentoVO topografiaProcedimento;
	
	private List<ListaPacientesCCIHVO> lista;
	
	private ListaPacientesCCIHVO selecionado;
	
	@Inject
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private IControleInfeccaoFacade controleInfeccaoFacade;

    @Inject @Paginator
	private DynamicDataModel<ListaPacientesCCIHVO> dataModel;
	
	public void desabilitarFiltros() {
		if (this.prontuario != null || this.consulta != null) {
			this.filtro = new FiltroListaPacienteCCIHVO();
			this.filtro.setProntuario(prontuario);
			this.filtro.setConsulta(consulta);
			this.procedimento = null;
			this.doencaCondicao = null;
			this.topografiaProcedimento = null;
			this.exibeModoLeitura = true;
			this.periodoObrigatorio = false;
			this.fecharSliders();

		} else {
			this.filtro = new FiltroListaPacienteCCIHVO();
		}
	}
	
	public void limpar() {
		this.dataModel.limparPesquisa();
		lista = null;
		this.filtro = new FiltroListaPacienteCCIHVO();
		this.prontuario = null;
		this.consulta = null;
		this.procedimento = null;
		this.doencaCondicao = null;
		this.topografiaProcedimento = null;
		this.exibeModoLeitura = false;
		this.periodoObrigatorio = true;
		this.selecionado = null;
		this.abrirSliders();
	}
	
	public void abreFechaSlider(String slider) {
		switch (slider) {
		case "sliderAbertoCirurgias":
			sliderAbertoCirurgias = !sliderAbertoCirurgias;  break;
		case "sliderAbertoInternacoes":
			sliderAbertoInternacoes = !sliderAbertoInternacoes;  break;
		case "sliderAbertoNotificacoes":
			sliderAbertoNotificacoes = !sliderAbertoNotificacoes;  break;
		}
		
	}
	
	
	private void abrirSliders() {
		this.sliderAbertoCirurgias = true;
		this.sliderAbertoInternacoes = true;
		this.sliderAbertoNotificacoes = true;
	}
	
	private void fecharSliders() {
		this.sliderAbertoCirurgias = false;
		this.sliderAbertoInternacoes = false;
		this.sliderAbertoNotificacoes = false;
	}
	
	public void pesquisar(){
		try {
			this.controleInfeccaoFacade.validarNotificacaoSelecionada(filtro);
			this.controleInfeccaoFacade.validarPeriodoPesquisaAtendimento(filtro);
			this.dataModel.reiniciarPaginator();
			this.selecionado = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.dataModel.limparPesquisa();
		}
	}
	
	public String obterProntuarioFormatado(Integer prontuario){
		return CoreUtil.formataProntuario(prontuario);
	}
	
	public String colorirTabela(Boolean portadorGMR) {
		if (Boolean.TRUE.equals(portadorGMR)) {
			return "background-color: #01FFFF";
		}
		return "";
	}
	
	@Override
	public Long recuperarCount() {
		try {
			return controleInfeccaoFacade.pesquisarPacientesCCIHCount(filtro);
		} catch (ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public List<ListaPacientesCCIHVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		try {
			return controleInfeccaoFacade.pesquisarPacientesCCIH(filtro, firstResult, maxResults, orderProperty, asc);
		} catch (ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
		this.selecionado = null;
	}
	
	/*
	 * MÉTODOS DAS SUGGESTIONS
	 */
	
	// UNIDADES FUNCIONAIS - CIRURGIAS
	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionais(String param) {
		
		// MÉTODO EDITADO
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica((String)param, CARACTERISTICAS_UNIDADE_FUNCIONAL, DominioSituacao.A, Boolean.FALSE), pesquisarAghUnidadesFuncionaisCount(param));

	
	}
	
	// UNIDADES FUNCIONAIS - INTERNAÇÕES (#47999)
	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisInternacoes(String param) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(param, true, false, new Object[] { ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA });
	}
	
	// Contador de Unidade Funcional - Cirurgia
	public Long pesquisarAghUnidadesFuncionaisCount(String param) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristicaCount(param, CARACTERISTICAS_UNIDADE_FUNCIONAL,  DominioSituacao.A, Boolean.FALSE);
	}
	
	// Contador de Unidade Funcional - Internação
	public int pesquisarAghUnidadesFuncionaisCountInternacoes(String param) {
		List<AghUnidadesFuncionais> temp = this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(param, true, false, new Object[] { ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO,ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA });
		return temp.size();
	}
	
	public void posRemoverUnidadeFuncionalInternacao() {
		filtro.setLeito(null);
	}
	
	// LEITOS
	public List<AinLeitos> pesquisarAinLeitos(String param) {
		return this.internacaoFacade.pesquisarLeitosPorUnidadeFuncional(param, filtro.getUnfInternacao() != null ? filtro.getUnfInternacao().getSeq() : null);
	}
	
	public Long pesquisarAinLeitosCount(Object param) {
		return this.internacaoFacade.pesquisarLeitosPorUnidadeFuncionalCount(param, filtro.getUnfInternacao() != null ? filtro.getUnfInternacao().getSeq() : null);
	}
	
	public List<MbcEquipeVO> listarProfissionaisAtuamUnidCirurgica(String filtro) {
		return blocoCirurgicoFacade.obterProfissionaisAtuamUnidCirurgica(filtro);
	}
	
	public Long listarProfissionaisAtuamUnidCirurgicaCount(Object filtro) {
		return blocoCirurgicoFacade.obterProfissionaisAtuamUnidCirurgicaCount(filtro);
	}
	
	public List<ProcedimentoCirurgicoVO> listarProcedimentosCirurgicos(String filtro) {
		return blocoCirurgicoFacade.listarProcCirurgicosPorGrupo(filtro);
	}
	
	public Long listarProcedimentosCirurgicosCount(Object filtro) {
		return blocoCirurgicoFacade.listarProcCirurgicosPorGrupoCount(filtro);
	}

	public void selecionaProcedimentoVO(){
		filtro.setCodigoProcedimento(procedimento.getCodigoProcedimento());
	}
	
	public void limpaProcedimento(){
		filtro.setCodigoProcedimento(null);
	}

	public List<DoencaInfeccaoVO> listarDoencasInfeccao(String param) {
		return controleInfeccaoFacade.buscarDoencaInfeccaoPaiChaveAtivos(param);
	}

	public Long listarDoencasInfeccaoCount(Object param) {
		return controleInfeccaoFacade.buscarDoencaInfeccaoPaiChaveAtivosCount(param);
	}
	
	public List<TopografiaProcedimentoVO> listarTopografiaProcedimentoAtivas(String param) {
		return controleInfeccaoFacade.listarTopografiaProcedimentoAtivas(param);
	}

	public Long listarTopografiaProcedimentoAtivasCount(Object param) {
		return controleInfeccaoFacade.listarTopografiaProcedimentoAtivasCount(param);
	}
	
	public void selecionaTopografia(){
		filtro.setCodigoTopografia(topografiaProcedimento.getSeq());
		filtro.setDoencaCondicao(false);
		filtro.setProcedimentoRisco(false);
		filtro.setFatoresPredisponente(false);
		filtro.setGmr(false);
		filtro.setTopografiaInfeccao(true);
		this.doencaCondicao = null;
	}
	
	public void limpaTopografia(){
		filtro.setCodigoTopografia(null);
		filtro.setTopografiaInfeccao(false);
	}
	
	public void selecionaCodigoDoencaCondicao(){
		filtro.setCodigoDoencaCondicao(doencaCondicao.getSeqPai());
		filtro.setTopografiaInfeccao(false);
		filtro.setProcedimentoRisco(false);
		filtro.setFatoresPredisponente(false);
		filtro.setGmr(false);
		filtro.setDoencaCondicao(true);
		this.topografiaProcedimento = null;
	}
	
	public void limpaCodigoDoencaCondicao(){
		filtro.setCodigoDoencaCondicao(null);
		filtro.setDoencaCondicao(false);
		doencaCondicao = null;
	}
	
	public void selecionaFatorProcedimentoGMR() {
		doencaCondicao = null;
		topografiaProcedimento = null;
	}
	
	public void verificaConferidoCCIH(){
		if (DominioConfirmacaoCCI.P.equals(filtro.getConferido())){
			filtro.setProcedimentoRisco(false);
		}
		filtro.setFatoresPredisponente(false);
	}
	
	public void selecionaProcedimentoRisco(){
		if (filtro.isProcedimentoRisco() && DominioConfirmacaoCCI.P.equals(filtro.getConferido())) {
			filtro.setConferido(null);
		}
		
		selecionaFatorProcedimentoGMR();
	}
	
	public void marcaDoencaCondicao(){
		if (filtro.isDoencaCondicao()) {
			topografiaProcedimento = null;
			this.filtro.setTopografiaInfeccao(false);
		} else {
			this.doencaCondicao = null;
		}
	}
	
	public void marcaTopografia(){
		if (filtro.isTopografiaInfeccao()) {
			doencaCondicao = null;
			this.filtro.setDoencaCondicao(false);
		} else {
			this.topografiaProcedimento = null;
		}
	}
	
	public String fatorPredisponente() {
		return FATOR_PREDISPONENTE;
	}
	
	public String notificarGMR() {
		return LISTA_GMR_PACIENTE;
	}
	
	public String notificacarTopografia() {
		return NOTIFICACAO_TOPOGRAFIA;
	}
	
	public String procedimentoRisco() {
		return PROCEDIMENTO_RISCO;
	}
	
	public String notificacarPreventiva() {
		return NOTIFICACAO_PREVENTIVA;				
	}
	
	public String detalhesPaciente() {
		return DETALHES_PACIENTE;
	}
	
	public String patologiasInfeccao() {
		return PATOLOGIA_INFECCAO;
	}
		
	public String notasCCIH() {
		return NOTAS_CCIH;
	}
	
	public FiltroListaPacienteCCIHVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroListaPacienteCCIHVO filtro) {
		this.filtro = filtro;
	}

	public boolean isSliderAbertoCirurgias() {
		return sliderAbertoCirurgias;
	}

	public void setSliderAbertoCirurgias(boolean sliderAbertoCirurgias) {
		this.sliderAbertoCirurgias = sliderAbertoCirurgias;
	}

	public boolean isSliderAbertoInternacoes() {
		return sliderAbertoInternacoes;
	}

	public void setSliderAbertoInternacoes(boolean sliderAbertoInternacoes) {
		this.sliderAbertoInternacoes = sliderAbertoInternacoes;
	}

	public boolean isSliderAbertoNotificacoes() {
		return sliderAbertoNotificacoes;
	}

	public void setSliderAbertoNotificacoes(boolean sliderAbertoNotificacoes) {
		this.sliderAbertoNotificacoes = sliderAbertoNotificacoes;
	}
	
	public ProcedimentoCirurgicoVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedimentoCirurgicoVO procedimento) {
		this.procedimento = procedimento;
	}

	public DoencaInfeccaoVO getDoencaCondicao() {
		return doencaCondicao;
	}

	public void setDoencaCondicao(DoencaInfeccaoVO doencaCondicao) {
		this.doencaCondicao = doencaCondicao;
	}

	public TopografiaProcedimentoVO getTopografiaProcedimento() {
		return topografiaProcedimento;
	}

	public void setTopografiaProcedimento(TopografiaProcedimentoVO topografiaProcedimento) {
		this.topografiaProcedimento = topografiaProcedimento;
	}

	public boolean isExibeModoLeitura() {
		return exibeModoLeitura;
	}

	public void setExibeModoLeitura(boolean exibeModoLeitura) {
		this.exibeModoLeitura = exibeModoLeitura;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public boolean isPeriodoObrigatorio() {
		return periodoObrigatorio;
	}

	public List<ListaPacientesCCIHVO> getLista() {
		return lista;
	}

	public void setLista(List<ListaPacientesCCIHVO> lista) {
		this.lista = lista;
	}

	public DynamicDataModel<ListaPacientesCCIHVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ListaPacientesCCIHVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ListaPacientesCCIHVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ListaPacientesCCIHVO selecionado) {
		this.selecionado = selecionado;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getSliderAberta() {
		return sliderAberta;
	}

	public void setSliderAberta(Boolean sliderAberta) {
		this.sliderAberta = sliderAberta;
	}
}
