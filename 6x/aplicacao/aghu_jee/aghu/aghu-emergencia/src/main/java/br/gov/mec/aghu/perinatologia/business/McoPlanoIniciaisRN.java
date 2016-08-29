package br.gov.mec.aghu.perinatologia.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.perinatologia.dao.McoPlanoIniciaisDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class McoPlanoIniciaisRN extends BaseBusiness {

	private static final long serialVersionUID = 2944924568557316408L;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoPlanoIniciaisDAO mcoPlanoIniciaisDAO;
	
	private enum McoPlanoIniciaisRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_NULL;
	}

	public void persistir(McoPlanoIniciais mcoPlanoIniciais) throws BaseException{
		
		try {
			Validate.notNull(mcoPlanoIniciais);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(McoPlanoIniciaisRNExceptionCode.ERRO_PERSISTIR_NULL);
		}
		
		if (mcoPlanoIniciais.getId() == null) {
			inserir(mcoPlanoIniciais);
		} else {
			atualizar(mcoPlanoIniciais);
		}
	}
	
	
	private void atualizar(McoPlanoIniciais mcoPlanoIniciais) {
		
		mcoPlanoIniciais.setSerMatricula(usuario.getMatricula());
		mcoPlanoIniciais.setSerVinCodigo(usuario.getVinculo());
		
		mcoPlanoIniciaisDAO.atualizar(mcoPlanoIniciais);
	}


	private void inserir(McoPlanoIniciais mcoPlanoIniciais) {
		
		mcoPlanoIniciais.setSerMatricula(usuario.getMatricula());
		mcoPlanoIniciais.setSerVinCodigo(usuario.getVinculo());
		
		mcoPlanoIniciaisDAO.persistir(mcoPlanoIniciais);
	}

	@Override
	protected Log getLogger() {
		return null;
	}

}
