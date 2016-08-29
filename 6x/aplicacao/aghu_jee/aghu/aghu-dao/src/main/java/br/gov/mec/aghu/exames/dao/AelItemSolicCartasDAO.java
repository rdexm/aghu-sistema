package br.gov.mec.aghu.exames.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoCartaColeta;
import br.gov.mec.aghu.exames.vo.CartaRecoletaVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.AelItemSolicCartasId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;


public class AelItemSolicCartasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelItemSolicCartas> {
	
		
	private static final long serialVersionUID = -5083634964035458671L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicCartas.class);
		return criteria;
    }
	
	

	public List<AelItemSolicCartas> buscarAelItemSolicCartasPorAelItemSolicitacaoExames(
			AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelItemSolicCartas.Fields.ISE_SOE_SEQ.toString(), aelItemSolicitacaoExames.getId().getSoeSeq()));
		dc.add(Restrictions.eq(AelItemSolicCartas.Fields.ISE_SEQP.toString(), aelItemSolicitacaoExames.getId().getSeqp()));
		
		return executeCriteria(dc);
	}
	
	private DetachedCriteria obterCriteriaAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(DominioSituacaoCartaColeta situacao, Date dtInicio, 
			Date dtFim, Integer iseSoeSeq, Integer pacCodigo) {
		DetachedCriteria dc = obterCriteria();
		
		if(situacao != null) {
			dc.add(Restrictions.eq(AelItemSolicCartas.Fields.SITUACAO.toString(), situacao));
		}
		
		if(dtInicio != null && dtFim != null) {
			Calendar inicio = Calendar.getInstance();
			inicio.setTime(dtInicio);
			inicio.set(Calendar.HOUR_OF_DAY, 0);
			inicio.set(Calendar.MINUTE, 0);
			inicio.set(Calendar.SECOND, 0);
			
			Calendar fim = Calendar.getInstance();
			fim.setTime(dtFim);
			fim.set(Calendar.HOUR_OF_DAY, 23);
			fim.set(Calendar.MINUTE, 59);
			fim.set(Calendar.SECOND, 59);

			dc.add(Restrictions.between(AelItemSolicCartas.Fields.ALTERADO_EM.toString(), inicio.getTime(), fim.getTime()));
		}
		
		if(iseSoeSeq != null) {
			dc.add(Restrictions.eq(AelItemSolicCartas.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		}
		
		if(pacCodigo != null) {
			DetachedCriteria sq = DetachedCriteria.forClass(AelSolicitacaoExames.class);
			sq.setProjection(Projections.property(AelSolicitacaoExames.Fields.SEQ.toString()));
			sq.createAlias(AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
			sq.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
			dc.add(Subqueries.propertyIn(AelItemSolicCartas.Fields.ISE_SOE_SEQ.toString(), sq));
		}
		
		
		dc.setFetchMode(AelItemSolicCartas.Fields.SOLICITACAO_EXAMES.toString(), FetchMode.JOIN);
		
		dc.setFetchMode(AelItemSolicCartas.Fields.SOLICITACAO_EXAMES.toString()+"."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), FetchMode.JOIN);
		
		dc.setFetchMode(AelItemSolicCartas.Fields.SOLICITACAO_EXAMES.toString()+"."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), FetchMode.JOIN);
		
		dc.setFetchMode(AelItemSolicCartas.Fields.MODELO.toString(), FetchMode.JOIN);
				
		dc.setFetchMode(AelItemSolicCartas.Fields.MOTIVO_RETORNO.toString(), FetchMode.JOIN);

		return dc;
    }

	public List<AelItemSolicCartas> listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, DominioSituacaoCartaColeta situacao, Date dtInicio, 
			Date dtFim, Integer iseSoeSeq, Integer pacCodigo) {
		
		 List<AelItemSolicCartas> retorno;
		
		DetachedCriteria dc = obterCriteriaAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
		
		retorno = executeCriteria(dc, firstResult, maxResult, orderProperty, asc);
		
		for (AelItemSolicCartas itemSolicCartas : retorno){
			if (itemSolicCartas.getSolicitacaoExame().getAtendimento() != null){
				itemSolicCartas.getSolicitacaoExame().getAtendimento().getPaciente().getNome();
			}else if (itemSolicCartas.getSolicitacaoExame().getAtendimentoDiverso() != null){
				itemSolicCartas.getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getNome();
			}
		}	
		
		return retorno;
	}
	
	public Long listarAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigoCount(DominioSituacaoCartaColeta situacao, Date dtInicio, 
			Date dtFim, Integer iseSoeSeq, Integer pacCodigo) {
		
		DetachedCriteria dc = obterCriteriaAelItemSolicCartasPorSituacaoDtInicioEFimEPacCodigo(situacao, dtInicio, dtFim, iseSoeSeq, pacCodigo);
		
		return executeCriteriaCount(dc);
	}

	public List<CartaRecoletaVO> obterCartaParaImpressao(Short iseSeqp, Integer iseSoeSeq, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicCartas.class, "ITE");
		criteria.createAlias(AelItemSolicCartas.Fields.MODELO.toString(), "MRT");
		criteria.createAlias(AelItemSolicCartas.Fields.ITEM_SOLC.toString(), "ISE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_EXAMES_MATERIAL_ANALISE.toString(), "EMA");
		
		criteria.setProjection(
			Projections.projectionList()
				.add(Property.forName("ITE."+ AelItemSolicCartas.Fields.ISE_SOE_SEQ.toString()),"soeSeq")
				.add(Property.forName("EXA."+ AelExames.Fields.DESCRICAO_USUAL.toString()),"exame")
				.add(Property.forName("MRT."+ AelModeloCartas.Fields.TEXTO.toString()), "texto")
				.add(Property.forName("ITE."+ AelItemSolicCartas.Fields.OBSERVACOES.toString()), "observacao")
				.add(Property.forName("EMA."+ AelExamesMaterialAnalise.Fields.TEMPO_JEJUM.toString()), "jejum"));
		
		criteria.add(Restrictions.eq("ITE."+ AelItemSolicCartas.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq("ITE."+ AelItemSolicCartas.Fields.ISE_SEQP, iseSeqp));
		criteria.add(Restrictions.eq("ITE."+ AelItemSolicCartas.Fields.SEQP, seqp));

		criteria.setResultTransformer(Transformers.aliasToBean(CartaRecoletaVO.class));
		
		return executeCriteria(criteria);
	}



	public AelItemSolicCartas obterAelItemSolicCartasComPaciente(
			AelItemSolicCartasId id) {
		AelItemSolicCartas retorno;
		
		retorno = this.obterPorChavePrimaria(id,true,
				AelItemSolicCartas.Fields.MODELO,
				AelItemSolicCartas.Fields.MOTIVO_RETORNO,
				AelItemSolicCartas.Fields.SOLICITACAO_EXAMES);
		
		
		if(retorno.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento() != null) {
			retorno.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente().getNome();
		}
		else if(retorno.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso() != null) {
			retorno.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getNome();
		}
		
		return retorno;
	}
	
}
