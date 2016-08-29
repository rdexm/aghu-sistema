package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ManterFavoritosUsuarioVO;

public class MptFavoritoServidorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptFavoritoServidor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8148465783652984074L;
	
	private static final String FAV_PONTO = "FAVORITOS.";

	/**
	 * #41730 - C8 - Consulta que retorna os valores favoritos do servidor selecionado para os campos de pesquisa por Tipo de Sessão e Sala.
	 * @param servidor
	 * @return
	 */
	public MptFavoritoServidor obterPorServidor(RapServidores servidor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritoServidor.class, "FAV");
		
		criteria.createAlias("FAV." + MptFavoritoServidor.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("FAV." + MptFavoritoServidor.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.INNER_JOIN);
		criteria.createAlias("FAV." + MptFavoritoServidor.Fields.SALA.toString(), "SAL", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		
		List<MptFavoritoServidor> lista = executeCriteria(criteria);
		
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	/**
	 * Consulta que retorna os valores favoritos do servidor selecionado para os campos de pesquisa por Tipo de Sessão e Sala. (C5)
	 * @param parametro
	 * @param codigoTipoSessao
	 * @return List<MptSalas>
	 */
	public MptFavoritoServidor buscarFavoritosServidor(RapServidores usuarioLogado){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritoServidor.class, "FS");
		
		criteria.add(Restrictions.eq("FS."+MptFavoritoServidor.Fields.SERVIDOR.toString(), usuarioLogado));
		
		return (MptFavoritoServidor) executeCriteriaUniqueResult(criteria);
	}
	
	// C3 #44466 - Monta criteria
	private DetachedCriteria criteriaObterFavoritoPorPesCodigo(RapServidores servidor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritoServidor.class, "FAVORITOS");
		
		criteria.createAlias(FAV_PONTO + MptFavoritoServidor.Fields.SERVIDOR.toString(), "SERVIDORES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FAV_PONTO + MptFavoritoServidor.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FAV_PONTO + MptFavoritoServidor.Fields.SALA.toString(), "SALAS", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(FAV_PONTO+MptFavoritoServidor.Fields.SEQ.toString()), ManterFavoritosUsuarioVO.Fields.FAV_SEQ.toString());
		projList.add(Projections.property("TPS."+MptTipoSessao.Fields.DESCRICAO.toString()), ManterFavoritosUsuarioVO.Fields.TPS_DESCRICAO.toString());
		projList.add(Projections.property("SALAS."+MptSalas.Fields.DESCRICAO.toString()), ManterFavoritosUsuarioVO.Fields.SALAS_DESCRICAO.toString());
		
		criteria.setProjection(projList);
		
		if (servidor != null) {
			criteria.add(Restrictions.eq("SERVIDORES."+RapServidores.Fields.PES_CODIGO.toString(), servidor.getPessoaFisica().getCodigo()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ManterFavoritosUsuarioVO.class));
		
		return criteria;
	}
	
	/**C3 #44466 - obtem um favoritos por codigo de pessoa fisica do servidor logado**/
	public ManterFavoritosUsuarioVO obterFavoritoPorPesCodigo(RapServidores servidor){
		return (ManterFavoritosUsuarioVO) executeCriteriaUniqueResult(criteriaObterFavoritoPorPesCodigo(servidor));
	}
	
	public MptFavoritoServidor pesquisarTipoSessaoUsuario(Integer matricula, Short vinCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritoServidor.class);
		criteria.add(Restrictions.eq(MptFavoritoServidor.Fields.SERVIDOR_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(MptFavoritoServidor.Fields.SERVIDOR_VIN_CODIGO.toString(), vinCodigo));
		return (MptFavoritoServidor) executeCriteriaUniqueResult(criteria);		
	}	
	/**
	 * #44249 C8
	 * Consulta que retorna os valores favoritos do servidor selecionado para os campos de pesquisa por Tipo de Sessão e Sala
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	public MptFavoritoServidor obterServidorSelecionadoPorMatriculaVinculo(Integer matricula, Short vinculo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritoServidor.class, "MFS");
		criteria.createAlias(MptFavoritoServidor.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.INNER_JOIN);
		criteria.createAlias(MptFavoritoServidor.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MFS."+MptFavoritoServidor.Fields.SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("MFS."+MptFavoritoServidor.Fields.SER_VIN_CODIGO.toString(), vinculo));
		return (MptFavoritoServidor) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MptFavoritoServidor> obterFavoritos(Integer matriculaServidor, Short vinCodigoServidor) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritoServidor.class);		
		criteria.add(Restrictions.eq(MptFavoritoServidor.Fields.SER_MATRICULA.toString(), matriculaServidor));
		criteria.add(Restrictions.eq(MptFavoritoServidor.Fields.SER_VIN_CODIGO.toString(), vinCodigoServidor));
		
		return executeCriteria(criteria);	
	}
}
