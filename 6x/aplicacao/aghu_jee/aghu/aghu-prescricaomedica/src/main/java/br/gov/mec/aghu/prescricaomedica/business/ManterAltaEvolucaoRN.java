package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAltaEvolucaoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaEvolucaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1613512780999956928L;

	public enum ManterAltaEvolucaoRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ALTA_EVOLUCAO, ERRO_ATUALIZAR_ALTA_EVOLUCAO;

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
	 * Insere objeto MpmAltaEvolucao.
	 * 
	 * @param {MpmAltaEvolucao} altaEvolucao
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaEvolucao(MpmAltaEvolucao altaEvolucao) throws ApplicationBusinessException {
		if (altaEvolucao.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaEvolucao nao esta associado corretamente a MpmAltaSumario.");
		}

		try {

			this.preInserirAltaEvolucao(altaEvolucao);
			this.getAltaEvolucaoDAO().persistir(altaEvolucao);
			this.getAltaEvolucaoDAO().flush();

		} catch (Exception e) {
			ManterAltaEvolucaoRNExceptionCode.ERRO_INSERIR_ALTA_EVOLUCAO.throwException(e);
		}
	}
	
	/**
	 * Atualizar um objeto <code>MpmAltaEvolucao</code>
	 * 
	 * @param altaEvolucao
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaEvolucao(MpmAltaEvolucao altaEvolucao) throws ApplicationBusinessException {
		if (altaEvolucao.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaEvolucao nao esta associado corretamente a MpmAltaSumario.");
		}

		try {
			if (altaEvolucao.getId() == null) {
				throw new IllegalArgumentException("MpmAltaEvolucao sem id. nao deveria ser uma acao de atualziar.");				
			}
			this.preAtualizarAltaEvolucao(altaEvolucao);
			this.getAltaEvolucaoDAO().atualizar(altaEvolucao);
			this.getAltaEvolucaoDAO().flush();
		} catch (Exception e) {
			ManterAltaEvolucaoRNExceptionCode.ERRO_ATUALIZAR_ALTA_EVOLUCAO.throwException(e);
		}
	}
	
	/**
	 * Remove um objeto <code>MpmAltaEvolucao</code>
	 * 
	 * @param altaEvolucao
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaEvolucao(MpmAltaEvolucao altaEvolucao) throws ApplicationBusinessException {
		
		this.preRemoverAltaEvolucao(altaEvolucao);
		this.getAltaEvolucaoDAO().remover(altaEvolucao);
		this.getAltaEvolucaoDAO().flush();
		
	}
	
	
	/**
	 * Método que valida a verificação
	 * da evolução da alta do paciente. Deve
	 * ter pelo menos um registro associado ao
	 * sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public Boolean validarAltaEvolucao(MpmAltaSumarioId altaSumarioId) {
		List<Long> result = this.getAltaEvolucaoDAO().listAltaEvolucao(altaSumarioId);
		
		Long rowCount = 0L;
		if(!result.isEmpty()){
			rowCount = (Long) result.get(0);
		}
		
		return rowCount > 0;
	}
	
	

	/**
	 * ORADB Trigger MPMT_AEV_BRI
	 * 
	 * @param {MpmAltaEvolucao} altaEvolucao
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAltaEvolucao(MpmAltaEvolucao altaEvolucao) throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaEvolucao.getAltaSumario());
		
	}

	/**
	 * ORADB Trigger MPMT_AEV_BRU
	 * 
	 * @param {MpmAltaEvolucao} altaEvolucao
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizarAltaEvolucao(MpmAltaEvolucao altaEvolucao) throws ApplicationBusinessException {
		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaEvolucao.getAltaSumario());
		
	}
	
	/**
	 * ORADB Trigger MPMT_AEV_BRD
	 * 
	 * @param {MpmAltaEvolucao} altaEvolucao
	 * @throws ApplicationBusinessException
	 */
	protected void preRemoverAltaEvolucao(MpmAltaEvolucao altaEvolucao) throws ApplicationBusinessException {
		
		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaEvolucao.getAltaSumario());
		
	}

	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaEvolucaoDAO getAltaEvolucaoDAO() {
		return mpmAltaEvolucaoDAO;
	}

}
