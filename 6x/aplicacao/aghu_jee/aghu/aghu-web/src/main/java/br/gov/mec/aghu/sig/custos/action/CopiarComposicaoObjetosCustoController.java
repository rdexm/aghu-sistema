package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class CopiarComposicaoObjetosCustoController extends ActionController {
	
	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final long serialVersionUID = 2150466627329253216L;

	public enum CopiarComposicaoObjetosCustoExceptionCode implements BusinessExceptionCode {
		MSG_COPIA_NENHUM_ITEM_SELECIONADO
	}

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private SigObjetoCustoVersoes objetoCustoVersao;

	private SigObjetoCustoVersoes objetoCustoVersaoSuggestion;

	private Integer seqObjetoCustoVersao;

	private List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes;

	private Map<SigObjetoCustoComposicoes, Boolean> objetoCustoComposicoesSelecionados;

	private Integer codigoCentroCusto;
	private FccCentroCustos fccCentroCustos;

	private Boolean stateCheckedBox;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.objetoCustoVersao = this.custosSigFacade.obterObjetoCustoVersoes(this.seqObjetoCustoVersao);
		this.fccCentroCustos = this.centroCustoFacade.obterCentroCustoPorChavePrimaria(this.codigoCentroCusto);
		this.stateCheckedBox = true;
	
	}

	public List<SigObjetoCustoVersoes> pesquisarComposicao(String paramPesquisa) throws BaseException {
		return this.custosSigFacade.pesquisarObjetoCustoVersoesAssistencial(this.fccCentroCustos, this.seqObjetoCustoVersao, paramPesquisa);
	}

	public void loadTable() {
		this.listaObjetoCustoComposicoes = new ArrayList<SigObjetoCustoComposicoes>();
		if (this.objetoCustoVersaoSuggestion != null) {
			List<SigObjetoCustoComposicoes> listResult = this.custosSigFacade.pesquisarComposicoesPorObjetoCustoVersaoAtivo(objetoCustoVersaoSuggestion
					.getSeq());
			if (listResult != null && !listResult.isEmpty()) {
				this.listaObjetoCustoComposicoes = listResult;
			}
		}
		this.objetoCustoComposicoesSelecionados = new HashMap<SigObjetoCustoComposicoes, Boolean>();
		this.atualizaCheckBoxList(true);

	}

	private void atualizaCheckBoxList(boolean bool) {
		for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : this.listaObjetoCustoComposicoes) {
			this.objetoCustoComposicoesSelecionados.put(sigObjetoCustoComposicoes, bool);
		}
	}

	public void seleciona() {
		this.atualizaCheckBoxList(this.getStateCheckedBox());
	}

	public void deleteTable() {
		this.objetoCustoComposicoesSelecionados = new HashMap<SigObjetoCustoComposicoes, Boolean>();
		this.setListaObjetoCustoComposicoes(null);
	}

	public String gravar() {
		try {
			List<SigObjetoCustoComposicoes> copia = new ArrayList<SigObjetoCustoComposicoes>();

			for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : this.listaObjetoCustoComposicoes) {
				if (this.objetoCustoComposicoesSelecionados.get(sigObjetoCustoComposicoes)) {
					copia.add(sigObjetoCustoComposicoes);
				}
			}

			if (copia == null || copia.isEmpty()) {
				throw new ApplicationBusinessException(CopiarComposicaoObjetosCustoExceptionCode.MSG_COPIA_NENHUM_ITEM_SELECIONADO);
			}

			this.custosSigFacade.copiaObjetoCustoComposicoes(this.objetoCustoVersao, copia);

			this.custosSigFacade.iniciaProcessoHistoricoCopiaObjetoCusto(this.objetoCustoVersao, this.objetoCustoVersaoSuggestion, copia,
					this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));

			this.apresentarMsgNegocio(Severity.INFO, "MSG_COPIA_COMPOSICAO_SUCESSO",
					this.objetoCustoVersaoSuggestion.getSigObjetoCustos().getNome(), this.objetoCustoVersao.getSigObjetoCustos().getNome());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		} 
		return this.voltar();
	}

	public String voltar() {
		this.objetoCustoVersaoSuggestion = null;
		this.deleteTable();
		return MANTER_OBJETOS_CUSTO;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public SigObjetoCustoVersoes getObjetoCustoVersao() {
		return objetoCustoVersao;
	}

	public void setObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao) {
		this.objetoCustoVersao = objetoCustoVersao;
	}

	public SigObjetoCustoVersoes getObjetoCustoVersaoSuggestion() {
		return objetoCustoVersaoSuggestion;
	}

	public void setObjetoCustoVersaoSuggestion(SigObjetoCustoVersoes objetoCustoVersaoSuggestion) {
		this.objetoCustoVersaoSuggestion = objetoCustoVersaoSuggestion;
	}

	public List<SigObjetoCustoComposicoes> getListaObjetoCustoComposicoes() {
		return listaObjetoCustoComposicoes;
	}

	public void setListaObjetoCustoComposicoes(List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes) {
		this.listaObjetoCustoComposicoes = listaObjetoCustoComposicoes;
	}

	public Map<SigObjetoCustoComposicoes, Boolean> getObjetoCustoComposicoesSelecionados() {
		return objetoCustoComposicoesSelecionados;
	}

	public void setObjetoCustoComposicoesSelecionados(Map<SigObjetoCustoComposicoes, Boolean> objetoCustoComposicoesSelecionados) {
		this.objetoCustoComposicoesSelecionados = objetoCustoComposicoesSelecionados;
	}

	public Boolean getStateCheckedBox() {
		return stateCheckedBox;
	}

	public void setStateCheckedBox(Boolean stateCheckedBox) {
		this.stateCheckedBox = stateCheckedBox;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

}
