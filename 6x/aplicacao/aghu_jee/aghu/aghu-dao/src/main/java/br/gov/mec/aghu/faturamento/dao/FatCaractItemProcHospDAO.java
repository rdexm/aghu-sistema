package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCaractItemProcHospId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoCaractItens;

public class FatCaractItemProcHospDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCaractItemProcHosp> {

	private static final long serialVersionUID = -3397741056306549272L;

	/**
	 * Usado por
	 * {@link CaracteristicaItemProcedimentoHospitalarRN#obterCaracteristicaProcHospPorId(FatCaractItemProcHospId)}
	 * 
	 * @param fatItemProcHospId
	 * @return
	 */
	public FatCaractItemProcHosp obterPorItemProcHospTipoCaract(FatItensProcedHospitalarId fatItemProcHospId, Integer fatTipoCaractSeq) {

		FatCaractItemProcHosp result = null;
		FatCaractItemProcHospId id = null;

		id = new FatCaractItemProcHospId(fatItemProcHospId.getPhoSeq(), fatItemProcHospId.getSeq(), fatTipoCaractSeq);
		result = this.obterPorChavePrimaria(id);

		return result;
	}

	/**
	 * Busca FatCaractItemProcHosp pelo tipo de característica
	 * (FatCaractItemProcHosp)
	 * 
	 * @param tipoCaractItemSeq
	 * @return
	 */
	public List<FatCaractItemProcHosp> listarPorFatTipoCaractItens(Integer tipoCaractItemSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class);
		criteria.add(Restrictions.isNotNull(FatCaractItemProcHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString()));
		criteria.createAlias(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(),
				FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString());
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.SEQ_TIPO_CARACTERISTICA_ITEM.toString(), tipoCaractItemSeq));
		return executeCriteria(criteria);
	}

	public FatCaractItemProcHosp obterQtdeFatCaractItemProcHosp(final FatItensProcedHospitalar proced, final Integer... tctSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class);
		criteria.add(Restrictions.isNotNull(FatCaractItemProcHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString()));
		criteria.createAlias(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(),
				FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.in(FatCaractItemProcHosp.Fields.SEQ_TIPO_CARACTERISTICA_ITEM.toString(), tctSeq));
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), proced));

		criteria.createAlias(FatCaractItemProcHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(),
				FatCaractItemProcHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), JoinType.LEFT_OUTER_JOIN);

		FatCaractItemProcHosp retorno = (FatCaractItemProcHosp) executeCriteriaUniqueResult(criteria);
		return retorno;
	}

	/**
	 * Listar FatCaractItemProcHosp filtrando por iphPhoSeq e iphSeq, que são
	 * parte do ID desta entidade.
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return List<FatCaractItemProcHosp>
	 */
	public List<FatCaractItemProcHosp> listarCaractItemProcHospPorSeqEPhoSeq(Short iphPhoSeq, Integer iphSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class);
		criteria.createAlias(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(),
				FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString());

		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		return executeCriteria(criteria);
	}

	/**
	 * Listar FatCaractItemProcHosp filtrando por iphPhoSeq, iphSeq e
	 * caracteristica do tipoCaracteristicaItem
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param caracteristica
	 * @return
	 */
	public List<FatCaractItemProcHosp> listarPorIphCaracteristica(Integer iphSeq, Short iphPhoSeq, DominioFatTipoCaractItem caracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class);

		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		criteria.createAlias(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(),
				FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString());
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString() + "."
				+ FatTipoCaractItens.Fields.CARACTERISTICA.toString(), caracteristica.getDescricao()));

		return executeCriteria(criteria);
	}

	public List<FatCaractItemProcHosp> listarCaractItemProcHospPorPhoSeqECodTabela(Short iphPhoSeq, Long codTabela) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class);
		criteria.createAlias(FatCaractItemProcHosp.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), "IPH");

		criteria.createAlias(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(),
				FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString(), JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), codTabela));

		return executeCriteria(criteria);
	}

	public List<FatCaractItemProcHosp> obterFatCaractItemProcHosp(Short iphPhoSeq, Integer iphSeq, Integer tctSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class, "IPH");
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.TCT_SEQ.toString(), tctSeq));
		return executeCriteria(criteria);
	}

	public void removerPorId(FatCaractItemProcHospId id) {
		remover(obterPorChavePrimaria(id));
	}
	
	public boolean existeFatCaractItemProcHosp(Short iphPhoSeq, BigDecimal valorNumerico, Integer iphSeq,FatCaractItemProcHosp fatCaractItemProcHosp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class, "IPH");
		if (valorNumerico != null) {
			criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_SEQ.toString(), valorNumerico.intValue()));
		}
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(FatCaractItemProcHosp.Fields.TCT_SEQ.toString(), iphSeq));
		
		List<FatCaractItemProcHosp> caractItemProcHosp = executeCriteria(criteria);

		if (caractItemProcHosp.isEmpty()) {
			return false;
		} 
		
		fatCaractItemProcHosp.setValorData(caractItemProcHosp.get(0).getValorData());
		fatCaractItemProcHosp.setValorNumerico(caractItemProcHosp.get(0).getValorNumerico());
		fatCaractItemProcHosp.setValorChar(caractItemProcHosp.get(0).getValorChar());
		
		return true;
	}
}
