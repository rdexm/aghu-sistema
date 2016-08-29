package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Responsavel pela montagem da busca de Atendimentos 
 * para a tela de Solicitacao de Exames.<br>
 * Classe concretas de build devem sempre ter modificador de acesso Default.<br>
 * 
 * <p>Exemplo de uso do QueryBuilder para javax.persistence.Query. 
 * Com metodo unico para setagem dos parametros.</p>
 * 
 * 
 * @see javax.persistence.Query
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AtendimentoParaSolicitacaoExameQueryBuilder extends QueryBuilder<Query> {
	
	private static final String STR_AND = " and ";
	private static final String STR_OBJECT = " o.";
	private static final String ALIAS_INTERNACAO = " int";
	private static final String SEPARATOR = ".";

	
	private SolicitacaoExameFilter filter;
	private List<String> fonemasPaciente; 
	private boolean isOrder;
	private String orderProperty;
	private boolean asc;
	
	
	// Neste caso especifico este metodo ficou disponivel para 
	// uma sub-classe sobrescreve-lo para reuso do mesmo filtro.
	protected StringBuilder makeQueryBasic() {
		final StringBuilder hql = new StringBuilder(30);
		hql.append(" select o ");
		hql.append(" from ");
		hql.append(AghAtendimentos.class.getSimpleName()).append(" o ");		
		
		return hql;
	}

	@Override
	protected Query createProduct() {
		// Esta sub-classe usou o metodo createQuery(String hql)  para criar o Produto.
		// E depois o setProduct para setar o produto gerado.
		return null;
	}
	
	@Override
	protected void doBuild(Query aProduct) {
		StringBuilder hql = makeQueryBasic();
		
		Query query = getQueryPesquisaSolicitacaoExamePaginada(hql, filter, fonemasPaciente, isOrder, orderProperty, asc);
		
		this.setProduct(query);
	}
	
	public void setParameters(final SolicitacaoExameFilter aFilter, final List<String> thefonemasPaciente
			, final boolean order, final String orderP, final boolean isAsc) {
		// Metodo para setar os objetos necessarios para a construcao da Query.
		// Pode ser usado apenas um metodo, como este exemplo. OU vários metodos de set, conforme vontade do desenvolvedor.
		
		// Fazer validacoes de obrigatoriedade dos parametros, quando necessario;
		/*
		if (aFilter == null) {
			throw new IllegalArgumentException("Parametro filtro nao informado!!!!");
		}
		*/
		
		this.asc = isAsc;
		this.filter = aFilter;
		this.fonemasPaciente = thefonemasPaciente;
		this.isOrder = order;
		this.orderProperty = orderP;
		
	}
	
	
	private javax.persistence.Query getQueryPesquisaSolicitacaoExamePaginada(final StringBuilder hql, final SolicitacaoExameFilter filter,
			final List<String> fonemasPaciente, final boolean isOrder, final String orderProperty, final boolean asc) {
		if (filter != null) {
			addFilterBinds(hql, filter, fonemasPaciente);
		}

		if (isOrder) {
			addOrderBy(hql, orderProperty, asc);
		}

		final Query query = this.createQuery(hql.toString());

		if (filter != null) {
			addValuesBinds(filter, fonemasPaciente, query);
		}

		return query;
	}

	private void addValuesBinds(final SolicitacaoExameFilter filter,
			final List<String> fonemasPaciente, final Query query) {
		if (filter.getProntuario() != null) {
			query.setParameter("pProntuario", filter.getProntuario());
		}
		if (filter.getNumero() != null) {
			query.setParameter("pNumero", filter.getNumero());
		}
		if (StringUtils.isNotBlank(filter.getNomePaciente()) && fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
			int size = 0;
			for (final String fonema : fonemasPaciente) {
				query.setParameter("fonema" + size, fonema);
				size++;
			}
			// query.setParameter("pNomePac1",
			// "%".concat(filter.getNomePaciente().toUpperCase()).concat("%"));
			// query.setParameter("pNomePac2",
			// "%".concat(filter.getNomePaciente()).concat("%"));
		}
		if (filter.getOrigem() != null) {
			query.setParameter("pOrigem", filter.getOrigem());
		}
		if (StringUtils.isNotBlank(filter.getLeito())) {
			query.setParameter("pLeito1", "%".concat(filter.getLeito().toUpperCase()).concat("%"));
			query.setParameter("pLeito2", "%".concat(filter.getLeito()).concat("%"));
		}
		if (StringUtils.isNotBlank(filter.getQuarto())) {
			query.setParameter("pQuarto", filter.getQuarto());
		}
		if (filter.getUnidade() != null) {
			query.setParameter("pUnf", filter.getUnidade());
		}
		if (filter.getInternado() != null) {
			query.setParameter("indPacienteInternado", filter.getInternado());
		}
	}

	private void addOrderBy(final StringBuilder hql,
			final String orderProperty, final boolean asc) {
		hql.append(" order by o.");
		if (StringUtils.isNotBlank(orderProperty)) {
			hql.append(orderProperty);
		} else {
			hql.append(AghAtendimentos.Fields.DTHR_INICIO.toString());
		}

		if (asc) {
			hql.append(" asc ");
		} else {
			hql.append(" desc ");
		}
	}

	private void addFilterBinds(final StringBuilder hql, final SolicitacaoExameFilter filter, final List<String> fonemasPaciente) {
		boolean buff = false;
		if (filter.getProntuario() != null || filter.getNumero() != null || StringUtils.isNotBlank(filter.getNomePaciente())
				|| filter.getOrigem() != null || StringUtils.isNotBlank(filter.getLeito()) || StringUtils.isNotBlank(filter.getQuarto())
				|| filter.getUnidade() != null) {
			
			hql.append(" inner join o.").append(AghAtendimentos.Fields.PACIENTE.toString()).append(" pac ");

			if (filter.getInternado() != null) {
				hql.append(" inner join o.").append(AghAtendimentos.Fields.INTERNACAO.toString()).append(" int ");
			}

			hql.append(" left outer join o.").append(AghAtendimentos.Fields.ESPECIALIDADE.toString()).append(" esp ");
			hql.append(" left outer join o.").append(AghAtendimentos.Fields.QUARTO.toString()).append(" qrt ");
			
			hql.append(" where ");

			buff = addFilterProntuario(hql, filter, buff);
			buff = addFilterNumero(hql, filter, buff);

			buff = addFilterNomePaciente(hql, filter, fonemasPaciente, buff);
			buff = addFilterOrigem(hql, filter, buff);
			buff = addFilterLeito(hql, filter, buff);
			buff = addFilterQuarto(hql, filter, buff);
			buff = addFilterInternado(hql, filter, buff);
			addFilterUnidade(hql, filter, buff);
		}
	}

	private boolean addFilterUnidade(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (filter.getUnidade() != null) {
			if (buff) {
				hql.append(STR_AND);
			}
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()).append(" = :pUnf ");
			buff = true;
		}
		return buff;
	}

	private boolean addFilterQuarto(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (StringUtils.isNotBlank(filter.getQuarto())) {
			if (buff) {
				hql.append(STR_AND);
			}
			hql.append("qrt.").append(AinQuartos.Fields.DESCRICAO.toString()).append(" = :pQuarto ");
			buff = true;
		}
		return buff;
	}

	private boolean addFilterLeito(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (StringUtils.isNotBlank(filter.getLeito())) {
			if (buff) {
				hql.append(STR_AND);
			}
			hql.append(" ( ");
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.LEITO.toString()).append('.');
			hql.append(AinLeitos.Fields.LTO_ID.toString()).append(" like :pLeito1 ");
			hql.append(" or ");
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.LEITO.toString()).append('.');
			hql.append(AinLeitos.Fields.LTO_ID.toString()).append(" like :pLeito2 ");
			hql.append(" ) ");
			buff = true;
		}
		return buff;
	}

	private boolean addFilterOrigem(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (filter.getOrigem() != null) {
			if (buff) {
				hql.append(STR_AND);
			}
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.ORIGEM.toString()).append(" = :pOrigem ");
			buff = true;
		}
		return buff;
	}

	private boolean addFilterNomePaciente(final StringBuilder hql,
			final SolicitacaoExameFilter filter,
			final List<String> fonemasPaciente, boolean buff) {
		if (StringUtils.isNotBlank(filter.getNomePaciente()) && fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
			if (buff) {
				hql.append(STR_AND);
			}
			
			hql.append(" ( ");
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.PACIENTE.toString());

			hql.append(" in ( ");
			int count = 0;
			if (fonemasPaciente != null && !fonemasPaciente.isEmpty()) {
				count += fonemasPaciente.size();
				for (int i = 0; i < fonemasPaciente.size(); i++) {
					hql.append(" SELECT distinct f");
					hql.append(i);
					hql.append(".aipPaciente.codigo ");
					hql.append(" FROM AipFonemaPacientes f");
					hql.append(i);
					hql.append(" WHERE f");
					hql.append(i);
					hql.append(".aipFonemas.fonema = :fonema");
					hql.append(i);
					hql.append("   AND f");
					hql.append(i);
					hql.append(".aipPaciente.codigo IN ( ");
				}
			}
			hql.delete(hql.length() - 32, hql.length());
			// Retira o último AND f.aipPaciente.codigo IN ("
			for (int i = 0; i < count - 1; i++) {
				hql.append(" ) ");
			}
			hql.append(" ) ");

			hql.append(" ) ");
			buff = true;
		}
		return buff;
	}

	private boolean addFilterNumero(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (filter.getNumero() != null) {
			if (buff) {
				hql.append(STR_AND);
			}
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.NUMERO_CONSULTA.toString()).append(" = :pNumero ");
			buff = true;
		}
		return buff;
	}

	private boolean addFilterProntuario(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (filter.getProntuario() != null) {
			hql.append(STR_OBJECT).append(AghAtendimentos.Fields.PRONTUARIO_PACIENTE.toString()).append(" = :pProntuario ");
			buff = true;
		}
		return buff;
	}
	
	private boolean addFilterInternado(final StringBuilder hql,
			final SolicitacaoExameFilter filter, boolean buff) {
		if (filter.getInternado() != null) {
			if (buff) {
				hql.append(STR_AND);
			}

			hql.append(ALIAS_INTERNACAO).append(SEPARATOR).append(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString()).append(" = :indPacienteInternado ");
			buff = true;
		}
		return buff;
	}
}
