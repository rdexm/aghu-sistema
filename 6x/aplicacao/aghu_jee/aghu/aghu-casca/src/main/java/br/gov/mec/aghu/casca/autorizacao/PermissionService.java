package br.gov.mec.aghu.casca.autorizacao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisPermissoes;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

/**
 * Classe responsável por fazer a verificação de permissão (target/action) para
 * um usuário
 * 
 * @author geraldo
 * 
 */
@Stateless
@Remote (IPermissionService.class)
public class PermissionService implements IPermissionService {


	/**
	 * 
	 */
	private static final long serialVersionUID = 146037184236684192L;
	
	
	@Inject
	private DataAccessService dataAcess;

	/**
	 * Dado o login do usuario, verificar se ele tem uma determinada permissão
	 * 
	 * @param login
	 *            o login do usuário
	 * @param permissao
	 *            o nome da permissão a ser testada
	 * @return true se o usuario tem a permissão, false caso contrário
	 */
	
	public boolean usuarioTemPermissao(String login, String componente,	String metodo) {

		List<PermissoesComponentes> usuarioPermissoes = this.usuarioPermissoes(login, componente, metodo);

		return !usuarioPermissoes.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<PermissoesComponentes> usuarioPermissoes(String login, String componente,	String metodo) {

		Criteria criteria = dataAcess.createCriteria(PermissoesComponentes.class);

		Criteria criteriaComponente = criteria.createCriteria(PermissoesComponentes.Fields.COMPONENTE.toString());
		criteriaComponente.add(Restrictions.eq(Componente.Fields.NOME.toString(), componente));

		criteria.createCriteria(PermissoesComponentes.Fields.METODO.toString(),	"metodo")
				.add(Restrictions.eq(Metodo.Fields.NOME.toString(), metodo))
				.add(Restrictions.eq(Metodo.Fields.ATIVO.toString(), true));

		// trunca o horário para aproveitar o cache
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		criteria.createCriteria(
				PermissoesComponentes.Fields.PERMISSAO.toString(), "permissao")
				.add(Restrictions.eq(Permissao.Fields.ATIVO.toString(),	DominioSituacao.A))
				.createCriteria(Permissao.Fields.PERFIS_PERMISSOES.toString(),	"perfilPermissao")
				.createCriteria(PerfisPermissoes.Fields.PERFIL.toString(), "perfil")
				.add(Restrictions.eq(Perfil.Fields.SITUACAO.toString(),	DominioSituacao.A))
				.createCriteria(Perfil.Fields.PERFIS_USUARIOS.toString(),"perfilUsuario")
				.add(Restrictions.or(
						Restrictions.isNull("perfilUsuario."
								+ PerfisUsuarios.Fields.DATA_EXPIRACAO
										.toString()),
						Restrictions.gt(
								"perfilUsuario."
										+ PerfisUsuarios.Fields.DATA_EXPIRACAO
												.toString(), today)))
				.createCriteria(PerfisUsuarios.Fields.USUARIO.toString())
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(),Boolean.TRUE))
				.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login).ignoreCase());

		criteria.setCacheable(true);
	
		return criteria.list();
	}

}
