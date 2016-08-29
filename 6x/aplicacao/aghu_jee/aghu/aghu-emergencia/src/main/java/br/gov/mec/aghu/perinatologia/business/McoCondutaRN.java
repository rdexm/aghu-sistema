package br.gov.mec.aghu.perinatologia.business;

import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoCondutaDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class McoCondutaRN extends BaseBusiness {

	private static final long serialVersionUID = 3508315959061339579L;

	@Override
	protected Log getLogger() {
		return null;
	}

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Inject
	private McoCondutaDAO anamneseEfsDAO;

	private enum McoCondutaRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_NULL;
	}

	public void persistir(McoConduta mcoConduta) throws BaseException {

		try {
			Validate.notNull(mcoConduta);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(
					McoCondutaRNExceptionCode.ERRO_PERSISTIR_NULL);
		}

		if (mcoConduta.getSeq() == null) {
			inserir(mcoConduta);
		} else {
			atualizar(mcoConduta);
		}
	}

	private void atualizar(McoConduta mcoConduta) {
		
		mcoConduta.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));

		anamneseEfsDAO.atualizar(mcoConduta);
	}

	private void inserir(McoConduta mcoConduta) {
		
		mcoConduta.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));

		anamneseEfsDAO.persistir(mcoConduta);
	}

}
