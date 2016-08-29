package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUsoSumario;
import br.gov.mec.aghu.model.FatMotivoSaidaPaciente;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatSituacaoSaidaPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatSituacaoSaidaPaciente> {

	private static final long serialVersionUID = 3129324726506937654L;

	public List<FatSituacaoSaidaPaciente> listarSituacaoSaidaPaciente(Object objPesquisa, Short mspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class);
		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString());
		
		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString() + "." + FatMotivoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString() + "." + FatMotivoSaidaPaciente.Fields.SEQ.toString(), mspSeq));
		
		if(CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.SEQ.toString(), Short.valueOf((String)objPesquisa)));
		}
		else {
			if(!StringUtils.isEmpty((String)objPesquisa)) {
				criteria.add(Restrictions.ilike(FatMotivoSaidaPaciente.Fields.DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
			}
		}
 
		List<FatSituacaoSaidaPaciente> lista = executeCriteria(criteria);
		
		return lista;
	}


	public Long listarSituacaoSaidaPacienteCount(Object objPesquisa, Short mspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class);
		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString());
		
		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString() + "." + FatMotivoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString() + "." + FatMotivoSaidaPaciente.Fields.SEQ.toString(), mspSeq));
		
		if(CoreUtil.isNumeroShort(objPesquisa)) {
			criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.SEQ.toString(), Short.valueOf((String)objPesquisa)));
		}
		else {
			if(!StringUtils.isEmpty((String)objPesquisa)) {
				criteria.add(Restrictions.ilike(FatMotivoSaidaPaciente.Fields.DESCRICAO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
			}
		}
 
		return this.executeCriteriaCount(criteria);
	}

	public FatSituacaoSaidaPaciente obterFatMotivoSaidaPacientePorCodigoSusSituacaoAberto(final Byte mspCodigoSus, final Byte siaCodigoSus, final Short ...seqsMotivoSaidaPac){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class,"SSP");
		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), "MSP");
		
		criteria.add(Restrictions.in("MSP."+FatMotivoSaidaPaciente.Fields.SEQ.toString(), seqsMotivoSaidaPac));
		criteria.add(Restrictions.eq("MSP."+FatMotivoSaidaPaciente.Fields.CODIGO_SUS.toString(), mspCodigoSus)); 

		criteria.add(Restrictions.eq("SSP."+FatSituacaoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("SSP."+FatSituacaoSaidaPaciente.Fields.CODIGO_SUS.toString(), siaCodigoSus));
		
		
		return (FatSituacaoSaidaPaciente) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Recupera lista paginada para a tela Detalhar Motivo de Saída de Pacientes
	 * 
	 * @param firstResult {@link Integer}
	 * @param maxResult {@link Integer}
	 * @param orderProperty {@link String}
	 * @param asc {@link Boolean}
	 * @param filtro {@link FatMotivoSaidaPaciente}
	 * @return {@link List} de {@link FatSituacaoSaidaPaciente}
	 */
	public List<FatSituacaoSaidaPaciente> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatMotivoSaidaPaciente filtro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class);

		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(),
				FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString());

		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MSP_SEQ.toString(), filtro.getSeq()));

		if (orderProperty == null) {
		criteria.addOrder(Order.asc(FatSituacaoSaidaPaciente.Fields.SEQ.toString()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty,	asc);
	}
	
	/**
	 * Recupera o contador para a tela Detalhar Motivo de Saída de Pacientes
	 * 
	 * @param filtro {@link FatMotivoSaidaPaciente}
	 * @return {@link Long}
	 */
	public Long recuperarCount(FatMotivoSaidaPaciente filtro) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class);

		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(),
				FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString());

		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MSP_SEQ.toString(), filtro.getSeq()));

		return executeCriteriaCount(criteria);
	}


	public List<FatSituacaoSaidaPaciente> listarEstadoClinicoPacienteAtivos(Short idade, DominioSexoDeterminante sexo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class, "SSP");
		
		criteria.createAlias("SSP."+FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), "MSP", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MSP."+FatMotivoSaidaPaciente.Fields.SITUACAO.toString() ,DominioSituacao.A));
		criteria.add(Restrictions.eq("SSP."+FatSituacaoSaidaPaciente.Fields.SITUACAO.toString() ,DominioSituacao.A));
		criteria.add(Restrictions.eq("SSP."+FatSituacaoSaidaPaciente.Fields.TIPO_USO.toString() ,DominioTipoUsoSumario.A));
		criteria.add(Restrictions.isNotNull("SSP."+FatSituacaoSaidaPaciente.Fields.DESCRICAO_EDITADA.toString()));
		criteria.add(Restrictions.in("SSP." + FatSituacaoSaidaPaciente.Fields.SEXO.toString(), 
				new DominioSexoDeterminante[]{DominioSexoDeterminante.Q, sexo}));
		criteria.add(Restrictions.le("SSP." + FatSituacaoSaidaPaciente.Fields.IDADE_MINIMA.toString(), idade));
		criteria.add(Restrictions.ge("SSP." + FatSituacaoSaidaPaciente.Fields.IDADE_MAXIMA.toString(), idade));
		criteria.addOrder(Order.asc("SSP." + FatSituacaoSaidaPaciente.Fields.DESCRICAO_EDITADA.toString()));
		
		List<FatSituacaoSaidaPaciente> lista = executeCriteria(criteria);
		
		return lista;
	
	}
	
	public List<FatSituacaoSaidaPaciente> listarEstadoClinicoPacienteObitoAtivos(Short idade, DominioSexoDeterminante sexo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class, "SSP");
		
		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(), "MSP");
		criteria.add(Restrictions.eq("SSP." + FatSituacaoSaidaPaciente.Fields.SITUACAO.toString() ,DominioSituacao.A));
		criteria.add(Restrictions.eq("MSP." + FatMotivoSaidaPaciente.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("SSP." + FatSituacaoSaidaPaciente.Fields.TIPO_USO.toString(), DominioTipoUsoSumario.O));
		criteria.add(Restrictions.isNotNull("SSP." + FatSituacaoSaidaPaciente.Fields.DESCRICAO_EDITADA.toString()));
		criteria.add(Restrictions.in("SSP." + FatSituacaoSaidaPaciente.Fields.SEXO.toString(), 
				new DominioSexoDeterminante[]{DominioSexoDeterminante.Q, sexo}));
		criteria.add(Restrictions.le("SSP." + FatSituacaoSaidaPaciente.Fields.IDADE_MINIMA.toString(), idade));
		criteria.add(Restrictions.ge("SSP." + FatSituacaoSaidaPaciente.Fields.IDADE_MAXIMA.toString(), idade));
		criteria.addOrder(Order.asc("SSP." + FatSituacaoSaidaPaciente.Fields.DESCRICAO_EDITADA.toString()));
		
		List<FatSituacaoSaidaPaciente> lista = executeCriteria(criteria);
		
		return lista;
	
	}
	
	/**
	 * Consulta por {@link FatSituacaoSaidaPaciente} vinculados ao {@link FatMotivoSaidaPaciente}
	 * 
	 * @param entity {@link FatMotivoSaidaPaciente}
	 * @return {@link List} de {@link FatSituacaoSaidaPaciente}
	 */
	public List<FatSituacaoSaidaPaciente> listarFatSituacaoSaidaPacientePorFatMotivoSaidaPaciente(FatMotivoSaidaPaciente entity) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class);

		criteria.createAlias(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString(),
				FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString());

		criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MOTIVO_SAIDA_PACIENTE.toString()
				+ "."
				+ FatMotivoSaidaPaciente.Fields.SEQ.toString(), entity.getSeq()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem ultimo valor de registro de {@link FatSituacaoSaidaPaciente} 
	 * cadastrado para um determinado {@link FatMotivoSaidaPaciente}
	 * 
	 * @param mspSeq {@link Short} seq de {@link FatMotivoSaidaPaciente}
	 * @return {@link Short} máx seq de {@link FatSituacaoSaidaPaciente} + 1
	 */
	public Short obterMaxSeqFatSituacaoSaidaPaciente(Short mspSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSituacaoSaidaPaciente.class);
		
		// Com esta restrição, para cada novo Motivo, 
		// se inicia nova sequencia de valores para as seq de Situações 
		// criteria.add(Restrictions.eq(FatSituacaoSaidaPaciente.Fields.MSP_SEQ.toString(), mspSeq));
		
		criteria.setProjection(Projections.max(FatSituacaoSaidaPaciente.Fields.SEQ.toString()));
		
		Short maxSeq = (Short) this.executeCriteriaUniqueResult(criteria);
		
		if (maxSeq != null) {
			
			return Short.valueOf(String.valueOf(maxSeq + 1));
		}
		return 1;
	}
}
