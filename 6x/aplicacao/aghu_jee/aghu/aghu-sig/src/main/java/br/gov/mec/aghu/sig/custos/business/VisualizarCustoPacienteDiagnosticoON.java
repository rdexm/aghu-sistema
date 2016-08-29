package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioCidCustoPacienteDiagnostico;
import br.gov.mec.aghu.sig.custos.vo.VSigCustosCalculoCidVO;
import br.gov.mec.aghu.sig.dao.VSigCustosCalculoCidDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class VisualizarCustoPacienteDiagnosticoON  extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8251634605990940780L;

	/**
	 * 
	 */
	
	private static final Log LOG = LogFactory.getLog(VisualizarCustoPacienteDiagnosticoON.class);
	
	@Inject
	private VSigCustosCalculoCidDAO sigCustosCalculoCidDAO;
	
	private Map<Integer, List<VSigCustosCalculoCidVO>> mapDiagnosticoPorAtendimento;
	private Map<String, List<VSigCustosCalculoCidVO>> mapDiagnosticoPorDescricao;
	private List<VSigCustosCalculoCidVO> listaSegundoNivel;
	private List<VSigCustosCalculoCidVO> listaSegundoNivelPorDescricao;
	 
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public List<VSigCustosCalculoCidVO> pesquisarDiagnosticosSegundoNivel(Integer pmuSeq, Integer cidSeq, List<Integer> listaSeq, Integer quantidadeCids, DominioCidCustoPacienteDiagnostico tipo) {
		  List<VSigCustosCalculoCidVO> lista =  this.getSigCustosCalculoCidDAO().pesquisarDiagnosticosSegundoNivel(pmuSeq, cidSeq, listaSeq, quantidadeCids, tipo);
		  listaSegundoNivel = new ArrayList<VSigCustosCalculoCidVO>();
		  listaSegundoNivelPorDescricao = new ArrayList<VSigCustosCalculoCidVO>();
		  
		  if(lista!=null){
			  mapDiagnosticoPorAtendimento =  new LinkedHashMap<Integer, List<VSigCustosCalculoCidVO>>();
		  }
		  for(VSigCustosCalculoCidVO vo: lista){
			  if(!this.mapDiagnosticoPorAtendimento.containsKey(vo.getAtdSeq())){
					List<VSigCustosCalculoCidVO> listaNodo = new ArrayList<VSigCustosCalculoCidVO>();
					listaNodo.add(vo);
					this.mapDiagnosticoPorAtendimento.put(vo.getAtdSeq(), listaNodo);
				} else if (this.mapDiagnosticoPorAtendimento.containsKey(vo.getAtdSeq())) {
					List<VSigCustosCalculoCidVO> listaNodo = this.mapDiagnosticoPorAtendimento.get(vo.getAtdSeq());
					listaNodo.add(vo);
				} 
		  }
		  
		  for (Map.Entry<Integer, List<VSigCustosCalculoCidVO>> entry : mapDiagnosticoPorAtendimento.entrySet()){
				List<VSigCustosCalculoCidVO> listaNodos = entry.getValue();
				VSigCustosCalculoCidVO primeiroNodo = listaNodos.get(0);
				String descricao=primeiroNodo.getCidDescricao();
				for(VSigCustosCalculoCidVO nodo: listaNodos){
					if(StringUtils.isNotEmpty(nodo.getCidDescricaoSecundario())){
						descricao = descricao.concat("+"+nodo.getCidDescricaoSecundario());	
					}
				}
				primeiroNodo.setCidDescricao(descricao);
				listaSegundoNivel.add(primeiroNodo);		

		  }
		  
		 return this.pesquisarDiagnosticosSegundoNivelDescricao(pmuSeq);
	}
	
	public List<VSigCustosCalculoCidVO> pesquisarDiagnosticosSegundoNivelDescricao(Integer pmuSeq) {
		 if(listaSegundoNivel!=null){
			  mapDiagnosticoPorDescricao =  new LinkedHashMap<String, List<VSigCustosCalculoCidVO>>();
		  }
		  
		  for(VSigCustosCalculoCidVO vo: listaSegundoNivel){
			  if(!this.mapDiagnosticoPorDescricao.containsKey(vo.getCidDescricao())){
					List<VSigCustosCalculoCidVO> listaNodo = new ArrayList<VSigCustosCalculoCidVO>();
					listaNodo.add(vo);
					this.mapDiagnosticoPorDescricao.put(vo.getCidDescricao(), listaNodo);
				} else if (this.mapDiagnosticoPorAtendimento.containsKey(vo.getAtdSeq())) {
					List<VSigCustosCalculoCidVO> listaNodo = this.mapDiagnosticoPorDescricao.get(vo.getCidDescricao());
					listaNodo.add(vo);
				} 
		  }
		  
		  for (Map.Entry<String, List<VSigCustosCalculoCidVO>> entry : mapDiagnosticoPorDescricao.entrySet()){
				List<VSigCustosCalculoCidVO> listaNodos = entry.getValue();
				VSigCustosCalculoCidVO primeiroNodo = listaNodos.get(0);
				Long quantidade = 0L;
				BigDecimal receitaTotal=BigDecimal.ZERO;
				BigDecimal custoTotal=BigDecimal.ZERO;
				List<Integer> listaAtdSeq = new ArrayList<Integer>();
				for(VSigCustosCalculoCidVO nodo: listaNodos){
					if(nodo.getQuantidadeSecundario()!=0L){
						if(nodo.getQuantidadeSecundario()!=null){
							quantidade = quantidade + nodo.getQuantidadeSecundario();	
						}
						if(nodo.getReceitaTotalSecundario()!=null){
							receitaTotal = receitaTotal.add(nodo.getReceitaTotalSecundario());	
						}
						if(nodo.getCustoTotalSecundario()!=null){
							custoTotal = custoTotal.add(nodo.getCustoTotalSecundario());	
						}
					} else if(nodo.getQuantidade()!=0L) {
						if(nodo.getQuantidade()!=null){
							quantidade = quantidade+nodo.getQuantidade();	
						}
						if(nodo.getReceitaTotal()!=null){
							receitaTotal = receitaTotal.add(nodo.getReceitaTotal());	
						} 
						if(nodo.getCustoTotal()!=null){
							custoTotal = custoTotal.add(nodo.getCustoTotal());	
						}
					}
					listaAtdSeq.add(nodo.getAtdSeq());
				}
				primeiroNodo.setQuantidade(quantidade);
				primeiroNodo.setReceitaTotal(receitaTotal);
				primeiroNodo.setCustoTotal(custoTotal);
				primeiroNodo.setListaAtdSeq(listaAtdSeq);
				primeiroNodo.setSegundoNivel(true);
				
				listaSegundoNivelPorDescricao.add(primeiroNodo);		
		  }
		  return listaSegundoNivelPorDescricao;
	}

	public VSigCustosCalculoCidDAO getSigCustosCalculoCidDAO() {
		return sigCustosCalculoCidDAO;
	}

	public void setSigCustosCalculoCidDAO(
			VSigCustosCalculoCidDAO sigCustosCalculoCidDAO) {
		this.sigCustosCalculoCidDAO = sigCustosCalculoCidDAO;
	}



		
}
