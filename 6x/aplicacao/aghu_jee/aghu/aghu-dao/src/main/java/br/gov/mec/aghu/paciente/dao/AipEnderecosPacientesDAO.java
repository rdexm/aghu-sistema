package br.gov.mec.aghu.paciente.dao;

import static org.hibernate.criterion.Projections.projectionList;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.vo.AipEnderecoPacienteVO;

public class AipEnderecosPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipEnderecosPacientes> {
	
	private static final long serialVersionUID = 5585576102916385292L;

	public List<AipEnderecosPacientes> obterEndPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE
				.toString(), pacCodigo));
		criteria.addOrder(Order.desc(AipEnderecosPacientes.Fields.IND_PADRAO
				.toString()));

		return executeCriteria(criteria);
	}
	
	
	public List<AipEnderecosPacientes> obterEnderecosCompletosPaciente(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(
				AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.addOrder(Order.desc(AipEnderecosPacientes.Fields.IND_PADRAO
				.toString()));

		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString(),
				FetchMode.JOIN);

		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO
								.toString(), FetchMode.JOIN);

		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO
								.toString() + "."
						+ AipCepLogradouros.Fields.LOGRADOURO.toString(),
				FetchMode.JOIN);

		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO
								.toString() + "."
						+ AipCepLogradouros.Fields.LOGRADOURO.toString() + "."
						+ AipLogradouros.Fields.TIPO.toString(), FetchMode.JOIN);

		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO
								.toString() + "."
						+ AipCepLogradouros.Fields.LOGRADOURO.toString() + "."
						+ AipLogradouros.Fields.TITULO.toString(),
				FetchMode.JOIN);
		
		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO
								.toString() + "."
						+ AipCepLogradouros.Fields.LOGRADOURO.toString() + "."
						+ AipLogradouros.Fields.CIDADE.toString(),
				FetchMode.JOIN);
		
		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.AIP_CEP_LOGRADOURO
								.toString() + "."
						+ AipCepLogradouros.Fields.LOGRADOURO.toString() + "."
						+ AipLogradouros.Fields.CIDADE.toString() + "."
						+ AipCidades.Fields.UF.toString(), FetchMode.JOIN);

		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString()
						+ "."
						+ AipBairrosCepLogradouro.Fields.BAIRRO.toString(),
				FetchMode.JOIN);
		
		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.CIDADE.toString(),
				FetchMode.JOIN);
		
		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.CIDADE.toString() + "." + AipCidades.Fields.UF.toString(),
				FetchMode.JOIN);		
		
		criteria.setFetchMode(
				AipEnderecosPacientes.Fields.UF.toString(),
				FetchMode.JOIN);

		return executeCriteria(criteria);
	}

	
	@SuppressWarnings("unchecked")
	public List<AipEnderecosPacientesId> obterIdEndPaciente(Integer pacCodigo) {
		String stConsulta  = "select e.id from AipEnderecosPacientes e where e.id.pacCodigo = :pacCodigo";
		
		Query consulta = this.createQuery(stConsulta);
		
		consulta.setParameter("pacCodigo", pacCodigo);	

		return consulta.getResultList() ;
	}
	
	public short obterSeqEnderecoPacienteAtual(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE
				.toString(), paciente.getCodigo()));
		criteria.setProjection(Projections
				.max(AipEnderecosPacientes.Fields.SEQUENCIAL.toString()));

		Short valor = (Short) this.executeCriteriaUniqueResult(criteria, true);

		if (valor == null) {
			valor = 0;
		}
		return valor;
	}

	public AipEnderecosPacientes obterAipEnderecosPacientes(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);

		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.SEQUENCIAL.toString(), seqp));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(AipEnderecosPacientes.Fields.CIDADE.toString()));
		criteria.add(Restrictions.isNull(AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString()));

		return (AipEnderecosPacientes) executeCriteriaUniqueResult(criteria);
	}

	public AipEnderecosPacientes obterEnderecoPacienteJoin2(Integer codigoPaciente, Short seqEnderecoCorrespondencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class, "aep");

		criteria.createAlias("aep." + AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString(), "logr", Criteria.INNER_JOIN);
		criteria.createAlias("aep." + AipEnderecosPacientes.Fields.BAIRROS_CEP_LOGRADOURO.toString(), "baiceplogr",
				Criteria.INNER_JOIN);
		criteria.createAlias("baiceplogr." + AipBairrosCepLogradouro.Fields.BAIRRO.toString(), "bairro", Criteria.INNER_JOIN);
		criteria.createAlias("logr." + AipLogradouros.Fields.TITULO.toString(), "tituloLogr", Criteria.LEFT_JOIN);
		criteria.createAlias("logr." + AipLogradouros.Fields.TIPO.toString(), "tipoLogr", Criteria.INNER_JOIN);
		criteria.createAlias("logr." + AipLogradouros.Fields.CIDADE.toString(), "cidade", Criteria.INNER_JOIN);
		criteria.createAlias("aep." + AipEnderecosPacientes.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("aep." + AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("aep." + AipEnderecosPacientes.Fields.SEQUENCIAL.toString(), seqEnderecoCorrespondencia));

		return (AipEnderecosPacientes) executeCriteriaUniqueResult(criteria);
	}

	public AipEnderecosPacientes obterEnderecoPacienteJoin3(Integer codigoPaciente, Short seqEnderecoCorrespondencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class, "aep");

		criteria.createAlias("aep." + AipEnderecosPacientes.Fields.CIDADE.toString(), "cidade", Criteria.INNER_JOIN);
		criteria.createAlias("aep." + AipEnderecosPacientes.Fields.PACIENTE.toString(), "pac", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("aep." + AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.isNull("aep." + AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString()));
		criteria.add(Restrictions.eq("aep." + AipEnderecosPacientes.Fields.SEQUENCIAL.toString(), seqEnderecoCorrespondencia));

		return (AipEnderecosPacientes) executeCriteriaUniqueResult(criteria);
	}

	public List<AipEnderecosPacientes> obterEnderecosResidencialPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), paciente.getCodigo()));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.TIPO_ENDERECO.toString(), DominioTipoEndereco.R));
		criteria.addOrder(Order.desc(AipEnderecosPacientes.Fields.IND_PADRAO.toString()));

		return executeCriteria(criteria);
	}

	public Object[] obterInformacoesEnderecoAnterior(AipEnderecosPacientesId idEnderecoAnterior) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);

		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.ID.toString(), idEnderecoAnterior));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.CIDADE.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.END_CIDADE.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.IND_PADRAO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.LOGRADOURO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.NRO_LOGRADOURO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.TIPO_ENDERECO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.UF.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.COMPLEMENTO_LOGRADOURO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.BAIRRO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.CEP.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.IND_EXCLUSAO_FORCADA.toString()))

		);

		return (Object[]) this.executeCriteriaUniqueResult(criteria);
	}

	public AipBairrosCepLogradouro obterBairroCepLogradouro(AipEnderecosPacientesId idEnderecoAnterior) {
		Query consulta = this.createQuery("select a.aipBairrosCepLogradouro from AipEnderecosPacientes a where a.id = :identificador");

		consulta.setParameter("identificador", idEnderecoAnterior);

		return (AipBairrosCepLogradouro) consulta.getSingleResult();
	}

	public Long buscaQuantidadeEnderecosPaciente(AipPacientes paciente) {
		DetachedCriteria criteriaQuantidadeEnderecosBanco = DetachedCriteria.forClass(AipEnderecosPacientes.class);

		criteriaQuantidadeEnderecosBanco.add(Restrictions.eq(AipEnderecosPacientes.Fields.PACIENTE.toString(), paciente));

		return this.executeCriteriaCount(criteriaQuantidadeEnderecosBanco);
	}
	
	public Short obterSeqEnderecoPadraoPaciente(Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.IND_PADRAO.toString(), DominioSimNao.S));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipEnderecosPacientes.Fields.SEQUENCIAL.toString())));

		// #11955 Comentado e adicionado trecho abaixo para evitar
		// UniqueResultException caso haja inconsistência de dados.
		// seqp = (Short) executeCriteriaUniqueResult(criteria);
		List<Short> listaEnderecos = executeCriteria(criteria, true);
		if (listaEnderecos != null && !listaEnderecos.isEmpty()) {
			return listaEnderecos.get(0);
		}
		return null;
	}
	
	public Short obterSeqEnderecoResidencialPaciente(Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.TIPO_ENDERECO.toString(), DominioTipoEndereco.R));
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipEnderecosPacientes.Fields.SEQUENCIAL.toString())));

		// #11955 Comentado e adicionado trecho abaixo para evitar
		// UniqueResultException caso haja inconsistência de dados.
		// seqp = (Short) executeCriteriaUniqueResult(criteria);
		List<Short> listaEnderecos = executeCriteria(criteria);
		if (listaEnderecos != null && !listaEnderecos.isEmpty()) {
			return listaEnderecos.get(0);
		}
		return null;
	}
	
	/**
	 * Cursor 1 da function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public String pesquisarCidadeCursor1(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		// AIP_LOGRADOUROS
		criteria.createAlias(AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString(),
				AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString());
		// AIP_CIDADES
		criteria.createAlias(AipEnderecosPacientes.Fields.AIP_LOGRADOURO.toString() + "." + AipLogradouros.Fields.CIDADE.toString(),
				AipLogradouros.Fields.CIDADE.toString());
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipLogradouros.Fields.CIDADE.toString() + "." + AipCidades.Fields.NOME.toString())));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.SEQUENCIAL.toString(), seqp));
		return (String) executeCriteriaUniqueResult(criteria);	
	}
	
	/**
	 * Cursor 2 da function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public String pesquisarCidadeCursor2(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		// AIP_CIDADES
		criteria.createAlias(AipEnderecosPacientes.Fields.CIDADE.toString(), AipEnderecosPacientes.Fields.CIDADE.toString());
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipEnderecosPacientes.Fields.CIDADE.toString() + "." + AipCidades.Fields.NOME.toString())));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(AipEnderecosPacientes.Fields.BCL_CLO_LGR_CODIGO.toString()));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.SEQUENCIAL.toString(), seqp));

		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Cursor 3 da function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public String pesquisarCidadeCursor3(Integer pacCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AipEnderecosPacientes.Fields.END_CIDADE.toString())));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.isNull(AipEnderecosPacientes.Fields.BCL_CLO_LGR_CODIGO.toString()));
		criteria.add(Restrictions.isNull(AipEnderecosPacientes.Fields.CDD_CODIGO.toString()));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.SEQUENCIAL.toString(), seqp));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AipEnderecosPacientes> listarEnderecosPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);

		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<Object[]> obterInformacoesEnderecoPacienteMamcGetPostoPac(Integer pacCodigo, DominioTipoEndereco tipoEndereco) {
		DetachedCriteria c_end = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		
		c_end.createAlias("aipLogradouro", "lgr", Criteria.LEFT_JOIN)
				.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), pacCodigo))
				.add(Restrictions.eq(AipEnderecosPacientes.Fields.TIPO_ENDERECO.toString(), tipoEndereco));
		c_end.setProjection(projectionList().add(Projections.property(AipEnderecosPacientes.Fields.BCL_CLO_LGR_CODIGO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.NRO_LOGRADOURO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.LOGRADOURO.toString()))
				.add(Projections.property(AipEnderecosPacientes.Fields.CDD_CODIGO.toString()))
				.add(Projections.property("lgr." + AipLogradouros.Fields.CDD_CODIGO.toString())));

		return executeCriteria(c_end);
	}

	public boolean existeEnderecoPaciente(
			AipEnderecosPacientesId enderecoPacienteId) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipEnderecosPacientes.class);

		criteria.add(Restrictions.eq(
				AipEnderecosPacientes.Fields.ID.toString(), enderecoPacienteId));

		return this.executeCriteriaCount(criteria) > 0;
	}
	
	
	public AipEnderecosPacientes obterEnderecoResidencialPadraoPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), paciente.getCodigo()));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.TIPO_ENDERECO.toString(), DominioTipoEndereco.R));
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.IND_PADRAO.toString(), DominioSimNao.S));
		
        
		return (AipEnderecosPacientes) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #52025  CONSULTA UTILIZADA EM FUNCTION P9 AIPC_PROCEDENCIA_PAC
	 * @param codigo
	 * @return
	 */
	public List<AipEnderecoPacienteVO> obterAipEnderecoVOPorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEnderecosPacientes.class);
		criteria.add(Restrictions.eq(AipEnderecosPacientes.Fields.COD_PACIENTE.toString(), codigo));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipEnderecosPacientes.Fields.BCL_CLO_LGR_CODIGO.toString()), AipEnderecoPacienteVO.Fields.BCL_CLO_LGR_CODIGO.toString())
				.add(Projections.property(AipEnderecosPacientes.Fields.CDD_CODIGO.toString()), AipEnderecoPacienteVO.Fields.CDD_CODIGO.toString())
				.add(Projections.property(AipEnderecosPacientes.Fields.CIDADE.toString()), AipEnderecoPacienteVO.Fields.CIDADE.toString())
				.add(Projections.property(AipEnderecosPacientes.Fields.TIPO_ENDERECO.toString()), AipEnderecoPacienteVO.Fields.TIPO_ENDERECO.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AipEnderecoPacienteVO.class));
		return executeCriteria(criteria);
	}
}
