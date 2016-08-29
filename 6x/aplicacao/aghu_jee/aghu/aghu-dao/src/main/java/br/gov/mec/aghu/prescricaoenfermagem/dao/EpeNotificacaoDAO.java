package br.gov.mec.aghu.prescricaoenfermagem.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoNotificacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.EpeNotificacao;
/**
 * 
 * @modulo prescricaoenfermagem
 *
 */
public class EpeNotificacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeNotificacao> {

	private static final long serialVersionUID = 5195775658264602141L;

	public boolean existeComunicadoUlceraPressaoPorAtendimento(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EpeNotificacao.class);
		criteria.createAlias(EpeNotificacao.Fields.AGH_ATENDIMENTOS.toString(),
				EpeNotificacao.Fields.AGH_ATENDIMENTOS.toString());

		criteria.add(Restrictions.eq(
				EpeNotificacao.Fields.AGH_ATENDIMENTOS.toString() + "."
						+ AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EpeNotificacao.Fields.TIPO.toString(),
				DominioTipoNotificacao.U));

		return executeCriteriaCount(criteria) > 0 ? true : false;
	}
	
}
