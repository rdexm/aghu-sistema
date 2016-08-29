package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalFornecedoresVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ProgramacaoEntregaGlobalFornecedoresON extends BaseBusiness {

	private static final long serialVersionUID = 7510890957753310547L;
	private static final Log LOG = LogFactory.getLog(ProgramacaoEntregaGlobalFornecedoresON.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	public List<ProgramacaoEntregaGlobalFornecedoresVO> listarProgramacaoEntregaGlobalFornecedores(Integer codigoGrupoMaterial, 
			Date dataInicial, Date dataFinal, String tipoValor, ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO, ScoFornecedor fornecedor) {

		if(dataInicial == null) {
			try {
				dataInicial = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL).getVlrData();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		if(dataFinal == null) {
			try {
				dataFinal = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL).getVlrData();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		EntregasGlobaisAcesso tipoValorEnum = null;
		if(tipoValor != null && !tipoValor.isEmpty()) {
			tipoValorEnum = EntregasGlobaisAcesso.getValue(tipoValor);
		}

		List<ProgramacaoEntregaGlobalFornecedoresVO> listaProgramacaoEntregaGlobalFornecedoresVO = getScoItemAutorizacaoFornDAO().listarProgramacaoEntregaGlobalFornecedores(codigoGrupoMaterial, dataInicial, dataFinal, tipoValorEnum, fornecedor);


		Double totalSaldoProgramado = Double.valueOf(0);
		Double totalValorALiberar = Double.valueOf(0);
		Double totalValorLiberado = Double.valueOf(0);
		Double totalValorEmAtraso = Double.valueOf(0);

		for (ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO : listaProgramacaoEntregaGlobalFornecedoresVO) {
			buscarValores(codigoGrupoMaterial, dataInicial,
					dataFinal, tipoValor,
					programacaoEntregaGlobalFornecedoresVO, tipoValorEnum);

			if(programacaoEntregaGlobalFornecedoresVO.getSaldoProgramado() != null) {
				totalSaldoProgramado += programacaoEntregaGlobalFornecedoresVO.getSaldoProgramado().doubleValue(); 
			}

			if(programacaoEntregaGlobalFornecedoresVO.getValorALiberar() != null) {
				totalValorALiberar += programacaoEntregaGlobalFornecedoresVO.getValorALiberar().doubleValue();
			}

			if(programacaoEntregaGlobalFornecedoresVO.getValorLiberado() != null) {
				totalValorLiberado += programacaoEntregaGlobalFornecedoresVO.getValorLiberado().doubleValue();
			}

			if(programacaoEntregaGlobalFornecedoresVO.getValorEmAtraso() != null) {
				totalValorEmAtraso += programacaoEntregaGlobalFornecedoresVO.getValorEmAtraso().doubleValue();
			}
		}

		List<ProgramacaoEntregaGlobalFornecedoresVO> listagemNova = validarLinhaRn2(
				programacaoEntregaGlobalTotalizadorVO,
				listaProgramacaoEntregaGlobalFornecedoresVO,
				totalSaldoProgramado, totalValorALiberar, totalValorLiberado,
				totalValorEmAtraso);
		
		Collections.sort(listagemNova);

		return listagemNova;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private List<ProgramacaoEntregaGlobalFornecedoresVO> validarLinhaRn2(ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO,
			List<ProgramacaoEntregaGlobalFornecedoresVO> listaProgramacaoEntregaGlobalFornecedoresVO,
			Double totalSaldoProgramado, Double totalValorALiberar,
			Double totalValorLiberado, Double totalValorEmAtraso) {
		programacaoEntregaGlobalTotalizadorVO.setTotalSaldoProgramado(BigDecimal.valueOf(totalSaldoProgramado));
		programacaoEntregaGlobalTotalizadorVO.setTotalValorALiberar(BigDecimal.valueOf(totalValorALiberar));
		programacaoEntregaGlobalTotalizadorVO.setTotalValorLiberado(BigDecimal.valueOf(totalValorLiberado));
		programacaoEntregaGlobalTotalizadorVO.setTotalValorEmAtraso(BigDecimal.valueOf(totalValorEmAtraso));
		List<ProgramacaoEntregaGlobalFornecedoresVO> listagemNova = new ArrayList<ProgramacaoEntregaGlobalFornecedoresVO>();
		Double saldoProgramado = new Double(0);
		Double valorALiberar = new Double(0);
		Double valorLiberado = new Double(0);
		Double valorEmAtraso = new Double(0);
		for (ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO : listaProgramacaoEntregaGlobalFornecedoresVO) {
			if(programacaoEntregaGlobalFornecedoresVO.getSaldoProgramado()!=null){
				saldoProgramado = programacaoEntregaGlobalFornecedoresVO.getSaldoProgramado().doubleValue();
			}
			if(programacaoEntregaGlobalFornecedoresVO.getValorALiberar()!=null){
				valorALiberar = programacaoEntregaGlobalFornecedoresVO.getValorALiberar().doubleValue();
			}
			if(programacaoEntregaGlobalFornecedoresVO.getValorLiberado()!=null){
				valorLiberado = programacaoEntregaGlobalFornecedoresVO.getValorLiberado().doubleValue();
			}
			if(programacaoEntregaGlobalFornecedoresVO.getValorEmAtraso()!=null){
				valorEmAtraso = programacaoEntregaGlobalFornecedoresVO.getValorEmAtraso().doubleValue();
			}
			if (saldoProgramado > 0 || valorALiberar > 0 || valorLiberado > 0 || valorEmAtraso > 0) {
				listagemNova.add(programacaoEntregaGlobalFornecedoresVO);
			}
		}
		return listagemNova;
	}

	private void buscarValores(Integer codigoGrupoMaterial,Date dataInicial,Date dataFinal,
			String tipoValor,ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO,
			EntregasGlobaisAcesso tipoValorEnum) {
		BigDecimal saldoProgramado = BigDecimal.ZERO;
		BigDecimal valorALiberar = BigDecimal.ZERO;
		BigDecimal valorLiberado = BigDecimal.ZERO;
		BigDecimal valorEmAtraso = BigDecimal.ZERO;
		BigDecimal valor = BigDecimal.ZERO;


		if(tipoValor == null || tipoValor.isEmpty()) {
			tipoValorEnum = EntregasGlobaisAcesso.SALDO_PROGRAMADO;
			saldoProgramado = getScoItemAutorizacaoFornDAO().obterProgramacaoEntregaGlobalFornecedoresTipoValor(codigoGrupoMaterial, dataInicial, dataFinal, 
					programacaoEntregaGlobalFornecedoresVO.getNumeroFornecedor(), tipoValorEnum);

			tipoValorEnum = EntregasGlobaisAcesso.VALOR_LIBERAR;
			valorALiberar = getScoItemAutorizacaoFornDAO().obterProgramacaoEntregaGlobalFornecedoresTipoValor(codigoGrupoMaterial, dataInicial, dataFinal, 
					programacaoEntregaGlobalFornecedoresVO.getNumeroFornecedor(), tipoValorEnum);

			tipoValorEnum = EntregasGlobaisAcesso.VALOR_LIBERADO;
			valorLiberado = getScoItemAutorizacaoFornDAO().obterProgramacaoEntregaGlobalFornecedoresTipoValor(codigoGrupoMaterial, dataInicial, dataFinal, 
					programacaoEntregaGlobalFornecedoresVO.getNumeroFornecedor(), tipoValorEnum);

			tipoValorEnum = EntregasGlobaisAcesso.VALOR_ATRASO;
			valorEmAtraso = getScoItemAutorizacaoFornDAO().obterProgramacaoEntregaGlobalFornecedoresTipoValor(codigoGrupoMaterial, dataInicial, dataFinal, 
					programacaoEntregaGlobalFornecedoresVO.getNumeroFornecedor(), tipoValorEnum);

			programacaoEntregaGlobalFornecedoresVO.setSaldoProgramado(saldoProgramado);
			programacaoEntregaGlobalFornecedoresVO.setValorALiberar(valorALiberar);
			programacaoEntregaGlobalFornecedoresVO.setValorLiberado(valorLiberado);
			programacaoEntregaGlobalFornecedoresVO.setValorEmAtraso(valorEmAtraso);
		} else {
			valor = getScoItemAutorizacaoFornDAO().obterProgramacaoEntregaGlobalFornecedoresTipoValor(codigoGrupoMaterial, dataInicial, dataFinal, 
					programacaoEntregaGlobalFornecedoresVO.getNumeroFornecedor(), tipoValorEnum);

			if(tipoValorEnum == EntregasGlobaisAcesso.SALDO_PROGRAMADO) {
				programacaoEntregaGlobalFornecedoresVO.setSaldoProgramado(valor);
			} else if(tipoValorEnum == EntregasGlobaisAcesso.VALOR_LIBERAR) {
				programacaoEntregaGlobalFornecedoresVO.setValorALiberar(valor);
			} else if(tipoValorEnum == EntregasGlobaisAcesso.VALOR_LIBERADO) {
				programacaoEntregaGlobalFornecedoresVO.setValorLiberado(valor);
			} else if(tipoValorEnum == EntregasGlobaisAcesso.VALOR_ATRASO) {
				programacaoEntregaGlobalFornecedoresVO.setValorEmAtraso(valor);
			}
		}
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected void setScoItemAutorizacaoFornDAO(ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO) {
		this.scoItemAutorizacaoFornDAO = scoItemAutorizacaoFornDAO;
	}
	
	

}
