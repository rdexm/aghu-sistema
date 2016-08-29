package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Retorna 'S' ou 'N' caso haja MOVIMENTOS de medicamentos a serem impressos na
 * farmácia em questão ou não (igual a query do report afar_imp_mvto_prmd   
 * 
 * @ORADB MPMC_VER_FARM_MOV_ME
 * 
 * @author foliveira
 * 
 */
@Stateless
public class VerificarFarmaciaMovimentacaoMedicamentoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1591818903372253934L;
	private static final Log LOG = LogFactory.getLog(VerificarFarmaciaMovimentacaoMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * @param atdSeq
	 * @param dthrInicio
	 * @param dthrFim
	 * @param unfSeq
	 * @param dthrMovimento
	 * */
	public DominioSimNao verificarFarmaciaMovimentacaoMedicamento(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento){
		// C_VMP
		Boolean prescricaoMedicamento = verificarPrescricaoMedicamentos(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
		
		// C_PMD
		Boolean prescricaoItemMedicamento = verificarPrescricaoMedicamentos(atdSeq, dthrInicio, dthrFim, unfSeq, dthrMovimento);
		
		if(prescricaoMedicamento && prescricaoItemMedicamento){
			return DominioSimNao.N;
		}else{
			return DominioSimNao.S;
		}
	}
	
	/**
	 * C_VPM
	 * @param atdSeq
	 * @param dthrInicio
	 * @param dthrFim
	 * @param unfSeq
	 * @param dthrMovimento
	 * */
	public Boolean verificarPrescricaoMedicamentos(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento){
		Boolean result = Boolean.TRUE;
		return result;
	}
	
	/**
	 * C_PMD
	 * @param atdSeq
	 * @param dthrInicio
	 * @param dthrFim
	 * @param unfSeq
	 * @param dthrMovimento
	 * */
	public Boolean verificarPrescricaoItemMedicamentos(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento){
		Boolean result = Boolean.TRUE;
		return result;
	}	
	
}
