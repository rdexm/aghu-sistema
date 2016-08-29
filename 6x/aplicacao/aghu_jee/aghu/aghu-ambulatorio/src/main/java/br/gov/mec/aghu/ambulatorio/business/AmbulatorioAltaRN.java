package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioRetornoAgenda;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * Migração da package ambulatório alta
 * 
 *  ORADB: MAMK_ALTA
 * 
 * @author dansantos
 */
@Stateless
public class AmbulatorioAltaRN extends BaseBusiness {





private static final Log LOG = LogFactory.getLog(AmbulatorioAltaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8362633515599592636L;


	/**
	 * FUNCTION
	 * ORADB: mamc_bloqueio_alta
	 */
	public DominioRetornoAgenda verificarTipoRetornoAgenda(Integer codigoPaciente, Short especialidadeSeq){
		//TODO implementar método na migração da package
		return null;
	}
	
	/**
	 * FUNCTION
	 * ORADB: mamc_ver_alta_pac
	 */
	public Integer verificarAltaPaciente(Integer codigoPaciente, Short especialidadeSeq){
		//TODO implementar método na migração da package
		return null;
	}
	
	
	/**
	 * FUNCTION
	 * ORADB: mamc_get_nm_res_alta
	 */
	public String obterNomeProfessor(Integer altaSumarioSeq){
		//TODO implementar método na migração da package
		return null;
	}
}
