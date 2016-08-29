package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.vo.DetalhamentoCustosGeralVO;
import br.gov.mec.aghu.core.action.ActionController;


public class CustoVisaoGeralPaginatorController extends ActionController {
	private List<DetalhamentoCustosGeralVO> lista;
	
	private static final Log LOG = LogFactory.getLog(CustoVisaoGeralPaginatorController.class);

	private static final long serialVersionUID = -8346981833567308418L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	private Integer pmuSeq;
	private Integer ocvSeq;
	private FccCentroCustos fccCentroCustos;
	private DominioTipoVisaoAnalise tipoVisaoAnaliseItens;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciarAbaVisaoGeral(int pmuSeq, int ocvSeq, FccCentroCustos fccCentroCustos) {
		this.pmuSeq = pmuSeq;
		this.ocvSeq = ocvSeq;
		this.fccCentroCustos = fccCentroCustos;
		this.tipoVisaoAnaliseItens = DominioTipoVisaoAnalise.OBJETO_CUSTO;
		this.pesquisar();
	}

	public void iniciarAbaVisaoGeralCentroCusto(int pmuSeq, FccCentroCustos fccCentroCustos) {
		this.pmuSeq = pmuSeq;
		this.fccCentroCustos = fccCentroCustos;
		this.tipoVisaoAnaliseItens = DominioTipoVisaoAnalise.CENTRO_CUSTO;
		this.pesquisar();
	}

	public void pesquisar() {
		lista = this.custosSigCadastrosBasicosFacade.buscarMovimentosGeral(this.pmuSeq, this.ocvSeq, this.fccCentroCustos, this.tipoVisaoAnaliseItens);
	}
	
	public Integer getPmuSeq() {
		return pmuSeq;
	}

	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public DominioTipoVisaoAnalise getTipoVisaoAnaliseItens() {
		return tipoVisaoAnaliseItens;
	}

	public void setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		this.tipoVisaoAnaliseItens = tipoVisaoAnaliseItens;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public List<DetalhamentoCustosGeralVO> getLista() {
		return lista;
	}

	public void setLista(List<DetalhamentoCustosGeralVO> lista) {
		this.lista = lista;
	}
}
