package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

/**
 * Classe implementada para auxiliar na migração para Java do seguinte cursor:
 * 
 * CURSOR C_BUSCA_NPT IS
 *	SELECT ICH.PHI_SEQ, TRUNC(DTHR_REALIZADO) DATA_RZDO, COUNT(*) TOTAL
 *	FROM FAT_PROCED_HOSP_INTERNOS PHI,
 *	 	FAT_ITENS_CONTA_HOSPITALAR ICH
 *	WHERE PHI.TIPO_NUTR_PARENTERAL IS NOT NULL
 *		AND PHI.SEQ = ICH.PHI_SEQ  
 *		AND ICH.IND_SITUACAO = 'A'  
 *		AND ICH.CTH_SEQ = P_CTH_SEQ
 *	GROUP BY ICH.PHI_SEQ, TRUNC(DTHR_REALIZADO)
 *	HAVING COUNT(*) > 1;
 * Este cursor é utilizado em FATP_CANC_NPT_DUPLA
 * 
 * OUTRO:
 * 	CURSOR c_max_item ( v_cth_seq IN NUMBER ) IS
 * 	SELECT phi_seq, MAX(dthr_realizado) data_limite
 * 	FROM fat_itens_conta_hospitalar
 * 	WHERE cth_seq = v_cth_seq
 *   	AND dthr_realizado > pDataNova
 *   	AND ind_situacao = 'A'
 *   	AND ind_origem <> 'MPM' -- ETB 22/10/07
 * 	GROUP BY phi_seq
 * 	ORDER BY 2 DESC;
 * Este cursor é utilizado em FATK_CTH_RN_UN.RN_CTHC_VER_DATAS
 * 
 */


public class FatItemContaHospitalarVO {
	
	private Integer phiSeq;
	private Date dthrRealizado;
	private Integer count;
	private Short quantidadeRealizada;
	
	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Date getDthrRealizado() {
		return dthrRealizado;
	}
	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public enum Fields {
		PHI_SEQ("phiSeq"),
		DTHR_REALIZADO("dthrRealizado"),
		COUNT("count"),
		QUANTIDADE_REALIZADA("quantidadeRealizada")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Short getQuantidadeRealizada() {
		return quantidadeRealizada;
	}
	public void setQuantidadeRealizada(Short quantidadeRealizada) {
		this.quantidadeRealizada = quantidadeRealizada;
	}
		
}
