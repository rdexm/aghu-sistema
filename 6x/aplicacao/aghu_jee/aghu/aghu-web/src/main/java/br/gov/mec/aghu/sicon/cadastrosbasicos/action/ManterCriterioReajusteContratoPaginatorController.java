package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCriterioReajusteContratoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3954557528351325575L;
	
	private static final String PAGE_MANTER_CRITERIO_REAJUSTE_CONTRATO = "manterCriterioReajusteContrato";

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	
	@Inject @Paginator
	private DynamicDataModel<ScoCriterioReajusteContrato> dataModel;

	// filtros de pesquisa
	private Integer codigoCriterio;
	private String descricao;
	private DominioSituacao situacao;
	private Integer seqCriterioReajusteContrato;

	private ScoCriterioReajusteContrato scoCriterioReajusteContratoSelecionado;

	private boolean manterCodigoCriterio;
	private boolean manterDescricao;
	private boolean manterSituacao;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {

		if (codigoCriterio != null) {
			setManterCodigoCriterio(true);
		} else {
			setManterCodigoCriterio(false);
		}

		if (!StringUtils.isEmpty(descricao)) {
			setManterDescricao(true);
		} else {
			setManterDescricao(false);
		}

		if (situacao != null) {
			setManterSituacao(true);
		} else {
			setManterSituacao(false);
		}

		dataModel.reiniciarPaginator();
	}

	public void excluirCriterioReajusteContrato() {
		ScoCriterioReajusteContrato criterioReajusteContrato = cadastrosBasicosSiconFacade
				.obterCriterioReajusteContrato(scoCriterioReajusteContratoSelecionado.getSeq());
		try {
			this.cadastrosBasicosSiconFacade.excluirCriterioReajusteContrato(scoCriterioReajusteContratoSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CRITERIO_REAJUSTE_CONTRATO");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		criterioReajusteContrato.setSeq(null);
		dataModel.reiniciarPaginator();

	}

	public String incluirCriterioReajusteContrato() {

		// verifica qual(is) filtro(s) deve(m) ser mantido(s) após retornar para
		// a tela de pesquisa.
		if (isManterCodigoCriterio() == false) {
			setCodigoCriterio(null);
		}
		if (isManterDescricao() == false) {
			setDescricao(null);
		}
		if (isManterSituacao() == false) {
			setSituacao(null);
		}

		return PAGE_MANTER_CRITERIO_REAJUSTE_CONTRATO;
	}
	
	public String editar(){
		return PAGE_MANTER_CRITERIO_REAJUSTE_CONTRATO;
	}

	@Override
	public List<ScoCriterioReajusteContrato> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<ScoCriterioReajusteContrato> list = this.cadastrosBasicosSiconFacade.pesquisarCriterioReajusteContrato(firstResult,
				maxResult, orderProperty, asc, codigoCriterio, descricao, situacao);

		return list;
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosBasicosSiconFacade.pesquisarCriterioReajusteContratoCount(codigoCriterio, descricao, situacao);
	}

	public void limpar() {
		this.codigoCriterio = null;
		this.descricao = null;
		this.situacao = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public Integer getCodigoCriterio() {
		return codigoCriterio;
	}

	public void setCodigoCriterio(Integer codigoCriterio) {
		this.codigoCriterio = codigoCriterio;
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

	public Integer getSeqCriterioReajusteContrato() {
		return seqCriterioReajusteContrato;
	}

	public void setSeqCriterioReajusteContrato(Integer seqCriterioReajusteContrato) {
		this.seqCriterioReajusteContrato = seqCriterioReajusteContrato;
	}

	public boolean isManterCodigoCriterio() {
		return manterCodigoCriterio;
	}

	public void setManterCodigoCriterio(boolean manterCodigoCriterio) {

		this.manterCodigoCriterio = manterCodigoCriterio;
	}

	public boolean isManterDescricao() {

		return manterDescricao;
	}

	public void setManterDescricao(boolean manterDescricao) {

		this.manterDescricao = manterDescricao;
	}

	public boolean isManterSituacao() {
		return manterSituacao;
	}

	public void setManterSituacao(boolean manterSituacao) {

		this.manterSituacao = manterSituacao;
	}

	public DynamicDataModel<ScoCriterioReajusteContrato> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoCriterioReajusteContrato> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoCriterioReajusteContrato getScoCriterioReajusteContratoSelecionado() {
		return scoCriterioReajusteContratoSelecionado;
	}

	public void setScoCriterioReajusteContratoSelecionado(ScoCriterioReajusteContrato scoCriterioReajusteContratoSelecionado) {
		this.scoCriterioReajusteContratoSelecionado = scoCriterioReajusteContratoSelecionado;
	}
}
