package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

@Stateless
public class ClinicaPorProcedimentoUtil extends BaseBusiness {

	private static final long serialVersionUID = 8913373862899091941L;

	private static final Log LOG = LogFactory.getLog(ClinicaPorProcedimentoUtil.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ClinicaPorProcedimentoUtilExceptionCode implements BusinessExceptionCode {
		ERRO_REL_CLIN_POR_PROCED_P_COD_ATO_OPM;
	}
	
	public BigDecimal somar(BigDecimal ... valores ){
		
		BigDecimal total = BigDecimal.ZERO;
		
		for (BigDecimal valor : valores) {
			total = total.add(nvlBigDecimalDefaultZero(valor));
		}
		
		return total;
	}
	
	public BigDecimal nvlBigDecimalDefaultZero(BigDecimal valor) {
		return (BigDecimal) CoreUtil.nvl(valor, BigDecimal.ZERO);
	}
	
	public String formatarMoeda(BigDecimal valor) {
		return AghuNumberFormat.formatarNumeroMoeda(valor);
	}
	
	private Long obterParametro(AghuParametrosEnum parametrosEnum, BusinessExceptionCode exceptionCode) throws BaseException {		
		try {
			return parametroFacade.buscarValorLong(parametrosEnum);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(exceptionCode);
		}		
	}
	
	public Byte obterCodAtoOPM() throws BaseException {
		return obterParametro(AghuParametrosEnum.P_COD_ATO_OPM, ClinicaPorProcedimentoUtilExceptionCode.ERRO_REL_CLIN_POR_PROCED_P_COD_ATO_OPM).byteValue();	
	}
	
	public void notNull(Object object, BusinessExceptionCode exceptionCode) throws ApplicationBusinessException {	
		if(object == null){
			throw new ApplicationBusinessException(exceptionCode);
		}
	}
	
	public void processarClinicaPorProcedimentoVO(Collection<ClinicaPorProcedimentoVO> lista){
		for (ClinicaPorProcedimentoVO vo : lista) {
			vo.setValorSadtRealiz(nvlBigDecimalDefaultZero(vo.getValorSadtRealiz()));
			vo.setValorShRealiz(nvlBigDecimalDefaultZero(vo.getValorShRealiz()));
			vo.setValorSpRealiz(nvlBigDecimalDefaultZero(vo.getValorSpRealiz()));
			vo.setSadtProc(nvlBigDecimalDefaultZero(vo.getSadtProc()));
			vo.setServHospProc(nvlBigDecimalDefaultZero(vo.getServHospProc()));
			vo.setServProfProc(nvlBigDecimalDefaultZero(vo.getServProfProc()));
			vo.setSadAih(nvlBigDecimalDefaultZero(vo.getSadAih()));
			vo.setHospAih(nvlBigDecimalDefaultZero(vo.getHospAih()));
			vo.setProfAih(nvlBigDecimalDefaultZero(vo.getProfAih()));
		}
	}

	public void processarTotalGeralClinicaPorProcedimentoVO(TotalGeralClinicaPorProcedimentoVO vo) {
		vo.setValorUti(nvlBigDecimalDefaultZero(vo.getValorUti()));
		vo.setValorAcomp(nvlBigDecimalDefaultZero(vo.getValorAcomp()));
		vo.setValorShUti(nvlBigDecimalDefaultZero(vo.getValorShUti()));
		vo.setValorSpUti(nvlBigDecimalDefaultZero(vo.getValorSpUti()));
		vo.setValorSadtUti(nvlBigDecimalDefaultZero(vo.getValorSadtUti()));
		vo.setValorShAcomp(nvlBigDecimalDefaultZero(vo.getValorShAcomp()));
		vo.setValorSpAcomp(nvlBigDecimalDefaultZero(vo.getValorSpAcomp()));
		vo.setValorSadtAcomp(nvlBigDecimalDefaultZero(vo.getValorSadtAcomp()));
	}

}
