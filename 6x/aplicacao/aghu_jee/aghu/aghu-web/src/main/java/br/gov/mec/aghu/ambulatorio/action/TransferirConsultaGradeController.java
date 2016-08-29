package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.FiltroGradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.ambulatorio.vo.VRapPessoaServidorVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class TransferirConsultaGradeController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4994413134117852933L;

	private static final String PESQUISAR_CONSULTAS_POR_GRADE = "pesquisarConsultasPorGrade";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private FiltroGradeConsultasVO filtro = new FiltroGradeConsultasVO();
	
	@Inject
	private PesquisaConsultaGradePaginatorController pesquisaConsultaController;
	
	/**
	 * recebe instancia da grade
	 */
	private GradeVO grade = new GradeVO();
	
	/**
	 * É necessario exibir no fildset o numero da grade das consultas selecionadas
	 */
	private Integer seqGradeSelecionada;
	/**
	 * recebe instancia da consulta por grade
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
	
	private List<GradeConsultasVO> listaConsultasSelecionadas = new ArrayList<GradeConsultasVO>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar(){
		filtroPesquisaGrade();
		obterAacGradeAgendamentoConsultas();
	}
	
	public void iniciar(){
		if(getListaConsultasSelecionadas() != null){
			getListaConsultasSelecionadas();
		}
//		pesquisar();
	}
	
	public String limpar(){
		filtro = new FiltroGradeConsultasVO();
		listaGrade = new ArrayList<GradeVO>();
		grade = new GradeVO();
		equipe = null;
		especialidade = null;
		profissional = null;
		setorSala = null;
		listaConsultasSelecionadas = null;
		return null;
	}
	
	public void obterAacGradeAgendamentoConsultas(){
		setListaGrade(ambulatorioFacade.obterAacGradeAgendamentoConsultas(filtro));
	}
	
	public String cancelar(){
		limpar();
		return PESQUISAR_CONSULTAS_POR_GRADE;
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
	
	public String trocarGrade() throws ApplicationBusinessException{
		try{
			ambulatorioFacade.trocarConsultaGrade(seqGradeSelecionada, grade.getSeq(), listaConsultasSelecionadas);
			limpar();
			pesquisaConsultaController.setAllCheck(Boolean.FALSE);
			pesquisaConsultaController.setGradeConsultaVOSelecionadas(new ArrayList<GradeConsultasVO>());
			apresentarMsgNegocio("TRANSFERIR_CONSULTA_GRADE_ATUALIZADO_SUCESSO");
			return PESQUISAR_CONSULTAS_POR_GRADE;
		}
		catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
		return null;
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
	
	public void filtroPesquisaGrade(){
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

	public GradeConsultasVO getGradeConsultasVO() {
		return gradeConsultasVO;
	}

	public void setGradeConsultasVO(GradeConsultasVO gradeConsultasVO) {
		this.gradeConsultasVO = gradeConsultasVO;
	}

	public Integer getSeqGradeSelecionada() {
		return seqGradeSelecionada;
	}

	public void setSeqGradeSelecionada(Integer seqGradeSelecionada) {
		this.seqGradeSelecionada = seqGradeSelecionada;
	}

	public List<GradeConsultasVO> getListaConsultasSelecionadas() {
		return listaConsultasSelecionadas;
	}

	public void setListaConsultasSelecionadas(
			List<GradeConsultasVO> listaConsultasSelecionadas) {
		this.listaConsultasSelecionadas = listaConsultasSelecionadas;
	}

	public PesquisaConsultaGradePaginatorController getPesquisaConsultaController() {
		return pesquisaConsultaController;
	}

	public void setPesquisaConsultaController(
			PesquisaConsultaGradePaginatorController pesquisaConsultaController) {
		this.pesquisaConsultaController = pesquisaConsultaController;
	}

}