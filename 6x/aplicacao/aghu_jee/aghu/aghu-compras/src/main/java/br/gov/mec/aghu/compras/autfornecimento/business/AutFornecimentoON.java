/**
 * 
 */
package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroPesquisaAssinarAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFJnVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAFPVO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoJustificativaDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoListasSiafiFonteRecDAO;
import br.gov.mec.aghu.compras.dao.ScoPrazoPagamentoDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioAndamentoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoConsultaAssinarAF;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoListasSiafiFonteRec;
import br.gov.mec.aghu.model.ScoPrazoPagamento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AutFornecimentoON extends BaseBusiness{

	@EJB
	private ScoAutorizacaoFornecedorPedidoON scoAutorizacaoFornecedorPedidoON;
	
	@EJB
	private ScoAutorizacaoFornJnRN scoAutorizacaoFornJnRN;
	
	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	
	@EJB
	private PublicaAFPRN publicaAFPRN;
	
	@EJB
	private ISiconFacade siconFacade;
	
	private static final Log LOG = LogFactory.getLog(AutFornecimentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoAutorizacaoFornJnDAO scoAutorizacaoFornJnDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject
	private ScoPrazoPagamentoDAO scoPrazoPagamentoDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoListasSiafiFonteRecDAO scoListasSiafiFonteRecDAO;
	
	@Inject
	private ScoJustificativaDAO scoJustificativaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IOrcamentoFacade orcamentoFacade;
	
	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2261955322391131845L;
	
	public enum AutFornecimentoONExceptionCode implements BusinessExceptionCode {
		ASSINAR_AF_M1, ASSINAR_AF_MATERIAL, ASSINAR_AF_SERVICO, SELECIONE_REGISTRO_ASSINAR_AF, ERRO_PARAMETRO_TIPO_IMPORTACAO, ERRO_CANCELAMENTO_ASSINATURA_AF, 
		ERRO_PERSISTIR_CANCELAMENTO_ASSINATURA_AF, MENSAGEM_RN14_AF_DEVE_SER_ASSINADA_ANTES_DA_AFP;
	}
	
	public RelatorioAFPVO pesquisarAFsPorLicitacaoComplSeqAlteracao(Integer numPac, Short nroComplemento)
			throws ApplicationBusinessException{
		
		AghParametros pEspecieEmpenho = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
		RelatorioAFPVO afVO = new RelatorioAFPVO();
		
		List<ScoItemAFPVO> listaItensAf =  this.getScoAutorizacaoFornDAO().pesquisarAFsPorLicitacaoComplSeqAlteracao(numPac, nroComplemento , pEspecieEmpenho.getVlrNumerico().intValue());
		for (ScoItemAFPVO item: listaItensAf){
			ScoItemAutorizacaoForn itemAutorizacaoForn = getScoItemAutorizacaoFornDAO().obterItemAutorizacaoFornPorIdRelatorio(item.getAutorizacaoForn().getNumero(), item.getNumeroItem());
			item.setItemAutorizacaoForn(itemAutorizacaoForn);
			afVO.setAutorizacaoForn(item.getAutorizacaoForn());
			
			if(item.getSolicitacaoCompra() != null){
				LOG.info(item.getSolicitacaoCompra().getMaterial());
			}
			
			if(item.getSolicitacaoServico() != null){
				LOG.info(item.getSolicitacaoServico().getServico());
			}
		}
		afVO.setListaItensAfVO(listaItensAf);
		return afVO;
	}
	
	public RelatorioAFJnVO pesquisarJnAFsPorLicitacaoComplSeqAlteracao(Integer pacNumero, Short nroComplemento, Short sequenciaAlteracao)
			throws ApplicationBusinessException{
		
		AghParametros pEspecieEmpenho = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
		RelatorioAFJnVO afJnVo = new RelatorioAFJnVO();
		//  verificar se existe empenho para a autForn
		ScoAutorizacaoForn autortizaoForn = this.getScoAutorizacaoFornDAO().buscarAutFornPorNumPac(pacNumero, nroComplemento);
		if (autortizaoForn!=null && autortizaoForn.getAfEmpenho()!=null && autortizaoForn.getAfEmpenho().size()>0){
			 List<ScoAutorizacaoFornJn> listaAfsJn = this.getScoAutorizacaoFornJnDAO().pesquisarAFsPorLicitacaoComplSeqAlteracao(pacNumero,nroComplemento,sequenciaAlteracao , pEspecieEmpenho.getVlrNumerico().intValue());
			if (listaAfsJn!=null && listaAfsJn.size()>0){
				afJnVo.setAutorizacaForn(listaAfsJn.get(0));
			}
		}else {
			afJnVo.setAutorizacaForn(this.getScoAutorizacaoFornJnDAO().obterScoAutorizacaoFornJn(pacNumero,nroComplemento,sequenciaAlteracao));
			
		}
		 return afJnVo;
	}
	
	public String cfParcelasPgto(Integer cdpNumero) throws ApplicationBusinessException{
		String wParcelas = null; 
		
		
		List<ScoPrazoPagamento> lista = getScoPrazoPagamentoDAO().listarParcelas(cdpNumero);
		
		for(int i = 0;i<lista.size(); i++){
			if(wParcelas == null){
				wParcelas =  lista.get(i).getParcela().toString();
			}else{
				wParcelas = wParcelas.concat("/").concat(lista.get(i).getParcela().toString());
			}
		}
		return wParcelas;
	}
	
	/**
	 * Obtem o nome do arquivo da bandeira com a cor definida conforme o andamento da AF 
	 * @param andamento
	 * @return String
	 */
	public String obterNomeImagemAutorizacaoFornecimento(DominioAndamentoAutorizacaoFornecimento andamento) {
		String ret = "";
		
		if (andamento == DominioAndamentoAutorizacaoFornecimento.VERSAO_JA_ASSINADA) {
			ret = "flag_blue.png";
		} else if (andamento == DominioAndamentoAutorizacaoFornecimento.AF_LIBERADA_ASSINATURA) {
			ret = "flag_green.png";
		} else if (andamento == DominioAndamentoAutorizacaoFornecimento.VERSAO_EMPENHADA) {
			ret = "flag_purple.png";
		} else if (andamento == DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA) {
			ret = "flag_yellow.png";
		}  else if (andamento == DominioAndamentoAutorizacaoFornecimento.ALTERACAO_PENDENTE_JUSTIFICATIVA) {
			ret = "flag_red.png";
		}
		
		return ret;
	}

	/**
	 * Obtem a cor definida para o inputText conforme o andamento da AF
	 * @param andamento
	 * @return
	 */
	public String obterCorFundoAndamentoAutorizacaoFornecimento(DominioAndamentoAutorizacaoFornecimento andamento) {
		String ret = "";
		
		if (andamento == DominioAndamentoAutorizacaoFornecimento.VERSAO_JA_ASSINADA) {
			ret = "#00FFFF";
		} else if (andamento == DominioAndamentoAutorizacaoFornecimento.AF_LIBERADA_ASSINATURA) {
			ret = "#00FF00";
		} else if (andamento == DominioAndamentoAutorizacaoFornecimento.VERSAO_EMPENHADA) {
			ret = "#800080";
		} else if (andamento == DominioAndamentoAutorizacaoFornecimento.LIBERAR_AF_ASSINATURA) {
			ret = "#FFFF00";
		}  else if (andamento == DominioAndamentoAutorizacaoFornecimento.ALTERACAO_PENDENTE_JUSTIFICATIVA) {
			ret = "#FF0000";
		}
		
		return ret;
	}

	/**
	 * Obtem o valor bruto (não efetivado) da AF
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param listaItens
	 * @return Double
	 */
	public Double obterValorBrutoAf(Integer afnNumero, Short numeroComplemento, List<ScoItemAutorizacaoForn> listaItens) {
		Double valorBruto = 0.00;
		for (ScoItemAutorizacaoForn item : listaItens) {
			if (item.getScoFaseSolicitacao() != null && !item.getScoFaseSolicitacao().isEmpty()) {
				if (item.getScoFaseSolicitacao().get(item.getScoFaseSolicitacao().size()-1).getTipo() == DominioTipoFaseSolicitacao.S) {
					valorBruto=valorBruto + ((Double)CoreUtil.nvl(item.getValorUnitario(), 0.00)  - (Double) CoreUtil.nvl(item.getValorEfetivado(),0.00));
				} else {					
					Integer qtdSolicitada = (Integer) CoreUtil.nvl(item.getQtdeSolicitada(), 0);
					Integer qtdRecebida = (Integer) CoreUtil.nvl(item.getQtdeRecebida(), 0);

					valorBruto=valorBruto + (qtdSolicitada - qtdRecebida) * (Double)CoreUtil.nvl(item.getValorUnitario(), 0.00);
				}
			}	
		}
		return valorBruto;
	}
	
	/**
	 * Obtem o valor de acrescimo da AF
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param valorBruto
	 * @param listaItens
	 * @return Double
	 */
	public Double obterValorAcrescimoAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, List<ScoItemAutorizacaoForn> listaItens) {
		Double valorAcrescimoItem = 0.00;
		Double valorAcrescimoCondicaoPagto = 0.00;
				
		for (ScoItemAutorizacaoForn item : listaItens) {
			
			if (item.getPercAcrescimoItem() != null && item.getPercAcrescimoItem() > 0.00) {
				valorAcrescimoItem = valorAcrescimoItem + valorBruto * (item.getPercAcrescimoItem() / 100);	
			}
			if (item.getPercAcrescimo() != null && item.getPercAcrescimo() > 0.00) {
				valorAcrescimoCondicaoPagto = valorAcrescimoCondicaoPagto + valorBruto * (item.getPercAcrescimo() / 100);
			}
		}
		return valorAcrescimoItem+valorAcrescimoCondicaoPagto;
	}
	
	/**
	 * Obtem o valor de desconto da AF
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param valorBruto
	 * @param listaItens
	 * @return Double
	 */
	public Double obterValorDescontoAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, List<ScoItemAutorizacaoForn> listaItens) {
		Double valorDescontoItem = 0.00;
		Double valorDescontoCondicaoPagto = 0.00;
				
		for (ScoItemAutorizacaoForn item : listaItens) {
			
			if (item.getPercDescontoItem() != null && item.getPercDescontoItem() > 0.00) {
				valorDescontoItem = valorDescontoItem + valorBruto * (item.getPercDescontoItem() / 100);	
			}
			if (item.getPercDesconto() != null && item.getPercDesconto() > 0.00) {
				valorDescontoCondicaoPagto = valorDescontoCondicaoPagto + valorBruto * (item.getPercDesconto() / 100);
			}
		}
		return valorDescontoItem+valorDescontoCondicaoPagto;
	}
	
	/**
	 * Obtem o valor do IPI da AF
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param valorBruto
	 * @param valorDesconto
	 * @param valorAcrescimo
	 * @param listaItens
	 * @return Double
	 */
	public Double obterValorIpiAf(Integer afnNumero, Short numeroComplemento, Double valorBruto, Double valorDesconto, Double valorAcrescimo, List<ScoItemAutorizacaoForn> listaItens) {
		Double valorIpi = 0.00;
		
		for (ScoItemAutorizacaoForn item : listaItens) {
			if (item.getPercIpi() != null && item.getPercIpi() > 0.00) {
				valorIpi = valorIpi + ((valorBruto - valorDesconto + valorAcrescimo) * (item.getPercIpi() / 100));
			}
		}
		return valorIpi;
	}
	
	/**
	 * Obtem o valor Efetivado da AF
	 * @param afnNumero
	 * @param numeroComplemento
	 * @param listaItens
	 * @return
	 */
	public Double obterValorEfetivadoAf(Integer afnNumero, Short numeroComplemento, List<ScoItemAutorizacaoForn> listaItens) {
		Double valorEfetivado = 0.00;
		
		for (ScoItemAutorizacaoForn item : listaItens) {
			if (item.getValorEfetivado() != null) {
				valorEfetivado = valorEfetivado + item.getValorEfetivado();
			}
		}
		return valorEfetivado;
	}
	
	
	public String obterDescricaoQtdItemAF(ScoItemAutorizacaoForn itemAutForn, Integer saldo){
		String descricaoQtdItemAF;
		// Monta a String com a descrição apenas se todos os campo forem diferente de nulo, 
		// e as unidades de medida (padrão e do fornecedor) forem diferentes
		if (!verificarCamposNulosItemAF(itemAutForn) &&
				!itemAutForn.getUmdCodigoForn().getCodigo().equals(itemAutForn.getUnidadeMedida().getCodigo())){	
			BigDecimal qtd = new BigDecimal(saldo);
			qtd = qtd.divide(new BigDecimal(itemAutForn.getFatorConversaoForn()), 2, RoundingMode.HALF_UP); 
			BigDecimal qtdInteira = new BigDecimal(qtd.intValue());
			if (qtd.compareTo(qtdInteira) == 0){
				qtd = qtdInteira;
			}
			
			String unidadeMedidaForn = itemAutForn.getUmdCodigoForn().getCodigo(); 
			String fatorConversaoForn = itemAutForn.getFatorConversaoForn().toString();
			String unidadeMedida = itemAutForn.getUnidadeMedida().getCodigo();
			BigDecimal vlUnitarioEmbalagem = new BigDecimal(itemAutForn.getValorUnitario()).multiply(new BigDecimal(itemAutForn.getFatorConversaoForn()));
			vlUnitarioEmbalagem = vlUnitarioEmbalagem.setScale(4, RoundingMode.HALF_UP);
			StringBuilder descricao = new StringBuilder();
			descricao.append(qtd.toString())
			.append(' ').append(unidadeMedidaForn).append(" c/ ")
			.append(fatorConversaoForn).append(' ').append(unidadeMedida).append('.');
			
			String valorFormatado = this.getValorFormatado(vlUnitarioEmbalagem);
			
			if (vlUnitarioEmbalagem != null){
				descricao.append(" Valor Unitário da Embalagem: ").append(valorFormatado);
			}
			descricaoQtdItemAF = descricao.toString();
		} else {
			descricaoQtdItemAF = null;
		}
		return descricaoQtdItemAF;		
	}
	
	private String getValorFormatado(BigDecimal valor) {
		Locale locBR = new Locale("pt", "BR"); 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,###,###,###,###,##0.0000####", dfSymbols);

        return (valor == null)?null:format.format(valor);
	}

	private Boolean verificarCamposNulosItemAF(ScoItemAutorizacaoForn itemAutForn){
		Boolean existeCampoNulo = false;
		if (itemAutForn.getQtdeSolicitada() == null || itemAutForn.getFatorConversaoForn() == null || itemAutForn.getUmdCodigoForn() == null || itemAutForn.getFatorConversaoForn() == null || itemAutForn.getValorUnitario() == null){
			existeCampoNulo = true;
		}
		return existeCampoNulo;
	}
	
	
	
	/**
	 * Obtem o maior número de sequencia de alteração da AF
	 * @param numAf
	 * @return
	 */
	public Integer obterMaxSequenciaAlteracaoAF(Integer numAf) {
		return this.getScoItemAutorizacaoFornDAO().obterMaxSequenciaAlteracaoAF(numAf);
	}
	
	/**
	 * Calcula o valor líquido da AF
	 * @param valorBruto
	 * @param valorIpi
	 * @param valorAcrescimo
	 * @param valorDesconto
	 * @return Double
	 */
	public Double getValorLiquidoAf(Double valorBruto, Double valorIpi, Double valorAcrescimo, Double valorDesconto) {
		return (valorBruto + valorAcrescimo + valorIpi - valorDesconto );
	}

	/**
	 * Calcula o valor total da AF
	 * @param valorLiquido
	 * @param valorEfetivado
	 * @return Double
	 */
	public Double getValorTotalAf(Double valorLiquido, Double valorEfetivado) {
		return (valorLiquido + valorEfetivado);
	}
	
	public String verificarCaracteristicaAssinarAf(){
		
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
		
		Set<ScoCaracteristicaUsuarioCentroCusto> listaCaracteristica = servidor.getCaracUsuariosCentroCustos();
		Iterator<ScoCaracteristicaUsuarioCentroCusto> iterator = listaCaracteristica.iterator();
		while(iterator.hasNext()){
			ScoCaracteristicaUsuarioCentroCusto caracteristicaUsuarioCentroCusto = (ScoCaracteristicaUsuarioCentroCusto) iterator.next();
			
			if(caracteristicaUsuarioCentroCusto.getCaracteristica().getCaracteristica().equals(this.getResourceBundleValue(AutFornecimentoONExceptionCode.ASSINAR_AF_MATERIAL.toString()))){
				return caracteristicaUsuarioCentroCusto.getCaracteristica().getCaracteristica();
			} else if(caracteristicaUsuarioCentroCusto.getCaracteristica().getCaracteristica().equals(this.getResourceBundleValue(AutFornecimentoONExceptionCode.ASSINAR_AF_SERVICO.toString()))){
				return caracteristicaUsuarioCentroCusto.getCaracteristica().getCaracteristica();
			}
		}
		return  null;
	}
	
	public List<PesquisaAutorizacaoFornecimentoVO> pesquisarListaAfsAssinar(Integer first, Integer max,
			String order, boolean asc, FiltroPesquisaAssinarAFVO filtro) {
		List<PesquisaAutorizacaoFornecimentoVO> retorno = new ArrayList<PesquisaAutorizacaoFornecimentoVO>();
		if (filtro == null || filtro.getTipoConsulta() == null) {
			List<PesquisaAutorizacaoFornecimentoVO> retornoAF = this.getScoAutorizacaoFornJnDAO().pesquisarListaJnAfs(filtro);
			List<PesquisaAutorizacaoFornecimentoVO> retornoAFP = this.getScoAutorizacaoFornDAO().pesquisarListaAfs(filtro);
			this.getInformacaoAF(retornoAF);
			retorno.addAll(retornoAF);
			
			this.getInformacaoAFP(retornoAFP);
			retorno.addAll(retornoAFP);

			// reordenar lista por DT_ALTERACAO DESC, NUMERO DESC
			Collections.sort(retorno, new AutorizacaoFornecimentoVOComparator());
		} else {
			switch (filtro.getTipoConsulta()) {
			case AF: {
				List<PesquisaAutorizacaoFornecimentoVO> retornoAF = this.getScoAutorizacaoFornJnDAO().pesquisarListaJnAfs(filtro);
				this.getInformacaoAF(retornoAF);
				retorno.addAll(retornoAF);
			}
				break;

			case AFP: {
				List<PesquisaAutorizacaoFornecimentoVO> retornoAFP = this.getScoAutorizacaoFornDAO().pesquisarListaAfs(filtro);
				this.getInformacaoAFP(retornoAFP);
				retorno.addAll(retornoAFP);
			}
				break;
			default:
				List<PesquisaAutorizacaoFornecimentoVO> retornoAF = this.getScoAutorizacaoFornJnDAO().pesquisarListaJnAfs(filtro);
				List<PesquisaAutorizacaoFornecimentoVO> retornoAFP = this.getScoAutorizacaoFornDAO().pesquisarListaAfs(filtro);
				this.getInformacaoAF(retornoAF);
				retorno.addAll(retornoAF);
				this.getInformacaoAFP(retornoAFP);
				retorno.addAll(retornoAFP);
				// reordenar lista por DT_ALTERACAO DESC, NUMERO DESC
				Collections.sort(retorno, new AutorizacaoFornecimentoVOComparator());
				break;
			}
		}
		
		if (!retorno.isEmpty()) {
			int indPrimeiro = first;
			int indUltimo = first + max;
			int tamLista = retorno.size();
			if (indUltimo > tamLista) {
				indUltimo = tamLista;
			}
			return retorno.subList(indPrimeiro, indUltimo);
		}

		return retorno;
	}

	public Long pesquisarListaAfsAssinarCount(FiltroPesquisaAssinarAFVO filtro) {
		Long count = 0L;
		if (filtro == null || filtro.getTipoConsulta() == null) {
			Long retornoAF = this.getScoAutorizacaoFornJnDAO().pesquisarListaJnAfsCount(filtro);
			Long retornoAFP = this.getScoAutorizacaoFornDAO().pesquisarListaAfsCount(filtro);
			count = retornoAF + retornoAFP;
		} else {
			switch (filtro.getTipoConsulta()) {
			case AF: {
				count = this.getScoAutorizacaoFornJnDAO().pesquisarListaJnAfsCount(filtro);
			}
				break;

			case AFP: {
				count = this.getScoAutorizacaoFornDAO().pesquisarListaAfsCount(filtro);
			}
				break;
			default:
				Long retornoAF = this.getScoAutorizacaoFornJnDAO().pesquisarListaJnAfsCount(filtro);
				Long retornoAFP = this.getScoAutorizacaoFornDAO().pesquisarListaAfsCount(filtro);
				count = retornoAF + retornoAFP;
				break;
			}
		}
		
		return count;
	}

	// DT_ALTERACAO DESC, NUMERO DESC
	private class AutorizacaoFornecimentoVOComparator implements Comparator<PesquisaAutorizacaoFornecimentoVO> {
		public int compare(PesquisaAutorizacaoFornecimentoVO vo1, PesquisaAutorizacaoFornecimentoVO o) {
			if (vo1 == o) {
				return 0;
			}
			if (vo1 == null) {
				return -1;
			}
			if (o == null) {
				return 1;
			}
			if (vo1.getDtAlteracao() == null && o.getDtAlteracao() == null) {
				return this.compareToNumero(vo1, o);
			}
			if (vo1.getDtAlteracao() == null) {
				return -1;
			}
			if (o.getDtAlteracao() == null) {
				return 1;
			}
			int ret = o.getDtAlteracao().compareTo(vo1.getDtAlteracao());
			if (ret == 0) {
				return compareToNumero(vo1, o);
			}
			return ret;
		}

		private int compareToNumero(PesquisaAutorizacaoFornecimentoVO conta, PesquisaAutorizacaoFornecimentoVO o) {
			if (conta.getAfnNumero() == o.getAfnNumero()) {
				return 0;
			}
			if (conta.getAfnNumero() == null) {
				return -1;
			}
			if (o.getAfnNumero() == null) {
				return 1;
			}
			return o.getAfnNumero().compareTo(conta.getAfnNumero());
		}
	}

	private void getInformacaoAFP(List<PesquisaAutorizacaoFornecimentoVO> retornoAFP) {
		for (PesquisaAutorizacaoFornecimentoVO vo : retornoAFP) {
			// #30302 - #25272 - Assinar AF - Coluna Seq Alt na Lista
			vo.setSequenciaAlteracao(null);	
			this.popularOrigem(vo);
			vo.setTipoConsultaAssinarAF(DominioTipoConsultaAssinarAF.AFP);
			vo.setValorReforco(vo.getVlrTotalAfp().doubleValue());
			vo.setValorTotal(vo.getVlrTotalAfp().doubleValue());
			vo.setNumeroAFP(vo.getMaxNumeroAfp());
		}
	}

	/**
	 * 
	 * @ORABD SCOF_ASSINATURA_AFP – EVT_POST_QUERY
	 * @param af
	 */
	private void popularOrigem(PesquisaAutorizacaoFornecimentoVO af) {
		Integer nrEntregas = af.getQtdCum();
		if (nrEntregas == null || nrEntregas <= 0) {
			if (af.getTipoSolicitacao() == null) {
				this.setOrigemCUM(af);
			} else {
				switch (af.getTipoSolicitacao()) {
				case "C":
					af.setOrigem(this.getResourceBundleValue("VALOR_C_ASSINAR_AF"));
					af.setHintOrigem(this.getResourceBundleValue("LABEL_C_ASSINAR_AF"));
					break;
				case "S":
					af.setOrigem(this.getResourceBundleValue("VALOR_S_ASSINAR_AF"));
					af.setHintOrigem(this.getResourceBundleValue("LABEL_S_ASSINAR_AF"));
					break;
				default:
					this.setOrigemCUM(af);
					break;
				}
			}
		} else {
			this.setOrigemCUM(af);
		}
	}

	private void setOrigemCUM(PesquisaAutorizacaoFornecimentoVO af) {
		af.setOrigem(this.getResourceBundleValue("VALOR_CUM_ASSINAR_AF"));
		af.setHintOrigem(this.getResourceBundleValue("LABEL_CUM_ASSINAR_AF"));
	}

	private void getInformacaoAF(List<PesquisaAutorizacaoFornecimentoVO> retornoAF) {
		 for (PesquisaAutorizacaoFornecimentoVO vo : retornoAF) {
			this.popularOrigem(vo);
			this.setarVlrReforco(vo);
			this.setarVlrTotal(vo);
			vo.setTipoConsultaAssinarAF(DominioTipoConsultaAssinarAF.AF);
		 }		
	}
	
	private void setarVlrReforco(PesquisaAutorizacaoFornecimentoVO af){
		if (af != null){
			if (af.getSequenciaAlteracao().equals(af.getSequenciaAlteracaoAf())
							&& DominioSituacaoAutorizacaoFornecimento.EX.equals(af.getSituacaoAfjnAf())) {
				af.setValorReforco(0.0);				
			}
		}
	}
			
	private void setarVlrTotal(PesquisaAutorizacaoFornecimentoVO af){
		if (af != null){
			if (DominioSituacaoAutorizacaoFornecimento.EX.equals(af.getSituacaoAf())){
				af.setValorTotal(0.0);
			}
			else if (af.getSequenciaAlteracao().equals(af.getSequenciaAlteracaoAf())
							&& DominioSituacaoAutorizacaoFornecimento.EX.equals(af.getSituacaoAfjnAf())) {
				af.setValorTotal(0.0);
			} 
		}
	}

	private void validarListaAssinaturaAf(
			Set<PesquisaAutorizacaoFornecimentoVO> lista)
			throws ApplicationBusinessException {
		if (lista.size() == 0) {
			throw new ApplicationBusinessException(
					AutFornecimentoONExceptionCode.SELECIONE_REGISTRO_ASSINAR_AF);
		}
	}
	
	private AghParametros getParametroTipoImportacao() throws ApplicationBusinessException {
		return this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_IMPORTACAO);
	}
	
	private ScoAutorizacaoFornecedorPedido criarScoAutorizacaoFornecedorPedido(final Integer afnNumero, final Integer numeroAFP, final Integer eslSeqFatura, final Date dtEnvioFornecedor) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido = new ScoAutorizacaoFornecedorPedido();
		ScoAutorizacaoFornecedorPedidoId id = new ScoAutorizacaoFornecedorPedidoId();
		id.setAfnNumero(afnNumero);
		id.setNumero(numeroAFP);
		autorizacaoFornecedorPedido.setId(id);
		autorizacaoFornecedorPedido.setDtGeracao(new Date());
		autorizacaoFornecedorPedido.setRapServidor(servidorLogado);
		autorizacaoFornecedorPedido.setDtEnvioFornecedor(dtEnvioFornecedor);
		autorizacaoFornecedorPedido.setEslSeqFatura(eslSeqFatura);
		this.getScoAutorizacaoFornecedorPedidoON().persistir(autorizacaoFornecedorPedido);
		return autorizacaoFornecedorPedido;
	}


	public List<PesquisaAutorizacaoFornecimentoVO> confirmarAssinaturaAf(Set<PesquisaAutorizacaoFornecimentoVO> lista)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		List<PesquisaAutorizacaoFornecimentoVO> listaAssinadas = new ArrayList<PesquisaAutorizacaoFornecimentoVO>(); 
		Date dtEnvioFornecedor;
		Boolean envioFornecedor;
		this.validarListaAssinaturaAf(lista);
		Iterator<PesquisaAutorizacaoFornecimentoVO> iterator = lista
				.iterator();
		while (iterator.hasNext()) {
			PesquisaAutorizacaoFornecimentoVO autorizacaoFornecimento = (PesquisaAutorizacaoFornecimentoVO) iterator
					.next();
			if (!autorizacaoFornecimento.isAfp()) {
				ScoAutorizacaoForn autorizacaoForn = this
						.getScoAutorizacaoFornDAO().obterPorChavePrimaria(autorizacaoFornecimento.getAfnNumero());
				autorizacaoForn.setAprovada(DominioAprovadaAutorizacaoForn.A);
				autorizacaoForn.setServidorAssinaCoord(servidorLogado);
				autorizacaoForn.setDtAssinaturaCoord(new Date());
				this.getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(autorizacaoForn);
				ScoAutorizacaoFornJn autorizacaoFornJn = this.getScoAutorizacaoFornJnDAO().obterScoAutorizacaoFornJnPorAFSequencia(autorizacaoFornecimento.getAfnNumero(), autorizacaoFornecimento.getSequenciaAlteracao());
				ScoAutorizacaoFornJn autorizacaoFornJnOriginal = this.getScoAutorizacaoFornJnDAO().obterScoAutorizacaoFornJnPorAFSequencia(autorizacaoFornecimento.getAfnNumero(), autorizacaoFornecimento.getSequenciaAlteracao());
				if(autorizacaoFornJn!=null){
					autorizacaoFornJn.setServidorAssinaCoord(servidorLogado);
					autorizacaoFornJn.setDtAssinaturaCoord(new Date());
					autorizacaoFornJn.setIndAprovada(DominioAprovadaAutorizacaoForn.A);
					this.getScoAutorizacaoFornJnRN().atualizar(autorizacaoFornJn, autorizacaoFornJnOriginal);
					this.getScoAutorizacaoFornJnDAO().flush();
					listaAssinadas.add(autorizacaoFornecimento);
				}
				
				AghParametros pHuUtilizaPortalForn = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HU_UTILIZA_PORTAL_FORN);
				
				if (pHuUtilizaPortalForn != null &&
					pHuUtilizaPortalForn.getVlrTexto().equalsIgnoreCase("P") &&
					!autorizacaoForn.getEntregaProgramada()){
					this.objetosOracleDAO.executarRotinaSIAF(autorizacaoForn.getNumero(), servidorLogado.getUsuario());					
				}
				
			} else {
				//RN14 - Não permite assinar uma Parcela se a AF correspondente ainda não foi assinada
				Short seq = 0; 
				ScoAutorizacaoFornJn autFornJn = this.getScoAutorizacaoFornJnDAO().obterScoAutorizacaoFornJnPorAFSequencia(autorizacaoFornecimento.getAfnNumero(), seq);
				if(autFornJn != null) {
					if (autFornJn.getDtAssinaturaCoord() == null) {
						throw new ApplicationBusinessException(AutFornecimentoONExceptionCode.MENSAGEM_RN14_AF_DEVE_SER_ASSINADA_ANTES_DA_AFP);
					}
				}
				final AghParametros parametroTipoImportacao = this.getParametroTipoImportacao();
				if(autorizacaoFornecimento.getCondicaoPagamentoPropos()!=null && autorizacaoFornecimento.getCondicaoPagamentoPropos().getFormaPagamento()!=null && parametroTipoImportacao.getVlrNumerico().shortValue()==autorizacaoFornecimento.getCondicaoPagamentoPropos().getFormaPagamento().getCodigo()){
					dtEnvioFornecedor = new Date();
					envioFornecedor = Boolean.TRUE;
				} else {
					dtEnvioFornecedor = null;
					envioFornecedor = Boolean.FALSE;
				}
				Integer eslSeqFatura = null;
				List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgEntregaItemAutorizacaoFornecimentos = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarProgEntregaItemAf(autorizacaoFornecimento.getAfnNumero(), true, false, false, null, null);
				if(listaProgEntregaItemAutorizacaoFornecimentos!=null && listaProgEntregaItemAutorizacaoFornecimentos.size()>0){
					ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento = listaProgEntregaItemAutorizacaoFornecimentos.get(0);
					eslSeqFatura = progEntregaItemAutorizacaoFornecimento.getEslSeqFatura();
				}
				this.criarScoAutorizacaoFornecedorPedido(autorizacaoFornecimento.getAfnNumero(), autorizacaoFornecimento.getNumeroAFP(), eslSeqFatura, dtEnvioFornecedor);
				
				List<ScoListasSiafiFonteRec> listaScoListasSiafiFonteRecs = this.getScoListasSiafiFonteRecDAO().pesquisarListasSiafiFonteRecAtivos();
				Boolean empSaldoAf = null;
				if(listaScoListasSiafiFonteRecs!=null && listaScoListasSiafiFonteRecs.size()>0){
					ScoListasSiafiFonteRec listaSiafiFonteRec = listaScoListasSiafiFonteRecs.get(0);
					empSaldoAf = listaSiafiFonteRec.getEmpSaldoAf();
				}
				
				
				for(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento2: listaProgEntregaItemAutorizacaoFornecimentos){
					ScoAutorizacaoFornecedorPedidoId autorizacaoFornecedorPedidoId = new ScoAutorizacaoFornecedorPedidoId();
					autorizacaoFornecedorPedidoId.setAfnNumero(autorizacaoFornecimento.getAfnNumero());
					autorizacaoFornecedorPedidoId.setNumero(autorizacaoFornecimento.getNumeroAFP());
					ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido2 = this.getScoAutorizacaoFornecedorPedidoDAO().obterPorChavePrimaria(autorizacaoFornecedorPedidoId);
					progEntregaItemAutorizacaoFornecimento2.setScoAfPedido(autorizacaoFornecedorPedido2);
					progEntregaItemAutorizacaoFornecimento2.setIndAssinatura(Boolean.TRUE);
					progEntregaItemAutorizacaoFornecimento2.setDtAssinatura(new Date());
					progEntregaItemAutorizacaoFornecimento2.setRapServidor(servidorLogado);
					if(empSaldoAf==Boolean.TRUE){
						progEntregaItemAutorizacaoFornecimento2.setIndEmpenhada(DominioAfEmpenhada.P);	
						progEntregaItemAutorizacaoFornecimento2.setScoJustificativa(null);
					} else {
						progEntregaItemAutorizacaoFornecimento2.setIndEmpenhada(DominioAfEmpenhada.S);	
						ScoJustificativa justificativa = this.getScoJustificativaDAO().obterPorChavePrimaria(Short.valueOf("1"));
						progEntregaItemAutorizacaoFornecimento2.setScoJustificativa(justificativa);
					}
					progEntregaItemAutorizacaoFornecimento2.setIndEnvioFornecedor(envioFornecedor);
					this.getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntregaItemAutorizacaoFornecimento2);
					this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();	
					boolean publicaCum = this.publicaAFPRN.publicaAfpFornecedor(autorizacaoFornecimento.getAfnNumero(), autorizacaoFornecimento.getNumeroAFP());
					autorizacaoFornecimento.setPublicaCUM(publicaCum);					
					listaAssinadas.add(autorizacaoFornecimento);
				}
				
			
			}
		}
		return listaAssinadas;
	}
	
	public void cancelarAssinaturaAf(
			PesquisaAutorizacaoFornecimentoVO item) throws ApplicationBusinessException{
			List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgEntregaItemAutorizacaoFornecimento = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisarProgEntregaItemAf(item.getAfnNumero(), true, null, false, null,null);
			if(listaProgEntregaItemAutorizacaoFornecimento==null || listaProgEntregaItemAutorizacaoFornecimento.size()==0){
				throw new ApplicationBusinessException(AutFornecimentoONExceptionCode.ERRO_CANCELAMENTO_ASSINATURA_AF);
			}
			for(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAutorizacaoFornecimento: listaProgEntregaItemAutorizacaoFornecimento){
				getScoProgEntregaItemAutorizacaoFornecimentoDAO().refresh(progEntregaItemAutorizacaoFornecimento);
				progEntregaItemAutorizacaoFornecimento.setIndPlanejamento(Boolean.FALSE);
				progEntregaItemAutorizacaoFornecimento.setIndEmpenhada(DominioAfEmpenhada.N);
				try{
					this.getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntregaItemAutorizacaoFornecimento);
				} catch(Exception e){
					throw new ApplicationBusinessException(AutFornecimentoONExceptionCode.ERRO_PERSISTIR_CANCELAMENTO_ASSINATURA_AF);
				}
				
				this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
			}
	}

	public void validarComplementoAssinarAf(FiltroPesquisaAssinarAFVO filtro) throws ApplicationBusinessException{
		if(filtro.getNumeroComplemento()!=null && filtro.getNumeroAf()==null){
			throw new ApplicationBusinessException(AutFornecimentoONExceptionCode.ASSINAR_AF_M1);
		}
	}
	
	public ScoAutorizacaoForn buscarAutFornPorNumPac(Integer numPac, Short numComplemento){
		ScoAutorizacaoForn af = this.getScoAutorizacaoFornDAO().buscarAutFornPorNumPac(numPac, numComplemento);
		af.setItensAutorizacaoForn(this.getScoItemAutorizacaoFornDAO().pesquisarItemAfPorNumeroAf(af.getNumero()));
		return af;
	}
	
	public ScoAutorizacaoForn obterAfByNumero(Integer numeroAf){
		ScoAutorizacaoForn af = this.getScoAutorizacaoFornDAO().obterAfByNumero(numeroAf);
		af.setItensAutorizacaoForn(this.getScoItemAutorizacaoFornDAO().pesquisarItemAfDetalhadoPorNumeroAf(af.getNumero()));
		return af;
	}
	
	
	public ScoAutorizacaoForn obterAfByNumeroComPropostaFornecedor(Integer numeroAf){
		ScoAutorizacaoForn af = this.obterAfByNumero(numeroAf);
		if(af.getPropostaFornecedor() != null){
			Hibernate.initialize(af.getPropostaFornecedor().getLicitacao());
			Hibernate.initialize(af.getPropostaFornecedor().getFornecedor());
		}
		return af;
	} 
	
	public List<ScoAutorizacaoForn> listarAfByFornAndLic(ScoLicitacao licitacao, ScoFornecedor fornecedor) {
		List<ScoAutorizacaoForn> lista = getScoAutorizacaoFornDAO().listarAfByFornAndLic(licitacao, fornecedor);
		for (ScoAutorizacaoForn a : lista) {
			Hibernate.initialize(a.getPropostaFornecedor());
			Hibernate.initialize(a.getConvenioFinanceiro());
			if (a.getPropostaFornecedor() != null) {
				Hibernate.initialize(a.getPropostaFornecedor().getFornecedor());
				Hibernate.initialize(a.getPropostaFornecedor().getItens());
				Hibernate.initialize(a.getPropostaFornecedor().getLicitacao());
				if (a.getPropostaFornecedor().getLicitacao() != null) {
					Hibernate.initialize(a.getPropostaFornecedor().getLicitacao().getModalidadeLicitacao());
				}
			}
			if (a.getItensAutorizacaoForn() != null) {
				for (ScoItemAutorizacaoForn iten : a.getItensAutorizacaoForn()) {
					Hibernate.initialize(iten.getItemPropostaFornecedor());
					if (iten.getItemPropostaFornecedor() != null) {
						Hibernate.initialize(iten.getItemPropostaFornecedor().getUnidadeMedida());
					}
					Hibernate.initialize(iten.getScoFaseSolicitacao());
					if (iten.getScoFaseSolicitacao() != null) {
						for (ScoFaseSolicitacao fase : iten.getScoFaseSolicitacao()) {
							Hibernate.initialize(fase.getSolicitacaoDeCompra());
							if (fase.getSolicitacaoDeCompra() != null) {
								Hibernate.initialize(fase.getSolicitacaoDeCompra().getMaterial());
							}
							Hibernate.initialize(fase.getSolicitacaoServico());
							if (fase.getSolicitacaoServico() != null) {
								Hibernate.initialize(fase.getSolicitacaoServico().getServico());
							}
						}
					}
				}
			}
		}
		return lista;
	}
		
	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoAutorizacaoFornJnDAO getScoAutorizacaoFornJnDAO() {
		return scoAutorizacaoFornJnDAO;
	}
	
	protected ScoAutorizacaoFornecedorPedidoDAO getScoAutorizacaoFornecedorPedidoDAO() {
		return scoAutorizacaoFornecedorPedidoDAO;
	}
	
	protected ScoPrazoPagamentoDAO getScoPrazoPagamentoDAO() {
		return scoPrazoPagamentoDAO;
	}
	
	protected ScoJustificativaDAO getScoJustificativaDAO() {
		return scoJustificativaDAO;
	}
	
	protected ScoListasSiafiFonteRecDAO getScoListasSiafiFonteRecDAO() {
		return scoListasSiafiFonteRecDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected ScoPropostaFornecedorDAO getScoPropostaFornecedorDAO() {
		return scoPropostaFornecedorDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IOrcamentoFacade getOrcamentoFacade() {
		return orcamentoFacade;
	}
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoRN getScoProgEntregaItemAutorizacaoFornecimentoRN() {
		return scoProgEntregaItemAutorizacaoFornecimentoRN;
	}
	
	protected ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}

	protected ScoAutorizacaoFornJnRN getScoAutorizacaoFornJnRN() {
		return scoAutorizacaoFornJnRN;
	}
			
	protected ScoAutorizacaoFornecedorPedidoON getScoAutorizacaoFornecedorPedidoON() {
		return scoAutorizacaoFornecedorPedidoON;
	}
	
	protected ISiconFacade getSiconFacade() {
		return this.siconFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}	
