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

import br.gov.mec.aghu.dominio.DominioProcedimentoCustoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoCompetencia;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CalculoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemProcedHospVO;
import br.gov.mec.aghu.sig.dao.VSigCustosCalculoProcedDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class VisualizarCustoPacienteProcedimentoON extends BaseBusiness{

	private static final long serialVersionUID = 1401789463336146208L;
	
	private static final Log LOG = LogFactory.getLog(VisualizarCustoPacienteProcedimentoON.class);
	
	@Inject
	private VSigCustosCalculoProcedDAO sigCustosCalculoProcedDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum VisualizarCustoPacienteProcedimentoONExceptionCode implements
		BusinessExceptionCode{
		PACIENTE_CUSTO_MENSAGEM_PROCEDIMENTO_DUPLICADO
	}
	
	
	/**
	 * Adiciona um procedimento na lista
	 * 
	 * @param listaProcedimento
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 */
	public void adicionarProcedimentoNaLista(List<ItemProcedHospVO> listaProcedimento, ItemProcedHospVO procedimento) throws ApplicationBusinessException{
		
		if(procedimento == null){
			return;
		}
		
		boolean procedimentoDuplicado = false;
		
		if(listaProcedimento == null){
			listaProcedimento = new ArrayList<ItemProcedHospVO>();
		}
		
		for (ItemProcedHospVO procedimentoInt : listaProcedimento) {
			if(procedimentoInt.getIphPhoSeq().equals(procedimento.getIphSeq())&&procedimentoInt.getIphSeq().equals(procedimento.getIphSeq())){
				procedimentoDuplicado = true;
				break;
			}
		}
	
		if(!procedimentoDuplicado){
			listaProcedimento.add(procedimento);
		}  else {
			throw new ApplicationBusinessException(VisualizarCustoPacienteProcedimentoONExceptionCode.PACIENTE_CUSTO_MENSAGEM_PROCEDIMENTO_DUPLICADO);
		}
	}

	/**
	 * Deleta o procedimento da lista
	 * 
	 * @param listaProcedimento
	 * @param procedimento
	 */
	public void deletarProcedimentoDaLista(List<ItemProcedHospVO> listaProcedimento, ItemProcedHospVO procedimento) {
		if(listaProcedimento != null){
			for(int i = 0; i < listaProcedimento.size(); i++){
				if(listaProcedimento.get(i).getIphPhoSeq().equals(procedimento.getIphPhoSeq())&&listaProcedimento.get(i).getIphSeq().equals(procedimento.getIphSeq())){
					listaProcedimento.remove(i);
					return;
				}
			}
		}
	}
	
	public List<CalculoProcedimentoVO> pesquisarDiagnosticosSegundoNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Short iphPhoSeq, Integer iphSeq, List<ItemProcedHospVO> listaProcedimento, Integer quantidadeProcedimentos, DominioProcedimentoCustoPaciente tipo, List<Short> conveniosSelecionados) {
		  List<CalculoProcedimentoVO> lista =  this.getSigCustosCalculoProcedDAO().buscarProcedimentosSegundoNivel(processamento, tipoCompetencia, iphPhoSeq, iphSeq, listaProcedimento, quantidadeProcedimentos, tipo, conveniosSelecionados);
		  List<CalculoProcedimentoVO> listaSegundoNivel = new ArrayList<CalculoProcedimentoVO>();
		  List<CalculoProcedimentoVO> listaSegundoNivelPorDescricao = new ArrayList<CalculoProcedimentoVO>();
		  Map<Integer, List<CalculoProcedimentoVO>> mapProcedimentoPorAtendimento = new LinkedHashMap<Integer, List<CalculoProcedimentoVO>>();
		  
		  for(CalculoProcedimentoVO vo: lista){
			  if(!mapProcedimentoPorAtendimento.containsKey(vo.getAtdSeq())){
					List<CalculoProcedimentoVO> listaNodo = new ArrayList<CalculoProcedimentoVO>();
					listaNodo.add(vo);
					mapProcedimentoPorAtendimento.put(vo.getAtdSeq(), listaNodo);
				} else if (mapProcedimentoPorAtendimento.containsKey(vo.getAtdSeq())) {
					List<CalculoProcedimentoVO> listaNodo = mapProcedimentoPorAtendimento.get(vo.getAtdSeq());
					listaNodo.add(vo);
				} 
		  }
		  
		  for (Map.Entry<Integer, List<CalculoProcedimentoVO>> entry : mapProcedimentoPorAtendimento.entrySet()){
				List<CalculoProcedimentoVO> listaNodos = entry.getValue();
				CalculoProcedimentoVO primeiroNodo = listaNodos.get(0);
				String descricao=primeiroNodo.getDescricao();
				for(CalculoProcedimentoVO nodo: listaNodos){
					if(StringUtils.isNotEmpty(nodo.getDescricaoSecundario()) && !primeiroNodo.getDescricao().equals(nodo.getDescricaoSecundario())){
						descricao = descricao.concat("+"+nodo.getDescricaoSecundario());	
					}
				}
				primeiroNodo.setDescricao(descricao);
				listaSegundoNivel.add(primeiroNodo);		

		  }
		  
		 return this.pesquisarProcedimentosSegundoNivelDescricao(listaSegundoNivel, mapProcedimentoPorAtendimento, listaSegundoNivelPorDescricao);
	}
	
	public List<CalculoProcedimentoVO> pesquisarProcedimentosSegundoNivelDescricao(List<CalculoProcedimentoVO> listaSegundoNivel, Map<Integer, List<CalculoProcedimentoVO>> mapProcedimentoPorAtendimento,  List<CalculoProcedimentoVO> listaSegundoNivelPorDescricao) {
		 
		 Map<String, List<CalculoProcedimentoVO>> mapProcedimentoPorDescricao = new LinkedHashMap<String, List<CalculoProcedimentoVO>>();
		  
		 for(CalculoProcedimentoVO vo: listaSegundoNivel){
			  if(!mapProcedimentoPorDescricao.containsKey(vo.getDescricao())){
					List<CalculoProcedimentoVO> listaNodo = new ArrayList<CalculoProcedimentoVO>();
					listaNodo.add(vo);
					mapProcedimentoPorDescricao.put(vo.getDescricao(), listaNodo);
				} else if (mapProcedimentoPorAtendimento.containsKey(vo.getAtdSeq())) {
					List<CalculoProcedimentoVO> listaNodo = mapProcedimentoPorDescricao.get(vo.getDescricao());
					listaNodo.add(vo);
				} 
		  }
		  
		  for (Map.Entry<String, List<CalculoProcedimentoVO>> entry : mapProcedimentoPorDescricao.entrySet()){
				List<CalculoProcedimentoVO> listaNodos = entry.getValue();
				CalculoProcedimentoVO primeiroNodo = listaNodos.get(0);
				Long quantidade = 0L;
				BigDecimal receitaTotal=BigDecimal.ZERO;
				BigDecimal custoTotal=BigDecimal.ZERO;
				List<Integer> listaAtdSeq = new ArrayList<Integer>();
				for(CalculoProcedimentoVO nodo: listaNodos){
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
	
	public VSigCustosCalculoProcedDAO getSigCustosCalculoProcedDAO() {
		return sigCustosCalculoProcedDAO;
	}

	public void setSigCustosCalculoProcedDAO(
			VSigCustosCalculoProcedDAO sigCustosCalculoProcedDAO) {
		this.sigCustosCalculoProcedDAO = sigCustosCalculoProcedDAO;
	}
}
