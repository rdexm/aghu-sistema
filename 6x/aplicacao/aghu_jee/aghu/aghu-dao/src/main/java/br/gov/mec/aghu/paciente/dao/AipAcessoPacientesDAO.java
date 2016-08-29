package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipAcessoPacientes;
import br.gov.mec.aghu.model.RapServidores;

public class AipAcessoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipAcessoPacientes> {

	private static final long serialVersionUID = -6407233068053889518L;

	public List<AipAcessoPacientes> listarAcessosPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAcessoPacientes.class);

		criteria.add(Restrictions.eq(AipAcessoPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	/**
	 * Metodo que implementa um criteria comum aos cursores:
	 * 
	 *   - c_acesso_com
	 *   - c_acesso_sem
	 *   - c_acesso_pac
	 * 
	 * @param pacCodigo
	 * @param indAcessoSemAtend
	 * @param indAcessoComAtend
	 * @return
	 */
	private DetachedCriteria criarCiteriaPesquisarAcessoPaciente(Integer pacCodigo, DominioSimNao indAcessoSemAtend, DominioSimNao indAcessoComAtend, String loginUsuario) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "ACX");
		
		dc.add(Restrictions.eq("ACX.".concat(AipAcessoPacientes.Fields.PAC_CODIGO.toString()), pacCodigo));
		dc.add(Restrictions.eq("ACX.".concat(AipAcessoPacientes.Fields.IND_SITUACAO.toString()), DominioSituacao.A));
		
		if (indAcessoComAtend != null) {
			dc.add(Restrictions.eq("ACX.".concat(AipAcessoPacientes.Fields.IND_ACESSO_COM_ATEND.toString()), indAcessoComAtend));			
		}
		
		if (indAcessoSemAtend != null) {
			dc.add(Restrictions.eq("ACX.".concat(AipAcessoPacientes.Fields.IND_ACESSO_SEM_ATEND.toString()), indAcessoSemAtend));
		}
		
		if (loginUsuario != null) {
			dc.createAlias("ACX.".concat(AipAcessoPacientes.Fields.SERVIDOR_ACESSADO.toString()), "USR");
			dc.add(Restrictions.or(
					Restrictions.eq("USR.".concat(RapServidores.Fields.USUARIO.toString()), loginUsuario.toLowerCase()), 
					Restrictions.eq("USR.".concat(RapServidores.Fields.USUARIO.toString()), loginUsuario.toUpperCase())));
		}
		
		dc.setProjection(Projections.countDistinct("ACX.".concat(AipAcessoPacientes.Fields.SEQ.toString())));
		
		return dc;
	}
	
	/**
	 * Metodo implementa o cursor c_acesso_com
	 * @param pacCodigo
	 * @param loginUsuario
	 * @return
	 */
	public Long pesquisarAcessoPacienteComCount(Integer pacCodigo, String loginUsuario) {
		return executeCriteriaCount(criarCiteriaPesquisarAcessoPaciente(pacCodigo, null, DominioSimNao.S, loginUsuario));
	}
	
	/**
	 * Metodo implementa o cursor c_acesso_sem
	 * @param pacCodigo
	 * @param loginUsuario
	 * @return
	 */
	public Long pesquisarAcessoPacienteSemCount(Integer pacCodigo, String loginUsuario) {
		return executeCriteriaCount(criarCiteriaPesquisarAcessoPaciente(pacCodigo, DominioSimNao.S, null, loginUsuario));
	}
	
	/**
	 * Metodo implementa o cursor c_acesso_pac
	 * @param pacCodigo
	 * @return
	 */
	public Long pesquisarAcessoPacientePacCount(Integer pacCodigo) {
		return executeCriteriaCount(criarCiteriaPesquisarAcessoPaciente(pacCodigo, null, DominioSimNao.S, null));
	}

}
