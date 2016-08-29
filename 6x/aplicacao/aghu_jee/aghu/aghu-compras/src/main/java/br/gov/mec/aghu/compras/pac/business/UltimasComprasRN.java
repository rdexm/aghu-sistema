package br.gov.mec.aghu.compras.pac.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class UltimasComprasRN extends BaseBusiness {

	private static final long serialVersionUID = -5922834917785524834L;
	
	private static final Log LOG = LogFactory.getLog(UltimasComprasRN.class);
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;

	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMaterias(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,String modl, Date DataNrInicial, Integer matCodigo, boolean historico) {
		List<ScoUltimasComprasMaterialVO> ultimasComprasAux = new ArrayList<ScoUltimasComprasMaterialVO> (); 
		if(historico){
			ultimasComprasAux = pesquisarUltimasComprasMateriasHistorico(firstResult, maxResult, orderProperty, asc, modl, matCodigo);
		}else{
			ultimasComprasAux = pesquisarUltimasComprasMateriasSemHistorico(firstResult, maxResult, orderProperty, asc, modl, DataNrInicial, matCodigo, historico);
		}
		return ultimasComprasAux;
	}
	
	
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMateriasSemHistorico(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,String modl, Date DataNrInicial, Integer matCodigo, boolean historico) {
		List<ScoUltimasComprasMaterialVO> ultimasComprasAux = new ArrayList<ScoUltimasComprasMaterialVO> ();
		List<ScoUltimasComprasMaterialVO> ultimasCompras = new ArrayList<ScoUltimasComprasMaterialVO> ();
		ultimasCompras.addAll(scoLicitacaoDAO.pesquisarUltimasComprasMaterias(firstResult, maxResult, orderProperty, asc, modl, DataNrInicial, matCodigo));
		for (ScoUltimasComprasMaterialVO scoUltimasComprasMaterialVO : ultimasCompras) {
			ScoUltimasComprasMaterialVO ultimaCompra = new ScoUltimasComprasMaterialVO ();
			ultimaCompra= scoLicitacaoDAO.pesquisarUltimasComprasMateriasLicitacao(scoUltimasComprasMaterialVO.getNroAF());
			scoUltimasComprasMaterialVO.setModlDesc(ultimaCompra.getModlDesc());
			scoUltimasComprasMaterialVO.setModl(ultimaCompra.getModl());
			scoUltimasComprasMaterialVO.setNroPAC(ultimaCompra.getNroPAC());
			scoUltimasComprasMaterialVO.setDtAbertura(ultimaCompra.getDtAbertura());
			scoUltimasComprasMaterialVO.setInciso(ultimaCompra.getInciso());
			ultimasComprasAux.add(scoUltimasComprasMaterialVO);
		}
		return ultimasComprasAux;
	}
	
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMateriasHistorico(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,String modl,  Integer matCodigo) {
		List<ScoUltimasComprasMaterialVO> ultimasComprasAux = new ArrayList<ScoUltimasComprasMaterialVO> (); 
		List<ScoUltimasComprasMaterialVO> ultimasCompras = new ArrayList<ScoUltimasComprasMaterialVO> ();
		ultimasCompras.addAll(estoqueFacade.pesquisarUltimasComprasMateriasHistorico(firstResult, maxResult, orderProperty, asc, modl, matCodigo));
		for (ScoUltimasComprasMaterialVO scoUltimasComprasMaterialVO : ultimasCompras) {
			ScoUltimasComprasMaterialVO ultimaCompra = new ScoUltimasComprasMaterialVO ();
			ultimaCompra= scoLicitacaoDAO.pesquisarUltimasComprasMateriasLicitacao(scoUltimasComprasMaterialVO.getNroAF());
			scoUltimasComprasMaterialVO.setModlDesc(ultimaCompra.getModlDesc());
			scoUltimasComprasMaterialVO.setModl(ultimaCompra.getModl());
			scoUltimasComprasMaterialVO.setNroPAC(ultimaCompra.getNroPAC());
			scoUltimasComprasMaterialVO.setDtAbertura(ultimaCompra.getDtAbertura());
			ultimasComprasAux.add(scoUltimasComprasMaterialVO);
		}
		return ultimasComprasAux;
	}
	
	public Long pesquisarUltimasComprasMateriasCount(String modl, Date DataNrInicial, Integer matCodigo, boolean historico) {
		Long count = Long.valueOf("0");
		if(historico){
			count = estoqueFacade.pesquisarUltimasComprasMateriasHistoricoCount(modl, matCodigo);
		}else{
			count = scoLicitacaoDAO.pesquisarUltimasComprasMateriasCount(modl, DataNrInicial, matCodigo);
		}
		return count;
	}
	
	
	
	public List<String> obterEmailsFornecedor(Integer numeroFornecedor){
		List<String> email = new ArrayList<String>();
		if(numeroFornecedor!=null){
			ScoFornecedor fornecedor = scoFornecedorDAO.obterFornecedorPorNumero(numeroFornecedor);
			email = scoContatoFornecedorDAO.obterContatosPorEmailFornecedor(fornecedor);
		}
		return email;
	}
	


	@Override
	protected Log getLogger() {
		return LOG;
	}
	
}
