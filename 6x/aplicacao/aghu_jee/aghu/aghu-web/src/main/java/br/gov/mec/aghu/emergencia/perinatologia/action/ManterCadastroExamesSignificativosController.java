package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ManterCadastroExamesSignificativosController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -756417967447211564L;

	private static final Integer VALOR_MAXIMO_EXIBE_HINT_UNIDADE_FUNCIONAL = 42;
	private static final Integer VALOR_MAXIMO_EXIBE_HINT_EXAME = 32;
	
	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private boolean manterExamesSignificativos;
	private boolean consultarExamesSignificativos;
	@Inject @Paginator
	private DynamicDataModel<ExameSignificativoVO> dataModel;
	private ExameSignificativoVO exameSignificativo;
	private Boolean pesquisaAtiva;
	private UnidadeFuncionalVO filtroUnidadeFuncional;
	private ExameMaterialAnaliseVO filtroExameMaterialAnalise;
	private DominioSimNao filtroCargaExame;
	private UnidadeFuncionalVO novoUnidadeFuncional;
	private ExameMaterialAnaliseVO novoExameMaterialAnalise;
	private Boolean novoCargaExame;

	/**
	 * Ação do botão de Pequisa da pagina de exames significativos
	 */
	public void pesquisar() {
		setPesquisaAtiva(Boolean.TRUE);
		this.getDataModel().reiniciarPaginator();
	}

	/**
	 * Ação do botão limpar da pagina de exames significativos
	 */
	public void limparPesquisa() {
		setPesquisaAtiva(Boolean.FALSE);
		setFiltroCargaExame(null);
		setFiltroExameMaterialAnalise(null);
		setFiltroUnidadeFuncional(null);
		limparCamposNovoExameSignificativo();
		this.getDataModel().limparPesquisa();
	}

	private void limparCamposNovoExameSignificativo() {
		setNovoCargaExame(null);
		setNovoExameMaterialAnalise(null);
		setNovoUnidadeFuncional(null);
	}

	/**
	 * Ação do botão de pesquisa por unidade funcional da pagina de exames
	 * significativos
	 * 
	 * @param descricao
	 * @return List<UnidadeFuncionalVO>
	 */
	public List<UnidadeFuncionalVO> pesquisarUnidadeFuncional(String descricao) {
		List<UnidadeFuncionalVO> listaUnidadeFuncional = new ArrayList<UnidadeFuncionalVO>();

		try {
			listaUnidadeFuncional = this.emergenciaFacade.pesquisarUnidadeFuncionalVO((String) descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return  this.returnSGWithCount(listaUnidadeFuncional,pesquisarUnidadeFuncionalCount(descricao));
	}

	public Long pesquisarUnidadeFuncionalCount(String descricao) {
		Long retorno = 0L;

		try {
			retorno = this.emergenciaFacade.pesquisarUnidadeFuncionalCount((String) descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	/**
	 * Ação do botão de pesquisa por Exames material da pagina de exames
	 * significativos
	 * 
	 * @param descricao
	 * @return List<ExameMaterialAnaliseVO>
	 */
	public List<ExameMaterialAnaliseVO> pesquisarExameMaterialAnalise(String descricao) {
		List<ExameMaterialAnaliseVO> listaExameMaterialAnalise = new ArrayList<ExameMaterialAnaliseVO>();

		try {
			listaExameMaterialAnalise = this.emergenciaFacade.pesquisarExameMaterial((String) descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return  this.returnSGWithCount(listaExameMaterialAnalise,pesquisarExameMaterialAnaliseCount(descricao));
	}

	public Long pesquisarExameMaterialAnaliseCount(String descricao) {
		Long retorno = 0L;
		try {
			retorno = this.emergenciaFacade.pesquisarExameMaterialCount((String) descricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	/**
	 * Ação do botão de adicionar novo exame significativo
	 */
	public void adicionarExameSignificativo() {
		Short unfSeq = novoUnidadeFuncional.getSeq();
		String siglaExame = novoExameMaterialAnalise.getSigla();
		Integer seqMatAnls = novoExameMaterialAnalise.getMamSeq();
		Boolean cargaExame = novoCargaExame != null ? novoCargaExame : Boolean.FALSE;

		try {
			this.emergenciaFacade.persitirExameSignificativo(unfSeq, siglaExame, seqMatAnls, cargaExame);
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_CADASTRO_EXAME_SIGNIFICATIVO");
			this.limparCamposNovoExameSignificativo();
			this.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação di botão de ativar/inativar exame significativo
	 */
	public void ativarInativar() {
		Short unfSeq = exameSignificativo.getUnfSeq();
		String siglaExame = exameSignificativo.getSiglaExame();
		Integer seqMatAnls = exameSignificativo.getEmaManSeq();
		Boolean cargaExame = exameSignificativo.getIndCargaExame() == Boolean.TRUE ? Boolean.FALSE : Boolean.TRUE;

		try {
			this.emergenciaFacade.persitirExameSignificativo(unfSeq, siglaExame, seqMatAnls, cargaExame);
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_ALTERACAO_CARGA_EXAME");
			this.limparCamposNovoExameSignificativo();
			this.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão excluir exame significativo
	 */
	public void excluir() {
		Short unfSeq = exameSignificativo.getUnfSeq();
		String siglaExame = exameSignificativo.getSiglaExame();
		Integer seqMatAnls = exameSignificativo.getEmaManSeq();

		try {
			this.emergenciaFacade.excluirExameSignificativo(unfSeq, siglaExame, seqMatAnls);
			apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUSAO_EXAME_SIGNIFICATIVO");
			this.limparCamposNovoExameSignificativo();
			this.getDataModel().reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean exibeHintUnidadeFuncional(String descricao){
		if(VALOR_MAXIMO_EXIBE_HINT_UNIDADE_FUNCIONAL <= descricao.length()){
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}
	
	public Boolean exibeHintExame(String descricao){
		if(VALOR_MAXIMO_EXIBE_HINT_EXAME <= descricao.length() ){
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}
	
	public String formataDescricaoUnidadeFuncional(String descricao){
		if(VALOR_MAXIMO_EXIBE_HINT_UNIDADE_FUNCIONAL <= descricao.length() ){
			return formataDescricao(descricao, VALOR_MAXIMO_EXIBE_HINT_UNIDADE_FUNCIONAL);
		}
		else {
			return descricao;
		}
	}
	
	public String formataDescricaoExame(String descricao){
		if(VALOR_MAXIMO_EXIBE_HINT_EXAME <= descricao.length() ){
			return formataDescricao(descricao, VALOR_MAXIMO_EXIBE_HINT_EXAME);
		}
		else {
			return descricao;
		}
	}

	private String formataDescricao(String descricao, Integer valor){
		return descricao.substring(0, (valor - 3)).concat("...");
	}
	
	@PostConstruct
	public void init() {
		begin(conversation, true);

		this.manterExamesSignificativos = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterExamesSignificativos",
				"executar");

		this.consultarExamesSignificativos = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "consultarExamesSignificativos",
				"visualizar");
	}

	@Override
	public List<ExameSignificativoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<ExameSignificativoVO> listaExameSignificativoVO = new ArrayList<ExameSignificativoVO>();

		Short unfSeq = null;
		String siglaExame = null;
		Integer seqMatAnls = null;
		Boolean cargaExame = null;
		
		if(filtroUnidadeFuncional != null ){
			unfSeq =  filtroUnidadeFuncional.getSeq();
		}
		if(filtroExameMaterialAnalise != null){
			siglaExame = filtroExameMaterialAnalise.getSigla();
			seqMatAnls = filtroExameMaterialAnalise.getMamSeq();
		}
		if(filtroCargaExame != null){
			cargaExame = filtroCargaExame.isSim();
		}
		
		try {
			listaExameSignificativoVO = this.emergenciaFacade.pesquisarExamesSignificativos(unfSeq, siglaExame, seqMatAnls, cargaExame, firstResult, maxResult);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaExameSignificativoVO;
	}

	
	
	@Override
	public Long recuperarCount() {
		Long retorno = 0L;

		Short unfSeq = null;
		String siglaExame = null;
		Integer seqMatAnls = null;
		Boolean cargaExame = null;
		
		if(filtroUnidadeFuncional != null ){
			unfSeq =  filtroUnidadeFuncional.getSeq();
		}
		if(filtroExameMaterialAnalise != null){
			siglaExame = filtroExameMaterialAnalise.getSigla();
			seqMatAnls = filtroExameMaterialAnalise.getMamSeq();
		}
		if(filtroCargaExame != null){
			cargaExame = filtroCargaExame.isSim();
		}
		
		try {
			retorno = this.emergenciaFacade.pesquisarExamesSignificativosCount(unfSeq, siglaExame, seqMatAnls, cargaExame);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public Boolean getConvertDominioSimNaoToBoolean(DominioSimNao situacao) {
		if (situacao.isSim()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isManterExamesSignificativos() {
		return manterExamesSignificativos;
	}

	public void setManterExamesSignificativos(boolean manterExamesSignificativos) {
		this.manterExamesSignificativos = manterExamesSignificativos;
	}

	public DynamicDataModel<ExameSignificativoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ExameSignificativoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public UnidadeFuncionalVO getFiltroUnidadeFuncional() {
		return filtroUnidadeFuncional;
	}

	public void setFiltroUnidadeFuncional(UnidadeFuncionalVO filtroUnidadeFuncional) {
		this.filtroUnidadeFuncional = filtroUnidadeFuncional;
	}

	public ExameMaterialAnaliseVO getFiltroExameMaterialAnalise() {
		return filtroExameMaterialAnalise;
	}

	public void setFiltroExameMaterialAnalise(ExameMaterialAnaliseVO filtroExameMaterialAnalise) {
		this.filtroExameMaterialAnalise = filtroExameMaterialAnalise;
	}

	public DominioSimNao getFiltroCargaExame() {
		return filtroCargaExame;
	}

	public void setFiltroCargaExame(DominioSimNao filtroCargaExame) {
		this.filtroCargaExame = filtroCargaExame;
	}

	public UnidadeFuncionalVO getNovoUnidadeFuncional() {
		return novoUnidadeFuncional;
	}

	public void setNovoUnidadeFuncional(UnidadeFuncionalVO novoUnidadeFuncional) {
		this.novoUnidadeFuncional = novoUnidadeFuncional;
	}

	public ExameMaterialAnaliseVO getNovoExameMaterialAnalise() {
		return novoExameMaterialAnalise;
	}

	public void setNovoExameMaterialAnalise(ExameMaterialAnaliseVO novoExameMaterialAnalise) {
		this.novoExameMaterialAnalise = novoExameMaterialAnalise;
	}

	public Boolean getNovoCargaExame() {
		return novoCargaExame;
	}

	public void setNovoCargaExame(Boolean novoCargaExame) {
		this.novoCargaExame = novoCargaExame;
	}

	public boolean isConsultarExamesSignificativos() {
		return consultarExamesSignificativos;
	}

	public void setConsultarExamesSignificativos(boolean consultarExamesSignificativos) {
		this.consultarExamesSignificativos = consultarExamesSignificativos;
	}

	public ExameSignificativoVO getExameSignificativo() {
		return exameSignificativo;
	}

	public void setExameSignificativo(ExameSignificativoVO exameSignificativo) {
		this.exameSignificativo = exameSignificativo;
	}

}
