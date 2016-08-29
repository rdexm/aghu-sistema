package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.MpmAltaSumario;

public class MamAtestadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAtestados> {

	private static final long serialVersionUID = 1483716067302573509L;	
	
	public List<MamAtestados> listarAtestadosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);

		criteria.add(Restrictions.eq(MamAtestados.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	public MamAtestados buscarAtestadoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);

		criteria.add(Restrictions.eq(MamAtestados.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamAtestados> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * #43023 - C1
	 * @return MamAtestados
	 */
	public List<MamAtestados> obterAtestadoPorNumeroConsulta(Integer numeroConsulta) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteria(criteria);
	}
	
	/**
	 * #45902 - C3
	 * @return MamAtestados
	 */
	public List<MamAtestados> obterAtestadoPorNumeroConsultaDthrValidaNula(Integer numeroConsulta) {
		DetachedCriteria criteria = obterCriteriaAtestadoPorConsultaDthrValidaNula(numeroConsulta);

		return executeCriteria(criteria);
	}

	/**
	 * @param numeroConsulta
	 * @return
	 */
	private DetachedCriteria obterCriteriaAtestadoPorConsultaDthrValidaNula(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.isNull(MamAtestados.Fields.DT_HR_VALIDA_MVTO.toString()));

		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(),new Object[] {DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V}));
		return criteria;
	}
	
	/**
	 * #43023 - CURSOR CUR_ATE P2
	 * @return List<MamAtestados>
	 */
	public List<MamAtestados> obterAtestadoPorNumeroConsultaTipoAtestado(Integer numeroConsulta, Short tasSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(MamAtestados.Fields.TAS_SEQ.toString(), tasSeq));
		criteria.add(Restrictions.isNull(MamAtestados.Fields.DT_HR_VALIDA_MVTO.toString()));

		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(),new Object[] {DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V}));

		return executeCriteria(criteria);
	}
	
	/**
	 * #43023 - CURSOR C_ATESTADO - P1
	 * @param seq
	 * @return AtestadoVO
	 */
	public AtestadoVO obterCursorAtestadosPorSequencial(Long seq) {		
		CursorAtestadosPorSequencialQueryBuilder builder = new CursorAtestadosPorSequencialQueryBuilder();
		return (AtestadoVO) executeCriteriaUniqueResult(builder.build(seq, isOracle()));		
	}

	@Override
	public MamAtestados obterPorChavePrimaria(Object pk) {
		MamAtestados mamAtestados = super.obterPorChavePrimaria(pk);
		return mamAtestados;
	}

	@Override
	public MamAtestados atualizar(MamAtestados elemento) {
		MamAtestados mamAtestados = super.atualizar(elemento);
		return mamAtestados;
	}
	
	public List<MamAtestados> listarAtestadosPorPacienteTipo(Integer atdSeq, Short tasSeq,MpmAltaSumario mpmAltaSumario) {
		DetachedCriteria criteria = obterAtestadosPorPacienteTipo(tasSeq);
		
		if(atdSeq != null){
			criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.ATD_SEQ.toString(), atdSeq));
		}
		else if(mpmAltaSumario != null){
			criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.ASU_APA_ATD_SEQ.toString(), mpmAltaSumario.getId().getApaAtdSeq()));
			criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.ASU_APA_SEQ.toString(), mpmAltaSumario.getId().getApaSeq()));
			criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.ASU_SEQP.toString(), mpmAltaSumario.getId().getSeqp()));
		}

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterAtestadosPorPacienteTipo(Short tasSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class, "ATE");
		criteria.createAlias("ATE." + MamAtestados.Fields.AIP_PACIENTES.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("ATE." + MamAtestados.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);

		//criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.TAS_SEQ.toString(), tasSeq));
		criteria.add(Restrictions.in("ATE." + MamAtestados.Fields.IND_PENDENTE.toString(),
		new Object[] {DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.R,
		DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.P}));
		return criteria;
	}

	/**
	 * #46252 - C1
	 * @return MamAtestados
	 */
	public List<MamAtestados> obterAtestadoPorSumarioAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_SEQP.toString(), seqp));
		criteria.add(Restrictions.isNull(MamAtestados.Fields.DT_HR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(),
				new Object[] {DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.V}));

		return executeCriteria(criteria);
}
	public MamAtestados obterAtestadoEAghCidPorSeq(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class, "ATE");
		criteria.createAlias("ATE." + MamAtestados.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.SEQ.toString(), seq));

		return (MamAtestados) executeCriteriaUniqueResult(criteria);
}

public List<MamAtestados> obterAtestadosPendentes(Integer apaAtdSeq, Integer apaSeq, Short seqp, Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_SEQP.toString(), seqp));
		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(),
				new Object[] {DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.E}));
		if (seq != null) {
			criteria.add(Restrictions.eq(MamAtestados.Fields.SEQ.toString(), seq));
		}

		return executeCriteria(criteria);
	}

	public List<MamAtestados> obterAtestadosCancelamentoAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp, Date dtHrMvto, Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_SEQP.toString(), seqp));
		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(),
				new Object[] {DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.E}));
		
		if (seq != null) {
			Restrictions.eq(MamAtestados.Fields.SEQ.toString(), seq);
}
		
		if (dtHrMvto != null) {
			criteria.add(Restrictions.or(Restrictions.ge(MamAtestados.Fields.DT_HR_CRIACAO.toString(), dtHrMvto), 
					Restrictions.and(Restrictions.isNotNull(MamAtestados.Fields.DT_HR_MVTO.toString()), 
							Restrictions.ge(MamAtestados.Fields.DT_HR_MVTO.toString(), dtHrMvto))));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<MamAtestados> obterAtestadosPorAteSeq(Long ateSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		criteria.add(Restrictions.eq(MamAtestados.Fields.ATE_SEQ.toString(), ateSeq));
		return executeCriteria(criteria);
	}

	/**
	 * #46252 - C1
	 * @return MamAtestados
	 */
	public List<MamAtestados> obterAtestadoPorSumarioAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp,
			Short tasSeq, Object[] situacoes) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MamAtestados.Fields.ASU_SEQP.toString(), seqp));
		criteria.add(Restrictions.isNull(MamAtestados.Fields.DT_HR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(), situacoes));
		
		if (tasSeq != null) {
			criteria.add(Restrictions.eq(MamAtestados.Fields.TAS_SEQ.toString(), tasSeq));
		}

		return executeCriteria(criteria);
	}	

	/**
	 * #46218  - C2
	 * @author adrian.gois
	 */
	public List<MamAtestados> obterAtestadosPorAtdSeqTipo(Integer atdSeq,Short tasSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class,"ATE");
		criteria.createAlias("ATE." + MamAtestados.Fields.AIP_PACIENTES.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ATE."+MamAtestados.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ATE."+MamAtestados.Fields.TAS_SEQ.toString(), tasSeq));
		return executeCriteria(criteria);
	}
	
	/**
	 * #45218 - C3
	 * @author adrian.gois
	 */
	public List<MamAtestados> obterAtestadosPorSumarioAltaTipo(Integer apaAtdSeq, Integer apaSeq, Short seqP, Short tasSeq){ 
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class,"ATE");
		criteria.createAlias("ATE." + MamAtestados.Fields.AIP_PACIENTES.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("ATE."+MamAtestados.Fields.TAS_SEQ.toString(), tasSeq));
		criteria.add(Restrictions.eq("ATE."+MamAtestados.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq("ATE."+MamAtestados.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq("ATE."+MamAtestados.Fields.ASU_SEQP.toString(), seqP));
		return executeCriteria(criteria);
	}
	
	/**
	 * #11942
	 * @param consulta
	 * @return
	 */
	public List<MamAtestados> obterAtestadosConsultaEmAndamento(AacConsultas consulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		criteria.createAlias(MamAtestados.Fields.AAC_CONSULTA.toString(), "CON");
		
		criteria.add(Restrictions.eq("CON."+AacConsultas.Fields.NUMERO.toString(), consulta.getNumero()));
		criteria.add(Restrictions.ge(MamAtestados.Fields.DT_HR_CRIACAO.toString(), consulta.getDthrInicio()));
		criteria.add(Restrictions.eq(MamAtestados.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.P));
		return executeCriteria(criteria);
	}
	
	/**
	 * C2 da estória #11946
	 * @param atdSeq
	 * @param tasSeq
	 * @param mpmAltaSumario
	 * @return lista
	 */
	public List<MamAtestados> listarAtestadosPorPacienteTipoAtendimento(Integer consulta, Short tasSeq) {
		DetachedCriteria criteria = obterAtestadosPorPacienteTipo(tasSeq);
		
		if(consulta != null){
			criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.CON_NUMERO.toString(), consulta));
		}
		
		return executeCriteria(criteria);
	}
	
	/**
	 * C1 #11942 #11943
	 * @param consulta
	 * @return
	 */
	public List<MamAtestados> obterAtestadosDaConsulta(AacConsultas consulta, short tasSeq){
		DetachedCriteria criteria = obterCriteriaAtestadosDaConsulta(consulta,tasSeq);
		criteria.addOrder(Order.desc(MamAtestados.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * C1 #11944
	 * @param consulta
	 * @return
	 */
	public List<MamAtestados> obterAtestadosDaConsultaComCid(AacConsultas consulta, short tasSeq){
		DetachedCriteria criteria = obterCriteriaAtestadosDaConsulta(consulta,tasSeq);
		criteria.createAlias(MamAtestados.Fields.AGH_CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaAtestadosDaConsulta(AacConsultas consulta, short tasSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		criteria.createAlias(MamAtestados.Fields.AIP_PACIENTES.toString(), "PAC");
		
		DominioIndPendenteAmbulatorio [] values = {DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P};
		criteria.add(Restrictions.in(MamAtestados.Fields.IND_PENDENTE.toString(), values));
		criteria.add(Restrictions.eq(MamAtestados.Fields.CON_NUMERO.toString(), consulta.getNumero()));
		criteria.add(Restrictions.eq(MamAtestados.Fields.TAS_SEQ.toString(), tasSeq));
		return criteria;
	}
	
	public List<MamAtestados> buscarAtestado(MamAtestados atestado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAtestados.class);
		criteria.createAlias(MamAtestados.Fields.AAC_CONSULTA.toString(), "CN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CN." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MamAtestados.Fields.MAM_TIPO_ATESTADO.toString(), "MTA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("MTA." + MamTipoAtestado.Fields.DESCRICAO.toString(), atestado.getMamTipoAtestado().getDescricao()));
		criteria.add(Restrictions.eq(MamAtestados.Fields.CON_NUMERO.toString(), atestado.getConsulta().getNumero()));
		criteria.add(Restrictions.eq(MamAtestados.Fields.SEQ.toString(), atestado.getSeq()));
		return executeCriteria(criteria);
	}

	
}
