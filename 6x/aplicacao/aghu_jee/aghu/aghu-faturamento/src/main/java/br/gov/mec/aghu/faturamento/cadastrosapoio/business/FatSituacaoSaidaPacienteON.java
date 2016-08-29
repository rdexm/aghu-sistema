package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPacienteId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatSituacaoSaidaPacienteON extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 3507384518773133678L;

	private static final Log LOG = LogFactory.getLog(FatSituacaoSaidaPacienteON.class);
	
	@Inject
	private FatSituacaoSaidaPacienteRN fatSituacaoSaidaPacienteRN;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	public List<FatSituacaoSaidaPaciente> recuperarListaPaginada(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final FatMotivoSaidaPaciente filtro) {
		
		return this.fatSituacaoSaidaPacienteRN.recuperarListaPaginada(firstResult, maxResult, orderProperty, asc, filtro);
	}

	public Long recuperarCount(final FatMotivoSaidaPaciente filtro) {

		return this.fatSituacaoSaidaPacienteRN.recuperarCount(filtro);
	}
	
	/**
	 * Requisição para persistencia/alteração da entidade.
	 * 
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void gravarSituacaoSaidaPaciente(FatSituacaoSaidaPaciente entity) throws ApplicationBusinessException {
		
		this.fatSituacaoSaidaPacienteRN.gravar(entity);
	}
	
	/**
	 * Requisição para exclusão da entidade.
	 * 
	 * @param fatSituacaoSaidaPacienteId {@link FatSituacaoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void removerSituacaoSaidaPaciente(FatSituacaoSaidaPacienteId fatSituacaoSaidaPacienteId) throws ApplicationBusinessException {
		
		this.fatSituacaoSaidaPacienteRN.remover(fatSituacaoSaidaPacienteId);
	}
}
