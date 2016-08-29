package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GerarPDFSinanVO implements Serializable {

	private static final long serialVersionUID = -2838714088649611466L;
	
	private Integer seqNotificacao;
	private String tipoNotificacao;
	private String doenca;
	private String cid;
	private Date dtNotificacao;
	private String ufSede;
	private String municipioNotificacao;
	private String unidadeDeSaude;
	private Integer cnes;
	private Date dtDiagnostico;
	private String nomePaciente;
	private Date dtNascimento;
	private Short idade;
	private String especIdade;
	private String sexo;
	private String indGestante;
	private String raca;
	private String escolaridade;
	private Long nroCartaoSus;
	private String nomeMae;
	private String ufSigla;
	private String municipioResidencia;
	private Integer cddCodigo;
	private String distrito;
	private String bairro;
	private String logradouro;
	private Integer codigoLogradouro;
	private String numeroLogradouro;
	private String complLogradouro;
	private String geoCampo1;
	private String geoCampo2;
	private String pontoReferencia;
	private Integer cep;
	private Short dddTelefone;
	private Long numeroTelefone;
	private String zona;
	private String pais;
	private Integer prontuario;
	private String tipoEntrada;
	private Integer indLiberdade;
	private Integer indProfSaude;
	private Integer indSitRua;
	private Integer indImigrantes;
	private String indBeneficiario;
	private String forma;
	private String descExtrapulmonar;
	private String descrOutraExtrapulmonar;
	private Integer indAids;
	private Integer indDiabetes;
	private Integer indDoencaMental;
	private Integer indAlcoolismo;
	private Integer indTabagismo;
	private Integer indDrogasIlicitas;
	private Integer indOutrasDoencas;
	private String descOutroAgravo;
	private String baciloscopiaEscarro;
	private String raioxTorax;
	private String hiv;
	private String indAntiRetroviral;
	private String histopatologia;
	private String culturaEscarro;
	private String indTmr;
	private String indSensibilidade;
	private Date dtInicioTratAtual;
	private Short contatosRegistrados;
	private String prontuarioFormatado;
	private String cepFormatado;
	
	private String descPopulacEspecias;
	private String descDoeancasAgravosAssociados;
	
	private String nomeMedico;
	private String funcao;

	public enum Fields {

		SEQ_NOTIFICACAO("seqNotificacao"),
		TIPO_NOTIFICACAO("tipoNotificacao"),
		DOENCA("doenca"),
		CID("cid"),
		DT_NOTIFICACAO("dtNotificacao"),
		UF_SEDE("ufSede"),
		MUNICIPIO_NOTIFICACAO("municipioNotificacao"),
		UNIDADE_DE_SAUDE("unidadeDeSaude"),
		CNES("cnes"),
		DT_DIAGNOSTICO("dtDiagnostico"),
		NOME_PACIENTE("nomePaciente"),
		DT_NASCIMENTO("dtNascimento"),
		IDADE("idade"),
		ESPEC_IDADE("especIdade"),
		SEXO("sexo"),
		IND_GESTANTE("indGestante"),
		RACA("raca"),
		ESCOLARIDADE("escolaridade"),
		NRO_CARTAO_SUS("nroCartaoSus"),
		NOME_MAE("nomeMae"),
		UF_SIGLA("ufSigla"),
		MUNICIPIO_RESIDENCIA("municipioResidencia"),
		CDD_CODIGO("cddCodigo"),
		DISTRITO("distrito"),
		BAIRRO("bairro"),
		LOGRADOURO("logradouro"),
		CODIGO_LOGRADOURO("codigoLogradouro"),
		NUMERO_LOGRADOURO("numeroLogradouro"),
		COMPL_LOGRADOURO("complLogradouro"),
		GEO_CAMPO1("geoCampo1"),
		GEO_CAMPO2("geoCampo2"),
		PONTO_REFERENCIA("pontoReferencia"),
		CEP("cep"),
		DDD_TELEFONE("dddTelefone"),
		NUMERO_TELEFONE("numeroTelefone"),
		ZONA("zona"),
		PAIS("pais"),
		PRONTUARIO("prontuario"),
		TIPO_ENTRADA("tipoEntrada"),
		IND_LIBERDADE("indLiberdade"),
		IND_PROF_SAUDE("indProfSaude"),
		IND_SIT_RUA("indSitRua"),
		IND_IMIGRANTES("indImigrantes"),
		IND_BENEFICIARIO("indBeneficiario"),
		FORMA("forma"),
		DESC_EXTRAPULMONAR("descExtrapulmonar"),
		DESCR_OUTRA_EXTRAPULMONAR("descrOutraExtrapulmonar"),
		IND_AIDS("indAids"),
		IND_DIABETES("indDiabetes"),
		IND_DOENCA_MENTAL("indDoencaMental"),
		IND_ALCOOLISMO("indAlcoolismo"),
		IND_TABAGISMO("indTabagismo"),
		IND_DROGAS_ILICITAS("indDrogasIlicitas"),
		IND_OUTRAS_DOENCAS("indOutrasDoencas"),
		DESC_OUTRO_AGRAVO("descOutroAgravo"),
		BACILOSCOPIA_ESCARRO("baciloscopiaEscarro"),
		RAIOX_TORAX("raioxTorax"),
		HIV("hiv"),
		IND_ANTI_RETROVIRAL("indAntiRetroviral"),
		HISTOPATOLOGIA("histopatologia"),
		CULTURA_ESCARRO("culturaEscarro"),
		IND_TMR("indTmr"),
		IND_SENSIBILIDADE("indSensibilidade"),
		DT_INICIO_TRAT_ATUAL("dtInicioTratAtual"),
		CONTATOS_REGISTRADOS("contatosRegistrados");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(seqNotificacao).toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

		GerarPDFSinanVO other = (GerarPDFSinanVO) obj;

		return new EqualsBuilder().append(seqNotificacao, other.seqNotificacao).isEquals();
	}

	public Integer getSeqNotificacao() {
		return seqNotificacao;
	}

	public void setSeqNotificacao(Integer seqNotificacao) {
		this.seqNotificacao = seqNotificacao;
	}

	public String getTipoNotificacao() {
		return tipoNotificacao;
	}

	public void setTipoNotificacao(String tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}

	public String getDoenca() {
		return doenca;
	}

	public void setDoenca(String doenca) {
		this.doenca = doenca;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public Date getDtNotificacao() {
		return dtNotificacao;
	}

	public void setDtNotificacao(Date dtNotificacao) {
		this.dtNotificacao = dtNotificacao;
	}

	public String getUfSede() {
		return ufSede;
	}

	public void setUfSede(String ufSede) {
		this.ufSede = ufSede;
	}

	public String getMunicipioNotificacao() {
		return municipioNotificacao;
	}

	public void setMunicipioNotificacao(String municipioNotificacao) {
		this.municipioNotificacao = municipioNotificacao;
	}

	public String getUnidadeDeSaude() {
		return unidadeDeSaude;
	}

	public void setUnidadeDeSaude(String unidadeDeSaude) {
		this.unidadeDeSaude = unidadeDeSaude;
	}

	public Integer getCnes() {
		return cnes;
	}

	public void setCnes(Integer cnes) {
		this.cnes = cnes;
	}

	public Date getDtDiagnostico() {
		return dtDiagnostico;
	}

	public void setDtDiagnostico(Date dtDiagnostico) {
		this.dtDiagnostico = dtDiagnostico;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Short getIdade() {
		return idade;
	}

	public void setIdade(Short idade) {
		this.idade = idade;
	}

	public String getEspecIdade() {
		return especIdade;
	}

	public void setEspecIdade(String especIdade) {
		this.especIdade = especIdade;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getIndGestante() {
		return indGestante;
	}

	public void setIndGestante(String indGestante) {
		this.indGestante = indGestante;
	}

	public String getRaca() {
		return raca;
	}

	public void setRaca(String raca) {
		this.raca = raca;
	}

	public String getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(String escolaridade) {
		this.escolaridade = escolaridade;
	}

	public Long getNroCartaoSus() {
		return nroCartaoSus;
	}

	public void setNroCartaoSus(Long nroCartaoSus) {
		this.nroCartaoSus = nroCartaoSus;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getUfSigla() {
		return ufSigla;
	}

	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}

	public String getMunicipioResidencia() {
		return municipioResidencia;
	}

	public void setMunicipioResidencia(String municipioResidencia) {
		this.municipioResidencia = municipioResidencia;
	}

	public Integer getCddCodigo() {
		return cddCodigo;
	}

	public void setCddCodigo(Integer cddCodigo) {
		this.cddCodigo = cddCodigo;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getCodigoLogradouro() {
		return codigoLogradouro;
	}

	public void setCodigoLogradouro(Integer codigoLogradouro) {
		this.codigoLogradouro = codigoLogradouro;
	}

	public String getNumeroLogradouro() {
		return numeroLogradouro;
	}

	public void setNumeroLogradouro(String numeroLogradouro) {
		this.numeroLogradouro = numeroLogradouro;
	}

	public String getComplLogradouro() {
		return complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	public String getGeoCampo1() {
		return geoCampo1;
	}

	public void setGeoCampo1(String geoCampo1) {
		this.geoCampo1 = geoCampo1;
	}

	public String getGeoCampo2() {
		return geoCampo2;
	}

	public void setGeoCampo2(String geoCampo2) {
		this.geoCampo2 = geoCampo2;
	}

	public String getPontoReferencia() {
		return pontoReferencia;
	}

	public void setPontoReferencia(String pontoReferencia) {
		this.pontoReferencia = pontoReferencia;
	}

	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public Short getDddTelefone() {
		return dddTelefone;
	}

	public void setDddTelefone(Short dddTelefone) {
		this.dddTelefone = dddTelefone;
	}

	public Long getNumeroTelefone() {
		return numeroTelefone;
	}

	public void setNumeroTelefone(Long numeroTelefone) {
		this.numeroTelefone = numeroTelefone;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(String tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public String getIndBeneficiario() {
		return indBeneficiario;
	}

	public void setIndBeneficiario(String indBeneficiario) {
		this.indBeneficiario = indBeneficiario;
	}

	public String getForma() {
		return forma;
	}

	public void setForma(String forma) {
		this.forma = forma;
	}

	public String getDescrOutraExtrapulmonar() {
		return descrOutraExtrapulmonar;
	}

	public void setDescrOutraExtrapulmonar(String descrOutraExtrapulmonar) {
		this.descrOutraExtrapulmonar = descrOutraExtrapulmonar;
	}

	public String getDescOutroAgravo() {
		return descOutroAgravo;
	}

	public void setDescOutroAgravo(String descOutroAgravo) {
		this.descOutroAgravo = descOutroAgravo;
	}

	public String getBaciloscopiaEscarro() {
		return baciloscopiaEscarro;
	}

	public void setBaciloscopiaEscarro(String baciloscopiaEscarro) {
		this.baciloscopiaEscarro = baciloscopiaEscarro;
	}

	public String getRaioxTorax() {
		return raioxTorax;
	}

	public void setRaioxTorax(String raioxTorax) {
		this.raioxTorax = raioxTorax;
	}

	public String getHiv() {
		return hiv;
	}

	public void setHiv(String hiv) {
		this.hiv = hiv;
	}

	public String getIndAntiRetroviral() {
		return indAntiRetroviral;
	}

	public void setIndAntiRetroviral(String indAntiRetroviral) {
		this.indAntiRetroviral = indAntiRetroviral;
	}

	public String getHistopatologia() {
		return histopatologia;
	}

	public void setHistopatologia(String histopatologia) {
		this.histopatologia = histopatologia;
	}

	public String getCulturaEscarro() {
		return culturaEscarro;
	}

	public void setCulturaEscarro(String culturaEscarro) {
		this.culturaEscarro = culturaEscarro;
	}

	public String getIndTmr() {
		return indTmr;
	}

	public void setIndTmr(String indTmr) {
		this.indTmr = indTmr;
	}

	public String getIndSensibilidade() {
		return indSensibilidade;
	}

	public void setIndSensibilidade(String indSensibilidade) {
		this.indSensibilidade = indSensibilidade;
	}

	public Date getDtInicioTratAtual() {
		return dtInicioTratAtual;
	}

	public void setDtInicioTratAtual(Date dtInicioTratAtual) {
		this.dtInicioTratAtual = dtInicioTratAtual;
	}

	public Short getContatosRegistrados() {
		return contatosRegistrados;
	}

	public void setContatosRegistrados(Short contatosRegistrados) {
		this.contatosRegistrados = contatosRegistrados;
	}

	public String getDescExtrapulmonar() {
		return descExtrapulmonar;
	}

	public void setDescExtrapulmonar(String descExtrapulmonar) {
		this.descExtrapulmonar = descExtrapulmonar;
	}

	public Integer getIndLiberdade() {
		return indLiberdade;
	}

	public void setIndLiberdade(Integer indLiberdade) {
		this.indLiberdade = indLiberdade;
	}

	public Integer getIndProfSaude() {
		return indProfSaude;
	}

	public void setIndProfSaude(Integer indProfSaude) {
		this.indProfSaude = indProfSaude;
	}

	public Integer getIndSitRua() {
		return indSitRua;
	}

	public void setIndSitRua(Integer indSitRua) {
		this.indSitRua = indSitRua;
	}

	public Integer getIndImigrantes() {
		return indImigrantes;
	}

	public void setIndImigrantes(Integer indImigrantes) {
		this.indImigrantes = indImigrantes;
	}

	public Integer getIndAids() {
		return indAids;
	}

	public void setIndAids(Integer indAids) {
		this.indAids = indAids;
	}

	public Integer getIndDiabetes() {
		return indDiabetes;
	}

	public void setIndDiabetes(Integer indDiabetes) {
		this.indDiabetes = indDiabetes;
	}

	public Integer getIndDoencaMental() {
		return indDoencaMental;
	}

	public void setIndDoencaMental(Integer indDoencaMental) {
		this.indDoencaMental = indDoencaMental;
	}

	public Integer getIndAlcoolismo() {
		return indAlcoolismo;
	}

	public void setIndAlcoolismo(Integer indAlcoolismo) {
		this.indAlcoolismo = indAlcoolismo;
	}

	public Integer getIndTabagismo() {
		return indTabagismo;
	}

	public void setIndTabagismo(Integer indTabagismo) {
		this.indTabagismo = indTabagismo;
	}

	public Integer getIndDrogasIlicitas() {
		return indDrogasIlicitas;
	}

	public void setIndDrogasIlicitas(Integer indDrogasIlicitas) {
		this.indDrogasIlicitas = indDrogasIlicitas;
	}

	public Integer getIndOutrasDoencas() {
		return indOutrasDoencas;
	}

	public void setIndOutrasDoencas(Integer indOutrasDoencas) {
		this.indOutrasDoencas = indOutrasDoencas;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public String getCepFormatado() {
		return cepFormatado;
	}

	public void setCepFormatado(String cepFormatado) {
		this.cepFormatado = cepFormatado;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}

	public String getDescPopulacEspecias() {
		return descPopulacEspecias;
	}

	public void setDescPopulacEspecias(String descPopulacEspecias) {
		this.descPopulacEspecias = descPopulacEspecias;
	}

	public String getDescDoeancasAgravosAssociados() {
		return descDoeancasAgravosAssociados;
	}

	public void setDescDoeancasAgravosAssociados(
			String descDoeancasAgravosAssociados) {
		this.descDoeancasAgravosAssociados = descDoeancasAgravosAssociados;
	}

}
