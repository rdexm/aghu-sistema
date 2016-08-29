package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
 

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class NotasRecebimentoGeradasMesVO implements BaseBean {

	//VO
	private String tipo;
	
	private Integer nr;
	private Date geracao;
	private Long nf;
	private Date emissaoAF;
	private Integer af;
	private Short complemento;
	private Long cnpj;
	private Long cpf;
	private String fornecedor;
	private Integer grupoNaturezaAF;
	private Byte naturezaAF;
	private Integer grupoMaterialServico;
	private BigDecimal grupoNaturezaSIAFI;
	private Byte naturezaSIAFI;
	private Double valor;
	private Integer tributada;
	
	//Filtro
	private Date mesAnoFiltro;
	private DominioSimNao tribFiltro;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4478976221970791104L;

	public enum Fields {
		
		TIPO("tipo"),
		NR("nr"),
		GERACAO("geracao"),
		NF("nf"),
		EMISSAO_AF("emissaoAF"),
		AF("af"),
		COMPLEMENTO("complemento"),
		CNPJ("cnpj"),
		CPF("cpf"),
		FORNECEDOR("fornecedor"),
		GRUPO_NATUREZA_AF("grupoNaturezaAF"),
		NATUREZA_AF("naturezaAF"),
		GRUPO_MATERIAL_SERVICO("grupoMaterialServico"),
		GRUPO_NATUREZA_SIAFI("grupoNaturezaSIAFI"),
		NATUREZA_SIAFI("naturezaSIAFI"),
		VALOR("valor"),
		TRIBUTADA("tributada");
	
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	//Gets e Sets VO
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getNr() {
		return nr;
	}

	public void setNr(Integer nr) {
		this.nr = nr;
	}

	public Date getGeracao() {
		return geracao;
	}

	public void setGeracao(Date geracao) {
		this.geracao = geracao;
	}

	public Long getNf() {
		return nf;
	}

	public void setNf(Long nf) {
		this.nf = nf;
	}

	public Date getEmissaoAF() {
		return emissaoAF;
	}

	public void setEmissaoAF(Date emissaoAF) {
		this.emissaoAF = emissaoAF;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getGrupoNaturezaAF() {
		return grupoNaturezaAF;
	}

	public void setGrupoNaturezaAF(Integer grupoNaturezaAF) {
		this.grupoNaturezaAF = grupoNaturezaAF;
	}

	public Byte getNaturezaAF() {
		return naturezaAF;
	}

	public void setNaturezaAF(Byte naturezaAF) {
		this.naturezaAF = naturezaAF;
	}

	public Integer getGrupoMaterialServico() {
		return grupoMaterialServico;
	}

	public void setGrupoMaterialServico(Integer grupoMaterialServico) {
		this.grupoMaterialServico = grupoMaterialServico;
	}

	public BigDecimal getGrupoNaturezaSIAFI() {
		return grupoNaturezaSIAFI;
	}

	public void setGrupoNaturezaSIAFI(BigDecimal grupoNaturezaSIAFI) {
		this.grupoNaturezaSIAFI = grupoNaturezaSIAFI;
	}

	public Byte getNaturezaSIAFI() {
		return naturezaSIAFI;
	}

	public void setNaturezaSIAFI(Byte naturezaSIAFI) {
		this.naturezaSIAFI = naturezaSIAFI;
	}
	
	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getTributada() {
		return tributada;
	}

	public void setTributada(Integer tributada) {
		this.tributada = tributada;
	}

	//Gets e Sets Filtro
	public Date getMesAnoFiltro() {
		return mesAnoFiltro;
	}
	
	public void setMesAnoFiltro(Date mesAnoFiltro) {
		this.mesAnoFiltro = mesAnoFiltro;
	}
	
	public DominioSimNao getTribFiltro() {
		return tribFiltro;
	}
	
	public void setTribFiltro(DominioSimNao tribFiltro) {
		this.tribFiltro = tribFiltro;
	}
	
	public String getValorFormatado() {
        String retorno = "";
        if (this.getValor() != null) {
              DecimalFormat df = new DecimalFormat("0.00");
       
              retorno = df.format(this.getValor());
        }
 
        return retorno;
	}
	
	public Long getCpfCnpjFormatado() {
		if (this.cnpj == null) {
			if (this.cpf == null) {
				return null;
			}
			return this.cpf;
		}
		return this.cnpj;
	}
	
	public String getAFComplemento() {
        return this.af.toString() + "/" + this.complemento.toString();
	}
	
	public String getTributadaFormatada() {
		if (this.tributada != null) {
			
			if(this.tributada > 0){
				return "Sim";
			}
		}
		return "Não";
	}
	
}
