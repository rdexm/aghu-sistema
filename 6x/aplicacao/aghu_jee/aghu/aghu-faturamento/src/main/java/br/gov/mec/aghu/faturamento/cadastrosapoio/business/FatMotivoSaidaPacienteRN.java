package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatMotivoSaidaPacienteDAO;
import br.gov.mec.aghu.faturamento.dao.FatSituacaoSaidaPacienteDAO;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatMotivoSaidaPacienteRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 4288970936561684192L;
	
	private static final Log LOG = LogFactory.getLog(FatMotivoSaidaPacienteRN.class);
	
	@Inject
	private FatMotivoSaidaPacienteDAO fatMotivoSaidaPacienteDAO;
	
	@Inject
	private FatSituacaoSaidaPacienteDAO fatSituacaoSaidaPacienteDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FatMotivoSaidaPacienteExceptionCode implements BusinessExceptionCode {
		OFG_00005_M1
	}

	/**
	 * Método responsável pela persistencia/alteração da entidade.
	 *  
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @throws ApplicationBusinessException
	 */
	public void gravar(FatMotivoSaidaPaciente entity) throws ApplicationBusinessException {
		
		if (entity.getSeq() != null) {
			
			this.fatMotivoSaidaPacienteDAO.atualizar(entity);
			
		} else {
			
			this.fatMotivoSaidaPacienteDAO.persistir(entity);
		}
	}

	/**
	 * Método responsável pela exclusão da entidade.
	 *  
	 * @param id {@link Short}
	 * @throws ApplicationBusinessException
	 */
	public void remover(Short id) throws ApplicationBusinessException {
		
		if (id != null) {

			FatMotivoSaidaPaciente entity = this.fatMotivoSaidaPacienteDAO.obterPorChavePrimaria(id);
			
			if (!possuiSituacoesSaidaPaciente(entity)) {
				
				this.fatMotivoSaidaPacienteDAO.remover(entity);
				
			} else {
				
				throw new ApplicationBusinessException(FatMotivoSaidaPacienteExceptionCode.OFG_00005_M1);
			}
		}
		
	}
	
	/**
	 * Valida se possui registros de Situações de Saída de Pacientes vinculados ao registro de Motivo de Saída de Paciente.
	 * 
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @return <code>true</code> se possui registro vinculado ou <code>false</code> caso contrário.
	 */
	private boolean possuiSituacoesSaidaPaciente(FatMotivoSaidaPaciente entity) {
		
		List<FatSituacaoSaidaPaciente> situacoes = this.fatSituacaoSaidaPacienteDAO.listarFatSituacaoSaidaPacientePorFatMotivoSaidaPaciente(entity);
		
		if (situacoes != null && !situacoes.isEmpty()) {
			
			return true;
		}
		
		return false;
	}
	
}
