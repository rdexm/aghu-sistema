package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

/**
 * 
 * @author lalegre
 *
 */


public class AltaSumarioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3819095684560681022L;
	private MpmAltaSumarioId id;
	private String nome;
	private String nomeSocial;
	private DominioSexo sexo;
	private Integer prontuario;
	private String local;
	private String descConvenio;
	private String descPlanoConvenio;
	private String descEquipeResponsavel;
	private String descServico;
	private Date dataInternacao;
	private Date dataAlta;
	private Date dataNascimento;
	private Date dataAtendimento;
	private String diasPermanenciaFormatado;
	private String idadeFormatado;
	private Short diasPermanencia;
	private Short idadeAnos;
	private Integer idadeMeses;
	private Integer idadeDias;
	private Date horaObito;
	private Date dataElaboracaoTransf;
	private String descEquipe;
	private String descEsp;
	private String descUnidade;
	private String descInstituicao;
	private String destino;
		
	//GETS AND SETS

	public MpmAltaSumarioId getId() {
		return this.id;
	}

	public void setId(MpmAltaSumarioId id) {
		this.id = id;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeSocial() {
		return nomeSocial;
	}
	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}
	
	public DominioSexo getSexo() {
		return this.sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getDescConvenio() {
		return this.descConvenio;
	}

	public void setDescConvenio(String descConvenio) {
		this.descConvenio = descConvenio;
	}

	public String getDescPlanoConvenio() {
		return this.descPlanoConvenio;
	}

	public void setDescPlanoConvenio(String descPlanoConvenio) {
		this.descPlanoConvenio = descPlanoConvenio;
	}

	public String getDescEquipeResponsavel() {
		return this.descEquipeResponsavel;
	}

	public void setDescEquipeResponsavel(String descEquipeResponsavel) {
		this.descEquipeResponsavel = descEquipeResponsavel;
	}

	public Date getDataInternacao() {
		return this.dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Date getDataAlta() {
		return this.dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public String getLocal() {
		return this.local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getDiasPermanenciaFormatado() {
		return this.diasPermanenciaFormatado;
	}

	public void setDiasPermanenciaFormatado(String diasPermanenciaFormatado) {
		this.diasPermanenciaFormatado = diasPermanenciaFormatado;
	}

	public String getIdadeFormatado() {
		return this.idadeFormatado;
	}

	public void setIdadeFormatado(String idadeFormatado) {
		this.idadeFormatado = idadeFormatado;
	}

	public Short getDiasPermanencia() {
		return this.diasPermanencia;
	}

	public void setDiasPermanencia(Short diasPermanencia) {
		this.diasPermanencia = diasPermanencia;
	}

	public Short getIdadeAnos() {
		return this.idadeAnos;
	}

	public void setIdadeAnos(Short idadeAnos) {
		this.idadeAnos = idadeAnos;
	}

	public Integer getIdadeMeses() {
		return this.idadeMeses;
	}

	public void setIdadeMeses(Integer idadeMeses) {
		this.idadeMeses = idadeMeses;
	}

	public Integer getIdadeDias() {
		return this.idadeDias;
	}

	public void setIdadeDias(Integer idadeDias) {
		this.idadeDias = idadeDias;
	}

	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getDescServico() {
		return this.descServico;
	}

	public void setDescServico(String descServico) {
		this.descServico = descServico;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public Date getHoraObito() {
		return horaObito;
	}

	public void setHoraObito(Date horaObito) {
		
		if(horaObito != null){
			
			// Calendário de hora de origem
			Calendar calendarioOrigemHora = Calendar.getInstance();
			calendarioOrigemHora.setTime(horaObito);

			// Calendário da data do óbito de destino
			Calendar calendarioDestinoDataObito = Calendar.getInstance();
			calendarioDestinoDataObito.setTime(getDataAlta());
			
			// Relaciona a hora do óbito com a data do óbito
			calendarioDestinoDataObito.set(Calendar.HOUR_OF_DAY, calendarioOrigemHora.get(Calendar.HOUR_OF_DAY));
			calendarioDestinoDataObito.set(Calendar.MINUTE, calendarioOrigemHora.get(Calendar.MINUTE));
			calendarioDestinoDataObito.set(Calendar.SECOND, 0);
			calendarioDestinoDataObito.set(Calendar.MILLISECOND, 0);	
			
			// Persiste data e hora do óbito
			this.setDataAlta(calendarioDestinoDataObito.getTime());

		}
		
		this.horaObito = horaObito;
		
	}

	public void setDataElaboracaoTransf(Date dataElaboracaoTransf) {
		this.dataElaboracaoTransf = dataElaboracaoTransf;
	}

	public Date getDataElaboracaoTransf() {
		return dataElaboracaoTransf;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getDestino() {
		return destino;
	}

	public String getDescEquipe() {
		return descEquipe;
	}

	public void setDescEquipe(String descEquipe) {
		this.descEquipe = descEquipe;
	}

	public String getDescEsp() {
		return descEsp;
	}

	public void setDescEsp(String descEsp) {
		this.descEsp = descEsp;
	}

	public String getDescUnidade() {
		return descUnidade;
	}

	public void setDescUnidade(String descUnidade) {
		this.descUnidade = descUnidade;
	}

	public String getDescInstituicao() {
		return descInstituicao;
	}

	public void setDescInstituicao(String descInstituicao) {
		this.descInstituicao = descInstituicao;
	}

	
}
