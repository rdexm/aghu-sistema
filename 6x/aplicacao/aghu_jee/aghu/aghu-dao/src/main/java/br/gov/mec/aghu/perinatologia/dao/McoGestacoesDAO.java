package br.gov.mec.aghu.perinatologia.dao;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.paciente.prontuario.vo.InformacoesPerinataisVO;

public class McoGestacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoGestacoes> {

	private static final long serialVersionUID = -1916058189531775471L;

	public List<McoGestacoes> pesquisarMcoGestacoes(Integer pacCodigo) {
		// Busca todas as gestações do prontuario que se quer unificar
		DetachedCriteria criteria = getCriteriaMcoGestacoes(pacCodigo);
		return executeCriteria(criteria);
	}
	
	public boolean verificarExisteGestacao(Integer pacCodigo) {
		// Busca todas as gestações do prontuario que se quer unificar
		DetachedCriteria criteria = getCriteriaMcoGestacoes(pacCodigo);
		
		return this.executeCriteriaExists(criteria);
	}

	private DetachedCriteria getCriteriaMcoGestacoes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		return criteria;
	}

	private DetachedCriteria criarCriteriaMaxSeqMcoGestacoes(Integer pacCodigo) {
		DetachedCriteria criteria = getCriteriaMcoGestacoes(pacCodigo);

		criteria.setProjection(Projections.max(McoGestacoes.Fields.SEQUENCE.toString()));
		return criteria;
	}

	public Short obterMaxSeqMcoGestacoesComGravidezConfirmada(Integer pacCodigo, Byte gesta) {
		Short seqp = null;

		// Verifica se já existe a gestação que se quer gravar no prontuário
		// mais antigo
		DetachedCriteria criteria = criarCriteriaMaxSeqMcoGestacoes(pacCodigo);
		criteria.add(Restrictions.eq(McoGestacoes.Fields.GESTACAO.toString(), gesta));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.GRAVIDEZ.toString(), DominioGravidez.GCO));

		List<Short> lista = executeCriteria(criteria);
		if (!lista.isEmpty() && lista.get(0) != null) {
			seqp = lista.get(0);
		}

		return seqp;
	}

	public Short obterMaxSeqMcoGestacoes(Integer pacCodigo) {
		Short seqp = 0;

		// Busca a última gestação registrada para o prontuario mais antigo
		DetachedCriteria criteria = criarCriteriaMaxSeqMcoGestacoes(pacCodigo);

		List<Short> lista = executeCriteria(criteria);
		if (!lista.isEmpty() && lista.get(0) != null) {
			seqp = lista.get(0);
		}

		return seqp;
	}

	public List<McoGestacoes> listarGestacoesPorCodigoPacienteEGestacao(Integer pPacCodigoPara, Byte cGesta) {
		DetachedCriteria criteria = getCriteriaMcoGestacoes(pPacCodigoPara);
		criteria.add(Restrictions.eq(McoGestacoes.Fields.GESTACAO.toString(), cGesta));

		return executeCriteria(criteria);
	}
	
	public Date obterMaxCriadoEmMcoGestacoes(Integer pacCodigo, Byte gesta, Date criadoEm) {
		DetachedCriteria criteria = getCriteriaMcoGestacoes(pacCodigo);
		
		criteria.add(Restrictions.eq(McoGestacoes.Fields.GESTACAO.toString(), gesta));
		criteria.add(Restrictions.lt(McoGestacoes.Fields.CRIADO_EM.toString(), criadoEm));
		criteria.setProjection(Projections.max(McoGestacoes.Fields.CRIADO_EM.toString()));
		
		java.sql.Date retorno = (java.sql.Date) executeCriteriaUniqueResult(criteria);
		
		return retorno;
	}
	
	/**
	 * Q_CONS_PERIN
	 * @param pacCodigo
	 * @return
	 */
	public List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);		

		DetachedCriteria criteriaAtendimentos = DetachedCriteria.forClass(AghAtendimentos.class);
		criteriaAtendimentos.add(Restrictions.eq(AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteriaAtendimentos.setProjection(Projections.property(AghAtendimentos.Fields.PAC_CODIGO.toString()));
		
		criteria.createAlias(McoGestacoes.Fields.MCO_RECEM_NASCIDOS.toString(), "RECEM_NASCIDOS", JoinType.INNER_JOIN);
		criteria.createAlias("RECEM_NASCIDOS."+McoGestacoes.Fields.PACIENTE.toString(), "PACIENTES", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RECEM_NASCIDOS." + McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString()), InformacoesPerinataisVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property(McoGestacoes.Fields.CODIGO_PACIENTE.toString()), InformacoesPerinataisVO.Fields.GSO_PAC_CODIGO.toString())
				.add(Projections.property(McoGestacoes.Fields.SEQUENCE.toString()), InformacoesPerinataisVO.Fields.GSO_SEQP.toString())
				.add(Projections.property("RECEM_NASCIDOS." + McoRecemNascidos.Fields.SEQP.toString()), InformacoesPerinataisVO.Fields.RNA_SEQP.toString())
				.add(Projections.sqlProjection("0 as ATD_SEQ", new String[] {InformacoesPerinataisVO.Fields.ATD_SEQ.toString()}, new Type[] {StringType.INSTANCE}))  
				.add(Projections.property(McoGestacoes.Fields.GESTA.toString()), InformacoesPerinataisVO.Fields.GSO_GESTA.toString())
				.add(Projections.property(McoGestacoes.Fields.PARA.toString()), InformacoesPerinataisVO.Fields.GSO_PARA.toString())
				.add(Projections.property(McoGestacoes.Fields.CESAREA.toString()), InformacoesPerinataisVO.Fields.GSO_CESAREA.toString())
				.add(Projections.property(McoGestacoes.Fields.ABORTO.toString()), InformacoesPerinataisVO.Fields.GSO_ABORTO.toString())
				.add(Projections.property("RECEM_NASCIDOS." + McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString()), InformacoesPerinataisVO.Fields.DTHR_INICIO.toString())
				.add(Projections.sqlProjection("'N' as TIPO", new String[] {InformacoesPerinataisVO.Fields.TIPO.toString()}, new Type[] {StringType.INSTANCE}))
		);
		
		//criteria.add(Restrictions.eq("RECEM_NASCIDOS." + McoRecemNascidos.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("PACIENTES." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Subqueries.notExists(criteriaAtendimentos));		
		criteria.addOrder(Order.desc("RECEM_NASCIDOS." +McoRecemNascidos.Fields.DTHR_NASCIMENTO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(InformacoesPerinataisVO.class));
		
		List<InformacoesPerinataisVO> resultado = executeCriteria(criteria);
		resultado.addAll(pesquisarInformacoesPerinataisCodigoPacienteUnion(pacCodigo));
		
		Collections.sort(resultado, new Comparator<InformacoesPerinataisVO>() {

			@Override
			public int compare(InformacoesPerinataisVO o1,
					InformacoesPerinataisVO o2) {
				if(!o1.getDthrInicio().equals(o2.getDthrInicio())){
					if(o1.getDthrInicio().after(o2.getDthrInicio())){
						return -1;
					}else{
						return 1;
					}
					
				}
				return 0;
			}
			
		});		    
		
		return resultado;
	}
	
	private List<InformacoesPerinataisVO> pesquisarInformacoesPerinataisCodigoPacienteUnion(Integer pacCodigo) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);		
		criteria.createAlias(McoGestacoes.Fields.MCO_RECEM_NASCIDOS.toString(), "RECEM_NASCIDOS", JoinType.INNER_JOIN);
		
		//criteria.createAlias("RECEM_NASCIDOS." + McoRecemNascidos.Fields.AGH_ATENDIMENTOS.toString(), "ATENDIMENTOS", JoinType.INNER_JOIN);
		criteria.createAlias("RECEM_NASCIDOS." + McoRecemNascidos.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createAlias("pac." + AipPacientes.Fields.ATENDIMENTOS.toString(), "ATENDIMENTOS", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RECEM_NASCIDOS." + McoRecemNascidos.Fields.GESTACOES_CODIGO_PACIENTE.toString()), InformacoesPerinataisVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property(McoGestacoes.Fields.CODIGO_PACIENTE.toString()), InformacoesPerinataisVO.Fields.GSO_PAC_CODIGO.toString())
				.add(Projections.property(McoGestacoes.Fields.SEQUENCE.toString()), InformacoesPerinataisVO.Fields.GSO_SEQP.toString())
				.add(Projections.property("RECEM_NASCIDOS." + McoRecemNascidos.Fields.SEQP.toString()), InformacoesPerinataisVO.Fields.RNA_SEQP.toString())
				.add(Projections.property("ATENDIMENTOS." + AghAtendimentos.Fields.SEQ), InformacoesPerinataisVO.Fields.ATD_SEQ.toString())
				.add(Projections.property(McoGestacoes.Fields.GESTA.toString()), InformacoesPerinataisVO.Fields.GSO_GESTA.toString())
				.add(Projections.property(McoGestacoes.Fields.PARA.toString()), InformacoesPerinataisVO.Fields.GSO_PARA.toString())
				.add(Projections.property(McoGestacoes.Fields.CESAREA.toString()), InformacoesPerinataisVO.Fields.GSO_CESAREA.toString())
				.add(Projections.property(McoGestacoes.Fields.ABORTO.toString()), InformacoesPerinataisVO.Fields.GSO_ABORTO.toString())
				.add(Projections.property("ATENDIMENTOS." + AghAtendimentos.Fields.DATA_HORA_INICIO.toString()), InformacoesPerinataisVO.Fields.DTHR_INICIO.toString())
				.add(Projections.property("ATENDIMENTOS." + AghAtendimentos.Fields.ORIGEM), InformacoesPerinataisVO.Fields.TIPO.toString())
		);
		
		criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		//criteria.add(Restrictions.eq("RECEM_NASCIDOS." + McoRecemNascidos.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.desc("ATENDIMENTOS." +AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(InformacoesPerinataisVO.class));
		return executeCriteria(criteria);		      
	}

    public boolean verificarExisteAtendimentoGestacaoUnidadeFuncional(Integer atdSeq,Short unfSeq){
        StringBuffer hql = new StringBuffer(200);
        hql.append(" select 1 from "+ McoGestacoes.class.getName()+" gso, "+ AghAtendimentos.class.getName()+" atd where")
                .append(" atd.seq = :atdSeq and ")
                .append(" atd.gsoPacCodigo = gso.paciente.codigo and ")
                .append(" atd.gsoSeqp = gso.id.seqp  and")
                .append(" atd.gsoPacCodigo is not null and")
                //.append(" gso.dthrSumarioAltaMae is null and")
                .append(" atd.unidadeFuncional.seq = :unfSeq ");
        Query q = createHibernateQuery(hql.toString());
        q.setLong("atdSeq", atdSeq);
        q.setLong("unfSeq", unfSeq);
        return !q.list().isEmpty();
    }

    public McoGestacoes obterUltimaGestacaoRNNormal(Integer pacCodigo){
        DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
        criteria.createAlias(McoGestacoes.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
        criteria.addOrder(Order.desc(McoGestacoes.Fields.SEQUENCE.toString()));
        List<McoGestacoes> lista = executeCriteria(criteria, 0, 1, null, true);
        return !lista.isEmpty()?lista.get(0):null;
    }

    public McoGestacoes obterUltimaGestacaoGE(Integer pacCodigo){
        DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
        criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString() , pacCodigo));
        criteria.add(Restrictions.isNotNull(McoGestacoes.Fields.DATA_HORA_SUMARIO_ALTA_MAE.toString()));
        criteria.addOrder(Order.desc(McoGestacoes.Fields.SEQUENCE.toString()));
        List<McoGestacoes> lista = executeCriteria(criteria, 0, 1, null, true);
        return !lista.isEmpty()?lista.get(0):null;
    }

	/**
	 * Consulta utilizada para recuperar os dados da gestação tendo o sequencial da gestação.
	 * 
	 * C5 de #36508 - Pesquisar Gestações
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public List<McoGestacoes> pesquisarPorPaciente(Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		}
		return super.executeCriteria(criteria);
	}
	
	public McoGestacoes pesquisarMcoGestacaoPorId(Integer pacCodigo, Short seqp) {
		final DetachedCriteria criteria = getCriteriaMcoGestacaoPorId(pacCodigo, seqp);
		return (McoGestacoes) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria getCriteriaMcoGestacaoPorId(Integer pacCodigo, Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
		
		criteria.add(Restrictions.eq(McoGestacoes.Fields.SEQUENCE.toString(), seqp));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		return criteria;
	}
	
	public String countMcoGestacaoPorId(Integer pacCodigo, Short seqp) {
		final DetachedCriteria criteria = getCriteriaMcoGestacaoPorId(pacCodigo, seqp);
		criteria.setProjection(Projections.property(McoGestacoes.Fields.GEMELAR.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<McoGestacoes> listaGestacaoPorPaciente(Integer pacCodigo, Byte gestacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
		
		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.GESTA.toString(), gestacao));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.GRAVIDEZ.toString(), DominioGravidez.GCO));
		
		return executeCriteria(criteria);
	}
	
	public Short obterMaxSeqpMcoGestacoes(Integer pacCodigo) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		
		criteria.setProjection(Projections.max(McoGestacoes.Fields.SEQUENCE.toString()));
		
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return (short) 1;
	}
	
	public String obterGemelarPorPaciente(Integer pacCodigo, Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class, "GES");
		criteria.createAlias("GES." + McoGestacoes.Fields.MCO_NASCIMENTOS.toString(), "NAS");
		
		criteria.setProjection(Projections.distinct(Projections.property("GES." + McoGestacoes.Fields.GEMELAR.toString())));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.SEQUENCE.toString(), seqp));
		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		List<String> result = super.executeCriteria(criteria, 0, 1, null, true);
		if(result != null && !result.isEmpty()){
			return result.get(0);
		}		
		return null;
	}
	
	// C3 - #864
	public List<McoGestacoes> obterUltimaGestacaoRegistrada(Integer pacCodigo){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoGestacoes.class);
		criteria.add(Restrictions.eq(McoGestacoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		
		Calendar dtGestacao = Calendar.getInstance();
		dtGestacao.setTime(new Date());
		dtGestacao.add(Calendar.MONTH, -9);
		
		criteria.add(Restrictions.gt(McoGestacoes.Fields.CRIADO_EM.toString(), dtGestacao.getTime()));
		criteria.addOrder(Order.desc(McoGestacoes.Fields.SEQUENCE.toString()));
		
		return executeCriteria(criteria);
	}
	
}
