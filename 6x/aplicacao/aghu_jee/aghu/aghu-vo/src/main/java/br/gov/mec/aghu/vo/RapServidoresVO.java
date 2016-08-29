package br.gov.mec.aghu.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.core.commons.BaseBean;


public class RapServidoresVO implements BaseBean {

	private static final long serialVersionUID = 5923384172482999554L;

	private Short vinculo;
	
	private String descricaoVinculo;
	
	private Integer matricula;
	
	private String nome;
	
	private Integer codigoPessoa;
	
	private Integer codigoCentroCustoAtuacao;

	private Integer codigoCentroCustoLotacao;
	
	private String descricaoCentroCustoAtuacao;

	private String descricaoCentroCustoLotacao;
	
	private String cbo;
	
	private String ocupacao;
	
	private DominioSituacaoVinculo indSituacao;
	
	private Date dtInicioVinculo;

	private Date dtFimVinculo;
	
	private String nroRegConselho;
	
	private String siglaConselho;
	
	private DominioSexo sexo;
	
	private String tituloMasculinoConselho;
	
	private String tituloFemininoConselho;
	
	private String nomeProfissional;
	
	private String tituloMasculinoVinculo;
	
	private String tituloFemininoVinculo;
	
	private Boolean indResponsavel;
	
	private Integer ramal;
	
	private Integer codStarh;
	
	private Integer ramalChefiaCCLotacao;
	
	private Integer ramalChefiaCCAtuacao;
	
	private Boolean situacaoFuncionario;
	
	private String carteiraUnimed;
	
	private String rpfNome;
	private Integer rpfCodigo;
	private String labelProfissional;
	
	/*Informação vinda da tabela MbcProfAtuaUnidCirgs*/
	private DominioFuncaoProfissional funcaoProfissional;
	
	public RapServidoresVO(Short vinculo, Integer matricula, String nome) {
		this.vinculo = vinculo;
		this.matricula = matricula;
		this.nome = nome;
	}
	
	public RapServidoresVO() {}
	
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getMatriculaVinculo() {
		return this.getVinculo() + " " + this.getMatricula();
	}

	
	public enum Fields {
		MATRICULA("matricula"), 
		VINCULO("vinculo"), 
		DESCRICAO_VINCULO("descricaoVinculo"), 
		NOME("nome"), 
		CBO("cbo"),
		OCUPACAO("ocupacao"),
		CODIGO_PESSOA("codigoPessoa"),
		CODIGO_CENTRO_CUSTO_LOT("codigoCentroCustoLotacao"),
		CODIGO_CENTRO_CUSTO_ATU("codigoCentroCustoAtuacao"),
		DESCRICAO_CENTRO_CUSTO_LOT("descricaoCentroCustoLotacao"),
		DESCRICAO_CENTRO_CUSTO_ATU("descricaoCentroCustoAtuacao"),
		IND_SITUACAO("indSituacao"),
		DATA_INICIO_VINCULO("dtInicioVinculo"),
		DATA_FIM_VINCULO("dtFimVinculo"),
		RAMAL("ramal"),
		COD_STARH("codStarh"),
		NUMERO_REG_CONSELHO("nroRegConselho"),
		SIGLA_CONSELHO("siglaConselho"),
		RPF_NOME("rpfNome"),
		RPF_CODIGO("rpfCodigo"),
		NOME_PROFISSIONAL("nomeProfissional");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result + ((vinculo == null) ? 0 : vinculo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RapServidoresVO other = (RapServidoresVO) obj;
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (vinculo == null) {
			if (other.vinculo != null) {
				return false;
			}
		} else if (!vinculo.equals(other.vinculo)) {
			return false;
		}
		return true;
	}

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	public String getOcupacao() {
		return ocupacao;
	}

	public void setOcupacao(String ocupacao) {
		this.ocupacao = ocupacao;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public Integer getCodigoCentroCustoAtuacao() {
		return codigoCentroCustoAtuacao;
	}

	public void setCodigoCentroCustoAtuacao(Integer codigoCentroCustoAtuacao) {
		this.codigoCentroCustoAtuacao = codigoCentroCustoAtuacao;
	}

	public Integer getCodigoCentroCustoLotacao() {
		return codigoCentroCustoLotacao;
	}

	public void setCodigoCentroCustoLotacao(Integer codigoCentroCustoLotacao) {
		this.codigoCentroCustoLotacao = codigoCentroCustoLotacao;
	}

	public String getDescricaoCentroCustoAtuacao() {
		return descricaoCentroCustoAtuacao;
	}

	public void setDescricaoCentroCustoAtuacao(String descricaoCentroCustoAtuacao) {
		this.descricaoCentroCustoAtuacao = descricaoCentroCustoAtuacao;
	}

	public String getDescricaoCentroCustoLotacao() {
		return descricaoCentroCustoLotacao;
	}

	public void setDescricaoCentroCustoLotacao(String descricaoCentroCustoLotacao) {
		this.descricaoCentroCustoLotacao = descricaoCentroCustoLotacao;
	}

	public DominioSituacaoVinculo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoVinculo indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setDtInicioVinculo(Date dtInicioVinculo) {
		this.dtInicioVinculo = dtInicioVinculo;
	}
	
	public Date getDtInicioVinculo() {
		return dtInicioVinculo;
	}
	
	public Date getDtFimVinculo() {
		return dtFimVinculo;
	}

	public void setDtFimVinculo(Date dtFimVinculo) {
		this.dtFimVinculo = dtFimVinculo;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public String getTituloMasculinoConselho() {
		return tituloMasculinoConselho;
	}

	public void setTituloMasculinoConselho(String tituloMasculinoConselho) {
		this.tituloMasculinoConselho = tituloMasculinoConselho;
	}

	public String getTituloFemininoConselho() {
		return tituloFemininoConselho;
	}

	public void setTituloFemininoConselho(String tituloFemininoConselho) {
		this.tituloFemininoConselho = tituloFemininoConselho;
	}

	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}

	public String getNomeProfissional() {
		return nomeProfissional;
	}

	public String getTituloMasculinoVinculo() {
		return tituloMasculinoVinculo;
	}

	public void setTituloMasculinoVinculo(String tituloMasculinoVinculo) {
		this.tituloMasculinoVinculo = tituloMasculinoVinculo;
	}

	public String getTituloFemininoVinculo() {
		return tituloFemininoVinculo;
	}

	public void setTituloFemininoVinculo(String tituloFemininoVinculo) {
		this.tituloFemininoVinculo = tituloFemininoVinculo;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public String getSiglaConselho() {
		return siglaConselho;
	}

	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}

	public Boolean getIndResponsavel() {
		return indResponsavel;
	}

	public void setIndResponsavel(Boolean indResponsavel) {
		this.indResponsavel = indResponsavel;
	}

	public String getDescricaoVinculo() {
		return descricaoVinculo;
	}

	public void setDescricaoVinculo(String descricaoVinculo) {
		this.descricaoVinculo = descricaoVinculo;
	}

	public Integer getCodStarh() {
		return codStarh;
	}

	public void setCodStarh(Integer codStarh) {
		this.codStarh = codStarh;
	}

	@Override
	public String toString() {
		return "RapServidoresVO [vinculo=" + vinculo + ", matricula="
				+ matricula + ", nome=" + nome + ", codigoPessoa="
				+ codigoPessoa + ", cbo=" + cbo + ", ocupacao=" + ocupacao
				+ ", indSituacao=" + indSituacao + ", dtFimVinculo="
				+ dtFimVinculo + ", nroRegConselho=" + nroRegConselho
				+ ", siglaConselho=" + siglaConselho + ", sexo=" + sexo
				+ ", tituloMasculinoConselho=" + tituloMasculinoConselho
				+ ", tituloFemininoConselho=" + tituloFemininoConselho
				+ ", nomeProfissional=" + nomeProfissional
				+ ", tituloMasculinoVinculo=" + tituloMasculinoVinculo
				+ ", tituloFemininoVinculo=" + tituloFemininoVinculo
				+ ", indResponsavel=" + indResponsavel
				+ ", funcaoProfissional=" + funcaoProfissional + "]";
	}
	
	public Integer getRamalChefiaCCLotacao() {
		return ramalChefiaCCLotacao;
	}
	
	public void setRamalChefiaCCLotacao(Integer ramalChefiaCCLotacao) {
		this.ramalChefiaCCLotacao = ramalChefiaCCLotacao;
	}
	
	public Integer getRamalChefiaCCAtuacao() {
		return ramalChefiaCCAtuacao;
	}
	
	public void setRamalChefiaCCAtuacao(Integer ramalChefiaCCAtuacao) {
		this.ramalChefiaCCAtuacao = ramalChefiaCCAtuacao;
}

	public Integer getRamal() {
		return ramal;
	}

	public void setRamal(Integer ramal) {
		this.ramal = ramal;
	}

	public Boolean getSituacaoFuncionario() {
		return situacaoFuncionario;
	}

	public void setSituacaoFuncionario(Boolean situacaoFuncionario) {
		this.situacaoFuncionario = situacaoFuncionario;
	}

	public String getCarteiraUnimed() {
		return carteiraUnimed;
	}

	public void setCarteiraUnimed(String carteiraUnimed) {
		this.carteiraUnimed = carteiraUnimed;
	}	
	
	public String getRpfNome() {
		return rpfNome;
}

	public void setRpfNome(String rpfNome) {
		this.rpfNome = rpfNome;
	}

	public Integer getRpfCodigo() {
		return rpfCodigo;
	}

	public void setRpfCodigo(Integer rpfCodigo) {
		this.rpfCodigo = rpfCodigo;
	}	
	
	public String getLabelProfissional() {
		if(this.getVinculo() != null && this.getMatricula() != null){
		labelProfissional = this.getVinculo() + " - " + this.getMatricula();
		}
		return labelProfissional;
	}

	public void setLabelProfissional(String labelProfissional) {
		this.labelProfissional = labelProfissional;
	}
	
}
