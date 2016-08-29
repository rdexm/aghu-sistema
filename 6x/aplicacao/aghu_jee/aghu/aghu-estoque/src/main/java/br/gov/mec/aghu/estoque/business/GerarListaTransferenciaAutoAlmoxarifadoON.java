package br.gov.mec.aghu.estoque.business;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoTransferenciaAutomaticaDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria  - Gerar lista de transferências automáticas entre almoxarifados
 *
 */
@Stateless
public class GerarListaTransferenciaAutoAlmoxarifadoON extends BaseBusiness{


@EJB
private SceItemTransferenciaRN sceItemTransferenciaRN;

@EJB
private SceTransferenciaRN sceTransferenciaRN;

private static final Log LOG = LogFactory.getLog(GerarListaTransferenciaAutoAlmoxarifadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemTransferenciaDAO sceItemTransferenciaDAO;

@Inject
private SceTransferenciaDAO sceTransferenciaDAO;

@Inject
private SceAlmoxarifadoTransferenciaAutomaticaDAO sceAlmoxarifadoTransferenciaAutomaticaDAO;

@EJB
private IComprasFacade comprasFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1549117358041960352L;

	public enum GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ALMOXARIFADOS_IGUAIS,SCE_00766,SCE_00670,SCE_00671;
	}

	/**
	 * 
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 */
	private void prePersistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws ApplicationBusinessException {
		
		this.validarAlmoxarifados(transferencia); // RN1
		this.validarNumeroCn5(transferencia); // RN2
		
		// RN3: EVT_PRE_INSERT
		transferencia.setTransferenciaAutomatica(Boolean.TRUE);
		transferencia.setEfetivada(Boolean.FALSE);
		transferencia.setEstorno(Boolean.FALSE);
		
	} 
	
	/**
	 * Persite transferência automática entre almoxarifados
	 * @param transferencia
	 * @throws BaseException
	 */
	public void persistirTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException{
		this.prePersistirTransferenciaAutoAlmoxarifado(transferencia);
		this.getSceTransferenciaRN().inserir(transferencia);
	}
	
	/**
	 * Remove transferência automática entre almoxarifados
	 * @param transferencia
	 * @throws BaseException
	 */
	public void removerTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException{
		this.getSceTransferenciaRN().remover(transferencia);
	}
	
	/**
	 * Remove transferências automáticas NÃO efetivadas no almoxarifado de destino
	 * @param transferencia
	 * @throws BaseException
	 */
	public void removerTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento,Long numeroClassifMatNiv5) throws BaseException{
		
		List<SceTransferencia> listaTransferenciaAutomaticaNaoEfetivadaDestino = this.getSceTransferenciaDAO().pesquisarTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);
		
		for (SceTransferencia transferencia : listaTransferenciaAutomaticaNaoEfetivadaDestino) {
			this.getSceTransferenciaRN().remover(transferencia);
		}

	}
	

	/**
	 * Gera lista de itens de transferência automatica
	 * @param transferencia
	 * @throws BaseException
	 */
	public void gerarListaTransferenciaAutoAlmoxarifado(SceTransferencia transferencia) throws BaseException{
		this.validarMaterialEstoqueMinimo(transferencia);
	}
	
	/**
	 * Remove item de transferência automática
	 * @param itemTransferencia
	 * @throws BaseException
	 */
	public void removerItemTransferenciaAutoAlmoxarifado(SceItemTransferencia itemTransferencia) throws BaseException {
		this.getSceItemTransferenciaRN().remover(itemTransferencia);
	}

	/*
	 * Métodos para preGerarListaTransferenciaAutoAlmoxarifado
	 */
	
	/**
	 * Valida os almoxarifados de origem e recebimento
	 */
	protected void validarAlmoxarifados(SceTransferencia transferencia) throws ApplicationBusinessException {
		
		// Verifica se o almoxarifado de origem é igual ao de recebimento
		if(transferencia.getAlmoxarifado().equals(transferencia.getAlmoxarifadoRecebimento())){
			throw new ApplicationBusinessException(GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.MENSAGEM_ALMOXARIFADOS_IGUAIS);
		}
		
		final Short almSeq =  transferencia.getAlmoxarifado().getSeq();
		final Short almSeqRecebe = transferencia.getAlmoxarifadoRecebimento().getSeq();
		
		final SceAlmoxarifadoTransferenciaAutomatica transferenciaAutomatica = this.getSceAlmoxarifadoTransferenciaAutomaticaDAO().obterAlmoxarifadoTransferenciaAutomaticaPorAlmoxarifadoOrigemDestino(almSeq, almSeqRecebe);
		
		// Verifica se a transferência entre almoxarifados foi prevista	e está ativa	
		if(transferenciaAutomatica == null){
			throw new ApplicationBusinessException(GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00670);
		} else if (DominioSituacao.I.equals(transferenciaAutomatica.getSituacao())){
			throw new ApplicationBusinessException(GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00766);
		}
		
	}
	
	/**
	 * ORADB PROCEDURE EVT_WHEN_VALIDATE_ITEM
	 */
	public void validarNumeroCn5(SceTransferencia transferencia) throws ApplicationBusinessException {
		
		if(transferencia.getClassifMatNiv5() != null && transferencia.getClassifMatNiv5().getNumero() < 100){
			final String cn5NumeroModificado = transferencia.getClassifMatNiv5().getNumero() + "0000000000";
			transferencia.getClassifMatNiv5().setNumero(Long.parseLong(cn5NumeroModificado));
		}
		
	}
	
	/**
	 * ORADB PROCEDURE LEIT_MAT_EM_ESTQ_MIN
	 * Testa a existência da transferência e a existência de materiais em estoque mínimo no almoxarifado destino
	 */
	public void validarMaterialEstoqueMinimo(SceTransferencia transferencia) throws BaseException {
		
		/* Obs. sobre a RN1: O teste da existência de transferência automática não efetivada 
		 * para o almoxarifado de destino foi replicado na tela, logo extraído da "procedure"!
		 */
		
		// Valida e localiza níveis de classificação, insere itens de transferência
		this.validarNiveisClassificacaoInformados(transferencia); // RN2
	}
	
	/**
	 * Testa a existência de transferência automática não efetivada para o almoxarifado de destino
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 */
	/*public Boolean existeTransferenciaAutomaticaNaoEfetivadaDestino(SceTransferencia transferencia) {
		
		final Integer seqTransferencia = transferencia.getSeq();
		final Short seqAlmoxarifado = transferencia.getAlmoxarifado().getSeq();
		final Short seqAlmoxarifadoRecebimento = transferencia.getAlmoxarifadoRecebimento().getSeq();
		Integer codigoClassifMatNiv5 = null;
		if(transferencia.getClassifMatNiv5() != null){
			codigoClassifMatNiv5 = transferencia.getClassifMatNiv5().getCodigo();
		}

		return this.getSceTransferenciaDAO().existeTransferenciaAutomaticaNaoEfetivadaDestino(seqTransferencia, seqAlmoxarifado, seqAlmoxarifadoRecebimento, codigoClassifMatNiv5);

	}*/
	

	/**
	 * Testa a existência de transferência automática não efetivada para o almoxarifado de destino
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 */
	public Boolean existeTransferenciaAutomaticaNaoEfetivadaDestino(final Short seqAlmoxarifado, final Short seqAlmoxarifadoRecebimento, final Long numeroClassifMatNiv5) {
		return this.getSceTransferenciaDAO().existeTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);
	}
	
	/**
	 * Valida e localiza níveis de classificação para o número classificação informado e valida 
	 * materiais com saldo inferior ou equivalente ao estoque mínimo no almoxarifado de destino.
	 * @param transferencia
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void validarNiveisClassificacaoInformados(SceTransferencia transferencia) throws BaseException {
		
		// Pesquisa o código de classificação da transferência
		final ScoClassifMatNiv5 classifMatNiv5 = this.getComprasFacade().obterClassifMatNiv5PorNumero(transferencia.getClassifMatNiv5().getNumero());
		
		if(classifMatNiv5 != null){
			
			Long valorCodigo = 0L;
			
			if(classifMatNiv5.getCodigo() == 0){
				valorCodigo = 99L;
			}

			if((classifMatNiv5.getScoClassifMatNiv4() != null) && classifMatNiv5.getScoClassifMatNiv4().getId().getCodigo() == 0){
				valorCodigo = valorCodigo + 9900L;
			}
			
			if((classifMatNiv5.getScoClassifMatNiv4() != null && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3() != null) && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getId().getCodigo() == 0){
				valorCodigo = valorCodigo + 9900L;
			}
			
			if((classifMatNiv5.getScoClassifMatNiv4() != null && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3() != null && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2() != null) && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getId().getCodigo() == 0){
				 valorCodigo = valorCodigo + 99000000L;
			}
			
			if((classifMatNiv5.getScoClassifMatNiv4() != null && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3() != null && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2() != null && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getScoClassifMatNiv1() != null) && classifMatNiv5.getScoClassifMatNiv4().getScoClassifMatNiv3().getScoClassifMatNiv2().getScoClassifMatNiv1().getId().getCodigo() == 0){
				 valorCodigo = valorCodigo + 9900000000L;
			}
			
			// Constantes locais para o cálculo da classificação
			final Long valorClassificacaoMaterialInicial = classifMatNiv5.getNumero();
			final Long valorClassificacaoMaterialFinal = valorClassificacaoMaterialInicial + valorCodigo;
			
			// Constantes locais para as consultas de classificação de materiais
			final AghParametros parametroHospital = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NUMERO_FORNECEDOR_HU);
			final Integer numeroFornecedorHospital = parametroHospital.getVlrNumerico().intValue();
			final Short seqAlmoxarifadoOrigem = transferencia.getAlmoxarifado().getSeq();
			final Short seqAlmoxarifadoRecebe = transferencia.getAlmoxarifadoRecebimento().getSeq();
			
			List<SceEstoqueAlmoxarifado> listaMaterialClassificados = null;
			List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifados = null;
			
			//  Verifica se a classificação inicial é DIFERENTE da classificação geral de material expediente!
			if(valorClassificacaoMaterialInicial != 40000000000L){

				listaMaterialClassificados = this.getSceEstoqueAlmoxarifadoDAO().pesquisarMaterialSaldoMenorIgualEstoqueMinimoAlmoxarifadoDestino(numeroFornecedorHospital, seqAlmoxarifadoOrigem, seqAlmoxarifadoRecebe, valorClassificacaoMaterialInicial,valorClassificacaoMaterialFinal);
				
				// Classifica e seleciona estoque almoxarifados através do cálculo das quantidades
				listaEstoqueAlmoxarifados = this.classificarListaEstoqueAlmoxarifadoPorQuantidades(listaMaterialClassificados);

			} else{ // Classificação geral de material expediente...
				
				// TODO utilizar após criacao do parametro GRUPOS_VALIDOS_MATERIAL_EXPEDIENTE
				// Obtem o intervalo de grupo de materiais válidos para a pesquisa de material expediente
				//AghParametros paramGruposMaterialValidos = getAghuFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPOS_VALIDOS_MATERIAL_EXPEDIENTE);
				List<Integer> intervaloGruposMaterialValidos = new LinkedList<Integer>();
				/*for (String valor : paramGruposMaterialValidos.getVlrTexto().split(",")) {
					intervaloGruposMaterialValidos.add(Integer.valueOf(valor));
				}*/
	
				// Pequisa de material expediente
				listaMaterialClassificados = this.getSceEstoqueAlmoxarifadoDAO().pesquisarMaterialClassificacaoGeralMaterialExpediente(numeroFornecedorHospital, seqAlmoxarifadoOrigem, seqAlmoxarifadoRecebe, intervaloGruposMaterialValidos);
			
				// Classifica e seleciona estoque almoxarifados através do cálculo das quantidades
				listaEstoqueAlmoxarifados = this.classificarListaEstoqueAlmoxarifadoPorQuantidades(listaMaterialClassificados);

			}
			
			// Valida a lista de estoque almoxarifado classificado
			if(listaEstoqueAlmoxarifados == null || listaEstoqueAlmoxarifados.isEmpty()){
				throw new ApplicationBusinessException(GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00671);
			}
			
			// Percorre lista de estoque almoxarifado classificado, gera itens de transferência e insere os mesmos
			this.inserirNiveisClassificacaoInformados(transferencia, seqAlmoxarifadoOrigem, listaEstoqueAlmoxarifados);
			
		} else{
			throw new ApplicationBusinessException(GerarListaTransferenciaAutoAlmoxarifadoONExceptionCode.SCE_00671);
		}
	}
	
	/**
	 * Classifica e seleciona estoque almoxarifados através do cálculo das quantidades
	 * @param listaOrigem
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private List<SceEstoqueAlmoxarifado> classificarListaEstoqueAlmoxarifadoPorQuantidades(List<SceEstoqueAlmoxarifado> listaOrigem){
		
		List<SceEstoqueAlmoxarifado> listaClassificada = new LinkedList<SceEstoqueAlmoxarifado>();
		
		if(listaOrigem != null){
			
			for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaOrigem) {
				   
				Integer quantidadeDisponivel = estoqueAlmoxarifado.getQtdeDisponivel() != null ? estoqueAlmoxarifado.getQtdeDisponivel() : 0;
				Integer quantidadeBloqueada = estoqueAlmoxarifado.getQtdeBloqueada() != null ? estoqueAlmoxarifado.getQtdeBloqueada() : 0;
				Integer quantidadeBloqueadaEntradaTransferencia = estoqueAlmoxarifado.getQtdeBloqEntrTransf() != null ? estoqueAlmoxarifado.getQtdeBloqEntrTransf() : 0;
				
				Integer quantidadeEstoqueMinima = estoqueAlmoxarifado.getQtdeEstqMin() != null ? estoqueAlmoxarifado.getQtdeEstqMin() : 0;
				
				if(quantidadeEstoqueMinima > 0 && (quantidadeDisponivel + quantidadeBloqueada + quantidadeBloqueadaEntradaTransferencia) <= quantidadeEstoqueMinima){
					listaClassificada.add(estoqueAlmoxarifado);
				}

			}	
		}

		return listaClassificada;
	}
	
	/**
	 * Percorre a lista de estoque almoxarifado classificado, gera itens de transferência e insere os mesmos
	 * 
	 * @param transferencia
	 * @param seqAlmoxarifadoOrigem
	 * @param listaEstoqueAlmoxarifadosClassificados
	 */
	private void inserirNiveisClassificacaoInformados(SceTransferencia transferencia, final Short seqAlmoxarifadoOrigem, List<SceEstoqueAlmoxarifado> listaEstoqueAlmoxarifadosClassificados) throws BaseException{
		
		
		/*
		 * Atenção reutilizar este trecho no code review
		 */
		
		// Percorre lista de estoque almoxarifado classificado
		for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : listaEstoqueAlmoxarifadosClassificados) {
			
			final Integer valorQuantidadeEnviar = this.calcularQuantidadeDiferencaEstoqueMinimo(estoqueAlmoxarifado);
			
			if (valorQuantidadeEnviar > 0) {
				
				SceItemTransferencia itemTransferencia = new SceItemTransferencia();
		
				itemTransferencia.setTransferencia(transferencia);

				final Integer codigoMaterial = estoqueAlmoxarifado.getMaterial().getCodigo(); // ITR.DSP_MAT_CODIGO
				final Integer numeroFornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();  // ITR.DSP_UMD_CODIGO

				// Obtem o estoque almoxarifado 
				SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem = this.getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoOrigemSaldoMenorIgualEstoqueMinimoPorMaterialFornecedor(seqAlmoxarifadoOrigem, codigoMaterial, numeroFornecedor);
				
				itemTransferencia.setEstoqueAlmoxarifadoOrigem(estoqueAlmoxarifadoOrigem); // ITR.EAL_SEQ_ORIG
				itemTransferencia.setEstoqueAlmoxarifado(estoqueAlmoxarifado); // ITR.EAL_SEQ
				itemTransferencia.setUnidadeMedida(estoqueAlmoxarifado.getUnidadeMedida());
				
				// Almoxarifado com reposição diferenciada (Somente completa o mínimo)
				Integer diasEstqMinimo = estoqueAlmoxarifado.getAlmoxarifado()!=null&&estoqueAlmoxarifado.getAlmoxarifado().getDiasEstqMinimo()!=null?estoqueAlmoxarifado.getAlmoxarifado().getDiasEstqMinimo():0;
				
				if (diasEstqMinimo > 0) {
				
					itemTransferencia.setQuantidade(valorQuantidadeEnviar); // ITR.QUANTIDADE
				
				} else {
					
					itemTransferencia.setQuantidade(estoqueAlmoxarifado.getQtdeEstqMin()); //ITR.QUANTIDADE
				
				}
				
				itemTransferencia.setTransferencia(transferencia);
				
				
				if (getSceItemTransferenciaDAO().obterItemTransferencia(itemTransferencia) == null) {
					// Insere um item de transferência
					this.getSceItemTransferenciaRN().inserir(itemTransferencia);
				}
				
				
			} else if (estoqueAlmoxarifado.getAlmoxarifado() != null
					&& estoqueAlmoxarifado.getAlmoxarifado().getDiasEstqMinimo() != null
						&& estoqueAlmoxarifado.getAlmoxarifado().getDiasEstqMinimo() == 0) {
				
				SceItemTransferencia itemTransferencia = new SceItemTransferencia();
				itemTransferencia.setTransferencia(transferencia);
				
				final Integer codigoMaterial = estoqueAlmoxarifado.getMaterial().getCodigo();
				final Integer numeroFornecedor = estoqueAlmoxarifado.getFornecedor().getNumero();
				
				SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem = this.getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoOrigemSaldoMenorIgualEstoqueMinimoPorMaterialFornecedor(seqAlmoxarifadoOrigem, codigoMaterial, numeroFornecedor);
			
				itemTransferencia.setEstoqueAlmoxarifadoOrigem(estoqueAlmoxarifadoOrigem);
				itemTransferencia.setEstoqueAlmoxarifado(estoqueAlmoxarifado);
				itemTransferencia.setUnidadeMedida(estoqueAlmoxarifado.getUnidadeMedida());
				itemTransferencia.setQuantidade(estoqueAlmoxarifado.getQtdeEstqMin());
				
				if (getSceItemTransferenciaDAO().obterItemTransferencia(itemTransferencia) == null) {
					// Insere um item de transferência
					this.getSceItemTransferenciaRN().inserir(itemTransferencia);
				}
				
			}
			
		}
		
	}
	
	/**
	 * Calcula a quantidade sugerida para transferência automática
	 * @param estoqueAlmoxarifado
	 * @return
	 */
	protected final Integer calcularQuantidadeDiferencaEstoqueMinimo(SceEstoqueAlmoxarifado estoqueAlmoxarifado){

		Integer qtdeEstqMin = estoqueAlmoxarifado.getQtdeEstqMin();
		Integer qtdeDisponivel = estoqueAlmoxarifado.getQtdeDisponivel();
		Integer qtdeBloqueada = estoqueAlmoxarifado.getQtdeBloqueada();
		Integer qtdeBloqEntrTransf = estoqueAlmoxarifado.getQtdeBloqEntrTransf();
		
		qtdeEstqMin = qtdeEstqMin != null ? qtdeEstqMin : 0;
		qtdeDisponivel = qtdeDisponivel != null ? qtdeDisponivel : 0;
		qtdeBloqueada = qtdeBloqueada != null ? qtdeBloqueada : 0;
		qtdeBloqEntrTransf = qtdeBloqEntrTransf != null ? qtdeBloqEntrTransf : 0;

		return qtdeEstqMin - (qtdeDisponivel + qtdeBloqueada + qtdeBloqEntrTransf);
		
	}
	
	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public SceTransferenciaRN getSceTransferenciaRN(){
		return sceTransferenciaRN;
	}
	
	public SceItemTransferenciaRN getSceItemTransferenciaRN(){
		return sceItemTransferenciaRN;
	}
	
	public SceAlmoxarifadoTransferenciaAutomaticaDAO getSceAlmoxarifadoTransferenciaAutomaticaDAO(){
		return sceAlmoxarifadoTransferenciaAutomaticaDAO;
	}
	
	protected SceTransferenciaDAO getSceTransferenciaDAO(){
		return sceTransferenciaDAO;
	}

	public SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	public SceItemTransferenciaDAO getSceItemTransferenciaDAO(){
		return sceItemTransferenciaDAO;
	}

}