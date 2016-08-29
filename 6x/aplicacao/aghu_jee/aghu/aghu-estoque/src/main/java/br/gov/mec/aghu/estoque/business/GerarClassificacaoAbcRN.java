package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialAbcVO;
import br.gov.mec.aghu.estoque.vo.TabelaCurvaAbcVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE SCEP_GERA_CLAS_ABC
 * @author aghu
 *
 */
@Stateless
public class GerarClassificacaoAbcRN extends BaseBusiness {

	@EJB
	private ExecutarFechamentoEstoqueMensalRN executarFechamentoEstoqueMensalRN;
	
	private static final Log LOG = LogFactory.getLog(GerarClassificacaoAbcRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 296667802191924294L;
	
	/**
	 * ORADB PROCEDURE SCEP_GERA_CLAS_ABC
	 * @param dataCompetencia
	 * @param quantidadeMeses
	 * @throws BaseException
	 */
	public void gerarClassificacaoAbc(final Date dataCompetencia, final Integer quantidadeMeses) throws BaseException{

		/**
		 * Pesquisa movimentos de material para geração da classificação ABC
		 * Obs. este bloco foi retirado da procedure INICIALIZA_TAB_ABC para permitir
		 * o retorno do VO TabelaCurvaAbcVO através da lista de movimento de material
		 */
		List<MovimentoMaterialAbcVO> listaMovimentoMaterialAbcVO = this.getSceMovimentoMaterialDAO().pesquisarMovimentoMaterialCurvaAbc(dataCompetencia, quantidadeMeses);

		if(listaMovimentoMaterialAbcVO != null && !listaMovimentoMaterialAbcVO.isEmpty()){
			
			// Inicializa a tabela ABC, determina as faixas e calcula a classificacao ABC
			TabelaCurvaAbcVO curvaAbcVO = this.inicializarTabelaAbc(listaMovimentoMaterialAbcVO);
			this.determinarFaixas(curvaAbcVO);
			this.calcularAbc(dataCompetencia, listaMovimentoMaterialAbcVO, curvaAbcVO);
			
		}

	}
	
	/**
	 * ORADB PROCEDURE INICIALIZA_TAB_ABC
	 * @param listaMovimentoMaterialAbcVO
	 * @return
	 */
	protected TabelaCurvaAbcVO inicializarTabelaAbc(List<MovimentoMaterialAbcVO> listaMovimentoMaterialAbcVO){
	
		TabelaCurvaAbcVO tabelaCurvaAbcVO = null;
		
		if(listaMovimentoMaterialAbcVO != null && !listaMovimentoMaterialAbcVO.isEmpty()){
			
			tabelaCurvaAbcVO = new TabelaCurvaAbcVO();
			
			Double valorTotal = 0d;
			
			for (MovimentoMaterialAbcVO movimentoMaterialAbcVO : listaMovimentoMaterialAbcVO) {
				
				/**
				 * Recalcula o valor total do movimento material
				 * Obs. Está operação corresponde ao "ROUND(...) /3,2) VALOR" da consulta original
				 */
				final double valorMovimentoMaterialCalculado = this.calcularValorMovimentoMaterial(movimentoMaterialAbcVO);
				
				// Seta valor calculado no item atual de movimento de material
				movimentoMaterialAbcVO.setValor(valorMovimentoMaterialCalculado);
				
				// Acumula/Soma o valor total calculado
				valorTotal += valorMovimentoMaterialCalculado;

			}	
	
			// Seta valor total de todos os movimentos de materiais na tabela de curva ABC
			tabelaCurvaAbcVO.setValorTotal(valorTotal);
			
		}
		
		return tabelaCurvaAbcVO;

	}
	
	/**
	 * Calcula o valor do movimento material
	 * @param movimentoMaterialVO
	 * @return
	 */
	private Double calcularValorMovimentoMaterial(MovimentoMaterialAbcVO movimentoMaterialVO){
		
		final DominioIndOperacaoBasica operacaoBasica = DominioIndOperacaoBasica.valueOf(movimentoMaterialVO.getOperacaoBasica());
		final Double valor = movimentoMaterialVO.getValor() != null ? movimentoMaterialVO.getValor().doubleValue() : 0;
		final Boolean estorno = movimentoMaterialVO.getEstorno();
		
		final Double valorNaoEstorno = Boolean.FALSE.equals(estorno) ? (DominioIndOperacaoBasica.DB.equals(operacaoBasica) ? valor : valor * -1) : 0;
		final Double valorEstorno = Boolean.TRUE.equals(estorno) ? (DominioIndOperacaoBasica.DB.equals(operacaoBasica) ? valor : valor * -1) : 0;
		
		final Double calculo = (valorNaoEstorno - valorEstorno) / 3;
		final BigDecimal bigDecimal = new BigDecimal(calculo.toString()).setScale(2, RoundingMode.HALF_UP);
		
		return bigDecimal.doubleValue();
	}

	/**
	 * ORADB PROCEDURE DETERMINA_FAIXAS
	 * @param tabelaCurvaAbcVO
	 * @return
	 * @throws BaseException
	 */
	protected TabelaCurvaAbcVO determinarFaixas(TabelaCurvaAbcVO tabelaCurvaAbcVO) throws BaseException{

		// Resgata valor total
		Double valorTotal = tabelaCurvaAbcVO.getValorTotal();
		
		// Busca o parâmetro de classificação da faixa A
		final AghParametros parametroFaixaClassA = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FAIXA_CLASS_A);
		
		// Busca o parâmetro de classificação da faixa B
		final AghParametros parametroFaixaClassB = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FAIXA_CLASS_B);
		
		// A divisão por 100 é parte da FUNCTION GET_PARAM_FAIXA que não foi migrada
		Double valorFaixaA = parametroFaixaClassA.getVlrNumerico().doubleValue() / 100; // Atenção na divisão por 100!
		Double valorFaixaB = parametroFaixaClassB.getVlrNumerico().doubleValue() / 100;
		
		Double valorA = valorFaixaA * valorTotal;
		Double valorB = valorFaixaB * valorTotal;
		
		Double valorAA = valorFaixaA * valorA;
		Double valorAB = valorFaixaB * valorA;
		
		Double valorBA = valorA + valorFaixaA * (valorB - valorA);
		Double valorBB = valorA + valorFaixaB * (valorB - valorA);
		
		Double valorCA = valorB + valorFaixaA * (valorTotal - valorB);
		Double valorCB = valorB + valorFaixaB * (valorTotal - valorB);

		// Seta valores determinados...
		tabelaCurvaAbcVO.setValorFaixaA(valorFaixaA);
		tabelaCurvaAbcVO.setValorFaixaB(valorFaixaB);
		tabelaCurvaAbcVO.setValorA(valorA);
		tabelaCurvaAbcVO.setValorB(valorB);
		tabelaCurvaAbcVO.setValorAA(valorAA);
		tabelaCurvaAbcVO.setValorAB(valorAB);
		tabelaCurvaAbcVO.setValorBA(valorBA);
		tabelaCurvaAbcVO.setValorBB(valorBB);
		tabelaCurvaAbcVO.setValorCA(valorCA);
		tabelaCurvaAbcVO.setValorCB(valorCB);
		
		return tabelaCurvaAbcVO;
	}
	
	/**
	 * ORADB PROCEDURE CALCULA_ABC
	 * @param dataCompetencia
	 * @param listaMovimentoMaterialAbcVO
	 * @param tabelaCurvaAbcVO
	 * @throws BaseException
	 */
	protected void calcularAbc(final Date dataCompetencia, List<MovimentoMaterialAbcVO> listaMovimentoMaterialAbcVO, TabelaCurvaAbcVO tabelaCurvaAbcVO) throws BaseException{
		
		Double valorAcumulado = 0D;
		
		Double valorA = tabelaCurvaAbcVO.getValorA();
		Double valorB = tabelaCurvaAbcVO.getValorB();
		Double valorAA = tabelaCurvaAbcVO.getValorAA();
		Double valorAB = tabelaCurvaAbcVO.getValorAB();
		Double valorBA = tabelaCurvaAbcVO.getValorBA();
		Double valorBB = tabelaCurvaAbcVO.getValorBB();
		Double valorCA = tabelaCurvaAbcVO.getValorCA();
		Double valorCB = tabelaCurvaAbcVO.getValorCB();
		
		for (MovimentoMaterialAbcVO movimentoMaterialAbcVO : listaMovimentoMaterialAbcVO) {
			
			valorAcumulado += movimentoMaterialAbcVO.getValor() != null ? movimentoMaterialAbcVO.getValor().doubleValue() : 0;
			
			DominioClassifABC classificacaoAbc = null;
			DominioClassifABC subClassificacaoAbc = null;

			if(valorAcumulado <= valorA){
				
				classificacaoAbc = DominioClassifABC.A;
				
				if(valorAcumulado < valorAA) {
					subClassificacaoAbc = DominioClassifABC.A;
				} else if (valorAcumulado > valorAA && valorAcumulado < valorAB){
					subClassificacaoAbc = DominioClassifABC.B;
				} else{
					subClassificacaoAbc = DominioClassifABC.C;
				}
				
			} else if(valorAcumulado > valorA && valorAcumulado < valorB) {
				
				classificacaoAbc = DominioClassifABC.B;
				
				if(valorAcumulado < valorBA) {
					subClassificacaoAbc = DominioClassifABC.A;
				} else if (valorAcumulado > valorBA && valorAcumulado < valorBB){
					subClassificacaoAbc = DominioClassifABC.B;
				} else{
					subClassificacaoAbc = DominioClassifABC.C;
				}
				
			} else{
				
				classificacaoAbc = DominioClassifABC.C;
				
				if(valorAcumulado < valorCA) {
					subClassificacaoAbc = DominioClassifABC.A;
				} else if (valorAcumulado > valorCA && valorAcumulado < valorCB){
					subClassificacaoAbc = DominioClassifABC.B;
				} else{
					subClassificacaoAbc = DominioClassifABC.C;
				}
				
			}

			/*
			 * Chamada para PROCEDURE ATUALIZA_ESTQ_GERAIS
			 * Atualização em estoque gerais
			 */
			this.atualizarEstoqueGerais(dataCompetencia, movimentoMaterialAbcVO.getCodigoMaterial(), classificacaoAbc, subClassificacaoAbc);
		}

	}
	
	/**
	 * ORADB PROCEDURE ATUALIZA_ESTQ_GERAIS
	 * @param dataCompetencia
	 * @param material
	 * @param classificacaoAbc
	 * @param subClassificacaoAbc
	 * @throws BaseException
	 */
	protected void atualizarEstoqueGerais(final Date dataCompetencia, final Integer codigoMaterial, final DominioClassifABC classificacaoAbc, final DominioClassifABC subClassificacaoAbc) throws BaseException{
		
		// Obtém transação de usuário
		//UserTransaction userTransaction = null;
		
		// Atualiza geral estoque por material e data de competência do mês seguinte ao fechamento de estoque
		this.getSceEstoqueGeralDAO().atualizarEstoqueGeralFechamentoMensalEstoqueClassificacaoAbc(dataCompetencia, codigoMaterial, classificacaoAbc, subClassificacaoAbc);
		//this.getExecutarFechamentoEstoqueMensalRN().commitUserTransaction(userTransaction); // COMMIT
		

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

	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO(){
		return sceEstoqueGeralDAO;
	}
	
	protected ExecutarFechamentoEstoqueMensalRN getExecutarFechamentoEstoqueMensalRN() {
		return executarFechamentoEstoqueMensalRN;
	}
	
}
