package br.gov.mec.aghu.estoque.business;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.vo.GerarNotaRecebimentoVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemRmsId;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #6558 - Gerar nota de recebimento
 * @author lsamberg
 *
 */
@Stateless
public class GerarNotaRecebimentoON extends BaseBusiness{


@EJB
private SceItemRmsRN sceItemRmsRN;

@EJB
private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;

@EJB
private SceReqMateriaisRN sceReqMateriaisRN;

@EJB
private GerarSolicitacaoCompraAutomaticaRN gerarSolicitacaoCompraAutomaticaRN;

@EJB
private SceNotaRecebimentoRN sceNotaRecebimentoRN;

@EJB
private SceTipoMovimentosRN sceTipoMovimentosRN;

private static final Log LOG = LogFactory.getLog(GerarNotaRecebimentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@Inject
private SceItemNotaRecebimentoDevolucaoFornecedorDAO sceItemNotaRecebimentoDevolucaoFornecedorDAO;

@EJB
private IAutFornecimentoFacade autFornecimentoFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3501438079231454712L;

	public enum GerarNotaRecebimentoONExceptionCode implements BusinessExceptionCode {
		GERAR_NOTA_ERRO_SALDO_INSUFICIENTE;
	}


	private void preGerarNotaRecebimento(SceNotaRecebimento notaRecebimento) throws ApplicationBusinessException {
		
		List<SceItemNotaRecebimento> itens = getSceItemNotaRecebimentoDAO().pesquisarItensNotaRecebimentoPorNotaRecebimento(notaRecebimento);
		
		for(SceItemNotaRecebimento item : itens){
			verificaSaldoAssinadoAutorizacaoFornecedor(item);
		}
		
		//ORADB VERIFICA_SALDO_EMPENHO Implementar
		//verificaSaldoEmpenho();
	}
	
	/**
	 * ORADB VERIFICA_SALD_ASSINADO_AF
	 * Verifica se existe Saldo Assinado para entrada do material.
	 * @param notaRecebimento
	 * @throws ApplicationBusinessException 
	 */
	protected void verificaSaldoAssinadoAutorizacaoFornecedor(SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException {
		Double valorAssinadoItem = 0.0;
		Double percVarPreco = 0.0;
		Integer sequenciaAlteracao = 0;
//		Double vlrNrItem = 0.0;
//		Double valorEmDf = 0.0;
		
//		Double valorEfetivadoItem = 0.0;
//		Double valorAssinadoConsiderado = 0.0;
//		Double valorTotalNr = 0.0;
		
		ScoItemAutorizacaoFornJn itemAutorizacaoFornJn = getComprasFacade().obterValorAssinadoPorItemNotaPorItemAutorizacaoForn(itemNotaRecebimento.getItemAutorizacaoForn());
		
		if(itemAutorizacaoFornJn != null){
			//decode(fsc.tipo,'C',((iafjn.qtde_solicitada - iafjn.qtde_recebida) * iafjn.valor_unitario),iafjn.valor_unitario)
			List<ScoFaseSolicitacao> fases = getComprasFacade().pesquisarFasePorIafAfnNumeroIafNumero(itemAutorizacaoFornJn.getAfnNumero(),itemAutorizacaoFornJn.getNumero());
			ScoFaseSolicitacao fase = fases != null && !fases.isEmpty() ? fases.get(0) : null;
			if(fase != null){
				if(DominioTipoFaseSolicitacao.C.equals(fase.getTipo())){
					valorAssinadoItem = (itemAutorizacaoFornJn.getQtdeSolicitada() - itemAutorizacaoFornJn.getQtdeRecebida()) * itemAutorizacaoFornJn.getValorUnitario();
				}else{
					valorAssinadoItem = itemAutorizacaoFornJn.getValorUnitario();
				}
			}
			
			percVarPreco = itemAutorizacaoFornJn.getPercVarPreco();
			//TODO ajustar o codigo abaixo, poiis o metodo getScoAfJn está comentado no POJO
			//sequenciaAlteracao = itemAutorizacaoFornJn.getScoAfJn().getSequenciaAlteracao() != null ? itemAutorizacaoFornJn.getScoAfJn().getSequenciaAlteracao().intValue() : 0;
			sequenciaAlteracao = 0;
		}
		
//		vlrNrItem = 
				getSceItemNotaRecebimentoDAO().pesquisarValorEmNrPorItemAutorizacaoForn(itemNotaRecebimento.getItemAutorizacaoForn());
		 
//		valorEmDf = 
				getSceItemNotaRecebimentoDevolucaoFornecedorDAO().pesquisaValorEmDfPorItemAutorizacaoForn(itemNotaRecebimento);
		
//		valorEfetivadoItem = vlrNrItem - valorEmDf;
		
		// v_valor_assinado_considerado := (v_valor_assinado_item + (v_valor_assinado_item *  (v_perc_var_preco/100)));
//		valorAssinadoConsiderado = (valorAssinadoItem + (valorAssinadoItem * (percVarPreco/100)));
//		
//		valorTotalNr = valorEfetivadoItem + (itemNotaRecebimento.getValor() != null ? itemNotaRecebimento.getValor() : 0.0);
		
		if(itemNotaRecebimento.getValor() > valorAssinadoItem){
			throw new ApplicationBusinessException(GerarNotaRecebimentoONExceptionCode.GERAR_NOTA_ERRO_SALDO_INSUFICIENTE,itemNotaRecebimento.getItemAutorizacaoForn().getId().getNumero(), itemNotaRecebimento.getValor(), valorAssinadoItem, sequenciaAlteracao, percVarPreco);
		}
		
	}

	/**
	 * Gerar Nota de Recebimento 
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public void gerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException{
		this.preGerarNotaRecebimento(notaRecebimento);
		this.getSceNotaRecebimentoRN().inserir(notaRecebimento, true);
		this.posGerarNotaRecebimento(notaRecebimento, nomeMicrocomputador);
	}
	
	/**
	 * Gerar Nota de Recebimento com Solicitação de Compra Automática
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public void gerarNotaRecebimentoSolicitacaoCompraAutomatica(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException{
		
		// Gera uma Solicitação de Compra Automática através de uma Nota de Recebimento
		this.getGerarSolicitacaoCompraAutomaticaRN().gerarSolicitacaoCompraAutomaticaNotaRecebimento(notaRecebimento);
		
		/*
		 * Insere Nota de Recebimento com Solicitação de Compra Automática
		 * Obs. Método relacionado com a tarefa #16137 Gerar Nota de Recebimento sem AF
		 */
		this.getSceNotaRecebimentoRN().inserirComSolicitacaoDeCompraAutomatica(notaRecebimento);
		
		this.posGerarNotaRecebimento(notaRecebimento, nomeMicrocomputador);
	}


	/**
	 * 
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	private void posGerarNotaRecebimento(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		this.gerarRmPorNr(notaRecebimento, nomeMicrocomputador);
	}
	
	/**
	 * ORADB RN_RMSP_GERA_RM_NR
	 * Gerar RM para material de rtese/Prótese a partir de uma NR.
	 * @param notaRecebimento
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void gerarRmPorNr(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException{
		//BigDecimal grupoMatOrdProt = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_ORT_PROT).getVlrNumerico();
		BigDecimal grupoMatPeqPorte = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_PEQ_PORTE).getVlrNumerico();
		BigDecimal grupoMatPatrimonio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_PATRIMONIO).getVlrNumerico();
		// originalmente buscava de P_FORNECEDOR_HCPA
//		BigDecimal frnNumeroHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NUMERO_FORNECEDOR_HU).getVlrNumerico();
		BigDecimal tmvSeq = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_RM).getVlrNumerico();
		SceTipoMovimento tipoMovimentos = getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tmvSeq.shortValue());
//		Byte tmvComplemento = tipoMovimentos.getId().getComplemento();
		Double valorUnitario = 0.0;
		
		Integer codigoSolicAnt = 0;
		Integer codigoAplicAnt = 0;
		SceReqMaterial rmsSeq = null;
		BigDecimal valorLimitePeqPorte = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VLR_LIM_MAT_PEQ_PORTE).getVlrNumerico();
		Boolean gerarNovaRM = false;
		Integer qtdeRms = 0;
		
		valorLimitePeqPorte = valorLimitePeqPorte.divide(new BigDecimal("100"));
		
		List<SceItemNotaRecebimento> itens = getSceItemNotaRecebimentoDAO().pesquisarItensNotaRecebimentoPorNotaRecebimento(notaRecebimento);
		
		for(SceItemNotaRecebimento item : itens){
			
			List<ScoFaseSolicitacao> fasesSolicitacao = getComprasFacade().pesquisarFasePorIafAfnNumeroIafNumero(item.getItemAutorizacaoForn().getId().getAfnNumero(),item.getItemAutorizacaoForn().getId().getNumero());
			ScoFaseSolicitacao fase = new ScoFaseSolicitacao();
			if(fasesSolicitacao != null && !fasesSolicitacao.isEmpty()){
				fase = fasesSolicitacao.get(0);
			}else{
				continue;
			}
			//complementando regras da query
			if(fase.getExclusao()){
				continue;
			}
			List<SceEstoqueAlmoxarifado> estoqueAlmoxarifados = getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorMaterialAlmoxarifadoFornecedor(item.getMaterial().getCodigo(), item.getMaterial().getAlmoxarifado().getSeq(), 1);
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = null;
			if(estoqueAlmoxarifados == null || estoqueAlmoxarifados.isEmpty()){
				continue;
			}else{
				estoqueAlmoxarifado = estoqueAlmoxarifados.get(0);
			}
			
			ScoGrupoMaterial itemGrupoMaterial = item.getMaterial().getGrupoMaterial();
			if(!itemGrupoMaterial.getCodigo().equals(grupoMatPeqPorte.intValue()) && !itemGrupoMaterial.getCodigo().equals(grupoMatPatrimonio.intValue())){
				continue;
			}else{
				valorUnitario = item.getValor() / item.getQuantidade();
				if(valorUnitario > valorLimitePeqPorte.doubleValue()){
					continue;
				}
			}
			
			FccCentroCustos fccCentroCustos= fase.getSolicitacaoDeCompra().getCentroCusto();
			FccCentroCustos fccCentroAplica= fase.getSolicitacaoDeCompra().getCentroCustoAplicada();
			if(!fccCentroCustos.getCodigo().equals(codigoSolicAnt) || !fccCentroAplica.getCodigo().equals(codigoAplicAnt)){
				if(rmsSeq != null){
					rmsSeq.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
					getSceReqMateriaisRN().atualizar(rmsSeq, nomeMicrocomputador);
					rmsSeq.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
					getSceReqMateriaisRN().atualizar(rmsSeq, nomeMicrocomputador);
				}
				
				codigoSolicAnt = fccCentroCustos.getCodigo();
				codigoAplicAnt = fccCentroAplica.getCodigo();
				gerarNovaRM = true;
				qtdeRms = 0;
			}
			
			if(gerarNovaRM){
				SceReqMaterial reqMateriaisNovo = new SceReqMaterial();
				reqMateriaisNovo.setAlmoxarifado(item.getMaterial().getAlmoxarifado());
				reqMateriaisNovo.setCentroCusto(fase.getSolicitacaoDeCompra().getCentroCusto());
				reqMateriaisNovo.setCentroCustoAplica(fase.getSolicitacaoDeCompra().getCentroCustoAplicada());
				reqMateriaisNovo.setGrupoMaterial(item.getMaterial().getGrupoMaterial());
				reqMateriaisNovo.setTipoMovimento(tipoMovimentos);
				reqMateriaisNovo.setDtGeracao(new Date());
				reqMateriaisNovo.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
				reqMateriaisNovo.setEstorno(false);
				reqMateriaisNovo.setIndImpresso(DominioImpresso.N);
				reqMateriaisNovo.setNomeImpressora("");
				getSceReqMateriaisRN().inserir(reqMateriaisNovo);
				
				gerarNovaRM = false;
				qtdeRms++;
				rmsSeq = reqMateriaisNovo;
			}
			
			estoqueAlmoxarifado.setQtdeBloqueada(estoqueAlmoxarifado.getQtdeBloqueada() - item.getQuantidade());
			estoqueAlmoxarifado.setQtdeDisponivel(estoqueAlmoxarifado.getQtdeDisponivel() + item.getQuantidade());
			getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmoxarifado, nomeMicrocomputador, true);
			
			SceItemRms itemRms = new SceItemRms();
			SceItemRmsId itemRmsId = new SceItemRmsId();
			itemRmsId.setRmsSeq(rmsSeq.getSeq());
			itemRmsId.setEalSeq(estoqueAlmoxarifado.getSeq());
			itemRms.setId(itemRmsId);
			itemRms.setScoUnidadeMedida(estoqueAlmoxarifado.getUnidadeMedida());
			itemRms.setQtdeRequisitada(item.getQuantidade());
			itemRms.setIndTemEstoque(true);
			getSceItemRmsRN().inserir(itemRms, nomeMicrocomputador);
		}
		
		if(qtdeRms > 0){
			rmsSeq.setIndSituacao(DominioSituacaoRequisicaoMaterial.C);
			getSceReqMateriaisRN().atualizar(rmsSeq, nomeMicrocomputador);
			rmsSeq.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
			getSceReqMateriaisRN().atualizar(rmsSeq, nomeMicrocomputador);
		}
		
	}
	
	public List<GerarNotaRecebimentoVO> pesquisarAutorizacoesFornecimentoPorSeqDescricao(Object param) {

		// Lista com os resultados (parcial e final) da consulta principal
		List<ScoItemAutorizacaoForn> listaConsulta = this.getAutFornecimentoFacade().pesquisarAutorizacoesFornecimentoPorSeqDescricao(param);
		List<GerarNotaRecebimentoVO> listaRetornoVO = new LinkedList<GerarNotaRecebimentoVO>();
		
		if(listaConsulta != null && !listaConsulta.isEmpty()){
			
			for (ScoItemAutorizacaoForn item : listaConsulta) {
				
				GerarNotaRecebimentoVO vo = new GerarNotaRecebimentoVO();
				
				vo.setItemAutorizacaoForn(item);
				vo.setAutorizacaoForn(item.getAutorizacoesForn());

				List<ScoFaseSolicitacao> l = item.getScoFaseSolicitacao();
				if(!l.isEmpty()){
					
					ScoFaseSolicitacao faseSolicitacao = l.get(0);
					
					ScoMaterial material = faseSolicitacao.getSolicitacaoDeCompra() != null ? faseSolicitacao.getSolicitacaoDeCompra().getMaterial() : null;
					ScoServico servico =  faseSolicitacao.getSolicitacaoServico() != null ?  faseSolicitacao.getSolicitacaoServico().getServico() : null;
					
					// Para fase de solicitação de compra do tipo Compra/Material 
					if(material != null ){
						
						vo.setCodigoMaterialServico(material.getCodigo());
						vo.setMaterialServico(material.getNome());
						vo.setUnidade(material.getUnidadeMedida());
						
					} else{ // Para fase de solicitação de serviço
					
						vo.setCodigoMaterialServico(servico.getCodigo());
						vo.setMaterialServico(servico.getNome());

					}
				
					/*
					 * É necessária a presença de registros na solicitação de compras para serviço ou material,
					 * caso contrário, o registro será retirado da consulta
					 */
					if (material != null || servico != null){
						if(listaRetornoVO.size() >= 100){
							break;
						}
						listaRetornoVO.add(vo);
					}
					
				}

			}
		}
		
		return listaRetornoVO;
	}
	
	/**
	 * Getters para RNs e DAOs
	 */

	public SceNotaRecebimentoRN getSceNotaRecebimentoRN(){
		return sceNotaRecebimentoRN;
	}
	
	public SceItemRmsRN getSceItemRmsRN(){
		return sceItemRmsRN;
	}
	
	public SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}
	
	public SceTipoMovimentosRN getSceTipoMovimentosRN(){
		return sceTipoMovimentosRN;
	}
	
	public SceReqMateriaisRN getSceReqMateriaisRN(){
		return sceReqMateriaisRN;
	}
	
	public SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO(){
		return sceItemNotaRecebimentoDAO;
	}
	
	public SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	public SceItemNotaRecebimentoDevolucaoFornecedorDAO getSceItemNotaRecebimentoDevolucaoFornecedorDAO(){
		return sceItemNotaRecebimentoDevolucaoFornecedorDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private IAutFornecimentoFacade getAutFornecimentoFacade(){
		return autFornecimentoFacade;
	}
	
	protected GerarSolicitacaoCompraAutomaticaRN getGerarSolicitacaoCompraAutomaticaRN(){
		return gerarSolicitacaoCompraAutomaticaRN;
	}

}