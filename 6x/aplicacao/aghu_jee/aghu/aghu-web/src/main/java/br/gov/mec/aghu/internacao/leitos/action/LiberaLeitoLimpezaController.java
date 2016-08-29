package br.gov.mec.aghu.internacao.leitos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.vo.LiberaLeitoLimpezaVO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class LiberaLeitoLimpezaController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2718169945643128708L;

	/**
	 * Responsável pela pesquisa de Leitos Limpeza
	 */
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	

	/**
	 * Responsavel em obter leito.
	 */
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Responsavel em obter internacao.
	 */
	@EJB
	private IInternacaoFacade internacaoFacade;

	/**
	 * Leito selecionado pelo usuário.
	 */
	private AinLeitos leito;

	/**
	 * Internação
	 */
	private AinInternacao internacao;

	/**
	 * Filtro por leito
	 */
	private String leitoId;

	@Inject @Paginator
	private DynamicDataModel<LiberaLeitoLimpezaVO> dataModel;
	private LiberaLeitoLimpezaVO selection = new LiberaLeitoLimpezaVO();

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	/**
	 * Ação do botão pesquisar
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		Long count = this.leitosInternacaoFacade.pesquisarLeitosLimpezaCount(leitoId);
		return count;
	}

	@Override
	public List<LiberaLeitoLimpezaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<LiberaLeitoLimpezaVO> leitosLimpeza = this.leitosInternacaoFacade.pesquisarLeitosLimpeza(leitoId, firstResult, maxResult, orderProperty, asc);

		if (leitosLimpeza == null) {
			return new ArrayList<LiberaLeitoLimpezaVO>();
		}

		return leitosLimpeza;
	}

	/**
	 * Leito selecionado pelo usuário.
	 * 
	 * @param leito
	 * @return
	 */
	public void obterLeito(String leito) {
		this.leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leito);
	}

	/**
	 * Leito selecionado pelo usuário.
	 * 
	 * @param leito
	 * @param intSeq
	 */
	public void obterInternacao(String leito, Integer intSeq) {
		this.leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leito);
		if (intSeq != null) {
			internacao = internacaoFacade.obterInternacaoPorSeq(intSeq);
		}
	}

	/**
	 * Executa ação de liberar leito
	 */
	public void liberar() {
		try {
			leitosInternacaoFacade.liberarLeitoLimpeza(leito);
			limparPesquisa();
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "Leito liberado com sucesso!");
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Executa a ação de bloquear o leito
	 */
	public void bloquear() {
		try {
			leitosInternacaoFacade.bloquearLeitoLimpeza(leito, internacao);
			limparPesquisa();
			dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "Leito bloqueado com sucesso!");
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão limpar pesquisa
	 */
	public void limparPesquisa() {
		this.leito = null;
		this.internacao = null;
		this.leitoId = null;
		this.dataModel.limparPesquisa();
	}

	// GETs AND SETs
	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	public DynamicDataModel<LiberaLeitoLimpezaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<LiberaLeitoLimpezaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public LiberaLeitoLimpezaVO getSelection() {
		return selection;
	}

	public void setSelection(LiberaLeitoLimpezaVO selection) {
		this.selection = selection;
	}
	
}
