package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemEntregaExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;

public class AelProtocoloEntregaExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProtocoloEntregaExames>{

	private static final long serialVersionUID = -8485252619143294989L;

	
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumProtocolo(Long seq_protocolo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);

		criteria.createAlias(AipPacientes.Fields.ATENDIMENTOS.toString(), "atd", JoinType.INNER_JOIN);// join atendimentos
		criteria.createAlias("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.LEFT_OUTER_JOIN);//itens solicitação exames
		criteria.createAlias("atd."+AghAtendimentos.Fields.SOLICITACAO_EXAMES.toString(), "soe", JoinType.INNER_JOIN); //join solicitação exames
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(),"isoe" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("isoe."+AelItemSolicitacaoExames.Fields.ITEM_ENTREGA_EXAMES.toString(),"iee" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("iee."+AelItemEntregaExames.Fields.PROTOCOLO.toString(),"pee" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("pee." + AelProtocoloEntregaExames.Fields.SEQ, seq_protocolo));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(AipPacientes.Fields.CODIGO.toString())))
				.add(Projections.property(AipPacientes.Fields.NOME.toString()), "nome")
				.add(Projections.property(AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("cons."+AacConsultas.Fields.NUMERO.toString()), "consulta")
		);
		
		List<PesquisaExamesPacientesVO> listaPacientes = new ArrayList<PesquisaExamesPacientesVO>();
		
		List<Object[]> valores = executeCriteria(criteria);
		for (Object[] valor : valores) {
			PesquisaExamesPacientesVO paciente = new PesquisaExamesPacientesVO();
			
			paciente.setCodigo((Integer)valor[0]);
			paciente.setNomePaciente((String)valor[1]);
			paciente.setProntuario((Integer)valor[2]);
			paciente.setConsulta((Integer)valor[3]);
			
			listaPacientes.add(paciente);
		}
		
		return listaPacientes;

	}

	public AelProtocoloEntregaExames buscarProtocolo(Long seq_protocolo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProtocoloEntregaExames.class);
		criteria.add(Restrictions.eq(AelProtocoloEntregaExames.Fields.SEQ.toString(), seq_protocolo));
		
		return (AelProtocoloEntregaExames) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelProtocoloEntregaExames> buscarProtocoloPorSolicitacao(Integer solicitacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProtocoloEntregaExames.class, "PEE");
		criteria.createAlias("PEE."+AelProtocoloEntregaExames.Fields.ITEM_ENTREGA_EXAMES,"IEE" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IEE." + AelItemEntregaExames.Fields.ITEM_S0LICITACAO_EXAMES.toString(),"ISE" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(),"SE" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("SE." + AelSolicitacaoExames.Fields.SEQ.toString(), solicitacao));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(AelProtocoloEntregaExames.Fields.SEQ.toString())),"seq")
				.add(Projections.property(AelProtocoloEntregaExames.Fields.CRIADO_EM.toString()), "criadoEm")
				.add(Projections.property(AelProtocoloEntregaExames.Fields.RETIRADO_POR.toString()), "nomeResponsavelRetirada")
				.add(Projections.property(AelProtocoloEntregaExames.Fields.USUARIO_LOGADO.toString()), "servidor")
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelProtocoloEntregaExames.class));
		
		return executeCriteria(criteria);
		
	}
	
	public List<AelProtocoloEntregaExames> buscarProtocoloPorProntuario(Integer pronturario) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProtocoloEntregaExames.class, "PEE");
		criteria.createAlias("PEE."+AelProtocoloEntregaExames.Fields.ITEM_ENTREGA_EXAMES,"IEE" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IEE." + AelItemEntregaExames.Fields.ITEM_S0LICITACAO_EXAMES.toString(),"ISE" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(),"SE" ,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SE."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(),"ATD" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PRONTUARIO.toString(), pronturario));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(AelProtocoloEntregaExames.Fields.SEQ.toString())),"seq")
				.add(Projections.property(AelProtocoloEntregaExames.Fields.CRIADO_EM.toString()), "criadoEm")
				.add(Projections.property(AelProtocoloEntregaExames.Fields.RETIRADO_POR.toString()), "nomeResponsavelRetirada")
				.add(Projections.property(AelProtocoloEntregaExames.Fields.USUARIO_LOGADO.toString()), "servidor")
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelProtocoloEntregaExames.class));
		
		return executeCriteria(criteria);
		
	}
	
	public List<AelItemEntregaExames> buscarItensProtocolo(Long protocolo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemEntregaExames.class);
		criteria.createAlias(AelItemEntregaExames.Fields.PROTOCOLO.toString(),"pee" ,JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("pee." + AelProtocoloEntregaExames.Fields.SEQ, protocolo));
		
		return executeCriteria(criteria);
		
	}
	
}
