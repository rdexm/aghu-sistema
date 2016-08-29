package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.paciente.dao.AipFinalidadesMovimentacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de finalidade de movimentação.
 */
@Stateless
public class FinalidadeMovimentacaoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FinalidadeMovimentacaoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipFinalidadesMovimentacaoDAO aipFinalidadesMovimentacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6698677543515958290L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * finalidade de movimentação.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de finalidade de movimentação.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum FinalidadeMovimentacaoCRUDExceptionCode implements BusinessExceptionCode {
		DESCRICAO_FINALIDADE_MOVIMENTACAO_OBRIGATORIO, ERRO_PERSISTIR_FINALIDADE_MOVIMENTACAO, ERRO_REMOVER_FINALIDADE_MOVIMENTACAO, ERRO_REMOVER_FINALIDADE_MOVIMENTACAO_COM_SOLICITANTE_PRONTUARIO, ERRO_REMOVER_FINALIDADE_MOVIMENTACAO_COM_SOLICITACAO_PRONTUARIO;
	}

	public List<AipFinalidadesMovimentacao> pesquisa(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {
		return getAipFinalidadesMovimentacaoDAO().pesquisa(firstResult, maxResults, orderProperty, asc, codigo, descricao, situacao);
	}

	public List<AipFinalidadesMovimentacao> pesquisaFinalidadesMovimentacao(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer codigo, String descricao, DominioSituacao situacao) {
		return getAipFinalidadesMovimentacaoDAO().pesquisaFinalidadesMovimentacao(firstResult, maxResults, orderProperty, asc, codigo,
				descricao, situacao);
	}

	public Long pesquisaCount(Integer codigo, String descricao, DominioSituacao situacao) {
		return getAipFinalidadesMovimentacaoDAO().pesquisaCount(codigo, descricao, situacao);
	}

	public Long pesquisaFinalidadeMovimentacaoCount(Integer codigo, String descricao) {
		return getAipFinalidadesMovimentacaoDAO().pesquisaFinalidadeMovimentacaoCount(codigo, descricao);
	}

	/**
	 * Método responsável pela persistência de uma finalidade de movimentação.
	 * 
	 * @param finalidadeMovimentacao
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadeMovimentacao) throws ApplicationBusinessException {
		if (finalidadeMovimentacao.getCodigo() == null) {
			// inclusão
			this.incluirFinalidadeMovimentacao(finalidadeMovimentacao);
		} else {
			// edição
			this.atualizarFinalidadeMovimentacao(finalidadeMovimentacao);
		}
	}

	/**
	 * Método responsável por incluir uma nova finalidade de movimentação.
	 * 
	 * @param finalidadeMovimentacao
	 * @throws ApplicationBusinessException
	 */
	private void incluirFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadeMovimentacao) throws ApplicationBusinessException {
		this.validarDadosFinalidadeMovimentacao(finalidadeMovimentacao);
		this.getAipFinalidadesMovimentacaoDAO().persistir(finalidadeMovimentacao);
		this.getAipFinalidadesMovimentacaoDAO().flush();
	}

	/**
	 * Método responsável pela atualização de uma finalidade de movimentação.
	 * 
	 * @param finalidadeMovimentacao
	 * @throws ApplicationBusinessException
	 */
	private void atualizarFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadeMovimentacao) throws ApplicationBusinessException {
		this.validarDadosFinalidadeMovimentacao(finalidadeMovimentacao);
		this.getAipFinalidadesMovimentacaoDAO().atualizar(finalidadeMovimentacao);
		this.getAipFinalidadesMovimentacaoDAO().flush();
	}

	/**
	 * Método responsável pelas validações dos dados de finalidade de
	 * movimentação. Método utilizado para inclusão e atualização de finalidade
	 * de movimentação.
	 * 
	 * @param finalidadeMovimentacao
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadeMovimentacao) throws ApplicationBusinessException {
		if (StringUtils.isBlank(finalidadeMovimentacao.getDescricao())) {
			throw new ApplicationBusinessException(FinalidadeMovimentacaoCRUDExceptionCode.DESCRICAO_FINALIDADE_MOVIMENTACAO_OBRIGATORIO);
		}
	}

	public AipFinalidadesMovimentacao obterFinalidadeMovimentacao(Integer aipFinalidadeMovimentacaoCodigo) {
		AipFinalidadesMovimentacao retorno = getAipFinalidadesMovimentacaoDAO().obterPorChavePrimaria(aipFinalidadeMovimentacaoCodigo.shortValue());
		return retorno;
	}

	/**
	 * @dbtables AipFinalidadesMovimentacao select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AipFinalidadesMovimentacao> pesquisarFinalidadeMovimentacaoPorCodigoEDescricao(Object objPesquisa) {
		return getAipFinalidadesMovimentacaoDAO().pesquisarFinalidadeMovimentacaoPorCodigoEDescricao(objPesquisa);
	}

	/**
	 * Método para excluir uma finalidade movimentacao.
	 */
	
	public void excluirFinalidadeMovimentacao(AipFinalidadesMovimentacao finalidadesMovimentacao) throws ApplicationBusinessException {
		try {
			finalidadesMovimentacao = this.getAipFinalidadesMovimentacaoDAO().merge(finalidadesMovimentacao);
			this.getAipFinalidadesMovimentacaoDAO().remover(finalidadesMovimentacao);
			this.getAipFinalidadesMovimentacaoDAO().flush();
		} catch (Exception e) {
			logError("Erro ao remover a finalidadesMovimentacao.", e);
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e.getCause();
				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "AIP_SOP_FMV_FK1")) {
					throw new ApplicationBusinessException(
							FinalidadeMovimentacaoCRUDExceptionCode.ERRO_REMOVER_FINALIDADE_MOVIMENTACAO_COM_SOLICITANTE_PRONTUARIO);
				}
				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "AIP_SLP_FMV_FK1")) {
					throw new ApplicationBusinessException(
							FinalidadeMovimentacaoCRUDExceptionCode.ERRO_REMOVER_FINALIDADE_MOVIMENTACAO_COM_SOLICITACAO_PRONTUARIO);
				}
			}
			throw new ApplicationBusinessException(FinalidadeMovimentacaoCRUDExceptionCode.ERRO_REMOVER_FINALIDADE_MOVIMENTACAO);
		}
	}

	private AipFinalidadesMovimentacaoDAO getAipFinalidadesMovimentacaoDAO() {
		return aipFinalidadesMovimentacaoDAO;
	}
}
