package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CseCategoriaPerfil;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;

public class CseCategoriaPerfilDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CseCategoriaPerfil> {
	
	private static final long serialVersionUID = -1863440119521977729L;

	@Inject
	private PerfilDAO perfilDAO;

	public Boolean validarPermissaoAmbulatorioItensPorServidor(String login, DominioAnamneseEvolucao dominio) {

		String[] perfis = obterPerfisPorUsuario(login);
		if (perfis == null) {
			return false;
		}				

		DetachedCriteria criteria = DetachedCriteria.forClass(CseCategoriaPerfil.class, "categoriaPerfil");
		criteria.add(Restrictions.in(CseCategoriaPerfil.Fields.PERFIL.toString(), perfis));
		criteria.createAlias("categoriaPerfil."+CseCategoriaPerfil.Fields.CSE_CATEGORIA_PROFISSIONAL.toString(),"categoriaProfissional");
		
		if (DominioAnamneseEvolucao.A.equals(dominio)){
			criteria.createAlias("categoriaProfissional."+CseCategoriaProfissional.Fields.TIPO_ITEM_ANAMNESE.toString(), "tipoItemAnamnese");
			criteria.add(Restrictions.eq("tipoItemAnamnese."+MamTipoItemAnamneses.Fields.IND_SITUACAO, DominioSituacao.A));
		}else if (DominioAnamneseEvolucao.E.equals(dominio)){
			criteria.createAlias("categoriaProfissional."+CseCategoriaProfissional.Fields.TIPO_ITEM_EVOLUCAO.toString(), "tipoItemEvolucao");
			criteria.add(Restrictions.eq("tipoItemEvolucao."+MamTipoItemEvolucao.Fields.IND_SITUACAO, DominioSituacao.A));
		}
		
		criteria.add(Restrictions.eq("categoriaProfissional."+CseCategoriaProfissional.Fields.IND_SITUACAO, DominioSituacao.A));
		criteria.add(Restrictions.eq("categoriaPerfil."+CseCategoriaPerfil.Fields.IND_SITUACAO, DominioSituacao.A));
	
		return this.executeCriteriaCount(criteria) > 0;
	}	

	private String[] obterPerfisPorUsuario(String login)  {
		List<String> perfisUsuario = perfilDAO.obterNomePerfisPorUsuario(login);
		if (perfisUsuario == null || perfisUsuario.isEmpty()) {
			return null;
		}
		
		return perfisUsuario.toArray(new String[perfisUsuario.size()]);		
	}	
}
