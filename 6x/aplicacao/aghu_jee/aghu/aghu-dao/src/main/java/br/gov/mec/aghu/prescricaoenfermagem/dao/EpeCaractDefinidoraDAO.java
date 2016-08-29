package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class EpeCaractDefinidoraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeCaractDefinidora> {

	private static final long serialVersionUID = 4042468518926971775L;

	public DetachedCriteria obterCriteriaSinaisSintomasFiltros(Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefinidora.class, "cde");
		if (codigo != null && codigo > 0) {
			criteria.add(Restrictions.eq(EpeCaractDefinidora.Fields.CODIGO.toString(),codigo));
		}
		if (descricao != null && StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.like(EpeCaractDefinidora.Fields.DESCRICAO.toString(),descricao,MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(EpeCaractDefinidora.Fields.SITUACAO.toString(),situacao));
		}
		return criteria;
	}

	public List<EpeCaractDefinidora> listarSinaisSintomasPorCodigoDescricaoSituacao(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc, Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaSinaisSintomasFiltros(codigo,descricao,situacao);
		criteria.addOrder(Order.asc(EpeCaractDefinidora.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}
	
	public Long listarSinaisSintomasPorCodigoDescricaoSituacaoCount(Integer codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = obterCriteriaSinaisSintomasFiltros(codigo,descricao,situacao);
		return executeCriteriaCount(criteria);
	}
	
	public List<EpeCaractDefinidora> pesquisarCaracteristicasDefinidoras(Object objSinaisSintomas, List<EpeSinCaractDefinidora> listaSinonimos){
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefinidora.class);
		criteria.add(Restrictions.eq(EpeCaractDefinidora.Fields.SITUACAO.toString(), DominioSituacao.A));
		if(!listaSinonimos.isEmpty()){
			ArrayList<Integer> listaCodigosSinonimos = new ArrayList<Integer>();
			for (EpeSinCaractDefinidora escd : listaSinonimos) {
				listaCodigosSinonimos.add(escd.getId().getCdeCodigoPossui());
			}
			criteria.add(Restrictions.not(Restrictions.in(EpeCaractDefinidora.Fields.CODIGO.toString(), listaCodigosSinonimos)));
		}
		if(!objSinaisSintomas.toString().isEmpty()){
			if(CoreUtil.isNumeroInteger(objSinaisSintomas)){
				criteria.add(Restrictions.eq(EpeCaractDefinidora.Fields.CODIGO.toString(), Integer.valueOf(objSinaisSintomas.toString())));
			} else {			
				criteria.add(Restrictions.ilike(EpeCaractDefinidora.Fields.DESCRICAO.toString(), objSinaisSintomas.toString(), MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc(EpeCaractDefinidora.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	protected DetachedCriteria obterCriteriaPesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefinidora.class);
		criteria.add(Restrictions.eq(EpeCaractDefinidora.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (!filtro.toString().isEmpty()) {
			if (CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.eq(EpeCaractDefinidora.Fields.CODIGO.toString(), Integer.valueOf(filtro.toString())));
			} else {
				criteria.add(Restrictions.ilike(EpeCaractDefinidora.Fields.DESCRICAO.toString(), filtro.toString(), MatchMode.ANYWHERE));
			}
		}
		DetachedCriteria subCriteria = obterCriteriaPesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia);
		subCriteria.setProjection(Projections.property(EpeCaractDefDiagnostico.Fields.CDE_CODIGO.toString()));
		criteria.add(Subqueries.propertyNotIn(EpeCaractDefinidora.Fields.CODIGO.toString(),subCriteria));
		return criteria;
	}

	protected DetachedCriteria obterCriteriaPesquisarCaractDefDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeCaractDefDiagnostico.class);
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), snbGnbSeq));
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), snbSequencia));
		criteria.add(Restrictions.eq(EpeCaractDefDiagnostico.Fields.DGN_SEQUENCIA.toString(), sequencia));
		return criteria;
	}

	public List<EpeCaractDefinidora> pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia){
		DetachedCriteria criteria = obterCriteriaPesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(filtro, snbGnbSeq, snbSequencia, sequencia); 
		criteria.addOrder(Order.asc(EpeCaractDefinidora.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Long pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnosticoCount(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia){
		DetachedCriteria criteria = obterCriteriaPesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(filtro, snbGnbSeq, snbSequencia, sequencia);
		return executeCriteriaCount(criteria);
	}

}
