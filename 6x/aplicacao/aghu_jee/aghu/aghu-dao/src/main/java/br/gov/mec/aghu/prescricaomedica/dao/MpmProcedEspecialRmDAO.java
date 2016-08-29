package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmProcedEspecialRmId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;

public class MpmProcedEspecialRmDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmProcedEspecialRm> {

	private static final long serialVersionUID = 5994942130175616623L;

	/**
	 * Lista os procedimentos especiais ativos vinculados ao procedimento especial diverso
	 * 
	 * @author rogeriovieira (o método já existia e foi reutilizado)
	 * @param pedSeq identificador do procedimento especial diverso
	 * @return procedimentos especiais
	 */
	public List<MpmProcedEspecialRm> listarProcedimentosRmAtivosPeloPedSeq(Short pedSeq) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmProcedEspecialRm.class);

		criteria.add(Restrictions.eq(MpmProcedEspecialRm.Fields.PED_SEQ
				.toString(), pedSeq));
		criteria.add(Restrictions.eq(MpmProcedEspecialRm.Fields.SITUACAO
				.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

	private MpmProcedEspecialRm obterOriginal(Short pedSeq, Integer matCodigo) {
		StringBuilder hql = new StringBuilder(100);
		hql.append("select o.").append(MpmProcedEspecialRm.Fields.ID.toString());
		hql.append(", o.").append(MpmProcedEspecialRm.Fields.MATERIAL.toString());
		hql.append(", o.").append(MpmProcedEspecialRm.Fields.SERVIDOR.toString());
		hql.append(", o.").append(MpmProcedEspecialRm.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(MpmProcedEspecialRm.Fields.SITUACAO.toString());
		hql.append(" from ").append(MpmProcedEspecialRm.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(MpmProcedEspecialRm.Fields.PED_SEQ.toString()).append(" = :pedSeq ");
		hql.append(" and o.").append(MpmProcedEspecialRm.Fields.MAT_CODIGO.toString()).append(" = :matCodigo ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("pedSeq", pedSeq);
		query.setParameter("matCodigo", matCodigo);
		
		
		MpmProcedEspecialRm retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new MpmProcedEspecialRm();

			
			retorno.setId( (MpmProcedEspecialRmId) campos[0]);
			retorno.setMaterial((ScoMaterial) campos[1]);
			retorno.setServidor((RapServidores) campos[2]);
			retorno.setCriadoEm((Date) campos[3]);
			retorno.setSituacao((DominioSituacao) campos[4]);
		}		
		
		return retorno;
	}
	
	@Override
	public MpmProcedEspecialRm obterOriginal(MpmProcedEspecialRm elemento) {
		if (elemento != null && elemento.getId() != null) {
			MpmProcedEspecialRmId intervaloId = elemento.getId();
			return this.obterOriginal(intervaloId.getPedSeq(), intervaloId.getMatCodigo());
		} else {
			return null;
		}
	}
	
	public List<MpmProcedEspecialRm> listarProcedimentosRmPeloPedSeq(Short pedSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialRm.class);
		criteria.createAlias(MpmProcedEspecialRm.Fields.MATERIAL.toString(), "MATA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmProcedEspecialRm.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmProcedEspecialRm.Fields.PED_SEQ.toString(), pedSeq));
		return executeCriteria(criteria);
	}
	
	public List<Integer> listarProcedimentosRmPeloPedSeqII(Short pedSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmProcedEspecialRm.class);
		criteria.add(Restrictions.eq(MpmProcedEspecialRm.Fields.PED_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.eq(MpmProcedEspecialRm.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	
}
