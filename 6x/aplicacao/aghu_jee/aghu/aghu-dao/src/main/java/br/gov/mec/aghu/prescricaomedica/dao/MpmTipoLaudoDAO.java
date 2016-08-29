package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;

public class MpmTipoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTipoLaudo> {

	private static final long serialVersionUID = 48952276178266340L;

	/**
	 * Busca a primeira entidade que for encontrada pelo criterio de busca. 
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public MpmTipoLaudo buscaTipoLaudoMenorPermanencia(Short tipoLaudoSeq, Short tipoLaudoConvenioCnvCodigo, Byte tipoLaudoConvenioSeq) {
		if (tipoLaudoSeq == null || tipoLaudoConvenioCnvCodigo == null || tipoLaudoConvenioSeq == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		
		StringBuilder hql = new StringBuilder(200);
		
		hql.append("select laudo ");
		hql.append("from ").append(MpmTipoLaudoConvenio.class.getSimpleName()).append(" tlc ");
		hql.append("inner join tlc.").append(MpmTipoLaudoConvenio.Fields.MPM_TIPO_LAUDO.toString()).append(" as laudo "); 
		hql.append("where laudo.").append(MpmTipoLaudo.Fields.SEQ.toString()).append(" = :laudoSeq "); 
		hql.append("and laudo.").append(MpmTipoLaudo.Fields.SITUACAO.toString()).append(" = :situacaoLaudo "); 
		hql.append("and tlc.").append(MpmTipoLaudoConvenio.Fields.CONVENIO_SAUDE_PLANO_SEQ.toString()).append(" = :tipoLaudoConvenioSeq ");
		hql.append("and tlc.").append(MpmTipoLaudoConvenio.Fields.CONVENIO_SAUDE_PLANO_CNV_CODIGO.toString()).append(" = :tipoLaudoConvenioCnvCodigo ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("laudoSeq", tipoLaudoSeq);
		query.setParameter("situacaoLaudo", DominioSituacao.A);
		query.setParameter("tipoLaudoConvenioSeq", tipoLaudoConvenioSeq);
		query.setParameter("tipoLaudoConvenioCnvCodigo", tipoLaudoConvenioCnvCodigo);
		
		List<MpmTipoLaudo> lista = query.getResultList();
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null;
	}

	public List<MpmTipoLaudo> listarTiposLaudo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short seq,
			String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createTiposLaudoCriteria(seq, descricao, situacao);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarTiposLaudoCount(Short seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = createTiposLaudoCriteria(seq, descricao, situacao);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createTiposLaudoCriteria(Short seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoLaudo.class);

		if (seq != null) {
			criteria.add(Restrictions.eq(MpmTipoLaudo.Fields.SEQ.toString(), seq));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MpmTipoLaudo.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(MpmTipoLaudo.Fields.SITUACAO.toString(), situacao));
		}

		return criteria;
	}

	public MpmTipoLaudo obterTipoLaudoComTiposSecundarios(Short seq) {
		MpmTipoLaudo retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoLaudo.class);

		criteria.createAlias(MpmTipoLaudo.Fields.TIPOS_LAUDO_TEXTO_PADRAO.toString(), "TLT");
		criteria.createAlias(MpmTipoLaudo.Fields.TIPOS_LAUDO_CONVENIO.toString(), "TLC");
		
		if (seq != null) {
			criteria.add(Restrictions.eq(MpmTipoLaudo.Fields.SEQ.toString(), seq));
		}
		
		List<MpmTipoLaudo> listaResultados = executeCriteria(criteria);
		
		if (listaResultados != null && !listaResultados.isEmpty()) {
			retorno = listaResultados.get(0);
		}
		
		return retorno;
	}
}
