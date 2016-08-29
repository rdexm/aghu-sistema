package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioTipoEventoMbcPrincipal;
import br.gov.mec.aghu.dominio.DominioTipoOcorrenciaFichaFarmaco;
import br.gov.mec.aghu.model.MbcEventoPrincipal;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaEvento;
import br.gov.mec.aghu.model.MbcFichaOrgaoTransplante;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaEvento;

public class MbcOcorrenciaFichaEventosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcOcorrenciaFichaEvento> {
	
	private static final long serialVersionUID = 1353410351222798712L;
	public final String FICHA_EVENTO = "fet";
	
	public MbcOcorrenciaFichaEvento obterMbcOcorrenciaFichaEventosComFicha(
			Long seqFichaAnestesia, DominioOcorrenciaFichaEvento tipoOcorrencia, Short seqMbcEventoPrincipal) {
		
		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".") + MbcFichaEvento.Fields.FICHA_ANESTESIA.toString()  + "." + MbcFichaAnestesias.Fields.SEQ.toString(), seqFichaAnestesia));
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".") + MbcFichaEvento.Fields.EVENTO_PRINCIPAL.toString() + "." + MbcEventoPrincipal.Fields.SEQ.toString(), seqMbcEventoPrincipal));
		
		criteria.add(Restrictions.eq(MbcOcorrenciaFichaEvento.Fields.TIPO_OCORRENCIA.toString(), tipoOcorrencia));
		
		return (MbcOcorrenciaFichaEvento) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria getCriteriaJoinFichaEvento() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcOcorrenciaFichaEvento.class);
		criteria.createAlias(MbcOcorrenciaFichaEvento.Fields.FICHA_EVENTO.toString(), FICHA_EVENTO);
		return criteria;
	}
	
	
	public MbcOcorrenciaFichaEvento obterOcorrenciaPorFicSeqEvpSeqTipoOcorrencia(Long ficSeq, Short evpSeq, DominioOcorrenciaFichaEvento tipoOcorrencia) {
		
		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.EVENTO_PRINCIPAL.toString(), "evp");
		
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".")+MbcFichaEvento.Fields.FICHA_ANESTESIA_SEQ.toString(), ficSeq));
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".")+MbcFichaEvento.Fields.EVENTO_PRINCIPAL_SEQ.toString(), evpSeq));
		criteria.add(Restrictions.eq(MbcOcorrenciaFichaEvento.Fields.TIPO_OCORRENCIA.toString(), tipoOcorrencia));
		
		return (MbcOcorrenciaFichaEvento) executeCriteriaUniqueResult(criteria);
		
	}
	
	

	public MbcOcorrenciaFichaEvento obterOcorrenciaFichaEvento(
			Long seqMbcFichaAnest, Short seqMbcOrgaoTransplantado,
			DominioOcorrenciaFichaEvento tipoOcorrencia) {
		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".") + MbcFichaEvento.Fields.FICHA_ANESTESIA.toString().concat(".")  + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnest));
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".") + MbcFichaEvento.Fields.ORGAO_TRANSPLANTADO.toString().concat(".") + MbcFichaOrgaoTransplante.Fields.SEQ.toString(), seqMbcOrgaoTransplantado));
		if(tipoOcorrencia != null){
			criteria.add(Restrictions.eq(MbcOcorrenciaFichaEvento.Fields.TIPO_OCORRENCIA.toString(), tipoOcorrencia));
		}
		
		return (MbcOcorrenciaFichaEvento) executeCriteriaUniqueResult(criteria);
	}

	public MbcOcorrenciaFichaEvento obterMbcOcorrenciaFichaEventoComEventoPrincipal(
			Long seqMbcFichaAnestesia, DominioOcorrenciaFichaEvento tipoOcorrenciaFichaEvento) {
		
		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.EVENTO_PRINCIPAL.toString(), "evp");
		
		criteria.add(Restrictions.eq(FICHA_EVENTO.concat(".") + MbcFichaEvento.Fields.FICHA_ANESTESIA.toString()  + "." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.add(Restrictions.eq(MbcOcorrenciaFichaEvento.Fields.TIPO_OCORRENCIA.toString(), tipoOcorrenciaFichaEvento));
		criteria.add(Restrictions.eq("evp." + MbcEventoPrincipal.Fields.TIPO_EVENTO.toString(), DominioTipoEventoMbcPrincipal.A));
		
		return (MbcOcorrenciaFichaEvento) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcOcorrenciaFichaEvento> pesquisarMbcOcorrenciaFichaEvento(
			Long seqMbcFichaAnestesia,
			List<DominioTipoEventoMbcPrincipal> tipoEventosPrincipal,
			List<DominioTipoOcorrenciaFichaFarmaco> tipoOcorrenciasFichaFarmaco) {

		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.FICHA_ANESTESIA.toString(), "fic");
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.EVENTO_PRINCIPAL.toString(), "evp", Criteria.LEFT_JOIN);
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.ORGAO_TRANSPLANTADO.toString(), "otr", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		if(tipoOcorrenciasFichaFarmaco != null){
			criteria.add(Restrictions.in(MbcOcorrenciaFichaEvento.Fields.TIPO_OCORRENCIA.toString(), tipoOcorrenciasFichaFarmaco));
		}
		if(tipoEventosPrincipal != null){
			criteria.add(Restrictions.not(Restrictions.in("evp." + MbcEventoPrincipal.Fields.TIPO_EVENTO.toString(), tipoEventosPrincipal)));
		}
		
		return executeCriteria(criteria);
		
	}

	public Date obterDtOcorrenciaMaxMbcFichaOcorrenciaEvento(
			Integer seqMbcOcorrenciaFichaEvento, DominioTipoOcorrenciaFichaFarmaco tipoOcorrenciasFichaEvento) {
		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		criteria.setProjection(Projections.max(MbcOcorrenciaFichaEvento.Fields.DTHT_OCORRENCIA.toString()));
		
		if(tipoOcorrenciasFichaEvento != null){
			criteria.add(Restrictions.eq(MbcOcorrenciaFichaEvento.Fields.TIPO_OCORRENCIA.toString(), tipoOcorrenciasFichaEvento));
		}
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public List<MbcOcorrenciaFichaEvento> pesquisarMbcOcorrenciaFichaEventoComMbcEventoAdverso(
			Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = getCriteriaJoinFichaEvento();
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.FICHA_ANESTESIA.toString(), "fic");
		criteria.createAlias(FICHA_EVENTO + "." + MbcFichaEvento.Fields.EVENTO_ADVERSO.toString(), "ead");
		
		criteria.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.addOrder(Order.asc(MbcOcorrenciaFichaEvento.Fields.DTHT_OCORRENCIA.toString()));
		
		return executeCriteria(criteria);
	}


}
