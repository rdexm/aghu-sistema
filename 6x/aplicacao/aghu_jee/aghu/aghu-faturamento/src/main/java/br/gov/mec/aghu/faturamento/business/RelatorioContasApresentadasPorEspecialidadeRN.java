package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.dao.FatAtoMedicoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatCampoMedicoAuditAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.ContaApresentadaPacienteProcedimentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioContasApresentadasPorEspecialidadeRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8819518903346077958L;
	private static final Log LOG = LogFactory.getLog(RelatorioContasApresentadasPorEspecialidadeRN.class);

	@Inject
	private FatContasHospitalaresDAO fatContasHospitalares;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FatAtoMedicoAihDAO fatAtoMedicoAihDAO;

	@Inject
	private FatCampoMedicoAuditAihDAO fatCampoMedicoAuditAihDAO;

	
	/**
	 * RN 01 
	 * @param tipoTributo
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<ContaApresentadaPacienteProcedimentoVO> obterContaApresentadaEspecialidade(Short codigoEspecialidade, FatCompetencia competencia) throws ApplicationBusinessException {
		List<ContaApresentadaPacienteProcedimentoVO> lista = fatContasHospitalares.obterContasPorEspecialidade(codigoEspecialidade, competencia.getId().getDtHrInicio(), competencia.getId().getMes().byteValue(), competencia.getId().getAno().shortValue());
		
		if(lista != null && !lista.isEmpty()) {
			AghParametros opme = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COD_ATO_OPM);
			
			for(ContaApresentadaPacienteProcedimentoVO vo : lista) {
				
				List<ContaApresentadaPacienteProcedimentoVO> atos = fatAtoMedicoAihDAO.buscarAtosMedicos(opme.getVlrNumerico().intValue(),  vo.getCthSeq());
				List<ContaApresentadaPacienteProcedimentoVO> campos = fatCampoMedicoAuditAihDAO.buscarCamposMedicos(vo.getCthSeq());
				vo.setProcedimentos(new ArrayList<ContaApresentadaPacienteProcedimentoVO>(0));

				if(atos != null && !atos.isEmpty()) {
					vo.getProcedimentos().addAll(atos);
				}

				if(campos != null && !campos.isEmpty()) {
					vo.getProcedimentos().addAll(campos);
				}
			}
		}
		
		return lista;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}