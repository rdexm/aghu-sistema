package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.model.AelArquivoSolicitacao;

public class AelArquivoSolicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelArquivoSolicitacao> {

	private static final long serialVersionUID = 2469512259915943374L;

	public List<AelArquivoSolicitacao> pesquisarSolicitacoesPorArquivo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelArquivoIntegracao aelArquivoIntegracao,
			String nomePaciente, Integer numeroSolicitacao,
			boolean statusSucesso, boolean statusErro,
			boolean envioComAgendamento, boolean envioSemAgendamento) {

		DetachedCriteria criteria = this.montarCriteria(aelArquivoIntegracao,
				nomePaciente, numeroSolicitacao, statusSucesso, statusErro,
				envioComAgendamento, envioSemAgendamento);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}
	
	public Long pesquisarSolicitacoesPorArquivoCount(
			AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao, boolean statusSucesso,
			boolean statusErro, boolean envioComAgendamento,
			boolean envioSemAgendamento) {

		DetachedCriteria criteria = this.montarCriteria(aelArquivoIntegracao,
				nomePaciente, numeroSolicitacao, statusSucesso, statusErro,
				envioComAgendamento, envioSemAgendamento);
		return this.executeCriteriaCount(criteria);

	}
	
	private DetachedCriteria montarCriteria(AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao, boolean statusSucesso,
			boolean statusErro, boolean envioComAgendamento,
			boolean envioSemAgendamento){
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelArquivoSolicitacao.class);
		
		if (aelArquivoIntegracao != null) {
			criteria.add(Restrictions.eq(
					AelArquivoSolicitacao.Fields.AEL_ARQUIVO_INTEGRACOES
							.toString(), aelArquivoIntegracao));
		}
		if (StringUtils.isNotEmpty(nomePaciente)) {
			criteria.add(Restrictions.ilike(
					AelArquivoSolicitacao.Fields.NOME_PACIENTE.toString(),
					nomePaciente, MatchMode.ANYWHERE));
		}
		if (numeroSolicitacao != null) {
			criteria.add(Restrictions.eq(
					AelArquivoSolicitacao.Fields.AEL_SOLICITACAO_EXAMES_SEQ
							.toString(), numeroSolicitacao));
		}
		if (!statusSucesso) {
			criteria.add(Restrictions
					.isNotNull(AelArquivoSolicitacao.Fields.MOTIVO.toString()));
		}
		if (!statusErro) {
			criteria.add(Restrictions
					.isNull(AelArquivoSolicitacao.Fields.MOTIVO.toString()));
		}
		if (!envioComAgendamento) {
			criteria.add(Restrictions.eq(
					AelArquivoSolicitacao.Fields.SEM_AGENDA.toString(),
					DominioSimNao.S));
		}
		if (!envioSemAgendamento) {
			criteria.add(Restrictions.eq(
					AelArquivoSolicitacao.Fields.SEM_AGENDA.toString(),
					DominioSimNao.N));
		}
		return criteria;
	}

}
