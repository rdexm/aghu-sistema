package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTipoContratoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1990151600745189312L;

	private static final String PAGE_MANTER_TIPO_CONTRATO = "manterTipoContrato";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	@Inject @Paginator
	private DynamicDataModel<ScoTipoContratoSicon> dataModel;

	private ScoTipoContratoSicon tipoContratoSicon = new ScoTipoContratoSicon();
	private ScoTipoContratoSicon scoTipoContratoSiconSelecionado;
	private Integer seqContrato;
	
	private Integer codigoSicon;
	private String descricao;
	private DominioSituacao situacao;
	
	private String origem;

	private boolean alterar;

	private Integer codigoFiltro;
	private String descricaoFiltro;
	private DominioSituacao situacaoFiltro;
	private boolean temVinculoContrato;
	private Integer seqTipoContrato;
	private List<ScoTipoContratoSicon> listaTipoContrato;
	private Boolean modalidade;
	private Boolean insereItens;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<ScoTipoContratoSicon> recuperarListaPaginada(Integer _firstResult, Integer _maxResult, String _orderProperty,
			boolean _asc) {

		List<ScoTipoContratoSicon> result = cadastrosBasicosSiconFacade.pesquisarTiposContrato(_firstResult, _maxResult,
				_orderProperty, _asc, this.getCodigoSicon(), this.getDescricao(), this.getSituacao());

		if (result == null) {
			result = new ArrayList<ScoTipoContratoSicon>();
		}

		return result;
	}

	@Override
	public Long recuperarCount() {

		Long count = cadastrosBasicosSiconFacade.listarTipoContratoCount(this.getCodigoSicon(), this.getDescricao(),
				this.getSituacao());
		if (count == null) {
			count = 0l;
		}
		return count;
	}

	public void iniciar() {
	 


		this.setCodigoSicon(null);
		this.setDescricao(null);
		this.setSituacao(null);
		this.setModalidade(false);
		this.setInsereItens(true);

		setCodigoSicon(codigoFiltro);
		setDescricao(descricaoFiltro);
		setSituacao(situacaoFiltro);
		
	
	}

	public void limpar() {

		this.tipoContratoSicon = new ScoTipoContratoSicon();
		this.codigoSicon = null;
		this.descricao = null;
		this.situacao = null;
		dataModel.setPesquisaAtiva(false);
		this.origem = null;
		this.setModalidade(false);
		this.setInsereItens(true);

		// limpa as variáveis de filtro
		this.codigoFiltro = null;
		this.descricaoFiltro = null;
		this.situacaoFiltro = null;
	}

	public String incluirTipoContrato() {
		return PAGE_MANTER_TIPO_CONTRATO;
	}

	public String redirecionaEditar() {
		return PAGE_MANTER_TIPO_CONTRATO;
	}

	public void excluir() {

		try {
			if (scoTipoContratoSiconSelecionado != null) {
				this.cadastrosBasicosSiconFacade.excluirTipoContrato(scoTipoContratoSiconSelecionado.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TIPO_CONTRATO", scoTipoContratoSiconSelecionado.getCodigoSicon());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TIPO_CONTRATO_INEXISTENTE");
			}
		} catch (ApplicationBusinessException e) {
			this.tipoContratoSicon = new ScoTipoContratoSicon();
			this.tipoContratoSicon = null;
			this.descricao = null;

			apresentarExcecaoNegocio(e);
		}

		this.tipoContratoSicon = new ScoTipoContratoSicon();
		this.codigoSicon = null;
		this.descricao = null;
	}

	public void pesquisar() {

		this.codigoFiltro = this.codigoSicon;
		this.descricaoFiltro = this.descricao;
		this.situacaoFiltro = this.situacao;

		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}

	public ScoTipoContratoSicon getTipoContratoSicon() {
		return tipoContratoSicon;
	}

	public void setTipoContratoSicon(ScoTipoContratoSicon tipoContratoSicon) {
		this.tipoContratoSicon = tipoContratoSicon;
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

	public List<ScoTipoContratoSicon> getListaTipoContrato() {
		return listaTipoContrato;
	}

	public void setListaTipoContrato(List<ScoTipoContratoSicon> listaTipoContrato) {
		this.listaTipoContrato = listaTipoContrato;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public Integer getSeqContrato() {
		return seqContrato;
	}

	public void setSeqContrato(Integer seqContrato) {
		this.seqContrato = seqContrato;
	}

	public Integer getCodigoFiltro() {
		return codigoFiltro;
	}

	public void setCodigoFiltro(Integer codigoFiltro) {
		this.codigoFiltro = codigoFiltro;
	}

	public String getDescricaoFiltro() {
		return descricaoFiltro;
	}

	public void setDescricaoFiltro(String descricaoFiltro) {
		this.descricaoFiltro = descricaoFiltro;
	}

	public DominioSituacao getSituacaoFiltro() {
		return situacaoFiltro;
	}

	public void setSituacaoFiltro(DominioSituacao situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}

	public boolean isTemVinculoContrato() {
		return temVinculoContrato;
	}

	public void setTemVinculoContrato(boolean temVinculoContrato) {
		this.temVinculoContrato = temVinculoContrato;
	}

	public Integer getSeqTipoContrato() {
		return seqTipoContrato;
	}

	public void setSeqTipoContrato(Integer seqTipoContrato) {
		this.seqTipoContrato = seqTipoContrato;
	}

	public Boolean getModalidade() {
		return modalidade;
	}

	public void setModalidade(Boolean modalidade) {
		this.modalidade = modalidade;
	}

	public Boolean getInsereItens() {
		return insereItens;
	}

	public void setInsereItens(Boolean insereItens) {
		this.insereItens = insereItens;
	}

	public DynamicDataModel<ScoTipoContratoSicon> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoTipoContratoSicon> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoTipoContratoSicon getScoTipoContratoSiconSelecionado() {
		return scoTipoContratoSiconSelecionado;
	}

	public void setScoTipoContratoSiconSelecionado(ScoTipoContratoSicon scoTipoContratoSiconSelecionado) {
		this.scoTipoContratoSiconSelecionado = scoTipoContratoSiconSelecionado;
	}
}
