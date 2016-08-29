package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExameConselhoProfsDAO;
import br.gov.mec.aghu.model.AelExameConselhoProfs;
import br.gov.mec.aghu.model.AelExameConselhoProfsId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class AelExameConselhoProfsRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelExameConselhoProfsRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExameConselhoProfsDAO aelExameConselhoProfsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5706657980787998284L;

	public enum AelExameConselhoProfsRNExceptionCode implements BusinessExceptionCode {

		AEL_00437, AEL_00438, MENSAGEM_ERRO_UK_CONSELHO_PROFS;
		
	}
	
	/**
	 * ORADB TRIGGER AELT_ECP_BRI
	 * @param aelSinonimoExame
	 * @throws ApplicationBusinessException 
	 */
	public void inserirAelExameConselhoProfs(AelExameConselhoProfs aelExameConselhoProfs) throws ApplicationBusinessException {
		
		validarConselho(aelExameConselhoProfs);
		getAelExameConselhoProfsDAO().persistir(aelExameConselhoProfs);
		getAelExameConselhoProfsDAO().flush();
		
	}
	
	/**
	 * ORADB TRIGGER AELT_ECP_BRU
	 * @param aelSinonimoExame
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public void atualizarAelExameConselhoProfs(AelExameConselhoProfs aelExameConselhoProfs) throws ApplicationBusinessException {
		
		validarConselho(aelExameConselhoProfs);
		getAelExameConselhoProfsDAO().atualizar(aelExameConselhoProfs);
		getAelExameConselhoProfsDAO().flush();
		
	}
	
	/**
	 * ORADB aelk_ecp_rn.rn_ecpp_ver_conselho
	 * Conselho profissional deve estar ativo
	 * @param cprCodigo
	 * @throws ApplicationBusinessException 
	 */
	public void validarConselho(AelExameConselhoProfs aelExameConselhoProfs) throws ApplicationBusinessException {
		
		if (aelExameConselhoProfs.getConselhosProfissionais() == null) {
			
			throw new ApplicationBusinessException (AelExameConselhoProfsRNExceptionCode.AEL_00437);
			
		} else if (!aelExameConselhoProfs.getConselhosProfissionais().getIndSituacao().equals(DominioSituacao.A)) {
			
			throw new ApplicationBusinessException (AelExameConselhoProfsRNExceptionCode.AEL_00438);

		}
		
		AelExameConselhoProfs exameConselhoProfsIAux;
		//Busca Conselho e verifica se já está associado ao exame;
		AelExameConselhoProfsId id = new AelExameConselhoProfsId();
		id.setCprCodigo(aelExameConselhoProfs.getConselhosProfissionais().getCodigo());
		id.setEmaExaSigla(aelExameConselhoProfs.getExamesMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(aelExameConselhoProfs.getExamesMaterialAnalise().getId().getManSeq());
		
		exameConselhoProfsIAux = this.getAelExameConselhoProfsDAO().obterPorChavePrimaria(id);
		if(exameConselhoProfsIAux!= null){
			throw new ApplicationBusinessException (AelExameConselhoProfsRNExceptionCode.MENSAGEM_ERRO_UK_CONSELHO_PROFS);	
		}
		
		
		
	}
	
	protected AelExameConselhoProfsDAO getAelExameConselhoProfsDAO() {
		return aelExameConselhoProfsDAO;
	}
	
	/**
	 * Remove objeto do banco
	 * @param exameConselhoProfs
	 */
	public void removerAelExameConselhoProfs(AelExameConselhoProfs exameConselhoProfs){
		exameConselhoProfs = getAelExameConselhoProfsDAO().obterPorChavePrimaria(exameConselhoProfs.getId());
		getAelExameConselhoProfsDAO().remover(exameConselhoProfs);
		getAelExameConselhoProfsDAO().flush();
	}

}
