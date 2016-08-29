package br.gov.mec.aghu.farmacia.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
import br.gov.mec.aghu.core.utils.DateUtil;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AfaPrescricaoMedicamentoDAO extends BaseDao<AfaPrescricaoMedicamento> {

	private static final long serialVersionUID = -3818667573543893268L;
	
	public String gerarNovoNumeroExternoBySequence(){
		return this.getNextVal(SequenceID.AFA_PMM_SQ2).toString();
	}
	  
	public void persistirAfaPrescricaoMedicamento(
			AfaPrescricaoMedicamento prescricaoMedicamento) {
		prescricaoMedicamento.setCriadoEm(new Date());
		
		persistir(prescricaoMedicamento);
	}
	
	public List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacaoUnion2(String leitoId, Integer prontuario, String nome, Date dtHrInicio,
			Date dtHrFim, String seqPrescricao, Boolean indPacAtendimento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaPrescricaoMedicamento.class, "apm");
		
		criteria.createAlias("apm."+AfaPrescricaoMedicamento.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		
 		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString()).as(MpmPrescricaoMedicaVO.Fields.LEITO.toString()))
		.add(Projections.property("pac." + AipPacientes.Fields.PRONTUARIO.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_PRONTUARIO.toString()))
		.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()).as(MpmPrescricaoMedicaVO.Fields.PAC_NOME.toString()))
		.add(Projections.property("apm." + AfaPrescricaoMedicamento.Fields.SEQ.toString()).as(MpmPrescricaoMedicaVO.Fields.AFA_SEQ.toString()))
		.add(Projections.property("apm." + AfaPrescricaoMedicamento.Fields.NUMERO_EXTERNO.toString()).as(MpmPrescricaoMedicaVO.Fields.PRESCRICAO.toString()))
		.add(Projections.property("apm." + AfaPrescricaoMedicamento.Fields.ATD_SEQ.toString()).as(MpmPrescricaoMedicaVO.Fields.ATD_SEQ.toString()))
		.add(Projections.property("apm." + AfaPrescricaoMedicamento.Fields.DTHR_INICIO.toString()).as(MpmPrescricaoMedicaVO.Fields.DTHR_INICIO.toString()))
		.add(Projections.property("apm." + AfaPrescricaoMedicamento.Fields.DTHR_FIM.toString()).as(MpmPrescricaoMedicaVO.Fields.DTHR_FIM.toString()))
		;
	criteria.setProjection(projectionList);
	
		if (seqPrescricao != null && !seqPrescricao.isEmpty()){
			criteria.add(Restrictions.eq(AfaPrescricaoMedicamento.Fields.NUMERO_EXTERNO.toString(), seqPrescricao));
		}
		
		if (StringUtils.isNotBlank(leitoId)){
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.LTO_LTO_ID.toString(), leitoId));
		}
		
		if (prontuario != null){
			criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		
		if (StringUtils.isNotBlank(nome)){
			criteria.add(Restrictions.ilike("pac." + AipPacientes.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if (dtHrInicio != null){
			criteria.add(Restrictions.ge("apm." + AfaPrescricaoMedicamento.Fields.DTHR_INICIO.toString(), dtHrInicio));
		}
		
		if (dtHrFim != null){
			criteria.add(Restrictions.le("apm." + AfaPrescricaoMedicamento.Fields.DTHR_FIM.toString(), dtHrFim));
		}
		
		if(indPacAtendimento){
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(MpmPrescricaoMedicaVO.class));
		
		return this.executeCriteria(criteria);
		
	}
	
	public List<AfaPrescricaoMedicamento> pesquisarPrescricaoMedicamentos(Integer prontuario, Date dtReferenciaMinima) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaPrescricaoMedicamento.class, "pre");
		criteria.createAlias("pre." + AfaPrescricaoMedicamento.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd." + AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.ge("pre." + AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString(), DateUtil.truncaData(dtReferenciaMinima)));
		criteria.addOrder(Order.desc("pre." + AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString()));
		return executeCriteria(criteria);
	}

	public List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(
			Date dataEmissaoInicio, Date dataEmissaoFim) {
		String alias = "pmm";
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaPrescricaoMedicamento.class, alias);

		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.groupProperty(AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString()),QuantidadePrescricoesDispensacaoVO.Fields.DATA_EMISSAO.toString())
						.add(Projections.count(AfaPrescricaoMedicamento.Fields.SER_VIN_CODIGO.toString()),QuantidadePrescricoesDispensacaoVO.Fields.QUANTIDADE_PRESCRICOES.toString())
		);

		criteria.add(Restrictions.ge(AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString(), dataEmissaoInicio));
		
		if(dataEmissaoFim != null){
			criteria.add(Restrictions.le(AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString(), dataEmissaoFim));
		}
		criteria.add(Restrictions.and(
				Subqueries.propertyIn(AfaPrescricaoMedicamento.Fields.ATD_SEQ.toString(), 	getAfaDispensacao(AfaDispensacaoMdtos.Fields.ATENDIMENTO_SEQ, alias)), 
				Subqueries.propertyIn(AfaPrescricaoMedicamento.Fields.SEQ.toString(), 		getAfaDispensacao(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ, alias))
				));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QuantidadePrescricoesDispensacaoVO.class));
		
		criteria.addOrder(Order.asc(AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString()));
		
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria getAfaDispensacao(AfaDispensacaoMdtos.Fields coluna, String alias) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaDispensacaoMdtos.class, "adm");
		cri.setProjection(
				Projections.property(coluna.toString())
				);
		String atdSeqMain = alias.concat(".".concat(AfaPrescricaoMedicamento.Fields.ATD_SEQ.toString()));
		String seqMain = alias.concat(".".concat(AfaPrescricaoMedicamento.Fields.SEQ.toString()));
		
		/*cri.createAlias(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO.toString(), "afaPrescricaoMdto");*/
		/*cri.createAlias(AfaDispensacaoMdtos.Fields.ATENDIMENTO_SEQ.toString(), "atdAfaPresc");*/
		
		cri.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.AFA_PRESCRICAO_MEDICAMENTO_SEQ.toString(), seqMain));
		cri.add(Restrictions.eqProperty(AfaDispensacaoMdtos.Fields.ATENDIMENTO_SEQ.toString(), atdSeqMain));
		cri.add(Restrictions.in(AfaDispensacaoMdtos.Fields.IND_SITUACAO.toString(), Arrays.asList(DominioSituacaoDispensacaoMdto.D, DominioSituacaoDispensacaoMdto.T)));
		
		return cri;
	}
	
	public List<AfaPrescricaoMedicamento> pesquisarPrescricaoMedicamentosByAtdSeqEDtRefrencia(Integer atdSeq, Date dtReferencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaPrescricaoMedicamento.class, "pre");
		//criteria.createAlias("pre." + AfaPrescricaoMedicamento.Fields.ATENDIMENTO.toString(), "atd");

		criteria.add(Restrictions.eq("pre." + AfaPrescricaoMedicamento.Fields.ATD_SEQ.toString(), atdSeq));
		
		criteria.add(Restrictions.between("pre." + AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString(), 
				DateUtil.truncaDataFim(DateUtil.adicionaDias(dtReferencia, -1)), DateUtil.truncaDataFim(dtReferencia)));
		
		criteria.addOrder(Order.desc("pre." + AfaPrescricaoMedicamento.Fields.DT_REFERENCIA.toString()));
		return executeCriteria(criteria);
	}
	
	
	
}
