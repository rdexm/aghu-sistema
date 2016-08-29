package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;

import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;

/**
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_DIAGNOSTICOS.
 * 
 */
public class MamDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamDiagnostico> {

	private static final long serialVersionUID = -3421092985783892881L;

	public List<SumarioAltaDiagnosticosCidVO> pesquisarSumarioAltaDiagnosticosCidVO(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MamDiagnostico.class, "DIA");
		criteria.createAlias(MamDiagnostico.Fields.CID.toString(), "CID");
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull(MamDiagnostico.Fields.DATA_FIM
				.toString()));
		criteria.add(Restrictions.isNull(MamDiagnostico.Fields.CID_ATENDIMENTO
				.toString()));

		ProjectionList pList = Projections.projectionList();
		// "ciaSeq" deve ser null
		pList.add(
				Projections.property("DIA."
						+ MamDiagnostico.Fields.SEQ.toString()), "diaSeq");
		pList.add(
				Projections.property("DIA."
						+ MamDiagnostico.Fields.CID.toString()), "cid");
		pList.add(
				Projections.property("DIA."
						+ MamDiagnostico.Fields.COMPLEMENTO.toString()),
				"complemento");
		criteria.setProjection(pList);

		criteria.setResultTransformer(Transformers
				.aliasToBean(SumarioAltaDiagnosticosCidVO.class));

		return executeCriteria(criteria);
	}

	/**
	 * Retorna o valor atual do registro no banco.
	 * 
	 * @param diagnostico
	 * @return
	 */
	public MamDiagnostico obterOld(MamDiagnostico diagnostico) {

		MamDiagnostico result = null;

		if (diagnostico != null && diagnostico.getSeq() != null) {
			this.desatachar(diagnostico);
			result = this.obterPorChavePrimaria(diagnostico.getSeq());

			if (result == null || result.getSeq() == null) {
				result = null;
			}
		}

		return result;
	}

	public List<MamDiagnostico> pesquisarResolvidosPorPaciente(Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(
				MamDiagnostico.class, "mam");	
		criteria.createAlias(MamDiagnostico.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNotNull(MamDiagnostico.Fields.DATA_FIM
				.toString()));
		criteria.add(Restrictions.isNotNull(MamDiagnostico.Fields.CID
				.toString()));
		criteria.add(Subqueries.propertyNotIn(
				MamDiagnostico.Fields.SEQ.toString(), subCriteriaAtivos()));
		criteria.addOrder(Order.asc(MamDiagnostico.Fields.DATA.toString()));
		return executeCriteria(criteria);
	}

	public List<MamDiagnostico> pesquisarAtivosPorPaciente(Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(
				MamDiagnostico.class, "mam");
		
		criteria.createAlias(MamDiagnostico.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull(MamDiagnostico.Fields.DATA_FIM
				.toString()));
		criteria.add(Restrictions.isNotNull(MamDiagnostico.Fields.CID
				.toString()));
		criteria.add(Subqueries.propertyNotIn(
				MamDiagnostico.Fields.SEQ.toString(), subCriteriaAtivos()));
		criteria.addOrder(Order.asc(MamDiagnostico.Fields.DATA.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria subCriteriaAtivos() {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				MamDiagnostico.class, "mam2").setProjection(
				Property.forName(MamDiagnostico.Fields.DIA_SEQ.toString()));
		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.eqProperty(
				MamDiagnostico.Fields.DIA_SEQ.toString(), "mam.seq"));
		return criteria;
	}

	/**
	 * Lista os diagnósticos Ativos e validados de acordo com paciente e cid.
	 * 
	 * @param paciente
	 * @param cid
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosPorPacienteCid(
			AipPacientes paciente, AghCid cid) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamDiagnostico.class);

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.PAC_CODIGO.toString(),
				paciente.getCodigo()));

		criteria.add(Restrictions.eq(MamDiagnostico.Fields.SID_SEQ.toString(),
				cid.getSeq()));

		criteria.add(Restrictions.isNull(MamDiagnostico.Fields.DATA_FIM
				.toString()));

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));

		return this.executeCriteria(criteria);
	}


	public List<MamDiagnostico> listarDiagnosticosPorCirurgia(
			Integer crgSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamDiagnostico.class);
		
		criteria.add(Restrictions.eq(MamDiagnostico.Fields.CIRURGIA.toString() + "." + MbcCirurgias.Fields.SEQ.toString(),
				crgSeq));

		criteria.add(Restrictions.isNull(MamDiagnostico.Fields.DATA_FIM
				.toString()));

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));

		return this.executeCriteria(criteria);
	}

	/**
	 * Método que retorna os diagnósticos ativos relativos a um cidAtendimento.
	 * 
	 * @param cidAtendimento
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosAtivosPorCidAtendimento(
			MpmCidAtendimento cidAtendimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamDiagnostico.class);

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.CID_ATENDIMENTO.toString(),
				cidAtendimento));

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return this.executeCriteria(criteria);
	}

	/**
	 * Retorna os diagnósticos de um atendimento.
	 * 
	 * @param atendimento
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosPorAtendimento(
			AghAtendimentos atendimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamDiagnostico.class);

		criteria.add(Restrictions.eq(
				MamDiagnostico.Fields.ATENDIMENTO.toString(), atendimento));

		List<MamDiagnostico> lista = this.executeCriteria(criteria);

		return lista;

	}

	public List<MamDiagnostico> listarDiagnosticosPorCidAtendimentoSeq(Integer ciaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class);
		criteria.add(Restrictions.eq(MamDiagnostico.Fields.CID_ATENDIMENTO_SEQ.toString(), ciaSeq));

		return this.executeCriteria(criteria);
	}

	public List<MamDiagnostico> pesquisarDiagnosticosPorPaciente(AipPacientes paciente) {
		DetachedCriteria subquery = DetachedCriteria.forClass(MamDiagnostico.class, "diax");
		DetachedCriteria criteria = getCriteriaPesquisarDiagnosticosPorPaciente(paciente, subquery);
		criteria.addOrder(Order.desc("dia." + MamDiagnostico.Fields.DATA.toString()));
		criteria.addOrder(Order.desc("dia." + MamDiagnostico.Fields.DATA_FIM.toString()));
		
		criteria.setFetchMode(MamDiagnostico.Fields.CID.toString(), FetchMode.JOIN);

		return executeCriteria(criteria);
	}

	
	public Long pesquisarDiagnosticosPorPacienteCount(AipPacientes paciente) {
		DetachedCriteria subquery = DetachedCriteria.forClass(MamDiagnostico.class, "diax");
		DetachedCriteria criteria = getCriteriaPesquisarDiagnosticosPorPaciente(
				paciente, subquery);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getCriteriaPesquisarDiagnosticosPorPaciente(
			AipPacientes paciente, DetachedCriteria subquery) {
		subquery.add(Restrictions.eqProperty("diax." + MamDiagnostico.Fields.DIA_SEQ.toString(),
				"dia." + MamDiagnostico.Fields.SEQ.toString()));
		subquery.add(Restrictions.eq("diax." + MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));
		subquery.setProjection(Projections.property("diax."
				+ MamDiagnostico.Fields.DIA_SEQ.toString()));

		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class, "dia");
		criteria.add(Restrictions.eq("dia." + MamDiagnostico.Fields.PAC_CODIGO.toString(),
				paciente.getCodigo()));
		criteria.add(Restrictions.isNotNull("dia." + MamDiagnostico.Fields.SID_SEQ.toString()));
		criteria.add(Restrictions.eq("dia." + MamDiagnostico.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.not(Property.forName(
				"dia." + MamDiagnostico.Fields.SEQ.toString()).in(subquery)));
		return criteria;
	}
	
	public List<MamDiagnostico> listarDiagnosticoValidadoPorAtendimento(Integer atdSeq){
		DetachedCriteria criteriaDiagnostico = DetachedCriteria.forClass(MamDiagnostico.class);
		criteriaDiagnostico.createAlias(MamDiagnostico.Fields.ATENDIMENTO.toString(), "aliasAtd");
		criteriaDiagnostico.createAlias(MamDiagnostico.Fields.REL_DIAGNOSTICO.toString(), "aliasRelDiag");
		criteriaDiagnostico.createAlias("aliasAtd."+AghAtendimentos.Fields.PACIENTE.toString(), "aliasPac");
		criteriaDiagnostico.add(Restrictions.eq("aliasAtd."+AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteriaDiagnostico.add(Restrictions.isNull(MamDiagnostico.Fields.DATA_FIM.toString()));
		criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.IND_PENDENTE.toString(),DominioIndPendenteDiagnosticos.V));
		criteriaDiagnostico.add(Restrictions.isNull(MamDiagnostico.Fields.DTHR_VALIDA_MVTO.toString()));
		criteriaDiagnostico.add(Restrictions.isNotNull("aliasRelDiag."+EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString()));
		criteriaDiagnostico.add(Restrictions.isNotNull("aliasRelDiag."+EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString()));
		criteriaDiagnostico.add(Restrictions.isNotNull("aliasRelDiag."+EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString()));
		criteriaDiagnostico.add(Restrictions.isNotNull("aliasRelDiag."+EpeFatRelDiagnostico.Fields.FRE_SEQ.toString()));
		DetachedCriteria criteriaPrescCuidDiagnostico = DetachedCriteria.forClass(EpePrescCuidDiagnostico.class);
		criteriaPrescCuidDiagnostico.createAlias(EpePrescCuidDiagnostico.Fields.PRESCRICAO_CUIDADO.toString(),"aliasPrc");
		criteriaPrescCuidDiagnostico.createAlias("aliasPrc."+EpePrescricoesCuidados.Fields.PRESCRICAO_ENFERMAGEM.toString(), "aliasPen");
		criteriaPrescCuidDiagnostico.createAlias("aliasPen."+EpePrescricaoEnfermagem.Fields.ATENDIMENTO.toString(), "aliasPrcAtd");
		criteriaPrescCuidDiagnostico.add(Restrictions.eq("aliasPrcAtd."+AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		ProjectionList pList = Projections.projectionList();
		
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.CDG_CUI_SEQ.toString()), "cdgCuiSeq");
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SEQUENCIA.toString()), "cdgFdgDgnSequencia");
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_GNB_SEQ.toString()),
				"cdgFdgDgnSnbGnbSeq");
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_DGN_SNB_SEQUENCIA.toString()),
				"cdgFdgDgnSnbSequencia");
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.CDG_FDG_FRE_SEQ.toString()),
				"cdgFdgFreSeq");
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.PRC_ATD_SEQ.toString()),
				"prcAtdSeq");
	
		pList.add(
				Projections.property(EpePrescCuidDiagnostico.Fields.PRC_SEQ.toString()),
				"prcSeq");
	
		criteriaPrescCuidDiagnostico.setProjection(pList);
		
		DetachedCriteria criteriaPrescricaoCuidado = DetachedCriteria.forClass(EpePrescricoesCuidados.class);
		criteriaPrescricaoCuidado.add(Restrictions.or(Restrictions.isNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()), Restrictions.and(Restrictions.isNotNull(EpePrescricoesCuidados.Fields.DTHR_FIM.toString()),Restrictions.gt(EpePrescricoesCuidados.Fields.DTHR_FIM.toString(), new Date()))));
		criteriaPrescricaoCuidado.setProjection(Projections.property(EpePrescricoesCuidados.Fields.SEQ.toString()));
		criteriaPrescCuidDiagnostico.add(Subqueries.exists(criteriaPrescricaoCuidado));
		criteriaDiagnostico.add(Subqueries.notExists(criteriaPrescCuidDiagnostico));
		return executeCriteria(criteriaDiagnostico);
	}
	
	public List<MamDiagnostico> listarDiagnosticosPorFatRelDiagnosticoEPaciente(EpeFatRelDiagnosticoId id, Integer pacCodigo){
		DetachedCriteria criteriaDiagnostico = DetachedCriteria.forClass(MamDiagnostico.class, "diag");
		if(id!=null){
			criteriaDiagnostico.createAlias(MamDiagnostico.Fields.ATENDIMENTO.toString(), MamDiagnostico.Fields.ATENDIMENTO.toString());
			criteriaDiagnostico.createAlias(MamDiagnostico.Fields.ATENDIMENTO.toString()+"."+AghAtendimentos.Fields.PACIENTE.toString(), MamDiagnostico.Fields.ATENDIMENTO.toString()+"."+AghAtendimentos.Fields.PACIENTE.toString());
			criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.REL_DIAGNOSTICO.toString()+"."+EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString(), id.getDgnSequencia()));
			criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.REL_DIAGNOSTICO.toString()+"."+EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), id.getDgnSnbGnbSeq()));
			criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.REL_DIAGNOSTICO.toString()+"."+EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), id.getDgnSnbSequencia()));
			criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.REL_DIAGNOSTICO.toString()+"."+EpeFatRelDiagnostico.Fields.FRE_SEQ.toString(), id.getFreSeq()));
			criteriaDiagnostico.add(Restrictions.eq(MamDiagnostico.Fields.ATENDIMENTO.toString()+"."+AghAtendimentos.Fields.PACIENTE.toString()+"."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
			return executeCriteria(criteriaDiagnostico);		
		} else {
			return null;
		}
		
	}
	
	public List<DiagnosticoEtiologiaVO> listarAtualizarMamDignosticos(Integer atdSeq){

		StringBuffer sql = new StringBuffer(1200);

		sql.append(" select atd.pac_codigo , dia.FDG_DGN_SNB_GNB_SEQ as cdgFdgDgnSnbGnbSeq  , dia.FDG_DGN_SNB_SEQUENCIA as cdgFdgDgnSnbSequencia, ");
		sql.append(" dia.FDG_DGN_SEQUENCIA as cdgFdgDgnSequencia , dia.FDG_FRE_SEQ as cdgFdgFreSeq ");
		sql.append(" from agh.mam_diagnosticos dia , agh.agh_atendimentos atd ");
		sql.append(" where atd.seq = " ).append(atdSeq);
		sql.append(" and dia.pac_codigo = atd.pac_codigo ");
		sql.append(" AND dia.data_fim IS NULL");
		sql.append(" AND dia.ind_situacao = 'A'");
		sql.append(" AND dia.ind_pendente = 'V'");
		sql.append(" AND dia.dthr_valida_mvto IS NULL");
		sql.append(" AND dia.FDG_DGN_SNB_GNB_SEQ IS NOT NULL");
		sql.append(" AND dia.FDG_DGN_SNB_SEQUENCIA IS NOT NULL");
		sql.append(" AND dia.FDG_DGN_SEQUENCIA IS NOT NULL");
		sql.append(" AND dia.FDG_FRE_SEQ IS NOT NULL");
		if(isOracle()){
			sql.append(" MINUS ");
		}else{
			sql.append(" EXCEPT ");
		}
		sql.append(" select atd.pac_codigo , pcd.cdg_fdg_dgn_snb_gnb_seq as cdgFdgDgnSnbGnbSeq, pcd.cdg_fdg_dgn_snb_sequencia as cdgFdgDgnSnbSequencia, ");
		sql.append(" pcd.cdg_fdg_dgn_sequencia as cdgFdgDgnSequencia, pcd.cdg_fdg_fre_seq as cdgFdgFreSeq"); 
		sql.append(" from agh.epe_presc_cuid_diagnosticos pcd , agh.agh_atendimentos atd ");
		sql.append(" where atd.seq = ").append(atdSeq);
		sql.append(" and pcd.prc_atd_seq  = atd.seq");
		sql.append(" and (pcd.prc_atd_seq, pcd.prc_seq) in");
		sql.append(" (select prc.atd_seq, prc.seq");
		sql.append(" from agh.epe_prescricoes_cuidados prc ");
		sql.append(" where prc.atd_seq = pcd.prc_atd_seq ");
		sql.append(" and prc.seq = pcd.prc_seq " );
		sql.append(" and ((prc.dthr_fim is null) or (prc.dthr_fim is not null and prc.dthr_fim > ");
		
		if(isOracle()){
			sql.append(" SYSDATE ");
		}else{
			sql.append(" now() ");
		}
		sql.append(" ))) ");
		
		SQLQuery query = createSQLQuery(sql.toString());

		query.addScalar("cdgFdgDgnSnbGnbSeq", ShortType.INSTANCE);
		query.addScalar("cdgFdgDgnSnbSequencia", ShortType.INSTANCE);
		query.addScalar("cdgFdgDgnSequencia", ShortType.INSTANCE);
		query.addScalar("cdgFdgFreSeq", ShortType.INSTANCE);


		// Transforma e seta aliases do resultado no VO
		query.setResultTransformer(Transformers.aliasToBean(DiagnosticoEtiologiaVO.class));
		// Retorna Lista
		return query.list();

	}

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	public MamDiagnostico buscarDiagnosticoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class);

		criteria.add(Restrictions.eq(MamDiagnostico.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamDiagnostico> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * #41798
	 * Consulta C3
	 * @param pacCodigo
	 * @return
	 */
	public Boolean verificarPacienteTemDiabetesPorPacCodigo(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class, "DIA");
		final String E10 = "E10";
		final String E11 = "E11";
		
		criteria.createAlias("DIA."+MamDiagnostico.Fields.CID.toString(), "CID");
		
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull("DIA."+MamDiagnostico.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.or(Restrictions.ilike("CID."+AghCid.Fields.CODIGO.toString(), E10, MatchMode.START),
				Restrictions.ilike("CID."+AghCid.Fields.CODIGO.toString(), E11, MatchMode.START)));
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		return (executeCriteriaCount(criteria) > 0);
	}
	
	/**
	 * #41798
	 * Consulta C4
	 * @param pacCodigo
	 * @return
	 */
	public Boolean verificarPacienteHIVPositivoPorPacCodigo(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class, "DIA");
		
		final String B20 = "B20";
		final String B24_9 = "B24.9";
		final String F02_4 = "F02.4";
		final String R75 = "R75";
		final String Z21 = "Z21";
		
		criteria.createAlias("DIA."+MamDiagnostico.Fields.CID.toString(), "CID");
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull("DIA."+MamDiagnostico.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.or(Restrictions.between("CID."+AghCid.Fields.CODIGO.toString(), B20, B24_9),
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), F02_4),
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), R75),
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), Z21)));
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		return (executeCriteriaCount(criteria) > 0);
	}

	/**
	 * #41798
	 * Consulta C5
	 * @param pacCodigo
	 * @return
	 */
	public Boolean verificarPacienteTemHepatiteBPorPacCodigo(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class, "DIA");
		
		final String B16 = "B16";
		final String B18_0 = "B18.0";
		final String B18_1 = "B18.1";
		
		criteria.createAlias("DIA."+MamDiagnostico.Fields.CID.toString(), "CID");
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull("DIA."+MamDiagnostico.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.or(
				Restrictions.ilike("CID."+AghCid.Fields.CODIGO.toString(), B16, MatchMode.START),
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), B18_0),
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), B18_1)));
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		return (executeCriteriaCount(criteria) > 0);
	}

	/**
	 * #41798
	 * Consulta C6
	 * @param pacCodigo
	 * @return
	 */
	public Boolean verificarPacienteTemHepatiteCPorPacCodigo(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class, "DIA");
		
		final String B17_1 = "B17.1";
		final String B18_2 = "B18.2";
		
		criteria.createAlias("DIA."+MamDiagnostico.Fields.CID.toString(), "CID");
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.V));
		criteria.add(Restrictions.isNull("DIA."+MamDiagnostico.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.or(
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), B17_1),
				Restrictions.eq("CID."+AghCid.Fields.CODIGO.toString(), B18_2)));
		criteria.add(Restrictions.eq("DIA."+MamDiagnostico.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		return (executeCriteriaCount(criteria) > 0);
	}
	
	
	/**
	 * Obter os diagnósticos do prontuário do paciente
	 * #50931 - C5
	 * @param prontuario
	 * @param dataFiltro
	 * @return
	 */
	public List<MamDiagnostico> listarDiagnosticosPortalPacienteInternado(Integer pacCodigo, Date dataFiltro) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamDiagnostico.class, "diag");
		criteria.createAlias("diag." + MamDiagnostico.Fields.CID.toString(), "cid",  JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("diag." + MamDiagnostico.Fields.PAC_CODIGO.toString(),  pacCodigo));
		if (dataFiltro != null){
			criteria.add(Restrictions.le("diag." + MamDiagnostico.Fields.DATA.toString(),  dataFiltro));
		}
		criteria.add(Restrictions.eq("diag." + MamDiagnostico.Fields.IND_SITUACAO.toString(),  DominioSituacao.A));
		
		criteria.addOrder(Order.asc("diag." + MamDiagnostico.Fields.DATA.toString()));

		return this.executeCriteria(criteria);
	}
}
