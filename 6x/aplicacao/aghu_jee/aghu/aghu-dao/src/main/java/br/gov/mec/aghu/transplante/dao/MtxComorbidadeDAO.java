package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class MtxComorbidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxComorbidade> {

	private static final long serialVersionUID = 6765230090731612009L;
	
	public List<MtxComorbidade> pesquisarComorbidadePorDescricaoTipoAtivo(MtxComorbidade mtxComorbidade, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxComorbidade.class, "C");
		criteria.createAlias("C."+MtxComorbidade.Fields.CID_SEQ.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.CID_SEQ.toString(), mtxComorbidade.getCidSeq());
		TipoCriteria.ILIKE.addRestriction(criteria, MtxComorbidade.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao());
		TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.TIPO.toString(), mtxComorbidade.getTipo());
		TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.SITUACAO.toString(), mtxComorbidade.getSituacao());
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarComorbidadePorDescricaoTipoAtivoCount(MtxComorbidade mtxComorbidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxComorbidade.class);
		TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.CID_SEQ.toString(), mtxComorbidade.getCidSeq());
		TipoCriteria.ILIKE.addRestriction(criteria, MtxComorbidade.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao());
		TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.TIPO.toString(), mtxComorbidade.getTipo());
		TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.SITUACAO.toString(), mtxComorbidade.getSituacao());
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MtxComorbidade> pesquisarDoenca(MtxComorbidade mtxComorbidade){
		return executeCriteria(criarCriteria(mtxComorbidade));
	}
	
	public Long pesquisarDoencaCount(MtxComorbidade mtxComorbidade){
		return executeCriteriaCount(criarCriteria(mtxComorbidade));
	}
	
	public List<MtxComorbidade> pesquisarComorbidadePorTipoDescricaoCid(MtxComorbidade mtxComorbidade){
		DetachedCriteria criteria = criarCriteria(mtxComorbidade);
		criteria.add(Restrictions.or(Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), mtxComorbidade.getTipo()),
				Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.X)));
		criteria.addOrder(Order.asc("C."+MtxComorbidade.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("CID."+AghCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarComorbidadePorTipoDescricaoCidCount(MtxComorbidade mtxComorbidade){
		DetachedCriteria criteria = criarCriteria(mtxComorbidade);
		criteria.add(Restrictions.or(Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), mtxComorbidade.getTipo()),
				Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.X)));
		criteria.addOrder(Order.asc("C."+MtxComorbidade.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("CID."+AghCid.Fields.DESCRICAO.toString()));
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria criarCriteria(MtxComorbidade mtxComorbidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxComorbidade.class, "C");
		criteria.createAlias("C."+MtxComorbidade.Fields.CID_SEQ.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		StringBuilder cmbSeq = new StringBuilder(70);
		cmbSeq.append(" this_.").append(MtxComorbidade.Fields.SEQ.toString())
		.append(" like ").append("'%").append(mtxComorbidade.getDescricao()).append("%'")
		.append(" or ").append("cid1_.").append(AghCid.Fields.CODIGO.toString())
		.append(" like ").append("'%").append(mtxComorbidade.getDescricao()).append("%'");
		
		if(mtxComorbidade.getDescricao().matches("-?\\d+(\\.\\d+)?") && mtxComorbidade.getDescricao().length() < 9){//quantidade maxima de digitos de um inteiro
			criteria.add(Restrictions.or(Restrictions.ilike("CID."+AghCid.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao(),  MatchMode.ANYWHERE),
				Restrictions.ilike("C."+MtxComorbidade.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao(),  MatchMode.ANYWHERE), 
				Restrictions.sqlRestriction(cmbSeq.toString())));
		}else{
			criteria.add(Restrictions.or(Restrictions.ilike("CID."+AghCid.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao(),  MatchMode.ANYWHERE),
					Restrictions.ilike(MtxComorbidade.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao(),  MatchMode.ANYWHERE)));
		}
		TipoCriteria.EQ.addRestriction(criteria, MtxComorbidade.Fields.SITUACAO.toString(), DominioSituacao.A);
		return criteria;
	}
	
	public List<MtxTransplantes> carregarDadosPaciente(Integer prontuario, Object tipo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class);
		criteria.createAlias(MtxTransplantes.Fields.DOADOR.toString(), "DOA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MtxTransplantes.Fields.RECEPTOR.toString(), "REC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.or(Restrictions.eq("REC."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario),
				Restrictions.eq("DOA."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario)));
		if(tipo instanceof DominioTipoOrgao){
			criteria.add(Restrictions.eq(MtxTransplantes.Fields.TIPO_ORGAO.toString(), ((DominioTipoOrgao) tipo)));
		}else if(tipo instanceof DominioSituacaoTmo){
			criteria.add(Restrictions.eq(MtxTransplantes.Fields.TIPO_TMO.toString(), ((DominioSituacaoTmo) tipo)));
		}
		criteria.setProjection(
				Projections.distinct(
						Projections.projectionList()
						.add(Projections.property(MtxTransplantes.Fields.DOADOR.toString()).as(MtxTransplantes.Fields.DOADOR.toString()))
						.add(Projections.property(MtxTransplantes.Fields.RECEPTOR.toString()).as(MtxTransplantes.Fields.RECEPTOR.toString()))
						.add(Projections.property(MtxTransplantes.Fields.TIPO_ORGAO.toString()).as(MtxTransplantes.Fields.TIPO_ORGAO.toString()))
						.add(Projections.property(MtxTransplantes.Fields.TIPO_TMO.toString()).as(MtxTransplantes.Fields.TIPO_TMO.toString()))
						.add(Projections.property(MtxTransplantes.Fields.TIPO_ALOGENICO.toString()).as(MtxTransplantes.Fields.TIPO_ALOGENICO.toString()))
						.add(Projections.property(MtxTransplantes.Fields.SITUACAO.toString()).as(MtxTransplantes.Fields.SITUACAO.toString()))
				));
		criteria.setResultTransformer(Transformers.aliasToBean(MtxTransplantes.class));
		return executeCriteria(criteria);
	}
	
	public Long validarInclusaoComorbidade(MtxComorbidade mtxComorbidade){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxComorbidade.class);
		if(TipoCriteria.EQ.addRestriction(criteria,  MtxComorbidade.Fields.CID_SEQ.toString(), mtxComorbidade.getCidSeq())){//if it is null, add descricao as filter
			TipoCriteria.EQ.addRestriction(criteria, MtxComorbidade.Fields.DESCRICAO.toString(), mtxComorbidade.getDescricao());
		}
		if(mtxComorbidade.getTipo() == DominioTipoTransplante.X){
			criteria.add(Restrictions.or(Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.O),
					Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.M), Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.X)));
		}else{
			criteria.add(Restrictions.or(Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), mtxComorbidade.getTipo()),
					Restrictions.eq(MtxComorbidade.Fields.TIPO.toString(), DominioTipoTransplante.X)));
		}
		TipoCriteria.NE.addRestriction(criteria, MtxComorbidade.Fields.SEQ.toString(), mtxComorbidade.getSeq());
		
		return executeCriteriaCount(criteria);
	}
	
	public void gravarAtualizarComorbidade(MtxComorbidade mtxComorbidade) throws ApplicationBusinessException, BaseException{
		if(mtxComorbidade.getSeq() == null){//gravar
			persistir(mtxComorbidade);
		}else{
			atualizar(mtxComorbidade);
		}
		flush();
	}
	

	
 enum TipoCriteria {
	 NE{
		@Override
		public boolean addRestriction(DetachedCriteria criteria, String propertyName, Object value) {
			if(value != null){
				criteria.add(Restrictions.ne(propertyName, value));
				return false;
			}
			return true;
		}
	},
	EQ{
		@Override
		public boolean addRestriction(DetachedCriteria criteria, String propertyName, Object value) {
			if(value != null){
				criteria.add(Restrictions.eq(propertyName, value instanceof  String ? ((String) value).toUpperCase() : value ));
				return false;
			}
			return true;
		}
	},
	ILIKE {
		@Override
		public boolean addRestriction(DetachedCriteria criteria, String propertyName,	Object value) {
			if(value != null){
				criteria.add(Restrictions.ilike(propertyName, ((String) value).toUpperCase(), MatchMode.ANYWHERE));
				return false;
			}
			return true;
		}
	};	
	TipoCriteria() {
		
	}
	public abstract boolean addRestriction(DetachedCriteria criteria, String propertyName, Object value);
 }
 
}
