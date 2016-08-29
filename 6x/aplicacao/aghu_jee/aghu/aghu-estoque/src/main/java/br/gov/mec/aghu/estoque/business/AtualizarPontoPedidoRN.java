package br.gov.mec.aghu.estoque.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.AtualizarPontoPedidoVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialAbcVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE SCEP_ATU_PTO_PEDIDO
 * @author aghu
 *
 */
@Stateless
public class AtualizarPontoPedidoRN extends BaseBusiness {

	@EJB
	private CalcularMediaPonderadaRN calcularMediaPonderadaRN;
	@EJB
	private ExecutarFechamentoEstoqueMensalRN executarFechamentoEstoqueMensalRN;
	
	private static final Log LOG = LogFactory.getLog(AtualizarPontoPedidoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1247798640894020973L;
	
	/**
	 * ORADB PROCEDURE SCEP_ATU_PTO_PEDIDO
	 */
	public void atualizarPontoPedido(final Date dataCompetencia, final Integer codigoMaterial) throws BaseException{

		// Almoxarifado CENTRAL, atualiza neste o ponto pedido e o tempo de reposição
		final AghParametros parametroAlmoxarifadoCentral = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL);	
		
		// Obtém fornecedor HU ou PADRÃO
		final AghParametros parametroFornecedorHU = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		// Pesquisa os materiais conforme critério único estoque
		final Short seqAlmoxarifadoCentral = parametroAlmoxarifadoCentral.getVlrNumerico().shortValue();
		final Integer numeroFornecedor = parametroFornecedorHU.getVlrNumerico().intValue();
		
		// Obtém almoxarifado CENTRAL
		final SceAlmoxarifado almoxarifadoCentral = this.getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeq(seqAlmoxarifadoCentral);

		// Obtém fornecedor HU ou PADRÃO	
		final ScoFornecedor fornecedorHu = this.getComprasFacade().obterFornecedorPorNumero(numeroFornecedor);
		
		// Pesquisa estoque almoxarifado para geração de ponto pedido
		List<AtualizarPontoPedidoVO> listaEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoAtualizarPontoPedido(codigoMaterial, seqAlmoxarifadoCentral, numeroFornecedor);

		if(listaEstoqueAlmoxarifado != null && !listaEstoqueAlmoxarifado.isEmpty()){

			// Obtém transação de usuário
		//	UserTransaction userTransaction = null;
			
			for (AtualizarPontoPedidoVO pontoPedidoVO : listaEstoqueAlmoxarifado) {
		
				// Calcular a quantidade consumo no ÚLTIMO SEMESTRE...
				final Integer quantidadeConsumoMes1 = this.calcularQuantidadeConsumoMesAtual(dataCompetencia, pontoPedidoVO.getMatCodigo()); // Atenção: Aqui é o código do material da consulta e não o do parâmetro do método!
				final Integer quantidadeConsumoMes2 = this.calcularQuantidadeConsumoMes(dataCompetencia, pontoPedidoVO.getMatCodigo(), 1);
				final Integer quantidadeConsumoMes3 = this.calcularQuantidadeConsumoMes(dataCompetencia, pontoPedidoVO.getMatCodigo(), 2);
				final Integer quantidadeConsumoMes4 = this.calcularQuantidadeConsumoMes(dataCompetencia, pontoPedidoVO.getMatCodigo(), 3);
				final Integer quantidadeConsumoMes5 = this.calcularQuantidadeConsumoMes(dataCompetencia, pontoPedidoVO.getMatCodigo(), 4);
				final Integer quantidadeConsumoMes6 = this.calcularQuantidadeConsumoMes(dataCompetencia, pontoPedidoVO.getMatCodigo(), 5);
				
				// Calcula a média do mês para quantidade consumo
				Integer valorMediaMeses = 0;
				if(Boolean.TRUE.equals(pontoPedidoVO.getCalculaMediaPonderada())){
					// Chamada para função que calcula a média ponderada: FUNCTION SCEC_MEDIA_PONDERADA
					valorMediaMeses = this.getCalcularMediaPonderadaRN().calcularMediaPonderada(quantidadeConsumoMes1, quantidadeConsumoMes2, quantidadeConsumoMes3, quantidadeConsumoMes4, quantidadeConsumoMes5, quantidadeConsumoMes6);
				} else {
					valorMediaMeses = (quantidadeConsumoMes1 + quantidadeConsumoMes2 + quantidadeConsumoMes3 + quantidadeConsumoMes4 + quantidadeConsumoMes5 + quantidadeConsumoMes6) / 6;
				}

				// Calcula a média do dia para quantidade consumo
				Integer valorMediaDia = valorMediaMeses / 30; // Calcula o Consumo de 1 dia
				
				// Calcula o valor da quantidade do ponto pedido
				Integer quantidadePontoPedido = null;
				final Integer tempoReposicao = pontoPedidoVO.getTempoReposicao() != null ? pontoPedidoVO.getTempoReposicao().intValue() : 0;
				
				if(tempoReposicao == 0){
					
					if(valorMediaDia > 1){
						quantidadePontoPedido = valorMediaDia;
					} else{
						quantidadePontoPedido = 1;
					}
					
				} else{
					
					quantidadePontoPedido = tempoReposicao * valorMediaDia;
				
					if(quantidadePontoPedido < 1){
						quantidadePontoPedido = 1;
					}
					
				}
				
				// Testa o intervalo válido da quantidade do ponto pedido
				if(quantidadePontoPedido >= 1 && quantidadePontoPedido <= 9999999){

					this.getSceEstoqueAlmoxarifadoDAO().atualizarEstoqueAlmoxarifadoFechamentoMensalEstoqueQuantidadePontoPedido(quantidadePontoPedido, pontoPedidoVO.getSeq());
					//this.getExecutarFechamentoEstoqueMensalRN().commitUserTransaction(userTransaction); // COMMIT
		
					// Se o local de estoque não é Almoxarifado CENTRAL, atualiza neste o ponto pedido e o tempo de reposição
					if (!seqAlmoxarifadoCentral.equals(pontoPedidoVO.getAlmSeq())){
						
						this.getSceEstoqueAlmoxarifadoDAO().atualizarEstoqueAlmoxarifadoFechamentoMensalEstoqueQuantidadePontoPedidoAlmoxarifadoCentral(quantidadePontoPedido, almoxarifadoCentral.getSeq(), pontoPedidoVO.getMatCodigo(), fornecedorHu.getNumero());
						//this.getExecutarFechamentoEstoqueMensalRN().commitUserTransaction(userTransaction); // COMMIT
					}
					
				}

			}
			
			
		}
		

		
	}

	/**
	 * 
	 * @param dataCompetencia
	 * @param codigoMaterial
	 * @return
	 * @throws BaseException
	 */
	protected Integer calcularQuantidadeConsumoMesAtual(final Date dataCompetencia, final Integer codigoMaterial) throws BaseException{
		return calcularQuantidadeConsumoMes(dataCompetencia, codigoMaterial, 0);
	}
	
	/**
	 * 
	 * @param dataCompetencia
	 * @param codigoMaterial
	 * @throws BaseException
	 */
	protected Integer calcularQuantidadeConsumoMes(final Date dataCompetencia, final Integer codigoMaterial, final int valorMesSubtraido) throws BaseException{
		
		Date novaDataCompetencia = dataCompetencia;
		
		if(valorMesSubtraido != 0){
			// Subtrai mês na data de competência
			novaDataCompetencia = this.subtrairMesDataCompetencia(dataCompetencia, valorMesSubtraido);
		}
		
		// Valida data de competência
		novaDataCompetencia = this.validarMesDataCompetencia(novaDataCompetencia);
		
		// Pesquisa movimentos de material para geração de ponto pedido
		List<MovimentoMaterialAbcVO> listaMovimentoMaterialAbcVO = this.getSceMovimentoMaterialDAO().pesquisarMovimentoMaterialAtualizarPontoPedido(novaDataCompetencia,codigoMaterial);

		Integer quantidadeConsumoMesAtual = 0;
		
		for (MovimentoMaterialAbcVO movimentoMaterialAbcVO : listaMovimentoMaterialAbcVO) {
			
			/**
			 * Recalcula a quantidade total do movimento material
			 */
			final Integer valorMovimentoMaterialCalculado = this.calcularQuantidadeConsumoMesMovimentoMaterial(movimentoMaterialAbcVO);
			
			// Acumula/Soma o valor total calculado
			quantidadeConsumoMesAtual += valorMovimentoMaterialCalculado;
		}
		
		return quantidadeConsumoMesAtual;
		
	}
	
	/**
	 *  Subtrai mês na data de competência
	 * @param dataCompetencia
	 * @param valorMesSubtraido
	 * @return
	 */
	protected Date subtrairMesDataCompetencia(final Date dataCompetencia, final int valorMesSubtraido){
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataCompetencia);
		calendar.add(Calendar.MONTH, -(valorMesSubtraido));
		
		return calendar.getTime();
	}
	
	/**
	 * Valida data de competência (com mês subtraído)
	 * Obs. A quantidade de mêses padrão do AGH é 88
	 * @param dataCompetencia
	 * @param valorMesSubtraido
	 * @return
	 */
	protected Date validarMesDataCompetencia(final Date dataCompetencia) throws BaseException{

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataCompetencia);

		SimpleDateFormat formatador = new SimpleDateFormat("yyyyMM");
		final String valorMesCompetencia = formatador.format(dataCompetencia).substring(4,6);
		
		if(Integer.parseInt(valorMesCompetencia) == 0){

			final AghParametros parametroQuantidadeMesesSubtraido = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_QUANTIDADE_MESES_SUBTRAIDOS_PONTO_PEDIDO);	
			calendar.add(Calendar.MONTH, -(parametroQuantidadeMesesSubtraido.getVlrNumerico().intValue()));
			
		}
		
		return calendar.getTime();
		
	}
	
	/**
	 * Calcula a quantidade do movimento material
	 * @param movimentoMaterialVO
	 * @return
	 */
	private Integer calcularQuantidadeConsumoMesMovimentoMaterial(MovimentoMaterialAbcVO movimentoMaterialAbcVO){

		final DominioIndOperacaoBasica operacaoBasica = DominioIndOperacaoBasica.valueOf(movimentoMaterialAbcVO.getOperacaoBasica());
		final Integer quantidade = movimentoMaterialAbcVO.getQuantidade() != null ? movimentoMaterialAbcVO.getQuantidade() : 0;
		final Boolean estorno = movimentoMaterialAbcVO.getEstorno();

		final Integer somaQuantidade = Boolean.FALSE.equals(estorno) ? (DominioIndOperacaoBasica.DB.equals(operacaoBasica) ? quantidade : quantidade * -1) : 0;
		final Integer somaQuantidadeEstorno = Boolean.TRUE.equals(estorno) ? (DominioIndOperacaoBasica.DB.equals(operacaoBasica) ? quantidade : quantidade * -1) : 0;

		return (somaQuantidade - somaQuantidadeEstorno);
	}

	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}
	
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected CalcularMediaPonderadaRN getCalcularMediaPonderadaRN() {
		return calcularMediaPonderadaRN;
	}

	protected ExecutarFechamentoEstoqueMensalRN getExecutarFechamentoEstoqueMensalRN() {
		return executarFechamentoEstoqueMensalRN;
	}
	
}
