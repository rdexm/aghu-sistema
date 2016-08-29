package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.FiltroGradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.ambulatorio.vo.VRapPessoaServidorVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class PesquisaConsultaGradePaginatorController extends ActionController implements ActionPaginator{
	
	private static final long serialVersionUID = 3643914104469810998L;
	
	private static final String TRANSFERIR_GRADE = "transferirConsultasGrade";
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<GradeConsultasVO> dataModel;
	
	private FiltroGradeConsultasVO filtro = new FiltroGradeConsultasVO();
	
	/**
	 * recebe instancia da grade
	 */
	private GradeVO grade = new GradeVO();
	
	/**
	 * recebe instancia da consulta por grada
	 */
	private GradeConsultasVO gradeConsultasVO = new GradeConsultasVO();
	
	/**
	 * recebe instancia do sb setor/sala
	 */
	private VAacSiglaUnfSalaVO setorSala;
	
	/**
	 * recebe instancia do sb profissional
	 */
	private VRapPessoaServidorVO profissional;
	
	/**
	 * recebe instancia do sb equipe
	 */
	private AghEquipes equipe;
	
	/**
	 * recebe instancia do sb especialidade
	 */
	private AghEspecialidadeVO especialidade;
	
	private List<GradeVO> listaGrade = new ArrayList<GradeVO>();
	
	private List<AacSituacaoConsultas> listSituacoes = new ArrayList<AacSituacaoConsultas>();
	
	private List<GradeConsultasVO> gradeConsultaVOSelecionadas = new ArrayList<GradeConsultasVO>();
	
	private List<GradeConsultasVO> listaConsultas = new ArrayList<GradeConsultasVO>();
	
	private Map<Integer,String> listaDiaDaSemana = new HashMap<Integer,String>();
	
	private Boolean exibirGrade = Boolean.FALSE;
	private Boolean exibirGradeConsulta = Boolean.FALSE;
	private Boolean allCheck = Boolean.FALSE;
	private Boolean habilitaQuantidade = Boolean.TRUE;
	private String orderPropertyAux;
	private boolean ascAux = true;
	private Boolean executouCheckAllOrCheckItem = Boolean.FALSE;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar(){
		filtroPesquisaGrade();
		obterAacGradeAgendamentoConsultas();
	}
	
	public void iniciar(){
		obterSituacoesAtivas();
		preencherListaDiaDaSemana();
	}
	
	public void preencherListaDiaDaSemana(){
		listaDiaDaSemana.put(1,"Domingo");
		listaDiaDaSemana.put(2,"Segunda-Feira");
		listaDiaDaSemana.put(3,"Terça-Feira");
		listaDiaDaSemana.put(4,"Quarta-Feira");
		listaDiaDaSemana.put(5,"Quinta-Feira");
		listaDiaDaSemana.put(6,"Sexta-Feira");
		listaDiaDaSemana.put(7,"Sábado");
	}
	
	public String limpar(){
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		
		filtro = new FiltroGradeConsultasVO();
		listaGrade = new ArrayList<GradeVO>();
		grade = new GradeVO();
		exibirGrade = Boolean.FALSE;
		exibirGradeConsulta = Boolean.FALSE;
		habilitaQuantidade = Boolean.TRUE;
		allCheck = Boolean.FALSE;
		executouCheckAllOrCheckItem = Boolean.FALSE;
		equipe = null;
		especialidade = null;
		profissional = null;
		setorSala = null;
		dataModel.limparPesquisa();
		return null;
	}
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	
	public void obterAacGradeAgendamentoConsultas(){
		setListaGrade(ambulatorioFacade.obterAacGradeAgendamentoConsultas(filtro));
		checarGrade();
		//#6807
		//sempre que consulta C6 trouxer resultado, selecionar o primeiro registro e efetuar a consulta C7
		if(!getListaGrade().isEmpty()){
			this.grade = getListaGrade().get(0);
			limparListaSelecao();
			if(grade.getSeq() != null){
				obterListaConsultaPorGrade(this.grade);
			}
		}
		else{
			this.grade = new GradeVO();
			limparListaSelecao();
			this.dataModel.setPesquisaAtiva(Boolean.TRUE);
			exibirGradeConsulta = Boolean.TRUE;
		}
	}
	
	public void obterListaConsultaPorGrade(GradeVO grade){
		if(grade != null){
			this.grade = grade;
			dataModel.limparPesquisa();
			this.dataModel.reiniciarPaginator();
			exibirGradeConsulta = Boolean.TRUE;
		}
	}
	//#6807
	//Caso a consulta de grade traga apenas um resultado, esta grid não deverá ser exibida
	private void checarGrade(){
		if(getListaGrade().size() == 1 || filtro.getGrade()!= null){
			exibirGrade = Boolean.FALSE;
		}
		else{
			exibirGrade = Boolean.TRUE;
		}
	}
	//#6807
	//O campo 'Quantidade' deve iniciar desabilitado e somente ser habilitado enquanto o campo 'Horario' tiver preenchido
	public void habilitarQuantidade(){
		if(filtro.getHorario() != null){
			habilitaQuantidade = Boolean.FALSE;
		}
		else{
			habilitaQuantidade = Boolean.TRUE;
		}
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}	
	
	public void obterSituacoesAtivas() {
		setListSituacoes(ambulatorioFacade.obterSituacoesAtivas());
	}
	
	public String trocarGrade(){
		return TRANSFERIR_GRADE;
	}
		
	public List<VAacSiglaUnfSalaVO> obterListaSetorSala(String parametro) throws ApplicationBusinessException {
		return returnSGWithCount(ambulatorioFacade.pesquisarListaSetorSala(parametro.trim()), obterListaSetorSalaCount(parametro));
	}
	public Long obterListaSetorSalaCount(String parametro) throws ApplicationBusinessException {
		return ambulatorioFacade.pesquisarListaSetorSalaCount(parametro.trim());
	}
	
	public List<AghEspecialidadeVO> obterEspecialidade(String parametro) throws ApplicationBusinessException {
		return returnSGWithCount(aghuFacade.pesquisarEspecialidadesPorSiglaNomeCodigo(parametro.trim()), obterEspecialidadeCount(parametro));
	}
	
	public Long obterEspecialidadeCount(String parametro) throws ApplicationBusinessException {
		return aghuFacade.pesquisarEspecialidadesPorSiglaNomeCodigoCount(parametro.trim());
	}
	
	public List<AghEquipes> obterEquipes(String parametro) {
		return returnSGWithCount(aghuFacade.pesquisarEquipes(parametro.trim()),obterEquipeCount(parametro));
	}
	
	public Long obterEquipeCount(String parametro) {
		return aghuFacade.pesquisarEquipesCount(parametro.trim());
	}
	
	public List<VRapPessoaServidorVO> obterProfissional(String parametro) throws ApplicationBusinessException {
		return returnSGWithCount(registroColaboradorFacade.pesquisarPessoasServidores(parametro.trim()),obterProfissionalCount(parametro));
	}
	
	public Long obterProfissionalCount(String parametro) throws ApplicationBusinessException {
		return registroColaboradorFacade.pesquisarPessoasServidoresCount(parametro.trim());
	}
	
	
	public boolean verificarPermissao() throws ApplicationBusinessException{
		return pacienteFacade.verificarAcaoQualificacaoMatricula("MANTER GRADES CONSULTAS");
	}
	
	@Override
	public Long recuperarCount() {
		return  filtro.getQuantidade() == null ? ambulatorioFacade.pesquisarConsultasPorGradeCount(grade.getSeq(),filtro) : filtro.getQuantidade();
	}
	
	@Override
	public List<GradeConsultasVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		if (this.getListaGrade() != null && this.getListaGrade().isEmpty()) {
			this.dataModel.setCount(0L);
			listaConsultas = new ArrayList<GradeConsultasVO>();
		} else {
			listaConsultas = ambulatorioFacade.pesquisarConsultasPorGrade(grade.getSeq(), filtro, firstResult, maxResult, orderProperty,asc);
			alterarSelecaoNaListaVO();
		}
		
		orderPropertyAux = orderProperty;
		ascAux = asc;
		
		if(!executouCheckAllOrCheckItem){
			checkAll();
		}
		
		executouCheckAllOrCheckItem = Boolean.FALSE;
		
		return listaConsultas;
	}
	// Sempre que efetuar uma nova pesquisa a lista de Consultas selecionadas deverá ser limpa e a variável AllCheck setada como FALSE.
	public void limparListaSelecao(){
		allCheck = Boolean.FALSE;
		executouCheckAllOrCheckItem = Boolean.FALSE;
		gradeConsultaVOSelecionadas = new ArrayList<GradeConsultasVO>();
	}
	
	public void filtroPesquisaGrade(){
		dataModel.limparPesquisa();
		limparFiltroSB();
		if(equipe != null){
			if(equipe.getSeq() != null){
				filtro.setEqpSeq(equipe.getSeq());
			}
		}
		if(especialidade != null){
			if(especialidade.getSeq() != null){
				filtro.setEspSeq(especialidade.getSeq());
			}
		}
		if(setorSala != null){
			if(setorSala.getUnfSeq() != null){
				filtro.setSetorSalaSeq(setorSala.getUnfSeq());
			}
		}
		if(profissional != null){
			if(profissional.getMatricula() != null){
				filtro.setMatricula(profissional.getMatricula());
			}
			if(profissional.getVinculo() != null){
				filtro.setVinculo(profissional.getVinculo());
			}
		}
	}
	private void limparFiltroSB(){
		filtro.setMatricula(null);
		filtro.setVinculo(null);
		filtro.setSetorSalaSeq(null);
		filtro.setEspSeq(null);
		filtro.setEqpSeq(null);
	}
	
	//MARCA DESMARCA TODOS
	public void checkAll(){
		
		gradeConsultaVOSelecionadas = new ArrayList<GradeConsultasVO>();
		
		if (allCheck) {
			
			int firstResultAux = 0;
			int  maxResultAux = filtro.getQuantidade() == null ? 
					ambulatorioFacade.pesquisarConsultasPorGradeCount(grade.getSeq(), filtro).intValue() : filtro.getQuantidade().intValue();

			for(GradeConsultasVO vo: ambulatorioFacade.pesquisarConsultasPorGrade(grade.getSeq(), filtro, firstResultAux, maxResultAux, orderPropertyAux, ascAux)) {
				vo.setSelecionado(Boolean.TRUE);
				this.gradeConsultaVOSelecionadas.add(vo);
			}
			
		} 
		
		executouCheckAllOrCheckItem = Boolean.TRUE;
	}	
	
	//MARCA DESMARCA 1
	public void checkItem(GradeConsultasVO item){
		if(gradeConsultaVOSelecionadas.contains(item)){
			gradeConsultaVOSelecionadas.remove(item);
			allCheck = Boolean.FALSE;
		}else{
			gradeConsultaVOSelecionadas.add(item);
		}
		
		executouCheckAllOrCheckItem = Boolean.TRUE;
		
		alterarSelecaoNaListaVO();
	}
	
	//ALTERA A SELECAO NA LISTA
	private void alterarSelecaoNaListaVO(){
		for(GradeConsultasVO vo: listaConsultas){
			if(gradeConsultaVOSelecionadas.contains(vo)){
				vo.setSelecionado(true);
			}else{
				vo.setSelecionado(false);
			}
		}
	}
	
	public String getDescricaoCompletaSalaSetor() {
		if (setorSala != null){
			return setorSala.getSigla().concat(StringUtils.LF + setorSala.getSala());
		}
		return "";
	}
	public String getDescricaoCompletaEspecialidade() {
		if (especialidade != null){
			return especialidade.getEspSigla().concat(StringUtils.LF + especialidade.getEspNomeEspecialidade());
		}
		return "";
	}
	public String getDescricaoCompletaProfissional() {
		if (profissional != null){
			return String.valueOf(profissional.getMatricula()).concat(StringUtils.LF + profissional.getNome());
		}
		return "";
	}
	
	public FiltroGradeConsultasVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroGradeConsultasVO filtro) {
		this.filtro = filtro;
	}

	public GradeVO getGrade() {
		return grade;
	}

	public void setGrade(GradeVO grade) {
		this.grade = grade;
	}

	public AghEspecialidadeVO getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidadeVO especialidade) {
		this.especialidade = especialidade;
	}

	public VAacSiglaUnfSalaVO getSetorSala() {
		return setorSala;
	}

	public void setSetorSala(VAacSiglaUnfSalaVO setorSala) {
		this.setorSala = setorSala;
	}

	public VRapPessoaServidorVO getProfissional() {
		return profissional;
	}

	public void setProfissional(VRapPessoaServidorVO profissional) {
		this.profissional = profissional;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public List<GradeVO> getListaGrade() {
		return listaGrade;
	}

	public void setListaGrade(List<GradeVO> listaGrade) {
		this.listaGrade = listaGrade;
	}

	public Boolean getExibirGrade() {
		return exibirGrade;
	}

	public void setExibirGrade(Boolean exibirGrade) {
		this.exibirGrade = exibirGrade;
	}

	public DynamicDataModel<GradeConsultasVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<GradeConsultasVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getExibirGradeConsulta() {
		return exibirGradeConsulta;
	}

	public void setExibirGradeConsulta(Boolean exibirGradeConsulta) {
		this.exibirGradeConsulta = exibirGradeConsulta;
	}

	public GradeConsultasVO getGradeConsultasVO() {
		return gradeConsultasVO;
	}

	public void setGradeConsultasVO(GradeConsultasVO gradeConsultasVO) {
		this.gradeConsultasVO = gradeConsultasVO;
	}

	public List<AacSituacaoConsultas> getListSituacoes() {
		return listSituacoes;
	}

	public void setListSituacoes(List<AacSituacaoConsultas> listSituacoes) {
		this.listSituacoes = listSituacoes;
	}

	public List<GradeConsultasVO> getGradeConsultaVOSelecionadas() {
		return gradeConsultaVOSelecionadas;
	}

	public void setGradeConsultaVOSelecionadas(List<GradeConsultasVO> gradeConsultaVOSelecionadas) {
		this.gradeConsultaVOSelecionadas = gradeConsultaVOSelecionadas;
	}

	public Boolean getAllCheck() {
		return allCheck;
	}

	public void setAllCheck(Boolean allCheck) {
		this.allCheck = allCheck;
	}

	public Boolean getHabilitaQuantidade() {
		return habilitaQuantidade;
	}

	public void setHabilitaQuantidade(Boolean habilitaQuantidade) {
		this.habilitaQuantidade = habilitaQuantidade;
	}

	public Map<Integer,String> getListaDiaDaSemana() {
		return listaDiaDaSemana;
	}

	public void setListaDiaDaSemana(Map<Integer,String> listaDiaDaSemana) {
		this.listaDiaDaSemana = listaDiaDaSemana;
	}

	public String getOrderPropertyAux() {
		return orderPropertyAux;
	}

	public void setOrderPropertyAux(String orderPropertyAux) {
		this.orderPropertyAux = orderPropertyAux;
	}

	public boolean isAscAux() {
		return ascAux;
	}

	public void setAscAux(boolean ascAux) {
		this.ascAux = ascAux;
	}

	public Boolean getExecutouCheckAllOrCheckItem() {
		return executouCheckAllOrCheckItem;
	}

	public void setExecutouCheckAllOrCheckItem(
			Boolean executouCheckAllOrCheckItem) {
		this.executouCheckAllOrCheckItem = executouCheckAllOrCheckItem;
	}

}
