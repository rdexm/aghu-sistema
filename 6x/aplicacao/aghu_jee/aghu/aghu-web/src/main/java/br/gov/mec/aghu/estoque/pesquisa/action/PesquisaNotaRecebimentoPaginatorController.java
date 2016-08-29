package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoPesquisaNotaRecebimento;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * @author lessandro.lucas
 * 
 */

public class PesquisaNotaRecebimentoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<SceNotaRecebimento> dataModel;

	private static final long serialVersionUID = 2189882189129398429L;

	private static final String PESQUISAR_NR="estoque-consultarNotaRecebimento";

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ConsultaNotaRecebimentoController consultaNotaRecebimentoController;
	
	private SceNotaRecebimento notaRecebimento;
	private DominioSituacaoPesquisaNotaRecebimento situacaoPesquisa;
	private Date dataSituacao;
	private Date dataFinal;

	private Integer numeroComplementoAF;

	private Boolean habilitarDataFinal;

	private ScoFornecedor fornecedor;

	private DominioSimNao estorno;

	private DominioSimNao debitoNotaRecebimento;

	public SceNotaRecebimento getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public void iniciar() {
	 

		if (notaRecebimento == null) {
			setNotaRecebimento(new SceNotaRecebimento());
			getNotaRecebimento().setTipoMovimento(new SceTipoMovimento());
			getNotaRecebimento().setAutorizacaoFornecimento(new ScoAutorizacaoForn());
			getNotaRecebimento().getAutorizacaoFornecimento().setPropostaFornecedor(new ScoPropostaFornecedor());
			getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().setId(new ScoPropostaFornecedorId());
			getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().setFornecedor(null);
			getNotaRecebimento().setDocumentoFiscalEntrada((new SceDocumentoFiscalEntrada()));
			this.situacaoPesquisa = DominioSituacaoPesquisaNotaRecebimento.G;
		}
		verificarDataSituacao();
	
	}

	public String pesquisar() {
		String retorno = null;
		try {
			verificaDataSituacaoInicialDataSituacaoFinal();
			if (numeroComplementoAF != null) {
				getNotaRecebimento().getAutorizacaoFornecimento().setNroComplemento(numeroComplementoAF.shortValue());
			}
			this.dataModel.reiniciarPaginator();

			List<SceNotaRecebimento> listNotas = this.dataModel.getPaginator().recuperarListaPaginada(0, 2, null, true);
			if (listNotas != null && listNotas.size() == 1) {
				retorno = PESQUISAR_NR;
				consultaNotaRecebimentoController.setNotaRecebimento(listNotas.get(0));
				consultaNotaRecebimentoController.setOrigem("estoque-pesquisarNotaRecebimento");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	private void verificaDataSituacaoInicialDataSituacaoFinal() throws ApplicationBusinessException {
		estoqueFacade.verificaDataSituacaoInicialDataSituacaoFinal(getDataSituacao(), getDataFinal());
	}

	public Integer getSeq() {
		return getDataModel().getRowCount() == 1 ? ((SceNotaRecebimento) getDataModel().getRowData()).getSeq() : null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		Integer numeroFornecedor = null, numeroProcessoCompra = null;

		if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor() != null) {
			if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getId() != null) {
				numeroProcessoCompra = getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getId().getLctNumero();
			}
			if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor() != null) {
				numeroFornecedor = getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor().getNumero();
			}
		}

		if (getFornecedor() != null) {
			getNotaRecebimento().getDocumentoFiscalEntrada().setFornecedor(fornecedor);
		} else {
			getNotaRecebimento().getDocumentoFiscalEntrada().setFornecedor(null);
		}

		return this.estoqueFacade.pesquisarNotasRecebimentoConsulta(getNotaRecebimento().getSeq(), converterEstornoDebitoBoolean(this.estorno),
				converterEstornoDebitoBoolean(this.debitoNotaRecebimento), getSituacaoPesquisa(), getDataSituacao(), getDataFinal(), numeroProcessoCompra, getNotaRecebimento()
						.getAutorizacaoFornecimento().getNroComplemento(), getNotaRecebimento().getAutorizacaoFornecimento().getSituacao(), numeroFornecedor, getNotaRecebimento()
						.getDocumentoFiscalEntrada(), firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		Integer numeroFornecedor = null, numeroProcessoCompra = null;

		if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor() != null) {
			if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getId() != null) {
				numeroProcessoCompra = getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getId().getLctNumero();
			}
			if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor() != null) {
				numeroFornecedor = getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor().getNumero();
			}
		}

		if (getFornecedor() != null) {
			getNotaRecebimento().getDocumentoFiscalEntrada().setFornecedor(fornecedor);
		} else {
			getNotaRecebimento().getDocumentoFiscalEntrada().setFornecedor(null);
		}

		return estoqueFacade.pesquisarNotasRecebimentoConsultaCount(getNotaRecebimento().getSeq(), converterEstornoDebitoBoolean(this.estorno),
				converterEstornoDebitoBoolean(this.debitoNotaRecebimento), getSituacaoPesquisa(), getDataSituacao(), getDataFinal(), numeroProcessoCompra, getNotaRecebimento()
						.getAutorizacaoFornecimento().getNroComplemento(), getNotaRecebimento().getAutorizacaoFornecimento().getSituacao(), numeroFornecedor, getNotaRecebimento()
						.getDocumentoFiscalEntrada());
	}

	private boolean converterEstornoDebitoBoolean(DominioSimNao simNao) {
		return simNao != null ? simNao.isSim() : false;
	}

	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(Object objPesquisa) {
		return comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(objPesquisa);
	}

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos() {
		setNotaRecebimento(new SceNotaRecebimento());
		getNotaRecebimento().setTipoMovimento(new SceTipoMovimento());
		getNotaRecebimento().setAutorizacaoFornecimento(new ScoAutorizacaoForn());
		getNotaRecebimento().getAutorizacaoFornecimento().setPropostaFornecedor(new ScoPropostaFornecedor());
		getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().setId(new ScoPropostaFornecedorId());
		getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().setFornecedor(null);
		getNotaRecebimento().setDocumentoFiscalEntrada((new SceDocumentoFiscalEntrada()));
		setSituacaoPesquisa(null);
		setDataSituacao(null);
		setDataFinal(null);
		setNumeroComplementoAF(null);
		setFornecedor(null);
		setEstorno(null);
		setDebitoNotaRecebimento(null);
		this.dataModel.limparPesquisa();
	}

	// Redireciona para consulta de nota de recebimento
	// public String consultaNotaRecebimento(){
	// return PAGE_ESTOQUE_CONSULTAR_NOTA_RECEBIMENTO;
	// }

	/**
	 * Obtem lista para sugestion box de fornecedores
	 * 
	 * @param param
	 * @return
	 */
	public List<ScoFornecedor> obterFornecedores(String param) {
		return comprasFacade.obterFornecedor(param);
	}

	/**
	 * Utilizado pelo suggestionbox para pesquisar o servidor
	 * 
	 * @param parametro
	 * @return
	 */
	public List<RapServidores> pesquisarListaServidores(Object parametro) {
		return this.registroColaboradorFacade.pesquisarServidores(parametro);
	}

	public DominioSituacaoPesquisaNotaRecebimento getSituacaoPesquisa() {
		return situacaoPesquisa;
	}

	public void setSituacaoPesquisa(DominioSituacaoPesquisaNotaRecebimento situacaoPesquisa) {
		this.situacaoPesquisa = situacaoPesquisa;
	}

	public Date getDataSituacao() {
		return dataSituacao;
	}

	public void setDataSituacao(Date dataSituacao) {
		this.dataSituacao = dataSituacao;
	}

	public Integer getNumeroComplementoAF() {
		return numeroComplementoAF;
	}

	public void setNumeroComplementoAF(Integer numeroComplementoAF) {
		this.numeroComplementoAF = numeroComplementoAF;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Boolean getHabilitarDataFinal() {
		return habilitarDataFinal;
	}

	public void setHabilitarDataFinal(Boolean habilitarDataFinal) {
		this.habilitarDataFinal = habilitarDataFinal;
	}

	public void verificarDataSituacao() {
		setHabilitarDataFinal(estoqueFacade.habilitarCampoDataFinalConsultarNotaRecebimento(getDataSituacao()));
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public DynamicDataModel<SceNotaRecebimento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceNotaRecebimento> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioSimNao getEstorno() {
		return estorno;
	}

	public void setEstorno(DominioSimNao estorno) {
		this.estorno = estorno;
	}

	public DominioSimNao getDebitoNotaRecebimento() {
		return debitoNotaRecebimento;
	}

	public void setDebitoNotaRecebimento(DominioSimNao debitoNotaRecebimento) {
		this.debitoNotaRecebimento = debitoNotaRecebimento;
	}
}
