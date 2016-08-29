package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.ScoServicoCriteriaVO;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSiasgServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Controller responsável pela listagem de serviços.
 * 
 * @author mlcruz
 */

public class ServicoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4696532475030194013L;
	private static final Log LOG = LogFactory.getLog(ServicoPaginatorController.class);
	private static final String SERVICO_CRUD = "estoque-servicoCRUD";
	
	@Inject @Paginator
	private DynamicDataModel<ScoServico> dataModel;
	private ScoServicoCriteriaVO criteria;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	private ScoServico servico;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando conversation");
		this.begin(conversation);
	}
	
	/**
	 * Inicia.
	 */
	public void iniciar() {
	 

		if(criteria == null){
			limpar();
		}
	
	}
	
	/**
	 * Limpa critérios de busca e resultado da consulta.
	 */
	public void limpar() {
		criteria = new ScoServicoCriteriaVO();
		criteria.setSituacao(DominioSituacao.A);
		setAtivo(false);
	}
	
	/**
	 * Pesquisa grupos.
	 */
	public void pesquisar() {
		criteria.setNome(StringUtils.trimToNull(criteria.getNome()));
		reiniciarPaginator();
	}
	
	
	public String redirecionarPaginaServicoCRUD(){
		return SERVICO_CRUD;
	}	

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	
	/** {@inheritDoc} */
	@Override
	public Long recuperarCount() {
		return comprasFacade.contarServicos(criteria);
	}

	/** {@inheritDoc} */
	@Override
	public List<ScoServico> recuperarListaPaginada(Integer first, Integer max,
			String order, boolean asc) {
		return comprasFacade.pesquisarScoServico(criteria, first, max, order, asc);
	}
	
	/**
	 * Obtem grupos de serviço conforme filtro.
	 * 
	 * @param filter Filtro (ID ou parte da descrição do grupo).
	 * @return Grupos de serviço encontrados.
	 */
	public List<ScoGrupoServico> pesquisarGrupos(String filter) {
		return comprasFacade.listarGrupoServico(filter);
	}
	
	public String getDescricaoContrato(ScoServico servico) {
		if (servico.getIndContrato() != null) {
			return DominioSimNao.getInstance(servico.getIndContrato())
					.getDescricao();
		} else {
			return null;
		}
	}
	
	/**
	 * Obtem grupos de natureza de despesa.
	 * 
	 * @param filter
	 *            Filtro (código ou descrição do grupo).
	 * @return Grupos.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(
			String filter) {
		return cadastrosBasicosOrcamentoFacade
				.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(filter);
	}
		
	
	/**
	 * Obtem naturezas de despesa pelo grupo já selecionado.
	 * 
	 * @param filter
	 *            Código ou descrição da natureza.
	 * @return Naturezas de despesa.
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarNaturezasDespesa(
				criteria.getGrupoNatureza(), filter);
	}
	
	public List<ScoSiasgServico> pesquisarCatSer(String objCatSer){
		return cadastrosBasicosOrcamentoFacade.pesquisarCatSer(objCatSer);
	}

	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public ScoServicoCriteriaVO getCriteria() {
		return criteria;
	}

	public DynamicDataModel<ScoServico> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoServico> dataModel) {
	 this.dataModel = dataModel;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
}