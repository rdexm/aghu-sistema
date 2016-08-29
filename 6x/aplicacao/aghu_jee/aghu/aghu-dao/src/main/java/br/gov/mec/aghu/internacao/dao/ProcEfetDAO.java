package br.gov.mec.aghu.internacao.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AelTmpBullInt;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.EceItemAdministrado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.ProcEfet;
import br.gov.mec.aghu.model.ProcHcpaXConv;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.TabPgto;
import br.gov.mec.aghu.model.TipoItem;
import br.gov.mec.aghu.sig.custos.vo.SigProcedimentoMedicamentoExameVO;

public class ProcEfetDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ProcEfet> {

	private static final long serialVersionUID = -704597479100816605L;

	public List<ProcEfet> listarProcEfetPorCtacvNro(Integer ctacvNro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ProcEfet.class);

		criteria.add(Restrictions.eq(ProcEfet.Fields.CTACV_NRO.toString(), ctacvNro));

		return executeCriteria(criteria);
	}

	public List<ProcEfet> listarProcEfetPorCtacvNroPosDate(Integer ctacvNro, Date dateRef) {
		DetachedCriteria cri = DetachedCriteria.forClass(ProcEfet.class);
		cri.add(Restrictions.eq(ProcEfet.Fields.CTACV_NRO.toString(), ctacvNro));
		cri.add(Restrictions.gt(ProcEfet.Fields.DATA.toString(), dateRef));
		return executeCriteria(cri);
	}
	
	public List<ProcEfet> listarProcEfetPorConNumero(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ProcEfet.class, "PCE");
		criteria.createAlias("PCE." + ProcEfet.Fields.CNTA_CONV.toString(), "CTA");
		criteria.createAlias("CTA." + CntaConv.Fields.CONV.toString(), "CONV");
		criteria.add(Restrictions.eq(ProcEfet.Fields.CON_NUMERO.toString(), conNumero));
		return executeCriteria(criteria);
	}
	// #32240 - C2
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarProcedimentosComPhiMedicamentosPorInternacao(Integer intSeq,
			Short pAghuSigCodConvParticular, Short pAghuSigConvTipoTabPgtoCodMedic, Set<Integer> listaPhiSeq,
			Byte pAghuSigConvTipoItemCodMedicDialise) {
		
		StringBuffer sql = new StringBuffer(" select ")
			.append("  phi." + FatProcedHospInternos.Fields.SEQ.toString() + " as phiSeq ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + FatProcedHospInternos.class.getSimpleName() + " phi ")
			.append(" where ")
			.append("  pac." + PacIntdConv.Fields.ATD_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.INTD_COD_PRNT.toString() + " = pac." + PacIntdConv.Fields.COD_PRNT.toString())
			.append("  and pef." + ProcEfet.Fields.CTACV_NRO.toString() + " = cnt." + CntaConv.Fields.NRO.toString())
			
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " in ( select phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("    from ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc ")
			.append("    where ")
			.append("        phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " = :pAghuSigConvTipoTabPgtoCodMedic ) ")
			.append("        and phc." + ProcHcpaXConv.Fields.TPIT_COD.toString() + " != :pAghuSigConvTipoItemCodMedicDialise ) ")
			.append("  and phi." + FatProcedHospInternos.Fields.SEQ.toString() + " in ( :listaPhiSeq ) ")
			.append("  group by phi." + FatProcedHospInternos.Fields.SEQ.toString())
			.append("  order by phi." + FatProcedHospInternos.Fields.SEQ.toString());
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameter("pAghuSigCodConvParticular", pAghuSigCodConvParticular);
		sqlQuery.setParameter("pAghuSigConvTipoTabPgtoCodMedic", pAghuSigConvTipoTabPgtoCodMedic);
		sqlQuery.setParameter("pAghuSigConvTipoItemCodMedicDialise", pAghuSigConvTipoItemCodMedicDialise);
		sqlQuery.setParameterList("listaPhiSeq", listaPhiSeq);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
	// #32240 - C3
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarProcedimentosComPhiExamesPorInternacao(Integer intSeq,
			Short pAghuSigCodConvParticular, List<Short> pAghuSigConvTipoTabPgtoListaCodExames, List<Byte> pAghuSigConvTipoItemListaCodExames,
			Set<Integer> listaPhiSeq) {
		
		StringBuffer sql = new StringBuffer(" select ")
			.append("  phi." + FatProcedHospInternos.Fields.SEQ.toString() + " as phiSeq ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + FatProcedHospInternos.class.getSimpleName() + " phi ")
			.append(" where ")
			.append("  cnt." + CntaConv.Fields.ATENDIMENTO_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and pac." + PacIntdConv.Fields.SEQ.toString() + " = cnt." + CntaConv.Fields.PACC_SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.INTD_COD_PRNT.toString() + " = pac." + PacIntdConv.Fields.COD_PRNT.toString())
			.append("  and pef." + ProcEfet.Fields.CTACV_NRO.toString() + " = cnt." + CntaConv.Fields.NRO.toString())
			.append("  and phi." + FatProcedHospInternos.Fields.SEQ.toString() + " in ( :listaPhiSeq ) ")
			
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " in ( select phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("    from ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc ")
			.append("        ," + AelTmpBullInt.class.getSimpleName() + " ael ")
			.append("    where ")
			.append("        phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("        and ael." + AelTmpBullInt.Fields.EXA_SIGLA.toString() + " = phi." + FatProcedHospInternos.Fields.EMA_EXA_SIGLA.toString())
			.append("        and ael." + AelTmpBullInt.Fields.MAN_SEQ.toString() + " = phi." + FatProcedHospInternos.Fields.EMA_MAN_SEQ.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " in (:pAghuSigConvTipoTabPgtoListaCodExames ) ")
			.append("        and phc." + ProcHcpaXConv.Fields.TPIT_COD.toString() + " in (:pAghuSigConvTipoItemListaCodExames ) ")
			.append("        and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = ael." + AelTmpBullInt.Fields.COD_EXME.toString() + " ) ")
			.append("  group by phi." + FatProcedHospInternos.Fields.SEQ.toString())
			.append("  order by phi." + FatProcedHospInternos.Fields.SEQ.toString());
		
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameter("pAghuSigCodConvParticular", pAghuSigCodConvParticular);
		sqlQuery.setParameterList("pAghuSigConvTipoTabPgtoListaCodExames", pAghuSigConvTipoTabPgtoListaCodExames);
		sqlQuery.setParameterList("pAghuSigConvTipoItemListaCodExames", pAghuSigConvTipoItemListaCodExames);
		sqlQuery.setParameterList("listaPhiSeq", listaPhiSeq);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
	// #32240 - C4
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarProcedimentosComPhiCuidadosEnfermagemPorInternacao(Integer intSeq,
			Set<Integer> listaPhiSeq) {
		
		StringBuffer sql = new StringBuffer(" select ")
			.append("  iad." + EceItemAdministrado.Fields.PHI_SEQ.toString() + " as phiSeq ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + EceItemAdministrado.class.getSimpleName() + " iad ")
			.append(" where ")
			.append("  cnt." + CntaConv.Fields.ATENDIMENTO_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and pac." + PacIntdConv.Fields.SEQ.toString() + " = cnt." + CntaConv.Fields.PACC_SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.CTACV_NRO.toString() + " = cnt." + CntaConv.Fields.NRO.toString())
			.append("  and iad." + EceItemAdministrado.Fields.SEQ.toString() + " = pef." + ProcEfet.Fields.IAD_SEQ.toString())
			.append("  and iad." + EceItemAdministrado.Fields.TIPO_ITEM.toString() + " = :tipoItem ")
			.append("  and iad." + EceItemAdministrado.Fields.TIPO_PRESCRICAO.toString() + " = :tipoPrescricao ")
			.append("  and iad." + EceItemAdministrado.Fields.PHI_SEQ.toString() + " in ( :listaPhiSeq ) ")
			.append("  group by iad." + EceItemAdministrado.Fields.PHI_SEQ.toString())
			.append("  order by iad." + EceItemAdministrado.Fields.PHI_SEQ.toString());
		
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameter("tipoItem", "C");
		sqlQuery.setParameter("tipoPrescricao", "E");
		sqlQuery.setParameterList("listaPhiSeq", listaPhiSeq);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
	// #32240 - C5
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarProcedimentosComPhiDietasPorInternacao(Integer intSeq,
			Set<Integer> listaPhiSeq, Short pAghuSigConvTipoTabPgtoCodMedic, Short pAghuSigCodConvParticular,
			Short pAghuSigConvTabPgtoTpInsumoDietas) {
		StringBuffer sql = new StringBuffer(" select ")
			.append("  phi." + FatProcedHospInternos.Fields.SEQ.toString() + " as phiSeq ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + ProcHcpaXConv.class.getSimpleName() + " phc, ")
			.append("  " + FatProcedHospInternos.class.getSimpleName() + " phi ")
			.append(" where ")
			.append("  cnt." + CntaConv.Fields.ATENDIMENTO_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and pac." + PacIntdConv.Fields.SEQ.toString() + " = cnt." + CntaConv.Fields.PACC_SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.INTD_COD_PRNT.toString() + " = pac." + PacIntdConv.Fields.COD_PRNT.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " = :pAghuSigConvTipoTabPgtoCodMedic")
			.append("  and phi." + FatProcedHospInternos.Fields.SEQ.toString() + " in ( :listaPhiSeq ) ")
			
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " in ( select phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("    from ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc ")
			.append("        ," + TabPgto.class.getSimpleName() + " tpg ")
			.append("    where ")
			.append("        phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = tpg." + TabPgto.Fields.COD.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " = tpg." + TabPgto.Fields.TPTAB_COD.toString())
			.append("        and tpg." + TabPgto.Fields.TP_INSUMO.toString() + " = (:pAghuSigConvTabPgtoTpInsumoDietas )) ")
			
			.append("  group by phi." + FatProcedHospInternos.Fields.SEQ.toString())
			.append("  order by phi." + FatProcedHospInternos.Fields.SEQ.toString());
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameter("pAghuSigConvTipoTabPgtoCodMedic", pAghuSigConvTipoTabPgtoCodMedic);
		sqlQuery.setParameter("pAghuSigCodConvParticular", pAghuSigCodConvParticular);
		sqlQuery.setParameter("pAghuSigConvTabPgtoTpInsumoDietas", pAghuSigConvTabPgtoTpInsumoDietas);
		sqlQuery.setParameterList("listaPhiSeq", listaPhiSeq);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
	// #32240 - C6
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarProcedimentosComPhiMedicamentosDialisePorInternacao(Integer intSeq,
			Set<Integer> listaPhiSeq, Short pAghuSigConvTipoTabPgtoCodMedic, Short pAghuSigCodConvParticular,
			Byte pAghuSigConvTipoItemCodMedicDialise) {
		StringBuffer sql = new StringBuffer(" select ")
			.append("  phi." + FatProcedHospInternos.Fields.SEQ.toString() + " as phiSeq ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + ProcHcpaXConv.class.getSimpleName() + " phc, ")
			.append("  " + FatProcedHospInternos.class.getSimpleName() + " phi ")
			.append(" where ")
			.append("  cnt." + CntaConv.Fields.ATENDIMENTO_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and pac." + PacIntdConv.Fields.SEQ.toString() + " = cnt." + CntaConv.Fields.PACC_SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.INTD_COD_PRNT.toString() + " = pac." + PacIntdConv.Fields.COD_PRNT.toString())
			.append("  and pef." + ProcEfet.Fields.CTACV_NRO.toString() + " = cnt." + CntaConv.Fields.NRO.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = phi." + FatProcedHospInternos.Fields.MAT_CODIGO.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " = :pAghuSigConvTipoTabPgtoCodMedic")
			.append("  and phi." + FatProcedHospInternos.Fields.SEQ.toString() + " in ( :listaPhiSeq ) ")
			
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " in ( select phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("    from ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc ")
			.append("    where ")
			.append("        phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc." + ProcHcpaXConv.Fields.TPIT_COD.toString() + " = :pAghuSigConvTipoItemCodMedicDialise ) ")
			.append("  group by phi." + FatProcedHospInternos.Fields.SEQ.toString())
			.append("  order by phi." + FatProcedHospInternos.Fields.SEQ.toString());
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameter("pAghuSigConvTipoTabPgtoCodMedic", pAghuSigConvTipoTabPgtoCodMedic);
		sqlQuery.setParameter("pAghuSigCodConvParticular", pAghuSigCodConvParticular);
		sqlQuery.setParameter("pAghuSigConvTipoItemCodMedicDialise", pAghuSigConvTipoItemCodMedicDialise);
		sqlQuery.setParameterList("listaPhiSeq", listaPhiSeq);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
	// #47286 - C3
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarReceitaParaOrteseEProtese(Integer intSeq,
			Set<Integer> listaMateriais, Short pAghuSigConvTipoItemGrupoOrtProtCod, Short pAghuSigCodConvParticular) {
		StringBuffer sql = new StringBuffer(" select ")
			.append("  mat." + ScoMaterial.Fields.CODIGO.toString() + " as matCodigo ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + TipoItem.class.getSimpleName() + " tpi, ")
			.append("  " + ProcHcpaXConv.class.getSimpleName() + " phc, ")
			.append("  " + ScoMaterial.class.getSimpleName() + " mat ")
			.append(" where ")
			.append("  cnt." + CntaConv.Fields.ATENDIMENTO_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and pac." + PacIntdConv.Fields.SEQ.toString() + " = cnt." + CntaConv.Fields.PACC_SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.INTD_COD_PRNT.toString() + " = pac." + PacIntdConv.Fields.COD_PRNT.toString())
			.append("  and pef." + ProcEfet.Fields.CTACV_NRO.toString() + " = cnt." + CntaConv.Fields.NRO.toString())
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " = phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = mat." + ScoMaterial.Fields.CODIGO.toString())
			.append("  and tpi." + TipoItem.Fields.GIT_CODIGO.toString() + " = :pAghuSigConvTipoItemGrupoOrtProtCod ")
			.append("  and mat." + ScoMaterial.Fields.CODIGO.toString() + " in (:listaMateriais) ")
			
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " in ( select phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("    from ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc ")
			.append("    where ")
			.append("        phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString())
			.append("        and phc." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc." + ProcHcpaXConv.Fields.TPIT_COD.toString() + " = tpi." + TipoItem.Fields.COD.toString() + " ) ")
			.append("  group by mat." + ScoMaterial.Fields.CODIGO.toString())
			.append("  order by mat." + ScoMaterial.Fields.CODIGO.toString());
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameterList("listaMateriais", listaMateriais);
		sqlQuery.setParameter("pAghuSigCodConvParticular", pAghuSigCodConvParticular);
		sqlQuery.setParameter("pAghuSigConvTipoItemGrupoOrtProtCod", pAghuSigConvTipoItemGrupoOrtProtCod);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
	// #47286 - C4
	@SuppressWarnings("unchecked")
	public List<SigProcedimentoMedicamentoExameVO> buscarReceitaParaQuimioterapia(Integer intSeq,
			Set<Integer> listaMateriais, Short pAghuSigCodConvParticular,
			Short pAghuSigConvTipoTabPgtoCodMedicQuimio, Short pAghuSigConvTipoTabPgtoCodMedic) {
		StringBuffer sql = new StringBuffer(" select ")
			.append("  mat." + ScoMaterial.Fields.CODIGO.toString() + " as matCodigo ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE.toString() + ") as qtde ")
			.append("  , sum(pef." + ProcEfet.Fields.QTDE_CSH.toString() + ") as qtdeCsh ")
			.append(" from ")
			.append(ProcEfet.class.getSimpleName()).append(" pef, ")
			.append("  " + PacIntdConv.class.getSimpleName() + " pac, ")
			.append("  " + AinInternacao.class.getSimpleName() + " int, ")
			.append("  " + AghAtendimentos.class.getSimpleName() + " atd, ")
			.append("  " + CntaConv.class.getSimpleName() + " cnt, ")
			.append("  " + ProcHcpaXConv.class.getSimpleName() + " phc, ")
			.append("  " + ScoMaterial.class.getSimpleName() + " mat ")
			.append(" where ")
			
			.append("  cnt." + CntaConv.Fields.ATENDIMENTO_SEQ.toString() + " = atd." + AghAtendimentos.Fields.SEQ.toString())
			.append("  and pac." + PacIntdConv.Fields.SEQ.toString() + " = cnt." + CntaConv.Fields.PACC_SEQ.toString())
			.append("  and atd." + AghAtendimentos.Fields.INT_SEQ.toString() + " = int." + AinInternacao.Fields.SEQ.toString())
			.append("  and int." + AinInternacao.Fields.SEQ.toString() + " = :intSeq ")
			.append("  and pef." + ProcEfet.Fields.INTD_COD_PRNT.toString() + " = pac." + PacIntdConv.Fields.COD_PRNT.toString())
			.append("  and pef." + ProcEfet.Fields.CTACV_NRO.toString() + " = cnt." + CntaConv.Fields.NRO.toString())
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " = phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("  and phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString() + " = mat." + ScoMaterial.Fields.CODIGO.toString())
			.append("  and mat." + ScoMaterial.Fields.CODIGO.toString() + " in (:listaMateriais) ")
			.append("  and pef." + ProcEfet.Fields.PRHC_COD_HCPA.toString() + " in ( select phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("    from ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc, ")
			.append("         ").append(AfaTipoUsoMdto.class.getSimpleName()).append(" tum, ")
			.append("         ").append(AfaMedicamento.class.getSimpleName()).append(" medic, ")
			.append("         ").append(TabPgto.class.getSimpleName()).append(" tab, ")
			.append("         ").append(ProcHcpaXConv.class.getSimpleName()).append(" phc2 ")
			.append("    where ")
			.append("        phc." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString() + " = phc2." + ProcHcpaXConv.Fields.PRHC_COD_HCPA.toString())
			.append("        and phc2." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc2." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " = :pAghuSigConvTipoTabPgtoCodMedic ")
			.append("        and phc." + ProcHcpaXConv.Fields.CONV_COD.toString() + " = :pAghuSigCodConvParticular ")
			.append("        and phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString() + " = :pAghuSigConvTipoTabPgtoCodMedicQuimio ")
			.append("        and tab." + TabPgto.Fields.TPTAB_COD.toString() + " = phc." + ProcHcpaXConv.Fields.TABPAG_TPTAB_COD.toString())
			.append("        and tab." + TabPgto.Fields.COD.toString() + " = phc." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString())
			.append("        and medic." + AfaMedicamento.Fields.MAT_CODIGO.toString() + " = phc2." + ProcHcpaXConv.Fields.TAB_PAG_COD.toString())
			.append("        and tum." + AfaTipoUsoMdto.Fields.SIGLA.toString() + " = medic." + AfaMedicamento.Fields.TUM_SIGLA.toString())
			.append("        and tum." + AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString() + " = :indQuimioterapico ) ")
			.append("  group by mat." + ScoMaterial.Fields.CODIGO.toString())
			.append("  order by mat." + ScoMaterial.Fields.CODIGO.toString());
		
		final org.hibernate.Query sqlQuery = createHibernateQuery(sql.toString());
		sqlQuery.setParameter("intSeq", intSeq);
		sqlQuery.setParameterList("listaMateriais", listaMateriais);
		sqlQuery.setParameter("pAghuSigCodConvParticular", pAghuSigCodConvParticular);
		sqlQuery.setParameter("pAghuSigConvTipoTabPgtoCodMedic", pAghuSigConvTipoTabPgtoCodMedic);
		sqlQuery.setParameter("pAghuSigConvTipoTabPgtoCodMedicQuimio", pAghuSigConvTipoTabPgtoCodMedicQuimio);
		sqlQuery.setParameter("indQuimioterapico", Boolean.TRUE);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(SigProcedimentoMedicamentoExameVO.class));

		return sqlQuery.list();
	}
	
}
