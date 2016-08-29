package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioPOLVO;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
public class AghAtendimentoPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghAtendimentoPacientes> {

	private static final long serialVersionUID = -9018899633436577518L;


	@Override
	protected void obterValorSequencialId(AghAtendimentoPacientes elemento) {
			elemento.getId().setSeq(obterValorSequencialId());
	}
	
	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.AGH_APA_SQ1);
	}
	
	private DetachedCriteria obterCriteriaAtendimentoPacientes(){
	    DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentoPacientes.class);
	    return criteria;
	}
	
	/**
	 * Verifica se existe atendimento paciente
	 * @param altanAtdSeq
	 * @param apaSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarAghAtendimentoPacientes(Integer altanAtdSeq, Integer apaSeq) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = obterCriteriaAtendimentoPacientes();
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), altanAtdSeq));
		List<AghAtendimentoPacientes> atendimentoPacientes = executeCriteria(criteria);
		
		if (atendimentoPacientes != null && atendimentoPacientes.size() > 0) {
		
			return true;
		
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param altanAtdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer recuperarAtendimentoPaciente(Integer altanAtdSeq) throws ApplicationBusinessException { 
		
		DetachedCriteria criteria = obterCriteriaAtendimentoPacientes();
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), altanAtdSeq));
		criteria.addOrder(Order.desc(AghAtendimentoPacientes.Fields.SEQ.toString()));
		List<AghAtendimentoPacientes> retorno = executeCriteria(criteria);
		
		if (retorno != null && retorno.size() > 0) {
			
			return retorno.get(0).getId().getSeq();
			
		}
		
		return null;
		
	}
	
	/**
	 * Recupera atendimento paciente
	 * @param altanAtdSeq
	 * @param apaSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AghAtendimentoPacientes obterAtendimentoPaciente(Integer altanAtdSeq, Integer apaSeq) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = obterCriteriaAtendimentoPacientes();
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), altanAtdSeq));
		return (AghAtendimentoPacientes) executeCriteriaUniqueResult(criteria);
	}

	public List<AghAtendimentoPacientes> pesquisarAghAtendimentoPacientesPorAtendimento(AghAtendimentos atendimento) {
		Integer seq = atendimento != null ? atendimento.getSeq() : null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentoPacientes.class);
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), seq));
		return executeCriteria(criteria);
	}

	public List<AghAtendimentoPacientes> listarAtendimentosPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentoPacientes.class);

		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<AghAtendimentoPacientes> listarAtendimentosPacientes(Integer pacCodigo, Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentoPacientes.class);
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), seqAtendimento));

		return executeCriteria(criteria);
	}
	
	
	/**
	 * C2 da #18505 Sumário prescrição Quimio POL
	 * @param atdSeq
	 * @param apaSeq
	 * @return
	 */
	public SumarioQuimioPOLVO listarDadosCabecalhoSumarioPrescricaoQuimio(Integer atdSeq, Integer apaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentoPacientes.class, "apa");

		// where atd.seq = apa.atd_seq
		 criteria.createAlias("apa." + AghAtendimentoPacientes.Fields.ATENDIMENTO.toString(), "atd");
		 
		// where pac.codigo = atd.pac_codigo
		 criteria.createAlias("atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		 
		// where ser.matricula(+) = atd.ser_matricula
		//   and ser.vin_codigo(+) = atd.ser_vin_codigo  
		 criteria.createAlias("atd." + AghAtendimentos.Fields.SERVIDOR.toString(), "ser", Criteria.LEFT_JOIN);
		 
		// where pes.codigo(+) = ser.pes_codigo
		 criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", Criteria.LEFT_JOIN);
		 
		 // where esp.seq(+) = atd.esp_seq
		 criteria.createAlias("atd." + AghAtendimentos.Fields.ESPECIALIDADE.toString(),"esp", Criteria.LEFT_JOIN);
		 
		 // where int.seq(+) = atd.int_seq
		 criteria.createAlias("atd." + AghAtendimentos.Fields.INTERNACAO.toString(),"int", Criteria.LEFT_JOIN);
		 
		 // where apa.atd_seq = :atdSeq
		 criteria.add(Restrictions.eq("apa." + AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
	
		 // where apa.seq = :apaSeq
		 criteria.add(Restrictions.eq("apa." + AghAtendimentoPacientes.Fields.SEQ.toString(), apaSeq));

	
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("apa." + AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString()), 
					SumarioQuimioPOLVO.Fields.APA_ATD_SEQ.toString())
			.add(Projections.property("apa." + AghAtendimentoPacientes.Fields.SEQ.toString()), 
					SumarioQuimioPOLVO.Fields.APA_SEQ.toString())
			.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), 
					SumarioQuimioPOLVO.Fields.PAC_NOME.toString())
			.add(Projections.property("pac." + AipPacientes.Fields.SEXO.toString()), 
					SumarioQuimioPOLVO.Fields.PAC_SEXO.toString())
			.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()), 
					SumarioQuimioPOLVO.Fields.PAC_PRONTUARIO.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_LTO_ID.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.QRT_NUMERO.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_QRT_NUMERO.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.UNF_SEQ.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_UNF_SEQ.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_INICIO.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_DTHR_INICIO_TRAT.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.DTHR_FIM.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_DTHR_FIM_TRAT.toString())
			.add(Projections.property("pac." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), 
					SumarioQuimioPOLVO.Fields.PAC_DT_NASCIMENTO.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.INT_SEQ.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_INT_SEQ.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.ATU_SEQ.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_ATU_SEQ.toString())
			.add(Projections.property("atd." + AghAtendimentos.Fields.HOD_SEQ.toString()), 
					SumarioQuimioPOLVO.Fields.ATD_HOD_SEQ.toString())
			.add(Projections.property("int." + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()), 
					SumarioQuimioPOLVO.Fields.INT_DTHR_ALTA.toString())
			.add(Projections.property("pes." + RapPessoasFisicas.Fields.NOME.toString()), 
					SumarioQuimioPOLVO.Fields.PES_NOME.toString())
			.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), 
					SumarioQuimioPOLVO.Fields.ESP_NOME_ESPECIALIDADE.toString())
			.add(Projections.property("ser." + RapServidores.Fields.MATRICULA.toString()), 
					SumarioQuimioPOLVO.Fields.SER_MATRICULA.toString())
			.add(Projections.property("ser." + RapServidores.Fields.CODIGO_VINCULO.toString()), 
					SumarioQuimioPOLVO.Fields.SER_VIN_CODIGO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(SumarioQuimioPOLVO.class));
		
		return (SumarioQuimioPOLVO) executeCriteriaUniqueResult(criteria);
	}
	
public List<ListaPacientePrescricaoVO> obterListaDePacientes(Integer matricula, Short vinculo){
		
		//subQuery
		DetachedCriteria subQuery1 = DetachedCriteria.forClass(MpmListaServEspecialidade.class);
		
		subQuery1.createAlias(MpmListaServEspecialidade.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		subQuery1.add(Restrictions.eq("SER."+ RapServidores.Fields.MATRICULA.toString(), matricula));
		subQuery1.add(Restrictions.eq("SER."+ RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));
		subQuery1.setProjection(Projections.projectionList().add(Projections.property(MpmListaServEspecialidade.Fields.ESPECIALIDADE_SEQ.toString()), ListaPacientePrescricaoVO.Fields.LSE_ESP_SEQ.toString()));

		DetachedCriteria subQuery2 = DetachedCriteria.forClass(MpmPacAtendProfissional.class);
		
		subQuery2.createAlias(MpmPacAtendProfissional.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		subQuery2.add(Restrictions.eq("SER."+ RapServidores.Fields.MATRICULA.toString(), matricula));
		subQuery2.add(Restrictions.eq("SER."+ RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));
		subQuery2.setProjection(Projections.projectionList().add(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString()), ListaPacientePrescricaoVO.Fields.ATD_SEQ.toString()));
		
		DetachedCriteria subQuery3 = DetachedCriteria.forClass(MpmServidorUnidFuncional.class);
		
		subQuery3.createAlias(MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		subQuery3.add(Restrictions.eq("SER."+ RapServidores.Fields.MATRICULA.toString(), matricula));
		subQuery3.add(Restrictions.eq("SER."+ RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));
		subQuery3.setProjection(Projections.projectionList().add(Projections.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL_SEQ.toString()), ListaPacientePrescricaoVO.Fields.UNF_SEQ.toString()));
		
		DetachedCriteria subQuery4 = DetachedCriteria.forClass(MpmListaServEquipe.class);
		
		subQuery4.createAlias(MpmListaServEquipe.Fields.EQUIPE.toString(), "EQP", JoinType.INNER_JOIN);
		subQuery4.createAlias(MpmListaServEquipe.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		subQuery4.add(Restrictions.eq(MpmListaServEquipe.Fields.SER_MATRICULA.toString(), matricula));
		subQuery4.add(Restrictions.eq(MpmListaServEquipe.Fields.SER_VIN_CODIGO.toString(), vinculo));
		subQuery4.setProjection(Projections.projectionList()
				.add(Projections.property("EQP."+AghEquipes.Fields.SER_MATRICULA.toString()), ListaPacientePrescricaoVO.Fields.MATRICULA_SERVIDOR.toString())
				.add(Projections.property("EQP."+AghEquipes.Fields.SER_VIN_CODIGO.toString()), ListaPacientePrescricaoVO.Fields.VIN_CODIGO_SERVIDOR.toString()));
	
		
		DetachedCriteria subQuery5 = DetachedCriteria.forClass(MpmListaPacCpa.class);
			
		subQuery5.createAlias(MpmListaPacCpa.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		subQuery5.add(Restrictions.eq(MpmListaServEquipe.Fields.SER_MATRICULA.toString(), matricula));
		subQuery5.add(Restrictions.eq(MpmListaServEquipe.Fields.SER_VIN_CODIGO.toString(), vinculo));
		subQuery5.add(Restrictions.eq(MpmListaPacCpa.Fields.IND_PAC_CPA.toString(), DominioSimNao.S.isSim()));
		subQuery5.setProjection(Projections.property(MpmListaPacCpa.Fields.IND_PAC_CPA.toString()));

		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD."+AghAtendimentos.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.LEITO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD."+AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
					
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()), ListaPacientePrescricaoVO.Fields.ATD_SEQ.toString());
		projList.add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString()), ListaPacientePrescricaoVO.Fields.LTO_ID.toString());
		projList.add(Projections.property("QRT." + AinQuartos.Fields.NUMERO.toString()), ListaPacientePrescricaoVO.Fields.QRT_NUMERO.toString());
		projList.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), ListaPacientePrescricaoVO.Fields.UNF_SEQ.toString());
		projList.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString()), ListaPacientePrescricaoVO.Fields.DTHR_INICIO.toString());
		projList.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()),ListaPacientePrescricaoVO.Fields.PAC_CODIGO.toString());
		projList.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), ListaPacientePrescricaoVO.Fields.ATD_PRONTUARIO.toString());
		projList.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), ListaPacientePrescricaoVO.Fields.PAC_NOME.toString());
		projList.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), ListaPacientePrescricaoVO.Fields.MATRICULA_SERVIDOR.toString());
		projList.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), ListaPacientePrescricaoVO.Fields.VIN_CODIGO_SERVIDOR.toString());
		criteria.setProjection(projList);
		
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.in("ATD."+AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.getOrigensPrescricaoInternacao()));
		criteria.add(Restrictions.or(
				Subqueries.propertiesIn(new String[] {"ATD." + AghAtendimentos.Fields.ESPECIALIDADE_SEQ.toString()}, subQuery1),
				Subqueries.propertiesIn(new String[] {"ATD." + AghAtendimentos.Fields.SEQ.toString()}, subQuery2),
				Subqueries.propertiesIn(new String[] {"ATD." + AghAtendimentos.Fields.UNF_SEQ.toString()}, subQuery3),
				Subqueries.propertiesIn(new String[] {"SER." + RapServidores.Fields.MATRICULA.toString(),"SER." + RapServidores.Fields.VIN_CODIGO.toString()}, subQuery4),
				Restrictions.and(Restrictions.eq("ATD." + AghAtendimentos.Fields.IND_PAC_CPA.toString(), DominioSimNao.S.isSim()), Subqueries.exists(subQuery5))));
		criteria.addOrder(Order.asc("LTO."+AinLeitos.Fields.LTO_ID.toString()));
		criteria.addOrder(Order.asc("QRT."+AinQuartos.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("UNF."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ListaPacientePrescricaoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Date obterDtNascimentoPaciente(Integer codPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class);
		criteria.add(Restrictions.eq(AipPacientes.Fields.CODIGO.toString(), codPaciente));
		criteria.setProjection(Projections.property(AipPacientes.Fields.DATA_NASCIMENTO.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
}
