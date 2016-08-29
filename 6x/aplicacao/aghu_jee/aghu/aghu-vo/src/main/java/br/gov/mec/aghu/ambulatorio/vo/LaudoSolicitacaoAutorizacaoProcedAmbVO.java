package br.gov.mec.aghu.ambulatorio.vo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.swing.text.MaskFormatter;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.core.utils.DateUtil;


public class LaudoSolicitacaoAutorizacaoProcedAmbVO {
	
	private BigDecimal cep;
	private String cid10;
	private String cid10causas;
	private String cid10principal;
	private BigInteger cns;
	private Long codigoTabela;
	private BigDecimal codMunicipio;
	private String codOrgaoEmissor;
	private Date dataNascimentoPaciente;
	private Date dataSolicitacao;
	private String descricaoCid;
	private String descricaoIph;
	private String documento;
	private String documentoAtorizacao;
	private String endereco;
	private String municipioResidencia;
	private String nomeMaePaciente;
	private String nomePaciente;
	private String nomeProfissionalAutorizador;
	private String nomeProfissionalSolicitante;
	private String nomeResponsavel;
	private Long numeroAutorizacao;
	private String numeroCnsProfissionalSolicitante;
	private Long numeroDocumetoProfissionalAutorizador;
	private String observacaoLap;
	private String periodoValidadeApac;
	private Integer prontuarioPaciente;
	private DominioSexo sexoPaciente;
	private DominioCor raca;
	private String telefoneContatoMae;
	private String telefoneContatoResponsavel;
	private String uf;
	private Integer quantidade;
	private Long cmce;
	private Long numeroApac;
	private Long pesCpf;
	private Integer pesCodigo;
	
	//CONTROLE DE FREQUENCIA
	private String mesReferencia;
//	private String nomePaciente;
	private Long cpf;
//	private String nomeResponsavel;
//	private String endereco;
	private String municipio;
//	private String cep;
	private String telefonePaciente;
	private BigInteger nroCartaoSaude;
	private Date dataDeclaracao;
	private String descricao;
	private String localData;
	private Boolean otorrino;
	private Long cpfProfissional;//C23 - 42803 - Usado para obter o CNS
	
	public enum Fields {
		DATA_DECLARACAO("dataDeclaracao"),
		DESCRICAO("descricao"),
		
		SEXO("sexoPaciente"),
		CEP("cep"),
		CID_10("cid10"),
		CID_10_CAUSAS("cid10causas"),
		CID_10_PRINCIPAL("cid10principal"),
		CNS("cns"),
		CODIGO_TABELA("codigoTabela"),
		COD_MUNICIPIO("codMunicipio"),
		COD_ORGAO_EMISSOR("codOrgaoEmissor"),
		DATA_NASCIMENTO_PACIENTE("dataNascimentoPaciente"),
		DATA_SOLICITACAO("dataSolicitacao"),
		DESCRICAO_CID("descricaoCid"),
		DESCRICAO_IPH("descricaoIph"),
		DOCUMENTO("documeto"),
		ENDERECO("endereco"),
		MUNICIPIO_RESIDENCIA("municipioResidencia"),
		NOME_MAE_PACIENTE("nomeMaePaciente"),
		NOME_PACIENTE("nomePaciente"),
		NOME_PROFISSIONAL_SOLICITANTE("nomeProfissionalSolicitante"),
		NOME_PROFISSIONAL_AUTORIZADOR("nomeProfissionalAutorizador"),
		NOME_RESPONSAVEL("nomeResponsavel"),
		NUMERO_AUTORIZACAO("numeroAutorizacao"),
		NUMERO_CNS_PROFISSIONAL_SOLICITANTE("numeroCnsProfissionalSolicitante"),
		NUMERO_DOCUMENTO_PROFISSIONAL_AUTORIZADOR("numeroDocumetoProfissionalAutorizador"),
		OBSERVADOR_LAP("observacaoLap"),
		PERIODO_VALIDADE_APAC("periodoValidadeApac"),
		PRONTUARIO_PACIENTE("prontuarioPaciente"),
		RACA("raca"),
		TELEFONE_CONTATO_MAE("telefoneContatoMae"),
		TELEFONE_CONTATO_RESPONSAVEL("telefoneContatoResponsavel"),
		UF("uf"), 
		CMCE("cmce"), 
		NUMERO_APAC("numeroApac"), 
		PES_CPF("pesCpf"), 
		PES_CODIGO("pesCodigo"),
		PROF_CPF("cpfProfissional")
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
	


	public BigDecimal getCep() {
		return cep;
	}


	public void setCep(BigDecimal cep) {
		this.cep = cep;
	}


	public String getCid10() {
		return cid10;
	}


	public void setCid10(String cid10) {
		this.cid10 = cid10;
	}


	public String getCid10causas() {
		return cid10causas;
	}


	public void setCid10causas(String cid10causas) {
		this.cid10causas = cid10causas;
	}


	public String getCid10principal() {
		return cid10principal;
	}


	public void setCid10principal(String cid10principal) {
		this.cid10principal = cid10principal;
	}


	public BigInteger getCns() {
		return cns;
	}


	public void setCns(BigInteger cns) {
		this.cns = cns;
	}


	public Long getCodigoTabela() {
		return codigoTabela;
	}


	public void setCodigoTabela(Long codigoTabela) {
		this.codigoTabela = codigoTabela;
	}


	public BigDecimal getCodMunicipio() {
		return codMunicipio;
	}


	public void setCodMunicipio(BigDecimal codMunicipio) {
		this.codMunicipio = codMunicipio;
	}


	public String getCodOrgaoEmissor() {
		return codOrgaoEmissor;
	}


	public void setCodOrgaoEmissor(String codOrgaoEmissor) {
		this.codOrgaoEmissor = codOrgaoEmissor;
	}


	public Date getDataNascimentoPaciente() {
		return dataNascimentoPaciente;
	}


	public void setDataNascimentoPaciente(Date dataNascimentoPaciente) {
		this.dataNascimentoPaciente = dataNascimentoPaciente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}
	
	public String getDataSolicitacaoFormatada24h() {
		return DateUtil.dataToString(dataSolicitacao, "dd/MM/YY HH:mm");
	}
	

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}


	public String getDescricaoCid() {
		return descricaoCid;
	}


	public void setDescricaoCid(String descricaoCid) {
		this.descricaoCid = descricaoCid;
	}


	public String getDescricaoIph() {
		return descricaoIph;
	}


	public void setDescricaoIph(String descricaoIph) {
		this.descricaoIph = descricaoIph;
	}


	public String getDocumento() {
		return documento;
	}


	public void setDocumento(String documento) {
		this.documento = documento;
	}


	public String getEndereco() {
		return endereco;
	}


	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}


	public String getMunicipioResidencia() {
		return municipioResidencia;
	}


	public void setMunicipioResidencia(String municipioResidencia) {
		this.municipioResidencia = municipioResidencia;
	}


	public String getNomeMaePaciente() {
		return nomeMaePaciente;
	}


	public void setNomeMaePaciente(String nomeMaePaciente) {
		this.nomeMaePaciente = nomeMaePaciente;
	}


	public String getNomeProfissionalAutorizador() {
		return nomeProfissionalAutorizador;
	}


	public void setNomeProfissionalAutorizador(String nomeProfissionalAutorizador) {
		this.nomeProfissionalAutorizador = nomeProfissionalAutorizador;
	}


	public String getNomeProfissionalSolicitante() {
		return nomeProfissionalSolicitante;
	}


	public void setNomeProfissionalSolicitante(String nomeProfissionalSolicitante) {
		this.nomeProfissionalSolicitante = nomeProfissionalSolicitante;
	}


	public String getNomeResponsavel() {
		return nomeResponsavel;
	}


	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}


	public Long getNumeroAutorizacao() {
		return numeroAutorizacao;
	}


	public void setNumeroAutorizacao(Long numeroAutorizacao) {
		this.numeroAutorizacao = numeroAutorizacao;
	}


	public String getNumeroCnsProfissionalSolicitante() {
		return numeroCnsProfissionalSolicitante;
	}


	public void setNumeroCnsProfissionalSolicitante(
			String numeroCnsProfissionalSolicitante) {
		this.numeroCnsProfissionalSolicitante = numeroCnsProfissionalSolicitante;
	}


	public Long getNumeroDocumetoProfissionalAutorizador() {
		return numeroDocumetoProfissionalAutorizador;
	}


	public void setNumeroDocumetoProfissionalAutorizador(
			Long numeroDocumetoProfissionalAutorizador) {
		this.numeroDocumetoProfissionalAutorizador = numeroDocumetoProfissionalAutorizador;
	}

	public String getPeriodoValidadeApac() {
		return periodoValidadeApac;
	}


	public void setPeriodoValidadeApac(String periodoValidadeApac) {
		this.periodoValidadeApac = periodoValidadeApac;
	}


	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}


	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}


	public DominioCor getRaca() {
		return raca;
	}


	public void setRaca(DominioCor raca) {
		this.raca = raca;
	}

	public String getTelefoneContatoMae() {
		return telefoneContatoMae;
	}

	public void setTelefoneContatoMae(String telefoneContatoMae) {
		this.telefoneContatoMae = telefoneContatoMae;
	}


	public String getTelefoneContatoResponsavel() {
		return telefoneContatoResponsavel;
	}


	public void setTelefoneContatoResponsavel(String telefoneContatoResponsavel) {
		this.telefoneContatoResponsavel = telefoneContatoResponsavel;
	}


	public String getUf() {
		return uf;
	}


	public void setUf(String uf) {
		this.uf = uf;
	}


	public String getNomePaciente() {
		return nomePaciente;
	}


	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}


	public DominioSexo getSexoPaciente() {
		return sexoPaciente;
	}


	public void setSexoPaciente(DominioSexo sexoPaciente) {
		this.sexoPaciente = sexoPaciente;
	}


	public Integer getQuantidade() {
		return quantidade;
	}


	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getObservacaoLap() {
		return observacaoLap;
	}


	public void setObservacaoLap(String observacaoLap) {
		this.observacaoLap = observacaoLap;
	}


	public Long getCmce() {
		return cmce;
	}


	public void setCmce(Long cmce) {
		this.cmce = cmce;
	}


	public Long getNumeroApac() {
		return numeroApac;
	}


	public void setNumeroApac(Long numeroApac) {
		this.numeroApac = numeroApac;
	}


	public Long getPesCpf() {
		return pesCpf;
	}


	public void setPesCpf(Long pesCpf) {
		this.pesCpf = pesCpf;
	}


	public Integer getPesCodigo() {
		return pesCodigo;
	}


	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}


	public String getMesReferencia() {
		return mesReferencia;
	}


	public void setMesReferencia(String mesReferencia) {
		this.mesReferencia = mesReferencia;
	}


	public Long getCpf() {
		return cpf;
	}


	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getCpfFormatado(){
		MaskFormatter mascara;
		try {
			mascara = new MaskFormatter("###.###.###-##");
			mascara.setValueContainsLiteralCharacters(false);
			return mascara.valueToString(String.valueOf(cpf));
		} catch (ParseException e) {
			return "";
		}
	}

	public String getMunicipio() {
		return municipio;
	}


	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}


	public String getTelefonePaciente() {
		return telefonePaciente;
	}


	public void setTelefonePaciente(String telefonePaciente) {
		this.telefonePaciente = telefonePaciente;
	}


	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}


	public void setNroCartaoSaude(BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	public Date getDataDeclaracao() {
		return dataDeclaracao;
	}


	public void setDataDeclaracao(Date dataDeclaracao) {
		this.dataDeclaracao = dataDeclaracao;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public String getLocalData() {
		return localData;
	}


	public void setLocalData(String localData) {
		this.localData = localData;
	}


	public Boolean getOtorrino() {
		return otorrino;
	}


	public void setOtorrino(Boolean otorrino) {
		this.otorrino = otorrino;
	}

	public String getDocumentoAtorizacao() {
		return documentoAtorizacao;
	}


	public void setDocumentoAtorizacao(String documentoAtorizacao) {
		this.documentoAtorizacao = documentoAtorizacao;
	}
	
	
	/**
	 * @return the cpfProfissional
	 */
	public Long getCpfProfissional() {
		return cpfProfissional;
	}

	/**
	 * @param cpfProfissional the cpfProfissional to set
	 */
	public void setCpfProfissional(Long cpfProfissional) {
		this.cpfProfissional = cpfProfissional;
	}


}
