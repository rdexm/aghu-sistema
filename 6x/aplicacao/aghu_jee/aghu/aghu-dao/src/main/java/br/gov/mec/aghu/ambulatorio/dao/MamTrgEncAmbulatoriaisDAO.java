package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.TriagemRealizadaEmergenciaVO;
import br.gov.mec.aghu.model.MamTrgEncAmbulatoriais;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTriagens;

public class MamTrgEncAmbulatoriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgEncAmbulatoriais>{

	
	private static final long serialVersionUID = -6079889022395511360L;

	@SuppressWarnings("unchecked")
	public List<TriagemRealizadaEmergenciaVO> listarTriagemRealizadaEmergencia(Date vDtHrInicio, Date vDtHrFim) {
	
		Query q = this.obterQuerySession(vDtHrInicio, vDtHrFim, this.obterQueryUnionParte1());
		//executa a primeira query		
		List<TriagemRealizadaEmergenciaVO> listaVO = q.list();
		//executa a segunda query
		q = this.obterQuerySession(vDtHrInicio, vDtHrFim, this.obterQueryUnionParte2());
		//junta as duas queries
		listaVO.addAll(q.list());
		
		return listaVO;
	}
	
	private Query obterQuerySession(Date vDtHrInicio, Date vDtHrFim, StringBuilder query) {
		Query q = createHibernateQuery(query.toString());
		q.setDate("vDtHrInicio", vDtHrInicio);
		q.setDate("vDtHrFim", vDtHrFim);
		q.setResultTransformer(Transformers.aliasToBean(TriagemRealizadaEmergenciaVO.class));
		
		return q;
	}
	
	private StringBuilder obterQueryUnionParte1() {
		StringBuilder hql = new StringBuilder(400);
		hql.append(" SELECT ");
		hql.append("  TEA.").append(MamTrgEncAmbulatoriais.Fields.CRIADO_EM.toString()).append(" as criadoEm, ");
		hql.append("CASE WHEN TEA.").append(MamTrgEncAmbulatoriais.Fields.SER_MATRICULA.toString()).
		append(" is null THEN 9999999 ELSE TEA.").append(MamTrgEncAmbulatoriais.Fields.SER_MATRICULA.toString()).
		append(" END as serMatricula, ");
		//--NVL(SER_MATRICULA,9999999), ---SER_MATRICULA 
		hql.append("CASE WHEN TEA.").append(MamTrgEncAmbulatoriais.Fields.SER_VIN_CODIGO.toString())
		.append(" is null THEN 955 ELSE TEA.").append(MamTrgEncAmbulatoriais.Fields.SER_VIN_CODIGO.toString()).
		append(" END as serVinCodigo, ");
		//--NVL(SER_VIN_CODIGO,955), ---SER_VIN_CODIGO
		
		hql.append("  TRG.").append(MamTriagens.Fields.UNF_SEQ.toString()).append(" as unfSeq, ");
		hql.append("  TRG.").append(MamTriagens.Fields.PAC_CODIGO.toString()).append(" as pacCodigo ");
		hql.append(" FROM ");
		hql.append(' ').append(MamTrgEncAmbulatoriais.class.getSimpleName()).append(" tea ");
		hql.append(" 	,").append(MamTriagens.class.getSimpleName()).append(" trg ");
		hql.append(" WHERE ");
		hql.append(" 	trg.").append(MamTriagens.Fields.DTHR_ULT_MVTO.toString()).append(" BETWEEN :vDtHrInicio AND :vDtHrFim ");
		hql.append(" 	AND trg.").append(MamTriagens.Fields.SEG_SEQ.toString()).append("+0 = 9 ");
		hql.append(" 	AND tea.").append(MamTrgEncAmbulatoriais.Fields.TRG_SEQ.toString()).append("= trg.").append(MamTriagens.Fields.SEQ.toString());
		hql.append(" 	AND tea.").append(MamTrgEncAmbulatoriais.Fields.DTHR_ESTORNO.toString()).append(" IS NULL ");
		hql.append(" 	AND tea.").append(MamTrgEncAmbulatoriais.Fields.SER_MATRICULA_ESTORNADO.toString()).append(" IS NULL ");
		hql.append(" 	AND tea.").append(MamTrgEncAmbulatoriais.Fields.SER_VIN_CODIGO_ESTORNADO.toString()).append(" IS NULL ");
		
		return hql;
	}
	
	private StringBuilder obterQueryUnionParte2() {
		StringBuilder hql = new StringBuilder(400);
		hql.append(" SELECT	 ");
		hql.append("  TEE.").append(MamTrgEncExternos.Fields.CRIADO_EM.toString()).append(" as criadoEm,");
		hql.append("CASE WHEN TEE.").append(MamTrgEncExternos.Fields.SER_MATRICULA.toString()).
		append(" is null THEN 9999999 ELSE TEE.").append(MamTrgEncExternos.Fields.SER_MATRICULA.toString()).
		append(" END as serMatricula, ");
		//--NVL(SER_MATRICULA,9999999), ---SER_MATRICULA 
		hql.append("CASE WHEN TEE.").append(MamTrgEncExternos.Fields.SER_VIN_CODIGO.toString())
		.append(" is null THEN 955 ELSE TEE.").append(MamTrgEncExternos.Fields.SER_VIN_CODIGO.toString()).
		append(" END as serVinCodigo, ");
		//--NVL(SER_VIN_CODIGO,955), ---SER_VIN_CODIGO
		
		hql.append("  TRG.").append(MamTriagens.Fields.UNF_SEQ.toString()).append(" as unfSeq,");
		hql.append("  TRG.").append(MamTriagens.Fields.PAC_CODIGO.toString()).append(" as pacCodigo ");
		hql.append(" FROM ");
		hql.append("  ").append(MamTrgEncExternos.class.getSimpleName()).append(" tee ");
		hql.append(" 	,").append(MamTriagens.class.getSimpleName()).append(" trg ");
		hql.append(" WHERE ");
		hql.append(" 	trg.").append(MamTriagens.Fields.DTHR_ULT_MVTO.toString()).append(" BETWEEN :vDtHrInicio AND :vDtHrFim ");
		hql.append(" 	AND trg.").append(MamTriagens.Fields.SEG_SEQ.toString()).append("+0 = 9 ");
		hql.append(" 	AND tee.").append(MamTrgEncExternos.Fields.TRG_SEQ.toString()).append("= trg.").append(MamTriagens.Fields.SEQ.toString());
		hql.append(" 	AND tee.").append(MamTrgEncExternos.Fields.DTHR_ESTORNO.toString()).append(" IS NULL ");
		hql.append(" 	AND tee.").append(MamTrgEncExternos.Fields.SER_MATRICULA_ESTORNADO.toString()).append(" IS NULL ");
		hql.append(" 	AND tee.").append(MamTrgEncExternos.Fields.SER_VIN_CODIGO_ESTORNADO.toString()).append(" IS NULL ");
		
		return hql;
	}
	
	public MamTrgEncAmbulatoriais pesquisarEncaminhamentoAmbulatoriaisPorSeqTriagem(Long trgSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "TEA");
		
		dc.add(Restrictions.eq("TEA.".concat(MamTrgEncAmbulatoriais.Fields.TRG_SEQ.toString()), trgSeq));
		dc.add(Restrictions.isNull("TEA.".concat(MamTrgEncAmbulatoriais.Fields.DTHR_ESTORNO.toString())));
		
		Object obj = executeCriteriaUniqueResult(dc);
		
		if (obj != null) {
			return (MamTrgEncAmbulatoriais) obj;
		}
		return null;
	}
	
	/**
	 * CURSOR c_amb
	 * @param trgSeq
	 * @return MamTrgEncAmbulatoriais
	 */
	
	public MamTrgEncAmbulatoriais pesquisarMamTrgEncAmbulatoriaisComEspecialidade(Long trgSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncAmbulatoriais.class, "tea");
		criteria.createAlias("tea." + MamTrgEncAmbulatoriais.Fields.ESPECIALIDADE.toString(), "esp");
		
		criteria.add(Restrictions.eq("tea." + MamTrgEncAmbulatoriais.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.isNull("tea." + MamTrgEncAmbulatoriais.Fields.DTHR_ESTORNO.toString()));
		
		return (MamTrgEncAmbulatoriais) executeCriteriaUniqueResult(criteria);
	}
	
}
