package br.gov.mec.aghu.paciente.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author riccosta
 * 
 */

public class RelatorioPacienteVO {

	private String nome;
	private String nomeSocial;
	private String sexo;
	private String estadoCivil;
	private Date dtNascimento;
	private Integer prontuario;
	private Integer prontFamilia;
	private String nomeMae;
	private String cor;
	private Short dddFoneResidencial;
	private Long foneResidencial;
	private Short dddFoneRecado;
	private String foneRecado;
	private String codigoPaciente;

	private String agendaSalaTurno;

	private String nomePai;
	private Date dtIdentificacao; // dataCadastro
	private Date dtRecadastro;
	private String rg;
	private Long cpf;
	private String nomeAnterior;
	private String naturalidade; // cidadeNascimento
	private String ufNascimento;
	private String grauInstrucao;
	private String nacionalidade;
	private String profissao;
	private Integer codigoProfissao;
	private Long numeroPis;
	private String paisOrigem;
	private String siglaPaisNacionalidade;

	private String postoReferencia;
	private String logradouro;
	private Integer nroLogradouro;
	private String complementoLogradouro;
	private String bairro;
	private String cidade;
	private String ufLogradouro;
	private Integer cep;
	private BigDecimal codIbge;

	private String identificador;
	private String areaCadastradora;

	private String observacao;
	private Long nroCartaoSaude; // numerocartao Provissorio

	// DADOS PACIENTE CARTAO
	private Long cartaoNacionalSaudeMae;
	private Date dataEntradaBr;
	private Date dataNaturalizacao;
	private Long portariaNatural;
	private String tipoCertidao;
	private String nomeCartorio;
	private String livro;
	private Short folhas;
	private Integer termo;
	private Date dataEmissao;
	private Date dataEmissaoDocto;
	private Date criadoEm;
	private String motivoCadastro;
	private String orgaoEmissor;
	private Short codigoOrgaoEmissor;
	private String ufEmitiuDoc;
	private String docReferencia;
	private String horaCadastro;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Integer getProntuario() {		
		return prontuario;
	}
	
	public String getProntuarioFormatado() {
		String prontAux = null;
		if (prontuario != null && prontuario != 0) {
			prontAux = prontuario.toString();
			prontAux = prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1);
		}		
		return prontAux;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public Short getDddFoneResidencial() {
		return dddFoneResidencial;
	}

	public void setDddFoneResidencial(Short dddFoneResidencial) {
		this.dddFoneResidencial = dddFoneResidencial;
	}

	public Long getFoneResidencial() {
		return foneResidencial;
	}

	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}

	public Short getDddFoneRecado() {
		return dddFoneRecado;
	}

	public void setDddFoneRecado(Short dddFoneRecado) {
		this.dddFoneRecado = dddFoneRecado;
	}

	public String getFoneRecado() {
		return foneRecado;
	}

	public void setFoneRecado(String foneRecado) {
		this.foneRecado = foneRecado;
	}

	public String getAgendaSalaTurno() {
		return agendaSalaTurno;
	}

	public void setAgendaSalaTurno(String agendaSalaTurno) {
		this.agendaSalaTurno = agendaSalaTurno;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public Date getDtIdentificacao() {
		return dtIdentificacao;
	}

	public void setDtIdentificacao(Date dtIdentificacao) {
		this.dtIdentificacao = dtIdentificacao;
	}

	public Date getDtRecadastro() {
		return dtRecadastro;
	}

	public void setDtRecadastro(Date dtRecadastro) {
		this.dtRecadastro = dtRecadastro;
	}

	public String getRg() {
		String retorno = null;
		if (rg != null && !rg.isEmpty() ) {
			retorno = rg;
		}
		return retorno;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Long getCpf() {			
		return cpf;
	}

	public String getCpfFormatado(){
		String retorno = null;
		if (cpf != null && cpf != 0) { 
			retorno = cpf.toString();
			retorno = StringUtils.leftPad(retorno, 11, "0");
			retorno = (retorno.substring(0, 3) + "." + retorno.substring(3, 6) + "." + retorno.substring(6, 9) + "-" + retorno.substring(9, 11));			
		}
		return retorno;
	}
	
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getNomeAnterior() {
		return nomeAnterior;
	}

	public void setNomeAnterior(String nomeAnterior) {
		this.nomeAnterior = nomeAnterior;
	}

	public String getNaturalidade() {
		return naturalidade;
	}

	public void setNaturalidade(String naturalidade) {
		this.naturalidade = naturalidade;
	}

	public String getUfNascimento() {
		return ufNascimento;
	}

	public void setUfNascimento(String ufNascimento) {
		this.ufNascimento = ufNascimento;
	}

	public String getGrauInstrucao() {
		return grauInstrucao;
	}

	public void setGrauInstrucao(String grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}

	public String getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	public Integer getCodigoProfissao() {
		return codigoProfissao;
	}

	public void setCodigoProfissao(Integer codigoProfissao) {
		this.codigoProfissao = codigoProfissao;
	}

	public String getPostoReferencia() {
		return postoReferencia;
	}

	public void setPostoReferencia(String postoReferencia) {
		this.postoReferencia = postoReferencia;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	public String getComplementoLogradouro() {
		return complementoLogradouro;
	}

	public void setComplementoLogradouro(String complementoLogradouro) {
		this.complementoLogradouro = complementoLogradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUfLogradouro() {
		return ufLogradouro;
	}

	public void setUfLogradouro(String ufLogradouro) {
		this.ufLogradouro = ufLogradouro;
	}

	public Integer getCep() {
		return cep;
	}

	public String getCepFormatado() {
		String retorno = null;
		if(cep != null && cep != 0) {
			retorno = cep.toString();			
			retorno = retorno.substring(0, retorno.length() - 3) + "-" + retorno.substring(retorno.length() - 3, retorno.length());
		}
		return retorno;
	}
	
	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getAreaCadastradora() {
		return areaCadastradora;
	}

	public void setAreaCadastradora(String areaCadastradora) {
		this.areaCadastradora = areaCadastradora;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getTelefone() {
		if (getFoneResidencial() != null && getFoneResidencial() != 0) {
			return getDddFoneResidencial() + "-" + getFoneResidencial();
		} 
		if(getDddFoneRecado() != null && getDddFoneRecado() != 0) {
			return getDddFoneRecado() + "-" + getFoneRecado();
		}
		return null;
	}

	public BigDecimal getCodIbge() {
		return codIbge;
	}

	public void setCodIbge(BigDecimal codIbge) {
		this.codIbge = codIbge;
	}

	public Long getNumeroPis() {
		if (numeroPis != null && numeroPis == 0) {
			return null;
		}
		return numeroPis;
	}

	public void setNumeroPis(Long numeroPis) {
		this.numeroPis = numeroPis;
	}

	public Long getNroCartaoSaude() {
		if (nroCartaoSaude != null && nroCartaoSaude.intValue() == 0) {
			return null;
		}
		return nroCartaoSaude;
	}

	public void setNroCartaoSaude(Long nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	public Long getCartaoNacionalSaudeMae() {
		if (cartaoNacionalSaudeMae != null && cartaoNacionalSaudeMae == 0) {
			return null;
		}
		return cartaoNacionalSaudeMae;
	}

	public void setCartaoNacionalSaudeMae(Long cartaoNacionalSaudeMae) {
		this.cartaoNacionalSaudeMae = cartaoNacionalSaudeMae;
	}

	public Date getDataEntradaBr() {
		return dataEntradaBr;
	}

	public void setDataEntradaBr(Date dataEntradaBr) {
		this.dataEntradaBr = dataEntradaBr;
	}

	public Date getDataNaturalizacao() {
		return dataNaturalizacao;
	}

	public void setDataNaturalizacao(Date dataNaturalizacao) {
		this.dataNaturalizacao = dataNaturalizacao;
	}

	public Long getPortariaNatural() {
		if (portariaNatural != null && portariaNatural == 0) {
			return null;
		}
		return portariaNatural;
	}

	public void setPortariaNatural(Long portariaNatural) {
		this.portariaNatural = portariaNatural;
	}

	public String getTipoCertidao() {
		return tipoCertidao;
	}

	public void setTipoCertidao(String tipoCertidao) {
		this.tipoCertidao = tipoCertidao;
	}

	public String getNomeCartorio() {
		return nomeCartorio;
	}

	public void setNomeCartorio(String nomeCartorio) {
		this.nomeCartorio = nomeCartorio;
	}

	public String getLivro() {
		return livro;
	}

	public void setLivro(String livro) {
		this.livro = livro;
	}

	public Short getFolhas() {
		if (folhas != null && folhas == 0) {
			return null;
		}
		return folhas;
	}

	public void setFolhas(Short folhas) {
		this.folhas = folhas;
	}

	public Integer getTermo() {
		if (termo != null && termo == 0) {
			return null;
		}
		return termo;
	}

	public void setTermo(Integer termo) {
		this.termo = termo;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public Date getDataEmissaoDocto() {
		return dataEmissaoDocto;
	}

	public void setDataEmissaoDocto(Date dataEmissaoDocto) {
		this.dataEmissaoDocto = dataEmissaoDocto;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getMotivoCadastro() {
		return motivoCadastro;
	}

	public void setMotivoCadastro(String motivoCadastro) {
		this.motivoCadastro = motivoCadastro;
	}

	public String getOrgaoEmissor() {
		return orgaoEmissor;
	}

	public void setOrgaoEmissor(String orgaoEmissor) {
		this.orgaoEmissor = orgaoEmissor;
	}

	public Short getCodigoOrgaoEmissor() {
		return codigoOrgaoEmissor;
	}

	public void setCodigoOrgaoEmissor(Short codigoOrgaoEmissor) {
		this.codigoOrgaoEmissor = codigoOrgaoEmissor;
	}

	public String getUfEmitiuDoc() {
		return ufEmitiuDoc;
	}

	public void setUfEmitiuDoc(String ufEmitiuDoc) {
		this.ufEmitiuDoc = ufEmitiuDoc;
	}

	public String getDocReferencia() {
		return docReferencia;
	}

	public void setDocReferencia(String docReferencia) {
		this.docReferencia = docReferencia;
	}

	public String getSiglaPaisNacionalidade() {
		return siglaPaisNacionalidade;
	}

	public void setSiglaPaisNacionalidade(String siglaPaisNacionalidade) {
		this.siglaPaisNacionalidade = siglaPaisNacionalidade;
	}

	public String getPaisOrigem() {
		return paisOrigem;
	}

	public void setPaisOrigem(String paisOrigem) {
		this.paisOrigem = paisOrigem;
	}
		
	public String getNomeSocial() {
		return nomeSocial;
	}
	
	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public Integer getProntFamilia() {
		return prontFamilia;
	}
	
	public void setProntFamilia(Integer prontFamilia) {
		this.prontFamilia = prontFamilia;
	}

	public String getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(String codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getHoraCadastro() {
		return horaCadastro;
	}

	public void setHoraCadastro(String horaCadastro) {
		this.horaCadastro = horaCadastro;
	}
	
	
}
