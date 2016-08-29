package br.gov.mec.aghu.casca.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Acesso;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioTipoAcesso;

public class AcessoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<Acesso> {

	private static final long serialVersionUID = -4378486652776070024L;

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public List<Acesso> pesquisarAcessos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Usuario usuario,
			String enderecoOrigem, String mensagem, Boolean autorizado,DominioTipoAcesso tipoAcesso,
			Date dataInicial, Date dataFinal) {

		DetachedCriteria criteriaPesquisaAcessos = this.criarCriteriaPesquisaAcessos(usuario, enderecoOrigem,
						mensagem, autorizado,tipoAcesso, dataInicial, dataFinal);	

		return this.executeCriteria(criteriaPesquisaAcessos, firstResult, maxResult, orderProperty, asc);

	}

	private DetachedCriteria criarCriteriaPesquisaAcessos(Usuario usuario,	String enderecoOrigem, 
			String mensagem, Boolean autorizado, DominioTipoAcesso tipoAcesso,	Date dataInicial, Date dataFinal) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Acesso.class);
		criteria.createAlias(Acesso.Fields.USUARIO.toString(), "usuario");

		if (usuario != null) {
			criteria.add(Restrictions.eq(Acesso.Fields.USUARIO.toString(), usuario));
		}
		
		if (!StringUtils.isBlank(enderecoOrigem)) {
			criteria.add(Restrictions.like(Acesso.Fields.ENDERECO_ORIGEM.toString(), enderecoOrigem, MatchMode.ANYWHERE));
		}
		
		if (!StringUtils.isBlank(mensagem)) {
			criteria.add(Restrictions.like(Acesso.Fields.OBSERVACAO.toString(),	mensagem,MatchMode.ANYWHERE));
		}
		
		if (autorizado != null) {
			criteria.add(Restrictions.eq(Acesso.Fields.AUTORIZADO.toString(),autorizado));
		}
		
		if (tipoAcesso != null) {
			criteria.add(Restrictions.eq(Acesso.Fields.TIPO_ACESSO.toString(), tipoAcesso));
		}
		
		if (dataInicial != null && dataFinal == null){
			criteria.add(Restrictions.ge(Acesso.Fields.DATA.toString(), dataInicial));
		}else if (dataInicial == null && dataFinal != null){
			criteria.add(Restrictions.le(Acesso.Fields.DATA.toString(), dataFinal));
		}else if (dataInicial != null && dataFinal != null) {
			criteria.add(Restrictions.between(Acesso.Fields.DATA.toString(),dataInicial, dataFinal));
		}		
		return criteria;
	}

	public Long pesquisarAcessosCount(Usuario usuario,
			String enderecoOrigem, String mensagem, Boolean autorizado, DominioTipoAcesso tipoAcesso,
			Date dataInicial, Date dataFinal) {

		DetachedCriteria criteriaPesquisaAcessos = this
				.criarCriteriaPesquisaAcessos(usuario, enderecoOrigem,
						mensagem, autorizado, tipoAcesso,dataInicial, dataFinal);

		return this.executeCriteriaCount(criteriaPesquisaAcessos);

	}
	
	public Long pesquisarCountTentativasAcessoUltimoMinuto(String enderecoOrigem){
		Long retorno = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Acesso.class);
		
		criteria.add(Restrictions.eq(Acesso.Fields.AUTORIZADO.toString(),
				false));
		
		criteria.add(Restrictions.eq(Acesso.Fields.TIPO_ACESSO.toString(),
				DominioTipoAcesso.ENTRADA));
		
		Calendar ultimoMinuto = Calendar.getInstance();
		
		ultimoMinuto.set(Calendar.MINUTE, ultimoMinuto.get(Calendar.MINUTE) -1 );
		
		criteria.add(Restrictions.ge(Acesso.Fields.DATA.toString(),
				ultimoMinuto.getTime()));
		
		
		
		
		criteria.add(Restrictions.like(Acesso.Fields.ENDERECO_ORIGEM.toString(),
				enderecoOrigem, MatchMode.ANYWHERE));
		
		retorno = this.executeCriteriaCount(criteria);
		
		return retorno;
	}
	
	public void removerAcessos(Usuario usuario) {
		if (usuario != null) {
			StringBuilder hql = new StringBuilder();
			hql.append("delete from ")
			.append(Acesso.class.getName())
			
			.append(" where ")			
			.append(Acesso.Fields.USUARIO.toString())
			.append(" = :usuario ");
	
			Query query = createHibernateQuery(hql.toString());
			query.setParameter("usuario", usuario);
			query.executeUpdate();
		}
	}

}
