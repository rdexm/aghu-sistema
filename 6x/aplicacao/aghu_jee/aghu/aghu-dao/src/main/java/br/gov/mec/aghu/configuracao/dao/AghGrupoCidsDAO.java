package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghGrupoCids;

public class AghGrupoCidsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghGrupoCids> {

	private static final long serialVersionUID = 7612076390896261775L;

	public List<AghGrupoCids> pesquisarGruposCids(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo, String descricaoGrupo, DominioSituacao situacaoGrupo) {
		DetachedCriteria criteria = createPesquisaGruposCidsCriteria(capitulo, codigoGrupo, siglaGrupo, descricaoGrupo, situacaoGrupo);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisarGruposCidsCount(AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo, String descricaoGrupo,
			DominioSituacao situacaoGrupo) {
		return executeCriteriaCount(createPesquisaGruposCidsCriteria(capitulo, codigoGrupo, siglaGrupo, descricaoGrupo, situacaoGrupo));
	}

	private DetachedCriteria createPesquisaGruposCidsCriteria(AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo,
			String descricaoGrupo, DominioSituacao situacaoGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghGrupoCids.class);

		criteria.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), capitulo.getSeq()));

		if (codigoGrupo != null) {
			criteria.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), codigoGrupo));
		}

		if (StringUtils.isNotBlank(siglaGrupo)) {
			criteria.add(Restrictions.ilike(AghGrupoCids.Fields.SIGLA.toString(), siglaGrupo, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(descricaoGrupo)) {
			criteria.add(Restrictions.ilike(AghGrupoCids.Fields.DESCRICAO.toString(), descricaoGrupo, MatchMode.ANYWHERE));
		}

		if (situacaoGrupo != null) {
			criteria.add(Restrictions.eq(AghGrupoCids.Fields.IND_SITUACAO.toString(), situacaoGrupo));
		}
		
		return criteria;
	}
	
	
	public void setarSequencialID(AghGrupoCids aghGrupoCid){
		aghGrupoCid.getId().setSeq(this.getNextVal(SequenceID.AGH_GCD_SQ1));
	}

	public List<AghGrupoCids> pesquisarGrupoCidSIGLA(String sigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghGrupoCids.class);

		criteria.add(Restrictions.eq(AghGrupoCids.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (!StringUtils.isBlank(sigla)) {
			criteria.add(Restrictions.ilike(AghGrupoCids.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		}

		return executeCriteria(criteria);
	}

	public List<AghGrupoCids> pesquisarGrupoCidSIGLAS(String sigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghGrupoCids.class);

		criteria.add(Restrictions.eq(AghGrupoCids.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (!StringUtils.isBlank(sigla)) {
			criteria.add(Restrictions.ilike(AghGrupoCids.Fields.SIGLA.toString(), sigla, MatchMode.ANYWHERE));
		}

		return executeCriteria(criteria);
	}

	public AghGrupoCids obterGrupoCidPorId(Integer cpcSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghGrupoCids.class);
		criteria.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteria.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		AghGrupoCids aghGrupoCids = (AghGrupoCids) this.executeCriteriaUniqueResult(criteria);

		return aghGrupoCids;
	}
	
	public List<AghGrupoCids> pesquisarGrupoCidPorCapituloCid(Integer seqCapituloCid) {
		if (seqCapituloCid == null) {
			return null;
		} else {
			DetachedCriteria criteria = DetachedCriteria.forClass(AghGrupoCids.class);
			criteria.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), seqCapituloCid));
			criteria.add(Restrictions.eq(AghGrupoCids.Fields.SITUACAO.toString(), DominioSituacao.A));
			criteria.addOrder(Order.asc(AghGrupoCids.Fields.DESCRICAO.toString()));

			return executeCriteria(criteria);
		}
	}

	/**
	 * Método que obtem o seq do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	public Integer obterSeqAnterior(Integer seq, Integer cpcSeq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(AghGrupoCids.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghGrupoCids.Fields.SEQ.toString())));

		Integer seqAnterior = (Integer) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return seqAnterior;
	}

	/**
	 * Método que obtem o cpcSeq do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	public Integer obterCpcSeqAnterior(Integer seq, Integer cpcSeq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(AghGrupoCids.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString())));

		Integer cpcSeqAnterior = (Integer) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return cpcSeqAnterior;
	}

	/**
	 * Método que obtem a descrição do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	public String obterDescricaoAnterior(Integer seq, Integer cpcSeq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(AghGrupoCids.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghGrupoCids.Fields.DESCRICAO.toString())));

		String descricaoAnterior = (String) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return descricaoAnterior;
	}

	/**
	 * Método que obtem a data de criação do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	public Date obterDataCriacaoAnterior(Integer seq, Integer cpcSeq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(AghGrupoCids.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghGrupoCids.Fields.CRIADO_EM.toString())));

		Date dataCriadoEmAnterior = (Date) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return dataCriadoEmAnterior;
	}

	/**
	 * Método que obtem o servidor do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	public Integer obterMatriculaServidorAnterior(Integer seq, Integer cpcSeq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(AghGrupoCids.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghGrupoCids.Fields.RAP_SERVIDOR_MATRICULA.toString())));

		Integer matricula = (Integer) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return matricula;
	}

	/**
	 * Método que obtem o servidor do grupo salvo no banco
	 * 
	 * @param seq
	 *            , cpcSeq
	 * @return
	 */
	public Short obterVinCodigoServidorAnterior(Integer seq, Integer cpcSeq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria.forClass(AghGrupoCids.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ.toString(), seq));
		criteriaDadosAnteriores.add(Restrictions.eq(AghGrupoCids.Fields.SEQ_AGH_CAPITULO_CID.toString(), cpcSeq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghGrupoCids.Fields.RAP_SERVIDOR_VIN_CODIGO.toString())));

		Short vinCodigo = (Short) this.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return vinCodigo;
	}

	public AghGrupoCids obterGrupoCidPorSigla(String sigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghGrupoCids.class);
		criteria.add(Restrictions.ilike(AghGrupoCids.Fields.SIGLA.toString(), sigla, MatchMode.EXACT));
		return (AghGrupoCids) this.executeCriteriaUniqueResult(criteria);
	}

}
