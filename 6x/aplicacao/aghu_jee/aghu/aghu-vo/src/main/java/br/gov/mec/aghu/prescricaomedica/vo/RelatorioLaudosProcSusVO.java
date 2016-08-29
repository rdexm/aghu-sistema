package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class RelatorioLaudosProcSusVO {

	private String nomeHospital;
	private Integer cnes;
	private String nomePaciente;
	private Integer prontuario;
	private String leito;
	private String cartaoSus;
	private Date nascimento;
	private String sexo;
	private String nomeMae;
	private String telefone;
	private String endereco;
	private String municipio;
	private BigDecimal ibge;
	private String uf;
	private BigDecimal cep;
	private String cidPrincipal;
	private String cidSecundario;
	private String cidCausas;
	private String nomeSolicitante;
	private Date dataSolicitacao;
	private String cpfCnnSolicitante;
	private String regSolicitante;
	private List<SubRelatorioLaudosProcSusVO> justificativas;
	// #40967
	private String dataSaidaFormatada;
	private Integer mudProcSolic;
	private Integer mudProcRealiz;
	private String mudDescrSolic;
	private String mudDescrRealiz;

	public RelatorioLaudosProcSusVO(){
		
	}

	public RelatorioLaudosProcSusVO(String nomeHospital, Integer cnes,
			String nomePaciente, Integer prontuario, String leito,
			String cartaoSus, Date nascimento, String sexo, String nomeMae,
			String telefone, String endereco, String municipio,
			BigDecimal ibge, String uf, BigDecimal cep, String cidPrincipal,
			String cidSecundario, String cidCausas, String nomeSolicitante,
			Date dataSolicitacao, String cpfCnnSolicitante,
			String regSolicitante,
			List<SubRelatorioLaudosProcSusVO> justificativas) {
		super();
		this.nomeHospital = nomeHospital;
		this.cnes = cnes;
		this.nomePaciente = nomePaciente;
		this.prontuario = prontuario;
		this.leito = leito;
		this.cartaoSus = cartaoSus;
		this.nascimento = nascimento;
		this.sexo = sexo;
		this.nomeMae = nomeMae;
		this.telefone = telefone;
		this.endereco = endereco;
		this.municipio = municipio;
		this.ibge = ibge;
		this.uf = uf;
		this.cep = cep;
		this.cidPrincipal = cidPrincipal;
		this.cidSecundario = cidSecundario;
		this.cidCausas = cidCausas;
		this.nomeSolicitante = nomeSolicitante;
		this.dataSolicitacao = dataSolicitacao;
		this.cpfCnnSolicitante = cpfCnnSolicitante;
		this.regSolicitante = regSolicitante;
		this.justificativas = justificativas;
	}

	public String getNomeHospital() {
		return nomeHospital;
	}

	public void setNomeHospital(String nomeHospital) {
		this.nomeHospital = nomeHospital;
	}

	public Integer getCnes() {
		return cnes;
	}

	public void setCnes(Integer cnes) {
		this.cnes = cnes;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getCartaoSus() {
		return cartaoSus;
	}

	public void setCartaoSus(String cartaoSus) {
		this.cartaoSus = cartaoSus;
	}

	public Date getNascimento() {
		return nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public BigDecimal getIbge() {
		return ibge;
	}

	public void setIbge(BigDecimal ibge) {
		this.ibge = ibge;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public BigDecimal getCep() {
		return cep;
	}

	public void setCep(BigDecimal cep) {
		this.cep = cep;
	}

	public String getCidPrincipal() {
		return cidPrincipal;
	}

	public void setCidPrincipal(String cidPrincipal) {
		this.cidPrincipal = cidPrincipal;
	}

	public String getCidSecundario() {
		return cidSecundario;
	}

	public void setCidSecundario(String cidSecundario) {
		this.cidSecundario = cidSecundario;
	}

	public String getCidCausas() {
		return cidCausas;
	}

	public void setCidCausas(String cidCausas) {
		this.cidCausas = cidCausas;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public String getCpfCnnSolicitante() {
		return cpfCnnSolicitante;
	}

	public void setCpfCnnSolicitante(String cpfCnnSolicitante) {
		this.cpfCnnSolicitante = cpfCnnSolicitante;
	}

	public String getRegSolicitante() {
		return regSolicitante;
	}

	public void setRegSolicitante(String regSolicitante) {
		this.regSolicitante = regSolicitante;
	}

	public List<SubRelatorioLaudosProcSusVO> getJustificativas() {
		return justificativas;
	}

	public void setJustificativas(
			List<SubRelatorioLaudosProcSusVO> justificativas) {
		this.justificativas = justificativas;
	}
	
	public String getDataSaidaFormatada() {
		return dataSaidaFormatada;
	}

	public void setDataSaidaFormatada(String dataSaidaFormatada) {
		this.dataSaidaFormatada = dataSaidaFormatada;
	}

	public Integer getMudProcSolic() {
		return mudProcSolic;
	}

	public void setMudProcSolic(Integer mudProcSolic) {
		this.mudProcSolic = mudProcSolic;
	}

	public Integer getMudProcRealiz() {
		return mudProcRealiz;
	}

	public void setMudProcRealiz(Integer mudProcRealiz) {
		this.mudProcRealiz = mudProcRealiz;
	}

	public String getMudDescrSolic() {
		return mudDescrSolic;
	}

	public void setMudDescrSolic(String mudDescrSolic) {
		this.mudDescrSolic = mudDescrSolic;
	}

	public String getMudDescrRealiz() {
		return mudDescrRealiz;
	}

	public void setMudDescrRealiz(String mudDescrRealiz) {
		this.mudDescrRealiz = mudDescrRealiz;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cartaoSus == null) ? 0 : cartaoSus.hashCode());
		result = prime * result + ((cep == null) ? 0 : cep.hashCode());
		result = prime * result
				+ ((cidCausas == null) ? 0 : cidCausas.hashCode());
		result = prime * result
				+ ((cidPrincipal == null) ? 0 : cidPrincipal.hashCode());
		result = prime * result
				+ ((cidSecundario == null) ? 0 : cidSecundario.hashCode());
		result = prime * result + ((cnes == null) ? 0 : cnes.hashCode());
		result = prime
				* result
				+ ((cpfCnnSolicitante == null) ? 0 : cpfCnnSolicitante
						.hashCode());
		result = prime * result
				+ ((dataSolicitacao == null) ? 0 : dataSolicitacao.hashCode());
		result = prime * result
				+ ((endereco == null) ? 0 : endereco.hashCode());
		result = prime * result + ((ibge == null) ? 0 : ibge.hashCode());
		result = prime * result
				+ ((justificativas == null) ? 0 : justificativas.hashCode());
		result = prime * result + ((leito == null) ? 0 : leito.hashCode());
		result = prime * result
				+ ((municipio == null) ? 0 : municipio.hashCode());
		result = prime * result
				+ ((nascimento == null) ? 0 : nascimento.hashCode());
		result = prime * result
				+ ((nomeHospital == null) ? 0 : nomeHospital.hashCode());
		result = prime * result + ((nomeMae == null) ? 0 : nomeMae.hashCode());
		result = prime * result
				+ ((nomePaciente == null) ? 0 : nomePaciente.hashCode());
		result = prime * result
				+ ((nomeSolicitante == null) ? 0 : nomeSolicitante.hashCode());
		result = prime * result
				+ ((prontuario == null) ? 0 : prontuario.hashCode());
		result = prime * result
				+ ((regSolicitante == null) ? 0 : regSolicitante.hashCode());
		result = prime * result + ((sexo == null) ? 0 : sexo.hashCode());
		result = prime * result
				+ ((telefone == null) ? 0 : telefone.hashCode());
		result = prime * result + ((uf == null) ? 0 : uf.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.ExcessiveMethodLength")
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
		RelatorioLaudosProcSusVO other = (RelatorioLaudosProcSusVO) obj;
		if (cartaoSus == null) {
			if (other.cartaoSus != null) {
				return false;
			}
		} else if (!cartaoSus.equals(other.cartaoSus)) {
			return false;
		}
		if (cep == null) {
			if (other.cep != null) {
				return false;
			}
		} else if (!cep.equals(other.cep)) {
			return false;
		}
		if (cidCausas == null) {
			if (other.cidCausas != null) {
				return false;
			}
		} else if (!cidCausas.equals(other.cidCausas)) {
			return false;
		}
		if (cidPrincipal == null) {
			if (other.cidPrincipal != null) {
				return false;
			}
		} else if (!cidPrincipal.equals(other.cidPrincipal)) {
			return false;
		}
		if (cidSecundario == null) {
			if (other.cidSecundario != null) {
				return false;
			}
		} else if (!cidSecundario.equals(other.cidSecundario)) {
			return false;
		}
		if (cnes == null) {
			if (other.cnes != null) {
				return false;
			}
		} else if (!cnes.equals(other.cnes)) {
			return false;
		}
		if (cpfCnnSolicitante == null) {
			if (other.cpfCnnSolicitante != null) {
				return false;
			}
		} else if (!cpfCnnSolicitante.equals(other.cpfCnnSolicitante)) {
			return false;
		}
		if (dataSolicitacao == null) {
			if (other.dataSolicitacao != null) {
				return false;
			}
		} else if (!dataSolicitacao.equals(other.dataSolicitacao)) {
			return false;
		}
		if (endereco == null) {
			if (other.endereco != null) {
				return false;
			}
		} else if (!endereco.equals(other.endereco)) {
			return false;
		}
		if (ibge == null) {
			if (other.ibge != null) {
				return false;
			}
		} else if (!ibge.equals(other.ibge)) {
			return false;
		}
		if (justificativas == null) {
			if (other.justificativas != null) {
				return false;
			}
		} else if (!justificativas.equals(other.justificativas)) {
			return false;
		}
		if (leito == null) {
			if (other.leito != null) {
				return false;
			}
		} else if (!leito.equals(other.leito)) {
			return false;
		}
		if (municipio == null) {
			if (other.municipio != null) {
				return false;
			}
		} else if (!municipio.equals(other.municipio)) {
			return false;
		}
		if (nascimento == null) {
			if (other.nascimento != null) {
				return false;
			}
		} else if (!nascimento.equals(other.nascimento)) {
			return false;
		}
		if (nomeHospital == null) {
			if (other.nomeHospital != null) {
				return false;
			}
		} else if (!nomeHospital.equals(other.nomeHospital)) {
			return false;
		}
		if (nomeMae == null) {
			if (other.nomeMae != null) {
				return false;
			}
		} else if (!nomeMae.equals(other.nomeMae)) {
			return false;
		}
		if (nomePaciente == null) {
			if (other.nomePaciente != null) {
				return false;
			}
		} else if (!nomePaciente.equals(other.nomePaciente)) {
			return false;
		}
		if (nomeSolicitante == null) {
			if (other.nomeSolicitante != null) {
				return false;
			}
		} else if (!nomeSolicitante.equals(other.nomeSolicitante)) {
			return false;
		}
		if (prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!prontuario.equals(other.prontuario)) {
			return false;
		}
		if (regSolicitante == null) {
			if (other.regSolicitante != null) {
				return false;
			}
		} else if (!regSolicitante.equals(other.regSolicitante)) {
			return false;
		}
		if (sexo == null) {
			if (other.sexo != null) {
				return false;
			}
		} else if (!sexo.equals(other.sexo)) {
			return false;
		}
		if (telefone == null) {
			if (other.telefone != null) {
				return false;
			}
		} else if (!telefone.equals(other.telefone)) {
			return false;
		}
		if (uf == null) {
			if (other.uf != null) {
				return false;
			}
		} else if (!uf.equals(other.uf)) {
			return false;
		}
		return true;
	}

}
