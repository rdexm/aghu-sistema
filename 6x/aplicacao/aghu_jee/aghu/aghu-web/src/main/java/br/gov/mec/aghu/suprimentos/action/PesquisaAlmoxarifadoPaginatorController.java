package br.gov.mec.aghu.suprimentos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaAlmoxarifadoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		inicializarCampos();
	}

	@Inject @Paginator
	private DynamicDataModel<SceAlmoxarifado> dataModel;

	private static final long serialVersionUID = 8905251467266256821L;

	public enum EnumTargetPesquisaAlmoxarifado {
		MANTER_ALMOXARIFADO;
	}

	private static final String PAGE_ESTOQUE_MANTER_ALMOXARIFADO = "estoque-manterAlmoxarifado";

	private enum ManterAlmoxarifadoBusinessExceptionCode implements BusinessExceptionCode {
		ALMOXARIFADO_ALTERADO_COM_SUCESSO;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	private SceAlmoxarifado almoxarifado;

	private FccCentroCustos centroCustos;

	private DominioSimNao central;

	private DominioSimNao calculaMediaPonderada;

	private DominioSimNao bloqEntTransf;

	private String processo;

	private boolean reinicia;

	private boolean modoEdicao = Boolean.FALSE;
	
	private SceAlmoxarifado parametroSelecionado;
	
	@Inject
	private ManterAlmoxarifadoController manterAlmoxarifadoController;
	

	public void inicio() {
	 

		if (!isModoEdicao() && !this.getDataModel().getPesquisaAtiva()) {
			inicializarCampos();
		}

		if (this.reinicia) {
			this.getDataModel().reiniciarPaginator();
			this.reinicia = false;
		}
	
	}

	private void limparDominioSimNao() {
		setCentral(null);
		setCalculaMediaPonderada(null);
		setBloqEntTransf(null);
	}

	public void atualizarAlmoxarifado(final Short seq, final String processoSelecionado) {
		this.processo = processoSelecionado;
		try {
			if (StringUtils.isNotBlank(getProcesso())) {
				this.estoqueFacade.atualizarProcessoAlmoxarifado(seq, processoSelecionado);
				apresentarMsgNegocio(Severity.INFO, ManterAlmoxarifadoBusinessExceptionCode.ALMOXARIFADO_ALTERADO_COM_SUCESSO.toString(), estoqueFacade.obterAlmoxarifadoPorId(seq).getSeqDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.processo = null;
			this.getDataModel().reiniciarPaginator();
		}
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String parametro) throws BaseException {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(parametro),pesquisarCentroCustoCount(parametro));
	}

	public Integer pesquisarCentroCustoCount(String param) {
		return centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricaoCount(param);
	}

	public String criarNovoAlmoxarifado() {
		return PAGE_ESTOQUE_MANTER_ALMOXARIFADO;		
	}

	public String pesquisar() {

		String retorno = null;

		if (getCentral() != null) {
			getAlmoxarifado().setIndCentral(getCentral().isSim());
		} else {
			getAlmoxarifado().setIndCentral(null);
		}

		if (getCalculaMediaPonderada() != null) {
			getAlmoxarifado().setIndCalculaMediaPonderada(getCalculaMediaPonderada().isSim());
		} else {
			getAlmoxarifado().setIndCalculaMediaPonderada(null);
		}

		if (getBloqEntTransf() != null) {
			getAlmoxarifado().setIndBloqEntrTransf(getBloqEntTransf().isSim());
		} else {
			getAlmoxarifado().setIndBloqEntrTransf(null);
		}

		if (getCentroCustos() != null) {
			getAlmoxarifado().setCentroCusto(getCentroCustos());
		} else {
			getAlmoxarifado().setCentroCusto(null);
		}
		this.getDataModel().reiniciarPaginator();

		if (recuperarCount() == 1L) {
			manterAlmoxarifadoController.setSeq(getSeq());			
			retorno = PAGE_ESTOQUE_MANTER_ALMOXARIFADO;

		}
		return retorno;
	}

	@SuppressWarnings("unchecked")
	public Short getSeq() {		
		List<SceAlmoxarifado> listaAlmoxarifado = recuperarListaPaginada(0, 10, null, true);
		
		if(listaAlmoxarifado != null && listaAlmoxarifado.size() > 0) {
			return listaAlmoxarifado.get(0).getSeq();
		}
		
		return null;
	}

	public void limpar() {
		inicializarAntesReniciarPaginator();
		this.getDataModel().reiniciarPaginator();
		inicializarDepoisReniciarPaginator();
	}

	private void inicializarCampos() {
		limparDominioSimNao();
		limparDadosCentroCusto();
		setProcesso(null);
		setAlmoxarifado(new SceAlmoxarifado());
	}

	private void inicializarDepoisReniciarPaginator() {
		this.getDataModel().setPesquisaAtiva(Boolean.FALSE);
	}

	private void inicializarAntesReniciarPaginator() {
		inicializarCampos();
	}

	public void limparDadosCentroCusto() {
		setCentroCustos(null);
	}

	@Override
	public Long recuperarCount() {

		Short codigo = getAlmoxarifado().getSeq();
		String descricao = getAlmoxarifado().getDescricao();
		Integer codigoCentroCustos = getCentroCustos() != null ? getCentroCustos().getCodigo() : null;
		Integer diasEstoqueMinimo = getAlmoxarifado().getDiasEstqMinimo();
		Boolean central = getAlmoxarifado().getIndCentral();
		Boolean calculaMediaPonderada = getAlmoxarifado().getIndCalculaMediaPonderada();
		Boolean bloqueiaEntTransf = getAlmoxarifado().getIndBloqEntrTransf();
		DominioSituacao situacao = getAlmoxarifado().getIndSituacao();

		return this.estoqueFacade.listarAlmoxarifadosCount(codigo, descricao, codigoCentroCustos, diasEstoqueMinimo, central, calculaMediaPonderada, bloqueiaEntTransf, situacao);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		Short codigo = getAlmoxarifado().getSeq();
		String descricao = getAlmoxarifado().getDescricao();
		Integer codigoCentroCustos = getCentroCustos() != null ? getCentroCustos().getCodigo() : null;
		Integer diasEstoqueMinimo = getAlmoxarifado().getDiasEstqMinimo();
		Boolean central = getAlmoxarifado().getIndCentral();
		Boolean calculaMediaPonderada = getAlmoxarifado().getIndCalculaMediaPonderada();
		Boolean bloqueiaEntTransf = getAlmoxarifado().getIndBloqEntrTransf();
		DominioSituacao situacao = getAlmoxarifado().getIndSituacao();

		List<SceAlmoxarifado> result = this.estoqueFacade.listarAlmoxarifados(firstResult, maxResult, orderProperty, asc, codigo, descricao, codigoCentroCustos, diasEstoqueMinimo, central,
				calculaMediaPonderada, bloqueiaEntTransf, situacao);

		if (result == null) {
			result = new ArrayList<SceAlmoxarifado>();
		}

		return result;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public DominioSimNao[] getDominioSimNao() {
		DominioSimNao[] dom = { DominioSimNao.S, DominioSimNao.N };
		return dom;
	}

	public DominioSimNao getCentral() {
		return central;
	}

	public void setCentral(DominioSimNao central) {
		this.central = central;
	}

	public DominioSimNao getCalculaMediaPonderada() {
		return calculaMediaPonderada;
	}

	public void setCalculaMediaPonderada(DominioSimNao calculaMediaPonderada) {
		this.calculaMediaPonderada = calculaMediaPonderada;
	}

	public DominioSimNao getBloqEntTransf() {
		return bloqEntTransf;
	}

	public void setBloqEntTransf(DominioSimNao bloqEntTransf) {
		this.bloqEntTransf = bloqEntTransf;
	}

	public String truncarTexto(String texto) {
		return StringUtils.abbreviate(texto, 20);
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isReinicia() {
		return reinicia;
	}

	public void setReinicia(boolean reinicia) {
		this.reinicia = reinicia;
	}

	public DynamicDataModel<SceAlmoxarifado> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceAlmoxarifado> dataModel) {
		this.dataModel = dataModel;
	}
	
	public SceAlmoxarifado getParametroSelecionado() {
		return parametroSelecionado;
	}
	
	public void setParametroSelecionado(SceAlmoxarifado parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}