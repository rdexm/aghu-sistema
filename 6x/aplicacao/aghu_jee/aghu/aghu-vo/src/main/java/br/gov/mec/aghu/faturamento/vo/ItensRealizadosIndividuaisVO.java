package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.core.utils.StringUtil;

public class ItensRealizadosIndividuaisVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1880649885559402644L;
	
	private Integer mes;
	private Integer ano;
	private Integer prontuario;
	private Integer codigo;
	private Long apac;
	private String nome;
	private String dthrRealz;
	private Short grupoSeq;
	private String grupo;
	private Byte subGrupoSeq;
	private String subGrupo;
	private Byte formaOrganizacaoCodigo;
	private String formaOrganizacao;
	private Long procedimentoHospitalarCod;
	private String procedimentoHospitalarDesc;
	private BigDecimal valorProcedimento;
	private Long quantidade;
	private String procedencia;
	private String cidade;
	private String estado;
	private String cidadeLgd;
	private String estadoLgd;
	
	public Long getApac() {
		return apac;
	}

	public void setApac(Long apac) {
		this.apac = apac;
	}

	public Integer getAno() {
		return ano;
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public String getProntuarioFormat() {
		return (prontuario!=null)?prontuario.toString():null;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDthrRealz() {
		return dthrRealz;
	}

	public void setDthrRealz(String dthrRealz) {
		this.dthrRealz = dthrRealz;
	}

	public Short getGrupoSeq() {
		return grupoSeq;
	}

	public void setGrupoSeq(Short grupoSeq) {
		this.grupoSeq = grupoSeq;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public Byte getSubGrupoSeq() {
		return subGrupoSeq;
	}

	public void setSubGrupoSeq(Byte subGrupoSeq) {
		this.subGrupoSeq = subGrupoSeq;
	}

	public String getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(String subGrupo) {
		this.subGrupo = subGrupo;
	}

	public Byte getFormaOrganizacaoCodigo() {
		return formaOrganizacaoCodigo;
	}

	public void setFormaOrganizacaoCodigo(Byte formaOrganizacaoCodigo) {
		this.formaOrganizacaoCodigo = formaOrganizacaoCodigo;
	}

	public String getFormaOrganizacao() {
		return formaOrganizacao;
	}

	public void setFormaOrganizacao(String formaOrganizacao) {
		this.formaOrganizacao = formaOrganizacao;
	}

	public Long getProcedimentoHospitalarCod() {
		return procedimentoHospitalarCod;
	}

	public void setProcedimentoHospitalarCod(Long procedimentoHospitalarCod) {
		this.procedimentoHospitalarCod = procedimentoHospitalarCod;
	}

	public String getProcedimentoHospitalarDesc() {
		return procedimentoHospitalarDesc;
	}

	public void setProcedimentoHospitalarDesc(String procedimentoHospitalarDesc) {
		this.procedimentoHospitalarDesc = procedimentoHospitalarDesc;
	}

	public BigDecimal getValorProcedimento() {
		return valorProcedimento;
	}

	public void setValorProcedimento(BigDecimal valorProcedimento) {
		this.valorProcedimento = valorProcedimento;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Object quantidade) {
		if(quantidade instanceof Short) {
			this.quantidade = ((Short)quantidade).longValue();
		}
		else {
			this.quantidade = (Long)quantidade;
		}
	}

	public String getProcedencia() {
		if(cidade != null) {
			if(estado != null) {
				procedencia = StringUtil.trunc(cidade, false, 22l) + "/" + estado;				
			}
			else {
				procedencia = StringUtil.trunc(cidade, false, 22l);				
			}
		}

		if(procedencia == null && cidadeLgd != null) {
			if(estadoLgd != null) {
				procedencia = StringUtil.trunc(cidadeLgd, false, 22l) + "/" + estadoLgd;				
			}
			else {
				procedencia = StringUtil.trunc(cidadeLgd, false, 22l);				
			}
		}

		return procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
