package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseTabCustosObjetosCustoVO;


public class CustoIndiretoPaginatorController extends ActionController {
	
	

	private static final Log LOG = LogFactory.getLog(CustoIndiretoPaginatorController.class);

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	private static final long serialVersionUID = -8346981833567418418L;
	
	private List<VisualizarAnaliseTabCustosObjetosCustoVO> lista;
	private Integer seqCompetencia;
	private Integer seqObjetoCustoVersao;
	private Integer seqCentroCusto;
	private DominioTipoVisaoAnalise tipoVisaoAnaliseItens;
	
	private Integer codigoDebita;
	private List<VisualizarAnaliseTabCustosObjetosCustoVO> listaIteracoes;
	private String tituloModalIteracoes;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
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
		 this.lista = this.custosSigCadastrosBasicosFacade.buscarMovimentosIndiretos(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto, this.tipoVisaoAnaliseItens, null);
	}
	
	public  void visualizarIteracoes(){
		this.listaIteracoes =  this.custosSigCadastrosBasicosFacade.buscarMovimentosIndiretos(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto, this.tipoVisaoAnaliseItens, this.codigoDebita);
		
		if(listaIteracoes != null && !listaIteracoes.isEmpty()){
			this.tituloModalIteracoes = listaIteracoes.get(0).getDescricao()+ " - " + listaIteracoes.get(0).getNome();
		}
		else{
			this.tituloModalIteracoes = null;
		}
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

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
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

	public List<VisualizarAnaliseTabCustosObjetosCustoVO> getListaIteracoes() {
		return listaIteracoes;
	}

	public void setListaIteracoes(List<VisualizarAnaliseTabCustosObjetosCustoVO> listaIteracoes) {
		this.listaIteracoes = listaIteracoes;
	}

	public Integer getCodigoDebita() {
		return codigoDebita;
	}

	public void setCodigoDebita(Integer codigoDebita) {
		this.codigoDebita = codigoDebita;
	}

	public String getTituloModalIteracoes() {
		return tituloModalIteracoes;
	}

	public void setTituloModalIteracoes(String tituloModalIteracoes) {
		this.tituloModalIteracoes = tituloModalIteracoes;
	}
}
