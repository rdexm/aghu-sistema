package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.MovimentacaoFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaMovimentacaoFornecedorPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8052100492377994451L;

	private static final String PAGE_AUTORIZACAO_FORNECIMENTO = "compras-autorizacaoFornecimentoCRUD";


	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IContasPagarFacade contasPagarFacade;

	@EJB
	private IPermissionService permissionService;

	@Inject @Paginator
	private DynamicDataModel<MovimentacaoFornecedorVO> dataModel;

	// campos pesquisa
	private DominioSituacaoTitulo situacao = null;
	private ScoFornecedor fornecedor = null;
	private Integer nr = null;
	private Long nf = null;
	private String serie = null;
	private Integer nroAf = null;
	private Short complemento = null;
	
	private Boolean permissaoConsultarTitulo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		
		permissaoConsultarTitulo = permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "consultarPosicaoTitulo", "pesquisar");
	}

	public void pesquisar() {
		if (complemento != null && nroAf == null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PESQUISAR_COMPLEMENTO_SEM_AF");
			this.dataModel.limparPesquisa();
		} else {
			this.dataModel.reiniciarPaginator();
		}
	}

	public void limpar() {
		this.situacao = null;
		this.fornecedor = null;
		this.nr = null;
		this.nf = null;
		this.serie = null;
		this.nroAf = null;
		this.complemento = null;
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		try {
			return comprasFacade.pesquisarMovimentacaoFornecedoresCount(fornecedor.getNumero(), situacao, nr, serie, nroAf, complemento, nf);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public List<MovimentacaoFornecedorVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return comprasFacade.pesquisarMovimentacaoFornecedores(fornecedor.getNumero(), situacao, nr, serie, nroAf, complemento, nf, firstResult, maxResult, orderProperty, asc);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String redirecionarAutorizacaoFornecimento() {
		return PAGE_AUTORIZACAO_FORNECIMENTO;
	}


	/**
	 * Obtém a posição do título
	 * 
	 * @param numero
	 * @return
	 */
	public PosicaoTituloVO obterPosicaoTitulo(final Integer numero) {
		return this.contasPagarFacade.obterPosicaoTituloVOPorNumero(numero);
	}

	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(param, 0, 100, null, false),pesquisarFornecedoresCount(param));
	}

	public Long pesquisarFornecedoresCount(String param) {
		return this.comprasFacade.listarFornecedoresAtivosCount(param);
	}

	public DynamicDataModel<MovimentacaoFornecedorVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MovimentacaoFornecedorVO> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public DominioSituacaoTitulo getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoTitulo situacao) {
		this.situacao = situacao;
	}

	public Integer getNr() {
		return nr;
	}

	public void setNr(Integer nr) {
		this.nr = nr;
	}

	public Long getNf() {
		return nf;
	}

	public void setNf(Long nf) {
		this.nf = nf;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Integer getNroAf() {
		return nroAf;
	}

	public void setNroAf(Integer nroAf) {
		this.nroAf = nroAf;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Boolean getPermissaoConsultarTitulo() {
		return permissaoConsultarTitulo;
	}

	public void setPermissaoConsultarTitulo(Boolean permissaoConsultarTitulo) {
		this.permissaoConsultarTitulo = permissaoConsultarTitulo;
	}

}
