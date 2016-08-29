package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarAlmoxarifadoTransferenciaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<SceAlmoxarifadoTransferenciaAutomatica> dataModel;

	private static final long serialVersionUID = -5271884724378314890L;

	private static final String PAGE_ESTOQUE_MANTER_ALMOXARIFADO_TRANSFERENCIA = "estoque-manterAlmoxarifadoTransferencia";

	@EJB
	private IEstoqueFacade estoqueFacade;

	private SceAlmoxarifadoTransferenciaAutomatica almoxTransf;

	private SceAlmoxarifadoTransferenciaAutomatica parametroSelecionado;

	private SceAlmoxarifado almoxOrigem;
	private SceAlmoxarifado almoxDestino;
	private DominioSituacao indSituacao;

	// suggestions
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}

	public void pesquisar() {
		getDataModel().reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.almoxOrigem = null;
		this.almoxDestino = null;
		this.indSituacao = null;
		getDataModel().limparPesquisa();
	}

	public String inserirNovo() {
		return PAGE_ESTOQUE_MANTER_ALMOXARIFADO_TRANSFERENCIA;
	}

	@Override
	public Long recuperarCount() {
		return estoqueFacade.pesquisarSceAlmoxTransfAutomaticasCount((almoxOrigem != null) ? almoxOrigem.getSeq() : null, (almoxDestino != null) ? almoxDestino.getSeq() : null, this.indSituacao);
	}

	@Override
	public List<SceAlmoxarifadoTransferenciaAutomatica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<SceAlmoxarifadoTransferenciaAutomatica> result = this.estoqueFacade.pesquisarSceAlmoxTransfAutomaticas(firstResult, maxResult, orderProperty, asc,
				(almoxOrigem != null) ? almoxOrigem.getSeq() : null, (almoxDestino != null) ? almoxDestino.getSeq() : null, indSituacao);
		if (result == null) {
			result = new ArrayList<SceAlmoxarifadoTransferenciaAutomatica>();
		}
		return result;
	}

	public void excluir() {
		try {
			// SceAlmoxarifadoTransferenciaAutomatica almoxTransfAuto =
			// estoqueFacade.obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(almoxOrigemSeq,
			// almoxDestinoSeq);
			estoqueFacade.excluirSceAlmoxTransfAutomaticas(this.parametroSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_ALMOX_TRANS");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void mudaStatus() {
		try {
			this.almoxTransf.setSituacao(this.almoxTransf.getSituacao().equals(DominioSituacao.I) ? DominioSituacao.A : DominioSituacao.I);
			this.estoqueFacade.atualizarSceAlmoxTransfAutomaticas(this.almoxTransf);
			if (this.almoxTransf.getSituacao().equals(DominioSituacao.I)) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INATIVACAO_ALMOX_TRANS");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATIVACAO_ALMOX_TRANS");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void defineAlmox(SceAlmoxarifadoTransferenciaAutomatica almoxTransf) {
		this.almoxTransf = almoxTransf;
	}

	// getters e setters
	public SceAlmoxarifado getAlmoxOrigem() {
		return almoxOrigem;
	}

	public void setAlmoxOrigem(SceAlmoxarifado almoxOrigem) {
		this.almoxOrigem = almoxOrigem;
	}

	public SceAlmoxarifado getAlmoxDestino() {
		return almoxDestino;
	}

	public void setAlmoxDestino(SceAlmoxarifado almoxDestino) {
		this.almoxDestino = almoxDestino;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public SceAlmoxarifadoTransferenciaAutomatica getAlmoxTransf() {
		return almoxTransf;
	}

	public void setAlmoxTransf(SceAlmoxarifadoTransferenciaAutomatica almoxTransf) {
		this.almoxTransf = almoxTransf;
	}

	public DynamicDataModel<SceAlmoxarifadoTransferenciaAutomatica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceAlmoxarifadoTransferenciaAutomatica> dataModel) {
		this.dataModel = dataModel;
	}

	public SceAlmoxarifadoTransferenciaAutomatica getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SceAlmoxarifadoTransferenciaAutomatica parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}