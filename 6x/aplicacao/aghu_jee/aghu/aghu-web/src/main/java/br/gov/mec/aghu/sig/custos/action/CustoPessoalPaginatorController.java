package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;
import br.gov.mec.aghu.core.action.ActionController;


public class CustoPessoalPaginatorController extends ActionController {
   
	private List<VisualizarAnaliseTabCustosObjetosCustoVO> lista;

	private static final long serialVersionUID = 1934583712756763647L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	private Integer seqCompetencia;
	private Integer seqObjetoCustoVersao;
	private Integer seqCentroCusto;
	private DominioTipoVisaoAnalise tipoVisaoAnaliseItens;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciarAbaObjetoCusto(Integer seqCompetencia, Integer seqObjetoCustoVersao, Integer seqCentroCusto) {
		this.seqCompetencia = seqCompetencia;
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
		this.seqCentroCusto = seqCentroCusto;
		this.tipoVisaoAnaliseItens = DominioTipoVisaoAnalise.OBJETO_CUSTO;
		this.pesquisar();
	}

	public void iniciarAbaCentroCusto(Integer seqCompetencia, Integer seqCentroCusto) {
		this.seqCompetencia = seqCompetencia;
		this.seqCentroCusto = seqCentroCusto;
		this.tipoVisaoAnaliseItens = DominioTipoVisaoAnalise.CENTRO_CUSTO;
		this.pesquisar();
	}

	
	public void pesquisar(){
		 this.setLista(this.custosSigCadastrosBasicosFacade.buscarMovimentosPessoas(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto, this.tipoVisaoAnaliseItens));
	}

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
	}

	public Integer getSeqCompetencia() {
		return seqCompetencia;
	}

	public void setSeqCompetencia(Integer seqCompetencia) {
		this.seqCompetencia = seqCompetencia;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public DominioTipoVisaoAnalise getTipoVisaoAnaliseItens() {
		return tipoVisaoAnaliseItens;
	}

	public void setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		this.tipoVisaoAnaliseItens = tipoVisaoAnaliseItens;
	}

	public List<VisualizarAnaliseTabCustosObjetosCustoVO> getLista() {
		return lista;
	}

	public void setLista(List<VisualizarAnaliseTabCustosObjetosCustoVO> lista) {
		this.lista = lista;
	}
}
