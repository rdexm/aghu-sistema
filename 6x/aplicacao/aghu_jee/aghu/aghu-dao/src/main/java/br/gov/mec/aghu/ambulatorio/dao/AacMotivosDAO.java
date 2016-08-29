package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class AacMotivosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacMotivos> {

	
	private static final long serialVersionUID = -8204576537155477033L;
	
	private static final Log LOG = LogFactory.getLog(AacMotivosDAO.class);
	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * motivo consulta.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de Motivo Consulta.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum MotivoConsultaCRUDExceptionCode implements
			BusinessExceptionCode {
		DESCRICAO_MOTIVO_CONSULTA_OBRIGATORIO, ERRO_REMOVER_MOTIVO_CONSULTA, ERRO_PERSISTIR_MOTIVO_CONSULTA
	}
	public AacMotivos obterDescricaoMotivoPorCodigo(Short mtoSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacMotivos.class);
		criteria.add(Restrictions.eq(AacMotivos.Fields.CODIGO.toString(), mtoSeq));
		return (AacMotivos) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AacMotivos> obterListaMotivosAtivos(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacMotivos.class);
		criteria.add(Restrictions.eq(AacMotivos.Fields.IND_ATIVO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	public List<AacMotivos> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Short codigo,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacMotivos.class); 
				
		criteria = createPesquisaCriteria(codigo, descricao,
				situacao, criteria);
		
		if (StringUtils.isBlank(orderProperty)){
			orderProperty = AacMotivos.Fields.CODIGO.toString();
			asc = true;
		}

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}
	public Long pesquisaCount(Short codigo, String descricao,
			DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacMotivos.class);
		return executeCriteriaCount(createPesquisaCriteria(codigo, descricao,
				situacao, criteria));
	}
	private DetachedCriteria createPesquisaCriteria(Short codigo,
			String descricao, DominioSituacao situacao, DetachedCriteria criteria) {
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(AacMotivos.Fields.CODIGO.toString(),
					codigo.shortValue()));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AacMotivos.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					AacMotivos.Fields.IND_ATIVO.toString(), situacao));
		}

		return criteria;
	}
	/**
	 * Método responsável por incluir uma novo Motivo Consulta.
	 * 
	 * @param motivoConsulta
	 * @throws ApplicationBusinessException
	 */
	private void incluirMotivoConsulta(AacMotivos motivo)
			throws ApplicationBusinessException {
		this.validarDadosMotivoConsulta(motivo);

		try {
			this.obterValorSequencialId(motivo);
			persistir(motivo);
			flush();
		} catch (Exception e) {
			LOG.error("Erro ao incluir motivo consulta.", e);
			throw new ApplicationBusinessException(
					MotivoConsultaCRUDExceptionCode.ERRO_PERSISTIR_MOTIVO_CONSULTA);
		}
	}
	/**
	 * Método responsável pelas validações dos dados de motivo consulta. Método
	 * utilizado para inclusão e atualização de motivo consulta.
	 * 
	 * @param motivo
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosMotivoConsulta(AacMotivos motivo)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(motivo.getDescricao())) {
			throw new ApplicationBusinessException(
					MotivoConsultaCRUDExceptionCode.DESCRICAO_MOTIVO_CONSULTA_OBRIGATORIO);
		}
	}
	/**
	 * Método responsável pela persistência de motivo consulta.
	 * 
	 * @param motivoConsulta
	 * @throws ApplicationBusinessException
	 */
	
	public void persistirMotivoConsulta(AacMotivos motivo)
			throws ApplicationBusinessException {
		if (motivo.getCodigo() == null) {
			// inclusão
			this.incluirMotivoConsulta(motivo);
		} else {
			// edição
			this.atualizarMotivoConsulta(motivo);
		}
	}
	/**
	 * Método responsável pela atualização de um Motivo Consulta.
	 * 
	 * @param motivoConsulta
	 * @throws ApplicationBusinessException
	 */
	private void atualizarMotivoConsulta(AacMotivos motivo)
			throws ApplicationBusinessException {
		this.validarDadosMotivoConsulta(motivo);

		try {
			if (!contains(motivo)) {
				merge(motivo);
			}

			flush();
		} catch (Exception e) {
			LOG.error("Erro ao atualizar motivo consulta.", e);
			throw new ApplicationBusinessException(
					MotivoConsultaCRUDExceptionCode.ERRO_PERSISTIR_MOTIVO_CONSULTA);
		}
	}
	public AacMotivos obterMotivoConsulta(Short accMotivoConsultaCodigo) {
		AacMotivos retorno = findByPK(AacMotivos.class,
				accMotivoConsultaCodigo.shortValue());
		return retorno;
	}
}