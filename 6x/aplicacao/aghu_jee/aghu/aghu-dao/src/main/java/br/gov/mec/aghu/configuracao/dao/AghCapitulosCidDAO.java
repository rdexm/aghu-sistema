package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCapitulosCid;

public class AghCapitulosCidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCapitulosCid> {

	private static final long serialVersionUID = 4489142405477781137L;

	private DetachedCriteria createPesquisaCriteria(Short numero,
			String descricao, DominioSimNao indExigeCidSecundario,
			DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		if (numero != null) {
			criteria.add(Restrictions.eq(AghCapitulosCid.Fields.NUMERO
					.toString(), numero));
		}

		if (StringUtils.isNotBlank(descricao)) {
			descricao.toUpperCase();
			criteria.add(Restrictions.ilike(AghCapitulosCid.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (indExigeCidSecundario != null) {
			criteria.add(Restrictions.eq(
					AghCapitulosCid.Fields.IND_EXIGE_CID_SECUNDARIO.toString(),
					indExigeCidSecundario));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(AghCapitulosCid.Fields.IND_SITUACAO
					.toString(), indSituacao));
		}

		return criteria;
	}

	public List<AghCapitulosCid> pesquisarCapitulosCidsAtivo() {
		DetachedCriteria criteria = this.createPesquisaCriteria(null, null,
				null, DominioSituacao.A);
		criteria.addOrder(Order
				.asc(AghCapitulosCid.Fields.NUMERO.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Método que obtem o servidor do capitulo salvo no banco
	 * 
	 * @param seq
	 * @return
	 */
	public Short obterVinCodigoServidorAnterior(Integer seq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghCapitulosCid.Fields.SEQ
				.toString(), seq));
		criteriaDadosAnteriores
				.setProjection(Projections
						.projectionList()
						.add(
								Projections
										.property(AghCapitulosCid.Fields.RAP_SERVIDOR_VIN_CODIGO
												.toString())));

		Short vinCodigo = (Short) this
				.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return vinCodigo;
	}
	
	public Integer obterMatriculaServidorAnterior(Integer seq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghCapitulosCid.Fields.SEQ
				.toString(), seq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections
						.property(AghCapitulosCid.Fields.RAP_SERVIDOR_MATRICULA
								.toString())));

		Integer matricula = (Integer) this
				.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return matricula;
	}

	public Date obterDataCriacaoAnterior(Integer seq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghCapitulosCid.Fields.SEQ
				.toString(), seq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghCapitulosCid.Fields.CRIADO_EM
						.toString())));

		Date dataCriadoEmAnterior = (Date) this
				.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return dataCriadoEmAnterior;
	}

	public Integer obterSeqAnterior(Integer seq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghCapitulosCid.Fields.SEQ
				.toString(), seq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghCapitulosCid.Fields.SEQ.toString())));

		Integer seqAnterior = (Integer) this
				.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return seqAnterior;
	}

	public String obterDescricaoAnterior(Integer seq) {
		DetachedCriteria criteriaDadosAnteriores = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		criteriaDadosAnteriores.add(Restrictions.eq(AghCapitulosCid.Fields.SEQ
				.toString(), seq));
		criteriaDadosAnteriores.setProjection(Projections.projectionList().add(
				Projections.property(AghCapitulosCid.Fields.DESCRICAO
						.toString())));

		String descricaoAnterior = (String) this
				.executeCriteriaUniqueResult(criteriaDadosAnteriores);

		return descricaoAnterior;
	}

	public List<AghCapitulosCid> pesquisar(Integer firstResult,
			Integer maxResults, Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao) {
		DetachedCriteria criteria = this.createPesquisaCriteria(numero, descricao,
				indExigeCidSecundario, indSituacao);

		return this.executeCriteria(criteria, firstResult, maxResults, null, true);
	}

	public Long obterCapituloCidCount(Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao) {
		return this.executeCriteriaCount(this.createPesquisaCriteria(numero, descricao,
				indExigeCidSecundario, indSituacao));
	}
	
	public DominioSituacao obterSituacaoCapituloCid(Integer seqCapitulo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCapitulosCid.class);
		criteria.add(Restrictions.eq(AghCapitulosCid.Fields.SEQ.toString(), seqCapitulo));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghCapitulosCid.Fields.IND_SITUACAO.toString())));
		DominioSituacao indSituacao = (DominioSituacao) this.executeCriteriaUniqueResult(criteria);
		return indSituacao;
	}
	
	
	/**
	 * Obtém um capítulo CID pelo seu número cadastrado. Caso seja passado o
	 * atributo seq, a consulta irá desconsiderar registros com esse código.
	 * Essa consulta é utilizada na validação para garantir que não está sendo
	 * inserido um novo registro com um numero já existente. Ou alterando o
	 * número de um registro existente para outro existente.
	 * 
	 * @param numero
	 *            filtro da consulta
	 * @param seq
	 *            código do registro a ser desconsiderado
	 * @return capitulo CID, se existir. null caso contrário
	 */
	public AghCapitulosCid obterCapituloCidPorNumero(Short numero, Integer seq){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCapitulosCid.class);

		if (numero != null) {
			criteria.add(
					Restrictions.eq(AghCapitulosCid.Fields.NUMERO.toString(),
							numero));
			if(seq != null){
				criteria.add(
					Restrictions.not(Restrictions.eq(
							AghCapitulosCid.Fields.SEQ.toString(), seq)));
			}
		} else {
			return null;
		}
		
		return (AghCapitulosCid) this.executeCriteriaUniqueResult(criteria);
	}
	
}
