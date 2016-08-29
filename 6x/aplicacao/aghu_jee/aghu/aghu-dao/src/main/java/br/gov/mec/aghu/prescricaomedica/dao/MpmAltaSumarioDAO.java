package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.VAfaMdtoDescricao;
import br.gov.mec.aghu.paciente.prontuario.vo.AltaObitoSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * 
 * @modulo prescricaomedica
 * @author lalegre
 * 
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class MpmAltaSumarioDAO extends BaseDao<MpmAltaSumario> {

	private static final long serialVersionUID = -7977117431481708889L;

	@Override
	protected void obterValorSequencialId(MpmAltaSumario elemento) {
		if (elemento == null || elemento.getAtendimento() == null || elemento.getAtendimentoPaciente() == null) {
			throw new IllegalArgumentException("AltaSumario, Atendimento ou AtendimentoPaciente nao foi informado corretamente.");
		}
		
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		id.setApaAtdSeq(elemento.getAtendimento().getSeq());
		id.setApaSeq(elemento.getAtendimentoPaciente().getId().getSeq());
		Integer seqp = buscaMaxSeqMpmAltaSumario(id).intValue() + 1;
		id.setSeqp(seqp.shortValue());
		
		elemento.setId(id);
	}
	
	/**
	 * Busca o maior sequencial associado a MpmAltaSumario
	 * @param id
	 * @return
	 */
	private Short buscaMaxSeqMpmAltaSumario(MpmAltaSumarioId id) {
		
		Short returnValue = null;
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("select max(altaSumario.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmAltaSumario.class.getName()).append(" altaSumario");
		sql.append(" where altaSumario.").append(MpmAltaSumario.Fields.APA_ATD_SEQ.toString()).append(" = :apaAtdSeq ");
		sql.append(" and altaSumario.").append(MpmAltaSumario.Fields.APA_SEQ.toString()).append(" = :apaSeq ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("apaAtdSeq", id.getApaAtdSeq());
		query.setParameter("apaSeq", id.getApaSeq());			
			
		Object maxSeq = query.getSingleResult();
		
		if (maxSeq == null) {
			
			Integer val = 0;
			returnValue = val.shortValue();
			
		} else {
			
			returnValue = (Short) maxSeq; 
			
		}
		
		return returnValue;
	}

	/**
	 * Retorna sumário de alta ativo para o atendimento fornecido.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public MpmAltaSumario obterAltaSumarios(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}
	

	public Long obterQtAltasSumario(final Integer atdSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		
		criteria.setProjection(Projections.count(MpmAltaSumario.Fields.SEQP.toString()));
		
		Long result = (Long) executeCriteriaUniqueResult(criteria);
		
		return result;
	}

	public MpmAltaSumario pesquisarAltaSumarios(MpmAltaSumarioId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.createAlias(MpmAltaSumario.Fields.CONVENIO_SAUDE_PLANO.toString(), "CNP");
		criteria.createAlias("CNP."+ FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "CNV");
		criteria.createAlias(MpmAltaSumario.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.ID.toString(), id));

		return (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}

	public List<MpmAltaSumario> pesquisarAltaSumarios(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(MpmAltaSumario.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna sumário de alta ativo para o atendimento fornecido.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public MpmAltaSumario obterAltaSumariosAtivoConcluido(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.or(Restrictions.eq(MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.ALT),Restrictions.eq(MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.OBT)));
		
		return (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Retorna sumário de alta para o atendimento fornecido.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmAltaSumario> pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.IND_TIPO.toString(), 
				new DominioIndTipoAltaSumarios[] { DominioIndTipoAltaSumarios.ALT, DominioIndTipoAltaSumarios.OBT }));
		
		return executeCriteria(criteria);
	}
	
	public List<MpmAltaSumario> pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(List<Integer> atdSeqs) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);

		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.IND_TIPO.toString(), 
				new DominioIndTipoAltaSumarios[] { DominioIndTipoAltaSumarios.ALT, DominioIndTipoAltaSumarios.OBT }));
		
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeqs));
		
		return executeCriteria(criteria);
	}
	

	/**
	 * Retorna uma nova referência ao registro informado por parâmetro com os
	 * valores atuais do banco, utilizando o comando evict para não buscar do
	 * cache.
	 * 
	 * @param mpmAltaSumario
	 * @return
	 */
	public MpmAltaSumarioVO obterAltaSumarioOriginal(MpmAltaSumario mpmAltaSumario) {
		if (mpmAltaSumario == null || mpmAltaSumario.getId() == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		StringBuilder hql = new StringBuilder(100);
		hql.append("select o.").append(MpmAltaSumario.Fields.IND_CONCLUIDO.toString());
		hql.append(", o.").append(MpmAltaSumario.Fields.DTHR_ALTA.toString());
		hql.append(", o.").append(MpmAltaSumario.Fields.ID.toString());
		hql.append(", o.").append(MpmAltaSumario.Fields.TRANSFCONCLUIDA.toString());
		hql.append(" from ").append(MpmAltaSumario.class.getSimpleName()).append(" o ");
		
//		hql.append("left outer join o.").append(MpmAltaSumario.Fields.MATERIAL.toString());
//		hql.append(" left outer join o.").append(MpmAltaSumario.Fields.PCI_SEQ.toString());
//		hql.append(" left outer join o.").append(MpmAltaSumario.Fields.PED_SEQ.toString());
		
		hql.append(" where o.").append(MpmAltaSumario.Fields.ID.toString()).append(" = :sumarioAltaId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("sumarioAltaId", mpmAltaSumario.getId());
		
		List<Object[]> lista = query.getResultList();
		MpmAltaSumarioVO returnValue = null;
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new MpmAltaSumarioVO();
			for (Object[] listFileds : lista) {
				returnValue.setConcluido( (DominioIndConcluido) listFileds[0]);
				returnValue.setDthrAlta( (Date) listFileds[1]);
				returnValue.setId( (MpmAltaSumarioId) listFileds[2]);
				returnValue.setTransfConcluida( (Boolean) listFileds[3]);
			}
		}		
		
		return returnValue;
		
	}

	/**
	 * Obtém um alta sumário pelo seu ID.
	 * 
	 * bsoliveira 27/10/2010
	 * 
	 * @param {Integer} apaAtdSeq
	 * @param {Integer} apaSeq
	 * @param {Short} seqp
	 * 
	 * @return MpmAltaSumario
	 */
	public MpmAltaSumario obterAltaSumarioPeloId(Integer apaAtdSeq, Integer apaSeq, Short seqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		getRestrictionsAltaSumarioPorId(apaSeq, apaAtdSeq, seqp, criteria);
		return (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * Obtém um alta sumário pelo seu ID e pela situação.
	 * 
	 * bsoliveira 29/10/2010
	 * 
	 * @param {MpmAltaSumarioId} altaSumarioId
	 * @param {DominioSituacao} situacao
	 * 
	 * @return MpmAltaSumario
	 */
	public MpmAltaSumario obterAltaSumarioPeloIdESituacao(MpmAltaSumarioId altaSumarioId, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.ID.toString(), altaSumarioId));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(),
				situacao));
		MpmAltaSumario retorno = (MpmAltaSumario) this
				.executeCriteriaUniqueResult(criteria);

		return retorno;

	}
	
	/**
	 * Recupera o sumario alta
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @return
	 */
	public MpmAltaSumario recuperarSumarioAlta(Integer altanAtdSeq, Integer altanApaSeq) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.createAlias(MpmAltaSumario.Fields.PACIENTE.toString(), MpmAltaSumario.Fields.PACIENTE.toString(), JoinType.LEFT_OUTER_JOIN);
		
		MpmAltaSumario altaSumario = null;
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.ne(MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
		criteria.addOrder(Order.desc(MpmAltaSumario.Fields.APA_ATD_SEQ.toString()));
		criteria.addOrder(Order.desc(MpmAltaSumario.Fields.SEQP.toString()));
		
		List<MpmAltaSumario> retorno = executeCriteria(criteria);
		
		if (retorno != null && retorno.size() > 0) {
			
			altaSumario = retorno.get(0);
			
		}
		
		return altaSumario;
	}
	
	/**
	 * VERIFICA SE O SUMÁRIO É DA EMERGENCIA
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param seqp
	 * @return
	 */
	public boolean verificarEmergencia(Integer altanAtdSeq, Integer altanApaSeq, Short seqp) throws ApplicationBusinessException {
		MpmAltaSumario altaSumario = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		getRestrictionsAltaSumarioPorId(altanAtdSeq, altanApaSeq, seqp, criteria);
		altaSumario = (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
		
		if (altaSumario != null && altaSumario.getEmergencia()) {
			return true;
		}
		
		return false;
	}

	private void getRestrictionsAltaSumarioPorId(Integer altanAtdSeq,
			Integer altanApaSeq, Short seqp, DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.SEQP.toString(), seqp));
	}
	
	/**
	 * Consulta codigo de material vinculado ao sumário ativo.
	 * 
	 * @param {Integer} atdSeq Seq do atendimento.
	 * @return List<Object[]>.
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarAltaSumarioMaterialPorAtendimento(
			Integer atdSeq) {
		
		StringBuilder hql = new StringBuilder(300);
		hql.append(" select pfa."
				).append( MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString() ).append( ", ");
		hql.append(" pfa."
				).append( MpmAltaPrincFarmaco.Fields.MED_MAT_CODIGO.toString() ).append( ", ");
		hql.append(" b."
				).append( VAfaMdtoDescricao.Fields.MED_DESCRICAO_CODIGO.toString() ).append(' ');
		hql.append(" from ");
		hql.append(" MpmAltaSumario asu, VAfaMdtoDescricao b, MpmAltaPrincFarmaco pfa ");
		hql.append(" where ");
		hql.append(" asu."
				).append( MpmAltaSumario.Fields.APA_ATD_SEQ.toString()
				).append( " = pfa."
				).append( MpmAltaPrincFarmaco.Fields.ASU_APA_ATD_SEQ.toString() ).append( " AND ");
		hql.append(" asu."
				).append( MpmAltaSumario.Fields.APA_SEQ.toString()
				).append( " = pfa."
				).append( MpmAltaPrincFarmaco.Fields.ASU_APA_SEQ.toString() ).append( " AND ");
		hql.append(" asu."
				).append( MpmAltaSumario.Fields.SEQP.toString()
				).append( " = pfa."
				).append( MpmAltaPrincFarmaco.Fields.ASU_SEQP.toString() ).append( " AND ");
		hql.append(" pfa."
				).append( MpmAltaPrincFarmaco.Fields.MED_MAT_CODIGO.toString()
				).append( " = b."
				).append( VAfaMdtoDescricao.Fields.MED_MAT_CODIGO.toString() ).append( " AND ");
		hql.append(" asu."
				).append( MpmAltaSumario.Fields.ATD_SEQ.toString()
				).append( " = :pAtdSeq AND ");
		hql.append(" asu."
				).append( MpmAltaSumario.Fields.IND_SITUACAO.toString()
				).append( " = :pIndSituacao ");

		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("pAtdSeq", atdSeq);
		query.setParameter("pIndSituacao", DominioSituacao.A);

		return query.getResultList();

	}
	
	/**
	 * ORADB CURSOR c_sumario
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List executarCursorSumario(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSumarioAlta.class);
		criteria.add(Restrictions.eq(MpmSumarioAlta.Fields.SEQ.toString(), atdSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmSumarioAlta.Fields.DATA_ALTA.toString()));
		p.add(Projections.property(MpmSumarioAlta.Fields.MOTIVO_ALTA_MEDICA.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}
	

	/**
	 * ORADB CURSOR c_alta_sumario
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List executarCursorAltaSumario(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmAltaSumario.Fields.IND_CONCLUIDO.toString()));
		criteria.setProjection(p);

		criteria.addOrder(Order.desc(MpmAltaSumario.Fields.SEQP.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Obtém um lista de altas sumário concluídos pelo seu apa_atd_seq (atendimento).
	 * 
	 * @param {Integer} apaAtdSeq
	 * 
	 * @return List<MpmAltaSumario>
	 */
	public MpmAltaSumario obterAltaSumarioConcluidaPeloAtendimento(Integer apaAtdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO
				.toString(), DominioIndConcluido.S));
		
		return (MpmAltaSumario)this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtém um lista de altas sumário concluídos pelo seu apa_atd_seq (atendimento).
	 * 
	 * @param {Integer} apaAtdSeq
	 * 
	 * @return List<MpmAltaSumario>
	 */
	public MpmAltaSumario obterAltaSumarioConcluidaEAtivo(Integer altanAtdSeq, Integer altanApaSeq, Short seqp) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaSumario.class);
		getRestrictionsAltaSumarioPorId(altanApaSeq, altanAtdSeq, seqp, criteria);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO
				.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));
		
		return (MpmAltaSumario)this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtém um alta sumário pelo seu ID.
	 * 
	 * bsoliveira 27/10/2010
	 * 
	 * @param {Integer} apaAtdSeq
	 * @param {Integer} apaSeq
	 * @param {Short} seqp
	 * 
	 * @return MpmAltaSumario
	 */
	public MpmAltaSumario obterAltaSumarioAtivo(Integer apaAtdSeq, Integer apaSeq, Short seqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		getRestrictionsAltaSumarioPorId(apaSeq, apaAtdSeq, seqp, criteria);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
		
	}

	/**
	 * Verificar se o atendimento possui altas sumário concluídos.
	 * 
	 * @param {Integer} apaAtdSeq
	 * 
	 * @return List<MpmAltaSumario>
	 */
	public boolean existeAltaSumarioConcluidaPorAtendimento(Integer apaAtdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.IND_CONCLUIDO.toString(),
				DominioIndConcluido.S));

		return executeCriteriaCount(criteria) > 0 ? true : false;
	}
	
	public DominioSimNao verificarAltaSumarioObito(AghAtendimentos atendimento) {

		List<MpmAltaSumario> list = new ArrayList<MpmAltaSumario>();

		DetachedCriteria criteria = this.montarAltaSumarioConcluidoAtivo();
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.IND_TIPO.toString(),
				new DominioIndTipoAltaSumarios[] {
						DominioIndTipoAltaSumarios.ALT,
						DominioIndTipoAltaSumarios.OBT }));
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.ATENDIMENTO.toString(), atendimento));

		list = executeCriteria(criteria);
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			return DominioIndTipoAltaSumarios.OBT.equals(list.get(0).getTipo()) ? DominioSimNao.S
					: DominioSimNao.N;
		}

	}
	
	/**
	 * Retorna a altaSumário pelo atendimento
	 * @param atendimento
	 * @return
	 */
	public MpmAltaSumario obterAltaSumarioPorAtendimento(AghAtendimentos atendimento){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		
		return (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria montarAltaSumarioConcluidoAtivo() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.IND_CONCLUIDO.toString(),
				DominioIndConcluido.S));
		criteria.add(Restrictions.eq(
				MpmAltaSumario.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		return criteria;
	}
	
	public MpmAltaSumario pesquisarAltaSumarioConcluido(Integer apaAtdSeq) {
		DetachedCriteria cri = DetachedCriteria.forClass(MpmAltaSumario.class);

		cri.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(),
				apaAtdSeq));
		cri.add(Restrictions.eq(
				MpmAltaSumario.Fields.IND_CONCLUIDO.toString(),
				DominioIndConcluido.S));

		return (MpmAltaSumario) executeCriteriaUniqueResult(cri);
	}

	public List<MpmAltaSumario> listarAltasSumarioPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.ATD_SEQ.toString(), atdSeq));

		return executeCriteria(criteria);
	}
	
	public List<Object> executaCursorAltaSumarioAtendimentoEnforceRN(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(MpmAltaSumario.Fields.SEQP.toString()));
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(MpmAltaSumario.Fields.APA_SEQ.toString()));
		p.add(Projections.property(MpmAltaSumario.Fields.SEQP.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}

	public DadosAltaSumarioVO buscarDadosAltaSumario(Integer internacaoSeq) {
		DadosAltaSumarioVO dados = null;

		StringBuilder hql = new StringBuilder(400);
		hql.append(" SELECT ");
		hql.append(" new br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO( ");
		hql.append(" int.");
		hql.append(AinInternacao.Fields.SEQ.toString() ).append( ", ");
		hql.append(" atd.");
		hql.append(AghAtendimentos.Fields.DTHR_INICIO.toString() ).append( ", ");
		hql.append(" asu.");
		hql.append(MpmAltaSumario.Fields.DTHR_ALTA.toString() ).append( ", ");
		hql.append(" asu.");
		hql.append(MpmAltaSumario.Fields.IND_TIPO.toString() ).append( ", ");
		hql.append(" mot.");
		hql.append(MpmMotivoAltaMedica.Fields.SEQ.toString() ).append( ", ");
		hql.append(" tam.");
		hql.append(AinTiposAltaMedica.Fields.DESCRICAO.toString() ).append( ", ");
		hql.append(" tam.");
		hql.append(AinTiposAltaMedica.Fields.CODIGO.toString() ).append( ", ");
		hql.append(" map.");
		hql.append(MpmAltaPlano.Fields.COMPL_PLANO_POS_ALTA.toString() ).append( ", ");
		hql.append(" map.");
		hql.append(MpmAltaPlano.Fields.DESC_PLANO_POS_ALTA.toString());
		hql.append(" ) ");
		hql.append(" FROM ");

		hql.append(" MpmAltaSumario asu ");
		hql.append(" join asu." ).append( MpmAltaSumario.Fields.ATENDIMENTO.toString() ).append( " atd ");
		hql.append(" join atd." ).append( AghAtendimentos.Fields.INTERNACAO.toString() ).append( " int ");
		hql.append(" left join asu." ).append( MpmAltaSumario.Fields.ALTA_MOTIVOS.toString() ).append( " amt ");
		hql.append(" left join asu." ).append( MpmAltaSumario.Fields.ALTA_PLANOS.toString() ).append( " map ");
		hql.append(" left join amt." ).append( MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS.toString() ).append( " mot ");
		hql.append(" left join mot." ).append( MpmMotivoAltaMedica.Fields.TIPOS_ALTAS_MEDICAS.toString() ).append( " tam ");

		hql.append(" WHERE ");

		hql.append(" int." ).append( AinInternacao.Fields.SEQ.toString() ).append( " = :internacaoSeq ");
		hql.append(" AND asu." ).append( MpmAltaSumario.Fields.IND_CONCLUIDO.toString() ).append( " = :indConcluido ");

		javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("internacaoSeq", internacaoSeq);
		query.setParameter("indConcluido", DominioIndConcluido.S);
		query.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<DadosAltaSumarioVO> lista = query.getResultList();
		if (lista != null && lista.size() > 0) {
			dados = lista.get(0);
		}
		return dados;
	}
	public List<MpmAltaSumario> listarAltasSumario(Integer atdSeq, DominioSituacao situacao,
			DominioIndTipoAltaSumarios[] tiposAltaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(), situacao));
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.IND_TIPO.toString(), tiposAltaSumario));

		return executeCriteria(criteria);
	}

	public Date obterDataAltaPorInternacao(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.setProjection(Projections.property(MpmAltaSumario.Fields.DTHR_ALTA.toString()));
		criteria.createAlias(MpmAltaSumario.Fields.ATENDIMENTO.toString(), "ATD");

		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public List<MpmAltaSumario> listarAltasSumarioPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);

		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<MpmAltaSumario> listarAltasSumarios(Integer pacCodigo, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.ATENDIMENTO.toString(), atendimento));

		return executeCriteria(criteria);
	}

	public Boolean verificarSumarioTransfPacInternacao(Integer atdSeq, Short seqpAltaSumario) {
		DetachedCriteria criteria = getCriteriaAltaSumarioTransferenciaByAtendimento(atdSeq, seqpAltaSumario);
		List<MamAltaSumario> list = executeCriteria(criteria); 
		if( list != null && !list.isEmpty()){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}

	private DetachedCriteria getCriteriaAltaSumarioTransferenciaByAtendimento(
			Integer atdSeq, Short seqpAltaSumario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		if(seqpAltaSumario != null){
			criteria.add(Restrictions.eq(MpmAltaSumario.Fields.SEQP.toString(), seqpAltaSumario));
		}
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.TRANSFCONCLUIDA.toString(), Boolean.TRUE));
		return criteria;
	}

	public List<MpmAltaSumario> pesquisarAltaSumarioTransferencia(
			AghAtendimentos atendimento, Short seqpAltaSumario) {
		DetachedCriteria criteria = getCriteriaAltaSumarioTransferenciaByAtendimento(atendimento.getSeq(), seqpAltaSumario);
		return executeCriteria(criteria);
	}
		
	public Long buscaAltaSumarioTransferenciaCount(Integer apaAtdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.TRANSFCONCLUIDA.toString(), Boolean.TRUE));
		return executeCriteriaCount(criteria);
	}
	public List<MpmAltaSumario> obterTrfDestinoComAltaSumarioEPaciente(Integer apaAtdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
	//	criteria.createAlias(MpmTrfDestino.Fields.ALTA_SUMARIO.toString(), "ASU");
	//	criteria.createAlias("ASU."+MpmAltaSumario.Fields.PACIENTE.toString(), "PAC");
		
	//	if(apaAtdSeq != null){
	//		criteria.add(Restrictions.eq(MpmTrfDestino.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));		
	//	}
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.TRANSFCONCLUIDA.toString(), Boolean.TRUE));
//		criteria.add(Restrictions.eq("ASU."+MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.TRF));
//		criteria.add(Restrictions.eq("ASU."+MpmAltaSumario.Fields.TRANSFCONCLUIDA.toString(), Boolean.TRUE));	
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna sumário de alta e obito para o atendimento fornecido.
	 * Q_ALTA_SUMARIO
	 * @param atdSeq
	 * @param dominioSituacao
	 * @param dominioIndConcluido
	 * @return
	 */
	public List<AltaObitoSumarioVO> pesquisarAltaObitoSumariosAtdSeqSituacaoIndConcluido(Integer atdSeq,
			DominioSituacao dominioSituacao, DominioIndConcluido dominioIndConcluido) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.createAlias(MpmAltaSumario.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias(MpmAltaSumario.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias(MpmAltaSumario.Fields.ALTA_MOTIVOS.toString(), "MTV");
		criteria.createAlias("MTV."+MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS, "MAM");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MAM." + MpmMotivoAltaMedica.Fields.IND_OBITO.toString()), AltaObitoSumarioVO.Fields.IND_OBITO.toString())
				.add(Projections.property(MpmAltaSumario.Fields.APA_SEQ.toString()), AltaObitoSumarioVO.Fields.APA_SEQ.toString())
				.add(Projections.property(MpmAltaSumario.Fields.APA_ATD_SEQ.toString()), AltaObitoSumarioVO.Fields.APA_ATD_SEQ.toString())
				.add(Projections.property(MpmAltaSumario.Fields.SEQP.toString()), AltaObitoSumarioVO.Fields.SEQP.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.LTO_LTO_ID.toString()), AltaObitoSumarioVO.Fields.LTO_LTO_ID.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), AltaObitoSumarioVO.Fields.NOME.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), AltaObitoSumarioVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.QRT_NUMERO.toString()), AltaObitoSumarioVO.Fields.QRT_NUMERO.toString())
				.add(Projections.property(MpmAltaSumario.Fields.UNF_SEQ.toString()), AltaObitoSumarioVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_FIM.toString()), AltaObitoSumarioVO.Fields.DTHR_FIM.toString())
		);
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_SITUACAO.toString(), dominioSituacao));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), dominioIndConcluido));
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.IND_TIPO.toString(), 
				new DominioIndTipoAltaSumarios[] { DominioIndTipoAltaSumarios.ALT, DominioIndTipoAltaSumarios.OBT }));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AltaObitoSumarioVO.class));
		return executeCriteria(criteria);
	}

	//Utilizado Hql devido ao relacionamento entre tabelas sem mapeamento
	//#26194 C12
	public List<MamItemReceituario> listarItemReceituarioPorAltaSumario(Integer atdSeq, Integer apaSeq, Short seqp) {
		StringBuilder hql = new StringBuilder(200);
		hql.append("select ire ")
		.append("from MpmAltaSumario asu, ")
		.append("MamReceituarios rct, ")
		.append("MamItemReceituario ire ")
		.append("where rct.").append( MamReceituarios.Fields.SEQ.toString() ).append(" = ire.").append( MamItemReceituario.Fields.RECEITUARIO_SEQ.toString() ).append(' ')
		.append("and rct.").append( MamReceituarios.Fields.REGISTRO_SEQ.toString() ).append(" = asu.").append( MpmAltaSumario.Fields.REGISTRO_SEQ.toString() ).append(' ')
		.append("and asu.").append( MpmAltaSumario.Fields.APA_ATD_SEQ.toString() ).append(" = :apaAtdSeq ")
		.append("and asu.").append( MpmAltaSumario.Fields.APA_SEQ.toString() ).append(" = :apaSeq ")
		.append("and asu.").append( MpmAltaSumario.Fields.SEQP.toString() ).append(" = :seqp ");
		
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("apaAtdSeq", atdSeq);
		query.setParameter("apaSeq", apaSeq);
		query.setParameter("seqp", seqp);

		return query.getResultList();
	}
	
	public MpmAltaSumario obterServidorAltaSumariosConcluidoAltaEObitoPorAtdSeq(
			Integer atdSeq) {

		MpmAltaSumario altaSumario = null;

		StringBuilder hql = new StringBuilder(220);
		hql.append(" select asu ");
		hql.append(" from MpmAltaSumario asu, ");
		hql.append(" AghAtendimentoPacientes apa, ");
		hql.append(" AghAtendimentos atd ");
		hql.append(" where atd." + AghAtendimentos.Fields.SEQ.toString()
				+ " = :atdSeq ");
		hql.append(" and apa."
				+ AghAtendimentoPacientes.Fields.ATENDIMENTO.toString()
				+ " = atd." + AghAtendimentos.Fields.SEQ.toString());
		hql.append(" and asu." + MpmAltaSumario.Fields.ATENDIMENTO.toString()
				+ " = apa."
				+ AghAtendimentoPacientes.Fields.ATENDIMENTO.toString());

		hql.append(" and asu." + MpmAltaSumario.Fields.IND_SITUACAO.toString()
				+ " = 'A' ");
		hql.append(" and asu." + MpmAltaSumario.Fields.IND_TIPO.toString()
				+ " in ('ALT','OBT') ");
		hql.append(" and asu." + MpmAltaSumario.Fields.IND_CONCLUIDO.toString()
				+ " = 'S' ");
		hql.append(" and asu." + MpmAltaSumario.Fields.DTHR_ESTORNO.toString()
				+ " is null ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);

		@SuppressWarnings("unchecked")
		List<MpmAltaSumario> retorno = query.getResultList();

		if (retorno != null && retorno.size() > 0) {

			altaSumario = retorno.get(0);

		}

		return altaSumario;

	}
		
	/**
	 * ORADB: MAMC_GET_MAM_PLE_SQ1_NEXTVAL
	 */
	public Integer getNextValPleSeq(){
		return super.getNextVal(SequenceID.MAM_PLE_SQ1);
	}
	
	/**
	 * #38994 - Serviço que retorna altas por numero da consulta
	 * @param conNumero
	 * @return
	 */
	public MpmAltaSumario pesquisarAltaSumariosPorNumeroConsulta(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class, "ASU");
		criteria.createAlias(MpmAltaSumario.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("ATD."+ AghAtendimentos.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		return  (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #39007 - Serviço que retorna alta sumario por atendimento.
	 * @param atdSeq
	 * @return
	 */
	public MpmAltaSumario obterMpmAltaSumarioPorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class, "ASU");
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.in("ASU."+ MpmAltaSumario.Fields.IND_TIPO.toString(), Arrays.asList(DominioIndTipoAltaSumarios.ALT, DominioIndTipoAltaSumarios.OBT)));
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return  (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}
	/**
	 * #39010 - Busca alta de sumário concluído
	 * @param atdSeq
	 * @return
	 */
	public MpmAltaSumario obterMpmAltaSumarioConcluidoPorAtendimento(Integer atdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class, "ASU");
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("ASU."+ MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		return  (MpmAltaSumario) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean verificarAltaSumariosConcluido(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		
		return executeCriteriaCount(criteria) > 0;
	}

	public boolean validarSumarioConcluidoAltaEObitoPorAtdSeq(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaSumario.class);
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.in(MpmAltaSumario.Fields.IND_TIPO.toString(), 
				new DominioIndTipoAltaSumarios[] { DominioIndTipoAltaSumarios.ALT, DominioIndTipoAltaSumarios.OBT }));
		criteria.add(Restrictions.isNull(MpmAltaSumario.Fields.DTHR_ESTORNO.toString()));
		
		List<MpmAltaSumario> result = executeCriteria(criteria);
		
		return result != null & !result.isEmpty();
	}
}