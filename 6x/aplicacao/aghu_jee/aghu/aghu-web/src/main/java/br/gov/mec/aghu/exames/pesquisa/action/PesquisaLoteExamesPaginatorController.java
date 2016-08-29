package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaLoteExamesFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class PesquisaLoteExamesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3298926042870146446L;

	private static final String MANTER_LOTE_EXAMES_CRUD = "exames-manterLoteExamesCrud";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	private PesquisaLoteExamesFiltroVO filtro = new PesquisaLoteExamesFiltroVO();
	
	private DominioOrigemAtendimento[] origensPermitidas = {DominioOrigemAtendimento.A, DominioOrigemAtendimento.I,
															DominioOrigemAtendimento.U, DominioOrigemAtendimento.X,
															DominioOrigemAtendimento.D,	DominioOrigemAtendimento.H};


	@Inject @Paginator
	private DynamicDataModel<AelLoteExameUsual> dataModel;
	
	private AelLoteExameUsual selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		filtro = new PesquisaLoteExamesFiltroVO();
		dataModel.limparPesquisa();
	}
	
	@Override
	public List<AelLoteExameUsual> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisaLotesPorParametros(firstResult, maxResult, AelLoteExameUsual.Fields.SEQ.toString(), true, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisaLotesPorParametrosCount(this.filtro);
	}
	
	public void excluir(){
		try {
			this.solicitacaoExameFacade.removerAelLoteExameUsual(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_LOTE_EXAME_USUAL");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_LOTE_EXAMES_CRUD;
	}
	
	public String editar(){
		return MANTER_LOTE_EXAMES_CRUD;
	}

	// Metódo para Suggestion Box de Grupos
	public List<AelGrupoExameUsual> pesquisarGrupos(String objPesquisa){
		return this.examesFacade.obterGrupoPorCodigoDescricao(objPesquisa);
	}
	
	/*Metódo para Suggestion Box de Unidade Funcional*/
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String param) {
		String paramString = (String) param;
		Set<AghUnidadesFuncionais> result = new HashSet<AghUnidadesFuncionais>();
		
		try {
			result = new HashSet<AghUnidadesFuncionais>(prescricaoMedicaFacade.getListaUnidadesFuncionais(paramString));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(result);
		Collections.sort(resultReturn, new Comparator<AghUnidadesFuncionais>() {
									@Override
									public int compare(AghUnidadesFuncionais u1, AghUnidadesFuncionais u2) {
										int result = u1.getLPADAndarAlaDescricao().compareToIgnoreCase(
												u2.getLPADAndarAlaDescricao());
										return result;
									}
								}  );
		return resultReturn;
		//return this.aghuFacade.pesquisarUnidadesPorCodigoDescricao(param, true);
	}
	
	private static class UnidadeFuncionalComparator implements	Comparator<AghUnidadesFuncionais> {
		@Override
		public int compare(AghUnidadesFuncionais u1, AghUnidadesFuncionais u2) {
			int result = u1.getLPADAndarAlaDescricao().compareToIgnoreCase(
					u2.getLPADAndarAlaDescricao());
			return result;
		}
	}

	// Metódo para Suggestion Box de Agrupamentos de pesquisa
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa){
		return this.aghuFacade.pesquisarEspecialidadePorNomeOuSigla((String)objPesquisa);
	}
	
	// Metódo para Suggestion Box de Exames
	public List<VAelExamesSolicitacao> pesquisarExames(Object objPesquisa){
		return this.pesquisaExamesFacade.obterNomeExames(objPesquisa);
	}

	public PesquisaLoteExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaLoteExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public DominioOrigemAtendimento[] getOrigensPermitidas() {
		return origensPermitidas;
	}

	public void setOrigensPermitidas(
			DominioOrigemAtendimento[] origensPermitidas) {
		this.origensPermitidas = origensPermitidas;
	}

	public DynamicDataModel<AelLoteExameUsual> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelLoteExameUsual> dataModel) {
		this.dataModel = dataModel;
	}

	public AelLoteExameUsual getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelLoteExameUsual selecionado) {
		this.selecionado = selecionado;
	}
}