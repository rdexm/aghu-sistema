package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Utilizado para validações de um item de af é válido para programação de entrega
 * 
 * @author ueslei.rosado
 * 
 */
public class GrupoEntradaMateriasDiaVO implements Serializable {
	private static final long serialVersionUID = 3758522936860979868L;
	
	private BigDecimal oradbGmtEntrMatEstDia;
	private BigDecimal oradbGmtEntrMatDirDia;
	private BigDecimal oradbGmtEntrAcumMatEstMes;
	private BigDecimal oradbGmtConsAcumMatMes;
	private BigDecimal oradbCfEntrAcumMatMes;
	private BigDecimal oradbGmtConsMatEstDia;
	private BigDecimal oradbGmtConsMatDirDia;
	private BigDecimal oradbGmtConsAcumMatEstMes;
	private BigDecimal oradbGmtConsAcumMatDirMes;
	private BigDecimal oradbCfConsAcumMatMes;
	private Integer grupo;
	private String descricaoGrupo;


	public GrupoEntradaMateriasDiaVO() {

	}

	public GrupoEntradaMateriasDiaVO(BigDecimal oradbGmtEntrMatEstDia, BigDecimal oradbGmtEntrMatDirDia,
			BigDecimal oradbGmtEntrAcumMatEstMes, BigDecimal oradbGmtConsAcumMatMes, BigDecimal oradbCfEntrAcumMatMes,
			BigDecimal oradbGmtConsMatEstDia, BigDecimal oradbGmtConsMatDirDia, BigDecimal oradbGmtConsAcumMatEstMes,
			BigDecimal oradbGmtConsAcumMatDirMes, BigDecimal oradbCfConsAcumMatMes, Integer grupo, String descricaoGrupo) {
		
		this.oradbGmtEntrMatEstDia = oradbGmtEntrMatEstDia;
		this.oradbGmtEntrMatDirDia = oradbGmtEntrMatDirDia;
		this.oradbGmtEntrAcumMatEstMes = oradbGmtEntrAcumMatEstMes;
		this.oradbGmtConsAcumMatMes = oradbGmtConsAcumMatMes;
		this.oradbCfEntrAcumMatMes = oradbCfEntrAcumMatMes;
		this.oradbGmtConsMatEstDia = oradbGmtConsMatEstDia;
		this.oradbGmtConsMatDirDia = oradbGmtConsMatDirDia;
		this.oradbGmtConsAcumMatEstMes = oradbGmtConsAcumMatEstMes;
		this.oradbGmtConsAcumMatDirMes = oradbGmtConsAcumMatDirMes;
		this.oradbCfConsAcumMatMes = oradbCfConsAcumMatMes;		
		this.grupo = grupo;		
		this.descricaoGrupo = descricaoGrupo;		
	}

	public BigDecimal getOradbGmtEntrMatEstDia() {
		return oradbGmtEntrMatEstDia;
	}

	public void setOradbGmtEntrMatEstDia(BigDecimal oradbGmtEntrMatEstDia) {
		this.oradbGmtEntrMatEstDia = oradbGmtEntrMatEstDia;
	}

	public BigDecimal getOradbGmtEntrMatDirDia() {
		return oradbGmtEntrMatDirDia;
	}

	public void setOradbGmtEntrMatDirDia(BigDecimal oradbGmtEntrMatDirDia) {
		this.oradbGmtEntrMatDirDia = oradbGmtEntrMatDirDia;
	}

	public BigDecimal getOradbGmtEntrAcumMatEstMes() {
		return oradbGmtEntrAcumMatEstMes;
	}

	public void setOradbGmtEntrAcumMatEstMes(BigDecimal oradbGmtEntrAcumMatEstMes) {
		this.oradbGmtEntrAcumMatEstMes = oradbGmtEntrAcumMatEstMes;
	}

	public BigDecimal getOradbGmtConsAcumMatMes() {
		return oradbGmtConsAcumMatMes;
	}

	public void setOradbGmtConsAcumMatMes(BigDecimal oradbGmtConsAcumMatMes) {
		this.oradbGmtConsAcumMatMes = oradbGmtConsAcumMatMes;
	}

	public BigDecimal getOradbCfEntrAcumMatMes() {
		return oradbCfEntrAcumMatMes;
	}

	public void setOradbCfEntrAcumMatMes(BigDecimal oradbCfEntrAcumMatMes) {
		this.oradbCfEntrAcumMatMes = oradbCfEntrAcumMatMes;
	}

	public BigDecimal getOradbGmtConsMatEstDia() {
		return oradbGmtConsMatEstDia;
	}

	public void setOradbGmtConsMatEstDia(BigDecimal oradbGmtConsMatEstDia) {
		this.oradbGmtConsMatEstDia = oradbGmtConsMatEstDia;
	}

	public BigDecimal getOradbGmtConsMatDirDia() {
		return oradbGmtConsMatDirDia;
	}

	public void setOradbGmtConsMatDirDia(BigDecimal oradbGmtConsMatDirDia) {
		this.oradbGmtConsMatDirDia = oradbGmtConsMatDirDia;
	}

	public BigDecimal getOradbGmtConsAcumMatEstMes() {
		return oradbGmtConsAcumMatEstMes;
	}

	public void setOradbGmtConsAcumMatEstMes(BigDecimal oradbGmtConsAcumMatEstMes) {
		this.oradbGmtConsAcumMatEstMes = oradbGmtConsAcumMatEstMes;
	}

	public BigDecimal getOradbGmtConsAcumMatDirMes() {
		return oradbGmtConsAcumMatDirMes;
	}

	public void setOradbGmtConsAcumMatDirMes(BigDecimal oradbGmtConsAcumMatDirMes) {
		this.oradbGmtConsAcumMatDirMes = oradbGmtConsAcumMatDirMes;
	}

	public BigDecimal getOradbCfConsAcumMatMes() {
		return oradbCfConsAcumMatMes;
	}

	public void setOradbCfConsAcumMatMes(BigDecimal oradbCfConsAcumMatMes) {
		this.oradbCfConsAcumMatMes = oradbCfConsAcumMatMes;
	}

	public Integer getGrupo() {
		return grupo;
	}

	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}

	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}

	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}	
}
