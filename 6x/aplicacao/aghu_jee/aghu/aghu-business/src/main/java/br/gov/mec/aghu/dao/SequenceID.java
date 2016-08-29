package br.gov.mec.aghu.dao;

import br.gov.mec.aghu.core.dao.Sequence;


/**
 * Enumeracao com o id de todas as sequences usadas no AGHU
 */
public enum SequenceID implements Sequence {
	
	MPM_SCN_SQ1("agh.mpm_scn_sq1"),// Sequence utilizada em MPM_SOLICITACAO_CONSULTORIAS
	AGH_GCD_SQ1("agh.agh_gcd_sq1"),// Sequence utilizada em AGH_GRUPO_CIDS
	FAT_IPH_SQ1("agh.fat_iph_sq1"),// Sequence utilizada em FAT_ITENS_PROCED_HOSPITALAR
	AIN_INT_SQ1("agh.ain_int_sq1"),// Sequence utilizada em AIN_INTERNACOES
	AIN_AOC_SQ1("agh.ain_aoc_sq1"),// Sequence utilizada em AIN_OBSERVACOES_CENSO
	RAP_PTI_SQ1("agh.rap_pti_sq1"), // Sequence utilizada em RAP_PESSOA_TIPO_INFORMACOES
	MPM_MBD_SQ1("agh.mpm_mbd_sq1"),// Sequence utilizada em MPM_MOD_BASIC_DIETAS
	MPM_PCU_SQ1("agh.mpm_pcu_sq1"),// Sequence utilizada em MPM_PRESCRICAO_CUIDADOS
	MPM_PME_SQ1("agh.mpm_pme_sq1"),// Sequence utilizada em MPM_PRESCRICAO_MEDICAS
	MPM_PDT_SQ1("agh.mpm_pdt_sq1"),// Sequence utilizada em MPM_PRESCRICAO_DIETAS
	MPM_PMD_SQ1("agh.mpm_pmd_sq1"),// Sequence utilizada em MPM_PRESCRICAO_MDTOS
	MPM_PPR_SQ1("agh.mpm_ppr_sq1"),// Sequence utilizada em MPM_PRESCRICAO_PROCEDIMENTOS
	MPM_PNP_SQ1("agh.mpm_pnp_sq1"),// Sequence utilizada em MPM_PRESCRICAO_NPTS
	ABS_SHE_SQ1("agh.abs_she_sq1"),// Sequence utilizada em ABS_SOLICITACOES_HEMOTERAPICAS
	MPT_PMO_SQ1("agh.mpt_pmo_sq1"), // Sequence utilizada em MPT_PRESCRICAO_MEDICAMENTO
	MPT_PCO_SQ1("agh.mpt_pco_sq1"),// Sequence utilizada em MPT_PRESCRICAO_CUIDADOS
	MPM_MCU_SQ1("agh.mpm_mcu_sq1"),// Sequence utilizada em MPM_MOD_BASIC_CUIDADOS
	MPM_MBP_SQ1("agh.mpm_mbp_sq1"),// Sequence utilizada em MPM_MOD_BASIC_PROCEDIMENTOS
	AGH_APA_SQ1("agh.agh_apa_sq1"), // Sequence utilizada em AGH_ATENDIMENTO_PACIENTES
	MPM_MBM_SQ1("agh.mpm_mbm_sq1"),// Sequence utilizada em MPM_MOD_BASIC_MDTOS
	SCE_MMT_SQ1("agh.sce_mmt_sq1"),// Sequence utilizada em SCE_MOVIMENTO_MATERIAIS
	EPE_PRC_SQ1("agh.epe_prc_sq1"),// Sequence utilizada em EPE_PRESCRICOES_CUIDADOS
	SIG_MSL_SQ1("agh.sig_msl_sq1"),// Sequence utilizada em SIG_MVTO_CONTA_MENSAIS
	AEL_EXA_JN_SEQ("agh.ael_exa_jn_seq"),// Sequence utilizada em AEL_EXAMES_JN
	EPE_CUI_SQ1("agh.epe_cui_sq1"),// Sequence utilizada em EPE_CUIDADOS
	MBC_DPA_SQ1("agh.mbc_dpa_sq1"),// Sequence utilizada em MBC_DESTINO_PACIENTES
	MAM_PLE_SQ1("agh.mam_ple_sq1"),
	SCE_BOC_SQ1("agh.sce_boc_sq1"),
	AFA_PMM_SQ2("agh.afa_pmm_sq2"),//Utiliza em afa_prescricao_medicamentos, gera numero_externo
	MBC_AAO_SEQ_NAME("agh.mbc_aao_seq"),
	EPE_PEN_SQ1("agh.epe_pen_sq1"), // Sequence utilizada em EPE_PRESCRICOES_ENFERMAGEM
	SCO_ETP_SQ1("agh.sco_etp_sq1"), // Utilizada em SCO_ETAPA_PAC
	SCO_SEQ_COTACAO("agh.sco_seq_cotacao"), //Utilizado para gerar numero de cotacao
	AIP_ALP_SQ1("agh.aip_alp_sq1"), // Sequence utilizada em AIP_ALERGIA_PACIENTE
	MAM_RGT_SQ1("agh.mam_rgt_sq1"), 
	FAT_ICH_SQ1("agh.fat_ich_sq1"), // Sequence utilizada em FAT_ITENS_CONTA_HOSPITALAR
	PTM_AVT_SQ1("agh.ptm_avt_sq1");
	
	public String nameSequence;
	
	SequenceID(String nameSequence){
		this.nameSequence = nameSequence;
	}
	
	public String getSequenceName() {
		switch (this) {
		default:
			return this.nameSequence;
		}
	}
}