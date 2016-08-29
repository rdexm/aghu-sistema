package br.gov.mec.aghu.controleinfeccao.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class NotificacoesPacienteInfeccaoTopografiaQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private static final long serialVersionUID = -2163829901907470577L;
	private static final String PONTO = ".";
	private static final String ALIAS_MIT = "MIT"; // MCI_MVTO_INFECCAO_TOPOGRAFIAS
	private static final String ALIAS_MMP = "MMP"; // MCI_MVTO_MEDIDA_PREVENTIVAS
	private static final String ALIAS_TOP = "TOP"; // MCI_TOPOGRAFIA_PROCEDIMENTOS
	private static final String ALIAS_EIN = "EIN"; // MCI_ETIOLOGIA_INFECCOES
	private static final String ALIAS_ATD = "ATD"; // AGH_ATENDIMENTOS
	private static final String ALIAS_POD = "POD"; // MBC_PROC_DESCRICOES
	private static final String ALIAS_PCI = "PCI"; // MBC_PROCEDIMENTO_CIRURGICOS
	private static final String ALIAS_DCG = "DCG"; // MBC_DESCRICAO_CIRURGICAS
	private static final String ALIAS_CRG = "CRG"; // MBC_CIRURGIAS

	private DetachedCriteria criteria;
	private Boolean isOrder;
	private Boolean isCount;
	private Integer codigoPaciente;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class, ALIAS_MIT);
	}
	
	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		if(!isCount){
			setProjecao();
		}
		
		if(isOrder){
			setOrder();
		}
	}

	private void setJoin() {
		criteria.createAlias(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.MVTO_MEDIDA_PREVENTIVAS.toString(), ALIAS_MMP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO.toString(), ALIAS_TOP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.ETIOLOGIA_INFECCAO.toString(), ALIAS_EIN, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.ATENDIMENTO.toString(), ALIAS_ATD, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.PROC_DESCRICOES.toString(), ALIAS_POD, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_POD + PONTO + MbcProcDescricoes.Fields.PROCEDIMENTO_CIRURGICO.toString(), ALIAS_PCI, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_POD + PONTO + MbcProcDescricoes.Fields.DESCRICAO_CIRURGICA.toString(), ALIAS_DCG, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_DCG + PONTO + MbcDescricaoCirurgica.Fields.MBC_CIRURGIAS.toString(), ALIAS_CRG, JoinType.LEFT_OUTER_JOIN);
	}

	private void setFiltro() {
		if(codigoPaciente != null ){
			criteria.add(Restrictions.eq(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.COD_PACIENTE.toString(), codigoPaciente));
		}
	}
	
	private void setProjecao() {
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.ATENDIMENTO_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_ATENDIMENTO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.COD_PACIENTE.toString()), NotificacaoTopografiasVO.Fields.CODIGO_PACIENTE.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.EIN_TIPO.toString()), NotificacaoTopografiasVO.Fields.CODIGO_ETIOLOGIA_INFECCAO.toString())
			
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_TOPOGRAFIA_PROCEDIMENTO.toString())
			.add(Projections.property(ALIAS_TOP + PONTO + MciTopografiaProcedimento.Fields.DESCRICAO.toString()), NotificacaoTopografiasVO.Fields.DESCRICAO_TOPOGRAFIA_PROCEDIMENTO.toString())
			
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.CRIADO_EM.toString()), NotificacaoTopografiasVO.Fields.CRIADO_EM.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.DT_INICIO.toString()), NotificacaoTopografiasVO.Fields.DATA_INICIO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.CONFIRMACAO_CCI.toString()), NotificacaoTopografiasVO.Fields.CONFIRMACAO_CCI.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SERVIDOR_MATRICULA.toString()), NotificacaoTopografiasVO.Fields.SERVIDOR_MATRICULA.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SERVIDOR_VIN_CODIGO.toString()), NotificacaoTopografiasVO.Fields.SERVIDOR_VIN_CODIGO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.UNF_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_UNIDADE_FUNCIONAL.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.UNF_SEQ_NOTIFICADO.toString()), NotificacaoTopografiasVO.Fields.SEQ_UNIDADE_FUNCIONAL_NOTIFICADA.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.QRT_NUMERO.toString()), NotificacaoTopografiasVO.Fields.NUMERO_QUARTO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.QRT_NUMERO_NOTIFICADO.toString()), NotificacaoTopografiasVO.Fields.NUMERO_QUARTO_NOTIFICADO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.LTO_LTO_ID.toString()), NotificacaoTopografiasVO.Fields.LEITO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.LTO_LTO_ID_NOTIFICADO.toString()), NotificacaoTopografiasVO.Fields.LEITO_NOTIFICADO.toString())
			
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SERVIDOR_CONFIRMADO_MATRICULA.toString()), NotificacaoTopografiasVO.Fields.SERVIDOR_MATRICULA_CONFIRMADO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SERVIDOR_CONFIRMADO_VIN_CODIGO.toString()), NotificacaoTopografiasVO.Fields.SERVIDOR_VIN_CODIGO_CONFIRMADO.toString())
			
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.DT_FIM.toString()), NotificacaoTopografiasVO.Fields.DATA_FIM.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.MOTIVO_ENCERRAMENTO.toString()), NotificacaoTopografiasVO.Fields.MOTIVO_ENCERRAMENTO.toString())
			
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SERVIDOR_ENCERRADO_MATRICULA.toString()), NotificacaoTopografiasVO.Fields.SERVIDOR_ENCERRADO_MATRICULA.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.SERVIDOR_CONFIRMADO_VIN_CODIGO.toString()), NotificacaoTopografiasVO.Fields.SERVIDOR_ENCERRADO_VIN_CODIGO.toString())
			
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.INSTITUICAO_HOSPITALAR_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_INSTITUICOA_HOSPITALAR.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.RNI_PNN_SEQ.toString()), NotificacaoTopografiasVO.Fields.RNI_PNN_SEQ.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.RNI_SEQP.toString()), NotificacaoTopografiasVO.Fields.RNI_SEQP.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.IND_CONTAMINACAO.toString()), NotificacaoTopografiasVO.Fields.IND_CONTAMINACAO.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.POD_DCG_CRG_SEQ.toString()), NotificacaoTopografiasVO.Fields.POD_DCG_CRG_SEQ.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.POD_DCG_SEQP.toString()), NotificacaoTopografiasVO.Fields.POD_DCG_SEQP.toString())
			.add(Projections.property(ALIAS_MIT + PONTO + MciMvtoInfeccaoTopografias.Fields.POD_SEQP.toString()), NotificacaoTopografiasVO.Fields.POD_SEQP.toString())
			.add(Projections.property(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), NotificacaoTopografiasVO.Fields.DESCRICAO_PROCED.toString())
			.add(Projections.property(ALIAS_CRG + PONTO + MbcCirurgias.Fields.DTHR_INICIO_CIRG.toString()), NotificacaoTopografiasVO.Fields.DTHR_INICIO_CIRG.toString())
			
			// serviços
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.UNF_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_UNIDADE_FUNCIONA_ATENDIMENTO.toString())
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.QRT_NUMERO.toString()), NotificacaoTopografiasVO.Fields.NUMERO_QUARTO_ATENDIMENTO.toString())
			
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.DATA_HORA_INICIO.toString()), NotificacaoTopografiasVO.Fields.DTHR_INICIO_ATENDIMENTO.toString())
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.CON_NUMERO.toString()), NotificacaoTopografiasVO.Fields.NUMERO_CONSULTA.toString())
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.ATU_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_ATENDIMENTO_URGENCIA.toString())
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.INT_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_INTERNACAO.toString())
			.add(Projections.property(ALIAS_ATD + PONTO + AghAtendimentos.Fields.HOD_SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_HOSPITAIS_DIA.toString())
			
			.add(Projections.property(ALIAS_MMP + PONTO + MciMvtoMedidaPreventivas.Fields.SEQ.toString()), NotificacaoTopografiasVO.Fields.SEQ_MEDIDAD_PREVENTIVA.toString()) 
		);

		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoTopografiasVO.class));
	}
	
	private void setOrder() {
		criteria.addOrder(Order.desc(ALIAS_ATD + PONTO + AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
	}

	public DetachedCriteria build(Boolean isOrder, Boolean isCount, Integer codigoPaciente) {
		this.isOrder = isOrder;
		this.isCount = isCount;
		this.codigoPaciente = codigoPaciente;

		return super.build();
	}

}
