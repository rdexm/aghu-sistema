package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatConvFaixaEtaria;
import br.gov.mec.aghu.model.FatConvFxEtariaItem;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatConvFxEtariaItemDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvFxEtariaItem> {
	
	private static final long serialVersionUID = -5674745364946743186L;

	protected DetachedCriteria obterCriteriaPorIphCnv(
			final Short codConvenio,
			final Short iphPhoSeq,
			final Integer iphSeq) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(FatConvFxEtariaItem.class);
		result.add(Restrictions.eq(FatConvFxEtariaItem.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		result.add(Restrictions.eq(FatConvFxEtariaItem.Fields.IPH_SEQ.toString(), iphSeq));
		result.add(Restrictions.eq(FatConvFxEtariaItem.Fields.CFE_CNV_CODIGO.toString(), codConvenio));

		return result;
	}
	
	public int obterQtdPorIphCnv(
			final Short codConvenio,
			final Short iphPhoSeq,
			final Integer iphSeq) {
		
		Long result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorIphCnv(codConvenio, iphPhoSeq, iphSeq);
		result = this.executeCriteriaCount(criteria);
		
		return (result != null ? result.intValue() : 0);		
	}

	public List<Byte> obterListaCodSusPorIphCnvDataFaixaEtariaAtiva(final Short codConvenio, final Short iphPhoSeq, final Integer iphSeq,
			final Date data) {
		return this.executeCriteria(obterCriteriaListaCodSusPorIphCnvDataFaixaEtariaAtiva(codConvenio, iphPhoSeq, iphSeq, data));
	}

	public List<Byte> obterListaCodSusPorIphCnvIdadeDataFaixaEtariaAtiva(final Short codConvenio, final Short iphPhoSeq, final Integer iphSeq,
			final Date data, final Short idade) {
		return this.executeCriteria(obterCriteriaListaCodSusPorIphCnvIdadeDataFaixaEtariaAtiva(codConvenio, iphPhoSeq, iphSeq, data, idade));
	}

	private DetachedCriteria obterCriteriaListaCodSusPorIphCnvIdadeDataFaixaEtariaAtiva(final Short codConvenio, final Short iphPhoSeq,
			final Integer iphSeq, final Date data, final Short idade) {
		/*
		 * CURSOR c_fx_etaria ( p_cnv_codigo IN NUMBER, p_iph_pho_seq IN NUMBER,
		 * p_iph_seq IN NUMBER, p_idade IN NUMBER, p_data IN DATE ) IS SELECT
		 * cfe.codigo_sus FROM fat_conv_fx_etarias_itens cfi,
		 * fat_conv_faixa_etarias cfe WHERE cfi.cfe_seqp = cfe.seqp AND
		 * cfi.cfe_cnv_codigo = cfe.cnv_codigo AND cfi.iph_pho_seq =
		 * p_iph_pho_seq AND cfi.iph_seq = p_iph_seq AND cfi.cfe_cnv_codigo =
		 * p_cnv_codigo AND cfe.dt_inicio_validade <= p_data AND
		 * NVL(cfe.dt_fim_validade,SYSDATE) >= p_data AND cfe.idade_inicio <=
		 * p_idade AND cfe.idade_fim >= p_idade AND cfe.ind_situacao_registro =
		 * 'A';
		 */
		DetachedCriteria criteria = obterCriteriaListaCodSusPorIphCnvDataFaixaEtariaAtiva(codConvenio, iphPhoSeq, iphSeq, data);
		criteria.add(Restrictions.le("cfe." + FatConvFaixaEtaria.Fields.IDADE_INICIO, idade));
		criteria.add(Restrictions.ge("cfe." + FatConvFaixaEtaria.Fields.IDADE_FIM, idade));

		return criteria;
	}

	private DetachedCriteria obterCriteriaListaCodSusPorIphCnvDataFaixaEtariaAtiva(final Short codConvenio, final Short iphPhoSeq, final Integer iphSeq, final Date data) {
		/*
		 * CURSOR c_fx_etaria_item ( p_cnv_codigo IN NUMBER, p_iph_pho_seq IN
		 * NUMBER, p_iph_seq IN NUMBER, p_data IN DATE ) ISk SELECT
		 * cfe.codigo_sus FROM fat_conv_fx_etarias_itens cfi,
		 * fat_conv_faixa_etarias cfe WHERE cfe.seqp = cfi.cfe_seqp AND
		 * cfe.cnv_codigo = cfi.cfe_cnv_codigo AND cfe.ind_situacao_registro =
		 * 'A' AND cfe.dt_inicio_validade <= p_data AND
		 * NVL(cfe.dt_fim_validade,SYSDATE) >= p_data AND cfi.iph_pho_seq =
		 * p_iph_pho_seq AND cfi.iph_seq = p_iph_seq AND cfi.cfe_cnv_codigo =
		 * p_cnv_codigo;
		 */
		DetachedCriteria criteria = this.obterCriteriaPorIphCnv(codConvenio, iphPhoSeq, iphSeq);
		criteria.createAlias(FatConvFxEtariaItem.Fields.FAT_CONV_FAIXA_ETARIA.toString(), "cfe");
		criteria.add(Restrictions.eq("cfe." + FatConvFaixaEtaria.Fields.IND_SITUACAO_REGISTRO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.le("cfe." + FatConvFaixaEtaria.Fields.DT_INICIO_VALIDADE.toString(), data));

		criteria.add(Restrictions.sqlRestriction(this.isOracle() ? " NVL(cfe1_.DT_FIM_VALIDADE, SYSDATE) >= to_date('" + DateUtil.dataToString(data, "dd/MM/yyyy HH:mm") + "', 'dd/mm/yyyy hh24:mi') "
				: " (CASE WHEN cfe1_.DT_FIM_VALIDADE IS NULL THEN NOW() ELSE cfe1_.DT_FIM_VALIDADE END) >= to_date('" + DateUtil.dataToString(data, "dd/MM/yyyy HH:mm") + "', 'dd/mm/yyyy hh24:mi') "));//, data, TimestampType.INSTANCE));
		criteria.setProjection(Projections.property("cfe." + FatConvFaixaEtaria.Fields.CODIGO_SUS.toString()));
		return criteria;		
	}
}
