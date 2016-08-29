package br.gov.mec.aghu.faturamento.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemSugestoesDesdobramento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.vo.SugestoesDesdobramentoVO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobrId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class FatContaSugestaoDesdobrDAO extends BaseDao<FatContaSugestaoDesdobr> {

	private static final long serialVersionUID = -5506400967768889252L;

	/**
	 * Remove os FatContaSugestaoDesdobr relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCth(Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatContaSugestaoDesdobr.class.getName() + 
																	   " where " + FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}

	public List<FatContaSugestaoDesdobr> pesquisarSugestoesDesdobramento(final Date dataHoraSugestao, final Integer mdsSeq, final String descricao, final Integer cthSeq,
			 final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		
		final DetachedCriteria criteria = createCriteriaPesquisarSugestaoDesdobramentoCTH(dataHoraSugestao, mdsSeq, descricao, cthSeq);
		
		criteria.addOrder(Order.asc(FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString()));
		criteria.addOrder(Order.asc(FatContaSugestaoDesdobr.Fields.MDS_SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarSugestoesDesdobramentoCount(final Date dataHoraSugestao, final Integer mdsSeq, final String descricao, final Integer cthSeq) {
		final DetachedCriteria criteria = this.createCriteriaPesquisarSugestaoDesdobramentoCTH(dataHoraSugestao, mdsSeq, descricao, cthSeq);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria createCriteriaPesquisarSugestaoDesdobramentoCTH(
			final Date dataHoraSugestao, final Integer mdsSeq,
			final String descricao, final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContaSugestaoDesdobr.class);

		criteria.createAlias(FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString(), FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString());
		criteria.createAlias(FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO.toString(), FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO.toString());
		
		if(cthSeq != null){
			criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString(), cthSeq));
		}
		
		if (dataHoraSugestao != null) {
			Calendar calInicio = Calendar.getInstance();
			calInicio.setTime(dataHoraSugestao);
			calInicio.set(Calendar.SECOND, 0);
			calInicio.set(Calendar.MILLISECOND, 0);

			Calendar calFim = Calendar.getInstance();
			calFim.setTime(dataHoraSugestao);
			calFim.set(Calendar.SECOND, 59);
			calFim.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.between(FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString(), calInicio.getTime(),
					calFim.getTime()));
		}
		
		if (mdsSeq != null) {
			criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.MDS_SEQ.toString(), mdsSeq.byteValue()));
		}
		
		if (descricao != null && StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO.toString() + "."+FatMotivoDesdobramento.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
			

	public List<FatContaSugestaoDesdobr> pesquisarSugestoesDesdobramento(Date dataHoraSugestao, String origem, String leito,
			Integer prontuario, Boolean considera, DominioSituacaoConta[] situacoesContaHospitalar, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = this.createCriteriaPesquisaSugestoesDesdobramento(dataHoraSugestao, origem, leito, prontuario,
				considera, situacoesContaHospitalar);
		
		criteria.addOrder(Order.asc(FatContaSugestaoDesdobr.Fields.IND_CONSIDERA.toString()));
		criteria.addOrder(Order.asc(FatContaSugestaoDesdobr.Fields.LTO_ID.toString()));
		criteria.addOrder(Order.asc(FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString()));
		criteria.addOrder(Order.asc(FatContaSugestaoDesdobr.Fields.ORIGEM.toString()));
		criteria.createAlias(FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO.toString(), FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO.toString(), JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarSugestoesDesdobramentoCount(Date dataHoraSugestao, String origem, String leito, Integer prontuario,
			Boolean considera, DominioSituacaoConta[] situacoesContaHospitalar) {
		DetachedCriteria criteria = this.createCriteriaPesquisaSugestoesDesdobramento(dataHoraSugestao, origem, leito, prontuario,
				considera, situacoesContaHospitalar);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createCriteriaPesquisaSugestoesDesdobramento(Date dataHoraSugestao, String origem, String leito,
			Integer prontuario, Boolean considera, DominioSituacaoConta[] situacoesContaHospitalar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaSugestaoDesdobr.class);
  
		criteria.createAlias(FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString(),
				FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString() + "."
				+ VFatContaHospitalarPac.Fields.TAH.toString(), FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString()
				+ "." + VFatContaHospitalarPac.Fields.TAH.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString() + "."
				+ VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString()
				+ "." + VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString() + "."
				+ VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString(), FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString()
				+ "." + VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO.toString(), JoinType.LEFT_OUTER_JOIN);
		if (dataHoraSugestao != null) {
			Calendar calInicio = Calendar.getInstance();
			calInicio.setTime(dataHoraSugestao);
			calInicio.set(Calendar.SECOND, 0);
			calInicio.set(Calendar.MILLISECOND, 0);

			Calendar calFim = Calendar.getInstance();
			calFim.setTime(dataHoraSugestao);
			calFim.set(Calendar.SECOND, 59);
			calFim.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.between(FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString(), calInicio.getTime(),
					calFim.getTime()));
		}

		if (StringUtils.isNotBlank(origem)) {
			criteria.add(Restrictions.ilike(FatContaSugestaoDesdobr.Fields.ORIGEM.toString(), origem, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(leito)) {
			criteria.add(Restrictions.ilike(FatContaSugestaoDesdobr.Fields.LTO_ID.toString(), leito, MatchMode.ANYWHERE));
		}

		if (prontuario != null) {
			criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.VIEW_FAT_CONTAS_HOSPITALAR_PAC.toString() + "."
					+ VFatContaHospitalarPac.Fields.PAC_PRONTUARIO.toString(), prontuario));
		}

		if (considera != null) {
			criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.IND_CONSIDERA.toString(), considera));
		}

		if (situacoesContaHospitalar != null) {
			criteria.createAlias(FatContaSugestaoDesdobr.Fields.CONTA_HOSPITALAR.toString(),
					FatContaSugestaoDesdobr.Fields.CONTA_HOSPITALAR.toString(), JoinType.LEFT_OUTER_JOIN);

			criteria.add(Restrictions.in(FatContaSugestaoDesdobr.Fields.CONTA_HOSPITALAR.toString() + "."
					+ FatContasHospitalares.Fields.IND_SITUACAO.toString(), situacoesContaHospitalar));
		}

		return criteria;
	}

	public List<FatContaSugestaoDesdobr> listarContasSugestaoDesdobramento(Byte mdsSeq, Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaSugestaoDesdobr.class);

		criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString(), cthSeq));

		criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.MDS_SEQ.toString(), mdsSeq));

		return this.executeCriteria(criteria);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void removeContasSugestaoDesdobramento(Byte mdsSeq, Integer cthSeq, Date dthrSugestao, Boolean considera) {
		if (mdsSeq == null && cthSeq == null && dthrSugestao == null && considera == null) {
			throw new IllegalArgumentException("Informe ao menos um filtro para apagar FatContaSugestaoDesdobr.");
		}

		StringBuffer hql = new StringBuffer(80);

		hql.append(" delete from ").append(FatContaSugestaoDesdobr.class.getSimpleName());
		hql.append(" where 1 = 1");

		if (mdsSeq != null) {
			hql.append(" and ").append(FatContaSugestaoDesdobr.Fields.MDS_SEQ.toString()).append(" = :mdsSeq ");
		}

		if (cthSeq != null) {
			hql.append(" and ").append(FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString()).append(" = :cthSeq ");
		}

		if (dthrSugestao != null) {
			hql.append(" and ").append(FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString()).append(" = :dthrSugestao ");
		}

		if (considera != null) {
			hql.append(" and ").append(FatContaSugestaoDesdobr.Fields.IND_CONSIDERA.toString()).append(" = :considera ");
		}

		Query query = createHibernateQuery(hql.toString());
		if (mdsSeq != null) {
			query.setParameter("mdsSeq", mdsSeq);
		}

		if (cthSeq != null) {
			query.setParameter("cthSeq", cthSeq);
		}

		if (dthrSugestao != null) {
			query.setParameter("dthrSugestao", dthrSugestao);
		}

		if (considera != null) {
			query.setParameter("considera", considera);
		}

		query.executeUpdate();
	}

	public List<FatContaSugestaoDesdobr> pesquisarFatContaSugestaoDesdobrPorCthNaoConsidera(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaSugestaoDesdobr.class);
		criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString(), cthSeq));
		criteria.add(Restrictions.eq(FatContaSugestaoDesdobr.Fields.IND_CONSIDERA.toString(), false));
		return executeCriteria(criteria);
	}

	public void removerFatContaSugestaoDesdobrPorId(FatContaSugestaoDesdobrId id) {
		remover(obterPorChavePrimaria(id));
	}
	
	//#2155
	/**
	 * Pesuisar Relações de Sugestões de Desdobramento
	 * 
	 * @param origem
	 * @param incialPac
	 * @return
	 */
	public List<SugestoesDesdobramentoVO> pesquisarSugestoesDesdobramento(DominioOrigemSugestoesDesdobramento origem, String incialPac) {
		DetachedCriteria criteria = getCriteriaOrigemInicialPaciente(origem, incialPac);
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Criteria para listar Relações de Sugestões de Desdobramento por origem e incialPac
	 * 
	 * @param origem
	 * @param incialPac
	 * @return
	 */
	private DetachedCriteria getCriteriaOrigemInicialPaciente(DominioOrigemSugestoesDesdobramento origem, String incialPac) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatContaSugestaoDesdobr.class, "CSD");
			
			criteria.createAlias("CSD." + FatContaSugestaoDesdobr.Fields.CONTA_HOSPITALAR.toString(), "CTH", JoinType.INNER_JOIN);
			criteria.createAlias("CSD." + FatContaSugestaoDesdobr.Fields.FAT_MOTIVO_DESDOBRAMENTO.toString(), "MDS", JoinType.INNER_JOIN);
			criteria.createAlias("CTH." + FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "CIN", JoinType.INNER_JOIN);
			criteria.createAlias("CIN." + FatContasInternacao.Fields.INTERNACAO.toString(), "INT", JoinType.INNER_JOIN);
			criteria.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		
			criteria.setProjection(Projections.projectionList()
					.add(Projections.distinct(
					Projections.property("CSD." + FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString())), "dthrSugestao")
					.add(Projections.property("CSD." + FatContaSugestaoDesdobr.Fields.ORIGEM.toString()), "origem")
					.add(Projections.property("CSD." + FatContaSugestaoDesdobr.Fields.LTO_ID.toString()), "ltoId")
					.add(Projections.property("MDS." + FatMotivoDesdobramento.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
					.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "nome")
					.add(Projections.property("CSD." + FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString()), "cthSeq")
					.add(Projections.property("CTH." + FatContasHospitalares.Fields.DT_INT_ADMINISTRATIVA.toString()), "dtInternacaoAdministrativa")
					.add(Projections.property("CTH." + FatContasHospitalares.Fields.DT_ALTA_ADMINISTRATIVA.toString()), "dtAltaAdministrativa"));

			if (origem != null) {
				criteria.add(Restrictions.eq("CSD." + FatContaSugestaoDesdobr.Fields.ORIGEM.toString(), origem.toString()));
			}
					
			criteria.add(Restrictions.or(Restrictions.isNull("CSD." + FatContaSugestaoDesdobr.Fields.IND_CONSIDERA.toString()), 
										 Restrictions.eq("CSD." + FatContaSugestaoDesdobr.Fields.IND_CONSIDERA.toString(), DominioSimNao.S.isSim())));
			
			if (StringUtils.isNotBlank(incialPac)) {
				StringBuffer sql = new StringBuffer();
				for (String s : StringUtils.split(incialPac, ",")) {
					sql.append(",'").append(s).append('\'');
				}
				criteria.add(Restrictions.sqlRestriction(" SUBSTR(pac5_.NOME,1,1) in (" + sql.substring(1) + ")"));
			}
			
			criteria.addOrder(Order.asc("CSD." + FatContaSugestaoDesdobr.Fields.LTO_ID.toString()))
					.addOrder(Order.asc("PAC." + AipPacientes.Fields.PRONTUARIO.toString()))
					.addOrder(Order.asc("CSD." + FatContaSugestaoDesdobr.Fields.CTH_SEQ.toString()))
					.addOrder(Order.asc("CSD." + FatContaSugestaoDesdobr.Fields.DTHR_SUGESTAO.toString()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(SugestoesDesdobramentoVO.class));
				
		return criteria;
	}
}
