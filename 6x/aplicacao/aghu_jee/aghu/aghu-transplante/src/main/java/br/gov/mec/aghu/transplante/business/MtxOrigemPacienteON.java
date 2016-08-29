package br.gov.mec.aghu.transplante.business;

import javax.ejb.Stateless;
import org.apache.commons.logging.Log;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MtxOrigemPacienteON extends BaseBusiness {

	private static final long serialVersionUID = -1246077491374078346L;

	private enum OrigemPacienteONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_OBRIGATORIEDADE_DESCRICAO,
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void validarInputOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseListException {
		if(mtxOrigens.getDescricao().trim().equals("")){
		  throw new ApplicationBusinessException(OrigemPacienteONExceptionCode.MENSAGEM_OBRIGATORIEDADE_DESCRICAO);
		}
	}
}
