package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCatalogoController extends ActionController {

	private static final long serialVersionUID = 8747638844858253328L;

	private static final String PAGE_PESQUISAR_CATALOGO = "pesquisarCatalogo";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	

	private ScoCatalogoSicon catalogoSicon = new ScoCatalogoSicon();
	private Integer codigoSicon;
	private String descricao;
	private DominioTipoItemContrato tipoItemContrato;
	private DominioSituacao situacao;
	private boolean alterar;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Método inicial da tela
	 */
	public void iniciar() {
	 


		if (this.codigoSicon != null) {

			this.catalogoSicon = this.cadastrosBasicosSiconFacade.obterCatalogoSicon(this.getCodigoSicon());

			if (catalogoSicon != null) {

				this.setDescricao(this.catalogoSicon.getDescricao());
				this.setTipoItemContrato(this.catalogoSicon.getTipoItemContrato());
				this.setSituacao(this.catalogoSicon.getSituacao());
			} else {
				apresentarExcecaoNegocio(new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO));
				this.catalogoSicon = new ScoCatalogoSicon();
			}

		} else {

			this.catalogoSicon = new ScoCatalogoSicon();
			if (!this.alterar) {
				this.setSituacao(DominioSituacao.A);
			}
		}
	
	}

	public String salvar() {
		
		try {
			this.catalogoSicon.setCodigoSicon(this.getCodigoSicon());
			this.catalogoSicon.setDescricao(this.getDescricao().trim());
			this.catalogoSicon.setTipoItemContrato(getTipoItemContrato());
			this.catalogoSicon.setSituacao(getSituacao());

			if (isAlterar()) {
				this.cadastrosBasicosSiconFacade.alterarItemCatalogo(this.catalogoSicon);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_ITEM_CATALOGO");

			} else {
				this.cadastrosBasicosSiconFacade.inserirItemCatalogo(this.catalogoSicon);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ITEM_CATALOGO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		this.catalogoSicon = new ScoCatalogoSicon();
		this.codigoSicon = null;
		this.descricao = null;
		this.tipoItemContrato = null;
		this.situacao = null;

		return PAGE_PESQUISAR_CATALOGO;
	}

	public String voltar() {

		this.catalogoSicon = new ScoCatalogoSicon();
		this.setCodigoSicon(null);
		this.setDescricao(null);
		this.setTipoItemContrato(null);
		this.setSituacao(null);

		return PAGE_PESQUISAR_CATALOGO;

	}

	// Getters e Setters
	public ScoCatalogoSicon getCatalogoSicon() {
		return catalogoSicon;
	}

	public void setCatalogoSicon(ScoCatalogoSicon catalogoSicon) {
		this.catalogoSicon = catalogoSicon;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioTipoItemContrato getTipoItemContrato() {
		return tipoItemContrato;
	}

	public void setTipoItemContrato(DominioTipoItemContrato tipoItemContrato) {
		this.tipoItemContrato = tipoItemContrato;
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
}
