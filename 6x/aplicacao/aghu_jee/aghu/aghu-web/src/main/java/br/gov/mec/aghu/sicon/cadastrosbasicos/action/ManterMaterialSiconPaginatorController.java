package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class ManterMaterialSiconPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4124429093079907847L;
	
	private static final String PAGE_MANTER_MATERIAL_SICON = "manterMaterialSicon";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject @Paginator
	private DynamicDataModel<ScoMaterialSicon> dataModel;

	private ScoMaterialSicon materialSicon = new ScoMaterialSicon();
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private Integer codigoSicon;
	private DominioSituacao situacao;
	private boolean alterar;
	private boolean pesquisaAtivada;
	private ScoCatalogoSicon catalogoSiconMaterial;
	private ScoMaterialSicon scoMaterialSiconSelecionado;
	private String origem;

	private boolean manterCatalogoSiconMaterial;
	private boolean manterMaterial;
	private boolean manterGrupoMaterial;
	private boolean manterSituacao;
	private boolean manterCatalogo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<ScoMaterialSicon> recuperarListaPaginada(Integer _firstResult, Integer _maxResult, String _orderProperty,
			boolean _asc) {

		if (catalogoSiconMaterial == null) {
			codigoSicon = null;
		} else {
			codigoSicon = catalogoSiconMaterial.getCodigoSicon();
		}

		List<ScoMaterialSicon> result = cadastrosBasicosSiconFacade.pesquisarMateriaisSicon(_firstResult, _maxResult,
				_orderProperty, _asc, this.getCodigoSicon(), this.getMaterial(), this.getSituacao(), this.getGrupoMaterial());

		if (result == null) {
			result = new ArrayList<ScoMaterialSicon>();
		}

		return result;
	}

	@Override
	public Long recuperarCount() {

		if (catalogoSiconMaterial == null) {
			codigoSicon = null;
		} else {

			codigoSicon = catalogoSiconMaterial.getCodigoSicon();
		}

		return cadastrosBasicosSiconFacade.listarMateriaisSiconCount(this.getCodigoSicon(), this.getMaterial(),
				this.getSituacao(), getGrupoMaterial());
	}

	public void iniciar() {
	 


		if ((this.origem != null) && (this.getOrigem().equals("manterMaterialSicon"))) {
			dataModel.setPesquisaAtiva(Boolean.FALSE);
			pesquisar();
		}
	
	}

	public void limpar() {

		this.catalogoSiconMaterial = null;
		this.materialSicon = new ScoMaterialSicon();
		this.codigoSicon = null;
		this.material = null;
		
		this.situacao = null;
		this.grupoMaterial = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);

	}

	public String incluirMaterialSicon() {

		if (isManterCatalogoSiconMaterial() == false) {
			setCatalogoSiconMaterial(null);
		}

		if (isManterMaterial() == false) {
			setMaterial(null);
		}

		if (isManterSituacao() == false) {
			setSituacao(null);
		}

		if (isManterGrupoMaterial() == false) {
			setGrupoMaterial(null);
		}

		return PAGE_MANTER_MATERIAL_SICON;
	}

	public String alterarMaterialSicon() {

		return PAGE_MANTER_MATERIAL_SICON;
	}	

	public void pesquisar() {

		if (this.catalogoSiconMaterial != null) {
			setManterCatalogoSiconMaterial(true);
		} else {
			setManterCatalogoSiconMaterial(false);
		}

		if (this.material != null) {
			setManterMaterial(true);
		} else {
			setManterMaterial(false);
		}

		if (this.situacao != null) {
			setManterSituacao(true);
		} else {
			setManterSituacao(false);
		}

		if (this.grupoMaterial != null) {
			setManterGrupoMaterial(true);
		} else {
			setManterGrupoMaterial(false);
		}

		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}

	public List<ScoMaterial> pesquisarMateriais(String _input) {

		List<ScoMaterial> listaMaterial = null;

		try {

			// se não houver um grupo de material definido, faz validação de
			// número mínimo de caracteres
			// do input de material.
			if (getGrupoMaterial() == null) {
				listaMaterial = this.cadastrosBasicosSiconFacade.pesquisarMateriaisPorFiltro(_input);
			} else {

				// se um grupo de material foi selecionado, não faz a validação
				// de número mínimo de caracteres
				// do input de material.
				listaMaterial = this.comprasFacade.listarScoMateriaisGrupoAtiva(_input, getGrupoMaterial(), false, false);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return  this.returnSGWithCount(listaMaterial,pesquisarMateriaisCount(_input));
	}
	
	public Long pesquisarMateriaisCount(String _input) {

		return this.comprasFacade.pesquisarMateriaisPorFiltroCount(_input);
	}

	public List<ScoCatalogoSicon> listarCodigoSiconMaterialAtivo(String pesquisa) {

		List<ScoCatalogoSicon> catalogoSiconMaterial = null;

		try {
			catalogoSiconMaterial = this.cadastrosBasicosSiconFacade.listarCodigoSiconServicoAtivo(pesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return  catalogoSiconMaterial;
	}
	
	public Long listarCodigoSiconMaterialAtivoCount(Object pesquisa) {

		try {
			return cadastrosBasicosSiconFacade.listarCodigoSiconServicoAtivoCount(pesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	// getters e setters


	public List<ScoGrupoMaterial> pesquisarGrupoMateriais(String _input) {

		return  this.comprasFacade.pesquisarGrupoMaterialPorFiltro(_input);
	}

	public void setCadastrosBasicosSiconFacade(ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade) {
		this.cadastrosBasicosSiconFacade = cadastrosBasicosSiconFacade;
	}

	public ScoMaterialSicon getMaterialSicon() {
		return materialSicon;
	}

	public void setMaterialSicon(ScoMaterialSicon materialSicon) {
		this.materialSicon = materialSicon;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public boolean isPesquisaAtivada() {
		return pesquisaAtivada;
	}

	public void setPesquisaAtivada(boolean pesquisaAtivada) {
		this.pesquisaAtivada = pesquisaAtivada;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoCatalogoSicon getCatalogoSiconMaterial() {
		return catalogoSiconMaterial;
	}

	public void setCatalogoSiconMaterial(ScoCatalogoSicon catalogoSiconMaterial) {
		this.catalogoSiconMaterial = catalogoSiconMaterial;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isManterCatalogoSiconMaterial() {
		return manterCatalogoSiconMaterial;
	}

	public void setManterCatalogoSiconMaterial(boolean manterCatalogoSiconMaterial) {
		this.manterCatalogoSiconMaterial = manterCatalogoSiconMaterial;
	}

	public boolean isManterMaterial() {
		return manterMaterial;
	}

	public void setManterMaterial(boolean manterMaterial) {
		this.manterMaterial = manterMaterial;
	}

	public boolean isManterGrupoMaterial() {
		return manterGrupoMaterial;
	}

	public void setManterGrupoMaterial(boolean manterGrupoMaterial) {
		this.manterGrupoMaterial = manterGrupoMaterial;
	}

	public boolean isManterSituacao() {
		return manterSituacao;
	}

	public void setManterSituacao(boolean manterSituacao) {
		this.manterSituacao = manterSituacao;
	}

	public boolean isManterCatalogo() {
		return manterCatalogo;
	}

	public void setManterCatalogo(boolean manterCatalogo) {
		this.manterCatalogo = manterCatalogo;
	}

	public DynamicDataModel<ScoMaterialSicon> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoMaterialSicon> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoMaterialSicon getScoMaterialSiconSelecionado() {
		return scoMaterialSiconSelecionado;
	}

	public void setScoMaterialSiconSelecionado(ScoMaterialSicon scoMaterialSiconSelecionado) {
		this.scoMaterialSiconSelecionado = scoMaterialSiconSelecionado;
	}
}
