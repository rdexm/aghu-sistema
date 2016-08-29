package br.gov.mec.aghu.prescricaomedica.business;



import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.model.MpmObitoNecropsiaId;
import br.gov.mec.aghu.prescricaomedica.business.ManterObitoNecropsiaRN.ManterObitoNecropsiaRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObitoNecropsiaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObitoNecropsiaON extends BaseBusiness {


@EJB
private ManterObitoNecropsiaRN manterObitoNecropsiaRN;

private static final Log LOG = LogFactory.getLog(ManterObitoNecropsiaON.class);

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
	private static final long serialVersionUID = -3817734803174449706L;
	
	/**
	 * Cria uma cópia de ObitoNecropsia
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarObitoNecropsia(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		if(altaSumario!=null){
			MpmObitoNecropsia obitoNecropsia = this.getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
			
			if (obitoNecropsia != null) {
				
				MpmObitoNecropsia novoObitoNecropsia = new MpmObitoNecropsia();
				novoObitoNecropsia.setAltaSumario(altaSumario);
				novoObitoNecropsia.setNecropsia(obitoNecropsia.getNecropsia());
				this.getManterObitoNecropsiaRN().inserirObitoNecropsia(novoObitoNecropsia);
				
			}
		}

	}
	
	
	public MpmObitoNecropsia obterMpmObitoNecropsia(MpmAltaSumario altaSumario) {
		
		Integer altanAtdSeq = null;
		Integer altanApaSeq = null;
		Short altanAsuSeqp = null;
		
		if(altaSumario!=null){
			altanAtdSeq = altaSumario.getId().getApaAtdSeq();
			altanApaSeq = altaSumario.getId().getApaSeq();
			altanAsuSeqp = altaSumario.getId().getSeqp();
		}

		return getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}

	
	public String gravarObitoNecropsia(MpmAltaSumario altaSumario, DominioSimNao indicadorNecropsia) throws ApplicationBusinessException {
		String confirmacao = null;
		
		if(altaSumario != null){

			Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
			Integer altanApaSeq = altaSumario.getId().getApaSeq();
			Short altanAsuSeqp = altaSumario.getId().getSeqp();
			
			MpmObitoNecropsia obitoNecropsia = getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(altanAtdSeq, altanApaSeq, altanAsuSeqp);

			if (obitoNecropsia == null) {// Inserir Obito Necrópsia

				    obitoNecropsia = new MpmObitoNecropsia();
					MpmObitoNecropsiaId id = new  MpmObitoNecropsiaId();
					id.setAsuApaAtdSeq(altanAtdSeq);
					id.setAsuApaSeq(altanApaSeq);
					id.setAsuSeqp(altanAsuSeqp);
					obitoNecropsia.setId(id);
					
					obitoNecropsia.setAltaSumario(altaSumario);
					
					obitoNecropsia.setNecropsia(indicadorNecropsia);
					
					inserirObitoNecropsia(obitoNecropsia);
					
					confirmacao = "MENSAGEM_SUCESSO_INCLUSAO_OBITO_NECROPSIA";
	

			} else { // Atualizar Obito Necrópsia
				obitoNecropsia.setAltaSumario(altaSumario);
				obitoNecropsia.setNecropsia(indicadorNecropsia);
				atualizarObitoNecropsia(obitoNecropsia);
				confirmacao = "MENSAGEM_SUCESSO_ALTERACAO_OBITO_NECROPSIA";
			}
		}
		return confirmacao;
		}
	
	/**
	 * Insere objeto MpmObitoNecropsia.
	 * 
	 * @param {MpmObitoNecropsia} obitoNecropsia
	 */
	public void inserirObitoNecropsia(MpmObitoNecropsia obitoNecropsia)
			throws ApplicationBusinessException {

		try {

			getManterObitoNecropsiaRN().inserirObitoNecropsia(obitoNecropsia);

		} catch (Exception e) {

			logError(e.getMessage(), e);
			ManterObitoNecropsiaRNExceptionCode.ERRO_INSERIR_OBITO_NECROPSIA
					.throwException(e);

		}

	}
	
	/**
	 * Atualiza objeto MpmObitoNecropsia.
	 * 
	 * @param {MpmObitoNecropsia} obitoNecropsia
	 */
	public void atualizarObitoNecropsia(MpmObitoNecropsia obitoNecropsia)
			throws ApplicationBusinessException {
		try {
			getManterObitoNecropsiaRN().atualizarObitoNecropsia(obitoNecropsia);
		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterObitoNecropsiaRNExceptionCode.ERRO_ALTERAR_OBITO_NECROPSIA
					.throwException(e);
		}
	}	
	
	/**
	 * Remove ObitoNecropsia
	 * @param altaSumario
	 */
	public void removerObitoNecropsia(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		if(altaSumario!=null){
			MpmObitoNecropsia obitoNecropsia = this.getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
			
			if (obitoNecropsia != null) {
				
				this.getManterObitoNecropsiaRN().removerObitoNecropsia(obitoNecropsia);
				
			}
		}
		
	}
	
	protected ManterObitoNecropsiaRN getManterObitoNecropsiaRN() {
		return manterObitoNecropsiaRN;
	}
	
	protected MpmObitoNecropsiaDAO getMpmObitoNecropsiaDAO() {
		return mpmObitoNecropsiaDAO;
	}

}
