package br.gov.mec.aghu.blococirurgico.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;

public class MbcAlcadaAvalOpmsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAlcadaAvalOpms> {

	private static final long serialVersionUID = 3191623341939539319L;

	public List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupo(Short seq) {
		DetachedCriteria criteria = buscaNiveisAlcadaAprovacaoPorGrupoCriteria(seq);
		criteria.addOrder(Order.asc(MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA.toString()));
		return this.executeCriteria(criteria);
	}
	
	public List<MbcAlcadaAvalOpms> buscaNiveisAlcadaAprovacaoPorGrupoValor(Short seq) {
		DetachedCriteria criteria = buscaNiveisAlcadaAprovacaoPorGrupoCriteria(seq);
		criteria.addOrder(Order.asc(MbcAlcadaAvalOpms.Fields.VALOR_MINIMO.toString()));
		return this.executeCriteria(criteria);
	}
	
	public Long buscaNiveisAlcadaAprovacaoPorGrupoCount(Short seq) {
		DetachedCriteria criteria = buscaNiveisAlcadaAprovacaoPorGrupoCriteria(seq);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria buscaNiveisAlcadaAprovacaoPorGrupoCriteria(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAlcadaAvalOpms.class);
		criteria.add(Restrictions.eq(MbcAlcadaAvalOpms.Fields.GRUPO_ALCADA_SEQ.toString(), seq));
		return criteria;
	}
	
	public Boolean verificaExistenciaValorNivelAlcada(Short seq, BigDecimal valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAlcadaAvalOpms.class);
		criteria.add(Restrictions.eq(MbcAlcadaAvalOpms.Fields.GRUPO_ALCADA_SEQ.toString(), seq));
		criteria.add(Restrictions.le(MbcAlcadaAvalOpms.Fields.VALOR_MINIMO.toString(), valor));
		criteria.add(Restrictions.ge(MbcAlcadaAvalOpms.Fields.VALOR_MAXIMO.toString(), valor));
		criteria.addOrder(Order.asc(MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA.toString()));
		return this.executeCriteriaExists(criteria);
	}

	public Boolean verificaExistenciaDeServidoresAssociadosAoNivelDeAlcada(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcServidorAvalOpms.class);
		criteria.add(Restrictions.eq(MbcServidorAvalOpms.Fields.ALCADA.toString()+"."+MbcAlcadaAvalOpms.Fields.ID,seq));
		return this.executeCriteriaExists(criteria);
	}

	public MbcAlcadaAvalOpms buscaNivelAlcada(Short seqNivel) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAlcadaAvalOpms.class);
		criteria.add(Restrictions.eq(MbcAlcadaAvalOpms.Fields.ID.toString(), seqNivel));
		return (MbcAlcadaAvalOpms) this.executeCriteriaUniqueResult(criteria);
	}
	
	//#37054 C05_MAT_ALCADA
	public Integer buscarNivelAlcadaPorEspecialidadeValorMaterial(Short seqEspecialidade, BigDecimal valorMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAlcadaAvalOpms.class,"AAO");
		criteria.createAlias("AAO."+MbcAlcadaAvalOpms.Fields.GRUPO_ALCADA.toString(), "GAO");
		
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES_SEQ.toString(), seqEspecialidade));
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(), DominioTipoConvenioOpms.SUS));
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.SITUACAO.toString(), DominioSituacao.A));
		Criterion valorMinimo = Restrictions.le("AAO."+MbcAlcadaAvalOpms.Fields.VALOR_MINIMO.toString(), valorMaterial);
		Criterion valorMaximo = Restrictions.ge("AAO."+MbcAlcadaAvalOpms.Fields.VALOR_MAXIMO.toString(), valorMaterial);
		criteria.add(Restrictions.and(valorMinimo, valorMaximo));
		criteria.setResultTransformer(Transformers.aliasToBean(MbcAlcadaAvalOpms.class));
		criteria.setProjection(Projections.projectionList()
				.add((Projections.property("AAO."+MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA.toString()))));		
		return (Integer)  executeCriteriaUniqueResult(criteria);
		
	}	
	
}
