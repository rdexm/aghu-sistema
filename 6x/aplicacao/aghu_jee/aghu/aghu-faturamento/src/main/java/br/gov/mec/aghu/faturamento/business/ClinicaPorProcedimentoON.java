package br.gov.mec.aghu.faturamento.business;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatValorContaHospitalarDAO;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ClinicaPorProcedimentoON extends BaseBusiness {

	private static final long serialVersionUID = 4420573537469089547L;
	
	private static final Log LOG = LogFactory.getLog(ClinicaPorProcedimentoON.class);
	
	@EJB
	private ClinicaPorProcedimentoZIPRN clinicaPorProcedimentoZIPRN;
	
	@EJB
	private ClinicaPorProcedimentoUtil util;
	
	@Inject
	private FatValorContaHospitalarDAO fatValorContaHospitalarDAO;
	
	@Inject
	private FatContasHospitalaresDAO contasHospitalaresDAO;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ClinicaPorProcedimentoONExceptionCode implements BusinessExceptionCode {
		ERRO_REL_CLIN_POR_PROCED_COMPETENCIA;
	}

	private Byte obterCodAtoOPM() throws BaseException {
		return util.obterCodAtoOPM();	
	}
	
	public String geraRelCSVClinicaPorProcedimento(
			FatCompetencia competencia) throws BaseException {
		
		util.notNull(competencia, ClinicaPorProcedimentoONExceptionCode.ERRO_REL_CLIN_POR_PROCED_COMPETENCIA);

		return clinicaPorProcedimentoZIPRN.gerarClinicaPorProcedimentoZIP(competencia, DominioModuloCompetencia.INT, obterCodAtoOPM());
	}
	
	public TotalGeralClinicaPorProcedimentoVO obterTotalGeralRelatorio(
			FatCompetencia competencia) throws BaseException {
		
		util.notNull(competencia, ClinicaPorProcedimentoONExceptionCode.ERRO_REL_CLIN_POR_PROCED_COMPETENCIA);
		
		TotalGeralClinicaPorProcedimentoVO vo =  fatValorContaHospitalarDAO
				.obterTotalGeralClinicaPorProcedimento(DominioModuloCompetencia.INT, competencia.getId()
						.getMes(), competencia.getId().getAno(), competencia.getId().getDtHrInicio());
		
		util.processarTotalGeralClinicaPorProcedimentoVO(vo);
		
		return vo;
		
	}

	public Collection<ClinicaPorProcedimentoVO> recupearColecaoRelatorioClinicaPorProcedimento(FatCompetencia competencia) throws BaseException {
		util.notNull(competencia, ClinicaPorProcedimentoONExceptionCode.ERRO_REL_CLIN_POR_PROCED_COMPETENCIA);
		
		Collection<ClinicaPorProcedimentoVO> listVO = contasHospitalaresDAO.listarClinicaPorProcedimento(
				competencia.getId().getDtHrInicio(), 
				DominioModuloCompetencia.INT, 
				competencia.getId().getMes().byteValue(), 
				competencia.getId().getAno().shortValue(), obterCodAtoOPM());
		
		util.processarClinicaPorProcedimentoVO(listVO);
		
		return listVO;
	}


}
