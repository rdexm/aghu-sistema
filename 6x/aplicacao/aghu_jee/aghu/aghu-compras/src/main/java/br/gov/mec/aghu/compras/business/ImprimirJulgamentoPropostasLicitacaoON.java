package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesBullDAO;
import br.gov.mec.aghu.compras.dao.VScoItensLicitacaoDAO;
import br.gov.mec.aghu.compras.vo.JulgamentoPropostasLicitacaoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoSolicitacoesBull;
import br.gov.mec.aghu.core.business.BaseBusiness;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity","PMD.AtributoEmSeamContextManager"})
@Stateless
public class ImprimirJulgamentoPropostasLicitacaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ImprimirJulgamentoPropostasLicitacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoLicitacaoDAO scoLicitacaoDAO;

@Inject
private ScoSolicitacoesBullDAO scoSolicitacoesBullDAO;

@Inject
private VScoItensLicitacaoDAO vScoItensLicitacaoDAO;

@Inject
private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6630483759700414665L;

	private Integer scocBuscaSolLicitCompra;
	private Integer scocBuscaSolLicitServico;
	
	protected VScoItensLicitacaoDAO getVscoItensLicitacaoDAO(){
		return vScoItensLicitacaoDAO; 
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO(){
		return scoItemPropostaFornecedorDAO;
	}
	
	protected ScoSolicitacoesBullDAO getScoSolicitacoesBullDAO(){
		return scoSolicitacoesBullDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	
	
	public List<JulgamentoPropostasLicitacaoVO> gerarRelatorioSamberg(Integer nroLicitacao, Integer itemInicial, Integer itemFinal){
		List<JulgamentoPropostasLicitacaoVO> list = new ArrayList<JulgamentoPropostasLicitacaoVO>();
		ScoLicitacao licitacao = getScoLicitacaoDAO().obterLicitacaoPorNumIndExclusao(nroLicitacao, Boolean.FALSE);
		
				
		List<ScoItemLicitacao> itemLicitacoes = getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoEIndExclusao
			(nroLicitacao, Boolean.FALSE, itemInicial == null ? 0 : itemInicial, itemFinal == null ? 999 : itemFinal);
		
		for(ScoItemLicitacao itemLicit : itemLicitacoes){
			ScoFaseSolicitacao scoBuscaSolLicitServico = scocBuscaSolLicit(nroLicitacao, itemLicit.getId().getNumero(), DominioTipoFaseSolicitacao.S);
			
//			ScoFaseSolicitacao scoBuscaSolLicitCompra = 
					scocBuscaSolLicit(nroLicitacao, itemLicit.getId().getNumero(),	DominioTipoFaseSolicitacao.C);
			
			
			
			if(!getScoItemLicitacaoDAO().verificarExisteItemPropForn(itemLicit.getLicitacao().getNumero(), itemLicit.getId().getNumero(), 
					scoBuscaSolLicitServico.getSolicitacaoServico(), scoBuscaSolLicitServico.getSolicitacaoDeCompra())){
				continue;
			}
					
			List<Object[]> vil1 = this.getVscoItensLicitacaoDAO().obterItensLicitacaoVil1(itemLicit.getId().getNumero(),nroLicitacao);
			
			List<Object[]> ipfs = this.getScoItemPropostaFornecedorDAO().createAgrupamentoIpfHql(nroLicitacao, itemLicit.getId().getNumero(), scoBuscaSolLicitServico.getSolicitacaoServico(), scoBuscaSolLicitServico.getSolicitacaoDeCompra());
			for(Object[] ipf : ipfs){
				JulgamentoPropostasLicitacaoVO vo = new JulgamentoPropostasLicitacaoVO();
				vo.setLctDescricao(licitacao.getModalidadeLicitacao().getDescricao());
				vo.setLctNumero(licitacao.getNumero().toString());
				vo.setLctData(licitacao.getDthrAberturaProposta().toString());
				vo.setLctHora(licitacao.getHrAberturaProposta().toString());
				vo.setItlNumero(itemLicit.getId().getNumero().toString());
				vo.setVil1Solicit(buscarSolicitAuxFmla(vil1.size(),buscarMaxSolicit(vil1)));
				vo.setVil1BullFormula(solicitBullFormulaSamberg(vo.getVil1Solicit()));
				vo.setVil1MaxCodigo(buscarMaxCodigo(vil1).toString());
				vo.setVil1SumQtdeAprovada(buscarSumQtdeAprovada(vil1).toString());
				vo.setVil1MaxUndMedida(buscarMaxUnidMedida(vil1));
				vo.setVil1FirstNome(buscarFistNome(vil1));
				vo.setVil1FirstDescrSolic(buscarFistDescrSolic(vil1));
				vo.setVil1FirstIndMenorPreco(buscarFistIndMenorPreco(vil1));
				vo.setVil1FirstDescr(buscarFirstDescr(vil1));
				vo.setIpfRazaoSocial((String)ipf[4]);
				vo.setIpfCpMcmDesc((String)ipf[11]);
				vo.setIpfIndNacional( ((Boolean)ipf[22]).toString() ); //ajustar
				vo.setIpfUmdCodigo((String)ipf[23]);
				vo.setIpfCgcCpf(ipf[31] != null ? ((Long)ipf[31]).toString() : ((Long)ipf[32]).toString());
				vo.setIpfFatorConversao(((Integer)ipf[17]).toString());
				vo.setIpfQuantidade(((Long)ipf[18]).toString());
				vo.setIpfQuantidadeXFatorConversao(calculaQuantidaFatorConversao((Long)ipf[18], (Integer)ipf[17]));
				vo.setIpfValorUnitario(calculaValorUnitario( (BigDecimal)ipf[13], (Integer)ipf[17]));
				//vo.setIpfCpPrecoIpi((String)ipf[]);
				
				
				list.add(vo);
			}
		}
		
		Collections.sort(list);
		return list;
	}	

	

	private String calculaValorUnitario(BigDecimal ipf13, Integer ipf17) {
		BigDecimal result = (ipf13.divide(new BigDecimal(ipf17)));
		return result.toString();
	}

	private String calculaQuantidaFatorConversao(Long ipf18, Integer ipf17) {
		Long result = ipf18 * ipf17;		
		return result.toString();
	}

	private String buscarFirstDescr(List<Object[]> vil1) {
		String descr = "";
		for(Object[] obj : vil1){
			descr  = ((String)obj[5]);
			break;
		}
		return descr;
	}

	private String buscarFistIndMenorPreco(List<Object[]> vil1) {
		DominioSimNao indMenorPreco = null;
		for(Object[] obj : vil1){
			indMenorPreco  = ((DominioSimNao)obj[9]);
			break;
		}
		return indMenorPreco.toString();
	}

	private String buscarFistDescrSolic(List<Object[]> vil1) {
		String descrSolic = "";
		for(Object[] obj : vil1){
			descrSolic  = ((String)obj[6]);
			break;
		}
		return descrSolic;
	}

	private String buscarFistNome(List<Object[]> vil1) {
		String nome = "";
		for(Object[] obj : vil1){
			nome  = ((String)obj[4]);
			break;
		}
		return nome;
	}

	private String buscarMaxUnidMedida(List<Object[]> vil1) {
		//não utilizei max, usei o first
		String unidMedida = "";
		for(Object[] obj : vil1){
			unidMedida  = ((String)obj[3]);
			break;
		}
		return unidMedida;
	}

	private Integer buscarSumQtdeAprovada(List<Object[]> vil1) {
		Integer maxCodigo = 0;
		for(Object[] obj : vil1){
			maxCodigo += ((Integer)obj[2]);
		}
		return maxCodigo;
	}

	private Integer buscarMaxCodigo(List<Object[]> vil1) {
		Integer maxCodigo = 0;
		for(Object[] obj : vil1){
			if( ((Integer)obj[1]) > maxCodigo ){
				maxCodigo = ((Integer)obj[1]);
			}
		}
		return maxCodigo;
	}

	private Integer buscarMaxSolicit(List<Object[]> vil1List) {
		Integer maxSolict = 0;
		for(Object[] obj : vil1List){
			if( ((Integer)obj[0]) > maxSolict ){
				maxSolict = ((Integer)obj[0]);
			}
		}
		return maxSolict;
	}

	private String buscarSolicitAuxFmla(Integer countSolicit, Integer maxSolicit) {
		if(countSolicit == 1){
			return maxSolicit.toString();
		}else{
			return "*";
		}
	}
	
	public ScoFaseSolicitacao scocBuscaSolLicit(Integer pLctNumero, Short pItlNumero,
			DominioTipoFaseSolicitacao pTipoSol) {
		
		if(this.scocBuscaSolLicitCompra == null){
			this.scocBuscaSolLicitCompra = 0;	
		}
		if(this.scocBuscaSolLicitServico == null){
			this.scocBuscaSolLicitServico = 0;
		}
		 

		ScoFaseSolicitacao WSolicitacao = this.getScoFaseSolicitacaoDAO()
				.scocBuscaSolLicit(pLctNumero, pItlNumero, pTipoSol);

//		if (WSolicitacao != null && pTipoSol == DominioTipoFaseSolicitacao.C ) {
//			//this.scocBuscaSolLicitCompra = WSolicitacao.getSolicitacaoDeCompra().getNumero();
//			return WSolicitacao;
//		}
//		if (WSolicitacao != null && pTipoSol == DominioTipoFaseSolicitacao.S) {
//			//this.scocBuscaSolLicitServico = WSolicitacao.getSolicitacaoServico().getNumero();
//			return WSolicitacao;
//		}
		
		return WSolicitacao;
	}
	
	public String solicitBullFormulaSamberg(String itlSolicit){

		if(itlSolicit.equals("*")){
			return "*";
		}
		
		ScoSolicitacoesBull cSolicCBull = getScoSolicitacoesBullDAO().pesquisaSsagh(Integer.valueOf(itlSolicit));
				
		
		if(cSolicCBull != null){
			return cSolicCBull.getScBull().toString();	
		}else{
			cSolicCBull = getScoSolicitacoesBullDAO().pesquisaScagh(Integer.valueOf(itlSolicit));
			return cSolicCBull.getScBull().toString();
		}
	} 
	
}
