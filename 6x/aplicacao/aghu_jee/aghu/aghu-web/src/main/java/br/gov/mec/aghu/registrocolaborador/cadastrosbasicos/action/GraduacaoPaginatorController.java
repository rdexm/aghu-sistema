package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoQualificacao;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class GraduacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2997811579911502867L;

	private static final String CADASTRAR_GRADUACAO = "cadastrarGraduacao";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private Integer codigoPesquisaGraduacao;

	private String descricaoPesquisaGraduacao;

	private DominioTipoQualificacao tipoPesquisaGraduacao;

	private RapConselhosProfissionais conselhoPesquisaGraduacao;

	private DominioSituacao situacaoPesquisaGraduacao;

	private Integer codigoGraduacao;
	
	@Inject @Paginator
	private DynamicDataModel<RapTipoQualificacao> dataModel;
	
	private RapTipoQualificacao selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarGraduacaoCount(
				this.codigoPesquisaGraduacao, this.descricaoPesquisaGraduacao,
				this.situacaoPesquisaGraduacao, this.tipoPesquisaGraduacao,
				this.conselhoPesquisaGraduacao);
	}

	@Override
	public List<RapTipoQualificacao> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return cadastrosBasicosFacade.pesquisarGraduacao(this.codigoPesquisaGraduacao,
				this.descricaoPesquisaGraduacao,
				this.situacaoPesquisaGraduacao, this.tipoPesquisaGraduacao,
				this.conselhoPesquisaGraduacao, firstResult, maxResults,
				orderProperty, asc);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limpar() {
		dataModel.limparPesquisa();
		this.codigoPesquisaGraduacao = null;
		this.descricaoPesquisaGraduacao = null;
		this.situacaoPesquisaGraduacao = null;			
		this.conselhoPesquisaGraduacao = null;
		this.tipoPesquisaGraduacao = null;
	}

	public String editar(){
		return CADASTRAR_GRADUACAO;
	}

	public String inserir(){
		return CADASTRAR_GRADUACAO;
	}

	public void excluir() {
		try {
			this.cadastrosBasicosFacade.removerGraduacao(selecionado.getCodigo());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_GRADUACAO",selecionado.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Pesquisa conselhos para a suggestion box.
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosPorDescricao(String descricao) {
		return cadastrosBasicosFacade.pesquisarConselhosPorDescricao((String) descricao);
	}

	public Integer getCodigoPesquisaGraduacao() {
		return codigoPesquisaGraduacao;
	}

	public void setCodigoPesquisaGraduacao(Integer codigoPesquisaGraduacao) {
		this.codigoPesquisaGraduacao = codigoPesquisaGraduacao;
	}

	public String getDescricaoPesquisaGraduacao() {
		return descricaoPesquisaGraduacao;
	}

	public void setDescricaoPesquisaGraduacao(String descricaoPesquisaGraduacao) {
		this.descricaoPesquisaGraduacao = descricaoPesquisaGraduacao;
	}

	public DominioSituacao getSituacaoPesquisaGraduacao() {
		return situacaoPesquisaGraduacao;
	}

	public void setSituacaoPesquisaGraduacao(
			DominioSituacao situacaoPesquisaGraduacao) {
		this.situacaoPesquisaGraduacao = situacaoPesquisaGraduacao;
	}

	public Integer getCodigoGraduacao() {
		return codigoGraduacao;
	}

	public void setCodigoGraduacao(Integer codigoGraduacao) {
		this.codigoGraduacao = codigoGraduacao;
	}

	public DominioTipoQualificacao getTipoPesquisaGraduacao() {
		return tipoPesquisaGraduacao;
	}

	public void setTipoPesquisaGraduacao(
			DominioTipoQualificacao tipoPesquisaGraduacao) {
		this.tipoPesquisaGraduacao = tipoPesquisaGraduacao;
	}

	public RapConselhosProfissionais getConselhoPesquisaGraduacao() {
		return conselhoPesquisaGraduacao;
	}

	public void setConselhoPesquisaGraduacao(
			RapConselhosProfissionais conselhoPesquisaGraduacao) {
		this.conselhoPesquisaGraduacao = conselhoPesquisaGraduacao;
	}

	public DynamicDataModel<RapTipoQualificacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapTipoQualificacao> dataModel) {
		this.dataModel = dataModel;
	}

	public RapTipoQualificacao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapTipoQualificacao selecionado) {
		this.selecionado = selecionado;
	}
}
