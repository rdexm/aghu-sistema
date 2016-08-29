package br.gov.mec.aghu.compras.cadastrosapoio.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.FcpAgenciaBancoDAO;
import br.gov.mec.aghu.model.FcpAgenciaBanco;
import br.gov.mec.aghu.model.FcpAgenciaBancoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FcpAgenciaBancoRN extends BaseBusiness {

	private static final long serialVersionUID = -7583131212340381217L;
	private static final Log LOG = LogFactory.getLog(FcpAgenciaBancoRN.class);
	
	@Inject
	private FcpAgenciaBancoDAO agenciaBancoDAO;
	
	public enum FcpAgenciaBancoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_AGENCIA;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void persistir(FcpAgenciaBanco entity) throws ApplicationBusinessException {
		notNull(entity);
		if (agenciaBancoDAO.obterOriginal(entity.getId()) == null) {
			inserir(entity);
		} else {
			atualizar(entity);
		}
	}

	private void atualizar(FcpAgenciaBanco entity) throws ApplicationBusinessException {
		FcpAgenciaBanco entidadeAtualizada = agenciaBancoDAO.obterPorChavePrimaria(entity.getId());
		entidadeAtualizada.setDescricao(entity.getDescricao());
		entidadeAtualizada.setFcpBanco(entity.getFcpBanco());
		entidadeAtualizada.setItensContaHospitalar(entity.getItensContaHospitalar());
	}
	
	private void inserir(FcpAgenciaBanco entity) throws ApplicationBusinessException {
		agenciaBancoDAO.persistir(entity);
	}
	
	public void remover(FcpAgenciaBancoId idEntity) throws ApplicationBusinessException {
		notNull(idEntity);
		agenciaBancoDAO.removerPorId(idEntity);
	}
	
	public void notNull(Object object) throws ApplicationBusinessException { 
		try {
			Validate.notNull(object);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(FcpAgenciaBancoRNExceptionCode.ERRO_PERSISTENCIA_AGENCIA);
		}
	}
}
