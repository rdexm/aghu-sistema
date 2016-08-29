package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimentoId;
import br.gov.mec.aghu.model.RapServidores;

public class MpmTipoModoUsoProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTipoModoUsoProcedimento> {


	
	private static final long serialVersionUID = 781516460051632286L;

	private MpmTipoModoUsoProcedimento obterOriginal(Short pedSeq, Short seqp) {
		StringBuilder hql = new StringBuilder(100);
		hql.append("select o.").append(MpmTipoModoUsoProcedimento.Fields.ID.toString());
		hql.append(", o.").append(MpmTipoModoUsoProcedimento.Fields.SERVIDOR.toString());
		hql.append(", o.").append(MpmTipoModoUsoProcedimento.Fields.DESCRICAO.toString());
		hql.append(", o.").append(MpmTipoModoUsoProcedimento.Fields.IND_EXIGE_QUANTIDADE.toString());
		hql.append(", o.").append(MpmTipoModoUsoProcedimento.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(MpmTipoModoUsoProcedimento.Fields.IND_SITUACAO.toString());
		hql.append(" from ").append(MpmTipoModoUsoProcedimento.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(MpmTipoModoUsoProcedimento.Fields.PED_SEQ.toString()).append(" = :pedSeq ");
		hql.append(" and o.").append(MpmTipoModoUsoProcedimento.Fields.SEQP.toString()).append(" = :seqp ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("pedSeq", pedSeq);
		query.setParameter("seqp", seqp);
		
		
		MpmTipoModoUsoProcedimento retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new MpmTipoModoUsoProcedimento();

			
			retorno.setId( (MpmTipoModoUsoProcedimentoId) campos[0]);
			retorno.setServidor((RapServidores) campos[1]);
			retorno.setDescricao((String) campos[2]);
			retorno.setIndExigeQuantidade((Boolean) campos[3]);
			retorno.setCriadoEm((Date) campos[4]);
			retorno.setIndSituacao((DominioSituacao) campos[5]);
		}		
		
		return retorno;
	}


	private DetachedCriteria obterCriteriaTipoModoUsoProcedimento(){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoModoUsoProcedimento.class);
		return criteria;
	}

	public Short obterMaxSeqP(Short proced){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoModoUsoProcedimento.class);
		
		criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.PED_SEQ.toString(), proced));
		criteria.setProjection(Projections.max(MpmTipoModoUsoProcedimento.Fields.SEQP.toString()));
		
		Short result = (Short) executeCriteriaUniqueResult(criteria);
		
		return result != null ? result : Short.valueOf("0");
	}

	/*
	 * select TUP.PED_SEQ 
      ,TUP.SEQP  
      ,TUP.DESCRICAO 
      ,TUP.UMM_SEQ  
      ,UMM.DESCRICAO  
	from   MPM_TIPO_MOD_USO_PROCEDIMENTOS TUP
      ,MPM_UNIDADE_MEDIDA_MEDICAS UMM
	where  (TUP.PED_SEQ = :PPR.ped_seq  and  TUP.IND_SITUACAO = 'A')
	and    UMM.SEQ (+) = TUP.UMM_SEQ 
	order by  TUP.DESCRICAO asc
	OBS: (+) LEFT OUTER JOIN
	obs: PPR.ped_seq obtido de outra SB
	 */
	public List<MpmTipoModoUsoProcedimento> buscaMpmTipoModoUsoProcedimento(Object objPesquisa,MpmProcedEspecialDiversos procedEspecial){
		List<MpmTipoModoUsoProcedimento> list;
		String srtPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = obterCriteriaTipoModoUsoProcedimento();

		if(StringUtils.isNotBlank(srtPesquisa)){
			if(CoreUtil.isNumeroShort(objPesquisa)){
				criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.SEQP.toString(), Short.valueOf(srtPesquisa)));	    
			}else{
				criteria.add(Restrictions.ilike(MpmTipoModoUsoProcedimento.Fields.DESCRICAO.toString(),srtPesquisa,MatchMode.ANYWHERE));	
			}
		}	
		criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if(procedEspecial != null){
			criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.PED_SEQ.toString(), Short.valueOf(procedEspecial.getSeq().toString())));			    
		}

		criteria.addOrder(Order.asc(MpmTipoModoUsoProcedimento.Fields.DESCRICAO.toString()));

		list = super.executeCriteria(criteria);

		return list;
	}
	
	public List<MpmTipoModoUsoProcedimento> buscarModosUsoPorProcedimentoEspecial(Short procedimentoEspecialId) {
		DetachedCriteria criteria = obterCriteriaTipoModoUsoProcedimento();

		criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.PED_SEQ.toString(), procedimentoEspecialId));
		List<MpmTipoModoUsoProcedimento> result = executeCriteria(criteria);
		
		for (MpmTipoModoUsoProcedimento mpmTipoModoUsoProcedimento : result) {
			Hibernate.initialize(mpmTipoModoUsoProcedimento.getServidor().getPessoaFisica());
		}
		
		return result;
	}

	/**
	 * Obtém um tipo modo de uso de procedimento pelo seu ID.
	 * 
	 * bsoliveira 26/10/2010
	 * 
	 * @param {Short} pedSeq
	 * @param {Short} seqp
	 * 
	 * @return MpmTipoModoUsoProcedimento
	 */
	public MpmTipoModoUsoProcedimento obterTipoModoUsoProcedimentoPeloId(Short pedSeq, Short seqp) {

		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmTipoModoUsoProcedimento.class);
		criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.PED_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.eq(MpmTipoModoUsoProcedimento.Fields.SEQP.toString(), seqp));
		MpmTipoModoUsoProcedimento retorno = (MpmTipoModoUsoProcedimento) this
		.executeCriteriaUniqueResult(criteria);

		return retorno;

	}

	@Override
	public MpmTipoModoUsoProcedimento obterOriginal(MpmTipoModoUsoProcedimento elemento) {
		if (elemento != null && elemento.getId() != null) {
			MpmTipoModoUsoProcedimentoId intervaloId = elemento.getId();
			return this.obterOriginal(intervaloId.getPedSeq(), intervaloId.getSeqp());
		} else {
			return null;
		}
	}
	
	

}
