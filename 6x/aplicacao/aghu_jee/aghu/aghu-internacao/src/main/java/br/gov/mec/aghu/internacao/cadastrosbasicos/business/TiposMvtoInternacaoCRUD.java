package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.dao.AinTiposMvtoInternacaoDAO;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de tipo de movimento de internação.
 */
//
//
@Stateless
public class TiposMvtoInternacaoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposMvtoInternacaoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5294391215007956892L;


	private enum TiposMvtoInternacaoCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_TIPOSMVTOINTERNACAO, ERRO_REMOVER_TIPOSMVTOINTERNACAO, DESCRICAO_TIPOSMVTOINTERNACAO_OBRIGATORIO,
		DESCRICAO_TIPOSMVTOINTERNACAO_JA_EXISTENTE, ERRO_REMOVER_TIPOSMVTOINTERNACAO_COM_DEPENDENTES;
	}
	
	

	/**
	 * Método responsável pela persistência de um tipo de movimento de internação.
	 * 
	 * @param tipo de movimento de internação
	 * @throws ApplicationBusinessException
	 */
	public void persistirTiposMvtoInternacao(AinTiposMvtoInternacao tipoMvtoInternacao)
			throws ApplicationBusinessException {
		tipoMvtoInternacao.setIndCcihMvtoLocal(false);
		if (tipoMvtoInternacao.getCodigo() == null) {
			// inclusão			
			this.incluirTiposMvtoInternacao(tipoMvtoInternacao);
		} else {
			// edição
			this.atualizarTiposMvtoInternacao(tipoMvtoInternacao);
		}

	}

	/**
	 * Método responsável por incluir um novo tipo de movimento de internação.
	 * 
	 * @param tipo de movimento de internação
	 * @throws ApplicationBusinessException
	 */
	//
	private void incluirTiposMvtoInternacao(AinTiposMvtoInternacao tipoMvtoInternacao) throws ApplicationBusinessException {
		this.validarDadosTiposMvtoInternacao(tipoMvtoInternacao);
		
		AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO = this.getAinTiposMvtoInternacaoDAO();
		ainTiposMvtoInternacaoDAO.persistir(tipoMvtoInternacao);
		ainTiposMvtoInternacaoDAO.flush();
	}

	/**
	 * Método responsável pela atualização de um tipo de movimento de internação.
	 * 
	 * @param tipo de movimento de internação
	 * @throws ApplicationBusinessException
	 */
	//
	private void atualizarTiposMvtoInternacao(AinTiposMvtoInternacao tipoMvtoInternacao) throws ApplicationBusinessException {
		this.validarDadosTiposMvtoInternacao(tipoMvtoInternacao);
		
		AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO = this.getAinTiposMvtoInternacaoDAO();
		ainTiposMvtoInternacaoDAO.atualizar(tipoMvtoInternacao);
		ainTiposMvtoInternacaoDAO.flush();
	}

	/**
	 * Método responsável pelas validações dos dados de tipo de movimento de internação. Método
	 * utilizado para inclusão e atualização de tipo de movimento de internação.
	 * 
	 * @param tipo de movimento de internação
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosTiposMvtoInternacao(AinTiposMvtoInternacao tipoMvtoInternacao)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(tipoMvtoInternacao.getDescricao())) {
			throw new ApplicationBusinessException(
					TiposMvtoInternacaoCRUDExceptionCode.DESCRICAO_TIPOSMVTOINTERNACAO_OBRIGATORIO);
		}		
	}

	
	
	/**
	 * Apaga um tipo de movimento de internação do banco de dados.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void removerTiposMvtoInternacao(Integer codigo)
			throws ApplicationBusinessException {
		try {
			AinTiposMvtoInternacao tipoMvtoInternacao = this.getAinTiposMvtoInternacaoDAO().obterTiposMvtoInternacao(codigo);
			ainTiposMvtoInternacaoDAO.remover(tipoMvtoInternacao);
			ainTiposMvtoInternacaoDAO.flush();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			if(e.getCause() != null && e.getCause().getClass().equals(ConstraintViolationException.class)){
				throw new ApplicationBusinessException(
						TiposMvtoInternacaoCRUDExceptionCode.ERRO_REMOVER_TIPOSMVTOINTERNACAO_COM_DEPENDENTES);
			}
			//AGH.AIN_MVI_TMI_FK1
			logError("Erro ao remover o tipo de movimento de internação.", e);
			throw new ApplicationBusinessException(
					TiposMvtoInternacaoCRUDExceptionCode.ERRO_REMOVER_TIPOSMVTOINTERNACAO);
		}
	}
	
	
	protected AinTiposMvtoInternacaoDAO getAinTiposMvtoInternacaoDAO() {
		return ainTiposMvtoInternacaoDAO;
	}
	
	
}
