package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import br.gov.mec.aghu.dominio.DominioTipoImpressaoRelatorio;



public class RelSumarioAltaVO {
	
	//Identificação
	private String nomePaciente; 
	private String idade; 
	private String sexo; 
	private Date dataInternacao; 
	private Date dataAlta; //
	private String prontuario; 
	private String leito; 
	private String permanencia; 
	private String convenio; 
	private String equipeResponsavel; 
	private String servicoResponsavel;
	private String indetificador;
	private DominioTipoImpressaoRelatorio tipoImpressao;
	
	// Outra Equipe
	private List<LinhaReportVO> outrasEquipes;
	
	
	//Diagnóstico
	private List<LinhaReportVO> motivosInternacao;
	private String dignosticoPrincipalAlta;
	private List<LinhaReportVO>  dignosticosSecundarios;
	
	//Procedimentos Terapeuticos
	private List<LinhaReportVO> cirurgiasRealizadas;
	private List<LinhaReportVO> outrosProcedimentos;
	private List<LinhaReportVO> consultorias;
	private List<LinhaReportVO> principaisFarmacos;
	private List<LinhaReportVO> complementoFarmacos;
	
	//Evolução
	private String evolucao;
	
	//Plano Pós Alta
	private String motivoAlta;
	private String planoPosAlta;
	private List<LinhaReportVO> recomendacoesAlta;
	private List<LinhaReportVO> medicamentosAlta;
	
	//Seguimento do Atendimento
	private String consultas;
	private String reInternacao;
	private List<LinhaReportVO> examesSolicitados;
	
	//Estado do paciente na alta
	private String estadoPaciente;
	
	//Dados Relatório
	private Date dthrElaboracaoAlta;
	private String nomeSiglaRegistro;
	
	//Indices Details
	private String indiceDiagnostico;
	private String indiceProcedTerapeutico;
	private String indiceEvolucao;
	private String indicePlanoPosAlta;
	private String indiceSeguimentoAtendimento;
	private String indiceEstadoAlta;
	
	//Campo que delimita as vias do report
	private Integer ordemTela;
	
	private String localRodape;	

	private List<LinhaReportVO> consultasFuturas;

	
	public RelSumarioAltaVO(){		
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Date getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getPermanencia() {
		return permanencia;
	}

	public void setPermanencia(String permanencia) {
		this.permanencia = permanencia;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getEquipeResponsavel() {
		return equipeResponsavel;
	}

	public void setEquipeResponsavel(String equipeResponsavel) {
		this.equipeResponsavel = equipeResponsavel;
	}

	public String getServicoResponsavel() {
		return servicoResponsavel;
	}

	public void setServicoResponsavel(String servicoResponsavel) {
		this.servicoResponsavel = servicoResponsavel;
	}

	public String getIndetificador() {
		return indetificador;
	}

	public void setIndetificador(String indetificador) {
		this.indetificador = indetificador;
	}

	public List<LinhaReportVO> getOutrasEquipes() {
		return outrasEquipes;
	}

	public void setOutrasEquipes(List<LinhaReportVO> outrasEquipes) {
		this.outrasEquipes = outrasEquipes;
	}

	public List<LinhaReportVO> getMotivosInternacao() {
		return motivosInternacao;
	}

	public void setMotivosInternacao(List<LinhaReportVO> motivosInternacao) {
		this.motivosInternacao = motivosInternacao;
	}

	public String getDignosticoPrincipalAlta() {
		return dignosticoPrincipalAlta;
	}

	public void setDignosticoPrincipalAlta(String dignosticoPrincipalAlta) {
		this.dignosticoPrincipalAlta = dignosticoPrincipalAlta;
	}

	public List<LinhaReportVO> getDignosticosSecundarios() {
		return dignosticosSecundarios;
	}

	public void setDignosticosSecundarios(List<LinhaReportVO> dignosticosSecundarios) {
		this.dignosticosSecundarios = dignosticosSecundarios;
	}


	public List<LinhaReportVO> getOutrosProcedimentos() {
		return outrosProcedimentos;
	}

	public void setOutrosProcedimentos(List<LinhaReportVO> outrosProcedimentos) {
		this.outrosProcedimentos = outrosProcedimentos;
	}

	public List<LinhaReportVO> getConsultorias() {
		return consultorias;
	}

	public void setConsultorias(List<LinhaReportVO> consultorias) {
		this.consultorias = consultorias;
	}

	public List<LinhaReportVO> getPrincipaisFarmacos() {
		return principaisFarmacos;
	}

	public void setPrincipaisFarmacos(List<LinhaReportVO> principaisFarmacos) {
		this.principaisFarmacos = principaisFarmacos;
	}

	public List<LinhaReportVO> getComplementoFarmacos() {
		return complementoFarmacos;
	}

	public void setComplementoFarmacos(List<LinhaReportVO> complementoFarmacos) {
		this.complementoFarmacos = complementoFarmacos;
	}

	public String getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(String evolucao) {
		this.evolucao = evolucao;
	}

	public String getMotivoAlta() {
		return motivoAlta;
	}

	public void setMotivoAlta(String motivoAlta) {
		this.motivoAlta = motivoAlta;
	}

	public String getPlanoPosAlta() {
		return planoPosAlta;
	}

	public void setPlanoPosAlta(String planoPosAlta) {
		this.planoPosAlta = planoPosAlta;
	}

	public List<LinhaReportVO> getRecomendacoesAlta() {
		return recomendacoesAlta;
	}

	public void setRecomendacoesAlta(List<LinhaReportVO> recomendacoesAlta) {
		this.recomendacoesAlta = recomendacoesAlta;
	}

	public String getConsultas() {
		return consultas;
	}

	public void setConsultas(String consultas) {
		this.consultas = consultas;
	}

	public String getReInternacao() {
		return reInternacao;
	}

	public void setReInternacao(String reInternacao) {
		this.reInternacao = reInternacao;
	}

	public List<LinhaReportVO> getExamesSolicitados() {
		return examesSolicitados;
	}

	public void setExamesSolicitados(List<LinhaReportVO> examesSolicitados) {
		this.examesSolicitados = examesSolicitados;
	}

	public String getEstadoPaciente() {
		return estadoPaciente;
	}

	public void setEstadoPaciente(String estadoPaciente) {
		this.estadoPaciente = estadoPaciente;
	}

	public Date getDthrElaboracaoAlta() {
		return dthrElaboracaoAlta;
	}

	public void setDthrElaboracaoAlta(Date dthrElaboracaoAlta) {
		this.dthrElaboracaoAlta = dthrElaboracaoAlta;
	}

	public String getNomeSiglaRegistro() {
		return nomeSiglaRegistro;
	}

	public void setNomeSiglaRegistro(String nomeSiglaRegistro) {
		this.nomeSiglaRegistro = nomeSiglaRegistro;
	}

	public List<LinhaReportVO> getCirurgiasRealizadas() {
		return cirurgiasRealizadas;
	}

	public void setCirurgiasRealizadas(List<LinhaReportVO> cirurgiasRealizadas) {
		this.cirurgiasRealizadas = cirurgiasRealizadas;
	}

	public String getIndiceDiagnostico() {
		return indiceDiagnostico;
	}

	public void setIndiceDiagnostico(String indiceDiagnostico) {
		this.indiceDiagnostico = indiceDiagnostico;
	}

	public String getIndiceProcedTerapeutico() {
		return indiceProcedTerapeutico;
	}

	public void setIndiceProcedTerapeutico(String indiceProcedTerapeutico) {
		this.indiceProcedTerapeutico = indiceProcedTerapeutico;
	}

	public String getIndiceEvolucao() {
		return indiceEvolucao;
	}

	public void setIndiceEvolucao(String indiceEvolucao) {
		this.indiceEvolucao = indiceEvolucao;
	}

	public String getIndicePlanoPosAlta() {
		return indicePlanoPosAlta;
	}

	public void setIndicePlanoPosAlta(String indicePlanoPosAlta) {
		this.indicePlanoPosAlta = indicePlanoPosAlta;
	}

	public String getIndiceSeguimentoAtendimento() {
		return indiceSeguimentoAtendimento;
	}

	public void setIndiceSeguimentoAtendimento(String indiceSeguimentoAtendimento) {
		this.indiceSeguimentoAtendimento = indiceSeguimentoAtendimento;
	}

	public String getIndiceEstadoAlta() {
		return indiceEstadoAlta;
	}

	public void setIndiceEstadoAlta(String indiceEstadoAlta) {
		this.indiceEstadoAlta = indiceEstadoAlta;
	}

	public DominioTipoImpressaoRelatorio getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(DominioTipoImpressaoRelatorio tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}
	
	public Integer getOrdemTela() {
		return ordemTela;
	}


	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}
	
	public RelSumarioAltaVO copiar() {
		try {
			return (RelSumarioAltaVO) BeanUtils.cloneBean(this);
		} catch (Exception e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.			
			return null;
		}
	}
	public String getLocalRodape() {
		return localRodape;
	}

	public void setLocalRodape(String localRodape) {
		this.localRodape = localRodape;
	}

	public List<LinhaReportVO> getConsultasFuturas() {
		return consultasFuturas;
	}

	public void setConsultasFuturas(List<LinhaReportVO> consultasFuturas) {
		this.consultasFuturas = consultasFuturas;
	}

	public List<LinhaReportVO> getMedicamentosAlta() {
		return medicamentosAlta;
	}

	public void setMedicamentosAlta(List<LinhaReportVO> medicamentosAlta) {
		this.medicamentosAlta = medicamentosAlta;
	}
	
}
