package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class TopografiaPorProcedimentoPesquisaController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -518952133860286863L;
	private static final String INCLUSAO_EDICAO = "topografiaPorProcedimentoCadastro";
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private Short codigoTopografiaProcedimento;
	private String descricao;
	private DominioSituacao situacao;
	private TopografiaInfeccaoVO topografiaInfeccaoVO;
	
	@Inject @Paginator
	private DynamicDataModel<TopografiaProcedimentoVO> dataModel;
	private TopografiaProcedimentoVO itemSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		itemSelecionado = new TopografiaProcedimentoVO();
		definirPermissoes();
	
	}
	
	
	private void definirPermissoes() {
		dataModel.setUserRemovePermission(permissionService
				.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterTopografiaProcedimento", "excluir"));
		
		dataModel.setUserRemovePermission(permissionService
				.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterTopografiaProcedimento", "editar"));
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		dataModel.limparPesquisa();
		descricao =  null;
		situacao = null;
		itemSelecionado = null;
		topografiaInfeccaoVO = null;
		codigoTopografiaProcedimento = null;
	}
	
	public String  incluir(){
		return INCLUSAO_EDICAO;
	}
	
	public String editar(){
		return INCLUSAO_EDICAO;
	}
	
	public void  excluir(){
		try {
			controleInfeccaoFacade.excluirTopografiaProcedimentoVO(itemSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MSG_TOPO_PROC_SUCESSO_EXCLUSAO", itemSelecionado.getDescricao());
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return controleInfeccaoFacade.listarMciTopografiaProcedimentoPorSeqDescSitSeqTopCount(codigoTopografiaProcedimento, descricao, situacao, obterToiSeq());
	}

	@Override
	public List<TopografiaProcedimentoVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return controleInfeccaoFacade
				.listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(
						firstResult, maxResult, orderProperty, asc,
						codigoTopografiaProcedimento, descricao, situacao, obterToiSeq());
	}
	
	public List<TopografiaInfeccaoVO> suggestionBoxTopografiaInfeccaoPorSituacao(String strPesquisa) {
		return this.controleInfeccaoFacade.suggestionBoxTopografiaInfeccaoPorSeqOuDescricao((String) strPesquisa);
	}
	
	private Short obterToiSeq(){
		return topografiaInfeccaoVO != null ? topografiaInfeccaoVO.getSeq() : null;
	}

	public Short getCodigoTopografiaProcedimento() {
		return codigoTopografiaProcedimento;
	}

	public void setCodigoTopografiaProcedimento(Short codigoTopografiaProcedimento) {
		this.codigoTopografiaProcedimento = codigoTopografiaProcedimento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public TopografiaInfeccaoVO getTopografiaInfeccaoVO() {
		return topografiaInfeccaoVO;
	}

	public void setTopografiaInfeccaoVO(TopografiaInfeccaoVO topografiaInfeccaoVO) {
		this.topografiaInfeccaoVO = topografiaInfeccaoVO;
	}

	public DynamicDataModel<TopografiaProcedimentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<TopografiaProcedimentoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public TopografiaProcedimentoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(TopografiaProcedimentoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	
}
