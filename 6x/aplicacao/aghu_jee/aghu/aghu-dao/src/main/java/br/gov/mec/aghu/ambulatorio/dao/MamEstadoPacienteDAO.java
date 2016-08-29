package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioAtivoCancelado;
import br.gov.mec.aghu.dominio.DominioIndPendencia;
import br.gov.mec.aghu.dominio.DominioIndicadorOrigemTipoEstadoPacientes;
import br.gov.mec.aghu.model.MamEstadoPaciente;
import br.gov.mec.aghu.model.MamTipoEstadoPaciente;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.EstadoPacienteVO;

public class MamEstadoPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamEstadoPaciente> {

	private static final long serialVersionUID = 1435051602562578633L;

	public MamTipoEstadoPaciente obterEstadoAtual(Integer atdSeq, Date dataValidacao) {
		if (atdSeq == null) {
			return null;
		} else { 
			MamTipoEstadoPaciente tipoEstadoPaciente = null;

			DetachedCriteria criteria = DetachedCriteria
					.forClass(MamEstadoPaciente.class);

			criteria.add(Restrictions.eq(
					MamEstadoPaciente.Fields.ATD_SEQ.toString(), atdSeq));
			
			criteria.add(Restrictions.eq(
					MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V));

			if (dataValidacao != null){
				criteria.add(Restrictions.le(
					MamEstadoPaciente.Fields.DTHR_VALIDA.toString() , dataValidacao));
				
			}

			criteria.addOrder(Order.desc(MamEstadoPaciente.Fields.DTHR_VALIDA
					.toString()));
			
			criteria.addOrder(Order.desc(MamEstadoPaciente.Fields.SEQ
					.toString()));

			List<MamEstadoPaciente> estadoPaciente = executeCriteria(criteria);

			if (!estadoPaciente.isEmpty()) {
				if (estadoPaciente.get(0).getMamTipoEstadoPaciente() != null){
				   tipoEstadoPaciente = estadoPaciente.get(0).getMamTipoEstadoPaciente();
				}
			}

			return tipoEstadoPaciente;
		}
	}
	
	/**
	 * #44179 - C2
	 * @author marcelo.deus
	 */
	public String buscarEstadoPaciente(Integer atendimentoSeq, Integer prescricaoSeq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class, "MEP");
		
		criteria.createAlias("MEP." + MamEstadoPaciente.Fields.MAM_TIPO_ESTADO_PACIENTES.toString(), "MTEP");
		
		criteria.add(Restrictions.eq("MEP." + MamEstadoPaciente.Fields.ATD_SEQ.toString(), atendimentoSeq))
				.add(Restrictions.eq("MEP." + MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V));
		
		 criteria.setProjection(Projections.property("MTEP." + MamTipoEstadoPaciente.Fields.TITULO.toString()));

		 DetachedCriteria subCriteria = DetachedCriteria.forClass(MamEstadoPaciente.class, "MEP2");

		 subCriteria.add(Restrictions.eq("MEP2." + MamEstadoPaciente.Fields.ATD_SEQ.toString(), atendimentoSeq))
			.add(Restrictions.eq("MEP2." + MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V));

		 subCriteria.setProjection(Projections.max("MEP2." + MamEstadoPaciente.Fields.SEQ.toString()));

		 criteria.add(Subqueries.propertyEq("MEP." + MamEstadoPaciente.Fields.SEQ.toString(), subCriteria));

		 List<String> retorno = executeCriteria(criteria);
		 if (retorno.isEmpty()) {
			 return null;
		 }

		 return retorno.get(0);
	}
	
	/**
	 * #44179 - C2
	 * @author marcelo.deus
	 */
	public Long buscarTriagemEstadoPaciente(Integer atendimentoSeq, Integer prescricaoSeq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class, "MEP");
		
		criteria.add(Restrictions.eq("MEP." + MamEstadoPaciente.Fields.PRESCRICAO_ATD_SEQ.toString(), atendimentoSeq))
				.add(Restrictions.eq("MEP." + MamEstadoPaciente.Fields.PRESCRICAO_SEQ.toString(), prescricaoSeq));
		
		 criteria.setProjection(Projections.projectionList()
	    			.add(Projections.property("MEP." + MamEstadoPaciente.Fields.TRG_SEQ.toString())
	    					.as(MamEstadoPaciente.Fields.TRG_SEQ.toString())));
		 
		 return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #44179 - CURSOR cur_esa
	 * @author marcelo.deus
	 */
	public String listarCursorMamEstadoPacientes(Integer cPmeAtdSeq, Integer cPmeSeq, Long cRgtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class, "ESA");
		criteria.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.PRESCRICAO_ATD_SEQ.toString(), cPmeAtdSeq))
				.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.PRESCRICAO_SEQ.toString(), cPmeSeq))
				.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.MAM_REGISTRO_SEQ.toString(), cRgtSeq))
				.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V));
		Boolean  existe = executeCriteriaExists(criteria);
		
		if(existe){
			return "S";
		} else {
			return null;
		}
	}
	
	/**
	 * #44179 - CURSOR cur_esa_2
	 * @author marcelo.deus
	 */
	public String listarCursorMamEstadoPacientes2(Long cRgtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class, "ESA");
		criteria.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.MAM_REGISTRO_SEQ.toString(), cRgtSeq))
				.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V));
		Boolean  existe = executeCriteriaExists(criteria);
		
		if(existe){
			return "S";
		} else {
			return null;
		}
	}
	
	/**
	 * #1378 C5
	 * 
	 * @author adrian.gois
	 */
	public List<MamTipoEstadoPaciente> obterListaTipoEstadoPaciente(
			Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamTipoEstadoPaciente.class);
		List<String> dominios = null;
		Criterion clausula = null;

		if (atdSeq == null) {
			dominios = new ArrayList<String>();
			dominios.add(DominioIndicadorOrigemTipoEstadoPacientes.E.getValor());
			dominios.add(DominioIndicadorOrigemTipoEstadoPacientes.T.getValor());
			clausula = Restrictions.in(
					MamTipoEstadoPaciente.Fields.IND_ORIGEM.toString(),
					dominios);
		} else {
			dominios = new ArrayList<String>();
			dominios.add(DominioIndicadorOrigemTipoEstadoPacientes.C.getValor());
			dominios.add(DominioIndicadorOrigemTipoEstadoPacientes.T.getValor());
			clausula = Restrictions.in(
					MamTipoEstadoPaciente.Fields.IND_ORIGEM.toString(),
					dominios);
		}
		criteria.add(
				Restrictions.eq(
						MamTipoEstadoPaciente.Fields.IND_SITUACAO.toString(),
						DominioAtivoCancelado.A.toString()))
				.add(clausula)
				.addOrder(
						Order.asc(MamTipoEstadoPaciente.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * #1378 C6
	 * 
	 * @author adrian.gois
	 * @return
	 */
	public Long obterEsaSeqPorRgtSeq(Long rgtSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEstadoPaciente.class);
		criteria.add(Restrictions.eq(
				MamEstadoPaciente.Fields.RGT_SEQ.toString(), rgtSeq));
		List<MamEstadoPaciente> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0).getMamEstadoPaciente().getSeq();
		}
		return null;
	}
	
	/**
	 * #1378 C2
	 * @param atdSeq
	 * @return
	 */
	public EstadoPacienteVO obterEstadoPacientePeloAtdSeq(Long atdSeq){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEstadoPaciente.class, "ESA"); 
		
		criteria.createAlias("ESA." + MamEstadoPaciente.Fields.MAM_TIPO_ESTADO_PACIENTES.toString(),"TSA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.ATD_SEQ.toString(), atdSeq.intValue()))
				.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V))
				.add(Restrictions.isNull("ESA." + MamEstadoPaciente.Fields.DTHR_VALIDA_MVTO.toString()));
		ProjectionList p = Projections.projectionList()
				.add(Projections.property("ESA." + MamEstadoPaciente.Fields.SEQ.toString()),EstadoPacienteVO.Fields.ESA_SEQ.toString())
				.add(Projections.property("TSA." + MamTipoEstadoPaciente.Fields.SEQ.toString()),EstadoPacienteVO.Fields.TSA_SEQ.toString())
				.add(Projections.property("TSA." + MamTipoEstadoPaciente.Fields.TITULO.toString()), EstadoPacienteVO.Fields.TITULO.toString());
		criteria.setProjection(p);
		criteria.addOrder(Order.desc(MamTipoEstadoPaciente.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(EstadoPacienteVO.class));
		return  (EstadoPacienteVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #1378 C3
	 * @param trgSeq
	 * @return
	 */
	public EstadoPacienteVO obterEstadoPacientePeloTrgSeq(Long trgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class, "ESA");
		
		criteria.createAlias(MamEstadoPaciente.Fields.MAM_TIPO_ESTADO_PACIENTES.toString(),"TSA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MamEstadoPaciente.Fields.TRG_SEQ.toString(), trgSeq))
				.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.IND_PENDENTE.toString(), DominioIndPendencia.V))
				.add(Restrictions.isNull("ESA." + MamEstadoPaciente.Fields.DTHR_VALIDA_MVTO.toString()));
		ProjectionList p = Projections.projectionList()
				.add(Projections.property(MamEstadoPaciente.Fields.ESA_SEQ.toString()),EstadoPacienteVO.Fields.ESA_SEQ.toString())
				.add(Projections.property("TSA." + MamTipoEstadoPaciente.Fields.SEQ.toString()),EstadoPacienteVO.Fields.TSA_SEQ.toString())
				.add(Projections.property("TSA." + MamTipoEstadoPaciente.Fields.TITULO.toString()), EstadoPacienteVO.Fields.TITULO.toString());
		criteria.setProjection(p);
		criteria.addOrder(Order.desc(MamTipoEstadoPaciente.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(EstadoPacienteVO.class));
		
		return (EstadoPacienteVO) executeCriteriaUniqueResult(criteria);
	}
	
	public MamEstadoPaciente obterMamEstadoPacienteAtual(Long atdSeq){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamEstadoPaciente.class, "ESA"); 
		criteria.add(Restrictions.eq("ESA." + MamEstadoPaciente.Fields.ATD_SEQ.toString(), atdSeq.intValue()));
		criteria.addOrder(Order.desc("ESA." + MamEstadoPaciente.Fields.SEQ.toString()));
		List<MamEstadoPaciente> estado = executeCriteria(criteria);
		if(!estado.isEmpty()){
			return estado.get(0);
		}
		return  null;
	}

	
	public void atualizarEstadoPorSequencialIndicadorDataHoraValidaMtvo(Long esaSeqAnt,RapServidores servidorLogado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class);
		
		criteria.add(Restrictions.eq(MamEstadoPaciente.Fields.SEQ.toString(),esaSeqAnt));
		criteria.add(Restrictions.eq(MamEstadoPaciente.Fields.IND_PENDENTE.toString(),DominioIndPendencia.V));
		criteria.add(Restrictions.isNull(MamEstadoPaciente.Fields.DTHR_VALIDA_MVTO	.toString()));
		MamEstadoPaciente retorno = (MamEstadoPaciente) executeCriteriaUniqueResult(criteria, false);
		if (retorno != null) {
			retorno.setDthrMvto(new Date());
			retorno.setServMvto(servidorLogado);
			retorno.setIndPendencia(DominioIndPendencia.A);
			atualizar(retorno);
		}
	}

	/**
	 * #1378 - P2 - Cursor cur_esa
	 * @ORADB MAMP_EMG_CONC_ESA
	 */
	public List<MamEstadoPaciente> gerenciaEstadoPacientes(Long valorEsaSeq, Long rgtSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPaciente.class);
		
		criteria.add(Restrictions.eq(MamEstadoPaciente.Fields.RGT_SEQ. toString(), rgtSeq));
		criteria.add(Restrictions.in(MamEstadoPaciente.Fields.IND_PENDENTE.toString(), 
				new Object[] {DominioIndPendencia.R, DominioIndPendencia.P,	DominioIndPendencia.E }));
		criteria.add(Restrictions.or(Restrictions.isNull(MamEstadoPaciente.Fields.ESA_SEQ.toString()), 
					  Restrictions.eq(MamEstadoPaciente.Fields.ESA_SEQ.toString(), valorEsaSeq)));

		return executeCriteria(criteria);
	}
	
}
