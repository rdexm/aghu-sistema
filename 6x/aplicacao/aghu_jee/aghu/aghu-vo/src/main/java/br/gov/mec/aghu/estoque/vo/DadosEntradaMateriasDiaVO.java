package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import br.gov.mec.aghu.dominio.DominioSimNao;

/**
 * Utilizado para validações de um item de af é válido para programação de entrega
 * 
 * @author ueslei.rosado
 * 
 */
public class DadosEntradaMateriasDiaVO implements Serializable {
	private static final long serialVersionUID = 3758522936860979868L;
	
	private String sigla;
	private Integer grupo;
	private String descricaoGrupo;
	private String fornecedor;
	private Integer documento;
	private Integer codigo;
	private String nomeMaterial;
	private Boolean estoc;
	private Boolean indEstorno;
	private String unid;
	private Integer qtSolic;
	private Integer qtReceb;
	private BigDecimal valor;
	

	public DadosEntradaMateriasDiaVO() {

	}
	
	public DadosEntradaMateriasDiaVO(String sigla, Integer grupo, String descricaoGrupo, 
								String fornecedor, Integer documento, Integer codigo, 
								String nomeMaterial, Boolean estoc, String unid, Integer qtSolic,
								Integer qtReceb, BigDecimal valor) {
		
		this.sigla = sigla;
		this.grupo = grupo;
		this.descricaoGrupo = descricaoGrupo;
		this.fornecedor = fornecedor;
		this.documento = documento;
		this.codigo = codigo;
		this.nomeMaterial = nomeMaterial;
		this.estoc = estoc;
		this.unid = unid;
		this.qtSolic = qtSolic;
		this.qtReceb = qtReceb;
		this.valor = valor;
	}
	
	public enum Fields{
		SIGLA("sigla"),
		GRUPO("grupo"),
		DESCRICAO_GRUPO("descricaoGrupo"),
		FORNECEDOR("fornecedor"),		
		DOCUMENTO("Documento"),
		CODIGO("Codigo"),
		NOME_MATERIAL("nomeMaterial"),
		ESTOCAVEL("estoc"),
		IND_ESTORNO("indEstorno"),
		UNIDADE("unid"),
		QT_SOLICITACAO("qtSolic"),
		QT_RECEBIMENTO("qtReceb"), 
		VALOR("valor");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
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

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getDocumento() {
		return documento;
	}

	public void setDocumento(Integer documento) {
		this.documento = documento;
	}	

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Boolean getEstoc() {
		return estoc;
	}

	public void setEstoc(Boolean estoc) {
		this.estoc = estoc;
	}
	
	public String getEstocFormatado() {
		
		if (estoc){
			return DominioSimNao.S.toString();
		}else{
			return DominioSimNao.N.toString();
		}	
	}
	
	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public Integer getQtSolic() {
		return qtSolic;
	}

	public void setQtSolic(Integer qtSolic) {
		this.qtSolic = qtSolic;
	}

	public Integer getQtReceb() {
		return qtReceb == null ? 0 : qtReceb;
	}

	public void setQtReceb(Integer qtReceb) {
		this.qtReceb = qtReceb;
	}

	public BigDecimal getCustoUnit() {
		if (getQtReceb() != 0){
			BigDecimal valorFinal = valor.divide(BigDecimal.valueOf(getQtReceb().longValue()),2, RoundingMode.HALF_UP);
			return valorFinal;			
		}else{
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValorFormatado() {
		return indEstorno ? getValor().negate() : getValor();
	}
	public Boolean getIndEstorno() {
		return indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}
}
