package br.gov.mec.aghu.sig.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdProcedimentos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.ProcedimentoConvenioVO;
import br.gov.mec.aghu.sig.custos.vo.ProcedimentoSusVO;

public class SigCalculoAtdProcedimentosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdProcedimentos> {

	private static final long serialVersionUID = 4469839390409673519L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		sql.append(" DELETE ").append(SigCalculoAtdProcedimentos.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoAtdProcedimentos.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcedimentoSusVO> buscarProcedimentosSus(Integer intSeq, Short pConvenioSus, Short pTipoGrupoContaSus, boolean principal){
		
		StringBuilder sql = new StringBuilder(721);		   
		sql.append(" SELECT DISTINCT")
			.append(" coi.").append(FatContasInternacao.Fields.INT_SEQ).append(" as intSeq")
			.append(" ,cth.").append(FatContasHospitalares.Fields.PHI_SEQ_REALIZADO).append(" as phiSeq")
			.append(" ,cth.").append(FatContasHospitalares.Fields.SEQ).append(" as cthSeq")
			.append(" ,iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ).append(" as iphPhoSeq")
			.append(" ,iph.").append(FatItensProcedHospitalar.Fields.SEQ).append(" as iphSeq")
		.append(" FROM ")
			.append(FatContasInternacao.class.getSimpleName()).append(" coi ")
			.append(" ,").append(FatContasHospitalares.class.getSimpleName()).append(" cth ")
			.append(" ,").append(FatProcedHospInternos.class.getSimpleName()).append(" phi ")
			.append(" ,").append(FatConvGrupoItemProced.class.getSimpleName()).append(" cgi ")
			.append(" ,").append(FatItensProcedHospitalar.class.getSimpleName()).append(" iph ")
		.append(" WHERE ")
			.append(" coi.").append(FatContasInternacao.Fields.INT_SEQ).append(" = :intSeq")
			.append(" and coi.").append(FatContasInternacao.Fields.CTH_SEQ).append(" = cth.").append(FatContasHospitalares.Fields.SEQ)
			.append(" and cth.").append(FatContasHospitalares.Fields.IND_SITUACAO).append(" = 'O'")
			.append(" and cth.").append(FatContasHospitalares.Fields.CSP_CNV_CODIGO).append(" = :pConvenioSus")
			.append(" and cth.").append(FatContasHospitalares.Fields.PHI_SEQ_REALIZADO).append(" = phi.").append(FatProcedHospInternos.Fields.SEQ)
			.append(" and phi.").append(FatProcedHospInternos.Fields.SEQ).append(" = cgi.").append(FatConvGrupoItemProced.Fields.PHI_SEQ)
			.append(" and cgi.").append(FatConvGrupoItemProced.Fields.IPH_SEQ).append(" = iph.").append(FatItensProcedHospitalar.Fields.SEQ)
			.append(" and cgi.").append(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ).append(" = iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ)
			.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ).append(" = :pTipoGrupoContaSus")
			.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO).append(" = cth.").append(FatContasHospitalares.Fields.CSP_CNV_CODIGO_FIELD)
			.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ).append(" = cth.").append(FatContasHospitalares.Fields.CSP_SEQ_FIELD)
			.append(" and ( iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ).append(", iph.").append(FatItensProcedHospitalar.Fields.SEQ).append(" ) ")
			.append(principal ? " in " : " not in ")
			.append(" ( ")
				.append(" SELECT int.").append(AinInternacao.Fields.IPH_PHO_SEQ).append(", int.").append(AinInternacao.Fields.IPH_SEQ)
				.append(" FROM ").append(AinInternacao.class.getSimpleName()).append(" int ")
				.append(" WHERE ").append(" int.").append(AinInternacao.Fields.SEQ).append(" = coi.").append(FatContasInternacao.Fields.INT_SEQ)
			.append(" ) ")
		;		
		
		final Query query = this.createHibernateQuery(sql.toString());
		query.setParameter("intSeq", intSeq);
		query.setParameter("pConvenioSus", pConvenioSus);
		query.setParameter("pTipoGrupoContaSus", pTipoGrupoContaSus);
		query.setResultTransformer(Transformers.aliasToBean(ProcedimentoSusVO.class));
		
		return query.list();
	}
	
	
	public List<ProcedimentoSusVO> buscarProcedimentosSusPrincipaisInternacao(Integer intSeq, Short pConvenioSus, Short pTipoGrupoContaSus){
		return this.buscarProcedimentosSus(intSeq, pConvenioSus, pTipoGrupoContaSus, true);
	}
	
	@SuppressWarnings("unchecked")
	public Set<ProcedimentoSusVO> buscarProcedimentosSusSecundariosInternacao(Integer intSeq, Short pConvenioSus, Short pTipoGrupoContaSus){
		StringBuilder sql = new StringBuilder(721);	
		
		sql.append(" SELECT DISTINCT")
			.append(" coi.").append(FatContasInternacao.Fields.INT_SEQ).append(" as intSeq")
			.append(" ,cth.").append(FatContasHospitalares.Fields.PHI_SEQ_REALIZADO).append(" as phiSeq")
			.append(" ,cth.").append(FatContasHospitalares.Fields.SEQ).append(" as cthSeq")
			.append(" ,iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ).append(" as iphPhoSeq")
			.append(" ,iph.").append(FatItensProcedHospitalar.Fields.SEQ).append(" as iphSeq")
		.append(" FROM ")
			.append(FatContasInternacao.class.getSimpleName()).append(" coi ")
			.append(" ,").append(FatContasHospitalares.class.getSimpleName()).append(" cth ")
			.append(" ,").append(FatAtoMedicoAih.class.getSimpleName()).append(" ama ")
			.append(" ,").append(FatItensProcedHospitalar.class.getSimpleName()).append(" iph ")
		.append(" WHERE ")
			.append(" coi.").append(FatContasInternacao.Fields.INT_SEQ).append(" = :intSeq")
			.append(" and coi.").append(FatContasInternacao.Fields.CTH_SEQ).append(" = cth.").append(FatContasHospitalares.Fields.SEQ)
			.append(" and cth.").append(FatContasHospitalares.Fields.IND_SITUACAO).append(" = 'O'")
			.append(" and cth.").append(FatContasHospitalares.Fields.CSP_CNV_CODIGO).append(" = :pConvenioSus")
			.append(" and cth.").append(FatContasHospitalares.Fields.SEQ).append(" = ama.").append(FatAtoMedicoAih.Fields.EAI_CTH_SEQ)
			.append(" and ama.").append(FatAtoMedicoAih.Fields.TAO_SEQ).append(" = 1 ")
			.append(" and ama.").append(FatAtoMedicoAih.Fields.IPH_PHO_SEQ).append(" = iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ)
			.append(" and ama.").append(FatAtoMedicoAih.Fields.IPH_SEQ).append(" = iph.").append(FatItensProcedHospitalar.Fields.SEQ)
		;
		final Query query = this.createHibernateQuery(sql.toString());
		query.setParameter("intSeq", intSeq);
		query.setParameter("pConvenioSus", pConvenioSus);
		query.setResultTransformer(Transformers.aliasToBean(ProcedimentoSusVO.class));
		
		List<ProcedimentoSusVO> lista1 = query.list();
		List<ProcedimentoSusVO> lista2 = this.buscarProcedimentosSus(intSeq, pConvenioSus, pTipoGrupoContaSus, false);
		
		
		//Utiliza o set pois na segunda consulta pode retornar as mesmas informações da primeira, mas só deve aparecer uma vez no resultado final
		Set<ProcedimentoSusVO> union = new HashSet<ProcedimentoSusVO>();
		union.addAll(lista1);
		union.addAll(lista2);
		
		return union;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcedimentoConvenioVO> buscarProcedimentosConveniosInternacao(Integer intSeq, Short pConvenioSus){
		StringBuilder sql = new StringBuilder(721);	
		sql.append(" SELECT ")
			.append(" int.").append(AinInternacao.Fields.SEQ).append(" as intSeq ")
			.append(", cta.").append(CntaConv.Fields.NRO).append(" as ctaNro ")
			.append(", iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ).append(" as iphPhoSeq ")
			.append(", iph.").append(FatItensProcedHospitalar.Fields.SEQ).append(" as iphSeq ")
			.append(", cta.").append(CntaConv.Fields.CSP_CNV_CODIGO).append(" as ctaCspCnvCodigo  ")
			.append(", cta.").append(CntaConv.Fields.CSP_SEQ).append(" as ctaCspSeq ")
			.append(", int.").append(AinInternacao.Fields.CSP_CNV_CODIGO).append(" as intCspCnvCodigo ")
			.append(", int.").append(AinInternacao.Fields.CSP_SEQ).append(" as intCspSeq ")
		.append(" FROM ")
			.append(AinInternacao.class.getSimpleName()).append(" int ")
			.append(", ").append(AghAtendimentos.class.getSimpleName()).append(" atd ")
			.append(", ").append(CntaConv.class.getSimpleName()).append(" cta ")
			.append(", ").append(FatItensProcedHospitalar.class.getSimpleName()).append(" iph ")
		.append(" WHERE ")
			.append(" int.").append(AinInternacao.Fields.SEQ).append(" = :intSeq ")
			.append(" and int.").append(AinInternacao.Fields.CSP_CNV_CODIGO).append(" <> :pConvenioSus")
			.append(" and atd.").append(AghAtendimentos.Fields.SEQ).append(" = cta.").append(CntaConv.Fields.ATENDIMENTO_SEQ)
			.append(" and int.").append(AinInternacao.Fields.SEQ).append(" = atd.").append(AghAtendimentos.Fields.INT_SEQ)
			.append(" and int.").append(AinInternacao.Fields.IPH_PHO_SEQ).append(" = iph.").append(FatItensProcedHospitalar.Fields.PHO_SEQ)
			.append(" and int.").append(AinInternacao.Fields.IPH_SEQ).append(" = iph.").append(FatItensProcedHospitalar.Fields.SEQ)
			.append(" and cta.").append(CntaConv.Fields.DATA_COMP).append(" is not null ")
		;
		final Query query = this.createHibernateQuery(sql.toString());
		query.setParameter("intSeq", intSeq);
		query.setParameter("pConvenioSus", pConvenioSus);
		query.setResultTransformer(Transformers.aliasToBean(ProcedimentoConvenioVO.class));
		
		return query.list();
	}
	
	public List<SigCalculoAtdProcedimentos> buscarProcedimentosPacienteInternacao(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdProcedimentos.class, "ccp");
		criteria.createAlias("ccp." + SigCalculoAtdProcedimentos.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("ccp." + SigCalculoAtdProcedimentos.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), "iph");
		criteria.createAlias("ccp." + SigCalculoAtdProcedimentos.Fields.CONTA_CONVENIO.toString(), "cta", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cta." + CntaConv.Fields.FAT_CONV_SAUDE_PLANOS.toString(), "ctaCsp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ctaCsp." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "ctaCspConv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ccp." + SigCalculoAtdProcedimentos.Fields.CONTA_HOSPITAL.toString(), "cth", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cth." + FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO.toString(), "cthCsp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cthCsp." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "cthCspConv", JoinType.LEFT_OUTER_JOIN);		
		
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		else if (pmuSeq == null && atdSeq != null){
			DetachedCriteria criteriaPmuSeq = DetachedCriteria.forClass(SigCalculoAtdPaciente.class,"cac2");
			criteriaPmuSeq.setProjection(Projections.max("cac2."+SigCalculoAtdPaciente.Fields.PMU_SEQ));
			criteriaPmuSeq.add(Restrictions.eq("cac2." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
			criteria.add(Subqueries.propertyEq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), criteriaPmuSeq));
		}
		
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		
		criteria.addOrder(Order.desc("ccp." + SigCalculoAtdProcedimentos.Fields.PRINCIPAL.toString()));
		criteria.addOrder(Order.desc("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.asc("iph." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	public boolean verificarPossuiFaturamentoPendente(Integer seqCalculoPaciente, Short pConvenioSus) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		
		DetachedCriteria subCriteriaSus = DetachedCriteria.forClass(FatContasInternacao.class,"coi");
		subCriteriaSus.createAlias("coi." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "cth");
		subCriteriaSus.setProjection(Projections.property("coi."+FatContasInternacao.Fields.SEQ));
		subCriteriaSus.add(Restrictions.eqProperty("coi." + FatContasInternacao.Fields.INT_SEQ.toString(), "cac."+SigCalculoAtdPaciente.Fields.INTERNACAO_SEQ));
		subCriteriaSus.add(Restrictions.eq("cth." + FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		subCriteriaSus.add(Restrictions.ne("cth." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.O));
		
		DetachedCriteria subCriteriaConvenio = DetachedCriteria.forClass(CntaConv.class,"cta");
		subCriteriaConvenio.setProjection(Projections.property("cta."+CntaConv.Fields.NRO));
		subCriteriaConvenio.add(Restrictions.eqProperty("cta." + CntaConv.Fields.ATENDIMENTO_SEQ.toString(), "cac."+SigCalculoAtdPaciente.Fields.ATD_SEQ));
		subCriteriaConvenio.add(Restrictions.ne("cta." + CntaConv.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		subCriteriaConvenio.add(Restrictions.isNull("cta." + CntaConv.Fields.DATA_COMP.toString()));
			
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SEQ.toString(), seqCalculoPaciente));
		criteria.add(Restrictions.or(Subqueries.exists(subCriteriaSus),Subqueries.exists(subCriteriaConvenio)));
	
		return executeCriteriaExists(criteria);
	}	
	
	
	public AacPagador obterPagador(Integer intSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "int");
		criteria.createAlias("int." + AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "csp");
		criteria.createAlias("csp." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "cnv");
		
		criteria.setProjection(Projections.property("cnv."+FatConvenioSaude.Fields.PAGADOR));
		
		criteria.add(Restrictions.eq("int." + AinInternacao.Fields.SEQ.toString(), intSeq));
	
		return (AacPagador) executeCriteriaUniqueResult(criteria);
	}
}