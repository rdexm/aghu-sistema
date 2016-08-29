package br.gov.mec.aghu.administracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.model.AghNotificacaoDestinos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;

public class AghNotificacaoDestinosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghNotificacaoDestinos> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2623420738872299898L;
	

	public List<NotificacaoDestinoVO> obtemDestinosDaNotificacao(Integer ntsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNotificacaoDestinos.class, "ntd");
		criteria.createAlias("ntd." + AghNotificacaoDestinos.Fields.SERVIDOR_CONTATO.toString(), "ser", JoinType.INNER_JOIN);
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ntd." + AghNotificacaoDestinos.Fields.SEQ.toString()), NotificacaoDestinoVO.Fields.SEQ.toString());
		projList.add(Projections.property("ntd." + AghNotificacaoDestinos.Fields.NTS_SEQ.toString()), NotificacaoDestinoVO.Fields.NTS_SEQ.toString());
		projList.add(Projections.property("ntd." + AghNotificacaoDestinos.Fields.DDD_CELULAR.toString()), NotificacaoDestinoVO.Fields.DDD_CELULAR.toString());
		projList.add(Projections.property("ntd." + AghNotificacaoDestinos.Fields.CELULAR.toString()), NotificacaoDestinoVO.Fields.CELULAR.toString());
		projList.add(Projections.property("ntd." + AghNotificacaoDestinos.Fields.MATRICULA_CONTATO.toString()), NotificacaoDestinoVO.Fields.MATRICULA_CONTATO.toString());
		projList.add(Projections.property("ntd." + AghNotificacaoDestinos.Fields.VIN_CODIGO_CONTATO.toString()), NotificacaoDestinoVO.Fields.VIN_CODIGO_CONTATO.toString());
		projList.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()), NotificacaoDestinoVO.Fields.NOME_PESSOA_FISICA.toString());
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("ntd." + AghNotificacaoDestinos.Fields.NTS_SEQ.toString(), ntsSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoDestinoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Boolean existemDestinosAssociadosDeterminadaNotificacao(Integer ntsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNotificacaoDestinos.class, "NTD");
		criteria.add(Restrictions.eq("NTD." + AghNotificacaoDestinos.Fields.NTS_SEQ.toString(), ntsSeq));
		return executeCriteriaExists(criteria);
	}
}
