package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincFarmacoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author bsoliveira - 29/10/2010
 *
 */
@Stateless
public class ManterAltaPrincFarmacoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaPrincFarmacoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4158879809373894531L;

	public enum ManterAltaPrincFarmacoRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_PRINC_FARMACO, ERRO_REMOVER_ALTA_PRINC_FARMACO, ERRO_ATUALIZAR_ALTA_PRINC_FARMACO,MPM_02626,MPM_02618, MPM_02604, MPM_02611;

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
	 * Insere objeto MpmAltaPrincFarmaco.
	 * 
	 * @param {MpmAltaPrincFarmaco} altaPrincFarmaco
	 */
	public void inserirAltaPrincFarmaco(
			MpmAltaPrincFarmaco altaPrincFarmaco)
			throws ApplicationBusinessException {

		try {

			this.preInserirAltaPrincFarmaco(altaPrincFarmaco);
			//FAZER UM MERGE
			this.getAltaPrincFarmacoDAO().persistir(altaPrincFarmaco);
			this.getAltaPrincFarmacoDAO().flush();

		} catch (Exception e) {

			ManterAltaPrincFarmacoRNExceptionCode.ERRO_INSERIR_ALTA_PRINC_FARMACO
					.throwException(e);

		}

	}

	/**
	 * ORADB Trigger MPMT_PFA_BRI
	 * 
	 * @param {MpmAltaPrincFarmaco} altaPrincFarmaco
	 */
	private void preInserirAltaPrincFarmaco(
			MpmAltaPrincFarmaco altaPrincFarmaco) throws ApplicationBusinessException {
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaPrincFarmaco.getAltaSumario());
	}
	
	/**
	 * ORADB Trigger MPMT_PFA_BRD
	 * 
	 * @param {MpmAltaPrincFarmaco} altaPrincFarmaco
	 */
	private void preRemoverAltaPrincFarmaco(MpmAltaPrincFarmaco altaPrincFarmaco) throws ApplicationBusinessException {
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaPrincFarmaco.getAltaSumario());
	}
	
	
	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}
	
	protected MpmAltaPrincFarmacoDAO getAltaPrincFarmacoDAO() {
		return mpmAltaPrincFarmacoDAO;
	}

	/**
	 * Excluir MpmAltaPrincFarmaco selecionado
	 * 
	 * @param {MpmAltaPrincFarmaco} altaPrincFarmaco
	 */
	public void removerAltaPrincFarmaco(MpmAltaPrincFarmaco altaPrincFarmaco)throws ApplicationBusinessException {
	    
		try {
		
			this.preRemoverAltaPrincFarmaco(altaPrincFarmaco);
			this.getAltaPrincFarmacoDAO().remover(altaPrincFarmaco);	
			this.getAltaPrincFarmacoDAO().flush();
			
	    } catch (Exception e) {
	    	
	    	ManterAltaPrincFarmacoRNExceptionCode.ERRO_REMOVER_ALTA_PRINC_FARMACO.throwException(e);
		
	    }
	    
	}
	
	/**
	 * ORADB Trigger MPMT_PFA_BRI
	 * 
	 * @param {MpmAltaPrincFarmaco} altaPrincFarmaco
	 */
	private void preAtualizarAltaPrincFarmaco(
		MpmAltaPrincFarmaco altaPrincFarmaco) throws ApplicationBusinessException {
		
		MpmAltaPrincFarmaco altaPrincFarmacoVelho = this.getAltaPrincFarmacoDAO().obterPorChavePrimaria(altaPrincFarmaco.getId());

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaPrincFarmaco.getAltaSumario());

		this.getAltaSumarioRN().verificarSituacao(altaPrincFarmaco.getIndSituacao(), altaPrincFarmacoVelho.getIndSituacao(),ManterAltaPrincFarmacoRNExceptionCode.MPM_02626  );

		this.getAltaSumarioRN().verificarIndCarga(altaPrincFarmaco.getIndCarga(), altaPrincFarmacoVelho.getIndCarga(), ManterAltaPrincFarmacoRNExceptionCode.MPM_02618);

		this.verificarCodMedicamentosModificados(altaPrincFarmaco, altaPrincFarmacoVelho);

		if(CoreUtil.modificados(altaPrincFarmaco.getId(), altaPrincFarmacoVelho.getId())||  
				CoreUtil.modificados(altaPrincFarmaco.getIndCarga(), altaPrincFarmacoVelho.getIndCarga())||
				CoreUtil.modificados(altaPrincFarmaco.getMedicamento(), altaPrincFarmacoVelho.getDescMedicamento())||
				CoreUtil.modificados(altaPrincFarmaco.getAltaSumario(), altaPrincFarmacoVelho.getAltaSumario())||
				CoreUtil.modificados(altaPrincFarmaco.getIndSituacao(), altaPrincFarmacoVelho.getIndSituacao())||
				CoreUtil.modificados(altaPrincFarmaco.getDescMedicamento(), altaPrincFarmacoVelho.getDescMedicamento())){
			
			if(DominioSimNao.S.equals(altaPrincFarmaco.getIndCarga())){				
				throw new ApplicationBusinessException (ManterAltaPrincFarmacoRNExceptionCode.MPM_02618 );
			}
		}

	}

	/**
	 * @ORADB mpmk_pfa_rn.rn_pfap_ver_update
	 * @param altaPrincFarmaco
	 * @param altaPrincFarmacoVelho
	 */
	public void verificarCodMedicamentosModificados(
			MpmAltaPrincFarmaco altaPrincFarmaco,
			MpmAltaPrincFarmaco altaPrincFarmacoVelho)
	throws ApplicationBusinessException {

		if(!altaPrincFarmaco.getIndCarga()) {
			
			Integer atualMatCodigo = altaPrincFarmaco.getMedicamento() != null ? altaPrincFarmaco.getMedicamento().getMatCodigo() : null;
			Integer velhoMatCodigo = altaPrincFarmacoVelho.getMedicamento() != null ? altaPrincFarmacoVelho.getMedicamento().getMatCodigo() : null;

			if(atualMatCodigo != null || velhoMatCodigo != null) {
			
				if(CoreUtil.modificados(altaPrincFarmaco.getDescMedicamento(), altaPrincFarmacoVelho.getDescMedicamento())
						&& !CoreUtil.modificados(atualMatCodigo, velhoMatCodigo)){
					
					throw new ApplicationBusinessException (
							ManterAltaPrincFarmacoRNExceptionCode.MPM_02604);
					
				}
				
				if(CoreUtil.modificados(atualMatCodigo, velhoMatCodigo)
						&& !CoreUtil.modificados(altaPrincFarmaco.getDescMedicamento(), altaPrincFarmacoVelho.getDescMedicamento())) {
					
					throw new ApplicationBusinessException (
							ManterAltaPrincFarmacoRNExceptionCode.MPM_02611);
					
				}
			}
			
		}
	}
	
	public void atualizarAltaPrincFarmaco(
		MpmAltaPrincFarmaco altaPrincFarmaco)
		throws ApplicationBusinessException {

	try {

		this.preAtualizarAltaPrincFarmaco(altaPrincFarmaco);
		//FAZER UM MERGE
		this.getAltaPrincFarmacoDAO().atualizar(altaPrincFarmaco);
		this.getAltaPrincFarmacoDAO().flush();

	} catch (Exception e) {

		ManterAltaPrincFarmacoRNExceptionCode.ERRO_ATUALIZAR_ALTA_PRINC_FARMACO
				.throwException(e);

	}

}

	
}
