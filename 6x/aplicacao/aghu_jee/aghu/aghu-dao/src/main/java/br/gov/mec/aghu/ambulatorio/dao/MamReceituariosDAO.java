package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;

public class MamReceituariosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamReceituarios> {

	private static final long serialVersionUID = -320305037227717771L;

	private static final Log LOG = LogFactory.getLog(MamReceituariosDAO.class);

	/**
	 * Retornar a lista de MamReceituarios pertencentes a Alta Sumário cujo o
	 * movimento não esteja validado e o status seja 'V - Validado' ou 'P -
	 * Pendente'
	 * 
	 * @param altaSumario
	 * @return
	 */
	public List<MamReceituarios> buscarReceituariosPorAltaSumario(
			MpmAltaSumario altaSumario) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamReceituarios.class);

		criteria.add(Restrictions
				.isNull(MamReceituarios.Fields.DATA_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(
				MamReceituarios.Fields.IND_PENDENTE.toString(),
				new DominioIndPendenteAmbulatorio[] {
						DominioIndPendenteAmbulatorio.V,
						DominioIndPendenteAmbulatorio.P }));
		criteria.add(Restrictions.eq(
				MamReceituarios.Fields.MPM_ALTA_SUMARIO.toString(), altaSumario));
		return executeCriteria(criteria);
	}
	
	public List<MamReceituarios> pesquisarReceituariosPorConsulta(AacConsultas consulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.createAlias(MamReceituarios.Fields.CONSULTA.toString(), "CON");
		criteria.add(Restrictions.eq("CON.".concat(AacConsultas.Fields.NUMERO.toString()), consulta.getNumero()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retornar a lista de MamReceituarios pertencentes a Alta Sumário cujo o
	 * movimento não esteja assinado, status seja 'R - Validado', 'P -
	 * Pendente' ou 'E - Excluído'
	 * 
	 * @param {MpmAltaSumario} altaSumario
	 * @param {MamReceituarios} receita
	 * @return List<MamReceituarios>
	 */
	public List<MamReceituarios> buscarReceituariosPorAltaSumarioNaoAssinados(
			MpmAltaSumario altaSumario, MamReceituarios receita) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamReceituarios.class);

		if (receita != null && receita.getSeq() != null) {
			criteria.add(Restrictions.eq(
					MamReceituarios.Fields.SEQ.toString(), receita.getSeq()));
		}
		
		criteria.add(Restrictions.in(
				MamReceituarios.Fields.IND_PENDENTE.toString(),
				new DominioIndPendenteAmbulatorio[] {
						DominioIndPendenteAmbulatorio.R,
						DominioIndPendenteAmbulatorio.P,
						DominioIndPendenteAmbulatorio.E }));
		
		criteria.add(Restrictions.eq(
				MamReceituarios.Fields.MPM_ALTA_SUMARIO.toString(), altaSumario));
		
		return executeCriteria(criteria);
	
	}

	/**
	 * Retorna um receituário a partir do sumário de alta, o tipo de receita
	 * deve ser G (geral)ou E (Especial)
	 * 
	 * @param mpmAltaSumario
	 * @param tipoReceita
	 * @return Receituário ou null se não encontrar
	 */
	public MamReceituarios buscarReceituarioPorAltaSumario(
			MpmAltaSumario mpmAltaSumario, DominioTipoReceituario tipoReceita) {

		//mpmAltaSumario.getId().setSeqp(Short.valueOf("24"));
		MamReceituarios mamReceituarios = null;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamReceituarios.class);

		criteria.add(Restrictions.eq("mpmAltaSumario", mpmAltaSumario));

		criteria.add(Restrictions.isNull(MamReceituarios.Fields.CON_NUMERO
				.toString()));
		criteria.add(Restrictions.eq(
				MamReceituarios.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteAmbulatorio.P));
		
		if(tipoReceita != null){
			criteria.add(Restrictions.eq(MamReceituarios.Fields.TIPO.toString(),
					tipoReceita));
		}
		
		criteria.addOrder(Order.desc(MamReceituarios.Fields.SEQ.toString()));

		List<MamReceituarios> listReceituarios = executeCriteria(criteria);

		if (!listReceituarios.isEmpty()) {
			mamReceituarios = listReceituarios.get(0);
		}

		return mamReceituarios;
	}
	
	/**
	 *  Retorna lista de Receituários a partir do sumário de alta
	 *  e o status seja 'V - Validado' ou 'P - Pendente'
	 * 
	 * @param mpmAltaSumario
	 * @param antigoAsuSeqp
	 * 
	 * @return Lista de Receituários ou null se não encontrar
	 */
	public List<MamReceituarios> obterReceituarioVersionamento(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);

		criteria.add(Restrictions.eq(MamReceituarios.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MamReceituarios.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MamReceituarios.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		criteria.add(Restrictions.in(
				MamReceituarios.Fields.IND_PENDENTE.toString(),
				new DominioIndPendenteAmbulatorio[] {
						DominioIndPendenteAmbulatorio.P,
						DominioIndPendenteAmbulatorio.V }));
		
		return executeCriteria(criteria);
	}


	/**
	 * Metodo que retorna todos os receituarios para a chave primaria do sumario de alta
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param tipo
	 * @return
	 */
	public List<MamReceituarios> obterMamReceituarioList(Integer altanAtdSeq,
			Integer altanApaSeq, Short altanAsuSeqp, DominioTipoReceituario tipo) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(
				MamReceituarios.Fields.ASU_APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(
				MamReceituarios.Fields.ASU_APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(
				MamReceituarios.Fields.ASU_SEQP.toString(), altanAsuSeqp));
		
		if(tipo != null){
			criteria.add(Restrictions.eq(
	                MamReceituarios.Fields.TIPO.toString(), tipo));
		}
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Retorna MpmMamReceituarios
	 * 
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MamReceituarios obterMamReceituario(Integer altanAtdSeq,
			Integer altanApaSeq, Short altanAsuSeqp, DominioTipoReceituario tipo) {

		MamReceituarios mamReceituarios = null;
		
		List<MamReceituarios> listReceituarios = obterMamReceituarioList(altanAtdSeq, altanApaSeq, altanAsuSeqp, tipo);

		if (!listReceituarios.isEmpty()) {
			
			mamReceituarios = listReceituarios.get(0);
			
			for(MamReceituarios receituario : listReceituarios){
				
				if(receituario.getSeq() > mamReceituarios.getSeq()){
					mamReceituarios = receituario;
				}
			}
		}

		return mamReceituarios;
	}
	
	/**
	 * Busca um MamReceituarios pelo id, nao atachado.<br>
	 * NAO utiliza evict.<br>
	 * Objeto retornado contera os valores existentes na base.<br>
	 * Usar apenas para comparacoes com valores antigos.<br>
	 * Setar as informacoes conforme necessario.<br>
	 * Hoje eh carregada as seguintes informacoes:<br>
	 * - MamReceituarios.Fields.SEQ;
	 * - MamReceituarios.Fields.MPM_ALTA_SUMARIO;
	 * - MamReceituarios.Fields.IND_PENDENTE;
	 * - MamReceituarios.Fields.TIPO;
	 * 
	 * @param receita
	 * @return
	 */
	public MamReceituarios obterMamReceituarioOriginal(MamReceituarios receita) {
		if (receita == null || receita.getSeq() == null) {
			throw new IllegalArgumentException("Parametro receita nao foi informado ou nao possui seq.");
		}
		
		StringBuilder hql = new StringBuilder(100);
		hql.append("select o.").append(MamReceituarios.Fields.SEQ.toString());
		hql.append(", o.").append(MamReceituarios.Fields.MPM_ALTA_SUMARIO.toString());
		hql.append(", o.").append(MamReceituarios.Fields.IND_PENDENTE.toString());
		hql.append(", o.").append(MamReceituarios.Fields.TIPO.toString());
		hql.append(" from ").append(MamReceituarios.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(MamReceituarios.Fields.MPM_ALTA_SUMARIO.toString());
		hql.append(" where o.").append(MamReceituarios.Fields.SEQ.toString()).append(" = :pSeq ");
		
		LOG.info(hql.toString());
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("pSeq", receita.getSeq());
		List<Object[]> lista = query.getResultList();
		
		MamReceituarios returnValue = null;
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new MamReceituarios();
			for (Object[] listFileds : lista) {
				returnValue.setSeq( (Long) listFileds[0]);
				returnValue.setMpmAltaSumario( (MpmAltaSumario) listFileds[1] );
				returnValue.setPendente( (DominioIndPendenteAmbulatorio) listFileds[2]);
				returnValue.setTipo( (DominioTipoReceituario) listFileds[3]);
			}
		}
		
		return returnValue;
	}
	
	/**
	 * Se este nao possuir irmaos com indPendente <b>R</b> e <b>P</b>.<br>
	 * Retorna o MamReceituarios pai.<br>
	 * Caso contrario retorna null.<br>
	 * Esta regra era feita em um SQL.
	 * 
	 * @param rec
	 * @return
	 */
	public MamReceituarios obterReceituarioIndPendente(MamReceituarios receita) {
		if (receita == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
		}
		/*	
		 * SELECT rct.ind_pendente FROM  mam_receituarios rct 
		 * WHERE rct.seq = p_rct_seq
		 * AND NOT EXISTS (
		 * 			SELECT rct1.seq FROM mam_receituarios rct1 
		 * 			WHERE rct1.rct_seq = rct.seq 
		 * 			AND rct1.ind_pendente IN ('R','P'));
		 */
		MamReceituarios returnValue = null;
		
		//if (receita.getMamReceituarios() != null) {
		if (receita.getSeq() != null) {
			StringBuilder hql = new StringBuilder();
			hql.append("select o");
			hql.append(" from ").append(MamReceituarios.class.getSimpleName()).append(" o ");
			hql.append(" where o.").append(MamReceituarios.Fields.RCT_SEQ.toString()).append(" = :pSeqPai ");
			hql.append(" and o.").append(MamReceituarios.Fields.IND_PENDENTE.toString()).append(" in (").append(":pListIndPendente").append(") ");
			
			LOG.info(hql.toString());
			javax.persistence.Query query = this.createQuery(hql.toString());
			//query.setParameter("pSeqPai", receita.getMamReceituarios().getSeq());
			query.setParameter("pSeqPai", receita.getSeq());
			query.setParameter("pListIndPendente", Arrays.asList(DominioIndPendenteAmbulatorio.R, DominioIndPendenteAmbulatorio.P));
			List<MamReceituarios> lista = query.getResultList();
			
			if (lista == null || lista.isEmpty()) {
				returnValue = receita.getReceituario();
			}
		}
		
		return returnValue;
	}
	
	public List<MamReceituarios> obterReceituariosPorNumeroConsultaEIndPendente(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(),conNumero));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.DTHR_VALIDA_MVTO.toString()));
		
		criteria.add(Restrictions.in(MamReceituarios.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.V, DominioIndPendenteAmbulatorio.P}));
		
		return executeCriteria(criteria);
	}
	
	
	public List<MamReceituarios> pesquisarReceituarioParaCancelamento(Integer consultaNumero, Date dataHoraMovimento, Long rctSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), consultaNumero));
		if(dataHoraMovimento!=null){
			criteria.add(Restrictions.or(Restrictions.ge(MamReceituarios.Fields.DTHR_CRIACAO.toString(), dataHoraMovimento), Restrictions.and(Restrictions.isNotNull(MamReceituarios.Fields.DTHR_MOVIMENTO.toString()), Restrictions.ge(MamEvolucoes.Fields.DTHR_MOVIMENTO.toString(), dataHoraMovimento))));
		}
		criteria.add(Restrictions.in(MamReceituarios.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(rctSeq!=null){
			criteria.add(Restrictions.eq(MamReceituarios.Fields.SEQ.toString(),rctSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamReceituarios> pesquisarReceituarioParaConclusao(Integer consultaNumero, Date dataHoraMovimento, Long rctSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamReceituarios.Fields.PENDENTE.toString(), new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.E}));
		if(rctSeq!=null){
			criteria.add(Restrictions.eq(MamReceituarios.Fields.SEQ.toString(),rctSeq));
		}
		return executeCriteria(criteria);
	}
	
	public List<MamReceituarios> pesquisarReceituarioPorConsultaEIndPendenteDiferente(Integer conNumero, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(),
				conNumero));
		criteria.add(Restrictions.ne(MamReceituarios.Fields.PENDENTE.toString(),
				pendente));
		return executeCriteria(criteria);
	}

	/** Pesquisa Receituario por numero, seq e pendencia
	 * 
	 * @param consultaNumero
	 * @param seq
	 * @param pendente
	 * @return
	 */
	public List<MamReceituarios> pesquisarReceituarioPorNumeroSeqEPendencia(Integer consultaNumero, Long seq, DominioIndPendenteAmbulatorio pendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		if (consultaNumero != null){
			criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), consultaNumero));
		}
		if (pendente != null){
			criteria.add(Restrictions.eq(MamReceituarios.Fields.PENDENTE.toString(), pendente));
		}
		if (seq != null){
			criteria.add(Restrictions.eq(MamReceituarios.Fields.SEQ.toString(), seq));
		}
		return executeCriteria(criteria);
	}	
	
	public DetachedCriteria criteriaPesquisarReceituarioPorConsultaETipo(Integer consultaNumero, DominioTipoReceituario tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamReceituarios.Fields.PENDENTE.toString(),  
				new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.V}));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.DTHR_MOVIMENTO.toString()));
		criteria.add(Restrictions.eq(MamReceituarios.Fields.TIPO.toString(), tipo));

		return criteria;
	}
	
	public MamReceituarios primeiroReceituarioPorConsultaETipo(Integer consultaNumero, DominioTipoReceituario tipo) {
		DetachedCriteria criteria = this.criteriaPesquisarReceituarioPorConsultaETipo(consultaNumero, tipo);
		List<MamReceituarios> lista = executeCriteria(criteria, 0, 1, null, true);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	public List<MamReceituarios> listarReceituariosPorPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);

		criteria.add(Restrictions.eq(MamReceituarios.Fields.PACIENTE.toString(), paciente));
		
		return executeCriteria(criteria);
	}
	
	public List<MamReceituarios> pesquisarReceituarioPorConsultaOrdenaTipo(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.in(MamReceituarios.Fields.PENDENTE.toString(),  
				new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P,DominioIndPendenteAmbulatorio.V}));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.DTHR_MOVIMENTO.toString()));
		
		criteria.addOrder(Order.asc(MamReceituarios.Fields.TIPO.toString()));
		
		return executeCriteria(criteria);
	}

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamReceituarios buscarReceituariosPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);

		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamReceituarios> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	public List<MamReceituarios> obterDadosReceituario(Integer atendimentoSeq,DominioTipoReceituario tipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MamReceituarios.Fields.SEQ.toString()).as(MamReceituarios.Fields.SEQ.toString()))
			    .add(Projections.property(MamReceituarios.Fields.NRO_VIAS.toString()).as(MamReceituarios.Fields.NRO_VIAS.toString())));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.CON_NUMERO.toString()));
		criteria.add(Restrictions.eq(MamReceituarios.Fields.ATENDIMENTO_SEQ.toString(), atendimentoSeq));
		criteria.add(Restrictions.in(MamReceituarios.Fields.IND_PENDENTE.toString(),new Object[]{DominioIndPendenteAmbulatorio.R,DominioIndPendenteAmbulatorio.P}));
		criteria.add(Restrictions.eq(MamReceituarios.Fields.TIPO.toString(), tipo));
		criteria.addOrder(Order.desc(MamReceituarios.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamReceituarios.class));
		return executeCriteria(criteria);
	}
	
	public List<MamReceituarios> obterListaSeqTipoMamReceituarios(Integer atdSeq, Integer apaAtdSeq, Integer apaSeq, Integer seqp, Long trgSeq, Long rgtSeq, Integer conNumero,
			DominioTipoReceituario dominioTipoReceituario){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamReceituarios.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MamReceituarios.Fields.SEQ.toString()).as(MamReceituarios.Fields.SEQ.toString()))
				.add(Projections.property(MamReceituarios.Fields.TIPO.toString()).as(MamReceituarios.Fields.TIPO.toString()))
				);
		criteria.add(Restrictions.eq(MamReceituarios.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.ASU_APA_ATD_SEQ.toString()));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.ASU_APA_SEQ.toString()));
		criteria.add(Restrictions.isNull(MamReceituarios.Fields.ASU_SEQP.toString()));
		criteria.add(Restrictions.in(MamReceituarios.Fields.IND_PENDENTE.toString(), new DominioIndPendenteAmbulatorio[]{DominioIndPendenteAmbulatorio.P, DominioIndPendenteAmbulatorio.R}));
		criteria.add(Restrictions.eq(MamReceituarios.Fields.TIPO.toString(), dominioTipoReceituario));

		criteria.addOrder(Order.desc(MamReceituarios.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamReceituarios.class));
		
		return executeCriteria(criteria);
		
//comentada devido alteração no documento de especificação 50983 Revisão 2
//		StringBuilder consulta = new StringBuilder(500);
//			
//		consulta.append(" select  r.seq as seq, r.tipo as tipo ");
//		consulta.append(" from MamReceituarios r ");
//		consulta.append(" where (  ");
//		consulta.append(" 			(  ");
//		consulta.append(" 				(  ");
//		consulta.append("				r.consulta.numero = :conNumero ");
//		consulta.append(" and			r.mpmAltaSumario.id.apaAtdSeq is null ");
//		consulta.append(" and			r.mpmAltaSumario.id.apaSeq is null ");
//		consulta.append(" and			r.mpmAltaSumario.id.seqp is null ");
//		consulta.append(" and			:trgSeq is null ");
//		consulta.append(" and			:rgtSeq is null ");
//		consulta.append(" 				) or ");
//		consulta.append(" 				(");
//		consulta.append("				r.consulta.numero = :conNumero ");
//		consulta.append(" and			r.mpmAltaSumario.id.apaAtdSeq is null ");
//		consulta.append(" and			r.mpmAltaSumario.id.apaSeq is null ");
//		consulta.append(" and			r.mpmAltaSumario.id.seqp is null ");
//		consulta.append(" and			:trgSeq is not null ");
//		consulta.append(" and			r.mamTriagens.seq :trgSeq ");
//		consulta.append(" and			r.registro.seq :rgtSeq ");
//		consulta.append(" and			) or ");
//		consulta.append("				(");
//		consulta.append("				r.consulta.numero is null ");
//		consulta.append(" and			:apaAtdSeq is not null ");
//		consulta.append(" and			r.mpmAltaSumario.id.apaAtdSeq :apaAtdSeq ");
//		consulta.append(" and			r.mpmAltaSumario.id.apaSeq  :apaSeq ");
//		consulta.append(" and			r.mpmAltaSumario.id.seqp  :seqP ");
//		consulta.append(" and			r.registro.seq :rgtSeq ");
//		consulta.append(" and			) or ");
//		consulta.append("				(");
//		consulta.append("				r.consulta.numero is null");
//		consulta.append(" and			:apaAtdSeq is null ");
//		consulta.append(" and			:atdSeq is not null ");
//		consulta.append(" and			:rgtSeq is not null ");
//		consulta.append(" and			:trgSeq is null ");
//		consulta.append(" and			r.atendimentos.seq :atdSeq ");
//		consulta.append(" and			r.registro.seq :rgtSeq ");
//		consulta.append(" and			) or ");
//		consulta.append("				(");
//		consulta.append("				:atdSeq is not null ");
//		consulta.append(" and			:rgtSeq is not null ");
//		consulta.append(" and			:trgSeq is not null ");
//		consulta.append(" and			r.mamTriagens.seq :trgSeq ");
//		consulta.append(" and			r.registro.seq :rgtSeq ");
//		consulta.append("				)  ");
//		consulta.append("			)  ");
//		consulta.append(" and r.indImpresso in ('P', 'R') ");
//		consulta.append(" and r.tipo in ('G', 'E') ");
//		consulta.append(" order by r.seq desc ");
//
//		Query query = createHibernateQuery(consulta.toString());
//		query.setResultTransformer(Transformers.aliasToBean(MamReceituarios.class));
//
//		query.setInteger("atdSeq", atdSeq);
//		query.setLong("rgtSeq", rgtSeq);
//		query.setLong("trgSeq", trgSeq);
//		query.setInteger("apaAtdSeq", apaAtdSeq);
//		query.setInteger("apaSeq", apaSeq);
//		query.setInteger("seqP", seqp);
//		query.setInteger("conNumero", conNumero);
//		
//		return  query.list();
	}
}
