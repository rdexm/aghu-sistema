package br.gov.mec.aghu.blococirurgico.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

class ConsultarInfoProcCirgReqOPMEsQueryBuilder extends QueryBuilder<DetachedCriteria> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2199327345185372556L;
	
	private static final String ALIAS_ROP = "ROP";
	private static final String ALIAS_AGD = "AGD";
	private static final String ALIAS_CRG = "CRG";
	private static final String ALIAS_ESP = "ESP";
	private static final String ALIAS_EPR = "EPR";
	private static final String ALIAS_IPH = "IPH";
	private static final String ALIAS_UNF = "UNF";
	private static final String ALIAS_PAC = "PAC";
	private static final String ALIAS_CSP = "CSP";
	private static final String ALIAS_SER1 = "SER1";
	private static final String ALIAS_SER2 = "SER2";
	private static final String ALIAS_PCI = "PCI";
	private static final String ALIAS_CNV = "CNV";
	private static final String ALIAS_PES1 = "PES1";
	private static final String ALIAS_PES2 = "PES2";
	
	private static final String PONTO = ".";

	private DetachedCriteria criteria;
	private Short requisicaoSelecionada;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(MbcRequisicaoOpmes.class, ALIAS_ROP);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
		setResultTransformer();
	}

	private void setJoin() {
		criteria.createAlias(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.RAP_SERVIDORES.toString(), ALIAS_SER1);
		criteria.createAlias(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.AGENDA.toString(), ALIAS_AGD);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.CIRURGIAS.toString(), ALIAS_CRG, Criteria.LEFT_JOIN);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.ESPECIALIDADE.toString(), ALIAS_ESP);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.ESP_PROC_CIRGS.toString(), ALIAS_EPR);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.ITENS_PROCED_HOSPITALAR.toString(), ALIAS_IPH);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.UNF.toString(), ALIAS_UNF);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.PACIENTE.toString(), ALIAS_PAC);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO.toString(), ALIAS_CSP);
		criteria.createAlias(ALIAS_AGD + PONTO + MbcAgendas.Fields.PUC_SERVIDOR.toString(), ALIAS_SER2);
		criteria.createAlias(ALIAS_EPR + PONTO + MbcEspecialidadeProcCirgs.Fields.PROCEDIMENTO_CIRURGICOS.toString(), ALIAS_PCI);
		criteria.createAlias(ALIAS_CSP + PONTO + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), ALIAS_CNV);
		criteria.createAlias(ALIAS_SER1 + PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES1);
		criteria.createAlias(ALIAS_SER2 + PONTO + RapServidores.Fields.PESSOA_FISICA.toString(), ALIAS_PES2);
	}

	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.ID.toString(), requisicaoSelecionada));
	}

	private void setProjecao() {
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.CRIADO_EM.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.DATA_REQUISICAO.toString())
				.add(Projections.property(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.SER_MATRICULA_CRIACAO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.REQUERENTE.toString())
				.add(Projections.property(ALIAS_PES1 + PONTO + RapPessoasFisicas.Fields.NOME.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.NOME_REQUERENTE.toString())
				.add(Projections.property(ALIAS_AGD + PONTO + MbcAgendas.Fields.ESPECIALIDADE_SEQ.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.ESPECIALIDADE.toString())
				.add(Projections.property(ALIAS_ESP + PONTO + AghEspecialidades.Fields.CENTRO_CUSTO_CODIGO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.ESP_CENTRO_CUSTO.toString())
				.add(Projections.property(ALIAS_ESP + PONTO + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.NOME_ESPECIALIDADE.toString())
				.add(Projections.property(ALIAS_PCI + PONTO + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.PROCEDIMENTO_PRINCIPAL.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.PROCEDIMENTO_SUS.toString())
				.add(Projections.property(ALIAS_IPH + PONTO + FatItensProcedHospitalar.Fields.SEQ.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.COD_TABELA.toString())
				.add(Projections.property(ALIAS_AGD + PONTO + MbcAgendas.Fields.DT_AGENDA.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.DATA_PROCEDIMENTO.toString())
				.add(Projections.property(ALIAS_CRG + PONTO + MbcCirurgias.Fields.NUMERO_AGENDA.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.AGENDA.toString())
				.add(Projections.property(ALIAS_AGD + PONTO + MbcAgendas.Fields.UNF_SEQ.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.UNIDADE.toString())
				.add(Projections.property(ALIAS_AGD + PONTO + MbcAgendas.Fields.PUC_SER_MATRICULA.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.EQUIPE.toString())
				.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.NOME.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property(ALIAS_PAC + PONTO + AipPacientes.Fields.PRONTUARIO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(ALIAS_AGD + PONTO + MbcAgendas.Fields.FAT_CONVENIO_SAUDE_CODIGO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.PLANO_CONVENIO.toString())
				.add(Projections.property(ALIAS_CNV + PONTO + FatConvenioSaude.Fields.DESCRICAO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.CONVENIO_DESCRICAO.toString())
				.add(Projections.property(ALIAS_AGD + PONTO + MbcAgendas.Fields.CONVENIO_SAUDE_PLANO_CSP_SEQ.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.CONVENIO_SAUDE_PLANO_CSP_SEQ.toString())
				.add(Projections.property(ALIAS_CSP + PONTO + FatConvenioSaudePlano.Fields.DESCRICAO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.CONV_SAUDE_PLANO_DESC.toString())
				.add(Projections.property(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.OBSERVACAO_OPME.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.OBSERVACOES_GERAIS.toString())
				.add(Projections.property(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.JUST_REQUISICAO_OPME.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.JUST_REQUISICAO_OPME.toString())
				.add(Projections.property(ALIAS_ROP + PONTO + MbcRequisicaoOpmes.Fields.IND_AUTORIZADO.toString()), InfoProcdCirgRequisicaoOPMEVO.Fields.IND_AUTORIZADO.toString())
			);	
	}

	public void setResultTransformer() {
		criteria.setResultTransformer(Transformers.aliasToBean(InfoProcdCirgRequisicaoOPMEVO.class));
	}
	
	public DetachedCriteria build(final Short requisicaoSelecionada) {
		this.requisicaoSelecionada = requisicaoSelecionada;
		return super.build();
	}
	
}
