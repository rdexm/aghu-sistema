package br.gov.mec.aghu.transplante.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class MtxOrigensDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxOrigens> {

	private static final long serialVersionUID = 822186225970772831L;
	//#41770 - Consultar Critérios de Priorização de Atendimento
    private DetachedCriteria criteriaMtxOrigensPorSeqCodDescricao(String pesquisa){
    	
          DetachedCriteria criteria = DetachedCriteria.forClass(MtxOrigens.class);
          criteria.add(Restrictions.eq(MtxOrigens.Fields.SITUACAO.toString(),DominioSituacao.A));
          if(StringUtils.isNotEmpty(pesquisa)){
                 if(CoreUtil.isNumeroInteger(pesquisa)){
                        criteria.add(Restrictions.eq(MtxOrigens.Fields.SEQ.toString(), Integer.valueOf(pesquisa)));
                 }else{
                        criteria.add(Restrictions.ilike(MtxOrigens.Fields.DESCRICAO.toString(), pesquisa, MatchMode.ANYWHERE));
                 }            
          }   
          return criteria;
    }
    
    /**
     * #41768 C1 - Realiza a pesquisa de CIDS por sequencia, codigo ou descricao.       
     **/
    public List<MtxOrigens> pesquisarMtxOrigensPorSeqCodDescricao(String pesquisa){
          DetachedCriteria criteria = criteriaMtxOrigensPorSeqCodDescricao(pesquisa);
          return executeCriteria(criteria, 0, 100, MtxOrigens.Fields.DESCRICAO.toString(), true);
    }
    
    public Long pesquisarMtxOrigensPorSeqCodDescricaoCount(String pesquisa){
          DetachedCriteria criteria = criteriaMtxOrigensPorSeqCodDescricao(pesquisa);
          return executeCriteriaCount(criteria);
    }
    
	//#46359 pesquisa de MtxOrigens por situacao e/ou descricao
    public List<MtxOrigens> pesquisarMtxOrigensPorSituacaoDesc(MtxOrigens mtxOrigens, Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
    	
          DetachedCriteria criteria = DetachedCriteria.forClass(MtxOrigens.class);
          if(mtxOrigens.getSituacao() != null){
        	 criteria.add(Restrictions.eq(MtxOrigens.Fields.SITUACAO.toString(), mtxOrigens.getSituacao())); 
          }
          if(StringUtils.isNotEmpty(mtxOrigens.getDescricao())){
        	  criteria.add(Restrictions.ilike(MtxOrigens.Fields.DESCRICAO.toString(), mtxOrigens.getDescricao(), MatchMode.ANYWHERE));
          }
          return executeCriteria(criteria, firstResult, maxResults, MtxOrigens.Fields.DESCRICAO.toString(), asc);
    }
    
    public Long pesquisarMtxOrigensPorSituacaoDescCount(MtxOrigens mtxOrigens, boolean validaInsercao){
    	
        DetachedCriteria criteria = DetachedCriteria.forClass(MtxOrigens.class);
        if(validaInsercao){//Se estiver validando a insercao
        	TipoCriteria.NE.addRestriction(criteria, MtxOrigens.Fields.SEQ.toString(), mtxOrigens.getSeq());
        	TipoCriteria.EQ.addRestriction(criteria, MtxOrigens.Fields.DESCRICAO.toString(), mtxOrigens.getDescricao());
        }else{//Se for somente uam chamada do DataModel para pesquisa
        	TipoCriteria.EQ.addRestriction(criteria, MtxOrigens.Fields.SITUACAO.toString(), mtxOrigens.getSituacao());
        	TipoCriteria.ILIKE.addRestriction(criteria, MtxOrigens.Fields.DESCRICAO.toString(), mtxOrigens.getDescricao());
        }
        return executeCriteriaCount(criteria);
  }
  //#46359 gravar ou atualizar de Origem de Paciente
    public void gravarAtualizarOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException{
    	mtxOrigens.setDescricao(mtxOrigens.getDescricao().toUpperCase());
    	if(mtxOrigens.getSeq() == null){//gravar
    		this.persistir(mtxOrigens);
    	}else{
    		this.atualizar(mtxOrigens);
    	}
    }
  //#46359 exluir de Origem de Paciente
    public void excluirOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException{
    	MtxOrigens mtxOrigemTemp =  this.obterPorChavePrimaria(mtxOrigens.getSeq());
		this.remover(mtxOrigemTemp);
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
