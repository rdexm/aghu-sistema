package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAgravoAssociado;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAgravoOutrasDoencas;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseAntirretroviral;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseBaciloscopiaEscarro;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseBeneficiario;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseCulturaEscarro;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseDrogasIlicitas;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseEscolaridade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseEspecIdade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseForma;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseHistopatologia;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseHiv;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseImigrantes;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndAids;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndAlcoolismo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndDiabetes;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndDoencaMental;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseIndGestante;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseLiberdade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseProfSaude;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseRaca;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseRaioXTorax;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSensibilidade;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSexo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseSitRua;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTMR;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTabagismo;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTipoEntrada;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseTipoNotificacao;
import br.gov.mec.aghu.dominio.DominioNotificacaoTuberculoseZona;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO de #45060: Complementacao da estória confirmar prescrição (#883) -
 * Formulário do SINAM
 * 
 * @author aghu
 *
 */
public class SinamVO implements BaseBean {

	private static final long serialVersionUID = 4573326713274553111L;
	
	private Integer atdSeq; // Atendimento
	private Integer seq; // MPM_NOTIFICACAO_TBS.SEQ
	

	/*
	 * Dados Gerais
	 */
	private DominioNotificacaoTuberculoseTipoNotificacao tipoNotificacao; // 1.
																			// MPM_NOTIFICACAO_TBS.TIPO_NOTIFICACAO
	private String doenca; // 2. MPM_NOTIFICACAO_TBS.DOENCA*. PADRÃO
									// TUBERCULOSE (CAMPO NOVO)
	private String codigoCid; // 2.1 MPM_NOTIFICACAO_TBS.CID
	private Date dataNotificacao; // / 3. MPM_NOTIFICACAO_TBS.DT_NOTIFICACAO
	private String ufHospital; // 4. PARÂMETRO P_AGHU_UF_SEDE_HU - MPM_NOTIFICACAO_TBS.UF_SIGLA
	private String municipioNotificacao; // 5. PARÂMETRO P_CIDADE_PADRAO - MPM_NOTIFICACAO_TBS.MUNICIPIO_NOTIFICACAO
	private String unidadeSaude; // 6. PARÂMETRO P_HOSPITAL_CLINICAS - MPM_NOTIFICACAO_TBS.UNIDADE_DE_SAUDE
	private Integer codigo; // 6.1 MPM_NOTIFICACAO_TBS.CNES*. PADRÃO PARÂMETRO
							// P_CNES_HCPA (CAMPO NOVO)
	private Date dataDiagnostico; // 7. MPM_NOTIFICACAO_TBS.DT_DIAGNOSTICO

	/*
	 * Notificação individual
	 */
	private String nomePaciente; // 8. #45722 TELA PRESCRIÇÃO
	private Date dataNascimento; // 9. #45722 TELA PRESCRIÇÃO
	private Short idade; // 10. #45722 TELA PRESCRIÇÃO
	private DominioNotificacaoTuberculoseEspecIdade mediaIdade; // 10.1
																// #45722
																// TELA
																// PRESCRIÇÃO
	private DominioNotificacaoTuberculoseSexo sexo; // 11.
													// MPM_NOTIFICACAO_TBS.SEXO
	private DominioNotificacaoTuberculoseIndGestante gestante; // 12.
																// MPM_NOTIFICACAO_TBS.IND_GESTANTE
	private DominioNotificacaoTuberculoseRaca raca; // 13. #45722 TELA
													// PRESCRIÇÃO
	private DominioNotificacaoTuberculoseEscolaridade escolaridade; // 14.
																	// MPM_NOTIFICACAO_TBS.ESCOLARIDADE
	private Long numeroCartaoSus; // 15. #45722 TELA PRESCRIÇÃO
	private String nomeMae; // 16. #45722 TELA PRESCRIÇÃO

	/*
	 * Dados de residência
	 */
	private String ufResidencia; // 17. #45722 TELA PRESCRIÇÃO
	private String municipioResidencia; // 18. #45722 TELA PRESCRIÇÃO
	private Integer codigoIbge; // 18.1 FUTURA MELHORIA?
	private String distrito; // 19. #45722 TELA PRESCRIÇÃO
	private String bairro; // 20. #45722 TELA PRESCRIÇÃO
	private String logradouro; // 21 #45722 TELA PRESCRIÇÃO
	private Integer codigoLogradouro; // 21.1 MPM_NOTIFICACAO_TBS.LOGRAD_CODIGO*
										// (CAMPO NOVO)
	private Integer numero; // 22. #45722 TELA PRESCRIÇÃO
	private String complemento; // 23. #45722 TELA PRESCRIÇÃO
	private String geoCampo1; // 24. MPM_NOTIFICACAO_TBS.GEO_CAMPO1
	private String geoCampo2; // 25. MPM_NOTIFICACAO_TBS.GEO_CAMPO2
	private String pontoReferencia; // 26. #45722 TELA PRESCRIÇÃO
	private Integer cep; // 27. #45722 TELA PRESCRIÇÃO
	private Short dddTelefone; // // 28. #45722 TELA PRESCRIÇÃO
	private Long numeroTelefone; // 28. #45722 TELA PRESCRIÇÃO
	private DominioNotificacaoTuberculoseZona zona; // 29.
													// MPM_NOTIFICACAO_TBS.ZONA
	private String pais; // 30. #45722 TELA PRESCRIÇÃO

	/*
	 * Dados complementares do caso
	 */
	private String nroProntuario; // // 31. #45722 TELA PRESCRIÇÃO - prontuario formatado
	private DominioNotificacaoTuberculoseTipoEntrada tipoEntrada; // 32.
																	// MPM_NOTIFICACAO_TBS.TIPO_ENTRADA
	// private Object populacoesEpeciais; // 33. MPM_NOTIFICACAO_TBS.

	private DominioNotificacaoTuberculoseLiberdade populacaoPrivadaLiberdade; // 33.1
																				// MPM_NOTIFICACAO_TBS.IND_LIBERDADE
	private DominioNotificacaoTuberculoseProfSaude profissionalSaude; // 33.2
																		// MPM_NOTIFICACAO_TBS.IND_PROFSAUDE
	private DominioNotificacaoTuberculoseSitRua populacaoSituacaoRua; // 33.3
																		// MPM_NOTIFICACAO_TBS.IND_SITRUA
	private DominioNotificacaoTuberculoseImigrantes imigrantes; // 33.4
																// MPM_NOTIFICACAO_TBS.IND_IMIGRANTES
	private DominioNotificacaoTuberculoseBeneficiario beneficiarioProgramaTransferenciaRendaGoverno; // 34.
																										// MPM_NOTIFICACAO_TBS.IND_BENEFICIARIO
	private DominioNotificacaoTuberculoseForma forma; // 35.
														// MPM_NOTIFICACAO_TBS.FORMA
	private boolean pleural; // 36.1 MPM_NOTIFICACAO_TBS.IND_PLEURAL
	private boolean gangPerif; // 36.2 MPM_NOTIFICACAO_TBS.IND_GANG_PERIF
	private boolean geniturinaria; // 36.3
									// MPM_NOTIFICACAO_TBS.IND_GENITO_URINARIA
	private boolean ossea; // 36.4 MPM_NOTIFICACAO_TBS.IND_OSSEA
	private boolean ocular; // 36.5 MPM_NOTIFICACAO_TBS.IND_OCULAR
	private boolean miliar; // 36.6 MPM_NOTIFICACAO_TBS.IND_MILIAR
	private boolean meningite; // 36.7 MPM_NOTIFICACAO_TBS.IND_MENINGITE
	private boolean cutanea; // 36.8 MPM_NOTIFICACAO_TBS.IND_CUTANEA
	private boolean laringea; // 36.9 MPM_NOTIFICACAO_TBS.IND_LARINGEA
	private String outraExtrapulmonar; // 36.10
										// MPM_NOTIFICACAO_TBS.IND_OUTRA_EXTRAPULMONAR?
	private String descrOutraExtrapulmonar;

	private DominioNotificacaoTuberculoseIndAids aids; // 37.1
														// MPM_NOTIFICACAO_TBS.IND_AIDS
	private DominioNotificacaoTuberculoseIndDiabetes diabetes; // 37.2
																// MPM_NOTIFICACAO_TBS.IND_DIABETES
	private DominioNotificacaoTuberculoseIndDoencaMental doencaMental; // 37.3
																		// MPM_NOTIFICACAO_TBS.IND_DOENCA_MENTAL
	private DominioNotificacaoTuberculoseIndAlcoolismo alcoolismo; // 37.4
																	// MPM_NOTIFICACAO_TBS.IND_ALCOOLISMO
	private DominioNotificacaoTuberculoseTabagismo tabagismo; // 37.5
																// MPM_NOTIFICACAO_TBS.IND_TABAGISMO
	private DominioNotificacaoTuberculoseDrogasIlicitas usoDrogasIlicitas; // 37.6
																			// MPM_NOTIFICACAO_TBS.IND_DROGASILICITAS
	private DominioNotificacaoTuberculoseAgravoAssociado outroAgravos;
	
	private DominioNotificacaoTuberculoseAgravoOutrasDoencas indOutrasDoencas;
	
	private String descricaoAgravo; // 37.8
									// MPM_NOTIFICACAO_TBS.DESC_OUTRO_AGRAVO
	private DominioNotificacaoTuberculoseBaciloscopiaEscarro baciloscopiaEscarro; // 38.
																					// MPM_NOTIFICACAO_TBS.BACILOSCOPIA_ESCARRO
	private DominioNotificacaoTuberculoseRaioXTorax radiografiaTorax; // 39.
																		// MPM_NOTIFICACAO_TBS.RAIOX_TORAX
	private DominioNotificacaoTuberculoseHiv hiv; // 40.MPM_NOTIFICACAO_TBS.HIV
	private DominioNotificacaoTuberculoseAntirretroviral terapiaAntirretroviral; // 41.
																					// MPM_NOTIFICACAO_TBS.IND_ANTIRRETROVIRAL
	private DominioNotificacaoTuberculoseHistopatologia histopatologia; // 42.
																		// MPM_NOTIFICACAO_TBS.HISTOPATOLOGIA
	private DominioNotificacaoTuberculoseCulturaEscarro cultura; // 43.MPM_NOTIFICACAO_TBS.CULTURA_ESCARRO
	private DominioNotificacaoTuberculoseTMR testeMolecularRapido; // 44.MPM_NOTIFICACAO_TBS.IND_TMR
	private DominioNotificacaoTuberculoseSensibilidade testeSensibilidade; // 45.MPM_NOTIFICACAO_TBS.IND_SENSIBILIDADE
	private Date dataInicioTratamentoAtual; // 46.MPM_NOTIFICACAO_TBS.DT_INICIO_TRAT_ATUAL
	private Short totalContatosIdentificados; // 47.MPM_NOTIFICACAO_TBS.CONTATOS_REGISTRADOS

	/*
	 * Getters e setters
	 */
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public DominioNotificacaoTuberculoseTipoNotificacao getTipoNotificacao() {
		return tipoNotificacao;
	}

	public void setTipoNotificacao(DominioNotificacaoTuberculoseTipoNotificacao tipoNotificacao) {
		this.tipoNotificacao = tipoNotificacao;
	}

	public String getDoenca() {
		return doenca;
	}

	public void setDoenca(String doenca) {
		this.doenca = doenca;
	}

	public String getCodigoCid() {
		return codigoCid;
	}

	public void setCodigoCid(String codigoCid) {
		this.codigoCid = codigoCid;
	}

	public Date getDataNotificacao() {
		return dataNotificacao;
	}

	public void setDataNotificacao(Date dataNotificacao) {
		this.dataNotificacao = dataNotificacao;
	}

	public String getUfHospital() {
		return ufHospital;
	}

	public void setUfHospital(String ufHospital) {
		this.ufHospital = ufHospital;
	}

	public String getMunicipioNotificacao() {
		return municipioNotificacao;
	}

	public void setMunicipioNotificacao(String municipioNotificacao) {
		this.municipioNotificacao = municipioNotificacao;
	}

	public String getUnidadeSaude() {
		return unidadeSaude;
	}

	public void setUnidadeSaude(String unidadeSaude) {
		this.unidadeSaude = unidadeSaude;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Date getDataDiagnostico() {
		return dataDiagnostico;
	}

	public void setDataDiagnostico(Date dataDiagnostico) {
		this.dataDiagnostico = dataDiagnostico;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Short getIdade() {
		return idade;
	}

	public void setIdade(Short idade) {
		this.idade = idade;
	}

	public DominioNotificacaoTuberculoseEspecIdade getMediaIdade() {
		return mediaIdade;
	}

	public void setMediaIdade(DominioNotificacaoTuberculoseEspecIdade mediaIdade) {
		this.mediaIdade = mediaIdade;
	}

	public DominioNotificacaoTuberculoseSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioNotificacaoTuberculoseSexo sexo) {
		this.sexo = sexo;
	}

	public DominioNotificacaoTuberculoseIndGestante getGestante() {
		return gestante;
	}

	public void setGestante(DominioNotificacaoTuberculoseIndGestante gestante) {
		this.gestante = gestante;
	}

	public DominioNotificacaoTuberculoseRaca getRaca() {
		return raca;
	}

	public void setRaca(DominioNotificacaoTuberculoseRaca raca) {
		this.raca = raca;
	}

	public DominioNotificacaoTuberculoseEscolaridade getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(DominioNotificacaoTuberculoseEscolaridade escolaridade) {
		this.escolaridade = escolaridade;
	}

	public Long getNumeroCartaoSus() {
		return numeroCartaoSus;
	}

	public void setNumeroCartaoSus(Long numeroCartaoSus) {
		this.numeroCartaoSus = numeroCartaoSus;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getUfResidencia() {
		return ufResidencia;
	}

	public void setUfResidencia(String ufResidencia) {
		this.ufResidencia = ufResidencia;
	}

	public String getMunicipioResidencia() {
		return municipioResidencia;
	}

	public void setMunicipioResidencia(String municipioResidencia) {
		this.municipioResidencia = municipioResidencia;
	}

	public Integer getCodigoIbge() {
		return codigoIbge;
	}

	public void setCodigoIbge(Integer codigoIbge) {
		this.codigoIbge = codigoIbge;
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

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
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

	public DominioNotificacaoTuberculoseZona getZona() {
		return zona;
	}

	public void setZona(DominioNotificacaoTuberculoseZona zona) {
		this.zona = zona;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getNroProntuario() {
		return nroProntuario;
	}

	public void setNroProntuario(String nroProntuario) {
		this.nroProntuario = nroProntuario;
	}

	public DominioNotificacaoTuberculoseTipoEntrada getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(DominioNotificacaoTuberculoseTipoEntrada tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public DominioNotificacaoTuberculoseLiberdade getPopulacaoPrivadaLiberdade() {
		return populacaoPrivadaLiberdade;
	}

	public void setPopulacaoPrivadaLiberdade(DominioNotificacaoTuberculoseLiberdade populacaoPrivadaLiberdade) {
		this.populacaoPrivadaLiberdade = populacaoPrivadaLiberdade;
	}

	public DominioNotificacaoTuberculoseProfSaude getProfissionalSaude() {
		return profissionalSaude;
	}

	public void setProfissionalSaude(DominioNotificacaoTuberculoseProfSaude profissionalSaude) {
		this.profissionalSaude = profissionalSaude;
	}

	public DominioNotificacaoTuberculoseSitRua getPopulacaoSituacaoRua() {
		return populacaoSituacaoRua;
	}

	public void setPopulacaoSituacaoRua(DominioNotificacaoTuberculoseSitRua populacaoSituacaoRua) {
		this.populacaoSituacaoRua = populacaoSituacaoRua;
	}

	public DominioNotificacaoTuberculoseImigrantes getImigrantes() {
		return imigrantes;
	}

	public void setImigrantes(DominioNotificacaoTuberculoseImigrantes imigrantes) {
		this.imigrantes = imigrantes;
	}

	public DominioNotificacaoTuberculoseBeneficiario getBeneficiarioProgramaTransferenciaRendaGoverno() {
		return beneficiarioProgramaTransferenciaRendaGoverno;
	}

	public void setBeneficiarioProgramaTransferenciaRendaGoverno(DominioNotificacaoTuberculoseBeneficiario beneficiarioProgramaTransferenciaRendaGoverno) {
		this.beneficiarioProgramaTransferenciaRendaGoverno = beneficiarioProgramaTransferenciaRendaGoverno;
	}

	public DominioNotificacaoTuberculoseForma getForma() {
		return forma;
	}

	public void setForma(DominioNotificacaoTuberculoseForma forma) {
		this.forma = forma;
	}

	public boolean isPleural() {
		return pleural;
	}

	public void setPleural(boolean pleural) {
		this.pleural = pleural;
	}

	public boolean isGangPerif() {
		return gangPerif;
	}

	public void setGangPerif(boolean gangPerif) {
		this.gangPerif = gangPerif;
	}

	public boolean isGeniturinaria() {
		return geniturinaria;
	}

	public void setGeniturinaria(boolean geniturinaria) {
		this.geniturinaria = geniturinaria;
	}

	public boolean isOssea() {
		return ossea;
	}

	public void setOssea(boolean ossea) {
		this.ossea = ossea;
	}

	public boolean isOcular() {
		return ocular;
	}

	public void setOcular(boolean ocular) {
		this.ocular = ocular;
	}

	public boolean isMiliar() {
		return miliar;
	}

	public void setMiliar(boolean miliar) {
		this.miliar = miliar;
	}

	public boolean isMeningite() {
		return meningite;
	}

	public void setMeningite(boolean meningite) {
		this.meningite = meningite;
	}

	public boolean isCutanea() {
		return cutanea;
	}

	public void setCutanea(boolean cutanea) {
		this.cutanea = cutanea;
	}

	public boolean isLaringea() {
		return laringea;
	}

	public void setLaringea(boolean laringea) {
		this.laringea = laringea;
	}

	public String getOutraExtrapulmonar() {
		return outraExtrapulmonar;
	}

	public void setOutraExtrapulmonar(String outraExtrapulmonar) {
		this.outraExtrapulmonar = outraExtrapulmonar;
	}

	public DominioNotificacaoTuberculoseIndAids getAids() {
		return aids;
	}

	public void setAids(DominioNotificacaoTuberculoseIndAids aids) {
		this.aids = aids;
	}

	public DominioNotificacaoTuberculoseIndDiabetes getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(DominioNotificacaoTuberculoseIndDiabetes diabetes) {
		this.diabetes = diabetes;
	}

	public DominioNotificacaoTuberculoseIndDoencaMental getDoencaMental() {
		return doencaMental;
	}

	public void setDoencaMental(DominioNotificacaoTuberculoseIndDoencaMental doencaMental) {
		this.doencaMental = doencaMental;
	}

	public DominioNotificacaoTuberculoseIndAlcoolismo getAlcoolismo() {
		return alcoolismo;
	}

	public void setAlcoolismo(DominioNotificacaoTuberculoseIndAlcoolismo alcoolismo) {
		this.alcoolismo = alcoolismo;
	}

	public DominioNotificacaoTuberculoseTabagismo getTabagismo() {
		return tabagismo;
	}

	public void setTabagismo(DominioNotificacaoTuberculoseTabagismo tabagismo) {
		this.tabagismo = tabagismo;
	}

	public DominioNotificacaoTuberculoseDrogasIlicitas getUsoDrogasIlicitas() {
		return usoDrogasIlicitas;
	}

	public void setUsoDrogasIlicitas(DominioNotificacaoTuberculoseDrogasIlicitas usoDrogasIlicitas) {
		this.usoDrogasIlicitas = usoDrogasIlicitas;
	}

	public String getDescricaoAgravo() {
		return descricaoAgravo;
	}

	public void setDescricaoAgravo(String descricaoAgravo) {
		this.descricaoAgravo = descricaoAgravo;
	}

	public DominioNotificacaoTuberculoseBaciloscopiaEscarro getBaciloscopiaEscarro() {
		return baciloscopiaEscarro;
	}

	public void setBaciloscopiaEscarro(DominioNotificacaoTuberculoseBaciloscopiaEscarro baciloscopiaEscarro) {
		this.baciloscopiaEscarro = baciloscopiaEscarro;
	}

	public DominioNotificacaoTuberculoseRaioXTorax getRadiografiaTorax() {
		return radiografiaTorax;
	}

	public void setRadiografiaTorax(DominioNotificacaoTuberculoseRaioXTorax radiografiaTorax) {
		this.radiografiaTorax = radiografiaTorax;
	}

	public DominioNotificacaoTuberculoseHiv getHiv() {
		return hiv;
	}

	public void setHiv(DominioNotificacaoTuberculoseHiv hiv) {
		this.hiv = hiv;
	}

	public DominioNotificacaoTuberculoseAntirretroviral getTerapiaAntirretroviral() {
		return terapiaAntirretroviral;
	}

	public void setTerapiaAntirretroviral(DominioNotificacaoTuberculoseAntirretroviral terapiaAntirretroviral) {
		this.terapiaAntirretroviral = terapiaAntirretroviral;
	}

	public DominioNotificacaoTuberculoseHistopatologia getHistopatologia() {
		return histopatologia;
	}

	public void setHistopatologia(DominioNotificacaoTuberculoseHistopatologia histopatologia) {
		this.histopatologia = histopatologia;
	}

	public DominioNotificacaoTuberculoseCulturaEscarro getCultura() {
		return cultura;
	}

	public void setCultura(DominioNotificacaoTuberculoseCulturaEscarro cultura) {
		this.cultura = cultura;
	}

	public DominioNotificacaoTuberculoseTMR getTesteMolecularRapido() {
		return testeMolecularRapido;
	}

	public void setTesteMolecularRapido(DominioNotificacaoTuberculoseTMR testeMolecularRapido) {
		this.testeMolecularRapido = testeMolecularRapido;
	}

	public DominioNotificacaoTuberculoseSensibilidade getTesteSensibilidade() {
		return testeSensibilidade;
	}

	public void setTesteSensibilidade(DominioNotificacaoTuberculoseSensibilidade testeSensibilidade) {
		this.testeSensibilidade = testeSensibilidade;
	}

	public Date getDataInicioTratamentoAtual() {
		return dataInicioTratamentoAtual;
	}

	public void setDataInicioTratamentoAtual(Date dataInicioTratamentoAtual) {
		this.dataInicioTratamentoAtual = dataInicioTratamentoAtual;
	}

	public Short getTotalContatosIdentificados() {
		return totalContatosIdentificados;
	}

	public void setTotalContatosIdentificados(Short totalContatosIdentificados) {
		this.totalContatosIdentificados = totalContatosIdentificados;
	}

	public String getDescrOutraExtrapulmonar() {
		return descrOutraExtrapulmonar;
	}

	public void setDescrOutraExtrapulmonar(String descrOutraExtrapulmonar) {
		this.descrOutraExtrapulmonar = descrOutraExtrapulmonar;
	}

	public DominioNotificacaoTuberculoseAgravoAssociado getOutroAgravos() {
		return outroAgravos;
	}

	public void setOutroAgravos(DominioNotificacaoTuberculoseAgravoAssociado outroAgravos) {
		this.outroAgravos = outroAgravos;
	}

	public DominioNotificacaoTuberculoseAgravoOutrasDoencas getIndOutrasDoencas() {
		return indOutrasDoencas;
	}

	public void setIndOutrasDoencas(DominioNotificacaoTuberculoseAgravoOutrasDoencas indOutrasDoencas) {
		this.indOutrasDoencas = indOutrasDoencas;
	}

}
