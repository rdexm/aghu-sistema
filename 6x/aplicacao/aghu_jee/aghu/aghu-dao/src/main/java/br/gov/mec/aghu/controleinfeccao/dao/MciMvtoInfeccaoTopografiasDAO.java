package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;

public class MciMvtoInfeccaoTopografiasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMvtoInfeccaoTopografias> {

	private static final long serialVersionUID = 3920581059252352371L;

	public List<MciMvtoInfeccaoTopografias> listarMvtosInfeccoesTopologiasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class);
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.COD_PACIENTE.toString(), pacCodigo));
		return executeCriteria(criteria);
	}
	
	public List<MciMvtoInfeccaoTopografias> listarMvtosInfeccoesTopografias(Integer pacCodigo, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class);
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.ATENDIMENTO.toString(), atendimento));
		return executeCriteria(criteria);
	}
	
	public List<Integer> listarMvtosInfeccoesTopografiasPorUnfSeqTipoOrigem(Short unfSeq, String codigoOrigem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class);
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(MciMvtoInfeccaoTopografias.Fields.SEQ.toString())));
		
		Criterion critUnfSeq = Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.UNF_SEQ.toString(), unfSeq);
		Criterion critUnfSeqNotificado = Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.UNF_SEQ_NOTIFICADO.toString(), unfSeq);
		
		criteria.add(Restrictions.or(critUnfSeq, critUnfSeqNotificado));
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.EIN_TIPO.toString(), codigoOrigem));
		
		return executeCriteria(criteria);
	}
	
	public List<MciMvtoInfeccaoTopografias> listarPorTopSeq(Short topSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class);
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO_SEQ.toString(), topSeq));
		return executeCriteria(criteria);
	}

	//#1297 -C9
	public boolean validarExistenciaTopografiaNaoEncerradaPorAtendimento(Integer seq, Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class, "MIT");
		criteria.createCriteria("MIT." + MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO, "TOP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("MIT." + MciMvtoInfeccaoTopografias.Fields.ATENDIMENTO_SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.isNull("MIT." + MciMvtoInfeccaoTopografias.Fields.DT_FIM.toString()));
		criteria.add(Restrictions.eq("TOP." + MciTopografiaProcedimento.Fields.IND_PERM_SOBREPOSICAO .toString(), Boolean.FALSE));
		if(seq != null) {
			criteria.add(Restrictions.ne("MIT." + MciMvtoInfeccaoTopografias.Fields.SEQ.toString(), seq));
		}
		return executeCriteriaCount(criteria) > 0;
		
	}
	
	public List<NotificacaoTopografiasVO> listarMciTopografiaProcedimentoPorSeqDescSitSeqTop( Integer codigoPaciente) {
		NotificacoesPacienteInfeccaoTopografiaQueryBuilder queryBuilder = new NotificacoesPacienteInfeccaoTopografiaQueryBuilder();
		return executeCriteria(queryBuilder.build(Boolean.TRUE, Boolean.FALSE, codigoPaciente));
	}
	
	public Boolean existeNotificacaoNaoConferidaListarPacientesCCIH(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class);
		criteria.createAlias(MciMvtoInfeccaoTopografias.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.CONFIRMACAO_CCI.toString(), DominioConfirmacaoCCI.N));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		return executeCriteriaExists(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesTopografias(final Integer codigoPaciente) {
		DetachedCriteria criteria = montarCriteriaNotificacaoTopografias(codigoPaciente);
				
		criteria.addOrder(Order.desc("MIT." + MciMvtoInfeccaoTopografias.Fields.DT_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesTopografiasBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		DetachedCriteria criteria = montarCriteriaNotificacaoTopografias(codigoPaciente);
				
		criteria.add(Restrictions.eq("MIT." + MciMvtoInfeccaoTopografias.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		
		criteria.addOrder(Order.desc("MIT." + MciMvtoInfeccaoTopografias.Fields.DT_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria montarCriteriaNotificacaoTopografias(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class, "MIT");
		criteria.createAlias("MIT." + MciMvtoInfeccaoTopografias.Fields.MVTO_MEDIDA_PREVENTIVAS.toString(), "MMP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MIT." + MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO.toString(), "TOP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.MCI_ETIOLOGIA_INFECCAO.toString(), "EIN", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.TOP_SEQ.toString())
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.TOP_DESCRICAO.toString())
				.add(Projections.property("MIT." + MciMvtoInfeccaoTopografias.Fields.DT_INICIO.toString())
						, NotificacoesGeraisVO.Fields.DT_INICIO.toString())
				.add(Projections.property("MIT." + MciMvtoInfeccaoTopografias.Fields.DT_FIM.toString())
						, NotificacoesGeraisVO.Fields.DT_FIM.toString())
				.add(Projections.property("EIN." + MciEtiologiaInfeccao.Fields.DESCRICAO)
						, NotificacoesGeraisVO.Fields.ETG_DESCRICAO.toString()));
		
		criteria.add(Restrictions.eq("MIT." + MciMvtoInfeccaoTopografias.Fields.COD_PACIENTE.toString(), codigoPaciente));
		
		return criteria;
	}
	
}
