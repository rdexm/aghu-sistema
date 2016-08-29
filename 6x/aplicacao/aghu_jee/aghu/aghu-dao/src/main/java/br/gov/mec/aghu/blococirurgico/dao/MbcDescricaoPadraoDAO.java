package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;

public class MbcDescricaoPadraoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcDescricaoPadrao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4690741623522415911L;

	public List<MbcDescricaoPadrao> buscarMbcDescricaoPadrao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Short especialidadeId, Integer procedimentoId, String titulo) {
		MbcDescricaoPadrao descricaoPadrao = criarDescricaoPadrao(especialidadeId, procedimentoId, titulo);
		final DetachedCriteria criteria = this.criarCriteria(descricaoPadrao);
		List<MbcDescricaoPadrao> listaMbcDescricaoPadrao = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		return listaMbcDescricaoPadrao;
	}

	private MbcDescricaoPadrao criarDescricaoPadrao(Short especialidadeId, Integer procedimentoId, String titulo){
		
		MbcDescricaoPadrao descricaoPadrao = new MbcDescricaoPadrao();
		if(especialidadeId!=null){
			AghEspecialidades especialidade = new AghEspecialidades();
			especialidade.setSeq(especialidadeId);
			descricaoPadrao.setAghEspecialidades(especialidade);
		}
		
		if(procedimentoId!=null){
			MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = new MbcProcedimentoCirurgicos();
			mbcProcedimentoCirurgicos.setSeq(procedimentoId);
			descricaoPadrao.setMbcProcedimentoCirurgicos(mbcProcedimentoCirurgicos);
		}
		
		if(StringUtils.isEmpty(titulo)){
			descricaoPadrao.setTitulo(null);
		}else{
			descricaoPadrao.setTitulo(titulo);
		}
		
		return descricaoPadrao;
		
	}

	private DetachedCriteria criarCriteria(final MbcDescricaoPadrao  descricaoPadrao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoPadrao.class);
		criteria.createAlias(MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), "ESP");
		criteria.createAlias(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), "MPC");
		
		if (descricaoPadrao != null) {
			if (descricaoPadrao.getId() != null) {
				if (descricaoPadrao.getId().getSeqp() != null) {
					criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.ID_SEQP.toString(), descricaoPadrao.getId().getSeqp()));
				}
				if (descricaoPadrao.getId().getEspSeq() != null) {
					criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.ID_ESP_SEQ.toString(), descricaoPadrao.getId().getEspSeq()));
				}
			}
			if (descricaoPadrao.getAghEspecialidades()!=null){
				criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.AGH_ESPECIALIDADES.toString(), descricaoPadrao.getAghEspecialidades().getSeq()));
			}

			if (descricaoPadrao.getMbcProcedimentoCirurgicos()!=null){
				criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), descricaoPadrao.getMbcProcedimentoCirurgicos().getSeq()));
			}
			
			if (descricaoPadrao.getTitulo()!=null){
				criteria.add(Restrictions.ilike(MbcDescricaoPadrao.Fields.TITULO.toString(), descricaoPadrao.getTitulo(), MatchMode.ANYWHERE));
			}
			
		}
		return criteria;
	}

	public Long contarMbcDescricaoPadrao(Short especialidadeId, Integer procedimentoId, String titulo) {
		MbcDescricaoPadrao descricaoPadrao = criarDescricaoPadrao(especialidadeId, procedimentoId, titulo);
		return executeCriteriaCount(this.criarCriteria(descricaoPadrao));
	}

	public MbcDescricaoPadrao obterDescricaoPadraoById(Integer seqp, Short espSeq){
		MbcDescricaoPadrao descricaoPadrao =  new MbcDescricaoPadrao();
		descricaoPadrao.setId(new MbcDescricaoPadraoId(espSeq, seqp));
		final DetachedCriteria criteria = this.criarCriteria(descricaoPadrao);
		descricaoPadrao = (MbcDescricaoPadrao)executeCriteriaUniqueResult(criteria);
		return descricaoPadrao; 
	}
	
	public Integer obterSequenceDescricaoPadrao(Short espSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoPadrao.class);
		criteria.setProjection(Projections.max(MbcDescricaoPadrao.Fields.ID_SEQP.toString()));
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.ID_ESP_SEQ.toString(), espSeq));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AghEspecialidades> buscarEspecialidadesDaDescricaoPadraoComProcedimentoAtivo() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoPadrao.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString() + "." + AghEspecialidades.Fields.SEQ.toString())), AghEspecialidades.Fields.SEQ.toString())
				.add(Projections.property(MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString() + "." + AghEspecialidades.Fields.SIGLA.toString()), AghEspecialidades.Fields.SIGLA.toString()));
		criteria.createAlias(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString() + "." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.setResultTransformer(Transformers.aliasToBean(AghEspecialidades.class));
		
		return executeCriteria(criteria);
	}

	public List<MbcProcedimentoCirurgicos> buscarProcCirurgicosAtivosDaDescricaoPadraoPelaEspecialidade(Short espSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoPadrao.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString() + "." + MbcProcedimentoCirurgicos.Fields.SEQ.toString())), MbcProcedimentoCirurgicos.Fields.SEQ.toString())
				.add(Projections.property(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString() + "." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		criteria.createAlias(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString() + "." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.AGH_ESPECIALIDADES.toString(), espSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(MbcProcedimentoCirurgicos.class));
		
		return executeCriteria(criteria);
	}

	public List<MbcDescricaoPadrao> buscarDescricaoPadraoPelaEspecialidadeEProcedimento(Short espSeq, Integer procSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcDescricaoPadrao.class);
		criteria.createAlias(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), MbcDescricaoPadrao.Fields.ESPECIALIDADES.toString(), JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.PROCEDIMENTO_CIRURGICOS.toString() + "." + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.AGH_ESPECIALIDADES.toString(), espSeq));
		criteria.add(Restrictions.eq(MbcDescricaoPadrao.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), procSeq));
		criteria.addOrder(Order.asc(MbcDescricaoPadrao.Fields.TITULO.toString()));
		
		return executeCriteria(criteria);
	}
}
