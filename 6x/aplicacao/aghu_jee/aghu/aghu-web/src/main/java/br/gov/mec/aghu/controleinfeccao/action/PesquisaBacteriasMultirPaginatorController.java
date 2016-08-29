package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controleinfeccao.MciBacteriasMultirVO;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaBacteriasMultirPaginatorController extends ActionController implements ActionPaginator {

	private static final String PAGE_CADASTRO_PATOLOGIAS_INFECCAO = "cadastroBacteriasMultir";
	private static final long serialVersionUID = -5159107032113993392L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	// formulario
	private Integer seq;
	
	private String descricao;
	
	private DominioSituacao situacao;

	// lista
	@Inject @Paginator
	private DynamicDataModel<MciBacteriasMultirVO> dataModel;
	
	private MciBacteriasMultirVO selecionado = new MciBacteriasMultirVO();
	
	private List<MciBacteriaMultir> lista = new ArrayList<MciBacteriaMultir>();
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterBacteriaGMR", "manter");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {	
		this.seq = null;
		this.descricao = null;
		this.situacao = null;
		this.dataModel.setPesquisaAtiva(false);
		dataModel.limparPesquisa();
		lista = new ArrayList<MciBacteriaMultir>();
	}
	
	public String novo(){
		return PAGE_CADASTRO_PATOLOGIAS_INFECCAO;
	}
	
	public String editar(){
		return PAGE_CADASTRO_PATOLOGIAS_INFECCAO;
	}
	
	public void excluir()  {
		try {
			controleInfeccaoFacade.removerBacteriaMultir(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_BACTERIAS_MULTIR",selecionado.getDescricao());
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	@Override
	public Long recuperarCount() {
		Long count = controleInfeccaoFacade.obterBacteriasMultirPorSeqDescricaoSituacaoCount(this.seq, this.descricao, this.situacao);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public  List<MciBacteriasMultirVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		lista = controleInfeccaoFacade.obterBacteriaMultirPorSeqDescricaoSituacao(this.seq, this.descricao, this.situacao, firstResult, maxResult, orderProperty, asc);
		return populaVO(lista);
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	public MciBacteriasMultirVO getSelecionado() {
		return selecionado;
	}


	public void setSelecionado(MciBacteriasMultirVO selecionado) {
		this.selecionado = selecionado;
	}


	public DynamicDataModel<MciBacteriasMultirVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<MciBacteriasMultirVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<MciBacteriasMultirVO> populaVO(List<MciBacteriaMultir> lista){
		List<MciBacteriasMultirVO> listaVO = new ArrayList<MciBacteriasMultirVO>();
		for (MciBacteriaMultir linha : lista) {
			MciBacteriasMultirVO item = new MciBacteriasMultirVO();
			item.setSeq(linha.getSeq());
			item.setDescricao(linha.getDescricao());
			item.setCriadoEm(linha.getCriadoEm());
			item.setSituacao(linha.getSituacao());	
			if(linha.getAlteradoEm() != null){
				item.setCriadoAlterado(linha.getAlteradoEm());	
			}else if(linha.getCriadoEm() != null){
				item.setCriadoAlterado(linha.getCriadoEm());	
			}else{
				item.setCriadoAlterado(null);
			}
			listaVO.add(item);
		}
		return listaVO;
	}

	public List<MciBacteriaMultir> getLista() {
		return lista;
	}

	public void setLista(List<MciBacteriaMultir> lista) {
		this.lista = lista;
	}

}
