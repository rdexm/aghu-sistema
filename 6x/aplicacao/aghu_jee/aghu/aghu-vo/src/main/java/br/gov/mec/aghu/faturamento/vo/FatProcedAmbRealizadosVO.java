package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.core.commons.BaseBean;



public class FatProcedAmbRealizadosVO implements BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8372868478938588929L;
	private Long seq;
	private Long codTabela;
	private Integer phiSeqProced;
	private String descricaoProced;
	private Integer pacCodigo;
	private String pacNome;
	private Integer prontuario;
	private Date dthrRealizado;
	private Short quantidade;
	private Integer conNumero;
	private String descricaoConsulta;
	private Integer phiSeqConsulta;
	private Integer atdSeq;
	
	private String sigla;
	private String descricaoExame;
	private Integer seqMaterial;
	private Integer soeSeq;
	private String descricaoProcCir;
	private Integer seqCirurgia;
	private Short seqUF;
	private String descricaoUF;
	private DominioOrigemProcedimentoAmbulatorialRealizado indOrigem;
	private DominioSituacaoProcedimentoAmbulatorio situacao;
	
	private boolean indOts;
	private Short seqEspecialidade;
	
/*
 * #{_procedimentos.consultaProcedHospitalar.descricaoSugesttion}
 */
	
	public enum Fields {
		SEQ("seq"), 
		ATD_SEQ("atdSeq"),
		COD_TABELA("codTabela"), 
		PHI_SEQ_PROCED("phiSeqProced"),
		DESCRICAO_PROCED("descricaoProced"),
		PAC_CODIGO("pacCodigo"),
		PAC_NOME("pacNome"),
		PRONTUARIO("prontuario"),
		DT_HR_REALIZADO("dthrRealizado"),
		QUANTIDADE("quantidade"),
		CON_NUMERO("conNumero"),
		SIGLA("sigla"),
		SEQ_MATERIAL("seqMaterial"),		
		SOE_SEQ("soeSeq"),
		DESCRICAO_PROC_CIR("descricaoProcCir"),
		SEQ_CIRURGIA("seqCirurgia"),
		SEQ_UF("seqUF"),
		DESCRICAO_UF("descricaoUF"),
		IND_ORIGEM("indOrigem"),
		SITUACAO("situacao"),
		DESCRICAO_CONSULTA("descricaoConsulta"),
		PHI_SEQ_CONSULTA("phiSeqConsulta"),
		DESCRICAO_EXAME("descricaoExame"),
		SEQ_ESPECIALIDADE("seqEspecialidade")
		;
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public Long getSeq() {
		return seq;
	}


	public void setSeq(Long seq) {
		this.seq = seq;
	}


	public Long getCodTabela() {
		return codTabela;
	}


	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}


	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	public String getPacNome() {
		return pacNome;
	}


	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public Date getDthrRealizado() {
		return dthrRealizado;
	}


	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}


	public Short getQuantidade() {
		return quantidade;
	}


	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}


	public Integer getConNumero() {
		return conNumero;
	}


	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}


	public String getSigla() {
		return sigla;
	}


	public void setSigla(String sigla) {
		this.sigla = sigla;
	}


	public Integer getSeqMaterial() {
		return seqMaterial;
	}


	public void setSeqMaterial(Integer seqMaterial) {
		this.seqMaterial = seqMaterial;
	}


	public Integer getSoeSeq() {
		return soeSeq;
	}


	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}


	public String getDescricaoProcCir() {
		return descricaoProcCir;
	}


	public void setDescricaoProcCir(String descricaoProcCir) {
		this.descricaoProcCir = descricaoProcCir;
	}


	public Integer getSeqCirurgia() {
		return seqCirurgia;
	}


	public void setSeqCirurgia(Integer seqCirurgia) {
		this.seqCirurgia = seqCirurgia;
	}


	public Short getSeqUF() {
		return seqUF;
	}


	public void setSeqUF(Short seqUF) {
		this.seqUF = seqUF;
	}


	public String getDescricaoUF() {
		return descricaoUF;
	}


	public void setDescricaoUF(String descricaoUF) {
		this.descricaoUF = descricaoUF;
	}


	public DominioOrigemProcedimentoAmbulatorialRealizado getIndOrigem() {
		return indOrigem;
	}


	public void setIndOrigem(
			DominioOrigemProcedimentoAmbulatorialRealizado indOrigem) {
		this.indOrigem = indOrigem;
	}


	public DominioSituacaoProcedimentoAmbulatorio getSituacao() {
		return situacao;
	}


	public void setSituacao(DominioSituacaoProcedimentoAmbulatorio situacao) {
		this.situacao = situacao;
	}


	public String getDescricaoProced() {
		return descricaoProced;
	}


	public void setDescricaoProced(String descricaoProced) {
		this.descricaoProced = descricaoProced;
	}


	public Integer getPhiSeqProced() {
		return phiSeqProced;
	}


	public void setPhiSeqProced(Integer phiSeqProced) {
		this.phiSeqProced = phiSeqProced;
	}


	public String getDescricaoConsulta() {
		return descricaoConsulta;
	}


	public void setDescricaoConsulta(String descricaoConsulta) {
		this.descricaoConsulta = descricaoConsulta;
	}


	public Integer getPhiSeqConsulta() {
		return phiSeqConsulta;
	}


	public void setPhiSeqConsulta(Integer phiSeqConsulta) {
		this.phiSeqConsulta = phiSeqConsulta;
	}


	public String getDescricaoExame() {
		return descricaoExame;
	}


	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}


	public boolean isIndOts() {
		return indOts;
	}


	public void setIndOts(boolean ots) {
		this.indOts = ots;
	}


	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}


	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}	
}
