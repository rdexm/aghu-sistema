package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagPrincipalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author bsoliveira - 29/10/2010
 * 
 */
@Stateless
public class ManterAltaDiagPrincipalRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaDiagPrincipalRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;

@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8394994148358169923L;

	public enum ManterAltaDiagPrincipalRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_DIAG_PRINCIPAL, ERRO_REMOVER_ALTA_DIAG_PRINCIPAL;

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
	 * Insere objeto MpmAltaDiagPrincipal.
	 * 
	 * @param {MpmAltaDiagPrincipal} altaDiagPrincipal
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaDiagPrincipal(MpmAltaDiagPrincipal altaDiagPrincipal)
			throws BaseException {

		try {

			this.preInserirAltaDiagPrincipal(altaDiagPrincipal);
			this.getAltaDiagPrincipalDAO().persistir(altaDiagPrincipal);
			this.getAltaDiagPrincipalDAO().flush();

		} catch (BaseException e) {
			LOG.error("Exceção BaseException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			ManterAltaDiagPrincipalRNExceptionCode.ERRO_INSERIR_ALTA_DIAG_PRINCIPAL.throwException(e);
		}

	}

	/**
	 * Exclui objeto MpmAltaDiagPrincipal.
	 * 
	 * @param mpmAltaDiagPrincipal
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaDiagPrincipal(
			MpmAltaDiagPrincipal altaDiagPrincipal)
			throws ApplicationBusinessException {

		try {
			
			MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaDiagPrincipal.getId().getApaAtdSeq(), altaDiagPrincipal.getId().getApaSeq(), altaDiagPrincipal.getId().getSeqp());
			
			this.preRemoverAltaDiagPrincipal(altaDiagPrincipal);
						
			this.getAltaDiagPrincipalDAO().remover(altaDiagPrincipal);
			altaSumario.setAltaDiagPrincipal(null);
			this.getMpmAltaSumarioDAO().atualizar(altaSumario);
			
			this.getAltaDiagPrincipalDAO().flush();

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(
					ManterAltaDiagPrincipalRNExceptionCode.ERRO_REMOVER_ALTA_DIAG_PRINCIPAL);
		}
		
	}

	/**
	 * @ORADB Trigger MPMT_ADP_BASE_BRI ORADB Trigger MPMT_ADP_BRI
	 * 
	 * @param {MpmAltaDiagPrincipal} altaDiagPrincipal
	 * @throws ApplicationBusinessException
	 */
	private void preInserirAltaDiagPrincipal(
			MpmAltaDiagPrincipal altaDiagPrincipal) throws ApplicationBusinessException {
		
		if (altaDiagPrincipal.getCidAtendimento() != null) {
			this.getAltaSumarioRN().verificarCidAtendimento(altaDiagPrincipal.getCidAtendimento().getSeq());
		}

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaDiagPrincipal.getAltaSumario());

	}

	/**
	 * @ORADB Trigger MPMT_ADP_BRD
	 * 
	 * @param {MpmAltaDiagPrincipal} altaDiagPrincipal
	 * @throws ApplicationBusinessException
	 */
	private void preRemoverAltaDiagPrincipal(MpmAltaDiagPrincipal altaDiagPrincipal)
			throws ApplicationBusinessException {
		
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaDiagPrincipal.getAltaSumario());

	}
	
	/**
	 * Método que verifica a validação
	 * do diagnóstico principal da alta 
	 * do paciente. Deve pelo menos ter 
	 * um registro ativo associado ao sumário 
	 * do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public Boolean validarAltaDiagPrincipal(MpmAltaSumarioId altaSumarioId) {
		List<Long> result = this.getAltaDiagPrincipalDAO().listAltaDiagPrincipal(altaSumarioId);
		
		Long rowCount = 0L;
		if(!result.isEmpty()){
			rowCount = (Long) result.get(0);
		}
		
		return rowCount > 0;
	}
	

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaDiagPrincipalDAO getAltaDiagPrincipalDAO() {
		return mpmAltaDiagPrincipalDAO;
	}

	public MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	public void setMpmAltaSumarioDAO(MpmAltaSumarioDAO mpmAltaSumarioDAO) {
		this.mpmAltaSumarioDAO = mpmAltaSumarioDAO;
	}

}
