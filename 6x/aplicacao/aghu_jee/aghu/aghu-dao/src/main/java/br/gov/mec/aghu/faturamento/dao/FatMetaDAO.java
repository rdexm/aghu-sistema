package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.model.FatSubGrupo;

public class FatMetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMeta> {

	private static final long serialVersionUID = -7932108879594317570L;

	/**
	 * Retorna a lista de metas ordenada de forma decrescente pela data de
	 * início da vigência.
	 * 
	 * @param grpSeq
	 * @param subGrupoSeq
	 * @param formOrgCodigo
	 * @param carcFinancSeq
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return
	 */
	public List<FatMeta> listarMetaPeloGrupoPeloSubGrupoPelaFormOrgPeloFinancPeloProced(
			final Short grpSeq, final Byte subGrupoSeq,
			final Byte formOrgCodigo, final Integer carcFinancSeq,
			final Short iphPhoSeq, final Integer iphSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatMeta.class);

		criteria.createAlias(FatMeta.Fields.FAT_FORMAS_ORGANIZACAO.toString(),
				"FOF");
		criteria.createAlias(
				FatMeta.Fields.FAT_CARACT_FINANCIAMENTO.toString(), "CRF");
		criteria.createAlias(
				FatMeta.Fields.FAT_ITENS_PROCED_HOSPITALAR.toString(), "IPH",
				Criteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("FOF."
				+ FatFormaOrganizacao.Fields.ID_SGR_GRP_SEQ.toString(), grpSeq));

		criteria.add(Restrictions.eq("FOF."
				+ FatFormaOrganizacao.Fields.ID_SGR_SUB_GRUPO.toString(),
				subGrupoSeq));

		criteria.add(Restrictions.eq("FOF."
				+ FatFormaOrganizacao.Fields.ID_CODIGO.toString(),
				formOrgCodigo));

		criteria.add(Restrictions.eq(
				"CRF." + FatCaractFinanciamento.Fields.SEQ.toString(),
				carcFinancSeq));

		if (iphPhoSeq != null && iphSeq != null) {
			criteria.add(Restrictions.eq("IPH."
					+ FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
			criteria.add(Restrictions.eq("IPH."
					+ FatItensProcedHospitalar.Fields.PHO_SEQ.toString(),
					iphPhoSeq));
		} else {
			criteria.add(Restrictions.isNull("IPH."
					+ FatItensProcedHospitalar.Fields.ID.toString()));
		}

		criteria.addOrder(Order.desc(FatMeta.Fields.DTHR_INICIO_VIG.toString()));

		return executeCriteria(criteria);
	}

	public List<FatMeta> pesquisarFatMeta(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean indInternacao,
			final Boolean indAmbulatorio) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatMeta.class);

		criteria.createAlias(FatMeta.Fields.GRUPO.toString(), "grp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatMeta.Fields.SGR_SUB_GRUPO.toString(), "sgr", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatMeta.Fields.FAT_FORMAS_ORGANIZACAO.toString(), "fog", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatMeta.Fields.FAT_CARACT_FINANCIAMENTO.toString(), "fcf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatMeta.Fields.FAT_ITENS_PROCED_HOSPITALAR.toString(), "iph", JoinType.LEFT_OUTER_JOIN);
		
		if (formaOrganizacao != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_FORMAS_ORGANIZACAO.toString(),
					formaOrganizacao));
		}

		if (subGrupo != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.SGR_SUB_GRUPO.toString(), subGrupo));
		}

		if (grupo != null) {
			criteria.add(Restrictions.eq(FatMeta.Fields.GRUPO.toString(), grupo));
		}

		if (financiamento != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_CARACT_FINANCIAMENTO.toString(),
					financiamento));
		}

		if (procedimento != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_ITENS_PROCED_HOSPITALAR.toString(),
					procedimento));
		}
		
		//desconsiderar os filtros IND_INTERNACAO e IND_AMBULATORIO se os dois forem FALSE
		if (Boolean.TRUE.equals(indInternacao) || Boolean.TRUE.equals(indAmbulatorio)) {
			if (indAmbulatorio != null) {
				criteria.add(Restrictions.eq(
						FatMeta.Fields.IND_AMBULATORIO.toString(),
						indAmbulatorio));
			}
			
			if (indInternacao != null) {
				criteria.add(Restrictions.eq(
						FatMeta.Fields.IND_INTERNACAO.toString(),
						indInternacao));
			}
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	public Long pesquisarFatMetaCount(final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean indInternacao,
			final Boolean indAmbulatorio) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatMeta.class);

		if (formaOrganizacao != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_FORMAS_ORGANIZACAO.toString(),
					formaOrganizacao));
		}

		if (subGrupo != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.SGR_SUB_GRUPO.toString(), subGrupo));
		}

		if (grupo != null) {
			criteria.add(Restrictions.eq(FatMeta.Fields.GRUPO.toString(), grupo));
		}

		if (financiamento != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_CARACT_FINANCIAMENTO.toString(),
					financiamento));
		}

		if (procedimento != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_ITENS_PROCED_HOSPITALAR.toString(),
					procedimento));
		}

		//desconsiderar os filtros IND_INTERNACAO e IND_AMBULATORIO se os dois forem FALSE
		if (Boolean.TRUE.equals(indInternacao) || Boolean.TRUE.equals(indAmbulatorio)) {
			if (indAmbulatorio != null) {
				criteria.add(Restrictions.eq(
						FatMeta.Fields.IND_AMBULATORIO.toString(),
						indAmbulatorio));
			}
			
			if (indInternacao != null) {
				criteria.add(Restrictions.eq(
						FatMeta.Fields.IND_INTERNACAO.toString(),
						indInternacao));
			}
		}
		
		return executeCriteriaCount(criteria);
	}

	public boolean pesquisarFatMetaComDthrFimVig(final FatGrupo grupo,
			final FatSubGrupo subGrupo,
			final FatFormaOrganizacao formaOrganizacao,
			final FatCaractFinanciamento financiamento,
			final FatItensProcedHospitalar procedimento,
			final Boolean ambulatorio,
			final Boolean internacao) {

		boolean comDthrFimVig = true;

		DetachedCriteria criteria = DetachedCriteria.forClass(FatMeta.class);

		if (formaOrganizacao != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_FORMAS_ORGANIZACAO.toString(),
					formaOrganizacao));
		} else {
			criteria.add(Restrictions
					.isNull(FatMeta.Fields.FAT_FORMAS_ORGANIZACAO.toString()));
		}

		if (subGrupo != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.SGR_SUB_GRUPO.toString(), subGrupo));
		} else {
			criteria.add(Restrictions.isNull(FatMeta.Fields.SGR_SUB_GRUPO
					.toString()));
		}

		if (grupo != null) {
			criteria.add(Restrictions.eq(FatMeta.Fields.GRUPO.toString(), grupo));
		} else {
			criteria.add(Restrictions.isNull(FatMeta.Fields.GRUPO.toString()));
		}

		if (financiamento != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_CARACT_FINANCIAMENTO.toString(),
					financiamento));
		} else {
			criteria.add(Restrictions
					.isNull(FatMeta.Fields.FAT_CARACT_FINANCIAMENTO.toString()));
		}

		if (procedimento != null) {
			criteria.add(Restrictions.eq(
					FatMeta.Fields.FAT_ITENS_PROCED_HOSPITALAR.toString(),
					procedimento));
		} else {
			criteria.add(Restrictions
					.isNull(FatMeta.Fields.FAT_ITENS_PROCED_HOSPITALAR
							.toString()));
		}

		List<FatMeta> metasRepetidas = executeCriteria(criteria);
		for (FatMeta meta : metasRepetidas) {
			// percorre todos os registros buscando a DthrFimVig
			if ((meta.getIndAmbulatorio() && meta.getIndAmbulatorio().equals(
					ambulatorio))
					|| (meta.getIndInternacao() && meta.getIndInternacao()
							.equals(internacao))
					&& meta.getDthrFimVig() == null) {
				comDthrFimVig = false;// Se DthrFimVig igual a null, meta está
										// ATIVA - Não pode cadastrar outra
										// igual.
				break;
			}
		}
		return comDthrFimVig;
	}
}
