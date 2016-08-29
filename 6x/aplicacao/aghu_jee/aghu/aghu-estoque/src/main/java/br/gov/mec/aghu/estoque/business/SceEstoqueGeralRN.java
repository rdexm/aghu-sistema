package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SceEstoqueGeralRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(SceEstoqueGeralRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@Inject
private SceEstoqueGeralDAO sceEstoqueGeralDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4908227089521896404L;

	public enum SceEstoqueGeralRNExceptionCode implements BusinessExceptionCode {
		SCE_00281,MSG_CUSTO_MEDIO_PONDERADO_MENOR_ZERO,MENSAGEM_ROTINA_FECHAMENTO_EM_EXECUCAO,MENSAGEM_PARAMETRO_TRATAMENTO_P_COMPETENCIA;
	}
	
	/*
	 * Métodos para Inserir SceEstoqueGeral
	 */
	
	/**
	 * ORADB SCET_EGR_BRI (INSERT)
	 * @param estoqueGeral
	 * @throws BaseException
	 */
	private void preInserir(SceEstoqueGeral estoqueGeral) throws BaseException{
		
		this.atualizarScoMaterialDataCompetencia(estoqueGeral); // RN1
		
		// Verifica se unidade medida é igual a do cadastro de Material
		this.validarUnidadeMedidaGrupoMaterial(estoqueGeral); // RN2
	}
	
	/**
	 * Inserir ScoMaterial
	 * @param estoqueGeral
	 * @throws BaseException
	 */
	public void inserir(SceEstoqueGeral estoqueGeral) throws BaseException{
		this.preInserir(estoqueGeral);
		this.getSceEstoqueGeralDAO().persistir(estoqueGeral);

	}
	
	/*
	 * RNs Inserir
	 */
	
	
	/**
	 * ORADB SCEK_EGR_RN.RN_EGRP_ATU_DT_COMP
	 * Atualiza a data de competência do sistema
	 * @param material
	 */
	protected void atualizarScoMaterialDataCompetencia(SceEstoqueGeral estoqueGeral) throws BaseException{
		
		final AghParametros parametroCompetencia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		final Date valorDataCompetencia = parametroCompetencia.getVlrData();
		
		final AghParametros parametroFechamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FECHAMENTO_MENSAL);
		final String valorFechamento = parametroFechamento.getVlrTexto();
		
		if("N".equalsIgnoreCase(valorFechamento)){
			if(valorDataCompetencia == null){ 
				throw new ApplicationBusinessException(SceEstoqueGeralRNExceptionCode.MENSAGEM_PARAMETRO_TRATAMENTO_P_COMPETENCIA);
			}
			estoqueGeral.getId().setDtCompetencia(valorDataCompetencia);
		}else{
			// Mensagem adicionada por Fábio (svn fwink) e NÃO foi fixado a issue
			throw new ApplicationBusinessException(SceEstoqueGeralRNExceptionCode.MENSAGEM_ROTINA_FECHAMENTO_EM_EXECUCAO);
		}
	}
	
	
	/**
	 * ORADB SCEK_EGR_RN.RN_EGRP_VER_UMD_MAT
	 * Verifica se unidade de medida é igual a do material
	 * @param estoqueGeral
	 */
	protected void validarUnidadeMedidaGrupoMaterial(SceEstoqueGeral estoqueGeral) throws BaseException{
		
		// TODO SCEK_SCE_RN.RN_SCEP_VER_UMD_MAT(p_new_mat_codigo, p_new_umd_codigo);

		final ScoUnidadeMedida unidadeMedidaEstoqueGeral =  estoqueGeral.getUnidadeMedida();
		final ScoUnidadeMedida unidadeMedidaMaterial = estoqueGeral.getMaterial().getUnidadeMedida();

		if(!unidadeMedidaEstoqueGeral.equals(unidadeMedidaMaterial)){
			throw new ApplicationBusinessException(SceEstoqueGeralRNExceptionCode.SCE_00281);
		}
	}
	
	/**
	 * Atualizar SceEstoqueGeral
	 * @param estoqueGeral
	 * @throws BaseException
	 */
	public void atualizar(SceEstoqueGeral estoqueGeral) throws BaseException{
		this.preAtualizar(estoqueGeral);
		this.getSceEstoqueGeralDAO().atualizar(estoqueGeral);
		this.getSceEstoqueGeralDAO().flush();

	}
	
	/**
	 * ORADB SCET_EGR_BRU (UPDATE)
	 * @param estoqueGeral
	 * @throws BaseException
	 */
	private void preAtualizar(SceEstoqueGeral estoqueGeral) throws BaseException {
		
		SceEstoqueGeral estoqueGeralOld = (SceEstoqueGeral) this.getSceEstoqueGeralDAO().obterOriginal(estoqueGeral);
		
		if (!estoqueGeral.getUnidadeMedida().equals(estoqueGeralOld.getUnidadeMedida())) {
			
			validarUnidadeMedidaGrupoMaterial(estoqueGeral);
			
		}
		
		if (estoqueGeral.getQtde() != null && estoqueGeral.getQtde() > 0) {
			
			if (!estoqueGeralOld.getValor().equals(estoqueGeral.getValor()) || !estoqueGeralOld.getQtde().equals(estoqueGeral.getQtde())) {
				
				if (estoqueGeral.getValor() != null && estoqueGeral.getQtde() != null) {
					
					//definir a formatacao para valor Custo Médio
					Double custoMedioCalculado = this.calcularCustoMedioPonderado(estoqueGeral.getValor(), estoqueGeral.getQtde());
					BigDecimal custoMedio = NumberUtil.truncateFLOOR(new BigDecimal(custoMedioCalculado), 4);
					custoMedio = NumberUtil.truncate(custoMedio, 4);
					estoqueGeral.setCustoMedioPonderado(custoMedio);
					
					//valor resultante para residuo
					Double valorDivisao = NumberUtil.truncateHALFEVEN(estoqueGeral.getQtde() * estoqueGeral.getCustoMedioPonderado().doubleValue(), 2);
					BigDecimal valorResiduo = new BigDecimal(estoqueGeral.getValor() - valorDivisao);
					estoqueGeral.setResiduo(valorResiduo.doubleValue());
					
				}
				
			}
			
		}
		
		if (estoqueGeral.getCustoMedioPonderado() != null && estoqueGeral.getCustoMedioPonderado().doubleValue() < 0) {
			
			throw new ApplicationBusinessException(SceEstoqueGeralRNExceptionCode.MSG_CUSTO_MEDIO_PONDERADO_MENOR_ZERO);
		
		}
		
	}

	/**
	 * Calcula custo médio ponderado
	 * @param valor
	 * @param quantidade
	 * @return
	 */
	public Double calcularCustoMedioPonderado(Double valor, Integer quantidade) {

		Double resultado = 0d;
		
		if(quantidade == null){
			quantidade = 0;
		}
		
		if(valor == null){
			valor = 0d;
		}

		if(quantidade != 0 && valor != 0) {
			resultado = valor / quantidade;
		}
		
		return resultado;
	}



	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO(){
		return sceEstoqueGeralDAO;
	}
	
}
