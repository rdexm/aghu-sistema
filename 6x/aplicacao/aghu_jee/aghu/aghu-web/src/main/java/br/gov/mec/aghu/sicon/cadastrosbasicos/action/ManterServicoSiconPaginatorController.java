package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;


public class ManterServicoSiconPaginatorController extends
		ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8835763390972444967L;
	
	private static final String PAGE_MANTER_SERVICO_SICON = "manterServicoSicon";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject @Paginator
	private DynamicDataModel<ScoServicoSicon> dataModel;

	// filtros de pesquisa
	private Integer seq;
	private Integer codigoSicon;
	private ScoServico servico;
	private ScoCatalogoSicon catalogoSiconServico;
	private ScoServicoSicon scoServicoSiconSelecionado;
	private DominioSituacao situacao;
	private ScoGrupoServico grupoServico;
	private Integer codigoGrupo;

	private boolean manterCatalogoSiconServico;
	private boolean manterSituacao;
	private boolean manterGrupoServico;
	private boolean manterServico;

	private List<SelectItem> listaGrupos;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public String incluirServicoSicon() {

		if (isManterCatalogoSiconServico() == false) {
			setCatalogoSiconServico(null);
		}

		if (isManterGrupoServico() == false) {
			setGrupoServico(null);
		}

		if (isManterServico() == false) {
			setServico(null);
		}

		if (isManterSituacao() == false) {
			setSituacao(null);
		}

		return PAGE_MANTER_SERVICO_SICON;
	}
	
	public String editar(){
		return PAGE_MANTER_SERVICO_SICON;
	}

	public void pesquisar() {

		if (this.catalogoSiconServico != null) {
			setManterCatalogoSiconServico(true);
		} else {
			setManterCatalogoSiconServico(false);
		}

		if (this.servico != null) {
			setManterServico(true);
		} else {
			setManterServico(false);
		}

		if (this.grupoServico != null) {
			setManterGrupoServico(true);
		} else {
			setManterGrupoServico(false);
		}

		if (this.situacao != null) {
			setManterSituacao(true);
		} else {
			setManterSituacao(false);
		}		
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}

	@Override
	public List<ScoServicoSicon> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		if (catalogoSiconServico == null) {
			codigoSicon = null;
		} else {
			codigoSicon = catalogoSiconServico.getCodigoSicon();
		}

		List<ScoServicoSicon> list = this.cadastrosBasicosSiconFacade
				.pesquisarServicoSicon(firstResult, maxResult, orderProperty,
						asc, codigoSicon, servico, situacao, grupoServico);

		return list;
	}

	@Override
	public Long recuperarCount() {
		if (catalogoSiconServico == null) {
			codigoSicon = null;
		} else {
			codigoSicon = catalogoSiconServico.getCodigoSicon();
		}

		return this.cadastrosBasicosSiconFacade.pesquisarServicoSiconCount(
				codigoSicon, servico, situacao, grupoServico);
	}

	public List<ScoServico> listarServicosAtivos(String pesquisa) {

		List<ScoServico> servicos = new ArrayList<ScoServico>();

		try {
			servicos = cadastrosBasicosSiconFacade.listarServicosDeGruposAtivos(
					pesquisa, getGrupoServico());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return this.returnSGWithCount(null,listarServicosAtivosCount(pesquisa));
		}

		return servicos;
	}
	
	public Long listarServicosAtivosCount(String pesquisa) {

		try {
			return cadastrosBasicosSiconFacade.listarServicosDeGruposAtivosCount(
					pesquisa, getGrupoServico());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public List<ScoCatalogoSicon> listarCodigoSiconServicoAtivo(String pesquisa) {

		List<ScoCatalogoSicon> catalogoSiconServico = cadastrosBasicosSiconFacade
				.listarCatalogoSiconServicoAtivo(pesquisa);

		return this.returnSGWithCount(catalogoSiconServico,listarCodigoSiconServicoAtivoCount(pesquisa));
	}
	
	public Long listarCodigoSiconServicoAtivoCount(String pesquisa) {

		return cadastrosBasicosSiconFacade
				.listarCatalogoSiconServicoAtivoCount(pesquisa);
	}

	public List<ScoGrupoServico> listarGrupoServico(String pesquisa) {

		List<ScoGrupoServico> grupoServico = comprasFacade
				.listarGrupoServico(pesquisa);

		return grupoServico;
	}

	public void limpar() {
		this.catalogoSiconServico = null;
		this.codigoSicon = null;
		this.servico = null;
		this.situacao = null;
		this.grupoServico = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoCatalogoSicon getCatalogoSiconServico() {
		return catalogoSiconServico;
	}

	public void setCatalogoSiconServico(ScoCatalogoSicon catalogoSiconServico) {
		this.catalogoSiconServico = catalogoSiconServico;
	}

	public Integer getCodigoGrupo() {
		return codigoGrupo;
	}

	public void setCodigoGrupo(Integer codigoGrupo) {
		this.codigoGrupo = codigoGrupo;
	}

	public List<SelectItem> getListaGrupos() {
		return listaGrupos;
	}

	public void setListaGrupos(List<SelectItem> listaGrupos) {
		this.listaGrupos = listaGrupos;
	}

	public boolean isManterCatalogoSiconServico() {
		return manterCatalogoSiconServico;
	}

	public void setManterCatalogoSiconServico(boolean manterCatalogoSiconServico) {
		this.manterCatalogoSiconServico = manterCatalogoSiconServico;
	}

	public boolean isManterSituacao() {
		return manterSituacao;
	}

	public void setManterSituacao(boolean manterSituacao) {
		this.manterSituacao = manterSituacao;
	}

	public boolean isManterGrupoServico() {
		return manterGrupoServico;
	}

	public void setManterGrupoServico(boolean manterGrupoServico) {
		this.manterGrupoServico = manterGrupoServico;
	}

	public boolean isManterServico() {
		return manterServico;
	}

	public void setManterServico(boolean manterServico) {
		this.manterServico = manterServico;
	} 


	public DynamicDataModel<ScoServicoSicon> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoServicoSicon> dataModel) {
	 this.dataModel = dataModel;
	}

	public ScoServicoSicon getScoServicoSiconSelecionado() {
		return scoServicoSiconSelecionado;
	}

	public void setScoServicoSiconSelecionado(ScoServicoSicon scoServicoSiconSelecionado) {
		this.scoServicoSiconSelecionado = scoServicoSiconSelecionado;
	}
}
