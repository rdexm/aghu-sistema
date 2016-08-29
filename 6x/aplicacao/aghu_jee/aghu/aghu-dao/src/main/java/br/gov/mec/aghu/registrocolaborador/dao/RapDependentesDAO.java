package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;

import br.gov.mec.aghu.dominio.DominioGrauParentesco;
import br.gov.mec.aghu.dominio.DominioSexoDependente;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
/**
 * 
 * @modulo registrocolaborador
 *
 */
public class RapDependentesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapDependentes> {
	
	private static final long serialVersionUID = 600001317528469554L;

	public enum DependenteDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVIDOR_INEXISTENTE;
	}
	
	public List<RapDependentes> pesquisarDependente(Integer pesCodigo,
			Integer codigo, Integer codPessoaFisica,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {

		DetachedCriteria criteriaFiltro = montarConsulta(pesCodigo, codigo, null, null, null, null, null, null, codPessoaFisica);
		criteriaFiltro.createAlias(RapDependentes.Fields.PACIENTE.toString(),   "PC", JoinType.LEFT_OUTER_JOIN);

		return executeCriteria(criteriaFiltro, firstResult, maxResult, orderProperty, asc);
	}	

	public Long pesquisarDependenteCount(Integer pesCodigo, Integer codigo, Integer codPessoaFisica) throws ApplicationBusinessException {

		DetachedCriteria criteria = montarConsulta(pesCodigo, codigo, null, null, null, null, null, null, codPessoaFisica);

		return executeCriteriaCount(criteria);
	}	
	
	private DetachedCriteria montarConsulta(Integer pesCodigo, Integer codigo, String nome, Date dtNascimento, DominioSexoDependente sexo,
			DominioGrauParentesco grauParentesco, Integer pacProntuario, AipPacientes  paciente, Integer codPessoaFisica) throws ApplicationBusinessException {

		RapDependentes example = new RapDependentes();

		example.setDtNascimento(dtNascimento);
		example.setGrauParentesco(grauParentesco);
		example.setNome(nome);
		//example.setPacCodigo(pacCodigo);
		example.setPaciente(paciente);
		example.setPacProntuario(pacProntuario);		
		example.setSexo(sexo);

		DetachedCriteria criteria = DetachedCriteria.forClass(RapDependentes.class);

		criteria.add(Example.create(example).enableLike(MatchMode.EXACT));

		if (pesCodigo != null) {
			criteria.add(Restrictions.eq(RapDependentes.Fields.PES_CODIGO.toString(), pesCodigo));
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(RapDependentes.Fields.CODIGO.toString(), codigo));
		}

		// busca servidor para adicionar criterio por pessoa fisica.
		if (codPessoaFisica != null) {
			criteria.add(Restrictions.eq(RapDependentes.Fields.PES_CODIGO.toString(), codPessoaFisica));
		}

		return criteria;
	}
	
	/**
	 * Atribui o próximo valor de código ao objeto a ser inserido.
	 * 
	 * @param qualificacao
	 */
	public void atribuiCodigoDependente(RapDependentes dependente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapDependentes.class);

		criteria.setProjection(Projections.max(RapDependentes.Fields.CODIGO
				.toString()));
		criteria.add(Restrictions.eq(RapDependentes.Fields.PES_CODIGO.toString(),
				dependente.getId().getPesCodigo()));
		
		Integer codigo = (Integer) executeCriteriaUniqueResult(criteria);
		codigo = (codigo == null) ? (Integer) 0 : codigo;

		dependente.getId().setCodigo(++codigo);
	}
	
	public RapDependentes obterDependentePeloPacCodigoPeloVinculoEMatricula(Integer codigo, Short vinCodigo, Integer matricula) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapDependentes.class);
		
		criteria.createAlias(RapDependentes.Fields.PESSOA_FISICA.toString(), "PF");
		criteria.createAlias("PF."+RapPessoasFisicas.Fields.SERVIDOR.toString(), "SER");
		
		criteria.add(Restrictions.eq(RapDependentes.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.eq("SER."+ RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("SER."+ RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		
		return (RapDependentes)executeCriteriaUniqueResult(criteria);
	}

	public List<RapDependentes> listarDependentesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapDependentes.class);

		criteria.add(Restrictions.eq(RapDependentes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public Long buscaNroDependentesAbaixo7AnosPorCentroCusto(FccCentroCustos centroCusto){
		
		Date d = new Date();  
	    Calendar c = Calendar.getInstance();  
	    c.setTime(d);  
	    c.add(Calendar.YEAR,-7);
	    d = c.getTime(); 
	
	    DetachedCriteria criteria = DetachedCriteria.forClass(RapDependentes.class);
		criteria.createAlias(RapDependentes.Fields.PESSOA_FISICA.toString(), "PF");
		criteria.createAlias("PF."+RapPessoasFisicas.Fields.RAP_SERVIDORESES, "SER");
		
		criteria.add(Restrictions.eq("SER." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(RapDependentes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(RapDependentes.Fields.PARENTESCO.toString(), DominioGrauParentesco.FILHO));
		
		criteria.add(Restrictions.between(RapDependentes.Fields.DT_NASCIMENTO.toString(), DateUtil.truncaData(d), DateUtil.truncaData(new Date()) ));
		
		criteria.add(Restrictions.sqlRestriction("coalesce(ser2_.cct_codigo_atua, ser2_.cct_codigo) = ? " , centroCusto.getCodigo(), IntegerType.INSTANCE));
		
		
		return this.executeCriteriaCount(criteria);
		
	
	}
	

}
