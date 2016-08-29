package br.gov.mec.aghu.perinatologia.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoLogImpressoesId;
import br.gov.mec.aghu.model.McoRecemNascidos;

public class McoLogImpressoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoLogImpressoes> {

	private static final long serialVersionUID = -4264285863919266052L;

	public List<McoLogImpressoes> listarLogImpressoes(Integer codigoPaciente, Short sequence) {
		return pesquisarLogImpressoes(codigoPaciente, sequence, null, null);
	}
	
	public List<McoLogImpressoes> listarLogImpressoesPorCodigoPaciente(Integer codigoPaciente) {
		return pesquisarLogImpressoes(codigoPaciente, null, null, null);
	}

	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorConsultaObs(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return pesquisarLogImpressoes(gsoPacCodigo, gsoSeqp, conNumero, DominioEventoLogImpressao.MCOR_CONSULTA_OBS);
	}

	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorAdmissaoObs(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		return pesquisarLogImpressoes(gsoPacCodigo, gsoSeqp, conNumero, DominioEventoLogImpressao.MCOR_ADMISSAO_OBS);
	}

	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorNascimento(Integer gsoPacCodigo, Short gsoSeqp) {
		return pesquisarLogImpressoes(gsoPacCodigo, gsoSeqp, null, DominioEventoLogImpressao.MCOR_NASCIMENTO);
	}

	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorRnSlParto(Integer gsoPacCodigo, Short gsoSeqp) {
		return pesquisarLogImpressoes(gsoPacCodigo, gsoSeqp, null, DominioEventoLogImpressao.MCOR_RN_SL_PARTO);
	}

	public List<McoLogImpressoes> pesquisarLogImpressoesEventoMcorExFisicoRn(Integer gsoPacCodigo, Short gsoSeqp) {
		return pesquisarLogImpressoes(gsoPacCodigo, gsoSeqp, null, DominioEventoLogImpressao.MCOR_EX_FISICO_RN);
	}
	
	public List<McoLogImpressoes> pesquisarLogImpressoes(Integer gsoPacCodigo,
			Short gsoSeqp, Integer conNumero, DominioEventoLogImpressao evento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		
		if(gsoPacCodigo != null){
			criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		}
		
		if(gsoSeqp != null){
			criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		}
		
		if(conNumero != null) {
			criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CON_NUMERO.toString(), conNumero));
		}
		
		if(evento != null){
			criteria.add(Restrictions.eq(McoLogImpressoes.Fields.EVENTO.toString(), evento.getCodigo()));
		}
		
		criteria.addOrder(Order.desc(McoLogImpressoes.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}

	protected DetachedCriteria criteriaImpressaoLog(Integer pacCodigo, String evento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.createAlias(McoLogImpressoes.Fields.RECEM_NASCIDO.toString(), "RNSC");
		criteria.createAlias("RNSC."+McoRecemNascidos.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.EVENTO.toString(), evento));

		return criteria;
	}

	public Boolean isImpressaoLogNecessaria(Integer pacCodigo) {
		return executeCriteriaCount(criteriaImpressaoLog(pacCodigo, "MCOR_RN_SL_PARTO")) > 0 ? true : false;
	}

	public Boolean isImpressaoRelatorioFisicoNecessaria(Integer pacCodigo) {
		return executeCriteriaCount(criteriaImpressaoLog(pacCodigo, "MCOR_EX_FISICO_RN")) > 0 ? true : false;
	}

	public Boolean verificaExisteLogImpressao(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));			
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(),
				new String[] {DominioEventoLogImpressao.MCOR_ADMISSAO_OBS.getCodigo(),
			DominioEventoLogImpressao.MCOR_RN_SL_PARTO.getCodigo(), DominioEventoLogImpressao.MCOR_CONSULTA_OBS.getCodigo(),
			DominioEventoLogImpressao.MCOR_EX_FISICO_RN.getCodigo(), DominioEventoLogImpressao.MCOR_NASCIMENTO.getCodigo()}));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	// C5 - #864
	public McoLogImpressoes verificaImpressaoAdmissaoObstetrica(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));			
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.EVENTO.toString(), DominioEventoLogImpressao.MCOR_ADMISSAO_OBS.getCodigo()));
		
		List<McoLogImpressoes> lista = executeCriteria(criteria, 0, 1, null, false);
		
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		
		return null;
	}
	
	/**
	 * #28986 C7 - Obter data inicio log impressões
	 * @param pacCodigo
	 * @return
	 */
	public Date obterDataInicioLogImpressoes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));			
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.EVENTO.toString(), DominioEventoLogImpressao.MCOR_CONSULTA_OBS.getCodigo()));
		criteria.setProjection(Projections.property(McoLogImpressoes.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.desc(McoLogImpressoes.Fields.CRIADO_EM.toString()));
		
		List<Date> list =  executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return (Date) list.get(0);
		}
		
		return null;
	}

	/**
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @param eventos
	 * @return
	 */
	public DetachedCriteria montarCriteriaImpressao(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero,
			DominioEventoLogImpressao... eventos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CON_NUMERO.toString(), conNumero));
		if (eventos != null && eventos.length > 0) {
			List<String> codigos = new ArrayList<String>();
			for (DominioEventoLogImpressao evento : eventos) {
				codigos.add(evento.getCodigo());
			}
			criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(), codigos));
		}
		return criteria;
	}

	/**
	 * Consulta utilizada para obter a ultima impressão
	 * 
	 * C4 de #26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @param eventos
	 * @return
	 */
	public McoLogImpressoes buscarUltimaImpressao(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero,
			DominioEventoLogImpressao... eventos) {
		McoLogImpressoes resultado = null;
		DetachedCriteria criteria = this.montarCriteriaImpressao(gsoPacCodigo, gsoSeqp, conNumero, eventos);
		List<McoLogImpressoes> list = super.executeCriteria(criteria, 0, 1, McoLogImpressoes.Fields.CRIADO_EM.toString(), false);
		if (list != null && !list.isEmpty()) {
			resultado = list.get(0);
		}
		return resultado;
	}

	/**
	 * Consulta utilizada para obter o evento de impressões para a gestação atual e consulta
	 * 
	 * C5 de #26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @param eventos
	 * @return
	 */
	public String buscarUltimoEvento(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero, DominioEventoLogImpressao... eventos) {
		String resultado = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.or(Restrictions.eq(McoLogImpressoes.Fields.CON_NUMERO.toString(), conNumero),
				Restrictions.isNull(McoLogImpressoes.Fields.CON_NUMERO.toString())));

		if (eventos != null && eventos.length > 0) {
			List<String> codigos = new ArrayList<String>();
			for (DominioEventoLogImpressao evento : eventos) {
				codigos.add(evento.getCodigo());
			}
			criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(), codigos));
		}

		criteria.setProjection(Projections.property(McoLogImpressoes.Fields.EVENTO.toString()));

		List<String> list = super.executeCriteria(criteria, 0, 1, McoLogImpressoes.Fields.CRIADO_EM.toString(), false);
		if (list != null && !list.isEmpty()) {
			resultado = list.get(0);
		}

		return resultado;
	}

	/**
	 * Consulta utilizada para verificar se o relatorio MCOR_CONSULTA_OBS já foi impresso
	 * 
	 * C6 de #26349
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @param eventos
	 * @return
	 */
	public boolean verificarExisteImpressao(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero, DominioEventoLogImpressao... eventos) {
		DetachedCriteria criteria = this.montarCriteriaImpressao(gsoPacCodigo, gsoSeqp, conNumero, eventos);
		return super.executeCriteriaExists(criteria);
	}
	

	@Override
	protected void obterValorSequencialId(McoLogImpressoes elemento) {
		Integer seqp = gerarProximoSeqp(elemento.getMcoGestacoes().getId().getPacCodigo()
				, elemento.getMcoGestacoes().getId().getSeqp());
		
		McoLogImpressoesId id = new McoLogImpressoesId();	
		id.setGsoPacCodigo(elemento.getMcoGestacoes().getId().getPacCodigo());
		id.setGsoSeqp(elemento.getMcoGestacoes().getId().getSeqp());
		id.setSeqp(seqp);
		elemento.setId(id);
		
	}

	
	/**
	 * C2 de 26349
	 * 
	 * Consulta utilizada para obter o SEQP para inserção do log de impressão
	 * 
	 */
	private Integer gerarProximoSeqp(Integer gsoPacCodigo, Short gsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.setProjection(Projections.max(McoLogImpressoes.Fields.SEQ.toString()));
		Integer seqp = (Integer) super.executeCriteriaUniqueResult(criteria);
		if (seqp == null) {
			seqp = 0;
		}
		seqp++;
		return seqp;
	}
	
	//C11 - #26325
	public Integer obterSeqpLogImpressao(Integer pacCodigo, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);
		
		criteria.setProjection(Projections.max(McoLogImpressoes.Fields.SEQ.toString()));
		
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), seqp));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	// Criteria - #28249
	public DetachedCriteria montarCriteria(Integer gsoPacCodigo, Short gsoSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(McoLogImpressoes.class);

		if(isOracle()){
			criteria.setProjection(Projections.sqlProjection("trunc(sysdate - {alias}." + McoLogImpressoes.Fields.CRIADO_EM.name() + ") qde ", new String[]{"qde"}, new Type[] {IntegerType.INSTANCE}));
		} else {
			criteria.setProjection(Projections.sqlProjection("date_trunc(now() - {alias}." + McoLogImpressoes.Fields.CRIADO_EM.name() + ") qde ", new String[]{"qde"}, new Type[] {IntegerType.INSTANCE}));
		}
		
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.addOrder(Order.asc(McoLogImpressoes.Fields.CRIADO_EM.toString()));
		return criteria;
	}
	
	// C1 - #28249
	public Integer cLogCons(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero){
		DetachedCriteria criteria = this.montarCriteria(gsoPacCodigo, gsoSeqp);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(),
				new String[] {
					DominioEventoLogImpressao.MCOR_CONSULTA_OBS.getCodigo(),
					DominioEventoLogImpressao.MCOR_ADMISSAO_OBS.getCodigo()}));
		List<Integer> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	// C2 - #28249
	public Integer cLogRn(Integer gsoPacCodigo, Short gsoSeqp, Byte rnaSeqp){
		DetachedCriteria criteria = this.montarCriteria(gsoPacCodigo, gsoSeqp);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.RNA_SEQP.toString(), rnaSeqp));
		criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(),
				new String[] {
					DominioEventoLogImpressao.MCOR_RN_SL_PARTO.getCodigo()}));
		List<Integer> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}	
	
	// C3 - #28249
	public Integer cLogEx(Integer gsoPacCodigo, Short gsoSeqp, Byte rnaSeqp){
		DetachedCriteria criteria = this.montarCriteria(gsoPacCodigo, gsoSeqp);
		criteria.add(Restrictions.eq(McoLogImpressoes.Fields.RNA_SEQP.toString(), rnaSeqp));
		criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(),
				new String[] {
					DominioEventoLogImpressao.MCOR_EX_FISICO_RN.getCodigo()}));
		List<Integer> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	// C4 - #28249
	public Integer cLogNas(Integer gsoPacCodigo, Short gsoSeqp){
		DetachedCriteria criteria = this.montarCriteria(gsoPacCodigo, gsoSeqp);
		criteria.add(Restrictions.in(McoLogImpressoes.Fields.EVENTO.toString(),
				new String[] {DominioEventoLogImpressao.MCOR_NASCIMENTO.getCodigo()}));
		List<Integer> lista = executeCriteria(criteria);
		if(lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}		
}
