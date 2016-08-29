package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoPorCentroCustoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisarObjetoCustoCentroCustoPaginatorController extends ActionController implements ActionPaginator {

	private static final String VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS_CENTRO_CUSTO = "visualizarObjetosCustoAssociadosCentroCusto";

	@Inject @Paginator
	private DynamicDataModel<ObjetoCustoPorCentroCustoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisarObjetoCustoCentroCustoPaginatorController.class);

	private static final long serialVersionUID = 3304329435979836330L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private FccCentroCustos centroCusto;
	private SigCentroProducao centroProducao;
	private DominioSimNao indPossuiObjCusto;
	private DominioSimNao indPossuiComposicao;
	private DominioSituacao indSituacao;
	private Integer cctCodigo;
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (this.getCentroProducao() == null) {
			this.setCentroProducao(new SigCentroProducao());
		}
		
		// Seleciona o default para o combo "centro produção": centro de
		// produção do centro de custo do usuário logado
		if (this.getCentroProducao() == null) {
			FccCentroCustos centroCusto = null;
			try {
				if (this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getCentroCustoAtuacao() != null) {
					centroCusto = this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getCentroCustoAtuacao();
				} else {
					centroCusto = this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getCentroCustoLotacao();
				}
			} catch (ApplicationBusinessException e) {
				centroCusto = null;
			}
			// centro de produção
			if (centroCusto != null) {
				this.setCentroProducao(centroCusto.getCentroProducao());
			}
		}

		this.setIndSituacao(DominioSituacao.A);
	
	}

	public void pesquisarObjetoCustoCentroCusto() {
		this.reiniciarPaginator();
	}

	public void limparObjetoCustoCentroCusto() {
		this.setCentroCusto(null);
		this.setCentroProducao(null);
		this.setIndPossuiObjCusto(null);
		this.setIndSituacao(null);
		this.setIndPossuiComposicao(null);
		this.setAtivo(false);
		this.iniciar();
	}

	public String visualizarObjetosCustoAssociados() {
		return VISUALIZAR_OBJETOS_CUSTO_ASSOCIADOS_CENTRO_CUSTO;
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
	}

	public List<SigCentroProducao> listarCentroProducao() {
		return this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao();
	}
	
	public void limparCentroCusto() {
		this.setCentroCusto(null);
	}

	
	@Override
	public Long recuperarCount() {
		return Long.valueOf(this.custosSigFacade.pesquisarObjetoCustoPorCentroCustoCount(this.getCentroCusto(), ((this.getIndPossuiObjCusto() == null) ? null : this.getIndPossuiObjCusto().isSim()), ((this.getIndPossuiComposicao() == null) ? null : this.getIndPossuiComposicao().isSim()), this.getCentroProducao(), this.getIndSituacao()));
	}

	@Override
	public List<ObjetoCustoPorCentroCustoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.custosSigFacade.pesquisarObjetoCustoPorCentroCusto(firstResult, maxResult, orderProperty, asc, this.getCentroCusto(), ((this.getIndPossuiObjCusto() == null) ? null : this.getIndPossuiObjCusto().isSim()), ((this.getIndPossuiComposicao() == null) ? null : this
				.getIndPossuiComposicao().isSim()), this.getCentroProducao(), this.getIndSituacao());
	}

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}

	public DominioSimNao getIndPossuiObjCusto() {
		return indPossuiObjCusto;
	}

	public void setIndPossuiObjCusto(DominioSimNao indPossuiObjCusto) {
		this.indPossuiObjCusto = indPossuiObjCusto;
	}

	public DominioSimNao getIndPossuiComposicao() {
		return indPossuiComposicao;
	}

	public void setIndPossuiComposicao(DominioSimNao indPossuiComposicao) {
		this.indPossuiComposicao = indPossuiComposicao;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public SigCentroProducao getCentroProducao() {
		return centroProducao;
	}

	public void setCentroProducao(SigCentroProducao centroProducao) {
		this.centroProducao = centroProducao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public DynamicDataModel<ObjetoCustoPorCentroCustoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ObjetoCustoPorCentroCustoVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
