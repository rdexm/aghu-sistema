package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.jfree.util.Log;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedorMarca;
import br.gov.mec.aghu.model.ScoFornecedorMarcaId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCadastroMarcasFornecedorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	
	private static final long serialVersionUID = 2886547572554808231L;

	private static final String PAGE_COMPRAS_PESQUISAR_MARCA_COMERCIAL = "compras-pesquisarMarcaComercial";

	@EJB
	private IComprasFacade comprasFacade;

	@Inject @Paginator
	private DynamicDataModel<ScoFornecedor> dataModel;

	private ScoFornecedorMarca parametroSelecionado;

	private ScoFornecedorMarca scoFornecedorMarca;
	private ScoFornecedorMarcaId id;


	private ScoMarcaComercial scoMarcaComercial;
	private String descricaoMarca;
	private ScoFornecedor scoFornecedor;

	private Integer codigoFornecedor;
	private Integer numeroPac;
	private String voltarParaUrl;

	// @Out(required=false)
	private ScoFornecedorMarca fornecedorMarcaInserida;

	public void iniciar() {
	 
		this.scoFornecedorMarca = new ScoFornecedorMarca();		
		this.scoFornecedorMarca.setId(new ScoFornecedorMarcaId());
		setId(new ScoFornecedorMarcaId());
		
		if (this.codigoFornecedor != null) {
			this.scoFornecedor = this.comprasFacade.obterFornecedorPorChavePrimaria(this.codigoFornecedor);
			this.pesquisar();
		} 
	}
	

	public String cancelar() {
		this.limpar();
		return this.voltarParaUrl;
	}

	// suggestions
	public List<ScoMarcaComercial> pesquisarMarcas(String param) {
		return  this.comprasFacade.obterMarcas(param);
	}

	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return  this.comprasFacade.listarFornecedoresAtivos(param, 0, 100, null, false);
	}

	public void gravar() {
		this.scoFornecedorMarca.getId().setFrnNumero(this.scoFornecedor.getNumero());
		this.scoFornecedorMarca.getId().setMcmCodigo(this.scoMarcaComercial.getCodigo());

		try {

			this.comprasFacade.persistirFornecedorMarca(this.scoFornecedorMarca);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_MARCA_FORNECEDOR", this.scoMarcaComercial.getDescricao());

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}

		try {
			this.fornecedorMarcaInserida = (ScoFornecedorMarca) BeanUtils.cloneBean(this.scoFornecedorMarca);
		} catch (Exception e) {
			Log.error(e);
		}

		this.scoMarcaComercial = null;
		this.getDataModel().reiniciarPaginator();
	}

	public void limpar() {
		this.scoFornecedorMarca = new ScoFornecedorMarca();
		this.scoFornecedorMarca.setId(new ScoFornecedorMarcaId());
		this.parametroSelecionado = null;
		this.scoFornecedor = null;
		this.scoMarcaComercial = null;
		this.descricaoMarca = null;
	}

	public void excluir() {
		try {
			if (parametroSelecionado != null) {
				ScoFornecedorMarca fornecedorMarcaExcluir = this.comprasFacade.obterScoFornecedorMarcaPorId(this.parametroSelecionado.getId());
				if (fornecedorMarcaExcluir != null) {
					this.comprasFacade.excluirScoFornecedorMarca(fornecedorMarcaExcluir);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_MARCA_FORNECEDOR", fornecedorMarcaExcluir.getScoMarcaComercial().getDescricao());
				}
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
	}

	public String marcaComercial() {
		return PAGE_COMPRAS_PESQUISAR_MARCA_COMERCIAL;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.comprasFacade.listaFornecedorMarca(firstResult, maxResult, orderProperty, asc, this.scoFornecedor, descricaoMarca);
	}

	@Override
	public Long recuperarCount() {
		return this.comprasFacade.listaFornecedorMarcaCount(this.scoFornecedor);
	}

	// Getters and Setters

	public ScoFornecedorMarca getScoFornecedorMarca() {
		return scoFornecedorMarca;
	}

	public void setScoFornecedorMarca(ScoFornecedorMarca scoFornecedorMarca) {
		this.scoFornecedorMarca = scoFornecedorMarca;
	}

	public ScoMarcaComercial getScoMarcaComercial() {
		return scoMarcaComercial;
	}

	public void setScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.scoMarcaComercial = scoMarcaComercial;
	}

	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	public String getDescricaoMarca() {
		return descricaoMarca;
	}

	public void setDescricaoMarca(String descricaoMarca) {
		this.descricaoMarca = descricaoMarca;
	}

	public Integer getCodigoFornecedor() {
		return codigoFornecedor;
	}

	public void setCodigoFornecedor(Integer codigoFornecedor) {
		this.codigoFornecedor = codigoFornecedor;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public ScoFornecedorMarca getFornecedorMarcaInserida() {
		return fornecedorMarcaInserida;
	}

	public void setFornecedorMarcaInserida(ScoFornecedorMarca fornecedorMarcaInserida) {
		this.fornecedorMarcaInserida = fornecedorMarcaInserida;
	}

	public DynamicDataModel<ScoFornecedor> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoFornecedor> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoFornecedorMarca getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(ScoFornecedorMarca parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}


	public ScoFornecedorMarcaId getId() {
		return id;
	}


	public void setId(ScoFornecedorMarcaId id) {
		this.id = id;
	}

}
