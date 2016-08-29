package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.emergencia.vo.DadosAcolhimentoBoletimAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.PacienteEmAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.TrgEncInternoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamFluxograma;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;

public class MamTrgEncInternoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgEncInterno> {

	private static final long serialVersionUID = 7222154807062491184L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(MamTrgEncInterno.class);
    }
	
	public List<MamTrgEncInterno> obterPorConsulta(AacConsultas consulta) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(MamTrgEncInterno.Fields.CONSULTA.toString(), consulta));
		criteria.add(Restrictions.isNull(MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria criarCriteriaPesquisarEncaminhamentoInternoPorSeqTriagem(Long trgSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "TEI");
		
		dc.add(Restrictions.eq("TEI.".concat(MamTrgEncInterno.Fields.TRG_SEQ.toString()), trgSeq));
		dc.add(Restrictions.isNull("TEI.".concat(MamTrgEncInterno.Fields.DTHR_ESTORNO.toString())));
		
		return dc;
	}
	
	/**
	 *Estoria #17315
	 *Q_TEI
	 * 
	 * */
	public MamTrgEncInterno pesquisarEncaminhamentoInternoPorSeqTriagem(Long trgSeq) {
		Object obj = executeCriteriaUniqueResult(criarCriteriaPesquisarEncaminhamentoInternoPorSeqTriagem(trgSeq));
		
		if (obj != null) {
			return (MamTrgEncInterno) obj;
		}
		return null;
	}
	
	public List<MamTrgEncInterno> obterPorConsultaOrderDesc(Integer numero) { 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "t1");
		
		criteria.createAlias("t1." + MamTrgEncInterno.Fields.TRIAGEM.toString(), "triagem");			
				
		criteria.add(Restrictions.eq("t1." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(), numero));
		criteria.add(Restrictions.isNull("t1." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.addOrder(Order.desc("t1." + MamTrgEncInterno.Fields.SEQ_P.toString()));		
//		return this.executeCriteria(criteria, 0, 1, MamTrgEncInterno.Fields.SEQP.toString(), false);
		return this.executeCriteria(criteria);
	}	
	
	public MamTrgEncInterno buscarTriagemPorConsulta(Integer numConsulta) { 
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "t1");
		
		criteria.createAlias("t1." + MamTrgEncInterno.Fields.TRIAGEM.toString(), "triagem");			
		criteria.add(Restrictions.eq("t1." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(), numConsulta));
		
		List<MamTrgEncInterno> result = super.executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
	}		
		return null;		
	}

	/**
	 * #42360 - Consulta para obter o cursor
	 * 
	SELECT tei.trg_seq
    FROM mam_trg_enc_internos tei
    WHERE tei.con_numero = c_con_numero
    AND tei.dthr_estorno IS NULL
    ORDER BY tei.seqp DESC;
	*/
	
	public List<MamTrgEncInterno> obterCursorTei(Integer cConNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEI");
		
		criteria.add(Restrictions.eq("TEI."+ MamTrgEncInterno.Fields.CON_NUMERO.toString(), cConNumero));
		criteria.add(Restrictions.isNull("TEI."+ MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		
		criteria.addOrder(Order.desc(MamTrgEncInterno.Fields.SEQ_P.toString()));
		
		return executeCriteria (criteria);
	}
	
	/**
	 * #44179 - C5
	 * @author marcelo.deus
	 */
	public Long obterTriagemPorAtendimento(Integer atendimentoSeq){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEI");
		
		criteria.createAlias("TEI." + MamTrgEncInterno.Fields.TRIAGEM.toString(), "TRG")
				.createAlias("TEI." + MamTrgEncInterno.Fields.CONSULTA.toString(), "CON")
				.createAlias("CON." + AacConsultas.Fields.ATENDIMENTO.toString(), "ATD");
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atendimentoSeq))
				.add(Restrictions.isNotNull("ATD." + AghAtendimentos.Fields.ATU_SEQ.toString()))
				.add(Restrictions.isNotNull("ATD." + AghAtendimentos.Fields.CON_NUMERO.toString()))
				.add(Restrictions.isNull("TEI." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()))
				.add(Restrictions.eq("TRG." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));

		criteria.add(Restrictions.sqlRestriction(" this_.seqp in (select max(teis.seqp) from agh.mam_trg_enc_internos teis  where  teis.trg_seq = this_.trg_seq)"));
		
		MamTrgEncInterno trgEncInt = (MamTrgEncInterno) executeCriteriaUniqueResult(criteria);
		
		if(trgEncInt != null && trgEncInt.getTriagem() != null){
			return trgEncInt.getTriagem().getSeq();
		} else {
			return null;
		}
	}
	
	/**
	 * #1378 - F3 CURSOR cur_tei
	 * @author marcelo.deus
	 */
	public MamTrgEncInterno recuperarCurTei(Long cTrgSeq){

		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class,"TEI");
		criteria.add(Restrictions.eq("TEI."+MamTrgEncInterno.Fields.TRG_SEQ.toString(), cTrgSeq));
		criteria.add(Restrictions.isNull("TEI."+MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.addOrder(Order.desc("TEI."+MamTrgEncInterno.Fields.SEQ_P.toString()));
		
		return (MamTrgEncInterno) executeCriteriaUniqueResult(criteria);
		
	}

	
	/**
	 * C7	
	 * @param seqTriagem
	 * @return
	 */
	public Short obterMaxSeqPTriagem(		
			Long seqTriagem){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "MamTriagemEncInterno");
			
		criteria.add(Restrictions.eq("MamTriagemEncInterno." + MamTrgEncInterno.Fields.TRG_SEQ.toString(), seqTriagem));
		criteria.setProjection(Projections.max("MamTriagemEncInterno."+ MamTrgEncInterno.Fields.SEQP.toString()));
		
		Short maxSeqP = (Short) this.executeCriteriaUniqueResult(criteria);
		if (maxSeqP != null) {
			return Short.valueOf(String.valueOf(maxSeqP + 1));
		}
		return 1;
	}	
	
	/****
	 * C12
	 * @param listConsultas
	 * @return
	 */	
	public List<Integer> obterQuantidadePacienteAtendimento(){
		
	    DominioTipoMovimento[] tipoDominio = new DominioTipoMovimento[]{DominioTipoMovimento.E, DominioTipoMovimento.S};
	
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "MamTrgEncInterno");
		criteria.createAlias("MamTrgEncInterno." + MamTrgEncInterno.Fields.TRIAGEM.toString(), "MamTriagem");
		
		criteria.setProjection(Projections.property("MamTrgEncInterno." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString()));
				
		criteria.add(Restrictions.eq("MamTriagem." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString() , DominioPacAtendimento.S));
		criteria.add(Restrictions.in("MamTriagem." + MamTriagens.Fields.ULT_TIPO_MVTO.toString(), tipoDominio));
		criteria.add(Restrictions.isNull("MamTrgEncInterno." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		
		DetachedCriteria subQuerySeq = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEIS");
		subQuerySeq.add(Restrictions.eqProperty("TEIS." + MamTrgEncInterno.Fields.TRG_SEQ.toString(), "MamTrgEncInterno." + MamTrgEncInterno.Fields.TRG_SEQ.toString()));
		subQuerySeq.add(Restrictions.isNull("TEIS." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		subQuerySeq.setProjection(Projections.max("TEIS."+ MamTrgEncInterno.Fields.SEQ_P.toString()));
		criteria.add(Subqueries.propertyEq("MamTrgEncInterno." + MamTrgEncInterno.Fields.SEQ_P.toString(), subQuerySeq));
		
		DetachedCriteria subQueryCarac = DetachedCriteria.forClass(MamCaractSitEmerg.class, "MamCaractSitEmerg");
		subQueryCarac.add(Restrictions.eqProperty("MamTriagem." + MamTriagens.Fields.SEG_SEQ.toString(), "MamCaractSitEmerg." + MamCaractSitEmerg.Fields.SEG_SEQ.toString()));
		subQueryCarac.add(Restrictions.eq("MamCaractSitEmerg." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), DominioCaracteristicaEmergencia.LISTA_EM_ATEND));
		subQueryCarac.setProjection(Projections.property("MamCaractSitEmerg." + MamCaractSitEmerg.Fields.SEG_SEQ.toString()));
		criteria.add(Subqueries.exists(subQueryCarac));
		
		return this.executeCriteria(criteria);
    }
	
	public List<TrgEncInternoVO> obterListaTrgEncInternoOrdenada(Long trgSeq) {
    	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class);
    	
    	criteria.setProjection(Projections.projectionList()
    			.add(Projections.property(MamTrgEncInterno.Fields.SEQP.toString())
    					,TrgEncInternoVO.Fields.SEQP.toString())
    			.add(Projections.property(MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString())
    					,TrgEncInternoVO.Fields.CON_NUMERO.toString()));
    	criteria.add(Restrictions.eq(MamTrgEncInterno.Fields.TRG_SEQ.toString(), trgSeq));
    	criteria.setResultTransformer(Transformers.aliasToBean(TrgEncInternoVO.class));
    	
    	return executeCriteria(criteria);
    }
    
    public DadosAcolhimentoBoletimAtendimentoVO obterDadosDaClassificacao(Integer consulta) {
    	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class);
    	criteria.createAlias(MamTrgEncInterno.Fields.TRIAGEM.toString(), "TRG",JoinType.INNER_JOIN);
    	criteria.createAlias("TRG." + MamTriagens.Fields.MAM_TRG_GRAVIDADE.toString(), "TGG",JoinType.INNER_JOIN);
    	criteria.createAlias("TGG." + MamTrgGravidade.Fields.MAM_GRAVIDADES.toString(), "GRV",JoinType.INNER_JOIN);
    	criteria.createAlias("TGG." + MamTrgGravidade.Fields.MAM_DESCRITORES.toString(), "DSC",JoinType.INNER_JOIN);
    	criteria.createAlias("DSC." + MamDescritor.Fields.FLUXOGRAMA.toString(), "FLX",JoinType.INNER_JOIN);

    	criteria.setProjection(Projections.projectionList()
    			.add(Projections.property("TRG." + MamTriagens.Fields.QUEIXA_PRINCIPAL.toString()))
    			.add(Projections.property("GRV." + MamGravidade.Fields.DESCRICAO.toString()))
    			.add(Projections.property("DSC." + MamDescritor.Fields.DESCRICAO.toString()))
    			.add(Projections.property("FLX." + MamFluxograma.Fields.DESCRICAO.toString())));

    	criteria.add(Restrictions.eq(MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(), consulta));
    	
    	criteria.addOrder(Order.desc("TGG." + MamTrgGravidade.Fields.SEQP.toString()));
    	
    	List<Object[]> lstObj = executeCriteria(criteria);
    	DadosAcolhimentoBoletimAtendimentoVO retorno = new DadosAcolhimentoBoletimAtendimentoVO();
		for(Object[] obj : lstObj) {
			retorno.setQueixaPrincipal((String) obj[0]);
			retorno.setGravidade((String) obj[1]);
			retorno.setDescritor((String) obj[2]);
			retorno.setFluxograma((String) obj[3]);
			break;
		}
		return retorno;
    }

    public String obterProtocolo(Integer consulta) {
    	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEI");
    	criteria.createAlias(MamTrgEncInterno.Fields.TRIAGEM.toString(), "TRG",JoinType.INNER_JOIN);
    	criteria.createAlias("TRG." + MamTriagens.Fields.MAM_UNID_ATENDIMENTO.toString(), "UAN",JoinType.INNER_JOIN);
    	criteria.createAlias("UAN." + MamUnidAtendem.Fields.MAM_PROT_CLASSIF_RISCO.toString(), "PCR",JoinType.INNER_JOIN);

    	criteria.setProjection(Projections.projectionList()
    			.add(Projections.property("PCR." + MamProtClassifRisco.Fields.DESCRICAO.toString())));

    	criteria.add(Restrictions.eq("TEI." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(), consulta));
    	
    	DetachedCriteria subQuerySeq = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEIS");
		subQuerySeq.add(Restrictions.eqProperty("TEIS." + MamTrgEncInterno.Fields.TRG_SEQ.toString(), "TEI." + MamTrgEncInterno.Fields.TRG_SEQ.toString()));
		subQuerySeq.add(Restrictions.isNull("TEIS." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		subQuerySeq.setProjection(Projections.max("TEIS."+ MamTrgEncInterno.Fields.SEQ_P.toString()));
		criteria.add(Subqueries.propertyEq("TEI." + MamTrgEncInterno.Fields.SEQ_P.toString(), subQuerySeq));
    	
    	return (String)executeCriteriaUniqueResult(criteria);
    }

	/**
	 * Obtém o identificador da triagem
	 * 
	 * C7 de #37859
	 * 
	 * @param consulta
	 * @return
	 */
	public Long obterTrgSeq(Integer consulta) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class);
		criteria.add(Restrictions.eq(MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(), consulta));
		criteria.add(Restrictions.isNull(MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		criteria.setProjection(Projections.property(MamTrgEncInterno.Fields.TRG_SEQ.toString()));
		List<Long> result = super.executeCriteria(criteria, 0, 1, MamTrgEncInterno.Fields.CRIADO_EM.toString(), false);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public List<PacienteEmAtendimentoVO> listarPacientesEmAtendimento(Short unfSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEI");
		criteria.createAlias("TEI." + MamTrgEncInterno.Fields.TRIAGEM.toString(), "TRG");
		criteria.createAlias("TRG." + MamTriagens.Fields.MAM_UNID_ATENDIMENTO.toString(), "UAN");
		criteria.createAlias("TRG." + MamTriagens.Fields.SITUACAO_EMERGENCIA.toString(), "SEG");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("TRG." + MamTriagens.Fields.PAC_CODIGO.toString())
						, PacienteEmAtendimentoVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("TRG." + MamTriagens.Fields.SEQ.toString())
						,PacienteEmAtendimentoVO.Fields.TRG_SEQ.toString())
				.add(Projections.property("TRG." + MamTriagens.Fields.SEG_SEQ.toString())
						,PacienteEmAtendimentoVO.Fields.SEG_SEQ.toString())
				.add(Projections.property("TRG." + MamTriagens.Fields.ULT_TIPO_MVTO.toString())
						,PacienteEmAtendimentoVO.Fields.ULT_TIPO_MVTO.toString())
				.add(Projections.property("TRG." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString())
						,PacienteEmAtendimentoVO.Fields.IND_PAC_ATENDIMENTO.toString())
				.add(Projections.property("TRG." + MamTriagens.Fields.DTHR_ULT_MVTO.toString())
						,PacienteEmAtendimentoVO.Fields.DT_HR_ULT_MVTO.toString())
				.add(Projections.property("TRG." + MamTriagens.Fields.UNF_SEQ.toString())
						,PacienteEmAtendimentoVO.Fields.TRG_UNF_SEQ.toString())
				.add(Projections.property("SEG." + MamSituacaoEmergencia.Fields.DESCRICAO.toString())
						,PacienteEmAtendimentoVO.Fields.SEG_DESCRICAO.toString())
				.add(Projections.property("TEI." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString())
						,PacienteEmAtendimentoVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("TEI." + MamTrgEncInterno.Fields.SEQP.toString())
						,PacienteEmAtendimentoVO.Fields.TEI_SEQP.toString()));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamCaractSitEmerg.class, "MCE");
		subCriteria.setProjection(Projections.property("MCE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("MCE." + MamCaractSitEmerg.Fields.SEG_SEQ.toString(),
				"TRG." + MamTriagens.Fields.SEG_SEQ.toString()));
		subCriteria.add(Restrictions.eq("MCE." + MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), DominioCaracteristicaEmergencia.LISTA_EM_ATEND));
		
		criteria.add(Subqueries.exists(subCriteria));
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq("TRG." + MamTriagens.Fields.ULT_TIPO_MVTO.toString(), DominioTipoMovimento.E));
		criteria.add(Restrictions.eq("UAN." + MamUnidAtendem.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.isNull("TEI." + MamTrgEncInterno.Fields.DTHR_ESTORNO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteEmAtendimentoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public MamTrgEncInterno obterUltimoEncaminhamentoInterno(Long trgSeq){
    	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class);
    	criteria.add(Restrictions.eq(MamTrgEncInterno.Fields.TRG_SEQ.toString(), trgSeq));
		    	
    	criteria.addOrder(Order.desc(MamTrgEncInterno.Fields.SEQP.toString()));
    	List<MamTrgEncInterno> result = executeCriteria(criteria);

    	if(result.isEmpty()){
	    	return null;
	    } else {
	    	return result.get(0);
	    }
    }
	
	//C8 #27542 - Consulta utilizada para obter os dados da triagem associada ao número da consulta
	public List<MamTrgEncInterno> obterTriagemPorNumeroDaConsulta(Integer numeroConsulta) {
	   	final DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgEncInterno.class, "TEI");
	   	
    	criteria.createAlias(MamTrgEncInterno.Fields.TRIAGEM.toString(), "TRG",JoinType.INNER_JOIN);
	    criteria.add(Restrictions.eq("TEI." + MamTrgEncInterno.Fields.CONSULTA_NUMERO.toString(), numeroConsulta));
	    	
	 	return executeCriteria(criteria);
	}

}
