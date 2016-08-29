package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamEmgServEspecialidade;
import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.registrocolaborador.vo.ProgramaEspecialidadeVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * DAO da entidade MamEmgServidor
 * 
 * @author luismoura
 * 
 */
public class MamEmgServidorDAO extends BaseDao<MamEmgServidor> {
	private static final long serialVersionUID = -1758387083975690166L;

	/**
	 * Consulta que traz os dados dos servidores cadastrados
	 * 
	 * @param serVinCodigo
	 * @param matricula
	 * @param indSituacao
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<MamEmgServidor> pesquisarServidoresEmergencia(Short serVinCodigo, Integer matricula, String indSituacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		final DetachedCriteria criteria = this.montarPesquisaServidoresEmergencia(serVinCodigo, matricula, indSituacao);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Consulta que traz os dados dos servidores cadastrados
	 * 
	 * @param serVinCodigo
	 * @param matricula
	 * @param indSituacao
	 * @return
	 */
	public List<MamEmgServidor> pesquisarServidoresEmergencia(Short serVinCodigo, Integer matricula, String indSituacao) {
		final DetachedCriteria criteria = this.montarPesquisaServidoresEmergencia(serVinCodigo, matricula, indSituacao);
		return this.executeCriteria(criteria);
	}

	/**
	 * Executa o count da pesquisa dos servidores cadastrados
	 * 
	 * @param serVinCodigo
	 * @param matricula
	 * @param indSituacao
	 * @return
	 */
	public Long pesquisarServidoresEmergenciaCount(Short serVinCodigo, Integer matricula, String indSituacao) {
		final DetachedCriteria criteria = this.montarPesquisaServidoresEmergencia(serVinCodigo, matricula, indSituacao);
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Executa o exists da pesquisa dos servidores cadastrados
	 * 
	 * @param serVinCodigo
	 * @param matricula
	 * @return
	 */
	public Boolean pesquisarServidoresEmergenciaExist(Short serVinCodigo, Integer matricula) {
		final DetachedCriteria criteria = this.montarPesquisaServidoresEmergencia(serVinCodigo, matricula, null);
		return this.executeCriteriaExists(criteria);
	}

	/**
	 * Monta a pesquisa de dados dos servidores cadastrados
	 * 
	 * @param serVinCodigo
	 * @param matricula
	 * @param indSituacao
	 * @return
	 */
	private DetachedCriteria montarPesquisaServidoresEmergencia(Short serVinCodigo, Integer matricula, String indSituacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgServidor.class, "MamEmgServidor");
		if (serVinCodigo != null) {
			criteria.add(Restrictions.eq(MamEmgServidor.Fields.SER_VIN_CODIGO_BY_MAM_ESE_SER_FK1.toString(), serVinCodigo));
		}
		if (matricula != null) {
			criteria.add(Restrictions.eq(MamEmgServidor.Fields.SER_MATRICULA_BY_MAM_ESE_SER_FK1.toString(), matricula));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamEmgServidor.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		return criteria;
	}
	
	public ProgramaEspecialidadeVO obterNomeEspecialidadeServidorContratado(Integer matricula, Short vinculo){
		ProgramaEspecialidadeVO programaEspecialidadeVO = null;
		List<ProgramaEspecialidadeVO> retornoLista;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgServidor.class, "ESE");
		
		criteria.createAlias("ESE."  + MamEmgServidor.Fields.MAM_EMG_SERV_ESPECIALIDADEES.toString(),"SEE");
		criteria.createAlias("SEE."  + MamEmgServEspecialidade.Fields.MAM_EMG_ESPECIALIDADES.toString(),"MES");
		criteria.createAlias("MES."  + MamEmgEspecialidades.Fields.ESPECIALIDADE.toString(),"ESP");
		
		criteria.setProjection(
		    Projections.alias(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), ProgramaEspecialidadeVO.Fields.NOME_ESPECIALIDADE.toString())
	    );
		
		criteria.add(Restrictions.eq("ESE." + MamEmgServidor.Fields.SER_MATRICULA_BY_MAM_ESE_SER_FK1.toString(), matricula));
		criteria.add(Restrictions.eq("ESE." + MamEmgServidor.Fields.SER_VIN_CODIGO_BY_MAM_ESE_SER_FK1.toString(), vinculo));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProgramaEspecialidadeVO.class));
		
		retornoLista = executeCriteria(criteria);
		
		if(retornoLista != null && !retornoLista.isEmpty()){
			programaEspecialidadeVO = retornoLista.get(0);
		}
		
		return programaEspecialidadeVO;
	}

}
