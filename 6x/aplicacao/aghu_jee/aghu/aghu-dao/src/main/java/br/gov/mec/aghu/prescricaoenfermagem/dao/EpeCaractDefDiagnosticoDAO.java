package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeDiagnostico;

public class EpeCaractDefDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeCaractDefDiagnostico> {

	private static final long serialVersionUID = 7364760771917143393L;

	public List<EpeCaractDefDiagnostico> pesquisarCaractDefDiagnosticoPorGrupoSubgrupoEDescricao(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefDiagnostico.class);
		criteria.createAlias(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString(), EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString());
		if(dgnSnbGnbSeq!=null){
			criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DIAGNOSTICO_SNB_GNB_SEQ.toString(), dgnSnbGnbSeq));	
		}
		if(dgnSnbSequencia!=null){
			criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), dgnSnbSequencia));	
		}
		if(StringUtils.isNotEmpty(descricao)){
			criteria.add(Restrictions.ilike(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString()+"."+EpeCaractDefinidora.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));	
		}
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString()+"."+EpeCaractDefinidora.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString()+"."+EpeCaractDefinidora.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<EpeCaractDefDiagnostico> pesquisarCaractDefDiagnosticoPorCodigoCaractDefinidora(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefDiagnostico.class);
		criteria.createAlias(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString(), EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString());
		criteria.createAlias(EpeCaractDefDiagnostico.Fields.DIAGNOSTICO.toString(), EpeCaractDefDiagnostico.Fields.DIAGNOSTICO.toString());
		if(codigo!=null){
			criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString()+"."+EpeCaractDefinidora.Fields.CODIGO.toString(), codigo));	
		}
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DIAGNOSTICO.toString()+"."+EpeDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(EpeCaractDefDiagnostico.Fields.DIAGNOSTICO.toString()+"."+EpeDiagnostico.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	protected DetachedCriteria obterCriteriaPesquisarCaractDefDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefDiagnostico.class);
		criteria.createAlias(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString(), "caract_def", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), snbGnbSeq));
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), snbSequencia));
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SEQUENCIA.toString(), sequencia));
		return criteria;
	}
	
	public List<EpeCaractDefDiagnostico> pesquisarCaractDefDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return executeCriteria(obterCriteriaPesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia));
	}

	public List<EpeCaractDefDiagnostico> pesquisarCaractDefDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return executeCriteria(obterCriteriaPesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia), firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCaractDefDiagnosticoPorSubgrupoCount(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		return executeCriteriaCount(obterCriteriaPesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia));
	}
	
	public Boolean possuiEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefDiagnostico.class);
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.CARACT_DEFINIDORA.toString(),epeCaractDefinidora));
		return executeCriteriaExists(criteria);
	}
}
