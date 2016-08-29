package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.vo.ParametrosGeracaoLaudoOtorrinoVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FatResumoApacs;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;

public class FatCandidatosApacOtorrinoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCandidatosApacOtorrino> {

	private static final String PRH = "PRH.";
	private static final long serialVersionUID = -9172318316871876315L;

	public List<FatCandidatosApacOtorrino> listarCandidatosApacOtorrinoPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCandidatosApacOtorrino.class);

		criteria.add(Restrictions.eq(FatCandidatosApacOtorrino.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public FatCandidatosApacOtorrino obterCandidatosApacOtorrinoPorPmrSeqESituacao(Long pmrSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCandidatosApacOtorrino.class);

		criteria.add(Restrictions.eq(FatCandidatosApacOtorrino.Fields.PMR_SEQ.toString(), pmrSeq));
		criteria.add(Restrictions.eq(FatCandidatosApacOtorrino.Fields.SITUACAO.toString(), situacao));

		return (FatCandidatosApacOtorrino) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 42803
	 * C9
	 * C10 
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterCandidatosApacOtorrino(ParametrosGeracaoLaudoOtorrinoVO parametrosGeracaoLaudoOtorrinoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCandidatosApacOtorrino.class, "caot");
		criteria.createAlias("caot." + FatCandidatosApacOtorrino.Fields.PRODCEDIMENTO_HOSPITALAR_SUGERIDO.toString(), "psug", JoinType.INNER_JOIN);
		criteria.createAlias("psug." + FatProcedHospInternosPai.Fields.V_FAT_ASSOCIACAO_PROCD_HOSP.toString(), "v", JoinType.INNER_JOIN);
		criteria.createAlias("caot." + FatCandidatosApacOtorrino.Fields.PROCEDIMENTO_AMB_REALIZADO.toString(), "pmr", JoinType.INNER_JOIN);
		criteria.createAlias("pmr." + FatProcedAmbRealizado.Fields.CID.toString(), "cid", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("pmr." + FatProcedAmbRealizado.Fields.CONSULTA.toString(), "con", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("v." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CODIGO_TABELA.toString()))
				.add(Projections.property("v." + VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO_IPH.toString()))
				.add(Projections.property("con." + AacConsultas.Fields.COD_CENTRAL.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CMCE.toString()))
				.add(Projections.property("cid." + AghCid.Fields.CODIGO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CID_10_PRINCIPAL.toString()))
				.add(Projections.property("cid." + AghCid.Fields.DESCRICAO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO_CID.toString()))));
		
		criteria.add(Restrictions.eq("caot." + FatCandidatosApacOtorrino.Fields.PAC_CODIGO.toString(), parametrosGeracaoLaudoOtorrinoVO.getCodigoPaciente()));
		criteria.add(Restrictions.eq("v." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), Short.valueOf("12")));
//		criteria.add(Restrictions.eq("caot." + FatCandidatosApacOtorrino.Fields.CANDIDATO.toString(), candidato));
		criteria.add(Restrictions.eq("caot."  + FatCandidatosApacOtorrino.Fields.PHI_SEQ.toString(), parametrosGeracaoLaudoOtorrinoVO.getSeqProcedimentoHospitalarInterno()));
		criteria.add(Restrictions.eq("caot."  + FatCandidatosApacOtorrino.Fields.SEQ.toString(), parametrosGeracaoLaudoOtorrinoVO.getFatCandidatoApacSeq()));
		criteria.add(Restrictions.eq("pmr."  + FatProcedAmbRealizado.Fields.PHI_SEQ.toString(), parametrosGeracaoLaudoOtorrinoVO.getSeqProcedimentoHospitalarInterno()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		
		LaudoSolicitacaoAutorizacaoProcedAmbVO vo = (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
		if(vo !=null){
			if(vo.getCid10principal()==null){
				vo.setCid10principal("H91.9");
			}
			if(vo.getDescricaoCid()==null){
				vo.setDescricaoCid("PERDA NAO ESPECIFICADA DE AUDICAO");
			}
		}
		return vo;
	}
	
	
	
	/**
	 * C22 - #42803
	 * Método para obter o Cid quando a especialidade da consulta for Otorrino
	 * @param numeroConsulta
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterCidOtorrinoNumeroConsulta(Integer numeroConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultaProcedHospitalar.class, "PRH");
		criteria.createAlias(PRH + AacConsultaProcedHospitalar.Fields.CID.toString(), "cid", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("cid." + AghCid.Fields.CODIGO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CID_10_PRINCIPAL.toString()))
				.add(Projections.property("cid." + AghCid.Fields.DESCRICAO.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO_CID.toString()))));
		criteria.add(Restrictions.eq(PRH + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(PRH + AacConsultaProcedHospitalar.Fields.IND_CONSULTA.toString(), false));
		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		LaudoSolicitacaoAutorizacaoProcedAmbVO vo = (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
		String descricaoCid = "PERDA NAO ESPECIFICADA DE AUDICAO";
		String cid10principal = "H91.9";

		if(vo !=null){
			if(vo.getCid10principal()==null){
				vo.setCid10principal(descricaoCid);
			}
			if(vo.getDescricaoCid()==null){
				vo.setDescricaoCid(cid10principal);
			}
		}

		if(vo == null){
			vo = new LaudoSolicitacaoAutorizacaoProcedAmbVO();
			vo.setDescricaoCid(descricaoCid);
			vo.setCid10principal(cid10principal);
		}

		return vo;
	}

	

	/**
	 * C23 - #42803
	 * Método para obter o Cid quando a especialidade da consulta for Otorrino
	 * @param numeroConsulta
	 * @return
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterNomeCPFProfissResponsavel(LaudoSolicitacaoAutorizacaoProcedAmbVO laudoSolicitacaoAutorizacaoProcedAmbVO, Integer numeroConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.INNER_JOIN);
		criteria.createAlias("EQP." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "PROF", JoinType.INNER_JOIN);
		criteria.createAlias("PROF." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.INNER_JOIN);

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.NOME_PROFISSIONAL_SOLICITANTE.toString()))
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.CPF.toString()).as(
						LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.PROF_CPF.toString()))));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		LaudoSolicitacaoAutorizacaoProcedAmbVO vo = (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
		if(vo!=null){
			laudoSolicitacaoAutorizacaoProcedAmbVO.setNomeProfissionalSolicitante(vo.getNomeProfissionalSolicitante());
			laudoSolicitacaoAutorizacaoProcedAmbVO.setCpfProfissional(vo.getCpfProfissional());
		}
		return laudoSolicitacaoAutorizacaoProcedAmbVO;
	}

	
	/* #42803
	 * ORADB: c_maxima_data
	 */
	public Date obterDataMaxCriacao(){
		DetachedCriteria criteria  = DetachedCriteria.forClass(FatCandidatosApacOtorrino.class);
		
		criteria.setProjection(Projections.max(FatCandidatosApacOtorrino.Fields.CRIADO_EM.toString()));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	/* #42803
	 * ORADB: c_ver_candidato
	 */
	public Integer obterCandidato(Integer codigo, String candidato){
		DetachedCriteria criteria = obterCriteriaBuscaCandidato(codigo,candidato);
		criteria.setProjection(Projections.property(FatCandidatosApacOtorrino.Fields.SEQ.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria obterCriteriaBuscaCandidato(Integer codigo,String candidato) {
		DetachedCriteria criteria  = DetachedCriteria.forClass(FatCandidatosApacOtorrino.class);
		criteria.add(Restrictions.eq(FatCandidatosApacOtorrino.Fields.PAC_CODIGO.toString(),codigo));
		criteria.add(Restrictions.eq(FatCandidatosApacOtorrino.Fields.CANDIDATO.toString(), candidato));
		return criteria;
	}
	
	/*
	 * #42803
	 * ORADB: c_caot_reav
	 */
	
	public FatCandidatosApacOtorrino buscarCandidatoNaoReavaliacao(Integer codigo){
		StringBuilder hql = new StringBuilder(" FROM ")
		.append(FatCandidatosApacOtorrino.class.getName())
		.append(" WHERE ").append(FatCandidatosApacOtorrino.Fields.PAC_CODIGO.toString())
		.append(" =:codigoPaciente and ").append(FatCandidatosApacOtorrino.Fields.CANDIDATO.toString())
		.append(" <>:candidato and (").append(FatCandidatosApacOtorrino.Fields.SITUACAO.toString())
		.append(" =:indSituacaA or (").append(FatCandidatosApacOtorrino.Fields.SITUACAO.toString())
		.append(" =:indSituacaoB and exists (").append(" select 1 from ").append(FatResumoApacs.class.getName()).append(" rao,")
			.append(FatCompetencia.class.getName()).append(" cpe where cpe.")
			.append(FatCompetencia.Fields.MODULO.toString()).append(" = 'APAT' and cpe.")
			.append(FatCompetencia.Fields.IND_SITUACAO.toString()).append(" =:indSituacaoC and rao.")
			.append(FatResumoApacs.Fields.PAC_CODIGO.toString()).append(" =:codigoPaciente and ")
			.append(" to_number(to_char(rao.").append(FatResumoApacs.Fields.DT_INICIO.toString())
			.append(",'mmyyyy'),'999999') = cast((cast(cpe.").append(FatCompetencia.Fields.MES.toString())
			.append(" as char) || cast(cpe.").append(FatCompetencia.Fields.ANO.toString())
			.append(" as char )) as int)")
			.append(" )"
			+ "))  order by ").append(FatCandidatosApacOtorrino.Fields.DT_EVENTO.toString()).append(" desc");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("codigoPaciente", codigo);
		query.setParameter("candidato", "REAVALIACAO");
		query.setParameter("indSituacaA", DominioSituacao.A);
		query.setParameter("indSituacaoB", DominioSituacao.I);
		query.setParameter("indSituacaoC", DominioSituacaoCompetencia.A);
		
		return (FatCandidatosApacOtorrino) query.uniqueResult();
	}
	
	/*
	 * #42803
	 * ORADB: c_busca_candidato
	 */
	
	public FatCandidatosApacOtorrino buscarCandidato(Integer codigo, String candidato){
		StringBuilder hql = new StringBuilder(" FROM ")
		.append(FatCandidatosApacOtorrino.class.getName())
		.append(" WHERE ").append(FatCandidatosApacOtorrino.Fields.PAC_CODIGO.toString())
		.append(" =:codigoPaciente and ").append(FatCandidatosApacOtorrino.Fields.CANDIDATO.toString())
		.append(" =:candidato and (").append(FatCandidatosApacOtorrino.Fields.SITUACAO.toString())
		.append(" =:indSituacaA or (").append(FatCandidatosApacOtorrino.Fields.SITUACAO.toString())
		.append(" =:indSituacaoB and exists (").append(" select 1 from ").append(FatResumoApacs.class.getName()).append(" rao,")
			.append(FatCompetencia.class.getName()).append(" cpe where cpe.")
			.append(FatCompetencia.Fields.MODULO.toString()).append(" = 'APAT' and cpe.")
			.append(FatCompetencia.Fields.IND_SITUACAO.toString()).append(" =:indSituacaoC and rao.")
			.append(FatResumoApacs.Fields.PAC_CODIGO.toString()).append(" =:codigoPaciente and ")
			.append(" to_number(to_char(rao.").append(FatResumoApacs.Fields.DT_INICIO.toString());
		
			if(isOracle()){
				hql.append(",'mmyyyy')) = cpe.").append(FatCompetencia.Fields.MES.toString())
				.append(" || cpe.").append(FatCompetencia.Fields.ANO.toString());
			}else{
				hql.append(",'mmyyyy'),'999999') = cast((cast(cpe.").append(FatCompetencia.Fields.MES.toString())
				.append(" as char) || cast(cpe.").append(FatCompetencia.Fields.ANO.toString())
				.append(" as char )) as int))");
			}
			hql.append(" ))  order by ").append(FatCandidatosApacOtorrino.Fields.DT_EVENTO.toString()).append(" desc");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("codigoPaciente", codigo);
		query.setParameter("candidato", candidato);
		query.setParameter("indSituacaA", DominioSituacao.A);
		query.setParameter("indSituacaoB", DominioSituacao.I);
		query.setParameter("indSituacaoC", DominioSituacaoCompetencia.A);
		
		return (FatCandidatosApacOtorrino) query.uniqueResult();
	}

	public FatCandidatosApacOtorrino buscarCandidatoPorPmrSeq(Long pmrSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCandidatosApacOtorrino.class);
		criteria.add(Restrictions.eq(FatCandidatosApacOtorrino.Fields.PMR_SEQ.toString(), pmrSeq));
		return (FatCandidatosApacOtorrino) executeCriteriaUniqueResult(criteria);
}
}
