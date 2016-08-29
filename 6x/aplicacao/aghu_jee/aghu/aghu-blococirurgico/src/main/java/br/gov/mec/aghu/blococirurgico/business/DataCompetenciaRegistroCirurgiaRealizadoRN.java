package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DataCompetenciaRegistroCirurgiaRealizadoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DataCompetenciaRegistroCirurgiaRealizadoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	private static final long serialVersionUID = -6723795765814617530L;
	
	public enum CompetenciaRegistroCirurgiaRealizadoONExceptionCode implements BusinessExceptionCode {
		FAT_00564;
	}

	/**
	 * ORADB MBCP_POPULA_PHI.CUR_CPE pll
	 * @param origemPacienteCirurgia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date getValorDataCompetencia(
			DominioOrigemPacienteCirurgia origemPacienteCirurgia) throws ApplicationBusinessException {

		Date valorDtComp = null;
		List<FatCompetencia> competencia = this.getFaturamentoFacade()
				.pesquisarCompetenciaProcedimentoHospitalarInternoPorModulo(
						getDominioModuloCompetencia(origemPacienteCirurgia));

		if (!competencia.isEmpty()) {
			valorDtComp =  getDataCompetencia(competencia.get(0).getId().getMes(), competencia.get(0).getId().getAno());
		} else {
			throw new ApplicationBusinessException(CompetenciaRegistroCirurgiaRealizadoONExceptionCode.FAT_00564);
		}
		
		return valorDtComp;
	}

	private Date getDataCompetencia(final Integer mes, final Integer ano) {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, mes);
		calendar.set(Calendar.YEAR, ano);

		calendar.set(Calendar.HOUR_OF_DAY, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MILLISECOND, 00);

		return calendar.getTime();
	}

	private DominioModuloCompetencia getDominioModuloCompetencia(DominioOrigemPacienteCirurgia origemPacienteCirurgia) {

		if (DominioOrigemPacienteCirurgia.I.equals(origemPacienteCirurgia)) {
			return DominioModuloCompetencia.INT;
		} else if (DominioOrigemPacienteCirurgia.A.equals(origemPacienteCirurgia)) {
			return DominioModuloCompetencia.AMB;
		} else {
			return null;
		}
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}

}
