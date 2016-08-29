package br.gov.mec.aghu.faturamento.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.faturamento.vo.FatEspelhoEncerramentoPreviaVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class FatEspelhoEncerramentoPreviaVOQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -8403676521295401183L;

	private static final String ALIAS_FE  = "FE";
	private static final String ALIAS_PAC = "PAC";
	private static final String ALIAS_IPHR = "IPHR";
	private static final String ALIAS_IPHS = "IPHS";
	private static final String ALIAS_AC = "AC";
	private static final String ALIAS_CTH = "CTH";
	
	private static final String PONTO  = ".";
	private static final Integer SEQP_VALUE = 1;
	
	private DetachedCriteria criteria;
	private ProjectionList listProj = Projections.projectionList();
	private Integer cthSeq;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatEspelhoAih.class, ALIAS_FE);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
		setProjecaoContinuacaoPrimeiro();
		setProjecaoContinuacaoSegundo();
		setProjecaoFinal();
		setOrder();
	}
	
	private void setJoin() {
		criteria.createAlias(ALIAS_FE+PONTO+FatEspelhoAih.Fields.ESPECIALIDADE_AIH_CLINICA.toString(), ALIAS_AC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_FE+PONTO+FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), ALIAS_CTH, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_FE+PONTO+FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), ALIAS_IPHR,JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_FE+PONTO+FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_SOLICITADO.toString(), ALIAS_IPHS, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_FE+PONTO+FatEspelhoAih.Fields.PAC_PRONTUARIO_ENTIDADE.toString(), ALIAS_PAC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_FE+PONTO+FatEspelhoAih.Fields.FAT_COMPETENCIA.toString(), "CPE", JoinType.LEFT_OUTER_JOIN);
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_FE + PONTO + FatEspelhoAih.Fields.CTH_SEQ.toString(), this.cthSeq));
		criteria.add(Restrictions.eq(ALIAS_FE + PONTO + FatEspelhoAih.Fields.SEQP.toString(), SEQP_VALUE));
	}
	
	private void setProjecao() {
		
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NUMERO_AIH.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NUMERO_AIH.toString());
		listProj.add(Projections.property(ALIAS_CTH + PONTO + FatContasHospitalares.Fields.IND_SITUACAO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.IND_SITUACAO.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.AIH_DTHR_EMISSAO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.AIH_DTHR_EMISSAO.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.ESPECIALIDADE_AIH.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.ESPECIALIDADE_AIH.toString());
		listProj.add(Projections.property(ALIAS_AC + PONTO + AghClinicas.Fields.DESCRICAO.toString()),
				FatEspelhoEncerramentoPreviaVO.Fields.DESCRICAO_ESPECIALIDADE.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.ENFERMARIA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.ENFERMARIA.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.LEITO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.LEITO.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.CPF_MEDICO_AUDITOR.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CPF_MEDICO_AUDITOR.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CPF_MEDICO_RESPONS.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.CPF_MEDICO_SOLIC_RESPONS.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CPF_MEDICO_SOLIC.toString());
		listProj.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.NRO_CARTAO_SAUDE.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NRO_CARTAO_SAUDE.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.PAC_NOME.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_NOME_PACIENTE.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.PAC_PRONTUARIO_ENTIDADE.toString()+".prontuario"), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_PRONTUARIO.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.PAC_DT_NASCIMENTO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_DT_NASCIMENTO.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.PAC_SEXO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_SEXO.toString());
		
		criteria.setProjection(listProj);
	}
	
	private void setProjecaoContinuacaoPrimeiro(){
		
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.NACIONALIDADE_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NACIONALIDADE_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE + PONTO + FatEspelhoAih.Fields.IND_DOC_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.IND_DOC_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.PAC_RG.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_RG.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NOME_RESPONSAVEL_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NOME_RESPONSAVEL_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.PAC_NOME_MAE.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_NOME_MAE.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.END_LOGRADOURO_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.END_LOGRADOURO_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.END_NRO_LOGRADOURO_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.END_NRO_LOGRADOURO_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.END_BAIRRO_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.END_BAIRRO_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.PAC_COR.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.PAC_COR.toString());
		listProj.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.ETNIA.toString()+".id"), 
				FatEspelhoEncerramentoPreviaVO.Fields.ETN_ID.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.COD_IBGE_CIDADE_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.COD_IBGE_CIDADE_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.END_CIDADE_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.END_CIDADE_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.END_UF_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.END_UF_PAC.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.END_CEP_PAC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.END_CEP_PAC.toString());
		listProj.add(Projections.property("CPE"+ PONTO + FatCompetencia.Fields.VERSAO_SISAIH.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.VERSAO_SISAIH.toString());
		
		criteria.setProjection(listProj);
	}
	
	private void setProjecaoContinuacaoSegundo(){
		
		listProj.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.DDD_FONE_RESIDENCIAL.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DDD_FONE_RESIDENCIAL.toString());
		listProj.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.FONE_RESIDENCIAL.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.FONE_RESIDENCIAL.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.IPH_COD_SUS_SOLIC.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.IPH_COD_SUS_SOLIC.toString());
		listProj.add(Projections.property(ALIAS_IPHS + PONTO + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DESCRICAO_IPHS.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.IPH_COD_SUS_REALIZ.toString());
		listProj.add(Projections.property(ALIAS_IPHR + PONTO + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DESCRICAO_IPHR.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.CID_PRIMARIO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CID_PRIMARIO.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.CID_SECUNDARIO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CID_SECUNDARIO.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.SAIDAS_OBITO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CAUSA_OBITO.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.TCI_COD_SUS.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.TCI_COD_SUS.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.DATA_INTERNACAO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DATA_INTERNACAO.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.DATA_SAIDA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DATA_SAIDA.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.MOTIVO_COBRANCA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.MOTIVO_COBRANCA.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.EXCL_CRITICA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.EXCLUSAO_CRITICA.toString());
		
		criteria.setProjection(listProj);
	}

	private void setProjecaoFinal(){
		
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.DAU_SENHA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DAU_SENHA.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NUMERO_AIH_ANTERIOR.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NUMERO_AIH_ANTERIOR.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NUMERO_AIH_POSTERIOR.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NUMERO_AIH_POSTERIOR.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.CTH_SEQ.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CTH_SEQ.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NASCIDOS_VIVOS.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NASCIDOS_VIVOS.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NASCIDOS_MORTOS.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NASCIDOS_MORTOS.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.SAIDAS_ALTA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.SAIDAS_ALTA.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.SAIDAS_OBITO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.SAIDAS_OBITO.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.SAIDAS_TRANSFERENCIA.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.SAIDAS_TRANSFERENCIA.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.NRO_SISPRENATAL.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.NRO_SISPRENATAL.toString());
		listProj.add(Projections.property(ALIAS_CTH+ PONTO + FatContasHospitalares.Fields.SEQ.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.CTH_SEQ_REAPRESENTADA.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.DCI_CPE_MES.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DCI_CPE_MES.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.DCI_CPE_ANO.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.DCI_CPE_ANO.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.IPH_PHO_SEQ_REALIZ.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.IPH_PHO_SEQ_REALIZ.toString());
		listProj.add(Projections.property(ALIAS_FE+ PONTO + FatEspelhoAih.Fields.IPH_SEQ_REALIZ.toString()), 
				FatEspelhoEncerramentoPreviaVO.Fields.IPH_SEQ_REALIZ.toString());

		criteria.setProjection(listProj);
		criteria.setResultTransformer(Transformers.aliasToBean(FatEspelhoEncerramentoPreviaVO.class));
	}
	
	private void setOrder(){
		
		criteria.addOrder(Order.asc(ALIAS_FE+PONTO+FatEspelhoAih.Fields.PAC_NOME.toString()));
		criteria.addOrder(Order.asc(ALIAS_FE+PONTO+FatEspelhoAih.Fields.CTH_SEQ.toString()));
		criteria.addOrder(Order.asc(ALIAS_FE+PONTO+FatEspelhoAih.Fields.SEQP.toString()));
	}
	
	public DetachedCriteria build(Integer cthSeq) {
		this.cthSeq = cthSeq;
		return super.build();
	}
}
