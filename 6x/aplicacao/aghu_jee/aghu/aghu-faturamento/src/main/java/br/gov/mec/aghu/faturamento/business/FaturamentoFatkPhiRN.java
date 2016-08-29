package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * ORADB Package FATK_PHI_RN
 * 
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class FaturamentoFatkPhiRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(FaturamentoFatkPhiRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatProcedHospInternosDAO fatProcedHospInternosDAO;

	private static final long serialVersionUID = 954977845146336106L;

	/**
	 * ORADB Procedure RN_PHIC_VER_ATIVO - VERIFICA SE REGISTRO ESTA ATIVO
	 * @param seq Identificador do Procedimento Hospitalar Interno
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean rnPhicVerAtivo(final Integer seq) throws ApplicationBusinessException {
		FatProcedHospInternos phi = getFatProcedHospInternosDAO().obterPorChavePrimaria(seq);
		return rnPhicVerAtivo(phi);
	}
	
	/**
	 * ORADB Procedure RN_PHIC_VER_ATIVO - VERIFICA SE REGISTRO ESTA ATIVO
	 * @param phi Procedimento Hospitalar Interno
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean rnPhicVerAtivo(FatProcedHospInternos phi) throws ApplicationBusinessException {
		if(phi == null || !DominioSituacao.A.equals(phi.getSituacao())){
			return Boolean.FALSE;
		}
		
		/*
		(P_SEQ IN NUMBER
		 )
		 RETURN BOOLEAN
		 IS
		cursor c_ativo is
		  select ind_situacao
		    from fat_proced_hosp_internos
		   where seq = p_seq;
		  v_sit fat_proced_hosp_internos.ind_situacao%type;
		  BEGIN
		     v_sit := null;
		     ---
		     open c_ativo;
		     fetch c_ativo into v_sit;
		     close c_ativo;
		     ---
		     if v_sit != 'A' then
		        return FALSE;
		     end if;
		     return TRUE;
		  END;
		  */
		return Boolean.TRUE;
	}
	
	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

}
