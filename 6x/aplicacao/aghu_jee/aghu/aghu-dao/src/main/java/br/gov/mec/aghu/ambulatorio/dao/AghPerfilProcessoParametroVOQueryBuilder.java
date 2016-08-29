package br.gov.mec.aghu.ambulatorio.dao;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.ParametrosAghPerfilProcessoVO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CseAplicProcesso;
import br.gov.mec.aghu.model.CsePerfilProcessos;
import br.gov.mec.aghu.model.CsePerfisUsuarios;
import br.gov.mec.aghu.model.CseProcessos;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AghPerfilProcessoParametroVOQueryBuilder extends QueryBuilder<DetachedCriteria>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1180313873859638146L;
	
	private DetachedCriteria criteria;
	
	private String usuarioLogado;
	
	private final static String ALIAS_PPP = "ALIAS_PPP";
	
	private final static String PONTO = ".";
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(CseAplicProcesso.class, "APP");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setJoin();
		setProjecao();
		this.criteria.setResultTransformer(Transformers.aliasToBean(ParametrosAghPerfilProcessoVO.class));
	}
	
	private void setJoin(){
		criteria.createAlias("APP."+ CseAplicProcesso.Fields.CSE_PROCESSOS.toString(), "ROC", JoinType.INNER_JOIN);
		criteria.createAlias("ROC."+ CseProcessos.Fields.PERFIL_PROCESSO.toString(), ALIAS_PPP, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PPP+PONTO+ CsePerfilProcessos.Fields.PERFIL.toString(), "PER", JoinType.INNER_JOIN);
		criteria.createAlias("PER."+ Perfil.Fields.CSE_PERFIS_USUARIOS.toString(), "PFU", JoinType.INNER_JOIN);
}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq("APP." + CseAplicProcesso.Fields.IND_SITUACAO.toString(), "A"));
		criteria.add(Restrictions.eq(ALIAS_PPP+PONTO + CsePerfilProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("ROC." + CseProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PFU." + CsePerfisUsuarios.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.eq("APP." + CseAplicProcesso.Fields.SEQ_APLICACAOCODIGO.toString(), "MAMF_ATU_ATESTADOS"));
		criteria.add(Restrictions.eq("PFU." + CsePerfisUsuarios.Fields.USR_ID, usuarioLogado));
	}
	
	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(ALIAS_PPP+PONTO + CsePerfilProcessos.Fields.IND_CONSULTA.toString()), ParametrosAghPerfilProcessoVO.Fields.IND_CONSULTA.toString());
		projList.add(Projections.property(ALIAS_PPP+PONTO + CsePerfilProcessos.Fields.IND_EXECUTA.toString()), ParametrosAghPerfilProcessoVO.Fields.IND_EXECUTA.toString());
		projList.add(Projections.property(ALIAS_PPP+PONTO + CsePerfilProcessos.Fields.IND_ASSINA.toString()), ParametrosAghPerfilProcessoVO.Fields.IND_ASSINA.toString());
		criteria.setProjection(projList);
	}
	
	public DetachedCriteria build(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
		return super.build();
	}
	
	public DetachedCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetachedCriteria criteria) {
		this.criteria = criteria;
	}

	public String getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}


}
