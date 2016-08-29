package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class ConsultarItemTabelaPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5735815629766155811L;

    private static final String MANTER_ITEM_PRINCIPAL = "faturamento-manterItemPrincipal";

	private static final Log LOG = LogFactory.getLog(ConsultarItemTabelaPaginatorController.class);

	private Log getLog() {
		return LOG;
	}

	private String descricao;

	private FatProcedimentosHospitalares procedimentoHospitalar;

	private Long codTabela;

	private boolean inicial = true;

	private boolean exibirBotaoNovo;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject @Paginator
	private DynamicDataModel<FatItensProcedHospitalar> dataModel;

	@PostConstruct
	protected void init() {
		begin(conversation);
		inicio();
	}

	public void inicio() {
		if (isInicial()) {
			setInicial(false);
			carregaTabelaPadrao();
			pesquisar();
			setExibirBotaoNovo(true);
		}
	}

	public String iniciarInclusao() {
		return encaminharManterItemPrincipal();
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.listarFatItensProcedHospitalarCount(getDescricao(), getProcedimentoHospitalar() == null ? null
				: getProcedimentoHospitalar().getSeq(), getCodTabela());
	}

	@Override
	public List<FatItensProcedHospitalar> recuperarListaPaginada(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		List<FatItensProcedHospitalar> lista;
		try {
			lista = this.faturamentoFacade.listarFatItensProcedHospitalar(firstResult, maxResult, orderProperty, asc, getDescricao(),
					getProcedimentoHospitalar() == null ? null : getProcedimentoHospitalar().getSeq(), getCodTabela());
		} catch (final BaseException e) {
			getLog().error("Exceção capturada: ", e);
			lista = new ArrayList<FatItensProcedHospitalar>(0);
		}
		return lista;
	}

	public void pesquisar() {
		// if (StringUtils.isBlank(getDescricao()) && getCodTabela() == null) {
		// this.getStatusMessages().addFromResourceBundle(Severity.ERROR,
		// ConsultarItemTabelaControllerExceptionCode.ITEM_CONTA_CODIGO_OBRIGATORIO.toString());
		// } else {
		dataModel.reiniciarPaginator();
		setExibirBotaoNovo(true);
		// }
	}

	public void limparPesquisa() {
		this.descricao = null;
		this.procedimentoHospitalar = null;
		this.codTabela = null;
		carregaTabelaPadrao();
		this.dataModel.limparPesquisa();
		setExibirBotaoNovo(false);
	}

	public List<FatProcedimentosHospitalares> pesquisarTabela(final String param) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarProcedimentosHospitalar(param),pesquisarTabelaCount(param));
		} catch (final BaseException e) {
			getLog().error("Exceção capturada: ", e);
			return new ArrayList<FatProcedimentosHospitalares>(0);
		}
	}

	public Long pesquisarTabelaCount(final String param) {
		try {
			return faturamentoFacade.listarProcedimentosHospitalarCount(param);
		} catch (final BaseException e) {
			getLog().error("Exceção capturada: ", e);
			return 0L;
		}
	}
	
	public String encaminharManterItemPrincipal(){
		return MANTER_ITEM_PRINCIPAL;
	}

	private void carregaTabelaPadrao() {
		setProcedimentoHospitalar(faturamentoFacade.obterFatProcedimentosHospitalaresPadrao());
	}

	public void setProcedimentoHospitalar(final FatProcedimentosHospitalares procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public FatProcedimentosHospitalares getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setCodTabela(final Long codTabela) {
		this.codTabela = codTabela;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	//
	// public void setHabilitaBotaoNovo(final boolean habilitaBotaoNovo) {
	// this.habilitaBotaoNovo = habilitaBotaoNovo;
	// }
	//
	// public boolean isHabilitaBotaoNovo() {
	// return habilitaBotaoNovo;
	// }

	public void setInicial(final boolean inicial) {
		this.inicial = inicial;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void setExibirBotaoNovo(final boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public DynamicDataModel<FatItensProcedHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItensProcedHospitalar> dataModel) {
		this.dataModel = dataModel;
	}
}
