package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObitoNecropsiaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObitoNecropsiaRN extends BaseBusiness {


@EJB
private ManterObtCausaDiretaRN manterObtCausaDiretaRN;

@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterObitoNecropsiaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmObitoNecropsiaDAO mpmObitoNecropsiaDAO;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2071662543811172614L;

	public enum ManterObitoNecropsiaRNExceptionCode implements BusinessExceptionCode {

		MPM_02742,ERRO_INSERIR_OBITO_NECROPSIA,ERRO_ALTERAR_OBITO_NECROPSIA;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}
	
	
	
	/**
	 * ORADB TRIGGER MPMT_ONE_BRI
	 * @param ObitoNecropsia
	 */
	public void inserirObitoNecropsia(MpmObitoNecropsia obitoNecropsia) throws ApplicationBusinessException {
		
		this.preInserirObitoNecropsia(obitoNecropsia);
		this.getMpmObitoNecropsiaDAO().persistir(obitoNecropsia);
		this.getMpmObitoNecropsiaDAO().flush();
		
	}
	
	/**
	 * ORADB TRIGGER MPMT_ONE_BRI
	 * @param ObitoNecropsia
	 */
	public void atualizarObitoNecropsia(MpmObitoNecropsia obitoNecropsia) throws ApplicationBusinessException {
		
		this.preInserirObitoNecropsia(obitoNecropsia);
		this.getMpmObitoNecropsiaDAO().atualizar(obitoNecropsia);
		this.getMpmObitoNecropsiaDAO().flush();
		
	}
	
	/**
	 * Validacoes da trigger
	 * @param ObitoNecropsia
	 */
	private void preInserirObitoNecropsia(MpmObitoNecropsia obitoNecropsia) throws ApplicationBusinessException {
		
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(obitoNecropsia.getAltaSumario());
		this.getManterObtCausaDiretaRN().verificarTipoAltaSumario(obitoNecropsia.getAltaSumario());
		verificarIndicadorNecropsia(obitoNecropsia.getNecropsia(), obitoNecropsia.getAltaSumario());
		
	}
	
	/**
	 * Remove MpmObitoNecropsia
	 * @param ObitoNecropsia
	 */
	public void removerObitoNecropsia(MpmObitoNecropsia obitoNecropsia) throws ApplicationBusinessException {
		
		this.getMpmObitoNecropsiaDAO().remover(obitoNecropsia);
		this.getMpmObitoNecropsiaDAO().flush();
		
	}
	
	/**
	 * ORADB PROCEDURE mpmk_one_rn.rn_onep_ver_necro
	 * @param simNao
	 * @param altaSumario
	 *  
	 */
	public void verificarIndicadorNecropsia(DominioSimNao necropsia, MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		if (necropsia != null && necropsia.equals(DominioSimNao.S)) {
			
			if (altaSumario.getTipo() != DominioIndTipoAltaSumarios.OBT || altaSumario.getSituacao() != DominioSituacao.A) {
				
				ManterObitoNecropsiaRNExceptionCode.MPM_02742.throwException();
				
			}
			
		}
		
	}
	
	public boolean validaAoMenosUmaNecropsia(MpmAltaSumarioId altaSumarioId) {
		
		MpmObitoNecropsia obitoNecropsia = this.getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(), altaSumarioId.getSeqp());
		
		if(obitoNecropsia == null || obitoNecropsia.getId() == null){
			return false;
		}
		
		return true;
		
	}
	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}
	
	protected ManterObtCausaDiretaRN getManterObtCausaDiretaRN() {
		return manterObtCausaDiretaRN;
	}
	
	protected MpmObitoNecropsiaDAO getMpmObitoNecropsiaDAO() {
		return mpmObitoNecropsiaDAO;
	}

}
