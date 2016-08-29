package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.IPlanejamentoFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class InclusaoManualReposicaoAutomaticaController extends ActionController {

	private static final String SOLICITACAO_COMPRA_CRUD = "solicitacaoCompraCRUD";

	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";

	private static final long serialVersionUID = 6311943154533715902L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private IPlanejamentoFacade planejamentoFacade;
	
	@Inject
	private ReposicaoMaterialPaginatorController reposicaoMaterialPaginatorController;
	
	private List<ItemReposicaoMaterialVO> listaInclusaoPontual;
	
	private ScoMaterial material;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private Boolean possuiInclusaoPendente;
	private String voltarParaUrl;
	private Integer seqLoteReposicao;
	private ScoLoteReposicao loteReposicao;
	private Boolean novaInclusao;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		if (novaInclusao) {
			this.listaInclusaoPontual = new ArrayList<ItemReposicaoMaterialVO>();
			this.possuiInclusaoPendente = Boolean.FALSE;
			if (this.seqLoteReposicao != null) {
				loteReposicao = this.planejamentoFacade.obterLoteReposicaoPorSeq(seqLoteReposicao);
			}
			this.novaInclusao = false;
		}
	}
	
	
	public String confirmarInclusaoPontual() {
		return this.voltar(false);
	}
	
	public String voltar(Boolean limparLista) {
		if (limparLista) {
			this.listaInclusaoPontual = null;
		}
		reposicaoMaterialPaginatorController.setListaInclusaoPontual(listaInclusaoPontual);
		return this.voltarParaUrl;
	}
	
	public String redirecionarEstatisticaConsumo(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}
	
	public String redirecionarSolicitacaoCompraCRUD(){
		return SOLICITACAO_COMPRA_CRUD;
	}
	
	public void excluirItem(ItemReposicaoMaterialVO item) {
		Integer index = this.listaInclusaoPontual.indexOf(item);
		
		if (index >= 0) {
			this.listaInclusaoPontual.remove(item);
		}
	}
	
	public Boolean verificarMaterialExisteLista(ScoMaterial mat) {
		Boolean existeLote = this.planejamentoFacade.verificarMaterialExistente(getLoteReposicao(), mat);
		ItemReposicaoMaterialVO vo = new ItemReposicaoMaterialVO();
		vo.setMatCodigo(mat.getCodigo());
		return this.listaInclusaoPontual.contains(vo) || existeLote;
	}
	
	public void adicionarCesta() {
		if (this.material == null && this.solicitacaoCompra == null) {
			this.apresentarMsgNegocio(Severity.FATAL, "MENSAGEM_CAMPOS_OBRIGATORIOS_INCLUSAO_PONTUAL");
		} else {
			ScoMaterial mat = this.material;
			if (this.solicitacaoCompra != null) {
				mat = this.solicitacaoCompra.getMaterial();
			}
			if (mat != null) {
				if (!verificarMaterialExisteLista(mat)) {
					ItemReposicaoMaterialVO vo = this.planejamentoFacade.montarItemReposicaoVO(mat, this.loteReposicao, 
							this.solicitacaoCompra, null);
					
					this.listaInclusaoPontual.add(vo);
					this.possuiInclusaoPendente = Boolean.TRUE;
				} else {
					this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_MATERIAL_EXISTE_LISTA_REPOSICAO");
				}
			}
			this.solicitacaoCompra = null;
			this.material = null;
		} 
	}

	public List<ScoMaterial> listarMateriais(String param) throws BaseException {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(param, null, true),listarMateriaisCount(param));
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.solicitacaoComprasFacade.listarMateriaisAtivosCount(param, this.obterLoginUsuarioLogado());
	}
	
	public List<ScoSolicitacaoDeCompra> pesquisarSolicCompraCodigoDescricao(String filter) {
		return this.solicitacaoComprasFacade.pesquisarSolicitacaoCompraPorNumeroOuDescricao(filter, false);
	}

	public String getSplited(final String descricao, final Integer tam) {
		return this.planejamentoFacade.getSplited(descricao, tam);
	}

	public String getListaScs(ItemReposicaoMaterialVO item) {
		return this.planejamentoFacade.getListaScs(item);
	}

	public String getScRelacionada(ItemReposicaoMaterialVO item) {
		return this.planejamentoFacade.getScRelacionada(item);
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public Boolean getPossuiInclusaoPendente() {
		return possuiInclusaoPendente;
	}

	public void setPossuiInclusaoPendente(Boolean possuiInclusaoPendente) {
		this.possuiInclusaoPendente = possuiInclusaoPendente;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Integer getSeqLoteReposicao() {
		return seqLoteReposicao;
	}

	public void setSeqLoteReposicao(Integer seqLoteReposicao) {
		this.seqLoteReposicao = seqLoteReposicao;
	}

	public List<ItemReposicaoMaterialVO> getListaInclusaoPontual() {
		return listaInclusaoPontual;
	}

	public void setListaInclusaoPontual(
			List<ItemReposicaoMaterialVO> listaInclusaoPontual) {
		this.listaInclusaoPontual = listaInclusaoPontual;
	}

	public ScoLoteReposicao getLoteReposicao() {
		return loteReposicao;
	}

	public void setLoteReposicao(ScoLoteReposicao loteReposicao) {
		this.loteReposicao = loteReposicao;
	}

	public Boolean getNovaInclusao() {
		return novaInclusao;
	}

	public void setNovaInclusao(Boolean novaInclusao) {
		this.novaInclusao = novaInclusao;
	}
}