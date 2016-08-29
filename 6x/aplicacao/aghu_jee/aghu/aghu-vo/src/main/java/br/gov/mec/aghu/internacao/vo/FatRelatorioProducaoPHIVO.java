package br.gov.mec.aghu.internacao.vo;

import java.math.BigInteger;
import java.util.Date;

/**
 * Classe implementada para auxiliar na migração para Java do seguinte cursor:
 * 
CURSOR c_caps IS
SELECT TO_CHAR(pac.prontuario)||';'||
       pac.nome||';'||
       TO_CHAR(pmr.phi_seq)||';'||
       phi.descricao||';'||
       TO_CHAR(dthr_realizado,'dd/mm/yyyy hh24:mi:ss')||';'||
       TO_CHAR(PAC.NRO_CARTAO_SAUDE)
FROM   aip_pacientes pac,
	 fat_proced_hosp_internos phi,
	 FAT_PROCED_AMB_REALIZADOS pmr,
       aac_consultas con,
       aac_retornos ret
WHERE  pac.codigo = pmr.pac_codigo
AND    phi.seq    = pmr.phi_seq
AND    INSTR(p_char_field50,TO_CHAR(pmr.phi_seq)) > 0 --(5663,5664,5665)
AND    dthr_realizado BETWEEN P_DATE_FIELD1
                      AND (TO_DATE(TO_CHAR(NVL(P_DATE_FIELD2,SYSDATE),'DDMMYYYY')||'2359','DDMMYYYYHH24MI'))
AND    con.numero = pmr.prh_con_numero
AND    ret.seq = con.ret_seq
AND    ret.ind_fatura_sus = 'S'
ORDER BY pac.prontuario,pmr.dthr_realizado;
 * 
 */


public class FatRelatorioProducaoPHIVO {
	
	private String nome;
	private String descricao;
	private Integer prontuario;
	private Integer phiSeq;
	private BigInteger nroCartaoSaude;
	private Date dthrRealizado;
	
	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(final Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(final Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}
	
	public enum Fields {
		NOME("nome"),
		DESCRICAO("descricao"),
		PRONTUARIO("prontuario"),
		PHI_SEQ("phiSeq"),
		NRO_CARTAO_SAUDE("nroCartaoSaude"),
		DTHR_REALIZADO("dthrRealizado"),
		;
		
		private final String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(final Integer prontuario) {
		this.prontuario = prontuario;
	}

	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}

	public void setNroCartaoSaude(final BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
		
}
