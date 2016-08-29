package br.gov.mec.aghu.compras.pac.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioAgrupadorItemFornecedorMarca;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVOPai;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class RelatorioUltimasComprasPACON extends BaseBusiness implements Serializable{

	private static final Log LOG = LogFactory.getLog(RelatorioUltimasComprasPACON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IComprasFacade comprasFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216766104835407921L;
		

	/**
	 * Cria uma lista de itens para o relatório de ultimas compras do material.
	 * @param numLicitacao
	 * @param itens
	 * @return
	 * @author bruno.mourao 
	 * @since 07/06/2011
	 */
	public List<RelUltimasComprasPACVOPai> gerarRelatorioUltimasComprasPAC(Integer numLicitacao, List<Integer> itens, List<String> itensModalidade, Integer qtdeUltimasCompras, DominioAgrupadorItemFornecedorMarca agrupador){
		
		List<RelUltimasComprasPACVO> result = new ArrayList<RelUltimasComprasPACVO>();
		
		
		result = getComprasFacade().obterItensRelatorioUltimasComprasMaterial(numLicitacao,converterItensToShort(itens), itensModalidade, agrupador);
		//result= this.buscarUltimasComprasPAC(numLicitacao, itens, itensModalidade, qtdeUltimasCompras);
		
		//Map<Object, List<RelUltimasComprasPACVO>> mapVo = agruparCompras(result, agrupador);
		
		Map<Short, List<RelUltimasComprasPACVO>> mapVo = agruparComprasMesmoItem(result);
		
		//Cria lista pai com cada agrupamento
		List<RelUltimasComprasPACVOPai> resultPai = new ArrayList<RelUltimasComprasPACVOPai>();
		for(Object key : mapVo.keySet()){
			
			RelUltimasComprasPACVOPai pai = new RelUltimasComprasPACVOPai();
			
			//Pega o item
			List<RelUltimasComprasPACVO> lista = mapVo.get(key);
			
			if(lista != null && !lista.isEmpty()){
				
				pai.setNroItem(lista.get(0).getNroItem());
				pai.setCodMaterial(lista.get(0).getCodMaterial());
				pai.setDescMaterial(lista.get(0).getDescMaterial());
				pai.setDescUnidade(lista.get(0).getDescUnidade());

				List<RelUltimasComprasPACVO> novaLista = obterUltimasCompras(lista,qtdeUltimasCompras);				
				
				//Adiciona as compras na lista
				pai.setCompras(novaLista);				
				
				
			}
			
			resultPai.add(pai);
		}
		
		return resultPai;
	}

	public List<RelUltimasComprasPACVO> buscarUltimasComprasPAC(Integer numLicitacao, List<Integer> itens, List<String> itensModalidade, Integer qtdRegistros, DominioAgrupadorItemFornecedorMarca tipoAgrupamento){
		List<RelUltimasComprasPACVO> result = getComprasFacade().obterItensRelatorioUltimasComprasMaterial(numLicitacao, converterItensToShort(itens), itensModalidade , tipoAgrupamento);
		
		if (DominioAgrupadorItemFornecedorMarca.FORNECEDOR.equals(tipoAgrupamento)){
			return agruparPorFornecedor(result,qtdRegistros);
		} else if (DominioAgrupadorItemFornecedorMarca.MARCA.equals(tipoAgrupamento)){
			return agruparPorMarca(result,qtdRegistros);
		} else {
			return result;
		}		
	}
	
	public List<RelUltimasComprasPACVO> agruparPorFornecedor(List<RelUltimasComprasPACVO> result, Integer qtdRegistros){
		
		List<RelUltimasComprasPACVO> retorno  = new ArrayList<RelUltimasComprasPACVO>();
		
		if (result != null && result.size() >0){
			String descrFornecedor =  result.get(0).getDescFornecedor();
			Short nroItem = result.get(0).getNroItem();
			Integer count = 0;			
			
			for(RelUltimasComprasPACVO linha : result){
				
				if ((descrFornecedor.equals(linha.getDescFornecedor()) &&
					 nroItem.equals(linha.getNroItem()))){
					if (count < qtdRegistros){
					    retorno.add(linha);		
					}		
					
				}
				else {		
				
					count = 0;
					retorno.add(linha);
					
				}
				
				descrFornecedor = linha.getDescFornecedor();
				nroItem = linha.getNroItem();
				count++;
				
			}
		}		
		return retorno;
	}
	
   public List<RelUltimasComprasPACVO> agruparPorMarca(List<RelUltimasComprasPACVO> result, Integer qtdRegistros){
		
		List<RelUltimasComprasPACVO> retorno  = new ArrayList<RelUltimasComprasPACVO>();
		
		if (result != null && result.size() >0){
			String descrMarca=  result.get(0).getDescMarca();
			Short nroItem = result.get(0).getNroItem();
			Integer count = 0;			
			
			for(RelUltimasComprasPACVO linha : result){
				
				if ((descrMarca.equals(linha.getDescMarca()) &&
					 nroItem.equals(linha.getNroItem()))){
					if (count < qtdRegistros){
					    retorno.add(linha);		
					}		
					
				}
				else {		
				
					count = 0;
					retorno.add(linha);
					
				}
				
				descrMarca = linha.getDescMarca();
				nroItem = linha.getNroItem();
				count++;
				
			}
		}		
		return retorno;
	}
	
	/**
	 * @param itens
	 * @author bruno.mourao
	 * @since 10/06/2011
	 */
	public List<Short> converterItensToShort(List<Integer> itens) {
		if(itens == null || itens.isEmpty()){
			return null;
		}
		else{
			//Converte os itens de inteiro pra short
			List<Short> itensShort = new ArrayList<Short>();
			for(Integer item : itens){
				itensShort.add(item.shortValue());
			}
			return itensShort;
		}
	}	
	

	/* @param result
	 * @return
	 * @author bruno.mourao
	 * @since 22/06/2011
	 */
	private Map<Short, List<RelUltimasComprasPACVO>> agruparComprasMesmoItem(
			List<RelUltimasComprasPACVO> result) {
		Map<Short, List<RelUltimasComprasPACVO>> mapVo = new TreeMap<Short, List<RelUltimasComprasPACVO>>();
		
		//Para cada item, agrupa as compras do mesmo item
		for(RelUltimasComprasPACVO vo : result){
			List<RelUltimasComprasPACVO> lista;
			if(mapVo.containsKey(vo.getNroItem())){
				lista = mapVo.get(vo.getNroItem());
				lista.add(vo);
			}
			else{
				lista = new ArrayList<RelUltimasComprasPACVO>();
				lista.add(vo);
				mapVo.put(vo.getNroItem(), lista);
			}
		}
		return mapVo;
	}
	
	
	
	
	
	/**
	 * @param lista
	 * @return
	 * @author bruno.mourao
	 * @since 22/06/2011
	 */
	private List<RelUltimasComprasPACVO> obterUltimasCompras(
			List<RelUltimasComprasPACVO> lista, Integer qtdeUltimasCompras) {
		
		//Ordena pelo número de solicitacao	    
		/*Collections.sort(lista, new Comparator<RelUltimasComprasPACVO>() {
			@Override
			public int compare(RelUltimasComprasPACVO arg0,
					RelUltimasComprasPACVO arg1) {				
				return arg0.getNroSolicitacao().compareTo(arg1.getNroSolicitacao());
			}
		});*/
		
		//Collections.sort(lista);
		
		//Seleciona somente as 3 ultimas compras
		List<RelUltimasComprasPACVO> novaLista = new ArrayList<RelUltimasComprasPACVO>();
		Iterator<RelUltimasComprasPACVO> iterator = lista.iterator();
		int incluidos =0;
		while(iterator.hasNext() && incluidos < qtdeUltimasCompras){
			novaLista.add(iterator.next());
			incluidos++;
		}
		return novaLista;
	}

	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	
}
