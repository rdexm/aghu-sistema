package br.gov.mec.aghu.faturamento.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.vo.BuscaContaVO;
import br.gov.mec.aghu.faturamento.vo.CursorClinicaCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorIntCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.faturamento.vo.CursorVerificacaoDtNascDtInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.FatContasIntPacCirurgiasVO;
import br.gov.mec.aghu.faturamento.vo.FatContasInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.PacienteContaHospitalarVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarContaInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;


public class FatContasInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatContasInternacao> {

	private static final long serialVersionUID = 5664157637654978736L;
	
	private static final Log LOG = LogFactory.getLog(FatContasInternacaoDAO.class);

	public List<FatContasInternacao> listarContasInternacao(Integer cthSeq) {
		return this.listarContasInternacao(cthSeq, null, null);
	}
	
	public List<FatContasInternacao> listarContasInternacao(Integer cthSeq, Integer firstResult, Integer maxResults) {
		DetachedCriteria criteria = buscaCriteriaListarContasInternacao(cthSeq);
		
		if (firstResult != null && maxResults != null) {
			return executeCriteria(criteria, firstResult, maxResults, null, false);
		} else {
			return executeCriteria(criteria);
		}
	}
	
	public FatContasInternacao buscarPrimeiraContaInternacao(Integer cthSeq) {
		
		DetachedCriteria criteria = buscaCriteriaListarContasInternacao(cthSeq);
		
		List<FatContasInternacao> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	public FatContasInternacao obterPrimeiraContaInternacao(Integer cthSeq) {
			
		DetachedCriteria criteria = obterCriteriaListarContasInternacao(cthSeq);
		
		List<FatContasInternacao> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
}
	
	public List<FatContasInternacao> buscarContaInternacao(Integer cthSeq) {
		return executeCriteria(buscaCriteriaListarContasInternacao(cthSeq));
	}
	
	/**
	 * Remove os FatContasInternacao relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCth(Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatContasInternacao.class.getName() + 
																	   " where " + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatContasInternacao relacionados a uma conta hospitalar e uma Intenação.
	 *  
	 * @param cthSeq conta hospitalar
	 * @param intSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCtheIntSeq(Integer cthSeq, Integer intSeq){
		javax.persistence.Query query = createQuery("delete " + FatContasInternacao.class.getName() + 
																	   " where " + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString() + " = :cthSeq " +
																	   " and" + FatContasInternacao.Fields.INT_SEQ.toString() + " = :intSeq ");
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("intSeq", intSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatContasInternacao relacionados a uma Intenação.
	 *  
	 * @param intSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorSeqInternacao(Integer intSeq, Integer seqContaHospitalar){
		javax.persistence.Query query = createQuery("delete " + FatContasInternacao.class.getName() + 
																	   " where " + FatContasInternacao.Fields.INT_SEQ.toString() + " = :intSeq " +
																	   "and " + FatContasInternacao.Fields.CTH_SEQ.toString() + " <> :seqContaHospitalar ");
		query.setParameter("intSeq", intSeq);
		query.setParameter("seqContaHospitalar", seqContaHospitalar);
		return query.executeUpdate();
	}

	private DetachedCriteria buscaCriteriaListarContasInternacao(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());		
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));
		return criteria;
	}
	
	public FatContasInternacao buscaContaInternacao(Integer seqContaInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
		criteria.createAlias(FatContasInternacao.Fields.INTERNACAO.toString(), "INT");		
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.SEQ.toString(), seqContaInternacao));
		FatContasInternacao contaInternacao = (FatContasInternacao) executeCriteriaUniqueResult(criteria);
		super.initialize(contaInternacao.getInternacao());
		return contaInternacao;
	}	
	
	private DetachedCriteria obterCriteriaListarContasInternacao(Integer cthSeq) {
			
			DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, "FCI");
			
			criteria.createAlias("FCI."+FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),"FCH");
			criteria.createAlias("FCI."+FatContasInternacao.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("FCI."+FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));
			return criteria;
		}
	
	/**
	 * ORADB: cursor: FATK_CTH2_RN_UN.C_TCI2
	 */
	public Integer buscarContaInternacaoCursorTci2(Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
		criteria.createAlias(FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString(),"dadosContaSemInt");
		criteria.createAlias("dadosContaSemInt."+FatDadosContaSemInt.Fields.TIPO_CARATER_INTERNACAO.toString(),"tipoCarterInternacao");
		
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));
		criteria.setProjection(Projections.property("tipoCarterInternacao."+AinTiposCaraterInternacao.Fields.CODIGO_SUS.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	protected DetachedCriteria obterCriteriaPorAtendimento(Integer intSeq) {
		DetachedCriteria result = DetachedCriteria.forClass(FatContasInternacao.class);
		result.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		return result;
	}

	protected DetachedCriteria obterCriteriaCthSeqPorAtendimento(Integer intSeq) {
		
		DetachedCriteria result = null;
		ProjectionList projList = null;
		
		result = this.obterCriteriaPorAtendimento(intSeq);
		projList = Projections.projectionList();
		projList.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString()));
		result.setProjection(projList);
		
		return result;
	}
	
	public FatContasInternacao obterFatContasInternacaoPorContasHospitalaresPorDCSSeq(final Integer dcsSeq, final Integer cthSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, "COI");

		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "CTH");
		criteria.add(Restrictions.eq("COI."+FatContasInternacao.Fields.DCS_SEQ.toString(), dcsSeq));
		criteria.add(Restrictions.eq("CTH."+FatContasHospitalares.Fields.SEQ.toString(), cthSeq));
		
		return (FatContasInternacao) executeCriteriaUniqueResult(criteria);
		
	}
	
	public List<Integer> obterListaCthSeqPorAtendimento(Integer intSeq) {
		
		List<Integer> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaCthSeqPorAtendimento(intSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	public List<FatContasHospitalares> listarContasHospitalares(Integer pIntSeq, Date pDataAnterior, String pTipoData){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
		
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), pIntSeq));
		
		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
		
		criteria.add(Restrictions.isNull(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.CTH.toString()));
		
		criteria.add(Restrictions.or(
						Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.A), 
						Restrictions.and(
							Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.F), 
							Restrictions.isNull(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.MOTIVO_SAIDA_PACIENTE.toString()))));
		
		if("I".equals(pTipoData)){
			Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString(), pDataAnterior);
		} else if("A".equals(pTipoData) && pDataAnterior != null){
			Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString(), pDataAnterior);
		}
				
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FatContasHospitalares.class, "SUB");
		subCriteria.add(Restrictions.eqProperty("SUB."
				+ FatContasHospitalares.Fields.CTH_SEQ.toString(),
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()
						+ "." + FatContasHospitalares.Fields.CTH_SEQ));	
		subCriteria.setProjection(Projections.property(FatContasHospitalares.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.notExists(subCriteria));
		
		criteria.setProjection(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()));
		
		return executeCriteria(criteria);
	}

	public FatContasHospitalares buscarPrimeiroContasHospitalares(Integer pIntSeq, DominioSituacaoConta[] situacoesContaHospitalar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);

		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
		criteria.createAlias(FatContasInternacao.Fields.INTERNACAO.toString(), FatContasInternacao.Fields.INTERNACAO.toString());
		criteria.createAlias(FatContasInternacao.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.ATENDIMENTO.toString(), "atd");

		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), pIntSeq));
		criteria.add(Restrictions.in(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString(), situacoesContaHospitalar));
		criteria.add(Restrictions.eqProperty(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA, FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA));
		criteria.addOrder(Order.asc( FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA));
		criteria.setProjection(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()));

		List<FatContasHospitalares> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	protected DetachedCriteria obterCriteriaPorCthSeqPhiSeq(Integer cthSeq, Integer phiSeq) {
		
		DetachedCriteria result = null;
		DetachedCriteria subCriteria = null;
		String ichStr = null;
		
		result = this.buscaCriteriaListarContasInternacao(cthSeq);
		ichStr = "SUB_ICH";
		subCriteria = DetachedCriteria.forClass(FatItemContaHospitalar.class, ichStr);
		subCriteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.CTH_SEQ.toString(), cthSeq));
		subCriteria.add(Restrictions.eq(FatItemContaHospitalar.Fields.PHI_SEQ.toString(), phiSeq));
		subCriteria.setProjection(Projections.property(FatItemContaHospitalar.Fields.SEQ.toString()));
		result.add(Subqueries.exists(subCriteria));
		
		return result;
	}
	
	public List<FatContasInternacao> listarPorCthSeqPhiSeq(Integer cthSeq, Integer phiSeq) {
		List<FatContasInternacao> result = null;
		DetachedCriteria criteria = this.obterCriteriaPorCthSeqPhiSeq(cthSeq, phiSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	/**
	 * Método para buscar a conta de internação / contas hospitalar através do
	 * SEQ da internação que esteja com situação A, E ou F.
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public FatContasInternacao obterContaHospitalarePorInternacao(Integer seqInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);

		List<DominioSituacaoConta> situacoesContaHospitalar = new ArrayList<DominioSituacaoConta>();
		situacoesContaHospitalar.add(DominioSituacaoConta.A);
		situacoesContaHospitalar.add(DominioSituacaoConta.F);
		situacoesContaHospitalar.add(DominioSituacaoConta.E);

		criteria.createAlias(
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),
				"cth");

		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.in("cth." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacoesContaHospitalar));
		criteria.addOrder(Order.desc("cth." + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()));

		List<FatContasInternacao> contasInternacao = executeCriteria(criteria, 0, 1, null, true);
		
		if (contasInternacao == null || contasInternacao.isEmpty()) {
			return null;
		} else {
			return contasInternacao.get(0);
		}
	}
	
	public List<FatContasInternacao> obterContaHospitalarePorInternacaoSemFiltro(Integer seqInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, "FCI");
		criteria.createAlias("FCI."+FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), "FCH", JoinType.INNER_JOIN);
		criteria.createAlias("FCI."+FatContasInternacao.Fields.INTERNACAO.toString(), "INT2", JoinType.INNER_JOIN);
		
		criteria.setFetchMode("FCI."+FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),FetchMode.JOIN);
		criteria.setFetchMode("FCI."+FatContasInternacao.Fields.INTERNACAO.toString(),FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), seqInternacao));
		return  executeCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<FatContasInternacao> obterContasHospitalaresPorInternacaoPacienteDataSolicitacao(Integer intSeq, Integer pacCodigo, Date dthrSolicitacao, Short cspCnvCodigo) {
		StringBuilder hql = new StringBuilder(400);
		
		hql.append(" select coi");
		hql.append(" from ");
		hql.append(FatContasInternacao.class.getSimpleName()).append(" coi ");
		hql.append(" join coi.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append(" cth ");
		hql.append(" left join coi.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append(" int ");
		hql.append(" left join int.").append(AinInternacao.Fields.ATENDIMENTO.toString()).append(" atd ");
		hql.append(" where atd.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo ");
		hql.append(" and atd.").append(AghAtendimentos.Fields.ORIGEM.toString()).append(" = :origemAtendimento ");
		hql.append(" and cth.").append(FatContasHospitalares.Fields.IND_SITUACAO.toString()).append(" <> :indSituacao ");
		hql.append(" and :dthrSolicitacao between");
			hql.append(" cth.").append(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString());
			hql.append(" and coalesce(cth.").append(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()).append(", :dataAtual ) ");	
		hql.append(" and ( (coi.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append('.').append(AinInternacao.Fields.CSP_CNV_CODIGO.toString()).append(" = :cspCnvCodigo ");
		hql.append("      and ( (coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" = :indSituacao and coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" is not null ) ) ");
		hql.append("      or ( (coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" <> :indSituacao or coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" is null ) ) ) ) ");
		
		Query query = createQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("dataAtual", new Date());
		query.setParameter("dthrSolicitacao", dthrSolicitacao);
		query.setParameter("cspCnvCodigo", cspCnvCodigo);
		query.setParameter("indSituacao", DominioSituacaoConta.C);
		query.setParameter("origemAtendimento", DominioOrigemAtendimento.I);
		
		return (List<FatContasInternacao>)query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<FatContasInternacao> obterContasHospitalaresPorInternacaoPacienteDataSolicitacaoComExamesEspeciais(Integer intSeq, Integer pacCodigo, Date dthrSolicitacao, Short cspCnvCodigo) {
		StringBuilder hql = new StringBuilder(300);
		
		hql.append(" select coi")
		.append(" from ")
		.append(FatContasInternacao.class.getSimpleName()).append(" coi ")
		.append(" where coi.").append(FatContasInternacao.Fields.INT_SEQ.toString()).append(" = :intSeq ")
		.append(" and coi.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append('.' ).append( AinInternacao.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo ")
		.append(" and coi.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append('.' ).append( FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()).append(" <= :dthrSolicitacao ")
		.append(" and ( coi.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append('.' ).append( FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()).append(" < :dthrSolicitacao and coi.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append('.' ).append( FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()).append(" is not null) ")
		.append(" and ( (coi.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append('.' ).append( AinInternacao.Fields.CSP_CNV_CODIGO.toString()).append(" = :cspCnvCodigo ")
		.append("      and ( (coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" = :indSituacao and coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" is not null ) ) ")
		.append("      or ( (coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" <> :indSituacao or coi.").append(FatContasInternacao.Fields.CONTA_HOSPITALAR_SITUACAO.toString()).append(" is null ) ) ) ) ");
		
		Query query = createQuery(hql.toString());
		query.setParameter("intSeq", intSeq);
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("dthrSolicitacao", dthrSolicitacao);
		query.setParameter("cspCnvCodigo", cspCnvCodigo);
		query.setParameter("indSituacao", DominioSituacaoConta.C);
		
		return (List<FatContasInternacao>)query.getResultList();
	}

	public Integer obterMaxSeqInternacao(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);

		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());

		criteria.setProjection(Projections.max(FatContasInternacao.Fields.INT_SEQ.toString()));

		criteria.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));

		return (Integer) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * TODO
	 * Parte do cursor <code>FATF_ARQ_TXT_INT.BUSCA_CONTA.C_CTH_PER</code>
	 * @param cthAlias TODO coiAlias TODO
	 */
	protected DetachedCriteria obterCriteriaParaCsvCotnasPeridoCth(String cthAlias, String coiAlias, Short cnvCodigo, Byte cspSeq, DominioSituacaoConta... indSituacao) {
		
		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatContasInternacao.class, coiAlias);
		result.createAlias(coiAlias + "." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), cthAlias);
		result.add(Restrictions.in(cthAlias + "." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), indSituacao));
		result.add(Restrictions.eq(cthAlias + "." + FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString(), cnvCodigo));
		result.add(Restrictions.eq(cthAlias + "." + FatContasHospitalares.Fields.CSP_SEQ.toString(), cspSeq));
		
		return result;
	}
	
	protected void adicionarProjectionsCsvCth(ProjectionList projLst, String cth) {
	
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.SEQ.toString()), "cthSeq"); // diferente	
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.IND_SITUACAO.toString()), "indSituacao");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.NRO_AIH.toString()), "nroAih");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.MDS_SEQ.toString()), "mdsSeq"); //diferente
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()), "dataInternacaoAdministrativa");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()), "dtAltaAdministrativa");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.DT_ENVIO_SMS.toString()), "dtEnvioSms");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.IND_AUTORIZADO_SMS.toString()), "indAutorizadoSms");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.PHI_SEQ.toString()), "phiSeq"); // diferente
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString()), "cspCnvCodigo"); //diferente
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.CSP_SEQ.toString()), "cspSeq"); // diferente
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.PHI_SEQ_REALIZADO.toString()), "phiRealizSeq"); // diferente
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.IND_ENVIADO_SMS.toString()), "indEnviadoSms");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.IND_CODIGO_EXCLUSAO_CRITICA.toString()), "codigo");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_SH.toString()), "valorSh");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_UTI.toString()), "valorUti");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_UTIE.toString()), "valorUtie");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_SP.toString()), "valorSp");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_ACOMP.toString()), "valorAcomp");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_RN.toString()), "valorRn");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_SADT.toString()), "valorSadt");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_HEMAT.toString()), "valorHemat");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_TRANSP.toString()), "valorTransp");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_OPM.toString()), "valorOpm");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_ANESTESISTA.toString()), "valorAnestesista");
		projLst.add(Projections.property(cth + "." + FatContasHospitalares.Fields.VALOR_PROCEDIMENTO.toString()), "valorProcedimento");
	}
	
	protected void adicionarProjectionsCsvPac(ProjectionList projLst, String pac) {
	
		projLst.add(Projections.property(pac + "." + AipPacientes.Fields.NOME.toString()), "nome");
		projLst.add(Projections.property(pac + "." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
	}
	
	protected void adicionarProjectionsCsvUnf(ProjectionList projLst, String unf) {	
		projLst.add(Projections.property(unf + "." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()), "descricao");
	}

	protected void adicionarProjectionsCsvInt(ProjectionList projLst, String inte) {	
		projLst.add(Projections.property(inte + "." + AinInternacao.Fields.LEITO_ID.toString()), "leitoID");
		projLst.add(Projections.property(inte + "." + AinInternacao.Fields.TCI_SEQ.toString()), "tciSeq");
	}

	protected DetachedCriteria obterCriteriaParaCsvContasPeriodoViaInt(
			String coi,
			String cth,
			String inte,
			String pac,
			String unf,
			Byte cspSeq,
			Short cnvCodigo,
			DominioSituacaoConta... indSituacao) {
	
		DetachedCriteria result = null;
		
		// query
		result = this.obterCriteriaParaCsvCotnasPeridoCth(cth, coi, cnvCodigo, cspSeq, indSituacao);
		// alias
		result.createAlias(coi + "." + FatContasInternacao.Fields.INTERNACAO.toString(), inte);
		result.createAlias(inte + "." + AinInternacao.Fields.PACIENTE.toString(), pac);
		result.createAlias(inte + "." + AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(), unf, JoinType.LEFT_OUTER_JOIN);
		// restricao
		result.add(Restrictions.isNotNull(coi + "." + FatContasInternacao.Fields.INTERNACAO.toString()));
		result.add(Restrictions.isNotNull(inte + "." + AinInternacao.Fields.PACIENTE.toString()));
		//result.add(Restrictions.isNotNull(FatContasInternacao.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.UNIDADE_FUNCIONAL.toString()));
		
		return result;
	}

	/**
	 * TODO
	 * Parte do cursor <code>FATF_ARQ_TXT_INT.BUSCA_CONTA.C_CTH_PER</code>
	 */
	protected DetachedCriteria obterCriteriaParaCsvContasPeriodoViaIntParaVO(Short cnvCodigo, Byte cspSeq, DominioSituacaoConta... indSituacao) {
		
		DetachedCriteria result = null;
		ProjectionList projLst = null;
		String coi = null;
		String cth = null;
		String inte = null;
		String pac = null;
		String unf = null;

		// alias
		coi = "COI";
		cth = "CTH";
		inte = "INT";
		pac = "PAC";
		unf = "UNF";
		result = this.obterCriteriaParaCsvContasPeriodoViaInt(coi, cth, inte, pac, unf, cspSeq, cnvCodigo, indSituacao);
		// proj
		projLst = Projections.projectionList();
		this.adicionarProjectionsCsvCth(projLst, cth);
		this.adicionarProjectionsCsvPac(projLst, pac);
		this.adicionarProjectionsCsvUnf(projLst, unf);
		this.adicionarProjectionsCsvInt(projLst, inte);
		projLst.add(Projections.sqlProjection("'R' as realizadoSolicitado", new String[]{"realizadoSolicitado"}, new Type[]{StringType.INSTANCE}), "realizadoSolicitado");
		result.setProjection(Projections.distinct(projLst));
		result.setResultTransformer(Transformers.aliasToBean(BuscaContaVO.class));
		
		return result;
	}

	public List<BuscaContaVO> listarParaCsvContasPeriodoViaInt(Short cnvCodigo, Byte cspSeq, DominioSituacaoConta... indSituacao) {
		
		List<BuscaContaVO> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaParaCsvContasPeriodoViaIntParaVO(cnvCodigo, cspSeq, indSituacao);
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	protected DetachedCriteria obterCriteriaParaCsvContasPeriodoViaDcs(
			String coi,
			String cth,
			String dcs,
			String pac,
			String unf,
			Byte cspSeq,
			Short cnvCodigo,
			DominioSituacaoConta... indSituacao) {

		DetachedCriteria result = null;
		
		// query
		result = this.obterCriteriaParaCsvCotnasPeridoCth(cth, coi, cnvCodigo, cspSeq, indSituacao);
		// alias
		result.createAlias(coi + "." + FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString(), dcs);
		result.createAlias(dcs + "." + FatDadosContaSemInt.Fields.PACIENTE.toString(), pac);
		result.createAlias(dcs + "." + FatDadosContaSemInt.Fields.UNF.toString(), unf, JoinType.LEFT_OUTER_JOIN);
		// alias
		result.add(Restrictions.isNotNull(coi + "." + FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString()));
		result.add(Restrictions.isNotNull(dcs + "." + FatDadosContaSemInt.Fields.PACIENTE.toString()));
		return result;
	}

	/**
	 * TODO
	 * Parte do cursor <code>FATF_ARQ_TXT_INT.BUSCA_CONTA.C_CTH_PER</code>
	 */
	protected DetachedCriteria obterCriteriaParaCsvContasPeriodoViaDcsParaVO(Short cnvCodigo, Byte cspSeq, DominioSituacaoConta... indSituacao) {
		
		DetachedCriteria result = null;
		ProjectionList projLst = null;
		String coi = null;
		String cth = null;
		String dcs = null;
		String pac = null;
		String unf = null;
	
		// alias
		dcs = "DCS";
		pac = "PAC";
		unf = "UNF";
		cth = "CTH";
		coi = "COI";
		result = this.obterCriteriaParaCsvContasPeriodoViaDcs(coi, cth, dcs, pac, unf, cspSeq, cnvCodigo, indSituacao);
		// proj
		projLst = Projections.projectionList();
		this.adicionarProjectionsCsvCth(projLst, cth);
		this.adicionarProjectionsCsvPac(projLst, pac);
		this.adicionarProjectionsCsvUnf(projLst, unf);
		projLst.add(Projections.sqlProjection("'S' as realizadoSolicitado", new String[]{"realizadoSolicitado"}, new Type[]{StringType.INSTANCE}), "realizadoSolicitado");
		result.setProjection(Projections.distinct(projLst));
		result.setResultTransformer(Transformers.aliasToBean(BuscaContaVO.class));
		return result;
	}

	public List<BuscaContaVO> listarParaCsvContasPeriodoViaDcs(Short cnvCodigo, Byte cspSeq, DominioSituacaoConta... indSituacao) {
		List<BuscaContaVO> result = null;
		DetachedCriteria criteria = null;
		criteria = this.obterCriteriaParaCsvContasPeriodoViaDcsParaVO(cnvCodigo, cspSeq, indSituacao);
		result = this.executeCriteria(criteria);
		return result;
	}

	/**
	 * Método para buscar contas de internação através do seqInternacao.
	 * @param seqInternacao
	 */
	public List<FatContasInternacao> pesquisarContaInternacaoPorInternacao(Integer seqInternacao) {
		if (seqInternacao == null) {
			return null;
		} else {
			DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
			criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), seqInternacao));
			return executeCriteria(criteria);
		}
	}

	/**
	 * Método para buscar registros de FatContasInternacao através do
	 * seqInternacao e que não tenham contaManuseada
	 * 
	 * @param seqInternacao
	 * @return
	 */
	public List<FatContasInternacao> obterContaInternacaoNaoManuseada(Integer seqInternacao) {
		if (seqInternacao == null) {
			return null;
		} else {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(FatContasInternacao.class);

			criteria.createAlias(
					FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES
							.toString(),
					FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES
							.toString());
			
			criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ
					.toString(), seqInternacao));
			criteria.add(Restrictions.eq(
					FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES
							.toString()
							+ "."
							+ FatContasHospitalares.Fields.CONTA_MANUSEADA
									.toString(), false));
			criteria
					.add(Restrictions
							.isNotNull(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES
									.toString()));
			return executeCriteria(criteria);
		}
	}
	
	public FatContasInternacao obterContaInternacaoEmFatDadosContaSemInt(final Integer dcsSeq, final Integer cthSeq) {
		if (dcsSeq == null) {
			return null;
		} else {
			final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
			criteria.createAlias(FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString(), FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString());
			criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
			criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "." + FatContasHospitalares.Fields.ESPECIALIDADE.toString(), FatContasHospitalares.Fields.ESPECIALIDADE.toString());
			criteria.add(Restrictions.eq(FatContasInternacao.Fields.DCS_SEQ.toString(), dcsSeq));
			criteria.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));
			return (FatContasInternacao) executeCriteriaUniqueResult(criteria);
		}
	}
	
	/**
	 * Método para chamar procedure do módulo de faturamento.
	 * 
	 * ORADB Procedure FATK_CTH4_RN_UN.RN_CTHP_BUSCA_SANGUE
	 * 
	 * @param seqContaInternacao
	 */
	public void buscarSangue(final Integer seqContaInternacao, String usuarioLogado) throws ApplicationBusinessException {
		if (seqContaInternacao != null) {
			if (!isOracle()) {
				return;
			}
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.FATK_CTH4_RN_UN_RN_CTHP_BUSCA_SANGUE;
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(final Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall("{call " + nomeObjeto + "(?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, seqContaInternacao);
						cs.execute();
					} finally {
						if(cs != null){
							cs.close();
						}
					}
				}
			};
			try {
				this.doWork(work);
			} catch (final Exception e) {
				final String valores = CoreUtil.configurarValoresParametrosCallableStatement(seqContaInternacao);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
			if (work.getException() != null){
				final String valores = CoreUtil.configurarValoresParametrosCallableStatement(seqContaInternacao);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<FatContasInternacaoVO> listaFatContasInternacaoVOPorCthSeq(Integer cthSeq) {
		StringBuffer hql = new StringBuffer(180);
		hql.append(" select distinct new br.gov.mec.aghu.faturamento.vo.FatContasInternacaoVO (int, dcsi) ");
		hql.append(" from ").append(FatContasInternacao.class.getSimpleName()).append(" fci ");
		hql.append(" join fci.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append(" cth ");
		hql.append(" left join fci.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append(" int ");
		hql.append(" left join fci.").append(FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString()).append(" dcsi ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :cthSeq ");
		Query query = createQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CursorClinicaCadastroSugestaoDesdobramentoVO> cursorClinicaCadastroSugestaoDesdobramento(Integer pCthSeq,
			Integer firstResult, Integer maxResults) {
		StringBuffer hql = new StringBuffer(240);
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorClinicaCadastroSugestaoDesdobramentoVO(");
		hql.append(" 	max(");
		hql.append(" 		int.").append(AinInternacao.Fields.SEQ.toString());
		hql.append(" 	), ");
		hql.append(" 	clc.").append(AghClinicas.Fields.CODIGO.toString());
		hql.append(" )");
		hql.append(" from ").append(FatContasInternacao.class.getSimpleName()).append(" cin ");
		hql.append(" join cin.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append(" cth ");
		hql.append(" join cin.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append(" int ");
		hql.append(" join int.").append(AinInternacao.Fields.ESPECIALIDADE.toString()).append(" esp ");
		hql.append(" join esp.").append(AghEspecialidades.Fields.CLINICA.toString()).append(" clc ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :pCthSeq ");
		hql.append(" group by clc.").append(AghClinicas.Fields.CODIGO.toString());
		hql.append(" order by 1 desc ");
		Query query = createQuery(hql.toString());
		query.setParameter("pCthSeq", pCthSeq);
		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}
		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<CursorIntCadastroSugestaoDesdobramentoVO> listarCursorIntCadastroSugestaoDesdobramento(Integer cthSeq, Integer firstResult, Integer maxResults) {
		StringBuffer hql = new StringBuffer(270);
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorIntCadastroSugestaoDesdobramentoVO(");
		hql.append(" 	max(");
		hql.append(" 		int.").append(AinInternacao.Fields.DT_INTERNACAO.toString());
		hql.append(" 	) ");
		hql.append(" 	, qrt.").append(AinQuartos.Fields.NUMERO.toString());
		hql.append(" 	, lto.").append(AinLeitos.Fields.LTO_ID.toString());
		hql.append(" )");
		hql.append(" from ").append(FatContasInternacao.class.getSimpleName()).append(" cin ");
		hql.append(" 	join cin.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append(" cth ");
		hql.append(" 	join cin.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append(" int ");
		hql.append(" 	left join int.").append(AinInternacao.Fields.LEITO.toString()).append(" lto ");
		hql.append(" 	left join int.").append(AinInternacao.Fields.QUARTO.toString()).append(" qrt ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :cthSeq ");
		hql.append(" group by ");
		hql.append("	lto.").append(AinLeitos.Fields.LTO_ID.toString());
		hql.append(" 	, qrt.").append(AinQuartos.Fields.NUMERO.toString());
		hql.append(" order by 1 desc ");
		Query query = createQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		if (firstResult != null) {
			query.setFirstResult(firstResult);
		}
		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<PacienteContaHospitalarVO> listarPacienteContaHospitalarVOComInternacao(Integer cthSeq) {
		StringBuffer hql = new StringBuffer(200);
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.PacienteContaHospitalarVO(");
		hql.append(" 	pac.").append(AipPacientes.Fields.CODIGO);
		hql.append(" 	, pac.").append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" 	, int.").append(AinInternacao.Fields.SEQ.toString());
		hql.append(" )");
		hql.append(" from ").append(FatContasInternacao.class.getSimpleName()).append(" coi ");
		hql.append(" 	join coi.").append(FatContasInternacao.Fields.INTERNACAO.toString()).append(" int ");
		hql.append(" 	join coi.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append(" cth ");
		hql.append(" 	join int.").append(AinInternacao.Fields.PACIENTE.toString()).append(" pac ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :cthSeq ");
		hql.append(" order by int.").append(AinInternacao.Fields.DT_INTERNACAO.toString()).append(" desc ");
		Query query = createQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<PacienteContaHospitalarVO> listarPacienteContaHospitalarVO(Integer cthSeq) {
		StringBuffer hql = new StringBuffer(170);
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.PacienteContaHospitalarVO(");
		hql.append(" 	pac.").append(AipPacientes.Fields.CODIGO);
		hql.append(" 	, pac.").append(AipPacientes.Fields.PRONTUARIO.toString());
		hql.append(" )");
		hql.append(" from ").append(FatContasInternacao.class.getSimpleName()).append(" coi ");
		hql.append(" 	join coi.").append(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()).append(" cth ");
		hql.append(" 	join coi.").append(FatContasInternacao.Fields.DADOS_CONTA_SEM_INT.toString()).append(" dcs ");
		hql.append(" 	join dcs.").append(FatDadosContaSemInt.Fields.PACIENTE.toString()).append(" pac ");
		hql.append(" where cth.").append(FatContasHospitalares.Fields.SEQ.toString()).append(" = :cthSeq ");
		Query query = createQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		return query.getResultList();
	}

	public List<FatContasInternacao> pesquisarContasInternacaoParaUpdate(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		criteria.setLockMode(LockMode.PESSIMISTIC_WRITE);
		return executeCriteria(criteria);
	}

	/**
	 * Lista as contas de uma internação
	 * @param intSeq
	 * @return
	 */
	public List<FatContasInternacao> pesquisarContasInternacaoOrderDtInternacaoDesc(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);
		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
				+ FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CSP." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CON", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		criteria.addOrder(Order.desc(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
				+ FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * ORADB VIEW V_FAT_CONTA_HOSP_INTERNACAO Este método implementa a view
	 * acima e retorna sua criteria
	 * @return cri
	 */
	public DetachedCriteria obterCriteriaViewContaHospitalizacaoInternacao() {
		DetachedCriteria cri = DetachedCriteria.forClass(FatContasInternacao.class);
		cri.setProjection(Projections
				.projectionList()
				.add(Projections.property(FatContasInternacao.Fields.INT_SEQ.toString()), "intSeq")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString()), "espSeq")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.ACM_SEQ.toString()), "acmSeq")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString()), "cspCnvCodigo")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.CSP_SEQ.toString()), "cspSeq")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()), "dtAltaAdministrativa")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()), "dtIntAdministrativa")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.DT_EMIS_CONT.toString()), "dtEmisConta")
				.add(Projections.property(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
						+ FatContasHospitalares.Fields.PHI_SEQ.toString()), "phiSeq"));
		cri.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), JoinType.INNER_JOIN);
		return cri;
	}
	
	public Long acomodacaoInternacaoConvenioCount(AinInternacao internacao) {
		DetachedCriteria cri = obterCriteriaViewContaHospitalizacaoInternacao();
		cri.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), internacao.getSeq()));
		cri.add(Restrictions.isNull(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
				+ FatContasHospitalares.Fields.ACOMODACAO.toString()));
		cri.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + "."
				+ FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.A));
		return executeCriteriaCount(cri);
	}
	
	@SuppressWarnings("unchecked")
	public List<AtualizarContaInternacaoVO> listarAtualizarContaInternacaoVO(Integer seqInternacao,
			DominioSituacaoConta indSituacaoContaHospitalar) {
		StringBuffer hql = new StringBuffer(460);
		hql.append(" select new br.gov.mec.aghu.internacao.business.vo.AtualizarContaInternacaoVO (");
		hql.append(" 	cth.seq, ");
		hql.append(" 	cth.dataInternacaoAdministrativa, ");
		hql.append(" 	cth.indSituacao, ");
		hql.append(" 	atd.seq, ");
		hql.append(" 	cnv.grupoConvenio ");
		hql.append(" ) ");
		hql.append(" from FatContasInternacao cti ");
		hql.append(" 	join cti.contaHospitalar as cth ");
		hql.append(" 	join cth.convenioSaude as cnv ");
		hql.append(" 	join cti.internacao.atendimento as atd ");
		hql.append(" where cti.internacao.seq = :seqInternacao ");
		hql.append(" 	and cth.indSituacao != :indSituacaoContaHospitalar ");
		hql.append(" order by cth.dataInternacaoAdministrativa ");
		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqInternacao", seqInternacao);
		query.setParameter("indSituacaoContaHospitalar", indSituacaoContaHospitalar);
		return query.list();
	}

	public FatContasInternacao obtePrimeiraContaHospitalar(Integer seqInternacao, Short cspCnvCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);

		String separador = ".";
		// Alias
		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),
				FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString());
		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + separador
				+ FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO, FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString()
				+ separador + FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO);
		// Restricoes
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.INT_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.eq(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + separador
				+ FatContasHospitalares.Fields.CONVENIO_SAUDE_PLANO + separador + FatConvenioSaudePlano.Fields.CODIGO.toString(),
				cspCnvCodigo));
		criteria.add(Restrictions.ne(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + separador
				+ FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.C));
		criteria.add(Restrictions.isNotNull(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString() + separador
				+ FatContasHospitalares.Fields.ACM_SEQ));

		List<FatContasInternacao> contasInternacao = this.executeCriteria(criteria);
		if (contasInternacao != null && !contasInternacao.isEmpty()) {
			return contasInternacao.get(0);
		}
		return null;
	}
	
	public Integer obterMaxSeqConta(Date pcDthr, Integer atdSeq, Short cnvCodigo, Byte cnvSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class);

		criteria.createAlias(FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(),
				"CTH");
		criteria.createAlias(FatContasInternacao.Fields.INTERNACAO.toString(), "INT");
		criteria.createAlias("INT.".concat(AinInternacao.Fields.ATENDIMENTO.toString()), 
				"ATD");
		criteria.setProjection(Projections.max(FatContasInternacao.Fields.SEQ.toString()));
		criteria.add(Restrictions.in("CTH.".concat(FatContasHospitalares.Fields.IND_SITUACAO.toString()), 
				new DominioSituacaoConta[]{DominioSituacaoConta.A, DominioSituacaoConta.F}));
		criteria.add(Restrictions.eq("ATD.".concat(AghAtendimentos.Fields.SEQ.toString()), atdSeq));
		criteria.add(Restrictions.eq("CTH.".concat(FatContasHospitalares.Fields.CSP_CNV_CODIGO.toString()), cnvCodigo));
		criteria.add(Restrictions.eq("CTH.".concat(FatContasHospitalares.Fields.CSP_SEQ.toString()), cnvSeq));
		Object[] values = { DateUtil.obterDataComHoraFinal(pcDthr), DateUtil.obterDataComHoraInical(null)}; 
		Type[] types = { TimestampType.INSTANCE, TimestampType.INSTANCE};
		criteria.add(Restrictions.sqlRestriction(" ? BETWEEN cth1_."
				.concat(FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.name()).concat(" and  COALESCE(" +
						"cth1_.").concat(FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.name()).concat(", ?)"), 
						values, types));
		return (Integer) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Pesquisa contas de internação com data desdobrada
	 * @param pPacCodigo, pDthrRealizado
	 * @return
	 */
	public List<Integer> pesquisarContasInternacaoDesdobradas(final Integer pPacCodigo, Date pDthrRealizado, DominioSituacaoConta ... pIndSituacao) {

		if (pPacCodigo == null || pDthrRealizado == null || pIndSituacao == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado");
		}
		
		final String ponto = ".";
		final String aliasINT = "int";
		final String aliasCTH = "cth";
		final String aliasHibernateCTH = "cth2_";
		final String aliasCOI = "coi";
        
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, aliasINT);
		criteria.createAlias(aliasINT + ponto + FatContasInternacao.Fields.INTERNACAO.toString(), aliasCOI);
		criteria.createAlias(aliasINT + ponto + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES.toString(), aliasCTH);
		criteria.add(Restrictions.eq(aliasCOI + ponto + AinInternacao.Fields.PAC_CODIGO.toString(), pPacCodigo));
		criteria.add(Restrictions.in(aliasCTH + ponto + FatContasHospitalares.Fields.IND_SITUACAO.toString(), pIndSituacao));
		criteria.add(Restrictions.sqlRestriction(" ? >= " + aliasHibernateCTH + ponto + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.name(), 
				pDthrRealizado, TimestampType.INSTANCE));
		final String sql = "( ? <= COALESCE("+ aliasHibernateCTH + ponto + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.name() + ", ? ))";
		final Object[] values = {pDthrRealizado, pDthrRealizado};
		final Type[] types = {TimestampType.INSTANCE, TimestampType.INSTANCE};
		criteria.add(Restrictions.sqlRestriction(sql, values, types));
		criteria.setProjection(Projections.property(aliasCTH + ponto + FatContasHospitalares.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}

	public List<FatContasIntPacCirurgiasVO> obterFatContasIntPacCirurgiasVO(final Integer cthSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, "COI");

		criteria.createAlias("COI."+FatContasInternacao.Fields.INTERNACAO.toString(), "INT");
		criteria.createAlias("INT."+AinInternacao.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.CIRURGIAS.toString(), "CRG");

		criteria.add(Restrictions.eq("COI."+FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));
		
		criteria.setProjection(Projections.projectionList()
								.add(Projections.property("INT."+AinInternacao.Fields.PAC_CODIGO.toString()), FatContasIntPacCirurgiasVO.Fields.PAC_CODIGO.toString())
								.add(Projections.property("CRG."+MbcCirurgias.Fields.SEQ.toString()), FatContasIntPacCirurgiasVO.Fields.SEQ_CIRURGIA.toString())
							  );
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatContasIntPacCirurgiasVO.class));
		return executeCriteria(criteria);
	}
	
	public CursorVerificacaoDtNascDtInternacaoVO obterCursorVerificacaoDtNascDtInternacao(final Integer cthSeq){

		final SQLQuery sqlQuery = createSQLQuery(new CursorVerificacaoDtNascDtInternacaoQueryBuilder().builder());
		sqlQuery.setParameter(CursorVerificacaoDtNascDtInternacaoQueryBuilder.Parameter.P_CTH_SEQ.name(), cthSeq);
		sqlQuery.addScalar(CursorVerificacaoDtNascDtInternacaoVO.Fields.DATA_NASCIMENTO.toString(), TimestampType.INSTANCE)
			.addScalar(CursorVerificacaoDtNascDtInternacaoVO.Fields.DATA_INTERNACAO.toString(), TimestampType.INSTANCE);	
		sqlQuery.setResultTransformer(Transformers.aliasToBean(CursorVerificacaoDtNascDtInternacaoVO.class));
		List<CursorVerificacaoDtNascDtInternacaoVO> resultados = sqlQuery.list();
		return (resultados != null && !resultados.isEmpty()) ? resultados.get(0) : null;
}
}
