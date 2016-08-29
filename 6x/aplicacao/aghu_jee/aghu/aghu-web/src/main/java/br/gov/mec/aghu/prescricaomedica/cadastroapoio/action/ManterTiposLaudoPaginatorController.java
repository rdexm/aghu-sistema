package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ManterTiposLaudoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5052354636603686762L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private Short seqTipoLaudo;

	private String descricaoTipoLaudo;

	private DominioSituacao situacaoTipoLaudo;

	private boolean exibirBotaoIncluirTipoLaudo = false;
	
	@Inject @Paginator
	private DynamicDataModel<MpmTipoLaudo> dataModel;
	
	private MpmTipoLaudo tipoLaudoSelecionado;
	
	private final String PAGE_TIPO_LAUDO_CRUD = "manterTiposLaudoCRUD";

	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return this.prescricaoMedicaFacade.listarTiposLaudoCount(this.seqTipoLaudo, this.descricaoTipoLaudo, this.situacaoTipoLaudo);
	}

	@Override
	public List<MpmTipoLaudo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.prescricaoMedicaFacade.listarTiposLaudo(firstResult, maxResult, MpmTipoLaudo.Fields.DESCRICAO.toString(), true, this.seqTipoLaudo,
				this.descricaoTipoLaudo, this.situacaoTipoLaudo);
	}

	public String incluirEditar() {
		return PAGE_TIPO_LAUDO_CRUD;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.seqTipoLaudo = null;
		this.descricaoTipoLaudo = null;
		this.situacaoTipoLaudo = null;
		this.exibirBotaoIncluirTipoLaudo = false;
		this.dataModel.limparPesquisa();
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirTipoLaudo = true;
	}

	public boolean isExibirBotaoIncluirTipoLaudo() {
		return exibirBotaoIncluirTipoLaudo;
	}

	public void setExibirBotaoIncluirTipoLaudo(boolean exibirBotaoIncluirTipoLaudo) {
		this.exibirBotaoIncluirTipoLaudo = exibirBotaoIncluirTipoLaudo;
	}

	public Short getSeqTipoLaudo() {
		return seqTipoLaudo;
	}

	public void setSeqTipoLaudo(Short seqTipoLaudo) {
		this.seqTipoLaudo = seqTipoLaudo;
	}

	public String getDescricaoTipoLaudo() {
		return descricaoTipoLaudo;
	}

	public void setDescricaoTipoLaudo(String descricaoTipoLaudo) {
		this.descricaoTipoLaudo = descricaoTipoLaudo;
	}

	public DominioSituacao getSituacaoTipoLaudo() {
		return situacaoTipoLaudo;
	}

	public void setSituacaoTipoLaudo(DominioSituacao situacaoTipoLaudo) {
		this.situacaoTipoLaudo = situacaoTipoLaudo;
	}

	public DynamicDataModel<MpmTipoLaudo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmTipoLaudo> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmTipoLaudo getTipoLaudoSelecionado() {
		return tipoLaudoSelecionado;
	}

	public void setTipoLaudoSelecionado(MpmTipoLaudo tipoLaudoSelecionado) {
		this.tipoLaudoSelecionado = tipoLaudoSelecionado;
	}
}
