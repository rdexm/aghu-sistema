package br.gov.mec.aghu.paciente.dao;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipGrupoFamiliar;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.vo.PacienteGrupoFamiliarVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class AipGrupoFamiliarDAO extends BaseDao<AipGrupoFamiliar> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2572445095877330842L;
	private static final String PAC = "PAC";
	private static final String PONTO = ".";
	private static final String GFP = "GFP";
	private static final String END = "END";
	private static final String UF = "UF";

	
	/**
	 * #42462
	 * RN01 Criteria todos os familiares vinculados ao mesmo grupo familiar do contexto
	 * @author rodrigo.saraujo
	 * @param pacCodigo
	 * @return
	 */
	private DetachedCriteria pesquisarFamiliaresVinculados(AipPacientes pacienteContexto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		projectionsGrupoFamiliar(criteria);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.GRUPO_FAMILIAR_PACIENTE.toString(), GFP, JoinType.INNER_JOIN);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.ENDERECOS.toString(), END, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(END + PONTO + AipEnderecosPacientes.Fields.UF.toString(), UF, JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.ne(PAC + PONTO + AipPacientes.Fields.CODIGO, pacienteContexto.getCodigo()));
		criteria.add(Restrictions.eq(GFP + PONTO + AipGrupoFamiliarPacientes.Fields.AGF_SEQ, pacienteContexto.getGrupoFamiliarPaciente().getAgfSeq()));
		criteria.add(Restrictions.or(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.IND_PADRAO, DominioSimNao.S),Restrictions.isNull(END + PONTO + AipEnderecosPacientes.Fields.IND_PADRAO)));
		return criteria;
	}
	

	/**
	 * #42462
	 * RN01 listar todos os familiares vinculados ao mesmo grupo familiar do contexto
	 * @author rodrigo.saraujo
	 * @param pacCodigo
	 * @return
	 */
	public List<PacienteGrupoFamiliarVO> obterFamiliaresVinculados(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc, AipPacientes pacienteContexto) {
		DetachedCriteria criteria = pesquisarFamiliaresVinculados(pacienteContexto);
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteGrupoFamiliarVO.class));
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * #42462
	 * RN01 Numero de familiares vinculados ao mesmo grupo familiar do contexto (COUNT)
	 * @author rodrigo.saraujo
	 * @param pacienteContexto
	 * @return
	 */
	public Long obterFamiliaresVinculadosCount(AipPacientes pacienteContexto) {
		DetachedCriteria criteria = pesquisarFamiliaresVinculados(pacienteContexto);
		return executeCriteriaCount(criteria);
	}

	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * RN02 Criteria lista de pacientes sugeridos vinculados a um grupo familiar
	 */

	private DetachedCriteria pesquisarProntuariosSugeridosVinculados(AipPacientes pacienteContexto, AipEnderecosPacientes enderecoPacienteContexto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		projectionsGrupoFamiliar(criteria);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.GRUPO_FAMILIAR_PACIENTE.toString(), GFP, JoinType.INNER_JOIN);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.ENDERECOS.toString(), END, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(END + PONTO + AipEnderecosPacientes.Fields.UF.toString(), UF, JoinType.LEFT_OUTER_JOIN);
		if (enderecoPacienteContexto.getCep() != null) {
			criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.CEP.toString(), enderecoPacienteContexto.getCep()));
		} else {
			criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.LOGRADOURO.toString(), enderecoPacienteContexto.getLogradouro()));
		}
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.NRO_LOGRADOURO.toString(), enderecoPacienteContexto.getNroLogradouro()));
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.COMPLEMENTO_LOGRADOURO.toString(), enderecoPacienteContexto.getComplLogradouro()));
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.IND_PADRAO, DominioSimNao.S));
		// TODO
		criteria.add(Restrictions.ne(PAC + PONTO + AipPacientes.Fields.CODIGO, pacienteContexto.getCodigo()));
		
		return criteria;
	}
	
	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * RN02 Obtem lista de pacientes sugeridos vinculados a um grupo familiar
	 */

	public List<PacienteGrupoFamiliarVO> obterProntuariosSugeridosVinculados(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc,
			AipPacientes pacienteContexto, AipEnderecosPacientes enderecoPacienteContexto) {
		DetachedCriteria criteria;
		criteria = pesquisarProntuariosSugeridosVinculados(pacienteContexto, enderecoPacienteContexto);
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteGrupoFamiliarVO.class));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * RN02  Numero de pacientes sugeridos vinculados a um grupo familiar (Count)
	 */

	public Long obterProntuariosSugeridosVinculadosCount(AipPacientes pacienteContexto, AipEnderecosPacientes enderecoPacienteContexto) {
		DetachedCriteria criteria;
		criteria = pesquisarProntuariosSugeridosVinculados(pacienteContexto, enderecoPacienteContexto);
		return executeCriteriaCount(criteria);
	}

	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * RN02 Criteria lista de pacientes sugeridos nao vinculados a um grupo familiar
	 */

	private DetachedCriteria pesquisarProntuariosSugeridosNaoVinculados(AipPacientes pacienteContexto, AipEnderecosPacientes enderecoPacienteContexto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		projectionsGrupoFamiliar(criteria);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.GRUPO_FAMILIAR_PACIENTE.toString(), GFP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.ENDERECOS.toString(), END, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(END + PONTO + AipEnderecosPacientes.Fields.UF.toString(), UF, JoinType.LEFT_OUTER_JOIN);
		if (enderecoPacienteContexto.getCep() != null) {
			criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.CEP.toString(), enderecoPacienteContexto.getCep()));
		} else {
			criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.LOGRADOURO.toString(), enderecoPacienteContexto.getLogradouro()));
		}
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.IND_PADRAO, DominioSimNao.S));
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.NRO_LOGRADOURO.toString(), enderecoPacienteContexto.getNroLogradouro()));
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.COMPLEMENTO_LOGRADOURO.toString(), enderecoPacienteContexto.getComplLogradouro()));
		criteria.add(Restrictions.ne(PAC + PONTO + AipPacientes.Fields.CODIGO, pacienteContexto.getCodigo()));
		criteria.add(Restrictions.isNull(GFP + PONTO + AipGrupoFamiliarPacientes.Fields.AGF_SEQ));
		return criteria;
	}

	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * RN02 Obtem lista de pacientes sugeridos nao vinculados a um grupo familiar
	 */
	public List<PacienteGrupoFamiliarVO> obterProntuariosSugeridosNaoVinculados(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc,
			AipPacientes pacienteContexto, AipEnderecosPacientes enderecoPacienteContexto) {
		DetachedCriteria criteria;
		criteria = pesquisarProntuariosSugeridosNaoVinculados(pacienteContexto, enderecoPacienteContexto);
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteGrupoFamiliarVO.class));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * RN02 Numero de pacientes sugeridos vinculados a um grupo familiar (Count)
	 */
	public Long obterProntuariosSugeridosNaoVinculadosCount(AipPacientes pacienteContexto, AipEnderecosPacientes enderecoPacienteContexto) {
		DetachedCriteria criteria;
		criteria = pesquisarProntuariosSugeridosNaoVinculados(pacienteContexto, enderecoPacienteContexto);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * #42462
	 * @author rodrigo.saraujo
	 * Projections utilizadas nas grids familiares vinculados e prontuarios sugeridos 
	 * @param criteria
	 */

	private void projectionsGrupoFamiliar(DetachedCriteria criteria) {
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(PAC + PONTO + AipPacientes.Fields.CODIGO.toString()), PacienteGrupoFamiliarVO.Fields.CODIGO.toString())
				.add(Projections.property(PAC + PONTO + AipPacientes.Fields.PRONTUARIO.toString()), PacienteGrupoFamiliarVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(PAC + PONTO + AipPacientes.Fields.NOME.toString()), PacienteGrupoFamiliarVO.Fields.NOME.toString())
				.add(Projections.property(END + PONTO + AipEnderecosPacientes.Fields.LOGRADOURO.toString()), PacienteGrupoFamiliarVO.Fields.LOGRADOURO.toString())
				.add(Projections.property(END + PONTO + AipEnderecosPacientes.Fields.COMPLEMENTO_LOGRADOURO.toString()), PacienteGrupoFamiliarVO.Fields.COMPLLOGRADOURO.toString())
				.add(Projections.property(END + PONTO + AipEnderecosPacientes.Fields.NRO_LOGRADOURO.toString()), PacienteGrupoFamiliarVO.Fields.NRO_LOGRADOURO.toString())
				.add(Projections.property(END + PONTO + AipEnderecosPacientes.Fields.BAIRRO.toString()), PacienteGrupoFamiliarVO.Fields.BAIRRO.toString())
				.add(Projections.property(END + PONTO + AipEnderecosPacientes.Fields.END_CIDADE.toString()), PacienteGrupoFamiliarVO.Fields.CIDADE.toString())
				.add(Projections.property(UF + PONTO + AipUfs.Fields.SIGLA.toString()), PacienteGrupoFamiliarVO.Fields.UF.toString())
				.add(Projections.property(GFP + PONTO + AipGrupoFamiliarPacientes.Fields.AGF_SEQ.toString()), PacienteGrupoFamiliarVO.Fields.GRUPO_FAMILIAR_SEQ.toString()));
	}

	public AipPacientes obterPacienteFull(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, PAC);
		criteria.createAlias(PAC + PONTO + AipPacientes.Fields.ENDERECOS.toString(), END, JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(PAC + PONTO + AipPacientes.Fields.CODIGO, pacCodigo));
		criteria.add(Restrictions.eq(END + PONTO + AipEnderecosPacientes.Fields.IND_PADRAO, DominioSimNao.S));
		return (AipPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public AipGrupoFamiliarPacientes obterProntuarioFamiliaPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipGrupoFamiliarPacientes.class, GFP);
		criteria.add(Restrictions.eq(GFP + PONTO + AipGrupoFamiliarPacientes.Fields.PAC_CODIGO, pacCodigo));
		return (AipGrupoFamiliarPacientes) executeCriteriaUniqueResult(criteria);
	}
	
	public Integer obterValorSequencial() {
		StringBuffer select = new StringBuffer(50);
		if (isOracle()) {
			select.append("select AGH.AIP_GFM_SQ1.NEXTVAL from dual");
		} else if (isPostgreSQL()) {
			select.append("select nextval('AGH.AIP_GFM_SQ1')");
		} else if (isHSQL()) {
			select.append("CALL NEXT VALUE FOR AGH.AIP_GFM_SQ1");
		}

		List<?> listCurrVal = this.createNativeQuery(select.toString()).getResultList();

		Number currVal = null;
		if (isOracle()) {
			currVal = (BigDecimal) listCurrVal.get(0);			
		} else if (isPostgreSQL()) {
			currVal = (BigInteger) listCurrVal.get(0);
		} else if (isHSQL()) {
			currVal = (Integer) listCurrVal.get(0);
		}

		return currVal.intValue();
	}
}
