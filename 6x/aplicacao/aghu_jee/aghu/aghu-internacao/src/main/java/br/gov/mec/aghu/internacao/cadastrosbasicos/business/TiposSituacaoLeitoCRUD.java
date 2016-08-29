package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de tipo de situação de leito.
 */
//
//
@Stateless
public class TiposSituacaoLeitoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposSituacaoLeitoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1697450704887117334L;



	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * tipo de situação de leito.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de tipo de situação de leito.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum TiposSituacaoLeitoCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_REMOVER_TIPO_SITUACAO_LEITO, ERRO_PERSISTIR_TIPO_SITUACAO_LEITO, DESCRICAO_TIPO_SITUACAO_LEITO_OBRIGATORIO, DESCRICAO_TIPO_SITUACAO_LEITO_JA_EXISTENTE, CODIGO_TIPO_SITUACAO_LEITO_OBRIGATORIO, CODIGO_TIPO_SITUACAO_LEITO_JA_EXISTENTE, 
		ERRO_REMOVER_TIPO_SIT_LEITO_FK_EXTRATO, ERRO_REMOVER_TIPO_SIT_LEITO_FK_LEITO;
	}



	/**
	 * Método responsável pela persistência de um tipo de situação de leito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select,insert
	 * 
	 * @param tipoSituacaoLeito
	 * @throws ApplicationBusinessException
	 */
	public void criarTipoSituacaoLeito(AinTiposMovimentoLeito tipoSituacaoLeito)
			throws ApplicationBusinessException {
		this.validarDadosTipoSituacaoLeito(tipoSituacaoLeito, true);

		AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO = this.getAinTiposMovimentoLeitoDAO();
		ainTiposMovimentoLeitoDAO.persistir(tipoSituacaoLeito);
		ainTiposMovimentoLeitoDAO.flush();
	}

	/**
	 * Método responsável pela alteração de um tipo de situação de leito.
	 * 
	 * @dbtables AinTiposMovimentoLeito select,update
	 * 
	 * @param tipoSituacaoLeito
	 * @throws ApplicationBusinessException
	 */
	public void alterarTipoSituacaoLeito(
			AinTiposMovimentoLeito tipoSituacaoLeito)
			throws ApplicationBusinessException {
		this.validarDadosTipoSituacaoLeito(tipoSituacaoLeito, false);

		AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO = this.getAinTiposMovimentoLeitoDAO();
		ainTiposMovimentoLeitoDAO.atualizar(tipoSituacaoLeito);
		ainTiposMovimentoLeitoDAO.flush();
	}

	private void validarDadosTipoSituacaoLeito(
			AinTiposMovimentoLeito tipoSituacaoLeito, boolean isCriacao)
			throws ApplicationBusinessException {
		if (tipoSituacaoLeito.getCodigo() == null) {
			throw new ApplicationBusinessException(
					TiposSituacaoLeitoCRUDExceptionCode.CODIGO_TIPO_SITUACAO_LEITO_OBRIGATORIO);
		}

		if (StringUtils.isBlank(tipoSituacaoLeito.getDescricao())) {
			throw new ApplicationBusinessException(
					TiposSituacaoLeitoCRUDExceptionCode.DESCRICAO_TIPO_SITUACAO_LEITO_OBRIGATORIO);
		}

		List<AinTiposMovimentoLeito> tiposSituacaoLeito;

		if (isCriacao) {
			// validar se existe algum tipo de situação de leito com o mesmo
			// código cadastrado.
			tiposSituacaoLeito = getAinTiposMovimentoLeitoDAO().getTiposSituacaoLeitoComMesmoCodigo(tipoSituacaoLeito);
			if (tiposSituacaoLeito != null && !tiposSituacaoLeito.isEmpty()) {
				throw new ApplicationBusinessException(
						TiposSituacaoLeitoCRUDExceptionCode.CODIGO_TIPO_SITUACAO_LEITO_JA_EXISTENTE);
			}
		}

		// validar se existe algum tipo de situação de leito com a mesma
		// descrição cadastrada.
		tiposSituacaoLeito = getAinTiposMovimentoLeitoDAO().getTiposSituacaoLeitoComMesmaDescricao(tipoSituacaoLeito);
		if (tiposSituacaoLeito != null && !tiposSituacaoLeito.isEmpty()) {
			throw new ApplicationBusinessException(
					TiposSituacaoLeitoCRUDExceptionCode.DESCRICAO_TIPO_SITUACAO_LEITO_JA_EXISTENTE);
		}
	}



	/**
	 * Apaga um tipo de situação de leito do banco de dados.
	 * 
	 * @dbtables AinTiposMovimentoLeito delete
	 * 
	 * @param tipoSituacaoLeito
	 *            Tipo de Situação de Leito a ser removido.
	 * @throws ApplicationBusinessException
	 */
	public void removerTipoSituacaoLeito(
			Short codigo)
			throws ApplicationBusinessException {
		try {
			AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO = this.getAinTiposMovimentoLeitoDAO();
			AinTiposMovimentoLeito tipoMovimentoLeito = ainTiposMovimentoLeitoDAO.obterTipoSituacaoLeito(codigo);
			ainTiposMovimentoLeitoDAO.remover(tipoMovimentoLeito);
			ainTiposMovimentoLeitoDAO.flush();
		} catch (Exception e) {
			logError("Erro ao remover o tipo de situação de leito.", e);
			Throwable cause = ExceptionUtils.getRootCause(e);
			if (cause != null && cause.getMessage()!= null) {
				if (StringUtils.containsIgnoreCase(cause.getMessage(), "AIN_EXL_TML_FK1")) {
					throw new ApplicationBusinessException(TiposSituacaoLeitoCRUDExceptionCode.ERRO_REMOVER_TIPO_SIT_LEITO_FK_EXTRATO);
				}
				if (StringUtils.containsIgnoreCase(cause.getMessage(), "AIN_LTO_TML_FK1")) {
					throw new ApplicationBusinessException(TiposSituacaoLeitoCRUDExceptionCode.ERRO_REMOVER_TIPO_SIT_LEITO_FK_LEITO);
				}
			}
			throw new ApplicationBusinessException(
					TiposSituacaoLeitoCRUDExceptionCode.ERRO_REMOVER_TIPO_SITUACAO_LEITO);
		}
	}
	
	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}

}
