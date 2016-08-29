package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

public class FatExcCnvGrpItemProcDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatExcCnvGrpItemProc> {

	private static final long serialVersionUID = -5507879966127300807L;

	public Long obterQtdPorIphPhiGrcCnv(
			Short iphPhoRealizSeq,Integer iphRealizSeq, Integer phiSeq, 
			Short cpgGrcSeq, Byte cpgCphCspSeq, Short cphCphCspCnvCodigo, String ttrCodigo) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatExcCnvGrpItemProc.class);
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ_REALIZADO.toString(), iphPhoRealizSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.IPH_SEQ_REALIZADO.toString(), iphRealizSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_GRC_SEQ.toString(), cpgGrcSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cphCphCspCnvCodigo));
		criteria.add(Restrictions.eqProperty(FatExcCnvGrpItemProc.Fields.CPG_CPH_PHO_SEQ.toString(),
										   FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ.toString()));
		
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.TTR_CODIGO.toString(), ttrCodigo));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		return this.executeCriteriaCount(criteria);
	}
	
	public Long buscaExcecaoPhiSsmEnviado(Integer phiSeq, Short grcSus, Short phoRealiz, Integer iphRealiz, Byte cspSeq,
			Short cnvCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExcCnvGrpItemProc.class);

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_GRC_SEQ.toString(), grcSus));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ_REALIZADO.toString(), phoRealiz));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.IPH_SEQ_REALIZADO.toString(), iphRealiz));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eqProperty(FatExcCnvGrpItemProc.Fields.CPG_CPH_PHO_SEQ.toString(),
				FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ.toString()));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		return executeCriteriaCount(criteria);
	}

	@SuppressWarnings("unchecked")
	public FatExcCnvGrpItemProc buscaItemProcedimentoHospitalarEquivalenteProcedimentoHospitalarInternoTabelaExcecao(
			Integer phiSeq, Short quantidadeRealizada, Short grcSus, DominioSituacao situacao, Short phoRealiz, Integer iphRealiz,
			Byte cspSeq, Short cnvCodigo) {
		StringBuffer hql = new StringBuffer(450);

		hql.append(" select new br.gov.mec.aghu.model.FatExcCnvGrpItemProc(")
					.append("  egi.").append(FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ.toString())
					.append(", egi.").append(FatExcCnvGrpItemProc.Fields.IPH_SEQ.toString())
					.append(')');
		hql.append(" from ");
		hql.append(FatExcCnvGrpItemProc.class.getSimpleName());
		hql.append(" as egi, ");
		hql.append(FatItensProcedHospitalar.class.getSimpleName());
		hql.append(" as iph ");
		hql.append(" where egi.");
		hql.append(FatExcCnvGrpItemProc.Fields.IPH_SEQ.toString());
		hql.append(" = iph.");
		hql.append(FatItensProcedHospitalar.Fields.SEQ.toString());
		hql.append(" and egi.");
		hql.append(FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ.toString());
		hql.append(" = iph.");
		hql.append(FatItensProcedHospitalar.Fields.PHO_SEQ.toString());
		hql.append(" and iph.");
		hql.append(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao ");
		hql.append(" and coalesce(iph.");
		hql.append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.toString());
		hql.append(", 1) <= :quantidadeRealizada ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ_REALIZADO.toString()).append(" = :phoRealiz ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.IPH_SEQ_REALIZADO.toString()).append(" = :iphRealiz ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.PHI_SEQ.toString()).append(" = :phiSeq ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.CPG_GRC_SEQ.toString()).append(" = :grcSus ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo ");
		hql.append(" and egi.").append(FatExcCnvGrpItemProc.Fields.CPG_CPH_PHO_SEQ.toString()).append(" = egi.").append(
				FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ.toString());
		hql.append(" order by substring(cast(iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" as string),1,5), iph.");
		hql.append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.toString());
		hql.append(" desc ");

		Query query = createHibernateQuery(hql.toString());
		query.setMaxResults(1);

		query.setParameter("phiSeq", phiSeq);
		query.setParameter("quantidadeRealizada", quantidadeRealizada);
		query.setParameter("grcSus", grcSus);
		query.setParameter("situacao", situacao);
		query.setParameter("phoRealiz", phoRealiz);
		query.setParameter("iphRealiz", iphRealiz);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setMaxResults(1);
		
		List<FatExcCnvGrpItemProc> list = query.list();
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public FatExcCnvGrpItemProc buscarPrimeiraFatExcCnvGrpItemProcProcedimentoNecessitaNF(Integer pPhiSeq, Short pIphPhoSeq,
			Integer pIphSeq, Short pCnvCodigo, Byte pCnvCspSeq, Short grcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExcCnvGrpItemProc.class);

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ_REALIZADO.toString(), pIphPhoSeq));

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.IPH_SEQ_REALIZADO.toString(), pIphSeq));

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.PHI_SEQ.toString(), pPhiSeq));

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_SEQ.toString(), pCnvCspSeq));

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), pCnvCodigo));

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_GRC_SEQ.toString(), grcSeq));

		criteria.add(Restrictions.eqProperty(FatExcCnvGrpItemProc.Fields.CPG_CPH_PHO_SEQ.toString(),
				FatExcCnvGrpItemProc.Fields.IPH_PHO_SEQ.toString()));

		List<FatExcCnvGrpItemProc> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	/**
	 * Metodo que monta uma criteria para listar FatExcCnvGrpItemProc filtrando por Plano (cpgCphCspSeq), Convenio (cpgCphCspCnvCodigo), ProcedInterno (phiSeq) e Tabela(phoSeq),
	 * ordenando por COD_TABELA do item procedimento hospitalar realizado e pelo COD_TABELA item procedimento hospitalar; 
	 * 
	 * @param phiSeq
	 * @param phoSeq
	 * @param cpgCphCspCnvCodigo
	 * @param cpgCphCspSeq
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriterialistarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(Integer phiSeq, Short phoSeq,
			Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatExcCnvGrpItemProc.class);
		
		criteria.createAlias(FatExcCnvGrpItemProc.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), FatExcCnvGrpItemProc.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatExcCnvGrpItemProc.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), FatExcCnvGrpItemProc.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatExcCnvGrpItemProc.Fields.FAT_TIPO_TRANSPLANTE.toString(), FatExcCnvGrpItemProc.Fields.FAT_TIPO_TRANSPLANTE.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.PHI_SEQ.toString(), phiSeq));

		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_PHO_SEQ.toString(), phoSeq));
		
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		
		criteria.add(Restrictions.eq(FatExcCnvGrpItemProc.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		
		return criteria;
	}
	
	/**
	 * Metodo para listar FatExcCnvGrpItemProc filtrando por Plano (cpgCphCspSeq), Convenio (cpgCphCspCnvCodigo), ProcedInterno (phiSeq) e Tabela(phoSeq),
	 * ordenando por COD_TABELA do item procedimento hospitalar realizado e pelo COD_TABELA item procedimento hospitalar; 
	 * 
	 * @param phiSeq
	 * @param phoSeq
	 * @param cpgCphCspCnvCodigo
	 * @param cpgCphCspSeq
	 * @return List<FatExcCnvGrpItemProc>
	 */
	public List<FatExcCnvGrpItemProc> listarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(Integer phiSeq, Short phoSeq,
			Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq) {
	
		DetachedCriteria criteria = montarCriterialistarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(phiSeq, phoSeq,
				cpgCphCspCnvCodigo, cpgCphCspSeq); 
		
		criteria.addOrder(Order.asc(FatExcCnvGrpItemProc.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString() + "." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(FatExcCnvGrpItemProc.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString() + "." + FatItensProcedHospitalar.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}

}
