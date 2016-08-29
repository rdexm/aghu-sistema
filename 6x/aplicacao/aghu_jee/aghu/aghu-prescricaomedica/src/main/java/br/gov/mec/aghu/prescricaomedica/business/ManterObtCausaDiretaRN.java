package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaDiretaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterObtCausaDiretaRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterObtCausaDiretaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmObtCausaDiretaDAO mpmObtCausaDiretaDAO;

@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6853321491425894277L;

	public enum ManterObtCausaDiretaRNExceptionCode implements BusinessExceptionCode {

		MPM_02688;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * TRIGGER MPMT_OCD_BASE_BRI, MPMT_OCD_BRI
	 * @param obtCausaDireta
	 *  
	 */
	public void inserirObtCausaDireta(MpmObtCausaDireta obtCausaDireta) throws ApplicationBusinessException {	
		this.preInserirObtCausaDireta(obtCausaDireta);
		this.getMpmObtCausaDiretaDAO().persistir(obtCausaDireta);
		this.getMpmObtCausaDiretaDAO().flush();

	}

	/**
	 * Atualiza objeto MpmAltaDiagMtvoInternacao.
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} mpmAltaDiagMtvoInternacao
	 */
	public void atualizarObtCausaDireta(MpmObtCausaDireta obtCausaDireta) throws ApplicationBusinessException {	


		this.preAtualizarObtCausaDireta(obtCausaDireta);
		this.getMpmObtCausaDiretaDAO().merge(obtCausaDireta);
		this.getMpmObtCausaDiretaDAO().flush();


	}

	/**
	 * Validações
	 * @param obtCausaDireta
	 */
	protected void preInserirObtCausaDireta(MpmObtCausaDireta obtCausaDireta) throws ApplicationBusinessException {

		if (obtCausaDireta.getMpmCidAtendimentos() != null) {
			this.getAltaSumarioRN().verificarCidAtendimento(obtCausaDireta.getMpmCidAtendimentos().getSeq());
		}
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(obtCausaDireta.getMpmAltaSumarios());
		this.verificarTipoAltaSumario(obtCausaDireta.getMpmAltaSumarios());
	}


	protected void preAtualizarObtCausaDireta(MpmObtCausaDireta obtCausaDireta) throws ApplicationBusinessException {
		this.preInserirObtCausaDireta(obtCausaDireta);
	}

	/**
	 * Verifica se o tipo do sumário é de óbito
	 * @param obtCausaDireta
	 */
	public void verificarTipoAltaSumario(MpmAltaSumario altaSumario) throws ApplicationBusinessException {

		if (altaSumario != null && altaSumario.getSituacao().equals(DominioSituacao.A) && altaSumario.getTipo() != DominioIndTipoAltaSumarios.OBT) {

			ManterObtCausaDiretaRNExceptionCode.MPM_02688.throwException();

		}

	}


	/**
	 * Remove objeto MpmObtCausaDireta
	 * ORADB TRIGGER MPMT_OCD_BRD
	 * 
	 * @param {MpmObtCausaDireta} obtCausaDireta
	 */
	public void removerObtCausaDireta(MpmObtCausaDireta obtCausaDireta) throws ApplicationBusinessException {	
		
		
		MpmAltaSumario mpmAltaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioAtivo(obtCausaDireta.getMpmAltaSumarios().getId().getApaAtdSeq(), 
				obtCausaDireta.getMpmAltaSumarios().getId().getApaSeq(),
				obtCausaDireta.getMpmAltaSumarios().getId().getSeqp());
		
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(mpmAltaSumario);		
		this.getMpmObtCausaDiretaDAO().remover(obtCausaDireta);
		this.getMpmObtCausaDiretaDAO().flush();

	}


	public boolean validarAoMenosUmaCausaDireta(MpmAltaSumarioId altaSumarioId) {
		// Pesquisa a ocorrência da Causa Direta da Morte
		MpmObtCausaDireta obtCausaDireta = this.getMpmObtCausaDiretaDAO()
		.obterObtCausaDireta(altaSumarioId.getApaAtdSeq(),
				altaSumarioId.getApaSeq(), altaSumarioId.getSeqp());
		if (obtCausaDireta == null || obtCausaDireta.getId() == null) {
			return false;
		}
		return true;
	}

	public boolean validarMaisDeUmaCausaDireta(MpmAltaSumarioId altaSumarioId) {
		// Pesquisa a ocorrência da Causa Direta da Morte
		try {
			this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(
					altaSumarioId.getApaAtdSeq(), altaSumarioId.getApaSeq(),
					altaSumarioId.getSeqp());
		} catch (HibernateException e) {
			logError("Exceção capturada: ", e);
			// ocorreu exception uniqueResult - if there is more than one
			// matching result
			return false;
		}
		return true;
	}

	public MpmObtCausaDireta obterObtCausaDireta(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
	throws ApplicationBusinessException {
		return getMpmObtCausaDiretaDAO().obterObtCausaDireta(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmObtCausaDiretaDAO getMpmObtCausaDiretaDAO() {
		return mpmObtCausaDiretaDAO;
	}

	public MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	public void setMpmAltaSumarioDAO(MpmAltaSumarioDAO mpmAltaSumarioDAO) {
		this.mpmAltaSumarioDAO = mpmAltaSumarioDAO;
	}


}
