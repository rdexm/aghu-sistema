package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoConvenioOpms;
import br.gov.mec.aghu.model.MbcAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcGrupoAlcadaAvalOpms;
import br.gov.mec.aghu.model.MbcServidorAvalOpms;
import br.gov.mec.aghu.model.RapServidores;

public class MbcServidorAvalOpmsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcServidorAvalOpms> {

	private static final long serialVersionUID = -6279951350444124179L;
	
	public boolean isServidorCadastrado(MbcServidorAvalOpms servidor) {
		StringBuilder hql = new StringBuilder(200).append("Select 1 from  MbcServidorAvalOpms sao");
		hql.append(" WHERE sao.alcada = :alcada");
		hql.append(" AND sao.rapServidores  = :rapServidores");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("alcada", servidor.getAlcada());
		query.setParameter("rapServidores", servidor.getRapServidores());
		return !query.list().isEmpty();
	}
	
	public Integer maxSequenciaPorGrupoAlcada(MbcServidorAvalOpms servidor){
		StringBuilder hql = new StringBuilder(100).append("Select max(sao.sequencia) from  MbcServidorAvalOpms sao");
		hql.append(" WHERE sao.alcada = :alcada");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("alcada", servidor.getAlcada());
		Object result = query.uniqueResult();
		return result == null ? 0 :(Integer)query.uniqueResult();
	}

	public List<MbcServidorAvalOpms> buscaServidoresPorNivelAlcada(MbcAlcadaAvalOpms nivelAlcada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcServidorAvalOpms.class);
		criteria.createAlias(MbcServidorAvalOpms.Fields.RAP_SERVIDORES.toString(), "servidores");
		criteria.createAlias("servidores."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica");
		criteria.add(Restrictions.eq(MbcServidorAvalOpms.Fields.ALCADA.toString(), nivelAlcada));
		
		return executeCriteria(criteria);
	}

	public MbcServidorAvalOpms buscaServidoresPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcServidorAvalOpms.class);
		criteria.add(Restrictions.eq(MbcServidorAvalOpms.Fields.ID.toString(), seq));
		return (MbcServidorAvalOpms) executeCriteriaUniqueResult(criteria);
	}	

	
	// #37054 C01_SERV_ALCADA_1, C02_ SERV_ALCADA_2, C03_ SERV_ALCADA_3
	public List<MbcServidorAvalOpms> buscarServidoresPorEspConvenio(Short seqEspecialidade, Integer nivelAlcada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcServidorAvalOpms.class,"SAO");
		criteria.createAlias("SAO."+MbcServidorAvalOpms.Fields.ALCADA.toString(), "AAO");
		criteria.createAlias("AAO."+MbcAlcadaAvalOpms.Fields.GRUPO_ALCADA.toString(), "GAO");
		
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES_SEQ.toString(), seqEspecialidade));
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(), DominioTipoConvenioOpms.SUS));
		criteria.add(Restrictions.eq("AAO."+MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA .toString(), nivelAlcada));
		//TODO ALTERACAO
		criteria.add(Restrictions.eq("SAO."+MbcServidorAvalOpms.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public MbcServidorAvalOpms buscaServidorNivelAlcadaAtivo(MbcAlcadaAvalOpms alcada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcServidorAvalOpms.class);
		criteria.add(Restrictions.eq(MbcServidorAvalOpms.Fields.ALCADA.toString(), alcada));
		criteria.add(Restrictions.eq(MbcServidorAvalOpms.Fields.SITUACAO.toString(), DominioSituacao.A));
		return(MbcServidorAvalOpms) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MbcServidorAvalOpms> buscarServidores(Short seqEspecialidade, Integer nivelAlcada, RapServidores servidorLogado, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcServidorAvalOpms.class,"SAO");
		criteria.createAlias("SAO."+MbcServidorAvalOpms.Fields.ALCADA.toString(), "AAO");
		criteria.createAlias("AAO."+MbcAlcadaAvalOpms.Fields.GRUPO_ALCADA.toString(), "GAO");
		
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.AGH_ESPECIALIDADES_SEQ.toString(), seqEspecialidade));
		criteria.add(Restrictions.eq("GAO."+MbcGrupoAlcadaAvalOpms.Fields.TIPO_CONVENIO.toString(), DominioTipoConvenioOpms.SUS));
		criteria.add(Restrictions.eq("AAO."+MbcAlcadaAvalOpms.Fields.NIVEL_ALCADA .toString(), nivelAlcada));
		
		criteria.add(Restrictions.eq("SAO."+MbcServidorAvalOpms.Fields.SERVIDOR_CODIGO.toString(), servidorLogado.getId().getVinCodigo()));
		criteria.add(Restrictions.eq("SAO."+MbcServidorAvalOpms.Fields.SERVIDOR_MATRICULA.toString(), servidorLogado.getId().getMatricula()));
		
		if(situacao != null){
			criteria.add(Restrictions.eq("SAO."+MbcServidorAvalOpms.Fields.SITUACAO.toString(), situacao));
		}	
		return executeCriteria(criteria);
	}
}
