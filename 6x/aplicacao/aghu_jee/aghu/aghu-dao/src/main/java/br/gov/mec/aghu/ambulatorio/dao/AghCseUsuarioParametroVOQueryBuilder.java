package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CsePerfilProcessos;
import br.gov.mec.aghu.model.CsePerfisUsuarios;
import br.gov.mec.aghu.model.CseProcessos;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.MamTipoAtestadoProcesso;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class AghCseUsuarioParametroVOQueryBuilder extends QueryBuilder<DetachedCriteria>{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1723991832551735901L;

	private DetachedCriteria criteria;
	
	private String usuarioLogado;
	
	private Integer cConNumero;
	
	private final static String ALIAS_ROC = "ROC.";

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(CseProcessos.class, "ROC");
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setFiltro();
		setJoin();
		setProjecao();
		
	}
	
	private void setJoin(){
		criteria.createAlias(ALIAS_ROC + CseProcessos.Fields.PERFIL_PROCESSO.toString(), "PPP", JoinType.INNER_JOIN);
		criteria.createAlias("PPP."+ CsePerfilProcessos.Fields.PERFIL.toString(), "PER", JoinType.INNER_JOIN);
		criteria.createAlias("PER."+ Perfil.Fields.CSE_PERFIS_USUARIOS.toString(), "PFU", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_ROC + CseProcessos.Fields.TIPO_ATESTADO_PROCESSO.toString(), "TSP", JoinType.INNER_JOIN);
		criteria.createAlias("TSP."+ MamTipoAtestadoProcesso.Fields.MAM_TIPO_ATESTADOS.toString(), "TAS", JoinType.INNER_JOIN);
		criteria.createAlias("TAS."+ MamTipoAtestado.Fields.MAM_ATESTADO.toString(), "ATE", JoinType.INNER_JOIN);
}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq("ATE." + MamAtestados.Fields.CON_NUMERO.toString(), cConNumero));
		criteria.add(Restrictions.like("ATE." + MamAtestados.Fields.IND_PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull("ATE." + MamAtestados.Fields.DT_HR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.like("TAS." + MamTipoAtestado.Fields.IND_SITUACAO.toString(), "A"));
		criteria.add(Restrictions.like("TSP." + MamTipoAtestadoProcesso.Fields.IND_SITUACAO.toString(), "A"));
		criteria.add(Restrictions.eq(ALIAS_ROC  + CseProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PPP." + CsePerfilProcessos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PPP." + CsePerfilProcessos.Fields.IND_CONSULTA.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq("PFU." + CsePerfisUsuarios.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.like("PFU." + CsePerfisUsuarios.Fields.USR_ID.toString(), usuarioLogado));
	}
	
	private void setProjecao() {
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(ALIAS_ROC + CseProcessos.Fields.SEQ.toString()));
	}
	
	public DetachedCriteria build(String usuarioLogado, Integer cConNumero) {
		this.usuarioLogado = usuarioLogado;
		this.cConNumero = cConNumero;
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

	public Integer getcConNumero() {
		return cConNumero;
	}

	public void setcConNumero(Integer cConNumero) {
		this.cConNumero = cConNumero;
	}

}
