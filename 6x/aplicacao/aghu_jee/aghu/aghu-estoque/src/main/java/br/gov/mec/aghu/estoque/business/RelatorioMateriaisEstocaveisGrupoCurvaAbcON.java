package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisEstocaveisGrupoCurvaAbcVO;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioMateriaisEstocaveisGrupoCurvaAbcON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioMateriaisEstocaveisGrupoCurvaAbcON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@Inject
private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2382982587270875605L;

	
	public List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisCurvaAbc(boolean considerarCompras){
		Date dtCompetencia = getSceEstoqueGeralDAO().obterMaxDtCompetencia();
		List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> list = new ArrayList<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO>();
		if(considerarCompras){
			list = getComprasFacade().buscarRelatorioMaterialEstocaveisCurvaAbc(dtCompetencia);
		}else{
			list = getComprasFacade().buscarRelatorioMaterialEstocaveisSemComprasCurvaAbc(dtCompetencia);
		}
		
		//aplicar ordenação
		List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> listFormatada;
		Collections.sort(list);
		
		if(considerarCompras){
			listFormatada = formatarRelatorio(list);
		}else{
			listFormatada = list;
		}
		
		//converteParaXml(listFormatada);
		return listFormatada;
	}
	
	private List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> formatarRelatorio(
			List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> list) {
		List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> listAux = new ArrayList<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO>();
		String material = "";
		for( RelatorioMateriaisEstocaveisGrupoCurvaAbcVO vo : list){
			RelatorioMateriaisEstocaveisGrupoCurvaAbcVO voAux = new RelatorioMateriaisEstocaveisGrupoCurvaAbcVO();
			if(!material.equals(vo.getNomeMaterial())){
				material = vo.getNomeMaterial();
				voAux.setGrupo(vo.getGrupo());
				voAux.setAbc(vo.getAbc());
				voAux.setCodMat(vo.getCodMat());
				voAux.setNomeMaterial(vo.getNomeMaterial());
				listAux.add(voAux);
				//vo.setGrupo(vo.getGrupo());
				//vo.setAbc(vo.getAbc());
				vo.setCodMat("");
				vo.setNomeMaterial("");
				listAux.add(vo);
			}else{
				//vo.setGrupo(vo.getGrupo());
				//vo.setAbc(vo.getAbc());
				vo.setCodMat("");
				vo.setNomeMaterial("");
				listAux.add(vo);				
			}
		}
		
		return listAux;		
	}



	/**
	 * ORADB scec_busca_licitacao
	 * 
	 */
	@SuppressWarnings("ucd")
	public Integer buscarLicitacaoPorSlcNumero(Integer numero){
		List<ScoFaseSolicitacao> faseSolicitacao = getComprasFacade().pesquisarFaseSolicitacaoPorNumeroSolCompra(numero);
		if(faseSolicitacao != null && !faseSolicitacao.isEmpty()){
			if(faseSolicitacao.get(0).getItemLicitacao() != null) {
				return faseSolicitacao.get(0).getItemLicitacao().getLicitacao().getNumero();
			}
		}
		return null;
	}
	
	/**
	 * ORADB SCEC_BUSCA_DADOS_AF1
	 */
	@SuppressWarnings("ucd")
	public String buscarDadosAf1(Integer numero){
		List<ScoFaseSolicitacao> faseSolicitacao = getComprasFacade().pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(numero);
		if(faseSolicitacao != null && !faseSolicitacao.isEmpty()){
			return faseSolicitacao.get(0).getItemAutorizacaoForn() != null ? faseSolicitacao.get(0).getItemAutorizacaoForn().getAutorizacoesForn().getPropostaFornecedor().getLicitacao().getNumero().toString() : "/" + faseSolicitacao.get(0).getItemAutorizacaoForn() != null ? faseSolicitacao.get(0).getItemAutorizacaoForn().getAutorizacoesForn().getNroComplemento().toString() : "";
		}
		return null;
	}
	
	/**
	 * ORADB SCEC_BUSCA_DADOS_AF2
	 */
	@SuppressWarnings("ucd")
	public String buscarDadosAf2(Integer numero){
		List<ScoFaseSolicitacao> faseSolicitacao = getComprasFacade().pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(numero);
		if(faseSolicitacao != null && !faseSolicitacao.isEmpty()){
			return faseSolicitacao.get(0).getItemAutorizacaoForn() != null ? faseSolicitacao.get(0).getItemAutorizacaoForn().getAutorizacoesForn().getSituacao().toString() : "";
		}
		return null;
	}
	
	/**
	 * ORADB SCEC_BUSCA_DADOS_AF3
	 */
	@SuppressWarnings("ucd")
	public Date buscarDadosAf3(Integer numero){
		List<ScoFaseSolicitacao> faseSolicitacao = getComprasFacade().pesquisarFaseSolicitacaoPorNumeroSolCompraIndExcAfnSit(numero);
		if(faseSolicitacao != null && !faseSolicitacao.isEmpty()){
			return faseSolicitacao.get(0).getItemAutorizacaoForn() != null ? faseSolicitacao.get(0).getItemAutorizacaoForn().getAutorizacoesForn().getDtPrevEntrega() : null;
		}
		return null;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	public SceEstoqueGeralDAO getSceEstoqueGeralDAO(){
		return sceEstoqueGeralDAO;
	}
	
}
