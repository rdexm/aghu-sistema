package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SolicitanteProntuarioCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SolicitanteProntuarioCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipSolicitantesProntuarioDAO aipSolicitantesProntuarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6757285339142205641L;

	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * de solicitante de prontuário.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de solicitante de prontuario.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum SolicitanteProntuarioCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_SOLICITANTE_PRONTUARIO, RESTRICAO_CADASTRO_SOLICITANTE_PRONTUARIO, VOLUMES_MANUSEADOS_SOLICITANTE_PRONTUARIO_OBRIGATORIO, AGHU_VIOLACAO_FK
	}

	public Long obterSolicitanteProntuarioCount(Integer seq,
			DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados) {
		return this.getAipSolicitantesProntuarioDAO().obterSolicitanteProntuarioCount(seq, indSituacao, unidadeFuncional,
				origemEvento, finalidadeMovimentacao, descricao, exigeResponsavel, mensagemSamis, separacaoPrevia, retorno,
				volumesManuseados);
	}

	public List<AipSolicitantesProntuario> pesquisarSolicitanteProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer seq, DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados) {
		return this.getAipSolicitantesProntuarioDAO().pesquisarSolicitanteProntuario(firstResult, maxResults, orderProperty, asc, seq,
				indSituacao, unidadeFuncional, origemEvento, finalidadeMovimentacao, descricao, exigeResponsavel, mensagemSamis,
				separacaoPrevia, retorno, volumesManuseados);
	}
	
	/**
	 * 
	 * @dbtables AipSolicitantesProntuario select
	 * 
	 * @param aipSolicitanteProntuarioCodigo
	 * @return
	 */
	public AipSolicitantesProntuario obterSolicitanteProntuario(
			Short aipSolicitanteProntuarioCodigo) {
		return this.getAipSolicitantesProntuarioDAO().obterPorChavePrimaria(aipSolicitanteProntuarioCodigo);
	}

	/**
	 * Método responsável pela persistência de um solicitante de prontuário.
	 * 
	 * @dbtables AipSolicitantesProntuario insert,update
	 * 
	 * @param solicitanteProntuario
	 * @throws ApplicationBusinessException
	 */
	public void persistirSolicitanteProntuario(
			AipSolicitantesProntuario solicitanteProntuario)
			throws ApplicationBusinessException {
		if (solicitanteProntuario.getSeq() == null) {
			// inclusão
			this.incluirSolicitanteProntuario(solicitanteProntuario);
		} else {
			// edição
			this.atualizarSolicitanteProntuario(solicitanteProntuario);
		}
	}

	/**
	 * Método responsável por incluir um novo solicitante de prontuário.
	 * 
	 * @param solicitanteProntuario
	 * @throws ApplicationBusinessException
	 */
	private void incluirSolicitanteProntuario(
			AipSolicitantesProntuario solicitanteProntuario)
			throws ApplicationBusinessException {
		this.validarDadosSolicitanteProntuario(solicitanteProntuario);
		this.getAipSolicitantesProntuarioDAO().persistir(solicitanteProntuario);
		this.getAipSolicitantesProntuarioDAO().flush();
	}

	/**
	 * Método responsável pela atualização de um solicitante de prontuário.
	 * 
	 * @param solicitanteProntuario
	 * @throws ApplicationBusinessException
	 */
	private void atualizarSolicitanteProntuario(
			AipSolicitantesProntuario solicitanteProntuario)
			throws ApplicationBusinessException {
		this.validarDadosSolicitanteProntuario(solicitanteProntuario);

		try {
			this.getAipSolicitantesProntuarioDAO().atualizar(solicitanteProntuario);
			this.getAipSolicitantesProntuarioDAO().flush();
		}catch (PersistenceException ce) {
			logError("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
						SolicitanteProntuarioCRUDExceptionCode.AGHU_VIOLACAO_FK,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * Método responsável pelas validações dos dados de solicitante de
	 * prontuário. Método utilizado para inclusão e atualização de solicitantes
	 * de prontuário.
	 * 
	 * @param solicitanteProntuario
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosSolicitanteProntuario(
			AipSolicitantesProntuario solicitanteProntuario)
			throws ApplicationBusinessException {
		// Regra de negócio #1: O usuário deve selecionar uma, e apenas uma,
		// opção entre os seguintes campos: unidades funcionais, origem de
		// eventos e descrição
		int count = 0;
		if (solicitanteProntuario.getUnidadesFuncionais() != null) {
			count++;
		}
		if (solicitanteProntuario.getOrigemEventos() != null) {
			count++;
		}
		if (!StringUtils.isBlank(solicitanteProntuario.getDescricao())) {
			count++;
		}
		if (count != 1) {
			throw new ApplicationBusinessException(
					SolicitanteProntuarioCRUDExceptionCode.RESTRICAO_CADASTRO_SOLICITANTE_PRONTUARIO);
		}

		// Regra de negócio #2: O campo Volumes Manuseados é obrigatório
		if (solicitanteProntuario.getVolumesManuseados() == null) {
			throw new ApplicationBusinessException(
					SolicitanteProntuarioCRUDExceptionCode.VOLUMES_MANUSEADOS_SOLICITANTE_PRONTUARIO_OBRIGATORIO);
		}
	}

	protected AipSolicitantesProntuarioDAO getAipSolicitantesProntuarioDAO() {
		return aipSolicitantesProntuarioDAO;
	}
	
}
