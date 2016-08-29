package br.gov.mec.aghu.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe utilizado para recuperar dados da Base.<br>
 * Utilizada para verificacoes apenas.<br>
 *  
 * @author rcorvalao
 *
 */

public class AghAtendimentosVO implements BaseBean{
	
	private static final long serialVersionUID = 7687250289019476784L;
	
	private Integer prontuario;
	private String nomePaciente;
	private String nomeMaePaciente;
	private Date dtNascimentoPaciente;
	
	private Boolean controleAtd;
	private BigDecimal valorTotalCusto;
	private BigDecimal valorTotalReceita;
	private BigDecimal valorUltimaFatura;	private Integer seq;
	private String descricao;
	private Integer codigo;
	private Short qrtNumero;
	private Byte andar;


	private Date dtHrInicio;
	private Date dtHrFim;
	private AghAla aghAla;
	private String leitoId;
	
	public AghAtendimentosVO(){}
	
	public enum Fields {
		
		PRONTUARIO("prontuario"),
		SEQ("seq"),
		CODIGO("codigo"),
		NOME_PACIENTE("nomePaciente"),
		NOME_PACIENTE_MAE("nomeMaePaciente"),
		DT_NASCIMENTO_PACIENTE("dtNascimentoPaciente"),
		LTO_LTO_ID("leitoId"),
		QRT_NUMERO("qrtNumero"),
		AGH_UNIDADES_FUNCIONAIS_ANDAR("andar"),
		AGH_UNIDADES_FUNCIONAIS_IND_ALA("aghAla"),
		AGH_UNIDADES_FUNCIONAIS_DESCRICAO("descricao"),
		DTHR_INICIO("dtHrInicio"),
		DTHR_FIM("dtHrFim");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setDtNascimentoPaciente(Date dtNascimentoPaciente) {
		this.dtNascimentoPaciente = dtNascimentoPaciente;
	}


	public Date getDtNascimentoPaciente() {
		return dtNascimentoPaciente;
	}


	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}


	public String getNomePaciente() {
		return nomePaciente;
	}


	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}


	public String getNomeMaePaciente() {
		return nomeMaePaciente;
	}


	public void setControleAtd(Boolean controleAtd) {
		this.controleAtd = controleAtd;
	}


	public Boolean getControleAtd() {
		return controleAtd;
	}


	public BigDecimal getValorTotalCusto() {
		return valorTotalCusto;
	}


	public void setValorTotalCusto(BigDecimal valorTotalCusto) {
		this.valorTotalCusto = valorTotalCusto;
	}


	public BigDecimal getValorTotalReceita() {
		return valorTotalReceita;
	}


	public void setValorTotalReceita(BigDecimal valorTotalReceita) {
		this.valorTotalReceita = valorTotalReceita;
	}


	public BigDecimal getValorUltimaFatura() {
		return valorUltimaFatura;
	}


	public void setValorUltimaFatura(BigDecimal valorUltimaFatura) {
		this.valorUltimaFatura = valorUltimaFatura;
	}
	public Integer getSeq() {
		return seq;
	}




	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Integer getCodigo() {
		return codigo;
	}


	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}


	public Short getQrtNumero() {
		return qrtNumero;
	}


	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}


	public Byte getAndar() {
		return andar;
	}


	public void setAndar(Byte andar) {
		this.andar = andar;
	}


	public Date getDtHrInicio() {
		return dtHrInicio;
	}


	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}


	public Date getDtHrFim() {
		return dtHrFim;
	}


	public void setDtHrFim(Date dtHrFim) {
		this.dtHrFim = dtHrFim;
	}


	public AghAla getAghAla() {
		return aghAla;
	}


	public void setAghAla(AghAla aghAla) {
		this.aghAla = aghAla;
	}


	public String getLeitoId() {
		return leitoId;
	}


	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}
}
