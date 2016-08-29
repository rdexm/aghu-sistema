package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class PesoObjetoCustoPaginatorController extends ActionController implements ActionPaginator {

	private static final String PESO_OBJETO_CUSTO_CRUD = "pesoObjetoCustoCRUD";
	private static final long serialVersionUID = -979705412934731570L;
	private static final Log LOG = LogFactory.getLog(PesoObjetoCustoPaginatorController.class);

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICentroCustoFacade centroCustosFacade;
	
	@Inject @Paginator
	private DynamicDataModel<FccCentroCustos> dataModel;

	private SigCentroProducao centroProducao;
	private DominioTipoCentroProducaoCustos tipo;

	private String descricao;
	private DominioSituacao situacao;

	private Integer codigoCentroCusto;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (this.getCentroProducao() == null) {
			FccCentroCustos centroCusto = null;
			try {
				RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				if (servidor.getCentroCustoAtuacao() != null) {
					centroCusto = servidor.getCentroCustoAtuacao();
				} else {
					centroCusto = servidor.getCentroCustoLotacao();
				}
			} catch (ApplicationBusinessException e) {
				centroCusto = null;
			}

			if (centroCusto != null) {
				this.setCentroProducao(centroCusto.getCentroProducao());
			}
		}

		if (this.getSituacao() == null) {
			this.setSituacao(DominioSituacao.A);
		}
	
	}

	public String configurarRateio() {
		return PESO_OBJETO_CUSTO_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return centroCustosFacade.pesquisarCentroCustoCount(this.getCentroProducao(), this.getTipo(), this.getDescricao(), this.getSituacao());
	}

	@Override
	public List<FccCentroCustos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return centroCustosFacade.pesquisarCentroCusto(firstResult, maxResult, orderProperty, asc, this.getCentroProducao(), this.getTipo(),
				this.getDescricao(), this.getSituacao());
	}

	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	
	
	public void pesquisar() {
		reiniciarPaginator();
	}

	public void limpar() {
		this.setCentroProducao(null);
		this.setTipo(null);
		this.setDescricao(null);
		this.setSituacao(null);
		this.setAtivo(false);
	}
	
	public List<SigCentroProducao> listarCentroProducao() {
		return this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao();
	}

	public SigCentroProducao getCentroProducao() {
		return centroProducao;
	}

	public void setCentroProducao(SigCentroProducao centroProducao) {
		this.centroProducao = centroProducao;
	}

	public DominioTipoCentroProducaoCustos getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoCentroProducaoCustos tipo) {
		this.tipo = tipo;
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

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}
	
	public DynamicDataModel<FccCentroCustos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<FccCentroCustos> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
}
