package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatMotivoSaidaPacienteON extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = -3405304634292150821L;

	private static final Log LOG = LogFactory.getLog(FatMotivoSaidaPacienteON.class);
	
	@Inject
	private FatMotivoSaidaPacienteRN fatMotivoSaidaPacienteRN;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Requisição para persistencia/alteração da entidade.
	 *  
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void gravarMotivoSaidaPaciente(FatMotivoSaidaPaciente entity) throws ApplicationBusinessException {
		
		this.fatMotivoSaidaPacienteRN.gravar(entity);
	}
	
	/**
	 * Requisição para exclusão da entidade.
	 *  
	 * @param id {@link Short}
	 * @throws ApplicationBusinessException
	 */
	public void removerMotivoSaidaPaciente(Short id) throws ApplicationBusinessException {
		
		this.fatMotivoSaidaPacienteRN.remover(id);
	}
}
